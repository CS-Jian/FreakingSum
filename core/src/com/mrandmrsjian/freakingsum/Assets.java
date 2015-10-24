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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {

	public static final String TAG = Assets.class.getName();

	public static final Assets instance = new Assets();

	private AssetManager assetManager;

	public AssetFonts fonts;
    public TextureAtlas atlas;


	// singleton: prevent instantiation from other classes
	private Assets() {
	}

	public class AssetFonts {
		public final BitmapFont defaultTiny;
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;

		public AssetFonts() {
			// create three fonts using Libgdx's built-in 15px bitmap font
			defaultTiny = new BitmapFont(Gdx.files.internal("fontTiny.fnt"), false);
			defaultSmall = new BitmapFont(Gdx.files.internal("fontSmall.fnt"), false);
			defaultNormal = new BitmapFont(Gdx.files.internal("fontMedium.fnt"), false);
			defaultBig = new BitmapFont(Gdx.files.internal("fontBig.fnt"), false);
			// enable linear texture filtering for smooth fonts
			defaultTiny.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	public void init(AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load("images.pack", TextureAtlas.class);
		// load sounds

		// load music

		// start loading assets and wait until finished
		assetManager.finishLoading();

		Gdx.app.debug(TAG,
				"# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + a);
		}

		fonts = new AssetFonts();
        atlas = assetManager.get("images.pack");

        // enable texture filtering for pixel smoothing
        for (Texture t : atlas.getTextures()) {
            t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		fonts.defaultTiny.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'",
				(Exception) throwable);

	}

}
