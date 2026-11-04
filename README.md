# LootFilter

A Minecraft **1.20.1 Forge** add-on focused on custom enchantments for **Timeless and Classics Zero (TaCZ)** firearms.

## Requirements

- Minecraft 1.20.1
- Minecraft Forge 47.x
- TaCZ installed on both client and server

## Development

- Java 17
- ForgeGradle 6.x

The initial project scaffold is in place. Custom enchantments and their gameplay effects will be added incrementally, then tested in a Forge development client and server.

## Build

On a machine with Java 17 and Gradle available:

```bash
gradle build
```

The built mod JAR will be in `build/libs/`.

## Current status

- [x] Forge 1.20.1 project scaffold
- [x] Mod metadata and enchantment registry
- [ ] Register the first enchantment
- [ ] Hook enchantment effects into TaCZ weapon/projectile gameplay
- [ ] Add automated build and runtime testing
