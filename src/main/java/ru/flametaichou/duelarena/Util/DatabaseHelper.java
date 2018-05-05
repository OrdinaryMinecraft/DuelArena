package ru.flametaichou.duelarena.Util;

import ru.flametaichou.duelarena.Model.ArenaPoint;

import java.sql.*;
import java.util.ArrayList;

import java.util.List;

public class DatabaseHelper {

    public DatabaseHelper() { }

    public static Connection getConnection() {
        Connection dbConnection = null;
        String url = ConfigHelper.url;
        String dbName = ConfigHelper.dbName;
        String driver = ConfigHelper.driver;
        String userName = ConfigHelper.userName;
        String password = ConfigHelper.password;
        try {
            Class.forName(driver).newInstance();
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            dbConnection = DriverManager.getConnection(url+dbName,userName,password);
            System.out.println("Connected to the database");
        } catch (Exception e) {
            catchError(e.getMessage());
        }
        return dbConnection;
    }

    public static void closeConnection(Connection dbConnection) {
        try {
            dbConnection.close();
        } catch (SQLException e) {
            catchError(e.getMessage());
        }
        System.out.println("Disconnected from database");
    }

    public static void catchError (String error) {
        System.out.println("DATABASE ERROR: " + error);
    }

    public static void execute(String request) {
        Statement statement = null;
        try {
            Connection dbConnection = getConnection();
            statement = dbConnection.createStatement();
            statement.executeUpdate(request);
            closeConnection(dbConnection);
        } catch (SQLException e) {
            catchError(e.getMessage());
        }
    }

    public static List<ArenaPoint> fetchAllArenaPoints() {
        List<ArenaPoint> arenaPoints = new ArrayList<ArenaPoint>();
        try {
            Connection dbConnection = getConnection();
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM `duelarena-points`;");
            ResultSet resultSet= statement.executeQuery();

            while (resultSet.next()) {
                arenaPoints.add(new ArenaPoint(resultSet.getInt("id"), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")));
            }
            closeConnection(dbConnection);
        } catch (SQLException e) {
            catchError(e.getMessage());
        }
        return arenaPoints;
    }

    public static void addArenaPoint(Integer x, Integer y, Integer z) {
        execute("INSERT INTO `duelarena-points` (id, x, y, z) VALUES (NULL, "+x+", "+y+", "+z+");");
    }

    public static void removeArenaPoint(Integer id) {
        execute("DELETE FROM `duelarena-points` WHERE id="+id+";");
    }

    public static Integer getPlayerPoints(String playerName) {
        Integer points = null;
        try {
            Connection dbConnection = getConnection();
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM `honor-points` WHERE playername='"+playerName+"';");
            ResultSet resultSet= statement.executeQuery();

            if (resultSet.next()) {
                points = resultSet.getInt("points");
            } else {
                execute("INSERT INTO `honor-points` (playername, points) VALUES ('"+playerName+"', 100);");
                points = 100;
            }
            closeConnection(dbConnection);
        } catch (SQLException e) {
            catchError(e.getMessage());
        }
        return points;
    }

    public static void updatePlayerPoints(String playerName, Integer pointsCount) {
        Integer playerpoints = getPlayerPoints(playerName);
        playerpoints = playerpoints + pointsCount;
        if (playerpoints < 0) {
            playerpoints = 0;
        }
        execute("UPDATE `honor-points` SET points="+playerpoints+" WHERE playername='"+playerName+"';");
    }

    public static List<String> fetchTop10HonorPoints() {
        List<String> honorPoints = new ArrayList<String>();
        try {
            Connection dbConnection = getConnection();
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM `honor-points` ORDER BY `points` DESC LIMIT 10;");
            ResultSet resultSet= statement.executeQuery();

            while (resultSet.next()) {
                honorPoints.add("- " + resultSet.getString("playername") + ": " + resultSet.getInt("points"));
            }
            closeConnection(dbConnection);
        } catch (SQLException e) {
            catchError(e.getMessage());
        }
        return honorPoints;
    }

    /*
    CREATE TABLE `legacy-arena`.`duelarena-points` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `x` INT UNSIGNED NOT NULL , `y` INT UNSIGNED NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
     */
}
