package mplugin.net.bTEProgressPolygons.commands;

import mplugin.net.bTEProgressPolygons.resources.Coordinate;
import mplugin.net.bTEProgressPolygons.resources.DatabaseManager;
import mplugin.net.bTEProgressPolygons.resources.Polygon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class getPolygonID implements CommandExecutor {
    JavaPlugin plugin;
    DatabaseManager databaseManager;
    Connection databaseConnection;

    public getPolygonID(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        databaseConnection = databaseManager.getConnection();

        StringBuilder SBName = new StringBuilder();

        for(String word : args){
            SBName.append(word).append(" ");
        }
        SBName.append("%");

        String name = SBName.toString();


        if(name.length() > 1){
            String sql = "SELECT id FROM polygons WHERE name LIKE ?";
            ArrayList<String> foundPolys = new ArrayList<>();
            try{
                PreparedStatement ps = databaseConnection.prepareStatement(sql);
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        commandSender.sendMessage("§3Found Polygon ID: " + rs.getInt("id"));
                    }
                    commandSender.sendMessage("§2Listed all found polygons!");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            commandSender.sendMessage("§4No name has been entered as an argument!");
        }

        databaseManager.closeDatabase();
        return false;
    }
}
