package candys3.Game.Objects.Character;

import candys3.Game.Game;
import candys3.Game.Objects.Flashlight;
import candys3.Game.Objects.Functions.*;
import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import candys3.GameData;
import util.Hitbox;
import util.SoundHandler;
import util.SpriteObject;
import util.Time;

public class Vinnie extends SpriteObject {
    private final Hitbox hitbox;
    private Door door;
    private final Bed bed;
    private final Peek peek;
    private final Leave leave;
    private final Twitch twitch;
    private final Tape tape;

    private float transitionCooldown;

    private final String name;
    private final byte type;
    private byte state;
    private byte side;

    private float attackPosition;
    private int targetAttackPosition;
    private float turns;
    private boolean turnPositive;
    private boolean attack;
    private float flashTime;
    private float killTimer;
    private float reactionTimer;
    private int moves;

    public Vinnie(byte type){
        super();
        name = type == 0 ? "Vinnie" : "Shadow Vinnie";
        this.type = type;
        twitch = new Twitch();
        tape = new Tape();
        hitbox = new Hitbox(100);
        if (type == 0) door = new Door(8);
        else if (type == 1) door = new Door(8);
        bed = new Bed();
        peek = new Peek();
        leave = new Leave(1.25f);
        door.reset(5, 4);
    }

    public boolean execute(SoundHandler soundHandler, Game game, Player player, Room room, boolean twitching){
        var flashlight = player.getFlashlight();
        twitching = input(soundHandler, game, player, room, flashlight.getX(), flashlight.getY(), twitching);
        twitching = update(soundHandler, game, player, room, flashlight, twitching);
        return twitching;
    }

    private boolean input(SoundHandler soundHandler, Game game, Player player, Room room, float mx, float my, boolean twitching){
        boolean hovered = hitbox.isHovered(mx, my);
        boolean imageHovered = this.mouseOverWithPanning(mx, my);

        switch (state){
            case 0:
                if (door.input(soundHandler, room, hovered)) {
                    hitbox.setCoord(0, 0);
                }
                break;
            case 1:
                boolean looking = room.getFrame() == 0 && room.getState() == 0;
                if (!attack && moves > 0 && looking && imageHovered){
                    if (!player.isScared()) player.setScared();
                    if (!player.isAttack()) player.setAttack();
                    soundHandler.play("attack_begin");
                    soundHandler.play("attack");
                    soundHandler.setSoundEffect(soundHandler.LOOP, "attack", 1);
                    killTimer = 1;
                    attack = true;
                }

                if (hovered && looking) {
                    if (attack) {
                        if (reactionTimer <= 0.25f) reactionTimer = Time.increaseTimeValue(reactionTimer, 0.25f, 0.5f);
                        killTimer = Time.increaseTimeValue(killTimer, 2, 0.75f);
                        flashTime = Time.decreaseTimeValue(flashTime, 0, 1);
                        player.addFlashPoints();
                    }
                    if (flashTime == 0 && moves > 0) hitbox.setCoord(0, 0);
                } else if ((flashTime > 0 || moves > 0) && (int) turns == 0) {
                    reactionTimer = Time.decreaseTimeValue(reactionTimer, 0, 1);
                    if (reactionTimer <= 0.375f) killTimer = Time.decreaseTimeValue(killTimer, 0, 1);
                }
                if (room.getFrame() == 0 && room.getState() == 0 && twitch.update(hovered)) twitching = true;
                break;
            case 2:
                bed.input(soundHandler, player, room, imageHovered);
                break;
            case 3:
                peek.input(soundHandler, game, player, room, hovered, 1.25f);
                if (room.getFrame() == 0 && room.getState() == 0 && twitch.update(hovered)) twitching = true;
                break;
        }
        return twitching;
    }

    private boolean update(SoundHandler soundHandler, Game game, Player player, Room room, Flashlight flashlight, boolean twitching){
        var rat = game.characters.getRat();
        var cat = game.characters.getCat();

        switch (state) {
            case 0:
                door.update(soundHandler, player, side);
                boolean delayEntry = (cat != null && cat.getState() == 1)
                        || (rat != null && (rat.getState() == 1 || rat.getState() >= 3));
                if (door.isSignal()) {
                    if (delayEntry) {
                        door.pause();
                        break;
                    }
                    if (rat != null && rat.getState() == 0 && rat.getSide() != -1) {
                        if (rat.getSide() == 0) {
                            side = (byte) (Math.random() * 2 + 1);
                        } else if (rat.getSide() == 1) {
                            if (Math.random() * 2 == 0) side = 0;
                            else side = 2;
                        } else {
                            side = (byte) (Math.random() * 2);
                        }
                    } else {
                        side = (byte) (Math.random() * 3);
                    }

                    if (side == 0) hitbox.setCoord(471, 655);
                    else if (side == 1) hitbox.setCoord(1531, 707);
                    else hitbox.setCoord(2537, 681);
                    changePath();
                }
                if (!door.isTimeUp()) return twitching;
                transitionRoomState((byte) 1);
                soundHandler.play("get_in");
                player.setBlacknessTimes(2);
                player.setBlacknessSpeed(6);
                break;
            case 1:
                if (killTimer == 0 && reactionTimer == 0 && !game.noJumpscares){
                    player.setJumpscare();
                    if (type == 0) Jumpscare.set("game/Cat/Jumpscare/room");
                    else if (type == 1) {
                        if (!game.classicJumpscares) Jumpscare.set("game/Shadow Cat/Jumpscare", 0.75f);
                        else Jumpscare.classicSet("game/Shadow Cat/Classic/Jumpscare", 2, 145, 850, 20);
                    } else Jumpscare.set("game/Shadow Cat/HellJumpscare");
                    return twitching;
                }

                if (player.isScared()){
                    float pitch = Time.increaseTimeValue(soundHandler.getSoundEffect(soundHandler.PITCH, "attack"), 2, 0.01f);
                    soundHandler.setSoundEffect(soundHandler.PITCH, "attack", pitch);
                }

                if (side == 0){
                    if (attackPosition == 0) hitbox.setCoord(407, 867);
                } else if (side == 1){
                    if (attackPosition == 0) hitbox.setCoord(1509, 811);
                } else {
                    if (attackPosition == 0) hitbox.setCoord(2189, 880);
                }

                if (moves == 0 && turns == 0){

                }

                if (turns > 0){
                    float speed = Time.getDelta() * 20;
                    turns -= speed;
                    if (turnPositive) attackPosition += speed;
                    else attackPosition -= speed;

                    if (attackPosition < 0) attackPosition += 14;
                    else if (attackPosition >= 14) attackPosition -= 14;

                    if (turns <= 0){
                        attackPosition = targetAttackPosition;
                        turns = 0;
                        soundHandler.stop("dodgeVinnie");
                    }
                } else if (flashTime == 0){
                    moves--;
                    flashTime = 0.35f;
                    reactionTimer = 0.35f;
                    soundHandler.play("dodgeVinnie");
                    turns = (float) (2 * Math.random() * 7);
                    turnPositive = Math.random() < 0.5;
                    if (turnPositive) {
                        targetAttackPosition += (int) turns;
                        if (targetAttackPosition >= 14) targetAttackPosition -= 14;
                        turns--;
                        attackPosition++;
                    } else {
                        targetAttackPosition -= (int) turns;
                        if (targetAttackPosition < 0) targetAttackPosition += 14;
                    }
                }

                break;
        }

        return twitching;
    }

    public void changePath() {
        hitbox.setSize(100, GameData.hitboxMultiplier);
        if (state == 0) {
            if (side == 0) {
                setPath("game/" + name + "/Door/Left");
                setX(418);
                setY(373);
                setWidth(70);
            } else if (side == 1) {
                setPath("game/" + name + "/Door/Middle");
                setX(1409);
                setY(380);
                setWidth(231);
            } else {
                setPath("game/" + name + "/Door/Right");
                setX(2490);
                setY(320);
                setWidth(94);
            }
        } else if (state == 1) {
            if (side == 0) {
                hitbox.setSize(75, GameData.hitboxMultiplier);
                setPath("game/" + name + "/Battle/Left/");
                setX(166);
                setY(288);
                setWidth(504);
                setHeight(675);
            } else if (side == 1) {
                setPath("game/" + name + "/Battle/Middle/");
                setX(1185);
                setY(130);
                setWidth(698);
                setHeight(802);
            } else {
                setPath("game/" + name + "/Battle/Right/");
                setX(2222);
                setY(184);
                setWidth(707);
                setHeight(840);
            }
        } else if (state == 2) {
            if (side == 0) {
                setPath("game/" + name + "/Bed/Left");
                setX(0);
                setY(155);
                setWidth(915);
                setHeight(529);
            } else {
                setPath("game/" + name + "/Bed/Right");
                setX(1438);
                setY(190);
                setWidth(610);
                setHeight(521);
            }
        } else if (state == 3) {
            if (side == 0) {
                hitbox.setCoord(707, 450);
                setPath("game/" + name + "/Peek/Left");
                setX(415);
                setY(217);
                setWidth(425);
                setHeight(369);
            } else {
                hitbox.setCoord(2362, 470);
                setPath("game/" + name + "/Peek/Right");
                setX(2128);
                setY(195);
                setWidth(589);
                setHeight(496);
            }
        } else {
            hitbox.setCoord(0, 0);
            if (side == 0) {
                setPath("game/" + name + "/Leaving/Left");
                setX(280);
                setY(376);
                setWidth(210);
                setHeight(392);
            } else {
                setPath("game/" + name + "/Leaving/Right");
                setX(2490);
                setY(285);
                setWidth(311);
                setHeight(567);
            }
        }
    }

    public void transitionRoomState(byte state){
        this.state = state;
        if (state == 0) door.reset(5);
        else if (state == 1){
            attackPosition = 0;
            killTimer = 3;
            reactionTimer = 0.5f;
            flashTime = 0.85f;
            moves = 7;
        } else if (state == 2){
            if (type == 0) bed.reset(10, 1.15f);
            else bed.reset(8, 1.15f);
            tape.reset(4 - type);
        } else if (state == 3) {
            if (type == 0) peek.reset(3, 2.25f, 1.25f);
            else peek.reset(1.5f, 2.75f, 1.25f);
        }
        else if (state == 4) leave.reset();
        if (state != 0) changePath();
    }

    public String getName() {
        return name;
    }

    public byte getType() {
        return type;
    }

    public byte getState() {
        return state;
    }

    public byte getSide() {
        return side;
    }

    public int getTwitchFrame() {
        return twitch.getFrame();
    }

    public Door getDoor() {
        return door;
    }


    public float getAttackPosition() {
        return attackPosition;
    }

    public float getBedCooldown() {
        return bed.getCooldown();
    }

    public int getTapeFrame() {
        return tape.getFrame();
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public int getLeaveFrame(){
        return leave.getFrame();
    }
}
