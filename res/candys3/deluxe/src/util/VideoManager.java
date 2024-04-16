package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

import java.io.FileNotFoundException;

public class VideoManager {
    private static final VideoPlayer videoPlayer = VideoPlayerCreator.createVideoPlayer();
    private static String request;

    public static void setRequest(String path){
        request = path;
    }

    public static void updateRender(SpriteBatch batch, int width, int height){
        if (request != null){
            try {
                videoPlayer.play(Gdx.files.local("assets/" + request + ".webm"));
                videoPlayer.setOnCompletionListener(file -> dispose());
            } catch (FileNotFoundException e){
                Gdx.app.error("gdx-video", "Something is wrong!");
            }
            request = null;
        }
        if (videoPlayer == null || !videoPlayer.isPlaying()) return;
        videoPlayer.update();
        Texture video = videoPlayer.getTexture();
        if (video != null) batch.draw(video, 0, 0, width, height);
    }

    public static void dispose(){
        videoPlayer.dispose();
    }
}