# LootFilter

A Minecraft **1.20.1 Forge** mod foundation for configuring which blocks and items are eligible for automatic collection.

## Requirements

- Minecraft 1.20.1
- Minecraft Forge 47.x
- Java 17

## Current foundation

- LootFilter mod metadata and ID
- Client keybind (`O`) to open the filter screen
- Blocks and items are populated from the live Minecraft registries
- Grid-based category screen with enabled/disabled visual states and pagination
- Per-client selection persistence in `config/lootfilter.properties`

## Build

Run `gradle build` with Java 17 installed. The mod JAR is generated in `build/libs/`.

## Status

This commit replaces the old TaCZ Enchanting scaffold with the LootFilter foundation. The GUI and saved selections are the starting point; the actual automatic-pickup behavior and in-game testing still need to be implemented and validated.
