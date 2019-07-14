package online.kingdomkeys.kingdomkeys;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import online.kingdomkeys.kingdomkeys.client.gui.*;
import online.kingdomkeys.kingdomkeys.item.KeybladeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import online.kingdomkeys.kingdomkeys.block.ModBlocks;
import online.kingdomkeys.kingdomkeys.client.gui.CommandMenuGui;
import online.kingdomkeys.kingdomkeys.corsair.CorsairTickHandler;
import online.kingdomkeys.kingdomkeys.corsair.KeyboardManager;
import online.kingdomkeys.kingdomkeys.handler.EntityEvents;
import online.kingdomkeys.kingdomkeys.handler.InputHandler;
import online.kingdomkeys.kingdomkeys.item.ModItems;
import online.kingdomkeys.kingdomkeys.lib.Strings;
import online.kingdomkeys.kingdomkeys.proxy.ProxyClient;
import online.kingdomkeys.kingdomkeys.proxy.IProxy;
import online.kingdomkeys.kingdomkeys.proxy.ProxyServer;
import online.kingdomkeys.kingdomkeys.synthesis.keybladeforge.KeybladeDataLoader;

@Mod("kingdomkeys")
public class KingdomKeys {

	public static final Logger LOGGER = LogManager.getLogger();

	public static KingdomKeys instance;

	public static final String MODID = "kingdomkeys";
	public static final String MODNAME = "Kingdom Keys";
	public static final String MODVER = "2.0";
	public static final String MCVER = "1.14.3";

	public KeyboardManager keyboardManager;

	// The proxy instance created for the current dist double lambda prevents class
	// being loaded on the other dist
	@SuppressWarnings("Convert2MethodRef")
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ProxyClient(), () -> () -> new ProxyServer());

	public static ItemGroup orgWeaponsGroup = new ItemGroup(Strings.organizationGroup) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.eternalFlames);
		}
	};
	public static ItemGroup keybladesGroup = new ItemGroup(Strings.keybladesGroup) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.kingdomKey);
		}
	};
	public static ItemGroup miscGroup = new ItemGroup(Strings.miscGroup) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModBlocks.normalBlox);
		}
	};

	public KingdomKeys() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		//this.keyboardManager = new KeyboardManager();

		MinecraftForge.EVENT_BUS.register(this);
		// Client
		MinecraftForge.EVENT_BUS.register(new CommandMenuGui());
		MinecraftForge.EVENT_BUS.register(new PlayerPortraitGui());
		MinecraftForge.EVENT_BUS.register(new HPGui());
		MinecraftForge.EVENT_BUS.register(new MPGui());
		MinecraftForge.EVENT_BUS.register(new DriveGui());
		MinecraftForge.EVENT_BUS.register(new InputHandler());
		MinecraftForge.EVENT_BUS.register(new CorsairTickHandler(keyboardManager));
		MinecraftForge.EVENT_BUS.register(new ProxyClient());
		//this.keyboardManager.showLogo();

		for (InputHandler.Keybinds key : InputHandler.Keybinds.values())
			ClientRegistry.registerKeyBinding(key.getKeybind());

		// Server
		MinecraftForge.EVENT_BUS.register(new EntityEvents());
	}

	private void setup(final FMLCommonSetupEvent event) {
		// Run setup on proxies
		proxy.setup(event);
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerAboutToStartEvent event) {
		this.registerResourceLoader(event.getServer().getResourceManager());
	}

	private void registerResourceLoader(final IReloadableResourceManager resourceManager) {
		resourceManager.addReloadListener((IResourceManagerReloadListener)manager -> {
			KeybladeDataLoader.loadData(resourceManager);
			//OrganizationDataLoader.loadData(resourceManager);
		});
	}

	@SubscribeEvent
	public void hitEntity(LivingHurtEvent event) {
		if (event.getSource().getTrueSource() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
			if (player.getHeldItemMainhand().getItem() instanceof KeybladeItem) {
				KeybladeItem heldKeyblade = (KeybladeItem) player.getHeldItemMainhand().getItem();
				// TODO add player's strength stat
				// TODO improved damage calculation
				event.setAmount(heldKeyblade.getStrength(heldKeyblade.getKeybladeLevel()));
			}
		}
	}

	public KeyboardManager getKeyboardManager() {
		return this.keyboardManager;
	}
}
