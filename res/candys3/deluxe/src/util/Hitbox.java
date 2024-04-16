package util;

import core.data.Challenges;

public class Hitbox extends SpriteObject {
    public float size;

    public Hitbox(float size){
        super();
        this.size = size;
    }

    public void setSize(float size){
        if (Challenges.laserPointer) size *= 0.75f;
        this.size = size;
    }

    public void setCoord(int x, int y){
        setX(x);
        setY(y);
        if (x == 0 && y == 0) setSize(0);
        else if (size == 0) setSize(100);
    }

    public boolean isHovered(float mx, float my){
        float lineA = Math.abs(mx - getX());
        float lineB = Math.abs(my - getY());
        return Math.hypot(lineA, lineB) <= size;
    }
}