#!/usr/bin/env python3
"""Auto-resolve net.minecraft.* imports against the decompiled 26.2 source tree.

For every `import net.minecraft.<pkg>.<Class>;` line in the mod, look up <Class>
in /opt/mc-src. If it exists in exactly ONE package, rewrite the import to that
package (fixes classes that merely moved). Ambiguous / not-found classes are left
untouched (an agent or §9 handles them). Also applies a small yarn->Mojang class
rename map (name changed, not just package) to both imports and bare usages.
Skips mixins. Run once from repo root.
"""
import os, re, sys, collections

MC_SRC = "/opt/mc-src/net/minecraft"
ROOT = sys.argv[1] if len(sys.argv) > 1 else "tntmod/src/main/java/luckytnt"

# yarn simple-name -> Mojang simple-name (class was RENAMED, not just moved)
RENAME = {
    "FluidBlock": "LiquidBlock",
    "SpawnReason": "EntitySpawnReason",
    "MobEntity": "Mob",
    "HostileEntity": "Monster",
    "PathAwareEntity": "PathfinderMob",
    "LightningEntity": "LightningBolt",
    "StatusEffectInstance": "MobEffectInstance",
    "StatusEffects": "MobEffects",
    "RegistryEntry": "Holder",
    "Vec2f": "Vec2",
    "MathHelper": "Mth",
    "ServerCommandSource": "CommandSourceStack",
    "DefaultParticleType": "SimpleParticleType",
    "ArrowEntity": "Arrow",
    "SnowballEntity": "Snowball",
    "EggEntity": "ThrownEgg",
    "PotionEntity": "ThrownPotion",
    "ItemEntity": "ItemEntity",
    "FallingBlockEntity": "FallingBlockEntity",
    "TntEntity": "PrimedTnt",
    # common vanilla mobs: yarn XxxEntity -> Mojang Xxx
}

# Build index: simple class name -> set of dotted packages containing it
index = collections.defaultdict(set)
for dirpath, _, files in os.walk(MC_SRC):
    pkg = "net.minecraft" + dirpath[len(MC_SRC):].replace("/", ".")
    for f in files:
        if f.endswith(".java"):
            index[f[:-5]].add(pkg)

def resolve(cls):
    """Return the unique package for cls, or None if 0/many."""
    pkgs = index.get(cls)
    if pkgs and len(pkgs) == 1:
        return next(iter(pkgs))
    return None

# Auto yarn mob rename: XxxEntity -> Xxx when Xxx exists uniquely and XxxEntity doesn't
def auto_mob(cls):
    if cls.endswith("Entity") and cls not in ("ItemEntity", "FallingBlockEntity",
                                               "LivingEntity", "AreaEffectCloudEntity"):
        base = cls[:-6]
        if base and resolve(base) and not resolve(cls):
            return base
    return None

import_re = re.compile(r'^(import\s+(?:static\s+)?)net\.minecraft\.[\w.]+\.(\w+);\s*$')
files = []
for dp, _, fs in os.walk(ROOT):
    if "/mixin" in dp:
        continue
    for f in fs:
        if f.endswith(".java"):
            files.append(os.path.join(dp, f))

renamed_used = collections.Counter()
fixed_imports = 0
for path in files:
    with open(path) as fh:
        lines = fh.readlines()
    local_renames = {}  # simple name -> new simple name applied in this file
    out = []
    for line in lines:
        m = import_re.match(line)
        if m:
            cls = m.group(2)
            new_cls = RENAME.get(cls) or auto_mob(cls)
            target_cls = new_cls or cls
            pkg = resolve(target_cls)
            if pkg:
                out.append(f"{m.group(1)}{pkg}.{target_cls};\n")
                fixed_imports += 1
                if new_cls and new_cls != cls:
                    local_renames[cls] = new_cls
                continue
            elif new_cls and new_cls != cls:
                # renamed but couldn't locate; still swap the name, keep old pkg path removed
                local_renames[cls] = new_cls
        out.append(line)
    # apply bare-usage renames for classes renamed in this file
    if local_renames:
        text = "".join(out)
        for old, new in local_renames.items():
            text = re.sub(r'\b' + re.escape(old) + r'\b', new, text)
            renamed_used[f"{old}->{new}"] += 1
        out = [text]
    with open(path, "w") as fh:
        fh.writelines(out)

print(f"Rewrote {fixed_imports} import lines across {len(files)} files.")
if renamed_used:
    print("Class renames applied:", dict(renamed_used))
