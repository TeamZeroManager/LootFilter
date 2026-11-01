package com.teamzeromanager.taczenchanting;

import com.teamzeromanager.taczenchanting.menu.ModMenus;
import com.teamzeromanager.taczenchanting.network.ModNetwork;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TaczEnchanting.MOD_ID)
public final class TaczEnchanting {
    public static final String MOD_ID = "taczenchanting";

    public TaczEnchanting() {
        ModMenus.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModNetwork.register();
    }
}
