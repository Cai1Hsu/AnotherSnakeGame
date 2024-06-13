package me.Cai1Hsu.Game.Server;

import java.util.List;

import me.Cai1Hsu.Game.Shared.Gameplay.Direction;
import me.Cai1Hsu.Game.Shared.Gameplay.Food;
import me.Cai1Hsu.Game.Shared.Gameplay.PlayerSnake;
import me.Cai1Hsu.Game.Shared.Gameplay.Playfield;
import me.Cai1Hsu.Math.Vector2D;

public abstract class ServerBase implements IServer {
    protected int _frame = 0;
    protected static final int SPAWN_FOOD_TIMER = 10;

    protected float _frameRate = 25;

    protected Playfield _playfield;
    protected int _selfId;
    protected PlayerSnake _self;

    public void onStart(Vector2D fieldSize, float frameRate) {
        _playfield = new Playfield(fieldSize._x, fieldSize._y);
        _selfId = _playfield.joinGame();
        _self = _playfield.getPlayer(_selfId);

        _frameRate = frameRate;
    }

    public Vector2D getFieldSize() {
        return _playfield._size.clone();
    }

    public boolean isGameOver() {
        return _playfield.isGameOver;
    }

    public Direction getSelfDirection() {
        return _self.getDirection();
    }

    // FIXME: Verb
    public void setSelfDirection(Direction direction) {
        _self.setDirection(direction);
    }

    public List<Food> getFoods() {
        return _playfield._foods;
    }

    public List<PlayerSnake> getPlayers() {
        return _playfield._players;
    }

    public PlayerSnake getSelfPlayer() {
        return _self;
    }

    protected void gameUpdate() {
        _frame++;

        _playfield.update();

        if (_frame % SPAWN_FOOD_TIMER == 0) {
            _playfield.spawnFood();
        }

        _playfield.addScore(1 / _frameRate);
    }
}
