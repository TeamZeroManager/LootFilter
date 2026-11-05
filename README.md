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
- Automatic classification uses registry IDs and item properties. Unusual modded items may need their category rules refined.
- A 9-by-4 item grid, pagination, tooltips, and green/gray enabled states.
- **Every item is enabled by default.** Click an entry to disable it; click again to re-enable it. Only explicitly disabled entries are blocked from pickup.
- Disabled selections persist in `config/lootfilter.properties`.
- Block items can be disabled through their block entry.
- The category rules are being refined based on in-game testing and feedback.

## Important notes

- Category assignment is heuristic. Items added by mods are included, but some unusual modded items may appear under "Other Items" or a general block category.
- The current pickup rule filters normal ground-item pickup; it does not pull items from a distance.
- This first implementation uses the local config file for its disabled list. Multiplayer servers may need server-side configuration/network synchronization before the filter can behave consistently for every player.

## Build

Run `gradle build` with Java 17 installed. The mod JAR is generated in `build/libs/`.
