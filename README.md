# 🚁 Autonomous Drone Route Planner (Console Edition)

A lightweight, console-based Java application that simulates an autonomous drone navigating through a dynamic airspace. This project serves as a practical demonstration of core **Data Structures and Algorithms (DSA)**, including pathfinding, priority queues, and stack-based backtracking.

---

## 📖 Overview
This simulation models a drone attempting to fly from a starting outpost to a destination. The "airspace" is represented as a weighted graph. As the drone flies, users can dynamically spawn obstacles in its path. To survive and reach its goal, the drone uses pathfinding algorithms to reroute and uses short-term memory to physically backtrack out of dead ends. 

By running entirely in the terminal, the codebase remains clean, beginner-friendly, and heavily focused on the underlying logic rather than complex GUI code.

---

## ✨ Features
* **Interactive Terminal Menu:** Control the drone, view the map, and trigger obstacles via a clean text-based interface.
* **Smart Pathfinding:** Uses a simplified Dijkstra's Algorithm to calculate the absolute shortest route across the map.
* **Dynamic Threat Avoidance:** Obstacles are sorted by proximity. The drone always reacts to the most immediate threat first.
* **Intelligent Backtracking:** If cornered by a new obstacle, the drone remembers its flight path and reverses step-by-step until it finds a clear intersection to reroute.

---

## 🧠 Core Data Structures Used

This project relies on several fundamental DSA concepts:

| Data Structure | Implementation | Purpose |
| :--- | :--- | :--- |
| **Graph (Adjacency List)** | `HashMap` & `ArrayList` | Models the map. Stores waypoints and their connected flight paths efficiently. |
| **Min-Heap** | `PriorityQueue` | Sorts upcoming obstacles by distance, ensuring the closest hazard is handled first. |
| **Stack (LIFO)** | `ArrayDeque` | Acts as the drone's memory. Pushes nodes as it flies, pops them to backtrack safely. |

---

## 🛠️ Getting Started

### Prerequisites
* **Java Development Kit (JDK) 8 or higher** installed on your machine.

### Installation & Execution
1. Clone the repository to your local machine:
   ```bash
   git clone [https://github.com/yourusername/drone-route-planner.git](https://github.com/yourusername/drone-route-planner.git)