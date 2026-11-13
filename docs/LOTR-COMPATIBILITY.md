# LOTR Legacy compatibility

Target: Forge 1.7.10. Modern Combat & Movement does not depend on LOTR.

## Inspected implementations

- [Maltezak/LOTR-Mod source](https://github.com/Maltezak/LOTR-Mod), `main`, inspected as a technical reference. This repository includes fork changes and is not assumed to be identical to CurseForge's binary.
- [Official CurseForge Update 36.15, file 4091561](https://www.curseforge.com/minecraft/mc-mods/the-lord-of-the-rings-mod-legacy/files/4091561), published 2022-11-16, latest file shown during this investigation. Actual downloaded `LOTRMod-v36.15.jar`, SHA-256 `4f296e749c0d4739ecf859217a526b4218a2a45a768c08d3d551af0d0d3d5635`.
- Selected production classes were decompiled independently, including `LOTRNetHandlerPlayServer`, `LOTREventHandler`, `LOTRWeaponStats`, the transformer and player renderer.

## Why combat preservation is necessary

LOTR installs its own `LOTRNetHandlerPlayServer`. Its attack packet handler applies weapon-specific reach and an independent `lastAttackTime` gate before calling the player attack method. `LOTRWeaponStats.getAttackTimePlayer` uses LOTR weapon speed and a 15-tick player base. Its hurt event handler also changes damage immunity timing and handles faction protection, special armor and weapon effects. Replacing the final player damage formula cannot by itself remove the preceding packet gate.

The default `Compatibility.lotrCombatCompatibility=true` therefore disables MCM's melee cooldown/damage and knockback overrides for the whole 1.7.10 session when `lotr` is present. It preserves LOTR's actual combat owner rather than layering two timing systems. The effective policy is synchronized to clients. Movement and kit behavior remain independently configurable. Set the option false only when deliberately testing coexistence: modern parity under LOTR's packet gate has not been established.

LOTR weapons are not automatically profiled. Its actual `lotr:item.swordMithril` identity returns no MCM profile. The opt-in API permits case-sensitive legacy registry paths, but an explicit item registration does not remove LOTR's network-level timing/reach rules.

LOTR's transformer patches player eating, living armor/death handling and horse travel; inspected patches do not own the player pose methods used here. Its renderer adds shield/alignment features. Source inspection is not proof that every equipment renderer and action works with a low pose.

## Registry compatibility

LOTR reserves potion ID 30 for poisoned drinks. With LOTR present, fresh MCM configuration defaults are Weaving 31, Oozing 27, Infested 29, Wind Charged 28. Without LOTR they are 31, 30, 29, 28. Existing explicit configuration is preserved; occupied IDs fail with an actionable message rather than overwriting another effect or silently renumbering saved effects. Client and server must use matching IDs. Adding LOTR to an existing MCM save may require choosing unused IDs and handling already saved potion identities deliberately.

## Verification boundary

The actual CurseForge binary was launched with the production MCM JAR, UniMixins 0.3.1 and Modern Regen 1.0.0.4 on Java 8. Dedicated-server startup completed. Native callbacks confirmed the safe policy, unknown LOTR sword exclusion, a real mithril sword attack, LOTR's network-handler installation, collision-driven crawling/standing and preservation of its poison potion. The first launch exposed potion ID 30's collision; the documented fresh defaults correct it.

This does not qualify LOTR client rendering, riding, shields, faction battles, custom projectiles, every dimension, or prolonged multiplayer gameplay. Those remain runtime checks. No LOTR or UAB repository was modified.
