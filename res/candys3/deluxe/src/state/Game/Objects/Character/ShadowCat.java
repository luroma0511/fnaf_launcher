package state.Game.Objects.Character;

import deluxe.GameData;
import state.Game.Functions.*;
import state.Game.Objects.Flashlight;
import state.Game.Objects.Player;
import state.Game.Objects.Room;
import util.*;

import java.util.Arrays;
import java.util.Random;

public class ShadowCat extends SpriteObject{
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

    public ShadowCat(byte ai){
        super();
        this.ai = ai;
        random = new Random();
        twitch = new Twitch();
        hitbox = new Hitbox(100);
        bedSide = new BedSide(15);
        attack = new Attack();
        bed = new Bed();
        peek = new Peek();
        leave = new Leave(0);
        catPlay = false;
        side = (byte) (random.nextInt(2) * 2);
        roomState = 2;
        bed.reset(7, 1.15f);
        changePath();
    }

    public boolean execute(Player player, ShadowRat rat, Room room, Flashlight flashlight, boolean twitching){
        twitching = input(player, room, flashlight.getX(), flashlight.getY(), twitching);
        update(rat, player, room);
        return twitching;
    }

    private boolean input(Player player, Room room, float mx, float my, boolean twitching){
        boolean hovered = hitbox.isHovered(mx, my);
        boolean imageHovered = this.mouseOverWithPanning(mx, my);
        switch (roomState){
            case 0:
                if (bedSide.getFrame() > 0){
                    float catVolume = Time.increaseTimeValue(SoundManager.getSoundEffect(SoundManager.VOLUME, "cat"), 0.15f, 0.25f);
                    SoundManager.setSoundEffect(SoundManager.VOLUME, "cat", catVolume);
                }
                if (!player.isFreeze()) {
                    bedSide.input(hovered && room.getFrame() == 0 && room.getState() == 0, false);
                    if (room.getFrame() == 0 && room.getState() == 0) {
                        twitch.update(hovered);
                        if (hovered && !player.isScared()) player.setScared();
                        else if (!twitching && !hovered && !player.isAttack() && player.isScared()) player.setScared();
                        if (twitch.isTwitching()) twitching = true;
                    }
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

    private void update(ShadowRat rat, Player player, Room room){
        if (!catPlay){
            for (String catValue: Arrays.asList("cat", "catLeft", "catRight")) {
                SoundManager.play(catValue);
                SoundManager.setSoundEffect(SoundManager.VOLUME, catValue, 0);
                SoundManager.setSoundEffect(SoundManager.LOOP, catValue, 1);
            }
            catPlay = true;
        }
        if (roomState != 0 && roomState != 4){
            float catVolume = Time.increaseTimeValue(SoundManager.getSoundEffect(SoundManager.VOLUME, "cat"), 0.65f, 0.5f);
            SoundManager.setSoundEffect(SoundManager.VOLUME, "cat", catVolume);
            float catPitch = Time.increaseTimeValue(SoundManager.getSoundEffect(SoundManager.PITCH, "cat"), 2, 0.0075f);
            SoundManager.setSoundEffect(SoundManager.PITCH, "cat", catPitch);
        }
        switch (roomState){
            case 0:
                side = (byte) bedSide.update(random, side, false);
                if (bedSide.getFrame() > 0){
                    System.out.println(hitbox.getX() + " | " + hitbox.getY());
                    boolean change = true;
                    float bedPhase = bedSide.getPhase();
                    if (bedSide.getFrame() < 8) bedSide.setPhase(1);
                    else if (bedSide.getFrame() < 20) bedSide.setPhase(2);
                    else if (bedSide.getFrame() < 32) bedSide.setPhase(3);
                    else bedSide.setPhase(4);
                    if (bedPhase == bedSide.getPhase()) change = false;
                    if (change) changePath();
                    if (side == 0) SoundManager.setSoundEffect(SoundManager.VOLUME, "catLeft", (float) (bedSide.getFrame() - 8) / 75);
                    else SoundManager.setSoundEffect(SoundManager.VOLUME, "catRight", (float) (bedSide.getFrame() - 8) / 75);
                }
                hitbox.setSize(100, GameData.hitboxMultiplier);
                int hitboxPosition = bedSide.getFrame() - 7;
                switch (hitboxPosition){
                    case 1:
                        if (side == 2) hitbox.setCoord(2857, 512);
                        break;
                    case 2:
                        if (side == 2) hitbox.setCoord(2857, 513);
                        break;
                    case 3:
                        if (side == 2) hitbox.setCoord(2856, 514);
                        break;
                    case 4:
                        if (side == 2) hitbox.setCoord(2855, 515);
                        break;
                    case 5:
                        if (side == 2) hitbox.setCoord(2854, 516);
                        break;
                    case 6:
                        if (side == 2) hitbox.setCoord(2852, 518);
                        break;
                    case 7:
                        if (side == 2) hitbox.setCoord(2850, 521);
                        break;
                    case 8:
                        if (side == 2) hitbox.setCoord(2847, 525);
                        break;
                    case 9:
                        if (side == 2) hitbox.setCoord(2844, 529);
                        break;
                    case 10:
                        if (side == 2) hitbox.setCoord(2841, 533);
                        break;
                    case 11:
                        if (side == 2) hitbox.setCoord(2838, 537);
                        break;
                    case 12:
                        if (side == 2) hitbox.setCoord(2834, 541);
                        break;
                    case 13:
                        if (side == 2) hitbox.setCoord(2830, 546);
                        break;
                    case 14:
                        if (side == 2) hitbox.setCoord(2826, 552);
                        break;
                    case 15:
                        if (side == 2) hitbox.setCoord(2822, 557);
                        break;
                    case 16:
                        if (side == 2) hitbox.setCoord(2818, 561);
                        break;
                    case 17:
                        if (side == 2) hitbox.setCoord(2815, 564);
                        break;
                    case 18:
                        if (side == 2) hitbox.setCoord(2811, 567);
                        break;
                    case 19:
                        if (side == 2) hitbox.setCoord(2808, 570);
                        break;
                    case 20:
                        if (side == 2) hitbox.setCoord(2804, 573);
                        break;
                    case 21:
                        if (side == 2) hitbox.setCoord(2802, 575);
                        break;
                    case 22:
                        if (side == 2) hitbox.setCoord(2801, 576);
                        break;
                    case 23:
                        if (side == 2) hitbox.setCoord(2798, 578);
                        break;
                    case 24:
                        if (side == 2) hitbox.setCoord(2796, 580);
                        break;
                    case 25:
                        if (side == 2) hitbox.setCoord(2794, 583);
                        break;
                    case 26:
                        if (side == 2) hitbox.setCoord(2791, 585);
                        break;
                    case 27:
                        if (side == 2) hitbox.setCoord(2789, 588);
                        break;
                    case 28:
                        if (side == 2) hitbox.setCoord(2786, 591);
                        break;
                    case 29:
                        if (side == 2) hitbox.setCoord(2783, 594);
                        break;
                    case 30:
                        if (side == 2) hitbox.setCoord(2780, 596);
                        break;
                    case 31:
                        if (side == 2) hitbox.setCoord(2778, 599);
                        break;
                    case 32:
                        if (side == 2) hitbox.setCoord(2775, 601);
                        break;
                    case 33:
                        if (side == 2) hitbox.setCoord(2772, 603);
                        break;
                    case 34:
                        if (side == 2) hitbox.setCoord(2769, 605);
                        break;
                    case 35:
                        if (side == 2) hitbox.setCoord(2766, 607);
                        break;
                    case 36:
                        if (side == 2) hitbox.setCoord(2763, 609);
                        break;
                    default:
                        hitbox.setCoord(0, 0);
                        break;
                }
                if (!bedSide.endState()) return;
                if (rat == null || rat.getRoomState() != 2) side = (byte) (random.nextInt(2) * 2);
                else if (rat.getSide() == 0) side = 2;
                else side = 0;
                transitionRoomState((byte) 2);
                SoundManager.stop("catPulse");
                bedSide.setSoundLock();
                break;
            case 1:
                if (attack.getKillTimer() == 0){
                    player.setBlacknessTimes(3);
                    player.setBlacknessSpeed(6);
                    player.setFreeze();
                    player.setJumpscare();
                    Jumpscare.set("game/Shadow Cat/Jumpscare", 3);
                    return;
                }
                if (roomUpdate(rat, player) || attack.notMoved()) return;
                if (side == 0) {
                    if (attack.getPosition() == 0) hitbox.setCoord(381, 743);
                    else if (attack.getPosition() == 1) hitbox.setCoord(305, 630);
                    else if (attack.getPosition() == 2) hitbox.setCoord(475, 620);
                    else hitbox.setCoord(377, 548);
                } else if (side == 1) {
                    if (attack.getPosition() == 0) hitbox.setCoord(1524, 709);
                    else if (attack.getPosition() == 1) hitbox.setCoord(1318, 577);
                    else if (attack.getPosition() == 2) hitbox.setCoord(1700, 602);
                    else hitbox.setCoord(1520, 521);
                } else {
                    if (attack.getPosition() == 0) hitbox.setCoord(2559, 863);
                    else if (attack.getPosition() == 1) hitbox.setCoord(2401, 675);
                    else if (attack.getPosition() == 2) hitbox.setCoord(2712, 651);
                    else hitbox.setCoord(2544, 553);
                }
                if (side == 0) hitbox.setSize(75, GameData.hitboxMultiplier);
                else hitbox.setSize(100, GameData.hitboxMultiplier);
                attack.setMoved();
                if (attack.getFlashTime() != 0) {
                    attack.setLimit(3);
                    break;
                }
                if (attack.getLimit() != 0) {
                    if (random.nextInt(10) == 3) break;
                    if (random.nextInt(5) == 2) {
                        attack.setFlashTime(0.1f);
                        attack.setAttackTimer(attack.getAttackTimer() - 0.1f);
                        break;
                    }
                }
                attack.setFlashTime(0.175f + random.nextInt(4) * 0.05f);
                break;
            case 2:
                if (bed.killTime()) {
                    player.setBlacknessTimes(3);
                    player.setBlacknessSpeed(6);
                    player.setFreeze();
                    player.setJumpscare();
                    Jumpscare.set("game/Shadow Cat/Jumpscare", 3);
                    return;
                }
                if (!bed.update(player, room)) return;
                if (room.getState() == 0 && ((player.getSide() == 0 && side == 2) || (player.getSide() == 2 && side == 0))) {
                    transitionRoomState((byte) 3);
                    SoundManager.play("peek");
                    break;
                }
                player.setBlacknessTimes(3);
                player.setBlacknessSpeed(6);
                player.setFreeze();
                player.setJumpscare();
                Jumpscare.set("game/Shadow Cat/Jumpscare", 3);
                break;
            case 3:
                if (!peek.update()) {
                    if (!peek.isKillTime()) return;
                    player.setBlacknessTimes(3);
                    player.setBlacknessSpeed(6);
                    player.setFreeze();
                    player.setJumpscare();
                    Jumpscare.set("game/Shadow Cat/Jumpscare", 3);
                    return;
                }
                hitbox.setCoord(0, 0);
                player.setScared();
                player.setFreeze();
                if (side == 0) side = (byte) (1 + random.nextInt(2));
                else side = (byte) (random.nextInt(2));
                transitionRoomState((byte) 1);
                break;
            case 4:
                if (!leave.update(player, side)) return;
                transitionRoomState((byte) 0);
                break;
        }
    }

    public void changePath(){
        hitbox.setSize(100, GameData.hitboxMultiplier);
        if (roomState == 0) {
            if (side == 0) {
                setPath("game/Shadow Cat/Retreat/Left");
                append(String.valueOf(bedSide.getPhase()));
                setX(0);
                if (bedSide.getFrame() < 8){
                    setY(218);
                    setWidth(390);
                } else if (bedSide.getPhase() == 2){
                    setY(274);
                    setWidth(400);
                } else {
                    setY(233);
                    setWidth(378);
                }
            } else {
                setPath("game/Shadow Cat/Retreat/Right/");
                setY(218);
                if (bedSide.getFrame() < 8){
                    append(String.valueOf(0));
                    setX(2648);
                    setWidth(390);
                } else {
                    setX(2483);
                    setWidth(555);
                }
            }
        } else if (roomState == 1){
            if (side == 0) {
                hitbox.setSize(75, GameData.hitboxMultiplier);
                setPath("game/Shadow Cat/Battle/Left");
                setX(174);
                setY(242);
                setWidth(464);
                setHeight(612);
            } else if (side == 1) {
                setPath("game/Shadow Cat/Battle/Middle");
                setX(1178);
                setY(52);
                setWidth(735);
                setHeight(861);
            } else {
                setPath("game/Shadow Cat/Battle/Right");
                setX(2134);
                setY(192);
                setWidth(870);
                setHeight(832);
            }
        } else if (roomState == 2){
            if (side == 0) {
                setPath("game/Shadow Cat/Bed/LeftUnder");
                setX(0);
                setY(214);
                setWidth(890);
                setHeight(446);
            } else {
                setPath("game/Shadow Cat/Bed/RightUnder");
                setX(1408);
                setY(204);
                setWidth(640);
                setHeight(420);
            }
        } else if (roomState == 3){
            if (side == 0) {
                hitbox.setCoord(648, 439);
                setPath("game/Shadow Cat/Bed/LeftPeek");
                setX(524);
                setY(222);
                setWidth(296);
                setHeight(378);
            } else {
                hitbox.setCoord(2347, 509);
                setPath("game/Shadow Cat/Bed/RightPeek");
                setX(2147);
                setY(198);
                setWidth(608);
                setHeight(521);
            }
        } else {
            hitbox.setCoord(0, 0);
            if (side == 0){
                setPath("game/Shadow Cat/Leaving/Left");
                setX(109);
                setY(337);
                setWidth(378);
                setHeight(445);
            } else if (side == 2){
                setPath("game/Shadow Cat/Leaving/Right");
                setX(2494);
                setY(272);
                setWidth(492);
                setHeight(582);
            } else {
                setPath("game/Shadow Cat/Leaving/Middle");
                setX(1414);
                setY(384);
                setWidth(226);
                setHeight(401);
            }
        }
    }

    private boolean roomUpdate(ShadowRat rat, Player player){
        if (attack.update(random, (byte) 1, player.isScared(), 0.01f)) {
            player.setBlacknessSpeed(6);
            if (attack.getTeleports() > 0) {
                attack.decreaseTeleports();
                attack.setFlashTime(0.8f);
                attack.setLimit(4);
                attack.setPosition((byte) 0);
                attack.setKillTimer(2);
                attack.setAttackTimer(4);
                attack.setMoved();
                player.setBlacknessTimes(1);
                if (side == 0) {
                    side = 1;
                    SoundManager.play("dodgeRight");
                } else if (side == 2) {
                    side = 1;
                    SoundManager.play("dodgeLeft");
                } else {
                    if (rat == null || rat.getRoomState() != 0 || rat.getDoor().getFrame() == 13 || rat.getSide() == 1) side = (byte) (random.nextInt(2) * 2);
                    else if (rat.getSide() == 0) side = 0;
                    else side = 2;
                    if (side == 0) SoundManager.play("dodgeLeft");
                    else SoundManager.play("dodgeRight");
                }
                changePath();
            } else {
                transitionRoomState((byte) 4);
                player.setBlacknessSpeed(6);
                player.setBlacknessTimes(2);
                SoundManager.setSoundEffect(SoundManager.VOLUME, "cat", 0);
                SoundManager.setSoundEffect(SoundManager.PITCH, "cat", 1);
                SoundManager.play("thunder");
                if (side != 1) SoundManager.play("leave");
                player.setScared();
                player.setAttack();
            }
            return true;
        } else if (rat != null && rat.getDoor().getFrame() != 13){
            attack.setKillTimer(Time.increaseTimeValue(attack.getKillTimer(), 2, 0.25f));
        }
        return false;
    }

    public void transitionRoomState(byte roomState){
        this.roomState = roomState;
        if (roomState == 0){
            bedSide.reset();
        } else if (roomState == 1){
            attack.reset(0.8f, 0.025f, 2f, (byte) 1, (byte) 0, 30);
            changePath();
        } else if (roomState == 2){
            bed.reset(12, 1.15f);
            changePath();
        } else if (roomState == 3){
            peek.reset(3, 2.5f, 0.75f);
            changePath();
        } else if (roomState == 4){
            leave.reset();
            changePath();
        }
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
