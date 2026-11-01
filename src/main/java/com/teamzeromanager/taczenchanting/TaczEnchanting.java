package com.teamzeromanager.taczenchanting;

import com.teamzeromanager.taczenchanting.registry.ModEnchantments;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TaczEnchanting.MOD_ID)
public final class TaczEnchanting {
    public static final String MOD_ID = "taczenchanting";

    public TaczEnchanting() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEnchantments.register(modEventBus);
    }
}
