# Architecture and ownership

The root build coordinates three independent Forge build islands. `common` contains Java 8, platform-neutral mechanics and public item-profile contracts. It contains no Minecraft, Forge, client or UAB classes. Each target compiles the shared source alongside its own native adapter, following the ExampleMod structure. No source preprocessing or runtime wrappers translate one target's entity APIs into another's.

| Subsystem | Canonical responsibility |
|---|---|
| movement | Shared verified arithmetic; target water travel replaces the applicable player branch exactly once. Native movement/collision remains authoritative for preserved paths. |
| pose | Player-attached state, collision-tested desired/fallback poses, anchored dimensions, exact gameplay eyes, lifecycle exclusions. Crawling is a low pose without the swimming movement flag. |
| combat | Exact item lookup, attack-speed attribute input, attack-strength progression, damage/critical/sweep rules. Legacy managed attacks have one dispatch owner; 1.12 retains equivalent native stages and receives targeted corrections. |
| knockback | Modern resistance and grounded/airborne velocity arithmetic, entered only for explicitly managed sources. Preserve the Forge event when the target actually supplies it. |
| offhand | Native 1.12 inventory stays canonical. Legacy `OffhandInventory` is a separate player-owned one-slot inventory, appended to the native player container; it does not renumber InventoryPlayer or armor slots. `Offhands` owns storage, lifecycle and validated swaps; `OffhandUses` dispatches supported callbacks without substituting the selected hotbar. |
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

## Combat kit ownership

`Crossbows` owns charged-ammunition NBT, launch and explicit enchantments. `CrossbowBolt` retains each target's native arrow collision/lifecycle and adapts only this owned entity. Ordinary/modded arrow instances do not opt in through inheritance or shooter equipment. `Totems` handles legacy death rescue after lethal damage; 1.12 uses its native death-protection owner. Optional late Mixins select the exact UAB crossbow class and suppress its legacy pre-armor totem check, while preserving UAB registration and client assets.

`Anchors` owns selected anchor interactions; `AnchorBlast` scopes explosion changes to those blasts. `KitEffects` owns effects missing from the inspected UAB potion table. Native web collision marks the stuck source so Weaving does not change unrelated modded slowdown. The server kit policy gates these actions and effect behavior. This grouping is a compatibility switch, not an alternate balance profile.

See COMBAT-KIT.md for content coverage and STATUS.md for evidence limits. A registered item kind or subsystem package is not a completed implementation.

## Equipment rendering and 1.7 eye coordinates

UAB's `ItemCrossbowRenderer`, `CrossbowPose` and charging-action Mixin are the technical equipment references. The 1.7 standalone crossbow renderer is an explicitly attributed LGPL component adaptation; UAB keeps its existing renderer when installed. Native generated item models own 1.8/1.12 rendering. All three adapters supply exact crossbow charge/hold arm poses and distinguish charged ammunition from charging icons. Spears are a separate animation migration; this work does not transplant UAB spear poses.

Native hand matrices differ: 1.8's held-item layer translates by `(-1/16,7/16,1/16)`, whereas 1.12 rotates `X=-90,Y=180` then translates `(1/16,1/8,-5/8)`. The 1.8 model display is converted between those bases, including its first-person `.4` scale; copying the 1.12 JSON unchanged produces the wrong orientation. The 1.7 adapter removes native legacy hand scaling before applying the official `.68` first-person and `.9` third-person model scales.

Forge 1.7's local player `posY` includes its standing `yOffset`; its default `getEyeHeight()` is a relative `.12` value. Server and observer players use different conventions. The adapter preserves that getter contract via `getDefaultEyeHeight() + poseEye - 1.62`, anchors picking to interpolated box feet plus the pose eye, and corrects native fluid/opaque-block eye sampling. Camera smoothing stays independent. Returning the same absolute getter on every 1.7 player falsely places local crawl queries in the ceiling and forces first person. World-space eyes, rather than raw getter/posY values across targets, are the parity measurement.

Later modularity pass (requested): provide UAB-style module and content controls for modpack authors, retain modern vanilla defaults, and validate coupled feature settings together. This is separate from the combat-kit implementation batch.


## Gliding ownership and food compatibility

`FlightHooks` validates known chest-slot Elytra, owns native entity flag 7 and the server durability counter. `FlightTravel` replaces only the owned air-travel branch; native collision and fluid movement remain authoritative. Shared `FlightMath` contains the inspected 1.21.11 equations. Takeoff requests are coalesced on the network thread and consumed on the server tick thread. Protocol 3 adds the appended fall-flying pose and flight policy bit; mismatched distribution versions cannot connect. Legacy standing eye-coordinate conventions still apply while gliding.

Standalone Elytra exist on 1.7/1.8; UAB suppresses their registration. An optional bridge makes UAB's glider getter/setter/tick consume the same canonical state, cancels duplicate flight travel and wear, and preserves external non-chest gliders. Native 1.12 Elytra remain registered by Minecraft, with the modern lifecycle and air physics adapted. `BoostRocket` owns attached vanilla-item rockets on legacy targets, with native tracking/spawn data, owner credit, star damage and NBT. Native ground-launched fireworks and arbitrary foreign projectiles retain their implementation. Crossbow firework ammunition is not yet supported.

`WingAnimation` advances once per player tick; render interpolation does not mutate it. Version renderers supply wing geometry, cape/Elytra skin selection and glint. Rendering is separate from the authoritative .6-block hitbox and .4-block eye. `Movement.elytraFlight` disables owned flight as a coupled unit; the movement subsystem must also be enabled. Foreign glider implementations retain their owner when this feature is disabled.

Golden apple adaptation changes the effect callback without replacing native item consumption or `FoodStats.addStats(ItemFood, ItemStack)`. This preserves AppleCore's food API and AppleSkin's observation path. Forge 1.7 requires Modern Regen 1.0.2.2+, whose actual transformer detects AppleCore and uses its regeneration events. On 1.8, the fallback native regeneration Mixin is omitted when AppleCore's API is present; a narrow optional event adapter adjusts only regeneration period, healing and exhaustion. AppleCore retains its update, timers, cancellation and synchronization. An installed Modern Regen remains the regeneration owner. Forge 1.12 retains native regeneration. MCM does not replace the hunger HUD, food capability, or AppleSkin network channel.

## Legacy offhand integration

`OffhandAccess` attaches storage lazily, including during native player-container construction. Player NBT uses the namespaced `MCMOffhand` compound. Native clone rules copy the stack without aliasing for dimension travel and keepInventory; native inventory drops and clear commands include the extra equipment. Existing vanilla slot numbers stay stable. Creative inventory has a scoped layout and native-packet extension for the actual appended slot; it validates creative authority and stack bounds on the owning server thread.

`OffhandAction` and `OffhandUse` carry intent, never survival item stacks. Handlers coalesce one pending value per authenticated player and consume it on the player tick; release uses the same use queue so a queued start cannot outlive a later release. Logout removes pending entries. Swaps validate the window, cursor, slot constraints and interaction access. Block use rechecks the server ray, reach, edit permission and Forge interaction event. Equipment snapshots include dimension, entity ID, UUID, stack and active-hand use time; client ticks preserve equal stack references and apply accepted equipment/use state. Native single-hand observer inference is prevented from replacing an accepted offhand use with main-hand use.

Target-specific integrations may call `OffhandUses.register(Item, Adapter)` during initialization. `Adapter.use` returns the actual hand remainder; `useOnBlock` receives its actual stack and validated block coordinates. Duplicate adapters fail. No global `getHeldItem` override or temporary hotbar replacement is used. Unknown foreign items retain storage and rendering but do not automatically opt into legacy use callbacks that may mutate the main hand. Entity-use adapters and native fishing-hook adaptation are still pending. The callback API does not claim that every native or foreign item family has complete modern behavior.
