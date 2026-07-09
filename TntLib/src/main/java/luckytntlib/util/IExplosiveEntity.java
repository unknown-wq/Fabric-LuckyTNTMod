package luckytntlib.util;

import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * IExplosiveEntity is implemented by all entities introduced by Lucky TNT Lib.
 * <p>
 * IExplosiveEntity is required because most of Minecraft's entities qualify as explosive entities but are not interchangeable.
 * Examples are {@link LivingEntity}, {@link TntEntity} and {@link PersistentProjectileEntity}.
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
	 * Gets the {@link World} of this IExplosiveEntity
	 * @return
	 */
	public World getLevel();

	/**
	 * Gets the current position of this IExplosiveEntity
	 * @return pos
	 */
	public Vec3d getPos();

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
	 * Gets the synchronized {@link NbtCompound} that contains any custom data that is being saved
	 * 
	 * @return the synchronized data
	 */
	public NbtCompound getPersistentData();
	
	/**
	 * Sets the synchronized {@link NbtCompound} that contains any custom data that is being saved
	 * 
	 * @param tag  the synchronized data
	 */
	public void setPersistentData(NbtCompound tag);
}
