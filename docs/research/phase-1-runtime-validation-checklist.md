# Phase 1 — Runtime validation checklist (TaCZ 1.1.8-hotfix2)

**Status: IN PROGRESS.** A clean client smoke test has passed for startup, new-world loading, basic weapon use, and save/reopen according to the user's report. Phase 1 remains open; data persistence internals, attachment lifecycle, event behavior, dedicated-server validation, and compatibility testing are still pending.

## Goal

Validate the TaCZ integration points and data-persistence assumptions in a controlled Minecraft 1.20.1 / Forge environment before implementing any enchantment behavior.

## Test environment record

### Run 1 — clean client smoke test

- Minecraft: 1.20.1
- Forge: 47.4.28
- Java: 17.0.15 (Microsoft)
- TaCZ: 1.1.8-hotfix2
- Other mods: TaCZ was the only third-party content mod found in the instance's `mods` folder; Forge/Minecraft libraries and TaCZ's bundled libraries are expected.
- Client or dedicated server: Client with integrated server
- World type / test save: New test world ("Mundo nuevo")
- Result date: 2026-11-01 (conversation record; exact local test date not independently verified)
- Logs: user-provided `latest(2).log`
- User-reported gameplay result: firing, reloading, and reopening the world worked.

### Observed results

- **PASS — client startup and world load:** the log identifies TaCZ 1.1.8-hotfix2, discovers the default `tacz_default_gun` pack, starts the integrated server, and loads the new world.
- **PASS — save and clean disconnect:** the log records saving all dimensions and stopping without a crash stack trace.
- **PASS — basic weapon smoke test (user-reported):** the user reports that firing, reloading, and reopening the world all work.
- **LIMITATION — this is not a persistence-harness test:** the test did not add and inspect a mod-owned NBT tag, so it does not prove custom enchantment data would survive save/load or synchronization.
- **PENDING — dedicated server:** no dedicated-server/client connection test has been run.
- **PENDING — attachments and events:** attachment custom-data lifecycle and TaCZ event counts/sides have not been instrumented.
- **NOTES:** TaCZ server config values were initialized/corrected to defaults in the new world's serverconfig. No startup-blocking issue was observed in this run.

## A. Baseline and loading

- [x] Launch a clean Forge 1.20.1 client with TaCZ only; reach the title screen and load a test world.
- [x] Confirm there are no missing-dependency, class-loading, or registry errors in the new clean-world run that prevent startup or world loading.
- [ ] Repeat on a dedicated server with a client connecting; confirm both sides start and connect cleanly.
- [x] Record exact Forge and Java versions, TaCZ version, and relevant log evidence.

**Pass criteria:** no crash or relevant error during startup, world load, connection, or disconnect.

## B. Representative gun identification

- [ ] Test at least one ordinary TaCZ firearm and, if available in the installed content, one gun supplied by an additional TaCZ-compatible gun pack.
- [ ] Confirm the item is recognized through TaCZ's public gun interface, IGun.getIGunOrNull(ItemStack), rather than by a hard-coded item ID list.
- [ ] Check empty, loaded, and reloaded weapon states.
- [ ] Check a non-gun item returns no gun interface.
- [ ] Record any unusual weapons or packs that cannot be classified safely.

**User smoke-test note:** firing and reloading worked, and the user reports the world could be reopened. This does not yet validate the interface-based detection or all weapon states.

**Pass criteria:** detection is based on the TaCZ interface and fails safely for non-guns; unknown gun categories do not crash or silently receive category-specific behavior.

## C. Gun stack data persistence (no enchantment implementation yet)

Use a disposable test item and a temporary, clearly namespaced test tag only in a test harness; do not edit or replace TaCZ's existing root tag.

- [ ] Establish that the test tag can coexist with the gun's existing NBT.
- [ ] Save and quit; reopen the world and inspect whether the test tag remains.
- [ ] Move the item between inventory slots and containers; inspect whether the tag remains.
- [ ] If practical, transfer the item through a server/client round trip and inspect whether the tag remains.
- [ ] Confirm existing gun identity, ammo, fire mode, and attachments remain functional after the test.
- [ ] Remove the temporary test tag after the run.

**Pass criteria:** mod-owned data survives the tested operations without altering TaCZ-owned data. If any operation strips or resets data, document it before choosing a storage design.

## D. Attachment serialization and lifecycle

Run only in a disposable world with a compatible attachment and a gun that accepts it.

- [ ] Record the attachment stack's pre-install data, including any harmless test-only custom tag.
- [ ] Install the attachment and inspect the gun's attachment data.
- [ ] Save/reload the world and verify the attachment remains installed and functional.
- [ ] Remove and reinstall the attachment; inspect whether its custom data survives.
- [ ] Repeat for more than one attachment type if available (for example scope and muzzle).
- [ ] Check the client view against server state and note any mismatch.

**Pass criteria:** observed behavior is recorded per attachment type. Do not assume attachment ItemStack tags survive install/removal/synchronization just because the bytecode serializes the stack during installation.

## E. Event behavior and duplicate-effect risk

Use a temporary diagnostic harness or debugger; do not cancel events or modify gameplay in this test.

- [ ] Count GunFireEvent observations per trigger shot on client and server separately.
- [ ] Check automatic fire, single fire, and a reload or weapon switch.
- [ ] Count EntityHurtByGunEvent.Pre and .Post for a confirmed living target hit.
- [ ] Test a non-lethal hit and, separately, a lethal hit.
- [ ] If possible, test a non-living target/target minecart and record which event route is used.
- [ ] Confirm server-side gameplay effects would not be applied twice due to client-side event republication.
- [ ] Confirm no assumption is made that .Post occurs on every impact or death path.

**Pass criteria:** event counts and logical sides are understood for tested cases; no listener cancellation is used; server gameplay logic has a clear single-authority path.

## F. Compatibility smoke tests

- [x] Clean Forge + TaCZ client baseline passes for startup, new-world load, and basic user-reported weapon use.
- [ ] TaCZ gun pack, if used, passes the representative gun checks.
- [ ] Run the intended Otherworld: Dungeons & Dragons modpack with exact version and its required dependencies, without the new mod first, to establish a baseline.
- [ ] Only after the baseline is stable, add the scaffold/new mod and compare startup, world load, weapon use, and logs.
- [ ] Record failures already present in the unmodified modpack separately from regressions introduced by this mod.
- [ ] Dedicated-server baseline passes.

**Pass criteria:** no new crash or relevant regression attributable to Tacz Enchanting in the tested setup. A smoke test is not a guarantee of compatibility with every modpack.

## G. Phase 1 exit criteria

Phase 1 may be closed only when:

- [ ] The clean client and dedicated-server baseline results are recorded.
- [ ] Gun detection behavior is confirmed with representative items.
- [ ] Gun data persistence has evidence from save/reload and server/client testing where available.
- [ ] Attachment behavior is tested or explicitly excluded from first-release scope.
- [ ] Event side/count behavior is recorded for normal, lethal, and relevant alternate paths.
- [ ] The first-release scope and acceptance criteria are agreed.
- [ ] Findings and unresolved limitations are committed to the research report.

**Until these criteria are met, do not implement enchantment mechanics or mark Phase 1 complete.**

## Result template

For each checkbox, record PASS / FAIL / BLOCKED / NOT TESTED, plus the Minecraft/Forge/Java/TaCZ versions, reproduction steps, and log or screenshot evidence. Never mark a check PASS based only on static bytecode inspection.
