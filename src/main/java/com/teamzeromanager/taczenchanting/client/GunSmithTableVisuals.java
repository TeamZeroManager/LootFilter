package com.teamzeromanager.taczenchanting.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tacz.guns.api.item.IGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * First client-side visual pass for TaCZ's Gun Smith Table.
 * Shows the player's held gun floating above a nearby table with enchantment particles.
 * Does not modify TaCZ's block, item NBT, or server gameplay.
 */
@Mod.EventBusSubscriber(modid = "taczenchanting", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunSmithTableVisuals {
    private static final ResourceLocation GUN_SMITH_TABLE = new ResourceLocation("tacz", "gun_smith_table");
    private static final int SEARCH_RADIUS = 5;

    private GunSmithTableVisuals() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        Level level = minecraft.level;
        ItemStack gun = findHeldGun(minecraft);
        if (gun.isEmpty()) return;

        BlockPos table = findNearestTable(level, minecraft.player.blockPosition());
        if (table == null) return;

        // Subtle particles only around the single table selected for the floating gun.
        if (level.getGameTime() % 3L == 0L) {
            RandomSource random = level.random;
            double x = table.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.9D;
            double z = table.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.9D;
            double y = table.getY() + 1.08D + random.nextDouble() * 0.28D;
            level.addParticle(ParticleTypes.ENCHANT, x, y, z,
                    (random.nextDouble() - 0.5D) * 0.04D, 0.035D,
                    (random.nextDouble() - 0.5D) * 0.04D);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        ItemStack gun = findHeldGun(minecraft);
        if (gun.isEmpty()) return;

        Level level = minecraft.level;
        BlockPos table = findNearestTable(level, minecraft.player.blockPosition());
        if (table == null) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        var cameraPosition = event.getCamera().getPosition();
        double cameraX = cameraPosition.x;
        double cameraY = cameraPosition.y;
        double cameraZ = cameraPosition.z;

        // Render exactly one gun, centered on the selected table's top surface.
        double time = level.getGameTime() + event.getPartialTick();
        double bob = Math.sin(time * 0.08D) * 0.035D;
        poseStack.pushPose();
        poseStack.translate(table.getX() + 0.5D - cameraX,
                table.getY() + 1.12D + bob - cameraY,
                table.getZ() + 0.5D - cameraZ);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) (time * 1.1D % 360.0D)));
        poseStack.mulPose(Axis.XP.rotationDegrees(-18.0F));
        poseStack.scale(0.28F, 0.28F, 0.28F);
        itemRenderer.renderStatic(gun, ItemDisplayContext.FIXED,
                LevelRenderer.getLightColor(level, table.above()), OverlayTexture.NO_OVERLAY,
                poseStack, buffers, level, 0);
        poseStack.popPose();
        buffers.endBatch();
    }

    private static BlockPos findNearestTable(Level level, BlockPos center) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                center.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
            if (!isGunSmithTable(level.getBlockState(pos))) continue;
            double dx = pos.getX() + 0.5D - (center.getX() + 0.5D);
            double dy = pos.getY() + 0.5D - (center.getY() + 0.5D);
            double dz = pos.getZ() + 0.5D - (center.getZ() + 0.5D);
            double distance = dx * dx + dy * dy + dz * dz;
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = pos.immutable();
            }
        }
        return nearest;
    }

    private static boolean isGunSmithTable(BlockState state) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return GUN_SMITH_TABLE.equals(id);
    }

    private static ItemStack findHeldGun(Minecraft minecraft) {
        ItemStack main = minecraft.player.getMainHandItem();
        if (!main.isEmpty() && IGun.getIGunOrNull(main) != null) return main;
        ItemStack offhand = minecraft.player.getOffhandItem();
        if (!offhand.isEmpty() && IGun.getIGunOrNull(offhand) != null) return offhand;
        return ItemStack.EMPTY;
    }
}
