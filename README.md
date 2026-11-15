# ModernCombat&Movement
Modern combat mechanics and movement, including swimming and crawling for legacy versions.

Targets: Forge 1.7.10, 1.8.9 and 1.12.2, running Java 8. The reference is vanilla Java Edition 1.21.11, with verified later corrections tracked separately. This is an early development build; [implementation status](docs/STATUS.md) and [combat kit coverage](docs/COMBAT-KIT.md) distinguish implemented behavior from planned parity.

Install matching mod versions on the client and server. Forge 1.7.10 requires UniMixins 0.3.1 and Modern Regen 1.0.2.2 or newer; 1.12.2 requires MixinBooter 11.18. The 1.8.9 distribution includes its Mixin runtime. UAB is optional: its existing item registrations are reused through explicit adapters. [LOTR Legacy compatibility](docs/LOTR-COMPATIBILITY.md) preserves LOTR combat by default. Unknown modded weapons and projectiles retain their own behavior unless an integration explicitly registers them.

Build all targets with `python scripts/build.py all`, or one target with `python scripts/build.py build 1.7.10` (substitute another supported version). PowerShell and shell entry points are also in `scripts`. Builds require Python 3, JDK 25 for 1.7.10 and JDK 21 for 1.8.9/1.12.2; packaged bytecode remains Java 8. Set `JAVA25_HOME` and `JAVA21_HOME` if the launchers cannot find those JDKs. `version.properties` manages the version and next build number; standard distributable JARs go to `build/release`.

See the [mechanics parity matrix](docs/PARITY.md) and [technical architecture and integration API](docs/ARCHITECTURE.md) for source references, ownership and development conventions. Configuration uses Forge's native configuration files; legacy potion and enchantment IDs must match between client and server.

On 1.7.10 and 1.8.9, the offhand slot appears beside the player in the inventory and is saved with the player. Press **F** to swap hands, or hover an eligible container slot and press F to exchange it with the offhand. The key can be rebound in Controls. Native/MCM supported items use the offhand when the main interaction passes; crossbows can take its arrows and totems can activate from either hand. Modded items can be stored and rendered, while use may require an explicit integration. Shields, entity interactions and full per-item animation parity remain under development; see the coverage matrix.
