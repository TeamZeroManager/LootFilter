package com.teamzeromanager.taczenchanting.network;

import com.teamzeromanager.taczenchanting.enchantment.EnchantmentData;
import com.teamzeromanager.taczenchanting.menu.EnchantingMenu;
import com.teamzeromanager.taczenchanting.registry.ModEnchantments;
import com.tacz.guns.api.item.IGun;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EnchantingActionPacket(int level) {
    public static void encode(EnchantingActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.level());
    }

    public static EnchantingActionPacket decode(FriendlyByteBuf buffer) {
        return new EnchantingActionPacket(buffer.readVarInt());
    }

    public static void handle(EnchantingActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.containerMenu instanceof EnchantingMenu)
                    || packet.level() < 0 || packet.level() > 3) {
                return;
            }
            ItemStack gun = player.getMainHandItem();
            if (gun.isEmpty() || IGun.getIGunOrNull(gun) == null) {
                player.sendSystemMessage(Component.literal("Sostén un arma de TaCZ en la mano principal."));
                return;
            }
            EnchantmentData.setLevel(gun, ModEnchantments.INCENDIARY, packet.level());
            String message = packet.level() == 0
                    ? "Incendiario eliminado del arma."
                    : "Incendiario " + roman(packet.level()) + " aplicado al arma.";
            player.sendSystemMessage(Component.literal(message));
        });
        context.setPacketHandled(true);
    }

    private static String roman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> Integer.toString(level);
        };
    }
}
