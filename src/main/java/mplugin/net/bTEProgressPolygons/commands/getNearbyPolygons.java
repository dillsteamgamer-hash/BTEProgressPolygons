package mplugin.net.bTEProgressPolygons.commands;

import mplugin.net.bTEProgressPolygons.resources.Coordinate;
import mplugin.net.bTEProgressPolygons.resources.DatabaseManager;
import mplugin.net.bTEProgressPolygons.resources.Polygon;
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
import java.util.ArrayList;

public class getNearbyPolygons implements CommandExecutor{
    JavaPlugin plugin;
    DatabaseManager databaseManager;
    Connection databaseConnection;
    public getNearbyPolygons(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        plugin.saveDefaultConfig();
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        databaseConnection = databaseManager.getConnection();

        ArrayList<Polygon> polygons = new ArrayList<>();
        Player player = (Player) commandSender;

        int radius = plugin.getConfig().getInt("Polygon-Load-Distance");

        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();

        int minX = playerX - radius;
        int maxX = playerX + radius;
        int minZ = playerZ - radius;
        int maxZ = playerZ + radius;

        String sql = "SELECT * FROM polygons WHERE centreX BETWEEN ? AND ? AND centreZ BETWEEN ? AND ?";
        try (PreparedStatement ps = databaseConnection.prepareStatement(sql)) {
            ps.setInt(1, minX);
            ps.setInt(2, maxX);
            ps.setInt(3, minZ);
            ps.setInt(4, maxZ);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    polygons.add(new Polygon());
                    polygons.getLast().ID = rs.getInt("id");
                    polygons.getLast().name = rs.getString("name");

                    Coordinate centre = new Coordinate();
                    centre.x = rs.getInt("centreX");
                    centre.z = rs.getInt("centreZ");
                    polygons.getLast().centre = centre;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Polygon poly : polygons){
            player.sendMessage("§uFound polygon!");
            player.sendMessage("§3ID: " + poly.ID);
            player.sendMessage("§3Name: " + poly.name);
        }

        databaseManager.closeDatabase();
        return false;
    }
}
