2026-11-12 06:30 — Initialize Modern Combat & Movement

Retain ExampleMod's independent Forge 1.7.10, 1.8.9 and 1.12.2 builds, shared Java 8 mechanics and transactional version management. Establish modern pose validation, water travel, player-state synchronization, client pose/camera rendering, scoped combat foundations and exact item-property registration. Require Modern Regen on 1.7.10 and keep UAB optional. Define the modern combat toolkit as content scope without adding progression materials or world generation.


2026-11-12 23:25 — Add scoped modern kit behavior and legacy compatibility

Add standalone crossbows with saved ammunition, Quick Charge, Multishot and initial Piercing, plus main-hand lethal-damage totems on legacy targets. Reuse UAB item/enchantment registrations through optional late adapters and suppress its pre-armor totem rescue. Add Weaving, Oozing, Infested and Wind Charged effects, native web factors, chargeable anchors and scoped anchor explosions without progression materials or world generation. Preserve LOTR combat by default and avoid its reserved potion ID. Correct legacy collision-vector ownership, UniMixins loading and client model field access. Retain exact opt-in item profiles and document incomplete toolkit and parity coverage.


2026-11-13 00:01 — Adapt crossbow equipment rendering and legacy eye coordinates

Use UAB's equipment rendering as the reference for standalone crossbow sprite extrusion, charge/hold arm poses and charged/charging models. Preserve its existing item renderer when UAB is present, retain the adapted renderer's component license, and convert display transforms for each native rendering engine. Correct 1.7 local-player eye coordinates for picking, water queries and opaque-block detection so collision-driven crawling does not put the camera inside the ceiling. Keep spear animations independently scoped.


2026-11-13 05:50 — Expand modern combat equipment and add Elytra rocket flight

Add copper equipment, seven spear tiers, mace and their selected native-source combat/animation rules across all three targets. Reuse UAB identities through explicit adapters. Add scoped modern armor/toughness/protection, owned anchor/crystal/wind explosions, crystal placement and legacy golden-apple effects while preserving native food callbacks. Correct legacy hurt knockback strength, dedicated-server item-use access, Forge armor hooks and offhand spear ownership. Align the 1.8 Mixin compiler/runtime ABI.

Add standalone legacy Elytra, modern validated flight and tracked wing rendering, plus attached vanilla-item rocket boosts and star damage. Reuse UAB Elytra without duplicate travel, wear or wing layers; adapt native 1.12 flight and rockets, including player-versus-head direction and default rocket duration. Keep foreign item subclasses and projectiles outside native item hooks. Retain Java 8 and version-matched networking. Require the AppleCore-aware Modern Regen 1.0.2.2+ build on 1.7 and use AppleCore regeneration events on 1.8 without replacing its food update or AppleSkin HUD.


2026-11-13 07:05 � Add persistent legacy offhand equipment and active-hand ownership

Add real player offhand storage on Forge 1.7.10 and 1.8.9, preserving vanilla inventory/armor indices and item identities. Integrate native container interactions, F swaps, save/load, keepInventory/dimension cloning, death drops and clear commands. Synchronize equipment and active hand to owners/observers, with basic first/third-person equipment rendering and an offhand hotbar display. Extend the native creative slot layout and packet path with server authority and stack validation.

Route supported item-use completion and remainders to their actual hand without replacing the selected hotbar. Add offhand crossbow ammunition, either-hand totem rescue and correct active-hand spear enchantment/wear ownership. Managed modern swords no longer invoke legacy blocking; foreign/LOTR combat remains its own owner. Supply explicit foreign-item use adapters and retain native 1.12 offhand storage. Standalone shields and complete entity/item animation parity remain separate work.

2026-11-16 04:49 — Complete shield ownership and expand the UAB-based combat kit

Adapt UAB shield, trident and wind-charge implementations for all three targets, preserving existing UAB registrations and content-disable settings. Add scoped offhand animal interactions, fishing ownership, independent swing/equip state and crossbow firework ammunition. Apply modern shield delay, directional reduction, durability, axe disabling and explosion handling. Preserve the captured offhand when a shield breaks, and prevent obsolete attacker recoil after native 1.12 shield breakage. Tools yield to offhand blocking under the selected 26.3 interaction rule.

Add server-owned Riptide spin contact attacks and synchronized poses while retaining the actual launching hand stack. Correct Impaling's aquatic targets and owned trident armor classification. Adapt UAB Mending to damaged-item selection, modern XP rounding and the real offhand; add legacy Sweeping Edge without replacing occupied IDs. Retain native 1.12 enchantment identities and validate live client/server effect and enchantment assignments.

Separate shield, projectile, offhand and flight configuration with migration of existing equipment/flight settings. Match vanilla creative offhand placement, inventory artwork, empty-slot marker and hotbar frame on legacy targets. Replace incorrect legacy shield/bow hand transforms for the selected shield/trident models while retaining foreign rendering paths and the UAB-derived geometry.

2026-11-16 15:56 — Native anchor lifecycle and UAB potion/item completion

Reused UAB's native anchor binding and death-respawn stages while preserving existing MCM save keys, safe exits and one-charge consumption. Corrected legacy client block-event ordering and offhand glowstone charging. Matched vanilla's creative armor/player-preview layout around the offhand slot. Adapted UAB fireproof-item hooks for exact standalone netherite equipment and retained UAB flammability ownership. Legacy pearls now support creative use and a shared one-second cooldown. Added UAB-derived legacy lingering bottles, tracked projectiles/clouds, dispensers and rendering with modern effect scaling, waiting, reapplication, radius consumption and saved ownership; native 1.12 potion delivery receives scoped cloud corrections.


2026-11-16 16:46 — Combat potion completion and inventory alignment

Added Slow Falling, extended Slow Falling, Turtle Master variants and strong Slowness with native drink/splash/lingering delivery. Slow Falling now controls native air/fluid gravity, owned swimming/gliding gravity and fall-distance reset. New legacy effects reuse UAB's persistent cooperative allocator while existing configured effect IDs remain fixed and late overwrites are rejected. Corrected legacy splash duration and localized custom potion names. Preserved UAB lingering item identity in creative variants. The survival offhand frame follows the actual container position when active effects shift the inventory.

2026-11-16 17:33 — UAB ammunition delivery and native offhand frame

Adapt UAB tipped/spectral items, dispensers, recipes, outline rendering and native bow callbacks on legacy targets. Preserve actual ammunition through pickup and persistence, select offhand ammunition first, and apply modern raw custom-effect scaling while retaining UAB's single-effect API format. Keep native 1.12 ammunition with a scoped custom-duration correction. Correct the inherited inventory-screen field access and 1.8 Slow Falling effect category.

2026-11-16 21:08 — Keep standalone acquisition outside MCM scope

Remove the newly added standalone tipped/spectral arrow recipes. Retain existing project recipes and the adapter for raw potion data in UAB's existing tipped-arrow recipe. Standalone brewing and survival acquisition are outside this project's scope.

2026-11-16 21:32 — Native ground fireworks and UAB data ownership

Reuse owned rocket damage and collision calculations for native ground fireworks while preserving native entity identities and saves. Correct default components, retain a one-item snapshot and player attribution, and test the attempted collision segment before movement. Resolve UAB Glowing after registration, correct legacy accessor selection, retain named tipped-arrow presentation, and preserve original potion NBT through UAB's existing lingering conversion before its brewed event.

2026-11-17 04:28 — Complete the shared item presentation, controls and feedback paths

Adapt native spear use/attack selection, item pivots and actual-hand poses; correct stale shield/offhand release and sword-block isolation; clear invalid gliding states and reuse Combatives' legacy swim/crawl patterns with explicit optional ownership. Add hold/toggle movement controls, server-capped temporary creative-flight boost, attack indicators and actual item cooldown overlays. Reuse native item/equipment/flight sounds, gust/sweep/smash effects and UAB block hooks; retain one authoritative playback and movement owner. Complete projectile, repair, totem and saved crystal-ritual lifecycle corrections while preserving existing identities and excluding standalone acquisition changes.
