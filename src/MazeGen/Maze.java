package MazeGen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class Maze {
    public final int width;
    public final int height;
    public final Random random;
    private final JPanel panel;
    public List<Cell> cells;
    private final JFrame jFrame;
    public Cell start;
    public Cell stop;
    private ArrayList<Cell> visited;

    public Maze(int width, int height, Random random) {
        this.width = width;
        this.height = height;
        this.random = random;
        this.start = null;
        this.stop = null;
        generate();

        this.jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);

        this.panel = new JPanel();
        panel.setLayout(new GridLayout(height, width));
        jFrame.add(panel);
    }

    public void generate() {
        this.cells = new ArrayList<>();
        for (int i = 0; i < width * height; i++) {
            Cell cell = new Cell(new Random(), i);
            cell.updateBitField();
            cells.add(cell);
        }

        List<Cell> westCells = cells.stream().filter(c -> c.index % width == 0).collect(Collectors.toList());
        List<Cell> eastCells = cells.stream().filter(c -> c.index % (width) == width - 1).collect(Collectors.toList());
        List<Cell> topCells = cells.stream().filter(c -> c.index < width).collect(Collectors.toList());
        List<Cell> bottomCells = cells.stream().filter(c -> c.index >= width * (height - 1)).collect(Collectors.toList());

        westCells.forEach(c -> c.addFlag(Direction.W));
        eastCells.forEach(c -> c.addFlag(Direction.E));
        topCells.forEach(c -> c.addFlag(Direction.N));
        bottomCells.forEach(c -> c.addFlag(Direction.S));

        this.start = setAsGateway(topCells, Direction.N);
        this.stop = setAsGateway(bottomCells, Direction.S);
    }

    private Cell setAsGateway(List<Cell> list, Direction direction) {
        Cell cell;
        do {
            cell = list.get(random.nextInt(list.size() - 1));
        } while (cell.equals(start));
        cell.setGate(true);
        cell.removeFlag(direction);
        return cell;
    }

    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int i = cell.index;
        if (i - 1 >= 0 && i / width == (i - 1) / width) {
            Cell prev = cells.get(i - 1);
            if (prev.isAccessibleFrom(Direction.E) && cell.isAccessibleFrom(Direction.W)) neighbors.add(prev);
        }
        if (i + 1 <= width * height - 1 && i / width == (i + 1) / width) {
            Cell next = cells.get(i + 1);
            if (next.isAccessibleFrom(Direction.W) && cell.isAccessibleFrom(Direction.E)) neighbors.add(next);
        }
        if (i - width >= 0) {
            Cell prevR = cells.get(i - width);
            if (prevR.isAccessibleFrom(Direction.S) && cell.isAccessibleFrom(Direction.N)) neighbors.add(prevR);
        }
        if (i + width <= width * height - 1) {
            Cell nextR = cells.get(i + width);
            if (nextR.isAccessibleFrom(Direction.N) && cell.isAccessibleFrom(Direction.S)) neighbors.add(nextR);
        }
        return neighbors;
    }

    public boolean isSolvable() {
        this.visited = new ArrayList<>();
        List<Cell> visitedThisIteration;
        visited.add(start);
        do {
            visitedThisIteration = new ArrayList<>();
            for (Cell cell : visited) {
                List<Cell> unvisitedNeighbors = new ArrayList<>();
                for (Cell c : getNeighbors(cell)) {
                    if (!visited.contains(c) && !visitedThisIteration.contains(c)) unvisitedNeighbors.add(c);
                }
                visitedThisIteration.addAll(unvisitedNeighbors);
            }
            visited.addAll(visitedThisIteration);
        } while (visitedThisIteration.size() > 0);
        visited.forEach(c -> cells.get(cells.indexOf(c)).setVisited(true));
        return visited.contains(stop);
    }

    public void solve() {
        List<Cell> visited = new ArrayList<>();
        visited.add(start);
        List<Path> paths = new ArrayList<>();
        paths.add(new Path(start));
        List<Path> visitedPaths = new ArrayList<>();
        do {
            List<Path> addPath = new ArrayList<>();
            List<Path> removePath = new ArrayList<>();
            for (Path path : paths) {
                addPath = new ArrayList<>();
                Cell lastCell = path.last();
                List<Cell> unvisitedNeighbors = getNeighbors(lastCell).stream().filter(c -> !visited.contains(c)).collect(Collectors.toList());
                for (Cell unvisitedCell : unvisitedNeighbors) {
                    visited.add(unvisitedCell);
                    addPath.add(new Path(path, unvisitedCell));
                    if (unvisitedCell.equals(stop)) break;
                }
                removePath.add(path);
                if (path.isSolution(stop)) break;
            }
            paths.addAll(addPath);
            paths.removeAll(removePath);
            visitedPaths.addAll(removePath);
        } while (paths.size() > 0);
        visitedPaths.addAll(paths);
        if (visited.contains(stop) && visitedPaths.stream().anyMatch(p -> p.isSolution(stop))) {
            for (Path path : visitedPaths) {
                if (path.isSolution(stop)) {
                    path.cells.forEach(c -> cells.get(cells.indexOf(c)).setSolution(true));
                    break;
                } else path.cells.forEach(c -> cells.get(cells.indexOf(c)).setVisited(true));
            }
        }
    }

    public void display() {
        try {
            panel.removeAll();
            for (Cell cell : cells) {
                panel.add(new JLabel(new ImageIcon(cell.getImage())));
            }
            jFrame.pack();
//            jFrame.setLocationRelativeTo(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Container container = jFrame.getContentPane();
            BufferedImage img = new BufferedImage(container.getWidth(), container.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            container.printAll(g);
            g.dispose();
            ImageIO.write(img, "png", new File("mazes/" + UUID.randomUUID() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
