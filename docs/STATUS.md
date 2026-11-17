# Development status

Last fully packaged development build: **0.1.0.33**. All three distributions passed compilation, production remapping/packaging and Java 8 archive checks. The current combined item, animation, controls, HUD and audiovisual source batch has not yet been compiled or exercised. The full task remains **NOT RELEASE READY**.


## Current release-development work

### Expanded animation, controls, HUD and audiovisual pass

All three targets remain in scope. The user's six screenshots reproduce defects on 1.7.10 with MCM 0.1.0.33 and UAB 1.1.6; they do not establish the result on other targets. These changes join the unfinished item batch before consolidated acceptance.

| Required subsystem | Current disposition | Planned acceptance |
|---|---|---|
| Spear idle, attack, active use, release/recovery and actual hand | Source diagnosis in progress; correct animation selection and item/model coordinate conversion together | Every spear tier on each target; first person, front/back and observer; main/alternate hand and movement interruptions |
| Shield release and main-sword block isolation | Ordered legacy use acknowledgements, immediate release and missing render-path isolation added to source; not yet verified | Each target; begin/hold/release, sword+shield, item switch, GUI/focus, remote observer and delayed snapshots |
| Elytra invalidation and pose transitions | Water/ladder eligibility and client/server invalidation corrected in source on all targets; not yet verified | Water and ground landing, collisions/cancel, creative flight, death/respawn/dimension/mount and low-pose transitions |
| Swimming/crawling animation and geometry | Native/Combatives-based limb adaptation and optional movement ownership added to source; not yet verified | Each target idle/moving/surface/underwater/entry/exit/clearance, first/third/remote perspectives |
| General state entry/continuation/interruption/exit | Reconcile authoritative state, active hand and visual selection before acceptance | Standing/walk/sprint/sneak/swim/crawl/jump/fall/glide/block/charge/attack/spin/mount/creative/death/respawn; no input required to clear invalid states |
| Attack indicator and general cooldown HUD | IMPLEMENTED; crosshair/hotbar/off and actual item cooldown overlays; BUILD VERIFIED; runtime NOT TESTED | Crosshair/hotbar/off; actual cooldown and attack-speed changes; all timed items in inventories/hotbar, item switching and synchronization |
| Hold/toggle sprint/sneak and creative-flight boost | IMPLEMENTED with GUI sliders, server cap and baseline restoration; BUILD VERIFIED; runtime NOT TESTED; actual Better Sprinting source inspected on all three targets | Config GUI/slider, GUI/focus/lifecycle cleanup, baseline flight-speed restoration and server cap; no survival boost or duplicate Better Sprinting handling |
| Sound and effects | Dedicated native item sound assets and several playback paths added to source; complete event/timing audit pending | Every supported mechanic and weapon class; positional timing/volume/pitch, local/remote playback and no duplication; native particles/framebuffer |
| Optional integration ownership | Combatives retains locomotion, geometry, camera and low-pose model when present; MCM retains its item/combat work; UAB registrations reused | MCM alone, MCM+Combatives, MCM+UAB, all three on 1.7.10; all applicable native/UAB paths on 1.8/1.12; Better Sprinting where supported |

Implementation status is independent from BUILD VERIFIED, AUTOMATED TESTED and RUNTIME VERIFIED. The combined item/animation/controls/HUD/audio batch is BUILD VERIFIED and its shared logic AUTOMATED TESTED (43 cases per target); runtime NOT TESTED. The final ground/ledge/lava source changes are awaiting their combined build. A subset passing on one target will not close this pass. Runtime acceptance begins after the combined implementation-readiness review; confirmed failures are corrected in batches.

### Combined item implementation and acceptance batch

This batch covers the remaining item work across all three targets before routine compilation or runtime acceptance. Source implementation and acceptance are tracked separately. The combined item, animation, controls, HUD and audiovisual source batch has passed independent compilation and 43 shared automated tests on each target. A final ground/ledge/lava correction follows that batch and is awaiting consolidated production validation. Retain native behavior where already correct; derive missing behavior from UAB and inspected native source. Standalone brewing, new recipes and survival acquisition are excluded. Existing recipes and UAB recipe/brewing compatibility remain in scope.

| Item family and relevant boundaries | Implementation readiness | Consolidated acceptance coverage |
|---|---|---|
| Copper/netherite equipment and enchantments: durability, repair, eligibility, configuration and material ownership | IMPLEMENTED; actual repair materials and native costs reviewed; runtime NOT TESTED | Each target; actual anvil/enchanting path, unavailable materials, UAB ownership |
| Shields and legacy offhand: native slot, hand priority, activation/break, wear and presentation | Core behavior previously implemented; combined presentation corrections implemented; runtime NOT TESTED | Each target connected use; legacy standalone/UAB inventory and both hands; tracked observer and break |
| Totems: genuinely lethal rescue, either hand, effects, consumption, statistic and activation presentation | Native-derived legacy presentation added in current source; combined source review completed; native 1.12 retained; runtime NOT TESTED | Each target lethal/blocked rescue, main/offhand, effect reset; connected legacy activation sound/particles/display and observer |
| Anchors: native binding/respawn, charge, blocked exits, ownership and persistence | Core behavior previously implemented; source boundary review completed; final runtime NOT TESTED | Each target native death/respawn and blocked exit; representative UAB path; save/reconnect |
| Combat potions and effects: all delivery forms, duration, spawn constraints and wind interactions | Delivery previously implemented; source effect constraints reviewed/corrected; final runtime NOT TESTED | Every effect family; native/standalone/UAB delivery paths; connected effect/outline presentation and saved state |
| Bow/ammunition and crossbow: loading, ammunition priority, effects, pickup, components and observer presentation | Core behavior previously implemented; both-hand presentation review completed; final runtime NOT TESTED | Each target connected bow/crossbow use, tipped/spectral/plain/rocket representative ammunition, reload/pickup and observer |
| Tridents: throw, collision, Loyalty, Riptide, Channeling, Impaling, wear, pickup, saved/client state | Lifecycle/save/spawn/glint corrections added in current source; UAB NPC/enchantment source review completed; final runtime NOT TESTED | Each target throw/impact/return/spin; UAB NPC path; save/retrack and enchanted projectile/held visuals |
| Wind charges, fireworks and Elytra: use, collision, components, boosting, durability and tracked state | Core behavior previously implemented; remaining component/presentation review pending | Each target connected charge and flight/rocket use; collision/landing; representative UAB owner and observer |
| End crystals: placement transaction, explosion, saved respawn ritual, beams, pillars and recurring dragon lifecycle | Native/UAB-derived legacy ritual added in current source; combined review pending; native 1.12 manager retained | Each target placement/failure/creative rules; completed and aborted ritual; save/reload, beams and repeat-kill reward; affected UAB path |
| Spears/mace: use/contact, wear, enchantments, repair, held/observer presentation | Core behavior previously implemented; remaining component/repair/presentation review pending | Each target connected jab/charge/smash and applicable enchantments; actual hand owner and observer |
| Native combat utilities/consumables: pearls, apples, buckets, blocks, TNT and XP bottles | Existing native/adapted paths; remaining collision/component review pending | Each family explicitly covered on affected targets; shared native paths use representative variants |

Before routine validation, every implementation row must be implemented, already supported by source/evidence, excluded, or genuinely blocked with an explanation. Then perform one combined source/static review, compile affected targets together, and run consolidated acceptance on production artifacts. Record confirmed failures across independent scenarios and batch corrections; retain earlier results only for unchanged executing paths. This is not an absolute one-build rule.

Standalone brewing, new recipes and survival acquisition are excluded from the task. Existing project recipes remain; UAB owns its progression and receives compatibility corrections where needed. The newly added standalone arrow recipes have been removed from current source.

The 0.1.0.29 potion batch started on standalone 1.7/1.8/1.12 and UAB 1.7/1.12. Native air/water travel callbacks verified descending Slow Falling gravity while retaining normal upward gravity on every combination. Legacy raw potion variants, full splash durations and once-only lingering scaling passed; native 1.12 potion registrations have the selected Slow Falling, Turtle Master and strong Slowness durations/amplifiers. New legacy IDs use UAB's persistent cooperative ledger while retaining all four original configured IDs.

The ammunition batch reuses UAB's tipped item, dispenser, bow callbacks, pickup and outline presentation. Production 0.1.0.30 callbacks passed offhand-first native bow use, tipped-arrow consumption despite Infinity, actual raw ammunition NBT, one-eighth custom durations, pickup/full-inventory/creative-only rules, spectral effect identity and dispenser behavior on standalone 1.7/1.8. Native 1.12 bow/custom-effect callbacks passed with and without UAB. On 0.1.0.31, UAB 1.7 passed bow, pickup, its existing recipe's potion data and dispenser checks; its spectral check exposed MCM caching Glowing before UAB registration. The final lookup now occurs at load completion and awaits retest. Current source also preserves potion NBT through UAB's existing lingering conversion.

Actual 0.1.0.31 framebuffers show the corrected filled and empty survival offhand frame/glyph on 1.7, including the inventory position shifted by active effects. The filled UAB inventory also aligns correctly. Both 1.7 clients expose ten custom lingering variants with the registered bottle identity. Earlier creative layout and shield presentation checks remain valid where unchanged; the latest 1.8 startup/presentation check still must pass.

Current source extends the existing rocket lifecycle to native ground-firework damage/components and tests the attempted collision segment before movement for owned rockets. These newest source changes are not yet qualified by production runtime results.

The full release task remains active. UAB-based shield, trident and wind-charge implementations now exist on all three targets. Legacy entity interactions/fishing, crossbow fireworks, independent offhand swings/equip transitions, selected 26.3 tool priority, configuration migration and live effect/enchantment agreement checks are implemented. Riptide has a 20-tick server-owned swept contact attack using the launching hand stack. Shield breakage preserves the other hand and suppresses obsolete 1.12 attacker recoil.

| Evidence on production 0.1.0.22 | 1.7.10 | 1.8.9 | 1.12.2 |
|---|---|---|---|
| Compilation, production remapping/packaging, Java 8 archive checks and existing shared suite | Passed | Passed | Passed |
| Five focused Riptide/tool-priority/snapshot callback scenarios | Passed standalone and UAB 1.1.6 | Passed standalone | Passed standalone and UAB 1.1.6 |
| Native shield-break callback: other hand retained, no obsolete attacker recoil | Passed standalone and UAB 1.1.6 | Passed standalone | Passed standalone and UAB 1.1.6 |
| Connected native right-click cow feeding and authoritative offhand consumption | Passed standalone and UAB 1.1.6 | Passed standalone | Passed standalone |
| Real server save/restart and same-player reconnect, custom offhand NBT and main hand | Passed standalone and UAB 1.1.6 | Passed standalone | Passed standalone |
| Connected shield activation on the authoritative server | Passed standalone and UAB 1.1.6 | Passed standalone | Passed standalone |
| Native shield framebuffer | Captured; legacy hand-transform correction required | Captured; legacy model size/UV correction required | Captured; native model retained |

Subsequent production builds through 0.1.0.27 add UAB-based Mending with actual offhand repair, legacy Sweeping Edge, corrected trident damage classification, shield/trident hand transforms, vanilla offhand artwork and the matching creative armor/player-preview layout. The creative layout was directly observed without overlap on 1.8; final presentation checks on the other legacy combinations remain part of acceptance.

The 0.1.0.23 enchantment callbacks exercised Mending rounding, hand ownership, sweep damage and owned trident armor/toughness on standalone targets and UAB 1.7/1.12. Guardian Impaling was exercised where a native guardian exists; that scenario is inapplicable to standalone 1.7. A connectionless-player callback exposed an unsafe snapshot send, now corrected before touching Forge's shared channel. Production 0.1.0.27 connectionless callbacks and subsequent real 1.12 client connections passed with and without UAB.

UAB-derived native anchor binding/respawn replaces the former post-respawn teleport path while preserving `MCMAnchor` save keys. Production 0.1.0.27 connected native binding/death/respawn passed on all three standalone targets: safe Nether exit, one successful-respawn charge, retained forced spawn and actual offhand data. Native dimension travel retained the offhand, a later spawn change cleared the older binding, and offhand glowstone charged once while preserving the main sword. UAB retains its own native respawn hooks; its obstructed-exit/reconnect qualification remains open.

The 0.1.0.27 production transaction passed all three independent builds, remapping, Java 8 archive checks and the existing 32-case shared suite in each target. Focused runtime batches passed standalone netherite fireproof damage handling, legacy shared-hand pearl cooldown/creative use, and actual lingering-projectile/cloud effects, waiting/reapplication/radius/NBT on applicable standalone/UAB combinations. Native 1.12 potion delivery retains its lifecycle with scoped custom-duration and entity-hit origin corrections. Earlier results are retained only where the implementation is unchanged.

Callback checks are game-thread invocations of production code in real dedicated JVMs. Connected input checks dispatch through the native client right-click path and real connection; they are not complete human-input survival/PvP acceptance. The 32 shared numerical/API cases are the same cases in common and each target, not different suites. No modern-runtime differential, latency, long-session or performance result is claimed by these checks.

## Implemented

- Independent Forge targets, centralized next-build allocation and verified distributable JARs in root `build/release`; shared platform-neutral Java 8 calculations and explicit item-property/cooldown integration API.
- Sided initialization, configuration, coalesced network intents, server-owned player poses, observer tracking and collision-safe dimensions. Initial modern movement/swimming/crawling corrections, feet-anchored eyes and basic third-person poses.
- Scoped legacy melee cooldown/scaling/critical/sprint/sweep foundation and native 1.12 corrections. Known-gear armor/toughness, Strength/Weakness, protection, netherite resistance and owned explosion-resistance rules. Unknown foreign weapons, armor and projectiles retain their defined owner.
- Standalone copper/netherite equipment, seven spear tiers, mace, crossbows, legacy either-hand totems, anchors, placed crystals and four modern effects. UAB registrations are reused and selected behavior is adapted without duplicate ownership. See COMBAT-KIT.md for remaining families.
- Native-source spear jab/kinetic charge, reach/occlusion/contact cooldown, Lunge, mace smash/Density/Breach/Wind Burst, modern owned anchor/crystal explosions and legacy golden-apple effects. Native item consumption is preserved.
- Standalone Elytra on 1.7/1.8, modern scoped flight on all targets, legacy attached rocket entities with boost/star damage, server durability/takeoff validation, .6-block flight box/.4-block eye, tracked wing interpolation and rendered equipment. UAB's chest Elytra shares the canonical flight owner. Foreign gliders are preserved.
- Mandatory Modern Regen **1.0.2.2+** on 1.7. AppleCore owns its food update; 1.8 uses a narrow optional regeneration event bridge, and 1.12 retains native regeneration. AppleSkin's hunger HUD and synchronization are preserved.
- LOTR Legacy combat and knockback are preserved by default, independently of movement and kit configuration. This is a compatibility exception, not modern LOTR combat parity.

## Retained 0.1.0.13 verification

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

## Legacy offhand continuation (0.1.0.16)

Forge 1.7.10 and 1.8.9 now have a player-owned, persistent offhand inventory, appended native container slot, cursor/quick-move handling, F swaps in play and over eligible container slots, explicit active-use ownership, equipment snapshots and basic first/third-person rendering plus a hotbar display. Vanilla inventory/armor indices and registered item identities are preserved. Save/load, keepInventory, death drops and dimension clone rules follow the native player lifecycle. Storage stays available independently of combat configuration.

Supported native/MCM callbacks receive the actual offhand stack. Food completion and its Forge event write the remainder to that hand. Modern managed swords pass use through rather than starting legacy sword blocking; foreign/LOTR sword ownership remains unchanged. Offhand crossbow ammunition and either-hand totems use the same canonical inventory. Spear charge enchantments and wear now follow the active legacy hand, as already implemented on 1.12.2. Unknown foreign items can be stored/rendered; use requires an explicit adapter when their callbacks assume the selected hotbar.

The new work has twelve foundational server callback checks per legacy target: native container/cursor operations, quick-move, swapping/use cancellation, window/armor validation, NBT, clone/keepInventory, native drops, native food completion/remainder, crossbow ammunition, genuinely lethal either-hand totems, snapshot encoding and foreign-use exclusion. Connected clients additionally exercise the registered swap key and native right-click packet path; native framebuffer captures show the inventory slot and both equipped items. These are bounded mod-code checks, not a renewed vanilla, LOTR, UAB, food-mod or full gameplay qualification. An additional actual spear-charge callback on each legacy target checks offhand Fire Aspect and durability while the main spear remains untouched. Creative native packet updates, invalid/survival rejection, server restart/reconnection persistence and survival/creative framebuffer inventory output also pass. Final 0.1.0.16 distributions for all three targets pass production packaging, remapping, Java 8 archive checks and the 32 shared cases in each target. Root common tests pass the same 32 cases; those unchanged results are retained. Final 1.7/1.8 servers and clients run the byte-identical release JARs. The 1.12 runtime was not relaunched in this continuation. The twelve foundational checks and connected food/swap tests were established on 0.1.0.15; their implementation is unchanged in 0.1.0.16, which only relocates the creative slot and updates the build version. Final layout and spear checks run on 0.1.0.16.

Full offhand parity remains unfinished: entity interaction/feeding, native fishing-hook ownership, non-player offhand equipment, exact per-item hand animations, custom inventory/render integration and latency/retracking coverage remain pending. Standalone shields and later native 1.12 shield rules have not been implemented in this continuation. No complete movement/combat parity is claimed.

## Remaining work and next priorities

1. Finish the current potion/item/anchor and presentation acceptance across all three targets; complete offhand dimension transfer, observer re-tracking and representative latency checks.
2. Complete remaining kit gaps: newest ammunition/ground-rocket qualification, totem presentation, crystal fight lifecycle, pearl collision differences, equipment/repair behavior and per-item animations. Standalone acquisition, brewing and new recipes are excluded.
3. Finish source-led combat sweep/enchantment/hurt-time/reach/timing and movement/ledge/step/fluid boundary corrections where native behavior differs. Existing source/numerical checks do not establish complete runtime parity.
4. Finish anchor respawn/obstructed-exit gameplay, UAB disabled-content coverage, 1.8 Modern Regen + AppleCore, representative LOTR/foreign-owner acceptance and connected survival/PvP/flight checks.
5. Perform controlled modern vanilla differential scenarios where the environment supports them, final production packaging, documentation/report matrices and release disposition.

Occupied effect/enchantment IDs are never silently overwritten. Removing UAB from an existing save is not an automatic registry migration. Required missing systems remain release requirements; they have not been reclassified as optional. Current disposition remains **NOT RELEASE READY** while material implementation and acceptance work remains.
