package candys2.Game.player;

import candys2.Game.enemy.Penguin;
import com.badlogic.gdx.Input;
import core.Engine;
import util.*;

public class Player {
    public boolean lookCamera;
    public float roomFrame;
    public float staticFrame;
    public float position;
    public float flashDelay;
    public boolean flashNow;
    public float flashAlpha;
    public float flashSpeed;
    public float signalLost;
    public int cameraFlashes;
    public float flashX;
    public float flashY;

    public int flickerTimes;
    public float viewAlpha;
    public float jumpscareDelay;
    public String jumpscareEnemy;

    public Telephone telephone = new Telephone();
    public Monitor monitor = new Monitor();

    public void load(TextureHandler textureHandler, boolean mapDebug){
        telephone.load(textureHandler);
        monitor.load(textureHandler, mapDebug);
    }

    public void reset(){
        telephone.reset();
        monitor.reset();
        lookCamera = false;
        roomFrame = 0;
        staticFrame = 0;
        position = 0;
        flashDelay = 0;
        flashSpeed = 0;
        flashAlpha = 0;
        signalLost = 0;
        cameraFlashes = 0;
        flickerTimes = 2;
        viewAlpha = 0;
        jumpscareDelay = 0;
        jumpscareEnemy = "";
    }

    public void update(Engine engine, boolean limitedStorage, boolean faultyPhones){
        InputManager inputManager = engine.appHandler.getInput();
        SoundHandler soundHandler = engine.appHandler.soundHandler;
        Window window = engine.appHandler.window;

        if (jumpscareDelay == 0 && !jumpscareEnemy.isEmpty()) return;

        if (!jumpscareEnemy.isEmpty()){
            jumpscareDelay -= Time.getDelta();
            if (jumpscareDelay <= 0.5f){
                viewAlpha -= Time.getDelta() * 7;
                if (viewAlpha <= 0){
                    viewAlpha = 0;
                    if (flickerTimes > 0) {
                        flickerTimes--;
                        viewAlpha = 1;
                    }
                }
            }

            if (jumpscareDelay <= 0){
                jumpscareDelay = 0;
                soundHandler.stopAllSounds();
                VideoManager.setRequest("game/enemy/" + jumpscareEnemy + "/jumpscare");
                return;
            }
        }

        if (jumpscareEnemy.isEmpty() || jumpscareDelay > 0.75f){
            viewAlpha += Time.getDelta() * 1.5f;
            if (viewAlpha > 1) viewAlpha = 1;
        }

        float mouseX = inputManager.getX();
        float mouseY = inputManager.getY();
        boolean space = inputManager.keyPressed(Input.Keys.SPACE);
        flashNow = false;

        //hover logic for camera
        if (flashAlpha == 0) {
            if ((int) roomFrame == 0 && mouseY < (float) window.height() / 6 && !lookCamera) {
                lookCamera = true;
                roomFrame = 1;
                soundHandler.play("monitorUp");
                soundHandler.stop("monitorDown");
            } else if ((int) roomFrame == 17 && mouseY > (float) window.height() / 6 * 5 && lookCamera) {
                lookCamera = false;
                roomFrame = 16.99f;
                soundHandler.play("monitorDown");
                soundHandler.stop("monitorUp");
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

        if (signalLost > 0 && roomFrame < 17){
            signalLost = 0;
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
            if (space && flashDelay == 0 && (!limitedStorage || cameraFlashes < 100)){
                flashDelay = 0.65f;
                flashSpeed = 8;
                cameraFlashes++;
                soundHandler.play("flash");
                flashNow = true;
                flashX = inputManager.getX() + position;
                flashY = inputManager.getY() + 24;
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
        } else if ((int) roomFrame == 17){
            staticFrame += Time.getDelta() * 10;
            if (staticFrame >= 3){
                staticFrame = 0;
            }

            if (signalLost > 0){
                if (signalLost == 6.99f) soundHandler.play("signalLost");
                signalLost -= Time.getDelta() * 20;
                if (signalLost <= 0) signalLost = 0;
            }
        }

        monitor.input(inputManager, soundHandler, (int) roomFrame);
        monitor.update(soundHandler);
        if (!monitor.error && monitor.glitchCooldown == 0 && (int) roomFrame == 17) {
            telephone.input(inputManager, soundHandler, monitor.activeCamera, faultyPhones);
        }

        telephone.update(soundHandler, faultyPhones, !monitor.error && monitor.glitchCooldown == 0);

        if (flashDelay > 0){
            flashDelay -= Time.getDelta();
            if (flashDelay < 0){
                flashDelay = 0;
            }
        }
    }

    public void setJumpscare(String name, float delay){
        if (delay > jumpscareDelay && jumpscareDelay > 0) return;
        jumpscareDelay = delay;
        jumpscareEnemy = name;
    }

    public void dispose(){
        monitor.dispose();
        telephone.dispose();
    }

    public void setSignalLost(){
        if ((int) signalLost == 0) signalLost = 6.99f;
    }

    public void cancelSignalLost(){
        signalLost = 0;
    }
}
