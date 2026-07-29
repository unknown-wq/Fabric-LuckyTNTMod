# Lucky TNT Mod for Fabric — TNT & Explosives Mod for Minecraft 26.2

**Lucky TNT Mod on Fabric for Minecraft 26.2** — an unofficial community port of the
[Lucky TNT Mod](https://www.curseforge.com/minecraft/mc-mods/luckytnt) and its required library
[Lucky TNT Lib](https://www.curseforge.com/minecraft/mc-mods/lucky-tnt-lib), which add **hundreds of
new TNTs, dynamites and explosives** to Minecraft. The official builds do not run on Minecraft 26.2;
this repository ports both mods to **26.2** on the **Fabric** loader with **Java 25**.

![Minecraft 26.2](https://img.shields.io/badge/Minecraft-26.2-brightgreen "Supported Minecraft version: 26.2")
![Mod loader: Fabric](https://img.shields.io/badge/Loader-Fabric%20%E2%89%A50.19.3-blue "Mod loader: Fabric 0.19.3 or newer")
![Java 25](https://img.shields.io/badge/Java-25-orange "Requires Java 25")
![Unofficial port](https://img.shields.io/badge/Port-unofficial-red "Unofficial community port, all rights reserved by the original authors")

> **Unofficial community port.** Not an official release, **not affiliated with or endorsed by** the
> original authors. All original code, assets and rights belong to them — see
> [Credits & license](#credits--license).

> **Download:** two **compiled, ready-to-install jars** are committed in
> **[`dist/`](dist/README.md)** — nothing to build, no toolchain needed. Drop them into `mods/`
> together with Fabric API.

---

## Download Lucky TNT Mod for Minecraft 26.2 (Fabric)

`dist/` holds **already compiled builds**, not just source code. The sources they were built from
are in `tntmod/` and `TntLib/`; you only need those if you want to compile it yourself.

| | |
|---|---|
| **Mod** | [`fabric-luckytntmod-26.2-6.0.jar`](https://github.com/unknown-wq/Fabric-LuckyTNTMod/raw/26.2/dist/fabric-luckytntmod-26.2-6.0.jar) (compiled, ready to use) |
| **Library (required)** | [`fabric-luckytntlib-26.2-0.100.6.1.jar`](https://github.com/unknown-wq/Fabric-LuckyTNTMod/raw/26.2/dist/fabric-luckytntlib-26.2-0.100.6.1.jar) |
| **Minecraft version** | 26.2 (`>=26.2 <26.3`) |
| **Mod loader** | Fabric, loader 0.19.3 or newer |
| **Java version** | 25 |
| **Required dependency** | Fabric API for 26.2 (e.g. 0.154.2+26.2) |
| **Mod / library version** | 6.0 / 0.100.6.1 |
| **License** | All Rights Reserved by the original authors (see below) |

Checksums and build details: [`dist/README.md`](dist/README.md).

### How to install

1. Install the **Fabric loader** (0.19.3+) for Minecraft **26.2** from
   [fabricmc.net](https://fabricmc.net/use/installer/), and make sure you are running **Java 25**.
2. Download **[Fabric API](https://modrinth.com/mod/fabric-api)** for 26.2 — the mod will not load
   without it.
3. Download **both** jars from [`dist/`](dist/README.md): the mod **and** the library.
4. Put all **three** jars (mod + library + Fabric API) into the `mods/` folder of your Fabric 26.2
   profile — or of your Fabric server.
5. Launch and look for the **Lucky TNT** creative tabs.

> The `-sources.jar` files in `dist/` are for developers only — do **not** put them in `mods/`.

## Features

Content is the original mod's; the port keeps it intact.

- **216 TNT blocks** — from Kinetic and Meteoric TNT up to Tsar Bomba, Hydrogen Bomb and Black Hole
  TNT, plus **68 throwable dynamites** and other explosives.
- **171 distinct explosion effects** — terraforming, biome-warping, structure-spawning, mob-spawning,
  weather and gravity effects, not just bigger craters.
- **Explosives sandbox extras** — TNT-related blocks, rails, items, potion effects, sounds and
  worldgen features.
- **Added in this port:** **Bomb Rain TNT** and **Bomb Rain Dynamite**, which spawn a bomb ~100
  blocks above the blast.
- **Languages:** English, German, Russian.

## Port status — what works and what was cut

**Server-side is verified:** a dedicated Minecraft 26.2 Fabric server boots green with these jars —
`Done (0.411s)!`, zero `ERROR` lines, all TNT registered, all six loaded mixins applied, and all 355
recipe JSON files migrated to the 26.2 format. One harmless mixin warning remains (`FireBlockMixin`
`canBurn`, now defined by vanilla `FireBlock`).

**Gameplay is not play-tested and the client has no verified run.** Three client-side crashes
(Death Ray, Stone Cold, and a stale cross-level explosion singleton) were found and fixed by code
inspection, and a performance pass trimmed the heaviest terrain and projectile effects — but none of
that replaces actually playing it. Treat this as a testing build and back up your world.

Known cuts and behaviour changes, all deliberate, to reach a green build:

- **Vacuum Cleaner** and the **Midas Touch** effect register and are craftable but are **inert** —
  their logic relied on APIs removed in 26.2.
- **Ore smelting/blasting multipliers are gone:** four custom cooking recipes now yield **1** item
  instead of the original ×N.
- **`OBSIDIAN_POWERED_RAIL` no longer accelerates minecarts** — cart movement moved into
  `MinecartBehavior`, out of reach of the original mixin.
- **Freeze / contamination screen overlay is not shown** — the HUD mixin was dropped; the effect is
  rewritten but not yet wired to the Fabric HUD API.
- **Four living-TNT entities** (Attacking, Walking, Vicious, Evil TNT) fall back to the default
  renderer.
- Six blocks (X-Ray TNT, Custom Firework, obsidian rails) still need a `render_type` in their model
  JSON and may render opaque; the Custom Firework block item lost its grass tint.
- Several effects kept their portable parts but lost biome overwriting, chunk resync or structure
  generation: **Flower Forest, Atlantis, Structure, Jungle, Nether, World of Wools, Wool, Item
  Firework, Zombie Apocalypse, Stone Cold and Night TNT**.

The complete per-file list is the **Disabled content** log in [`PORT-STATUS.md`](PORT-STATUS.md) —
read it before reporting a missing effect as a bug.

## What this port changes technically

Minecraft 26.2 is the first **deobfuscated** release: Yarn/Intermediary mappings are discontinued,
the game runs on **Java 25**, and rendering, NBT, item models and the HUD were all reworked. So the
port moves along two axes at once:

- **Mappings:** Yarn → **Mojang official** names across 357 Java files (`Identifier`,
  `MinecraftClient`→`Minecraft`, `World`→`Level`, `Item.Settings`→`Item.Properties`, …).
- **Vanilla API:** render-state renderers and `submit`, `ValueInput`/`ValueOutput` NBT, item model
  definitions, `ServerClockManager`/`WorldClock` day time, reworked worldgen and structure APIs, and
  Fabric conventional tags (`c:ores`, `c:dyes`) in place of Forge tags.

The reference documents used for the port — reusable for porting any 1.21.x mod to 26.2 — are
[`PORTING-GUIDE-26.2.md`](PORTING-GUIDE-26.2.md), [`PORT-MOD-26.2.md`](PORT-MOD-26.2.md),
[`PORT-PLAN-26.2.md`](PORT-PLAN-26.2.md) and [`PORT-CHEATSHEET.md`](PORT-CHEATSHEET.md).

## Repository layout

```
/
├── TntLib/            # source of Lucky TNT Lib, ported to Fabric / 26.2
├── tntmod/            # source of Lucky TNT Mod, ported to Fabric / 26.2
├── dist/              # COMPILED, ready-to-install jars (+ sources jars for developers)
├── gradle-dist/       # vendored Gradle 9.6.1 distribution (offline install)
├── PORT-STATUS.md     # live port status and the full disabled-content log
├── PORTING-GUIDE-26.2.md, PORT-MOD-26.2.md, PORT-PLAN-26.2.md, PORT-CHEATSHEET.md
└── port-rename*.sh, port-resolve-imports.py, fix-recipes.py   # one-off migration scripts
```

## Building from source

You do not need this to play — [`dist/`](dist/README.md) already contains compiled jars.

The build needs **Gradle 9.6.1** and **Java 25** (Gradle 8.x cannot run on Java 25). A Gradle 9.6.1
distribution is vendored in [`gradle-dist/`](gradle-dist) for environments where the wrapper cannot
download it:

```sh
# One-time: install the vendored Gradle + Java 25
./gradle-dist/install.sh                       # extracts to /opt/gradle-9.6.1
sudo apt-get install -y openjdk-25-jdk
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
export PATH=/opt/gradle-9.6.1/bin:$PATH

# Build the library first, then the mod
cd TntLib && gradle build --no-daemon && cd ..
cd tntmod && gradle build --no-daemon && cd ..
```

Output jars land in `TntLib/build/libs/` and `tntmod/build/libs/`. `tntmod` compiles against the
prebuilt library jar in `dist/`, so if you change the library, rebuild it and copy it there first.

## FAQ

**Is there a Fabric version of Lucky TNT Mod for Minecraft 26.2?**
Not officially. This repository is an unofficial Fabric port, and the compiled jars for Minecraft
26.2 are in [`dist/`](dist/README.md).

**Does Lucky TNT Mod work on Minecraft 26.2?**
The official releases do not. This port does — Minecraft 26.2, Fabric, Java 25.

**Do I need to compile anything?**
No. `dist/` contains finished jars — drop them into `mods/`.

**Why are there two jars?**
Lucky TNT Mod requires the separate **Lucky TNT Lib** library. You need both, plus Fabric API.

**Does it work on a server?**
Yes — a dedicated 26.2 server boots green with these jars. Gameplay has not been play-tested.

**Which Minecraft versions are supported?**
Only 26.2 (`>=26.2 <26.3`). For older versions use the
[official releases](https://www.curseforge.com/minecraft/mc-mods/luckytnt).

**Is every TNT working?**
Most are. A handful lost biome, structure or chunk-sync behaviour, and two items are inert — see
[`PORT-STATUS.md`](PORT-STATUS.md).

**Is this the same as Lucky Block?**
No. Lucky TNT Mod is a TNT and explosives mod, unrelated to the Lucky Block mod.

## Credits & license

All content, code and assets are the work of the original authors — this repository only ports them
to Fabric and Minecraft 26.2.

- **Original mods:** *Lucky TNT Mod* and *Lucky TNT Lib* by **Fleshcrafter** and **SlimingHD**.
  - Lucky TNT Mod — <https://www.curseforge.com/minecraft/mc-mods/luckytnt>
  - Lucky TNT Lib — <https://www.curseforge.com/minecraft/mc-mods/lucky-tnt-lib>
- **License:** the original project is released **All Rights Reserved** by its authors. This
  unofficial port inherits that license and grants no additional rights; all rights remain with the
  original authors. Please support the official releases.

If you are one of the original authors and want this port changed or taken down, please open an issue
and it will be removed.

## Issues

For bugs **in this port**, [open an issue](https://github.com/unknown-wq/Fabric-LuckyTNTMod/issues)
and include: the jar versions, Minecraft version, Fabric loader and Fabric API versions, the full
client **and** server log, and a screenshot if it is visual. Check the disabled-content log in
[`PORT-STATUS.md`](PORT-STATUS.md) first — a missing effect is probably a known cut, not a bug.

Do **not** report port-specific bugs to the original authors.

## Русская версия

**Lucky TNT Mod для Fabric на Minecraft 26.2** — неофициальный порт мода на **ТНТ, динамит и
взрывчатку** (216 видов ТНТ и 68 динамитов) вместе с обязательной библиотекой **Lucky TNT Lib**.
Официальные сборки на 26.2 не работают; здесь мод портирован на загрузчик **Fabric** и
**Minecraft 26.2** (Java 25).

**Скачать готовые сборки** — в папке [`dist/`](dist/README.md) лежат **уже скомпилированные jar**,
собирать ничего не нужно:
[мод](https://github.com/unknown-wq/Fabric-LuckyTNTMod/raw/26.2/dist/fabric-luckytntmod-26.2-6.0.jar)
и обязательная
[библиотека](https://github.com/unknown-wq/Fabric-LuckyTNTMod/raw/26.2/dist/fabric-luckytntlib-26.2-0.100.6.1.jar).
Положите оба файла вместе с [Fabric API](https://modrinth.com/mod/fabric-api) в папку `mods/`
профиля или сервера Fabric 26.2. Файлы `-sources.jar` в `mods/` кидать не нужно — они для
разработчиков.

Что нужно знать: сервер запускается чисто и проверен, **геймплей не тестировался**, клиент отдельно
не проверялся. Часть эффектов лишилась генерации структур и смены биомов, Vacuum Cleaner и Midas
Touch не работают, обсидиановый рельс-ускоритель не разгоняет вагонетки — полный список в
[`PORT-STATUS.md`](PORT-STATUS.md). Авторы оригинала — **Fleshcrafter** и **SlimingHD**, мод
распространяется по лицензии **All Rights Reserved**; порт неофициальный, все права остаются у
авторов — поддержите официальные релизы.

---

<sub>Keywords: Lucky TNT Mod Fabric, Lucky TNT Mod 26.2, Lucky TNT Fabric 26.2, Lucky TNT Lib
Fabric, Minecraft 26.2 TNT mod, Minecraft 26.2 explosion mod, dynamite mod Fabric, Minecraft 26.2
Fabric mods, Java 25 Minecraft mod, fabric-luckytntmod-26.2-6.0.jar, unofficial Lucky TNT port,
мод на ТНТ Minecraft 26.2, Lucky TNT Фабрик скачать.</sub>
