package mplugin.net.bTEProgressPolygons.eventListeners;

import mplugin.net.bTEProgressPolygons.resources.Coordinate;
import mplugin.net.bTEProgressPolygons.resources.Polygon;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class interact implements Listener {
    JavaPlugin plugin;

    public interact(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (player.getOpenInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getType() != InventoryType.CREATIVE){
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.BLAZE_ROD && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            Component displayName = meta.displayName();

            if (!Component.text("Polygon Creation Tool").equals(displayName)) {
                return;
            }
        }else{
            return;
        }

        clearFakeBlocks(player);


        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            leftClick(player, event);
        }else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            rightClick(player, event);
        }else{
            player.sendMessage("§4Must click a block to use this tool!");
            return;
        }

        Polygon poly = new Polygon();
        poly.setDataFromDatabaseFile(player.getMetadata("creatingPolygon").getFirst().asString());

        poly.setLines(player.getWorld());
        ArrayList<Location> blockLocations = poly.getLines();

        for(Location blockLoc : blockLocations){
            recordFakeBlock(player, blockLoc);
            player.sendBlockChange(blockLoc, Material.DIAMOND_BLOCK.createBlockData());
        }

        event.setCancelled(true);

    }

    public void leftClick(Player player, PlayerInteractEvent event){
        Polygon polygon = new Polygon();
        Block block = event.getClickedBlock();
        Coordinate blockCoordinate = new Coordinate();
        assert block != null;
        blockCoordinate.x = block.getX();
        blockCoordinate.z = block.getZ();

        polygon.addPoint(blockCoordinate);

        String polygonData = polygon.createDatabaseFile();

        player.setMetadata("creatingPolygon", new FixedMetadataValue(plugin, polygonData));
    }

    public void rightClick(Player player, PlayerInteractEvent event){
        if (!player.hasMetadata("creatingPolygon")) {
            player.sendMessage("§4No active polygon, start a selection with Left Click!");
            return;
        }
        Polygon polygon = new Polygon();
        Block block = event.getClickedBlock();
        Coordinate blockCoordinate = new Coordinate();
        assert block != null;
        blockCoordinate.x = block.getX();
        blockCoordinate.z = block.getZ();

        polygon.setDataFromDatabaseFile(player.getMetadata("creatingPolygon").getFirst().asString());

        int displacement = Math.toIntExact(Math.round(Math.sqrt(Math.pow(polygon.points.getFirst().x - blockCoordinate.x, 2) + Math.pow(polygon.points.getFirst().z - blockCoordinate.z, 2))));

        if(displacement >= 512){
            player.sendMessage("§4This point is > 512 blocks from the initial point, if needed create multiple (smaller) polygons for the area!");
            return;
        }

        polygon.setDataFromDatabaseFile(player.getMetadata("creatingPolygon").getFirst().asString());

        polygon.addPoint(blockCoordinate);

        String polygonData = polygon.createDatabaseFile();

        player.removeMetadata("creatingPolygon", plugin);
        player.setMetadata("creatingPolygon", new FixedMetadataValue(plugin, polygonData));
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
