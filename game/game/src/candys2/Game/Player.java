package candys2.Game;

import com.badlogic.gdx.Input;
import core.Engine;
import util.InputManager;
import util.Time;
import util.Window;

public class Player {
    public boolean lookCamera;
    public float roomFrame;
    public int camera;
    public float position;
    public float flashDelay;
    public float flashAlpha;
    public float flashSpeed;

    public float camera1Telephone;
    public float camera2Telephone;
    public float camera3Telephone;
    public float camera4Telephone;
    public float camera5Telephone;
    public float camera6Telephone;

    public void update(Engine candys2Deluxe){
        InputManager inputManager = candys2Deluxe.appHandler.getInput();
        Window window = candys2Deluxe.appHandler.window;

        float mouseX = inputManager.getX();
        float mouseY = inputManager.getY();

        boolean space = inputManager.keyPressed(Input.Keys.SPACE);

        //hover logic for camera
        if (flashAlpha == 0) {
            if ((int) roomFrame == 0 && mouseY < (float) window.height() / 6 && !lookCamera) {
                lookCamera = true;
                roomFrame = 1;
            } else if ((int) roomFrame == 17 && mouseY > (float) window.height() / 6 * 5 && lookCamera) {
                lookCamera = false;
                roomFrame = 16.99f;
            }
        }

        //frame logic
        if (lookCamera && roomFrame < 17){
            roomFrame += Time.getDelta() * 26;
            if (roomFrame > 17){
                roomFrame = 17;
            }
        } else if (!lookCamera && roomFrame > 1){
            roomFrame -= Time.getDelta() * 26;
            if (roomFrame < 1){
                roomFrame = 0;
            }
        }

        //room logic
        if ((int) roomFrame == 0){
            //look mechanic
            float limit = 1440 - 1280;
            float unit = limit / 1280;
            float targetPosition = unit * mouseX;
            float distance = (targetPosition - position) / 10;
            position += distance;

            //flash mechanic
            if (space && flashDelay == 0){
                flashDelay = 1;
                flashSpeed = 8;
                System.out.println("Flashed");
            }

            if (flashSpeed == 8){
               flashAlpha += Time.getDelta() * flashSpeed;
               if (flashAlpha > 1){
                   flashAlpha = 1;
                   flashSpeed = 4;
               }
            } else if (flashAlpha > 0){
                flashAlpha -= Time.getDelta() * flashSpeed;
                if (flashAlpha < 0){
                    flashAlpha = 0;
                }
            }
        }

        if (flashDelay > 0){
            flashDelay -= Time.getDelta();
            if (flashDelay < 0){
                flashDelay = 0;
            }
        }
    }
}
