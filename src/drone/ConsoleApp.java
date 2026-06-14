package drone;

import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AirspaceGraph graph = new AirspaceGraph();
        Drone drone = new Drone();

        // 1. Build a smaller, simple map for console testing
        buildSimpleMap(graph);

        System.out.println("==================================================");
        System.out.println("  Autonomous Drone Route Planner (Console Mode)   ");
        System.out.println("==================================================");

        boolean isRunning = true;

        // 2. The Main Menu Loop
        while (isRunning) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View Map (Cities & Connections)");
            System.out.println("2. Set Route (Choose Start and End)");
            System.out.println("3. Fly Drone (Move 1 Step)");
            System.out.println("4. Add an Obstacle");
            System.out.println("5. Exit");
            System.out.print("Type a number to choose: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                viewMap(graph);
            }
            else if (choice.equals("2")) {
                setRoute(scanner, graph, drone);
            }
            else if (choice.equals("3")) {
                flyDrone(graph, drone);
            }
            else if (choice.equals("4")) {
                addObstacle(scanner, graph, drone);
            }
            else if (choice.equals("5")) {
                System.out.println("Shutting down... Goodbye!");
                isRunning = false;
            }
            else {
                System.out.println("Invalid choice! Please type a number between 1 and 5.");
            }
        }

        scanner.close();
    }

    // --- Helper Methods to keep the code clean ---

    private static void viewMap(AirspaceGraph graph) {
        System.out.println("\n--- Current Map ---");
        for (Waypoint wp : graph.getWaypoints()) {
            System.out.print(wp.id + " (" + wp.label + ") connects to: ");

            List<Edge> edges = graph.getEdges(wp.id);
            if (edges.isEmpty()) {
                System.out.print("Nothing.");
            } else {
                for (Edge edge : edges) {
                    System.out.print(edge.to.id + " [dist: " + edge.weight + "]  ");
                }
            }
            System.out.println();
        }
    }

    private static void setRoute(Scanner scanner, AirspaceGraph graph, Drone drone) {
        System.out.print("\nEnter Start ID (e.g., W1): ");
        String startId = scanner.nextLine().toUpperCase();

        System.out.print("Enter Destination ID (e.g., W5): ");
        String endId = scanner.nextLine().toUpperCase();

        Waypoint startNode = graph.getWaypoint(startId);
        Waypoint endNode = graph.getWaypoint(endId);

        if (startNode == null || endNode == null) {
            System.out.println("Error: Could not find those waypoints. Check the map and try again.");
            return;
        }

        // Calculate the path using Dijkstra
        List<Waypoint> path = graph.dijkstra(startId, endId);

        if (path.isEmpty()) {
            System.out.println("No path could be found from " + startId + " to " + endId);
        } else {
            drone.setPath(path);
            drone.setDestination(endNode);
            System.out.print("Path found! Route: ");
            for (Waypoint p : path) {
                System.out.print(p.id + " -> ");
            }
            System.out.println("DONE");
        }
    }

    private static void flyDrone(AirspaceGraph graph, Drone drone) {
        if (drone.getState() == Drone.State.IDLE) {
            System.out.println("\nThe drone doesn't have a route yet. Please Set Route (Option 2) first.");
            return;
        }

        System.out.println("\n--- Executing Drone Step ---");
        boolean moved = drone.step(graph, "Dijkstra");

        // Print the most recent thing the drone logged
        List<String> logs = drone.getLog();
        if (!logs.isEmpty()) {
            System.out.println(logs.get(0));
        }

        System.out.println("Current Drone State: " + drone.getState());
    }

    private static void addObstacle(Scanner scanner, AirspaceGraph graph, Drone drone) {
        System.out.print("\nEnter the Waypoint ID to block (e.g., W3): ");
        String blockId = scanner.nextLine().toUpperCase();

        Waypoint target = graph.getWaypoint(blockId);
        if (target == null) {
            System.out.println("Error: Waypoint not found.");
            return;
        }

        target.isObstacle = true;

        // Tell the drone an obstacle appeared (proximity 10 for simulation sake)
        ObstacleEvent event = new ObstacleEvent(target, ObstacleEvent.Type.MOVING_HAZARD, 10.0);
        drone.addObstacle(event);

        System.out.println("Success: " + blockId + " is now blocked by an obstacle!");
    }

    private static void buildSimpleMap(AirspaceGraph graph) {
        // Create 5 simple points
        graph.addWaypoint(new Waypoint("W1", 0, 0, "Base"));
        graph.addWaypoint(new Waypoint("W2", 0, 0, "Outpost Alpha"));
        graph.addWaypoint(new Waypoint("W3", 0, 0, "Outpost Bravo"));
        graph.addWaypoint(new Waypoint("W4", 0, 0, "Outpost Charlie"));
        graph.addWaypoint(new Waypoint("W5", 0, 0, "Delivery Target"));

        // Connect them (creates a diamond shape with a straight line through the middle)
        graph.addEdge("W1", "W2", 10);
        graph.addEdge("W1", "W3", 15);
        graph.addEdge("W2", "W4", 12);
        graph.addEdge("W3", "W4", 10);
        graph.addEdge("W4", "W5", 5);
        graph.addEdge("W2", "W5", 25); // A very long alternative route
    }
}