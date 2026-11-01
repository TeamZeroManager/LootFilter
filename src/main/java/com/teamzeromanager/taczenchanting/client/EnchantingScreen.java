package com.teamzeromanager.taczenchanting.client;

import com.teamzeromanager.taczenchanting.menu.EnchantingMenu;
import com.teamzeromanager.taczenchanting.network.EnchantingActionPacket;
import com.teamzeromanager.taczenchanting.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "taczenchanting", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EnchantingScreen extends AbstractContainerScreen<EnchantingMenu> {
    public EnchantingScreen(EnchantingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 190;
        this.inventoryLabelY = 10000;
    }

    @SubscribeEvent
    public static void registerScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(
                com.teamzeromanager.taczenchanting.menu.ModMenus.ENCHANTING.get(), EnchantingScreen::new));
    }

    @Override
    protected void init() {
        super.init();
        int left = this.leftPos;
        int top = this.topPos;
        addRenderableWidget(Button.builder(Component.literal("Aplicar I"), b ->
                ModNetwork.CHANNEL.sendToServer(new EnchantingActionPacket(1)))
                .bounds(left + 18, top + 118, 66, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Aplicar II"), b ->
                ModNetwork.CHANNEL.sendToServer(new EnchantingActionPacket(2)))
                .bounds(left + 95, top + 118, 66, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Aplicar III"), b ->
                ModNetwork.CHANNEL.sendToServer(new EnchantingActionPacket(3)))
                .bounds(left + 172, top + 118, 66, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Quitar"), b ->
                ModNetwork.CHANNEL.sendToServer(new EnchantingActionPacket(0)))
                .bounds(left + 95, top + 145, 66, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xEE101321);
        graphics.fill(leftPos + 6, topPos + 6, leftPos + imageWidth - 6, topPos + imageHeight - 6, 0xFF25283A);
        graphics.fill(leftPos + 10, topPos + 10, leftPos + imageWidth - 10, topPos + 42, 0xFF171A29);
        graphics.fill(leftPos + 14, topPos + 48, leftPos + imageWidth - 14, topPos + 106, 0xFF1C2031);
        graphics.renderOutline(leftPos + 6, topPos + 6, imageWidth - 12, imageHeight - 12, 0xFF8265D6);
        graphics.renderOutline(leftPos + 10, topPos + 10, imageWidth - 20, 32, 0xFF4B4778);
        graphics.renderOutline(leftPos + 14, topPos + 48, imageWidth - 28, 58, 0xFF454B70);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(font, "MESA DE ENCANTAMIENTOS", imageWidth / 2, 19, 0xFFE8DCFF);
        graphics.drawString(font, "INCENDIARIO", 22, 56, 0xFFFFB56B);
        graphics.drawString(font, "I: 2 s de fuego", 22, 72, 0xFFD6D8E8);
        graphics.drawString(font, "II: 4 s de fuego", 22, 84, 0xFFD6D8E8);
        graphics.drawString(font, "III: 6 s de fuego", 135, 72, 0xFFD6D8E8);
        graphics.drawCenteredString(font, "Efecto garantizado al impactar", imageWidth / 2, 96, 0xFFB8A7F5);
        graphics.drawCenteredString(font, "Sin coste en este prototipo", imageWidth / 2, 174, 0xFF9CA3BD);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
