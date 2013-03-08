package com.blindtigergames.werescrewed.screens;

//import javax.vecmath.Vector4f;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.level.GleedLoader;

public class PolySpriteTestScreen extends Screen {
	PolySprite poly;
	//static ShaderProgram shader;
	
	public PolySpriteTestScreen( String name ){
		super();
		String filename = "data/levels/"+ name + ".xml";

		level = new GleedLoader().load( filename );
		level.world.setContactListener( MCL );
		//initShaders();
		//initPolySprite();
	}
	
	protected void initPolySprite(){
		Array<Vector2> verts = new Array<Vector2>();
		verts.add( new Vector2(-500f,-500f) );
		verts.add( new Vector2(500.0f,-500.0f) );
		verts.add( new Vector2(500.0f,500.0f) );
		verts.add( new Vector2(-500.0f,500.0f) );
		
		Array<Vector2> uvs = new Array<Vector2>();
		uvs.add( new Vector2(-4.0f,4.0f) );
		uvs.add( new Vector2(4.0f,4.0f) );
		uvs.add( new Vector2(4.0f,-4.0f) );
		uvs.add( new Vector2(-4.0f,-4.0f) );
		
		Texture polyTex = WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( ) + "/common/tileset/TilesetTest.png",
				Texture.class );
		
		//poly = new PolySprite(polyTex, verts, 1,1,1,1, uvs);
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
		
		
//		if (Gdx.graphics.isGL20Available( )){
//			batch.setProjectionMatrix( level.camera.combined() );
//			//batch.setShader( shader );
//			batch.enableBlending( );
//			batch.setBlendFunction( GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA );
//			
//			//batch.
//			//batch.begin( );
//			//poly.draw( batch );
//			//batch.end();
//			//Gdx.app.log( "Yo", "drawing" );
//		}
	}
}