package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

import java.io.FileNotFoundException;

public class VideoManager {
    private static VideoPlayer videoPlayer;
    private static String request;

    public static void setRequest(String path){
        request = path;
    }

    private static void play(String path){
        try {
            if (videoPlayer == null) videoPlayer = VideoPlayerCreator.createVideoPlayer();
            videoPlayer.play(Gdx.files.absolute(PathConstant.getAssetsPath() + path + ".webm"));
            videoPlayer.setOnCompletionListener(file -> dispose());
        } catch (FileNotFoundException e){
            Gdx.app.error("gdx-video", "Something is wrong!");
        }
    }

    public static void updateRender(SpriteBatch batch, int width, int height){
        if (request != null){
            play(request);
            request = null;
        }
        if (videoPlayer == null || !videoPlayer.isPlaying()) return;
        videoPlayer.update();
        Texture video = videoPlayer.getTexture();
        if (video != null){
            batch.draw(video, 0, 0, width, height);
        }
    }

    public static void dispose(){
        if (videoPlayer != null) videoPlayer.dispose();
    }
}
