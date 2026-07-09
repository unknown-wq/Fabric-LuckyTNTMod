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
