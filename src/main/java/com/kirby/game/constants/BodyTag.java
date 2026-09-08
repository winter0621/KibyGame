package com.kirby.game.constants;

/**
 * Box2D 碰撞体角色类型标记（见 userData标记规范.md）。
 * 挂在 Fixture 的 userData 上，供 CollisionHandler 识别碰撞双方。
 */
public enum BodyTag {
    PLAYER,      // 卡比本体
    ENEMY,       // 敌人本体
    GROUND,      // 地形（地面/墙壁，来自 Tiled 静态刚体）
    SENSOR_FEET, // 脚部传感器：同时用于「判定在地面」与「踩怪」
    STAR         // 吐出的星星弹丸（后期）
}
