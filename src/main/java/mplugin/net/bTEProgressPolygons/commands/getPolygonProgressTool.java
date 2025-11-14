package mplugin.net.bTEProgressPolygons.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class getPolygonProgressTool implements CommandExecutor {
    JavaPlugin plugin;
    public getPolygonProgressTool(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        ItemStack rod = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = rod.getItemMeta();

        meta.displayName(Component.text("Polygon Creation Tool"));
        rod.setItemMeta(meta);

        player.getInventory().addItem(rod);

        player.sendMessage("§2Given Polygon Creator Tool");
        player.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("On-Receive-Polygon-Creator-Tool")));

        return false;
    }
}
