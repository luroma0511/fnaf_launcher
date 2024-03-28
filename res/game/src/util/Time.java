package util;

import com.badlogic.gdx.Gdx;

public class Time {
    static float delta;
    public static boolean lock;

    public static void update(){
        if (!lock) delta = Gdx.graphics.getDeltaTime();
        lock = false;
    }

    public static float increaseTimeValue(float value, float limit, float speed){
        if (value >= limit) return value;
        value += delta * speed;
        if (value > limit) value = limit;
        return value;
    }

    public static float decreaseTimeValue(float value, float limit, float speed) {
        if (value <= limit) return value;
        value -= delta * speed;
        if (value < limit) value = limit;
        return value;
    }

    public static float convertValue(float value){
        return value * delta;
    }
}
