package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.screens.GameScreen;

public class Camera {
	public OrthographicCamera camera;
	public float viewportHeight;
	public float viewportWidth;
	public Vector3 position;
	
	public Player player;
	
	public Camera(float viewportWidth, float viewportHeight)
	{
		//this.player = player;
		camera = new OrthographicCamera(1, viewportHeight/viewportWidth);
		this.viewportHeight = Gdx.graphics.getHeight();
		this.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportWidth = this.viewportWidth;
		camera.viewportHeight = this.viewportHeight;
		camera.position.set(this.viewportWidth * .5f, this.viewportHeight * .5f, 0f);  
		position = camera.position;
        camera.update();  
	}
	
	public Camera(float viewportWidth, float viewportHeight, Player player)
	{
		this.player = player;
		camera = new OrthographicCamera(1, viewportHeight/viewportWidth);
		this.viewportHeight = Gdx.graphics.getHeight();
		this.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportWidth = this.viewportWidth;
		camera.viewportHeight = this.viewportHeight;
		camera.position.set(this.viewportWidth * .5f *GameScreen.PIXEL_TO_BOX , this.viewportHeight * .5f*GameScreen.PIXEL_TO_BOX, 0f);  
		position = camera.position;
		
        camera.update();  
	}
	
	public Matrix4 combined()
	{
		return camera.combined;
	}
	
	public void update()
	{
		handleInput();
		camera.update();
		position = camera.position;
		/*float lerp = 0.1f;
		Vector3 position = camera.position;
		position.x += (player.positionX - position.x) * lerp;
		position.y += (player.positionY - position.y) * lerp;
		camera.position.add(position);
		*/
	}

	private void followPlayer()
	{
		//camera.position = new Vector3(player.position.x, player.position.y, 0);
		camera.translate(player.position);
	}
    private void handleInput() {
            if(Gdx.input.isKeyPressed(Input.Keys.E)) {
                    camera.zoom += 0.02;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
                    camera.zoom -= 0.02;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    if (camera.position.x > 0)
                            camera.translate(-3, 0, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    if (camera.position.x < 1024)
                            camera.translate(3, 0, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    if (camera.position.y > 0)
                            camera.translate(0, -3, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    if (camera.position.y < 1024)
                            camera.translate(0, 3, 0);
            }

    }
}