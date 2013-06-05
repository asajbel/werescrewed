package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.gui.OptionButton;
import com.blindtigergames.werescrewed.gui.Slider;
import com.blindtigergames.werescrewed.sound.SoundManager;

public class MenuScreen extends Screen {
	
	protected TextureRegion buttonTex = null;

	public MenuScreen( ) {
		super( );
		loadSounds( );
	}

	protected void menuMoveUp(){
		sounds.playSound("menu_up", 0.1f);
		Buttons.get( buttonIndex ).setColored( false );
		if ( buttonIndex == 0 ) {
			buttonIndex = Buttons.size( ) - 1;
		} else {
			buttonIndex--;
		}
		Buttons.get( buttonIndex ).setColored( true );
		controllerTimer = controllerMax;
	}
	
	protected void menuMoveDown(){
		sounds.playSound("menu_down", 0.1f);
		Buttons.get( buttonIndex ).setColored( false );
		buttonIndex++;
		buttonIndex = buttonIndex % Buttons.size( );
		Buttons.get( buttonIndex ).setColored( true );
		controllerTimer = controllerMax;
	}
	
	protected void menuMoveLeft(){
		if ( Buttons.get( buttonIndex ) instanceof OptionButton ) {
			sounds.playSound("menu_left", 0.1f);
			OptionButton option = ( OptionButton ) Buttons
					.get( buttonIndex );
			if ( option.getOption( ) instanceof Slider ) {
				Slider slider = ( Slider ) option.getOption( );
				slider.moveLeft( );
			}
		}
	}
	
	protected void menuMoveRight(){
		if ( Buttons.get( buttonIndex ) instanceof OptionButton ) {
			sounds.playSound("menu_right", 0.1f);
			OptionButton option = ( OptionButton ) Buttons
					.get( buttonIndex );
			if ( option.getOption( ) instanceof Slider ) {
				Slider slider = ( Slider ) option.getOption( );
				slider.moveRight( );
			}
		}
	}
	
	protected void menuSelect(){
		sounds.playSound("menu_select", 0.5f);
		if ( Buttons.get( buttonIndex ) instanceof TextButton ) {
			transOutEnd = false;
		}
		else {
			Buttons.get( buttonIndex ).setSelected( true );
		}
		controllerTimer = controllerMax;
	}
	
	
	
	public void render( float delta ){
		super.render( delta );
		if ( Buttons.size( ) > 0 && transInEnd && transOutEnd ) {

			if ( controllerTimer > 0 ) {
				controllerTimer--;
			} else {

				if ( WereScrewedGame.p1Controller != null ) {
					if ( WereScrewedGame.p1ControllerListener.jumpPressed( )
							|| WereScrewedGame.p1ControllerListener
									.pausePressed( ) ) {
						menuSelect();
					} else if ( WereScrewedGame.p1ControllerListener
							.downPressed( ) ) {
						menuMoveDown();
					} else if ( WereScrewedGame.p1ControllerListener
							.upPressed( ) ) {
						menuMoveUp();
					} else if ( WereScrewedGame.p1ControllerListener
							.leftPressed( ) ) {
						menuMoveLeft();
					} else if ( WereScrewedGame.p1ControllerListener
							.rightPressed( ) ) {
						menuMoveRight();
					}
				}
				if ( WereScrewedGame.p2Controller != null ) {
					if ( WereScrewedGame.p2ControllerListener.jumpPressed( )
							|| WereScrewedGame.p2ControllerListener
									.pausePressed( ) ) {
						menuSelect();
					} else if ( WereScrewedGame.p2ControllerListener
							.downPressed( ) ) {
						menuMoveDown();
					} else if ( WereScrewedGame.p2ControllerListener
							.upPressed( ) ) {
						menuMoveUp();
					} else if ( WereScrewedGame.p2ControllerListener
							.leftPressed( ) ) {
						menuMoveLeft();
					} else if ( WereScrewedGame.p2ControllerListener
							.rightPressed( ) ) {
						menuMoveRight();
					}
				}

				if ( WereScrewedGame.p1Controller == null
						&& WereScrewedGame.p2Controller == null ) {
					if ( Gdx.input.isKeyPressed( Keys.ENTER ) ) {
						menuSelect();
					}
					if ( Gdx.input.isKeyPressed( Keys.DOWN ) ) {
						menuMoveDown();
					}
					if ( Gdx.input.isKeyPressed( Keys.UP ) ) {
						menuMoveUp();
					}
					if ( Gdx.input.isKeyPressed( Keys.LEFT ) ) {
						menuMoveLeft();
					}
					if ( Gdx.input.isKeyPressed( Keys.RIGHT ) ) {
						menuMoveRight();
					}
				}
			}
		}
	}
	
	public void loadSounds(){
		if (sounds == null)
			sounds = new SoundManager();
		sounds.getSound( "menu_up" , WereScrewedGame.dirHandle + "/menu/move.ogg");
		sounds.getSound( "menu_down" , WereScrewedGame.dirHandle + "/menu/move.ogg").setInternalPitch( 0.8f );
		sounds.getSound( "menu_left" , WereScrewedGame.dirHandle + "/menu/move.ogg").setInternalVolume( 0.75f);
		sounds.getSound( "menu_right" , WereScrewedGame.dirHandle + "/menu/move.ogg").setInternalVolume( 0.75f);
		sounds.getSound( "menu_select", WereScrewedGame.dirHandle + "/menu/select.ogg" );
	}
}
