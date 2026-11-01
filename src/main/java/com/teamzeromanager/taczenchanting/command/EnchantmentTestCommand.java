package com.teamzeromanager.taczenchanting.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.teamzeromanager.taczenchanting.TaczEnchanting;
import com.teamzeromanager.taczenchanting.enchantment.EnchantmentData;
import com.teamzeromanager.taczenchanting.registry.ModEnchantments;
import com.tacz.guns.api.item.IGun;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Temporary admin command for testing custom enchantments before the workbench UI exists.
 */
@Mod.EventBusSubscriber(modid = TaczEnchanting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantmentTestCommand {
    private EnchantmentTestCommand() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("taczenchanting")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("enchant")
                                .then(Commands.argument("enchantment", StringArgumentType.word())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 3))
                                                .executes(context -> apply(
                                                        context.getSource().getPlayerOrException(),
                                                        StringArgumentType.getString(context, "enchantment"),
                                                        IntegerArgumentType.getInteger(context, "level"))))))
        );
    }

    private static int apply(ServerPlayer player, String enchantmentId, int level) {
        if (ModEnchantments.get(enchantmentId).isEmpty()) {
            player.sendSystemMessage(Component.literal(
                    "Encantamiento desconocido: " + enchantmentId + ". Disponibles: incendiary"));
            return 0;
        }

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || IGun.getIGunOrNull(stack) == null) {
            player.sendSystemMessage(Component.literal(
                    "Sostén un arma de TaCZ en la mano principal antes de usar este comando."));
            return 0;
        }

        EnchantmentData.setLevel(stack, enchantmentId, level);
        String label = enchantmentId.equals(ModEnchantments.INCENDIARY) ? "Incendiario" : enchantmentId;
        if (level == 0) {
            player.sendSystemMessage(Component.literal("Se quitó " + label + " del arma."));
        } else {
            player.sendSystemMessage(Component.literal(
                    "Aplicado " + label + " " + roman(level) + " al arma que sostienes."));
        }
        return 1;
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
