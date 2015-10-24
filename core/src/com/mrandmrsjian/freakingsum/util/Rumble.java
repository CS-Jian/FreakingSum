package com.mrandmrsjian.freakingsum.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

/**
 * Created by Jian on 15/9/24.
 */
public class Rumble {
    private Random random;
    private float rumbleX;
    private float rumbleY;
    public float rumbleTime = 0;
    public float remainRumbleTime = 1;
    private float rumblePower = 0;
    private float currentRumblePower = 0;

    public Rumble() {
        new Rumble(0, 1);
    }

    // Call this function with the force of the shake
    // and how long it should last
    public Rumble(float rumblePower, float rumbleTime) {
        random = new Random();
        this.rumblePower = rumblePower;
        this.rumbleTime = rumbleTime;
        this.remainRumbleTime = rumbleTime;
        this.currentRumblePower = 0;
    }

    public void tick(float delta, Camera camera, Vector3 cameraPosition){
        if(remainRumbleTime > 0) {
            currentRumblePower = rumblePower * (remainRumbleTime / rumbleTime);
            //currentRumblePower = 5;
            rumbleX = (random.nextFloat() - 0.5f) * 2 * currentRumblePower;
            rumbleY = (random.nextFloat() - 0.5f) * 2 * currentRumblePower;

            camera.translate(-rumbleX, -rumbleY, 0);
            remainRumbleTime -= delta;
        } else {
            camera.position.x = cameraPosition.x;
            camera.position.y = cameraPosition.y;
        }
        /*
        if(currentRumbleTime <= rumbleTime) {
            currentRumblePower = rumblePower * ((rumbleTime - currentRumbleTime) / rumbleTime);

            rumbleX = (random.nextFloat() - 0.5f) * 2 * currentRumblePower;
            rumbleY = (random.nextFloat() - 0.5f) * 2 * currentRumblePower;

            camera.translate(-rumbleX, -rumbleY, 0);
            currentRumbleTime += delta;
        } else {
            camera.position.x = camOrigin.x;
            camera.position.y = camOrigin.y;
        }*/
    }

}
