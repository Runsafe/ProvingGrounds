package no.runsafe.provinggrounds;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.player.IPlayerMove;
import no.runsafe.framework.api.event.player.IPlayerRightClick;
import no.runsafe.framework.api.event.player.IPlayerTeleport;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.List;

public class Event implements IConfigurationChanged, IPlayerTeleport, IPlayerMove, IPlayerRightClick
{
	public Event(IOutput console, SkullsRepository skullsRepository, LockedPlayerRepository lockedPlayerRepository)
	{
		this.console = console;
		this.skullsRepository = skullsRepository;
		this.lockedPlayerRepository = lockedPlayerRepository;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		eventLocation = configuration.getConfigValueAsLocation("eventLocation");
		removeLocation = configuration.getConfigValueAsLocation("removeLocation");
		teleportLocation = configuration.getConfigValueAsLocation("teleportLocation");

		if (eventLocation != null)
			world = eventLocation.getWorld();

		skulls = skullsRepository.getSkulls();
		lockedPlayers = lockedPlayerRepository.getLockedPlayers();

		RunsafePlayer boss = RunsafeServer.Instance.getPlayerExact("Skalamandar");
		bossName = boss == null ? "Skalamandar" : boss.getPrettyName();

		boolean remaining = false;
		for (Skull skull : skulls)
		{
			if (!skull.isLooted())
				remaining = true;
		}

		if (remaining)
		{
			console.fine("We have remaining skulls, setting the event to running!");
			isRunning = true;
		}
	}

	@Override
	public boolean OnPlayerMove(RunsafePlayer player, RunsafeLocation from, RunsafeLocation to)
	{
		if (teleportLocation != null && to.getWorld().isWorld(teleportLocation.getWorld()) && to.distance(teleportLocation) < 2)
		{
			if (isPlayerLocked(player))
				player.sendColouredMessage("&cYou have already completed the proving grounds!");
			else if (!isRunning)
				player.sendColouredMessage("&cThe event is now over!");
			else
				player.teleport(eventLocation);
		}

		return true;
	}

	@Override
	public boolean OnPlayerTeleport(RunsafePlayer player, RunsafeLocation from, RunsafeLocation to)
	{
		if (!isRunning && removeLocation != null && world != null && to.getWorld().isWorld(world))
			player.teleport(removeLocation);

		return true;
	}

	@Override
	public boolean OnPlayerRightClick(RunsafePlayer player, RunsafeMeta usingItem, RunsafeBlock block)
	{
		console.fine("We detected a right click event");
		if (block != null)
		{
			console.fine("Detected right click on a skull");
			RunsafeLocation location = block.getLocation();
			boolean remaining = false;

			for (Skull skull : skulls)
			{
				if (skull.isThisSkull(location) && !skull.isLooted())
				{
					console.fine("We found a match that was not looted!");
					skull.pickupSkull();
					lockedPlayerRepository.lockPlayer(player);
					lockedPlayers.add(player.getName());

					if (removeLocation != null)
					{
						player.teleport(removeLocation);
						bossWhisper(player, "Well done, you have completed the proving grounds and demonstrated to us your skills. That is all, for now.");
					}

					skullsRepository.saveSkull(skull);
				}

				if (!skull.isLooted())
					remaining = true;
			}

			if (!remaining)
			{
				isRunning = false;
				if (world != null)
					for (RunsafePlayer worldPlayer : world.getPlayers())
						worldPlayer.teleport(removeLocation);

				bossSpeak("The trial is complete. We have observed all of you and gathered what information we needed. The winners shall be granted a small prize for your efforts, but this is only the first test. Come back soon for the next.");
			}
		}
		return true;
	}

	private boolean isPlayerLocked(RunsafePlayer player)
	{
		return lockedPlayers.contains(player.getName());
	}

	public void bossWhisper(RunsafePlayer player, String message)
	{
		player.sendColouredMessage("%s &3-> %s", bossName, message);
	}

	public void bossSpeak(String message)
	{
		RunsafeServer.Instance.broadcastMessage(String.format("%s&f: %s", bossName, message));
	}

	private IOutput console;
	private String bossName;
	private RunsafeWorld world;
	private RunsafeLocation eventLocation;
	private RunsafeLocation removeLocation;
	private RunsafeLocation teleportLocation;
	private boolean isRunning = false;
	private List<Skull> skulls;
	private SkullsRepository skullsRepository;
	private LockedPlayerRepository lockedPlayerRepository;
	private List<String> lockedPlayers;
}
