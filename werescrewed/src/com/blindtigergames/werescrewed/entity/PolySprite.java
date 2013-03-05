/**
 * 
 */
package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * @author Kevin
 *
 */
public class PolySprite extends Sprite {

	protected Mesh mesh;
	protected ShaderProgram shader;
	protected float[] verts;
	protected float[] uvs;
	/**
	 * @param f
	 * @param r
	 * @param c
	 * @param fr
	 * @param spriteSheetName
	 * @param loopType
	 */
	public PolySprite( int f, int r, int c, float fr, String spriteSheetName, int loopType , 
			             Array<Vector2> verts, Array<Vector2> uvs, ShaderProgram s) {
		super( f, r, c, fr, spriteSheetName, loopType );
		constructMesh(verts,uvs);
	}

	/**
	 * @param f
	 * @param r
	 * @param c
	 * @param fr
	 * @param spriteSheetTexture
	 * @param loopType
	 */
	public PolySprite( int f, int r, int c, float fr, Texture spriteSheetTexture, int loopType,
		Array<Vector2> verts, Array<Vector2> uvs, ShaderProgram s ) {
		super( f, r, c, fr, spriteSheetTexture, loopType );
		constructMesh(verts,uvs);
	}

	/**
	 * @param texture
	 */
	public PolySprite( Texture texture,
						Array<Vector2> verts, Array<Vector2> uvs, ShaderProgram s ) {
		super( texture );
		constructMesh(verts,uvs);
	}

	/**
	 * @param region
	 */
	public PolySprite( TextureRegion region,
			Array<Vector2> verts, Array<Vector2> uvs, ShaderProgram s ) {
		super( region );
		constructMesh(verts,uvs);
	}
		
	protected void constructMesh(Array<Vector2> verts, Array<Vector2> uvs){
		if (uvs.size != verts.size){
			Gdx.app.log( "PolySprite" , "Vertex/UV Mismatch: Got "+verts.size+" verts and "+uvs.size+" UV coordinates." );
		}
		this.verts = new float[verts.size * 3];
		this.uvs = new float[verts.size * 2];
		for (int i = 0; i < verts.size; i++){
			this.verts [3*i]   = verts.get( i ).x;
			this.verts [3*i+1] = verts.get( i ).y;
			this.verts [3*i+2] = 0.0f;
			
			this.uvs   [2*i]   = uvs.get( i ).x;
			this.uvs   [2*i+1] = uvs.get( i ).y;
		}
		mesh = new Mesh(true, verts.size, 0,
				new VertexAttribute(Usage.Position, 2, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		 mesh.setVertices(this.verts);
	}
	
	public void draw(SpriteBatch batch){
		draw(this.shader);
	}
	
	
	public void draw(ShaderProgram shader){
	    // this should be called in render()
	    if (mesh == null)
	        throw new IllegalStateException("drawMesh called before a mesh has been created.");
	 
	    GL20 gl = Gdx.graphics.getGL20();
	    if (gl != null){
		    //we don't necessarily need these, but its good practice to enable
		    //the things we need. we enable 2d textures and set the active one
		    //to 0. we could have multiple textures but we don't need it here.
		    gl.glEnable(GL20.GL_TEXTURE_2D);
		    gl.glActiveTexture(GL20.GL_TEXTURE0);
		 
		    shader.begin();
		    //this sets our uniform 'u_texture' (i.e. the gl texture we want to use) to 0.
		    shader.setUniformi("u_texture", 0);
		    this.getTexture( ).bind();
		    mesh.render(shader, GL20.GL_TRIANGLES);
		    shader.end();
		}
	}
}
