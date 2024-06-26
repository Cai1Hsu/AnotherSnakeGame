package me.Cai1Hsu.Game.Client;

import java.io.IOException;
import java.time.Duration;

import me.Cai1Hsu.Game.Client.Input.InputThread;
import me.Cai1Hsu.Game.Client.Input.LockedInputQueue;
import me.Cai1Hsu.Game.Server.IServer;
import me.Cai1Hsu.Game.Shared.Gameplay.Body;
import me.Cai1Hsu.Game.Shared.Gameplay.Direction;
import me.Cai1Hsu.Game.Shared.Gameplay.Food;
import me.Cai1Hsu.Game.Shared.Gameplay.FoodType;
import me.Cai1Hsu.Game.Shared.Gameplay.PlayerSnake;
import me.Cai1Hsu.Math.Vector2D;
import me.Cai1Hsu.Game.Client.Graphics.Canvas;
import me.Cai1Hsu.Game.Client.Graphics.Color;

public class GameHost {
    private boolean _running = true;
    private InputThread _inputThread;
    private LockedInputQueue _inputQueue;

    public Canvas _canvas;

    public static float FRAME_RATE = 10.0f;

    private IServer _server;

    private boolean connected = false;
    private int _localframe = 0;

    public GameHost(IServer server) {
        _inputQueue = new LockedInputQueue();
        _server = server;
    }

    public void requestClose() {
        _running = false;
    }

    private void registerThreads() throws IOException {
        _inputThread = new InputThread(_inputQueue);
        _inputThread.Register();
    }

    private void unregisterThreads() {
        _inputThread.Quit();
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

        _server.onEnd();
        unregisterThreads();
    }

    private void mainLoop() {
        _server.onStart(this, new Vector2D(40, 25), FRAME_RATE);

        var _canvasSize = _server.getFieldSize();
        _canvasSize._x = 2 * _canvasSize._x + 2;
        _canvasSize._y += 2; // Add one more line for the score.
        _canvas = new Canvas(_canvasSize);

        prepareDraw();

        while (_running) {
            if (connected) {
                handleInput();
    
                updateGame();
            }

            renderFeild();

            if (_server instanceof me.Cai1Hsu.Game.Server.LocalServer) {
                drawCanvas(_canvas.Render());
            } 

            if (!connected) {
                _server.connectServer();
                connected = true;
            }
        }

        cleanupDraw();
    }

    private void prepareDraw() {
        // Hide the cursor.
        System.out.print("\033[?25l");

        // Clear the screen.
        System.out.print("\033[2J");
    }

    private void cleanupDraw() {
        // Show the cursor.
        System.out.print("\033[?25h");
    }

    private void handleInput() {
        var queue = _inputQueue.LockedAccess();
        {
            Direction cur;
            Direction old = cur = _server.getSelfDirection();

            while (!queue.IsEmpty()) {
                var key = queue.Dequeue();

                switch (key) {
                    case UpArrow:
                        if (old != Direction.DOWN)
                            cur = Direction.UP;
                        break;

                    case DownArrow:
                        if (old != Direction.UP)
                            cur = Direction.DOWN;
                        break;

                    case LeftArrow:
                        if (old != Direction.RIGHT)
                            cur = Direction.LEFT;
                        break;

                    case RightArrow:
                        if (old != Direction.LEFT)
                            cur = Direction.RIGHT;
                        break;

                    default:
                        break;
                }
            }
            _server.setSelfDirection(cur);

            // Clear at the end of the frame.
            queue.Clear();
        }
        _inputQueue.Release();
    }

    private void updateGame() {
        if (_server.isGameOver()) {
            return;
        }

        // TODO: Calculate the delta time.
        _server.onUpdate(Duration.ofMillis(1000 / (long) FRAME_RATE));

        if (_server.isGameOver()) {
            _running = false;
        }
    }

    private void renderFeild() {
        _localframe ++;
        _canvas.clearScreen();

        if (connected) {
            // Draw food
            for (var f : _server.getFoods()) {
                var pos = asCanvasPosition(f.getPosition());
                var type = f.getType();
    
                var color = type == FoodType.Small ? Food.SMALL_FOOD_COLOR
                        : type == FoodType.Medium ? Food.MEDIUM_FOOD_COLOR
                                : Food.POISON_FOOD_COLOR;
    
                _canvas.drawSquare(pos, color);
            }
    
            // Draw player
            for (var p : _server.getPlayers()) {
                var idx = p._bodies.size() - 1;
                var it = p._bodies.descendingIterator();
                while (it.hasNext()) {
                    var b = it.next();
                    var pos = asCanvasPosition(b._position);
    
                    var color = idx == 0 ? Body.SELF_HEAD_COLOR
                            : idx < 5 ? Body.ESSENTIAL_COLOR
                                    : Body.BODY_COLOR;
    
                    _canvas.drawSquare(pos, color);
    
                    idx--;
                }
            }
        }

        // UIs should be drawn at the top.
        _canvas.fillRow(0, '-', Canvas.DEFAULT_TEXT_COLOR);
        _canvas.fillRow(-1, '-', Canvas.DEFAULT_TEXT_COLOR);
        _canvas.fillColumn(0, '|', Canvas.DEFAULT_TEXT_COLOR);
        _canvas.fillColumn(-1, '|', Canvas.DEFAULT_TEXT_COLOR);

        _canvas.drawChar(0, 0, '+');
        _canvas.drawChar(-1, 0, '+');
        _canvas.drawChar(0, -1, '+');
        _canvas.drawChar(-1, -1, '+');

        var ping = _server.getPing();
        var netColor = ping < 100 ? GOOD_CONNECTION
                : ping < 200 ? POOR_CONNECTION
                        : BAD_CONNECTION;

        var netInfo = " Server: %s | Ping: %d ms ".formatted(_server.getServerIp(), ping);

        _canvas.drawTextCentered(-1, netInfo, netColor);

        if (!connected) {
            _canvas.drawTextCentered(10, "Connecting" + ".".repeat(connecting_dot));

            if (_localframe % (FRAME_RATE / 3) == 0) {
                connecting_dot = (connecting_dot + 1) % 4;
            }

            return;
        }

        var selfPlayer = _server.getSelfPlayer();

        var frag = selfPlayer.getFragment();
        var frag_str = "[X]".repeat(frag) + "[ ]".repeat(PlayerSnake.FRAGMENT_TO_GROW - frag);
        _canvas.drawText(2, 0,
                " Score: %.0f | Length: %d | Fragment: %s ".formatted(selfPlayer.score, selfPlayer._bodies.size(),
                        frag_str));

        if (_server.isGameOver()) {
            _canvas.drawTextCentered(10, ":(");
            _canvas.drawTextCentered(12, "Game Over!");
            _canvas.drawTextCentered(14, "Score: %.0f".formatted(selfPlayer.score));
        }
    }

    private int connecting_dot = 0;

    private static final Color GOOD_CONNECTION = new Color(2, -1);
    private static final Color POOR_CONNECTION = new Color(3, -1);
    private static final Color BAD_CONNECTION = new Color(1, -1);

    private Vector2D asCanvasPosition(Vector2D pos) {
        return new Vector2D(2 * pos._x + 1, pos._y + 1);
    }

    public void drawCanvas(String str) {
        System.out.print("\033[0;0H");
        System.out.print(str);
    }
}
