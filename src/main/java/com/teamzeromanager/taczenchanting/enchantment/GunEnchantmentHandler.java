package com.teamzeromanager.taczenchanting.enchantment;

import com.teamzeromanager.taczenchanting.TaczEnchanting;
import com.teamzeromanager.taczenchanting.registry.ModEnchantments;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

/**
 * Server-authoritative TaCZ hooks for custom weapon enchantments.
 */
@Mod.EventBusSubscriber(modid = TaczEnchanting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunEnchantmentHandler {
    private static final String INCENDIARY_LEVEL_KEY = "incendiary";

    private GunEnchantmentHandler() {}

    /**
     * Snapshot the enchantment level onto the projectile when it is created.
     * This prevents swapping weapons while a bullet is in flight from changing
     * which enchantments that bullet carries.
     */
    @SubscribeEvent
    public static void onBulletJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof EntityKineticBullet bullet)) {
            return;
        }

        Entity owner = bullet.getOwner();
        if (!(owner instanceof LivingEntity shooter)) {
            return;
        }

        ItemStack weapon = findMatchingGun(shooter, bullet.getGunId(), bullet.getGunDisplayId());
        if (weapon.isEmpty()) {
            return;
        }

        int level = EnchantmentData.getLevel(weapon, ModEnchantments.INCENDIARY);
        if (level > 0) {
            bullet.getPersistentData().putInt(INCENDIARY_LEVEL_KEY, level);
        }
    }

    /**
     * Every valid server-side impact applies fire when the projectile carries
     * Incendiary. There is deliberately no random chance roll.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunImpact(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide() != LogicalSide.SERVER || event.isCanceled()) {
            return;
        }

        if (!(event.getBullet() instanceof EntityKineticBullet bullet)) {
            return;
        }

        int level = bullet.getPersistentData().getInt(INCENDIARY_LEVEL_KEY);
        if (level <= 0) {
            return;
        }

        Entity target = event.getHurtEntity();
        if (target == null || target.isRemoved() || target.fireImmune()) {
            return;
        }

        // Fixed, deterministic duration by level: I = 2s, II = 4s, III = 6s.
        // Repeated impacts use Minecraft's normal fire timer behavior.
        target.setSecondsOnFire(2 * Math.min(level, 3));
    }

    private static ItemStack findMatchingGun(LivingEntity shooter, ResourceLocation gunId,
                                             ResourceLocation gunDisplayId) {
        ItemStack held = matchingStack(shooter.getMainHandItem(), gunId, gunDisplayId);
        if (!held.isEmpty()) {
            return held;
        }

        held = matchingStack(shooter.getOffhandItem(), gunId, gunDisplayId);
        if (!held.isEmpty()) {
            return held;
        }

        if (shooter instanceof Player player) {
            for (ItemStack candidate : player.getInventory().items) {
                ItemStack match = matchingStack(candidate, gunId, gunDisplayId);
                if (!match.isEmpty()) {
                    return match;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack matchingStack(ItemStack stack, ResourceLocation gunId,
                                           ResourceLocation gunDisplayId) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        IGun gun = IGun.getIGunOrNull(stack);
        if (gun == null) {
            return ItemStack.EMPTY;
        }

        if (java.util.Objects.equals(gun.getGunId(stack), gunId)
                && java.util.Objects.equals(gun.getGunDisplayId(stack), gunDisplayId)) {
            return stack;
        }

        return ItemStack.EMPTY;
    }
}
