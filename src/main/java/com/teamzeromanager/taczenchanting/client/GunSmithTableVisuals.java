package com.teamzeromanager.taczenchanting.client;

import com.tac.guns.item.GunItem;
import com.tac.guns.item.IGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

/**
 * First visual prototype: when the player approaches TaCZ's Gun Smith Table
 * while holding a TaCZ gun, render that gun hovering above the work surface and
 * surround it with deterministic enchantment particles.
 *
 * This is intentionally client-only and does not change TaCZ blocks or gameplay.
 */
@Mod.EventBusSubscriber(modid = "taczenchanting", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunSmithTableVisuals {
    private static final ResourceLocation GUN_SMITH_TABLE = new ResourceLocation("tacz", "gun_smith_table");
    private static final double SEARCH_RADIUS = 5.0D;

    private GunSmithTableVisuals() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        Level level = minecraft.level;
        BlockPos center = minecraft.player.blockPosition();
        int radius = (int) SEARCH_RADIUS;
        RandomSource random = level.random;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(pos);
            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            if (!GUN_SMITH_TABLE.equals(blockId)) continue;

            ItemStack gun = findHeldGun(minecraft);
            if (gun.isEmpty()) continue;

            // A restrained magical halo: enough to signal an active enchanting station,
            // without flooding the client's particle budget.
            if (level.getGameTime() % 3L == 0L) {
                double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.9D;
                double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.9D;
                double y = pos.getY() + 1.08D + random.nextDouble() * 0.28D;
                level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT,
                        x, y, z, (random.nextDouble() - 0.5D) * 0.04D, 0.04D,
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
        int radius = (int) SEARCH_RADIUS;
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        var camera = event.getCamera();
        double cameraX = camera.getPosition().x;
        double cameraY = camera.getPosition().y;
        double cameraZ = camera.getPosition().z;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos).getBlock());
            if (!GUN_SMITH_TABLE.equals(blockId)) continue;

            double time = level.getGameTime() + event.getPartialTick();
            double bob = Math.sin(time * 0.08D) * 0.035D;
            poseStack.pushPose();
            poseStack.translate(pos.getX() + 0.5D - cameraX,
                    pos.getY() + 1.36D + bob - cameraY,
                    pos.getZ() + 0.5D - cameraZ);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (time * 1.1D % 360.0D)));
            poseStack.mulPose(Axis.XP.rotationDegrees(-18.0F));
            poseStack.scale(0.78F, 0.78F, 0.78F);
            itemRenderer.renderStatic(gun, ItemDisplayContext.FIXED, LevelRenderer.getLightColor(level, pos.above()),
                    OverlayTexture.NO_OVERLAY, poseStack, buffers, level, 0);
            poseStack.popPose();
        }
        buffers.endBatch();
    }

    private static ItemStack findHeldGun(Minecraft minecraft) {
        ItemStack main = minecraft.player.getMainHandItem();
        if (!main.isEmpty() && IGun.getIGunOrNull(main) != null) return main;
        ItemStack offhand = minecraft.player.getOffhandItem();
        if (!offhand.isEmpty() && IGun.getIGunOrNull(offhand) != null) return offhand;
        return ItemStack.EMPTY;
    }
}
