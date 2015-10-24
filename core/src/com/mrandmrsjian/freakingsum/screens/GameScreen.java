package com.mrandmrsjian.freakingsum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.mrandmrsjian.freakingsum.Assets;
import com.mrandmrsjian.freakingsum.DirectedGame;
import com.mrandmrsjian.freakingsum.util.DrawTextUtils;

import com.badlogic.gdx.graphics.Color;
import com.mrandmrsjian.freakingsum.util.Rumble;

import java.util.Random;

/**
 * Created by C.S. Jian on 15/2/3.
 */
public class GameScreen extends AbstractGameScreen {
    static final String TAG = GameScreen.class.getName();
    SpriteBatch batch;

    TextureRegion line;
    TextureRegion dot;

    GameState gameState;
    final Random rand;
    Vector3 touchPosition=new Vector3();
    Rectangle[] blockBounds;
    Vector2 scorePosition;
    Vector2 crossSignPosition;
    Vector2 answerPosition;

    TextureRegion[] colorPads;
    TextureRegion[] colorRings;
    int[] number = new int[4];
    boolean[] selected = {false, false, false, false};
    int sum;
    int answer;
    int selectCount;
    long caseStartTime;
    long caseTotalTime = 5*1000;
    long elapseTime;
    int score;
    float lineStart;

    Rumble rumble;
    Vector3 cameraPosition;

    public GameScreen(DirectedGame game) {
        super(game);
        if(game.isAndroid){
            game.handler.setTrackerScreenName("com.mrandmrsjian.freakingsum.screens.GameScreen");
            game.handler.showAds(true);
        }

        Gdx.app.log(TAG, "in constructor");
        line = Assets.instance.atlas.findRegion("line");
        dot = Assets.instance.atlas.findRegion("dot");
        lineStart = (game.screenWidth-line.getRegionWidth())/2;

        colorPads = new TextureRegion[4];
        colorPads[0] = Assets.instance.atlas.findRegion("bluePad");
        colorPads[1] = Assets.instance.atlas.findRegion("greenPad");
        colorPads[2] = Assets.instance.atlas.findRegion("redPad");
        colorPads[3] = Assets.instance.atlas.findRegion("orangePad");
        colorRings = new TextureRegion[4];
        colorRings[0] = Assets.instance.atlas.findRegion("blueRing");
        colorRings[1] = Assets.instance.atlas.findRegion("greenRing");
        colorRings[2] = Assets.instance.atlas.findRegion("redRing");
        colorRings[3] = Assets.instance.atlas.findRegion("orangeRing");

        blockBounds = new Rectangle[4];
        blockBounds[0] = new Rectangle((game.screenWidth-400)/2, game.screenHeight - 400, 120, 120);
        blockBounds[1] = new Rectangle((game.screenWidth-400)/2 + (120 + 40), game.screenHeight - 400, 120, 120);
        blockBounds[2] = new Rectangle((game.screenWidth-400)/2, game.screenHeight - 560, 120, 120);
        blockBounds[3] = new Rectangle((game.screenWidth-400)/2 + (120 + 40), game.screenHeight - 560, 120, 120);

        scorePosition = new Vector2(game.screenWidth/2, game.screenHeight - 63);
        crossSignPosition = new Vector2(blockBounds[0].x + (120 + 20), blockBounds[0].y);
        answerPosition = new Vector2(blockBounds[1].x + (120 + 0), blockBounds[0].y);

        rand = new Random();
        gameState = GameState.Init;
        score = 0;

        // 一開始就初始化一個題目
        renderCase();
        batch = new SpriteBatch();

    }

    private void printSelected() {
        Gdx.app.log(TAG, "selected 0=" + selected[0] + ", 1=" + selected[1] + ", 2=" + selected[2] + ", 3="+ selected[3]);
    }

    private void renderCase() {
        printSelected();
        for (int i=0; i<4; i++) {
            number[i] = rand.nextInt(9) + 1;
            selected[i] = false;
        }
        int num1 = rand.nextInt(4);
        int num2 = (num1 + rand.nextInt(3)+1)%4;
        answer = number[num1] + number[num2];
        caseStartTime = TimeUtils.millis();
    }

    private void drawScreen() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.camera.update();
        batch.setProjectionMatrix(game.camera.combined);

        switch (gameState) {
            case Init:
            case Action:
            case CaseSolved:
            case CaseFail:
                drawRunning();
                break;
        }
    }

    private void drawRunning() {
        batch.begin();
        Color gray = new Color(102/255f, 102/255f, 102/255f,1);
        Color darkGreen = new Color(64/255f, 141/255f, 52/255f,1);

        if (gameState != GameState.GameOver) {
            // draw elapse time
            batch.draw(dot, 0, 0,
                    game.screenWidth - (game.screenWidth * (caseTotalTime - elapseTime)) / caseTotalTime, game.screenHeight);

            batch.draw(line, lineStart, game.screenHeight - 110);

            DrawTextUtils.DrawAlignLeft(Assets.instance.fonts.defaultSmall, batch, "Score", gray, scorePosition.x, scorePosition.y);
            DrawTextUtils.DrawAlignLeft(Assets.instance.fonts.defaultNormal, batch, score + "" , Color.BLACK, scorePosition.x + 150, scorePosition.y + 12);
            DrawTextUtils.DrawAlignCenter(Assets.instance.fonts.defaultBig, batch, "+", darkGreen, crossSignPosition.x, crossSignPosition.y);
            DrawTextUtils.DrawAlignLeft(Assets.instance.fonts.defaultBig, batch, "= " + answer, darkGreen, answerPosition.x, answerPosition.y);


            for (int i = 0; i < 4; i++) {
                batch.draw(selected[i]?colorRings[i]:colorPads[i], blockBounds[i].x, blockBounds[i].y);
                DrawTextUtils.DrawAlignCenter(Assets.instance.fonts.defaultSmall, batch, number[i] + "",
                        selected[i] ? Color.BLACK : Color.WHITE, blockBounds[i].x + 60, blockBounds[i].y + 75);
            }
        }
        batch.end();
    }


    private void update(float delta) {
        Gdx.app.log(TAG, "update");
        switch (gameState) {
            case Init:
                updateInit();
                break;
            case Action:
                updateAction(delta);
                break;
            case CaseSolved:
                updateCaseSolved(delta);
                break;
            case CaseFail:
                updateCaseFail(delta);
                break;
            case GameOver:
                updateGameOver();
                game.setScreen(new LeaderBoardScreen(game, true));
                break;
        }
    }

    private void updateInit () {
        if (Gdx.input.justTouched()) {
            game.camera.unproject(touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            for (int i=0; i<4; i++) {
                if (blockBounds[i].contains(touchPosition.x, touchPosition.y)) {
                    gameState = GameState.Action;
                    selected[i] = true;
                }
            }
            Gdx.app.log(TAG, "updateInit");
            printSelected();
        }
        caseStartTime = TimeUtils.millis();
    }

    private void updateAction (float delta) {
        elapseTime = TimeUtils.timeSinceMillis(caseStartTime);
        if (elapseTime >= caseTotalTime) {
            Gdx.app.log(TAG, "elapseTime >= caseTotalTime");
            gameState = GameState.CaseFail;
            return;
        }
        selectCount = 0;
        sum = 0;
        if (Gdx.input.justTouched()) {
            game.camera.unproject(touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            for (int i=0; i<4; i++) {
                if (blockBounds[i].contains(touchPosition.x, touchPosition.y)) {
                    selected[i] = !selected[i];
                }

                if (selected[i]) {
                    selectCount++;
                    sum += number[i];
                }
            }
            if (selectCount == 2) {
                if (sum == answer) {
                    gameState = GameState.CaseSolved;
                } else {
                    gameState = GameState.CaseFail;
                }
            }
        }

        if (gameState == GameState.CaseFail) {
            this.rumble = new Rumble(5f, 0.5f);
            cameraPosition = game.camera.position;
        }
    }

    private void updateCaseSolved(float delta) {
        gameState = GameState.Action;
        score += 1;
        Gdx.app.log(TAG, "current score=" + score);
        printSelected();
        renderCase();
    }

    private void updateCaseFail(float delta) {
        if (rumble.remainRumbleTime > 0){
            rumble.tick(delta, game.camera , cameraPosition);
        } else {
            gameState = GameState.GameOver;
        }
    }

    private void updateGameOver() {
        game.recordPosition = -1;

        game.recordPosition = game.leaderBoard.recordScore(score);
        if (game.recordPosition > -1) {
            game.saveLeaderBoard();
        }
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        drawScreen();
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

    @Override
    public void dispose () {

    }

    enum GameState {
        Init, CaseSolved, Action, CaseFail, GameOver
    }
}
