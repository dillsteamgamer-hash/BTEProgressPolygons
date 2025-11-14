package mplugin.net.bTEProgressPolygons.commands;

import mplugin.net.bTEProgressPolygons.resources.Coordinate;
import mplugin.net.bTEProgressPolygons.resources.DatabaseManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class teleportProgressPolygon implements CommandExecutor {
    JavaPlugin plugin;
    DatabaseManager databaseManager;
    Connection databaseConnection;

    public teleportProgressPolygon(JavaPlugin plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        databaseConnection = databaseManager.getConnection();
        Player player = (Player) commandSender;
        Coordinate centre = new Coordinate();

        try {
            final int ID = Integer.parseInt(args[0]);
            String sql = "SELECT * FROM polygons WHERE id=?";
            PreparedStatement ps = databaseConnection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                centre.x = rs.getInt("centreX");
                centre.z = rs.getInt("centreZ");
                World world = player.getWorld();
                player.teleport(new Location(world, centre.x, world.getHighestBlockYAt(centre.x, centre.z) + 1, centre.z));
                player.sendMessage("§2Successfully teleported to polygon ID " + ID);
            }else{
                player.sendMessage("§4Polygon not found in database!");
            }
        }catch(Exception e){
            player.sendMessage("§4Invalid argument, must be polygon ID!");
            e.printStackTrace();
        }


        databaseManager.closeDatabase();
        return false;
    }
}
