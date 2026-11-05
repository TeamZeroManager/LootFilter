package com.teamzeromanager.lootfilter.client;

import com.teamzeromanager.lootfilter.LootFilter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LootFilter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class LootFilterKeyHandler {
    private LootFilterKeyHandler() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        while (LootFilterClient.OPEN_FILTER.consumeClick()) {
            if (minecraft.screen == null) minecraft.setScreen(new LootFilterScreen());
        }
    }
}
