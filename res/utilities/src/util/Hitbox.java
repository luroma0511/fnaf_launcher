package util;

public class Hitbox extends SpriteObject {
    public float size;

    public Hitbox(float size){
        super();
        this.size = size;
    }

    public void setSize(float size, float multiplier){
        this.size = size * multiplier;
    }

    public void setCoord(int x, int y){
        setX(x);
        setY(y);
        if (x == 0 && y == 0) size = 0;
    }

    public boolean isHovered(float mx, float my){
        float lineA = Math.abs(mx - getX());
        float lineB = Math.abs(my - getY());
        return Math.hypot(lineA, lineB) <= size;
    }
}