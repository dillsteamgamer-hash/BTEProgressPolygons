package mplugin.net.bTEProgressPolygons.resources;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class reloadPolygons {
    Timer timer = new Timer();
    JavaPlugin plugin;
    public reloadPolygons(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        DatabaseManager databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        Connection databaseConnection = databaseManager.getConnection();

        ArrayList<Polygon> polygons = new ArrayList<>();


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    polygons.clear();
                    Chunk chunk = player.getLocation().getChunk();

                    clearFakeBlocks(player);

                    if(player.hasMetadata("creatingPolygon")){
                        Polygon poly = new Polygon();
                        poly.setDataFromDatabaseFile(player.getMetadata("creatingPolygon").getFirst().asString());

                        poly.setLines(player.getWorld());
                        ArrayList<Location> blockLocations = poly.getLines();

                        for(Location blockLoc : blockLocations){
                            recordFakeBlock(player, blockLoc);
                            player.sendBlockChange(blockLoc, Material.DIAMOND_BLOCK.createBlockData());
                        }
                    }

                    if(player.hasMetadata("isViewingPolygons")) {
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
                                    polygons.getLast().setDataFromDatabaseFile(rs.getString("points"));
                                    polygons.getLast().setLines(player.getWorld());
                                }
                                for (Polygon polygon : polygons) {
                                    ArrayList<Location> blockLocations = polygon.getLines();
                                    for (Location blockLoc : blockLocations) {
                                        recordFakeBlock(player, blockLoc);
                                        player.sendBlockChange(blockLoc, Material.EMERALD_BLOCK.createBlockData());
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }, 5000, plugin.getConfig().getInt("Reload-Polygon-Cooldown"));
    }


    public void recordFakeBlock(Player player, Location blockLoc) {

        // Get existing metadata string (if any)
        String existing = "";
        if (player.hasMetadata("listFakeBlocks")) {
            existing = player.getMetadata("listFakeBlocks").getFirst().asString().trim();
        }

        // Build new "x,y,z" entry
        String newEntry = blockLoc.getBlockX() + "," +
                (player.getWorld().getHighestBlockYAt(blockLoc.getBlockX(), blockLoc.getBlockZ()) + 1) + "," +
                blockLoc.getBlockZ();

        // Append properly
        String result;
        if (existing.isEmpty()) {
            result = newEntry; // first entry
        } else {
            result = existing + "/" + newEntry; // append entry
        }

        // Save back
        player.setMetadata("listFakeBlocks", new FixedMetadataValue(plugin, result));
    }


    public void clearFakeBlocks(Player player) {

        if (!player.hasMetadata("listFakeBlocks")) {
            return;
        }

        String data = player.getMetadata("listFakeBlocks").getFirst().asString();
        player.removeMetadata("listFakeBlocks", plugin);

        // Split each "x,y,z" entry
        String[] entries = data.split("/");
        World world = player.getWorld();

        for (String entry : entries) {
            String[] s = entry.split(",");
            if (s.length != 3) continue;

            int x = Integer.parseInt(s[0]);
            int y = Integer.parseInt(s[1]);
            int z = Integer.parseInt(s[2]);

            Location loc = new Location(world, x, y, z);

            // Reset fake block to real block
            player.sendBlockChange(loc, world.getBlockAt(loc).getBlockData());
        }
    }
}
