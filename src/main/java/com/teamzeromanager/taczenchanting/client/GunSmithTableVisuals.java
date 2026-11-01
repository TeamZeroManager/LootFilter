package com.teamzeromanager.taczenchanting.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tac.guns.item.IGun;
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

        BlockPos center = minecraft.player.blockPosition();
        RandomSource random = level.random;
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                center.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
            if (!isGunSmithTable(level.getBlockState(pos))) continue;

            // Low particle rate to keep the effect subtle and inexpensive.
            if (level.getGameTime() % 3L == 0L) {
                double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.9D;
                double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.9D;
                double y = pos.getY() + 1.08D + random.nextDouble() * 0.28D;
                level.addParticle(ParticleTypes.ENCHANT, x, y, z,
                        (random.nextDouble() - 0.5D) * 0.04D, 0.035D,
                        (random.nextDouble() - 0.5D) * 0.04D);
            }
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
        BlockPos center = minecraft.player.blockPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        var cameraPosition = event.getCamera().getPosition();
        double cameraX = cameraPosition.x;
        double cameraY = cameraPosition.y;
        double cameraZ = cameraPosition.z;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                center.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
            if (!isGunSmithTable(level.getBlockState(pos))) continue;

            double time = level.getGameTime() + event.getPartialTick();
            double bob = Math.sin(time * 0.08D) * 0.035D;
            poseStack.pushPose();
            poseStack.translate(pos.getX() + 0.5D - cameraX,
                    pos.getY() + 1.36D + bob - cameraY,
                    pos.getZ() + 0.5D - cameraZ);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (time * 1.1D % 360.0D)));
            poseStack.mulPose(Axis.XP.rotationDegrees(-18.0F));
            poseStack.scale(0.78F, 0.78F, 0.78F);
            itemRenderer.renderStatic(gun, ItemDisplayContext.FIXED,
                    LevelRenderer.getLightColor(level, pos.above()), OverlayTexture.NO_OVERLAY,
                    poseStack, buffers, level, 0);
            poseStack.popPose();
        }
        buffers.endBatch();
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
