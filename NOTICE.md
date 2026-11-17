The Forge 1.7.10 `render/CrossbowItemRenderer.java` component is adapted from Universal Adaptive Backporter's `ItemCrossbowRenderer` and retains LGPL-3.0 licensing. Its source is included in this repository; the original component license is retained in `LICENSES/UAB-LGPL-3.0.txt`. Other project code retains the repository license. UAB is an optional runtime integration; its code is not bundled as a dependency.

The standalone shield item and 1.7 shield renderer, trident items/projectiles/models/renderers, and wind-charge items/projectiles are adapted from Universal Adaptive Backporter under LGPL-3.0. Their source is included, and LICENSES/UAB-LGPL-3.0.txt is included in every target distribution. MCM supplies modern-rule corrections and explicit optional ownership adapters.

The Mending enchantment and experience-repair adapter reuse UAB's Mending and ModEnchantments implementations under LGPL-3.0, with modern random damaged-item selection, XP rounding and actual offhand ownership.

The anchor binding and native respawn hooks are adapted from UAB's RespawnAnchors and anchor mixins under LGPL-3.0. They retain MCM's existing saved binding keys and use each target's native player recreation lifecycle.

The standalone fireproof-item hooks and legacy lingering-potion item, projectile, cloud, dispenser and renderers are adapted from UAB under LGPL-3.0. Cloud application cadence, waiting, effect scaling, radius consumption and persistence are corrected to the selected modern rules.

Legacy allocation of newly added potion effects reuses UAB's cooperative PotionIdRegistry under LGPL-3.0. Existing MCM effects retain their configured numeric identities; new effects use the persistent allocation file and saved-world snapshot.

Legacy tipped/spectral ammunition reuses UAB's ItemArrowTipped, arrow dispenser, bow event callbacks, EntityTippedArrow pickup, spectral texture and glowing outline renderer/helper under LGPL-3.0. An optional adapter preserves potion data through UAB's existing recipe. MCM retains native arrow movement and adapts hand selection, raw effect scaling, exact item ownership, saved ammunition and native rendering APIs independently for each target.

The crawl limb animator is adapted from Combatives by GlowingFederal/HiddenMerit. Its original source-available license is retained in LICENSES/Combatives-Source-Available.txt and included in every target distribution. Combatives remains optional. Its swimming, geometry, camera and crawl implementation owns ordinary locomotion when installed.

Native Minecraft item, particle and HUD artwork and OGG assets retain Mojang ownership. Included selected resources are unchanged; their event namespaces and legacy atlas registration are adapted by MCM. Behavioral adapters are derived from inspected native implementations, including item-use transforms, crystal fight stages, particle lifecycles and flight audio.
