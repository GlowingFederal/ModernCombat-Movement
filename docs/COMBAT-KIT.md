# Modern combat kit coverage

The scope includes the complete modern combat toolkit, not only netherite. This table separates existing legacy content, implemented additions and pending migration. Presence in UAB is not evidence of modern behavioral parity.

| Family | Current implementation | Remaining work |
|---|---|---|
| Native swords, axes and tools | Exact native profiles; cooldown/scaling/crit/sprint/sweep foundation; known-source armor/toughness, Strength/Weakness and protection corrections | Full sweep eligibility, enchantment interactions and later weapon-component audit |
| Netherite / copper equipment | Standalone tools/armor; UAB identities reused; modern known-gear armor/toughness and netherite knockback resistance | Full durability, repair, fire-immunity and component audit; no new ingots/ores |
| Crossbows | Standalone all targets; explicit UAB adapters on 1.7/1.12; 25-tick charging, Quick Charge, persistent ammunition and offhand selection, Multishot, initial Piercing, native owned arrow entity | Firework ammunition, exact piercing/shield and pickup behavior, full first-person charge/reload/swing acceptance and remaining arm animation transitions |
| Totems | Standalone either-hand rescue on 1.7/1.8 after genuinely lethal damage; native 1.12 totems; UAB 1.7 pre-armor handler suppressed | Modern visual/sound/stat presentation and full effect-removal audit |
| Offhand | Native 1.12 storage retained; real legacy player storage/container slot, save/clone/drop lifecycle, F swaps, equipment/use snapshots, native/MCM use callbacks, offhand ammunition/totems and basic rendering | Entity interactions/fishing hooks, non-player equipment, exact per-item animations, custom inventories/renderers and latency/tracking acceptance |
| Shields | Native 1.12 systems retained where equivalent; UAB 1.7 shield remains its owner | Standalone legacy shields, explicit UAB offhand shield adapter, later shield differences on every target and 26.3 shield/hoe/shovel priority |
| Cobwebs / Weaving | Reuse native web; modern Weaving movement factors and initial death-web generation | Complete placement/death-effect differential tests; no duplicate cobweb content |
| Modern effects | Weaving, Oozing, Infested, Wind Charged; creative drink/splash delivery on legacy; native potion types on 1.12 | Remaining wind explosion interaction audit, exact spawning constraints, legacy lingering delivery and missing modern effects |
| Respawn anchors | Standalone chargeable block, initial Nether respawn storage, modern damage coefficient for owned blasts; UAB registration reused and explosion adapted | Full water/flowing-fluid exposure, light/comparator parity and actual respawn/obstructed exit gameplay acceptance |
| End crystals | Legacy standalone placement item and placed entity; native 1.12 item; UAB registration reused; one-air placement, modern owned explosion and no explosive chain | Complete respawn/fight lifecycle and multiplayer gameplay qualification |
| Spears / mace / tridents | Seven spear tiers and mace on all targets; explicit UAB adapters; piercing jab/kinetic charge, Lunge, smash/Density/Breach/Wind Burst; native-source animation curves | Tridents remain pending; complete enchantment, visuals, combat timing and offhand acceptance |
| Wind charges | Initial Wind Charged death gust only | Standalone charges, projectile collision, wind burst, triggerable blocks and fall-distance rules |
| Arrows / bow | Existing arrows and bow remain native; owned crossbow accepts explicit native/UAB tipped/spectral ammunition | Modern bow launch/damage audit, standalone missing ammunition, full pickup/potion persistence |
| Golden apples / combat potions | Modern normal/enchanted apple effect durations and absorption on legacy; native food callback preserved; native 1.12 effects retained | Turtle Master, Slow Falling, missing delivery forms and complete food-mod interaction qualification |
| Pearls, buckets, blocks, TNT, XP bottles | Existing native content remains present | Modern use restrictions, cooldowns, collision, explosions and component changes where they diverge |
| Elytra / rockets | Standalone 1.7/1.8 Elytra, UAB identity bridge; modern scoped flight all targets; legacy attached rockets, boost/star damage, tracked wings and glint | Crossbow rockets, legacy ground-rocket damage, complete sounds/component rules and long-session multiplayer gameplay |

New standalone equipment has no ores, ingots, biomes, world generation or unrelated survival progression dependencies. The crossbow uses sticks, iron, string and a tripwire hook. Other unsupported acquisition chains are not fabricated. Content registration is suppressed by UAB presence, while missing behavioral effects can still be supplied by MCM. Removing UAB from an existing world is not an automatic registry migration.

Unknown foreign item attacks and projectiles keep their own implementation. The public item-profile API is explicit opt-in; adapters still have to own the relevant behavior without double damage or velocity processing. See ARCHITECTURE.md.
