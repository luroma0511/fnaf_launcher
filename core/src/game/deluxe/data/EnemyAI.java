package game.deluxe.data;

public class EnemyAI {
    private boolean shadowNight_Rat;
    private boolean shadowNight_Cat;
    private float shadowNight_RatAlpha;
    private float shadowNight_CatAlpha;

    public void update(float deltaTime){
        shadowNight_RatAlpha = updateCharacterAlpha(shadowNight_Rat, shadowNight_RatAlpha, deltaTime);
        shadowNight_CatAlpha = updateCharacterAlpha(shadowNight_Cat, shadowNight_CatAlpha, deltaTime);
    }

    private float updateCharacterAlpha(boolean condition, float alpha, float deltaTime){
        float speedAlpha = 2;
        if (condition && alpha < 0.75f){
            alpha += deltaTime * speedAlpha;
            if (alpha > (float) 0.5) alpha = 0.75f;
        } else if (alpha > 0){
            alpha -= deltaTime * speedAlpha;
            if (alpha < 0) alpha = 0;
        }
        return alpha;
    }

    public boolean isShadowNight_Rat() {
        return shadowNight_Rat;
    }

    public void changeShadowNight_Rat() {
        shadowNight_Rat = !shadowNight_Rat;
    }

    public boolean isShadowNight_Cat() {
        return shadowNight_Cat;
    }

    public void changeShadowNight_Cat() {
        shadowNight_Cat = !shadowNight_Cat;
    }

    public float getShadowNight_RatAlpha() {
        return shadowNight_RatAlpha;
    }

    public float getShadowNight_CatAlpha() {
        return shadowNight_CatAlpha;
    }
}
