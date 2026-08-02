package luckytnt.tnteffects;

import java.util.ArrayList;
import java.util.List;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.monster.cubemob.MagmaCube;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.EntityTypes;

public class AnimalKingdomEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		Level level = ent.getLevel();
		// one RandomSource instead of ~62 `new Random()` allocations, each of which also re-seeded
		// itself from the global seed uniquifier
		RandomSource random = level.getRandom();
		List<Mob> list = new ArrayList<>();
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Allay(EntityTypes.ALLAY, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Axolotl(EntityTypes.AXOLOTL, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Bat(EntityTypes.BAT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Bee(EntityTypes.BEE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Blaze(EntityTypes.BLAZE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Cat(EntityTypes.CAT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new CaveSpider(EntityTypes.CAVE_SPIDER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Chicken(EntityTypes.CHICKEN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Cow(EntityTypes.COW, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Creeper(EntityTypes.CREEPER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Donkey(EntityTypes.DONKEY, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Drowned(EntityTypes.DROWNED, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 2 + random.nextInt(2); i < n; i++) {
			Mob mob = new ElderGuardian(EntityTypes.ELDER_GUARDIAN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new EnderMan(EntityTypes.ENDERMAN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Endermite(EntityTypes.ENDERMITE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 2 + random.nextInt(2); i < n; i++) {
			Mob mob = new Evoker(EntityTypes.EVOKER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Fox(EntityTypes.FOX, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Frog(EntityTypes.FROG, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Ghast(EntityTypes.GHAST, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Giant(EntityTypes.GIANT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Goat(EntityTypes.GOAT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Guardian(EntityTypes.GUARDIAN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 2 + random.nextInt(2); i < n; i++) {
			Mob mob = new Hoglin(EntityTypes.HOGLIN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Horse(EntityTypes.HORSE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Husk(EntityTypes.HUSK, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new IronGolem(EntityTypes.IRON_GOLEM, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Llama(EntityTypes.LLAMA, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new MagmaCube(EntityTypes.MAGMA_CUBE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new MushroomCow(EntityTypes.MOOSHROOM, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Mule(EntityTypes.MULE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Ocelot(EntityTypes.OCELOT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Panda(EntityTypes.PANDA, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Parrot(EntityTypes.PARROT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Phantom(EntityTypes.PHANTOM, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Pig(EntityTypes.PIG, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Piglin(EntityTypes.PIGLIN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new PiglinBrute(EntityTypes.PIGLIN_BRUTE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Pillager(EntityTypes.PILLAGER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new PolarBear(EntityTypes.POLAR_BEAR, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Rabbit(EntityTypes.RABBIT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 2; i++) {
			Mob mob = new Ravager(EntityTypes.RAVAGER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Sheep(EntityTypes.SHEEP, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Shulker(EntityTypes.SHULKER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Silverfish(EntityTypes.SILVERFISH, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Skeleton(EntityTypes.SKELETON, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new SkeletonHorse(EntityTypes.SKELETON_HORSE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Slime(EntityTypes.SLIME, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new SnowGolem(EntityTypes.SNOW_GOLEM, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Spider(EntityTypes.SPIDER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Stray(EntityTypes.STRAY, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Strider(EntityTypes.STRIDER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Turtle(EntityTypes.TURTLE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Vex(EntityTypes.VEX, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Villager(EntityTypes.VILLAGER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Vindicator(EntityTypes.VINDICATOR, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Witch(EntityTypes.WITCH, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new WitherSkeleton(EntityTypes.WITHER_SKELETON, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Wolf(EntityTypes.WOLF, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Zoglin(EntityTypes.ZOGLIN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new Zombie(EntityTypes.ZOMBIE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new ZombieHorse(EntityTypes.ZOMBIE_HORSE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new ZombieVillager(EntityTypes.ZOMBIE_VILLAGER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0, n = 4 + random.nextInt(3); i < n; i++) {
			Mob mob = new ZombifiedPiglin(EntityTypes.ZOMBIFIED_PIGLIN, ent.getLevel());
			list.add(mob);
		}
		
		// The old code scanned the whole Y column per mob (3 getBlockState + 2 getCollisionShape per
		// step, ~345k world reads and ~230k voxel shape builds for ~300 mobs in a single tick).
		// The MOTION_BLOCKING_NO_LEAVES heightmap gives the same surface Y in O(1).
		for(Mob mob : list) {
			int x = Mth.floor(ent.x()) + random.nextInt(101) - 50;
			int z = Mth.floor(ent.z()) + random.nextInt(101) - 50;
			int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
			BlockPos pos = new BlockPos(x, y, z);
			mob.setPos(pos.getX(), pos.getY(), pos.getZ());
			if(level instanceof ServerLevel sl) {
				mob.finalizeSpawn(sl, sl.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
			}
			level.addFreshEntity(mob);
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ANIMAL_KINGDOM.get();
	}
}
