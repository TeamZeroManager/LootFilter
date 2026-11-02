# LootFilter

A Minecraft **1.20.1 Forge** mod for configuring which blocks and items are allowed to be picked up from the ground.

## Requirements

- Minecraft 1.20.1
- Minecraft Forge 47.x
- Java 17

## Current features

- Press `O` to open the filter screen.
- A category browser organizes registered blocks and items into construction blocks, decoration blocks, saplings and plants, ores and mineral blocks, redstone, tools, weapons and armor, food, materials and resources, potions and brewing, transport, and other items.
- Categories are populated from Minecraft registries, so entries from installed mods are included too.
- Automatic classification uses item/block registry IDs and available item properties; anything that does not match a known rule remains available in an "Other Items" or general block category.
- A 9-by-4 item grid, pagination, tooltips, and green/gray enabled states.
- Selections persist in `config/lootfilter.properties`.
- When at least one entry is enabled, only enabled items (or enabled block-items) can be picked up from the ground. Disabled drops remain on the ground.
- When no entries are enabled, vanilla pickup behavior is unchanged.
- Existing unprefixed selections from the initial UI build are migrated on load.

## Important notes

- Category assignment is heuristic. Items added by mods are included, but some unusual modded items may appear under "Other Items" or a general block category.
- The current pickup rule filters normal ground-item pickup; it does not pull items from a distance.
- This first implementation uses the local config file for its allow-list. Multiplayer servers may need server-side configuration/network synchronization before the filter can behave consistently for every player.
- Please test in a disposable world first, especially after enabling a small allow-list: all unselected drops will be blocked while at least one selection is active.

## Build

Run `gradle build` with Java 17 installed. The mod JAR is generated in `build/libs/`.
