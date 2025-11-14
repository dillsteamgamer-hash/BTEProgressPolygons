package mplugin.net.bTEProgressPolygons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class toggleProgressPolygonVisibility implements CommandExecutor {
    JavaPlugin plugin;

    public toggleProgressPolygonVisibility(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        Player player = (Player) commandSender;

        if(player.hasMetadata("isViewingPolygons")){
            player.removeMetadata("isViewingPolygons", plugin);
        }else{
            player.setMetadata("isViewingPolygons", new FixedMetadataValue(plugin, ""));
        }

        return false;
    }
}
