package luckytnt.client;

import luckytnt.LevelVariables;
import luckytnt.client.gui.ConfigScreen;
import luckytnt.util.NuclearBombLike;
import luckytntlib.config.common.ConfigScreenFactory;
import luckytntlib.util.IExplosiveEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;

public class ClientAccess {
	
	private static final ConfigScreenFactory FACTORY = new ConfigScreenFactory() {
		
		@Override
		public Screen apply() {
			return new ConfigScreen();
		}
	};
	
	public static void syncLevelVariables(LevelVariables variables) {
		LevelVariables.clientSide = variables;
	}
	
	public static void displayHydrogenBombParticles(int id) {
		Minecraft minecraft = Minecraft.getInstance();
		Entity ent = minecraft.level == null ? null : minecraft.level.getEntity(id);
		if(ent != null) {
			if(ent instanceof IExplosiveEntity ient) {
				if(ient.getEffect() instanceof NuclearBombLike effect) {
					effect.displayMushroomCloud(ient);
				}
			}
		}
	}
	
	public static ConfigScreenFactory getFactory() {
		return FACTORY;
	}
}
