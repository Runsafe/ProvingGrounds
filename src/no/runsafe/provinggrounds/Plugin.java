package no.runsafe.provinggrounds;

import no.runsafe.framework.RunsafeConfigurablePlugin;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		addComponent(LockedPlayerRepository.class);
		addComponent(SkullsRepository.class);
		addComponent(Event.class);
	}
}
