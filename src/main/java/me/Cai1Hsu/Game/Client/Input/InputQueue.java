package me.Cai1Hsu.Game.Client.Input;

import java.util.LinkedList;
import java.util.Queue;

// Do not use this directly, use LockedInputQueue instead
public class InputQueue {
    private Queue<InputKeys> _queue;

    public InputQueue() {
        _queue = new LinkedList<InputKeys>();
    }

    public void Enqueue(InputKeys key) {
        _queue.add(key);
    }

    public InputKeys Dequeue() {
        return _queue.poll();
    }

    public boolean IsEmpty() {
        return _queue.isEmpty();
    }

    public void Clear() {
        _queue.clear();
    }
}
