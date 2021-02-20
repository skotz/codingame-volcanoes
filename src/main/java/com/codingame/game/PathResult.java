package com.codingame.game;

import java.util.ArrayList;
import java.util.List;

public class PathResult {
    public List<Integer> Path;
    public int Distance;
    public boolean Found;

    public PathResult() {
        this(new ArrayList<>());
    }

    public PathResult(List<Integer> path) {
        this(path, path.size());
    }

    public PathResult(List<Integer> path, int distance) {
        Path = path;
        Distance = distance;
        Found = Path != null && Path.size() > 0;
    }

    public PathResult(boolean found) {
        Found = found;
    }
}
