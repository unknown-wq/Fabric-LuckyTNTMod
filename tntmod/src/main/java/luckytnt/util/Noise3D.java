package luckytnt.util;

/**
 * A simple value noise grid.
 * <p>
 * Storage note: this used to be a {@code NoisePoint[][][]} with one {@code NoisePoint} and one {@code Vec3} object
 * per cell, which meant ~1 MB of object headers and ~29 MB of garbage for the sizes the Honey TNT used.
 * The grid is now backed by two flat primitive arrays. The generation algorithm, the iteration order and the
 * {@link Math#random()} call order are unchanged, so the produced values are the same as before for the same inputs.
 */
public class Noise3D {

	/** highest valid index on each axis (constructor argument - 1), kept public for source compatibility */
	public final int sizeX;
	public final int sizeY;
	public final int sizeZ;
	public final int scale;

	private final int dimY;
	private final int dimZ;
	private final double[] values;
	private final boolean[] set;

	public Noise3D(int sizeX, int sizeY, int sizeZ, int scale) {
		this.sizeX = sizeX - 1;
		this.sizeY = sizeY - 1;
		this.sizeZ = sizeZ - 1;
		this.scale = scale;
		this.dimY = sizeY;
		this.dimZ = sizeZ;
		this.values = new double[sizeX * sizeY * sizeZ];
		this.set = new boolean[sizeX * sizeY * sizeZ];
		calculateStartingPoints();
		calculateNoise();
	}

	private int index(int x, int y, int z) {
		return (x * dimY + y) * dimZ + z;
	}

	private void setValue(int x, int y, int z, double value) {
		int i = index(x, y, z);
		values[i] = value;
		set[i] = true;
	}

	private double value(int x, int y, int z) {
		return values[index(x, y, z)];
	}

	private boolean isSet(int x, int y, int z) {
		return set[index(x, y, z)];
	}

	public void calculateStartingPoints() {
		//Starting Points calculation
		for(int x = 0; x < sizeX; x += scale) {
			for(int y = 0; y < sizeY; y += scale) {
				for(int z = 0; z < sizeZ; z += scale) {
					if(!(x > sizeX) && !(y > sizeY)) {
						if((x == 0 || y == 0 || z == 0) || (x == sizeX || y == sizeY || z == sizeZ)) {
							setValue(x, y, z, 0);
						}
						else {
							setValue(x, y, z, Math.random());
						}
					}
				}
			}
		}
	}

	public void calculateNoise() {
		//X-Line calculation
		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y += scale) {
				for(int z = 0; z < sizeZ; z += scale) {
					if(!isSet(x, y, z)) {
						int previousX = x - (x % scale);
						int nextX = (previousX + scale) >= sizeX ? sizeX - 1 : (previousX + scale);
						setValue(x, y, z, value(previousX, y, z) - /*lerp calculation ->*/(value(previousX, y, z) / scale - value(nextX, y, z) / scale) * (x % scale) /*<- step calculation*/);
					}
				}
			}
		}
		//Plane calculation
		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z += scale) {
					if(!isSet(x, y, z)) {
						int previousY = y - (y % scale);
						int nextY = (previousY + scale) >= sizeY ? sizeY - 1 : (previousY + scale);
						setValue(x, y, z, value(x, previousY, z) - /*lerp calculation ->*/(value(x, previousY, z) / scale - value(x, nextY, z) / scale) * (y % scale) /*<- step calculation*/);
					}
				}
			}
		}
		//3D calculation
		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {
					if(!isSet(x, y, z)) {
						int previousZ = z - (z % scale);
						int nextZ = (previousZ + scale) >= sizeZ ? sizeZ - 1 : (previousZ + scale);
						setValue(x, y, z, value(x, y, previousZ) - /*lerp calculation ->*/(value(x, y, previousZ) / scale - value(x, y, nextZ) / scale) * (z % scale) /*<- step calculation*/);
					}
				}
			}
		}
	}

	public double getValue(int x, int y, int z) {
		if(x > sizeX || y > sizeY || z > sizeZ || x == 0 || y == 0 || z == 0) {
			return 0;
		}
		else {
			return value(x, y, z);
		}
	}
}
