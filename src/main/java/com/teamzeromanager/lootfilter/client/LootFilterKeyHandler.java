package com.teamzeromanager.lootfilter.client;

import com.teamzeromanager.lootfilter.LootFilter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LootFilter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class LootFilterKeyHandler {
    private LootFilterKeyHandler() {}

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (LootFilterClient.OPEN_FILTER.consumeClick() && minecraft.screen == null) {
            minecraft.setScreen(new LootFilterScreen());
        }
    }
}
