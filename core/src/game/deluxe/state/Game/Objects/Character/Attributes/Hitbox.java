package game.deluxe.state.Game.Objects.Character.Attributes;

import game.engine.util.SpriteObject;

public class Hitbox extends SpriteObject {
    public float size;

    private short leftDoorX;
    private short leftDoorY;
    private short middleDoorX;
    private short middleDoorY;
    private short rightDoorX;
    private short rightDoorY;

    public Hitbox(){
        super();
        size = 100;
    }

    public void initLeftDoor(int x, int y){
        leftDoorX = (short) x;
        leftDoorY = (short) y;
    }

    public void initMiddleDoor(int x, int y){
        middleDoorX = (short) x;
        middleDoorY = (short) y;
    }

    public void initRightDoor(int x, int y){
        rightDoorX = (short) x;
        rightDoorY = (short) y;
    }

    public void setCoord(int x, int y){
        setX(x);
        setY(y);
    }

    public boolean isHovered(float mx, float my){
        float lineA = Math.abs(mx - getX());
        float lineB = Math.abs(my - getY());
        return Math.hypot(lineA, lineB) <= size;
    }

    public short getLeftDoorX() {
        return leftDoorX;
    }

    public short getLeftDoorY() {
        return leftDoorY;
    }

    public short getMiddleDoorX() {
        return middleDoorX;
    }

    public short getMiddleDoorY() {
        return middleDoorY;
    }

    public short getRightDoorX() {
        return rightDoorX;
    }

    public short getRightDoorY() {
        return rightDoorY;
    }
}
