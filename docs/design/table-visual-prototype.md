# Gun Smith Table visual prototype

## Intent

The first in-game visual pass keeps TaCZ's existing `tacz:gun_smith_table` block untouched and adds a client-only preview when the player stands near it holding a TaCZ gun:

- the held gun is rendered floating just above the work surface;
- it slowly rotates and bobs;
- enchantment-style particles drift around the table;
- the prototype does not alter inventories, NBT, or server gameplay.

## Current limitations

This is a visual prototype, not the final table implementation. The rendered weapon follows the gun held in the player's main or off hand. It does not yet provide a GUI, remove exterior model details, or change particle type based on the selected enchantment. Those require separate steps and in-game review.

The visual code is client-only to avoid dedicated-server classloading issues. It targets TaCZ's registered `tacz:gun_smith_table` block and fails closed if the block or a held gun is absent.
