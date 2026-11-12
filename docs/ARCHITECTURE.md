# Architecture and ownership

The root build coordinates three independent Forge build islands. `common` contains Java 8, platform-neutral mechanics and public item-profile contracts. It contains no Minecraft, Forge, client or UAB classes. Each target compiles the shared source alongside its own native adapter, following the ExampleMod structure. No source preprocessing or runtime wrappers translate one target's entity APIs into another's.

| Subsystem | Canonical responsibility |
|---|---|
| movement | Shared verified arithmetic; target water travel replaces the applicable player branch exactly once. Native movement/collision remains authoritative for preserved paths. |
| pose | Player-attached state, collision-tested desired/fallback poses, anchored dimensions, exact gameplay eyes, lifecycle exclusions. Crawling is a low pose without the swimming movement flag. |
| combat | Exact item lookup, attack-speed attribute input, attack-strength progression, damage/critical/sweep rules. Legacy managed attacks have one dispatch owner; 1.12 retains equivalent native stages and receives targeted corrections. |
| knockback | Modern resistance and grounded/airborne velocity arithmetic, entered only for explicitly managed sources. Preserve the Forge event when the target actually supplies it. |
| offhand | Native 1.12 inventory stays canonical while its modern differences are audited. The legacy inventory/use/render migration is a separate milestone; package presence is not an implementation claim. |
| render | Client proxy, camera interpolation, low-pose transform, native crouch model adaptation. Gameplay hitboxes/eyes never use visual interpolation. |
| network | Protocol-versioned Forge channel; C2S input intent, S2C server policy and accepted pose; tracker-based observer dispatch. No packet supplies authoritative dimensions, health or damage. |
| compat | Exact identity adapters and content ownership. UAB classes never appear in core signatures. Foreign item inheritance never opts attacks or projectiles into interception. |
| config | Native Forge files in organized categories. Server gameplay policy is sent to clients; visual settings remain client-owned. Coupled crawling/swimming switches cannot select an invalid combination. |

## Threads and lifecycle

C2S handlers coalesce the latest input by authenticated player UUID. Player ticks consume it on the owning game thread. The queue stores at most one pending value per player; disconnect removes it. S2C handlers enqueue/coalesce immutable snapshots, which the client tick applies after checking dimension, entity ID and UUID. Tracking entry sends a snapshot; state changes and periodic refresh repair missed spawn ordering. This is synchronization, not asynchronous physics.

State belongs to player instances, so a replaced/respawned player does not retain another instance's pose. Sleeping, death, riding, flight, spectator and native elytra paths retain their native owners. Pose snapshots cannot force the server into a requested low box. Eye height used for targeting/suffocation is exact; only the camera interpolates.

Dedicated-server code references the common proxy only. Client classes, LWJGL and model classes are loaded by the sided proxy or client-only Mixin list. All targets emit Java 8 bytecode. 1.7.10 uses UniMixins; 1.8.9 supplies a version-compatible embedded Mixin loader; 1.12.2 uses MixinBooter. Mixin method aliases explicitly cover the target's MCP and SRG method names. They are not raw ASM transformers.

## Compatibility API

Call `CombatApi.register("othermod:exact_item", new ItemProperties(...))` during mod initialization, before `FMLLoadCompleteEvent`. Properties contain weapon kind, intrinsic total damage, attack speed, sweep eligibility and an explicit projectile opt-in. `replace` is a deliberate override with a returned previous profile; duplicate `register` calls fail rather than silently changing another integration. The registry freezes after loading. Exact unknown IDs return no profile and retain their owning mod's attack path.

There is no namespace wildcard, subclass inference or blanket interception of all bows/swords/projectiles. A projectile opt-in property is a permission boundary, not a claim that every foreign projectile engine has been migrated. Source-specific adapters must still implement the requested behavior. Vanilla and MCM defaults are explicit entries; UAB integration must bind its actual registered item identities and avoid double handlers.

## Build and release

`version.properties` contains semantic `mod_version` and the next production `build_number`. Root scripts retain ExampleMod's version transaction, per-target wrappers and archive verification. Independent builds produce per-target reobfuscated/remapped JARs; successful outputs are copied to root `build/release`. Aggregate release uses one number, verifies every expected target and advances only after all succeed. No GTNH pack or GTNH runtime is required merely because the 1.7 compiler uses GTNHGradle/Jabel.

Build/runtime JVMs differ: the retained 1.7 toolchain uses JDK 25 for its build and Jabel compilation; 1.8/1.12 use JDK 21 with Java 8 API/bytecode checks. Players and dedicated servers use Java 8. Optional content registration does not add progression ingredients or world generation. Removing UAB from an existing save is not a supported automatic registry migration; do not silently map its unrelated registry entries.
