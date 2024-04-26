

import java.io.*;
import java.util.*;

// Define a class called SearchNode, which represents a node in the search space for the A* algorithm
class SearchNode implements Comparable<SearchNode> {
    int posX; // Represents the x-coordinate of the node's position in the grid
    int posY; // Represents the y-coordinate of the node's position in the grid
    int distance; // Represents the cost of reaching this node from the start node
    int heuristic; // Represents the heuristic value of this node (estimated cost to reach the goal)
    List<String> pathTaken; // Represents the path taken to reach this node from the start node

    // Constructor to initialize a SearchNode object with the given parameters
    public SearchNode(int posX, int posY, int distance, int heuristic, List<String> pathTaken) {
        this.posX = posX; // Set the x-coordinate of the node
        this.posY = posY; // Set the y-coordinate of the node
        this.distance = distance; // Set the distance from the start node to this node
        this.heuristic = heuristic; // Set the heuristic value of this node
        // Create a new ArrayList to store the path taken to reach this node, copying the provided path
        this.pathTaken = new ArrayList<>(pathTaken);
    }

    // Override the compareTo method to compare nodes based on their total cost (distance + heuristic)
    public int compareTo(SearchNode other) {
        // Calculate the total cost of this node (distance + heuristic)
        int totalCost = this.distance + this.heuristic;
        // Calculate the total cost of the other node (distance + heuristic)
        int otherTotalCost = other.distance + other.heuristic;
        // Compare the total costs of the two nodes and return the result
        return Integer.compare(totalCost, otherTotalCost);
    }
}

public class Main {

    // Define a method called isValidMove that takes a 2D character array (grid) and two integers (x and y) as parameters,
    // and returns a boolean value indicating whether the move to the specified position (x, y) is valid
    public static boolean isValidMove(char[][] grid, int x, int y) {
        int n = grid.length; // Get the number of rows in the grid
        int m = grid[0].length; // Get the number of columns in the grid
        // Check if the specified position (x, y) is within the bounds of the grid and is not blocked by an obstacle ('0')
        return x >= 0 && x < n && y >= 0 && y < m && grid[x][y] != '0';
    }

    // Define a method called calculateHeuristicDistance that takes four integers (x, y, goalX, goalY) as parameters
    // and returns an integer representing the heuristic value, which estimates the cost to reach the goal from the current position
    public static int calculateHeuristicDistance(int x, int y, int goalX, int goalY) {
        // Calculate the absolute difference in x-coordinates and y-coordinates between the current position and the goal position
        int dx = Math.abs(x - goalX); // Absolute difference in x-coordinates
        int dy = Math.abs(y - goalY); // Absolute difference in y-coordinates
        // Calculate the Euclidean distance heuristic adjusted for obstacles, which is the square root of the sum of the squares of dx and dy
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    // Define a method called findPath using the A* algorithm that takes a 2D character array representing the grid,
    // and the starting and ending coordinates as parameters, and returns a list of strings representing the path from the start to the end
    public static List<String> findPath(char[][] grid, int startX, int startY, int endX, int endY) {
        // Define arrays to represent possible movements in the grid (Right, Left, Down, Up)
        int[] dx = {0, 0, 1, -1}; // Define movement in the x-direction
        int[] dy = {1, -1, 0, 0}; // Define movement in the y-direction
        String[] directions = {"Right", "Left", "Down", "Up"}; // Define corresponding directions for each movement
        // Create a priority queue to store nodes to be explored, sorted by their total cost (distance + heuristic)
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();
        // Add the starting node to the priority queue with a distance of 0 and the heuristic value calculated from the starting position
        pq.add(new SearchNode(startX, startY, 0, calculateHeuristicDistance(startX, startY, endX, endY), new ArrayList<>()));

        // Create a boolean array to mark visited cells in the grid
        boolean[][] visited = new boolean[grid.length][grid[0].length];

        // Loop until the priority queue is empty
        while (!pq.isEmpty()) {
            // Extract the node with the lowest total cost from the priority queue
            SearchNode current = pq.poll();

            // Check if the current node is the goal node
            if (current.posX == endX && current.posY == endY) {
                return current.pathTaken; // Return the path when the goal is reached
            }

            // Check if the current cell has already been visited
            if (visited[current.posX][current.posY]) {
                continue; // Skip already visited cells
            }

            // Mark the current cell as visited
            visited[current.posX][current.posY] = true;

            // Loop through each possible direction
            for (int i = 0; i < 4; i++) {
                int newX = current.posX;
                int newY = current.posY;

                // Keep sliding until hitting a wall, obstacle, or reaching the end of the slide
                while (isValidMove(grid, newX + dx[i], newY + dy[i])) {
                    newX += dx[i];
                    newY += dy[i];

                    // If the new position is the goal, return the path
                    if (newX == endX && newY == endY) {
                        List<String> newPath = new ArrayList<>(current.pathTaken);
                        newPath.add("Move " + directions[i] + " to (" + (newY + 1) + "," + (newX + 1) + ")");
                        return newPath;
                    }
                }

                // If the current cell is a rock (denoted by '0'), change direction and continue sliding
                if (grid[newX][newY] == '0') {
                    // Flag to track if a valid move is found
                    boolean foundValidMove = false;
                    // Loop through all possible directions
                    for (int j = 0; j < 4; j++) {
                        // Calculate the next coordinates based on the current direction
                        int nextX = newX + dx[j];
                        int nextY = newY + dy[j];
                        // Check if the next move is valid and unvisited
                        if (isValidMove(grid, nextX, nextY) && !visited[nextX][nextY]) {
                            // If a valid move is found, update the new coordinates and set the flag to true
                            newX = nextX;
                            newY = nextY;
                            foundValidMove = true;
                            // Exit the loop since a valid move is found
                            break;
                        }
                    }
                    // If no valid move is found, break the loop and continue to the next iteration
                    if (!foundValidMove) {
                        break;
                    }
                }

                // Add the move to the path if the position has changed
                if (newX != current.posX || newY != current.posY) {
                    List<String> newPath = new ArrayList<>(current.pathTaken);
                    newPath.add("Move " + directions[i] + " to (" + (newY + 1) + "," + (newX + 1) + ")");
                    // Add the new node to the priority queue with updated distance, heuristic, and path
                    pq.add(new SearchNode(newX, newY, current.distance + 1, calculateHeuristicDistance(newX, newY, endX, endY), newPath));
                }
            }
        }

        return null; // If no path is found
    }

    public static void main(String[] args) {
        // Array containing the filenames to search for
        String[] fileNames = {"input.txt", "maze10_4.txt", "puzzle_10.txt"};

        // Try block to handle potential IOException, FileNotFoundException,
        // IllegalArgumentException, and IndexOutOfBoundsException
        try {
            // Loop through each filename
            for (String fileName : fileNames) {
                // Create a File object with the specified file name
                File file = new File(fileName);

                // Check if the file exists
                if (file.exists()) {
                    System.out.println("Path finding for puzzle: "+file);
                    // If the file exists, read input from the file
                    readMazeFromFile(file.getPath());
                    System.out.println();
                } else {
                    // If the file does not exist, print a message
                    System.out.println("File not found: " + fileName);
                }
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            // If an IllegalArgumentException or IndexOutOfBoundsException occurs,
            // print the message
            System.out.println("Error: Invalid argument or index - " + e.getMessage());
        } catch (IOException e) {
            // If an IOException occurs during file reading, print the message
            System.out.println("Error: Failed to read the file - " + e.getMessage());
        }
    }

    // Method to read input from file
    public static void readMazeFromFile(String filePath) throws IOException {
        // Create a BufferedReader object to read from the file
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        // Initialize an empty list to store each line of the file
        List<String> lines = new ArrayList<>();

        // Initialize a variable to store each line read from the file
        String line;

        // Read each line from the file until the end is reached
        while ((line = br.readLine()) != null) {
            // Add the line to the list of lines if it's not empty
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }

        // Close the BufferedReader to release system resources
        br.close();

        // Extract the dimensions of the map (height and width)
        int height = lines.size(); // Number of rows in the map
        int width = lines.get(0).length(); // Number of columns in the map

        // Create a 2D array to represent the map grid
        char[][] grid = new char[height][width];

        // Initialize variables to store the coordinates of the starting and ending points
        int startX = -1, startY = -1, endX = -1, endY = -1;

        // Loop through each cell of the map
        for (int i = 0; i < height; i++) {
            String currentLine = lines.get(i);
            for (int j = 0; j < width; j++) {
                // Get the character representing the cell at position (i, j)
                if (currentLine.length() > j) {
                    char c = currentLine.charAt(j);
                    // Store the character in the grid array
                    grid[i][j] = c;
                    // Check if the current cell is the starting point 'S' or the ending point 'F'
                    if (c == 'S') {
                        startX = i; // Store the row index of the starting point
                        startY = j; // Store the column index of the starting point
                    } else if (c == 'F') {
                        endX = i; // Store the row index of the ending point
                        endY = j; // Store the column index of the ending point
                    }
                }
            }
        }

        // If the starting or ending points are not found, print an error message
        if (startX == -1 || startY == -1 || endX == -1 || endY == -1) {
            System.err.println("Invalid starting or ending points in the maze file.");
            return;
        }

        // Print the coordinates of the starting point
        System.out.println("1. Start at (" + (startY + 1) + "," + (startX + 1) + ")");

        // Find the solution path using the A* algorithm
        List<String> solutionPath = findPath(grid, startX, startY, endX, endY);

        // Print the solution path
        if (solutionPath != null) {
            // Print each step of the solution path
            for (int i = 0; i < solutionPath.size(); i++) {
                System.out.println((i + 2) + ". " + solutionPath.get(i)); // Adjust step numbering to start from 2
            }
            // Print "Done!" after reaching the end of the solution path
            System.out.println((solutionPath.size() + 2) + ". Done!"); // Adjust step numbering for final step
        } else {
            // If no solution is found, print a message indicating it
            System.out.println("No solution found.");
        }
    }
}