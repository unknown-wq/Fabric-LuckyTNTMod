package luckytnt.tnteffects;


import org.joml.Vector3f;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;

public class ExtinctionEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent.getLevel() instanceof ServerLevel sLevel) {
			try {
				sLevel.getServer().getCommandManager().getDispatcher().execute("kill @e", new CommandSourceStack(CommandOutput.DUMMY, ent.getPos(), Vec2.ZERO, sLevel, 4, "", Component.literal(""), ((Entity)ent).getServer(), ent.owner()));
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(0f*255)<<8)|(int)(0f*255), 1), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleOptions(((int)(1f*255)<<16)|((int)(1f*255)<<8)|(int)(1f*255), 1), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.EXTINCTION.get();
	}
}
