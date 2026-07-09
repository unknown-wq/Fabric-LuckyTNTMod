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
- [ ] Agent C — client renderers (render-state/`submit`), HUD overlay, config GUI
- [ ] Agent D — sweeper: tnteffects/*, feature/*, data JSON, full `build`
- [ ] runServer smoke test reaches `Done (…)!` with no `/ERROR]` lines

## Disabled content (§9)

_Nothing disabled yet._ Every cut goes here: file, what, why.
