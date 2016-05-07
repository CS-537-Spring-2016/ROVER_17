package swarmBots.Rover17Utilities;


public class Node {
    private Coordinates coord;
    private boolean passable = true;

    public Node(){
        coord = new Coordinates(-1,-1);
        passable = false;
    }

    public Node(Coordinates coord) {
        this.coord = coord;
    }
    public Node(Coordinates coord, boolean passable) {
        this.coord = coord;
        this.passable = passable;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public boolean getPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        return getCoord().equals(node.getCoord());

    }

    @Override
    public String toString() {
        return "Node (" +
                coord +
                ')';
    }

    @Override
    public int hashCode() {
        return coord.hashCode();
    }
}
