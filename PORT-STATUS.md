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

## Progress checklist (§5 areas)

- [x] Step 0 — toolchain, build files (`build.gradle`/`gradle.properties`/`settings.gradle`/`fabric.mod.json`/`mixins.json`), `port-rename.sh` first pass, genSources
- [ ] Agent A — registry/* , block/* , item/* , entity/* compile
- [ ] Agent B — 7 mixins re-verified against `/opt/mc-src/`
- [x] Agent C — client renderers (render-state/`submit`), HUD overlay, config GUI (edits done; pending central compile)
- [ ] Agent D — sweeper: tnteffects/*, feature/*, data JSON, full `build`
- [ ] runServer smoke test reaches `Done (…)!` with no `/ERROR]` lines

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
