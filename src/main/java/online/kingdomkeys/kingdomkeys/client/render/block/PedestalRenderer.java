package online.kingdomkeys.kingdomkeys.client.render.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import online.kingdomkeys.kingdomkeys.entity.block.PedestalTileEntity;
import online.kingdomkeys.kingdomkeys.item.KeychainItem;

public class PedestalRenderer implements BlockEntityRenderer<PedestalTileEntity> {

    private ItemRenderer renderItem;

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(PedestalTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
	    this.renderItem = Minecraft.getInstance().getItemRenderer();

	    if (!tileEntityIn.isStationOfAwakeningMarker()) {
			tileEntityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(iih -> {
				if (!iih.getStackInSlot(0).isEmpty()) {
					renderItem(tileEntityIn, matrixStackIn, bufferIn, partialTicks, iih.getStackInSlot(0).getItem() instanceof KeychainItem ? new ItemStack(((KeychainItem) iih.getStackInSlot(0).getItem()).getKeyblade()) : iih.getStackInSlot(0), combinedLightIn);
				}
			});
		} else {
	    	if (!tileEntityIn.hide) {
				renderItem(tileEntityIn, matrixStackIn, bufferIn, partialTicks, tileEntityIn.getDisplayStack(), combinedLightIn);
			}
		}
	}

	private void renderItem(PedestalTileEntity tileEntity, PoseStack matrixStack, MultiBufferSource buffer, float partialTicks, ItemStack toRender, int combinedLightIn) {
		matrixStack.pushPose();
		{
			RenderSystem.setShaderColor(1, 1, 1, 1);
			float height, rotation;
			if (!tileEntity.isPaused()) {
				float lerpedTicks = tileEntity.previousTicks + (tileEntity.ticksExisted() - tileEntity.previousTicks) * partialTicks;
				height = tileEntity.getBaseHeight() + (0.1F * (float) Math.sin(tileEntity.getBobSpeed() * lerpedTicks));
				rotation = lerpedTicks * tileEntity.getRotationSpeed() % 360F;
				tileEntity.setCurrentTransforms(rotation, height);
			} else {
				height = tileEntity.getSavedHeight();
				rotation = tileEntity.getSavedRotation();
			}

			matrixStack.translate(0.5F, height, 0.5F);
			matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), rotation, true));
			matrixStack.scale(tileEntity.getScale(), tileEntity.getScale(), tileEntity.getScale());
			if(tileEntity.isFlipped()) {
	        	matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));
				matrixStack.translate(0, -0.6F, 0);

			}
			BakedModel model = renderItem.getModel(toRender, tileEntity.getLevel(), null, 1);
			renderItem.render(toRender, TransformType.FIXED, false, matrixStack, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY, model);
		}
		matrixStack.popPose();
	}
}
