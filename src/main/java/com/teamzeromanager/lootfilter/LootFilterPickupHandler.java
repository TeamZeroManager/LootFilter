package com.teamzeromanager.lootfilter;

import com.teamzeromanager.lootfilter.config.LootFilterSelection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Blocks automatic pickup for disabled entries, while allowing a deliberate right-click pickup. */
@Mod.EventBusSubscriber(modid = LootFilter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootFilterPickupHandler {
    private LootFilterPickupHandler() {}

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        ItemStack stack = event.getItem().getItem();
        if (isDisabled(stack)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDisabledItemRightClick(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof ItemEntity itemEntity)) return;

        ItemStack droppedStack = itemEntity.getItem();
        if (!isDisabled(droppedStack)) return;

        // Disabled items require an intentional right-click instead of automatic pickup.
        event.setCanceled(true);
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (droppedStack.isEmpty() || itemEntity.hasPickUpDelay()) return;

        ItemStack inventoryStack = droppedStack.copy();
        int originalCount = inventoryStack.getCount();
        player.getInventory().add(inventoryStack);
        int pickedUpCount = originalCount - inventoryStack.getCount();

        if (pickedUpCount <= 0) return;

        if (inventoryStack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(inventoryStack);
        }
        player.take(itemEntity, pickedUpCount);
    }

    private static boolean isDisabled(ItemStack stack) {
        if (stack.isEmpty()) return false;

        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemKey == null) return false;

        String blockId = null;
        if (stack.getItem() instanceof BlockItem blockItem) {
            ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
            if (blockKey != null) blockId = blockKey.toString();
        }

        return LootFilterSelection.isDisabled(itemKey.toString(), blockId);
    }
}
