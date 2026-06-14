package drone;

public class Waypoint {
    public String id;
    public int x;
    public int y;
    public String label;
    public boolean isObstacle;

    public Waypoint(String id, int x, int y, String label) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.label = label;
        this.isObstacle = false; // By default, the path is clear
    }
}