package mplugin.net.bTEProgressPolygons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class progressPolygonHelp implements CommandExecutor {
    JavaPlugin plugin;
    public progressPolygonHelp(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        String delay = plugin.getConfig().getString("Reload-Polygon-Cooldown");
        commandSender.sendMessage("§6----------------------");
        commandSender.sendMessage("§3/deleteProgressPolygon: §8Deletes a progress polygon, requires 2 approvals");
        commandSender.sendMessage("§3/getNearbyPolygons: §8Returns the names and IDs of all nearby polygons");
        commandSender.sendMessage("§3/getPolygonID [name]: §8Returns the id of all polygons that begin with the inputted name");
        commandSender.sendMessage("§3/getPolygonName [ID]: §8Returns the name of the polygon with that name");
        commandSender.sendMessage("§3/getPolygonProgressTool: §8Gives the selector tool");
        commandSender.sendMessage("§3/makeProgressPolygon [name]: §8Creates a polygon with the selected area");
        commandSender.sendMessage("§3/teleportProgressPolygon [ID]: §8Teleports to the polygon with given ID");
        commandSender.sendMessage("§3/toggleProgressPolygonVisibility: §8Shows the outlines of polygons, reloads every " + delay + "ms");
        commandSender.sendMessage("§3/progressPolygonStats: §8Shows the number of polygons");
        commandSender.sendMessage("§3/progressPolygonHelp: §8This");
        commandSender.sendMessage("§6----------------------");


        return false;
    }
}
