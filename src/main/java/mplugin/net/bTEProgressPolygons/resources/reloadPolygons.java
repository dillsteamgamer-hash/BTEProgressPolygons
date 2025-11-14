package mplugin.net.bTEProgressPolygons.resources;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class reloadPolygons {
    public reloadPolygons(JavaPlugin plugin){
        Timer timer = new Timer();
        DatabaseManager databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        Connection databaseConnection = databaseManager.getConnection();

        ArrayList<Polygon> polygons = new ArrayList<>();


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(player.getMetadata("isViewingPolygons").getFirst().asBoolean()){
                        int radius = plugin.getConfig().getInt("Polygon-Load-Distance");

                        int playerX = player.getLocation().getBlockX();
                        int playerZ = player.getLocation().getBlockZ();

                        int minX = playerX - radius;
                        int maxX = playerX + radius;
                        int minZ = playerZ - radius;
                        int maxZ = playerZ + radius;


                        String sql = "SELECT * FROM polygons WHERE centreX BETWEEN ? AND ? AND centreZ BETWEEN ? AND ?";

                        polygons.clear();

                        try (PreparedStatement ps = databaseConnection.prepareStatement(sql)) {
                            ps.setInt(1, minX);
                            ps.setInt(2, maxX);
                            ps.setInt(3, minZ);
                            ps.setInt(4, maxZ);

                            try (ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    polygons.add(new Polygon());
                                    polygons.getLast().setDataFromDatabaseFile(rs.getString("points"));
                                    polygons.getLast().setLines(player.getWorld());
                                }
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        for(Polygon polygon : polygons){
                            ArrayList<Location> blockLocations = polygon.getLines();
                            for(Location blockLoc : blockLocations){
                                player.sendBlockChange(blockLoc, Material.DIAMOND_BLOCK.createBlockData());
                            }
                        }
                    }
                }
            }
        }, 5000, plugin.getConfig().getInt("Reload-Polygon-Cooldown"));
    }
}
