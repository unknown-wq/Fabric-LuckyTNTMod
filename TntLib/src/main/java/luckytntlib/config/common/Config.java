package luckytntlib.config.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * The Config class provides the ability to store some values as extra configuration for your mod. 
 * <p>
 * This class is only the base class for the different types of configs 
 * 
 * @see ClientConfig
 * @see ServerConfig
 */
public abstract class Config {
	
	protected final String modid;
	protected final List<ConfigValue<?>> configValues;
	protected final Optional<UpdatePacketCreator> packetCreator;
	
	/**
	 * Creates a new Config
	 * @param modid  the name for the file the data will be stored to. Should be the name of the mod the values are meant for
	 * @param configValues  a list of all values that should be stored within this config
	 * @param packetCreator  the {@link Optional} that can hold the packet that will automatically update the values on the clients if the instance of this config is a {@link ServerConfig} when {@link Config#save(World)} is called
	 */
	protected Config(String modid, List<ConfigValue<?>> configValues, Optional<UpdatePacketCreator> packetCreator) {
		this.modid = modid;
		this.configValues = configValues;
		this.packetCreator = packetCreator;
	}
	
	/**
	 * Should be called right after the Config is created.
	 * <p>
	 * Creates a {@link File} that will store the values if no file exists yet.<br>
	 * If a file exists already it will load the saved values from that {@link File}
	 */
	public abstract void init();
	
	/**
	 * Saves the currently stored values of the {@link ConfigValue}s to a {@link File}. <br>
	 * Should be called after any changes to the values.
	 * 
	 * @param world  used to ensure that the file saving will only be initiated from the server. Only neccessary if the instance of the config is a {@link ServerConfig}
	 * 
	 */
	public abstract void save(@Nullable World world);
	
	/**
	 * Saves the values to a {@link File}
	 * @param file  the {@link File} the {@link ConfigValue}s will be stored to
	 */
	protected void createConfigFile(File file) {
		try {
			file.createNewFile();
			
			FileWriter writer = new FileWriter(file);
			JsonWriter json = new JsonWriter(writer);
			
			json.setIndent("\t");
			json.beginObject();
			
			for(ConfigValue<?> value : configValues) {
				if(value instanceof IntValue intval) {
					json.name(value.getName()).value(intval.get());
				} else if(value instanceof DoubleValue dval) {
					json.name(value.getName()).value(dval.get());
				} else if(value instanceof BooleanValue bval) {
					json.name(value.getName()).value(bval.get());
				} else if(value instanceof EnumValue eval) {
					json.name(value.getName()).value(eval.getNameOfValue());
				}
			}
			
			json.endObject();
			
			json.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Loads the {@link ConfigValue}s values from a {@link File} and and updates the values
	 * @param file  the {@link File} the {@link ConfigValue}s will be loaded from
	 */
	protected void loadConfigValues(File file) {
		try {
			FileReader reader = new FileReader(file);
			JsonReader json = new JsonReader(reader);
			
			json.beginObject();
			
			while(json.hasNext()) {
				String name = json.nextName();
				
				for(ConfigValue<?> value : configValues) {
					if(value.getName().equals(name)) {
						if(value instanceof IntValue intval) {
							intval.set(json.nextInt());
						} else if(value instanceof DoubleValue dval) {
							dval.set(json.nextDouble());
						} else if(value instanceof BooleanValue bval) {
							bval.set(json.nextBoolean());
						} else if(value instanceof EnumValue eval) {
							eval.set(eval.getValueByName(json.nextString()));
						}
					}
				}
			}
			
			json.endObject();
			
			json.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return {@link Config#configValues}
	 */
	public List<ConfigValue<?>> getConfigValues() {
		return configValues;
	}
	
	/**
	 * Writes the data from a {@link List} of {@link ConfigValue}s to a {@link NbtCompound}. <br>
	 * Mainly used by {@link CustomPayload}s to serialize the data
	 * 
	 * @param values  the values that will be written to a new {@link NbtCompound}
	 * @return a {@link NbtCompound} that contains all values that were given to this method via <code>values</code>
	 */
	public static NbtCompound valuesToNbtCompound(List<ConfigValue<?>> values) {
		NbtCompound tag = new NbtCompound();
		
		for(ConfigValue<?> value : values) {
			if(value instanceof IntValue intval) {
				tag.putInt(value.getName(), intval.get().intValue());
			} else if(value instanceof DoubleValue dval) {
				tag.putDouble(value.getName(), dval.get().doubleValue());
			} else if(value instanceof BooleanValue bval) {
				tag.putBoolean(value.getName(), bval.get().booleanValue());
			} else if(value instanceof EnumValue eval) {
				tag.putString(value.getName(), eval.getNameOfValue());
			}
		}
		
		return tag;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Writes the data from a {@link NbtCompound} to a {@link List} of {@link ConfigValue}s. <br>
	 * Mainly used for handling {@link Packet}s
	 * 
	 * @param tag  a {@link NbtCompound} that contains the values 
	 * @param values  the {@link List} of {@link ConfigValue}s the data from <code>tag</code> will be written to
	 */
	public static void writeToValues(NbtCompound tag, List<ConfigValue<?>> values) {
		for(ConfigValue<?> value : values) {
			if(tag.contains(value.getName())) {
				if(value instanceof IntValue intval && tag.get(value.getName()) instanceof NbtInt nbt) {
					intval.set(nbt.intValue());
				} else if(value instanceof DoubleValue dval && tag.get(value.getName()) instanceof NbtDouble nbt) {
					dval.set(nbt.doubleValue());
				} else if(value instanceof BooleanValue bval && tag.get(value.getName()) instanceof NbtByte nbt) {
					bval.set(nbt.byteValue() == 0 ? false : true);
				} else if(value instanceof EnumValue eval && tag.get(value.getName()) instanceof NbtString nbt) {
					eval.set(eval.getValueByName(nbt.asString()));
				}
			}
		}
	}

	/**
	 * A Builder used to either create a {@link ServerConfig} or a {@link ClientConfig}.
	 */
	public static class Builder {
		
		private String id = "";
		private List<ConfigValue<?>> values = new ArrayList<>();
		private Optional<UpdatePacketCreator> packetCreator = Optional.empty();
		
		private Builder() {
		}
		
		/**
		 * Creates a new Builder with a name
		 * @param modid  the name the config {@link File} will have
		 * @return the new {@link Builder}
		 */
		public static Builder of(String modid) {
			Builder builder = new Builder();
			builder.id = modid;
			return builder;
		}
		
		public Builder addConfigValue(ConfigValue<?> value) {
			values.add(value);
			return this;
		}
		
		/**
		 * Optional. <br>
		 * Can be used to determine a packet that will be automatically send when calling {@link Config#save(World)} if the {@link Config} is a {@link ServerConfig}
		 *  
		 * @param creator  a {@link UpdatePacketCreator} 
		 * @return  a {@link Builder}
		 */
		public Builder setPacketCreator(UpdatePacketCreator creator) {
			packetCreator = Optional.of(creator);
			return this;
		}
		
		/**
		 * Builds the specified data into a <code>new</code> {@link ServerConfig}
		 * @return a <code>new</code> {@link ServerConfig}
		 */
		public ServerConfig buildServer() {
			return new ServerConfig(id, values, packetCreator);
		}
		
		/**
		 * Builds the specified data into a <code>new</code> {@link ClientConfig}
		 * @return a <code>new</code> {@link ClientConfig}
		 */
		public ClientConfig buildClient() {
			return new ClientConfig(id, values, packetCreator);
		}
	}
	
	/**
	 * Represents the base class for config values that will be stored by a {@link Config}
	 * @param <T> the type of value that should be saved with a ConfigValue
	 */
	public static abstract class ConfigValue<T> implements Supplier<T> {
		
		protected final T defaultValue;
		protected T value;
		protected final String name;
		
		/**
		 * Creates a new ConfigValue
		 * @param defaultValue  the value this ConfigValue will represent if the value is not set or edited
		 * @param name  the name of this ConfigValue
		 */
		private ConfigValue(T defaultValue, String name) {
			this.defaultValue = defaultValue;
			this.name = name;
		}
		
		/**
		 * Sets the value 
		 * @param value  the new value
		 */
		public void set(T value) {
			this.value = value;
		}

		@Override
		/**
		 * Returns {@link ConfigValue#value} if it's not <code>null</code>. <br>
		 * Otherwise returns {@link ConfigValue#defaultValue}
		 * 
		 * @return the stored value or the default value
		 */
		public T get() {
			return value != null ? value : defaultValue;
		}
		
		/**
		 * Returns {@link ConfigValue#defaultValue}
		 * @return the default value
		 */
		public T getDefault() {
			return defaultValue;
		}

		/**
		 Returns {@link ConfigValue#name}
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}
	
	/**
	 * IntValue is a extension of {@link ConfigValue} that can store {@link Integer}
	 */
	public static class IntValue extends ConfigValue<Integer> {
		
		protected final int minValue;
		protected final int maxValue;
		
		/**
		 * Creates a new IntValue
		 * @param defaultValue  the default value
		 * @param minValue  the minimum value this {@link ConfigValue} should be able to have
		 * @param maxValue  the maximum value this {@link ConfigValue} should be able to have
		 * @param name  the name of this {@link ConfigValue}
		 * 
		 * @throws IllegalAccessException if <code>minValue</code> is bigger than <code>maxValue</code>, <code>minValue</code> is bigger than <code>defaultValue</code> or <code>maxValue</code> is smaller than <code>defaultValue</code>
		 */
		public IntValue(int defaultValue, int minValue, int maxValue, String name) {
			super(defaultValue, name);
			this.minValue = minValue;
			this.maxValue = maxValue;
			
			if(defaultValue > maxValue || defaultValue < minValue || minValue > maxValue) {
				throw new IllegalArgumentException("Value bounds are arbitrary for Config Value \"" + name + "\"");
			}
		}

		@Override
		/**
		 * Sets the value. <br>
		 * <code>value</code> will be clamped to be between {@link IntValue#minValue} and {@link IntValue#maxValue}
		 * @param value  the new value
		 */
		public void set(Integer value) {
			this.value = MathHelper.clamp(value, minValue, maxValue);
		}
	}
	
	/**
	 * DoubleValue is a extension of {@link ConfigValue} that can store {@link Double}
	 */
	public static class DoubleValue extends ConfigValue<Double> {
		
		protected final double minValue;
		protected final double maxValue;
		
		/**
		 * Creates a new DoubleValue
		 * @param defaultValue  the default value
		 * @param minValue  the minimum value this {@link ConfigValue} should be able to have
		 * @param maxValue  the maximum value this {@link ConfigValue} should be able to have
		 * @param name  the name of this {@link ConfigValue}
		 * 
		 * @throws IllegalAccessException if <code>minValue</code> is bigger than <code>maxValue</code>, <code>minValue</code> is bigger than <code>defaultValue</code> or <code>maxValue</code> is smaller than <code>defaultValue</code>
		 */
		public DoubleValue(double defaultValue, double minValue, double maxValue, String name) {
			super(defaultValue, name);
			this.minValue = minValue;
			this.maxValue = maxValue;
			
			if(defaultValue > maxValue || defaultValue < minValue || minValue > maxValue) {
				throw new IllegalArgumentException("Value bounds are arbitrary for Config Value \"" + name + "\"");
			}
		}

		@Override
		/**
		 * Sets the value. <br>
		 * <code>value</code> will be clamped to be between {@link DoubleValue#minValue} and {@link DoubleValue#maxValue}
		 * @param value  the new value
		 */
		public void set(Double value) {
			this.value = MathHelper.clamp(value, minValue, maxValue);
		}
	}
	
	/**
	 * BooleanValue is a extension of {@link ConfigValue} that can store {@link Boolean}
	 */
	public static class BooleanValue extends ConfigValue<Boolean> {
		
		/**
		 * Creates a new BooleanValue
		 * @param defaultValue  the default value
		 * @param the name of this {@link ConfigValue}
		 */
		public BooleanValue(boolean defaultValue, String name) {
			super(defaultValue, name);
		}
	}
	
	/**
	 * EnumValue is a extension of {@link ConfigValue} that can store the values of an {@link Enum}. <br>
	 * The {@link Enum} has to implement {@link StringRepresentable}.
	 */
	public static class EnumValue<T extends Enum<T> & StringRepresentable> extends ConfigValue<T> {
		
		protected T[] values;
		
		/**
		 * Creates a new EnumValue
		 * @param defaultValue  the default value
		 * @param name  the name of this {@link ConfigValue}
		 * @param values  all possible values the {@link ConfigValue} can have. Using {@code Enum.values()} is recommended in most cases
		 */
		public EnumValue(T defaultValue, String name, T[] values) {
			super(defaultValue, name);
			this.values = values;
		}
		
		/**
		 * Gets the name of the current enum value
		 * @return the name of the current enum value
		 */
		public String getNameOfValue() {
			return get().getString();
		}
		
		/**
		 * Gets the enum value that has the <code>name</code> given to this method
		 * @param name  the name of the value you want to have
		 * @return the value of the enum that has the specified name or {@link ConfigValue#defaultValue} if it doesn't exist
		 */
		public T getValueByName(String name) {
			for(T t : values) {
				if(t.getString().equals(name)) {
					return t;
				}
			}
			return defaultValue;
		}
	}
	
	@FunctionalInterface
	public interface UpdatePacketCreator {
		public CustomPayload getPacket(List<ConfigValue<?>> configValues);
	}
}
