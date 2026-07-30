# Lucky TNT Mod — Empirical Runtime Performance Benchmark (Fabric / MC 26.2)

*Live document — regenerated after every batch. Last update: **2026-07-30 04:36 UTC**.*

**Coverage so far: 157 of 203 TNT variants measured on a real dedicated server.**

## Headline

- **44 of 157** measured TNTs made the server fall behind 20 TPS (a tick budget is 50 ms).
- Worst single tick observed: **`tnt_x10000` at 24092 ms** — 482x the 50 ms tick budget.
- Most entities spawned: **`particle_physics_tnt` — 5848 entities** within 400 blocks.
- Idle baseline across all runs: **1.20 ms/tick** median (superflat, 225 forceloaded chunks, no players).
- **3 TNTs never returned to baseline** before the sampling cap expired: `particle_physics_tnt`, `honey_tnt`, `spamming_tnt`.

### Worst freezes — single worst tick

| rank | TNT | peak ms/tick | x tick budget | shape |
|---|---|---:|---:|---|
| 1 | `tnt_x10000` | **24092** | 482x | single spike |
| 2 | `hydrogen_bomb` | **12094** | 242x | single spike |
| 3 | `tsar_bomba` | **6973** | 139x | single spike |
| 4 | `colossal_tnt` | **6601** | 132x | single spike |
| 5 | `tnt_x2000` | **4232** | 85x | single spike |
| 6 | `flying_tnt` | **4146** | 83x | single spike |
| 7 | `nether_tnt` | **3152** | 63x | single spike |
| 8 | `supernova` | **2395** | 48x | single spike |
| 9 | `jungle_tnt` | **1994** | 40x | single spike |
| 10 | `atlantis` | **1635** | 33x | few big ticks |
| 11 | `hyperion` | **1568** | 31x | single spike |
| 12 | `black_hole_tnt` | **1434** | 29x | few big ticks |

### Worst brown-outs — sustained load (100-tick rolling mean)

A tick budget is 50 ms. Anything here above 50 ms held the server below
20 TPS continuously, which players feel as lasting lag rather than a freeze.

| rank | TNT | sustained ms/tick | duration (ticks) | peak entities | shape |
|---|---|---:|---:|---:|---|
| 1 | `tnt_x10000` | **242.4** | 800 | 4 | single spike |
| 2 | `hydrogen_bomb` | **123.0** | 400 | 4 | single spike |
| 3 | `particle_physics_tnt` | **88.8** | 1755 | 5848 | sustained |
| 4 | `tsar_bomba` | **73.2** | 397 | 4 | single spike |
| 5 | `colossal_tnt` | **67.1** | 719 | 4 | single spike |
| 6 | `tnt_x2000` | **43.6** | 717 | 4 | single spike |
| 7 | `flying_tnt` | **42.9** | 347 | 4 | single spike |
| 8 | `fluorine_tnt` | **42.2** | 718 | 4 | sustained |
| 9 | `grande_finale` | **40.7** | 2998 | 3742 | sustained |
| 10 | `black_hole_tnt` | **37.8** | 816 | 2001 | few big ticks |
| 11 | `atlantis` | **36.8** | 706 | 43 | few big ticks |
| 12 | `nether_tnt` | **36.2** | 496 | 4 | single spike |

### Biggest entity storms

| rank | TNT | peak live entities | sustained ms/tick | peak ms/tick |
|---|---|---:|---:|---:|
| 1 | `particle_physics_tnt` | **5848** | 88.8 | 353 |
| 2 | `grande_finale` | **3742** | 40.7 | 104 |
| 3 | `winter_tnt` | **2040** | 27.4 | 1218 |
| 4 | `black_hole_tnt` | **2001** | 37.8 | 1434 |
| 5 | `spamming_tnt` | **1973** | 19.8 | 45 |
| 6 | `physics_tnt` | **1651** | 17.6 | 55 |
| 7 | `icy_tnt` | **840** | 7.0 | 80 |
| 8 | `honey_tnt` | **661** | 27.9 | 209 |
| 9 | `solar_eruption` | **450** | 6.2 | 14 |
| 10 | `animal_kingdom` | **321** | 26.2 | 313 |
| 11 | `picky_tnt` | **289** | 4.0 | 50 |
| 12 | `pompeii` | **264** | 3.9 | 13 |

---

## Methods

These are **real measurements taken from a live headless Fabric dedicated server**,
not estimates and not a synthetic proxy. Every number below came out of the server's
own `/tick query` telemetry over RCON.

### Stack under test

| Component | Version |
|---|---|
| Minecraft (dedicated server) | **26.2** |
| Fabric Loader | **0.19.3** |
| Fabric Installer / server launcher | 1.1.2 (`fabric-server-launch.jar` from `meta.fabricmc.net`) |
| Fabric API | **0.156.0+26.2** |
| Lucky TNT Mod | **6.0** — prebuilt `dist/fabric-luckytntmod-26.2-6.0.jar` (unmodified) |
| LuckyTNTLib | **0.100.6.1** — prebuilt `dist/fabric-luckytntlib-26.2-0.100.6.1.jar` (unmodified) |
| JVM | OpenJDK **25.0.3+9** (Ubuntu), G1GC |
| Host | 4 vCPU, 15 GiB RAM, Linux 6.18.5 |

JVM flags: `-Xms2G -Xmx6G -XX:+UseG1GC`

### Server configuration

```properties
level-type=minecraft:flat        # superflat: stable, terrain-noise-free baseline
online-mode=false
view-distance=10
simulation-distance=10
spawn-protection=0
max-tick-time=-1                 # watchdog OFF - a slow TNT must not kill the server
pause-when-empty-seconds=0       # CRITICAL: without this an empty server stops ticking
enable-rcon=true / port 25575
generate-structures=false
sync-chunk-writes=false
gamemode=creative, difficulty=normal
```

Gamerules applied before every run (26.2 renamed these to snake_case):

```
advance_time false          advance_weather false
spawn_monsters false        spawn_mobs false
spawn_patrols false         spawn_phantoms false
spawn_wandering_traders false
log_admin_commands false    send_command_feedback true
```

Natural mob spawning is disabled so that entity counts and tick cost are
attributable to the TNT and to the creepers we place deliberately.

> **Gotcha worth recording:** on 26.2 a dedicated server with zero players online
> *stops ticking entirely* after `pause-when-empty-seconds` (default 60). The first
> attempt at this benchmark produced a perfectly flat 12.2 ms/t trace and zero game-tick
> progress because of it. Any headless MC benchmark must set this to `0`.

### Per-TNT procedure

No players ever log in. Each TNT is detonated at its own isolated site so craters,
lava, fires and leftover entities can never contaminate the next measurement.

1. `forceload remove all`, `kill @e[type=!player]`.
2. Site for TNT *i* is `x = 3000 + 2000*i, y = -60, z = 0` — **2000 blocks apart**.
3. `forceload add x-112 z-112 x+112 z+112` (225 chunks — forceloaded chunks tick fully,
   which is what substitutes for a player being present).
4. `time set day`, `weather clear`, wait for ≥60 game ticks of chunk-gen settle,
   then `kill @e[type=!player]` again.
5. **Baseline:** 12 samples at 200 ms; the mean of the last 9 is the baseline ms/tick.
6. **Three creepers** are summoned around the TNT at (±3, 0) / (0, ±3) —
   `/summon minecraft:creeper` — to exercise the entity/mob interaction paths
   (knockback, damage, `Explosion#getEntities`, creeper chain-ignition).
7. **Arming uses the real gameplay path**, not `/summon` of a primed entity:
   `/setblock <x> <y> <z> luckytntmod:<id>` followed by
   `/setblock <x+1> <y> <z> minecraft:redstone_block`, which powers the block and
   primes it through the mod's own `LTNTBlock` code. This matters — `/summon`ing
   `PrimedLTNT` directly yields the *vanilla default* 80-tick fuse instead of the
   mod's configured fuse (verified: `/summon luckytntmod:tnt_x100` → `fuse: 80s`,
   whereas redstone-priming `luckytntmod:black_hole_tnt` → `fuse: 500s`).
   The handful of variants with no placeable block fall back to `/summon`
   and are flagged in the table.
8. **Sampling:** every ~200 ms until `fuse + 300` game ticks have elapsed *and*
   five consecutive samples are back near baseline, with a hard wall-clock cap.
   Each sample issues three RCON commands:
   - `tick query` → `Average time per tick`, `P50/P95/P99` over the trailing 100 ticks
   - `time query gametime` → the true game-tick counter
   - every 4th sample, `execute positioned <site> if entity @e[type=!player,distance=..400]`
     → live entity count near the blast
9. `kill @e[type=!player]`, `forceload remove all`.

### What the columns mean

- **baseline avg ms/t** — server-thread work per tick with the site loaded and idle.
- **peak avg ms/t** — highest `Average time per tick` (rolling 100-tick mean) observed.
  This is *sustained* load.
- **peak p99 ms/t** — highest P99 observed. Because P99 of a 100-tick window is
  effectively that window's worst tick, this is the best available proxy for the
  **single worst tick** and is what the table is sorted by.
- **mean avg ms/t** — mean of all `Average time per tick` samples across the event.
- **worst wall ms/t** — worst real wall-clock milliseconds per *game* tick between two
  consecutive samples (`Δwall / Δgametime`). At a healthy 20 TPS this is 50 ms.
  Values well above 50 ms mean the server genuinely lost time; this is derived from the
  game-tick counter, so it is immune to the 100-tick averaging window.
- **ticks obs** — game ticks elapsed between arming and the last sample.
- **peak entities** — max non-player entities within 400 blocks of the site
  (includes the 3 creepers and the primed TNT itself, so the floor is ~4).
- **behind?** — server logged `Can't keep up!`, or wall-clock exceeded 100 ms/tick.

### Known limitations

- The single-tick peak is inferred from P99 of a 100-tick window, so a spike shorter
  than one sampling interval is captured in magnitude but its exact tick index is
  only localised to ±~4 game ticks.
- RCON sampling itself runs on the server thread and costs a fraction of a millisecond
  per sample; it is present identically in baseline and event windows.
- Superflat has no trees, water, caves or ores. Terrain-dependent effects
  (ore-seeking, tree-eating, water/lava flow) will read *lower* here than on a normal
  world. Where a second normal-world pass was run it is reported separately.
- Trigger-fused variants (bouncing/jumping/leaping/mimic/sensor TNT, fuse ≥ 5000)
  never detonate on a timer with nothing to collide with; their rows measure the
  *idle-primed* cost only and are flagged.

---

## Results — sorted by peak ms/tick (worst first)

`peak p99` is the worst single-tick cost seen in any 100-tick window during the
event (the true spike). `peak avg` is the worst 100-tick rolling mean (sustained
load). `worst wall` is real wall-clock ms per game tick — anything above 50 ms
means the server was **not** keeping up with 20 TPS.

| # | TNT | effect class | fuse | baseline avg ms/t | peak avg ms/t | peak p99 ms/t | mean avg ms/t | worst wall ms/t | shape | ticks obs | peak entities | behind? | notes |
|---|-----|--------------|------|------------------:|--------------:|--------------:|--------------:|----------------:|-------|----------:|--------------:|:-------:|-------|
| 1 | `tnt_x10000` | `TNTXStrengthEffect` | 480 | 1.20 | 242.4 | 24091.5 | 31.18 | 12077 | single spike | 800 | 4 | **YES** | 1 'Can't keep up' events |
| 2 | `hydrogen_bomb` | `DropProjectileTNTEffect` | 80 | 1.27 | 123.0 | 12093.9 | 31.66 | 6079 | single spike | 400 | 4 | **YES** | 1 'Can't keep up' events |
| 3 | `tsar_bomba` | `DropProjectileTNTEffect` | 80 | 2.06 | 73.2 | 6972.6 | 18.92 | 3521 | single spike | 397 | 4 | **YES** | 1 'Can't keep up' events |
| 4 | `colossal_tnt` | `ColossalTNTEffect` | 400 | 1.07 | 67.1 | 6601.1 | 10.04 | 6638 | single spike | 719 | 4 | **YES** | 1 'Can't keep up' events |
| 5 | `tnt_x2000` | `TNTXStrengthEffect` | 400 | 1.11 | 43.6 | 4232.5 | 6.89 | 4256 | single spike | 717 | 4 | **YES** | 1 'Can't keep up' events |
| 6 | `flying_tnt` | `StackedPrimedTNTEffect` | 200 | 1.81 | 42.9 | 4146.4 | 13.21 | 1425 | single spike | 347 | 4 | **YES** | 1 'Can't keep up' events |
| 7 | `nether_tnt` | `NetherTNTEffect` | 180 | 1.09 | 36.2 | 3152.5 | 8.14 | 827 | single spike | 496 | 4 | **YES** | 1 'Can't keep up' events |
| 8 | `supernova` | `SupernovaEffect` | 300 | 1.08 | 25.0 | 2394.6 | 4.72 | 834 | single spike | 616 | 4 | **YES** | 1 'Can't keep up' events |
| 9 | `jungle_tnt` | `JungleTNTEffect` | 160 | 1.53 | 21.3 | 1994.4 | 5.68 | 540 | single spike | 306 | 4 | **YES** |  |
| 10 | `atlantis` | `AtlantisEffect` | 240 | 1.48 | 36.8 | 1634.9 | 8.46 | 588 | few big ticks | 706 | 43 | **YES** |  |
| 11 | `hyperion` | `HyperionEffect` | 140 | 1.62 | 17.3 | 1568.5 | 5.34 | 445 | single spike | 290 | 4 | **YES** |  |
| 12 | `black_hole_tnt` | `BlackHoleTNTEffect` | 500 | 2.26 | 37.8 | 1433.9 | 11.29 | 754 | few big ticks | 816 | 2001 | **YES** |  |
| 13 | `giant_tnt` | `GiantTNTEffect` | 320 | 0.91 | 14.5 | 1349.3 | 2.60 | 311 | single spike | 637 | 4 | **YES** |  |
| 14 | `winter_tnt` | `WinterTNTEffect` | 200 | 1.28 | 27.4 | 1217.8 | 14.06 | 454 | few big ticks | 347 | 2040 | **YES** |  |
| 15 | `stone_cold` | `StoneColdEffect` | 140 | 1.40 | 12.5 | 1121.0 | 4.41 | 328 | single spike | 288 | 4 | **YES** |  |
| 16 | `flat_earth` | `FlatTNTEffect` | 80 | 0.98 | 11.4 | 1030.8 | 3.69 | 379 | single spike | 318 | 4 | **YES** |  |
| 17 | `hungry_tnt` | `HungryTNTEffect` | 600 | 1.20 | 10.7 | 969.2 | 1.87 | 981 | single spike | 918 | 4 | **YES** |  |
| 18 | `pulsar_tnt` | `PulsarTNTEffect` | 400 | 1.42 | 27.4 | 848.6 | 5.45 | 633 | few big ticks | 716 | 4 | **YES** |  |
| 19 | `aether_tnt` | `AetherTNTEffect` | 200 | 1.51 | 8.5 | 750.8 | 2.26 | 229 | single spike | 517 | 4 | **YES** |  |
| 20 | `fluorine_tnt` | `FluorineTNTEffect` | 400 | 1.13 | 42.2 | 739.1 | 11.17 | 396 | sustained | 718 | 4 | **YES** |  |
| 21 | `tnt_x500` | `TNTXStrengthEffect` | 240 | 1.20 | 8.4 | 738.0 | 2.24 | 228 | single spike | 558 | 4 | **YES** |  |
| 22 | `nuclear_tnt` | `NuclearTNTEffect` | 200 | 1.23 | 8.6 | 736.1 | 2.52 | 292 | single spike | 517 | 7 | **YES** |  |
| 23 | `levitating_tnt` | `StackedPrimedTNTEffect` | 160 | 0.91 | 8.0 | 708.2 | 2.95 | 217 | single spike | 308 | 4 | **YES** |  |
| 24 | `russian_roulette` | `RussianRouletteEffect` | 160 | 2.46 | 6.8 | 556.9 | 2.74 | 185 | single spike | 309 | 4 | **YES** |  |
| 25 | `wither_storm` | `WitherStormEffect` | 160 | 1.11 | 11.9 | 485.3 | 3.93 | 172 | few big ticks | 2998 | 180 | **YES** |  |
| 26 | `fiery_hell` | `FieryHellEffect` | 240 | 1.03 | 7.1 | 479.2 | 2.39 | 196 | few big ticks | 556 | 16 | **YES** |  |
| 27 | `graveyard_tnt` | `StackedPrimedTNTEffect` | 80 | 0.99 | 5.3 | 440.3 | 2.69 | 157 | single spike | 226 | 4 | **YES** |  |
| 28 | `dividing_tnt` | `DividingTNTEffect` | 80 | 1.07 | 7.2 | 397.8 | 3.02 | 150 | few big ticks | 278 | 103 | **YES** |  |
| 29 | `sculk_tnt` | `SculkTNTEffect` | 80 | 1.48 | 5.0 | 378.6 | 2.63 | 397 | single spike | 229 | 4 | **YES** |  |
| 30 | `flower_forest_tnt` | `FlowerForestTNTEffect` | 160 | 0.90 | 4.7 | 373.6 | 2.00 | 134 | single spike | 309 | 4 | **YES** |  |
| 31 | `illuminati_tnt` | `IlluminatiTNTEffect` | 120 | 2.37 | 4.9 | 357.8 | 2.49 | 138 | single spike | 269 | 4 | **YES** |  |
| 32 | `particle_physics_tnt` | `ParticlePhysicsTNTEffect` | 160 | 1.28 | 88.8 | 352.8 | 14.41 | 277 | sustained | 1755 | 5848 | **YES** | 1 'Can't keep up' events |
| 33 | `animal_tnt` | `AnimalTNTEffect` | 80 | 1.10 | 15.8 | 339.6 | 5.32 | 439 | sustained | 1338 | 101 | **YES** |  |
| 34 | `animal_kingdom` | `AnimalKingdomEffect` | 80 | 2.32 | 26.2 | 312.7 | 9.78 | 173 | sustained | 2997 | 321 | **YES** |  |
| 35 | `poseidons_wave` | `PoseidonsWaveEffect` | 180 | 1.10 | 11.6 | 287.2 | 4.25 | 142 | sustained | 478 | 23 | **YES** |  |
| 36 | `firestorm_tnt` | `FirestormTNTEffect` | 160 | 1.06 | 4.3 | 281.9 | 1.79 | 113 | few big ticks | 477 | 4 | **YES** |  |
| 37 | `plantation_tnt` | `PlantationTNTEffect` | 160 | 3.19 | 3.7 | 266.9 | 1.66 | 114 | single spike | 476 | 4 | **YES** |  |
| 38 | `wasteland_tnt` | `WastelandTNTEffect` | 180 | 1.01 | 3.8 | 226.0 | 1.77 | 100 | few big ticks | 326 | 4 | no |  |
| 39 | `geode_tnt` | `GeodeTNTEffect` | 80 | 1.37 | 3.4 | 225.9 | 1.97 | 103 | few big ticks | 226 | 4 | **YES** |  |
| 40 | `floating_tnt` | `StackedPrimedTNTEffect` | 80 | 1.09 | 3.3 | 215.0 | 1.95 | 218 | few big ticks | 226 | 4 | **YES** |  |
| 41 | `honey_tnt` | `HoneyTNTEffect` | 120 | 1.06 | 27.9 | 209.2 | 18.93 | 141 | sustained | 1799 | 661 | **YES** |  |
| 42 | `reset_tnt` | `?` | 80 | 1.49 | 3.1 | 202.3 | 1.61 | 250 | few big ticks | 396 | 4 | **YES** |  |
| 43 | `tnt_x100` | `TNTXStrengthEffect` | 200 | 1.18 | 2.9 | 186.7 | 1.43 | 110 | few big ticks | 519 | 4 | **YES** |  |
| 44 | `hellfire_tnt` | `HellfireTNTEffect` | 160 | 1.06 | 3.9 | 185.9 | 1.82 | 96 | few big ticks | 306 | 7 | no |  |
| 45 | `asteroid_belt` | `AsteroidBeltEffect` | 160 | 1.00 | 2.7 | 174.5 | 1.32 | 85 | few big ticks | 309 | 33 | no |  |
| 46 | `hells_gate` | `HellsGateEffect` | 140 | 1.48 | 2.6 | 147.9 | 1.32 | 89 | few big ticks | 456 | 4 | no |  |
| 47 | `world_of_wools` | `WorldOfWoolsEffect` | 150 | 1.98 | 3.8 | 138.2 | 1.92 | 106 | few big ticks | 469 | 83 | **YES** |  |
| 48 | `end_gate` | `EndGateEffect` | 140 | 1.22 | 5.3 | 126.7 | 2.39 | 76 | sustained | 288 | 83 | no |  |
| 49 | `mountaintop_removal` | `MountaintopRemovalEffect` | 180 | 0.97 | 2.2 | 120.9 | 1.27 | 86 | few big ticks | 329 | 4 | no |  |
| 50 | `snowstorm_tnt` | `SnowstormTNTEffect` | 160 | 0.90 | 2.7 | 120.8 | 1.60 | 78 | few big ticks | 309 | 114 | no |  |
| 51 | `chunk_tnt` | `ChunkTNTEffect` | 160 | 1.16 | 2.1 | 113.2 | 1.23 | 69 | few big ticks | 308 | 4 | no |  |
| 52 | `tnt_x20` | `TNTXStrengthEffect` | 160 | 1.70 | 2.1 | 108.0 | 1.35 | 86 | few big ticks | 308 | 4 | no |  |
| 53 | `grande_finale` | `GrandeFinaleEffect` | 440 | 1.07 | 40.7 | 104.5 | 15.20 | 142 | sustained | 2998 | 3742 | **YES** |  |
| 54 | `compact_tnt` | `StackedPrimedTNTEffect` | 120 | 1.11 | 1.9 | 93.6 | 1.11 | 72 | few big ticks | 268 | 4 | no |  |
| 55 | `withering_tnt` | `WitheringTNTEffect` | 160 | 1.14 | 3.4 | 92.4 | 1.73 | 71 | few big ticks | 308 | 42 | no |  |
| 56 | `brickhouse_tnt` | `HouseTNTEffect` | 80 | 2.39 | 2.2 | 89.8 | 1.61 | 68 | few big ticks | 227 | 4 | no |  |
| 57 | `grove_tnt` | `GroveTNTEffect` | 80 | 1.46 | 2.1 | 87.5 | 1.57 | 52 | few big ticks | 229 | 4 | no |  |
| 58 | `catalyst_tnt` | `CatalystTNTEffect` | 340 | 1.03 | 8.8 | 86.8 | 2.89 | 72 | sustained | 657 | 4 | no |  |
| 59 | `nether_grove_tnt` | `NetherGroveTNTEffect` | 80 | 1.49 | 1.8 | 81.2 | 1.26 | 71 | few big ticks | 228 | 4 | no |  |
| 60 | `icy_tnt` | `IcyTNTEffect` | 180 | 0.93 | 7.0 | 80.1 | 4.07 | 64 | sustained | 327 | 840 | no |  |
| 61 | `end_tnt` | `EndTNTEffect` | 160 | 1.02 | 2.2 | 76.3 | 1.33 | 70 | few big ticks | 477 | 27 | no |  |
| 62 | `lush_tnt` | `LushTNTEffect` | 80 | 1.18 | 2.2 | 76.3 | 1.60 | 65 | few big ticks | 227 | 4 | no |  |
| 63 | `gotthard_tunnel` | `GotthardTunnelEffect` | 200 | 1.12 | 1.7 | 73.6 | 0.97 | 57 | few big ticks | 516 | 4 | no |  |
| 64 | `heavens_gate` | `HeavensGateEffect` | 140 | 0.88 | 1.6 | 70.9 | 1.09 | 58 | few big ticks | 287 | 4 | no |  |
| 65 | `mineral_tnt` | `MineralTNTEffect` | 150 | 1.21 | 1.6 | 67.4 | 1.11 | 51 | few big ticks | 298 | 4 | no |  |
| 66 | `dripstone_tnt` | `DripstoneTNTEffect` | 80 | 1.36 | 1.9 | 63.3 | 1.50 | 58 | few big ticks | 226 | 4 | no |  |
| 67 | `wool_tnt` | `WoolTNTEffect` | 80 | 2.03 | 1.5 | 62.9 | 0.89 | 57 | few big ticks | 227 | 4 | no |  |
| 68 | `global_disaster` | `GlobalDisasterEffect` | 240 | 1.10 | 1.4 | 62.2 | 0.93 | 52 | few big ticks | 556 | 4 | no |  |
| 69 | `zombie_apocalypse` | `ZombieApocalypseEffect` | 80 | 1.21 | 4.0 | 60.2 | 2.31 | 52 | sustained | 227 | 70 | no |  |
| 70 | `death_ray` | `DeathRayEffect` | 480 | 1.16 | 8.1 | 56.0 | 2.08 | 52 | sustained | 798 | 4 | no |  |
| 71 | `ghost_tnt` | `GhostTNTEffect` | 150 | 1.22 | 1.8 | 55.7 | 1.36 | 52 | few big ticks | 298 | 4 | no |  |
| 72 | `physics_tnt` | `PhysicsTNTEffect` | 80 | 1.50 | 17.6 | 54.6 | 7.40 | 66 | sustained | 277 | 1651 | no |  |
| 73 | `sinkhole_tnt` | `SinkholeTNTEffect` | 250 | 1.14 | 11.3 | 51.4 | 3.38 | 53 | sustained | 569 | 4 | no |  |
| 74 | `picky_tnt` | `PickyTNTEffect` | 80 | 1.38 | 4.0 | 50.2 | 2.38 | 61 | sustained | 227 | 289 | no |  |
| 75 | `easter_egg` | `EasterEggEffect` | 120 | 6.76 | 4.7 | 45.3 | 1.82 | 54 | sustained | 267 | 46 | no |  |
| 76 | `spamming_tnt` | `SpammingTNTEffect` | 160 | 0.98 | 19.8 | 45.1 | 11.29 | 57 | sustained | 1797 | 1973 | no |  |
| 77 | `tetrahedron_tnt` | `TetrahedronTNTEffect` | 100 | 1.30 | 1.4 | 44.7 | 1.07 | 54 | few big ticks | 248 | 4 | no |  |
| 78 | `ocean_tnt` | `OceanTNTEffect` | 160 | 1.19 | 4.8 | 44.3 | 2.19 | 59 | sustained | 327 | 13 | no |  |
| 79 | `xray_tnt` | `XRayTNTEffect` | 160 | 1.64 | 1.7 | 40.9 | 1.35 | 51 | sustained | 306 | 4 | no |  |
| 80 | `jumping_tnt` | `JumpingTNTEffect` | 100000 | 0.93 | 2.0 | 39.1 | 1.38 | 55 | sustained | 1217 | 12 | no | trigger-fused (no timer detonation) |
| 81 | `continental_drift` | `ContinentalDriftEffect` | 480 | 1.67 | 1.4 | 38.6 | 1.02 | 57 | few big ticks | 799 | 4 | no |  |
| 82 | `disintegrating_tnt` | `DisintegratingTNTEffect` | 200 | 1.48 | 8.6 | 36.4 | 3.17 | 51 | sustained | 375 | 53 | no |  |
| 83 | `air_strike` | `AirStrikeEffect` | 360 | 1.23 | 7.2 | 35.6 | 3.50 | 60 | sustained | 679 | 144 | no |  |
| 84 | `reversed_tnt` | `ReversedTNTEffect` | 140 | 0.91 | 1.2 | 31.9 | 0.94 | 50 | few big ticks | 289 | 4 | no |  |
| 85 | `inverted_tnt` | `InvertedTNTEffect` | 120 | 1.01 | 1.3 | 28.3 | 1.00 | 56 | sustained | 266 | 4 | no |  |
| 86 | `leaping_tnt` | `LeapingTNTEffect` | 100000 | 1.56 | 1.3 | 25.3 | 1.06 | 52 | sustained | 1218 | 4 | no | trigger-fused (no timer detonation) |
| 87 | `lava_ocean_tnt` | `LavaOceanTNTEffect` | 160 | 1.13 | 1.4 | 24.9 | 1.05 | 51 | sustained | 307 | 4 | no |  |
| 88 | `woodhouse_tnt` | `HouseTNTEffect` | 80 | 1.24 | 1.3 | 24.6 | 1.01 | 51 | sustained | 229 | 4 | no |  |
| 89 | `sensor_tnt` | `SensorTNTEffect` | 5000 | 1.40 | 1.2 | 24.4 | 0.90 | 58 | sustained | 1217 | 4 | no | trigger-fused (no timer detonation) |
| 90 | `meteor_storm` | `MeteorStormEffect` | 720 | 1.10 | 2.1 | 23.9 | 1.45 | 60 | sustained | 1036 | 31 | no |  |
| 91 | `compressed_tnt` | `StackedPrimedTNTEffect` | 240 | 1.18 | 1.2 | 23.1 | 0.98 | 57 | sustained | 556 | 4 | no |  |
| 92 | `floating_island` | `FloatingIslandEffect` | 120 | 1.20 | 1.5 | 22.8 | 1.20 | 54 | sustained | 266 | 4 | no |  |
| 93 | `swap_tnt` | `SwapTNTEffect` | 120 | 1.42 | 1.0 | 22.6 | 0.89 | 51 | sustained | 266 | 4 | no |  |
| 94 | `flak_tnt` | `FlakTNTEffect` | 800 | 0.98 | 1.1 | 21.9 | 0.88 | 55 | sustained | 1119 | 4 | no |  |
| 95 | `the_revolution` | `TheRevolutionEffect` | 140 | 0.93 | 7.0 | 21.7 | 2.49 | 51 | sustained | 398 | 121 | no |  |
| 96 | `reaction_tnt` | `ReactionTNTEffect` | 240 | 1.04 | 2.2 | 18.7 | 1.06 | 50 | sustained | 560 | 4 | no |  |
| 97 | `structure_tnt` | `StructureTNTEffect` | 160 | 1.29 | 1.0 | 17.4 | 0.89 | 51 | sustained | 476 | 4 | no |  |
| 98 | `timer_tnt` | `StackedPrimedTNTEffect` | 80 | 1.01 | 1.1 | 15.9 | 0.93 | 54 | sustained | 226 | 4 | no |  |
| 99 | `cobblestone_house_tnt` | `HouseTNTEffect` | 80 | 1.32 | 1.4 | 15.8 | 1.24 | 51 | sustained | 229 | 4 | no |  |
| 100 | `pulse_tnt` | `PulseTNTEffect` | 300 | 0.98 | 1.3 | 15.7 | 0.93 | 57 | sustained | 617 | 4 | no |  |
| 101 | `toxic_clouds` | `DisasterTNTEffect` | 80 | 1.17 | 1.3 | 15.5 | 1.14 | 53 | sustained | 229 | 4 | no |  |
| 102 | `big_tnt` | `BigTNTEffect` | 120 | 1.40 | 1.4 | 15.0 | 1.07 | 51 | sustained | 266 | 4 | no |  |
| 103 | `chemical_tnt` | `ChemicalTNTEffect` | 80 | 1.34 | 1.9 | 14.5 | 1.23 | 54 | sustained | 230 | 33 | no |  |
| 104 | `midas_tnt` | `MidasTNTEffect` | 160 | 1.32 | 1.6 | 14.2 | 1.20 | 51 | sustained | 306 | 4 | no |  |
| 105 | `dense_tnt` | `StackedPrimedTNTEffect` | 160 | 0.99 | 1.1 | 14.0 | 0.97 | 56 | sustained | 307 | 4 | no |  |
| 106 | `miningflat_tnt` | `MiningflatTNTEffect` | 80 | 1.00 | 1.2 | 13.8 | 1.01 | 55 | sustained | 229 | 4 | no |  |
| 107 | `solar_eruption` | `SolarEruptionEffect` | 360 | 0.92 | 6.2 | 13.7 | 3.10 | 54 | sustained | 677 | 450 | no |  |
| 108 | `pompeii` | `PompeiiEffect` | 80 | 1.89 | 3.9 | 13.4 | 1.78 | 57 | sustained | 226 | 264 | no |  |
| 109 | `spiral_tnt` | `SpiralTNTEffect` | 80 | 1.21 | 2.2 | 13.1 | 1.49 | 51 | sustained | 229 | 15 | no |  |
| 110 | `earthquake_tnt` | `EarthquakeTNTEffect` | 280 | 1.30 | 1.3 | 12.3 | 1.11 | 59 | sustained | 597 | 4 | no |  |
| 111 | `dust_bowl` | `DustBowlEffect` | 120 | 1.24 | 1.0 | 12.1 | 0.88 | 51 | sustained | 266 | 4 | no |  |
| 112 | `doomsday` | `DisasterTNTEffect` | 80 | 1.13 | 0.9 | 12.1 | 0.85 | 52 | sustained | 231 | 4 | no |  |
| 113 | `eye_of_the_sahara` | `EyeOfTheSaharaEffect` | 120 | 1.73 | 2.0 | 12.0 | 1.49 | 55 | sustained | 267 | 63 | no |  |
| 114 | `pumpkin_bomb` | `PumpkinBombEffect` | 80 | 1.57 | 2.2 | 11.7 | 1.59 | 51 | sustained | 227 | 244 | no |  |
| 115 | `erupting_tnt` | `EruptingTNTEffect` | 80 | 1.42 | 1.7 | 11.1 | 1.21 | 51 | sustained | 226 | 23 | no |  |
| 116 | `nuclear_waste_tnt` | `NuclearWasteTNTEffect` | 80 | 1.49 | 1.9 | 11.1 | 1.47 | 51 | sustained | 230 | 4 | no |  |
| 117 | `meteor_shower` | `MeteorShowerEffect` | 720 | 1.69 | 2.5 | 10.5 | 1.87 | 58 | sustained | 1037 | 114 | no |  |
| 118 | `lightning_storm` | `LightningStormEffect` | 160 | 1.17 | 1.5 | 10.4 | 1.12 | 52 | sustained | 306 | 69 | no |  |
| 119 | `squaring_tnt` | `SquaringTNTEffect` | 80 | 0.98 | 1.1 | 10.2 | 0.93 | 52 | sustained | 226 | 7 | no |  |
| 120 | `christmas_tnt` | `StackedPrimedTNTEffect` | 80 | 1.24 | 1.5 | 10.1 | 1.13 | 51 | sustained | 226 | 8 | no |  |
| 121 | `replay_tnt` | `?` | 80 | 1.32 | 1.5 | 9.7 | 1.32 | 52 | sustained | 399 | 4 | no |  |
| 122 | `unbreakable_tnt` | `UnbreakableTNTEffect` | 80 | 1.36 | 1.4 | 9.5 | 1.26 | 53 | sustained | 226 | 4 | no |  |
| 123 | `heat_death` | `DisasterTNTEffect` | 80 | 1.06 | 0.9 | 8.8 | 0.87 | 52 | sustained | 229 | 4 | no |  |
| 124 | `helix` | `HelixEffect` | 140 | 1.01 | 1.4 | 8.7 | 1.01 | 58 | sustained | 286 | 109 | no |  |
| 125 | `tnt_x5` | `TNTXStrengthEffect` | 120 | 1.18 | 1.2 | 8.5 | 1.13 | 51 | sustained | 266 | 4 | no |  |
| 126 | `farming_tnt` | `FarmingTNTEffect` | 80 | 1.16 | 1.3 | 8.4 | 1.18 | 55 | sustained | 229 | 4 | no |  |
| 127 | `acidic_tnt` | `AcidicTNTEffect` | 80 | 0.91 | 2.8 | 6.9 | 1.44 | 51 | sustained | 398 | 73 | no |  |
| 128 | `fire_tnt` | `FireTNTEffect` | 80 | 1.51 | 1.3 | 6.5 | 1.17 | 51 | sustained | 229 | 4 | no |  |
| 129 | `ring_tnt` | `RingTNTEffect` | 80 | 1.29 | 1.4 | 6.3 | 1.23 | 55 | sustained | 226 | 15 | no |  |
| 130 | `hexahedron` | `HexahedronEffect` | 140 | 0.89 | 0.9 | 6.2 | 0.86 | 54 | sustained | 286 | 4 | no |  |
| 131 | `bouncing_tnt` | `BouncingTNTEffect` | 100000 | 1.19 | 1.2 | 6.1 | 1.14 | 51 | sustained | 1217 | 4 | no | trigger-fused (no timer detonation) |
| 132 | `eating_tnt` | `EatingTNTEffect` | 400 | 1.10 | 1.2 | 6.1 | 1.11 | 51 | sustained | 719 | 4 | no |  |
| 133 | `toxic_cloud` | `ToxicCloudEffect` | 1200 | 0.97 | 1.3 | 5.8 | 1.07 | 52 | sustained | 1217 | 4 | no | trigger-fused (no timer detonation), summoned (no block) |
| 134 | `tunneling_tnt` | `TunnelingTNTEffect` | 80 | 1.08 | 1.0 | 5.8 | 0.91 | 52 | sustained | 227 | 4 | no |  |
| 135 | `random_tnt` | `RandomTNTEffect` | 120 | 1.23 | 1.2 | 5.6 | 1.08 | 50 | sustained | 269 | 4 | no |  |
| 136 | `knockback_tnt` | `KnockbackTNTEffect` | 300 | 1.07 | 1.3 | 4.6 | 1.13 | 52 | sustained | 617 | 4 | no |  |
| 137 | `multiplying_tnt` | `MultiplyingTNTEffect` | 80 | 1.48 | 1.3 | 3.9 | 1.22 | 50 | sustained | 229 | 20 | no |  |
| 138 | `vaporize_tnt` | `VaporizeTNTEffect` | 80 | 1.27 | 1.3 | 3.6 | 1.22 | 59 | sustained | 226 | 4 | no |  |
| 139 | `tnt_rain` | `DisasterTNTEffect` | 80 | 1.40 | 1.2 | 3.6 | 1.18 | 53 | sustained | 229 | 4 | no |  |
| 140 | `redstone_tnt` | `RedstoneTNTEffect` | 80 | 0.91 | 0.9 | 3.4 | 0.84 | 50 | sustained | 229 | 3 | no |  |
| 141 | `flat_tnt` | `FlatTNTEffect` | 80 | 1.22 | 1.3 | 3.4 | 1.17 | 58 | sustained | 227 | 4 | no |  |
| 142 | `ice_age` | `DisasterTNTEffect` | 80 | 1.20 | 1.2 | 3.2 | 1.09 | 52 | sustained | 230 | 4 | no |  |
| 143 | `lightning_tnt` | `LightningTNTEffect` | 200 | 0.92 | 1.5 | 3.1 | 1.11 | 51 | sustained | 347 | 21 | no |  |
| 144 | `kola_borehole_tnt` | `KolaBoreholeTNTEffect` | 200 | 1.09 | 1.1 | 3.0 | 1.07 | 54 | sustained | 516 | 4 | no |  |
| 145 | `mimic_tnt` | `MimicTNTEffect` | 20000 | 1.03 | 1.2 | 2.5 | 1.03 | 51 | sustained | 1217 | 4 | no | trigger-fused (no timer detonation) |
| 146 | `static_tnt` | `StackedPrimedTNTEffect` | 80 | 1.20 | 1.2 | 2.3 | 1.11 | 51 | sustained | 226 | 4 | no |  |
| 147 | `drilling_tnt` | `DrillingTNTEffect` | 80 | 1.30 | 1.1 | 2.2 | 1.07 | 54 | sustained | 230 | 4 | no |  |
| 148 | `cannon_tnt` | `CannonTNTEffect` | 600 | 1.29 | 1.2 | 2.1 | 1.10 | 56 | sustained | 919 | 4 | no |  |
| 149 | `gravity_tnt` | `GravityTNTEffect` | 300 | 1.34 | 1.2 | 2.1 | 1.13 | 51 | sustained | 616 | 4 | no |  |
| 150 | `prism_tnt` | `PrismTNTEffect` | 80 | 1.64 | 1.0 | 2.1 | 0.87 | 50 | sustained | 226 | 4 | no |  |
| 151 | `disaster_clearer` | `DisasterTNTEffect` | 80 | 1.29 | 1.3 | 1.9 | 1.25 | 51 | sustained | 230 | 4 | no |  |
| 152 | `tnt` | `TNTXStrengthEffect` | 80 | 0.99 | 0.9 | 1.8 | 0.85 | 52 | sustained | 229 | 4 | no |  |
| 153 | `digging_tnt` | `DiggingTNTEffect` | 80 | 0.89 | 0.9 | 1.8 | 0.83 | 50 | sustained | 230 | 4 | no |  |
| 154 | `say_goodbye` | `SayGoodbyeEffect` | 80 | 1.19 | 1.2 | 1.8 | 1.15 | 53 | sustained | 227 | 4 | no |  |
| 155 | `smoke_tnt` | `SmokeTNTEffect` | 520 | 1.20 | 1.2 | 1.7 | 1.09 | 50 | sustained | 837 | 4 | no |  |
| 156 | `turret_tnt` | `TurretTNTEffect` | 400 | 1.14 | 1.2 | 1.6 | 1.11 | 54 | sustained | 717 | 4 | no |  |
| 157 | `custom_tnt` | `CustomTNTEffect` | 80 | 0.93 | 0.9 | 1.5 | 0.81 | 58 | sustained | 226 | 4 | no |  |

## Detail — worst offenders

### `tnt_x10000`  (`TNTXStrengthEffect`)

- site `[65000, -60, 0]`, armed via **block+redstone**, declared fuse **480** ticks
- baseline **1.20 ms/t** (p99 16.50) -> peak avg **242.4 ms/t**, peak p99 **24091.5 ms/t**
- observed window: 800 game ticks over 64.0 s wall; worst wall-clock **12077 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.00  p99=    1.70  ents=4
t=    12  avg=   1.00  p99=    1.50  ents=
t=    24  avg=   1.00  p99=    1.40  ents=
t=    36  avg=   1.00  p99=    1.40  ents=
t=    48  avg=   1.00  p99=    1.40  ents=4
t=    60  avg=   1.10  p99=    1.40  ents=
t=    72  avg=   1.10  p99=    1.50  ents=
t=    84  avg=   1.10  p99=    1.50  ents=
t=    96  avg=   1.10  p99=    1.50  ents=4
t=   108  avg=   1.20  p99=    1.50  ents=
t=   121  avg=   1.10  p99=    1.50  ents=
t=   133  avg=   1.20  p99=    1.50  ents=
t=   145  avg=   1.10  p99=    1.50  ents=4
t=   157  avg=   1.10  p99=    1.50  ents=
t=   169  avg=   1.10  p99=    1.50  ents=
t=   181  avg=   1.10  p99=    1.50  ents=
t=   193  avg=   1.10  p99=    1.50  ents=4
t=   205  avg=   1.10  p99=    1.50  ents=
t=   217  avg=   1.20  p99=    1.50  ents=
t=   229  avg=   1.20  p99=    1.50  ents=
t=   241  avg=   1.20  p99=    2.60  ents=4
t=   254  avg=   1.20  p99=    2.60  ents=
t=   266  avg=   1.20  p99=    2.60  ents=
t=   278  avg=   1.20  p99=    2.60  ents=
t=   290  avg=   1.20  p99=    2.60  ents=4
t=   302  avg=   1.20  p99=    2.60  ents=
t=   314  avg=   1.20  p99=    2.60  ents=
t=   326  avg=   1.20  p99=    2.60  ents=
t=   338  avg=   1.20  p99=    2.60  ents=4
t=   350  avg=   1.20  p99=    1.60  ents=
t=   362  avg=   1.10  p99=    1.50  ents=
t=   374  avg=   1.10  p99=    1.50  ents=
t=   387  avg=   1.10  p99=    1.50  ents=4
t=   399  avg=   1.10  p99=    1.50  ents=
t=   411  avg=   1.20  p99=    1.50  ents=
t=   423  avg=   1.20  p99=    1.50  ents=
t=   435  avg=   1.20  p99=    1.50  ents=4
t=   447  avg=   1.20  p99=    1.50  ents=
t=   459  avg=   1.20  p99=    1.50  ents=
t=   471  avg=   1.20  p99=    1.50  ents=
t=   481  avg= 242.10  p99=24091.50  ents=1
t=   493  avg= 242.40  p99=24091.50  ents=
t=   505  avg= 242.40  p99=24091.50  ents=
t=   518  avg= 242.30  p99=24091.50  ents=
t=   530  avg= 242.30  p99=24091.50  ents=1
t=   542  avg= 242.30  p99=24091.50  ents=
t=   554  avg= 242.30  p99=24091.50  ents=
t=   566  avg= 242.20  p99=24091.50  ents=
t=   578  avg= 242.20  p99=24091.50  ents=1
t=   590  avg=   0.90  p99=    1.40  ents=
t=   602  avg=   0.90  p99=    1.40  ents=
t=   614  avg=   0.80  p99=    1.40  ents=
t=   626  avg=   0.80  p99=    1.40  ents=1
t=   639  avg=   0.70  p99=    1.40  ents=
t=   651  avg=   0.70  p99=    1.00  ents=
t=   663  avg=   0.70  p99=    1.00  ents=
t=   675  avg=   0.70  p99=    1.00  ents=1
t=   687  avg=   0.70  p99=    1.00  ents=
t=   699  avg=   0.70  p99=    1.00  ents=
t=   711  avg=   0.70  p99=    1.00  ents=
t=   723  avg=   0.70  p99=    1.00  ents=1
t=   735  avg=   0.70  p99=    1.00  ents=
t=   747  avg=   0.70  p99=    1.00  ents=
t=   759  avg=   0.70  p99=    1.00  ents=
t=   771  avg=   0.70  p99=    1.00  ents=1
t=   783  avg=   0.70  p99=    1.00  ents=
t=   796  avg=   0.70  p99=    0.90  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ██████████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (1 .. 4)
0:4 16:4 32:4 48:4 64:4 80:4 96:4 112:4 129:4 145:4 161:4 177:4 193:4 209:4 225:4 241:4 258:4 274:4 290:4 306:4 322:4 338:4 354:4 370:4 387:4 403:4 419:4 435:4 451:4 467:4 481:1 497:1 514:1 530:1 546:1 562:1 578:1 594:1 610:1 626:1 643:1 659:1 675:1 691:1 707:1 723:1 739:1 755:1 771:1 788:1
```

Server fell behind:

```
[03:38:29] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 24061ms or 481 ticks behind
```

### `hydrogen_bomb`  (`DropProjectileTNTEffect`)

- site `[7000, -60, 0]`, armed via **block+redstone**, declared fuse **80** ticks
- baseline **1.27 ms/t** (p99 21.30) -> peak avg **123.0 ms/t**, peak p99 **12093.9 ms/t**
- observed window: 400 game ticks over 32.0 s wall; worst wall-clock **6079 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.90  p99=    2.10  ents=4
t=     4  avg=   0.90  p99=    2.10  ents=
t=     8  avg=   0.90  p99=    2.00  ents=
t=    13  avg=   0.90  p99=    2.00  ents=
t=    17  avg=   1.00  p99=    4.10  ents=4
t=    21  avg=   1.00  p99=    4.10  ents=
t=    25  avg=   1.00  p99=    4.10  ents=
t=    29  avg=   1.00  p99=    4.10  ents=
t=    33  avg=   1.10  p99=    4.10  ents=4
t=    37  avg=   1.10  p99=    4.10  ents=
t=    41  avg=   1.10  p99=    4.10  ents=
t=    45  avg=   1.10  p99=    4.10  ents=
t=    49  avg=   1.10  p99=    4.10  ents=4
t=    53  avg=   1.10  p99=    4.10  ents=
t=    57  avg=   1.20  p99=    4.10  ents=
t=    61  avg=   1.20  p99=    4.10  ents=
t=    65  avg=   1.20  p99=    4.10  ents=4
t=    69  avg=   1.20  p99=    4.10  ents=
t=    73  avg=   1.30  p99=    4.10  ents=
t=    77  avg=   1.30  p99=    4.10  ents=
t=    81  avg=   1.30  p99=    4.10  ents=4
t=    85  avg=   1.30  p99=    4.10  ents=
t=    89  avg=   1.30  p99=    4.10  ents=
t=    93  avg=   1.40  p99=    4.10  ents=
t=    97  avg=   1.40  p99=    4.10  ents=4
t=   101  avg=   1.40  p99=    4.10  ents=
t=   105  avg=   1.40  p99=    4.10  ents=
t=   107  avg= 122.30  p99=12093.90  ents=
t=   112  avg= 122.90  p99=12093.90  ents=3
t=   116  avg= 122.90  p99=12093.90  ents=
t=   120  avg= 122.90  p99=12093.90  ents=
t=   125  avg= 122.90  p99=12093.90  ents=
t=   129  avg= 122.90  p99=12093.90  ents=2
t=   133  avg= 122.90  p99=12093.90  ents=
t=   137  avg= 122.90  p99=12093.90  ents=
t=   141  avg= 122.90  p99=12093.90  ents=
t=   145  avg= 122.90  p99=12093.90  ents=2
t=   149  avg= 123.00  p99=12093.90  ents=
t=   153  avg= 123.00  p99=12093.90  ents=
t=   157  avg= 123.00  p99=12093.90  ents=
t=   161  avg= 123.00  p99=12093.90  ents=2
t=   165  avg= 123.00  p99=12093.90  ents=
t=   169  avg= 123.00  p99=12093.90  ents=
t=   173  avg= 123.00  p99=12093.90  ents=
t=   177  avg= 123.00  p99=12093.90  ents=2
t=   181  avg= 123.00  p99=12093.90  ents=
t=   185  avg= 123.00  p99=12093.90  ents=
t=   189  avg= 123.00  p99=12093.90  ents=
t=   193  avg= 123.00  p99=12093.90  ents=2
t=   197  avg= 123.00  p99=12093.90  ents=
t=   201  avg= 123.00  p99=12093.90  ents=
t=   205  avg= 123.00  p99=12093.90  ents=
t=   209  avg=   1.50  p99=    5.90  ents=2
t=   213  avg=   1.50  p99=    4.00  ents=
t=   217  avg=   1.40  p99=    4.00  ents=
t=   221  avg=   1.40  p99=    4.00  ents=
t=   225  avg=   1.40  p99=    4.00  ents=2
t=   229  avg=   1.40  p99=    4.00  ents=
t=   233  avg=   1.30  p99=    4.00  ents=
t=   237  avg=   1.30  p99=    4.00  ents=
t=   241  avg=   1.30  p99=    4.00  ents=2
t=   245  avg=   1.30  p99=    4.00  ents=
t=   249  avg=   1.30  p99=    4.00  ents=
t=   254  avg=   1.20  p99=    4.00  ents=
t=   258  avg=   1.20  p99=    4.00  ents=2
t=   262  avg=   1.20  p99=    4.00  ents=
t=   266  avg=   1.20  p99=    4.00  ents=
t=   270  avg=   1.20  p99=    4.00  ents=
t=   274  avg=   1.20  p99=    1.80  ents=2
t=   278  avg=   1.20  p99=    1.80  ents=
t=   282  avg=   1.20  p99=    1.80  ents=
t=   286  avg=   1.20  p99=    1.80  ents=
t=   290  avg=   1.20  p99=    1.80  ents=2
t=   294  avg=   1.20  p99=    1.80  ents=
t=   298  avg=   1.20  p99=    1.80  ents=
t=   302  avg=   1.20  p99=    1.70  ents=
t=   306  avg=   1.20  p99=    1.70  ents=2
t=   310  avg=   1.20  p99=    1.70  ents=
t=   314  avg=   1.20  p99=    1.80  ents=
t=   318  avg=   1.20  p99=    1.80  ents=
t=   322  avg=   1.30  p99=    2.40  ents=2
t=   326  avg=   1.30  p99=    2.40  ents=
t=   330  avg=   1.30  p99=    2.40  ents=
t=   334  avg=   1.30  p99=    2.40  ents=
t=   338  avg=   1.30  p99=    2.40  ents=2
t=   342  avg=   1.30  p99=    2.40  ents=
t=   346  avg=   1.30  p99=    2.40  ents=
t=   350  avg=   1.30  p99=    2.40  ents=
t=   354  avg=   1.30  p99=    2.40  ents=2
t=   358  avg=   1.30  p99=    2.40  ents=
t=   362  avg=   1.30  p99=    2.40  ents=
t=   366  avg=   1.30  p99=    2.40  ents=
t=   371  avg=   1.30  p99=    2.40  ents=2
t=   375  avg=   1.30  p99=    2.40  ents=
t=   379  avg=   1.30  p99=    2.40  ents=
t=   383  avg=   1.20  p99=    2.40  ents=
t=   387  avg=   1.20  p99=    2.40  ents=2
t=   392  avg=   1.20  p99=    2.40  ents=
t=   396  avg=   1.20  p99=    2.40  ents=
t=   400  avg=   1.20  p99=    2.40  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ███████▄▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (2 .. 4)
0:4 17:4 33:4 49:4 65:4 81:4 97:4 112:3 129:2 145:2 161:2 177:2 193:2 209:2 225:2 241:2 258:2 274:2 290:2 306:2 322:2 338:2 354:2 371:2 387:2
```

Server fell behind:

```
[03:10:40] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 12046ms or 240 ticks behind
```

### `tsar_bomba`  (`DropProjectileTNTEffect`)

- site `[5000, -60, 0]`, armed via **block+redstone**, declared fuse **80** ticks
- baseline **2.06 ms/t** (p99 46.40) -> peak avg **73.2 ms/t**, peak p99 **6972.6 ms/t**
- observed window: 397 game ticks over 26.7 s wall; worst wall-clock **3521 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.00  p99=    4.20  ents=4
t=     4  avg=   1.10  p99=    8.90  ents=
t=     8  avg=   1.10  p99=    8.90  ents=
t=    12  avg=   1.10  p99=    8.90  ents=
t=    16  avg=   1.40  p99=   33.40  ents=4
t=    20  avg=   1.50  p99=   33.40  ents=
t=    25  avg=   1.50  p99=   33.40  ents=
t=    29  avg=   1.60  p99=   33.40  ents=
t=    33  avg=   1.60  p99=   33.40  ents=4
t=    37  avg=   1.60  p99=   33.40  ents=
t=    41  avg=   1.70  p99=   33.40  ents=
t=    45  avg=   1.70  p99=   33.40  ents=
t=    49  avg=   1.70  p99=   33.40  ents=4
t=    53  avg=   1.80  p99=   33.40  ents=
t=    57  avg=   1.80  p99=   33.40  ents=
t=    61  avg=   1.90  p99=   33.40  ents=
t=    65  avg=   1.90  p99=   33.40  ents=4
t=    69  avg=   1.90  p99=   33.40  ents=
t=    73  avg=   2.00  p99=   33.40  ents=
t=    77  avg=   2.00  p99=   33.40  ents=
t=    81  avg=   2.00  p99=   33.40  ents=4
t=    85  avg=   2.00  p99=   33.40  ents=
t=    89  avg=   2.10  p99=   33.40  ents=
t=    93  avg=   2.10  p99=   33.40  ents=
t=    97  avg=   2.10  p99=   33.40  ents=4
t=   101  avg=   2.10  p99=   33.40  ents=
t=   105  avg=   2.10  p99=   33.40  ents=
t=   107  avg=  71.80  p99= 6972.60  ents=
t=   112  avg=  72.90  p99= 6972.60  ents=1
t=   118  avg=  72.70  p99= 6972.60  ents=
t=   122  avg=  72.70  p99= 6972.60  ents=
t=   126  avg=  72.80  p99= 6972.60  ents=
t=   130  avg=  72.80  p99= 6972.60  ents=1
t=   134  avg=  72.90  p99= 6972.60  ents=
t=   138  avg=  72.90  p99= 6972.60  ents=
t=   142  avg=  73.00  p99= 6972.60  ents=
t=   146  avg=  73.00  p99= 6972.60  ents=2
t=   151  avg=  73.00  p99= 6972.60  ents=
t=   155  avg=  73.00  p99= 6972.60  ents=
t=   159  avg=  73.00  p99= 6972.60  ents=
t=   163  avg=  73.10  p99= 6972.60  ents=2
t=   167  avg=  73.10  p99= 6972.60  ents=
t=   171  avg=  73.10  p99= 6972.60  ents=
t=   175  avg=  73.10  p99= 6972.60  ents=
t=   179  avg=  73.10  p99= 6972.60  ents=2
t=   183  avg=  73.10  p99= 6972.60  ents=
t=   187  avg=  73.10  p99= 6972.60  ents=
t=   191  avg=  73.20  p99= 6972.60  ents=
t=   195  avg=  73.20  p99= 6972.60  ents=1
t=   199  avg=  73.20  p99= 6972.60  ents=
t=   203  avg=  73.10  p99= 6972.60  ents=
t=   207  avg=   3.40  p99=   54.70  ents=
t=   211  avg=   2.30  p99=    7.60  ents=1
t=   215  avg=   2.30  p99=    7.60  ents=
t=   219  avg=   2.10  p99=    5.80  ents=
t=   223  avg=   2.00  p99=    5.80  ents=
t=   227  avg=   2.00  p99=    5.80  ents=1
t=   231  avg=   1.90  p99=    5.80  ents=
t=   235  avg=   1.80  p99=    5.80  ents=
t=   239  avg=   1.80  p99=    5.80  ents=
t=   243  avg=   1.70  p99=    2.60  ents=1
t=   247  avg=   1.70  p99=    2.50  ents=
t=   251  avg=   1.70  p99=    2.50  ents=
t=   255  avg=   1.60  p99=    2.50  ents=
t=   259  avg=   1.60  p99=    2.50  ents=1
t=   264  avg=   1.50  p99=    2.50  ents=
t=   268  avg=   1.50  p99=    2.50  ents=
t=   272  avg=   1.50  p99=    2.50  ents=
t=   276  avg=   1.50  p99=    2.30  ents=2
t=   280  avg=   1.50  p99=    2.20  ents=
t=   284  avg=   1.50  p99=    2.20  ents=
t=   288  avg=   1.40  p99=    2.10  ents=
t=   292  avg=   1.40  p99=    2.10  ents=2
t=   296  avg=   1.40  p99=    2.10  ents=
t=   300  avg=   1.40  p99=    2.10  ents=
t=   304  avg=   1.40  p99=    2.10  ents=
t=   308  avg=   1.40  p99=    2.10  ents=1
t=   312  avg=   1.40  p99=    2.10  ents=
t=   316  avg=   1.40  p99=    2.10  ents=
t=   320  avg=   1.40  p99=    2.10  ents=
t=   324  avg=   1.40  p99=    2.10  ents=1
t=   328  avg=   1.40  p99=    2.10  ents=
t=   332  avg=   1.40  p99=    2.10  ents=
t=   336  avg=   1.40  p99=    2.10  ents=
t=   340  avg=   1.40  p99=    2.10  ents=1
t=   344  avg=   1.40  p99=    2.10  ents=
t=   348  avg=   1.40  p99=    2.10  ents=
t=   352  avg=   1.40  p99=    2.10  ents=
t=   356  avg=   1.40  p99=    2.10  ents=1
t=   360  avg=   1.40  p99=    2.10  ents=
t=   364  avg=   1.40  p99=    2.10  ents=
t=   368  avg=   1.40  p99=    2.10  ents=
t=   372  avg=   1.40  p99=    2.10  ents=1
t=   376  avg=   1.40  p99=    2.10  ents=
t=   380  avg=   1.40  p99=    1.90  ents=
t=   384  avg=   1.40  p99=    1.90  ents=
t=   389  avg=   1.40  p99=    1.90  ents=3
t=   393  avg=   1.40  p99=    1.90  ents=
t=   397  avg=   1.30  p99=    1.80  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ███████▁▁▃▃▃▁▁▁▁▁▃▃▁▁▁▁▁▆   (1 .. 4)
0:4 16:4 33:4 49:4 65:4 81:4 97:4 112:1 130:1 146:2 163:2 179:2 195:1 211:1 227:1 243:1 259:1 276:2 292:2 308:1 324:1 340:1 356:1 372:1 389:3
```

Server fell behind:

```
[03:10:02] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 6926ms or 138 ticks behind
```

### `colossal_tnt`  (`ColossalTNTEffect`)

- site `[79000, -60, 0]`, armed via **block+redstone**, declared fuse **400** ticks
- baseline **1.07 ms/t** (p99 29.60) -> peak avg **67.1 ms/t**, peak p99 **6601.1 ms/t**
- observed window: 719 game ticks over 42.5 s wall; worst wall-clock **6638 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.80  p99=    1.50  ents=4
t=    12  avg=   0.70  p99=    1.10  ents=
t=    24  avg=   0.70  p99=    1.30  ents=
t=    36  avg=   0.80  p99=    1.30  ents=
t=    49  avg=   0.80  p99=    1.30  ents=4
t=    61  avg=   0.80  p99=    1.30  ents=
t=    73  avg=   0.90  p99=    2.50  ents=
t=    85  avg=   0.90  p99=    2.50  ents=
t=    97  avg=   0.90  p99=    2.50  ents=4
t=   109  avg=   0.90  p99=    2.50  ents=
t=   121  avg=   0.90  p99=    2.50  ents=
t=   133  avg=   0.90  p99=    2.50  ents=
t=   145  avg=   1.00  p99=    2.50  ents=4
t=   157  avg=   1.00  p99=    2.50  ents=
t=   169  avg=   1.00  p99=    1.30  ents=
t=   181  avg=   1.00  p99=    1.30  ents=
t=   194  avg=   1.00  p99=    1.30  ents=4
t=   206  avg=   1.00  p99=    1.30  ents=
t=   218  avg=   1.00  p99=    1.30  ents=
t=   230  avg=   1.00  p99=    1.30  ents=
t=   242  avg=   1.00  p99=    1.30  ents=4
t=   254  avg=   1.00  p99=    1.30  ents=
t=   266  avg=   1.00  p99=    1.30  ents=
t=   278  avg=   1.00  p99=    1.30  ents=
t=   291  avg=   1.00  p99=    1.40  ents=4
t=   303  avg=   1.00  p99=    1.40  ents=
t=   315  avg=   1.00  p99=    1.40  ents=
t=   327  avg=   1.00  p99=    1.40  ents=
t=   339  avg=   1.00  p99=    1.40  ents=4
t=   351  avg=   1.00  p99=    1.40  ents=
t=   364  avg=   1.00  p99=    1.40  ents=
t=   376  avg=   1.00  p99=    1.40  ents=
t=   388  avg=   1.00  p99=    1.40  ents=4
t=   400  avg=   1.00  p99=    1.30  ents=
t=   410  avg=  67.10  p99= 6601.10  ents=
t=   422  avg=  67.10  p99= 6601.10  ents=
t=   434  avg=  67.10  p99= 6601.10  ents=2
t=   446  avg=  67.00  p99= 6601.10  ents=
t=   458  avg=  67.00  p99= 6601.10  ents=
t=   470  avg=  67.00  p99= 6601.10  ents=
t=   482  avg=  66.90  p99= 6601.10  ents=2
t=   494  avg=  66.90  p99= 6601.10  ents=
t=   506  avg=   0.70  p99=    1.50  ents=
t=   518  avg=   0.70  p99=    1.30  ents=
t=   530  avg=   0.70  p99=    0.90  ents=2
t=   542  avg=   0.70  p99=    0.90  ents=
t=   555  avg=   0.70  p99=    0.90  ents=
t=   567  avg=   0.70  p99=    0.90  ents=
t=   579  avg=   0.70  p99=    0.90  ents=2
t=   591  avg=   0.70  p99=    0.90  ents=
t=   603  avg=   0.70  p99=    0.90  ents=
t=   615  avg=   0.70  p99=    0.90  ents=
t=   627  avg=   0.70  p99=    0.90  ents=2
t=   639  avg=   0.70  p99=    0.90  ents=
t=   651  avg=   0.70  p99=    0.90  ents=
t=   663  avg=   0.70  p99=    0.90  ents=
t=   675  avg=   0.70  p99=    0.90  ents=2
t=   687  avg=   0.60  p99=    0.90  ents=
t=   699  avg=   0.60  p99=    0.90  ents=
t=   711  avg=   0.70  p99=    0.90  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  █████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (2 .. 4)
0:4 16:4 32:4 49:4 65:4 81:4 97:4 113:4 129:4 145:4 161:4 177:4 194:4 210:4 226:4 242:4 258:4 274:4 291:4 307:4 323:4 339:4 355:4 372:4 388:4 401:2 418:2 434:2 450:2 466:2 482:2 498:2 514:2 530:2 546:2 563:2 579:2 595:2 611:2 627:2 643:2 659:2 675:2 691:2 707:2
```

Server fell behind:

```
[03:43:36] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 6557ms or 131 ticks behind
```

### `tnt_x2000`  (`TNTXStrengthEffect`)

- site `[69000, -60, 0]`, armed via **block+redstone**, declared fuse **400** ticks
- baseline **1.11 ms/t** (p99 15.80) -> peak avg **43.6 ms/t**, peak p99 **4232.5 ms/t**
- observed window: 717 game ticks over 40.0 s wall; worst wall-clock **4256 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.90  p99=    1.50  ents=4
t=     8  avg=   0.90  p99=    1.40  ents=
t=    16  avg=   0.90  p99=    1.30  ents=4
t=    25  avg=   0.90  p99=    1.30  ents=
t=    33  avg=   1.00  p99=    1.30  ents=4
t=    41  avg=   1.00  p99=    1.30  ents=
t=    49  avg=   1.00  p99=    1.30  ents=4
t=    57  avg=   1.00  p99=    1.30  ents=
t=    65  avg=   1.00  p99=    1.30  ents=4
t=    73  avg=   1.00  p99=    1.40  ents=
t=    81  avg=   1.00  p99=    1.40  ents=4
t=    89  avg=   1.10  p99=    1.50  ents=
t=    97  avg=   1.10  p99=    1.50  ents=4
t=   105  avg=   1.10  p99=    1.50  ents=
t=   113  avg=   1.10  p99=    1.50  ents=4
t=   121  avg=   1.10  p99=    1.50  ents=
t=   129  avg=   1.10  p99=    1.50  ents=4
t=   137  avg=   1.10  p99=    1.50  ents=
t=   146  avg=   1.10  p99=    1.50  ents=4
t=   154  avg=   1.10  p99=    1.50  ents=
t=   162  avg=   1.10  p99=    1.50  ents=4
t=   170  avg=   1.20  p99=    1.50  ents=
t=   178  avg=   1.20  p99=    1.50  ents=4
t=   186  avg=   1.10  p99=    1.50  ents=
t=   194  avg=   1.10  p99=    1.50  ents=4
t=   202  avg=   1.10  p99=    1.50  ents=
t=   210  avg=   1.10  p99=    1.50  ents=4
t=   218  avg=   1.10  p99=    1.40  ents=
t=   226  avg=   1.10  p99=    2.00  ents=4
t=   234  avg=   1.10  p99=    2.00  ents=
t=   242  avg=   1.10  p99=    2.00  ents=4
t=   251  avg=   1.10  p99=    2.00  ents=
t=   259  avg=   1.10  p99=    2.00  ents=4
t=   267  avg=   1.10  p99=    2.00  ents=
t=   275  avg=   1.10  p99=    2.00  ents=4
t=   283  avg=   1.10  p99=    2.00  ents=
t=   291  avg=   1.10  p99=    6.80  ents=4
t=   299  avg=   1.10  p99=    6.80  ents=
t=   307  avg=   1.10  p99=    6.80  ents=4
t=   315  avg=   1.10  p99=    6.80  ents=
t=   323  avg=   1.10  p99=    6.80  ents=4
t=   332  avg=   1.10  p99=    6.80  ents=
t=   340  avg=   1.20  p99=    6.80  ents=4
t=   348  avg=   1.20  p99=    6.80  ents=
t=   356  avg=   1.20  p99=    6.80  ents=4
t=   364  avg=   1.20  p99=    6.80  ents=
t=   372  avg=   1.30  p99=    6.80  ents=4
t=   380  avg=   1.30  p99=    6.80  ents=
t=   388  avg=   1.30  p99=    6.80  ents=4
t=   396  avg=   1.20  p99=    4.80  ents=
t=   401  avg=  43.60  p99= 4232.50  ents=2
t=   410  avg=  43.60  p99= 4232.50  ents=
t=   418  avg=  43.60  p99= 4232.50  ents=2
t=   426  avg=  43.60  p99= 4232.50  ents=
t=   434  avg=  43.60  p99= 4232.50  ents=2
t=   442  avg=  43.50  p99= 4232.50  ents=
t=   450  avg=  43.50  p99= 4232.50  ents=2
t=   459  avg=  43.40  p99= 4232.50  ents=
t=   467  avg=  43.40  p99= 4232.50  ents=2
t=   475  avg=  43.30  p99= 4232.50  ents=
t=   483  avg=  43.30  p99= 4232.50  ents=2
t=   491  avg=  43.20  p99= 4232.50  ents=
t=   499  avg=  43.20  p99= 4232.50  ents=2
t=   507  avg=   0.80  p99=    1.70  ents=
t=   515  avg=   0.70  p99=    1.30  ents=2
t=   523  avg=   0.70  p99=    1.30  ents=
t=   531  avg=   0.70  p99=    1.30  ents=2
t=   539  avg=   0.70  p99=    1.30  ents=
t=   547  avg=   0.70  p99=    1.30  ents=2
t=   555  avg=   0.70  p99=    1.30  ents=
t=   563  avg=   0.70  p99=    0.90  ents=2
t=   571  avg=   0.70  p99=    0.90  ents=
t=   579  avg=   0.70  p99=    0.90  ents=2
t=   587  avg=   0.70  p99=    0.90  ents=
t=   595  avg=   0.70  p99=    0.90  ents=2
t=   604  avg=   0.70  p99=    1.10  ents=
t=   612  avg=   0.70  p99=    1.40  ents=2
t=   620  avg=   0.70  p99=    1.40  ents=
t=   628  avg=   0.70  p99=    1.40  ents=2
t=   636  avg=   0.70  p99=    1.40  ents=
t=   644  avg=   0.70  p99=    1.40  ents=2
t=   652  avg=   0.70  p99=    1.40  ents=
t=   660  avg=   0.70  p99=    1.40  ents=2
t=   668  avg=   0.70  p99=    1.40  ents=
t=   676  avg=   0.70  p99=    1.40  ents=2
t=   684  avg=   0.70  p99=    1.40  ents=
t=   692  avg=   0.70  p99=    1.40  ents=2
t=   700  avg=   0.70  p99=    1.40  ents=
t=   709  avg=   0.70  p99=    0.90  ents=2
t=   717  avg=   0.70  p99=    0.90  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  █████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (2 .. 4)
0:4 16:4 33:4 49:4 65:4 81:4 97:4 113:4 129:4 146:4 162:4 178:4 194:4 210:4 226:4 242:4 259:4 275:4 291:4 307:4 323:4 340:4 356:4 372:4 388:4 401:2 418:2 434:2 450:2 467:2 483:2 499:2 515:2 531:2 547:2 563:2 579:2 595:2 612:2 628:2 644:2 660:2 676:2 692:2 709:2
```

Server fell behind:

```
[03:40:01] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 4185ms or 83 ticks behind
```

### `flying_tnt`  (`StackedPrimedTNTEffect`)

- site `[123000, -60, 0]`, armed via **block+redstone**, declared fuse **200** ticks
- baseline **1.81 ms/t** (p99 30.30) -> peak avg **42.9 ms/t**, peak p99 **4146.4 ms/t**
- observed window: 347 game ticks over 21.4 s wall; worst wall-clock **1425 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.50  p99=   30.30  ents=4
t=     4  avg=   1.30  p99=   19.60  ents=
t=     8  avg=   1.30  p99=   19.60  ents=
t=    12  avg=   1.30  p99=   19.60  ents=
t=    16  avg=   1.30  p99=   19.60  ents=4
t=    20  avg=   1.30  p99=   19.60  ents=
t=    24  avg=   1.30  p99=   19.60  ents=
t=    28  avg=   1.30  p99=   19.60  ents=
t=    32  avg=   1.30  p99=   19.60  ents=4
t=    36  avg=   1.30  p99=   19.60  ents=
t=    40  avg=   1.30  p99=   19.60  ents=
t=    44  avg=   1.40  p99=   19.60  ents=
t=    48  avg=   1.40  p99=   19.60  ents=4
t=    52  avg=   1.40  p99=   19.60  ents=
t=    56  avg=   1.40  p99=   19.60  ents=
t=    60  avg=   1.50  p99=   19.60  ents=
t=    64  avg=   1.50  p99=   19.60  ents=4
t=    69  avg=   1.50  p99=   19.60  ents=
t=    73  avg=   1.50  p99=   19.60  ents=
t=    77  avg=   1.50  p99=   19.60  ents=
t=    81  avg=   1.50  p99=   19.60  ents=4
t=    85  avg=   1.60  p99=   19.60  ents=
t=    89  avg=   1.60  p99=   19.60  ents=
t=    93  avg=   1.60  p99=   19.60  ents=
t=    97  avg=   1.60  p99=   19.60  ents=4
t=   101  avg=   1.70  p99=   19.60  ents=
t=   105  avg=   1.50  p99=    2.60  ents=
t=   109  avg=   1.40  p99=    2.60  ents=
t=   113  avg=   1.40  p99=    2.60  ents=4
t=   117  avg=   1.40  p99=    2.60  ents=
t=   121  avg=   1.40  p99=    2.60  ents=
t=   125  avg=   1.40  p99=    2.60  ents=
t=   129  avg=   1.30  p99=    1.90  ents=4
t=   133  avg=   1.30  p99=    1.90  ents=
t=   137  avg=   1.30  p99=    1.90  ents=
t=   141  avg=   1.30  p99=    1.90  ents=
t=   145  avg=   1.30  p99=    1.90  ents=4
t=   149  avg=   1.30  p99=    1.90  ents=
t=   154  avg=   1.30  p99=    1.90  ents=
t=   158  avg=   1.30  p99=    1.90  ents=
t=   162  avg=   1.30  p99=    1.90  ents=4
t=   166  avg=   1.30  p99=    1.90  ents=
t=   170  avg=   1.30  p99=    1.90  ents=
t=   174  avg=   1.30  p99=    1.90  ents=
t=   178  avg=   1.30  p99=    1.90  ents=4
t=   182  avg=   1.30  p99=    1.90  ents=
t=   186  avg=   1.30  p99=    1.90  ents=
t=   190  avg=   1.30  p99=    1.90  ents=
t=   194  avg=   1.30  p99=    1.90  ents=4
t=   198  avg=   1.20  p99=    1.90  ents=
t=   201  avg=  42.70  p99= 4146.40  ents=
t=   205  avg=  42.80  p99= 4146.40  ents=
t=   209  avg=  42.80  p99= 4146.40  ents=2
t=   214  avg=  42.90  p99= 4146.40  ents=
t=   218  avg=  42.90  p99= 4146.40  ents=
t=   222  avg=  42.90  p99= 4146.40  ents=
t=   226  avg=  42.90  p99= 4146.40  ents=2
t=   230  avg=  42.90  p99= 4146.40  ents=
t=   234  avg=  42.90  p99= 4146.40  ents=
t=   238  avg=  42.80  p99= 4146.40  ents=
t=   242  avg=  42.80  p99= 4146.40  ents=2
t=   246  avg=  42.80  p99= 4146.40  ents=
t=   250  avg=  42.80  p99= 4146.40  ents=
t=   254  avg=  42.80  p99= 4146.40  ents=
t=   258  avg=  42.80  p99= 4146.40  ents=2
t=   262  avg=  42.80  p99= 4146.40  ents=
t=   266  avg=  42.80  p99= 4146.40  ents=
t=   270  avg=  42.70  p99= 4146.40  ents=
t=   274  avg=  42.70  p99= 4146.40  ents=2
t=   278  avg=  42.70  p99= 4146.40  ents=
t=   282  avg=  42.70  p99= 4146.40  ents=
t=   286  avg=  42.70  p99= 4146.40  ents=
t=   290  avg=  42.70  p99= 4146.40  ents=2
t=   294  avg=  42.70  p99= 4146.40  ents=
t=   298  avg=  42.60  p99= 4146.40  ents=
t=   302  avg=   1.10  p99=   10.00  ents=
t=   306  avg=   1.00  p99=    2.90  ents=2
t=   310  avg=   1.00  p99=    2.90  ents=
t=   314  avg=   0.90  p99=    1.70  ents=
t=   318  avg=   0.90  p99=    1.70  ents=
t=   323  avg=   0.80  p99=    1.40  ents=2
t=   327  avg=   0.80  p99=    1.40  ents=
t=   331  avg=   0.80  p99=    1.40  ents=
t=   335  avg=   0.80  p99=    1.40  ents=
t=   339  avg=   0.80  p99=    1.40  ents=2
t=   343  avg=   0.80  p99=    1.40  ents=
t=   347  avg=   0.80  p99=    1.40  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  █████████████▁▁▁▁▁▁▁▁▁   (2 .. 4)
0:4 16:4 32:4 48:4 64:4 81:4 97:4 113:4 129:4 145:4 162:4 178:4 194:4 209:2 226:2 242:2 258:2 274:2 290:2 306:2 323:2 339:2
```

Server fell behind:

```
[03:59:12] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 4100ms or 82 ticks behind
```

### `nether_tnt`  (`NetherTNTEffect`)

- site `[35000, -60, 0]`, armed via **block+redstone**, declared fuse **180** ticks
- baseline **1.09 ms/t** (p99 30.30) -> peak avg **36.2 ms/t**, peak p99 **3152.5 ms/t**
- observed window: 496 game ticks over 27.9 s wall; worst wall-clock **827 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.80  p99=    2.20  ents=4
t=     8  avg=   0.80  p99=    2.20  ents=
t=    16  avg=   0.70  p99=    2.20  ents=4
t=    24  avg=   0.80  p99=    2.20  ents=
t=    33  avg=   0.80  p99=    2.20  ents=4
t=    41  avg=   0.80  p99=    2.20  ents=
t=    49  avg=   0.80  p99=    2.20  ents=4
t=    57  avg=   0.80  p99=    2.20  ents=
t=    65  avg=   0.80  p99=    2.20  ents=4
t=    73  avg=   0.80  p99=    1.70  ents=
t=    81  avg=   0.90  p99=    1.70  ents=4
t=    89  avg=   0.90  p99=    1.70  ents=
t=    97  avg=   0.90  p99=    1.70  ents=4
t=   105  avg=   0.90  p99=    1.70  ents=
t=   113  avg=   0.90  p99=    1.20  ents=4
t=   121  avg=   0.90  p99=    1.20  ents=
t=   129  avg=   0.90  p99=    1.20  ents=4
t=   137  avg=   0.90  p99=    1.20  ents=
t=   145  avg=   0.90  p99=    1.20  ents=4
t=   153  avg=   0.90  p99=    1.20  ents=
t=   161  avg=   0.90  p99=    1.20  ents=4
t=   169  avg=   0.90  p99=    1.20  ents=
t=   177  avg=   0.90  p99=    1.20  ents=4
t=   186  avg=  32.50  p99= 3152.50  ents=
t=   194  avg=  32.50  p99= 3152.50  ents=3
t=   202  avg=  32.50  p99= 3152.50  ents=
t=   210  avg=  32.60  p99= 3152.50  ents=3
t=   218  avg=  34.80  p99= 3152.50  ents=
t=   226  avg=  34.80  p99= 3152.50  ents=0
t=   234  avg=  34.90  p99= 3152.50  ents=
t=   242  avg=  35.70  p99= 3152.50  ents=0
t=   250  avg=  35.70  p99= 3152.50  ents=
t=   258  avg=  35.70  p99= 3152.50  ents=0
t=   266  avg=  35.70  p99= 3152.50  ents=
t=   274  avg=  36.20  p99= 3152.50  ents=0
t=   282  avg=   4.60  p99=  167.20  ents=
t=   291  avg=   4.50  p99=  167.20  ents=0
t=   299  avg=   4.50  p99=  167.20  ents=
t=   307  avg=   5.20  p99=  167.20  ents=0
t=   315  avg=   3.00  p99=   65.40  ents=
t=   323  avg=   2.90  p99=   65.40  ents=0
t=   331  avg=   3.00  p99=   65.40  ents=
t=   339  avg=   3.00  p99=   65.40  ents=0
t=   347  avg=   2.10  p99=   65.40  ents=
t=   355  avg=   2.10  p99=   65.40  ents=0
t=   363  avg=   2.20  p99=   65.40  ents=
t=   371  avg=   1.80  p99=   65.40  ents=0
t=   379  avg=   1.70  p99=   65.40  ents=
t=   387  avg=   1.70  p99=   65.40  ents=0
t=   395  avg=   1.80  p99=   65.40  ents=
t=   403  avg=   1.10  p99=    8.80  ents=0
t=   411  avg=   1.10  p99=    8.80  ents=
t=   419  avg=   1.10  p99=    8.80  ents=0
t=   427  avg=   1.10  p99=    8.80  ents=
t=   436  avg=   1.00  p99=    6.90  ents=0
t=   444  avg=   1.00  p99=    6.90  ents=
t=   452  avg=   1.00  p99=    6.90  ents=0
t=   460  avg=   1.00  p99=    6.90  ents=
t=   468  avg=   0.90  p99=    6.90  ents=0
t=   476  avg=   0.90  p99=    6.90  ents=
t=   484  avg=   0.90  p99=    6.90  ents=0
t=   492  avg=   0.80  p99=    2.40  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ████████████▆▆▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (0 .. 4)
0:4 16:4 33:4 49:4 65:4 81:4 97:4 113:4 129:4 145:4 161:4 177:4 194:3 210:3 226:0 242:0 258:0 274:0 291:0 307:0 323:0 339:0 355:0 371:0 387:0 403:0 419:0 436:0 452:0 468:0 484:0
```

Server fell behind:

```
[03:24:45] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 3103ms or 62 ticks behind
```

### `supernova`  (`SupernovaEffect`)

- site `[95000, -60, 0]`, armed via **block+redstone**, declared fuse **300** ticks
- baseline **1.08 ms/t** (p99 23.10) -> peak avg **25.0 ms/t**, peak p99 **2394.6 ms/t**
- observed window: 616 game ticks over 33.1 s wall; worst wall-clock **834 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.00  p99=    1.70  ents=4
t=     8  avg=   1.00  p99=    3.40  ents=
t=    16  avg=   1.00  p99=    3.40  ents=4
t=    25  avg=   1.00  p99=    3.40  ents=
t=    33  avg=   1.00  p99=    3.40  ents=4
t=    41  avg=   1.00  p99=    3.40  ents=
t=    49  avg=   1.00  p99=    3.40  ents=4
t=    57  avg=   1.00  p99=    3.40  ents=
t=    65  avg=   1.00  p99=    3.40  ents=4
t=    73  avg=   0.90  p99=    3.40  ents=
t=    81  avg=   0.90  p99=    3.40  ents=4
t=    89  avg=   0.90  p99=    3.40  ents=
t=    97  avg=   0.90  p99=    3.40  ents=4
t=   105  avg=   0.90  p99=    1.90  ents=
t=   113  avg=   0.90  p99=    1.20  ents=4
t=   121  avg=   0.90  p99=    1.20  ents=
t=   129  avg=   0.90  p99=    1.20  ents=4
t=   137  avg=   0.90  p99=    1.30  ents=
t=   145  avg=   0.90  p99=    1.30  ents=4
t=   153  avg=   0.90  p99=    1.30  ents=
t=   161  avg=   0.90  p99=    1.30  ents=4
t=   169  avg=   0.90  p99=    1.30  ents=
t=   178  avg=   0.90  p99=    1.30  ents=4
t=   186  avg=   1.00  p99=    1.30  ents=
t=   194  avg=   1.00  p99=    1.30  ents=4
t=   202  avg=   1.00  p99=    1.30  ents=
t=   210  avg=   1.00  p99=    1.30  ents=4
t=   218  avg=   0.90  p99=    1.30  ents=
t=   226  avg=   0.90  p99=    1.30  ents=4
t=   234  avg=   0.90  p99=    1.30  ents=
t=   242  avg=   0.90  p99=    1.30  ents=4
t=   250  avg=   0.90  p99=    1.30  ents=
t=   258  avg=   0.90  p99=    1.30  ents=4
t=   266  avg=   0.90  p99=    1.30  ents=
t=   274  avg=   0.90  p99=    1.30  ents=4
t=   282  avg=   0.90  p99=    1.30  ents=
t=   290  avg=   0.90  p99=    1.30  ents=4
t=   298  avg=   0.90  p99=    1.30  ents=
t=   306  avg=  24.90  p99= 2394.60  ents=3
t=   314  avg=  25.00  p99= 2394.60  ents=
t=   323  avg=  25.00  p99= 2394.60  ents=3
t=   331  avg=  25.00  p99= 2394.60  ents=
t=   339  avg=  25.00  p99= 2394.60  ents=3
t=   347  avg=  25.00  p99= 2394.60  ents=
t=   355  avg=  24.90  p99= 2394.60  ents=3
t=   363  avg=  24.90  p99= 2394.60  ents=
t=   371  avg=  24.90  p99= 2394.60  ents=3
t=   379  avg=  24.90  p99= 2394.60  ents=
t=   387  avg=  24.90  p99= 2394.60  ents=3
t=   395  avg=  24.90  p99= 2394.60  ents=
t=   403  avg=   0.80  p99=    1.40  ents=3
t=   411  avg=   0.70  p99=    1.20  ents=
t=   419  avg=   0.70  p99=    1.20  ents=3
t=   427  avg=   0.70  p99=    1.10  ents=
t=   435  avg=   0.60  p99=    0.80  ents=3
t=   443  avg=   0.60  p99=    0.80  ents=
t=   451  avg=   0.60  p99=    0.80  ents=3
t=   459  avg=   0.60  p99=    0.80  ents=
t=   467  avg=   0.60  p99=    0.80  ents=3
t=   475  avg=   0.60  p99=    0.80  ents=
t=   483  avg=   0.60  p99=    0.80  ents=3
t=   492  avg=   0.60  p99=    0.80  ents=
t=   500  avg=   0.60  p99=    0.80  ents=3
t=   508  avg=   0.60  p99=    0.80  ents=
t=   516  avg=   0.60  p99=    0.80  ents=3
t=   524  avg=   0.60  p99=    0.80  ents=
t=   532  avg=   0.60  p99=    0.80  ents=3
t=   540  avg=   0.60  p99=    0.80  ents=
t=   548  avg=   0.60  p99=    0.80  ents=3
t=   556  avg=   0.60  p99=    0.80  ents=
t=   564  avg=   0.60  p99=    0.80  ents=3
t=   572  avg=   0.60  p99=    0.80  ents=
t=   580  avg=   0.60  p99=    0.80  ents=3
t=   588  avg=   0.60  p99=    0.80  ents=
t=   596  avg=   0.60  p99=    0.80  ents=3
t=   604  avg=   0.60  p99=    0.80  ents=
t=   612  avg=   0.60  p99=    0.80  ents=3
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ███████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (3 .. 4)
0:4 16:4 33:4 49:4 65:4 81:4 97:4 113:4 129:4 145:4 161:4 178:4 194:4 210:4 226:4 242:4 258:4 274:4 290:4 306:3 323:3 339:3 355:3 371:3 387:3 403:3 419:3 435:3 451:3 467:3 483:3 500:3 516:3 532:3 548:3 564:3 580:3 596:3 612:3
```

Server fell behind:

```
[03:48:40] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 2345ms or 46 ticks behind
```

### `jungle_tnt`  (`JungleTNTEffect`)

- site `[139000, -60, 0]`, armed via **block+redstone**, declared fuse **160** ticks
- baseline **1.53 ms/t** (p99 28.00) -> peak avg **21.3 ms/t**, peak p99 **1994.4 ms/t**
- observed window: 306 game ticks over 15.3 s wall; worst wall-clock **540 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███████████████▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███████████████▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.30  p99=   15.10  ents=4
t=     4  avg=   1.20  p99=    4.70  ents=
t=     8  avg=   1.20  p99=    4.70  ents=
t=    12  avg=   1.20  p99=    4.70  ents=
t=    16  avg=   1.20  p99=    4.70  ents=4
t=    20  avg=   1.20  p99=    4.70  ents=
t=    24  avg=   1.20  p99=    3.80  ents=
t=    28  avg=   1.50  p99=   39.60  ents=
t=    32  avg=   1.50  p99=   39.60  ents=4
t=    36  avg=   1.50  p99=   39.60  ents=
t=    40  avg=   1.50  p99=   39.60  ents=
t=    44  avg=   1.60  p99=   39.60  ents=
t=    48  avg=   1.60  p99=   39.60  ents=4
t=    52  avg=   1.60  p99=   39.60  ents=
t=    56  avg=   1.60  p99=   39.60  ents=
t=    60  avg=   1.60  p99=   39.60  ents=
t=    64  avg=   1.60  p99=   39.60  ents=4
t=    69  avg=   1.60  p99=   39.60  ents=
t=    73  avg=   1.60  p99=   39.60  ents=
t=    77  avg=   1.60  p99=   39.60  ents=
t=    81  avg=   1.60  p99=   39.60  ents=4
t=    85  avg=   1.70  p99=   39.60  ents=
t=    89  avg=   1.70  p99=   39.60  ents=
t=    93  avg=   1.70  p99=   39.60  ents=
t=    97  avg=   1.70  p99=   39.60  ents=4
t=   101  avg=   1.70  p99=   39.60  ents=
t=   105  avg=   1.70  p99=   39.60  ents=
t=   109  avg=   1.70  p99=   39.60  ents=
t=   113  avg=   1.70  p99=   39.60  ents=4
t=   117  avg=   1.70  p99=   39.60  ents=
t=   121  avg=   1.70  p99=   39.60  ents=
t=   125  avg=   1.70  p99=   39.60  ents=
t=   129  avg=   1.30  p99=    1.70  ents=4
t=   133  avg=   1.30  p99=    1.70  ents=
t=   137  avg=   1.30  p99=    1.70  ents=
t=   141  avg=   1.30  p99=    1.70  ents=
t=   145  avg=   1.30  p99=    1.70  ents=4
t=   149  avg=   1.30  p99=    1.70  ents=
t=   153  avg=   1.30  p99=    1.70  ents=
t=   157  avg=   1.30  p99=    1.70  ents=
t=   161  avg=  21.30  p99= 1994.40  ents=3
t=   204  avg=  20.90  p99= 1994.40  ents=
t=   209  avg=  20.80  p99= 1994.40  ents=
t=   213  avg=  20.80  p99= 1994.40  ents=
t=   217  avg=  20.80  p99= 1994.40  ents=3
t=   221  avg=  20.80  p99= 1994.40  ents=
t=   225  avg=  20.80  p99= 1994.40  ents=
t=   229  avg=  20.80  p99= 1994.40  ents=
t=   233  avg=  20.70  p99= 1994.40  ents=3
t=   237  avg=  20.70  p99= 1994.40  ents=
t=   241  avg=  20.70  p99= 1994.40  ents=
t=   245  avg=  20.70  p99= 1994.40  ents=
t=   249  avg=  20.70  p99= 1994.40  ents=3
t=   253  avg=  20.60  p99= 1994.40  ents=
t=   257  avg=  20.60  p99= 1994.40  ents=
t=   261  avg=   0.70  p99=    2.30  ents=
t=   265  avg=   0.70  p99=    1.20  ents=3
t=   269  avg=   0.70  p99=    1.20  ents=
t=   273  avg=   0.70  p99=    1.20  ents=
t=   277  avg=   0.70  p99=    1.20  ents=
t=   281  avg=   0.80  p99=    1.20  ents=3
t=   286  avg=   0.80  p99=    1.20  ents=
t=   290  avg=   0.80  p99=    1.20  ents=
t=   294  avg=   0.80  p99=    1.20  ents=
t=   298  avg=   0.90  p99=    1.20  ents=3
t=   302  avg=   0.90  p99=    1.20  ents=
t=   306  avg=   0.90  p99=    1.20  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ██████████▁▁▁▁▁▁▁   (3 .. 4)
0:4 16:4 32:4 48:4 64:4 81:4 97:4 113:4 129:4 145:4 161:3 217:3 233:3 249:3 265:3 281:3 298:3
```

### `atlantis`  (`AtlantisEffect`)

- site `[21000, -60, 0]`, armed via **block+redstone**, declared fuse **240** ticks
- baseline **1.48 ms/t** (p99 45.70) -> peak avg **36.8 ms/t**, peak p99 **1634.9 ms/t**
- observed window: 706 game ticks over 35.3 s wall; worst wall-clock **588 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 43

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▄▅▅▆▆▇▇▇████████▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.00  p99=    1.80  ents=4
t=     8  avg=   1.00  p99=    4.50  ents=
t=    16  avg=   1.00  p99=    4.50  ents=4
t=    24  avg=   1.00  p99=    4.50  ents=
t=    32  avg=   1.10  p99=    4.50  ents=4
t=    40  avg=   1.10  p99=    4.50  ents=
t=    48  avg=   1.10  p99=    4.50  ents=4
t=    56  avg=   1.20  p99=    4.50  ents=
t=    64  avg=   1.20  p99=    4.50  ents=4
t=    72  avg=   1.20  p99=    4.50  ents=
t=    80  avg=   1.20  p99=    4.50  ents=4
t=    88  avg=   1.30  p99=    4.50  ents=
t=    96  avg=   1.30  p99=    4.50  ents=4
t=   104  avg=   1.20  p99=    1.90  ents=
t=   112  avg=   1.20  p99=    1.90  ents=4
t=   121  avg=   1.20  p99=    1.70  ents=
t=   129  avg=   1.40  p99=   24.00  ents=4
t=   137  avg=   1.40  p99=   24.00  ents=
t=   145  avg=   1.40  p99=   24.00  ents=4
t=   153  avg=   1.40  p99=   24.00  ents=
t=   161  avg=   1.40  p99=   24.00  ents=4
t=   169  avg=   1.40  p99=   24.00  ents=
t=   177  avg=   1.40  p99=   24.00  ents=4
t=   185  avg=   1.40  p99=   24.00  ents=
t=   194  avg=   1.40  p99=   24.00  ents=4
t=   202  avg=   1.40  p99=   24.00  ents=
t=   210  avg=   1.40  p99=   24.00  ents=4
t=   218  avg=   1.40  p99=   24.00  ents=
t=   226  avg=   1.20  p99=    1.60  ents=4
t=   234  avg=   1.20  p99=    1.60  ents=
t=   241  avg=  17.50  p99= 1634.90  ents=43
t=   249  avg=  22.20  p99= 1634.90  ents=
t=   275  avg=  26.60  p99= 1634.90  ents=43
t=   302  avg=  31.10  p99= 1634.90  ents=
t=   310  avg=  32.40  p99= 1634.90  ents=43
t=   319  avg=  34.10  p99= 1634.90  ents=
t=   327  avg=  35.30  p99= 1634.90  ents=43
t=   335  avg=  36.20  p99= 1634.90  ents=
t=   343  avg=  20.70  p99=  186.00  ents=43
t=   351  avg=  16.80  p99=   57.30  ents=
t=   359  avg=  15.70  p99=   57.30  ents=43
t=   367  avg=  15.90  p99=   57.30  ents=
t=   376  avg=  15.90  p99=   57.30  ents=43
t=   384  avg=  15.50  p99=   57.30  ents=
t=   392  avg=  14.80  p99=   57.30  ents=43
t=   400  avg=  14.50  p99=   57.30  ents=
t=   408  avg=  13.70  p99=   57.30  ents=43
t=   416  avg=  12.60  p99=   27.40  ents=
t=   424  avg=  12.50  p99=   27.40  ents=43
t=   432  avg=  12.30  p99=   27.40  ents=
t=   440  avg=  11.90  p99=   26.50  ents=43
t=   448  avg=  11.50  p99=   26.50  ents=
t=   456  avg=  11.20  p99=   26.50  ents=43
t=   464  avg=  10.90  p99=   26.50  ents=
t=   473  avg=  10.40  p99=   26.50  ents=43
t=   481  avg=  10.00  p99=   26.50  ents=
t=   489  avg=   9.80  p99=   26.50  ents=43
t=   497  avg=   9.40  p99=   24.70  ents=
t=   505  avg=   9.10  p99=   23.90  ents=43
t=   513  avg=   8.90  p99=   23.90  ents=
t=   521  avg=   8.60  p99=   20.70  ents=43
t=   529  avg=   8.20  p99=   20.70  ents=
t=   537  avg=   8.00  p99=   15.10  ents=43
t=   545  avg=   7.80  p99=   15.10  ents=
t=   553  avg=   7.60  p99=   13.30  ents=43
t=   561  avg=   7.40  p99=   13.30  ents=
t=   569  avg=   7.20  p99=   13.30  ents=43
t=   577  avg=   7.20  p99=   13.30  ents=
t=   585  avg=   7.00  p99=   13.30  ents=43
t=   593  avg=   6.80  p99=   13.30  ents=
t=   602  avg=   6.70  p99=   13.30  ents=43
t=   610  avg=   6.50  p99=   13.30  ents=
t=   618  avg=   6.20  p99=   13.30  ents=43
t=   626  avg=   6.00  p99=   13.30  ents=
t=   634  avg=   5.90  p99=   13.30  ents=43
t=   642  avg=   5.60  p99=   13.20  ents=
t=   650  avg=   5.50  p99=   13.20  ents=43
t=   658  avg=   5.30  p99=   13.20  ents=
t=   666  avg=   5.10  p99=   13.20  ents=43
t=   674  avg=   4.90  p99=   13.20  ents=
t=   682  avg=   4.60  p99=   11.50  ents=43
t=   690  avg=   4.30  p99=   11.50  ents=
t=   698  avg=   4.10  p99=    9.80  ents=43
t=   706  avg=   3.80  p99=    9.80  ents=
```

</details>

Entity count peaked at **43** at t=241. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███████████████████████████   (4 .. 43)
0:4 16:4 32:4 48:4 64:4 80:4 96:4 112:4 129:4 145:4 161:4 177:4 194:4 210:4 226:4 241:43 275:43 310:43 327:43 343:43 359:43 376:43 392:43 408:43 424:43 440:43 456:43 473:43 489:43 505:43 521:43 537:43 553:43 569:43 585:43 602:43 618:43 634:43 650:43 666:43 682:43 698:43
```

### `hyperion`  (`HyperionEffect`)

- site `[181000, -60, 0]`, armed via **block+redstone**, declared fuse **140** ticks
- baseline **1.62 ms/t** (p99 36.70) -> peak avg **17.3 ms/t**, peak p99 **1568.5 ms/t**
- observed window: 290 game ticks over 14.5 s wall; worst wall-clock **445 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.00  p99=    2.50  ents=4
t=     4  avg=   1.00  p99=    1.80  ents=
t=     8  avg=   1.00  p99=    1.80  ents=
t=    12  avg=   1.00  p99=    1.80  ents=
t=    16  avg=   1.00  p99=    1.80  ents=4
t=    20  avg=   1.00  p99=    2.80  ents=
t=    24  avg=   1.00  p99=    2.80  ents=
t=    28  avg=   1.10  p99=    2.80  ents=
t=    32  avg=   1.10  p99=    2.80  ents=4
t=    37  avg=   1.10  p99=    2.80  ents=
t=    41  avg=   1.10  p99=    2.80  ents=
t=    45  avg=   1.10  p99=    2.80  ents=
t=    49  avg=   1.10  p99=    2.80  ents=4
t=    53  avg=   1.10  p99=    2.80  ents=
t=    57  avg=   1.10  p99=    2.80  ents=
t=    61  avg=   1.10  p99=    2.80  ents=
t=    65  avg=   1.20  p99=    2.80  ents=4
t=    69  avg=   1.20  p99=    2.80  ents=
t=    73  avg=   1.20  p99=    2.80  ents=
t=    77  avg=   1.20  p99=    2.80  ents=
t=    81  avg=   1.20  p99=    2.80  ents=4
t=    85  avg=   1.20  p99=    2.80  ents=
t=    89  avg=   1.20  p99=    2.80  ents=
t=    93  avg=   1.20  p99=    2.80  ents=
t=    97  avg=   1.20  p99=    2.80  ents=4
t=   101  avg=   1.20  p99=    2.80  ents=
t=   105  avg=   1.20  p99=    2.80  ents=
t=   109  avg=   1.20  p99=    2.80  ents=
t=   113  avg=   1.20  p99=    2.80  ents=4
t=   117  avg=   1.20  p99=    2.80  ents=
t=   121  avg=   1.20  p99=    1.60  ents=
t=   125  avg=   1.20  p99=    1.60  ents=
t=   129  avg=   1.20  p99=    1.60  ents=4
t=   133  avg=   1.20  p99=    1.60  ents=
t=   137  avg=   1.20  p99=    1.60  ents=
t=   141  avg=  17.30  p99= 1568.50  ents=
t=   177  avg=  17.20  p99= 1568.50  ents=3
t=   181  avg=  17.20  p99= 1568.50  ents=
t=   185  avg=  17.20  p99= 1568.50  ents=
t=   189  avg=  17.20  p99= 1568.50  ents=
t=   193  avg=  17.20  p99= 1568.50  ents=3
t=   197  avg=  17.20  p99= 1568.50  ents=
t=   201  avg=  17.20  p99= 1568.50  ents=
t=   205  avg=  17.20  p99= 1568.50  ents=
t=   209  avg=  17.20  p99= 1568.50  ents=3
t=   213  avg=  17.10  p99= 1568.50  ents=
t=   217  avg=  17.10  p99= 1568.50  ents=
t=   221  avg=  17.10  p99= 1568.50  ents=
t=   225  avg=  17.30  p99= 1568.50  ents=3
t=   229  avg=  17.30  p99= 1568.50  ents=
t=   233  avg=  17.30  p99= 1568.50  ents=
t=   237  avg=  17.30  p99= 1568.50  ents=
t=   241  avg=   1.60  p99=   42.30  ents=3
t=   245  avg=   1.00  p99=   16.80  ents=
t=   249  avg=   1.10  p99=   16.80  ents=
t=   253  avg=   1.10  p99=   16.80  ents=
t=   257  avg=   1.10  p99=   16.80  ents=3
t=   261  avg=   1.10  p99=   16.80  ents=
t=   265  avg=   1.10  p99=   16.80  ents=
t=   269  avg=   1.10  p99=   16.80  ents=
t=   273  avg=   1.20  p99=   16.80  ents=3
t=   277  avg=   1.10  p99=   16.80  ents=
t=   282  avg=   1.10  p99=   16.80  ents=
t=   286  avg=   1.10  p99=   16.80  ents=
t=   290  avg=   1.10  p99=   16.80  ents=3
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  █████████▁▁▁▁▁▁▁▁   (3 .. 4)
0:4 16:4 32:4 49:4 65:4 81:4 97:4 113:4 129:4 177:3 193:3 209:3 225:3 241:3 257:3 273:3 290:3
```

### `black_hole_tnt`  (`BlackHoleTNTEffect`)

- site `[3000, -60, 0]`, armed via **block+redstone**, declared fuse **500** ticks
- baseline **2.26 ms/t** (p99 16.00) -> peak avg **37.8 ms/t**, peak p99 **1433.9 ms/t**
- observed window: 816 game ticks over 40.8 s wall; worst wall-clock **754 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 2001

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▄▄▄▄▄▄▄▄▄▄▄▄▄▄▅▅▅▅▅▅▅▅▅▅▅▅▅▆▅▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▅▅▅▅▅▅▅▅▅▅█▇▇▆▆▆▆▆▅▅▅▅▅▄▄▄▄▄▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁██████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     1  avg=   3.30  p99=   86.70  ents=4
t=    14  avg=   3.40  p99=   86.70  ents=
t=    26  avg=   3.20  p99=   86.70  ents=
t=    38  avg=   3.30  p99=   86.70  ents=
t=    50  avg=   3.50  p99=   86.70  ents=4
t=    62  avg=   3.60  p99=   86.70  ents=
t=    75  avg=   3.70  p99=   86.70  ents=
t=    87  avg=   3.90  p99=   86.70  ents=
t=    99  avg=   3.10  p99=   18.10  ents=4
t=   111  avg=   2.60  p99=    4.70  ents=
t=   123  avg=   2.50  p99=    4.00  ents=
t=   135  avg=   2.50  p99=    4.00  ents=
t=   148  avg=   2.40  p99=    4.00  ents=4
t=   160  avg=   2.60  p99=   20.90  ents=
t=   173  avg=   5.70  p99=  111.90  ents=
t=   187  avg=   7.90  p99=  111.90  ents=
t=   199  avg=   8.90  p99=  111.90  ents=808
t=   211  avg=  10.40  p99=  111.90  ents=
t=   223  avg=  12.00  p99=  111.90  ents=
t=   235  avg=  14.10  p99=  111.90  ents=
t=   248  avg=  16.40  p99=  111.90  ents=1943
t=   260  avg=  18.50  p99=  111.90  ents=
t=   272  avg=  17.70  p99=   67.40  ents=
t=   284  avg=  17.80  p99=   30.50  ents=
t=   296  avg=  19.60  p99=   39.50  ents=1889
t=   309  avg=  20.60  p99=   39.50  ents=
t=   321  avg=  21.70  p99=   39.50  ents=
t=   333  avg=  22.50  p99=   39.50  ents=
t=   345  avg=  23.90  p99=   39.50  ents=1948
t=   358  avg=  24.00  p99=   39.50  ents=
t=   372  avg=  26.10  p99=  239.50  ents=
t=   384  avg=  25.90  p99=  239.50  ents=
t=   396  avg=  25.20  p99=  239.50  ents=1781
t=   408  avg=  25.00  p99=  239.50  ents=
t=   421  avg=  24.80  p99=  239.50  ents=
t=   433  avg=  24.40  p99=  239.50  ents=
t=   445  avg=  24.50  p99=  239.50  ents=1949
t=   458  avg=  24.60  p99=  239.50  ents=
t=   470  avg=  22.70  p99=   41.80  ents=
t=   482  avg=  23.00  p99=   41.80  ents=
t=   495  avg=  23.40  p99=   41.80  ents=1832
t=   533  avg=  30.70  p99= 1433.90  ents=
t=   545  avg=  27.00  p99= 1433.90  ents=
t=   557  avg=  24.40  p99= 1433.90  ents=
t=   570  avg=  21.50  p99= 1433.90  ents=0
t=   582  avg=  19.00  p99= 1433.90  ents=
t=   594  avg=  16.40  p99= 1433.90  ents=
t=   606  avg=   0.80  p99=    5.50  ents=
t=   618  avg=   0.90  p99=    5.50  ents=0
t=   630  avg=   0.90  p99=    5.50  ents=
t=   643  avg=   0.90  p99=    1.70  ents=
t=   655  avg=   0.80  p99=    1.70  ents=
t=   667  avg=   0.80  p99=    1.70  ents=0
t=   679  avg=   0.80  p99=    1.20  ents=
t=   691  avg=   0.80  p99=    1.20  ents=
t=   703  avg=   0.80  p99=    1.20  ents=
t=   715  avg=   0.80  p99=    1.20  ents=0
t=   727  avg=   0.80  p99=    1.20  ents=
t=   740  avg=   0.80  p99=    1.30  ents=
t=   752  avg=   0.80  p99=    1.30  ents=
t=   764  avg=   0.80  p99=    1.30  ents=0
t=   776  avg=   0.80  p99=    1.50  ents=
t=   788  avg=   0.80  p99=    1.50  ents=
t=   800  avg=   0.80  p99=    1.50  ents=
t=   812  avg=   0.80  p99=    2.20  ents=0
```

</details>

Entity count peaked at **2001** at t=359. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ▁▁▁▁▁▁▁▁▁▁▂▄▄▅▇████████▇███████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (0 .. 2001)
1:4 18:4 34:4 50:4 67:4 83:4 99:4 115:4 131:4 148:4 163:410 181:817 199:808 215:1198 231:1568 248:1943 264:1986 280:1911 296:1889 313:1889 329:1912 345:1948 359:2001 380:1698 396:1781 413:1845 429:1898 445:1949 462:1990 478:1819 495:1832 537:0 553:0 570:0 586:0 602:0 618:0 634:0 651:0 667:0 683:0 699:0 715:0 731:0 748:0 764:0 780:0 796:0 812:0
```

### `giant_tnt`  (`GiantTNTEffect`)

- site `[87000, -60, 0]`, armed via **block+redstone**, declared fuse **320** ticks
- baseline **0.91 ms/t** (p99 16.40) -> peak avg **14.5 ms/t**, peak p99 **1349.3 ms/t**
- observed window: 637 game ticks over 31.9 s wall; worst wall-clock **311 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.70  p99=    1.90  ents=4
t=     8  avg=   0.70  p99=    1.30  ents=
t=    16  avg=   0.70  p99=    1.60  ents=4
t=    24  avg=   0.70  p99=    1.60  ents=
t=    32  avg=   0.70  p99=    1.60  ents=4
t=    40  avg=   0.80  p99=    1.60  ents=
t=    48  avg=   0.80  p99=    1.60  ents=4
t=    56  avg=   0.90  p99=    1.60  ents=
t=    64  avg=   0.90  p99=    1.60  ents=4
t=    72  avg=   0.90  p99=    1.60  ents=
t=    81  avg=   0.90  p99=    1.60  ents=4
t=    89  avg=   1.00  p99=    1.60  ents=
t=    97  avg=   1.00  p99=    2.00  ents=4
t=   105  avg=   1.00  p99=    2.00  ents=
t=   113  avg=   1.00  p99=    2.00  ents=4
t=   121  avg=   1.00  p99=    2.00  ents=
t=   129  avg=   1.00  p99=    2.00  ents=4
t=   137  avg=   1.00  p99=    2.00  ents=
t=   145  avg=   1.00  p99=    2.00  ents=4
t=   153  avg=   1.00  p99=    2.00  ents=
t=   161  avg=   1.00  p99=    2.00  ents=4
t=   169  avg=   1.00  p99=    3.10  ents=
t=   177  avg=   1.00  p99=    3.10  ents=4
t=   185  avg=   1.00  p99=    3.10  ents=
t=   193  avg=   1.00  p99=    3.10  ents=4
t=   201  avg=   1.00  p99=    3.10  ents=
t=   209  avg=   1.00  p99=    3.10  ents=4
t=   217  avg=   1.00  p99=    3.10  ents=
t=   225  avg=   1.00  p99=    3.10  ents=4
t=   234  avg=   1.00  p99=    3.10  ents=
t=   242  avg=   1.00  p99=    3.10  ents=4
t=   250  avg=   1.00  p99=    3.10  ents=
t=   258  avg=   1.00  p99=    3.10  ents=4
t=   266  avg=   1.00  p99=    3.10  ents=
t=   274  avg=   1.00  p99=    1.30  ents=4
t=   282  avg=   1.00  p99=    1.30  ents=
t=   291  avg=   1.00  p99=    1.30  ents=4
t=   300  avg=   1.00  p99=    1.50  ents=
t=   308  avg=   1.00  p99=    1.50  ents=4
t=   316  avg=   1.00  p99=    1.50  ents=
t=   352  avg=  14.40  p99= 1349.30  ents=2
t=   360  avg=  14.40  p99= 1349.30  ents=
t=   368  avg=  14.40  p99= 1349.30  ents=2
t=   376  avg=  14.30  p99= 1349.30  ents=
t=   384  avg=  14.30  p99= 1349.30  ents=2
t=   392  avg=  14.30  p99= 1349.30  ents=
t=   400  avg=  14.30  p99= 1349.30  ents=2
t=   408  avg=  14.30  p99= 1349.30  ents=
t=   416  avg=  14.30  p99= 1349.30  ents=2
t=   424  avg=   0.80  p99=    2.30  ents=
t=   432  avg=   0.80  p99=    2.30  ents=2
t=   440  avg=   0.90  p99=    2.30  ents=
t=   448  avg=   0.90  p99=    1.50  ents=2
t=   456  avg=   0.90  p99=    1.30  ents=
t=   464  avg=   0.90  p99=    1.10  ents=2
t=   472  avg=   0.90  p99=    1.10  ents=
t=   480  avg=   0.90  p99=    1.10  ents=2
t=   488  avg=   0.80  p99=    1.00  ents=
t=   497  avg=   0.80  p99=    1.80  ents=2
t=   505  avg=   0.90  p99=    1.80  ents=
t=   513  avg=   0.90  p99=    1.80  ents=2
t=   521  avg=   0.90  p99=    1.80  ents=
t=   529  avg=   0.90  p99=    1.80  ents=2
t=   537  avg=   0.90  p99=    1.80  ents=
t=   545  avg=   0.90  p99=    1.80  ents=2
t=   553  avg=   0.90  p99=    1.80  ents=
t=   561  avg=   0.90  p99=    1.80  ents=2
t=   569  avg=   0.90  p99=    1.80  ents=
t=   577  avg=   0.90  p99=    1.80  ents=2
t=   585  avg=   0.90  p99=    1.80  ents=
t=   593  avg=   0.90  p99=    1.80  ents=2
t=   601  avg=   0.90  p99=    1.30  ents=
t=   609  avg=   0.90  p99=    1.30  ents=2
t=   617  avg=   0.90  p99=    1.30  ents=
t=   625  avg=   0.90  p99=    1.30  ents=2
t=   633  avg=   0.90  p99=    1.30  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (2 .. 4)
0:4 16:4 32:4 48:4 64:4 81:4 97:4 113:4 129:4 145:4 161:4 177:4 193:4 209:4 225:4 242:4 258:4 274:4 291:4 308:4 352:2 368:2 384:2 400:2 416:2 432:2 448:2 464:2 480:2 497:2 513:2 529:2 545:2 561:2 577:2 593:2 609:2 625:2
```

### `winter_tnt`  (`WinterTNTEffect`)

- site `[121000, -60, 0]`, armed via **block+redstone**, declared fuse **200** ticks
- baseline **1.28 ms/t** (p99 8.50) -> peak avg **27.4 ms/t**, peak p99 **1217.8 ms/t**
- observed window: 347 game ticks over 17.3 s wall; worst wall-clock **454 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 2040

```
avg ms/t  ▁▁▁▁▁▁▂▂▂▂▂▃▃▃▃▃▄▄▄▄▅▅▅▅▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▅▅▅▅█████▇▇▇▇▇▇▆▆▆▆▆▆▅▅▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███████████████████▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.50  p99=   19.40  ents=4
t=     4  avg=   2.30  p99=   32.40  ents=
t=     8  avg=   2.90  p99=   32.40  ents=
t=    12  avg=   3.30  p99=   32.40  ents=
t=    16  avg=   3.70  p99=   32.40  ents=820
t=    21  avg=   4.30  p99=   32.40  ents=
t=    25  avg=   4.80  p99=   32.40  ents=
t=    29  avg=   5.20  p99=   32.40  ents=
t=    34  avg=   5.80  p99=   32.40  ents=1738
t=    38  avg=   6.60  p99=   32.40  ents=
t=    42  avg=   7.30  p99=   32.40  ents=
t=    46  avg=   8.20  p99=   32.40  ents=
t=    50  avg=   9.00  p99=   32.40  ents=2003
t=    54  avg=   9.70  p99=   32.40  ents=
t=    58  avg=  10.40  p99=   32.40  ents=
t=    62  avg=  11.00  p99=   32.40  ents=
t=    66  avg=  11.70  p99=   32.40  ents=2033
t=    70  avg=  12.40  p99=   32.40  ents=
t=    74  avg=  13.20  p99=   32.40  ents=
t=    79  avg=  14.00  p99=   32.40  ents=
t=    83  avg=  15.10  p99=   34.80  ents=2017
t=    87  avg=  15.80  p99=   34.80  ents=
t=    91  avg=  16.70  p99=   34.80  ents=
t=    95  avg=  17.50  p99=   34.80  ents=
t=    99  avg=  18.20  p99=   34.80  ents=2015
t=   102  avg=  19.30  p99=  123.50  ents=
t=   108  avg=  19.20  p99=  123.50  ents=
t=   112  avg=  19.50  p99=  123.50  ents=
t=   116  avg=  19.70  p99=  123.50  ents=2014
t=   120  avg=  19.80  p99=  123.50  ents=
t=   124  avg=  19.80  p99=  123.50  ents=
t=   129  avg=  20.00  p99=  123.50  ents=
t=   133  avg=  20.10  p99=  123.50  ents=2017
t=   137  avg=  20.10  p99=  123.50  ents=
t=   141  avg=  20.00  p99=  123.50  ents=
t=   145  avg=  19.70  p99=  123.50  ents=
t=   149  avg=  19.30  p99=  123.50  ents=2016
t=   153  avg=  19.10  p99=  123.50  ents=
t=   157  avg=  19.00  p99=  123.50  ents=
t=   161  avg=  18.90  p99=  123.50  ents=
t=   165  avg=  18.70  p99=  123.50  ents=2012
t=   169  avg=  18.60  p99=  123.50  ents=
t=   173  avg=  18.40  p99=  123.50  ents=
t=   177  avg=  18.30  p99=  123.50  ents=
t=   181  avg=  17.90  p99=  123.50  ents=2040
t=   185  avg=  17.60  p99=  123.50  ents=
t=   189  avg=  17.20  p99=  123.50  ents=
t=   194  avg=  16.90  p99=  123.50  ents=
t=   198  avg=  16.60  p99=  123.50  ents=2002
t=   201  avg=  27.40  p99= 1217.80  ents=
t=   215  avg=  27.10  p99= 1217.80  ents=
t=   233  avg=  25.40  p99= 1217.80  ents=
t=   238  avg=  24.70  p99= 1217.80  ents=152
t=   242  avg=  24.20  p99= 1217.80  ents=
t=   246  avg=  23.70  p99= 1217.80  ents=
t=   250  avg=  23.20  p99= 1217.80  ents=
t=   254  avg=  22.60  p99= 1217.80  ents=3
t=   258  avg=  22.10  p99= 1217.80  ents=
t=   262  avg=  21.60  p99= 1217.80  ents=
t=   266  avg=  21.10  p99= 1217.80  ents=
t=   270  avg=  20.60  p99= 1217.80  ents=3
t=   274  avg=  20.00  p99= 1217.80  ents=
t=   278  avg=  19.50  p99= 1217.80  ents=
t=   282  avg=  19.00  p99= 1217.80  ents=
t=   286  avg=  18.40  p99= 1217.80  ents=3
t=   290  avg=  17.90  p99= 1217.80  ents=
t=   294  avg=  17.40  p99= 1217.80  ents=
t=   298  avg=  16.80  p99= 1217.80  ents=
t=   302  avg=   4.30  p99=   99.60  ents=3
t=   306  avg=   3.90  p99=   99.60  ents=
t=   310  avg=   3.60  p99=   99.60  ents=
t=   314  avg=   2.50  p99=    9.10  ents=
t=   318  avg=   2.20  p99=    8.10  ents=3
t=   322  avg=   2.00  p99=    6.50  ents=
t=   327  avg=   1.80  p99=    5.10  ents=
t=   331  avg=   1.70  p99=    5.10  ents=
t=   335  avg=   1.60  p99=    3.80  ents=3
t=   339  avg=   1.50  p99=    3.00  ents=
t=   343  avg=   1.50  p99=    3.00  ents=
t=   347  avg=   1.50  p99=    3.00  ents=
```

</details>

Entity count peaked at **2040** at t=181. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ▁▄▇██████████▁▁▁▁▁▁▁   (3 .. 2040)
0:4 16:820 34:1738 50:2003 66:2033 83:2017 99:2015 116:2014 133:2017 149:2016 165:2012 181:2040 198:2002 238:152 254:3 270:3 286:3 302:3 318:3 335:3
```

### `stone_cold`  (`StoneColdEffect`)

- site `[177000, -60, 0]`, armed via **block+redstone**, declared fuse **140** ticks
- baseline **1.40 ms/t** (p99 29.70) -> peak avg **12.5 ms/t**, peak p99 **1121.0 ms/t**
- observed window: 288 game ticks over 14.4 s wall; worst wall-clock **328 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.10  p99=    2.20  ents=4
t=     4  avg=   1.10  p99=    2.20  ents=
t=     8  avg=   1.10  p99=    2.20  ents=
t=    12  avg=   1.10  p99=    2.20  ents=
t=    16  avg=   1.10  p99=    2.20  ents=4
t=    20  avg=   1.10  p99=    2.20  ents=
t=    24  avg=   1.10  p99=    2.00  ents=
t=    28  avg=   1.10  p99=    2.00  ents=
t=    32  avg=   1.10  p99=    2.00  ents=4
t=    36  avg=   1.10  p99=    2.00  ents=
t=    40  avg=   1.10  p99=    2.00  ents=
t=    44  avg=   1.20  p99=    2.00  ents=
t=    48  avg=   1.20  p99=    2.00  ents=4
t=    52  avg=   1.20  p99=    2.00  ents=
t=    56  avg=   1.20  p99=    2.00  ents=
t=    60  avg=   1.20  p99=    2.00  ents=
t=    64  avg=   1.20  p99=    2.10  ents=4
t=    68  avg=   1.20  p99=    2.10  ents=
t=    72  avg=   1.20  p99=    2.10  ents=
t=    76  avg=   1.20  p99=    2.10  ents=
t=    80  avg=   1.30  p99=    2.10  ents=4
t=    84  avg=   1.30  p99=    2.10  ents=
t=    88  avg=   1.30  p99=    2.10  ents=
t=    92  avg=   1.30  p99=    2.10  ents=
t=    96  avg=   1.30  p99=    2.10  ents=4
t=   100  avg=   1.30  p99=    2.10  ents=
t=   105  avg=   1.30  p99=    2.10  ents=
t=   109  avg=   1.30  p99=    2.10  ents=
t=   113  avg=   1.30  p99=    2.10  ents=4
t=   117  avg=   1.30  p99=    2.10  ents=
t=   121  avg=   1.30  p99=    2.10  ents=
t=   125  avg=   1.30  p99=    2.10  ents=
t=   129  avg=   1.30  p99=    2.10  ents=4
t=   133  avg=   1.30  p99=    2.10  ents=
t=   137  avg=   1.30  p99=    2.10  ents=
t=   141  avg=  12.50  p99= 1121.00  ents=
t=   167  avg=  12.20  p99= 1121.00  ents=3
t=   171  avg=  12.20  p99= 1121.00  ents=
t=   175  avg=  12.20  p99= 1121.00  ents=
t=   179  avg=  12.20  p99= 1121.00  ents=
t=   183  avg=  12.20  p99= 1121.00  ents=3
t=   187  avg=  12.20  p99= 1121.00  ents=
t=   191  avg=  12.20  p99= 1121.00  ents=
t=   195  avg=  12.10  p99= 1121.00  ents=
t=   199  avg=  12.10  p99= 1121.00  ents=3
t=   203  avg=  12.10  p99= 1121.00  ents=
t=   207  avg=  12.10  p99= 1121.00  ents=
t=   211  avg=  12.10  p99= 1121.00  ents=
t=   215  avg=  12.10  p99= 1121.00  ents=3
t=   219  avg=  12.00  p99= 1121.00  ents=
t=   223  avg=  12.00  p99= 1121.00  ents=
t=   227  avg=  12.00  p99= 1121.00  ents=
t=   231  avg=  12.00  p99= 1121.00  ents=3
t=   235  avg=  12.00  p99= 1121.00  ents=
t=   239  avg=  12.00  p99= 1121.00  ents=
t=   243  avg=   0.80  p99=    1.40  ents=
t=   248  avg=   0.80  p99=    1.40  ents=3
t=   252  avg=   0.80  p99=    1.40  ents=
t=   256  avg=   0.80  p99=    1.40  ents=
t=   260  avg=   0.90  p99=    1.40  ents=
t=   264  avg=   0.90  p99=    1.40  ents=3
t=   268  avg=   0.90  p99=    1.30  ents=
t=   272  avg=   0.90  p99=    1.30  ents=
t=   276  avg=   0.90  p99=    1.30  ents=
t=   280  avg=   0.90  p99=    1.30  ents=3
t=   284  avg=   0.90  p99=    1.30  ents=
t=   288  avg=   0.90  p99=    1.30  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  █████████▁▁▁▁▁▁▁▁   (3 .. 4)
0:4 16:4 32:4 48:4 64:4 80:4 96:4 113:4 129:4 167:3 183:3 199:3 215:3 231:3 248:3 264:3 280:3
```

### `flat_earth`  (`FlatTNTEffect`)

- site `[291000, -60, 0]`, armed via **block+redstone**, declared fuse **80** ticks
- baseline **0.98 ms/t** (p99 19.30) -> peak avg **11.4 ms/t**, peak p99 **1030.8 ms/t**
- observed window: 318 game ticks over 15.9 s wall; worst wall-clock **379 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.70  p99=    1.80  ents=4
t=     4  avg=   0.70  p99=    1.80  ents=
t=     8  avg=   0.70  p99=    1.80  ents=
t=    12  avg=   0.70  p99=    1.20  ents=
t=    16  avg=   0.70  p99=    1.20  ents=4
t=    20  avg=   0.70  p99=    1.20  ents=
t=    24  avg=   0.70  p99=    1.50  ents=
t=    28  avg=   0.70  p99=    1.50  ents=
t=    32  avg=   0.70  p99=    1.50  ents=4
t=    36  avg=   0.70  p99=    1.50  ents=
t=    40  avg=   0.80  p99=    1.50  ents=
t=    44  avg=   0.80  p99=    1.50  ents=
t=    49  avg=   0.80  p99=    1.50  ents=4
t=    53  avg=   0.80  p99=    1.50  ents=
t=    57  avg=   0.80  p99=    1.50  ents=
t=    61  avg=   0.80  p99=    1.50  ents=
t=    65  avg=   0.90  p99=    1.50  ents=4
t=    69  avg=   0.90  p99=    1.50  ents=
t=    73  avg=   0.90  p99=    1.50  ents=
t=    77  avg=   0.90  p99=    1.50  ents=
t=    81  avg=   0.90  p99=    1.50  ents=4
t=    85  avg=   0.90  p99=    1.50  ents=
t=    89  avg=   0.90  p99=    1.50  ents=
t=    93  avg=   0.90  p99=    1.50  ents=
t=    97  avg=   1.00  p99=    1.50  ents=4
t=   101  avg=   1.00  p99=    1.50  ents=
t=   105  avg=   1.00  p99=    1.50  ents=
t=   109  avg=   1.00  p99=    1.50  ents=
t=   113  avg=   1.00  p99=    1.50  ents=4
t=   117  avg=   1.00  p99=    1.50  ents=
t=   121  avg=   1.00  p99=    1.30  ents=
t=   125  avg=   1.00  p99=    1.30  ents=
t=   129  avg=   1.00  p99=    1.30  ents=4
t=   133  avg=   1.00  p99=    1.30  ents=
t=   137  avg=   1.00  p99=    1.30  ents=
t=   141  avg=   1.00  p99=    1.30  ents=
t=   145  avg=   1.00  p99=    1.30  ents=4
t=   149  avg=   1.00  p99=    1.30  ents=
t=   153  avg=   1.00  p99=    1.30  ents=
t=   157  avg=   1.00  p99=    1.30  ents=
t=   161  avg=   0.90  p99=    1.30  ents=4
t=   165  avg=   0.90  p99=    1.30  ents=
t=   169  avg=   0.90  p99=    1.30  ents=
t=   173  avg=   0.90  p99=    1.30  ents=
t=   177  avg=   0.90  p99=    1.30  ents=4
t=   182  avg=   0.90  p99=    1.30  ents=
t=   186  avg=   0.90  p99=    1.30  ents=
t=   190  avg=   0.90  p99=    1.30  ents=
t=   194  avg=   0.90  p99=    1.30  ents=4
t=   198  avg=   0.90  p99=    1.30  ents=
t=   201  avg=  11.20  p99= 1030.80  ents=
t=   225  avg=  11.30  p99= 1030.80  ents=
t=   229  avg=  11.40  p99= 1030.80  ents=3
t=   233  avg=  11.40  p99= 1030.80  ents=
t=   237  avg=  11.40  p99= 1030.80  ents=
t=   241  avg=  11.40  p99= 1030.80  ents=
t=   245  avg=  11.40  p99= 1030.80  ents=3
t=   249  avg=  11.40  p99= 1030.80  ents=
t=   253  avg=  11.40  p99= 1030.80  ents=
t=   257  avg=  11.40  p99= 1030.80  ents=
t=   261  avg=  11.40  p99= 1030.80  ents=3
t=   265  avg=  11.40  p99= 1030.80  ents=
t=   269  avg=  11.40  p99= 1030.80  ents=
t=   273  avg=  11.40  p99= 1030.80  ents=
t=   278  avg=  11.40  p99= 1030.80  ents=3
t=   282  avg=  11.40  p99= 1030.80  ents=
t=   286  avg=  11.40  p99= 1030.80  ents=
t=   290  avg=  11.40  p99= 1030.80  ents=
t=   294  avg=  11.40  p99= 1030.80  ents=3
t=   298  avg=  11.40  p99= 1030.80  ents=
t=   302  avg=   1.00  p99=   12.70  ents=
t=   306  avg=   0.90  p99=    6.80  ents=
t=   310  avg=   0.90  p99=    6.80  ents=3
t=   314  avg=   1.00  p99=    6.80  ents=
t=   318  avg=   1.00  p99=    6.80  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  █████████████▁▁▁▁▁▁   (3 .. 4)
0:4 16:4 32:4 49:4 65:4 81:4 97:4 113:4 129:4 145:4 161:4 177:4 194:4 229:3 245:3 261:3 278:3 294:3 310:3
```

### `hungry_tnt`  (`HungryTNTEffect`)

- site `[59000, -60, 0]`, armed via **block+redstone**, declared fuse **600** ticks
- baseline **1.20 ms/t** (p99 25.40) -> peak avg **10.7 ms/t**, peak p99 **969.2 ms/t**
- observed window: 918 game ticks over 45.9 s wall; worst wall-clock **981 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.00  p99=    1.70  ents=4
t=    12  avg=   1.00  p99=    1.70  ents=
t=    24  avg=   0.90  p99=    1.40  ents=
t=    36  avg=   0.90  p99=    1.40  ents=
t=    48  avg=   1.00  p99=    1.40  ents=1
t=    60  avg=   1.00  p99=    1.40  ents=
t=    73  avg=   1.00  p99=    1.40  ents=
t=    85  avg=   1.00  p99=    1.40  ents=
t=    97  avg=   1.00  p99=    1.40  ents=1
t=   109  avg=   1.00  p99=    1.40  ents=
t=   121  avg=   1.00  p99=    1.40  ents=
t=   133  avg=   1.00  p99=    1.40  ents=
t=   145  avg=   1.10  p99=    1.80  ents=1
t=   157  avg=   1.10  p99=    1.80  ents=
t=   169  avg=   1.10  p99=    1.80  ents=
t=   181  avg=   1.10  p99=    1.80  ents=
t=   193  avg=   1.10  p99=    1.80  ents=1
t=   205  avg=   1.00  p99=    1.80  ents=
t=   217  avg=   1.00  p99=    1.80  ents=
t=   230  avg=   1.00  p99=    1.80  ents=
t=   242  avg=   1.00  p99=    1.30  ents=1
t=   254  avg=   1.00  p99=    1.30  ents=
t=   266  avg=   1.00  p99=    1.30  ents=
t=   278  avg=   1.00  p99=    1.30  ents=
t=   290  avg=   1.00  p99=    1.40  ents=1
t=   302  avg=   1.00  p99=    1.40  ents=
t=   314  avg=   1.00  p99=    1.40  ents=
t=   326  avg=   1.00  p99=    1.40  ents=
t=   339  avg=   1.00  p99=    1.40  ents=1
t=   351  avg=   1.00  p99=    1.40  ents=
t=   363  avg=   1.00  p99=    1.40  ents=
t=   375  avg=   1.00  p99=    1.40  ents=
t=   387  avg=   1.00  p99=    1.30  ents=1
t=   399  avg=   1.00  p99=    1.30  ents=
t=   411  avg=   1.00  p99=    1.40  ents=
t=   423  avg=   1.00  p99=    1.40  ents=
t=   435  avg=   1.00  p99=    1.40  ents=1
t=   447  avg=   1.00  p99=    1.40  ents=
t=   459  avg=   1.00  p99=    1.40  ents=
t=   471  avg=   1.00  p99=    1.40  ents=
t=   484  avg=   1.00  p99=    1.40  ents=1
t=   496  avg=   1.00  p99=    1.40  ents=
t=   508  avg=   1.00  p99=    1.30  ents=
t=   520  avg=   1.00  p99=    1.30  ents=
t=   532  avg=   1.00  p99=    1.30  ents=1
t=   544  avg=   1.00  p99=    1.30  ents=
t=   556  avg=   1.00  p99=    1.30  ents=
t=   568  avg=   1.00  p99=    1.30  ents=
t=   580  avg=   1.00  p99=    1.30  ents=1
t=   592  avg=   1.00  p99=    1.30  ents=
t=   601  avg=  10.70  p99=  969.20  ents=
t=   632  avg=  10.60  p99=  969.20  ents=
t=   644  avg=  10.60  p99=  969.20  ents=0
t=   656  avg=  10.60  p99=  969.20  ents=
t=   668  avg=  10.50  p99=  969.20  ents=
t=   680  avg=  10.50  p99=  969.20  ents=
t=   692  avg=  10.50  p99=  969.20  ents=0
t=   704  avg=   0.80  p99=    1.60  ents=
t=   716  avg=   0.90  p99=    1.60  ents=
t=   729  avg=   0.90  p99=    1.20  ents=
t=   741  avg=   0.90  p99=    1.20  ents=0
t=   753  avg=   0.90  p99=    1.20  ents=
t=   765  avg=   0.90  p99=    1.20  ents=
t=   777  avg=   0.90  p99=    1.20  ents=
t=   789  avg=   0.90  p99=    1.20  ents=0
t=   801  avg=   0.90  p99=    1.20  ents=
t=   813  avg=   0.90  p99=    1.20  ents=
t=   825  avg=   0.90  p99=    1.50  ents=
t=   837  avg=   0.90  p99=    1.50  ents=0
t=   849  avg=   0.90  p99=    1.50  ents=
t=   861  avg=   0.90  p99=    1.50  ents=
t=   873  avg=   0.90  p99=    1.50  ents=
t=   886  avg=   0.90  p99=    1.50  ents=0
t=   898  avg=   0.90  p99=    1.50  ents=
t=   910  avg=   0.90  p99=    1.50  ents=
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  █▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (0 .. 4)
0:4 16:1 32:1 48:1 65:1 81:1 97:1 113:1 129:1 145:1 161:1 177:1 193:1 209:1 226:1 242:1 258:1 274:1 290:1 306:1 322:1 339:1 355:1 371:1 387:1 403:1 419:1 435:1 451:1 467:1 484:1 500:1 516:1 532:1 548:1 564:1 580:1 596:1 628:0 644:0 660:0 676:0 692:0 708:0 725:0 741:0 757:0 773:0 789:0 805:0 821:0 837:0 853:0 869:0 886:0 902:0 918:0
```

### `pulsar_tnt`  (`PulsarTNTEffect`)

- site `[73000, -60, 0]`, armed via **block+redstone**, declared fuse **400** ticks
- baseline **1.42 ms/t** (p99 63.40) -> peak avg **27.4 ms/t**, peak p99 **848.6 ms/t**
- observed window: 716 game ticks over 35.8 s wall; worst wall-clock **633 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▃▃▂▂▂▂▂▃▃▃▃▃▃▄▄▄▄▄▄▅▅▅▅▅▆▅▅▅▅▇▆▆▆█▇▇▇▇▇▅▅▅▅▅▅▅▅▃▃▃▃▃▃▃▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▄▄▄▄▄▄▅▅▅▅▅▅▆▆▆▆▆▆▆▆▆▆▇▇▇▇█████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.80  p99=    1.40  ents=4
t=     8  avg=   0.70  p99=    1.40  ents=
t=    17  avg=   0.70  p99=    1.30  ents=4
t=    25  avg=   0.70  p99=    1.30  ents=
t=    33  avg=   0.80  p99=    1.30  ents=4
t=    41  avg=   0.80  p99=    1.30  ents=
t=    49  avg=   0.80  p99=    1.30  ents=4
t=    57  avg=   0.80  p99=    1.30  ents=
t=    65  avg=   0.80  p99=    1.30  ents=4
t=    73  avg=   0.80  p99=    1.30  ents=
t=    81  avg=   0.90  p99=    1.30  ents=4
t=    89  avg=   0.90  p99=    1.30  ents=
t=    97  avg=   0.90  p99=    1.30  ents=4
t=   106  avg=   1.50  p99=   57.20  ents=
t=   114  avg=   1.50  p99=   57.20  ents=3
t=   122  avg=   1.50  p99=   57.20  ents=
t=   130  avg=   1.50  p99=   57.20  ents=3
t=   138  avg=   2.40  p99=   87.60  ents=
t=   146  avg=   2.40  p99=   87.60  ents=3
t=   154  avg=   2.40  p99=   87.60  ents=
t=   161  avg=   3.60  p99=  129.00  ents=3
t=   171  avg=   3.60  p99=  129.00  ents=
t=   180  avg=   3.60  p99=  129.00  ents=3
t=   188  avg=   3.60  p99=  129.00  ents=
t=   198  avg=   5.50  p99=  187.30  ents=3
t=   206  avg=   4.90  p99=  187.30  ents=
t=   214  avg=   4.90  p99=  187.30  ents=3
t=   221  avg=   7.40  p99=  255.70  ents=
t=   234  avg=   6.50  p99=  255.70  ents=3
t=   242  avg=   6.50  p99=  255.70  ents=
t=   250  avg=   6.50  p99=  255.70  ents=3
t=   261  avg=   8.50  p99=  328.20  ents=
t=   269  avg=   8.50  p99=  328.20  ents=3
t=   278  avg=   8.50  p99=  328.20  ents=
t=   294  avg=  11.10  p99=  458.10  ents=3
t=   302  avg=  11.10  p99=  458.10  ents=
t=   310  avg=  11.10  p99=  458.10  ents=3
t=   326  avg=  14.50  p99=  596.70  ents=
t=   335  avg=  14.50  p99=  596.70  ents=3
t=   341  avg=  19.90  p99=  596.70  ents=
t=   359  avg=  16.60  p99=  596.70  ents=3
t=   367  avg=  16.60  p99=  596.70  ents=
t=   388  avg=  18.90  p99=  696.30  ents=3
t=   397  avg=  18.90  p99=  696.30  ents=
t=   422  avg=  21.40  p99=  848.60  ents=2
t=   430  avg=  21.40  p99=  848.60  ents=
t=   438  avg=  21.40  p99=  848.60  ents=2
t=   446  avg=  16.00  p99=  848.60  ents=
t=   454  avg=  16.00  p99=  848.60  ents=2
t=   462  avg=  16.00  p99=  848.60  ents=
t=   470  avg=  16.00  p99=  848.60  ents=2
t=   478  avg=   9.10  p99=  848.60  ents=
t=   486  avg=   9.10  p99=  848.60  ents=2
t=   494  avg=   9.10  p99=  848.60  ents=
t=   502  avg=   0.70  p99=    1.20  ents=2
t=   510  avg=   0.70  p99=    1.20  ents=
t=   518  avg=   0.70  p99=    1.20  ents=2
t=   526  avg=   0.70  p99=    1.20  ents=
t=   534  avg=   0.70  p99=    1.20  ents=2
t=   542  avg=   0.70  p99=    1.20  ents=
t=   551  avg=   0.70  p99=    1.20  ents=2
t=   559  avg=   0.70  p99=    1.20  ents=
t=   567  avg=   0.70  p99=    1.20  ents=2
t=   575  avg=   0.70  p99=    1.20  ents=
t=   583  avg=   0.70  p99=    1.20  ents=2
t=   591  avg=   0.70  p99=    1.00  ents=
t=   599  avg=   0.70  p99=    1.00  ents=2
t=   607  avg=   0.70  p99=    1.00  ents=
t=   615  avg=   0.70  p99=    1.00  ents=2
t=   623  avg=   0.70  p99=    1.00  ents=
t=   631  avg=   0.70  p99=    1.00  ents=2
t=   639  avg=   0.70  p99=    1.00  ents=
t=   647  avg=   0.70  p99=    1.00  ents=2
t=   655  avg=   0.70  p99=    1.00  ents=
t=   663  avg=   0.70  p99=    1.00  ents=2
t=   671  avg=   0.70  p99=    1.00  ents=
t=   679  avg=   0.70  p99=    1.00  ents=2
t=   688  avg=   0.70  p99=    1.00  ents=
t=   696  avg=   0.70  p99=    1.40  ents=2
t=   704  avg=   0.70  p99=    1.40  ents=
t=   712  avg=   0.70  p99=    1.40  ents=2
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ███████▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (2 .. 4)
0:4 17:4 33:4 49:4 65:4 81:4 97:4 114:3 130:3 146:3 161:3 180:3 198:3 214:3 234:3 250:3 269:3 294:3 310:3 335:3 359:3 388:3 422:2 438:2 454:2 470:2 486:2 502:2 518:2 534:2 551:2 567:2 583:2 599:2 615:2 631:2 647:2 663:2 679:2 696:2 712:2
```

### `aether_tnt`  (`AetherTNTEffect`)

- site `[113000, -60, 0]`, armed via **block+redstone**, declared fuse **200** ticks
- baseline **1.51 ms/t** (p99 49.20) -> peak avg **8.5 ms/t**, peak p99 **750.8 ms/t**
- observed window: 517 game ticks over 25.9 s wall; worst wall-clock **229 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁██████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁██████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.90  p99=    1.60  ents=4
t=     8  avg=   0.90  p99=    1.50  ents=
t=    16  avg=   0.90  p99=    1.50  ents=4
t=    24  avg=   0.90  p99=    1.20  ents=
t=    32  avg=   0.90  p99=    1.30  ents=4
t=    40  avg=   0.90  p99=    1.60  ents=
t=    48  avg=   0.90  p99=    1.60  ents=4
t=    56  avg=   1.00  p99=    1.60  ents=
t=    64  avg=   1.00  p99=    1.60  ents=4
t=    72  avg=   1.00  p99=    1.60  ents=
t=    80  avg=   1.00  p99=    1.60  ents=4
t=    89  avg=   1.00  p99=    1.60  ents=
t=    97  avg=   1.00  p99=    1.60  ents=4
t=   105  avg=   1.00  p99=    1.60  ents=
t=   113  avg=   1.00  p99=    1.60  ents=4
t=   121  avg=   1.00  p99=    1.60  ents=
t=   129  avg=   1.00  p99=    1.60  ents=4
t=   137  avg=   1.00  p99=    1.40  ents=
t=   145  avg=   1.00  p99=    1.40  ents=4
t=   153  avg=   1.00  p99=    1.40  ents=
t=   161  avg=   1.00  p99=    1.40  ents=4
t=   169  avg=   1.00  p99=    1.40  ents=
t=   177  avg=   1.00  p99=    1.40  ents=4
t=   185  avg=   1.00  p99=    1.40  ents=
t=   193  avg=   1.00  p99=    1.40  ents=4
t=   201  avg=   8.50  p99=  750.80  ents=
t=   224  avg=   8.40  p99=  750.80  ents=3
t=   232  avg=   8.40  p99=  750.80  ents=
t=   240  avg=   8.40  p99=  750.80  ents=3
t=   248  avg=   8.40  p99=  750.80  ents=
t=   256  avg=   8.40  p99=  750.80  ents=3
t=   264  avg=   8.40  p99=  750.80  ents=
t=   272  avg=   8.40  p99=  750.80  ents=3
t=   280  avg=   8.40  p99=  750.80  ents=
t=   288  avg=   8.40  p99=  750.80  ents=3
t=   296  avg=   8.40  p99=  750.80  ents=
t=   304  avg=   0.90  p99=    1.20  ents=3
t=   312  avg=   0.90  p99=    1.30  ents=
t=   320  avg=   0.90  p99=    1.30  ents=3
t=   328  avg=   0.90  p99=    1.30  ents=
t=   336  avg=   0.90  p99=    1.30  ents=3
t=   344  avg=   0.90  p99=    1.30  ents=
t=   352  avg=   0.90  p99=    1.30  ents=3
t=   360  avg=   0.90  p99=    1.30  ents=
t=   368  avg=   0.90  p99=    1.30  ents=3
t=   376  avg=   0.90  p99=    1.30  ents=
t=   384  avg=   0.90  p99=    1.30  ents=3
t=   393  avg=   0.90  p99=    1.30  ents=
t=   401  avg=   0.90  p99=    1.30  ents=3
t=   409  avg=   1.00  p99=    1.30  ents=
t=   417  avg=   1.00  p99=    1.30  ents=3
t=   425  avg=   1.00  p99=    1.80  ents=
t=   433  avg=   1.00  p99=    1.80  ents=3
t=   441  avg=   1.00  p99=    1.80  ents=
t=   449  avg=   1.00  p99=    1.80  ents=3
t=   457  avg=   1.00  p99=    1.80  ents=
t=   465  avg=   1.00  p99=    1.80  ents=3
t=   473  avg=   1.00  p99=    1.80  ents=
t=   481  avg=   1.00  p99=    1.80  ents=3
t=   489  avg=   1.00  p99=    1.80  ents=
t=   497  avg=   1.00  p99=    1.80  ents=3
t=   505  avg=   0.90  p99=    1.80  ents=
t=   513  avg=   0.90  p99=    1.80  ents=3
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  █████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (3 .. 4)
0:4 16:4 32:4 48:4 64:4 80:4 97:4 113:4 129:4 145:4 161:4 177:4 193:4 224:3 240:3 256:3 272:3 288:3 304:3 320:3 336:3 352:3 368:3 384:3 401:3 417:3 433:3 449:3 465:3 481:3 497:3 513:3
```

### `fluorine_tnt`  (`FluorineTNTEffect`)

- site `[71000, -60, 0]`, armed via **block+redstone**, declared fuse **400** ticks
- baseline **1.13 ms/t** (p99 21.90) -> peak avg **42.2 ms/t**, peak p99 **739.1 ms/t**
- observed window: 718 game ticks over 35.9 s wall; worst wall-clock **396 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▄▅▅▅▆▆▇▆▇▇██▇▇▆▆▇▇▇▆▆▆▇▆▆▆▆▅▅▆▆▇▆▇▇▇▇▆▆▆▅▅▅▅▅▅▅▄▄▄▃▃▃▃▃▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▄▄▄▄▄▆▆▆▆▆▆▆▆████████████▇▇▇▇▇▇▇▆▆▆▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▄▃▃▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.00  p99=    1.70  ents=4
t=     8  avg=   1.00  p99=    1.70  ents=
t=    16  avg=   1.00  p99=    1.70  ents=4
t=    24  avg=   1.00  p99=    1.70  ents=
t=    32  avg=   1.00  p99=    1.70  ents=4
t=    40  avg=   1.00  p99=    1.70  ents=
t=    48  avg=   1.00  p99=    1.70  ents=4
t=    56  avg=   1.00  p99=    1.70  ents=
t=    64  avg=   1.10  p99=    1.70  ents=4
t=    73  avg=   1.10  p99=    1.70  ents=
t=    81  avg=   1.10  p99=    1.70  ents=4
t=    89  avg=   1.10  p99=    1.70  ents=
t=   101  avg=   4.50  p99=  339.30  ents=3
t=   109  avg=   4.60  p99=  339.30  ents=
t=   115  avg=   9.50  p99=  486.00  ents=3
t=   132  avg=   9.40  p99=  486.00  ents=
t=   140  avg=   9.40  p99=  486.00  ents=3
t=   148  avg=   9.50  p99=  486.00  ents=
t=   155  avg=  16.80  p99=  739.10  ents=3
t=   165  avg=  24.30  p99=  739.10  ents=
t=   190  avg=  28.80  p99=  739.10  ents=3
t=   209  avg=  33.70  p99=  739.10  ents=
t=   221  avg=  34.20  p99=  739.10  ents=3
t=   238  avg=  39.10  p99=  739.10  ents=
t=   255  avg=  36.00  p99=  599.90  ents=3
t=   263  avg=  30.90  p99=  599.90  ents=
t=   271  avg=  32.40  p99=  599.90  ents=3
t=   288  avg=  34.90  p99=  599.90  ents=
t=   300  avg=  29.00  p99=  534.30  ents=3
t=   309  avg=  31.90  p99=  611.80  ents=
t=   327  avg=  31.60  p99=  611.80  ents=3
t=   338  avg=  28.40  p99=  611.80  ents=
t=   349  avg=  25.30  p99=  611.80  ents=3
t=   360  avg=  27.30  p99=  611.80  ents=
t=   380  avg=  30.60  p99=  611.80  ents=3
t=   388  avg=  34.20  p99=  611.80  ents=
t=   408  avg=  34.60  p99=  611.80  ents=2
t=   416  avg=  28.50  p99=  525.60  ents=
t=   424  avg=  26.00  p99=  525.60  ents=2
t=   432  avg=  23.50  p99=  525.60  ents=
t=   440  avg=  21.60  p99=  525.60  ents=2
t=   448  avg=  21.60  p99=  525.60  ents=
t=   456  avg=  18.40  p99=  525.60  ents=2
t=   464  avg=  14.50  p99=  525.60  ents=
t=   472  avg=  11.20  p99=  525.60  ents=2
t=   480  avg=  11.20  p99=  525.60  ents=
t=   488  avg=   3.10  p99=  246.90  ents=2
t=   496  avg=   0.70  p99=    1.90  ents=
t=   504  avg=   0.70  p99=    1.20  ents=2
t=   513  avg=   0.70  p99=    1.10  ents=
t=   521  avg=   0.70  p99=    1.10  ents=2
t=   529  avg=   0.70  p99=    1.10  ents=
t=   537  avg=   0.70  p99=    1.10  ents=2
t=   545  avg=   0.70  p99=    1.10  ents=
t=   553  avg=   0.70  p99=    1.10  ents=2
t=   561  avg=   0.70  p99=    1.10  ents=
t=   569  avg=   0.70  p99=    1.10  ents=2
t=   577  avg=   0.70  p99=    1.10  ents=
t=   585  avg=   0.70  p99=    1.00  ents=2
t=   593  avg=   0.70  p99=    1.00  ents=
t=   601  avg=   0.70  p99=    1.00  ents=2
t=   609  avg=   0.70  p99=    1.00  ents=
t=   617  avg=   0.70  p99=    1.00  ents=2
t=   625  avg=   0.70  p99=    0.90  ents=
t=   633  avg=   0.70  p99=    0.90  ents=2
t=   641  avg=   0.70  p99=    0.90  ents=
t=   649  avg=   0.70  p99=    0.90  ents=2
t=   657  avg=   0.70  p99=    0.90  ents=
t=   666  avg=   0.70  p99=    0.90  ents=2
t=   674  avg=   0.70  p99=    0.90  ents=
t=   682  avg=   0.60  p99=    0.90  ents=2
t=   690  avg=   0.60  p99=    0.90  ents=
t=   698  avg=   0.60  p99=    0.80  ents=2
t=   706  avg=   0.60  p99=    0.80  ents=
t=   714  avg=   0.60  p99=    0.80  ents=2
```

</details>

Entity count peaked at **4** at t=0. Full entity time series (game tick since arming -> live non-player entities within 400 blocks):

```
entities  ██████▄▄▄▄▄▄▄▄▄▄▄▄▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁   (2 .. 4)
0:4 16:4 32:4 48:4 64:4 81:4 101:3 115:3 140:3 155:3 190:3 221:3 255:3 271:3 300:3 327:3 349:3 380:3 408:2 424:2 440:2 456:2 472:2 488:2 504:2 521:2 537:2 553:2 569:2 585:2 601:2 617:2 633:2 649:2 666:2 682:2 698:2 714:2
```

---

## Are these numbers defensible? (instrument validity)

The worst ticks here are 12–24 **seconds**, ~250–480x the 50 ms budget. That is a big
enough claim to deserve an audit of the instrument, so here it is.

### What `/tick query` actually reports

The dedicated server keeps a ring buffer of the **last 100 tick durations** in
nanoseconds. `/tick query` prints:

- `Average time per tick: X ms` — the **arithmetic mean of those 100 samples**
- `P50 / P95 / P99` — percentiles over **the same 100 samples**, so with n=100 the
  P99 is effectively *the worst tick in the window*.

Two consequences, and neither of them inflates the numbers:

**1. The average is not "distorted" by a multi-second tick — it is diluted by it.**
A single 12,094 ms tick inside a window of 99 otherwise-cheap ticks contributes
12094/100 = 120.9 ms to the reported average. The measured `peak avg` for
`hydrogen_bomb` was **123.0 ms**. So the mean is behaving exactly as arithmetic says
it should. The `peak avg` column therefore *understates* instantaneous pain by ~100x
whenever the event is one big tick; it is only a meaningful "sustained load" number
for events that are genuinely spread over many ticks. That is why the table is sorted
by `peak p99`, not by `peak avg`.

This gives a free consistency check, reported as the **shape** column:

```
spike_share = peak_p99 / (peak_avg * 100)
```

- `spike_share -> 1.0` means one tick accounts for essentially the whole window's cost
  — a **single spike** (a freeze).
- `spike_share -> 0.0` means the cost is spread across the window — **sustained** load
  (a long brown-out).

Measured examples: `tnt_x10000` 0.99, `hydrogen_bomb` 0.98, `tsar_bomba` 0.95
(one catastrophic tick each) versus `black_hole_tnt` 0.38 and `grande_finale` 0.026
(many expensive ticks in a row). The arithmetic closing to ~1.0 on the spike cases is
independent evidence that both the average and the P99 are being read correctly.

**2. Does the window slide past the spike before we sample it?** No. The window is
100 *ticks*, not 100 ms. After a spike the server runs the following 100 ticks at
roughly 20 TPS, so the spike stays inside the buffer for about **5 seconds of wall
clock**. Sampling every ~200 ms gives ~25 independent observations of the same spike.
This is visible directly in the raw series — e.g. `tsar_bomba` reports the identical
`p99=6972.60` across 13 consecutive samples before it ages out. Nothing is being
missed, and nothing is being double-counted either, because we take the **max** over
the event rather than summing.

### `worst_wall_ms_per_tick` — a fully independent second instrument

This number never touches `/tick query`. For each adjacent pair of samples it is:

```
worst_wall_ms_per_tick = max over samples of  (wall_clock_delta_ms) / (gametime_delta_ticks)
```

where `gametime_delta_ticks` comes from `/time query gametime` (the server's real game
tick counter) and `wall_clock_delta_ms` comes from the benchmark client's own clock.
A healthy server pinned at 20 TPS reads **exactly 50 ms**; that is the floor, and it is
what every non-lagging TNT in the table reports (50–60 ms).

It is deliberately a **lower bound** on the worst tick, because a sampling gap may span
more than one game tick, which divides the stall across them. That is exactly what the
data shows: `hydrogen_bomb` reads 6079 ms wall against a 12094 ms P99 — the ~12 s stall
straddled a 2-tick gap. So the two instruments corroborate each other, with the
independent one being the conservative of the two.

Third corroboration: the server's own `Can't keep up! Running Xms or Y ticks behind`
warnings, scraped from `logs/latest.log` and attributed per TNT, agree with both.

### Summary of what to trust

| Claim | Trust | Why |
|---|---|---|
| "This TNT froze the server for N seconds" | **High** | Agreed on by three independent instruments (P99, wall-clock/game-tick ratio, `Can't keep up`) |
| Relative ranking of TNTs | **High** | Same host, same world, same procedure, sequential |
| Exact ms of the single worst tick | **Medium** | P99 of a 100-sample window ≈ but not provably = the max |
| Absolute ms on other hardware | **Low** | 4 vCPU cloud VM; a faster CPU scales all of these down |
| `peak avg` as "sustained load" | **Only when `shape` = sustained** | Otherwise it is a spike smeared over 100 ticks |

---

## Coverage

Measured **158 / 203** registered TNT variants. The sweep runs in priority order (the 20 the user called out first, then everything else sorted by fuse length and effect-class size), so the untested tail is the *short-fuse, small-effect* end of the distribution.

<details><summary>45 not yet measured</summary>

```
silk_touch_tnt            shatterproof_tnt          extinction                freeze_tnt                angry_miners              igniter_tnt
phantom_tnt               sphere_tnt                cubic_tnt                 snow_tnt                  arrow_tnt                 chicxulub
deimos                    ice_meteor_tnt            meteor_tnt                phobos                    present_drop              vredefort
cluster_bomb_tnt          night_tnt                 mansion                   ender_tnt                 butter_tnt                day_tnt
roulette_tnt              bomb_rain_tnt             mankinds_mark             village_defense           cluster_bomb              custom_firework
item_firework             lucky_doomsday            lucky_god                 lucky_tnt                 ore_tnt                   new_years_firework
entity_firework           rainbow_firework          gravel_firework           sand_firework             city_firework             tnt_firework
troll_tnt_mk3             troll_tnt                 troll_tnt_mk2
```

</details>

---

## Errors, crashes and log anomalies

### `heat_wave`

```
HARD CRASH: server died with net.minecraft.ReportedException: Ticking entity -> java.lang.IllegalAccessError on FireBlock.canBurn. Reproduced 3/3 times; the primed entity persisted in the chunk and re-crashed the server on every restart (crash loop) until the overworld entity storage was deleted by hand.
FATAL: IllegalAccessError: FireBlock.canBurn - server crash loop (see crash section)
```

