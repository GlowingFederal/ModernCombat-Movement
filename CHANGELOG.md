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
