package mplugin.net.bTEProgressPolygons.commands;

import mplugin.net.bTEProgressPolygons.resources.DatabaseManager;
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

public class deleteProgressPolygon implements CommandExecutor {
    JavaPlugin plugin;
    DatabaseManager databaseManager;
    Connection databaseConnection;

    public deleteProgressPolygon(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        databaseConnection = databaseManager.getConnection();

        int ID = 0;
        String uuidFromDB = null;

        if(args.length!=0){
            ID = Integer.parseInt(args[0]);
        }

        try{
            String sql = "SELECT deleted FROM polygons WHERE id=?";
            PreparedStatement ps = databaseConnection.prepareStatement(sql);
            ps.setInt(1,ID);
            try{
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    uuidFromDB = rs.getString("deleted");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Player player = (Player) commandSender;

        if(args.length == 2) {
            if(args[1].equals("yes")) {
                if (uuidFromDB == null) {
                    commandSender.sendMessage("Deletion Code 1 Authorized");
                    String sql = "UPDATE polygons SET deleted=? WHERE id=?";
                    PreparedStatement ps = null;
                    try {
                        ps = databaseConnection.prepareStatement(sql);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        ps = databaseConnection.prepareStatement(sql);
                        ps.setString(1, String.valueOf(player.getUniqueId()));
                        ps.setInt(2, ID);
                        ps.executeUpdate();
                        player.sendMessage("Updated database to show deletion authorization");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (!uuidFromDB.equals(String.valueOf(player.getUniqueId()))) {
                    player.sendMessage("§2Deletion Code 2 Authorized");
                    String sql = "DELETE FROM polygons WHERE id=?";
                    try {
                        PreparedStatement ps = databaseConnection.prepareStatement(sql);
                        ps.setInt(1, ID);
                        ps.executeUpdate();
                        player.sendMessage("§2Deleted polygon from database");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }else if(args[1].equals("no")){
                String sql = "UPDATE polygons SET deleted=NULL WHERE id=?";
                try{
                    PreparedStatement ps = databaseConnection.prepareStatement(sql);
                    ps.setInt(1, ID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            player.sendMessage("§4Too many arguments should be in form /deleteProgressPolygon [id] [yes/no]");
        }

        databaseManager.closeDatabase();
        return false;
    }
}
