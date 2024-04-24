package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

import java.io.FileNotFoundException;

public class VideoManager {
    private static final VideoPlayer videoPlayer = VideoPlayerCreator.createVideoPlayer();
    private static String request;
    private static boolean playing;

    public static void setRequest(String path){
        request = path;
    }

    public static void reset(){
        if (videoPlayer.isPlaying()) dispose();
    }

    public static boolean render(SpriteBatch batch, boolean flip, int width, int height){
        if (request != null){
            try {
                videoPlayer.play(Gdx.files.local("assets/" + request + ".webm"));
                playing = true;
                videoPlayer.setOnCompletionListener(file -> dispose());
            } catch (FileNotFoundException e){
                Gdx.app.error("gdx-video", "Something is wrong!");
            }
            request = null;
        }
        if (!videoPlayer.isPlaying()) {
            playing = false;
            return false;
        }
        videoPlayer.update();
        if (videoPlayer.getTexture() == null) return true;
        TextureRegion video = new TextureRegion(videoPlayer.getTexture());
        if (flip) video.flip(true, false);
        batch.draw(video, CameraManager.getX(), CameraManager.getY(), width, height);
        return true;
    }

    public static void dispose(){
        videoPlayer.dispose();
    }
}