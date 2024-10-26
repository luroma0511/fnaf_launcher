package core;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import javax.swing.*;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main (String[] arg) {
        if (arg.length == 0){
            arg = new String[]{"Guest", ""};
            var options = new String[]{"Candy's 2", "Candy's 3"};
            int option = JOptionPane.showOptionDialog(null, "Choose a game", "Candy's Deluxe Mini-Launcher",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
            if (option == 0) arg[1] = "candys2";
            else if (option == 1) arg[1] = "candys3";
            else return;
        }

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(1280, 720);
        config.setResizable(false);
        String title = "";
        switch (arg[1]){
            case "candys2" -> title = "Candy's 2 Deluxe";
            case "candys3" -> title = "Candy's 3 Deluxe";
        }
        config.setWindowIcon("assets/" + arg[1] + "/logo.png");
        config.setTitle(title);
        new Lwjgl3Application(new Engine(1280, 720, arg), config);
    }
}