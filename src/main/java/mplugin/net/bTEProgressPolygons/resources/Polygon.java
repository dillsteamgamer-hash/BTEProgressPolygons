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
        holder.deleteCharAt(holder.length()-1);
        return holder.toString();
    }


    ArrayList<Location> blockLocations = new ArrayList<>();


    public ArrayList<Location> getLines(){
        return blockLocations;
    }


    public void setLines(World world){
        blockLocations = new ArrayList<>();

        //Iterates between all points (excluding line between first and last)
        for(int i = 0; i < points.toArray().length - 1; i++){
            Coordinate point1 = points.get(i);
            Coordinate point2 = points.get(i+1);

            if(point2.x == point1.x){
                for(int j = point1.z; j <= point2.z; j++){
                    blockLocations.add(new Location(
                            world,
                            point1.x,
                            world.getHighestBlockYAt(point1.x, j) + 1,
                            j));
                }
            }else if(point2.z == point1.z){
                for(int j = point1.x; j <= point2.x; j++){
                    blockLocations.add(new Location(
                            world,
                            j,
                            world.getHighestBlockYAt(j, point1.z) + 1,
                            point1.z));
                }
            }else{
                float gradient = (float) (point2.z - point1.z) / (float) (point2.x - point1.x);
                float holder = point1.z;
                for(int j = point1.x + 1; j<= point2.x; j++){
                    holder = holder + gradient;
                    int zHolder = Math.round(holder);
                    blockLocations.add(new Location(world, j, world.getHighestBlockYAt(j, zHolder)+1, zHolder));
                }
            }
        }

        //Draws line between first and last point
        Coordinate point1 = points.getLast();
        Coordinate point2 = points.getFirst();
        if(point2.x == point1.x){
            for(int j = point1.z; j <= point2.z; j++){
                blockLocations.add(new Location(
                        world,
                        point1.x,
                        world.getHighestBlockYAt(point1.x, j) + 1,
                        j));
            }
        }else if(point2.z == point1.z){
            for(int j = point1.x; j <= point2.x; j++){
                blockLocations.add(new Location(
                        world,
                        j,
                        world.getHighestBlockYAt(j, point1.z) + 1,
                        point1.z));
            }
        }else{
            float gradient = (float) (point2.z - point1.z) / (float) (point2.x - point1.x);
            float holder = point1.z;
            for(int j = point1.x + 1; j<= point2.x; j++){
                holder = holder + gradient;
                int zHolder = Math.round(holder);
                blockLocations.add(new Location(world, j, world.getHighestBlockYAt(j, zHolder)+1, zHolder));
            }
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
}
