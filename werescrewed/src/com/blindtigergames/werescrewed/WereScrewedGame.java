package com.blindtigergames.werescrewed;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.blindtigergames.werescrewed.screens.Screen;
import com.blindtigergames.werescrewed.screens.ScreenManager;

public class WereScrewedGame extends Game {

    public static AssetManager manager = new AssetManager();

    @Override
    public void create() {
        ScreenManager.getInstance().initialize( this );

        // uncomment next line to bypass intro
        ScreenManager.getInstance().show( Screen.IMOVER );
    }

    @Override
    public void dispose() {
        super.dispose();
        ScreenManager.getInstance().dispose();
    }

    @Override
    public void render() {
        update( 0 );
        super.render();
    }

    public void update( float dT ) {
    }

}
