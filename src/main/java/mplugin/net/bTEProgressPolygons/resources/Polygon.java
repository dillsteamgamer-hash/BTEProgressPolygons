package mplugin.net.bTEProgressPolygons.resources;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

public class Polygon {
    public ArrayList<Coordinate> points = new ArrayList<>();
    public int ID;
    public String name;
    public Coordinate centre = new Coordinate();

    public void addPoint(Coordinate coordinate){
            points.add(coordinate);
    }

    public void setDataFromDatabaseFile(String pointsString){
        points.clear();
        String[] parts = pointsString.split("/");
        for (String part : parts) {
            String[] split = part.split(",");
            Coordinate coordinate = new Coordinate();
            coordinate.x = Integer.parseInt(split[0]);
            coordinate.z = Integer.parseInt(split[1]);
            points.add(coordinate);
        }
    }

    public String createDatabaseFile(){
        StringBuilder holder = new StringBuilder();
        for(Coordinate point : points){
            holder.append(point.x);
            holder.append(",");
            holder.append(point.z);
            holder.append("/");
        }
        if (!holder.isEmpty()) {
            holder.setLength(holder.length() - 1);
        }
        System.out.println(holder);
        return holder.toString();
    }


    ArrayList<Location> blockLocations = new ArrayList<>();


    public ArrayList<Location> getLines(){
        return blockLocations;
    }


    public void setLines(World world){
        blockLocations.clear();
        ArrayList<Coordinate> allLineBlocks = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            Coordinate start = points.get(i);
            Coordinate end = points.get((i + 1) % points.size()); // wraps around
            allLineBlocks.addAll(getLine(start, end));
        }

        for(Coordinate block : allLineBlocks){
            blockLocations.add(new Location(world, block.x, world.getHighestBlockYAt(block.x,block.z)+1,block.z));
        }
    }


    public void setCentre(ArrayList<Coordinate> points) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (Coordinate p : points) {
            if (p.x < minX) minX = p.x;
            if (p.x > maxX) maxX = p.x;
            if (p.z < minZ) minZ = p.z;
            if (p.z > maxZ) maxZ = p.z;
        }

        centre.x = (minX + maxX) / 2;
        centre.z = (minZ + maxZ) / 2;
    }

    public static ArrayList<Coordinate> getLine(Coordinate a, Coordinate b) {
        ArrayList<Coordinate> result = new ArrayList<>();

        int x1 = a.x;
        int z1 = a.z;
        int x2 = b.x;
        int z2 = b.z;

        int dx = Math.abs(x2 - x1);
        int dz = Math.abs(z2 - z1);

        int sx = x1 < x2 ? 1 : -1;
        int sz = z1 < z2 ? 1 : -1;

        int err = dx - dz;

        while (true) {
            Coordinate tempCoord = new Coordinate();
            tempCoord.x = x1;
            tempCoord.z = z1;
            result.add(tempCoord);

            if (x1 == x2 && z1 == z2)
                break;

            int e2 = 2 * err;

            if (e2 > -dz) {
                err -= dz;
                x1 += sx;
            }

            if (e2 < dx) {
                err += dx;
                z1 += sz;
            }
        }

        return result;
    }
}
