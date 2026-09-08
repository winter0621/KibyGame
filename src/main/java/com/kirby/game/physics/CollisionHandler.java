package com.kirby.game.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.kirby.game.constants.BodyTag;

/**
 * Box2D 碰撞契约（§5.2 ContactListener）的落地实现。
 * 负责接收所有碰撞回调，并按 {@code userData标记规范.md} 识别碰撞双方、分发到对应逻辑。
 *
 * 分发骨架已按规范（BodyTag）就位；各分支的具体实体逻辑待 Player/Enemy 实现后填充。
 */
public class CollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        BodyTag tagA = (BodyTag) contact.getFixtureA().getUserData();
        BodyTag tagB = (BodyTag) contact.getFixtureB().getUserData();

        // 未标记的 fixture 不参与业务分发
        if (tagA == null || tagB == null) {
            return;
        }

        // 归一化：让 tagA 序号 <= tagB，避免 fixtureA/B 顺序带来的重复判断
        if (tagA.ordinal() > tagB.ordinal()) {
            BodyTag tmp = tagA;
            tagA = tagB;
            tagB = tmp;
        }

        // 碰撞分发表（见 userData标记规范.md §5）
        if (tagA == BodyTag.PLAYER && tagB == BodyTag.GROUND) {
            // 玩家落地 / 贴墙（FR-07）—— 待 Player 实现后填充
        } else if (tagA == BodyTag.PLAYER && tagB == BodyTag.ENEMY) {
            // 玩家与敌人直接接触 -> 受伤（FR-13）；踩怪由 SENSOR_FEET 分支处理
        } else if (tagA == BodyTag.ENEMY && tagB == BodyTag.GROUND) {
            // 敌人贴地 / 碰到平台边缘（FR-09 巡逻转向）
        } else if (tagA == BodyTag.GROUND && tagB == BodyTag.SENSOR_FEET) {
            // 脚部传感器落地 -> 玩家处于地面，可跳跃 / 结束漂浮（FR-04/05）
        } else if (tagA == BodyTag.ENEMY && tagB == BodyTag.SENSOR_FEET) {
            // 脚部传感器碰到敌人 -> 踩怪消灭（FR-12）
        } else if (tagA == BodyTag.ENEMY && tagB == BodyTag.STAR) {
            // 星星命中敌人 -> 消灭（FR-11）
        }
    }

    @Override
    public void endContact(Contact contact) {
        // 接触结束：例如脚部传感器离开地面 -> 玩家进入空中（用于跳跃/漂浮状态）
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // 求解前回调：可通过 contact.setEnabled(false) 关闭本次碰撞（如单向平台/传感器）
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // 求解后回调：可读取碰撞冲量，用于「从上方踩中」等受力判定
    }
}
