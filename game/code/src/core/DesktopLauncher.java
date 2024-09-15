package core;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main (String[] arg) {
//        arg[1] = "candys2";
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(1280, 720);
        config.setResizable(false);
        switch (arg[1]){
            case "candys2" -> {
                config.setTitle("Candy's 2 Deluxe");
                config.setWindowIcon("assets/candys3/logo.png");
            }
            case "candys3" -> {
                config.setTitle("Candy's 3 Deluxe");
                config.setWindowIcon("assets/candys3/logo.png");
            }
        }
        new Lwjgl3Application(new Engine(1280, 720, arg), config);
    }
}