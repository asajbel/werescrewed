package com.blindtigergames.werescrewed.screens;

public enum Screen {
	 
    INTRO {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
            return new IntroScreen();
        }
    },
    PAUSE {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
            return new PauseScreen();
        }
    },
    MAIN_MENU {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
             return new MainMenuScreen();
        }
    },
 
    GAME {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
             return new GameScreen();
        }
    },

    CREDITS {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
             return new CreditsScreen();
        }
    },

    IMOVER {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
             return new IMoverGameScreen();
        }
    };
 
    protected abstract com.badlogic.gdx.Screen getScreenInstance();
 
}
