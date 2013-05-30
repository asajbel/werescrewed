package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.debug.FPSLoggerS;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.OptionButton;
import com.blindtigergames.werescrewed.gui.Slider;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.util.Util;

public class Screen implements com.badlogic.gdx.Screen {

	public ScreenType screenType;
	protected Level level;
	protected SpriteBatch batch;
	protected SBox2DDebugRenderer debugRenderer;
	private Color clearColor = new Color( 0, 0, 0, 1 );
	protected ArrayList< Button > Buttons = new ArrayList< Button >( );
	protected int controllerTimer = 10;
	protected int controllerMax = 10;
	protected int buttonIndex = 0;
	protected float alpha = 1.0f;
	protected boolean finish = false;
	
	private float accum = 0f;               
	private final float step = 1f / 60f;    
	private final float maxAccum = 1f / 17f;
	protected int width;
	protected int height;
	private int x;
	private int y;
	private int bX;
	private int bY;
	private float scale; 
	private ShapeRenderer shapeRenderer;

	BitmapFont debug_font;
	Camera uiCamera;

	public FPSLoggerS logger;

	public Screen( ) {

		// Gdx.app.log( "Screen", "Turning log level to none. SHHH" );
		// Gdx.app.setLogLevel( Application.LOG_NONE );


		shapeRenderer = new ShapeRenderer( );
		batch = new SpriteBatch( );
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		level = null;
		// if(WereScrewedGame.manager.isLoaded( "debug_font" ))
		debug_font = WereScrewedGame.manager.getFont( "debug_font" );

		logger = new FPSLoggerS( );
		uiCamera = new OrthographicCamera( WereScrewedGame.getWidth( ),
				WereScrewedGame.getHeight( ) );
		uiCamera.position.set( 0, 0, 0 ); // -Gdx.graphics.getWidth( ),
											// -Gdx.graphics.getHeight( )
		setClearColor( 0f, 0f, 0f, 1f );
	}

	@Override
	public void render( float delta ) {
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}
		/////////////////////// DON'T REMOVE FOR RELEASE ///////////////////////
		if ((Gdx.input.isKeyPressed( Keys.ALT_LEFT ) 
				|| Gdx.input.isKeyPressed( Keys.ALT_RIGHT )) 
				&& Gdx.input.isKeyPressed( Keys.ENTER ) ) {
			Gdx.graphics.setDisplayMode( Gdx.graphics.getDesktopDisplayMode( ) );
		}
		if ((Gdx.input.isKeyPressed( Keys.ALT_LEFT ) 
				|| Gdx.input.isKeyPressed( Keys.ALT_RIGHT )) 
				&& Gdx.input.isKeyPressed( Keys.ENTER ) ) {
			Gdx.graphics.setDisplayMode( 1280, 720, false );
		}
		//////////////////////////////////////////////////////////////////////
		Gdx.gl.glViewport(
				x,
				y,  
				width, 
				height);
		if ( Gdx.gl20 != null ) {
			Gdx.gl20.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( clearColor.r, clearColor.g, clearColor.b,
					clearColor.a );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}
		
		shapeRenderer.begin( ShapeType.FilledRectangle );
		shapeRenderer.filledRect( x - width / 2, y - height / 2, width, height );
		shapeRenderer.end( );
		
		
		
		if (level != null){			
			updateStep(delta);
			
			// background stuff
			if ( level.backgroundRootSkeleton != null ) {
				if(level.bgCamZoomScale!=0.0f){
					level.backgroundCam.zoom= Util.clamp( 1f+level.camera.camera.zoom*level.bgCamZoomScale,
							level.bgCamZoomMin, level.bgCamZoomMax );
					
				}
				level.backgroundCam.update( );
				level.backgroundRootSkeleton.update( delta );
				
				level.backgroundBatch
						.setProjectionMatrix( level.backgroundCam.combined );
				level.backgroundBatch.begin( );
				level.backgroundRootSkeleton.drawBGDecals(
						level.backgroundBatch, level.camera );
				level.backgroundRootSkeleton
						.draw( level.backgroundBatch, delta );
				level.backgroundBatch.end( );
			}

			level.draw( batch, debugRenderer, delta );

			level.camera.update( delta );

			int FPS = logger.getFPS( );
			
			if ( level.debug && debug_font != null ) {
				batch.setProjectionMatrix( uiCamera.combined );
				batch.begin( );
				debug_font.draw( batch, "FPS: " + FPS,
						-WereScrewedGame.getWidth( ) / 2,
						WereScrewedGame.getHeight( ) / 2 );// -Gdx.graphics.getWidth(
														// )/4,
														// Gdx.graphics.getHeight(
														// )/4
				// debug_font.draw(batch, "ALPHA BUILD", -Gdx.graphics.getWidth(
				// )/2, Gdx.graphics.getHeight( )/2);
				batch.end( );
			}
			
		}
		
		if ( Gdx.input.isKeyPressed( Input.Keys.BACKSPACE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.TROPHY );
		}

		if ( Buttons.size( ) > 0 ) {

			if ( controllerTimer > 0 ) {
				controllerTimer--;
			} else {

				if ( WereScrewedGame.p1Controller != null ) {
					if ( WereScrewedGame.p1ControllerListener.jumpPressed( )
							|| WereScrewedGame.p1ControllerListener
									.pausePressed( ) ) {
						Buttons.get( buttonIndex ).setSelected( true );
						controllerTimer = controllerMax;

					} else if ( WereScrewedGame.p1ControllerListener
							.downPressed( ) ) {

						Buttons.get( buttonIndex ).setColored( false );
						buttonIndex++;
						buttonIndex = buttonIndex % Buttons.size( );
						Buttons.get( buttonIndex ).setColored( true );
						controllerTimer = controllerMax;

					} else if ( WereScrewedGame.p1ControllerListener
							.upPressed( ) ) {

						Buttons.get( buttonIndex ).setColored( false );
						if ( buttonIndex == 0 ) {
							buttonIndex = Buttons.size( ) - 1;
						} else {
							buttonIndex--;
						}
						Buttons.get( buttonIndex ).setColored( true );
						controllerTimer = controllerMax;

					} else if ( WereScrewedGame.p1ControllerListener
							.leftPressed( ) ) {

						if ( Buttons.get( buttonIndex ) instanceof OptionButton ) {
							OptionButton option = ( OptionButton ) Buttons
									.get( buttonIndex );
							if ( option.getOption( ) instanceof Slider ) {
								Slider slider = ( Slider ) option.getOption( );
								slider.moveLeft( );
							}
						}
					} else if ( WereScrewedGame.p1ControllerListener
							.rightPressed( ) ) {

						if ( Buttons.get( buttonIndex ) instanceof OptionButton ) {
							OptionButton option = ( OptionButton ) Buttons
									.get( buttonIndex );
							if ( option.getOption( ) instanceof Slider ) {
								Slider slider = ( Slider ) option.getOption( );
								slider.moveRight( );
							}
						}
					}
				}
				if ( WereScrewedGame.p2Controller != null ) {
					if ( WereScrewedGame.p2ControllerListener.jumpPressed( )
							|| WereScrewedGame.p2ControllerListener
									.pausePressed( ) ) {
						Buttons.get( buttonIndex ).setSelected( true );
						controllerTimer = controllerMax;

					} else if ( WereScrewedGame.p2ControllerListener
							.downPressed( ) ) {

						Buttons.get( buttonIndex ).setColored( false );
						buttonIndex++;
						buttonIndex = buttonIndex % Buttons.size( );
						Buttons.get( buttonIndex ).setColored( true );
						controllerTimer = controllerMax;

					} else if ( WereScrewedGame.p2ControllerListener
							.upPressed( ) ) {

						Buttons.get( buttonIndex ).setColored( false );
						if ( buttonIndex == 0 ) {
							buttonIndex = Buttons.size( ) - 1;
						} else {
							buttonIndex--;
						}
						Buttons.get( buttonIndex ).setColored( true );
						controllerTimer = controllerMax;

					} else if ( WereScrewedGame.p2ControllerListener
							.leftPressed( ) ) {

						if ( Buttons.get( buttonIndex ) instanceof OptionButton ) {
							OptionButton option = ( OptionButton ) Buttons
									.get( buttonIndex );
							if ( option.getOption( ) instanceof Slider ) {
								Slider slider = ( Slider ) option.getOption( );
								slider.moveLeft( );
							}
						}
					} else if ( WereScrewedGame.p2ControllerListener
							.rightPressed( ) ) {

						if ( Buttons.get( buttonIndex ) instanceof OptionButton ) {
							OptionButton option = ( OptionButton ) Buttons
									.get( buttonIndex );
							if ( option.getOption( ) instanceof Slider ) {
								Slider slider = ( Slider ) option.getOption( );
								slider.moveRight( );
							}
						}
					}
				}

				if ( WereScrewedGame.p1Controller == null
						&& WereScrewedGame.p2Controller == null ) {
					if ( Gdx.input.isKeyPressed( Keys.ENTER ) ) {
						Buttons.get( buttonIndex ).setSelected( true );
						controllerTimer = controllerMax;
					}
					if ( Gdx.input.isKeyPressed( Keys.DOWN ) ) {
						Buttons.get( buttonIndex ).setColored( false );
						buttonIndex++;
						buttonIndex = buttonIndex % Buttons.size( );
						Buttons.get( buttonIndex ).setColored( true );
						controllerTimer = controllerMax;
					}
					if ( Gdx.input.isKeyPressed( Keys.UP ) ) {

						Buttons.get( buttonIndex ).setColored( false );
						if ( buttonIndex == 0 ) {
							buttonIndex = Buttons.size( ) - 1;
						} else {
							buttonIndex--;
						}
						Buttons.get( buttonIndex ).setColored( true );
						controllerTimer = controllerMax;
					}
					if ( Gdx.input.isKeyPressed( Keys.LEFT ) ) {
						if ( Buttons.get( buttonIndex ) instanceof OptionButton ) {
							OptionButton option = ( OptionButton ) Buttons
									.get( buttonIndex );
							if ( option.getOption( ) instanceof Slider ) {
								Slider slider = ( Slider ) option.getOption( );
								slider.moveLeft( );
							} 
						}
					}
					if ( Gdx.input.isKeyPressed( Keys.RIGHT ) ) {
						if ( Buttons.get( buttonIndex ) instanceof OptionButton ) {
							OptionButton option = ( OptionButton ) Buttons
									.get( buttonIndex );
							if ( option.getOption( ) instanceof Slider ) {
								Slider slider = ( Slider ) option.getOption( );
								slider.moveRight( );
							} 
						}
					}
				}
			}
		}

	}
	                                        
	private void updateStep(float delta) {   
		accum += delta;  
		accum = Math.min( accum, maxAccum );
		while (accum >= step) {    
			level.update( step );
		    accum -= step;                  
		}                                        
	}         

	protected void addFGSkeleton( Skeleton skel ) {
		addToSkelList( level.skelFGList, skel, false );
	}

	protected void addFGSkeletonBack( Skeleton skel ) {
		addToSkelList( level.skelFGList, skel, true );
	}

	protected void addBGSkeleton( Skeleton skel ) {
		addToSkelList( level.skelBGList, skel, false );
	}

	protected void addBGSkeletonBack( Skeleton skel ) {
		addToSkelList( level.skelBGList, skel, true );
	}

	protected void addFGEntity( Entity entity ) {
		addToEntityList( level.entityFGList, entity, false );
	}
	
	protected void addFGEntity( Entity... entities ) {
		for(Entity e : entities){
			addToEntityList( level.entityFGList, e, false );
		}
	}

	protected void addFGEntityToBack( Entity entity ) {
		addToEntityList( level.entityFGList, entity, true );
	}

	protected void addBGEntity( Entity entity ) {
		addToEntityList( level.entityBGList, entity, false );
	}

	protected void addBGEntityToBack( Entity entity ) {
		addToEntityList( level.entityBGList, entity, true );
	}

	private void addToEntityList( ArrayList< Entity > list, Entity e,
			boolean isBack ) {
		if ( !list.contains( e ) ) {
			if ( isBack )
				list.add( 0, e );
			else
				list.add( e );
		}
	}

	private void addToSkelList( ArrayList< Skeleton > list, Skeleton s,
			boolean isBack ) {
		if ( !list.contains( s ) ) {
			if ( isBack )
				list.add( 0, s );
			else
				list.add( s );
		}
	}

	@Override
	public void resize( int _width, int _height ) {
		if (_width > WereScrewedGame.getWidth( )) 
			scale = (float)_width/(float)WereScrewedGame.getWidth( ); 
		else 
			scale = 1.0f;
		width = (int) (scale * WereScrewedGame.getWidth( )); 
		height = (int) (scale * WereScrewedGame.getHeight( ));
		x = _width / 2 - width / 2; 
		y = _height / 2 - height / 2; 
		bX = -width/2;
		bY = -height/2;
		shapeRenderer.setProjectionMatrix( uiCamera.combined );
		shapeRenderer.setColor( clearColor.r,
				clearColor.g, clearColor.b,
				clearColor.a );
	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose( ) {
		if ( level != null )
			level.resetPhysicsWorld( );

	}

	/**
	 * Set clear color with values in 0-1 range
	 */
	public void setClearColor( float r, float g, float b, float a ) {
		clearColor = new Color( r, g, b, a );
	}

	/**
	 * Set clear color with values in 0-255 range
	 */
	public void setClearColor( int r, int g, int b, int a ) {
		setClearColor( r / 255f, g / 255f, b / 255f, a / 255f );
	}

	public float getAlpha( ) {
		return alpha;
	}

	public void setAlpha( float value ) {
		alpha += value;

		if ( alpha >= 1.0f ) {
			alpha = 1.0f;
			finish = true;
		} else if ( alpha < 0.0f ) {
			alpha = 0.0f;
			finish = true;
		}
	}

	public boolean isFinished( ) {
		return finish;
	}

	public void setFinish( boolean value ) {
		finish = value;
	}
}
