package mplugin.net.bTEProgressPolygons.commands;

import mplugin.net.bTEProgressPolygons.resources.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class progressPolygonStats implements CommandExecutor {
    JavaPlugin plugin;
    DatabaseManager databaseManager;
    Connection databaseConnection;

    public progressPolygonStats(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        databaseConnection = databaseManager.getConnection();
        Player player = (Player) commandSender;

        int count = 0;

        try{
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT COUNT(*) AS count FROM polygons");
            ResultSet rs = ps.executeQuery();
            count =  rs.getInt("count");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        commandSender.sendMessage("§6There are " + count + " polygons!");


        databaseManager.closeDatabase();
        return false;
    }
}
