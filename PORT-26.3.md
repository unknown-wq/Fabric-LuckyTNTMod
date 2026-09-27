# PORT-26.3: LuckyTNTLib + LuckyTNTMod, 26.2 → 26.3

Branch `26.3`, made from `26.2` at `ba6337b`. The goal was a **green compile only**: neither jar has been
started in a client or a server on 26.3.

## Toolchain

| | 26.2 | 26.3 |
|---|---|---|
| `minecraft_version` | 26.2 | 26.3 |
| `loader_version` | 0.19.3 | 0.19.5 |
| `loom_version` | 1.17.13 | 1.17.19 |
| `fabric_version` | 0.154.2+26.2 | 0.160.5+26.3 |
| `archives_base_name` | `…-26.2` | `…-26.3` |

- `fabric.mod.json` (both): `"minecraft": ">=26.3 <26.4"`, `"fabricloader": ">=0.19.5"`.
- `org.gradle.parallel=false` in both `gradle.properties`.
- `tntmod/build.gradle` compiles against `../dist/fabric-luckytntlib-26.3-0.100.6.1.jar`, so the library
  is built first and its jar copied into `dist/`.
- `dist/` holds only the 26.3 jars on this branch. The 26.2 jars remain on the `26.2` branch.

## Error counts

| | versions switched only | after the port |
|---|---|---|
| `TntLib` compileJava | 5 | 0 |
| `tntmod` compileJava | 78 (in 20 files) | 0 |

No new errors appeared after the first wave was fixed. `build` is green for both projects.

## What changed, and why

### Worldgen features (`Feature` / `ConfiguredFeature` merged)

In 26.3, `ConfiguredFeature`, `Feature<C>`, `FeatureConfiguration`/`NoneFeatureConfiguration` and
`FeaturePlaceContext` are gone. `Feature` is now an **interface** whose instances *are* the configured
features: `codec()` plus `place(WorldGenLevel, ChunkGenerator, RandomSource, BlockPos)`. The feature
*type* is only a `MapCodec<? extends Feature>` in `BuiltInRegistries.FEATURE_TYPE`
(`worldgen/feature_type`). The data-driven registry that used to be `Registries.CONFIGURED_FEATURE`
is now `Registries.FEATURE` (`worldgen/feature`).

- `feature/Grave.java`, `feature/Altar.java`: `extends Feature<NoneFeatureConfiguration>` →
  `implements Feature`, with a `MapCodec.unit(...)` codec and the new `place(...)` signature. The
  placement code is unchanged; it still uses `level.getRandom()` as before.
- `registry/FeatureRegistry.java`: registers the two codecs in `BuiltInRegistries.FEATURE_TYPE` under
  `luckytntmod:graves` / `luckytntmod:altar` (same ids as before).
- Data: `data/luckytntmod/worldgen/configured_feature/*.json` → `worldgen/feature/*.json`, in the new
  flat format. The `"config"` wrapper is dropped, and ore targets use the short block-state form
  (`"state": "luckytntmod:gunpowder_ore"`, as vanilla 26.3 does). Sizes, discard chance and rule tests
  are unchanged. `placed_feature/*.json` did not need changes, because their `"feature"` reference now
  resolves against `worldgen/feature`. `BiomeModificationRegistry` is unchanged.
- Effects that place vanilla features (`AetherTNTEffect`, `DripstoneTNTEffect`, `GeodeTNTEffect`,
  `SculkTNTEffect`, `LushTNTEffect`, `NetherGroveTNTEffect`): `ConfiguredFeature<?, ?>` → `Feature`,
  `Registries.CONFIGURED_FEATURE` → `Registries.FEATURE`. All `CaveFeatures`/`TreeFeatures`/
  `VegetationFeatures` keys they use still exist.
- `NetherGroveTNTEffect`: `NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL` and
  `WARPED_FOREST_VEGETATION_BONEMEAL` no longer exist. Vanilla 26.3 bonemeals nylium through a single
  `NetherFeatures.NYLIUM_BONEMEAL` overlay feature that selects crimson or warped vegetation from the
  block *below* the origin. The effect converts the ground to crimson or warped nylium before it places
  vegetation one block above it, so the effect now uses `NYLIUM_BONEMEAL` for both colours.
- `JungleTNTEffect`, `NetherTNTEffect`: only the unused `ConfiguredFeature` import was removed. The code
  that used it was already disabled in 26.2.

### Rendering

- `PoseStack.mulPose(Quaternionf)` was removed. `mulPose(Axis.YP.rotationDegrees(d))` →
  `rotateDegrees(Axis.YP, d)` (`LTNTRenderer`, `BouncingTNTRenderer`, as vanilla `TntRenderer` does), and
  `mulPose(camera.orientation)` → `rotate(camera.orientation)` (`LDynamiteRenderer`, as vanilla
  `ThrownItemRenderer` does).
- `BombRenderer`: `submitModel(...)` lost its trailing crumbling-overlay argument. The renderer now
  uses the 7-argument default overload, which passes tint `-1` and no UV mapping, the same values as before.

### Entity motion

- `Entity.hurtMarked` → `Entity.syncVelocity`. This is a pure rename: `ServerEntity` still sends a
  `ClientboundSetEntityMotionPacket` and clears the flag. Changed in `ImprovedExplosion` (lib),
  `KnockbackTNTEffect`, `GravityTNTEffect`, `HungryTNTEffect`, `BlackHoleTNTEffect`,
  `HomingDynamiteEffect`, `VacuumShotEffect`.

### Smaller renames

- `EnderMan` → `Enderman` (`EndGateEffect`, `EndTNTEffect`, `AnimalKingdomEffect`).
- `ServerLevel.getStructureManager()` (template manager) → `getStructureTemplateManager()`
  (`HouseTNTEffect`, `GroveTNTEffect`, `HyperionEffect`).
- `CommandSourceStack`: the 9-argument constructor with text and display names is gone. `ExtinctionEffect`
  uses `(source, pos, rot, level, permissions, server, entity)`, so the name now comes from the owner entity.
  This has no visible effect: `CommandSource.NULL` receives no feedback and informs no admins.
- `Block.spawnDestroyParticles(Level, Player, BlockPos, BlockState)` →
  `spawnDestroyByEntityParticles(Level, Entity, BlockPos, BlockState)`. Same behaviour
  (`LTNTBlock` in the lib, `TrollTNTMk2Block`).
- Block codecs are gone from 26.3 (`Block.codec()`/`simpleCodec` no longer exist).
  `NuclearWasteBlock` drops its `CODEC` and `codec()` override.

### Data files

- **Loot tables** (`loot_table/blocks/*gunpowder_ore.json`, `loot_table/chests/grave_loot_*.json`):
  26.3 renamed the fields. `"conditions": [..]` became `"condition": {..}`, and the dispatch key is now
  `"type"` instead of `"condition"`. `"functions": [..]` became `"modifier"`, which takes one function
  or an inline list, and here too the dispatch key is `"type"` instead of `"function"`. The old keys are not
  rejected. They are **silently ignored** by the optional-field codecs, which would have made the ores
  always drop themselves and the grave chests ignore their counts. All five files were rewritten; the
  values are unchanged. Integral floats became ints, because counts and rolls are int providers now.
- Recipes: the format is unchanged for crafting. Cooking recipes now *require* `cookingtime`; all 8
  mod cooking recipes already have it. Every vanilla item id and item tag referenced from recipes,
  tags and loot tables was checked against the 26.3 item list.
- Damage types, tags and client assets (blockstates, item definitions, models): no format change was found
  when comparing vanilla 26.2 and 26.3 files.
- No datagen exists in either project.

## Mixin audit

All targets were compared with `javap` on the 26.2 and 26.3 merged jars, and read in the 26.3 sources.

| Mixin | Target | 26.3 |
|---|---|---|
| lib `EntityMixin` | `Entity.load(ValueInput)V`, `Entity.saveWithoutId(ValueOutput)V` @HEAD | unchanged ✔ |
| lib `FireBlockMixin` | `FireBlock.checkBurnOut(Level,BlockPos,I,RandomSource,I)V` @HEAD; shadows `getBurnOdds`, `getStateWithAge` | unchanged ✔ |
| mod `LivingEntityMixin` | `LivingEntity.tick()V` @HEAD | unchanged ✔ |
| mod `AbstractMinecartEntityMixin` | `vehicle.minecart.AbstractMinecart.tick()V` @TAIL; calls `getCurrentBlockPosOrRailBelow`, `activateMinecart(ServerLevel,III,Z)` | unchanged ✔ |
| mod `HungerManagerMixin` | shadow `FoodData.tickTimer` (int) | unchanged ✔ |
| mod `FireBlockMixin` | shadow `FireBlock.getBurnOdds` | unchanged ✔; the old harmless WARN about `@Unique canBurn` colliding with vanilla's `protected canBurn` stays |
| mod `GameRendererMixin` (client) | `GameRenderer.extractCamera` @HEAD | **fixed**: 26.3 is `extractCamera(DeltaTracker, float)`, and the handler no longer takes the removed third float. Without this fix the client would fail at mixin apply time. Order is unchanged: `Camera.update` runs before it. |
| mod `CameraMixin` (client) | shadows `Camera.xRot`, `yRot`, `setRotation(FF)V` | unchanged ✔ |
| mod `InGameHudMixin` | not listed in `luckytntmod.mixins.json` | still disabled (26.2) |

No mixins were added. There are no access wideners.

## Compiles, but risky at runtime (untested)

1. **Worldgen data**: `worldgen/feature/{altar,graves,gunpowder_ores,uranium_ores}.json` were written
   against the 26.3 codecs read from source, not loaded by a real server. A mistake here fails the datapack
   and keeps worlds from loading, so this is the first thing to check on a boot.
2. **Loot tables**: same risk class, but a bad table only logs an error and drops nothing.
3. **NetherGrove vegetation** uses `NYLIUM_BONEMEAL`. Warped groves now also get nether sprouts and
   an occasional twisting vine, as vanilla warped-nylium bonemeal does. The spread pattern is 9 tries
   in a ±2 triangle instead of the old 3×1 spread.
4. **GameRendererMixin** (screen shake) was rewritten against a changed signature. Only the handler
   parameters changed, but it is client-only and nobody has run it.
5. **Renderers** (`LTNTRenderer`, `LDynamiteRenderer`, `BouncingTNTRenderer`, `BombRenderer`): the call
   replacements are semantically equivalent to vanilla 26.3 usage, but nobody has looked at the result.
6. Everything listed as disabled in `PORT-STATUS.md` (26.2 port) is still disabled.
