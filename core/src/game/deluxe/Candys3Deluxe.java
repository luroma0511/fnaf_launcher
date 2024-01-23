package game.deluxe;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.deluxe.state.Engine;

public class Candys3Deluxe extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	ScheduledExecutorService engineTimer;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		//thread code
		final Engine engine = new Engine();
		engineTimer = Executors.newSingleThreadScheduledExecutor();
		engineTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				engine.updateDeltaTime();
				engine.update();
			}
		}, 0, 16, TimeUnit.MILLISECONDS);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		engineTimer.shutdown();
	}
}
