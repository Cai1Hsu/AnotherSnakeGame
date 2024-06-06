package me.Cai1Hsu.Game.Client.Input;

import java.io.IOException;
import java.io.InputStream;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class InputThread {
    private Thread _inner;
    private boolean _running = false;
    private boolean _exited = false;
    private LockedInputQueue _queue;
    private Terminal _terminal;
    private InputStream _stdin;

    public InputThread(LockedInputQueue queue) throws IOException {
        _queue = queue;
        _inner = new Thread(() -> pollInputs());

        _terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        _terminal.enterRawMode();

        _stdin = _terminal.input();
    }

    public void Register() {
        _running = true;
        _exited = false;

        var queue = _queue.LockedAccess();
        {
            queue.Clear();
        }
        _queue.Release();

        _inner.setName("InputThread");
        _inner.setDaemon(true);
        _inner.start();
    }

    public LockedInputQueue getQueue() {
        return _queue;
    }

    public boolean isStarted() {
        return _running;
    }

    public boolean isExited() {
        return _exited;
    }

    public void Unregister() {
        _running = false;
        while (!_exited) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException _) {
                // Ignore
            }
        }
    }

    private InputKeys translateKey(char key) {
        switch (key) {
            case 'w':
                return InputKeys.UpArrow;
            case 's':
                return InputKeys.DownArrow;
            case 'a':
                return InputKeys.LeftArrow;
            case 'd':
                return InputKeys.RightArrow;
            default:
                return InputKeys.Unrecoginzed;
        }
    }

    private void pollInputs() {
        try {
            while (_running) {
                var key = (char) _stdin.read();

                if (key == -1) {
                    continue;
                }

                var input_key = translateKey(key);

                if (input_key == InputKeys.Unrecoginzed) {
                    continue;
                }

                var queue = _queue.LockedAccess();
                {
                    queue.Enqueue(input_key);
                }
                _queue.Release();
            }
        } catch (IOException _) {
            _exited = true;
        }
    }
}
