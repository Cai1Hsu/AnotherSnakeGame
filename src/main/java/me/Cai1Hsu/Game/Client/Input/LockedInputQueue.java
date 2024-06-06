package me.Cai1Hsu.Game.Client.Input;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockedInputQueue {
    private InputQueue _queue;
    private Lock _lock;

    public LockedInputQueue() {
        _queue = new InputQueue();
        _lock = new ReentrantLock();
    }

    public InputQueue LockedAccess() {
        _lock.lock();
        return _queue;
    }

    public void Release() {
        _lock.unlock();
    }
}
