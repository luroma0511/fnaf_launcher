package game.deluxe.state.Game.Objects.Character;

import java.util.Random;

import game.deluxe.state.Game.Objects.Character.Attributes.Attack;
import game.deluxe.state.Game.Objects.Character.Attributes.Door;
import game.deluxe.state.Game.Objects.Character.Attributes.Hitbox;
import game.engine.util.*;

public class Rat extends SpriteObject {
    private final Random random;

    private final Hitbox hitbox;
    private final Attack attack;
    private final Door door;

    private byte ai;
    private byte roomState;
    private byte side;

    public Rat(byte ai){
        super();
        this.ai = ai;
        random = new Random();
        hitbox = new Hitbox();
        door = new Door(4, 4, 13, 30);
        attack = new Attack(1, 1.15f, 0.225f, (byte) 0, 24, 30);
    }

    public void input(Engine engine, float mx, float my){
        switch (roomState){
            case 0:
                door.input(hitbox, side, mx, my);
                break;
            case 1:
                attack.input(engine);
                break;
        }
    }

    public void update(Engine engine){
        switch (roomState){
            case 0:
                if (doorUpdate(engine)) return;
                if (!door.isDoorSignal() && door.getDoorCooldown() == 0) {
                    if (door.getLeftDoorTimes() == 3) {
                        side = (byte) (1 + random.nextInt(2));
                    } else if (door.getMiddleDoorTimes() == 3) {
                        side = (byte) (2 * random.nextInt(2));
                    } else if (door.getRightDoorTimes() == 3) {
                        side = (byte) random.nextInt(2);
                    } else {
                        side = (byte) random.nextInt(3);
                    }
                }
                break;
            case 1:
                if (roomUpdate(engine)) return;
                break;
        }
    }

    private boolean doorUpdate(Engine engine){
        if (door.update(engine, hitbox)){
            transitionRoomState((byte) 1);
            return true;
        }
        return false;
    }

    private boolean roomUpdate(Engine engine){
        if (attack.update(engine)){
            transitionRoomState((byte) 2);
            return true;
        }
        return false;
    }

    public void transitionRoomState(byte roomState){
        this.roomState = roomState;
        if (roomState == 0){
            door.reset();
        } else if (roomState == 1){
            attack.reset();
        }
    }

    public void jumpscare(VideoManager videoManager){
        videoManager.setRequest("game/Rat/Jumpscare/room");
    }

    private final String[] textures = new String[]{
            "Battle/Left",
            "Battle/Middle",
            "Battle/Right",
            "Bed/LeftPeek",
            "Bed/RightPeek",
            "Leaving/Left",
            "Leaving/Right",
            "Tape/Tape",
            "Looking Away/Left",
            "Looking Away/Middle",
            "Looking Away/Right"
    };

    public void load(Request request){
        String prefix = "game/Rat/";
        for (String file: textures){
            request.addImageRequest(prefix + file + ".png");
        }
    }
}
