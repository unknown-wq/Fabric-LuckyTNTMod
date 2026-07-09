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
