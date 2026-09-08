package com.kirby.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.kirby.game.physics.PhysicsWorld;

public class KirbyGame extends Game {
    private PhysicsWorld physicsWorld;

    @Override
    public void create() {
        physicsWorld = new PhysicsWorld();
        Gdx.app.log("KirbyGame", "Box2D 空世界已创建，开始步进（重力 -9.8）");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        physicsWorld.step(Gdx.graphics.getDeltaTime());

        super.render();
    }

    @Override
    public void dispose() {
        physicsWorld.dispose();
        super.dispose();
    }
}
