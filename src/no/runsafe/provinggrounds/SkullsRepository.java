package no.runsafe.provinggrounds;

import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.IRow;
import no.runsafe.framework.api.database.ISet;
import no.runsafe.framework.api.database.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkullsRepository extends Repository
{
	public SkullsRepository(IDatabase database)
	{
		this.database = database;
	}

	@Override
	public String getTableName()
	{
		return "provingGrounds_skulls";
	}

	public List<Skull> getSkulls()
	{
		List<Skull> skulls = new ArrayList<Skull>();
		ISet rows = database.Query("SELECT x, y, z, looted FROM provingGrounds_skulls");
		for (IRow row : rows)
			skulls.add(new Skull(row.Integer("x"), row.Integer("y"), row.Integer("z"), (row.Integer("looted") == 1)));

		return skulls;
	}

	public void saveSkull(Skull skull)
	{
		database.Update(
				"UPDATE skulls SET looted = ? WHERE x = ? AND y = ? AND z = ?",
				skull.isLooted(), skull.getX(), skull.getY(), skull.getZ()
		);
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> queries = new HashMap<Integer, List<String>>();
		ArrayList<String> sql = new ArrayList<String>();
		sql.add(
				"CREATE TABLE `provingGrounds_skulls` (" +
						"`x` INT(10) NOT NULL," +
						"`y` INT(10) NOT NULL," +
						"`z` INT(10) NOT NULL," +
						"`looted` TINYINT(1) NOT NULL DEFAULT 0" +
					")"
		);
		queries.put(1, sql);
		return queries;
	}

	private IDatabase database;
}
