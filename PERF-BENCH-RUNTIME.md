# Lucky TNT Mod — Empirical Runtime Performance Benchmark (Fabric / MC 26.2)

*Live document — regenerated after every batch. Last update: **2026-07-30 03:28 UTC**.*

**Coverage so far: 22 of 203 TNT variants measured on a real dedicated server.**

## Headline

- **12 of 22** measured TNTs made the server fall behind 20 TPS (a tick budget is 50 ms).
- Worst single tick observed: **`hydrogen_bomb` at 12094 ms** — 242x the 50 ms tick budget.
- Most entities spawned: **`grande_finale` — 3742 entities** within 400 blocks.
- Idle baseline across all runs: **1.48 ms/tick** median (superflat, 225 forceloaded chunks, no players).

| rank | TNT | peak ms/tick | x tick budget |
|---|---|---:|---:|
| 1 | `hydrogen_bomb` | **12094** | 242x |
| 2 | `tsar_bomba` | **6973** | 139x |
| 3 | `nether_tnt` | **3152** | 63x |
| 4 | `atlantis` | **1635** | 33x |
| 5 | `black_hole_tnt` | **1434** | 29x |
| 6 | `wither_storm` | **485** | 10x |
| 7 | `animal_kingdom` | **313** | 6x |
| 8 | `firestorm_tnt` | **282** | 6x |
| 9 | `plantation_tnt` | **267** | 5x |
| 10 | `reset_tnt` | **202** | 4x |

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

| # | TNT | effect class | fuse | baseline avg ms/t | peak avg ms/t | peak p99 ms/t | mean avg ms/t | worst wall ms/t | ticks obs | peak entities | behind? | notes |
|---|-----|--------------|------|------------------:|--------------:|--------------:|--------------:|----------------:|----------:|--------------:|:-------:|-------|
| 1 | `hydrogen_bomb` | `DropProjectileTNTEffect` | 80 | 1.27 | 123.0 | 12093.9 | 31.66 | 6079 | 400 | 4 | **YES** | 1 'Can't keep up' events |
| 2 | `tsar_bomba` | `DropProjectileTNTEffect` | 80 | 2.06 | 73.2 | 6972.6 | 18.92 | 3521 | 397 | 4 | **YES** | 1 'Can't keep up' events |
| 3 | `nether_tnt` | `NetherTNTEffect` | 180 | 1.09 | 36.2 | 3152.5 | 8.14 | 827 | 496 | 4 | **YES** | 1 'Can't keep up' events |
| 4 | `atlantis` | `AtlantisEffect` | 240 | 1.48 | 36.8 | 1634.9 | 8.46 | 588 | 706 | 43 | **YES** |  |
| 5 | `black_hole_tnt` | `BlackHoleTNTEffect` | 500 | 2.26 | 37.8 | 1433.9 | 11.29 | 754 | 816 | 2001 | **YES** |  |
| 6 | `wither_storm` | `WitherStormEffect` | 160 | 1.11 | 11.9 | 485.3 | 3.93 | 172 | 2998 | 180 | **YES** |  |
| 7 | `animal_kingdom` | `AnimalKingdomEffect` | 80 | 2.32 | 26.2 | 312.7 | 9.78 | 173 | 2997 | 321 | **YES** |  |
| 8 | `firestorm_tnt` | `FirestormTNTEffect` | 160 | 1.06 | 4.3 | 281.9 | 1.79 | 113 | 477 | 4 | **YES** |  |
| 9 | `plantation_tnt` | `PlantationTNTEffect` | 160 | 3.19 | 3.7 | 266.9 | 1.66 | 114 | 476 | 4 | **YES** |  |
| 10 | `reset_tnt` | `?` | 80 | 1.49 | 3.1 | 202.3 | 1.61 | 250 | 396 | 4 | **YES** |  |
| 11 | `hells_gate` | `HellsGateEffect` | 140 | 1.48 | 2.6 | 147.9 | 1.32 | 89 | 456 | 4 | no |  |
| 12 | `world_of_wools` | `WorldOfWoolsEffect` | 150 | 1.98 | 3.8 | 138.2 | 1.92 | 106 | 469 | 83 | **YES** |  |
| 13 | `grande_finale` | `GrandeFinaleEffect` | 440 | 1.07 | 40.7 | 104.5 | 15.20 | 142 | 2998 | 3742 | **YES** |  |
| 14 | `end_tnt` | `EndTNTEffect` | 160 | 1.02 | 2.2 | 76.3 | 1.33 | 70 | 477 | 27 | no |  |
| 15 | `gotthard_tunnel` | `GotthardTunnelEffect` | 200 | 1.12 | 1.7 | 73.6 | 0.97 | 57 | 516 | 4 | no |  |
| 16 | `continental_drift` | `ContinentalDriftEffect` | 480 | 1.67 | 1.4 | 38.6 | 1.02 | 57 | 799 | 4 | no |  |
| 17 | `leaping_tnt` | `LeapingTNTEffect` | 100000 | 1.56 | 1.3 | 25.3 | 1.06 | 52 | 1218 | 4 | no | trigger-fused (no timer detonation) |
| 18 | `structure_tnt` | `StructureTNTEffect` | 160 | 1.29 | 1.0 | 17.4 | 0.89 | 51 | 476 | 4 | no |  |
| 19 | `meteor_shower` | `MeteorShowerEffect` | 720 | 1.69 | 2.5 | 10.5 | 1.87 | 58 | 1037 | 114 | no |  |
| 20 | `replay_tnt` | `?` | 80 | 1.32 | 1.5 | 9.7 | 1.32 | 52 | 399 | 4 | no |  |
| 21 | `acidic_tnt` | `AcidicTNTEffect` | 80 | 0.91 | 2.8 | 6.9 | 1.44 | 51 | 398 | 73 | no |  |
| 22 | `toxic_cloud` | `ToxicCloudEffect` | 1200 | 0.97 | 1.3 | 5.8 | 1.07 | 52 | 1217 | 4 | no | trigger-fused (no timer detonation), summoned (no block) |

## Detail — worst offenders

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

Entity peak 4 at t=0.

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

Entity peak 4 at t=0.

Server fell behind:

```
[03:10:02] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 6926ms or 138 ticks behind
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

Entity peak 4 at t=0.

Server fell behind:

```
[03:24:45] [Server thread/WARN]: Can't keep up! Is the server overloaded? Running 3103ms or 62 ticks behind
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

Entity peak 43 at t=241.

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

Entity peak 2001 at t=359.

### `wither_storm`  (`WitherStormEffect`)

- site `[9000, -60, 0]`, armed via **block+redstone**, declared fuse **160** ticks
- baseline **1.11 ms/t** (p99 17.10) -> peak avg **11.9 ms/t**, peak p99 **485.3 ms/t**
- observed window: 2998 game ticks over 149.9 s wall; worst wall-clock **172 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 180

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▄▅▆▆▆▇▇▇▇▇▇▇▇██████████▅▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁███████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.90  p99=   13.40  ents=4
t=    49  avg=   0.90  p99=    1.40  ents=4
t=    97  avg=   1.10  p99=    1.50  ents=4
t=   145  avg=   1.10  p99=    1.60  ents=4
t=   203  avg=   9.80  p99=  485.30  ents=165
t=   252  avg=  11.70  p99=  485.30  ents=165
t=   301  avg=   4.70  p99=    8.10  ents=163
t=   349  avg=   4.30  p99=    6.30  ents=163
t=   398  avg=   4.30  p99=    6.10  ents=163
t=   446  avg=   4.20  p99=    9.90  ents=163
t=   494  avg=   4.20  p99=    9.90  ents=164
t=   543  avg=   4.20  p99=    8.00  ents=163
t=   591  avg=   3.80  p99=    8.00  ents=163
t=   640  avg=   3.60  p99=    5.60  ents=163
t=   688  avg=   3.70  p99=    5.60  ents=163
t=   737  avg=   3.70  p99=    5.40  ents=163
t=   785  avg=   3.80  p99=    5.70  ents=164
t=   833  avg=   4.10  p99=    6.10  ents=164
t=   882  avg=   4.10  p99=    6.10  ents=164
t=   930  avg=   4.00  p99=    6.00  ents=164
t=   979  avg=   4.10  p99=    5.50  ents=165
t=  1027  avg=   4.20  p99=   29.50  ents=166
t=  1076  avg=   4.00  p99=   29.50  ents=165
t=  1124  avg=   3.60  p99=    5.40  ents=165
t=  1172  avg=   3.50  p99=    5.40  ents=165
t=  1221  avg=   3.60  p99=    5.40  ents=166
t=  1269  avg=   4.00  p99=    5.80  ents=167
t=  1318  avg=   4.30  p99=    9.80  ents=168
t=  1366  avg=   4.10  p99=    9.80  ents=168
t=  1415  avg=   4.00  p99=    6.50  ents=168
t=  1463  avg=   4.40  p99=    7.20  ents=169
t=  1512  avg=   4.20  p99=    8.90  ents=170
t=  1560  avg=   3.80  p99=    8.90  ents=170
t=  1609  avg=   3.70  p99=    8.90  ents=170
t=  1657  avg=   3.60  p99=    5.30  ents=170
t=  1706  avg=   3.70  p99=    5.80  ents=172
t=  1754  avg=   3.60  p99=    5.80  ents=172
t=  1803  avg=   3.50  p99=    5.80  ents=172
t=  1852  avg=   3.70  p99=    5.20  ents=172
t=  1900  avg=   3.90  p99=    5.40  ents=172
t=  1948  avg=   3.90  p99=    6.00  ents=173
t=  1997  avg=   3.80  p99=    6.60  ents=173
t=  2045  avg=   3.70  p99=    6.60  ents=174
t=  2093  avg=   3.70  p99=    6.60  ents=174
t=  2142  avg=   3.60  p99=    5.60  ents=174
t=  2191  avg=   3.60  p99=    4.90  ents=174
t=  2239  avg=   3.70  p99=    5.70  ents=174
t=  2287  avg=   3.80  p99=    5.80  ents=175
t=  2336  avg=   3.70  p99=    5.80  ents=175
t=  2384  avg=   3.60  p99=    5.30  ents=175
t=  2433  avg=   3.60  p99=    5.50  ents=175
t=  2481  avg=   3.70  p99=    5.50  ents=175
t=  2529  avg=   3.60  p99=    5.30  ents=176
t=  2578  avg=   3.60  p99=    5.30  ents=176
t=  2626  avg=   3.70  p99=   20.20  ents=176
t=  2674  avg=   3.80  p99=   20.20  ents=177
t=  2723  avg=   3.90  p99=   20.20  ents=177
t=  2771  avg=   3.80  p99=    5.30  ents=178
t=  2820  avg=   3.70  p99=    5.30  ents=178
t=  2868  avg=   3.70  p99=    5.50  ents=178
t=  2916  avg=   4.00  p99=    5.90  ents=178
t=  2966  avg=   4.60  p99=    6.70  ents=179
```

</details>

Entity peak 180 at t=2998.

### `animal_kingdom`  (`AnimalKingdomEffect`)

- site `[27000, -60, 0]`, armed via **block+redstone**, declared fuse **80** ticks
- baseline **2.32 ms/t** (p99 54.00) -> peak avg **26.2 ms/t**, peak p99 **312.7 ms/t**
- observed window: 2997 game ticks over 149.8 s wall; worst wall-clock **173 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 321

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▃▃▄▄▄▅▅▅▆▆▆▆▆▇▇▇▇█████▇▇▇▇▇▇▆▆▆▆▆▆▆▆▆▆▆▆▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▅▅▅▅▅▅▅▅▅▄▄▄▅▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▃▃▃▃▄▄▄▄▄▄▄▄▄▄▄▄▃▃▃▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▃▃▃▃▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁██████████████████████▄▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.20  p99=    6.40  ents=4
t=    49  avg=   1.10  p99=    3.10  ents=4
t=   108  avg=  12.50  p99=  312.70  ents=303
t=   156  avg=  22.10  p99=  312.70  ents=307
t=   205  avg=  20.10  p99=   34.10  ents=321
t=   253  avg=  17.00  p99=   34.00  ents=303
t=   302  avg=  15.00  p99=   36.10  ents=302
t=   354  avg=  15.30  p99=   73.50  ents=298
t=   403  avg=  14.40  p99=   73.50  ents=295
t=   451  avg=  12.60  p99=   32.50  ents=293
t=   499  avg=  12.20  p99=   27.40  ents=293
t=   549  avg=  13.40  p99=   40.60  ents=293
t=   597  avg=  13.70  p99=   40.60  ents=289
t=   646  avg=  12.40  p99=   42.60  ents=289
t=   694  avg=  11.10  p99=   42.60  ents=291
t=   742  avg=  10.90  p99=   38.80  ents=286
t=   791  avg=  10.40  p99=   33.00  ents=285
t=   839  avg=  10.00  p99=   36.10  ents=282
t=   888  avg=   9.90  p99=   36.10  ents=281
t=   936  avg=  11.10  p99=   46.10  ents=283
t=   985  avg=  11.10  p99=   46.10  ents=285
t=  1033  avg=   9.90  p99=   31.40  ents=283
t=  1082  avg=  10.80  p99=   32.30  ents=282
t=  1131  avg=  11.10  p99=   42.20  ents=281
t=  1179  avg=  10.30  p99=   42.20  ents=280
t=  1228  avg=   9.40  p99=   27.10  ents=282
t=  1276  avg=   8.80  p99=   23.20  ents=282
t=  1325  avg=   9.00  p99=   23.20  ents=283
t=  1373  avg=   8.80  p99=   34.00  ents=283
t=  1422  avg=   8.70  p99=   34.00  ents=281
t=  1470  avg=   8.60  p99=   26.00  ents=280
t=  1518  avg=   8.90  p99=   28.90  ents=279
t=  1567  avg=   9.00  p99=   28.90  ents=278
t=  1615  avg=   9.80  p99=   45.30  ents=279
t=  1664  avg=   9.40  p99=   45.30  ents=279
t=  1712  avg=   8.10  p99=   27.10  ents=279
t=  1761  avg=   7.80  p99=   27.10  ents=278
t=  1809  avg=   8.00  p99=   33.90  ents=277
t=  1857  avg=   7.80  p99=   33.90  ents=277
t=  1906  avg=   7.70  p99=   34.60  ents=277
t=  1954  avg=   7.80  p99=   34.60  ents=277
t=  2002  avg=   7.40  p99=   22.40  ents=278
t=  2051  avg=   7.60  p99=   24.30  ents=281
t=  2100  avg=   7.40  p99=   24.30  ents=281
t=  2148  avg=   7.50  p99=   21.10  ents=279
t=  2196  avg=   8.10  p99=   24.30  ents=279
t=  2245  avg=   7.90  p99=   24.30  ents=279
t=  2293  avg=   7.60  p99=   22.80  ents=279
t=  2342  avg=   8.70  p99=   40.90  ents=279
t=  2390  avg=   9.00  p99=   40.90  ents=279
t=  2439  avg=   8.20  p99=   38.50  ents=278
t=  2488  avg=   7.80  p99=   38.50  ents=278
t=  2536  avg=   7.60  p99=   25.90  ents=278
t=  2585  avg=   7.80  p99=   31.60  ents=278
t=  2633  avg=   8.20  p99=   36.60  ents=278
t=  2682  avg=   7.80  p99=   36.60  ents=278
t=  2731  avg=   7.70  p99=   22.90  ents=278
t=  2779  avg=   7.30  p99=   21.50  ents=278
t=  2828  avg=   7.80  p99=   31.20  ents=278
t=  2876  avg=   8.60  p99=   37.60  ents=278
t=  2924  avg=   8.40  p99=   37.60  ents=278
t=  2973  avg=   8.40  p99=   34.80  ents=278
```

</details>

Entity peak 321 at t=205.

### `firestorm_tnt`  (`FirestormTNTEffect`)

- site `[19000, -60, 0]`, armed via **block+redstone**, declared fuse **160** ticks
- baseline **1.06 ms/t** (p99 12.50) -> peak avg **4.3 ms/t**, peak p99 **281.9 ms/t**
- observed window: 477 game ticks over 23.8 s wall; worst wall-clock **113 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▇███████████████████████▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.90  p99=    2.00  ents=4
t=     4  avg=   0.80  p99=    2.00  ents=
t=     8  avg=   0.80  p99=    2.00  ents=
t=    12  avg=   0.80  p99=    1.70  ents=
t=    16  avg=   0.80  p99=    1.40  ents=4
t=    20  avg=   0.80  p99=    1.40  ents=
t=    24  avg=   0.80  p99=    1.40  ents=
t=    28  avg=   0.80  p99=    1.40  ents=
t=    32  avg=   0.90  p99=    1.40  ents=4
t=    36  avg=   0.90  p99=    1.40  ents=
t=    40  avg=   0.90  p99=    1.40  ents=
t=    44  avg=   0.90  p99=    1.40  ents=
t=    48  avg=   0.90  p99=    1.40  ents=4
t=    52  avg=   0.90  p99=    1.40  ents=
t=    56  avg=   0.90  p99=    1.40  ents=
t=    60  avg=   0.90  p99=    1.40  ents=
t=    64  avg=   0.90  p99=    1.30  ents=4
t=    68  avg=   0.90  p99=    1.30  ents=
t=    72  avg=   0.90  p99=    1.30  ents=
t=    76  avg=   0.90  p99=    1.30  ents=
t=    80  avg=   0.90  p99=    1.30  ents=4
t=    85  avg=   1.00  p99=    1.40  ents=
t=    89  avg=   1.00  p99=    1.40  ents=
t=    93  avg=   1.00  p99=    1.40  ents=
t=    97  avg=   1.00  p99=    1.40  ents=4
t=   101  avg=   1.00  p99=    1.40  ents=
t=   105  avg=   1.00  p99=    1.40  ents=
t=   109  avg=   1.00  p99=    1.40  ents=
t=   113  avg=   1.00  p99=    4.40  ents=4
t=   117  avg=   1.00  p99=    4.40  ents=
t=   121  avg=   1.00  p99=    4.40  ents=
t=   125  avg=   1.00  p99=    4.40  ents=
t=   129  avg=   1.00  p99=    4.40  ents=4
t=   133  avg=   1.00  p99=    4.40  ents=
t=   137  avg=   1.00  p99=    4.40  ents=
t=   141  avg=   1.00  p99=    4.40  ents=
t=   145  avg=   1.00  p99=    4.40  ents=4
t=   149  avg=   1.00  p99=    4.40  ents=
t=   153  avg=   1.00  p99=    4.40  ents=
t=   157  avg=   1.00  p99=    4.40  ents=
t=   161  avg=   3.80  p99=  281.90  ents=3
t=   170  avg=   3.90  p99=  281.90  ents=
t=   174  avg=   3.90  p99=  281.90  ents=
t=   178  avg=   3.90  p99=  281.90  ents=
t=   182  avg=   4.00  p99=  281.90  ents=3
t=   186  avg=   4.00  p99=  281.90  ents=
t=   191  avg=   4.00  p99=  281.90  ents=
t=   195  avg=   4.00  p99=  281.90  ents=
t=   199  avg=   4.10  p99=  281.90  ents=3
t=   203  avg=   4.10  p99=  281.90  ents=
t=   207  avg=   4.10  p99=  281.90  ents=
t=   211  avg=   4.10  p99=  281.90  ents=
t=   215  avg=   4.10  p99=  281.90  ents=3
t=   219  avg=   4.10  p99=  281.90  ents=
t=   223  avg=   4.10  p99=  281.90  ents=
t=   227  avg=   4.10  p99=  281.90  ents=
t=   231  avg=   4.20  p99=  281.90  ents=3
t=   235  avg=   4.20  p99=  281.90  ents=
t=   239  avg=   4.20  p99=  281.90  ents=
t=   243  avg=   4.20  p99=  281.90  ents=
t=   247  avg=   4.20  p99=  281.90  ents=3
t=   251  avg=   4.20  p99=  281.90  ents=
t=   255  avg=   4.20  p99=  281.90  ents=
t=   259  avg=   4.30  p99=  281.90  ents=
t=   263  avg=   1.50  p99=    9.00  ents=3
t=   267  avg=   1.50  p99=    9.00  ents=
t=   271  avg=   1.50  p99=    9.00  ents=
t=   275  avg=   1.50  p99=    9.00  ents=
t=   279  avg=   1.40  p99=    5.60  ents=3
t=   283  avg=   1.40  p99=    5.60  ents=
t=   287  avg=   1.40  p99=    5.60  ents=
t=   291  avg=   1.40  p99=    5.60  ents=
t=   295  avg=   1.40  p99=    5.60  ents=3
t=   299  avg=   1.40  p99=    5.60  ents=
t=   303  avg=   1.40  p99=    5.60  ents=
t=   307  avg=   1.40  p99=    5.60  ents=
t=   311  avg=   1.40  p99=    5.60  ents=3
t=   315  avg=   1.40  p99=    5.60  ents=
t=   319  avg=   1.40  p99=    5.60  ents=
t=   323  avg=   1.40  p99=    5.60  ents=
t=   327  avg=   1.40  p99=    5.60  ents=3
t=   331  avg=   1.40  p99=    2.40  ents=
t=   335  avg=   1.40  p99=    2.40  ents=
t=   339  avg=   1.40  p99=    2.40  ents=
t=   343  avg=   1.40  p99=    2.40  ents=3
t=   347  avg=   1.40  p99=    2.40  ents=
t=   352  avg=   1.40  p99=    2.40  ents=
t=   356  avg=   1.40  p99=    2.40  ents=
t=   360  avg=   1.40  p99=    2.40  ents=1
t=   364  avg=   1.40  p99=    2.40  ents=
t=   368  avg=   1.40  p99=    4.50  ents=
t=   372  avg=   1.40  p99=    4.50  ents=
t=   376  avg=   1.40  p99=    4.50  ents=1
t=   380  avg=   1.40  p99=    4.50  ents=
t=   384  avg=   1.40  p99=    4.50  ents=
t=   388  avg=   1.40  p99=    4.50  ents=
t=   392  avg=   1.40  p99=    4.50  ents=1
t=   396  avg=   1.40  p99=    4.50  ents=
t=   400  avg=   1.40  p99=    4.50  ents=
t=   404  avg=   1.40  p99=    4.50  ents=
t=   408  avg=   1.30  p99=    4.50  ents=1
t=   412  avg=   1.40  p99=    4.50  ents=
t=   416  avg=   1.40  p99=    4.50  ents=
t=   420  avg=   1.40  p99=    4.50  ents=
t=   424  avg=   1.40  p99=    4.50  ents=1
t=   428  avg=   1.30  p99=    4.50  ents=
t=   432  avg=   1.30  p99=    4.50  ents=
t=   436  avg=   1.30  p99=    4.50  ents=
t=   440  avg=   1.40  p99=    4.50  ents=1
t=   444  avg=   1.40  p99=    4.50  ents=
t=   448  avg=   1.40  p99=    4.50  ents=
t=   452  avg=   1.40  p99=    4.50  ents=
t=   456  avg=   1.40  p99=    4.50  ents=1
t=   460  avg=   1.40  p99=    4.50  ents=
t=   464  avg=   1.40  p99=    4.50  ents=
t=   468  avg=   1.30  p99=    2.90  ents=
t=   472  avg=   1.40  p99=    2.90  ents=1
t=   477  avg=   1.40  p99=    2.90  ents=
```

</details>

Entity peak 4 at t=0.

### `plantation_tnt`  (`PlantationTNTEffect`)

- site `[33000, -60, 0]`, armed via **block+redstone**, declared fuse **160** ticks
- baseline **3.19 ms/t** (p99 237.70) -> peak avg **3.7 ms/t**, peak p99 **266.9 ms/t**
- observed window: 476 game ticks over 23.8 s wall; worst wall-clock **114 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▇▇▇▇▇▇▇▇▇▇▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ██████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   3.10  p99=  237.70  ents=4
t=     4  avg=   3.10  p99=  237.70  ents=
t=     8  avg=   3.10  p99=  237.70  ents=
t=    12  avg=   3.00  p99=  237.70  ents=
t=    16  avg=   3.00  p99=  237.70  ents=4
t=    20  avg=   3.10  p99=  237.70  ents=
t=    24  avg=   3.10  p99=  237.70  ents=
t=    28  avg=   3.10  p99=  237.70  ents=
t=    32  avg=   3.10  p99=  237.70  ents=4
t=    36  avg=   3.10  p99=  237.70  ents=
t=    40  avg=   0.70  p99=    1.40  ents=
t=    44  avg=   0.80  p99=    1.40  ents=
t=    48  avg=   0.80  p99=    1.40  ents=4
t=    52  avg=   0.80  p99=    1.40  ents=
t=    56  avg=   0.80  p99=    1.40  ents=
t=    60  avg=   0.80  p99=    1.40  ents=
t=    64  avg=   0.80  p99=    1.40  ents=4
t=    68  avg=   0.80  p99=    1.40  ents=
t=    72  avg=   0.80  p99=    1.40  ents=
t=    76  avg=   0.80  p99=    1.40  ents=
t=    80  avg=   0.80  p99=    1.40  ents=4
t=    84  avg=   0.80  p99=    1.40  ents=
t=    88  avg=   0.90  p99=    1.40  ents=
t=    92  avg=   0.90  p99=    1.40  ents=
t=    96  avg=   0.90  p99=    1.40  ents=4
t=   100  avg=   0.90  p99=    1.40  ents=
t=   104  avg=   0.90  p99=    1.20  ents=
t=   109  avg=   0.80  p99=    1.20  ents=
t=   113  avg=   0.90  p99=    1.20  ents=4
t=   117  avg=   0.80  p99=    1.20  ents=
t=   121  avg=   0.80  p99=    1.20  ents=
t=   125  avg=   0.80  p99=    1.20  ents=
t=   129  avg=   0.80  p99=    1.20  ents=4
t=   133  avg=   0.80  p99=    1.20  ents=
t=   137  avg=   0.80  p99=    1.20  ents=
t=   141  avg=   0.80  p99=    1.20  ents=
t=   145  avg=   0.80  p99=    1.20  ents=4
t=   149  avg=   0.80  p99=    1.20  ents=
t=   153  avg=   0.80  p99=    1.20  ents=
t=   157  avg=   0.90  p99=    1.20  ents=
t=   161  avg=   3.50  p99=  266.90  ents=3
t=   170  avg=   3.50  p99=  266.90  ents=
t=   174  avg=   3.50  p99=  266.90  ents=
t=   178  avg=   3.50  p99=  266.90  ents=
t=   182  avg=   3.50  p99=  266.90  ents=3
t=   186  avg=   3.50  p99=  266.90  ents=
t=   190  avg=   3.50  p99=  266.90  ents=
t=   194  avg=   3.50  p99=  266.90  ents=
t=   198  avg=   3.60  p99=  266.90  ents=3
t=   202  avg=   3.60  p99=  266.90  ents=
t=   206  avg=   3.60  p99=  266.90  ents=
t=   210  avg=   3.60  p99=  266.90  ents=
t=   214  avg=   3.60  p99=  266.90  ents=3
t=   218  avg=   3.60  p99=  266.90  ents=
t=   222  avg=   3.60  p99=  266.90  ents=
t=   226  avg=   3.60  p99=  266.90  ents=
t=   230  avg=   3.60  p99=  266.90  ents=3
t=   234  avg=   3.70  p99=  266.90  ents=
t=   238  avg=   3.70  p99=  266.90  ents=
t=   242  avg=   3.70  p99=  266.90  ents=
t=   246  avg=   3.70  p99=  266.90  ents=3
t=   250  avg=   3.70  p99=  266.90  ents=
t=   254  avg=   3.70  p99=  266.90  ents=
t=   258  avg=   3.70  p99=  266.90  ents=
t=   262  avg=   1.00  p99=    2.20  ents=3
t=   266  avg=   1.00  p99=    2.20  ents=
t=   270  avg=   1.00  p99=    2.20  ents=
t=   275  avg=   1.00  p99=    2.20  ents=
t=   279  avg=   1.00  p99=    2.20  ents=3
t=   283  avg=   1.00  p99=    2.20  ents=
t=   287  avg=   1.00  p99=    2.20  ents=
t=   291  avg=   1.00  p99=    2.20  ents=
t=   295  avg=   1.00  p99=    2.20  ents=3
t=   299  avg=   1.00  p99=    2.20  ents=
t=   303  avg=   1.00  p99=    2.20  ents=
t=   307  avg=   1.00  p99=    2.20  ents=
t=   311  avg=   1.00  p99=    1.70  ents=3
t=   315  avg=   1.00  p99=    1.30  ents=
t=   319  avg=   1.00  p99=    1.30  ents=
t=   323  avg=   1.00  p99=    1.30  ents=
t=   327  avg=   1.00  p99=    1.30  ents=3
t=   331  avg=   1.00  p99=    1.30  ents=
t=   335  avg=   1.00  p99=    1.30  ents=
t=   339  avg=   1.00  p99=    1.30  ents=
t=   343  avg=   1.00  p99=    1.30  ents=3
t=   347  avg=   1.00  p99=    1.30  ents=
t=   351  avg=   1.00  p99=    1.30  ents=
t=   355  avg=   1.00  p99=    1.30  ents=
t=   359  avg=   1.00  p99=    1.30  ents=3
t=   363  avg=   1.00  p99=    1.30  ents=
t=   367  avg=   1.00  p99=    1.30  ents=
t=   371  avg=   1.00  p99=    1.30  ents=
t=   375  avg=   1.00  p99=    1.30  ents=3
t=   379  avg=   1.00  p99=    1.30  ents=
t=   383  avg=   1.00  p99=    1.30  ents=
t=   387  avg=   1.00  p99=    1.30  ents=
t=   391  avg=   1.00  p99=    1.30  ents=3
t=   395  avg=   1.00  p99=    1.30  ents=
t=   399  avg=   1.00  p99=    1.30  ents=
t=   403  avg=   1.00  p99=    1.30  ents=
t=   407  avg=   1.00  p99=    1.30  ents=3
t=   411  avg=   1.00  p99=    1.30  ents=
t=   415  avg=   1.00  p99=    1.30  ents=
t=   419  avg=   1.00  p99=    1.30  ents=
t=   423  avg=   1.00  p99=    1.30  ents=3
t=   427  avg=   1.00  p99=    1.30  ents=
t=   431  avg=   1.00  p99=    1.30  ents=
t=   436  avg=   1.00  p99=    1.30  ents=
t=   440  avg=   1.00  p99=    1.30  ents=3
t=   444  avg=   1.00  p99=    1.30  ents=
t=   448  avg=   1.00  p99=    1.30  ents=
t=   452  avg=   1.00  p99=    1.30  ents=
t=   456  avg=   1.00  p99=    1.30  ents=3
t=   460  avg=   1.00  p99=    1.30  ents=
t=   464  avg=   1.00  p99=    1.30  ents=
t=   468  avg=   1.00  p99=    1.30  ents=
t=   472  avg=   1.00  p99=    1.30  ents=3
t=   476  avg=   1.00  p99=    1.30  ents=
```

</details>

Entity peak 4 at t=0.

### `reset_tnt`  (`?`)

- site `[23000, -60, 0]`, armed via **block+redstone**, declared fuse **80** ticks
- baseline **1.49 ms/t** (p99 26.20) -> peak avg **3.1 ms/t**, peak p99 **202.3 ms/t**
- observed window: 396 game ticks over 19.8 s wall; worst wall-clock **250 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     1  avg=   1.00  p99=    1.70  ents=4
t=     2  avg=   3.00  p99=  202.30  ents=
t=    10  avg=   3.00  p99=  202.30  ents=
t=    14  avg=   3.00  p99=  202.30  ents=
t=    18  avg=   3.00  p99=  202.30  ents=4
t=    22  avg=   3.00  p99=  202.30  ents=
t=    26  avg=   3.00  p99=  202.30  ents=
t=    30  avg=   3.00  p99=  202.30  ents=
t=    34  avg=   3.00  p99=  202.30  ents=4
t=    38  avg=   3.00  p99=  202.30  ents=
t=    42  avg=   3.00  p99=  202.30  ents=
t=    46  avg=   3.00  p99=  202.30  ents=
t=    50  avg=   3.00  p99=  202.30  ents=4
t=    54  avg=   3.00  p99=  202.30  ents=
t=    58  avg=   3.00  p99=  202.30  ents=
t=    62  avg=   3.10  p99=  202.30  ents=
t=    66  avg=   3.10  p99=  202.30  ents=4
t=    70  avg=   3.10  p99=  202.30  ents=
t=    74  avg=   3.10  p99=  202.30  ents=
t=    78  avg=   3.10  p99=  202.30  ents=
t=    82  avg=   3.10  p99=  202.30  ents=4
t=    86  avg=   3.10  p99=  202.30  ents=
t=    90  avg=   3.10  p99=  202.30  ents=
t=    94  avg=   3.10  p99=  202.30  ents=
t=    98  avg=   3.10  p99=  202.30  ents=4
t=   102  avg=   1.10  p99=    1.40  ents=
t=   106  avg=   1.10  p99=    1.40  ents=
t=   110  avg=   1.10  p99=    1.40  ents=
t=   114  avg=   1.10  p99=    1.40  ents=4
t=   118  avg=   1.10  p99=    1.50  ents=
t=   122  avg=   1.10  p99=    1.50  ents=
t=   126  avg=   1.10  p99=    1.50  ents=
t=   130  avg=   1.10  p99=    1.50  ents=4
t=   134  avg=   1.10  p99=    1.50  ents=
t=   138  avg=   1.10  p99=    1.50  ents=
t=   142  avg=   1.10  p99=    1.50  ents=
t=   146  avg=   1.10  p99=    1.50  ents=4
t=   150  avg=   1.10  p99=    1.50  ents=
t=   154  avg=   1.10  p99=    1.50  ents=
t=   158  avg=   1.10  p99=    1.50  ents=
t=   162  avg=   1.10  p99=    1.50  ents=4
t=   166  avg=   1.10  p99=    1.50  ents=
t=   170  avg=   1.10  p99=    1.50  ents=
t=   174  avg=   1.20  p99=    1.50  ents=
t=   179  avg=   1.20  p99=    1.50  ents=4
t=   183  avg=   1.20  p99=    1.50  ents=
t=   187  avg=   1.20  p99=    1.50  ents=
t=   191  avg=   1.20  p99=    1.50  ents=
t=   195  avg=   1.20  p99=    1.50  ents=4
t=   199  avg=   1.20  p99=    1.50  ents=
t=   203  avg=   1.20  p99=    1.50  ents=
t=   207  avg=   1.20  p99=    1.50  ents=
t=   211  avg=   1.20  p99=    1.50  ents=4
t=   215  avg=   1.20  p99=    1.50  ents=
t=   219  avg=   1.20  p99=    1.50  ents=
t=   223  avg=   1.20  p99=    1.50  ents=
t=   227  avg=   1.20  p99=    1.50  ents=4
t=   231  avg=   1.20  p99=    1.50  ents=
t=   235  avg=   1.20  p99=    1.50  ents=
t=   239  avg=   1.20  p99=    1.50  ents=
t=   243  avg=   1.20  p99=    1.50  ents=4
t=   247  avg=   1.20  p99=    1.50  ents=
t=   251  avg=   1.20  p99=    1.50  ents=
t=   255  avg=   1.20  p99=    1.50  ents=
t=   259  avg=   1.10  p99=    1.50  ents=4
t=   263  avg=   1.10  p99=    1.50  ents=
t=   267  avg=   1.20  p99=    1.50  ents=
t=   271  avg=   1.10  p99=    1.50  ents=
t=   275  avg=   1.10  p99=    1.50  ents=4
t=   279  avg=   1.10  p99=    1.90  ents=
t=   283  avg=   1.20  p99=    2.30  ents=
t=   287  avg=   1.10  p99=    2.30  ents=
t=   291  avg=   1.10  p99=    2.30  ents=4
t=   295  avg=   1.10  p99=    2.30  ents=
t=   299  avg=   1.10  p99=    2.30  ents=
t=   303  avg=   1.10  p99=    2.30  ents=
t=   307  avg=   1.10  p99=    2.30  ents=4
t=   311  avg=   1.10  p99=    2.30  ents=
t=   316  avg=   1.10  p99=    2.30  ents=
t=   320  avg=   1.10  p99=    2.30  ents=
t=   324  avg=   1.10  p99=    2.30  ents=4
t=   328  avg=   1.20  p99=    2.30  ents=
t=   332  avg=   1.20  p99=    2.30  ents=
t=   336  avg=   1.20  p99=    2.30  ents=
t=   340  avg=   1.20  p99=    2.30  ents=4
t=   344  avg=   1.20  p99=    2.30  ents=
t=   348  avg=   1.20  p99=    2.30  ents=
t=   352  avg=   1.20  p99=    2.30  ents=
t=   356  avg=   1.20  p99=    2.30  ents=4
t=   360  avg=   1.20  p99=    2.30  ents=
t=   364  avg=   1.20  p99=    2.30  ents=
t=   368  avg=   1.20  p99=    2.30  ents=
t=   372  avg=   1.20  p99=    2.30  ents=4
t=   376  avg=   1.20  p99=    2.30  ents=
t=   380  avg=   1.20  p99=    2.30  ents=
t=   384  avg=   1.20  p99=    2.30  ents=
t=   388  avg=   1.20  p99=    2.30  ents=4
t=   392  avg=   1.20  p99=    2.30  ents=
t=   396  avg=   1.20  p99=    2.30  ents=
```

</details>

Entity peak 4 at t=1.

### `hells_gate`  (`HellsGateEffect`)

- site `[11000, -60, 0]`, armed via **block+redstone**, declared fuse **140** ticks
- baseline **1.48 ms/t** (p99 47.90) -> peak avg **2.6 ms/t**, peak p99 **147.9 ms/t**
- observed window: 456 game ticks over 22.8 s wall; worst wall-clock **89 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████████▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.80  p99=    1.60  ents=4
t=     4  avg=   0.80  p99=    1.60  ents=
t=     8  avg=   0.80  p99=    1.60  ents=
t=    12  avg=   0.80  p99=    1.50  ents=
t=    16  avg=   0.80  p99=    1.50  ents=4
t=    20  avg=   0.80  p99=    1.50  ents=
t=    24  avg=   0.80  p99=    1.50  ents=
t=    28  avg=   0.80  p99=    1.50  ents=
t=    32  avg=   0.80  p99=    1.50  ents=4
t=    36  avg=   0.80  p99=    1.50  ents=
t=    40  avg=   0.80  p99=    1.50  ents=
t=    44  avg=   0.80  p99=    1.50  ents=
t=    48  avg=   0.80  p99=    1.50  ents=4
t=    52  avg=   0.80  p99=    1.50  ents=
t=    56  avg=   0.80  p99=    1.50  ents=
t=    60  avg=   0.90  p99=    1.50  ents=
t=    64  avg=   0.90  p99=    1.50  ents=4
t=    68  avg=   0.90  p99=    1.50  ents=
t=    72  avg=   0.90  p99=    1.50  ents=
t=    76  avg=   0.90  p99=    1.50  ents=
t=    81  avg=   0.90  p99=    1.50  ents=4
t=    85  avg=   0.90  p99=    1.50  ents=
t=    89  avg=   0.90  p99=    1.50  ents=
t=    93  avg=   0.90  p99=    1.50  ents=
t=    97  avg=   1.00  p99=    1.50  ents=4
t=   101  avg=   1.00  p99=    1.40  ents=
t=   105  avg=   1.00  p99=    1.40  ents=
t=   109  avg=   1.00  p99=    1.40  ents=
t=   114  avg=   1.00  p99=    1.40  ents=4
t=   118  avg=   1.00  p99=    1.40  ents=
t=   122  avg=   1.00  p99=    1.40  ents=
t=   126  avg=   1.00  p99=    1.40  ents=
t=   130  avg=   1.00  p99=    1.40  ents=4
t=   134  avg=   1.00  p99=    1.40  ents=
t=   138  avg=   1.00  p99=    1.40  ents=
t=   141  avg=   2.50  p99=  147.90  ents=
t=   148  avg=   2.50  p99=  147.90  ents=3
t=   152  avg=   2.50  p99=  147.90  ents=
t=   156  avg=   2.50  p99=  147.90  ents=
t=   160  avg=   2.50  p99=  147.90  ents=
t=   164  avg=   2.50  p99=  147.90  ents=3
t=   168  avg=   2.50  p99=  147.90  ents=
t=   172  avg=   2.50  p99=  147.90  ents=
t=   176  avg=   2.50  p99=  147.90  ents=
t=   180  avg=   2.60  p99=  147.90  ents=3
t=   185  avg=   2.60  p99=  147.90  ents=
t=   189  avg=   2.60  p99=  147.90  ents=
t=   193  avg=   2.60  p99=  147.90  ents=
t=   197  avg=   2.60  p99=  147.90  ents=3
t=   201  avg=   2.60  p99=  147.90  ents=
t=   206  avg=   2.60  p99=  147.90  ents=
t=   210  avg=   2.60  p99=  147.90  ents=
t=   214  avg=   2.60  p99=  147.90  ents=3
t=   218  avg=   2.60  p99=  147.90  ents=
t=   222  avg=   2.60  p99=  147.90  ents=
t=   226  avg=   2.60  p99=  147.90  ents=
t=   230  avg=   2.60  p99=  147.90  ents=3
t=   234  avg=   2.60  p99=  147.90  ents=
t=   238  avg=   2.60  p99=  147.90  ents=
t=   242  avg=   1.20  p99=    3.90  ents=
t=   246  avg=   1.10  p99=    3.90  ents=3
t=   250  avg=   1.10  p99=    3.90  ents=
t=   254  avg=   1.10  p99=    3.90  ents=
t=   258  avg=   1.10  p99=    3.90  ents=
t=   262  avg=   1.10  p99=    2.20  ents=3
t=   266  avg=   1.10  p99=    2.20  ents=
t=   270  avg=   1.10  p99=    2.20  ents=
t=   274  avg=   1.10  p99=    1.60  ents=
t=   278  avg=   1.10  p99=    1.60  ents=3
t=   282  avg=   1.10  p99=    1.60  ents=
t=   287  avg=   1.10  p99=    1.60  ents=
t=   291  avg=   1.10  p99=    1.60  ents=
t=   295  avg=   1.10  p99=    1.60  ents=3
t=   299  avg=   1.10  p99=    1.60  ents=
t=   303  avg=   1.10  p99=    1.60  ents=
t=   307  avg=   1.10  p99=    1.50  ents=
t=   311  avg=   1.10  p99=    1.40  ents=3
t=   315  avg=   1.10  p99=    1.50  ents=
t=   319  avg=   1.10  p99=    1.50  ents=
t=   323  avg=   1.10  p99=    1.50  ents=
t=   327  avg=   1.10  p99=    1.50  ents=3
t=   331  avg=   1.10  p99=    1.50  ents=
t=   335  avg=   1.10  p99=    1.50  ents=
t=   339  avg=   1.10  p99=    1.50  ents=
t=   343  avg=   1.10  p99=    1.50  ents=3
t=   347  avg=   1.10  p99=    1.70  ents=
t=   351  avg=   1.10  p99=    1.70  ents=
t=   355  avg=   1.00  p99=    1.70  ents=
t=   359  avg=   1.00  p99=    1.70  ents=3
t=   363  avg=   1.00  p99=    1.70  ents=
t=   367  avg=   1.00  p99=    1.70  ents=
t=   371  avg=   1.00  p99=    1.70  ents=
t=   375  avg=   1.00  p99=    1.70  ents=3
t=   379  avg=   1.00  p99=    1.70  ents=
t=   383  avg=   1.00  p99=    1.70  ents=
t=   388  avg=   1.00  p99=    1.70  ents=
t=   392  avg=   1.00  p99=    1.70  ents=3
t=   396  avg=   1.00  p99=    1.70  ents=
t=   400  avg=   1.00  p99=    1.70  ents=
t=   404  avg=   1.00  p99=    1.70  ents=
t=   408  avg=   1.00  p99=    1.70  ents=3
t=   412  avg=   1.00  p99=    1.70  ents=
t=   416  avg=   1.00  p99=    1.70  ents=
t=   420  avg=   1.00  p99=    1.70  ents=
t=   424  avg=   1.00  p99=    1.70  ents=3
t=   428  avg=   1.00  p99=    1.70  ents=
t=   432  avg=   1.00  p99=    1.70  ents=
t=   436  avg=   1.00  p99=    1.70  ents=
t=   440  avg=   1.00  p99=    1.70  ents=3
t=   444  avg=   1.00  p99=    1.40  ents=
t=   448  avg=   1.00  p99=    1.30  ents=
t=   452  avg=   1.00  p99=    1.30  ents=
t=   456  avg=   1.00  p99=    1.30  ents=3
```

</details>

Entity peak 4 at t=0.

### `world_of_wools`  (`WorldOfWoolsEffect`)

- site `[29000, -60, 0]`, armed via **block+redstone**, declared fuse **150** ticks
- baseline **1.98 ms/t** (p99 31.90) -> peak avg **3.8 ms/t**, peak p99 **138.2 ms/t**
- observed window: 469 game ticks over 23.5 s wall; worst wall-clock **106 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 83

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▄▅▅▅▅▅▅▆▆▆▆▆▆▇▇▇▇▇▇▇█████▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▅▄▄▄▄▄▄▄▄▄▄▅▅
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.80  p99=   13.00  ents=4
t=     4  avg=   0.70  p99=    1.30  ents=
t=     8  avg=   0.70  p99=    1.30  ents=
t=    12  avg=   0.70  p99=    1.30  ents=
t=    16  avg=   0.60  p99=    1.20  ents=4
t=    20  avg=   0.60  p99=    1.20  ents=
t=    24  avg=   0.60  p99=    1.20  ents=
t=    28  avg=   0.60  p99=    1.20  ents=
t=    32  avg=   0.70  p99=    1.20  ents=4
t=    36  avg=   0.70  p99=    1.20  ents=
t=    40  avg=   0.70  p99=    1.20  ents=
t=    44  avg=   0.70  p99=    1.20  ents=
t=    48  avg=   0.70  p99=    1.20  ents=4
t=    52  avg=   0.70  p99=    1.20  ents=
t=    56  avg=   0.70  p99=    1.20  ents=
t=    60  avg=   0.70  p99=    1.20  ents=
t=    64  avg=   0.70  p99=    1.20  ents=4
t=    69  avg=   0.70  p99=    1.20  ents=
t=    73  avg=   0.70  p99=    1.20  ents=
t=    77  avg=   0.70  p99=    1.20  ents=
t=    81  avg=   0.70  p99=    1.20  ents=4
t=    85  avg=   0.80  p99=    1.20  ents=
t=    89  avg=   0.80  p99=    1.20  ents=
t=    93  avg=   0.80  p99=    1.20  ents=
t=    97  avg=   0.80  p99=    1.20  ents=4
t=   101  avg=   0.80  p99=    1.20  ents=
t=   105  avg=   0.80  p99=    1.20  ents=
t=   109  avg=   0.80  p99=    1.20  ents=
t=   113  avg=   0.80  p99=    1.20  ents=4
t=   117  avg=   0.80  p99=    1.10  ents=
t=   121  avg=   0.80  p99=    1.20  ents=
t=   125  avg=   0.80  p99=    1.20  ents=
t=   129  avg=   0.80  p99=    1.20  ents=4
t=   133  avg=   0.80  p99=    1.20  ents=
t=   137  avg=   0.80  p99=    1.20  ents=
t=   141  avg=   0.80  p99=    1.20  ents=
t=   145  avg=   0.80  p99=    1.20  ents=4
t=   149  avg=   0.80  p99=    1.20  ents=
t=   151  avg=   2.20  p99=  138.20  ents=
t=   157  avg=   2.30  p99=  138.20  ents=
t=   162  avg=   2.40  p99=  138.20  ents=83
t=   166  avg=   2.40  p99=  138.20  ents=
t=   170  avg=   2.50  p99=  138.20  ents=
t=   174  avg=   2.50  p99=  138.20  ents=
t=   178  avg=   2.60  p99=  138.20  ents=83
t=   182  avg=   2.70  p99=  138.20  ents=
t=   186  avg=   2.70  p99=  138.20  ents=
t=   190  avg=   2.80  p99=  138.20  ents=
t=   194  avg=   2.90  p99=  138.20  ents=83
t=   198  avg=   2.90  p99=  138.20  ents=
t=   202  avg=   3.00  p99=  138.20  ents=
t=   206  avg=   3.10  p99=  138.20  ents=
t=   210  avg=   3.10  p99=  138.20  ents=83
t=   214  avg=   3.20  p99=  138.20  ents=
t=   218  avg=   3.20  p99=  138.20  ents=
t=   222  avg=   3.30  p99=  138.20  ents=
t=   226  avg=   3.40  p99=  138.20  ents=83
t=   230  avg=   3.40  p99=  138.20  ents=
t=   234  avg=   3.50  p99=  138.20  ents=
t=   238  avg=   3.60  p99=  138.20  ents=
t=   242  avg=   3.60  p99=  138.20  ents=83
t=   246  avg=   3.70  p99=  138.20  ents=
t=   250  avg=   3.80  p99=  138.20  ents=
t=   254  avg=   2.40  p99=    3.50  ents=
t=   258  avg=   2.40  p99=    3.50  ents=83
t=   262  avg=   2.40  p99=    3.50  ents=
t=   266  avg=   2.40  p99=    3.50  ents=
t=   271  avg=   2.40  p99=    3.50  ents=
t=   275  avg=   2.40  p99=    3.50  ents=83
t=   279  avg=   2.40  p99=    3.50  ents=
t=   283  avg=   2.40  p99=    3.50  ents=
t=   287  avg=   2.40  p99=    3.50  ents=
t=   291  avg=   2.40  p99=    3.50  ents=83
t=   295  avg=   2.40  p99=    3.50  ents=
t=   299  avg=   2.40  p99=    3.50  ents=
t=   303  avg=   2.30  p99=    3.50  ents=
t=   307  avg=   2.30  p99=    3.50  ents=83
t=   311  avg=   2.30  p99=    3.50  ents=
t=   315  avg=   2.30  p99=    3.50  ents=
t=   319  avg=   2.30  p99=    3.50  ents=
t=   323  avg=   2.30  p99=    3.50  ents=83
t=   327  avg=   2.30  p99=    3.50  ents=
t=   331  avg=   2.30  p99=    3.50  ents=
t=   335  avg=   2.30  p99=    3.50  ents=
t=   339  avg=   2.30  p99=    3.20  ents=83
t=   343  avg=   2.30  p99=    3.20  ents=
t=   347  avg=   2.20  p99=    3.20  ents=
t=   351  avg=   2.20  p99=    3.20  ents=
t=   355  avg=   2.20  p99=    3.20  ents=83
t=   359  avg=   2.20  p99=    3.20  ents=
t=   363  avg=   2.20  p99=    3.20  ents=
t=   367  avg=   2.20  p99=    3.20  ents=
t=   371  avg=   2.20  p99=    3.20  ents=83
t=   375  avg=   2.20  p99=    3.20  ents=
t=   379  avg=   2.20  p99=    3.20  ents=
t=   383  avg=   2.20  p99=    3.20  ents=
t=   388  avg=   2.20  p99=    3.20  ents=83
t=   392  avg=   2.20  p99=    3.20  ents=
t=   396  avg=   2.20  p99=    3.20  ents=
t=   400  avg=   2.20  p99=    3.20  ents=
t=   404  avg=   2.20  p99=    3.20  ents=83
t=   408  avg=   2.20  p99=    3.20  ents=
t=   412  avg=   2.20  p99=    3.20  ents=
t=   417  avg=   2.30  p99=    7.10  ents=
t=   425  avg=   2.20  p99=    7.10  ents=83
t=   429  avg=   2.20  p99=    7.10  ents=
t=   433  avg=   2.20  p99=    7.10  ents=
t=   437  avg=   2.20  p99=    7.10  ents=
t=   441  avg=   2.20  p99=    7.10  ents=83
t=   445  avg=   2.20  p99=    7.10  ents=
t=   449  avg=   2.20  p99=    7.10  ents=
t=   453  avg=   2.20  p99=    7.10  ents=
t=   457  avg=   2.20  p99=    7.10  ents=83
t=   461  avg=   2.20  p99=    7.10  ents=
t=   465  avg=   2.30  p99=    7.10  ents=
t=   469  avg=   2.30  p99=    7.10  ents=
```

</details>

Entity peak 83 at t=162.

### `grande_finale`  (`GrandeFinaleEffect`)

- site `[15000, -60, 0]`, armed via **block+redstone**, declared fuse **440** ticks
- baseline **1.07 ms/t** (p99 21.90) -> peak avg **40.7 ms/t**, peak p99 **104.5 ms/t**
- observed window: 2998 game ticks over 149.9 s wall; worst wall-clock **142 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 3742

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▄▄▄▄▄▄▄▄▄▄▄▄▄▄▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▇▇▇▇█████████████████████▇▇▇▇▆▆▆▆▆▆▆▆▆▆▆▆▆▅▅▅▅▅▅▅▄▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▄▄▄▄▄▄▄▄▄▄▄▄▄▅▅▅▅▅▅▅▅▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▄▄▄▄▄█████████████████████▇▇▇▇▇▇▇▆▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.90  p99=    9.20  ents=4
t=    49  avg=   0.90  p99=    2.30  ents=6
t=    97  avg=   1.20  p99=    9.20  ents=430
t=   146  avg=   5.00  p99=   17.30  ents=1086
t=   194  avg=  10.30  p99=   17.30  ents=1115
t=   243  avg=  14.50  p99=   24.50  ents=1720
t=   292  avg=  19.80  p99=   51.30  ents=1614
t=   341  avg=  23.70  p99=   59.10  ents=2273
t=   390  avg=  26.30  p99=   59.10  ents=2079
t=   441  avg=  27.70  p99=   41.60  ents=3742
t=   502  avg=  38.30  p99=  104.50  ents=3161
t=   552  avg=  37.50  p99=   83.40  ents=2969
t=   602  avg=  29.00  p99=   51.80  ents=2457
t=   651  avg=  22.80  p99=   46.50  ents=2227
t=   699  avg=  16.90  p99=   46.50  ents=2227
t=   748  avg=  13.70  p99=   19.30  ents=2227
t=   796  avg=  13.40  p99=   19.30  ents=2227
t=   845  avg=  13.10  p99=   18.60  ents=2227
t=   893  avg=  13.20  p99=   19.70  ents=2227
t=   942  avg=  12.80  p99=   19.70  ents=2227
t=   990  avg=  12.50  p99=   32.90  ents=2227
t=  1039  avg=  12.80  p99=   32.90  ents=2227
t=  1088  avg=  12.50  p99=   18.70  ents=2227
t=  1136  avg=  13.10  p99=   18.90  ents=2227
t=  1185  avg=  14.00  p99=   18.90  ents=2227
t=  1233  avg=  14.60  p99=   20.80  ents=2227
t=  1282  avg=  14.90  p99=   37.20  ents=2227
t=  1331  avg=  14.10  p99=   37.20  ents=2227
t=  1380  avg=  13.60  p99=   18.80  ents=2227
t=  1428  avg=  14.20  p99=   30.60  ents=2227
t=  1477  avg=  14.20  p99=   30.60  ents=2227
t=  1525  avg=  12.80  p99=   19.30  ents=2227
t=  1574  avg=  12.90  p99=   17.30  ents=2227
t=  1622  avg=  14.30  p99=   37.40  ents=2227
t=  1671  avg=  14.40  p99=   37.40  ents=2227
t=  1720  avg=  14.20  p99=   20.20  ents=2227
t=  1769  avg=  14.10  p99=   20.20  ents=2227
t=  1817  avg=  12.80  p99=   18.20  ents=2227
t=  1866  avg=  12.70  p99=   19.20  ents=2227
t=  1914  avg=  13.60  p99=   19.70  ents=2227
t=  1963  avg=  14.10  p99=   27.40  ents=2227
t=  2011  avg=  13.00  p99=   27.40  ents=2227
t=  2060  avg=  12.70  p99=   23.30  ents=2227
t=  2110  avg=  14.30  p99=   37.60  ents=2227
t=  2165  avg=  19.90  p99=   57.70  ents=2227
t=  2219  avg=  21.20  p99=   57.70  ents=2227
t=  2268  avg=  17.10  p99=   52.90  ents=2227
t=  2317  avg=  14.80  p99=   36.30  ents=2227
t=  2366  avg=  14.00  p99=   19.80  ents=2227
t=  2414  avg=  13.00  p99=   18.10  ents=2227
t=  2463  avg=  13.00  p99=   18.80  ents=2227
t=  2512  avg=  14.10  p99=   19.10  ents=2227
t=  2561  avg=  13.70  p99=   19.10  ents=2227
t=  2609  avg=  12.80  p99=   30.40  ents=2227
t=  2658  avg=  13.50  p99=   30.40  ents=2227
t=  2707  avg=  13.80  p99=   18.90  ents=2227
t=  2755  avg=  13.60  p99=   18.90  ents=2227
t=  2804  avg=  13.10  p99=   17.80  ents=2227
t=  2853  avg=  13.80  p99=   19.40  ents=2227
t=  2901  avg=  15.20  p99=   20.10  ents=2227
t=  2950  avg=  15.00  p99=   31.60  ents=2227
t=  2998  avg=  13.60  p99=   31.60  ents=2227
```

</details>

Entity peak 3742 at t=441.

### `end_tnt`  (`EndTNTEffect`)

- site `[39000, -60, 0]`, armed via **block+redstone**, declared fuse **160** ticks
- baseline **1.02 ms/t** (p99 19.60) -> peak avg **2.2 ms/t**, peak p99 **76.3 ms/t**
- observed window: 477 game ticks over 23.9 s wall; worst wall-clock **70 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 27

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▆▆▆▆▆▇▇▇▇▇▇▇▇███████████▅▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.80  p99=    1.40  ents=4
t=     4  avg=   0.80  p99=    1.40  ents=
t=     8  avg=   0.70  p99=    1.40  ents=
t=    12  avg=   0.70  p99=    1.20  ents=
t=    16  avg=   0.70  p99=    1.20  ents=4
t=    20  avg=   0.70  p99=    1.20  ents=
t=    24  avg=   0.80  p99=    1.20  ents=
t=    28  avg=   0.80  p99=    1.20  ents=
t=    32  avg=   0.80  p99=    1.20  ents=4
t=    36  avg=   0.80  p99=    1.20  ents=
t=    40  avg=   0.80  p99=    1.20  ents=
t=    44  avg=   0.80  p99=    1.20  ents=
t=    48  avg=   0.80  p99=    1.20  ents=4
t=    52  avg=   0.80  p99=    1.40  ents=
t=    56  avg=   0.80  p99=    1.40  ents=
t=    60  avg=   0.80  p99=    1.40  ents=
t=    64  avg=   0.80  p99=    1.40  ents=4
t=    68  avg=   0.90  p99=    1.40  ents=
t=    72  avg=   0.90  p99=    1.40  ents=
t=    76  avg=   0.90  p99=    1.40  ents=
t=    80  avg=   0.90  p99=    1.40  ents=4
t=    84  avg=   0.90  p99=    1.40  ents=
t=    88  avg=   0.90  p99=    1.40  ents=
t=    92  avg=   0.90  p99=    1.40  ents=
t=    96  avg=   0.90  p99=    1.40  ents=4
t=   100  avg=   0.90  p99=    1.40  ents=
t=   104  avg=   0.90  p99=    1.40  ents=
t=   108  avg=   0.90  p99=    1.40  ents=
t=   112  avg=   0.90  p99=    1.40  ents=4
t=   116  avg=   0.90  p99=    1.40  ents=
t=   120  avg=   0.90  p99=    1.40  ents=
t=   124  avg=   0.90  p99=    1.40  ents=
t=   128  avg=   0.90  p99=    1.40  ents=4
t=   132  avg=   0.90  p99=    1.40  ents=
t=   136  avg=   0.90  p99=    1.40  ents=
t=   140  avg=   0.90  p99=    1.40  ents=
t=   144  avg=   0.90  p99=    1.40  ents=4
t=   148  avg=   0.90  p99=    1.40  ents=
t=   152  avg=   0.90  p99=    1.10  ents=
t=   156  avg=   0.90  p99=    1.10  ents=
t=   160  avg=   0.90  p99=    1.10  ents=27
t=   166  avg=   1.70  p99=   76.30  ents=
t=   170  avg=   1.80  p99=   76.30  ents=
t=   174  avg=   1.80  p99=   76.30  ents=
t=   178  avg=   1.80  p99=   76.30  ents=27
t=   182  avg=   1.80  p99=   76.30  ents=
t=   186  avg=   1.90  p99=   76.30  ents=
t=   190  avg=   1.90  p99=   76.30  ents=
t=   194  avg=   1.90  p99=   76.30  ents=27
t=   198  avg=   1.90  p99=   76.30  ents=
t=   202  avg=   2.00  p99=   76.30  ents=
t=   206  avg=   2.00  p99=   76.30  ents=
t=   210  avg=   2.00  p99=   76.30  ents=27
t=   214  avg=   2.00  p99=   76.30  ents=
t=   218  avg=   2.10  p99=   76.30  ents=
t=   222  avg=   2.10  p99=   76.30  ents=
t=   226  avg=   2.10  p99=   76.30  ents=27
t=   230  avg=   2.10  p99=   76.30  ents=
t=   234  avg=   2.10  p99=   76.30  ents=
t=   238  avg=   2.20  p99=   76.30  ents=
t=   242  avg=   2.20  p99=   76.30  ents=27
t=   247  avg=   2.20  p99=   76.30  ents=
t=   251  avg=   2.20  p99=   76.30  ents=
t=   255  avg=   2.20  p99=   76.30  ents=
t=   259  avg=   2.20  p99=   76.30  ents=27
t=   263  avg=   1.50  p99=    2.40  ents=
t=   267  avg=   1.50  p99=    2.40  ents=
t=   271  avg=   1.40  p99=    2.40  ents=
t=   275  avg=   1.40  p99=    2.40  ents=27
t=   279  avg=   1.40  p99=    2.40  ents=
t=   283  avg=   1.40  p99=    1.90  ents=
t=   287  avg=   1.40  p99=    1.90  ents=
t=   291  avg=   1.40  p99=    1.90  ents=27
t=   295  avg=   1.40  p99=    1.90  ents=
t=   299  avg=   1.40  p99=    2.00  ents=
t=   303  avg=   1.40  p99=    2.00  ents=
t=   307  avg=   1.40  p99=    2.00  ents=27
t=   311  avg=   1.40  p99=    2.00  ents=
t=   315  avg=   1.40  p99=    2.00  ents=
t=   319  avg=   1.40  p99=    2.00  ents=
t=   323  avg=   1.40  p99=    2.00  ents=27
t=   327  avg=   1.40  p99=    2.00  ents=
t=   331  avg=   1.40  p99=    2.00  ents=
t=   335  avg=   1.40  p99=    2.00  ents=
t=   339  avg=   1.40  p99=    2.00  ents=27
t=   343  avg=   1.40  p99=    2.00  ents=
t=   347  avg=   1.40  p99=    2.00  ents=
t=   351  avg=   1.40  p99=    2.00  ents=
t=   355  avg=   1.40  p99=    2.00  ents=27
t=   359  avg=   1.40  p99=    2.00  ents=
t=   363  avg=   1.40  p99=    2.20  ents=
t=   367  avg=   1.40  p99=    2.20  ents=
t=   371  avg=   1.40  p99=    2.20  ents=27
t=   375  avg=   1.40  p99=    2.20  ents=
t=   379  avg=   1.40  p99=    2.20  ents=
t=   383  avg=   1.40  p99=    2.20  ents=
t=   387  avg=   1.40  p99=    2.20  ents=27
t=   391  avg=   1.40  p99=    2.20  ents=
t=   395  avg=   1.40  p99=    2.20  ents=
t=   399  avg=   1.40  p99=    2.20  ents=
t=   403  avg=   1.40  p99=    2.20  ents=27
t=   408  avg=   1.40  p99=    2.20  ents=
t=   412  avg=   1.40  p99=    2.20  ents=
t=   416  avg=   1.40  p99=    2.20  ents=
t=   420  avg=   1.40  p99=    2.20  ents=27
t=   424  avg=   1.40  p99=    2.20  ents=
t=   428  avg=   1.40  p99=    2.20  ents=
t=   432  avg=   1.40  p99=    2.20  ents=
t=   436  avg=   1.40  p99=    2.20  ents=27
t=   440  avg=   1.40  p99=    2.20  ents=
t=   444  avg=   1.40  p99=    2.20  ents=
t=   448  avg=   1.40  p99=    2.20  ents=
t=   452  avg=   1.40  p99=    2.20  ents=27
t=   456  avg=   1.40  p99=    2.20  ents=
t=   460  avg=   1.40  p99=    2.20  ents=
t=   464  avg=   1.40  p99=    2.00  ents=
t=   468  avg=   1.40  p99=    1.80  ents=27
t=   472  avg=   1.40  p99=    1.80  ents=
t=   477  avg=   1.40  p99=    1.70  ents=
```

</details>

Entity peak 27 at t=160.

### `gotthard_tunnel`  (`GotthardTunnelEffect`)

- site `[31000, -60, 0]`, armed via **block+redstone**, declared fuse **200** ticks
- baseline **1.12 ms/t** (p99 15.60) -> peak avg **1.7 ms/t**, peak p99 **73.6 ms/t**
- observed window: 516 game ticks over 25.8 s wall; worst wall-clock **57 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
p99 ms/t  ▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.80  p99=   11.30  ents=4
t=     8  avg=   0.70  p99=    1.30  ents=
t=    16  avg=   0.70  p99=    1.30  ents=4
t=    24  avg=   0.70  p99=    1.10  ents=
t=    32  avg=   0.70  p99=    1.10  ents=4
t=    40  avg=   0.70  p99=    1.10  ents=
t=    48  avg=   0.70  p99=    1.10  ents=4
t=    56  avg=   0.80  p99=    1.30  ents=
t=    65  avg=   0.80  p99=    1.30  ents=4
t=    73  avg=   0.80  p99=    1.30  ents=
t=    81  avg=   0.80  p99=    2.10  ents=4
t=    89  avg=   0.90  p99=    2.10  ents=
t=    97  avg=   0.90  p99=    2.10  ents=4
t=   105  avg=   0.90  p99=    2.10  ents=
t=   113  avg=   0.90  p99=    2.10  ents=4
t=   121  avg=   0.90  p99=    2.10  ents=
t=   129  avg=   0.90  p99=    2.10  ents=4
t=   137  avg=   0.90  p99=    2.10  ents=
t=   145  avg=   0.90  p99=    2.10  ents=4
t=   153  avg=   0.90  p99=    2.10  ents=
t=   161  avg=   0.90  p99=    2.10  ents=4
t=   169  avg=   0.90  p99=    2.10  ents=
t=   177  avg=   0.90  p99=    1.20  ents=4
t=   185  avg=   0.90  p99=    1.20  ents=
t=   193  avg=   0.90  p99=    1.20  ents=4
t=   201  avg=   1.60  p99=   73.60  ents=
t=   210  avg=   1.70  p99=   73.60  ents=3
t=   218  avg=   1.70  p99=   73.60  ents=
t=   226  avg=   1.60  p99=   73.60  ents=3
t=   234  avg=   1.60  p99=   73.60  ents=
t=   242  avg=   1.60  p99=   73.60  ents=3
t=   250  avg=   1.60  p99=   73.60  ents=
t=   258  avg=   1.60  p99=   73.60  ents=3
t=   266  avg=   1.60  p99=   73.60  ents=
t=   275  avg=   1.60  p99=   73.60  ents=3
t=   283  avg=   1.60  p99=   73.60  ents=
t=   291  avg=   1.60  p99=   73.60  ents=3
t=   299  avg=   1.60  p99=   73.60  ents=
t=   307  avg=   0.80  p99=    1.20  ents=3
t=   315  avg=   0.80  p99=    1.10  ents=
t=   323  avg=   0.80  p99=    1.10  ents=3
t=   331  avg=   0.80  p99=    1.10  ents=
t=   339  avg=   0.80  p99=    1.10  ents=3
t=   347  avg=   0.80  p99=    1.10  ents=
t=   355  avg=   0.80  p99=    1.20  ents=3
t=   363  avg=   0.80  p99=    1.20  ents=
t=   371  avg=   0.80  p99=    1.20  ents=3
t=   379  avg=   0.80  p99=    1.20  ents=
t=   387  avg=   0.80  p99=    1.20  ents=3
t=   395  avg=   0.80  p99=    1.20  ents=
t=   403  avg=   0.80  p99=    1.20  ents=3
t=   411  avg=   0.80  p99=    1.20  ents=
t=   419  avg=   0.80  p99=    1.20  ents=3
t=   428  avg=   0.80  p99=    1.20  ents=
t=   436  avg=   0.80  p99=    1.20  ents=3
t=   444  avg=   0.80  p99=    1.20  ents=
t=   452  avg=   0.80  p99=    1.20  ents=3
t=   460  avg=   0.80  p99=    1.20  ents=
t=   468  avg=   0.80  p99=    1.20  ents=3
t=   476  avg=   0.80  p99=    1.20  ents=
t=   484  avg=   0.80  p99=    1.20  ents=3
t=   492  avg=   0.80  p99=    1.20  ents=
t=   500  avg=   0.80  p99=    1.20  ents=3
t=   508  avg=   0.70  p99=    1.00  ents=
t=   516  avg=   0.70  p99=    1.00  ents=3
```

</details>

Entity peak 4 at t=0.

### `continental_drift`  (`ContinentalDriftEffect`)

- site `[13000, -60, 0]`, armed via **block+redstone**, declared fuse **480** ticks
- baseline **1.67 ms/t** (p99 37.00) -> peak avg **1.4 ms/t**, peak p99 **38.6 ms/t**
- observed window: 799 game ticks over 40.0 s wall; worst wall-clock **57 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  █▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▇▇▇▇▇███▇████████████████▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▄▄▄▃▃▃▃▃▃▄▄▄▄▄▄▄▄▃▃▃▃▃▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
p99 ms/t  █▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████████████████████▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.40  p99=   37.00  ents=4
t=    12  avg=   0.80  p99=    1.90  ents=
t=    24  avg=   0.80  p99=    1.90  ents=
t=    36  avg=   0.80  p99=    1.90  ents=
t=    48  avg=   0.80  p99=    1.90  ents=4
t=    61  avg=   0.90  p99=    1.90  ents=
t=    73  avg=   0.90  p99=    1.90  ents=
t=    85  avg=   1.30  p99=   38.60  ents=
t=    97  avg=   1.30  p99=   38.60  ents=4
t=   109  avg=   1.40  p99=   38.60  ents=
t=   122  avg=   1.40  p99=   38.60  ents=
t=   134  avg=   1.40  p99=   38.60  ents=
t=   146  avg=   1.40  p99=   38.60  ents=4
t=   158  avg=   1.40  p99=   38.60  ents=
t=   170  avg=   1.40  p99=   38.60  ents=
t=   182  avg=   1.10  p99=    8.30  ents=
t=   194  avg=   1.10  p99=    8.30  ents=4
t=   206  avg=   1.10  p99=    8.30  ents=
t=   218  avg=   1.10  p99=    8.30  ents=
t=   231  avg=   1.00  p99=    6.90  ents=
t=   243  avg=   1.10  p99=    7.00  ents=4
t=   255  avg=   1.10  p99=    7.00  ents=
t=   267  avg=   1.10  p99=    7.00  ents=
t=   279  avg=   1.10  p99=    7.00  ents=
t=   291  avg=   1.00  p99=    7.00  ents=4
t=   303  avg=   1.10  p99=    7.30  ents=
t=   315  avg=   1.10  p99=    7.30  ents=
t=   328  avg=   1.10  p99=    7.30  ents=
t=   340  avg=   1.10  p99=    7.30  ents=4
t=   352  avg=   1.00  p99=    7.30  ents=
t=   364  avg=   1.00  p99=    7.30  ents=
t=   376  avg=   1.00  p99=    7.30  ents=
t=   388  avg=   1.00  p99=    7.30  ents=4
t=   400  avg=   1.10  p99=    7.30  ents=
t=   412  avg=   1.00  p99=    5.90  ents=
t=   424  avg=   1.00  p99=    6.10  ents=
t=   436  avg=   1.10  p99=    6.10  ents=4
t=   449  avg=   1.10  p99=    6.10  ents=
t=   461  avg=   1.00  p99=    6.10  ents=
t=   473  avg=   1.00  p99=    6.10  ents=
t=   485  avg=   1.10  p99=    6.10  ents=3
t=   497  avg=   1.00  p99=    6.10  ents=
t=   509  avg=   1.00  p99=    6.10  ents=
t=   521  avg=   1.00  p99=    5.90  ents=
t=   533  avg=   1.00  p99=    5.90  ents=3
t=   545  avg=   0.90  p99=    5.90  ents=
t=   557  avg=   0.90  p99=    5.90  ents=
t=   569  avg=   0.90  p99=    5.90  ents=
t=   581  avg=   0.90  p99=    1.40  ents=3
t=   594  avg=   0.90  p99=    1.40  ents=
t=   606  avg=   0.90  p99=    1.40  ents=
t=   618  avg=   0.80  p99=    1.20  ents=
t=   630  avg=   0.80  p99=    1.20  ents=3
t=   642  avg=   0.90  p99=    1.20  ents=
t=   654  avg=   0.90  p99=    1.40  ents=
t=   666  avg=   0.90  p99=    1.40  ents=
t=   678  avg=   0.90  p99=    1.40  ents=3
t=   690  avg=   0.90  p99=    1.40  ents=
t=   702  avg=   0.90  p99=    1.40  ents=
t=   714  avg=   0.90  p99=    1.40  ents=
t=   727  avg=   0.90  p99=    1.40  ents=3
t=   739  avg=   0.90  p99=    1.40  ents=
t=   751  avg=   0.90  p99=    1.30  ents=
t=   763  avg=   0.90  p99=    1.30  ents=
t=   775  avg=   0.90  p99=    1.30  ents=3
t=   787  avg=   0.90  p99=    1.30  ents=
t=   799  avg=   0.90  p99=    1.30  ents=
```

</details>

Entity peak 4 at t=0.

### `leaping_tnt`  (`LeapingTNTEffect`)

- site `[45000, -60, 0]`, armed via **block+redstone**, declared fuse **100000** ticks
- baseline **1.56 ms/t** (p99 61.50) -> peak avg **1.3 ms/t**, peak p99 **25.3 ms/t**
- observed window: 1218 game ticks over 60.9 s wall; worst wall-clock **52 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▄▄▄▄▄▁▁▂▂▂▂▂▂▂▂▂▄▄▄▆▆▆▆▆███▆▆▆▆▆▆▆▆▆▆▆▆▆▆▄▆▄▄▆▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▂▂▂▂▂▂▂▂▁▁▁▂▂▂▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▄▄▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▄▂▂▂▂▂▂▂▂▂▂▂█████████████▆▆█████████▄▄▄▄▄▄▄▄▄▄▄
p99 ms/t  ▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂████████████████████████▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.10  p99=    7.70  ents=4
t=    20  avg=   0.90  p99=    8.70  ents=
t=    41  avg=   1.00  p99=    8.70  ents=
t=    61  avg=   1.00  p99=    8.70  ents=
t=    81  avg=   1.20  p99=    8.70  ents=4
t=   101  avg=   1.30  p99=    8.70  ents=
t=   121  avg=   1.20  p99=    7.30  ents=
t=   142  avg=   1.20  p99=    7.30  ents=
t=   162  avg=   1.20  p99=    7.30  ents=4
t=   182  avg=   1.20  p99=    5.70  ents=
t=   202  avg=   1.10  p99=    5.70  ents=
t=   222  avg=   1.10  p99=    5.70  ents=
t=   242  avg=   1.10  p99=    5.60  ents=4
t=   262  avg=   1.10  p99=    5.60  ents=
t=   283  avg=   1.10  p99=    4.00  ents=
t=   303  avg=   1.10  p99=    6.50  ents=
t=   323  avg=   1.10  p99=    6.50  ents=4
t=   343  avg=   1.10  p99=    6.50  ents=
t=   363  avg=   1.10  p99=    6.50  ents=
t=   383  avg=   1.10  p99=    6.50  ents=
t=   403  avg=   1.00  p99=    3.60  ents=4
t=   423  avg=   1.00  p99=    3.60  ents=
t=   444  avg=   0.90  p99=    3.30  ents=
t=   464  avg=   0.90  p99=    5.90  ents=
t=   484  avg=   1.00  p99=    5.90  ents=4
t=   504  avg=   1.00  p99=    5.90  ents=
t=   524  avg=   1.00  p99=    5.90  ents=
t=   544  avg=   1.10  p99=    6.60  ents=
t=   564  avg=   1.00  p99=    6.60  ents=4
t=   585  avg=   1.00  p99=    6.60  ents=
t=   605  avg=   1.00  p99=    6.60  ents=
t=   625  avg=   1.00  p99=    6.60  ents=
t=   645  avg=   1.00  p99=    3.60  ents=4
t=   665  avg=   1.00  p99=    3.60  ents=
t=   685  avg=   1.00  p99=    3.60  ents=
t=   705  avg=   1.00  p99=    3.60  ents=
t=   726  avg=   1.00  p99=    2.90  ents=4
t=   746  avg=   1.00  p99=    2.90  ents=
t=   766  avg=   1.00  p99=    5.50  ents=
t=   786  avg=   1.00  p99=    5.50  ents=
t=   806  avg=   1.00  p99=    5.50  ents=4
t=   826  avg=   1.00  p99=    5.50  ents=
t=   846  avg=   1.00  p99=    5.50  ents=
t=   866  avg=   1.00  p99=    5.40  ents=
t=   887  avg=   1.00  p99=    5.40  ents=4
t=   907  avg=   1.00  p99=    3.80  ents=
t=   927  avg=   1.00  p99=    3.80  ents=
t=   947  avg=   1.00  p99=    3.80  ents=
t=   967  avg=   1.00  p99=    3.80  ents=4
t=   987  avg=   1.00  p99=    3.80  ents=
t=  1007  avg=   1.00  p99=    3.70  ents=
t=  1027  avg=   1.00  p99=    7.20  ents=
t=  1048  avg=   1.00  p99=    7.20  ents=4
t=  1068  avg=   1.00  p99=    7.20  ents=
t=  1088  avg=   1.30  p99=   25.30  ents=
t=  1108  avg=   1.30  p99=   25.30  ents=
t=  1128  avg=   1.30  p99=   25.30  ents=3
t=  1149  avg=   1.30  p99=   25.30  ents=
t=  1169  avg=   1.30  p99=   25.30  ents=
t=  1190  avg=   1.10  p99=    3.80  ents=
t=  1210  avg=   1.10  p99=    3.80  ents=3
```

</details>

Entity peak 4 at t=0.

### `structure_tnt`  (`StructureTNTEffect`)

- site `[37000, -60, 0]`, armed via **block+redstone**, declared fuse **160** ticks
- baseline **1.29 ms/t** (p99 43.20) -> peak avg **1.0 ms/t**, peak p99 **17.4 ms/t**
- observed window: 476 game ticks over 23.8 s wall; worst wall-clock **51 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▄▁▁▁▁▁▁▁▁▁▁▁▁▁▄▄▄▄▄▄▄▄▄▄████▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄
p99 ms/t  █▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.90  p99=   17.40  ents=4
t=     4  avg=   0.80  p99=    3.50  ents=
t=     9  avg=   0.80  p99=    3.50  ents=
t=    13  avg=   0.80  p99=    3.50  ents=
t=    17  avg=   0.80  p99=    3.50  ents=4
t=    21  avg=   0.80  p99=    3.50  ents=
t=    25  avg=   0.80  p99=    1.30  ents=
t=    29  avg=   0.80  p99=    1.30  ents=
t=    33  avg=   0.80  p99=    1.30  ents=4
t=    37  avg=   0.80  p99=    1.30  ents=
t=    41  avg=   0.80  p99=    1.30  ents=
t=    45  avg=   0.80  p99=    1.30  ents=
t=    49  avg=   0.80  p99=    1.30  ents=4
t=    53  avg=   0.80  p99=    1.30  ents=
t=    57  avg=   0.90  p99=    1.30  ents=
t=    61  avg=   0.90  p99=    1.30  ents=
t=    65  avg=   0.90  p99=    1.30  ents=4
t=    69  avg=   0.90  p99=    1.30  ents=
t=    73  avg=   0.90  p99=    1.30  ents=
t=    77  avg=   0.90  p99=    1.30  ents=
t=    81  avg=   0.90  p99=    1.30  ents=4
t=    85  avg=   0.90  p99=    1.30  ents=
t=    89  avg=   0.90  p99=    1.30  ents=
t=    93  avg=   0.90  p99=    1.30  ents=
t=    97  avg=   1.00  p99=    1.30  ents=4
t=   101  avg=   1.00  p99=    1.30  ents=
t=   105  avg=   1.00  p99=    1.30  ents=
t=   109  avg=   1.00  p99=    1.30  ents=
t=   113  avg=   0.90  p99=    1.30  ents=4
t=   117  avg=   0.90  p99=    1.30  ents=
t=   121  avg=   0.90  p99=    1.30  ents=
t=   125  avg=   0.90  p99=    1.30  ents=
t=   129  avg=   0.90  p99=    1.20  ents=4
t=   133  avg=   0.90  p99=    1.20  ents=
t=   137  avg=   0.90  p99=    1.20  ents=
t=   141  avg=   0.90  p99=    1.20  ents=
t=   145  avg=   0.90  p99=    1.20  ents=4
t=   149  avg=   0.90  p99=    1.20  ents=
t=   153  avg=   0.90  p99=    1.20  ents=
t=   157  avg=   0.90  p99=    1.20  ents=
t=   161  avg=   0.90  p99=    1.20  ents=3
t=   165  avg=   0.90  p99=    1.20  ents=
t=   169  avg=   0.90  p99=    1.20  ents=
t=   174  avg=   0.90  p99=    1.20  ents=
t=   178  avg=   0.90  p99=    1.20  ents=3
t=   182  avg=   0.90  p99=    1.20  ents=
t=   186  avg=   0.90  p99=    1.20  ents=
t=   190  avg=   0.90  p99=    1.20  ents=
t=   194  avg=   0.90  p99=    1.20  ents=3
t=   198  avg=   0.90  p99=    1.20  ents=
t=   202  avg=   0.90  p99=    1.20  ents=
t=   206  avg=   0.90  p99=    1.20  ents=
t=   210  avg=   0.90  p99=    1.20  ents=3
t=   214  avg=   0.90  p99=    1.20  ents=
t=   218  avg=   0.90  p99=    1.20  ents=
t=   222  avg=   0.90  p99=    1.20  ents=
t=   226  avg=   0.90  p99=    1.20  ents=3
t=   230  avg=   0.90  p99=    1.20  ents=
t=   234  avg=   0.90  p99=    1.20  ents=
t=   238  avg=   0.90  p99=    1.20  ents=
t=   242  avg=   0.90  p99=    1.20  ents=3
t=   246  avg=   0.90  p99=    1.20  ents=
t=   250  avg=   0.90  p99=    1.20  ents=
t=   254  avg=   0.90  p99=    1.20  ents=
t=   258  avg=   0.90  p99=    1.20  ents=3
t=   262  avg=   0.90  p99=    1.20  ents=
t=   266  avg=   0.90  p99=    1.20  ents=
t=   270  avg=   0.90  p99=    1.20  ents=
t=   274  avg=   0.90  p99=    1.20  ents=3
t=   278  avg=   0.90  p99=    1.20  ents=
t=   282  avg=   0.90  p99=    1.20  ents=
t=   286  avg=   0.90  p99=    1.20  ents=
t=   290  avg=   0.90  p99=    1.20  ents=3
t=   294  avg=   0.90  p99=    1.20  ents=
t=   298  avg=   0.90  p99=    1.20  ents=
t=   302  avg=   0.90  p99=    1.20  ents=
t=   306  avg=   0.90  p99=    1.20  ents=3
t=   310  avg=   0.90  p99=    1.20  ents=
t=   315  avg=   0.90  p99=    1.20  ents=
t=   319  avg=   0.90  p99=    1.20  ents=
t=   323  avg=   0.90  p99=    1.20  ents=3
t=   327  avg=   0.90  p99=    1.20  ents=
t=   331  avg=   0.90  p99=    1.20  ents=
t=   335  avg=   0.90  p99=    1.20  ents=
t=   339  avg=   0.90  p99=    1.20  ents=3
t=   343  avg=   0.90  p99=    1.20  ents=
t=   347  avg=   0.90  p99=    1.20  ents=
t=   351  avg=   0.90  p99=    1.20  ents=
t=   355  avg=   0.90  p99=    1.20  ents=3
t=   359  avg=   0.90  p99=    1.20  ents=
t=   363  avg=   0.90  p99=    1.20  ents=
t=   367  avg=   0.90  p99=    1.20  ents=
t=   371  avg=   0.90  p99=    1.20  ents=3
t=   375  avg=   0.90  p99=    1.20  ents=
t=   379  avg=   0.90  p99=    1.20  ents=
t=   383  avg=   0.90  p99=    1.20  ents=
t=   387  avg=   0.90  p99=    1.20  ents=3
t=   391  avg=   0.90  p99=    1.20  ents=
t=   395  avg=   0.90  p99=    1.20  ents=
t=   399  avg=   0.90  p99=    1.20  ents=
t=   403  avg=   0.90  p99=    1.20  ents=3
t=   407  avg=   0.90  p99=    1.20  ents=
t=   411  avg=   0.90  p99=    1.20  ents=
t=   415  avg=   0.90  p99=    1.20  ents=
t=   419  avg=   0.90  p99=    1.20  ents=3
t=   423  avg=   0.90  p99=    1.20  ents=
t=   427  avg=   0.90  p99=    1.20  ents=
t=   431  avg=   0.90  p99=    1.20  ents=
t=   435  avg=   0.90  p99=    1.20  ents=3
t=   439  avg=   0.90  p99=    1.20  ents=
t=   443  avg=   0.90  p99=    1.20  ents=
t=   447  avg=   0.90  p99=    1.20  ents=
t=   452  avg=   0.90  p99=    1.20  ents=3
t=   456  avg=   0.90  p99=    1.20  ents=
t=   460  avg=   0.90  p99=    1.20  ents=
t=   464  avg=   0.90  p99=    1.20  ents=
t=   468  avg=   0.90  p99=    1.20  ents=3
t=   472  avg=   0.90  p99=    1.20  ents=
t=   476  avg=   0.90  p99=    1.20  ents=
```

</details>

Entity peak 4 at t=0.

### `meteor_shower`  (`MeteorShowerEffect`)

- site `[17000, -60, 0]`, armed via **block+redstone**, declared fuse **720** ticks
- baseline **1.69 ms/t** (p99 34.70) -> peak avg **2.5 ms/t**, peak p99 **10.5 ms/t**
- observed window: 1037 game ticks over 51.9 s wall; worst wall-clock **58 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 114

```
avg ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▃▃▃▃▃▃▃▃▃▃▃▃▃▄▄▄▄▄▅▅▅▆▆▆▆▆▆▆▇▇▇▇▇▇▇▇▇▇▇▇▇▇█▇▇▇▇▇▇▇▇▇▇██▇▇██▇▇▇██████████████████████████▇▇▇▇▆▆▇▇▇▇▇▇▇▇███▇███████████████████████████████████████▇▇▇▇▇▇▇▇▇██▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▆▆▆▆▆▅▅▅▅▅▅▅▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄
p99 ms/t  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▄▄▄▄▄▅▅▅▅▅▅▅▅█████████████████████████▇▇▇▇▇▇▇▇▇▇▇▇▇▇█████████████████████████▇▇▇▇▇▇▇▇▇▇▅▅▅▅▅▅▅████████████████████████▇▇▇▇▇▇▇▇▆▆▆▆▆▆▆▆▆▆▆▆▆▆▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▃▃▂▂▂▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   0.80  p99=    1.50  ents=4
t=    16  avg=   0.80  p99=    1.70  ents=4
t=    32  avg=   0.80  p99=    1.70  ents=4
t=    48  avg=   0.80  p99=    1.70  ents=4
t=    64  avg=   0.90  p99=    1.70  ents=4
t=    80  avg=   1.00  p99=    1.70  ents=4
t=    97  avg=   1.10  p99=    2.00  ents=12
t=   113  avg=   1.10  p99=    2.20  ents=18
t=   129  avg=   1.20  p99=    2.20  ents=22
t=   146  avg=   1.20  p99=    2.20  ents=32
t=   162  avg=   1.20  p99=    2.20  ents=39
t=   178  avg=   1.30  p99=    2.20  ents=41
t=   194  avg=   1.30  p99=    5.10  ents=49
t=   210  avg=   1.40  p99=    6.60  ents=49
t=   226  avg=   1.50  p99=    6.60  ents=51
t=   242  avg=   1.70  p99=   10.50  ents=54
t=   259  avg=   1.90  p99=   10.50  ents=56
t=   275  avg=   2.00  p99=   10.50  ents=59
t=   291  avg=   2.10  p99=   10.50  ents=61
t=   307  avg=   2.20  p99=   10.50  ents=63
t=   323  avg=   2.20  p99=   10.50  ents=68
t=   339  avg=   2.30  p99=   10.50  ents=69
t=   356  avg=   2.10  p99=    8.70  ents=73
t=   372  avg=   2.20  p99=    8.70  ents=73
t=   388  avg=   2.30  p99=    8.70  ents=72
t=   404  avg=   2.30  p99=   10.20  ents=76
t=   420  avg=   2.30  p99=   10.20  ents=74
t=   436  avg=   2.50  p99=   10.20  ents=74
t=   452  avg=   2.50  p99=   10.20  ents=79
t=   469  avg=   2.40  p99=   10.20  ents=78
t=   485  avg=   2.30  p99=   10.20  ents=87
t=   501  avg=   2.40  p99=    8.60  ents=89
t=   517  avg=   2.30  p99=    8.60  ents=92
t=   533  avg=   2.10  p99=    8.60  ents=95
t=   549  avg=   2.10  p99=    6.20  ents=98
t=   565  avg=   2.20  p99=    6.20  ents=100
t=   582  avg=   2.30  p99=    9.40  ents=99
t=   598  avg=   2.30  p99=    9.40  ents=97
t=   614  avg=   2.40  p99=    9.40  ents=102
t=   630  avg=   2.30  p99=    9.40  ents=102
t=   646  avg=   2.40  p99=    9.40  ents=103
t=   662  avg=   2.40  p99=    9.40  ents=104
t=   678  avg=   2.40  p99=    8.30  ents=104
t=   694  avg=   2.40  p99=    8.30  ents=111
t=   710  avg=   2.30  p99=    8.10  ents=114
t=   726  avg=   2.30  p99=    8.10  ents=114
t=   743  avg=   2.30  p99=    8.10  ents=112
t=   759  avg=   2.20  p99=    6.50  ents=109
t=   775  avg=   2.10  p99=    6.50  ents=105
t=   791  avg=   2.30  p99=    7.10  ents=96
t=   807  avg=   2.20  p99=    7.10  ents=95
t=   823  avg=   2.20  p99=    7.10  ents=90
t=   839  avg=   2.20  p99=    7.10  ents=87
t=   855  avg=   2.10  p99=    7.10  ents=87
t=   872  avg=   2.00  p99=    7.10  ents=87
t=   888  avg=   1.80  p99=    6.10  ents=87
t=   904  avg=   1.70  p99=    6.10  ents=87
t=   920  avg=   1.60  p99=    3.90  ents=87
t=   936  avg=   1.50  p99=    3.40  ents=87
t=   952  avg=   1.50  p99=    2.20  ents=87
t=   968  avg=   1.50  p99=    2.10  ents=87
t=   984  avg=   1.50  p99=    2.20  ents=87
t=  1000  avg=   1.50  p99=    2.20  ents=87
t=  1016  avg=   1.50  p99=    2.20  ents=87
t=  1033  avg=   1.50  p99=    2.20  ents=87
```

</details>

Entity peak 114 at t=710.

### `replay_tnt`  (`?`)

- site `[25000, -60, 0]`, armed via **block+redstone**, declared fuse **80** ticks
- baseline **1.32 ms/t** (p99 32.60) -> peak avg **1.5 ms/t**, peak p99 **9.7 ms/t**
- observed window: 399 game ticks over 19.9 s wall; worst wall-clock **52 ms per tick**
- creepers spawned: 3; peak entities within 400 blocks: 4

```
avg ms/t  ▁▁▁▁▁▁▁▁▃▃▃▃▃▃▃▆▃▃▆▆▆██████▆▆▆▆▆▆▆▆▆▃▆▆▆▆▃▃▆▆▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▆▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▆
p99 ms/t  ▃▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁█████████
```

<details><summary>tick series (game tick since arming -> avg ms/t, p99 ms/t)</summary>

```
t=     0  avg=   1.20  p99=    4.80  ents=4
t=     4  avg=   1.20  p99=    5.80  ents=
t=     8  avg=   1.20  p99=    5.80  ents=
t=    12  avg=   1.20  p99=    5.80  ents=
t=    16  avg=   1.20  p99=    5.80  ents=4
t=    20  avg=   1.20  p99=    5.80  ents=
t=    24  avg=   1.20  p99=    5.80  ents=
t=    28  avg=   1.20  p99=    5.80  ents=
t=    32  avg=   1.30  p99=    5.80  ents=4
t=    36  avg=   1.30  p99=    5.80  ents=
t=    40  avg=   1.30  p99=    5.80  ents=
t=    44  avg=   1.30  p99=    5.80  ents=
t=    48  avg=   1.30  p99=    5.80  ents=4
t=    52  avg=   1.30  p99=    5.80  ents=
t=    56  avg=   1.30  p99=    5.80  ents=
t=    60  avg=   1.40  p99=    5.80  ents=
t=    64  avg=   1.30  p99=    5.80  ents=4
t=    68  avg=   1.30  p99=    5.80  ents=
t=    72  avg=   1.40  p99=    5.80  ents=
t=    76  avg=   1.40  p99=    5.80  ents=
t=    80  avg=   1.40  p99=    5.80  ents=4
t=    84  avg=   1.50  p99=    5.80  ents=
t=    88  avg=   1.50  p99=    5.80  ents=
t=    92  avg=   1.50  p99=    5.80  ents=
t=    96  avg=   1.50  p99=    5.80  ents=4
t=   100  avg=   1.50  p99=    5.80  ents=
t=   104  avg=   1.50  p99=    4.60  ents=
t=   108  avg=   1.40  p99=    4.60  ents=
t=   113  avg=   1.40  p99=    4.60  ents=4
t=   117  avg=   1.40  p99=    4.60  ents=
t=   121  avg=   1.40  p99=    4.60  ents=
t=   125  avg=   1.40  p99=    4.60  ents=
t=   129  avg=   1.40  p99=    4.60  ents=4
t=   133  avg=   1.40  p99=    4.60  ents=
t=   137  avg=   1.40  p99=    4.60  ents=
t=   141  avg=   1.40  p99=    4.60  ents=
t=   145  avg=   1.30  p99=    4.60  ents=4
t=   149  avg=   1.40  p99=    4.60  ents=
t=   153  avg=   1.40  p99=    4.60  ents=
t=   157  avg=   1.40  p99=    4.60  ents=
t=   161  avg=   1.40  p99=    4.60  ents=4
t=   165  avg=   1.30  p99=    4.60  ents=
t=   169  avg=   1.30  p99=    4.60  ents=
t=   173  avg=   1.40  p99=    4.60  ents=
t=   177  avg=   1.40  p99=    4.60  ents=4
t=   181  avg=   1.30  p99=    4.60  ents=
t=   185  avg=   1.30  p99=    2.80  ents=
t=   189  avg=   1.30  p99=    2.80  ents=
t=   193  avg=   1.30  p99=    2.80  ents=4
t=   197  avg=   1.30  p99=    2.80  ents=
t=   201  avg=   1.30  p99=    2.80  ents=
t=   205  avg=   1.30  p99=    2.80  ents=
t=   209  avg=   1.30  p99=    2.80  ents=4
t=   213  avg=   1.30  p99=    2.80  ents=
t=   217  avg=   1.30  p99=    2.80  ents=
t=   221  avg=   1.30  p99=    2.80  ents=
t=   225  avg=   1.30  p99=    2.80  ents=4
t=   229  avg=   1.30  p99=    2.80  ents=
t=   233  avg=   1.30  p99=    2.80  ents=
t=   237  avg=   1.30  p99=    2.80  ents=
t=   241  avg=   1.30  p99=    2.80  ents=4
t=   246  avg=   1.40  p99=    2.80  ents=
t=   250  avg=   1.30  p99=    2.80  ents=
t=   254  avg=   1.30  p99=    2.80  ents=
t=   258  avg=   1.30  p99=    2.80  ents=4
t=   262  avg=   1.30  p99=    2.80  ents=
t=   266  avg=   1.30  p99=    2.80  ents=
t=   270  avg=   1.30  p99=    2.80  ents=
t=   274  avg=   1.30  p99=    2.80  ents=4
t=   278  avg=   1.30  p99=    2.80  ents=
t=   282  avg=   1.30  p99=    2.80  ents=
t=   286  avg=   1.30  p99=    2.80  ents=
t=   290  avg=   1.30  p99=    2.80  ents=4
t=   294  avg=   1.30  p99=    2.80  ents=
t=   298  avg=   1.30  p99=    2.80  ents=
t=   302  avg=   1.30  p99=    2.80  ents=
t=   306  avg=   1.30  p99=    2.80  ents=4
t=   310  avg=   1.30  p99=    2.80  ents=
t=   314  avg=   1.30  p99=    2.80  ents=
t=   318  avg=   1.30  p99=    2.80  ents=
t=   322  avg=   1.30  p99=    2.80  ents=4
t=   326  avg=   1.30  p99=    2.80  ents=
t=   330  avg=   1.30  p99=    2.80  ents=
t=   334  avg=   1.30  p99=    2.80  ents=
t=   338  avg=   1.30  p99=    2.80  ents=4
t=   342  avg=   1.30  p99=    2.80  ents=
t=   346  avg=   1.30  p99=    2.80  ents=
t=   350  avg=   1.30  p99=    2.80  ents=
t=   354  avg=   1.30  p99=    2.80  ents=4
t=   358  avg=   1.30  p99=    2.80  ents=
t=   362  avg=   1.30  p99=    2.80  ents=
t=   366  avg=   1.30  p99=    9.70  ents=
t=   370  avg=   1.30  p99=    9.70  ents=4
t=   374  avg=   1.30  p99=    9.70  ents=
t=   378  avg=   1.30  p99=    9.70  ents=
t=   382  avg=   1.30  p99=    9.70  ents=
t=   386  avg=   1.30  p99=    9.70  ents=4
t=   390  avg=   1.30  p99=    9.70  ents=
t=   394  avg=   1.30  p99=    9.70  ents=
t=   399  avg=   1.40  p99=    9.70  ents=
```

</details>

Entity peak 4 at t=0.

---

## Errors, crashes and log anomalies

No exceptions, crashes or hangs recorded in the batches measured so far.

