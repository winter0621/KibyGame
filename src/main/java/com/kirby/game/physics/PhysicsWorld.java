package com.kirby.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * 封装 Box2D 世界：负责创建带重力的世界、以固定步长推进模拟、释放资源。
 * 当前为「空世界示例」阶段：只验证物理引擎能正常初始化与步进。
 */
public class PhysicsWorld {
    private static final Vector2 GRAVITY = new Vector2(0, -9.8f);
    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float MAX_FRAME_TIME = 0.25f;

    private final World world;
    private float accumulator;

    public PhysicsWorld() {
        world = new World(GRAVITY, true);
        world.setContactListener(new CollisionHandler());
        accumulator = 0f;
    }

    /**
     * 按固定步长推进物理世界，避免帧率波动导致模拟不稳定。
     *
     * @param deltaTime 距上一帧的时间（秒）
     */
    public void step(float deltaTime) {
        float frameTime = Math.min(deltaTime, MAX_FRAME_TIME);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    public World getWorld() {
        return world;
    }

    public void dispose() {
        world.dispose();
    }
}
