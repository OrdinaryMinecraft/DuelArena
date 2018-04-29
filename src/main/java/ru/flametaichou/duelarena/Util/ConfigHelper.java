package ru.flametaichou.duelarena.Util;

import net.minecraftforge.common.config.Configuration;

import java.util.Arrays;
import java.util.List;

public class ConfigHelper {

    public static String url;
    public static String dbName;
    public static String driver;
    public static String userName;
    public static String password;

    public static int timeout;
    public static int spectator_x;
    public static int spectator_y;
    public static int spectator_z;

    public static List<String> availableWorlds;
    public static String arena_world;

    public ConfigHelper() {
    }

    public static void setupConfig(Configuration config) {
        try {
            config.load();
            config.addCustomCategoryComment("Settings", "Mod settings.");
            config.addCustomCategoryComment("Database", "Database settings.");

            url = config.getString("URL", "Database", "jdbc:mysql://localhost/", "Database URL");
            dbName = config.getString("DB Name", "Database", "duelarena", "Database Name");
            driver = config.getString("Driver", "Database", "com.mysql.jdbc.Driver", "Database Driver");
            userName = config.getString("User", "Database", "root", "Database User");
            password = config.getString("Password", "Database", "password", "Database Password");
            driver = config.getString("Driver", "Database", "com.mysql.jdbc.Driver", "Database Driver");

            timeout = config.getInt("Timeout", "Settings", 60, 10 ,9999, "Timeout between requests");
            spectator_x = config.getInt("Spectator X", "Settings", 0, -99999, 99999, "Spectator Position X");
            spectator_y = config.getInt("Spectator Y", "Settings", 0, -99999, 99999, "Spectator Position Y");
            spectator_z = config.getInt("Spectator Z", "Settings", 0, -99999, 99999, "Spectator Position Z");

            availableWorlds = Arrays.asList(config.getStringList("Alailable Worlds IDs", "Lists", new String[]{"0", "1", "-1"}, "Worlds IDs where mod will search players for duel"));
            arena_world = config.getString("Arena World ID", "Database", "0", "Arena World ID");

        } catch(Exception e) {
            System.out.println("A severe error has occured when attempting to load the config file for this mod!");
        } finally {
            if(config.hasChanged()) {
                config.save();
            }
        }
    }
}
