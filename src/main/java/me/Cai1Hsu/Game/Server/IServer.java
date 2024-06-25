package me.Cai1Hsu.Game.Server;

import java.time.Duration;
import java.util.List;

import me.Cai1Hsu.Game.Client.GameHost;
import me.Cai1Hsu.Game.Shared.Gameplay.Direction;
import me.Cai1Hsu.Game.Shared.Gameplay.Food;
import me.Cai1Hsu.Game.Shared.Gameplay.PlayerSnake;
import me.Cai1Hsu.Math.Vector2D;

public interface IServer {
    void onStart(GameHost host, Vector2D fieldSize, float frameRate);

    void connectServer();

    void onUpdate(Duration delta);

    boolean swapMessage();

    void onEnd();

    String getServerIp();

    int getPing();

    Vector2D getFieldSize();

    boolean isGameOver();

    Direction getSelfDirection();
    void setSelfDirection(Direction direction);

    List<Food> getFoods();

    List<PlayerSnake> getPlayers();

    PlayerSnake getSelfPlayer();
}
