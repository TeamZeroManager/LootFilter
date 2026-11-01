package com.teamzeromanager.taczenchanting.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

public final class EnchantingMenuProvider implements MenuProvider {
    @Override
    public Component getDisplayName() {
        return Component.literal("Mesa de encantamientos");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, net.minecraft.world.entity.player.Player player) {
        return new EnchantingMenu(containerId, inventory);
    }
}
