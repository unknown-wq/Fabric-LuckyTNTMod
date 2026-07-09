package luckytnt.tnteffects;


import org.joml.Vector3f;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

public class ExtinctionEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		if(ent.getLevel() instanceof ServerWorld sLevel) {
			try {
				sLevel.getServer().getCommandManager().getDispatcher().execute("kill @e", new ServerCommandSource(CommandOutput.DUMMY, ent.getPos(), Vec2f.ZERO, sLevel, 4, "", Text.literal(""), ((Entity)ent).getServer(), ent.owner()));
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 0f, 0f), 1), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
		ent.getLevel().addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 1f), 1), ent.x(), ent.y() + 1f, ent.z(), 0, 0, 0);
	}
	
	@Override
	public Block getBlock() {
		return BlockRegistry.EXTINCTION.get();
	}
}
