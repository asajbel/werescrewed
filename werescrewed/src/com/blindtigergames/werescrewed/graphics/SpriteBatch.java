package com.blindtigergames.werescrewed.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class SpriteBatch extends com.badlogic.gdx.graphics.g2d.SpriteBatch {

	/**
	 * Constructs a new SpriteBatch. Sets the projection matrix to an
	 * orthographic projection with y-axis point upwards, x-axis point to the
	 * right and the origin being in the bottom left corner of the screen. The
	 * projection will be pixel perfect with respect to the screen resolution.
	 */
	public SpriteBatch( ) {
		this( 1000 );
	}

	/**
	 * Constructs a SpriteBatch with the specified size and (if GL2) the default
	 * shader. See {@link #SpriteBatch(int, ShaderProgram)}.
	 */
	public SpriteBatch( int size ) {
		this( size, null );
	}

	/**
	 * <p>
	 * Constructs a new SpriteBatch. Sets the projection matrix to an
	 * orthographic projection with y-axis point upwards, x-axis point to the
	 * right and the origin being in the bottom left corner of the screen. The
	 * projection will be pixel perfect with respect to the screen resolution.
	 * </p>
	 * 
	 * <p>
	 * The size parameter specifies the maximum size of a single batch in number
	 * of sprites
	 * </p>
	 * 
	 * <p>
	 * The defaultShader specifies the shader to use. Note that the names for
	 * uniforms for this default shader are different than the ones expect for
	 * shaders set with {@link #setShader(ShaderProgram)}. See the
	 * {@link #createDefaultShader()} method.
	 * </p>
	 * 
	 * @param size
	 *            the batch size in number of sprites
	 * @param defaultShader
	 *            the default shader to use. This is not owned by the
	 *            SpriteBatch and must be disposed separately.
	 */
	public SpriteBatch( int size, ShaderProgram defaultShader ) {
		this( size, 1, defaultShader );
	}

	/**
	 * Constructs a SpriteBatch with the specified size and number of buffers
	 * and (if GL2) the default shader. See
	 * {@link #SpriteBatch(int, int, ShaderProgram)}.
	 */
	public SpriteBatch( int size, int buffers ) {
		this( size, buffers, null );
	}

	public SpriteBatch( int size, int buffers, ShaderProgram defaultShader ) {
		super( size, buffers, defaultShader );
		if ( Gdx.graphics.isGL20Available( ) && defaultShader == null ) {
			setShader( createDefaultShader( ) );
		}
	}

	/**
	 * Returns a new instance of the default shader used by SpriteBatch for GL2
	 * when no shader is specified.
	 */
	static public ShaderProgram createDefaultShader( ) {
		String vertexShader = "attribute vec4 "
				+ ShaderProgram.POSITION_ATTRIBUTE
				+ ";\n" //
				+ "attribute vec4 "
				+ ShaderProgram.COLOR_ATTRIBUTE
				+ ";\n" //
				+ "attribute vec2 "
				+ ShaderProgram.TEXCOORD_ATTRIBUTE
				+ "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = "
				+ ShaderProgram.COLOR_ATTRIBUTE
				+ ";\n" //
				+ "   v_texCoords = "
				+ ShaderProgram.TEXCOORD_ATTRIBUTE
				+ "0;\n" //
				+ "   gl_Position =  u_projTrans * "
				+ ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" // Vertex Color
				+ "varying vec2 v_texCoords;\n" // Texture Coordinates
				+ "varying vec2 v_cameraCoords;\n" // Camera
				+ "uniform sampler2D u_texture;\n" // Diffuse Texture
				+ "uniform sampler2D s_texture;\n" // Specular Texture
				+ "uniform sampler2D n_texture;\n" // Normal Texture
				+ "void main()\n"//
				+ "{\n" //
				+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
				+ "}";

		ShaderProgram shader = new ShaderProgram( vertexShader, fragmentShader );
		if ( shader.isCompiled( ) == false )
			throw new IllegalArgumentException( "couldn't compile shader: "
					+ shader.getLog( ) );
		return shader;
	}
}
