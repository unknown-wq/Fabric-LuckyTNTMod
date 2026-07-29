# dist

Compiled builds of the Fabric / Minecraft 26.2 port. These are **finished, ready-to-install jars** —
you do not need to compile anything to play, just drop them into `mods/`. The sources they were built
from are in `../TntLib/` and `../tntmod/`.

You need **both** jars plus [Fabric API](https://modrinth.com/mod/fabric-api) for 26.2. The mod does
not load without the library.

| File | What it is |
|---|---|
| `fabric-luckytntmod-26.2-6.0.jar` | **Lucky TNT Mod** — the main mod |
| `fabric-luckytntlib-26.2-0.100.6.1.jar` | **Lucky TNT Lib** — required library |
| `fabric-luckytntmod-26.2-6.0-sources.jar` | sources, **developers only** — do not put in `mods/` |
| `fabric-luckytntlib-26.2-0.100.6.1-sources.jar` | sources, **developers only** — do not put in `mods/` |

| | |
|---|---|
| Minecraft | 26.2 (`>=26.2 <26.3`) |
| Loader | Fabric, loader ≥ 0.19.3 |
| Java | 25 |
| Requires | Fabric API for 26.2 (e.g. 0.154.2+26.2) |
| Side | client + server |

sha256:

```
74e1d2af5f80bacb07bcc941d6d4bc6ed59044890dc4709d57f957ddc8321368  fabric-luckytntmod-26.2-6.0.jar
b7c3d3f195ff76c4b14df8685572d2dc5522928ba2dd7d6a0c1579fe5dad9676  fabric-luckytntlib-26.2-0.100.6.1.jar
157a28eff54d52f661183d65594d84f415d5e42b953d39fa01043f826b7d1d65  fabric-luckytntmod-26.2-6.0-sources.jar
0f63f8450df374f024ea2d47eca8985af938dbbeced44f9288c9840d8045bac2  fabric-luckytntlib-26.2-0.100.6.1-sources.jar
```

**Server-side is verified** — a dedicated 26.2 server boots green with these jars (`Done (…)!`, zero
`ERROR` lines, all TNT registered, mixins applied). A clean boot exercises loading and registration
only: gameplay has not been play-tested and the client has no verified run. Several effects were
deliberately cut during the port — see the **Disabled content** log in `../PORT-STATUS.md` before
reporting a missing effect as a bug.

Rebuild with (library first — `tntmod` compiles against the library jar in this folder):

```sh
../gradle-dist/install.sh          # vendored Gradle 9.6.1
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
cd ../TntLib && /opt/gradle-9.6.1/bin/gradle build --no-daemon && cd -
cd ../tntmod && /opt/gradle-9.6.1/bin/gradle build --no-daemon && cd -
```
