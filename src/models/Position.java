package models;

import java.util.*;

/**
 *
 */
public class Position {

    public Position(int x, int y, ArrayList<Position> listeAdjascence) {
        this.x = x;
        this.y = y;
        this.listeAdjascence = listeAdjascence;
    }

    private int x;

    private int y;

    private List<Position> listeAdjascence;


    /**
     * @return
     */
    public Boolean estDansIleDestination() {
        // TODO implement here
        return null;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Position> getListeAdjascence() {
        return listeAdjascence;
    }

    public void setListeAdjascence(List<Position> listeAdjascence) {
        this.listeAdjascence = listeAdjascence;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            return ((Position) obj).getX() == x && ((Position) obj).getY() == y;
        }
        return false;
    }
}