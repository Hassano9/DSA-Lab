package drone;

public class Edge {
    public Waypoint from;
    public Waypoint to;
    public double weight; // The distance cost to travel this edge

    public Edge(Waypoint from, Waypoint to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}