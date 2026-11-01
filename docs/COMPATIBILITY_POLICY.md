# Modpack Compatibility Policy

## Goal

Tacz Enchanting should work in ordinary Minecraft Forge 1.20.1 modpacks that were not built specifically for it, including large RPG/adventure packs such as Otherworld [Dungeons & Dragons]. This is a design and test target, not a claim that compatibility with every pack has already been verified.

## Required baseline

- Minecraft 1.20.1 and Forge, using versions compatible with the target TaCZ release.
- TaCZ is the only gameplay mod dependency intended to be mandatory.
- No requirement for user-provided configs, custom gun packs, or pack-specific scripts.
- Never assume another mod, dimension, entity, weapon pack, quest system, skill system, or progression mod is installed.

## Implementation rules

1. **Isolate TaCZ integration.** Keep references to TaCZ APIs in a small integration layer. Verify API availability and version assumptions before use.
2. **Preserve foreign data.** Store enchantment state in our own namespaced data/attachment mechanism appropriate for Forge 1.20.1. Do not overwrite TaCZ's NBT, ammo, attachment, or gun identifiers. The exact storage mechanism must be verified before implementation.
3. **Use stable hooks.** Prefer documented Forge and TaCZ events/API over mixins, reflection, or assumptions about internal implementation. If a mixin becomes unavoidable, document the reason and add version-specific tests.
4. **Respect logical and physical sides.** Gameplay decisions and authoritative damage changes must be server-authoritative. Client-only classes must never be loaded on a dedicated server.
5. **Fail safely.** Missing optional integrations should disable only that integration, not prevent the whole mod from loading. Do not silently swallow errors that affect core functionality.
6. **Avoid global behavior changes.** Do not modify unrelated mobs, dimensions, loot tables, recipes, world generation, or progression unless a feature explicitly requires it.
7. **Keep configuration optional.** Defaults must work without custom configuration. Any config added later must be optional and safe with missing or malformed values.

## Validation matrix

A successful compile is necessary but not sufficient. Before declaring a release compatible, test as applicable:

- Clean Forge 1.20.1 instance with the supported TaCZ JAR.
- Dedicated server startup and client connection.
- Firing, hits, damage, death, weapon swapping, inventory movement, and persistence/relogging.
- TaCZ weapon/accessory combinations and cases where a gun or target is not a standard case.
- At least one large third-party modpack, with Otherworld [Dungeons & Dragons] as a target candidate, using its exact version and matching server pack where available.
- Startup logs, server logs, and crash reports; record exact modpack version and test result.

## Current status

This policy defines project requirements. Compatibility with Otherworld or any other third-party modpack remains **unverified** until tested in that pack. Phase 1 TaCZ investigation must be completed before enchantment implementation begins.
