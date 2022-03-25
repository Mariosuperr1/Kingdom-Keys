package online.kingdomkeys.kingdomkeys.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class GlobalCapabilities implements IGlobalCapabilities {

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag storage = new CompoundTag();
		storage.putInt("ticks_stopped", this.getStoppedTicks());
		storage.putFloat("stop_dmg", this.getDamage());
		storage.putInt("ticks_flat", this.getFlatTicks());
		return storage;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		CompoundTag properties = (CompoundTag) nbt;
		this.setStoppedTicks(properties.getInt("ticks_stopped"));
		this.setDamage(properties.getFloat("stop_dmg"));
		this.setFlatTicks(properties.getInt("ticks_flat"));
	}

	private int timeStopped, flatTicks;
	float stopDmg;
	private String stopCaster;

	@Override
	public void setStoppedTicks(int time) {
		this.timeStopped = time;
	}

	@Override
	public int getStoppedTicks() {
		return timeStopped;
	}

	@Override
	public void subStoppedTicks(int time) {
		this.timeStopped -= time;
	}

	@Override
	public float getDamage() {
		return stopDmg;
	}

	@Override
	public void setDamage(float dmg) {
		this.stopDmg = dmg;
	}

	@Override
	public void addDamage(float dmg) {
		this.stopDmg+=dmg;
	}


	@Override
	public void setStopCaster(String name) {
		this.stopCaster = name;
	}

	@Override
	public String getStopCaster() {
		return this.stopCaster;
	}

	@Override
	public int getFlatTicks() {
		return flatTicks;
	}

	@Override
	public void setFlatTicks(int time) {
		this.flatTicks = time;
	}

	@Override
	public void subFlatTicks(int time) {
		this.flatTicks -= time;
	}
}
