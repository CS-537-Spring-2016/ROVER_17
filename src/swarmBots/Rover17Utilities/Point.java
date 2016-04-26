package swarmBots.Rover17Utilities;


public class Point {
    private int x, y;
    private boolean isBlocked;

    public Point(){
    }

    public Point(int x, int y, boolean isBlocked){
        this.x = x;
        this.y = y;
        this.isBlocked = isBlocked;
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

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point p = (Point) o;
        return (getX() == p.getX() && getY() == p.getY());
    }
}
