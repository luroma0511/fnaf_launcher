package util;

import com.badlogic.gdx.graphics.Pixmap;

public class ImageData {
    private String file;
    private Pixmap.Format format;

    public ImageData(String file, Pixmap.Format format) {
        this.file = file;
        this.format = format;
    }

    public String getFile() {
        return file;
    }

    public Pixmap.Format getFormat() {
        return format;
    }
}
