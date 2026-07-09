package luckytntlib.util;

import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * IExplosiveEntity is implemented by all entities introduced by Lucky TNT Lib.
 * <p>
 * IExplosiveEntity is required because most of Minecraft's entities qualify as explosive entities but are not interchangeable.
 * Examples are {@link LivingEntity}, {@link PrimedTnt} and {@link AbstractArrow}.
 * <p>
 * It is advised to use methods given by this Interface rather than Minecraft's methods to make porting easier and to increase universalness.
 * @implNote Only entities implementing this Interface are capeable of using anything extending upon {@link PrimedTNTEffect}
 */
public interface IExplosiveEntity {

	/**
	 * Gets the current fuse of this IExplosiveEntity
	 * @return fuse
	 */
	public int getTNTFuse();

	/**
	 * Sets the fuse of this IExplosiveEntity.
	 *
	 * The fuse should not be set manually in most cases and is usually handled within {@link PrimedTNTEffect#baseTick(IExplosiveEntity)}
	 * @param fuse  the new fuse
	 */
	public void setTNTFuse(int fuse);

	/**
	 * Gets the {@link Level} of this IExplosiveEntity
	 * @return
	 */
	public Level getLevel();

	/**
	 * Gets the current position of this IExplosiveEntity
	 * @return pos
	 */
	public Vec3 getPos();

	/**
	 * Gets the current x position of this IExplosiveEntity
	 * @return x
	 */
	public double x();

	/**
	 * Gets the current y position of this IExplosiveEntity
	 * @return y
	 */
	public double y();

	/**
	 * Gets the current z position of this IExplosiveEntity
	 * @return z
	 */
	public double z();

	/**
	 * Calls the method {@link Entity#discard()}.
	 */
	public void destroy();

	/**
	 * Gets the {@link PrimedTNTEffect} of this IExplosiveEntity
	 *
	 * @return the PrimedTNTEffect
	 */
	public PrimedTNTEffect getEffect();

	/**
	 * Gets the Owner of this IExplosiveEntity.
	 *
	 * The Owner, usually a Player, of this IExplosiveEntity, which is mostly used for damage sources must not be set manually. It is automatically assigned in all classes implementing this Interface.
	 * @return the Owner
	 */
	public LivingEntity owner();

	/**
	 * Gets the synchronized {@link CompoundTag} that contains any custom data that is being saved
	 *
	 * @return the synchronized data
	 */
	public CompoundTag getPersistentData();

	/**
	 * Sets the synchronized {@link CompoundTag} that contains any custom data that is being saved
	 *
	 * @param tag  the synchronized data
	 */
	public void setPersistentData(CompoundTag tag);
}
