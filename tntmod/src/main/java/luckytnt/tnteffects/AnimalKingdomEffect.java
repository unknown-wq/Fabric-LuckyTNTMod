package luckytnt.tnteffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AnimalKingdomEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		List<MobEntity> list = new ArrayList<>();
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new AllayEntity(EntityType.ALLAY, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new AxolotlEntity(EntityType.AXOLOTL, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new BatEntity(EntityType.BAT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new BeeEntity(EntityType.BEE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new BlazeEntity(EntityType.BLAZE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new CatEntity(EntityType.CAT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new CaveSpiderEntity(EntityType.CAVE_SPIDER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new ChickenEntity(EntityType.CHICKEN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new CowEntity(EntityType.COW, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new CreeperEntity(EntityType.CREEPER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new DonkeyEntity(EntityType.DONKEY, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new DrownedEntity(EntityType.DROWNED, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 2 + new Random().nextInt(2); i++) {
			MobEntity mob = new ElderGuardianEntity(EntityType.ELDER_GUARDIAN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new EndermanEntity(EntityType.ENDERMAN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new EndermiteEntity(EntityType.ENDERMITE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 2 + new Random().nextInt(2); i++) {
			MobEntity mob = new EvokerEntity(EntityType.EVOKER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new FoxEntity(EntityType.FOX, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new FrogEntity(EntityType.FROG, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new GhastEntity(EntityType.GHAST, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new GiantEntity(EntityType.GIANT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new GoatEntity(EntityType.GOAT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new GuardianEntity(EntityType.GUARDIAN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 2 + new Random().nextInt(2); i++) {
			MobEntity mob = new HoglinEntity(EntityType.HOGLIN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new HorseEntity(EntityType.HORSE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new HuskEntity(EntityType.HUSK, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new IronGolemEntity(EntityType.IRON_GOLEM, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new LlamaEntity(EntityType.LLAMA, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new MagmaCubeEntity(EntityType.MAGMA_CUBE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new MooshroomEntity(EntityType.MOOSHROOM, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new MuleEntity(EntityType.MULE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new OcelotEntity(EntityType.OCELOT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new PandaEntity(EntityType.PANDA, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new ParrotEntity(EntityType.PARROT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new PhantomEntity(EntityType.PHANTOM, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new PigEntity(EntityType.PIG, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new PiglinEntity(EntityType.PIGLIN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new PiglinBruteEntity(EntityType.PIGLIN_BRUTE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new PillagerEntity(EntityType.PILLAGER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new PolarBearEntity(EntityType.POLAR_BEAR, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new RabbitEntity(EntityType.RABBIT, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 2; i++) {
			MobEntity mob = new RavagerEntity(EntityType.RAVAGER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new SheepEntity(EntityType.SHEEP, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new ShulkerEntity(EntityType.SHULKER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new SilverfishEntity(EntityType.SILVERFISH, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new SkeletonEntity(EntityType.SKELETON, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new SkeletonHorseEntity(EntityType.SKELETON_HORSE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new SlimeEntity(EntityType.SLIME, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new SnowGolemEntity(EntityType.SNOW_GOLEM, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new SpiderEntity(EntityType.SPIDER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new StrayEntity(EntityType.STRAY, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new StriderEntity(EntityType.STRIDER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new TurtleEntity(EntityType.TURTLE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new VexEntity(EntityType.VEX, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new VillagerEntity(EntityType.VILLAGER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new VindicatorEntity(EntityType.VINDICATOR, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new WitchEntity(EntityType.WITCH, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new WitherSkeletonEntity(EntityType.WITHER_SKELETON, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new WolfEntity(EntityType.WOLF, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new ZoglinEntity(EntityType.ZOGLIN, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new ZombieEntity(EntityType.ZOMBIE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new ZombieHorseEntity(EntityType.ZOMBIE_HORSE, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new ZombieVillagerEntity(EntityType.ZOMBIE_VILLAGER, ent.getLevel());
			list.add(mob);
		}
		for(int i = 0; i < 4 + new Random().nextInt(3); i++) {
			MobEntity mob = new ZombifiedPiglinEntity(EntityType.ZOMBIFIED_PIGLIN, ent.getLevel());
			list.add(mob);
		}
		
		for(MobEntity mob : list) {
			int offX = new Random().nextInt(101) - 50;
			int offZ = new Random().nextInt(101) - 50;
			for(int y = ent.getLevel().getTopY(); y > ent.getLevel().getBottomY(); y--) {
				BlockPos pos = toBlockPos(new Vec3d(ent.x() + offX, y, ent.z() + offZ));
				BlockState state = ent.getLevel().getBlockState(pos);
				if(Block.isFaceFullSquare(ent.getLevel().getBlockState(pos.down()).getCollisionShape(ent.getLevel(), pos.down()), Direction.UP) && !Block.isFaceFullSquare(state.getCollisionShape(ent.getLevel(), pos), Direction.UP)) {
					mob.setPosition(pos.getX(), pos.getY(), pos.getZ());
					if(ent.getLevel() instanceof ServerWorld sl) {
						mob.initialize(sl, ent.getLevel().getLocalDifficulty(pos), SpawnReason.MOB_SUMMONED, null);
					}
					ent.getLevel().spawnEntity(mob);
					break;
				}
			}
		}
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ANIMAL_KINGDOM.get();
	}
}
