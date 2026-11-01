package com.teamzeromanager.taczenchanting.network;

import com.teamzeromanager.taczenchanting.TaczEnchanting;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TaczEnchanting.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private static boolean registered;

    private ModNetwork() {}

    public static void register() {
        if (registered) return;
        registered = true;
        CHANNEL.messageBuilder(EnchantingActionPacket.class, 0)
                .encoder(EnchantingActionPacket::encode)
                .decoder(EnchantingActionPacket::decode)
                .consumerMainThread(EnchantingActionPacket::handle)
                .add();
    }
}
