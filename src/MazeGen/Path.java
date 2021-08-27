package MazeGen;

import java.util.ArrayList;
import java.util.List;

public class Path {
    public final List<Cell> cells;

    public Path(Cell cell) {
        this.cells = new ArrayList<>();
        cells.add(cell);
    }

    public Path(Path oldPath, Cell cell) {
        this.cells = new ArrayList<>();
        cells.addAll(oldPath.cells);
        cells.add(cell);
    }

    public Cell last() {
        return cells.get(cells.size() - 1);
    }

    public void add(Cell c) {
        cells.add(c);
    }

    public boolean isSolution(Cell c) {
        return cells.contains(c);
    }
}
