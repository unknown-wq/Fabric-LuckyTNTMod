package luckytntlib.item;

import java.util.List;
import java.util.function.Supplier;

import luckytntlib.entity.LExplosiveProjectile;
import luckytntlib.registry.RegistryHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * The LuckyDynamiteItem is an extension of the {@link LDynamiteItem} and serves the simple purpose of spawning a random
 * {@link LExplosiveProjectile} of a {@link LDynamiteItem} contained in a {@link List}.
 * The list could for instance be set to one of the many lists of {@link LDynamiteItem} found in the {@link RegistryHelper}.
 */
public class LuckyDynamiteItem extends LDynamiteItem{

	public List<Supplier<LDynamiteItem>> dynamites;
	
	public LuckyDynamiteItem(Item.Settings properties, List<Supplier<LDynamiteItem>> dynamites) {
		super(properties, null);
		this.dynamites = dynamites;
	}
	
	@Override
	public void usageTick(World level, LivingEntity player, ItemStack stack, int count) {
		if(player instanceof ServerPlayerEntity sPlayer) {
			shoot(level, player.getX(), player.getY() + player.getStandingEyeHeight(), player.getZ(), player.getRotationVec(1), 2, player);		
			if(!sPlayer.isCreative()) {
				stack.decrement(1);
			}
		}
	}
	
	/**
	 * Gets a random {@link LDynamiteItem} from the list held by this item and calls its shoot method.
	 * Can not shoot another {@link LuckyDynamiteItem}.
	 * @param level  the current level
	 * @param x  the x position
	 * @param y  the y position (eye level needs to be added manually!)
	 * @param z  the z position
	 * @param direction  the direction the projectile will be thrown in
	 * @param power  the power with which the projectile is thrown
	 * @param thrower  the owner for the spawned projectile (used primarely for the {@link DamageSource})
	 * @return {@link LExplosiveProjectile}
	 */
	@Override
	public LExplosiveProjectile shoot(World level, double x, double y, double z, Vec3d direction, float power, LivingEntity thrower) {
		int rand = random.nextInt(dynamites.size());
		return dynamites.get(rand).get().shoot(level, x, y, z, direction, power, thrower);
	}
}
