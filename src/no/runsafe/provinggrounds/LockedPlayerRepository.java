package no.runsafe.provinggrounds;

import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LockedPlayerRepository extends Repository
{
	public LockedPlayerRepository(IDatabase database)
	{
		this.database = database;
	}

	@Override
	public String getTableName()
	{
		return "provingGrounds_lockedPlayers";
	}

	public void lockPlayer(RunsafePlayer player)
	{
		database.Update("INSERT IGNORE INTO provingGrounds_lockedPlayers (player) VALUES(?)", player.getName());
	}

	public List<String> getLockedPlayers()
	{
		return database.QueryStrings("SELECT player FROM provingGrounds_lockedPlayers");
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> queries = new HashMap<Integer, List<String>>();
		ArrayList<String> sql = new ArrayList<String>();
		sql.add(
				"CREATE TABLE `provingGrounds_lockedPlayers` (" +
					"`player` VARCHAR(30) NOT NULL," +
					"PRIMARY KEY (`player`)" +
				")"
		);
		queries.put(1, sql);
		return queries;
	}

	private IDatabase database;
}
