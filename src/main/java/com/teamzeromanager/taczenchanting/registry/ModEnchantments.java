package com.teamzeromanager.taczenchanting.registry;

import java.util.Map;
import java.util.Optional;

/**
 * Stable identifiers and metadata for mod-owned enchantments.
 *
 * Levels are stored in the item's own "taczenchanting" NBT compound rather
 * than overwriting TaCZ or vanilla item data.
 */
public final class ModEnchantments {
    public static final String INCENDIARY = "incendiary";

    public record Definition(String id, int maxLevel) {}

    private static final Map<String, Definition> DEFINITIONS = Map.of(
            INCENDIARY, new Definition(INCENDIARY, 3)
    );

    private ModEnchantments() {}

    public static Optional<Definition> get(String id) {
        return Optional.ofNullable(DEFINITIONS.get(id));
    }

    public static Map<String, Definition> all() {
        return DEFINITIONS;
    }
}
