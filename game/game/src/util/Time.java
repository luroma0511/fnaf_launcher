package util;

import com.badlogic.gdx.Gdx;

public class Time {
    static float delta;
    public static boolean lock;
    static float speed = 1;

    public static void update(){
        if (!lock) delta = Gdx.graphics.getDeltaTime() * speed;
        lock = false;
    }

    public static float increaseTimeValue(float value, float limit, float fps){
        if (value >= limit) return value;
        value += delta * fps;
        if (value > limit) value = limit;
        return value;
    }

    public static float decreaseTimeValue(float value, float limit, float fps) {
        if (value <= limit) return value;
        value -= delta * fps;
        if (value < limit) value = limit;
        return value;
    }

    public static float convertValue(float value){
        return value * delta;
    }

    public static void changeSpeed(boolean increase){
        speed = increase ? speed + 0.05f : speed - 0.05f;
    }

    public static float getDelta() {
        return delta;
    }
}
