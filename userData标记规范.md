# Box2D `userData` 标记规范

| 项目 | 内容 |
| --- | --- |
| 状态 | 定稿 |
| 适用范围 | 所有 Box2D 刚体（Body）/ 夹具（Fixture）的 `userData` 用法 |
| 关联代码 | `PhysicsWorld`、`CollisionHandler`、`Player`、`Enemy`、`EntityFactory` |

---

## 1. 目的

统一 Box2D 中 `Body` / `Fixture` 的 `userData` 用法，让 `CollisionHandler`（`ContactListener`）能**稳定、低耦合**地识别「碰撞双方是谁」，从而把碰撞事件正确分发到对应逻辑（落地、踩怪、受伤、吐星等，对应 FR-07 / FR-11~13）。

---

## 2. 核心约定：两级标记

| 级别 | 放什么 | 用途 |
| --- | --- | --- |
| **Fixture.userData** | 枚举 `BodyTag`（见 §3） | 标记该碰撞体的「角色类型」，用于碰撞识别 |
| **Body.userData** | 对应的游戏实体对象 | 需要操作具体实体时取出（`Player` / `Enemy` 实例） |

> **`userData` 分两级 —— 夹具上放「类型标签」，刚体上放「实体引用」。**

---

## 3. 类型枚举 `BodyTag`

放在 `com.kirby.game.constants` 包，新建 `BodyTag.java`：

```java
public enum BodyTag {
    PLAYER,      // 卡比本体
    ENEMY,       // 敌人本体
    GROUND,      // 地形（地面/墙壁，来自 Tiled 静态刚体）
    SENSOR_FEET, // 脚部传感器：同时用于「判定在地面」与「踩怪」
    STAR         // 吐出的星星弹丸（FR-11，后期）
}
```

> 吸入（FR-10）不建传感器，用每帧「朝向 + 距离 + 角度」检测，故不在枚举中。

---

## 4. 使用规则

1. **创建刚体时必须设 `userData`**，创建者统一由 `EntityFactory` 负责：
   - 玩家 body → `body.setUserData(player)`；主 fixture → `fixture.setUserData(BodyTag.PLAYER)`。
   - 敌人同理 → `BodyTag.ENEMY`。
   - 地形（由 Tiled 生成）→ fixture 标 `BodyTag.GROUND`，body 的 userData 可为 `null`。
2. **`CollisionHandler` 的识别流程**（在 `beginContact` / `endContact` 内）：
   1. 取两个 fixture 的 `getUserData()`，得到 `BodyTag`；
   2. 按「双方 tag 组合」查分发表（§5）决定走哪段逻辑；
   3. 需要操作实体时，再通过 `body.getUserData()` 取 `Player` / `Enemy` 实例。
3. **禁止运行时 `instanceof` 链判断类型**，统一用枚举 tag 比较——更快、更清晰、易扩展。

---

## 5. 碰撞分发表

| Fixture A | Fixture B | 处理逻辑 | 关联需求 |
| --- | --- | --- | --- |
| PLAYER | GROUND | 玩家落地 / 贴墙 | FR-07 |
| PLAYER | ENEMY | 直接接触 → 受伤（踩怪走 SENSOR_FEET） | FR-13 |
| ENEMY | GROUND | 敌人贴地（巡逻边界可在此转向） | FR-09 |
| SENSOR_FEET | GROUND | 玩家处于地面（控制跳跃/漂浮） | FR-04 / FR-05 |
| SENSOR_FEET | ENEMY | 踩怪消灭 | FR-12 |
| STAR | ENEMY | 星星命中敌人 → 消灭 | FR-11 |

---

## 6. 与 `categoryBits` / `maskBits` 的分工

两层配合，各管一件事：

- **filter（`categoryBits` / `maskBits`）** → 管「**碰不碰**」：传感器不产生物理碰撞、星星只和敌人/地形碰撞等，用位掩码过滤，性能最好。
- **`userData` 的 `BodyTag`** → 管「**碰的是谁**」：在已经发生的接触里做身份识别与逻辑分发。

即：`filter` 先挡掉不该发生的接触，`userData` 再对「真正发生的接触」做业务判断。

---

## 7. 已确认结论

1. **枚举名**：`BodyTag`（避免与 Box2D 自带 `BodyType` 混淆）。
2. **踩怪判定**：用脚部传感器 `SENSOR_FEET`，一个传感器同时解决「判定在地面」与「踩怪」。
3. **吸入范围**：每帧「朝向 + 距离 + 角度」检测，不建传感器。
4. **地形刚体**：每个实心 tile 一个 fixture，统一挂在一个 static body 上。
