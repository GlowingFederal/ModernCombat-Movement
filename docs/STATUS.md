# Initial development milestone status

Development build: **0.1.0.13**. Forge 1.7.10, 1.8.9 and 1.12.2 distributions run Java 8. This is an implemented foundation and expanded combat-kit milestone, not complete modern gameplay parity. Modern rules remain the intended specification on every target.

## Implemented

- Independent Forge targets, centralized next-build allocation and verified distributable JARs in root `build/release`; shared platform-neutral Java 8 calculations and explicit item-property/cooldown integration API.
- Sided initialization, configuration, coalesced network intents, server-owned player poses, observer tracking and collision-safe dimensions. Initial modern movement/swimming/crawling corrections, feet-anchored eyes and basic third-person poses.
- Scoped legacy melee cooldown/scaling/critical/sprint/sweep foundation and native 1.12 corrections. Known-gear armor/toughness, Strength/Weakness, protection, netherite resistance and owned explosion-resistance rules. Unknown foreign weapons, armor and projectiles retain their defined owner.
- Standalone copper/netherite equipment, seven spear tiers, mace, crossbows, legacy main-hand totems, anchors, placed crystals and four modern effects. UAB registrations are reused and selected behavior is adapted without duplicate ownership. See COMBAT-KIT.md for remaining families.
- Native-source spear jab/kinetic charge, reach/occlusion/contact cooldown, Lunge, mace smash/Density/Breach/Wind Burst, modern owned anchor/crystal explosions and legacy golden-apple effects. Native item consumption is preserved.
- Standalone Elytra on 1.7/1.8, modern scoped flight on all targets, legacy attached rocket entities with boost/star damage, server durability/takeoff validation, .6-block flight box/.4-block eye, tracked wing interpolation and rendered equipment. UAB's chest Elytra shares the canonical flight owner. Foreign gliders are preserved.
- Mandatory Modern Regen **1.0.2.2+** on 1.7. AppleCore owns its food update; 1.8 uses a narrow optional regeneration event bridge, and 1.12 retains native regeneration. AppleSkin's hunger HUD and synchronization are preserved.
- LOTR Legacy combat and knockback are preserved by default, independently of movement and kit configuration. This is a compatibility exception, not modern LOTR combat parity.

## Verification actually performed

| Evidence | 1.7.10 | 1.8.9 | 1.12.2 |
|---|---|---|---|
| Final 0.1.0.13 production compilation, remapping, packaging and Java 8 archive verification | Passed | Passed | Passed |
| Shared numerical/API suite in each target | 32 passed | 32 passed | 32 passed |
| Production dedicated server startup | Passed | Passed | Passed |
| Earlier foundation and crossbow/totem callback batches | Passed | Passed | Passed |
| Expanded eleven-scenario weapon/armor/crystal/apple/regen callback batch | Passed with UAB | Passed | Passed |
| Final flight/rocket callback batch | 7 passed with UAB | 7 passed | 6 passed |
| Actual native hurt callback: damage-independent .4 knockback | Passed with UAB | Passed | Native version-specific path retained |
| Connected client, tracked flight pose and actual native framebuffer inspection | Passed standalone and with UAB | Passed | Passed |
| Real client rocket-use packet: server consumption and continued glide | Passed standalone and with UAB | Callback coverage; real use not separately recorded | Native callback coverage |
| AppleCore food/regen integration | 3.1.1 + Modern Regen; 3.3.13 + Modern Regen + UAB | 1.3.2 | 3.4.0 |
| AppleSkin 1.0.14 on both ends: saturation/exhaustion packets and HUD | No official target in this test set | No official target in this test set | Passed |
| Native offhand spear: correct enchantment and durability owner | Offhand pending | Offhand pending | Passed |
| Actual CurseForge LOTR 36.15 startup and six compatibility callbacks | Passed | Not applicable | Not applicable |
| Complete mouse-driven gameplay, long-session multiplayer and actual vanilla differential parity | Not established | Not established | Not established |

The root common module independently passes the same 32 tests; these are 32 shared cases, not 128 different cases. Tests cover mechanics arithmetic, API scope, spear/mace rules, native animation curves and flight/boost recurrence. Numerical assertions are derived from inspected vanilla source, not a simultaneous experiment on a running vanilla 1.21.11 or 26.3 server.

Expanded callbacks cover real registered equipment, multiple aligned spear targets, minimum range/occlusion/air cooldown, resolved-speed kinetic damage/contact cooldown, mace damage/aura, Density/Breach through the actual Forge armor path, one-air crystal placement, owned explosion damage/no explosive chaining, golden-apple food/effects and regeneration. Native offhand spear testing additionally checks that Fire Aspect and wear come from the active offhand stack.

Flight callbacks exercise rising starts, rejected ground/creative/broken starts, real travel interception, twentieth-tick wear, landing flag reset, accumulated fall distance, NBT and attached rocket boost/damage. The 1.8/1.12 regression deliberately separates head yaw from player yaw. The 1.12 test uses the actual native rocket item/entity and checks its corrected default duration and boost direction. UAB uses its actual Elytra and weapon registrations. Connected 1.7 use sends the native interaction packet; server stack consumption and continued glide are recorded separately from formula checks.

All game callbacks run on the owning game thread through JDI in disposable instances. Clients are muted and hidden; screenshots come from Minecraft's native framebuffer. This verifies those callbacks, classloading, selected packet paths and visuals. It does not qualify human input timing, complete flight trajectories under latency, every collision/landing case, sound fidelity, survival progression or complete PvP acceptance. UAB's initial resource download delayed its first client connection; the later initialized client connected and rendered successfully. Missing muted fixture sound assets are not evidence of sound parity.

Confirmed blocking defects corrected during this batch include the 1.8 Mixin runtime ABI mismatch, wrong Forge armor method case, a 1.12 Mixin-generated anonymous helper class, server access to client-only legacy item-use getters, legacy hurt damage being mistaken for knockback strength, and offhand spear enchantment/wear ownership. Final relevant callbacks pass. No performance measurements or full vanilla parity are claimed.

## Remaining work and next priorities

1. Real legacy offhand inventory, persistence, swaps, use priority, equipment synchronization and rendering, followed by standalone shields. Native 1.12 shields still require all later behavioral corrections; their presence is not an exemption.
2. Remaining kit families and behavior: tridents, standalone wind charges, crossbow fireworks, missing combat potions/ammunition, pearl/collision/cooldown differences, full equipment components and durability/repair/fire-immunity audit. Ground-launched legacy fireworks retain their old behavior.
3. Full combat enchantment/sweep/shield/hurt-time/reach and timing audit; native 26.3 interaction corrections; controlled vanilla 1.21.11/26.3 traces and representative latency tests. PvP HQ is observational reference only; no custom PvP profile is implemented.
4. Finish lava/blocks/effects, modern ledge/step protection, complete swimming/crawling/weapon animation and detailed flight landing/wall/obstruction acceptance. Existing movement corrections are not complete movement parity.
5. Anchor respawn/reconnection and full survival/observer gameplay qualification; complete UAB component coverage. The external Modern Regen 1.8 + AppleCore combination was not qualified; MCM defers to that regeneration owner rather than patching its repository.
6. Later UAB-style module/content configuration pass, as requested, with modern defaults and validation of coupled features.

Occupied legacy effect/enchantment IDs are never silently overwritten and must match between client and server. Removing UAB from an existing save is not an automatic registry migration. No personal installation/save was used, and no commits or external releases were created for this milestone.
