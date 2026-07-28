# Gradle 9.6.1 distribution (offline)

Minecraft **26.2** needs Java 25, which needs **Gradle 9.x** (Gradle 8.x cannot
run on Java 25). In this environment the Gradle wrapper cannot download its
distribution — `services.gradle.org` redirects to GitHub release assets, which
the egress policy blocks (HTTP 403). So the distribution is vendored here as a
multi-volume RAR (each part is < 100 MB to stay under GitHub's file-size limit).

## Install

```sh
./gradle-dist/install.sh          # extracts to /opt/gradle-9.6.1 and prints the path
export PATH=/opt/gradle-9.6.1/bin:$PATH
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
gradle --version                  # Gradle 9.6.1
```

Then build with the system Gradle (not `./gradlew`, whose wrapper download is
blocked):

```sh
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 gradle build --no-daemon
```

## Files

- `gradle-9.6.1-bin.part1.rar` … `part5.rar` — volumes of `gradle-9.6.1-bin.zip`.
- `install.sh` — unpacks the volumes and unzips to `/opt/gradle-9.6.1`.

Requires `unrar` (`sudo apt-get install -y unrar`).

## If `install.sh` does not work

On the Claude Code container image `unrar` is not installable (it lives in a
`multiverse` component that is not enabled) and Java 25 is not preinstalled —
only Java 21, and the `/opt/gradle` on `PATH` is 8.14.3, which cannot run on
Java 25. This sequence works instead and was verified end to end:

```sh
apt-get update
apt-get install -y 7zip                    # 7-Zip 23.01 reads multi-volume RAR natively
apt-get install -y openjdk-25-jdk-headless # 25.0.3, from the default noble repos

mkdir /tmp/gr && cp gradle-dist/gradle-9.6.1-bin.part*.rar /tmp/gr/
(cd /tmp/gr && 7z x -y gradle-9.6.1-bin.part1.rar)
unzip -q /tmp/gr/gradle-9.6.1-bin.zip -d /opt/

cd tntmod && JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 \
  /opt/gradle-9.6.1/bin/gradle build --no-daemon --console=plain
```

Dependency resolution needs no extra proxy work: `maven.fabricmc.net`,
`plugins.gradle.org` and `piston-meta.mojang.com` are all reachable, so Loom
downloads Minecraft 26.2 and the Fabric toolchain normally. The first
`compileJava` takes roughly 70 s; afterwards the build is incremental.

Note that `PORT-CHEATSHEET.md` says not to run Gradle because the porting
orchestrator compiled centrally. That applies to the bulk port only — for
ordinary work a local build is the fastest way to check a change.
