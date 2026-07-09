package luckytnt.tnteffects;

import java.lang.reflect.Field;

import luckytnt.entity.PrimedItemFirework;
import luckytnt.registry.BlockRegistry;
import luckytntlib.item.LDynamiteItem;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.SpectralArrowItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

public class ItemFireworkEffect extends PrimedTNTEffect {

	@Override
	public void explosionTick(IExplosiveEntity ent) {
		((Entity)ent).setDeltaMovement(((Entity)ent).getDeltaMovement().x, 0.8f, ((Entity)ent).getDeltaMovement().z);
	}
	
	@Override
	public void serverExplosion(IExplosiveEntity entity) {
		if(entity instanceof PrimedItemFirework ent) {
			Item item = ent.item;
			ItemStack stack = ent.stack == null ? ItemStack.EMPTY : ent.stack;
			stack.setCount(1);
			if(item == null) {
				item = Item.byRawId(ent.getPersistentData().getInt("itemID"));
			}
			if(item != null) { 
				if(item instanceof BoatItem boatitem) {
					boolean hasChest = false;
					BoatEntity.Type type = BoatEntity.Type.OAK;
					try {
						for(Field field : BoatItem.class.getDeclaredFields()) {
							field.setAccessible(true);
							if(field.get(boatitem) instanceof BoatEntity.Type t) {
								type = t;
							} else if(field.get(boatitem) instanceof Boolean b) {
								hasChest = b;
							}
						}
					} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					for(int i = 0; i < 300; i++) {
						BoatEntity boat = new BoatEntity(ent.getLevel(), ent.x(), ent.y(), ent.z());
						if(hasChest) {
							boat = new ChestBoatEntity(ent.getLevel(), ent.x(), ent.y(), ent.z());
						}
						boat.setVariant(type);
						boat.setDeltaMovement(Math.random() * 6D - 3D, Math.random() * 6D - 3D, Math.random() * 6D - 3D);
						ent.getLevel().addFreshEntity(boat);
					}
				} else if(item instanceof FireChargeItem) {
					double phi = Math.PI * (3D - Math.sqrt(5D));
					for(int i = 0; i < 300; i++) {
						double y = 1D - ((double)i / (300D - 1D)) * 2D;
						double radius = Math.sqrt(1D - y * y);
					
						double theta = phi * i;
					
						double x = Math.cos(theta) * radius;
						double z = Math.sin(theta) * radius;
						
						FireballEntity fireball = new FireballEntity(ent.getLevel(), ent.owner(), new Vec3((ent.x() + x * 15) - ent.x(), (ent.y() + y * 15) - ent.y(), (ent.z() + z * 15) - ent.z()).normalize().multiply(0.5D), 1);
						fireball.setPosition(ent.x() + x * 15, ent.y() + y * 15, ent.z() + z * 15);
						ent.getLevel().addFreshEntity(fireball);
					}
				} else if(item == Items.DRAGON_BREATH) {
					for(int i = 0; i < 300; i++) {
						DragonFireballEntity fireball = new DragonFireballEntity(ent.getLevel(), ent.owner(), new Vec3(Math.random() - 0.5f, Math.random() - 0.5f, Math.random() - 0.5f));
						fireball.setPosition(ent.getPos());
						ent.getLevel().addFreshEntity(fireball);
					}
				} else if(item instanceof ThrowablePotionItem) {
					for(int i = 0; i < 300; i++) {
						PotionEntity potion = new PotionEntity(ent.getLevel(), ent.x(), ent.y(), ent.z());
						potion.setItem(stack != null ? stack : new ItemStack(item));
						potion.setDeltaMovement(Math.random() * 3D - 1.5D, Math.random() * 3D - 1.5D, Math.random() * 3D - 1.5D);
						ent.getLevel().addFreshEntity(potion);
					}
				} else if(item instanceof ArrowItem) {
					for(int count = 0; count < 300; count++) {
						if(item instanceof SpectralArrowItem) {
							AbstractArrow arrow = new SpectralArrowEntity(ent.getLevel(), ent.x(), ent.y(), ent.z(), new ItemStack(Items.SPECTRAL_ARROW), null);
							arrow.setDeltaMovement(Math.random() * 6f - 3f, Math.random() * 6f - 3f, Math.random() * 6f - 3f);
							ent.getLevel().addFreshEntity(arrow);
						} else {
							ArrowEntity arrow = new ArrowEntity(ent.getLevel(), ent.x(), ent.y(), ent.z(), stack != null ? stack : new ItemStack(Items.ARROW), null);
							PotionContentsComponent potions = stack != null ? stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT) : PotionContentsComponent.DEFAULT;
							for(StatusEffectInstance effect : potions.getEffects()) {
								arrow.addEffect(effect);
							}
							arrow.setDeltaMovement(Math.random() * 6f - 3f, Math.random() * 6f - 3f, Math.random() * 6f - 3f);
							ent.getLevel().addFreshEntity(arrow);
						}
					}
				} else if(item instanceof EggItem) {
					for(int count = 0; count < 300; count++) {
						EggEntity egg = new EggEntity(ent.getLevel(), ent.x(), ent.y(), ent.z());
						egg.setDeltaMovement(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
						ent.getLevel().addFreshEntity(egg);
					}
				} else if(item instanceof SnowballItem) {
					for(int count = 0; count < 300; count++) {
						SnowballEntity ball = new SnowballEntity(ent.getLevel(), ent.x(), ent.y(), ent.z());
						ball.setDeltaMovement(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
						ent.getLevel().addFreshEntity(ball);
					}
				} else if(item instanceof LDynamiteItem dynamite) {
					for(int count = 0; count < 300; count++) {
						dynamite.shoot(ent.getLevel(), ent.x(), ent.y(), ent.z(), new Vec3(Math.random() * 6D - 3D, Math.random() * 6D - 3D, Math.random() * 6D - 3D), 1f + (float)Math.random(), null);
					}
				} else if(item instanceof FireworkRocketItem) {
					for(int count = 0; count < 300; count++) {
						FireworkRocketEntity rocket = new FireworkRocketEntity(ent.getLevel(), stack == null ? new ItemStack(item) : stack, ent.x(), ent.y(), ent.z(), true);
						rocket.setDeltaMovement(Math.random() * 2f - 1f, Math.random() * 2f - 1f, Math.random() * 2f - 1f);
						ent.getLevel().addFreshEntity(rocket);
					}
				} else {
					for(int i = 0; i < 300; i++) {
						ItemEntity itement = new ItemEntity(ent.getLevel(), ent.x(), ent.y(), ent.z(), stack == null ? new ItemStack(item) : stack.copy());
						itement.setDeltaMovement(Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f, Math.random() * 3f - 1.5f);
						ent.getLevel().addFreshEntity(itement);
					}
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x(), ent.y(), ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.ITEM_FIREWORK.get();
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 40;
	}
}
