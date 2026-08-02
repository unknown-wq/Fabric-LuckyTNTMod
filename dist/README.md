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
3087dd5786889d406fe9d635b07b019d9932bc0d1285091ba6b2147c83727d7a  fabric-luckytntmod-26.2-6.0.jar
c6c4cb70b179597bace3e1abecd676ebf5c1d46923c6083a82bd98c8b4685e0b  fabric-luckytntlib-26.2-0.100.6.1.jar
44f6a9fb280c83c529d3e6a136b4a877a69d8944478074d7ae694d9cbedd6695  fabric-luckytntmod-26.2-6.0-sources.jar
842be3ff2a3dcfdc30ef1cf1a8acddd30ae3093011dd3870bf134c354c988b41  fabric-luckytntlib-26.2-0.100.6.1-sources.jar
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
