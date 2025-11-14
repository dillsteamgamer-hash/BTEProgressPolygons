package mplugin.net.bTEProgressPolygons.commands;

import mplugin.net.bTEProgressPolygons.resources.DatabaseManager;
import mplugin.net.bTEProgressPolygons.resources.Polygon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class makeProgressPolygon implements CommandExecutor {
    JavaPlugin plugin;
    DatabaseManager databaseManager;
    Connection databaseConnection;

    public makeProgressPolygon(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initDatabase();
        databaseConnection = databaseManager.getConnection();
        Player player = (Player) commandSender;

        Polygon polygon = new Polygon();
        polygon.setDataFromDatabaseFile(player.getMetadata("creatingPolygon").getFirst().asString());

        StringBuilder sbName = new StringBuilder();

        for (String arg : args) {
            sbName.append(arg);
            sbName.append(" ");
        }

        polygon.name = sbName.toString();

        String sql = "SELECT MAX(ID) AS max_id FROM polygons";
        try (Statement statement = databaseConnection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            if (rs.next()) {
                polygon.ID = rs.getInt("max_id") + 1;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        polygon.createDatabaseFile();

        sql = "INSET INTO polygons (id, name, points) VALUES (?,?,?)";
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(sql);
            ps.setInt(1,polygon.ID);
            ps.setString(2,polygon.name);
            ps.setString(3,polygon.createDatabaseFile());
            ps.executeUpdate();
            player.sendMessage("§2Successfully added the polygon to the database:");
            player.sendMessage("§2ID = " + polygon.ID);
            player.sendMessage("§2Name = " + polygon.name);
            player.sendMessage("If you forgot to set a name, and need to, record this ID, run '/deleteProgressPolygon [ID] yes' and post a message in your staff chat for a second authorization!");
        } catch (SQLException e) {
            System.out.println("§4Fail to add polygon to database!" + e);
            commandSender.sendMessage("§4Error in adding the polygon to the database, see console for more info");
            e.printStackTrace();
        }


        return false;
    }
}
