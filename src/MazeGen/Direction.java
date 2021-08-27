package MazeGen;

public enum Direction {
    N(1),
    W(2),
    S(4),
    E(8),
    ;

    public int bit;
    Direction(int bit) {
        this.bit = bit;
    }
}
