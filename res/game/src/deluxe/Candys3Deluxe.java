package deluxe;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import deluxe.state.StateManager;
import util.CameraManager;
import util.FontManager;
import util.ImageManager;
import util.InputManager;
import util.PathConstant;
import util.RenderManager;
import util.SoundManager;
import util.Time;
import util.VideoManager;

public class Candys3Deluxe extends ApplicationAdapter {
	public static InputManager inputManager;
	public static RenderManager renderManager;
	public static StateManager stateManager;

	public static BitmapFont candysFont;
	public static BitmapFont aiFont;
	public static BitmapFont debugFont;
	public static BitmapFont captionFont;

	private static boolean fullscreen;
	private static Graphics.DisplayMode displayMode;
	public static int width;
	public static int height;

	public Candys3Deluxe(int width, int height){
		Candys3Deluxe.width = width;
		Candys3Deluxe.height = height;
	}

	@Override
	public void create(){
		PathConstant.fixPath();
		inputManager = new InputManager();
		stateManager = new StateManager();
		CameraManager.createCamera(width, height);
		renderManager = new RenderManager();
		Gdx.input.setInputProcessor(inputManager);
		displayMode = Gdx.graphics.getDisplayMode();
		SoundManager.add("menu");
		SoundManager.add("thunder");

		FontManager.generateFont("candysFont");
		candysFont = FontManager.initializeFont(40);
		aiFont = FontManager.initializeFont(80);
		FontManager.dispose();
		FontManager.generateFont("captionFont");
		captionFont = FontManager.initializeFont(15);
		debugFont = FontManager.initializeFont(20);
		FontManager.dispose();
	}

	@Override
	public void resize(int width, int height){
		if (CameraManager.getViewport() == null) return;
		CameraManager.getViewport().update(width, height);
		CameraManager.getViewport().apply();
	}

	@Override
	public void render() {
		Time.update();
		inputManager.update();
		fullscreen = inputManager.fullscreen(displayMode, fullscreen, width, height);
		stateManager.update();
		renderManager.requests();
		renderManager.viewportAdjust();
		stateManager.render();
		inputManager.reset();
	}
	
	@Override
	public void dispose () {
		candysFont.dispose();
		aiFont.dispose();
		debugFont.dispose();
		captionFont.dispose();
		ImageManager.dispose();
		VideoManager.dispose();
		SoundManager.dispose();
		stateManager.dispose();
		renderManager.dispose();
	}
}
