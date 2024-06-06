package me.Cai1Hsu.Game.Client;

import java.io.IOException;

import me.Cai1Hsu.Game.Client.Input.InputThread;
import me.Cai1Hsu.Game.Client.Input.LockedInputQueue;
import me.Cai1Hsu.Game.Shared.Gameplay.Body;
import me.Cai1Hsu.Game.Shared.Gameplay.Direction;
import me.Cai1Hsu.Game.Shared.Gameplay.Food;
import me.Cai1Hsu.Game.Shared.Gameplay.FoodType;
import me.Cai1Hsu.Game.Shared.Gameplay.PlayerSnake;
import me.Cai1Hsu.Game.Shared.Gameplay.Playfield;
import me.Cai1Hsu.Math.Vector2D;
import me.Cai1Hsu.Game.Client.Graphics.Canvas;
import me.Cai1Hsu.Game.Client.Input.InputKeys;

public class GameHost {
    private boolean _running = true;
    private InputThread _inputThread;
    private LockedInputQueue _inputQueue;

    private Playfield _playfield;
    private int _selfId;
    private PlayerSnake _self;

    private Canvas _canvas;
    private int _score = 0;

    private int _frame = 0;
    private static final int SPAWN_FOOD_TIMER = 5;

    public GameHost() {
        // TODO
        _inputQueue = new LockedInputQueue();
    }

    public void requestClose() {
        _running = false;
    }

    private void registerThreads() throws IOException {
        _inputThread = new InputThread(_inputQueue);
        _inputThread.Register();
    }

    private void unregisterThreads() {
        _inputThread.Unregister();
    }

    public void runMainLoop() {
        try {
            registerThreads();
        } catch (IOException e) {
            e.printStackTrace();

            return;
        }

        try {
            mainLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        unregisterThreads();
    }

    private void mainLoop() {
        var fieldSize = new Vector2D(40, 25);
        _playfield = new Playfield(fieldSize._x, fieldSize._y);
        _selfId = _playfield.joinGame();
        _self = _playfield.getPlayer(_selfId);

        var _canvasSize = Vector2D.clone(fieldSize);
        _canvasSize._x = 2 * _canvasSize._x + 2;
        _canvasSize._y += 2; // Add one more line for the score.
        _canvas = new Canvas(_canvasSize);

        prepareDraw();

        while (_running) {
            try {
                // Simulate 60 FPS.
                Thread.sleep(1000 / 25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handleInput();

            updateGame();
            renderFeild();
            drawCanvas();
        }
    }

    private void prepareDraw() {
        // Hide the cursor.
        System.out.print("\033[?25l");

        // Clear the screen.
        System.out.print("\033[2J");
    }

    private void handleInput() {
        var queue = _inputQueue.LockedAccess();
        {
            while (!queue.IsEmpty()) {
                var key = queue.Dequeue();

                Direction cur;
                Direction old = cur = _self.getDirection();

                switch (key) {
                    case InputKeys.UpArrow:
                        if (old != Direction.DOWN)
                            cur = Direction.UP;
                        break;

                    case InputKeys.DownArrow:
                        if (old != Direction.UP)
                            cur = Direction.DOWN;
                        break;

                    case InputKeys.LeftArrow:
                        if (old != Direction.RIGHT)
                            cur = Direction.LEFT;
                        break;

                    case InputKeys.RightArrow:
                        if (old != Direction.LEFT)
                            cur = Direction.RIGHT;
                        break;

                    default:
                        break;
                }

                _self.setDirection(cur);
            }

            // Clear at the end of the frame.
            queue.Clear();
        }
        _inputQueue.Release();
    }

    private void updateGame() {
        _frame++;

        _playfield.update();

        if (_frame % SPAWN_FOOD_TIMER == 0) {
            _playfield.spawnFood();
        }
    }

    private void renderFeild() {
        _canvas.clearScreen();

        if (_playfield.isGameOver) {
            _canvas.drawText(10, 10, "Game Over!");
            _canvas.drawText(10, 12, String.format("Score: %d", _score));
            return;
        }

        // Let's assume that the frame is the score.
        _score++;

        // Draw food
        for (var f : _playfield._foods) {
            var pos = asCanvasPosition(f.getPosition());
            var type = f.getType();

            var color = type == FoodType.Small ? Food.SMALL_FOOD_COLOR
                    : type == FoodType.Medium ? Food.MEDIUM_FOOD_COLOR
                            : Food.POISON_FOOD_COLOR;

            _canvas.drawSquare(pos, color);
        }

        // Draw player
        for (var p : _playfield._players) {
            for (var b : p._bodies) {
                var pos = asCanvasPosition(b._position);

                var bIdx = b.getIdx();
                var color = bIdx == 0 ? Body.SELF_HEAD_COLOR
                        : bIdx < 5 ? Body.ESSENTIAL_COLOR
                                : Body.BODY_COLOR;

                _canvas.drawSquare(pos, color);
            }
        }

        // UIs should be drawn at the top.
        _canvas.fillRow(0, '-', Canvas.DEFAULT_COLOR);
        _canvas.fillRow(-1, '-', Canvas.DEFAULT_COLOR);
        _canvas.fillColumn(0, '|', Canvas.DEFAULT_COLOR);
        _canvas.fillColumn(-1, '|', Canvas.DEFAULT_COLOR);

        _canvas.drawChar(0, 0, '+');
        _canvas.drawChar(-1, 0, '+');
        _canvas.drawChar(0, -1, '+');
        _canvas.drawChar(-1, -1, '+');
        _canvas.drawText(2, 0, String.format(" Score: %d | Length: %d ", _score, _self._bodies.size()));
    }

    private Vector2D asCanvasPosition(Vector2D pos) {
        return new Vector2D(2 * pos._x + 1, pos._y + 1);
    }

    private void drawCanvas() {
        var str = _canvas.Build();
        System.out.print("\033[0;0H");
        System.out.print(str);
    }
}