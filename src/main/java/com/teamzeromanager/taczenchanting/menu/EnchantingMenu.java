package com.teamzeromanager.taczenchanting.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class EnchantingMenu extends AbstractContainerMenu {
    public EnchantingMenu(int containerId, Inventory inventory, FriendlyByteBuf data) {
        this(containerId, inventory);
    }

    public EnchantingMenu(int containerId, Inventory inventory) {
        super(ModMenus.ENCHANTING.get(), containerId);
    }

    public ItemStack getHeldGun(Player player) {
        return player.getMainHandItem();
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isRemoved() && player.distanceToSqr(
                player.level().getSharedSpawnPos().getX(),
                player.level().getSharedSpawnPos().getY(),
                player.level().getSharedSpawnPos().getZ()) < Double.MAX_VALUE;
    }
}
