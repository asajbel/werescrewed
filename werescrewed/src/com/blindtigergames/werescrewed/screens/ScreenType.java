package com.blindtigergames.werescrewed.screens;

public enum ScreenType {
	CREDITS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new CreditsScreen( );
		}
	},

	GAME {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PhysicsTestScreen();
		}
	},
	GLEED {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
             return new GleedTestScreen("testLevel");
        }
    },
	INTRO {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new IntroScreen( );
		}
	},
	//TODO: Do we need different enums for each of the loading screens,
	//      since they'll each load different things depending on the level?
	LOADING {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen();
		}
	},
	PAUSE {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PauseScreen( );
		}
	},
	PLAYTEST {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new DebugPlayTestScreen( );
		}
	},
	PHYSICS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PhysicsTestScreen( );
		}
	},
	
	MAIN_MENU {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new MainMenuScreen( );
		}
	},
	
	
	WIN {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new WinScreen( );
		}
	},	
	
	TROPHY {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new TrophyScreen( );
		}
	};

	protected abstract com.badlogic.gdx.Screen getScreenInstance( );

}
