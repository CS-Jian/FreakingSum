package com.mrandmrsjian.freakingsum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrandmrsjian.freakingsum.Assets;
import com.mrandmrsjian.freakingsum.DirectedGame;
import com.mrandmrsjian.freakingsum.screens.transitions.ScreenTransition;
import com.mrandmrsjian.freakingsum.screens.transitions.ScreenTransitionFade;

/**
 * Created by Jian on 15/2/3.
 */
public class MenuScreen extends AbstractGameScreen {
    private static final String TAG = MenuScreen.class.getName();
    private Stage stage;
    private Skin skin;
    //private Table table;
    private Image title;
    private Image line;
    private Image playButton;
    private Image rankButton;

    public MenuScreen (DirectedGame game) {
        super(game);
        if(game.isAndroid){
            game.handler.setTrackerScreenName("com.mrandmrsjian.freakingsum.screens.MenuScreen");
            game.handler.showAds(true);
        }

        stage = new Stage(game.viewport);
        Gdx.input.setInputProcessor(stage);
        //skin = new Skin(Gdx.files.internal("uiskin.json"));

        title = new Image(Assets.instance.atlas.findRegion("title"));
        line = new Image(Assets.instance.atlas.findRegion("line"));
        playButton = new Image(Assets.instance.atlas.findRegion("playButton"));
        rankButton = new Image(Assets.instance.atlas.findRegion("rankButton"));

        //table = new Table();
        //table.add(playButton);
        //table.add(rankButton);

        //table.add(title).padTop(100);
        stage.addActor(title);
        stage.addActor(line);
        stage.addActor(playButton);
        stage.addActor(rankButton);
        //stage.addActor(table);

        playButton.addListener(new ClickListener(){
            /*@Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug(TAG, "call gameScreen");
                platform.setScreen(new GameScreen(platform));
            }*/
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                goPlay();
                return true;
            }
        });

        rankButton.addListener(new ClickListener(){
            /*@Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug(TAG, "call gameScreen");
                platform.setScreen(new GameScreen(platform));
            }*/
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                goLeaderBoard();
                return true;
            }
        });


    }

    private void goPlay() {
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new GameScreen(game), transition);
    }

    private void goLeaderBoard() {
        game.recordPosition = -1;
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new LeaderBoardScreen(game, false), transition);
    }

    @Override
    public void show() {
        title.setPosition((game.screenWidth - title.getWidth()) / 2, game.screenHeight - 300);
        line.setPosition((game.screenWidth-line.getWidth())/2, game.screenHeight - 310);
        playButton.setPosition((game.screenWidth - (120 + 80 + 120)) / 2, game.screenHeight - 500);
        rankButton.setPosition((game.screenWidth - (120 + 80 + 120)) / 2 + (120 + 80), game.screenHeight - 500);
        //table.setPosition(400, 320);

        //title.setScaling(Scaling.none);
        /*
        helpTip.setPosition(400-helpTip.getWidth()/2, 30);

        MoveToAction actionMove = Actions.action(MoveToAction.class);
        actionMove.setPosition(400-title.getWidth()/2, 320);
        actionMove.setDuration(2);
        actionMove.setInterpolation(Interpolation.elasticOut);
        title.addAction(actionMove);

        showMenu(true); */

        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Show the loading screen
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose () {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void hide () {
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void pause () {
        //paused = true;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    @Override
    public void resume () {
        super.resume();
        // Only called on Android!
        //paused = false;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
