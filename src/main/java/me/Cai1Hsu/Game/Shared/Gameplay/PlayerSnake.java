package me.Cai1Hsu.Game.Shared.Gameplay;

import java.util.LinkedList;

import me.Cai1Hsu.Math.Vector2D;

public class PlayerSnake {
    public static final int ESSENTIAL_LENGTH = 5;
    public static final int INIT_LENGTH = 15;

    public LinkedList<Body> _bodies = new LinkedList<>();
    public Direction _direction;
    public float score = 0;

    private int _id;
    private Vector2D _directionVector;
    private Playfield _playfield;

    private int collectedFragment = 0;
    public static final int FRAGMENT_TO_GROW = 3;

    public Direction getDirection() {
        return _direction;
    }

    public int getId() {
        return _id;
    }

    public int getFragment() {
        return collectedFragment;
    }

    public PlayerSnake(Playfield playfield, Vector2D initPos, Direction initDirection, int id) {
        this._playfield = playfield;
        this.setDirection(initDirection);
        this._id = id;

        for (int i = 0; i < INIT_LENGTH; i++) {
            // Reverse the list.
            _bodies.add(new Body(initPos));
        }
    }

    public void setDirection(Direction direction) {
        _direction = direction;

        switch (direction) {
            case UP:
                _directionVector = new Vector2D(0, -1);
                break;
            case DOWN:
                _directionVector = new Vector2D(0, 1);
                break;
            case LEFT:
                _directionVector = new Vector2D(-1, 0);
                break;
            case RIGHT:
                _directionVector = new Vector2D(1, 0);
                break;
        }
    }

    // Called on every frame
    public void onUpdate() {
        Body head = _bodies.getFirst();

        Vector2D nextPos = head._position.clone().plus(_directionVector);

        if (_playfield.testCollideWall(nextPos)) {
            _playfield.reportFail(_id);

            return;
        }

        var eat = _playfield.tryEatFood(nextPos, true);
        if (eat != null) {
            switch (eat.getType()) {
                case Poison:
                    collectedFragment = 0;
                    _playfield.reportFail(_id);
                    break;
                case Medium:
                    score += 20;
                    collectedFragment += 3;
                    break;
                case Small:
                    collectedFragment += 1;
                    score += 5;
                    break;

                default:
                    break;
            }

            if (collectedFragment >= FRAGMENT_TO_GROW) {
                collectedFragment -= FRAGMENT_TO_GROW;
                var tail = _bodies.getLast();
                var newTail = new Body(tail._position);
                _bodies.addLast(newTail);
            }
        }

        // move forward
        var next_pos = nextPos.clone();
        for (var b : this._bodies) {
            var t = b._position;
            b._position = next_pos;
            next_pos = t;
        }

        int eatSelfIdx = testEatSelf();
        if (eatSelfIdx != -1) {
            this.cutOffAt(eatSelfIdx);
            score = Math.max(score - 50, 0);
        }

        // Eat other player would not grow, but would not die either
        _playfield.actionOtherPlayers(p -> {
            int idx = 0;
            for (var b : p._bodies) {
                if (b._position.equals(nextPos)) {
                    p.cutOffAt(idx);

                    // Got 50 points for eating others
                    this.score += 50;

                    return;
                }

                idx++;
            }
        }, this);

        if (_bodies.size() < ESSENTIAL_LENGTH) {
            _playfield.reportFail(_id);
        }
    }

    private void cutOffAt(int idx) {
        var len = _bodies.size();

        // Goodbye. Go and see GC
        _bodies.subList(idx, len).clear();
    }

    private int testEatSelf() {
        var it = _bodies.iterator();
        var len = _bodies.size();

        var head = it.next();

        for (int i = 1; i < len; i++) {
            var b = it.next();

            if (b._position.equals(head._position))
                return i;
        }

        return -1;
    }
}
