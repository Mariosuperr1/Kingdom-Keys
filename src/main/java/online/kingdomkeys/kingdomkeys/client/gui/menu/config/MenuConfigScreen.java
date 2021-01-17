package online.kingdomkeys.kingdomkeys.client.gui.menu.config;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import online.kingdomkeys.kingdomkeys.client.gui.GuiHelper;
import online.kingdomkeys.kingdomkeys.client.gui.elements.MenuBackground;
import online.kingdomkeys.kingdomkeys.client.gui.elements.MenuBox;
import online.kingdomkeys.kingdomkeys.client.gui.elements.buttons.MenuButton;
import online.kingdomkeys.kingdomkeys.config.ModConfigs;
import online.kingdomkeys.kingdomkeys.lib.Strings;
import online.kingdomkeys.kingdomkeys.util.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuConfigScreen extends MenuBackground {

    enum ActualWindow {
        COMMAND_MENU, HP, MP, DRIVE, PLAYER, LOCK_ON_HP
    }

    ActualWindow window = ActualWindow.COMMAND_MENU;

    MenuButton back, commandMenuButton, hpButton;
    Button hideBarsButton, cmHeaderTextVisibleButton;
    MenuBox box;
    TextFieldWidget cmTextXOffsetBox, cmXScaleBox;

    boolean cmHeaderTextVisible;

    List<Widget> commandMenuList = new ArrayList<>();

    public MenuConfigScreen() {
        super(Strings.Gui_Menu_Config, new Color(0,0,255));
        drawPlayerInfo = false;
    }


    protected void action(String string) {
        switch(string) {
            case "back":
                GuiHelper.openMenu();
                break;
            case "textHeaderVisibility":
                cmHeaderTextVisible = !cmHeaderTextVisible;
                cmHeaderTextVisibleButton.setMessage(cmHeaderTextVisible+"");
                ModConfigs.setCmHeaderTextVisible(cmHeaderTextVisible);
                break;
        }

        updateButtons();
    }

    private void updateButtons() {

    }

    @Override
    public void init() {
        cmHeaderTextVisible = ModConfigs.cmHeaderTextVisible;
        float boxPosX = (float) width * 0.2F;
        float topBarHeight = (float) height * 0.17F;
        float boxWidth = (float) width * 0.67F;
        float middleHeight = (float) height * 0.6F;
        box = new MenuBox((int) boxPosX, (int) topBarHeight, (int) boxWidth, (int) middleHeight, new Color(4, 4, 68));

        super.init();
        this.buttons.clear();

        addButton(cmHeaderTextVisibleButton = new Button(box.x+99, (int) topBarHeight + 18, (int) (buttonWidth * 0.55F), 20, cmHeaderTextVisible+"", (e) -> { action("textHeaderVisibility"); }));
        addButton(cmTextXOffsetBox = new TextFieldWidget(minecraft.fontRenderer, box.x+100, (int) (topBarHeight + 20 * 2), minecraft.fontRenderer.getStringWidth("#####"), 16, "test"){
            @Override
            public boolean charTyped(char c, int i) {
                if (Utils.isNumber(c) || c == '-') {
                    String text = new StringBuilder(this.getText()).insert(this.getCursorPosition(), c).toString();

                    if (Utils.getInt(text) < 1000 && Utils.getInt(text) > -1000) {
                        super.charTyped(c, i);
                        ModConfigs.setCmTextXOffset(Utils.getInt(getText()));
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                super.keyPressed(keyCode, scanCode, modifiers);
                ModConfigs.setCmTextXOffset(Utils.getInt(getText()));
                return true;
            }
        });

        addButton(cmXScaleBox = new TextFieldWidget(minecraft.fontRenderer, box.x+100, (int) (topBarHeight + 20 * 3), minecraft.fontRenderer.getStringWidth("#####"), 16, new TranslationTextComponent("test")){
            @Override
            public boolean charTyped(char c, int i) {
                if (Utils.isNumber(c) || c == '-') {
                    String text = new StringBuilder(this.getText()).insert(this.getCursorPosition(), c).toString();
                    if (Utils.getInt(text) < 1000 && Utils.getInt(text) > -1000) {
                        super.charTyped(c, i);
                        ModConfigs.setCmXScale(Utils.getInt(getText()));
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                super.keyPressed(keyCode, scanCode, modifiers);
                ModConfigs.setCmXScale(Utils.getInt(getText()));
                return true;
            }

        });

        addButton(commandMenuButton = new MenuButton((int) buttonPosX, (int) topBarHeight + 5 + (0 * 18), (int) buttonWidth, Utils.translateToLocal("Command Menu"), MenuButton.ButtonType.BUTTON, (e) -> { window = ActualWindow.COMMAND_MENU; }));

        addButton(hpButton = new MenuButton((int) buttonPosX, (int) topBarHeight + 5 + (1 * 18), (int) buttonWidth, Utils.translateToLocal("HP Bar"), MenuButton.ButtonType.BUTTON, (e) -> { window = ActualWindow.HP; }));
        addButton(back = new MenuButton((int) buttonPosX, (int) topBarHeight + 5 + (2 * 18), (int) buttonWidth, Utils.translateToLocal(Strings.Gui_Menu_Back), MenuButton.ButtonType.BUTTON, (e) -> { action("back"); }));
        addButton(hideBarsButton = new MenuButton((int) width / 2 - (int)buttonWidth / 2, (int) topBarHeight + 5 + (7-2 * 18), (int) buttonWidth, Utils.translateToLocal("Background"), MenuButton.ButtonType.BUTTON, (e) -> { drawSeparately = !drawSeparately; }));

        cmTextXOffsetBox.setText(""+ModConfigs.cmTextXOffset);
        cmHeaderTextVisibleButton.setMessage(cmHeaderTextVisible+"");
        cmXScaleBox.setText(""+ModConfigs.cmXScale);

        commandMenuList.add(cmHeaderTextVisibleButton);
        commandMenuList.add(cmTextXOffsetBox);
        commandMenuList.add(cmTextXOffsetBox);
        commandMenuList.add(cmHeaderTextVisibleButton);
        commandMenuList.add(cmXScaleBox);
        updateButtons();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        commandMenuButton.active = window != ActualWindow.COMMAND_MENU;
        box.draw();
        super.render(mouseX, mouseY, partialTicks);

        for (Widget b : commandMenuList) {
            b.active = false;
            b.visible = false;
        }

        switch(window) {
        case COMMAND_MENU:
            for(Widget b : commandMenuList) {
                b.active = true;
                b.visible = true;
            }

            RenderSystem.pushMatrix();
            {
                RenderSystem.translatef(box.x+8, box.y+4, 1);
                drawString(minecraft.fontRenderer, Utils.translateToLocal("Command Menu"), 0, 0, 0xFF9900);
                drawString(minecraft.fontRenderer, Utils.translateToLocal("Header Title: "), 12, 20, 0xFF9900);
                drawString(minecraft.fontRenderer, Utils.translateToLocal("Text X Offset: "), 12, 20*2, 0xFF9900);
                drawString(minecraft.fontRenderer, Utils.translateToLocal("X Scale: "), 12, 20*3, 0xFF9900);
            }
            RenderSystem.popMatrix();

            break;


        }

    }

}