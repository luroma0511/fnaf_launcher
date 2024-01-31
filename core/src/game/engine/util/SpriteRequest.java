package game.engine.util;

public class SpriteRequest {
    private final String name;
    private final short width;
    private SpriteRequest next;

    public SpriteRequest(String name, short width){
        this.name = name;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public short getWidth() {
        return width;
    }

    public SpriteRequest getNext() {
        return next;
    }

    public void setNext(SpriteRequest next) {
        this.next = next;
    }
}
