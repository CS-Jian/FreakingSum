package com.mrandmrsjian.freakingsum.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mrandmrsjian.freakingsum.DirectedGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 480;
        config.height = 800;
		new LwjglApplication(new DirectedGame(null), config);
	}
}
