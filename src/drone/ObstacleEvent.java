package drone;

public class ObstacleEvent implements Comparable<ObstacleEvent> {
    public enum Type { BUILDING, NO_FLY_ZONE, MOVING_HAZARD }

    public Waypoint waypoint;
    public Type type;
    public double proximity; // Distance to the drone

    public ObstacleEvent(Waypoint waypoint, Type type, double proximity) {
        this.waypoint = waypoint;
        this.type = type;
        this.proximity = proximity;
    }

    // This tells the PriorityQueue how to sort obstacles (closest first)
    @Override
    public int compareTo(ObstacleEvent other) {
        if (this.proximity < other.proximity) {
            return -1;
        } else if (this.proximity > other.proximity) {
            return 1;
        } else {
            return 0;
        }
    }

    public String getTypeIcon() {
        if (type == Type.BUILDING) return "Building";
        if (type == Type.NO_FLY_ZONE) return "No-Fly Zone";
        return "Hazard";
    }
}