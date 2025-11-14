package mplugin.net.bTEProgressPolygons;

import mplugin.net.bTEProgressPolygons.commands.*;
import mplugin.net.bTEProgressPolygons.eventListeners.interact;
import mplugin.net.bTEProgressPolygons.resources.reloadPolygons;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.Objects;

public final class ProgressPolygons extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        //Sets up the timer for reloading polygons for valid users
        reloadPolygons rp = new reloadPolygons(this);

        //Command set-up
        Objects.requireNonNull(getCommand("deleteProgressPolygon")).setExecutor(new deleteProgressPolygon(this));
        Objects.requireNonNull(getCommand("getNearbyPolygons")).setExecutor(new getNearbyPolygons(this));
        Objects.requireNonNull(getCommand("getPolygonID")).setExecutor(new getPolygonID(this));
        Objects.requireNonNull(getCommand("getPolygonName")).setExecutor(new getPolygonName(this));
        Objects.requireNonNull(getCommand("getPolygonProgressTool")).setExecutor(new getPolygonProgressTool(this));
        Objects.requireNonNull(getCommand("teleportProgressPolygon")).setExecutor(new teleportProgressPolygon(this));
        Objects.requireNonNull(getCommand("makeProgressPolygon")).setExecutor(new makeProgressPolygon(this));
        Objects.requireNonNull(getCommand("toggleProgressPolygonVisibility")).setExecutor(new toggleProgressPolygonVisibility(this));

        //Listener set-up
        getServer().getPluginManager().registerEvents(new interact(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onLoad() {
        //Plugin load logic
        System.out.println("Loading ProgressPolygons by Dillsteamgamer");
        saveDefaultConfig();
    }
}
