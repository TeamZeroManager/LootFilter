package com.teamzeromanager.taczenchanting.menu;

import com.teamzeromanager.taczenchanting.TaczEnchanting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TaczEnchanting.MOD_ID);
    public static final RegistryObject<MenuType<EnchantingMenu>> ENCHANTING =
            MENUS.register("enchanting", () -> IForgeMenuType.create(EnchantingMenu::new));

    private ModMenus() {}

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}
