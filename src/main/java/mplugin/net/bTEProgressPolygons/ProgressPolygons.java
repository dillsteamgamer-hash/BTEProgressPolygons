package mplugin.net.bTEProgressPolygons;

import mplugin.net.bTEProgressPolygons.resources.reloadPolygons;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;

public final class ProgressPolygons extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        //Sets up the timer for reloading polygons for valid users
        reloadPolygons rp = new reloadPolygons(this);

        //Command set-up


        //Listener set-up


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onLoad() {
        //Plugin load logic
        System.out.println("Loading ProgressPolygons by Dillsteamgamer");
    }
}
