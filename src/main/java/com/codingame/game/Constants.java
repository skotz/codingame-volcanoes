package com.codingame.game;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Constants {
    public static int AllGrowMove = 80;

    /// <summary>
    /// An array mapping a source tile index to it's three connecting triangle indexes.
    /// E.G., ConnectingTiles[55] = { 33, 52, 74 } since tile 55 connects to tiles 33, 51, and 74.
    /// </summary>
    public static int[][] AdjacentIndexes = GetConnectingTiles();

    /// <summary>
    /// An array mapping a source tile index to it's tile name.
    /// E.G., TileNames[5] = "N02"
    /// </summary>
    public static String[] TileNames = GetTileNames();

    public static String[] OrderedTileNames = GetOrderedTileNames();

    /// <summary>
    /// FOR BACKWARDS COMPATIBILITY
    /// An array mapping a source tile index to it's tile name.
    /// E.G., TileNames[5] = "2A"
    /// </summary>
    public static String[] OldTileNames = GetOldTileNames();

    /// <summary>
    /// An array mapping a source tile to its antipodes (tile directly opposite on the 3D board shape).
    /// </summary>
    public static int[] Antipodes = GetAntipodes();

    /// <summary>
    /// An array mapping a source tile index to it's three or four kitty corner tile indexes.
    /// NOTE: Currently there are only mappings for "A" tiles (not B, C, or D)
    /// </summary>
    public static int[][] KittyCornerTiles = GetKittyCornerTiles();

    /// <summary>
    /// An HashMap mapping a tile name back to it's index.
    /// </summary>
    public static HashMap<String, Integer> TileIndexes = GetTileIndexes();

    private static int[][] GetConnectingTiles() {
        int[][] connections = new int[80][];

        // First row
        //    .
        //   /B\
        //  / A \   x5
        // /D   C\
        // -------
        for (int outer = 0; outer < 5; outer++) {
            // A
            int i = outer * 4;
            connections[i] = new int[]{i + 1, i + 2, i + 3};

            // B
            i++;
            connections[i] = new int[]{(i - 1 + 20) % 20, (i + 4) % 20, (i - 4 + 20) % 20};

            // C
            i++;
            connections[i] = new int[]{(i - 2 + 20) % 20, i + 21, (i + 5) % 20};

            // D
            i++;
            connections[i] = new int[]{(i - 3 + 20) % 20, i + 19, (i - 5 + 20) % 20};
        }

        // Second row
        // -------
        // \C   D/
        //  \ A /   x5
        //   \B/
        //    .
        for (int outer = 5; outer < 10; outer++) {
            // A
            int i = outer * 4;
            connections[i] = new int[]{i + 1, i + 2, i + 3};

            // B
            i++;
            connections[i] = new int[]{i - 1, i + 22, i + 17};
            connections[i][2] = i == 21 ? 58 : connections[i][2];

            // C
            i++;
            connections[i] = new int[]{i - 2, i - 19, i + 15};
            connections[i][2] = i == 22 ? 57 : connections[i][2];

            // D
            i++;
            connections[i] = new int[]{i - 3, i + 18, i - 21};
        }

        // Third row
        //    .
        //   /B\
        //  / A \   x5
        // /D   C\
        // -------
        for (int outer = 10; outer < 15; outer++) {
            // A
            int i = outer * 4;
            connections[i] = new int[]{i + 1, i + 2, i + 3};

            // B
            i++;
            connections[i] = new int[]{i - 1, i - 18, i - 15};
            connections[i][2] = i == 57 ? 22 : connections[i][2];

            // C
            i++;
            connections[i] = new int[]{i - 2, i - 17, i + 21};
            connections[i][1] = i == 58 ? 21 : connections[i][1];

            // D
            i++;
            connections[i] = new int[]{i - 3, i + 19, i - 22};
        }

        // Fourth row
        // -------
        // \C   D/
        //  \ A /   x5
        //   \B/
        //    .
        for (int outer = 15; outer < 20; outer++) {
            // A
            int i = outer * 4;
            connections[i] = new int[]{i + 1, i + 2, i + 3};

            // B
            i++;
            connections[i] = new int[]{i - 1, i + 4, i - 4};
            connections[i][2] = i == 61 ? 77 : connections[i][2];
            connections[i][1] = i == 77 ? 61 : connections[i][1];

            // C
            i++;
            connections[i] = new int[]{i - 2, i - 19, i - 3};
            connections[i][2] = i == 62 ? 79 : connections[i][2];

            // D
            i++;
            connections[i] = new int[]{i - 3, i + 3, i - 21};
            connections[i][1] = i == 79 ? 62 : connections[i][1];
        }

        return connections;
    }

    private static String[] GetTileNames() {
        return new String[]{
                "N07", "N01", "N08", "N06", "N10", "N02", "N11", "N09", "N13", "N03", "N14", "N12", "N16", "N04", "N17", "N15", "N19", "N05", "N20", "N18", "N22", "S32", "N21", "N23", "N26", "S36", "N25", "N27", "N30", "S40", "N29", "N31", "N34", "S24", "N33", "N35", "N38", "S28", "N37", "N39",
                "S34", "N24", "S35", "S33", "S38", "N28", "S39", "S37", "S22", "N32", "S23", "S21", "S26", "N36", "S27", "S25", "S30", "N40", "S31", "S29", "S16", "S04", "S15", "S17", "S19", "S05", "S18", "S20", "S07", "S01", "S06", "S08", "S10", "S02", "S09", "S11", "S13", "S03", "S12", "S14",
                "G",
        };
    }

    private static String[] GetOrderedTileNames() {
        return new String[]{
                "N01", "N02", "N03", "N04", "N05", "N06", "N07", "N08", "N09", "N10", "N11", "N12", "N13", "N14", "N15", "N16", "N17", "N18", "N19", "N20", "N21", "N22", "N23", "N24", "N25", "N26", "N27", "N28", "N29", "N30", "N31", "N32", "N33", "N34", "N35", "N36", "N37", "N38", "N39", "N40", "S01", "S02", "S03", "S04", "S05", "S06", "S07", "S08", "S09", "S10", "S11", "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20", "S21", "S22", "S23", "S24", "S25", "S26", "S27", "S28", "S29", "S30", "S31", "S32", "S33", "S34", "S35", "S36", "S37", "S38", "S39", "S40"
        };
    }

    private static String[] GetOldTileNames() {
        String[] names = new String[81];

        for (int outer = 0; outer < 20; outer++) {
            for (int inner = 0; inner < 4; inner++) {
                int index = outer * 4 + inner;
                names[index] = Integer.toString(outer + 1);

                switch (inner) {
                    case 0:
                        names[index] += "A";
                        break;

                    case 1:
                        names[index] += "B";
                        break;

                    case 2:
                        names[index] += "C";
                        break;

                    case 3:
                        names[index] += "D";
                        break;
                }
            }
        }

        // For growth
        names[80] = "G";

        return names;
    }

    private static int[] GetAntipodes() {
        int[] antipodes = new int[80];

        for (int i = 0; i < 12; i++) {
            int a = 68;
            a = i % 4 == 2 ? 69 : a;
            a = i % 4 == 3 ? 67 : a;

            antipodes[i] = i + a;
            antipodes[i + a] = i;
        }

        for (int i = 12; i < 20; i++) {
            int a = 48;
            a = i % 4 == 2 ? 49 : a;
            a = i % 4 == 3 ? 47 : a;

            antipodes[i] = i + a;
            antipodes[i + a] = i;
        }

        for (int i = 20; i < 32; i++) {
            int a = 28;
            a = i % 4 == 2 ? 29 : a;
            a = i % 4 == 3 ? 27 : a;

            antipodes[i] = i + a;
            antipodes[i + a] = i;
        }

        for (int i = 32; i < 40; i++) {
            int a = 8;
            a = i % 4 == 2 ? 9 : a;
            a = i % 4 == 3 ? 7 : a;

            antipodes[i] = i + a;
            antipodes[i + a] = i;
        }

        return antipodes;
    }

    private static int[][] GetKittyCornerTiles() {
        int[][] corners = new int[80][];

        for (int outer = 0; outer < 5; outer++) {
            // A
            int i = outer * 4;
            corners[i] = new int[]{(i + 4) % 20, i + 20, (i - 4 + 20) % 20};

            // TODO: B, C, D
            i++;
            corners[i] = new int[]{i, i, i};
            i++;
            corners[i] = new int[]{i, i, i};
            i++;
            corners[i] = new int[]{i, i, i};
        }

        for (int outer = 5; outer < 10; outer++) {
            // A
            int i = outer * 4;
            corners[i] = new int[]{i - 20, i + 20, i + 16};
            corners[i][2] = i == 20 ? 56 : corners[i][2];

            // TODO: B, C, D
            i++;
            corners[i] = new int[]{i, i, i};
            i++;
            corners[i] = new int[]{i, i, i};
            i++;
            corners[i] = new int[]{i, i, i};
        }

        for (int outer = 10; outer < 15; outer++) {
            // A
            int i = outer * 4;
            corners[i] = new int[]{i - 20, i - 16, i + 20};
            corners[i][1] = i == 56 ? 20 : corners[i][1];

            // TODO: B, C, D
            i++;
            corners[i] = new int[]{i, i, i};
            i++;
            corners[i] = new int[]{i, i, i};
            i++;
            corners[i] = new int[]{i, i, i};
        }

        for (int outer = 15; outer < 20; outer++) {
            // A
            int i = outer * 4;
            corners[i] = new int[]{i - 20, i + 4, i - 4};
            corners[i][1] = i == 76 ? 60 : corners[i][1];
            corners[i][2] = i == 60 ? 76 : corners[i][2];

            // TODO: B, C, D
            i++;
            corners[i] = new int[]{i, i, i};
            i++;
            corners[i] = new int[]{i, i, i};
            i++;
            corners[i] = new int[]{i, i, i};
        }

        return corners;
    }

    private static HashMap<String, Integer> GetTileIndexes() {
        HashMap<String, Integer> indexes = new HashMap<String, Integer>();

        // The 80th index is a pseudo tile for the growth phase
        for (int i = 0; i < 81; i++) {
            indexes.put(TileNames[i], i);
        }

        return indexes;
    }
}