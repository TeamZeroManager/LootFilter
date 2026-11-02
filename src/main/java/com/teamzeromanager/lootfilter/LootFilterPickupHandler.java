package com.teamzeromanager.lootfilter;

import com.teamzeromanager.lootfilter.config.LootFilterSelection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Applies the configured allow-list to normal ground-item pickup.
 * With no enabled selections, vanilla pickup behavior is left untouched.
 */
@Mod.EventBusSubscriber(modid = LootFilter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootFilterPickupHandler {
    private LootFilterPickupHandler() {}

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (!LootFilterSelection.hasAnyEnabled()) return;

        ItemStack stack = event.getItem().getItem();
        if (stack.isEmpty()) return;

        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemKey == null) return;

        String blockId = null;
        if (stack.getItem() instanceof BlockItem blockItem) {
            ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
            if (blockKey != null) blockId = blockKey.toString();
        }

        if (!LootFilterSelection.isItemAllowed(itemKey.toString(), blockId)) {
            event.setCanceled(true);
        }
    }
}
