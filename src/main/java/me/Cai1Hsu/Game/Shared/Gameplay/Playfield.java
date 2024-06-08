package me.Cai1Hsu.Game.Shared.Gameplay;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

import me.Cai1Hsu.Math.Vector2D;

public class Playfield {
    public Vector2D _size;

    public ArrayList<Food> _foods = new ArrayList<>();
    public ArrayList<PlayerSnake> _players = new ArrayList<>();

    public boolean isGameOver = false;

    // FIXME: This is hard to sync between server and client.
    private RandomGenerator _rng = RandomGenerator.getDefault();

    public PlayerSnake getPlayer(int id) {
        for (PlayerSnake player : _players) {
            if (player.getId() == id) {
                return player;
            }
        }

        return null;
    }

    public void update() {
        for (PlayerSnake player : _players) {
            player.onUpdate();
        }
    }

    public int joinGame() {
        // Pick a start position and init direction randomly
        int id = _players.size();

        int x = _rng.nextInt(4, _size._x - 5);
        int y = _rng.nextInt(4, _size._y - 5);
        Vector2D initPos = new Vector2D(x, y);

        Direction initDirection = Direction.values()[_rng.nextInt(0, 3)];

        PlayerSnake player = new PlayerSnake(this, initPos, initDirection, id);
        _players.add(player);

        return id;
    }

    public void actionOtherPlayers(Consumer<PlayerSnake> consumer, PlayerSnake self) {
        int thisId = self.getId();

        for (PlayerSnake player : _players) {
            if (player.getId() == thisId) {
                continue;
            }

            consumer.accept(player);
        }
    }

    public void reportFail(int playerId) {
        isGameOver = true;
    }

    public Food tryEatFood(Vector2D hPos, boolean removeOnEat) {
        int idx = 0;
        for (Food f : _foods) {
            if (f.testCollision(hPos)) {

                if (removeOnEat)
                    _foods.remove(idx);

                return f;
            }

            idx++;
        }

        return null;
    }

    public boolean testCollideWall(Vector2D hPos) {
        if (hPos._x < 0 || hPos._x >= _size._x) {
            return true;
        }

        if (hPos._y < 0 || hPos._y >= _size._y) {
            return true;
        }

        return false;
    }

    private boolean testCollidePlayer(Vector2D pos) {
        for (var p : _players) {
            for (var b : p._bodies) {
                if (b._position.equals(pos))
                    return true;
            }
        }

        return false;
    }

    private Vector2D getRandomValidPosition() {
        Vector2D pos = new Vector2D(0, 0);

        do {
            pos._x = _rng.nextInt(0, _size._x);
            pos._y = _rng.nextInt(0, _size._y);
        } while (tryEatFood(pos, false) != null || testCollidePlayer(pos));

        return pos;
    }

    public void spawnFood() {
        var pos = getRandomValidPosition();
        var type_factor = _rng.nextInt(0, 100);

        FoodType type = FoodType.Small;
        if (type_factor < 5) {
            type = FoodType.Poison;
        } else if (type_factor < 35) {
            type = FoodType.Medium;
        }

        Food food = new Food(type, pos);
        _foods.add(food);
    }

    public void spawnFoodAt(Food food) {
        _foods.add(food);
    }

    // Factory method used for player connection
    public static Playfield fromFeilds(Vector2D size, ArrayList<PlayerSnake> players) {
        Playfield playfield = new Playfield(size._x, size._y);
        playfield._players = players;

        return playfield;
    }

    public Playfield(int width, int height) {
        assert width > 20 && height > 20;

        _size = new Vector2D(width, height);
    }

    public void addScore(float score) {
        for (PlayerSnake player : _players) {
            player.score += score;
        }
    }
}
