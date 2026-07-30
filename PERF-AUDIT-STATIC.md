# Static Performance Audit — Fabric-LuckyTNTMod

Read-only analysis of `TntLib/src/main/java/luckytntlib/` and `tntmod/src/main/java/luckytnt/`
at commit `08d28c0`, **after** the optimization pass in `b32dfea..00f20a3`.

Findings already fixed by that pass are not repeated. Where a source comment claims a fix, the
claim was verified against the current code; three of them are correct but *incomplete* (F4, F6,
F13) and are called out as such.

## Method / cost model used for the estimates

* `Level.getBlockState(BlockPos)` resolves `getChunk(cx, cz)` → `getChunk(x, z, ChunkStatus.FULL, true)`.
  On a `ServerLevel` the `true` means **load-or-generate synchronously**. So a sweep over an
  N-block-wide box does not just read blocks, it can force-generate `(N/16)²` chunks. The previous
  pass fixed exactly this for Black Hole TNT (commit `319937e`, `sLevel.isLoaded(...)` guard); the
  other mega-TNTs never got the same guard.
* `setBlock(pos, state, 3)` = `UPDATE_NEIGHBORS | UPDATE_CLIENTS`: 6 neighbour shape/redstone
  updates plus a client sync entry per block. 288 of the 298 `setBlock` calls in the mod use flag 3;
  only 10 use `UPDATE_CLIENTS`/2/18.
* Sphere volume = `4/3·π·r³`; a cube of half-extent r = `(2r+1)³`.
* "ops/tick" is per detonation-tick unless the row says *sustained*.

---

## Summary table

| # | Effect / File | Hot spot | Complexity | Est. ops in the worst tick | Severity |
|---|---|---|---|---|---|
| F1 | `projectile/HydrogenBombBombEffect.java:51` (+ `ExplosionHelper.java:142`) | `doModifiedSphericalExplosion(r=250, scale 1/⅔/1)`; inner z loop never culled | O(r³) | **80.3 M loop iters, 53.4 M `getBlockState` + `new BlockPos` + `Math.random()`; ~1024 chunks force-loaded** | Critical |
| F2 | `projectile/TsarBombaBombEffect.java:64‑101` | r=300 sphere clipped to \|y\|≤100 | O(r³) | **54.5 M `getBlockState` + `new BlockPos`; ~1444 chunks force-loaded** | Critical |
| F3 | `ResetTNTEffect.java:93‑113 / 133‑150 / 157‑172` | r=100 save + restore + size count | 3 × O(r³) | **8.12 M iters ×3, 4.19 M `getBlockState` twice, up to 4.19 M `setBlock` flag 3, 16.8 MB `int[]` per entity** | Critical |
| F4 | `ExplosionHelper.java:338‑363` `doTopBlockExplosionForAll` | 2 `getBlockState` + 2 collision-shape calls + a `DirectionalPlaceContext` **allocation per solid block** | O(r³) | at r=150 (`WinterTNTEffect`): **14.1 M cells → ~28 M state reads + ~10 M context allocations** | Critical |
| F5 | `JungleTNTEffect.java:42+44` / `AtlantisEffect.java:119+121` | two full r=150 (resp. r=100) sphere sweeps back to back | 2 × O(r³) | Jungle **28.3 M**, Atlantis **8.4 M** `getBlockState` | Critical |
| F6 | `LevelEvents.java:262‑291` `getTopBlock` + its callers | full Y-column scan, 2 `new BlockPos` + 2 `getBlockState` per step, no heightmap | O(columns × 384) | **sustained** heat-death: ~460 k `getBlockState` + ~920 k `BlockPos` **per player per tick**; `AetherTNTEffect`: 16 M in one tick | Critical |
| F7 | `SinkholeTNTEffect.java:29‑46` | 67³ cube re-swept every 2 ticks for 150 ticks, `getPersistentData()` inside the innermost loop | O(r³) × 75 | 300 763 iters + 300 763 NBT lookups + 300 763 `BlockPos` **per firing**, ~113 k `setBlock` flag 3 per firing | High |
| F8 | `WinterTNTEffect.java:48‑54` | 51 `SnowySnowball` entities **every tick**, no server guard | O(1) but ×200 ticks | **10 200 projectiles**, allocated on client too | High |
| F9 | `DeathRayEffect.java:39‑62` | growing solid cube scanned to find a 3-block shell | Σ O(s³), s=1..80 | **81.9 M iters total, 4.17 M in the last tick, 94 % rejected** | High |
| F10 | `HeatWaveEffect.java:32`, `StoneColdEffect.java:64+81` | r=150 / r=130 sphere (+ top-block pass) | O(r³) | HeatWave **14.1 M**; StoneCold **9.2 M + 9.2 M (F4-priced)** | High |
| F11 | `FlowerForestTNTEffect.java:57‑72`, `PlantationTNTEffect.java:57‑71 / 79‑130` | `getTopBlock` per column + a second full column scan | O(r² × 384) | FlowerForest **~9.0 M**, Plantation **~4.4 M** `getBlockState` | High |
| F12 | `GrandeFinaleEffect.java:63‑113` | 1000 `FallingBlockEntity` + 500 `PrimedLTNT` in one tick, `explosionTick` has **no server guard** | O(1) ×1500 | 1500 entities server-side **and** 1500 built+discarded client-side | High |
| F13 | `BlackHoleTNTEffect.java:29,43,127` | 200-wide `getEntities` every tick × 350 ticks; 2000 concurrent falling blocks; 150 particles/tick | O(sections) per tick | ~2197 entity sections/tick **sustained**, up to 2000 physics entities | High |
| F14 | `ContinentalDriftEffect.java:102‑122` | 8 rift carvings, each ~43 k `setBlock` flag 3 in one tick | O(length × 441) | 97 020 cells → ~43 k `setBlock` per firing | Medium‑High |
| F15 | `CustomTNTEffect.java:116/231/320` + `ExplosionHelper.java:179‑195` | `doCubicalExplosion(5×intensity)`, no culling, `Math.sqrt` per cell | O(r³) | at max config: **8.12 M cells per detonation × up to 12 chained detonations** | Medium‑High |
| F16 | `MineralTNTEffect.java:112` | `new Random()` per replaced block | O(cells) allocs | ~38 k `Random` allocations (each hits the global seed uniquifier CAS) | Medium |
| F17 | `RingTNTEffect.java:36‑56` | Y-column scan with **no `break`** — always 384 iterations, 2 `getBlockState` + 2 `getCollisionShape` each | O(384) per call | 60 calls from `EyeOfTheSaharaEffect` → ~92 k collision-shape queries | Medium |
| F18 | `FlakTNTEffect.java:18`, `CannonTNTEffect.java:18`, `TurretTNTEffect.java:18` | one dynamite fired **per player per tick**, no cooldown | O(players) × fuse | Flak: **600 × N-players `DYNAMITE_X20` projectiles**, each a strength-20 explosion | Medium |
| F19 | `WitherStormEffect.java:57‑79`, `EndGateEffect.java:70‑88` | per-mob full Y-column scan with collision shapes | O(mobs × 384) | 160 × 768 and 80 × 768 collision-shape queries | Medium |
| F20 | 12 effects listed in §F20 | `explosionTick` entity/particle work with **no `ServerLevel` guard** | — | duplicated allocation on the logical client | Medium |
| F21 | `ImprovedExplosion.java:270,276` | `setBlock(..., 3)` for every block of a bulk annihilation | O(blocks) | at size 230 (H-Bomb) hundreds of thousands of neighbour-update cascades | Medium |
| F22 | `LevelEvents.java:112‑157` | biome overwrite + full chunk resync packet, per player, **every tick** | O(25 chunks × 1536) | 38 400 biome get/set + up to 25 full `ClientboundLevelChunkWithLightPacket` per player per tick | Medium |

---

## F1 — HydrogenBombBombEffect: 53 M block reads in one tick (worst hot spot in the mod)

`tntmod/src/main/java/luckytnt/tnteffects/projectile/HydrogenBombBombEffect.java:51`

```java
ExplosionHelper.doModifiedSphericalExplosion(ent.getLevel(), ent.getPos(), 250, new Vec3(1f, (2f/3f), 1f), new IForEachBlockExplosionEffect() {
    @Override
    public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
        // Both cheap rejects (radius + the 25% roll) now run before anything is allocated or read.
        if(distance > 250 || Math.random() >= 0.25f) {
            return;
        }
```

The comment is misleading: the reject *inside the callback* is too late. The allocation and the
world read already happened in the helper, `TntLib/src/main/java/luckytntlib/util/explosions/ExplosionHelper.java:160-166`:

```java
for(double offZ = -radius * scaling.z; offZ <= radius * scaling.z; offZ++) {
    double distance = Math.sqrt(offX * offX / scaling.x + offY * offY / scaling.y + offZ * offZ / scaling.z);
    if(distance <= radius) {
        BlockPos pos = new BlockPos(cx + (int)offX, cy + (int)offY, cz + (int)offZ);
        BlockState state = level.getBlockState(pos);
        blockEffect.doBlockExplosion(level, pos, state, distance);
    }
}
```

Arithmetic:

* x loop `-250..250` = 501 values, y loop `-166..166` = 334, z loop `-250..250` = 501.
* The x and y loops *are* culled (`ExplosionHelper.java:153,157`), leaving the ellipse
  `x² + 1.5y² ≤ 62500` → `π·250·204.1 ≈ 160 300` surviving (x, y) columns.
* **The z loop is not culled at all** — it always runs its full 501 iterations, each doing three
  divisions and a `Math.sqrt`: **160 300 × 501 = 80.3 M** inner iterations.
* Of those, the ones inside the ellipsoid are `4/3·π·250·204.1·250 =` **53.4 M**, each performing
  `new BlockPos` + `Level.getBlockState` + a callback that calls `Math.random()`.
* Spatial extent 500 × 500 blocks ⇒ **~1024 chunks** pulled to `ChunkStatus.FULL` synchronously by
  `getBlockState`, most of which are outside the player's view distance and would otherwise never
  have been generated.

At an optimistic 20 ns per `getBlockState`+alloc this is **>1 second of frozen server tick**, and
much worse when chunk generation is triggered.

**Fix.** Three independent changes, largest first:

1. Derive the z span in closed form like `ImprovedExplosion.forEachShellCell` already does:
   `zMax = floorSqrt(scaling.z · (radius² − x²/scaling.x − y²/scaling.y))`, then iterate
   `-zMax..zMax` — removes the 27 M wasted iterations and every `Math.sqrt`.
2. Push the `Math.random() >= 0.25` roll into the traversal (or take a
   `IBlockExplosionCondition`-style predicate that runs *before* the world read). 75 % of 53 M reads
   disappear for free.
3. The effect only ever writes to `pos.above()` of a block whose up-face is full — that is a surface
   operation. Replace the whole volumetric sweep with a column walk driven by
   `level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)` over the r=250 disc: **196 350 columns**
   instead of 53.4 M cells, a ~270× reduction. Guard each column with
   `level.isLoaded(pos)` so unloaded terrain is skipped rather than generated.

---

## F2 — TsarBombaBombEffect: 54.5 M block reads in one tick

`tntmod/src/main/java/luckytnt/tnteffects/projectile/TsarBombaBombEffect.java:64-101`

```java
for(int offX = -300; offX <= 300; offX++) {
    int dx2 = offX * offX;
    for(int offY = -300 / 3; offY <= 300 / 3; offY++) {
        int remaining = 90000 - dx2 - offY * offY;
        ...
        for(int offZ = -zMax; offZ <= zMax; offZ++) {
            BlockPos pos = new BlockPos(baseX + offX, baseY + offY, baseZ + offZ);
            BlockState state = level.getBlockState(pos);
            if(state.getBlock().getExplosionResistance() <= 200) {
                int d2 = dx2 + offY * offY + offZ * offZ;
                if(d2 <= 22500 && (state.isAir() || state.getDestroySpeed(level, pos) <= 0.2f)) {
```

The comment above the loop ("only the cells actually inside the r=300 sphere are visited") is
literally true and does not help — the sphere *is* the problem.

Arithmetic: the visited set is the r=300 ball clipped to `|y| ≤ 100`:

```
V = ∫_{-100}^{100} π(90000 − y²) dy = π(90000·200 − 2·100³/3) = π·17 333 333 ≈ 54.45 M cells
```

Each is one `new BlockPos` + one `getBlockState`. Footprint 600 × 600 blocks ⇒ **~1444 chunks**
force-loaded/generated.

What the loop actually *does* with those 54.45 M reads:

* nuclear-waste placement, gated on `d2 <= 22500` — i.e. only r ≤ 150, **12.5 % of the volume** —
  and only on blocks sitting on a sturdy face, i.e. surface blocks only;
* leaf removal, which is the only thing that needs the 150 < r ≤ 300 shell.

**Fix.**

* Nuclear waste: iterate the r=150 *disc* (70 686 columns) with
  `getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)` instead of the r=150 ball
  (14.1 M cells) — a 200× cut, and it places waste on exactly the same "block above a sturdy face"
  positions.
* Leaf removal: iterate chunk sections, not blocks. Get each `LevelChunk` in the r=300 disc
  (`(600/16)² ≈ 1444` chunks), skip any that is not already loaded via `level.isLoaded`, and skip
  whole `LevelChunkSection`s with `section.maybeHas(state -> state.is(BlockTags.LEAVES))` — a leafless
  section is rejected with one palette scan instead of 4096 `getBlockState` calls.
* Both writes use flag 3; the leaf clear should be `Block.UPDATE_CLIENTS` (see F21).

---

## F3 — ResetTNTEffect: 8.1 M-iteration cube, twice, plus a 16.8 MB array per entity

`tntmod/src/main/java/luckytnt/tnteffects/ResetTNTEffect.java:93-113` (save) and `:133-150` (restore)

```java
for(int offX = -RADIUS; offX <= RADIUS; offX++) {
    for(int offY = RADIUS; offY >= -RADIUS; offY--) {
        for(int offZ = -RADIUS; offZ <= RADIUS; offZ++) {
            if(offX * offX + offY * offY + offZ * offZ > RADIUS_SQ) {
                continue;
            }
            mutable.set(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
            BlockState state = level.getBlockState(mutable);
```

`RADIUS = 100`, so:

* cube iterations `201³ = 8 120 601` per pass; the sphere test rejects 48 % of them but the rejection
  is done *per cell* instead of by deriving the z span — 3.9 M pure-waste iterations per pass;
* accepted cells `4/3·π·100³ = 4 188 790` → **4.19 M `getBlockState` on save and another 4.19 M on
  restore**, plus up to 4.19 M `setBlock(..., 3)` on restore;
* `getSphereSize()` (`:157-172`) runs the same `201³` triple loop a third time (integer-only, once
  per JVM);
* `snapshotStates` is `new int[4 188 790]` = **16.8 MB, live for the entire 2400-tick (2 minute) fuse**,
  per Reset TNT entity. The class comment correctly notes this is down from ~200 MB, but 16.8 MB × N
  concurrent Reset TNTs is still a heap problem, and it is a single humongous array (G1 humongous
  allocation, ≥ region size).

The comment on `explosionTick` about the client-side guard is correct and holds.

**Fix.**

* Derive the z range once per (x, y) column (`zMax = floorSqrt(RADIUS_SQ - x² - y²)`, the pattern
  already used in `ImprovedExplosion.forEachShellCell:631`). Kills 3.9 M iterations per pass and makes
  `getSphereSize()` a closed-form count.
* Store per chunk-section, not per block: for each of the ~1300 sections the sphere touches, keep a
  copy of the section's `PalettedContainer` (`section.getStates().copy()`). That is the same data in
  the game's own compressed form (a stone-only section costs a few bytes instead of 4096 ints), and
  restore becomes a section-level swap plus one `ClientboundLevelChunkWithLightPacket` per chunk
  instead of 4.19 M individual `setBlock` calls.
* If the per-block form is kept, restore should use `Block.UPDATE_CLIENTS` — the whole sphere is
  rewritten in one pass, so the neighbour updates from flag 3 notify blocks that are overwritten
  microseconds later.

---

## F4 — `ExplosionHelper.doTopBlockExplosionForAll`: the optimized copy was never back-ported to the library

`TntLib/src/main/java/luckytntlib/util/explosions/ExplosionHelper.java:338-363`

```java
for(int offY = yMax; offY >= -yMax; offY--) {
    double distance = Math.sqrt(xzSqr + (double)offY * offY);
    BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
    BlockState state = level.getBlockState(pos);
    BlockPos below = pos.below();
    BlockState belowState = level.getBlockState(below);
    if((belowState.isCollisionShapeFullBlock(level, below) || belowState.isFaceSturdy(level, below, Direction.UP)) && (state.isAir() || (!state.isCollisionShapeFullBlock(level, pos) && state.getBlock().getExplosionResistance() == 0) || state.is(BlockTags.FLOWERS) || state.canBeReplaced(new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)))) {
```

This visits the **whole sphere volume**, not the surface, and per visited cell it does:

* 2 `new BlockPos` (`pos`, `pos.below()`) + 1 `new BlockPos` again inside `.below()`,
* 2 `getBlockState`,
* up to 2 collision-shape resolutions,
* and — for every solid block sitting on another solid block, i.e. essentially all underground rock —
  **a `new DirectionalPlaceContext(...)`**, which itself allocates a `BlockHitResult` and a `Vec3`,
  only for `canBeReplaced` to return `false`.

`tntmod/src/main/java/luckytnt/tnteffects/projectile/PresentMeteorEffect.java:93-150` already contains
a hand-optimized private copy (`forEachTopBlock`) whose own javadoc identifies exactly this
allocation as "what actually mattered". **That fix was never applied to the shared helper**, so every
other caller still pays it:

| caller | radius | sphere cells | context allocations (rough) |
|---|---|---|---|
| `WinterTNTEffect.java:36` | 150 | 14.1 M | ~10 M |
| `StoneColdEffect.java:81` | 130 | 9.2 M | ~6 M |
| `FirestormTNTEffect.java:66` | 50 | 524 k | ~350 k |
| `SnowstormTNTEffect.java:42` | 50 | 524 k | ~350 k |
| `FireTNTEffect.java:27`, `SnowTNTEffect.java:26` | `strength` | varies | varies |

`doTopBlockExplosion` (`:262-288`) and its condition overload (`:300-328`) have the same
`DirectionalPlaceContext` allocation, mitigated only by the `break` on the first hit per column.

**Fix.** Move `PresentMeteorEffect.forEachTopBlock` into `ExplosionHelper` and make
`doTopBlockExplosionForAll` / `doTopBlockExplosion` delegate to it (reuse two `MutableBlockPos`,
carry the state read one block down into the next iteration, and only build the
`DirectionalPlaceContext` when `!isCollisionShapeFullBlock || state.canBeReplaced()`). Then delete
the private copy. Additionally, start each column at
`getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)` rather than at `+yMax`: the callers want surface
blocks, and everything above the heightmap is guaranteed air.

---

## F5 — JungleTNTEffect / AtlantisEffect: two full-volume sweeps of the same sphere

`tntmod/src/main/java/luckytnt/tnteffects/JungleTNTEffect.java:42-60`

```java
replaceNonSolidBlockOrVegetationWithAir(ent, 150, 99, true);

ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() { ... });
```

`replaceNonSolidBlockOrVegetationWithAir` (`:89-122`) does correctly reject before allocating — the
comment holds — but it is still a full r=150 ball: `4/3·π·150³ = 14 137 167` cells. The
`doSphericalExplosion` immediately after it re-reads **the same 14.1 M positions**. Total
**~28.3 M `getBlockState` in one tick**, over a 300 × 300 area = ~361 chunks force-resolved.

`AtlantisEffect.java:119` + `:121` is the same double-sweep at r=100 → **8.4 M**. Worse, the Atlantis
callback (`:125-128`) starts with `if(dy < 0 || dy > 50) return;`, so half of the 4.19 M reads in the
second sweep are discarded on the first line.

**Fix.**

* Merge the two passes into one traversal. Both callbacks are position-local and their conditions
  are mutually exclusive on the same block (`replaceNonSolid...` only touches non-solid/vegetation,
  the grass pass only touches blocks whose up-face is full), so a single sweep with both bodies
  produces identical output at half the reads.
* The second (grass placement) pass is a pure surface operation — hoist it to a heightmap-driven
  column walk over the r=150 disc: **70 686 columns instead of 14.1 M cells**.
* For Atlantis, pass the y band into the traversal (`doCylindricalExplosion` with `radiusY = 50`
  centred correctly) instead of rejecting inside the callback.

---

## F6 — `LevelEvents.getTopBlock`: a 384-step column scan used as a heightmap, in a per-tick loop

`tntmod/src/main/java/luckytnt/event/LevelEvents.java:262-291`

```java
for(int offY = level.getMaxY(); offY >= level.getMinY(); offY--) {
    BlockPos pos = new BlockPos(Mth.floor(x), offY, Mth.floor(z));
    BlockPos posUp = new BlockPos(Mth.floor(x), offY + 1, Mth.floor(z));
    BlockState state = level.getBlockState(pos);
    BlockState stateUp = level.getBlockState(posUp);
```

Per call: up to 384 iterations, **2 `BlockPos` allocations and 2 `getBlockState` per iteration**
(the same position is read twice — as `pos` in step *n* and as `posUp` in step *n+1* — so half the
reads are pure duplication). Typical overworld terrain is found after ~256 steps.

`BlackHoleTNTEffect.java:93-95` documents replacing this call with
`sLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ...)`. That fix was applied to exactly
one call site. The remaining callers:

**Sustained, per player, per tick** — `LevelEvents.java:186-195` (heat death):

```java
for(int offX = -30; offX < 30; offX += 2) {
    for(int offZ = -30; offZ < 30; offZ += 2) {
        int posY = getTopBlock(sPlayer.level(), sPlayer.getX() + offX, sPlayer.getZ() + offZ, true);
```

30 × 30 = **900 column scans per player per server tick**, ≈ 900 × 256 × 2 = **460 800 `getBlockState`
and 921 600 `BlockPos` allocations per player per tick**, for as long as the heat-death disaster is
running (`1000 × MAXIMUM_DISASTER_TIME` ticks). Lines `:158-185` add two more `getTopBlock` loops
(and allocate `new Random()` four times per iteration, `:159,160,175,176`).

**One-shot but huge:**

* `AetherTNTEffect.java:56-73` — 31 417 in-circle columns × ~256 steps × 2 = **~16 M `getBlockState`**
  in the detonation tick. The same loop also calls
  `ent.getLevel().registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE)` (`:63`) **inside the
  inner loop**, 31 417 registry lookups for a loop-invariant value.
* `FlowerForestTNTEffect.java:66` — 17 671 columns → **~9.0 M**.
* `PlantationTNTEffect.java:66` — 5 545 columns → **~2.8 M**.
* `WorldOfWoolsEffect.java:140,173` — ~90 calls.

**Fix.**

* Replace the body of `getTopBlock` with
  `level.getHeight(ignoreLeaves ? Heightmap.Types.MOTION_BLOCKING_NO_LEAVES : Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z)) - 1`.
  That is an O(1) array read of a value the chunk already maintains. If the extra
  `getExplosionResistance() < 200` condition must be preserved, start the walk at the heightmap value
  and scan *down* from there — a handful of steps instead of 384.
* Failing that, at minimum: reuse one `MutableBlockPos`, carry `stateUp` from the previous iteration
  (halves the reads), and start at `level.getHeight(...)` instead of `level.getMaxY()`.
* Hoist the registry lookup out of `AetherTNTEffect`'s loop.

---

## F7 — SinkholeTNTEffect: a 300 k-cell cube re-swept 75 times, with an NBT read per cell

`tntmod/src/main/java/luckytnt/tnteffects/SinkholeTNTEffect.java:29-46`

```java
if(ent.getTNTFuse() <= 150 && !ent.getLevel().isClientSide() && ent.getTNTFuse() % 2 == 0) {
    for(int offX = -33; offX <= 33; offX++) {
        for(int offY = -33; offY <= 33; offY++) {
            for(int offZ = -33; offZ <= 33; offZ++) {
                double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ) + Math.random() * 4D - 2D;
                BlockPos pos = new BlockPos(Mth.floor(ent.x() + offX), Mth.floor(ent.y() + offY + ent.getPersistentData().getIntOr("depth", 0)), Mth.floor(ent.z() + offZ));
                if(distance <= 30 && ent.getLevel().getBlockState(pos).getBlock().getExplosionResistance() < 200) {
                    ent.getLevel().getBlockState(pos).getBlock().wasExploded((ServerLevel)ent.getLevel(), pos, ImprovedExplosion.dummyExplosion(ent.getLevel()));
                    ent.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
```

Per firing (`67³ = 300 763` iterations), *before* the distance test:

* `Math.sqrt` + 2 `Math.random()` calls,
* `ent.getPersistentData()` — a `SynchedEntityData.get()` plus a `CompoundTag` map lookup — **300 763
  times for a value that is constant for the whole firing**,
* `new BlockPos`.

Then for the ~113 097 cells in the r=30 sphere: `getBlockState` is called **twice** on the same
position (lines 35 and 36) and `setBlock` runs with flag 3.

Over the 75 firings (fuse 150 → 0, every 2 ticks): **22.6 M iterations, ~8.5 M `setBlock` flag 3**.
Successive firings also re-clear blocks that a previous firing already turned into air.

**Fix.**

1. Hoist `getPersistentData().getIntOr("depth", 0)`, `ent.x/y/z`, `Blocks.AIR.defaultBlockState()`
   and `ImprovedExplosion.dummyExplosion(level)` out of the loop (four loop-invariants).
2. Cull by squared distance and derive the z span: the RNG jitter is `±2`, so any cell with
   `d² > 32²` can never pass and any with `d² < 28²` always passes — only the 28..32 shell needs the
   RNG at all.
3. Only sweep the *new* shell each firing, as `MidasTNTEffect.java:38-42` already does for its
   growing radius. `depth` decreases by 1 per firing, so the newly reached volume is a thin slab, not
   the whole ball — this alone is a ~30× cut.
4. Read the state once, and use `Block.UPDATE_CLIENTS`.

---

## F8 — WinterTNTEffect: 10 200 unbounded, unguarded projectile spawns

`tntmod/src/main/java/luckytnt/tnteffects/WinterTNTEffect.java:48-54`

```java
@Override
public void explosionTick(IExplosiveEntity ent) {
    for(int i = 0; i <= 50; i++) {
        SnowySnowball ball = new SnowySnowball(ent.getLevel(), ...);
        ball.setDeltaMovement(...);
        ent.getLevel().addFreshEntity(ball);
    }
}
```

`explosionTick` runs **every tick on both logical sides** (`PrimedTNTEffect.baseTick:66`), and the
fuse is 200 (`:67`). No `ServerLevel` guard, no cap, no throttle:

* 51 × 200 = **10 200 `SnowySnowball` entities** spawned server-side over the effect;
* the identical 10 200 are constructed and thrown away on the logical client in single-player;
* a snowball falling from `y+30` at ~0.3 blocks/tick lives ~100 ticks, so the **steady-state
  population is ~5 000 concurrent projectiles**, each running collision ray-casts every tick.

This is the same class of problem the previous pass fixed for `SolarEruptionEffect.java:21`,
`SpammingTNTEffect.java:29`, `EndTNTEffect.java:39` (`MAX_ENDERMEN`) and `BlackHoleTNTEffect.java:29`
(`MAX_LIVE_FALLING_BLOCKS`) — Winter TNT was missed.

**Fix.** `if(!(ent.getLevel() instanceof ServerLevel)) return;`, fire every 4th tick with 12 balls
(same visual density, 4× fewer entities), and cap the live population by counting
`SnowySnowball` in a small AABB (the pattern in `BlackHoleTNTEffect.java:47,83`).

---

## F9 — DeathRayEffect: a solid cube scanned to find a 3-block-thick shell

`tntmod/src/main/java/luckytnt/tnteffects/DeathRayEffect.java:39-62`

```java
for(int offX = -size; offX <= size; offX++) {
    for(int offY = size; offY >= -size; offY--) {
        for(int offZ = -size; offZ <= size; offZ++) {
            double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
            if(distance <= size && distance > size - 3) {
                BlockPos pos = new BlockPos((int)ent.getPos().x, (int)ent.getPos().y, (int)ent.getPos().z).offset(offX, offY, offZ);
```

`size` starts at 1 and is incremented once per tick for the last 80 ticks of the fuse (`:64-66`).

* Iterations in the last tick: `161³ = 4 173 281`.
* Total across the 80 ticks: `Σ_{s=1..80} (2s+1)³ ≈ 2·80⁴ =` **81.9 M iterations**.
* Cells actually used at `s = 80`: the shell `77 < d ≤ 80` ≈ `4π·80²·3 ≈ 241 000` — so
  **94 % of the work is a `Math.sqrt` followed by a rejection**.
* Each accepted cell allocates **two** `BlockPos` (`new BlockPos(...)` then `.offset(...)`).
* `ent.getPos()` is called three times per accepted cell.
* Up to three `Math.random()` calls per accepted cell (`:49,51`).

**Fix.** Iterate the shell directly, exactly as `ImprovedExplosion.forEachShellCell:617-650` does:
for each (x, y) compute `zOuter = floorSqrt(size² − x² − y²)` and `zInner = ceilSqrt((size−3)² − x² − y²)`
and walk only `[-zOuter, -zInner] ∪ [zInner, zOuter]`. Hoist `ent.getPos()` and use one
`MutableBlockPos`. That turns 4.17 M iterations/tick into ~241 k.

---

## F10 — HeatWave / StoneCold: full-volume spheres for surface effects

`tntmod/src/main/java/luckytnt/tnteffects/HeatWaveEffect.java:32`

```java
ExplosionHelper.doSphericalExplosion(ent.getLevel(), ent.getPos(), 150, new IForEachBlockExplosionEffect() {
    public void doBlockExplosion(Level level, BlockPos pos, BlockState state, double distance) {
        if(state.isAir() && BlockSurviveChecks.canFirePlaceAt(state, level, pos)) {
```

`4/3·π·150³ = 14.1 M` `getBlockState` calls (plus 14.1 M `new BlockPos` inside the helper at
`ExplosionHelper.java:125`) to place fire, which by definition only happens on the *top* face of a
solid block. Below the surface every cell is a guaranteed reject.

`StoneColdEffect.java:64` sweeps r=130 (**9.2 M**), and its first branch is gated on `distance <= 90`
— the 90 < d ≤ 130 shell (6.1 M cells) exists only for the "water → ice" branch. `:81` then adds a
`doTopBlockExplosionForAll(130)` pass, priced per F4 (**another 9.2 M cells with per-cell
`DirectionalPlaceContext` allocations**).

**Fix.** Both are surface operations: walk the r=150/r=130 **disc** (70 686 / 53 093 columns) using
`getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)`. For StoneCold's water→ice branch, which does need
sub-surface reach, use `Heightmap.Types.OCEAN_FLOOR` as the lower bound of the column walk rather
than scanning the full ball.

---

## F11 — FlowerForest / Plantation: `getTopBlock` per column, plus a second full column scan

`tntmod/src/main/java/luckytnt/tnteffects/FlowerForestTNTEffect.java:57-72`

The radius pre-check the comment describes is present and correct, but each surviving column still
calls `LevelEvents.getTopBlock` (F6): 17 671 columns × ~256 steps × 2 reads ≈ **9.0 M `getBlockState`
and 9.0 M `BlockPos`** in one tick. The `doCylindricalExplosion(75, 75)` above it
(`:35`) visits `π·75²·151 = 2.67 M` cells but the callback rejects everything with
`distance > 50` (`:40`) — the horizontal distance, so **1.48 M of those reads are unconditionally
discarded**; the helper should have been called with `radius = 50`.

`PlantationTNTEffect.java:57-71` — 5 545 columns × `getTopBlock` ≈ **2.8 M**.
`PlantationTNTEffect.java:79-130` — a second pass over 5 281 columns doing its own manual Y scan,
starting at `baseY + 320` (i.e. `ent.y() + 320`, above the world ceiling — the first ~64 iterations
are guaranteed out-of-bounds returns) and running until it finds ground: **~1.6 M `getBlockState`**.
`placeCropsAndFarmland` (`:148-180`) allocates **up to two `new Random()` per crop placed**
(`:155,158,159,160,161,169,170`) — thousands of `Random` instances, each contending on the global
seed uniquifier.

**Fix.** `getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)` for both column loops; call the
cylinder helper with the radius the callback actually uses; hoist a single
`RandomSource random = level.getRandom()` and use it everywhere in `placeCropsAndFarmland`.

---

## F12 — GrandeFinaleEffect: 1500 entities in one tick, on both logical sides

`tntmod/src/main/java/luckytnt/tnteffects/GrandeFinaleEffect.java:63-113`

```java
@Override
public void explosionTick(IExplosiveEntity ent) {
    Level level = ent.getLevel();
    ...
    if(ent.getTNTFuse() == 0) {
        BlockState[] colors = concreteStates();
        for(int count = 0; count < 1000; count++) {
            ...
            block = FALLING_BLOCK_CONSTRUCTOR.newInstance(level, ent.x(), ent.y(), ent.z(), template);
            ...
            level.addFreshEntity(block);
        }
        for(int count = 0; count < 500; count++) {
            PrimedLTNT tnt = EntityRegistry.TNT.get().create(level, EntitySpawnReason.MOB_SUMMONED);
            ...
        }
    }
}
```

The constructor caching (`:37-45`) and the concrete-state caching (`:118-141`) from the previous pass
are real wins and hold. What remains:

* **No `ServerLevel` guard on `explosionTick`.** In single-player the client builds all 1000
  `FallingBlockEntity` (via reflective `newInstance`, which cannot be inlined) and all 500
  `PrimedLTNT`, then `addFreshEntity` discards them. The whole burst is paid twice.
* 500 `PrimedLTNT` with fuses spread over 80–180 ticks means **500 separate strength-4 explosions**,
  each running a full `ImprovedExplosion` ray-cast, within a ~5-second window. This is by design but
  is the dominant cost of the effect and deserves a config cap.
* `ent.getLevel()` is re-resolved ~10 times per tick (`:69,71,76,77,78,84`) although `level` is
  already in scope at `:65`.
* `:84-85` calls `setBlock(..., 3)` twice **every tick** for 440 ticks at the TNT's own position —
  880 block updates that are almost always no-ops.

**Fix.** Guard `explosionTick` with `instanceof ServerLevel`; use the `level` local; only clear the
two blocks when `level.getBlockState(...)` is not already air; make the 1000/500 counts config
values.

---

## F13 — BlackHoleTNTEffect: the AABB was halved but not shrunk, and 2000 entities is still 2000 entities

`tntmod/src/main/java/luckytnt/tnteffects/BlackHoleTNTEffect.java:43-48`

```java
if(ent.getTNTFuse() < 350 && ent.getTNTFuse() > 0 && ent.getLevel() instanceof ServerLevel sLevel) {
    AABB range = new AABB(ent.x() - 100, ent.y() - 100, ent.z() - 100, ent.x() + 100, ent.y() + 100, ent.z() + 100);
    int liveBlocks = 0;
    for(Entity target : sLevel.getEntities((Entity)ent, range)) {
```

The server guard and the single-pass merge are real and correct. What is left:

* The box is 200 × 200 × 200 = **~13³ = 2197 entity sections walked every tick for 350 consecutive
  ticks** (~769 000 section visits per detonation). The falling blocks are spawned within ±75
  horizontally (`:87-88`) and the pull is meaningless past ~50 blocks, so the box is oversized in
  every dimension; the y extent in particular usually spans the whole world height.
* `MAX_LIVE_FALLING_BLOCKS = 2000` (`:29`). 2000 `FallingBlockEntity` each run `move()` with
  collision resolution and are tracked/synced to every nearby player, every tick, for up to 350
  ticks. This is the single largest sustained cost of the effect, and 2000 is still an extreme cap.
* `spawnParticles` (`:127-143`) emits **150 particles per tick for 350 ticks = 52 500 particles**
  client-side, with a `new DustParticleOptions` allocated per particle (`:140`) even though the
  options are immutable and identical — the same allocation `GlobalDisasterEffect.java:20` already
  hoists into a `static final`.

**Fix.** Shrink the box to ±80 horizontally / ±48 vertically (clamped to build height, as
`ImprovedExplosion.entityBoundingBox():839-844` does); drop `MAX_LIVE_FALLING_BLOCKS` to ~400; hoist
the `DustParticleOptions` to a `static final` and drop the particle count to ~40/tick.

---

## F14 — ContinentalDriftEffect: 43 000 flag-3 block writes in a single tick, eight times

`tntmod/src/main/java/luckytnt/tnteffects/ContinentalDriftEffect.java:102-122`

```java
for(int i = 0; i < length; i++) {
    int lineX = startX + Mth.floor(i * vec.x);
    int lineZ = startZ + Mth.floor(i * vec.z);
    for(int offX = -10; offX <= 10; offX++) {
        ...
        for(int offZ = -10; offZ <= 10; offZ++) {
            double threshold = chance[row + offZ];
            if(Double.isNaN(threshold) || Math.random() <= threshold) {
                continue;
            }
            int z = lineZ + offZ;
            BlockPos pos1 = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1, z);
            if(level.getBlockState(pos1).getBlock().getExplosionResistance() <= 100) {
                level.setBlock(pos1, air, 3);
            }
```

The chance-table hoist and the heightmap lookup are genuine improvements. The remaining cost is the
raw volume:

* `carveRift` is called twice per firing with `length` 160 and 60 (`:65-66`), i.e. 220 line steps.
* 220 × 441 = **97 020 cells** examined per firing.
* Acceptance rate per in-range cell: ~161 cells at 90 %, ~88 at 50 %, ~68 at 10 % → **~196 carves per
  line step ⇒ ~43 000 `getHeight` + `getBlockState` + `setBlock(..., 3)` in one tick.**
* The firing repeats every 60 ticks from fuse 400 to 0 (`:58`) — **7–8 such spikes**.
* Successive line steps overlap heavily (a unit-length direction vector advances ~1 block per step
  while the stamp is 21 wide), so the same column is carved ~20 times over the rift.

**Fix.** Deduplicate the carved columns: accumulate `(x, z)` into a `LongOpenHashSet` over the whole
rift and write each column once — a ~20× cut in `setBlock` calls with an identical result. Use
`Block.UPDATE_CLIENTS`; the rift is a bulk terrain edit and the neighbour updates mostly notify
blocks being carved in the same pass. Also stagger the two `carveRift` calls onto different ticks
(`fuse % 60 == 0` and `fuse % 60 == 30`) to halve the spike height.

---

## F15 — `ExplosionHelper.doCubicalExplosion`: no culling, a `sqrt` per cell, up to 8.1 M cells

`TntLib/src/main/java/luckytntlib/util/explosions/ExplosionHelper.java:179-195`

```java
for(int offX = -radius; offX <= radius; offX++) {
    final long xSqr = (long)offX * offX;
    for(int offY = -radius; offY <= radius; offY++) {
        final long xySqr = xSqr + (long)offY * offY;
        for(int offZ = -radius; offZ <= radius; offZ++) {
            double distance = Math.sqrt(xySqr + (double)offZ * offZ);
            BlockPos pos = new BlockPos(cx + offX, cy + offY, cz + offZ);
            BlockState state = level.getBlockState(pos);
            blockEffect.doBlockExplosion(level, pos, state, distance);
```

Every cell of the cube gets a `Math.sqrt`, a `new BlockPos` and a `getBlockState`. Callers:

* `CustomTNTEffect.java:116, 231, 320` with `radius = 5 × CUSTOM_TNT_*_EXPLOSION_INTENSITY`. The
  config bound is 20 (`LuckyTNTConfigValues.java:26,29,32`), so **`radius = 100 ⇒ 201³ = 8 120 601`
  cells per detonation**. Each `CustomTNTEffect` level spawns 3 children (`:101-112`), and there are
  three configurable levels ⇒ up to **1 + 3 + 9 = 13 detonations ≈ 105 M cells**.
* `CubicTNTEffect.java:26` (used by `ChunkTNTEffect` at `radius = 50` ⇒ `101³ = 1.03 M`).

**Fix.** Most callbacks only use `distance` for a radius test — add a `doCubicalExplosion` overload
that passes squared distance (or no distance at all) so the `sqrt` disappears; reuse a
`MutableBlockPos`; and clamp `5 × intensity` — a 100-radius *cube* is 8.1 M blocks, which no
single-tick operation should ever touch. `ExplosionHelper.getBlocksInSphere/InCuboid/InCylinder`
(`:28-101`) are worse still — they build a `HashMap<BlockPos, BlockState>` of the whole volume
(≈ 4.19 M entries ≈ 400 MB at r=100). They are unused inside this repo but are public library API and
should at minimum carry a warning, or be changed to a callback form.

---

## F16 — `new Random()` inside hot loops

54 occurrences of `new Random()` remain across the mod. `java.util.Random()`'s no-arg constructor
seeds from a static `AtomicLong` via CAS, so allocating one per block is both an allocation and a
contended atomic.

The ones that are actually inside per-block loops:

* `MineralTNTEffect.java:112` — `int random = new Random().nextInt(7);` inside the r=40 replacement
  loop: **~38 000 allocations** in one tick.
* `PlantationTNTEffect.java:155,158,159,160,161,169,170` — up to **two per crop placed**, thousands
  of crops.
* `LevelEvents.java:159,160,175,176` — four per iteration of a loop that runs
  `1 + intensity/2` times **per player per tick**, plus `:209` once per TNT-rain spawn.
* `WorldOfWoolsEffect.java:204` (`randomColor()`) — once per sheep, called for every sheep in a
  200-wide AABB (`:180-183`).
* `DividingTNTEffect.java:52`, `ContinentalDriftEffect.java:44` — once per spawn, minor.

`AnimalKingdomEffect.java:89-91` shows the intended fix and its comment:
`RandomSource random = level.getRandom();`.

**Fix.** Replace all of them with a hoisted `level.getRandom()` (or a single hoisted `RandomSource`).
Mechanical and zero-risk.

---

## F17 — `RingTNTEffect.getFirstMotionBlockingBlock`: the loop has no `break`

`tntmod/src/main/java/luckytnt/tnteffects/RingTNTEffect.java:36-56`

```java
for(int offY = level.getMaxY(); offY >= level.getMinY(); offY--) {
    BlockPos pos = new BlockPos(Mth.floor(x), offY, Mth.floor(z));
    BlockPos posUp = new BlockPos(Mth.floor(x), offY + 1, Mth.floor(z));
    BlockState state = level.getBlockState(pos);
    BlockState stateUp = level.getBlockState(posUp);
    if(!blockFound) {
        if(!state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty() && stateUp.getCollisionShape(level, posUp, CollisionContext.empty()).isEmpty()) {
            blockFound = true;
            y = offY;
        }
    }
}
```

`blockFound` suppresses further *assignments* but the loop keeps running to `minY`. Every one of the
**384 iterations** unconditionally does 2 `new BlockPos`, 2 `getBlockState` and 2
`getCollisionShape` — including all the iterations after the answer is already known.

Cost: `RingTNTEffect.java:19` calls it 12 times; `EyeOfTheSaharaEffect.java:23` calls it **60 times**
(`for angle 0..360 step 6`) ⇒ 60 × 384 × 2 = **~46 000 collision-shape resolutions** in one tick, for
60 answers.

**Fix.** `return offY;` at the hit (or, better, `level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1`,
which is what "first motion-blocking block" literally means and is an O(1) lookup).

---

## F18 — Flak / Cannon / Turret: one projectile per player per tick, forever

`tntmod/src/main/java/luckytnt/tnteffects/FlakTNTEffect.java:18-34` (and the identical
`CannonTNTEffect.java:18-34`, `TurretTNTEffect.java:18-34`)

```java
if(!level.isClientSide() && entity.getTNTFuse() <= 600) {
    AABB range = new AABB(entity.getPos().add(-150, -150, -150), entity.getPos().add(150, 150, 150));
    for(Player player : level.players()) {
        ...
        ItemRegistry.DYNAMITE_X20.get().shoot(level, entity.x(), entity.y() + 0.5f, entity.z(), dir, 3, entity.owner());
    }
}
```

The switch from `getEntitiesOfClass` to `level.players()` (previous pass) is correct and is a real
win. What remains is the firing rate: there is no cooldown, so the effect launches **one projectile
per player per tick**:

| effect | active ticks | projectile | total per player |
|---|---|---|---|
| `FlakTNTEffect` (fuse 800) | 600 | `DYNAMITE_X20` | **600 strength-20 explosions** |
| `CannonTNTEffect` (fuse 600) | 400 | `DYNAMITE_X5` | 400 |
| `TurretTNTEffect` (fuse 400) | 300 | `DYNAMITE` | 300 |

With 4 players that is 2400 `DYNAMITE_X20` from a single Flak TNT, each detonating an
`ImprovedExplosion` of size 20 on impact.

**Fix.** Add a fire interval (`if(entity.getTNTFuse() % 5 != 0) return;`) — a 5× reduction with no
perceptible change to the effect — and cap the number of in-flight projectiles per TNT.

---

## F19 — Per-mob full Y-column scans with collision shapes

`tntmod/src/main/java/luckytnt/tnteffects/WitherStormEffect.java:64-73`

```java
for(int y = ent.getLevel().getMaxY(); y >= ent.getLevel().getMinY(); y--) {
    BlockPos pos = new BlockPos(Mth.floor(ent.x() + offX), y, Mth.floor(ent.z() + offZ));
    BlockState state = ent.getLevel().getBlockState(pos);
    if(!Block.isFaceFull(state.getCollisionShape(ent.getLevel(), pos), Direction.UP) && Block.isFaceFull(ent.getLevel().getBlockState(pos.below()).getCollisionShape(ent.getLevel(), pos.below()), Direction.UP)) {
```

160 wither skeletons (`:57`) × ~256 column steps × 2 `getBlockState` + 2 `getCollisionShape`
(`pos.below()` is built twice per iteration) ⇒ **~82 000 collision-shape resolutions and ~123 000
`BlockPos` allocations** in the detonation tick. `ent.getLevel()` is resolved 6 times per iteration.

`EndGateEffect.java:70-88` is the same shape: 80 endermen × ~256 steps × 2 shapes ⇒ ~41 000.

**Fix.** `int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);` — one array read
instead of the scan. Hoist `level` into a local.

---

## F20 — `explosionTick` without a `ServerLevel` guard

`PrimedTNTEffect.baseTick` (`TntLib/.../PrimedTNTEffect.java:66,97`) calls `explosionTick` on **both
logical sides**. In single-player every unguarded body therefore runs twice, and everything it
allocates on the client is immediately discarded (`addFreshEntity` is effectively a no-op on the
client `Level`).

Effects whose `explosionTick` allocates entities or writes blocks with no guard:

| file | per firing | firings | wasted client-side |
|---|---|---|---|
| `GrandeFinaleEffect.java:63` | 1500 entities (see F12) | 1 | 1500 (1000 via reflection) |
| `WinterTNTEffect.java:48` | 51 snowballs | 200 | **10 200** |
| `AirStrikeEffect.java:16` | 6 bombs | 64 | 384 |
| `MeteorShowerEffect.java:16` | 6 meteors | 64 | 384 |
| `MeteorStormEffect.java:16` | 6 meteors | 18 | 108 |
| `PompeiiEffect.java:20` | 30 projectiles **+ 30 identical `playSound` from one position** | 10 | 300 entities, 300 sound packets |
| `EruptingTNTEffect.java:20` | 1 projectile + 1 sound | ~20 | 20 |
| `TheRevolutionEffect.java:18` | 1 spiral TNT + 1 sound | 10 | 10 |
| `SupernovaEffect.java:19`, `ChunkTNTEffect.java:19` | 1 lightning bolt | 1 | 1 |
| `PhantomTNTEffect.java:22` | a full 384-step Y scan (see below) | 1 | 384 reads |
| `SpiralTNTEffect`, `HelixEffect`, `StaticTNTEffect`, `LevitatingTNTEffect`, `FloatingTNTEffect`, `DisintegratingTNTEffect`, `CityFireworkEffect`, `ItemFireworkEffect`, `CustomFireworkEffect`, the `*FireworkEffect` family | movement/particle only | — | minor |

`PhantomTNTEffect.java:27-34` additionally has the F17 bug — `foundBlock` guards the assignment but
the loop runs all 384 steps.

Also note `PompeiiEffect.java:31`: `playSound` is inside the 30-iteration spawn loop, broadcasting 30
identical explosion sounds from the same coordinate. `SolarEruptionEffect.java:30-31` documents
having fixed exactly this pattern; Pompeii was missed.

**Fix.** `if(!(entity.getLevel() instanceof ServerLevel)) return;` at the top of every
entity-spawning `explosionTick`; move `playSound` out of the spawn loop in `PompeiiEffect`; add a
`break` (or heightmap lookup) in `PhantomTNTEffect`.

---

## F21 — `setBlock(..., 3)` for bulk terrain annihilation

`TntLib/src/main/java/luckytntlib/util/explosions/ImprovedExplosion.java:265-271`

```java
for(IntIterator iterator = blocks.iterator(); iterator.hasNext();) {
    BlockPos blockPos = decodeBlockPos(iterator.nextInt(), tntX, tntY, tntZ);
    if(serverLevel != null) {
        level.getBlockState(blockPos).getBlock().wasExploded(serverLevel, blockPos, this);
    }
    level.setBlock(blockPos, air, 3);
}
```

Flag 3 = `UPDATE_NEIGHBORS | UPDATE_CLIENTS`. For a bulk annihilation where every neighbour is also
being deleted in the same pass, the neighbour half is pure waste: 6 `neighborChanged` dispatches per
block, each of which can schedule further block ticks and shape updates. For the Hydrogen Bomb
(`size = 230`) or Tsar Bomba (`size = 160`) that is on the order of **10⁵–10⁶ blocks × 6 neighbour
updates** in one tick.

`GlobalDisasterEffect.java:47-50` and `EndTNTEffect.java:63-69` already switched to
`Block.UPDATE_CLIENTS` with exactly this reasoning; the core explosion did not.

288 of the mod's 298 `setBlock` calls still use flag 3.

**Fix.** Use `Block.UPDATE_CLIENTS` (2) in `ImprovedExplosion.doBlockExplosion`, then issue a single
`level.blockUpdated(...)` pass over the *boundary* of the affected set (or simply let the next
neighbour interaction sort it out — vanilla's own `ServerExplosion` batches this). The same applies
to the bulk sweeps in F2, F3, F7, F10 and F14. `decodeBlockPos` also allocates a fresh `BlockPos` per
iteration in three separate loops (`:266,274`) — a `MutableBlockPos` overload would remove that.

---

## F22 — LevelEvents ice-age / heat-death: full chunk resync every tick

`tntmod/src/main/java/luckytnt/event/LevelEvents.java:112-133` (ice age) and `:138-157` (heat death,
identical body)

```java
for(double offX = -32; offX <= 32; offX += 16) {
    for(double offZ = -32; offZ <= 32; offZ += 16) {
        boolean needsUpdate = false;
        for(LevelChunkSection section : level.getChunk(new BlockPos(Mth.floor(x + offX), 0, Mth.floor(z + offZ))).getSections()) {
            for(int i = 0; i < 4; ++i) {
                for(int j = 0; j < 4; ++j) {
                    for(int k = 0; k < 4; ++k) {
                        if(section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container && section.getBiomes().get(i, j, k).value() != biome.value()) {
```

Per player, per tick:

* 5 × 5 = 25 chunks (many revisited: the `+= 16` step over `-32..32` gives 5 offsets, and
  `new BlockPos` is allocated for the chunk lookup **twice** per chunk, `:115` and `:128`);
* 24 sections × 64 biome cells = 1536 per chunk ⇒ **38 400 biome get/compare per player per tick**;
* `section.getBiomes()` is called **twice per cell** (once for the `instanceof`, once for the `get`) —
  76 800 virtual calls;
* if any cell differs, a full `ClientboundLevelChunkWithLightPacket` is sent — **the entire chunk's
  block data and light, up to 25 chunks per player per tick**. After the first tick every biome
  already matches so `needsUpdate` is false, but the 38 400 comparisons still run forever.

**Fix.** Track the set of already-converted `ChunkPos` in `LevelVariables` and skip them; hoist
`section.getBiomes()` into a local; reuse a `MutableBlockPos`; and run the conversion on a slow tick
(`level.getGameTime() % 20 == 0`) rather than every tick. The two blocks are byte-identical apart
from the biome constant and should be one helper method.

---

## Cross-cutting quick wins (low risk, mechanical)

1. **`new BlockPos` → `MutableBlockPos`** in every volumetric loop. `ExplosionHelper` allocates one
   (sometimes two, via `.below()`/`.above()`) per visited cell in all seven of its traversals
   (`:43, 67, 94, 125, 163, 189, 214, 246, 277+279, 315+317, 353+355`). At r=150 that is 14 M
   short-lived objects per call.
2. **Hoist `ImprovedExplosion.dummyExplosion(level)`** — still resolved per block in
   `SinkholeTNTEffect.java:36`, `FieryHellEffect.java:35,51,59`, `StoneColdEffect.java:70,75,87`,
   `PlantationTNTEffect.java:68,153,164,221,227`, `AtlantisEffect.java:131,142`,
   `JungleTNTEffect.java:56,113`. It is a static `WeakReference` deref plus an identity check, but it
   is called millions of times. `GlobalDisasterEffect.java:29` and `MineralTNTEffect.java:52` show
   the hoisted form.
3. **`Math.sqrt` where a squared compare suffices.** `ExplosionHelper.java:124,161,188,213,276,314,352`,
   `SinkholeTNTEffect.java:33`, `DeathRayEffect.java:42`, `MineralTNTEffect.java:81`,
   `AetherTNTEffect.java:58`, `KnockbackTNTEffect.java:44`, `ImprovedExplosion.java:379,487`
   (`distToCenterSqr` immediately followed by `Math.sqrt`, then only ever compared against a radius).
4. **`ent.getLevel()` / `ent.getPos()` / `ent.getPersistentData()` re-resolution inside loops.**
   Worst offenders: `SinkholeTNTEffect.java:34-38` (6 per cell, one of them an NBT lookup),
   `DeathRayEffect.java:44` (3 per cell), `WitherStormEffect.java:66-73` (6 per cell),
   `ToxicCloudEffect.java:49` (6 NBT reads per particle).
5. **`DustParticleOptions` is immutable** — hoist it to a `static final` as
   `GlobalDisasterEffect.java:20` already does. Still allocated per particle in
   `BlackHoleTNTEffect.java:140`, `TsarBombaBombEffect.java:107-128` (9 300 per call),
   `HydrogenBombBombEffect.java:80-101` (16 400 per call), `DeathRayEffect.java:76-78`,
   `WorldOfWoolsEffect.java:188`, `SupernovaEffect`, and the whole firework family.

---

## Files checked and found clean

`ImprovedExplosion.forEachShellCell` (closed-form shell iteration, correct),
`ImprovedExplosion.entityBoundingBox` (build-height clamped), `MidasTNTEffect` (shell-only growth),
`GlobalDisasterEffect`, `EndTNTEffect`, `SolarEruptionEffect`, `SpammingTNTEffect`,
`LightningStormEffect`, `KolaBoreholeTNTEffect`, `WorldOfWoolsEffect.placeRing`,
`PresentMeteorEffect.forEachTopBlock`, `EntityLivingEvent.onLivingTick` (early-out is correct),
`ToxicStoneBlock`, `EntityMixin`, `WastelandTNTEffect.doVaporizeExplosion` (rejects before allocating;
still O(r³) at r=75 ≈ 1.77 M cells, but the callback genuinely needs sub-surface reach).
