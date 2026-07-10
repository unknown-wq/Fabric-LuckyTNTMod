# Lucky TNT Mod — Fabric port for Minecraft 26.2

> **Unofficial community port** of the **Lucky TNT Mod** (and its required library **Lucky TNT Lib**) to **Minecraft 26.2** on the **Fabric** mod loader.
>
> This is **not** an official release and is **not affiliated with or endorsed by** the original authors. All original code, assets and rights belong to them — see [Credits & license](#credits--license).

Adds tons of new TNT, dynamite and explosives to blow up your world — now running on Minecraft **26.2** (the first deobfuscated version, Java 25) with Fabric.

---

## What this is

Minecraft 26.2 changed a lot (deobfuscated codebase, Java 25, new item-model system, etc.), so the original Forge/older-Fabric builds of Lucky TNT Mod do **not** run on it. This repository is a source port that gets both mods booting green on 26.2:

- **Lucky TNT Lib** (`luckytntlib`) — required library.
- **Lucky TNT Mod** (`luckytntmod`) — the main mod (hundreds of TNTs and dynamites).

**Keywords:** Lucky TNT Mod 26.2, Lucky TNT Fabric, Lucky TNT Mod Fabric 26.2, Minecraft 26.2 Fabric mods, Lucky TNT Lib Fabric, dynamite mod, TNT mod Fabric Java 25, unofficial Lucky TNT port.

---

## Requirements

| Component | Version |
|-----------|---------|
| Minecraft | **26.2** (`>=26.2 <26.3`) |
| Mod loader | **Fabric Loader** `>= 0.19.3` |
| [Fabric API](https://modrinth.com/mod/fabric-api) | any 26.2 build (e.g. `0.154.2+26.2`) |
| Java | **25+** |

Both mod jars **and** Fabric API are required — the mod will not load without all three.

---

## Download & install

Prebuilt jars live in the [`dist/`](dist) folder of this repo:

- `fabric-luckytntlib-26.2-0.100.6.1.jar` — the library (required)
- `fabric-luckytntmod-26.2-6.0.jar` — the mod

### Steps

1. Install **Fabric Loader** for Minecraft **26.2** ([fabricmc.net](https://fabricmc.net/use/installer/)).
2. Download **[Fabric API](https://modrinth.com/mod/fabric-api)** for 26.2.
3. Download the two jars from [`dist/`](dist) in this repo (open each file → **Download raw**), or grab them from the repository's Releases if published there.
4. Drop **all three** jars into your `.minecraft/mods/` folder.
5. Launch Minecraft with the Fabric 26.2 profile.

> The `-sources.jar` files in `dist/` are for developers only — **do not** put them in your `mods/` folder.

---

## Building from source

The build needs **Gradle 9.6.1** and **Java 25** (Gradle 8.x cannot run on Java 25). A Gradle 9.6.1 distribution is vendored in [`gradle-dist/`](gradle-dist) for environments where the wrapper cannot download.

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

Output jars land in `TntLib/build/libs/` and `tntmod/build/libs/`. `tntmod` depends on the library jar in `dist/`.

---

## Credits & license

- **Original mods:** *Lucky TNT Mod* and *Lucky TNT Lib* by **Fleshcrafter** and **SlimingHD**.
  - Lucky TNT Mod — <https://www.curseforge.com/minecraft/mc-mods/luckytnt>
  - Lucky TNT Lib — <https://www.curseforge.com/minecraft/mc-mods/lucky-tnt-lib>
- **License:** the original project is released **All Rights Reserved** by its authors. This unofficial port inherits that license; all rights remain with the original authors. Please support the official releases.

If you are one of the original authors and want this port taken down or changed, please open an issue.
