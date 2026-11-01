package com.teamzeromanager.taczenchanting.menu;

import com.teamzeromanager.taczenchanting.TaczEnchanting;
import com.tacz.guns.api.item.IGun;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = TaczEnchanting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TableInteractionHandler {
    private static final ResourceLocation TABLE_ID = new ResourceLocation("tacz", "gun_smith_table");

    private TableInteractionHandler() {}

    @SubscribeEvent
    public static void onRightClickTable(PlayerInteractEvent.RightClickBlock event) {
        if (!TABLE_ID.equals(ForgeRegistries.BLOCKS.getKey(event.getLevel().getBlockState(event.getPos()).getBlock()))) {
            return;
        }
        ItemStack held = event.getItemStack();
        if (held.isEmpty() || IGun.getIGunOrNull(held) == null) {
            return;
        }

        event.setCanceled(true);
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            NetworkHooks.openScreen(player, new EnchantingMenuProvider());
        }
    }
}
