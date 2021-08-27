package MazeGen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cell {
    public final int index;
    public int bitField;
    public List<Direction> directions;
    public boolean visited;
    public boolean solution;
    public boolean gate;

    public void updateBitField() {
        this.bitField = 0;
        for (Direction d : directions) {
            this.bitField += d.bit;
        }
    }

    public void addFlag(Direction d) {
        if (!directions.contains(d)) directions.add(d);
        updateBitField();
    }

    public void removeFlag(Direction d) {
        directions.remove(d);
        updateBitField();
    }

    public Cell(Random random, int index) {
        this.index = index;
        this.directions = new ArrayList<>();
        if (random.nextDouble() < 0.25) directions.add(Direction.N);
        else if (random.nextDouble() < 0.5) directions.add(Direction.E);
        else if (random.nextDouble() < 0.75) directions.add(Direction.S);
        else if (random.nextDouble() >= 0.75) directions.add(Direction.W);
    }

    public boolean isAccessibleFrom(Direction d) {
        return !directions.contains(d);
    }

    public void setVisited(boolean b) {
        this.visited = b;
    }

    public void setSolution(boolean b) {
        this.solution = b;
    }

    public void setGate(boolean b) {
        this.gate = b;
    }

    public BufferedImage getImage() throws IOException {
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.drawImage(ImageIO.read(new File("images/" + bitField + ".png")), 0, 0, null);
        if (solution) {
            g.drawImage(ImageIO.read(new File("images/sol.png")), 0, 0, null);
        } else if (visited) {
            g.drawImage(ImageIO.read(new File("images/expl.png")), 0, 0, null);
        }
        if(gate) {
            g.drawImage(ImageIO.read(new File("images/gate.png")), 0, 0, null);
        }
        g.dispose();
        return img;
    }
}
