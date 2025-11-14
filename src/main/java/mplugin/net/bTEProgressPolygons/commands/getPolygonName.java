package mplugin.net.bTEProgressPolygons.commands;

import mplugin.net.bTEProgressPolygons.resources.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class getPolygonName implements CommandExecutor {
    JavaPlugin plugin;
    DatabaseManager databaseManager;
    Connection databaseConnection;

    public getPolygonName(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        databaseConnection = databaseManager.getConnection();

        if(args.length == 1){
            String sql = "SELECT name FROM polygons WHERE id=?";
            try{
                PreparedStatement ps = databaseConnection.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(args[0]));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        commandSender.sendMessage("§3Found Polygon Name: " + rs.getString("name"));
                    }
                    commandSender.sendMessage("§2Listed all found polygons with that ID!");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            commandSender.sendMessage("§4Invalid amount of arguments, format: /getPolygonName [id]");
        }

        databaseManager.closeDatabase();
        return false;
    }
}
