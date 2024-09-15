package util.error;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.io.PrintWriter;
import java.io.StringWriter;

public class GameError {
    public final String text;
    public BitmapFont font;

    public GameError(Exception e){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        text = stringWriter.toString();
    }
}
