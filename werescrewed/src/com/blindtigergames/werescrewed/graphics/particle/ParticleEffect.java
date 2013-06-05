/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.blindtigergames.werescrewed.graphics.particle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;

/**
 * See <a href="http://www.badlogicgames.com/wordpress/?p=1255">http://www.
 * badlogicgames.com/wordpress/?p=1255</a>
 * 
 * @author mzechner Anders Sajbel
 */
public class ParticleEffect implements Disposable {
	private final Array< ParticleEmitter > emitters;
	/**
	 * If true, will update position and rotation when parent updates.
	 */
	public boolean updatePositionOnUpdate = true;
	/**
	 * Flag for parent to delete this when it particle effect is complete
	 */
	public boolean removeOnComplete = false;

	public boolean updateAngleWithParent = true;

	public Vector2 offsetFromParent = new Vector2( );

	public String name; // unique instance name

	public String effectName; // common name of effect

	public ParticleEffect( ) {
		emitters = new Array< ParticleEmitter >( 8 );
		name = null;
		effectName = null;
	}

	public ParticleEffect( ParticleEffect effect ) {
		updatePositionOnUpdate = effect.updatePositionOnUpdate;
		removeOnComplete = effect.removeOnComplete;
		name = effect.name;
		effectName = effect.effectName;
		emitters = new Array< ParticleEmitter >( true, effect.emitters.size );
		for ( int i = 0, n = effect.emitters.size; i < n; i++ )
			emitters.add( new ParticleEmitter( effect.emitters.get( i ) ) );

	}

	public void start( ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).start( );
	}

	public void reset( ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).reset( );
	}

	public void update( float delta ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).update( delta );
	}

	public void draw( SpriteBatch spriteBatch, Camera camera ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).draw( spriteBatch, camera );
	}

	public void draw( SpriteBatch spriteBatch, float delta, Camera camera ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).draw( spriteBatch, delta, camera );
	}

	public void allowCompletion( ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).allowCompletion( );
	}

	public boolean isComplete( ) {
		for ( int i = 0, n = emitters.size; i < n; i++ ) {
			ParticleEmitter emitter = emitters.get( i );
			if ( !emitter.isComplete( ) )
				return false;
		}
		return true;
	}

	public void setDuration( int duration ) {
		for ( int i = 0, n = emitters.size; i < n; i++ ) {
			ParticleEmitter emitter = emitters.get( i );
			emitter.setContinuous( false );
			emitter.duration = duration;
			emitter.durationTimer = 0;
		}
	}

	public void setPosition( float x, float y ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).setPosition( x, y );
	}

	/**
	 * Sets the angle of the emitters of the effect
	 * 
	 * @author stew
	 * @param radians
	 *            The angle of rotation in radians;
	 */
	public ParticleEffect setAngle( float radians ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).setAngle( radians );
		return this;
	}

	/**
	 * Sets the rotation of the sprites of the effect
	 * 
	 * @author Anders
	 * @param radians
	 *            The angle of rotation in radians;
	 */
	public ParticleEffect setRotation( float radians ) {
		for ( int i = 0; i < emitters.size; i++ )
			emitters.get( i ).setRotation( radians );
		return this;
	}

	/**
	 * Sets the angle of the entire effect setting both the emitter angle and
	 * sprite rotation
	 * 
	 * @author Anders
	 * @param radians
	 *            The angle of rotation in radians;
	 */
	public void setEffectAngle( float radians ) {
		setAngle( radians );
		setRotation( radians );
	}

	/**
	 * Sets the time it takes for a particle to be removed
	 * 
	 * @author Anders
	 * @param lifeMillis
	 *            The time in milliseconds
	 */
	public void setLifeTime( float lifeMaxMillis, float lifeMinMillis ) {
		for ( ParticleEmitter e : emitters ) {
			e.setLifeValue( lifeMaxMillis, lifeMinMillis );
		}
	}

	/**
	 * Sets the number of particles emitted in a second.
	 * 
	 * @author Anders
	 * @param emitsPerSecondMax
	 *            The maximum number of particles emitted in a second.
	 * @param emitsPerSecondMin
	 *            The minimum number of particles emitted in a second.
	 */
	public void setEmission( float emitsPerSecondMax, float emitsPerSecondMin ) {
		for ( ParticleEmitter e : emitters ) {
			e.setEmmisionValue( emitsPerSecondMax, emitsPerSecondMin );
		}
	}

	/**
	 * Adds particles to emitters in the effect. Do not call in an update loop
	 * as it resets the emitters.
	 * 
	 * @author Anders
	 * @param particles
	 *            Number of particles to add.
	 */
	public void addParticles( int particles ) {
		for ( ParticleEmitter e : emitters ) {
			int min = e.getMinParticleCount( );
			int max = e.getMaxParticleCount( );
			e.setMinParticleCount( min + particles );
			e.setMaxParticleCount( max + particles );
		}
	}

	/**
	 * Subtracts particles from the emitters in the effect. Do not call in an
	 * update loop as it resets the emitters.
	 * 
	 * @author Anders
	 * @param particles
	 *            Number of particles to subtract.
	 */
	public void subtractParticles( int particles ) {
		addParticles( -particles );
	}

	/**
	 * Sets the minimum number of particles the emitters in the effect can
	 * produce at a time
	 * 
	 * @author Anders
	 * @param min
	 *            Minimum Number.
	 */
	public void setMinParticleCount( int min ) {
		for ( ParticleEmitter e : emitters ) {
			e.setMinParticleCount( min );
		}
	}

	/**
	 * Sets the maximum number of particles the emitters in the effect can
	 * produce at a time
	 * 
	 * @author Anders
	 * @param min
	 *            Maximum Number.
	 */
	public void setMaxParticleCount( int max ) {
		for ( ParticleEmitter e : emitters ) {
			e.setMaxParticleCount( max );
		}
	}
	
	public void setAngleDiff( float high, float low ) {
		for ( ParticleEmitter e : emitters ) {
			e.setAngleDiff( high, low ); 
		}
	}

	/**
	 * Changes the maximum size of a particle effect. Do not call continuously.
	 * For fire change the values maxParticles = emitsMax * seconds + 10;
	 * emitsMax = 20 + 10 * seconds.
	 * 
	 * @author Anders
	 * @param maxLife
	 *            The maximum life time of a particle in milliseconds
	 * @param emitsMax
	 *            The maximum number of particles emitted per second
	 * @param maxParticles
	 *            The maximum number of particles allowed a a time.
	 */
	public void changeEffectMaxSize( float maxLife, float emitsMax,
			int maxParticles ) {
		for ( ParticleEmitter e : emitters ) {
			e.setMaxParticleCount( maxParticles );
			float lowEmit = e.getEmission( ).getLowMax( );
			e.setEmmisionValue( emitsMax, lowEmit );
			float lowLife = e.getLife( ).getLowMax( );
			e.setLifeValue( maxLife, lowLife );
		}
	}

	/**
	 * Changes the minimum size of a particle effect. Do not call continuously.
	 * 
	 * @author Anders
	 * @param maxLife
	 *            The minimum life time of a particle in milliseconds
	 * @param emitsMax
	 *            The minimum number of particles emitted per second
	 * @param maxParticles
	 *            The minimum number of particles allowed a a time.
	 */
	public void changeEffectMinSize( float minLife, float emitsMin,
			int minParticles ) {
		for ( ParticleEmitter e : emitters ) {
			e.setMinParticleCount( minParticles );
			float highEmit = e.getEmission( ).getHighMax( );
			e.setEmmisionValue( highEmit, emitsMin );
			float highLife = e.getLife( ).getHighMax( );
			e.setLifeValue( highLife, minLife );
		}
	}

	/**
	 * Sets the velocity values of all the emitters in a particle effect
	 * 
	 * @param maxVelocity
	 *            Maximum velocity in pixels per second
	 * @param minVelocity
	 *            Minimum velocity in pixels per second
	 */
	public void setVelocity( float maxVelocity, float minVelocity ) {
		for ( ParticleEmitter e : emitters ) {
			e.setVelocity( maxVelocity, minVelocity );
		}
	}

	/**
	 * Sets the size values of all the emitters in a particle effect
	 * 
	 * @param maxSize
	 *            Maximum size in pixels
	 * @param minSize
	 *            Minimum size in pixels
	 */
	public void setSize( float maxSize, float minSize ) {
		for ( ParticleEmitter e : emitters ) {
			e.setSize( maxSize, minSize );
		}
	}

	public void setOffset( float xOffset, float yOffset ) {
		for ( int i = 0; i < emitters.size; i++ )
			emitters.get( i ).setOffset( xOffset, yOffset );

	}

	public void setFlip( boolean flipX, boolean flipY ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).setFlip( flipX, flipY );
	}

	public void flipY( ) {
		for ( int i = 0, n = emitters.size; i < n; i++ )
			emitters.get( i ).flipY( );
	}

	public Array< ParticleEmitter > getEmitters( ) {
		return emitters;
	}

	/** Returns the emitter with the specified name, or null. */
	public ParticleEmitter findEmitter( String name ) {
		for ( int i = 0, n = emitters.size; i < n; i++ ) {
			ParticleEmitter emitter = emitters.get( i );
			if ( emitter.getName( ).equals( name ) )
				return emitter;
		}
		return null;
	}

	public void save( File file ) {
		Writer output = null;
		try {
			output = new FileWriter( file );
			int index = 0;
			for ( int i = 0, n = emitters.size; i < n; i++ ) {
				ParticleEmitter emitter = emitters.get( i );
				if ( index++ > 0 )
					output.write( "\n\n" );
				emitter.save( output );
				output.write( "- Image Path -\n" );
				output.write( emitter.getImagePath( ) + "\n" );
			}
		} catch ( IOException ex ) {
			throw new GdxRuntimeException( "Error saving effect: " + file, ex );
		} finally {
			try {
				if ( output != null )
					output.close( );
			} catch ( IOException ex ) {
			}
		}
	}

	public void load( FileHandle effectFile, FileHandle imagesDir ) {
		loadEmitters( effectFile );
		loadEmitterImages( imagesDir );
	}

	public void load( FileHandle effectFile, TextureAtlas atlas ) {
		loadEmitters( effectFile );
		loadEmitterImages( atlas );
	}

	public void loadEmitters( FileHandle effectFile ) {
		InputStream input = effectFile.read( );
		emitters.clear( );
		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new InputStreamReader( input ), 512 );
			while ( true ) {
				ParticleEmitter emitter = new ParticleEmitter( reader );
				reader.readLine( );
				emitter.setImagePath( reader.readLine( ) );
				emitters.add( emitter );
				if ( reader.readLine( ) == null )
					break;
				if ( reader.readLine( ) == null )
					break;
			}
		} catch ( IOException ex ) {
			throw new GdxRuntimeException( "Error loading effect: "
					+ effectFile, ex );
		} finally {
			try {
				if ( reader != null )
					reader.close( );
			} catch ( IOException ex ) {
			}
		}
	}

	public void loadEmitterImages( TextureAtlas atlas ) {
		for ( int i = 0, n = emitters.size; i < n; i++ ) {
			ParticleEmitter emitter = emitters.get( i );
			String imagePath = emitter.getImagePath( );
			if ( imagePath == null )
				continue;
			String imageName = new File( imagePath.replace( '\\', '/' ) )
					.getName( );
			int lastDotIndex = imageName.lastIndexOf( '.' );
			if ( lastDotIndex != -1 )
				imageName = imageName.substring( 0, lastDotIndex );
			Sprite sprite = atlas.createSprite( imageName );
			if ( sprite == null )
				throw new IllegalArgumentException(
						"SpriteSheet missing image: " + imageName );
			emitter.setSprite( sprite );
		}
	}

	public void loadEmitterImages( FileHandle imagesDir ) {
		for ( int i = 0, n = emitters.size; i < n; i++ ) {
			ParticleEmitter emitter = emitters.get( i );
			String imagePath = emitter.getImagePath( );
			if ( imagePath == null )
				continue;
			String imageName = new File( imagePath.replace( '\\', '/' ) )
					.getName( );
			emitter.setSprite( new Sprite( loadTexture( imagesDir
					.child( imageName ) ) ) );
		}
	}

	protected Texture loadTexture( FileHandle file ) {
		return new Texture( file, false );
	}

	/** Disposes the texture for each sprite for each ParticleEmitter. */
	public void dispose( ) {
		for ( int i = 0, n = emitters.size; i < n; i++ ) {
			ParticleEmitter emitter = emitters.get( i );
			emitter.getSprite( ).getTexture( ).dispose( );
		}
	}

	public void restartAt( float xPix, float yPix ) {
		setPosition( xPix, yPix );
		reset( );
		start( );
	}

	public void restartAt( Vector2 posPixels ) {
		restartAt( posPixels.x, posPixels.y );
	}

	public ParticleEffect setOffsetFromParent( int x, int y ) {
		this.offsetFromParent.x = x;
		this.offsetFromParent.y = y;
		return this;
	}

}
