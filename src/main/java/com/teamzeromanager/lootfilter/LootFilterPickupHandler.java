package com.teamzeromanager.lootfilter;

import com.teamzeromanager.lootfilter.config.LootFilterSelection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Blocks pickup only for entries explicitly disabled in the filter. */
@Mod.EventBusSubscriber(modid = LootFilter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootFilterPickupHandler {
    private LootFilterPickupHandler() {}

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        ItemStack stack = event.getItem().getItem();
        if (stack.isEmpty()) return;

        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemKey == null) return;

        String blockId = null;
        if (stack.getItem() instanceof BlockItem blockItem) {
            ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
            if (blockKey != null) blockId = blockKey.toString();
        }

        if (LootFilterSelection.isDisabled(itemKey.toString(), blockId)) {
            event.setCanceled(true);
        }
    }
}
