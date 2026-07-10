# PORT-STATUS — LuckyTNTMod (`tntmod/`) → Minecraft 26.2

Live status of the autonomous port. Law: `PORT-MOD-26.2.md`. Library `TntLib/` is
already ported — do not touch it.

## Toolchain / environment (ready)

- Gradle: `/opt/gradle-9.6.1/bin/gradle` (vendored, unpacked from `gradle-dist/`). NEVER run `./gradlew`.
- Java 25: `JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64`.
- Decompiled MC 26.2 sources unpacked to **`/opt/mc-src/`** (grep here for signatures; do NOT regenerate).
- Lib dependency: prebuilt jar `dist/fabric-luckytntlib-26.2-0.100.6.1.jar` via `implementation files(...)`.
- Build/compile command (run ONE at a time, never in parallel in this checkout):
  ```sh
  cd tntmod && JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 /opt/gradle-9.6.1/bin/gradle compileJava --no-daemon 2>&1 | tee /tmp/errors.txt
  ```

## Progress checklist (§5 areas) — ✅ COMPLETE

- [x] Step 0 — toolchain, build files, `port-rename.sh` first pass, genSources
- [x] Core (registry/block/item/entity/network/event) — compiles
- [x] Mixins — 7 re-verified against `/opt/mc-src/` (6 loaded, 1 disabled §9)
- [x] Client — renderers (render-state/`submit`), HUD overlay, config GUI
- [x] Sweep — tnteffects/*, feature/*, worldgen, recipe data JSON
- [x] `compileJava` GREEN — all 303 files compile
- [x] `build` GREEN — jar assembled, mixins applied, resources processed
- [x] **`runServer` GREEN — boots to `Done (0.411s)!` with ZERO `/ERROR]` lines**

Final result: server boots green on MC 26.2, all TNT registers, mixins apply, 355
recipe JSON migrated to the 26.2 plain-string format. One harmless mixin WARN remains
(`FireBlockMixin` `canBurn` @Unique discarded — vanilla `FireBlock` now defines it).

## Disabled content (§9)

- `registry/RenderLayerRegistry.java` — **init() body disabled** (kept in `/* */`). Fabric's
  `BlockRenderLayerMap`/`BlockRenderLayerMapImpl` API was removed in 26.x. Block render types
  are now declared in the block model JSON via `"render_type"` (e.g. `minecraft:cutout_mipped`).
  Affected blocks (need `render_type` in their data JSON — Agent D): CUSTOM_FIREWORK, XRAY_TNT,
  OBSIDIAN_ACTIVATOR_RAIL, OBSIDIAN_DETECTOR_RAIL, OBSIDIAN_POWERED_RAIL, OBSIDIAN_RAIL.
- `registry/ColorRegistry.java` — **item color provider dropped** (block grass tint kept via
  `BlockColorRegistry.register(List.of(BlockTintSources.grass()), CUSTOM_FIREWORK)`). 26.2 has no
  item `ColorProvider` API; item tints are now driven by item model JSON. Only the CUSTOM_FIREWORK
  block item lost its grass tint (cosmetic, non-blocking).
- `tnteffects/FlowerForestTNTEffect.java` — serverExplosion's biome-overwrite + chunk-resync +
  feature-gen block (biome loop, `ChunkDataS2CPacket` resync, `ConfiguredFeature.generate`).
  Why: 26.2-rewritten internals (`ChunkSection`→`LevelChunkSection`, `ReadableContainer`→
  `PalettedContainerRO`, `PalettedContainer.swapUnsafe`, `networkHandler.sendPacket`→
  `connection.send` + changed `ChunkDataS2CPacket` ctor, `getWorldChunk`/`getLightingProvider`
  renamed, `PATCH_GRASS` key absent). Kept the portable cylindrical explosion + grass placement.
  Class compiles.
- `tnteffects/AtlantisEffect.java` — serverExplosion's biome-overwrite + chunk-resync + Ocean-Ruin
  structure generation block (`Structure.createStructureStart`/`StructureStart.place` rewritten,
  `getStructureAccessor`, `getNoiseConfig`, `BlockBox`, `StructureKeys`). Why: same chunk/biome
  internals plus heavily-changed worldgen structure API. Kept the portable water/sand terraforming
  + squid spawning; explosionTick (weather/sound) fully ported. Class compiles.
- `item/VacuumCleaner.java` — `use()` / `inventoryTick()` bodies stubbed to no-ops (item still
  registers & is craftable). Why: original toggled a "using" flag in mutable CustomData NBT (removed
  in 26.2) and spawned VACUUM_SHOT projectiles via renamed velocity/damage accessors. (Stub
  pre-existed from an earlier pass; Agent CORE fixed the surrounding compile errors — `TooltipDisplay`
  package + `Item.Properties.setId`.)
- `effects/MidasTouchEffect.java` — `applyEffectTick()` body commented out (effect still registers,
  inert). Why: relied on removed PickaxeItem/SwordItem/ToolItem classes + RaycastContext(→ClipContext)
  + renamed item accessors. (Stub pre-existed; Agent CORE removed the now-invalid `isInstantenous()`
  override so it compiles.)

### Mixins (§9)
- `mixin/InGameHudMixin` — **removed from `luckytntmod.mixins.json`** (class kept as no-op). Why:
  yarn `InGameHud`→`Gui`; the `LayeredDraw`/`addLayer` API is gone (HUD moved to render-state). The
  freeze/contaminated overlay is rewritten in `client/overlay/OverlayTick` but must be registered via
  the Fabric HUD API (`HudElementRegistry`) rather than a mixin — registration hook not wired (client-only).
- `mixin/AbstractMinecartEntityMixin` — **powered-rail acceleration inject commented out** (activator-rail
  inject kept & working). Why: cart movement moved into `MinecartBehavior`; the `powerTrack`/`haltTrack`
  logic is unreachable from an `AbstractMinecart` mixin. Effect: `OBSIDIAN_POWERED_RAIL` no longer
  accelerates carts.

### Client renderers (§9)
- `registry/RendererRegistry.java` — **4 living-TNT renderer registrations commented out**
  (`ATTACKING_TNT`, `WALKING_TNT`, `VICIOUS_TNT`, `EVIL_TNT`). Why: these are `EntityType<LivingPrimedLTNT>`
  and the ported lib has no `LivingLTNTRenderer`; they render with the default renderer. Client-only,
  never exercised by the server. All 21 projectile renderers were fixed (→ `LDynamiteRenderer`).

### TNT effects (§9) — class compiles, portable parts kept, complex block commented in `/* */`
- `tnteffects/StructureTNTEffect.java` — whole `serverExplosion` + inner structure generators (yarn-only
  structure API: `createStructureStart`, `StructurePiecesCollector`, `ChunkRandom`, `*Generator`).
- `tnteffects/WorldOfWoolsEffect.java` + `tnteffects/WoolTNTEffect.java` — the MapColor→wool-shade
  classification (~60 yarn `MapColor` constant names with no verified 1:1 Mojang mapping). Rest of both
  effects (terraforming, rings, sheep) kept.
- `tnteffects/JungleTNTEffect.java` — `doJungleExplosion()` biome-overwrite + `ChunkDataS2CPacket` sync +
  `ConfiguredFeature.generate`. Core sphere explosion + grass conversion kept.
- `tnteffects/NetherTNTEffect.java` — feature-gen (`Feature.DISK`) + `NetherFossil` structure block.
  Terrain-shaping explosion + nylium conversion kept.
- `tnteffects/ItemFireworkEffect.java` — `serverExplosion` body (Boat/`Fireball`/`ThrownPotion`/
  `PotionContents` entity-constructor overhaul). Float-up tick kept.
- `tnteffects/ZombieApocalypseEffect.java`, `StoneColdEffect.java`, `NightTNTEffect.java` — single
  `setTimeOfDay(...)` line each (26.2 reworked the day-time system into `ServerClockManager`/`WorldClock`).
  All other behaviour (zombie spawn / freezing / night) kept.

### Data (recipes)
- `recipe/smelt_*`, `recipe/blast_*` (4 files) — custom `luckytntmod:smelting_mult`/`blasting_mult`
  cooking serializers were never ported to Java → remapped to vanilla `minecraft:smelting`/`blasting`.
  Effect: ore→ingot smelting/blasting yields **1** item instead of the original ×N multiplier.
- All 355 recipe JSON migrated to 26.2 plain-string ingredients; Forge tags (`forge:ores`, `forge:dyes`)
  → Fabric conventional tags (`c:ores`, `c:dyes`).
