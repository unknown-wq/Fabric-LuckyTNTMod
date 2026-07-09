package luckytntlib.item;

import java.util.Random;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import luckytntlib.entity.LExplosiveProjectile;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * The LDynamiteItem is an important step in making a custom explosive projectile.
 * It can be thrown and spawns a {@link LExplosiveProjectile} similar to an egg or a snowball.
 * If a {@link DispenserBehavior} has been registered dispensers can also throw the dynamite.
 */
public class LDynamiteItem extends Item{
	
	@Nullable
	protected Supplier<EntityType<LExplosiveProjectile>> dynamite;
	protected Random random = new Random();
	
	public LDynamiteItem(Item.Settings properties, @Nullable Supplier<EntityType<LExplosiveProjectile>> dynamite) {
		super(properties);
		this.dynamite = dynamite;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		usageTick(level, player, player.getStackInHand(hand) , player.getStackInHand(hand).getCount());
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}
	
	@Override
	public void usageTick(World level, LivingEntity player, ItemStack stack, int count) {
		if(player instanceof ServerPlayerEntity sPlayer && dynamite != null) {
			shoot(level, player.getX(), player.getY() + player.getStandingEyeHeight(), player.getZ(), player.getRotationVec(1), 2, player);		
			if(!sPlayer.isCreative()) {
				stack.decrement(1);
			}
		}
	}
	
	/**
	 * Spawns a new {@link LExplosiveProjectile} held by this item and launches it in the given direction.
	 * @param level  the current level
	 * @param x  the x position
	 * @param y  the y position (eye level needs to be added manually!)
	 * @param z  the z position
	 * @param direction  the direction the projectile will be thrown in
	 * @param power  the power with which the projectile is thrown
	 * @param thrower  the owner for the spawned projectile (used primarely for the {@link DamageSource})
	 * @return {@link LExplosiveProjectile} or null
	 * @throws NullPointerException
	 */
	@Nullable
	public LExplosiveProjectile shoot(World level, double x, double y, double z, Vec3d direction, float power, @Nullable LivingEntity thrower) throws NullPointerException {
		if(dynamite != null) {
			LExplosiveProjectile dyn = dynamite.get().create(level);
			dyn.setPosition(x, y, z);
			dyn.setVelocity(direction.x, direction.y, direction.z, power, 0);
			dyn.setOwner(thrower);
			level.spawnEntity(dyn);
			level.playSound(null, new BlockPos((int)x, (int)y, (int)z), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.MASTER, 1, 0.5f);
			return dyn;
		}
		throw new NullPointerException("Explosive projectile entity type is null");
	}
}
