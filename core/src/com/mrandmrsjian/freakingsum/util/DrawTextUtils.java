package com.mrandmrsjian.freakingsum.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * Created by C.S. Jian on 2014/10/28.
 */
public class DrawTextUtils {

    public static void DrawAlignCenter(BitmapFont font, SpriteBatch batch, String text, Color color, float centerX, float centerY) {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, text);
        if (color != null) {
            font.setColor(color);
        }
        font.draw(batch, text, centerX - glyphLayout.width/2, centerY);
    }

    public static void DrawAlignRight(BitmapFont font, SpriteBatch batch, String text, Color color, float rightX, float rightY) {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, text);
        if (color != null) {
            font.setColor(color);
        }
        font.draw(batch, text, rightX - glyphLayout.width, rightY);
    }

    public static void DrawAlignLeft(BitmapFont font, SpriteBatch batch, String text, Color color, float leftX, float leftY) {
        if (color != null) {
            font.setColor(color);
        }
        font.draw(batch, text, leftX, leftY);
    }
}
