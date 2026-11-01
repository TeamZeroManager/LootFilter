package com.teamzeromanager.taczenchanting.enchantment;

import com.teamzeromanager.taczenchanting.registry.ModEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Reads and writes Tacz Enchanting levels without replacing existing item NBT.
 */
public final class EnchantmentData {
    public static final String ROOT_KEY = "taczenchanting";
    private static final String LEVELS_KEY = "enchantments";

    private EnchantmentData() {}

    public static int getLevel(ItemStack stack, String enchantmentId) {
        if (stack == null || stack.isEmpty() || ModEnchantments.get(enchantmentId).isEmpty()) {
            return 0;
        }

        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(ROOT_KEY, CompoundTag.TAG_COMPOUND)) {
            return 0;
        }

        CompoundTag modData = root.getCompound(ROOT_KEY);
        if (!modData.contains(LEVELS_KEY, CompoundTag.TAG_COMPOUND)) {
            return 0;
        }

        int level = modData.getCompound(LEVELS_KEY).getInt(enchantmentId);
        return Math.max(0, Math.min(level, ModEnchantments.get(enchantmentId).orElseThrow().maxLevel()));
    }

    /**
     * Sets one enchantment level and preserves every unrelated NBT key.
     * A level of zero removes only this enchantment.
     */
    public static void setLevel(ItemStack stack, String enchantmentId, int requestedLevel) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        var definition = ModEnchantments.get(enchantmentId).orElse(null);
        if (definition == null) {
            return;
        }

        int level = Math.max(0, Math.min(requestedLevel, definition.maxLevel()));
        CompoundTag root = stack.getOrCreateTag();

        if (level == 0 && !root.contains(ROOT_KEY, CompoundTag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag modData = root.getCompound(ROOT_KEY);
        CompoundTag levels = modData.getCompound(LEVELS_KEY);

        if (level == 0) {
            levels.remove(enchantmentId);
        } else {
            levels.putInt(enchantmentId, level);
        }

        if (levels.isEmpty()) {
            modData.remove(LEVELS_KEY);
        } else {
            modData.put(LEVELS_KEY, levels);
        }

        if (modData.isEmpty()) {
            root.remove(ROOT_KEY);
        } else {
            root.put(ROOT_KEY, modData);
        }

        if (root.isEmpty()) {
            stack.setTag(null);
        }
    }
}
