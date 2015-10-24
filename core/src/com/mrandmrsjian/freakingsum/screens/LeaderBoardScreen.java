package com.mrandmrsjian.freakingsum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.mrandmrsjian.freakingsum.Assets;
import com.mrandmrsjian.freakingsum.DirectedGame;
import com.mrandmrsjian.freakingsum.util.DrawTextUtils;

/**
 * Created by C.S. Jian on 15/2/3.
 */
public class LeaderBoardScreen extends AbstractGameScreen {
    static final String TAG = LeaderBoardScreen.class.getName();
    SpriteBatch batch;
    TextureRegion line;
    TextureRegion dot;
    Vector3 touchPosition=new Vector3();
    boolean isGameOver;

    float lineStart;

    public LeaderBoardScreen(DirectedGame game, boolean isGameOver) {
        super(game);
        if(game.isAndroid){
            game.handler.setTrackerScreenName("com.mrandmrsjian.freakingsum.screens.LeaderBoardScreen");
            game.handler.showAds(true);
        }

        Gdx.app.log(TAG, "in constructor");
        line = Assets.instance.atlas.findRegion("line");
        dot = Assets.instance.atlas.findRegion("dot");
        lineStart = (game.screenWidth-line.getRegionWidth())/2;
        this.isGameOver = isGameOver;
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.camera.update();
        batch.setProjectionMatrix(game.camera.combined);

        batch.begin();
        batch.draw(line, lineStart, game.screenHeight - 110);
        Color darkOrange = new Color(255/255f, 125/255f, 65/255f,1);

        if (!isGameOver) {
            DrawTextUtils.DrawAlignCenter(Assets.instance.fonts.defaultSmall, batch, "Top 5", Color.BLACK, game.screenWidth / 2, game.screenHeight - 50);
        } else {
            if (game.recordPosition != -1) {
                DrawTextUtils.DrawAlignCenter(Assets.instance.fonts.defaultSmall, batch, "New Record", Color.BLACK, game.screenWidth / 2, game.screenHeight - 50);
            } else {
                DrawTextUtils.DrawAlignCenter(Assets.instance.fonts.defaultSmall, batch, "Top 5", Color.BLACK, game.screenWidth / 2, game.screenHeight - 50);
            }
        }
        for (int i=1;i<=5;i++) {
            if (game.leaderBoard.scores.get(i-1).score > 0) {
                if (i == game.recordPosition + 1) {
                    DrawTextUtils.DrawAlignLeft(Assets.instance.fonts.defaultTiny, batch,
                            "New Record",
                            darkOrange, 60, game.screenHeight - 180 - (i - 1) * 72);
                    DrawTextUtils.DrawAlignRight(Assets.instance.fonts.defaultTiny, batch,
                            game.leaderBoard.scores.get(i - 1).score + "",
                            darkOrange, 420, game.screenHeight - 180 - (i - 1) * 72);
                } else {
                    DrawTextUtils.DrawAlignLeft(Assets.instance.fonts.defaultTiny, batch,
                            game.leaderBoard.scores.get(i - 1).dateTime + "",
                            Color.BLACK, 60, game.screenHeight - 180 - (i - 1) * 72);
                    DrawTextUtils.DrawAlignRight(Assets.instance.fonts.defaultTiny, batch,
                            game.leaderBoard.scores.get(i - 1).score + "",
                            Color.BLACK, 420, game.screenHeight - 180 - (i - 1) * 72);
                }
            }
        }
        batch.draw(line, lineStart, 110);
        batch.end();

        update();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public InputProcessor getInputProcessor() {
        return null;
    }

    private void update() {
        if (Gdx.input.justTouched()) {
            game.camera.unproject(touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        }
    }

    @Override
    public void dispose () {

    }

}
