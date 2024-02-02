package game.engine.util;

public class TextureRequest {
    private final String name;
    private TextureRequest next;

    public TextureRequest(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public TextureRequest getNext() {
        return next;
    }

    public void setNext(TextureRequest next) {
        this.next = next;
    }
}
