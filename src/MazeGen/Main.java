package MazeGen;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int width = 35;
        int height = 16;

        Maze maze = new Maze(width, height, new Random());
        try {
            do {
                if(maze.isSolvable()) {
                    maze.display();
                    maze.save();
                    Thread.sleep(100);
                }
                maze.generate();
            } while (true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
