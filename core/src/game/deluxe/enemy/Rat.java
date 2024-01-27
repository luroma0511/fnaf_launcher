package game.deluxe.enemy;

import game.engine.util.*;

public class Rat {

    public Rat(){

    }

    public void jumpscare(VideoManager videoManager){
        videoManager.play("game/Rat/Jumpscare/room.webm");
    }

    public void load(SpriteManager spriteManager){
        spriteManager.create("game/Rat/Battle/Left", (short) 464);
        spriteManager.create("game/Rat/Battle/Middle", (short) 736);
        spriteManager.create("game/Rat/Battle/Right", (short) 826);
        spriteManager.create("game/Rat/Bed/LeftPeek", (short) 425);
        spriteManager.create("game/Rat/Bed/RightPeek", (short) 589);
        spriteManager.create("game/Rat/Leaving/Left", (short) 210);
        spriteManager.create("game/Rat/Leaving/Right", (short) 311);
        spriteManager.create("game/Rat/Tape/Tape", (short) 750);
        spriteManager.create("game/Rat/Looking Away/Left", (short) 130);
        spriteManager.create("game/Rat/Looking Away/Middle", (short) 232);
        spriteManager.create("game/Rat/Looking Away/Right", (short) 105);
    }
}
