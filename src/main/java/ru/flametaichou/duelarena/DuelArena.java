package ru.flametaichou.duelarena;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import ru.flametaichou.duelarena.Handlers.ChatHandler;
import ru.flametaichou.duelarena.Handlers.DuelArenaAdminCommands;
import ru.flametaichou.duelarena.Handlers.DuelArenaPlayerCommands;
import ru.flametaichou.duelarena.Handlers.ServerEventHandler;
import ru.flametaichou.duelarena.Model.ArenaEntity;
import ru.flametaichou.duelarena.Util.ConfigHelper;
import ru.flametaichou.duelarena.Util.DatabaseHelper;

@Mod (modid = "duelarena", name = "Duel Arena", version = "0.1", acceptableRemoteVersions = "*")

public class DuelArena {

	public static ArenaEntity arena;
	public static DatabaseHelper database;
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		arena = new ArenaEntity();
		event.registerServerCommand(new DuelArenaPlayerCommands());
		event.registerServerCommand(new DuelArenaAdminCommands());
		FMLCommonHandler.instance().bus().register(new ServerEventHandler());
		MinecraftForge.EVENT_BUS.register(new ChatHandler());
	}

	@EventHandler
	public void load(FMLPreInitializationEvent event) {
		ConfigHelper.setupConfig(new Configuration(event.getSuggestedConfigurationFile()));
		String url = ConfigHelper.url;
		String dbName = ConfigHelper.dbName;
		String driver = ConfigHelper.driver;
		String userName = ConfigHelper.userName;
		String password = ConfigHelper.password;
		database = new DatabaseHelper(url, dbName, driver, userName, password);
	}
	
}
