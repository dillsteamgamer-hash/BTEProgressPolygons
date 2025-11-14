package mplugin.net.bTEProgressPolygons.eventListeners;

import mplugin.net.bTEProgressPolygons.resources.Coordinate;
import mplugin.net.bTEProgressPolygons.resources.Polygon;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
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

        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            leftClick(player, event);
        }else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            rightClick(player, event);
        }else{
            player.sendMessage("§4Must click a block to use this tool!");
            return;
        }

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
        player.sendMessage(polygonData);

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
        player.sendMessage(polygon.createDatabaseFile());

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
}
