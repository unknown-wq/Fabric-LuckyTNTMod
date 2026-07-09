package luckytnt.tnteffects;

import luckytnt.registry.BlockRegistry;
import luckytntlib.util.IExplosiveEntity;
import luckytntlib.util.explosions.ImprovedExplosion;
import luckytntlib.util.tnteffects.PrimedTNTEffect;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class IlluminatiTNTEffect extends PrimedTNTEffect {

	@Override
	public void serverExplosion(IExplosiveEntity ent) {
		BlockPos pos = toBlockPos(ent.getPos());
		
		BlockPos A = pos.add(-60, -60, -60);
		BlockPos B = pos.add(60, -60, -60);
		BlockPos C = pos.add(60, -60, 60);
		BlockPos D = pos.add(-60, -60, 60);
		BlockPos E = pos.add(0, 60, 0);
		
		Vec3d EA = new Vec3d(A.getX() - E.getX(), A.getY() - E.getY(), A.getZ() - E.getZ());
		Vec3d EB = new Vec3d(B.getX() - E.getX(), B.getY() - E.getY(), B.getZ() - E.getZ());
		Vec3d EC = new Vec3d(C.getX() - E.getX(), C.getY() - E.getY(), C.getZ() - E.getZ());
		Vec3d ED = new Vec3d(D.getX() - E.getX(), D.getY() - E.getY(), D.getZ() - E.getZ());
		
		Vec3d NEAB = EB.crossProduct(EA);
		Vec3d NEAD = EA.crossProduct(ED);
		Vec3d NEDC = ED.crossProduct(EC);
		Vec3d NECB = EC.crossProduct(EB);
		Vec3d NABCD = new Vec3d(0, -1, 0);
		
		for (int offX = -70; offX <= 70; offX++) {
			for (int offY = -70; offY <= 70; offY++) {
				for (int offZ = -70; offZ <= 70; offZ++) {
					Vec3d vec = new Vec3d(Math.round(ent.x() + offX), Math.round(ent.y() + offY), Math.round(ent.z() + offZ));

					if (distance(vec, NEAB, E) <= 0 && distance(vec, NEAD, E) <= 0 && distance(vec, NEDC, E) <= 0 && distance(vec, NECB, E) <= 0 && distance(vec, NABCD, A) <= 0) {
						BlockPos pos5 = toBlockPos(ent.getPos()).add(offX, offY, offZ);

						if (ent.getLevel().getBlockState(pos5).getBlock().getBlastResistance() <= 200) {
							ent.getLevel().getBlockState(pos5).getBlock().onDestroyedByExplosion(ent.getLevel(), pos5, ImprovedExplosion.dummyExplosion(ent.getLevel()));
							ent.getLevel().setBlockState(pos5, Blocks.AIR.getDefaultState(), 3);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void spawnParticles(IExplosiveEntity ent) {
		for(double i = 0D; i <= 1D; i += 0.2D) {
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D + i, ent.y() + 1D, ent.z() - 0.5D, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D + i, ent.y() + 1D, ent.z() + 0.5D, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D, ent.y() + 1D, ent.z() - 0.5D + i, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D, ent.y() + 1D, ent.z() - 0.5D + i, 0, 0, 0);
		}
		
		Vec3d vec1 = new Vec3d(0.5D, 1D, 0.5D);
		Vec3d vec2 = new Vec3d(-0.5D, 1D, 0.5D);
		Vec3d vec3 = new Vec3d(0.5D, 1D, -0.5D);
		Vec3d vec4 = new Vec3d(-0.5D, 1D, -0.5D);
		
		for(double i = 0D; i < vec1.length(); i += vec1.length() / 5D) {
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D + vec1.x * i, ent.y() + 1D + vec1.y * i, ent.z() - 0.5D + vec1.z * i, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D + vec2.x * i, ent.y() + 1D + vec2.y * i, ent.z() - 0.5D + vec2.z * i, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() - 0.5D + vec3.x * i, ent.y() + 1D + vec3.y * i, ent.z() + 0.5D + vec3.z * i, 0, 0, 0);
			ent.getLevel().addParticle(ParticleTypes.FLAME, ent.x() + 0.5D + vec4.x * i, ent.y() + 1D + vec4.y * i, ent.z() + 0.5D + vec4.z * i, 0, 0, 0);
		}
	}
	
	@Override
	public int getDefaultFuse(IExplosiveEntity ent) {
		return 120;
	}
	
	@Override
	public Block getBlock()  {
		return BlockRegistry.ILLUMINATI_TNT.get();
	}
	
	public double distance(Vec3d point, Vec3d normal, BlockPos pointOnSide) {
		return TetrahedronTNTEffect.distance(point, normal, pointOnSide);
	}
}
