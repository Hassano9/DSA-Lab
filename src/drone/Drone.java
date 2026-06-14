package drone;

import java.util.*;

public class Drone {
    public enum State { IDLE, FLYING, BACKTRACKING, REROUTING, ARRIVED, BLOCKED }

    private Waypoint currentPosition;
    private Waypoint destination;
    private List<Waypoint> currentPath = new ArrayList<>();
    private int pathIndex = 0;

    // Use a simple Stack class
    private final Deque<Waypoint> visitedStack = new ArrayDeque<>();

    // PriorityQueue keeps obstacles sorted so the closest one is dealt with first
    private final PriorityQueue<ObstacleEvent> obstacleQueue = new PriorityQueue<>();

    private State state = State.IDLE;
    private final List<String> log = new ArrayList<>();

    public void setPath(List<Waypoint> path) {
        this.currentPath = new ArrayList<>(path);
        this.pathIndex = 0;
        visitedStack.clear();

        if (!path.isEmpty()) {
            currentPosition = path.get(0);
            visitedStack.push(currentPosition);
        }
        state = State.FLYING;
        logEvent("Path set: " + path.size() + " waypoints");
    }

    public boolean step(AirspaceGraph graph, String algorithm) {
        if (state == State.ARRIVED || state == State.IDLE || state == State.BLOCKED) {
            return false;
        }

        // 1. Check for obstacles
        if (!obstacleQueue.isEmpty()) {
            ObstacleEvent threat = obstacleQueue.peek();

            if (isOnPath(threat.waypoint)) {
                obstacleQueue.poll(); // Remove it from the queue
                logEvent("Warning! Obstacle ahead. Rerouting...");
                state = State.REROUTING;
                backtrackToSafeNode(graph);
                return false;
            }
        }

        // 2. Check if we reached the end
        if (pathIndex + 1 >= currentPath.size()) {
            state = State.ARRIVED;
            logEvent("Arrived at destination.");
            return false;
        }

        // 3. Move one step forward
        pathIndex++;
        currentPosition = currentPath.get(pathIndex);
        visitedStack.push(currentPosition);
        state = State.FLYING;
        logEvent("Moved to: " + currentPosition.id);

        if (destination != null && currentPosition.id.equals(destination.id)) {
            state = State.ARRIVED;
            logEvent("Arrived at destination.");
        }
        return true;
    }

    // Go backward until we find a clear intersection
    private void backtrackToSafeNode(AirspaceGraph graph) {
        state = State.BACKTRACKING;
        int backsteps = 0;

        while (!visitedStack.isEmpty()) {
            Waypoint previousNode = visitedStack.pop();
            backsteps++;

            if (previousNode.isObstacle) {
                continue; // Cannot use a blocked node
            }

            // Check if this node has any clear paths branching off
            boolean hasClearPath = false;
            List<Edge> options = graph.getEdges(previousNode.id);

            for (Edge option : options) {
                if (!option.to.isObstacle) {
                    hasClearPath = true;
                    break;
                }
            }

            if (hasClearPath) {
                currentPosition = previousNode;
                pathIndex = 0;
                logEvent("Backtracked " + backsteps + " step(s).");
                return;
            }
        }

        // If we emptied the whole stack and found no safe path
        state = State.BLOCKED;
        logEvent("Drone is completely blocked.");
    }

    private boolean isOnPath(Waypoint wp) {
        for (int i = pathIndex + 1; i < currentPath.size(); i++) {
            if (currentPath.get(i).id.equals(wp.id)) {
                return true;
            }
        }
        return false;
    }

    public void addObstacle(ObstacleEvent event) {
        obstacleQueue.offer(event);
        logEvent("Obstacle added at: " + event.waypoint.id);
    }

    // Standard Getters and Setters needed by the GUI
    public PriorityQueue<ObstacleEvent> getObstacleQueue() { return obstacleQueue; }
    public Waypoint getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Waypoint wp) { this.currentPosition = wp; }
    public Waypoint getDestination() { return destination; }
    public void setDestination(Waypoint dest) { this.destination = dest; }
    public List<Waypoint> getCurrentPath() { return currentPath; }
    public State getState() { return state; }
    public void setState(State s) { this.state = s; }
    public List<String> getLog() { return log; }
    public int getPathIndex() { return pathIndex; }
    public Deque<Waypoint> getVisitedStack() { return visitedStack; }
    public void setPathIndex(int i) { this.pathIndex = i; }

    private void logEvent(String msg) {
        log.add(0, msg);
        if (log.size() > 50) {
            log.remove(log.size() - 1); // Keep the list from getting too big
        }
    }

    public void reset() {
        currentPath.clear();
        visitedStack.clear();
        obstacleQueue.clear();
        pathIndex = 0;
        state = State.IDLE;
        log.clear();
        logEvent("System reset");
    }
}