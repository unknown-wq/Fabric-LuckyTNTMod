# dist

Compiled builds of the Fabric / Minecraft **26.3** port (branch `26.3`). These are finished jars; to
use them you only drop them into `mods/`, with nothing to compile. The sources they were built from are
in `../TntLib/` and `../tntmod/`. The 26.2 builds are on the `26.2` branch.

You need **both** jars plus [Fabric API](https://modrinth.com/mod/fabric-api) for 26.3. The mod does
not load without the library.

| File | What it is |
|---|---|
| `fabric-luckytntmod-26.3-6.0.jar` | **Lucky TNT Mod**, the main mod |
| `fabric-luckytntlib-26.3-0.100.6.1.jar` | **Lucky TNT Lib**, the required library |
| `fabric-luckytntmod-26.3-6.0-sources.jar` | sources, **developers only**, do not put in `mods/` |
| `fabric-luckytntlib-26.3-0.100.6.1-sources.jar` | sources, **developers only**, do not put in `mods/` |

| | |
|---|---|
| Minecraft | 26.3 (`>=26.3 <26.4`) |
| Loader | Fabric, loader ≥ 0.19.5 |
| Java | 25 |
| Requires | Fabric API for 26.3 (built against 0.160.5+26.3) |
| Side | client + server |

sha256:

```
e0f62fd9d6fe55da340c35793122e2c9353583ba9770e0305274867fd0d6e00f  fabric-luckytntmod-26.3-6.0.jar
6c0fc8f2178d2ee94a37ceb1a700a1e65b8d329248a512554e31195e8fdfe40e  fabric-luckytntlib-26.3-0.100.6.1.jar
b9c78619797ac3a5df939217cae7d8b57464a634a7c3b8fc3a156972681f5274  fabric-luckytntmod-26.3-6.0-sources.jar
9ee339363760d95ba2582ddf7ca27be6ce750dbfce3a23085af4b81f3b44a7d6  fabric-luckytntlib-26.3-0.100.6.1-sources.jar
```

**Compile-verified only.** Both projects build green against Minecraft 26.3, and the mixin targets
were checked by hand against the 26.3 classes. Neither jar has been started in a client or a server
on 26.3, so loading, worldgen and gameplay are untested. See `../PORT-26.3.md` for what changed and
what is most likely to break. The effects cut during the 26.2 port are still cut; they are listed
under **Disabled content** in `../PORT-STATUS.md`.

Rebuild with (library first, because `tntmod` compiles against the library jar in this folder):

```sh
../gradle-dist/install.sh          # vendored Gradle 9.6.1
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
cd ../TntLib && /opt/gradle-9.6.1/bin/gradle build --no-daemon && cd -
cp ../TntLib/build/libs/fabric-luckytntlib-26.3-0.100.6.1*.jar .
cd ../tntmod && /opt/gradle-9.6.1/bin/gradle build --no-daemon && cd -
```
