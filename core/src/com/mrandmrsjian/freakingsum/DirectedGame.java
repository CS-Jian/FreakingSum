/*******************************************************************************
 * Copyright 2013 Andreas Oehlke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package com.mrandmrsjian.freakingsum;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mrandmrsjian.freakingsum.screens.AbstractGameScreen;
import com.mrandmrsjian.freakingsum.screens.MenuScreen;
import com.mrandmrsjian.freakingsum.screens.transitions.ScreenTransition;
import com.mrandmrsjian.freakingsum.screens.transitions.ScreenTransitionSlice;

public class DirectedGame implements ApplicationListener {
	private static final String TAG = DirectedGame.class.getName();
	private boolean init;
	private AbstractGameScreen currScreen;
	private AbstractGameScreen nextScreen;
	private FrameBuffer currFbo;
	private FrameBuffer nextFbo;
	private SpriteBatch batch;
	private float t;
	private ScreenTransition screenTransition;

	public OrthographicCamera camera;
	public Viewport viewport;
	public int screenWidth;
	public int screenHeight;
	public boolean soundEnabled;
	public float soundVolume;
	private Preferences preferences;
	public IActivityRequestHandler handler;
	public boolean isAndroid = false;
	public int recordPosition;
	public LeaderBoard leaderBoard;

	public DirectedGame (IActivityRequestHandler IARH) {
		this.handler = IARH;
	}

	@Override
	public void create () {
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// Load assets
		Assets.instance.init(new AssetManager());

		screenWidth = 480;
		screenHeight = 480 *  Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		Gdx.app.debug(TAG, "w=" + screenWidth + ", h=" + screenHeight);
		camera = new OrthographicCamera();
		camera.position.set(screenWidth / 2, screenHeight / 2, 0);
		viewport = new FitViewport(screenWidth, screenHeight, camera);
		soundEnabled=loadSoundStatus();
		soundVolume=loadSoundVolume();

		prepareLeaderBoard();

		switch (Gdx.app.getType()) {
			case Android:
				isAndroid=true;
				Gdx.app.setLogLevel(Application.LOG_ERROR);
				break;
			case Desktop:
				Gdx.app.setLogLevel(Application.LOG_DEBUG);
				// desktop specific code
				break;
			case WebGL:
				// HTML5 specific code
				break;
			case iOS:
				break;
			default:
				// Other platforms specific code
		}


		// Start game at menu screen
		ScreenTransition transition = ScreenTransitionSlice.init(2, ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);
		setScreen(new MenuScreen(this), transition);
	}

	protected Preferences getPrefs() {
		if(preferences==null){
			preferences = Gdx.app.getPreferences("FreakingSum");
		}
		if(preferences==null){
			Gdx.app.log("info","null preferences");
		}
		return preferences;

	}

	public void saveSoundStatus(){
		getPrefs().putBoolean( "soundstatus", soundEnabled );
	}
	public boolean loadSoundStatus(){
		return getPrefs().getBoolean("soundstatus",true);
	}
	public void saveSoundVolume(){
		getPrefs().putFloat( "soundvolume", soundVolume );
	}
	public float loadSoundVolume( ){
		return getPrefs().getFloat("soundvolume",1.0f);
	}
	public void flushPref(){
		getPrefs().flush();
	}
	public void saveAll(){
		Gdx.app.log("info","saving preferences");
		saveSoundVolume();
		saveSoundStatus();
		flushPref();
	}

	public void prepareLeaderBoard() {
		leaderBoard = new LeaderBoard(5);
		leaderBoard.loadFromString(getPrefs().getString("leaderBoard"));
	}

	public void saveLeaderBoard() {
		getPrefs().putString("leaderBoard", leaderBoard.toString());
		flushPref();
	}

	public void setScreen (AbstractGameScreen screen) {
		setScreen(screen, null);
	}

	public void setScreen (AbstractGameScreen screen, ScreenTransition screenTransition) {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		if (!init) {
			currFbo = new FrameBuffer(Format.RGB888, w, h, false);
			nextFbo = new FrameBuffer(Format.RGB888, w, h, false);
			batch = new SpriteBatch();
			init = true;
		}
		// start new transition
		nextScreen = screen;
		nextScreen.show(); // activate next screen
		nextScreen.resize(w, h);
		nextScreen.render(0); // let next screen update() once
		if (currScreen != null) currScreen.pause();
		nextScreen.pause();
		Gdx.input.setInputProcessor(null); // disable input
		this.screenTransition = screenTransition;
		t = 0;
	}

	@Override
	public void render () {
        // get delta time and ensure an upper limit of one 60th second
		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);
		if (nextScreen == null) {
			// no ongoing transition
			if (currScreen != null) currScreen.render(deltaTime);
		} else {
			// ongoing transition
			float duration = 0;
			if (screenTransition != null) duration = screenTransition.getDuration();
			t = Math.min(t + deltaTime, duration);
			if (screenTransition == null || t >= duration) {
				// no transition effect set or transition has just finished
				if (currScreen != null) currScreen.hide();
				nextScreen.resume();
				// enable input for next screen
				Gdx.input.setInputProcessor(nextScreen.getInputProcessor());
				// switch screens
				currScreen = nextScreen;
				nextScreen = null;
				screenTransition = null;
			} else {
				// render screens to FBOs
				currFbo.begin();
				if (currScreen != null) currScreen.render(deltaTime);
				currFbo.end();
				nextFbo.begin();
				nextScreen.render(deltaTime);
				nextFbo.end();
				// render transition effect to screen
				float alpha = t / duration;
				screenTransition.render(batch, currFbo.getColorBufferTexture(), nextFbo.getColorBufferTexture(), alpha);
			}
		}
	}

	@Override
	public void resize (int width, int height) {
		if (currScreen != null) currScreen.resize(width, height);
		if (nextScreen != null) nextScreen.resize(width, height);
	}

	@Override
	public void pause () {
		if (currScreen != null) currScreen.pause();
	}

	@Override
	public void resume () {
		if (currScreen != null) currScreen.resume();
	}

	@Override
	public void dispose () {
		if (currScreen != null) currScreen.hide();
		if (nextScreen != null) nextScreen.hide();
		if (init) {
			currFbo.dispose();
			currScreen = null;
			nextFbo.dispose();
			nextScreen = null;
			batch.dispose();
			init = false;
		}
	}

}
