package com.codingame.game;

import java.util.*;

public class PathFinder {
    public PathFinder() {
    }

    public int[] GetNeighborTiles(Board state, int tileIndex) {
        return Constants.AdjacentIndexes[tileIndex];
    }

    public boolean IsTraversableTile(Board state, PlayerType player, int tileIndex) {
        // Ignore tiles that aren't the player's
        if ((state.Tiles[tileIndex] > 0 && player != PlayerType.One) || (state.Tiles[tileIndex] < 0 && player != PlayerType.Two)) {
            return false;
        }

        // Ignore magma chambers
        if (Math.abs(state.Tiles[tileIndex]) <= Settings.MaxMagmaChamberLevel) {
            return false;
        }

        return true;
    }

    public int GetDistance(Board state, int first, int second) {
        return 1;
    }

    public boolean HasPath(Board state, int startingIndex, int endingIndex) {
        return FindPath(state, startingIndex, endingIndex, false).Found;
    }

    public PathResult FindPath(Board state, int startingIndex, int endingIndex) {
        return FindPath(state, startingIndex, endingIndex, true);
    }

    private PathResult FindPath(Board state, int startingIndex, int endingIndex, boolean returnPath) {
        // The set of nodes already evaluated
        boolean[] closedSet = new boolean[80];

        PlayerType player = state.Tiles[startingIndex] == 0 ? PlayerType.Empty : (state.Tiles[startingIndex] > 0 ? PlayerType.One : PlayerType.Two);

        // The set of currently discovered nodes that are not evaluated yet.
        // Initially, only the start node is known.
        Queue<Integer> openSet = new LinkedList<>();
        openSet.add(startingIndex);

        // For each node, which node it can most efficiently be reached from.
        // If a node can be reached from many nodes, cameFrom will eventually contain the
        // most efficient previous step.
        int[] cameFrom = new int[80];

        // For each node, the cost of getting from the start node to that node.
        int[] gScore = new int[80];

        for (int i = 0; i < 80; i++) {
            cameFrom[i] = -1;
            gScore[i] = Integer.MAX_VALUE;
        }

        // The cost of going from start to start is zero.
        gScore[startingIndex] = 0;

        //// For each node, the total cost of getting from the start node to the goal
        //// by passing by that node. That value is partly known, partly heuristic.
        //int[] fScore = new int[80];
        //for (int i = 0; i < 80; i++)
        //{
        //    fScore[i] = int.MaxValue;
        //}

        //// For the first node, that value is completely heuristic. (All antipode paths are known to be 12 tiles long.)
        //fScore[startingIndex] = 12;

        while (openSet.size() > 0) {
            // Get the next item in the open set with the lowest fScore
            int current = openSet.remove();
            //int best = fScore[current];
            //foreach (int i in openSet)
            //{
            //    if (fScore[i] < best)
            //    {
            //        current = i;
            //        best = fScore[i];
            //    }
            //}

            // If we found a path from the start to the end, reconstruct the path and return it
            if (current == endingIndex) {
                if (returnPath) {
                    List<Integer> path = new ArrayList<Integer>();
                    path.add(current);

                    while (cameFrom[current] != -1) {
                        current = cameFrom[current];
                        path.add(current);
                    }

                    Collections.reverse(path);

                    return new PathResult(path, gScore[endingIndex]);
                } else {
                    // Just return that we found a path, don't bother piecing it together
                    return new PathResult(true);
                }
            }

            //openSet.Remove(current);
            closedSet[current] = true;

            for (int neighbor : GetNeighborTiles(state, current)) {
                // Ignore the neighbor which is already evaluated.
                if (closedSet[neighbor]) {
                    continue;
                }

                if (!IsTraversableTile(state, player, neighbor)) {
                    continue;
                }

                // The distance from start to a neighbor
                int tentative_gScore = gScore[current] + GetDistance(state, current, neighbor);

                // Discover a new node
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else if (tentative_gScore >= gScore[neighbor]) {
                    continue;
                }

                // This path is the best until now. Record it!
                cameFrom[neighbor] = current;
                gScore[neighbor] = tentative_gScore;
                //fScore[neighbor] = gScore[neighbor] /* + Math.Abs(neighbor - startingIndex) */;
            }
        }

        return new PathResult();
    }
}