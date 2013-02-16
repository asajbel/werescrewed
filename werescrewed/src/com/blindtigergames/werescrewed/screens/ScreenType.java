package com.blindtigergames.werescrewed.screens;

public enum ScreenType {

	INTRO {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new IntroScreen( );
		}
	},
	PAUSE {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PauseScreen( );
		}
	},
	LOADING {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( );
		}
	},
	MAIN_MENU {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new MainMenuScreen( );
		}
	},
	GAME {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PhysicsTestScreen();
<<<<<<< HEAD
=======
		}
	},
	
	WIN {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new WinScreen( );
>>>>>>> origin/master
		}
	},
	CREDITS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new CreditsScreen( );
		}
	},
	PHYSICS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PhysicsTestScreen( );
		}
	},
	IMOVER {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
             return new IMoverGameScreen();
        }
    },
	GLEED {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
             return new GleedTestScreen("testLevel");
        }
    };

	protected abstract com.badlogic.gdx.Screen getScreenInstance( );

}
