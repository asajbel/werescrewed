package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.level.GleedLoader;

public class PolySpriteTestScreen extends Screen {
	PolySprite poly;
	ShaderProgram shader;
	
	public PolySpriteTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";

		level = new GleedLoader().load( filename );
		level.world.setContactListener( MCL );
		initShaders();
		initPolySprite();
	}
	
	protected void initShaders(){
		if (Gdx.graphics.isGL20Available( )){
		//Extremely simple vert/fragment shader
		String vertexShader = "attribute vec4 a_position;    \n"
							+ "attribute vec4 a_color;       \n"
							+ "attribute vec2 a_texCoords;   \n"
							+ "varying vec4 v_color;         \n"
							+ "varying vec2 v_texCoords;     \n"
							+ "void main()                   \n"
							+ "{                             \n"
							+ "   v_color = a_color;         \n"
							+ "   v_texCoords = a_texCoords; \n"
							+ "   gl_Position = a_position;  \n"
							+ "}                             \n";
		String fragmentShader = "#ifdef GL_ES                \n"
							+ "precision mediump float;    \n"
							+ "#endif                      \n"
							+ "varying vec4 v_color;       \n"
							+ "varying vec2 v_texCoords;   \n"
							+ "uniform sampler2D u_texture;\n"
							+ "void main()                 \n"
							+ "{                           \n"
							+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);   \n"
							+ "}                           \n";
		shader = new ShaderProgram(vertexShader, fragmentShader);
		} else {
			Gdx.app.log( "PolySpriteTestScreen", "OpenGL ES 2.0 not available. Try again on better hardware." );
			shader = null;
		}
	}
	
	protected void initPolySprite(){
		Array<Vector2> verts = new Array<Vector2>();
		verts.add( new Vector2(0.0f,0.0f) );
		verts.add( new Vector2(2.0f,0.0f) );
		verts.add( new Vector2(1.0f,2.0f) );

		Array<Vector2> uvs = new Array<Vector2>();
		uvs.add( new Vector2(0.0f,0.0f) );
		uvs.add( new Vector2(2.0f,0.0f) );
		uvs.add( new Vector2(1.0f,2.0f) );
		
		Texture polyTex = WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( ) + "/common/tileset/TilesetTest.png",
				Texture.class );
		
		poly = new PolySprite(polyTex, verts, uvs, shader);
		
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
		if (Gdx.graphics.isGL20Available( )){
			poly.draw( shader );
		}
	}
}