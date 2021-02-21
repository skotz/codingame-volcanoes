package com.codingame.game;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;

public class Board {
    public int[] Tiles;
    public boolean[] Dormant;
    public PlayerType Player;
    public int Turn;

    public PlayerType Winner;
    public List<Integer> WinningPathPlayerOne;
    public List<Integer> WinningPathPlayerTwo;

    public boolean LastMoveIncreasedTile;

    public String Transcript;

    private static PathFinder pathFinder = new PathFinder();

    public boolean fastWinSearch;

    public GameState State() {
        return Winner == PlayerType.Empty ? GameState.InProgress : GameState.GameOver;
    }

    public Board() {
        Turn = 1;
        Player = PlayerType.One;
        Tiles = new int[80];
        Dormant = new boolean[80];
        Winner = PlayerType.Empty;
        WinningPathPlayerOne = new ArrayList<Integer>();
        WinningPathPlayerTwo = new ArrayList<Integer>();
    }

    public Board(Board copy) {
        Tiles = copy.Tiles.clone();
        Dormant = copy.Dormant.clone();
        Player = copy.Player;
        Turn = copy.Turn;
        Winner = copy.Winner;
        WinningPathPlayerOne = copy.WinningPathPlayerOne;
        WinningPathPlayerTwo = copy.WinningPathPlayerTwo;
    }

    /// <summary>
    /// Make a given move on the board.
    /// </summary>
    /// <param name="move"></param>
    public boolean MakeMove(int move) {
        return MakeMove(move, true, true);
    }

    /// <summary>
    /// Make a given move on the board.
    /// </summary>
    /// <param name="move"></param>
    public boolean MakeMove(int move, boolean checkForWin, boolean autoGrow) {
        Queue<Integer> eruptions = new LinkedList<>();

        if (move == Constants.AllGrowMove) {
            LastMoveIncreasedTile = false;

            for (int i = 0; i < 80; i++) {
                if (Tiles[i] != 0) {
                    if (!Settings.AllowDormantVolcanoes || !Dormant[i]) {
                        Tiles[i] = Tiles[i] > 0 ? Tiles[i] + 1 : Tiles[i] - 1;
                        if (Math.abs(Tiles[i]) >= Settings.MaxVolcanoLevel) {
                            eruptions.add(i);
                        }
                    }
                }
            }
        } else {
            LastMoveIncreasedTile = Tiles[move] != 0;

            Tiles[move] = Player == PlayerType.One ? Tiles[move] + 1 : Tiles[move] - 1;
            if (Math.abs(Tiles[move]) >= Settings.MaxVolcanoLevel) {
                eruptions.add(move);
            }
        }

        if (eruptions.size() > 0) {
            ProcessEruptions(eruptions);
        }

        if (Winner == PlayerType.Empty && checkForWin) {
            SearchForWin();
        }

        if (Winner == PlayerType.Empty) {
            Turn++;
            Player = GetPlayerForTurn(Turn);

            if (autoGrow && GetMoveTypeForTurn(Turn) == MoveType.AllGrow) {
                MakeMove(Constants.AllGrowMove);
                return true;
            }
        }

        return false;
    }

    private void ProcessEruptions(Queue<Integer> eruptions) {
        int phases = 100;
        Queue<Integer> deltaIndexes = new LinkedList<>();
        while (eruptions.size() > 0 && phases-- > 0) {
            // Phase one: get a list of deltas from eruptions
            int[] deltas = new int[80];
            while (eruptions.size() > 0) {
                int i = eruptions.remove();

                if (Settings.AllowDormantVolcanoes) {
                    // Make the volcano dormant
                    Tiles[i] = Tiles[i] > 0 ? Settings.MaxVolcanoLevel : -Settings.MaxVolcanoLevel;
                    Dormant[i] = true;
                } else {
                    // Downgrade to a level one volcano
                    Tiles[i] = Tiles[i] > 0 ? Settings.MaxMagmaChamberLevel + 1 : -(Settings.MaxMagmaChamberLevel + 1);
                }

                for (int adjacent : Constants.AdjacentIndexes[i]) {
                    deltaIndexes.add(adjacent);

                    // Blank tile
                    if (Tiles[adjacent] == 0) {
                        if (Tiles[i] > 0) {
                            deltas[adjacent] += Settings.EruptOverflowEmptyTileAmount;
                        } else {
                            deltas[adjacent] -= Settings.EruptOverflowEmptyTileAmount;
                        }
                    }

                    // Same owner
                    else if ((Tiles[adjacent] > 0 && Tiles[i] > 0) || (Tiles[adjacent] < 0 && Tiles[i] < 0)) {
                        if (!Settings.AllowDormantVolcanoes || !Dormant[adjacent]) {
                            if (Tiles[i] > 0) {
                                deltas[adjacent] += Settings.EruptOverflowFriendlyTileAmount;
                            } else {
                                deltas[adjacent] -= Settings.EruptOverflowFriendlyTileAmount;
                            }
                        }
                    }

                    // Enemy owner
                    else if ((Tiles[adjacent] > 0 && Tiles[i] < 0) || (Tiles[adjacent] < 0 && Tiles[i] > 0)) {
                        if (Tiles[i] > 0) {
                            deltas[adjacent] -= Settings.EruptOverflowEnemyTileAmount;
                        } else {
                            deltas[adjacent] += Settings.EruptOverflowEnemyTileAmount;
                        }
                    }
                }
            }

            // Phase two: process deltas
            while (deltaIndexes.size() > 0) {
                int i = deltaIndexes.remove();
                if (deltas[i] != 0) {
                    boolean playerOne = Tiles[i] > 0;
                    boolean playerTwo = Tiles[i] < 0;

                    // Someone already owns this tile, so the deltas can be taken as-is
                    Tiles[i] += deltas[i];

                    // If we changed the value so much that it switched sides
                    if (((Tiles[i] < 0 && playerOne) || (Tiles[i] > 0 && playerTwo)) && !Settings.EruptOverflowAllowCapture) {
                        // Clear the tile
                        Tiles[i] = 0;
                        Dormant[i] = false;
                    }

                    // Did this change trigger a chain reaction?
                    if (Math.abs(Tiles[i]) >= Settings.MaxVolcanoLevel) {
                        eruptions.add(i);
                    }

                    // So we don't process it a second time
                    deltas[i] = 0;
                }

                if (Settings.AllowDormantVolcanoes) {
                    Dormant[i] = Math.abs(Tiles[i]) == Settings.MaxVolcanoLevel;
                }
            }
        }

        // If we're caught in an infinite loop of volcano eruptions, call the game a draw
        if (phases <= 0) {
            Winner = PlayerType.Draw;
            WinningPathPlayerOne = new ArrayList<Integer>();
            WinningPathPlayerTwo = new ArrayList<Integer>();
        }
    }

    private void SearchForWin() {
        Winner = PlayerType.Empty;

        // We only need to cover the first 40 tiles since their antipodes cover the last 40
        for (int i = 0; i < 40; i++) {
            if (Tiles[i] != 0 &&
                    ((Tiles[Constants.Antipodes[i]] > 0 && Tiles[i] > 0) || (Tiles[Constants.Antipodes[i]] < 0 && Tiles[i] < 0)) &&
                    Math.abs(Tiles[i]) > Settings.MaxMagmaChamberLevel &&
                    Math.abs(Tiles[Constants.Antipodes[i]]) > Settings.MaxMagmaChamberLevel) {
                // Only search until we find a winner or detect a draw
                if (Winner == PlayerType.Empty || (Winner == PlayerType.One && Tiles[i] < 0) || (Winner == PlayerType.Two && Tiles[i] > 0)) {
                    if (fastWinSearch) {
                        PlayerType winner = FastWinSearch(i, Constants.Antipodes[i]);
                        if (winner != PlayerType.Empty) {
                            if (Tiles[i] > 0) {
                                Winner = Winner != PlayerType.Empty ? PlayerType.Draw : PlayerType.One;
                            } else {
                                Winner = Winner != PlayerType.Empty ? PlayerType.Draw : PlayerType.Two;
                            }

                            if (Winner == PlayerType.Draw) {
                                return;
                            }
                        }
                    } else {
                        List<Integer> path = pathFinder.FindPath(this, i, Constants.Antipodes[i]).Path;
                        if (path.size() > 0) {
                            if (Tiles[i] > 0) {
                                Winner = Winner != PlayerType.Empty ? PlayerType.Draw : PlayerType.One;
                                WinningPathPlayerOne = path;
                            } else {
                                Winner = Winner != PlayerType.Empty ? PlayerType.Draw : PlayerType.Two;
                                WinningPathPlayerTwo = path;
                            }

                            if (Winner == PlayerType.Draw) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private PlayerType FastWinSearch(int start, int end) {
        boolean[] visited = new boolean[80];
        Queue<Integer> queue = new LinkedList<>();

        queue.add(start);

        while (queue.size() > 0) {
            int next = queue.remove();

            if (!visited[next]) {
                visited[next] = true;

                if ((Tiles[next] > 0 && Tiles[start] > 0) || (Tiles[next] < 0 && Tiles[start] < 0)) {
                    if (next == end) {
                        return Tiles[next] > 0 ? PlayerType.One : PlayerType.Two;
                    }

                    for (int i = 0; i < Constants.AdjacentIndexes[next].length; i++) {
                        queue.add(Constants.AdjacentIndexes[next][i]);
                    }
                }
            }
        }

        return PlayerType.Empty;
    }

    /// <summary>
    /// Get a list of all valid moves for the current player on the current board state.
    /// </summary>
    /// <returns></returns>
    public List<Integer> GetMoves() {
        return GetMoves(true, true, true, Settings.MaxVolcanoLevel);
    }

    public List<Integer> GetRandomMoves() {
        List<Integer> moves = GetMoves(false, true, false, Settings.MaxVolcanoLevel);
        if (moves.size() > 0) {
            return moves;
        }
        return GetMoves();
    }

    /// <summary>
    /// Get a list of all valid moves for the current player on the current board state.
    /// </summary>
    /// <returns></returns>
    public List<Integer> GetMoves(boolean growthMoves, boolean expandMoves, boolean captureMoves, int maxGrowthValue) {
        List<Integer> moves = new ArrayList<>();

        if (Winner != PlayerType.Empty) {
            return moves;
        } else if (GetMoveTypeForTurn(Turn) == MoveType.AllGrow) {
            moves.add(Constants.AllGrowMove);
        } else {
            for (int i = 0; i < 80; i++) {
                // Grow existing tiles
                if (growthMoves && ((Tiles[i] > 0 && Player == PlayerType.One) || (Tiles[i] < 0 && Player == PlayerType.Two)) && Math.abs(Tiles[i]) < maxGrowthValue) {
                    if (!Settings.AllowDormantVolcanoes || !Dormant[i]) {
                        moves.add(i);
                    }
                }

                // Claim new tiles
                if (expandMoves && Tiles[i] == 0) {
                    moves.add(i);
                }

                // Capture enemy tiles
                if (captureMoves) {
                    PlayerType opponent = Player == PlayerType.One ? PlayerType.Two : PlayerType.One;
                    if (Settings.AllowMagmaChamberCaptures &&
                            ((Tiles[i] > 0 && opponent == PlayerType.One) || (Tiles[i] < 0 && opponent == PlayerType.Two)) &&
                            Math.abs(Tiles[i]) <= Settings.MaxMagmaChamberLevel) {
                        moves.add(i);
                    }
                    if (Settings.AllowVolcanoCaptures &&
                            ((Tiles[i] > 0 && opponent == PlayerType.One) || (Tiles[i] < 0 && opponent == PlayerType.Two)) &&
                            Math.abs(Tiles[i]) > Settings.MaxMagmaChamberLevel) {
                        moves.add(i);
                    }
                }
            }
        }

        if (moves.size() == 0) {
            if (growthMoves && expandMoves & captureMoves && maxGrowthValue >= Settings.MaxVolcanoLevel) {
                // There are no moves in this position
                return moves;
            } else {
                return GetMoves(true, true, true, Settings.MaxVolcanoLevel);
            }
        }

        return moves;
    }

    public boolean IsValidMove(int move) {
        return GetMoves().stream().anyMatch(x -> x == move);
    }

    /// <summary>
    /// Get the opponent for a given PlayerType.
    /// </summary>
    /// <param name="current"></param>
    /// <returns></returns>
    private PlayerType GetOpponent(PlayerType current) {
        return current == PlayerType.One ? PlayerType.Two : PlayerType.One;
    }

    public PlayerType GetPlayerForCurrentTurn() {
        return GetPlayerForTurn(Turn);
    }

    /// <summary>
    /// Which player should move on a given turn number.
    /// </summary>
    /// <param name="turn"></param>
    /// <returns></returns>
    private PlayerType GetPlayerForTurn(int turn) {
        switch ((turn - 1) % 6) {
            case 0:
            case 4:
            case 5:
                return PlayerType.One;

            case 1:
            case 2:
            case 3:
                return PlayerType.Two;

            default:
                return PlayerType.Empty;
        }
    }

    public PlayerType GetPlayerForPreviousTurn() {
        return GetPlayerForTurn(Turn - 1);
    }

    /// <summary>
    /// Get the type of move a specific turn requires.
    /// </summary>
    /// <param name="turn"></param>
    /// <returns></returns>
    private MoveType GetMoveTypeForTurn(int turn) {
        switch ((turn - 1) % 6) {
            case 2:
            case 5:
                return MoveType.AllGrow;

            case 0:
            case 1:
            case 3:
            case 4:
            default:
                return MoveType.SingleGrow;
        }
    }
}