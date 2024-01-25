package game.deluxe.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

import java.io.FileNotFoundException;

public class VideoManager {
    private VideoPlayer videoPlayer;
    private boolean show;

    public VideoManager(){
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        videoPlayer.setOnCompletionListener(new VideoPlayer.CompletionListener() {
            @Override
            public void onCompletionListener(FileHandle file) {
                show = false;
                videoPlayer.dispose();
            }
        });
    }

    public void play(String path){
        try {
            videoPlayer.play(Gdx.files.internal(path));
        } catch (FileNotFoundException e){
            Gdx.app.error("gdx-video", "Something is wrong!");
        }
        show = true;
    }

    public void updateRender(SpriteBatch batch){
        if (!show) return;
        videoPlayer.update();
        Texture video = videoPlayer.getTexture();
        if (video != null){
            batch.draw(video, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }
}
