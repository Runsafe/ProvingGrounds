package no.runsafe.provinggrounds;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.chunk.RunsafeChunk;

public class Skull
{
	public Skull(int x, int y, int z, boolean looted)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.looted = looted;
	}

	public void spawn(RunsafeWorld world)
	{
		RunsafeLocation location = new RunsafeLocation(world, x, y, z);
		allowEdit(location);
		location.getBlock().set(Item.Decoration.Head.Skeleton);
	}

	public void remove(RunsafeWorld world)
	{
		RunsafeLocation location = new RunsafeLocation(world, x, y, z);
		allowEdit(location);
		location.getBlock().set(Item.Unavailable.Air);
	}

	public boolean isThisSkull(RunsafeLocation location)
	{
		RunsafeLocation skullLocation = new RunsafeLocation(location.getWorld(), x, y, z);
		allowEdit(skullLocation);
		return location.distance(skullLocation) < 1;
	}

	private void allowEdit(RunsafeLocation location)
	{
		RunsafeChunk chunk = location.getChunk();
		if (!chunk.isUnloaded())
			chunk.load();
	}

	public void pickupSkull()
	{
		looted = true;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}

	public boolean isLooted()
	{
		return looted;
	}

	private int x;
	private int y;
	private int z;
	private boolean looted;
}
