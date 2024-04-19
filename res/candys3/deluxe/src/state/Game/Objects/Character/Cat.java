package state.Game.Objects.Character;

import java.util.Arrays;
import java.util.Random;

import data.Challenges;
import state.Game.Objects.Character.Attributes.*;
import state.Game.Objects.Flashlight;
import state.Game.Objects.Player;
import state.Game.Objects.Room;
import util.*;

public class Cat extends SpriteObject {
    private final Random random;

    private final Hitbox hitbox;
    private final Attack attack;
    private final BedSide bedSide;
    private final Bed bed;
    private final Peek peek;
    private final Leave leave;
    private final Twitch twitch;

    private byte ai;
    private byte roomState;
    private byte side;
    private boolean catPlay;

    public Cat(byte ai){
        super();
        this.ai = ai;
        random = new Random();
        twitch = new Twitch();
        hitbox = new Hitbox(100);
        bedSide = new BedSide(10);
        bedSide.reset();
        attack = new Attack();
        bed = new Bed();
        peek = new Peek();
        leave = new Leave(0);
        catPlay = false;
    }

    public boolean execute(Player player, Rat rat, Room room, Flashlight flashlight, boolean twitching){
        twitching = input(player, room, flashlight.getX(), flashlight.getY(), twitching);
        update(rat, player, room);
        return twitching;
    }

    private boolean input(Player player, Room room, float mx, float my, boolean twitching){
        boolean hovered = hitbox.isHovered(mx, my);
        boolean imageHovered = this.mouseOverWithPanning(mx, my);
        if (!catPlay){
            for (String catValue: Arrays.asList("cat", "catLeft", "catRight")) {
                SoundManager.play(catValue);
                SoundManager.setVolume(catValue, 0);
                SoundManager.setLoop(catValue, true);
            }
            catPlay = true;
        }
        switch (roomState){
            case 0:
                if (bedSide.getFrame() > 0){
                    float catVolume = Time.increaseTimeValue(SoundManager.getVolume("cat"), 0.15f, 0.25f);
                    SoundManager.setVolume("cat", catVolume);
                }
                if (!player.isFreeze() && bedSide.input(hitbox, room, mx, my)){
                    if (!bedSide.getSoundLock()){
                        SoundManager.play("catPulse");
                        SoundManager.setLoop("catPulse", true);
                        bedSide.setSoundLock();
                    }
                } else if (bedSide.getSoundLock()) {
                    SoundManager.stop("catPulse");
                    bedSide.setSoundLock();
                }
                break;
            case 1:
                if (attack.input(player, room, imageHovered, hovered, 1)) hitbox.setCoord(0, 0);
                if (room.getFrame() == 0 && room.getState() == 0) {
                    twitch.update(hovered);
                    if (twitch.isTwitching()) twitching = true;
                }
                break;
            case 2:
                bed.input(player, room, imageHovered);
                break;
            case 3:
                peek.input(player, room, hovered, 0.75f);
                if (room.getFrame() == 0 && room.getState() == 0) {
                    twitch.update(hovered);
                    if (twitch.isTwitching()) twitching = true;
                }
                break;
        }
        return twitching;
    }

    private void update(Rat rat, Player player, Room room){
        if (roomState != 0 && roomState != 4){
            float catVolume = Time.increaseTimeValue(SoundManager.getVolume("cat"), 0.65f, 0.5f);
            SoundManager.setVolume("cat", catVolume);
            float catPitch = Time.increaseTimeValue(SoundManager.getPitch("cat"), 2, 0.0075f);
            SoundManager.setPitch("cat", catPitch);
        }
        switch (roomState){
            case 0:
                side = (byte) bedSide.update(random, side);
                if (bedSide.getFrame() != 23 && bedSide.getFrame() != 42 && bedSide.getFrame() != 57) hitbox.setCoord(0, 0);
                if (bedSide.getFrame() > 0){
                    boolean change = true;
                    float bedPhase = bedSide.getPhase();
                    if (bedSide.getFrame() <= 23) bedSide.setPhase(1);
                    else if (bedSide.getFrame() <= 42) bedSide.setPhase(2);
                    else bedSide.setPhase(3);
                    if (bedPhase == bedSide.getPhase()) change = false;
                    if (change) changePath();
                    if (side == 0) SoundManager.setVolume("catLeft", (float) (bedSide.getFrame() - 23) / 75);
                    else SoundManager.setVolume("catRight", (float) (bedSide.getFrame() - 23) / 75);
                }
                if (bedSide.isSignal()) {
                    if (side == 0) {
                        if (bedSide.getFrame() <= 23) hitbox.setCoord(192, 548);
                        else if (bedSide.getFrame() <= 42) hitbox.setCoord(186, 720);
                        else hitbox.setCoord(187, 660);
                    } else {
                        if (bedSide.getFrame() <= 23) hitbox.setCoord(2910, 448);
                        else if (bedSide.getFrame() <= 42) hitbox.setCoord(2838, 547);
                        else hitbox.setCoord(2805, 619);
                    }
                }
                if (!bedSide.endState()) return;
                if (rat == null || (rat.getRoomState() != 2 && rat.getRoomState() != 3)) side = (byte) (random.nextInt(2) * 2);
                else if (rat.getSide() == 0) side = 2;
                else side = 0;
                transitionRoomState((byte) 2);
                SoundManager.stop("catPulse");
                bedSide.setSoundLock();
                break;
            case 1:
                if (roomUpdate(rat, player) || !attack.isMoved()) return;
                if (side == 0) {
                    if (attack.getPosition() == 0) hitbox.setCoord(366, 741);
                    else if (attack.getPosition() == 1) hitbox.setCoord(290, 632);
                    else hitbox.setCoord(466, 616);
                } else if (side == 1) {
                    if (attack.getPosition() == 0) hitbox.setCoord(1529, 724);
                    else if (attack.getPosition() == 1) hitbox.setCoord(1341, 583);
                    else hitbox.setCoord(1703, 605);
                } else {
                    if (attack.getPosition() == 0) hitbox.setCoord(2559, 890);
                    else if (attack.getPosition() == 1) hitbox.setCoord(2395, 714);
                    else hitbox.setCoord(2748, 669);
                }
                attack.setMoved();
                if (attack.getFlashTime() != 0) {
                    attack.setLimit(3);
                    break;
                }
                if (attack.getLimit() != 0) {
                    int chance = random.nextInt(10);
                    if (chance == 7) {
                        attack.setSkip();
                        attack.setLimit(0);
                        attack.setKillTimer(attack.getKillTimer() + 0.375f);
                        break;
                    } else if (chance == 3) {
                        attack.setLimit(attack.getLimit() - 1);
                        break;
                    }
                }
                attack.setLimit(3);
                attack.setFlashTime(0.25f + random.nextInt(4) * 0.125f);
                break;
            case 2:
                if (bed.killTime()) return;
                if (!bed.update(player, room)) return;
                transitionRoomState((byte) 3);
                SoundManager.play("peek");
                break;
            case 3:
                if (!peek.update()) return;
                hitbox.setCoord(0, 0);
                player.setScared();
                player.setFreeze();
                if (side == 0) {
                    if (rat == null || rat.getDoor().getFrame() == 13 || rat.getSide() == 0) side = (byte) (1 + random.nextInt(2));
                    else if (rat.getSide() == 1) side = 2;
                    else side = 1;
                } else {
                    if (rat == null || rat.getDoor().getFrame() == 13 || rat.getSide() == 2) side = (byte) (random.nextInt(2));
                    else if (rat.getSide() == 1) side = 0;
                    else side = 1;
                }
                transitionRoomState((byte) 1);
                break;
            case 4:
                if (!leave.update(player, side)) return;
                transitionRoomState((byte) 0);
                break;
        }
    }

    public void changePath(){
        hitbox.setSize(100, Challenges.hitboxMultiplier);
        if (roomState == 0) {
            if (side == 0) {
                setPath("game/Cat/Retreat/Left");
                append(String.valueOf(bedSide.getPhase()));
                setX(0);
                if (bedSide.getPhase() == 1){
                    setY(339);
                    setWidth(371);
                } else if (bedSide.getPhase() == 2){
                    setY(274);
                    setWidth(400);
                } else {
                    setY(233);
                    setWidth(378);
                }
            } else {
                setPath("game/Cat/Retreat/Right");
                append(String.valueOf(bedSide.getPhase()));
                setY(218);
                if (bedSide.getPhase() == 1){
                    setX(2761);
                    setWidth(274);
                } else if (bedSide.getPhase() == 2){
                    setX(2603);
                    setWidth(433);
                } else {
                    setX(2537);
                    setWidth(497);
                }
            }
        } else if (roomState == 1){
            if (side == 0) {
                hitbox.setSize(75, Challenges.hitboxMultiplier);
                setPath("game/Cat/Battle/Left");
                setX(171);
                setY(322);
                setWidth(406);
                setHeight(515);
            } else if (side == 1) {
                setPath("game/Cat/Battle/Middle");
                setX(1215);
                setY(78);
                setWidth(664);
                setHeight(791);
            } else {
                setPath("game/Cat/Battle/Right");
                setX(2229);
                setY(196);
                setWidth(711);
                setHeight(828);
            }
        } else if (roomState == 2){
            if (side == 0) {
                setPath("game/Cat/Bed/LeftUnder");
                setX(0);
                setY(170);
                setWidth(784);
                setHeight(509);
            } else {
                setPath("game/Cat/Bed/RightUnder");
                setX(1100);
                setY(141);
                setWidth(948);
                setHeight(538);
            }
        } else if (roomState == 3){
            if (side == 0) {
                hitbox.setCoord(652, 440);
                setPath("game/Cat/Bed/LeftPeek");
                setX(515);
                setY(231);
                setWidth(274);
                setHeight(345);
            } else {
                hitbox.setCoord(2349, 512);
                setPath("game/Cat/Bed/RightPeek");
                setX(2156);
                setY(194);
                setWidth(557);
                setHeight(525);
            }
        } else {
            hitbox.setCoord(0, 0);
            if (side == 0){
                setPath("game/Cat/Leaving/Left");
                setX(104);
                setY(323);
                setWidth(386);
                setHeight(459);
            } else if (side == 2){
                setPath("game/Cat/Leaving/Right");
                setX(2491);
                setY(259);
                setWidth(459);
                setHeight(589);
            } else {
                setPath("game/Cat/Leaving/Middle");
                setX(1414);
                setY(384);
                setWidth(226);
                setHeight(401);
            }
        }
    }

    private boolean roomUpdate(Rat rat, Player player){
        if (attack.update(random, (byte) 0, player.isScared(), 0.01f)) {
            transitionRoomState((byte) 4);
            player.setBlacknessSpeed(6);
            player.setBlacknessTimes(2);
            SoundManager.setVolume("cat", 0);
            SoundManager.setPitch("cat", 1);
            SoundManager.play("thunder");
            if (side != 1) SoundManager.play("leave");
            player.setScared();
            player.setAttack();
            return true;
        } else if (rat != null && rat.getDoor().getFrame() != 13){
//            attack.setKillTimer(Time.increaseTimeValue(attack.getKillTimer(), 2, 0.375f));
        }
        return false;
    }

    public void transitionRoomState(byte roomState){
        this.roomState = roomState;
        if (roomState == 0){
            bedSide.reset();
        } else if (roomState == 1){
            attack.reset(1.15f, 0.0375f, 7, (byte) 0, (byte) 0, 26);
            changePath();
        } else if (roomState == 2){
            bed.reset(15, 1.15f);
            changePath();
        } else if (roomState == 3){
            peek.reset(3, 2.25f, 0.75f);
            changePath();
        } else if (roomState == 4){
            leave.reset();
            changePath();
        }
    }

    public void jumpscare(){
        VideoManager.setRequest("game/Cat/Jumpscare/room");
    }

    private final String[] textures = new String[]{
            "Battle/Left",
            "Battle/Middle",
            "Battle/Right",
            "Bed/LeftUnder",
            "Bed/RightUnder",
            "Bed/LeftPeek",
            "Bed/RightPeek",
            "Leaving/Left",
            "Leaving/Middle",
            "Leaving/Right",
            "Tape/Tape",
            "Retreat/Left1",
            "Retreat/Left2",
            "Retreat/Left3",
            "Retreat/Right1",
            "Retreat/Right2",
            "Retreat/Right3"
    };

    public void load(){
        for (String file: textures) ImageManager.add("game/Cat/" + file);
    }

    public byte getRoomState() {
        return roomState;
    }

    public byte getSide() {
        return side;
    }

    public Twitch getTwitch() {
        return twitch;
    }

    public BedSide getBedSide() {
        return bedSide;
    }

    public Attack getAttack() {
        return attack;
    }

    public Peek getPeek() {
        return peek;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public float getAttackHealth(){
        return attack.getKillTimer() / 2;
    }

    public int getLeaveFrame(){
        return leave.getFrame();
    }
}
