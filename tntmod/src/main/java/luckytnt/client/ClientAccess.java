package luckytnt.client;

import luckytnt.LevelVariables;
import luckytnt.client.gui.ConfigScreen;
import luckytnt.util.NuclearBombLike;
import luckytntlib.config.common.ConfigScreenFactory;
import luckytntlib.util.IExplosiveEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;

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
		MinecraftClient minecraft = MinecraftClient.getInstance();
		Entity ent = minecraft.world.getEntityById(id);
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
