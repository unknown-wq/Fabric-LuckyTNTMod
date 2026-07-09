package luckytntlib.entity;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import luckytntlib.block.LTNTBlock;
import luckytntlib.item.LTNTMinecartItem;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * The LuckyTNTMinecart is an extension of the {@link LTNTMinecart}
 * and turns into a random {@link LTNTMinecart} of a list when fused.
 */
public class LuckyTNTMinecart extends LTNTMinecart{

	private List<Supplier<LTNTMinecartItem>> minecarts;
	
	public LuckyTNTMinecart(EntityType<LTNTMinecart> type, World level, Supplier<LTNTBlock> defaultRender, Supplier<Supplier<LTNTMinecartItem>> pickItem, List<Supplier<LTNTMinecartItem>> minecarts) {
		super(type, level, null, pickItem, false);
		effect = new PrimedTNTEffect() {
			@Override
			public Block getBlock() {
				return defaultRender.get();
			}
		};
		this.minecarts = minecarts;
		setTNTFuse(-1);
	}
	
	@Override
	public void fuse() {
		LTNTMinecart minecart = minecarts.get(new Random().nextInt(minecarts.size())).get().createMinecart(getWorld(), getX(), getY(), getZ(), placer);
		minecart.setYaw(getYaw());
		minecart.setVelocity(getVelocity());
		getWorld().spawnEntity(minecart);
		minecart.fuse();
		discard();
	}
}
