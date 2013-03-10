/**
 * 
 */
package com.blindtigergames.werescrewed.entity;

//import javax.vecmath.Vector4f;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.sun.java.swing.plaf.windows.resources.windows;

/**
 * COnstruct a sprite that fills a texture inside a CONVEX polygon
 * @author Stew / a little bit Kevin :D
 * 
 */
public class PolySprite extends Sprite {

	protected Mesh mesh;
	protected ShaderProgram shader;
	protected float[ ] verts;
	protected Rectangle bounds;
	protected Vector2 center;


	/**
	 * Construct a polysprite with a given texture
	 * @param texture
	 * @param verts an array of verts, each vector2 is x,y of a vertice
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public PolySprite( Texture texture, Array< Vector2 > verts, float r,
			float g, float b, float a ) {
		super( texture );
		constructMesh( verts, r, g, b, a );
	}

	public PolySprite( Texture texture, Array< Vector2 > verts ) {
		super( texture );
		constructMesh( verts, 1f, 1f, 1f, 1f );
	}

	protected void constructMesh( Array< Vector2 > verts, float r, float g,
			float b, float a ) {

		shader = WereScrewedGame.defaultShader;
		
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		// 9 is 3 positions, 4 colors, and 2 texcoords
		this.verts = new float[ verts.size * 9 ];
		// this.uvs = new float[ verts.size * 2 ];

		for ( int i = 0; i < verts.size; i++ ) {
			float x = verts.get( i ).x;
			float y = verts.get( i ).y;
			//get the bounds of the poly!
			if ( x < minX ) {
				minX = x;
			} else if ( x > maxX ) {
				maxX = x;
			}
			if ( y < minY ) {
				minY = y;
			} else if ( y > maxY ) {
				maxY = y;
			}
			this.verts[ 9 * i ] = x; // x
			this.verts[ 9 * i + 1 ] = y; // y
			this.verts[ 9 * i + 2 ] = 0.0f; // z

			this.verts[ 9 * i + 3 ] = r; // r
			this.verts[ 9 * i + 4 ] = g; // g
			this.verts[ 9 * i + 5 ] = b; // b
			this.verts[ 9 * i + 6 ] = a; // a
			
			// we actually set the uvs later because we need the bounds to
			// properly set it.
		}

		this.bounds = new Rectangle( minX, minY, maxX - minX, maxY - minY );
		center = new Vector2( bounds.x + bounds.width / 2, bounds.y
				+ bounds.height / 2 );

		float[ ] texCoords = createTexCoords( verts );

		for ( int i = 0; i < verts.size; i++ ) {
			this.verts[ 9 * i + 7 ] = texCoords[ 2 * i ]; // uv
			this.verts[ 9 * i + 8 ] = texCoords[ 2 * i + 1 ]; // uw
		}

		//System.out.println( "HELLA " + verts.size + "\n\n\n" );

		mesh = new Mesh( true, verts.size, ( verts.size - 2 ) * 3,
				VertexAttribute.Position( ), VertexAttribute.ColorUnpacked( ),
				VertexAttribute.TexCoords( 0 ) );
		mesh.setVertices( this.verts );
		mesh.setIndices( createIndices( verts.size ) );
	}

	/**
	 * Creates a triangle fan array of indices for the given vertices
	 * @author stew
	 * @param numVerts
	 * @return
	 */
	private short[ ] createIndices( int numVerts ) {
		int numTriangles = numVerts - 2;
		// 3 indices per triangle, (numVerts-2) triangles
		short[ ] indices = new short[ numTriangles * 3 ];
		// insert the first triangle cus it's a shitty mc-weird initial case:
		indices[ 0 ] = 0;
		indices[ 1 ] = 1;
		indices[ 2 ] = 2;
		// then do the rest of the triangles:
		for ( short i = 1; i < numTriangles; ++i ) {
			indices[ i * 3 ] = ( short ) ( i + 1 );
			indices[ i * 3 + 1 ] = ( short ) ( i + 2 );
			indices[ i * 3 + 2 ] = 0;
			//System.out.println( "DERP!" );
		}
		//System.out.print( "yo!" );
		/*for ( int i = 0; i < indices.length; ++i ) {
			System.out.print( indices[ i ] + ", " );
		}
		System.out.print( "\n" );*/
		return indices;
	}

	/**
	 * really nicely lerp texture coordinates so the texture is not skewed on the polygon.
	 * @param verts
	 * @return
	 */
	private float[ ] createTexCoords( Array< Vector2 > verts ) {
		float[ ] texCoords = new float[ verts.size * 2 ];
		float texWidth = bounds.width / getTexture( ).getWidth( );
		float texHeight = bounds.height / getTexture( ).getHeight( );
		float halfTexWidth = texWidth / 2;
		float halfTexHeight = texHeight / 2;

		for ( int i = 0; i < verts.size; ++i ) {
			texCoords[ 2 * i ] = verts.get( i ).x / bounds.width * texWidth
					- halfTexWidth;
			texCoords[ 2 * i + 1 ] = verts.get( i ).y / bounds.height
					* texHeight - halfTexHeight;
		}
		return texCoords;
	}

	@Override
	public void draw( SpriteBatch batch ) {
		// this should be called in render()
		if ( mesh == null )
			throw new IllegalStateException(
					"drawMesh called before a mesh has been created." );

		GL20 gl = Gdx.graphics.getGL20( );
		if ( gl != null ) {
			// we don't necessarily need these, but its good practice to enable
			// the things we need. we enable 2d textures and set the active one
			// to 0. we could have multiple textures but we don't need it here.
			gl.glEnable( GL20.GL_TEXTURE_2D );
			gl.glActiveTexture( GL20.GL_TEXTURE0 );
			
			//setWrap also binds the texture.
			this.getTexture( ).setWrap( Texture.TextureWrap.Repeat,
					Texture.TextureWrap.Repeat );
			mesh.render( shader, GL20.GL_TRIANGLES );
			//mesh.render( shader, GL20.GL_TRIANGLE_FAN );
		}
	}

}
