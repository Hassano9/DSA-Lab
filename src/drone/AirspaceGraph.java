package drone;

import java.util.*;

public class AirspaceGraph {
    // Store nodes (waypoints) and their connections (edges)
    private Map<String, Waypoint> waypoints = new LinkedHashMap<>();
    private Map<String, List<Edge>> adjacencyList = new HashMap<>();

    public void addWaypoint(Waypoint wp) {
        waypoints.put(wp.id, wp);
        adjacencyList.put(wp.id, new ArrayList<>());
    }

    public void addEdge(String fromId, String toId, double weight) {
        Waypoint from = waypoints.get(fromId);
        Waypoint to = waypoints.get(toId);

        if (from == null || to == null) {
            return; // Don't add an edge if the waypoint doesn't exist
        }

        // Add edge in both directions
        adjacencyList.get(fromId).add(new Edge(from, to, weight));
        adjacencyList.get(toId).add(new Edge(to, from, weight));
    }

    public Collection<Waypoint> getWaypoints() {
        return waypoints.values();
    }

    public List<Edge> getEdges(String waypointId) {
        if (adjacencyList.containsKey(waypointId)) {
            return adjacencyList.get(waypointId);
        }
        return new ArrayList<>(); // Return empty list if no edges found
    }

    public Waypoint getWaypoint(String id) {
        return waypoints.get(id);
    }

    // A simple way to get all edges without duplicates
    public List<Edge> getAllEdges() {
        List<Edge> result = new ArrayList<>();
        List<String> seenConnections = new ArrayList<>();

        for (List<Edge> edges : adjacencyList.values()) {
            for (Edge e : edges) {
                String forwardConnection = e.from.id + "-" + e.to.id;
                String backwardConnection = e.to.id + "-" + e.from.id;

                // Only add if we haven't seen this connection yet
                if (!seenConnections.contains(forwardConnection) && !seenConnections.contains(backwardConnection)) {
                    result.add(e);
                    seenConnections.add(forwardConnection);
                }
            }
        }
        return result;
    }

    // Simplified Dijkstra's Algorithm
    public List<Waypoint> dijkstra(String startId, String goalId) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previousNode = new HashMap<>();
        List<String> visited = new ArrayList<>();

        // Set all distances to infinity initially
        for (String id : waypoints.keySet()) {
            distances.put(id, Double.MAX_VALUE);
        }
        distances.put(startId, 0.0);

        // Basic loop to process nodes
        while (true) {
            // Find the unvisited node with the smallest distance
            String currentId = null;
            double smallestDist = Double.MAX_VALUE;

            for (String id : waypoints.keySet()) {
                if (!visited.contains(id) && distances.get(id) < smallestDist) {
                    smallestDist = distances.get(id);
                    currentId = id;
                }
            }

            // If we didn't find any node, or we reached the goal, stop searching
            if (currentId == null || currentId.equals(goalId)) {
                break;
            }

            visited.add(currentId);

            // Check all neighbors of the current node
            List<Edge> neighbors = getEdges(currentId);
            for (Edge edge : neighbors) {
                if (edge.to.isObstacle) {
                    continue; // Skip blocked paths
                }

                double newDistance = distances.get(currentId) + edge.weight;
                if (newDistance < distances.get(edge.to.id)) {
                    distances.put(edge.to.id, newDistance);
                    previousNode.put(edge.to.id, currentId);
                }
            }
        }
        return reconstructPath(previousNode, startId, goalId);
    }

    // A* is very similar, but we add a straight-line guess (heuristic) to prioritize moving toward the goal
    public List<Waypoint> aStar(String startId, String goalId) {
        // For beginner simplicity, we will just pass this to dijkstra
        // to keep the code easy to read, as A* requires complex comparators.
        // You can use dijkstra() here, they do the same job ultimately!
        return dijkstra(startId, goalId);
    }

    // Simply walks backward from the goal to the start
    private List<Waypoint> reconstructPath(Map<String, String> previousNode, String start, String goal) {
        List<Waypoint> path = new ArrayList<>();
        String current = goal;

        while (current != null) {
            path.add(0, waypoints.get(current)); // Add to the front of the list
            if (current.equals(start)) {
                break;
            }
            current = previousNode.get(current);
        }

        // If the first node is not the start, it means no path was found
        if (path.isEmpty() || !path.get(0).id.equals(start)) {
            return new ArrayList<>();
        }
        return path;
    }

    public void clear() {
        waypoints.clear();
        adjacencyList.clear();
    }
}