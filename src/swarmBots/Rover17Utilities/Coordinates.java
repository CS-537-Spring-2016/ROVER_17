package swarmBots.Rover17Utilities;

public class Coordinates {
    private int x;
    private int y;

    Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY(){
        return y;
    }

    public void setY(int y){
        this.y = y;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;

        Coordinates ip = (Coordinates) o;

        return (getX() == ip.getX() && getY() == ip.getY());

    }


    public String toString(){
        return getX() + ", " +  getY();
    }

    public String appendCoordinate(){
        return (getX() + "000" + getY());
    }


    @Override
    public int hashCode(){
        return Integer.parseInt(appendCoordinate());
    }
}
