package com.codingame.game;

import java.util.*;
import java.util.stream.Collectors;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.google.inject.Inject;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    private Board game;
    private Graphics graphics;
    private List<String> initialInput;
    private Random rand;

    @Override
    public void init() {
        // Initialize your game here.

        rand = new Random(gameManager.getSeed());
        game = new Board();
        graphics = new Graphics(graphicEntityModule, game);

        initialInput = new ArrayList<>();
        for (int i = 0; i < Constants.AdjacentIndexes.length; i++) {
            String line = Constants.TileNames[i];
            for (int neighbor : Constants.AdjacentIndexes[i]) {
                line += " " + Constants.TileNames[neighbor];
            }
            initialInput.add(line);
        }
        Collections.sort(initialInput);
        initialInput.add(0, Integer.toString(Constants.AdjacentIndexes.length));

        graphics.drawBackground();
        graphics.draw();
        graphics.drawAvatars(gameManager.getActivePlayers());
    }

    @Override
    public void gameTurn(int turn) {
        List<Player> players = gameManager.getActivePlayers();

        if (turn == 1) {
            for (Player player : players) {
                for (String line : initialInput) {
                    player.sendInputLine(line);
                }
            }
        }

        int playerIndex = -1;
        PlayerType playerToMove = game.GetPlayerForCurrentTurn();
        MoveType moveType = game.GetMoveTypeForCurrentTurn();

        if (game.Winner != PlayerType.Empty) {
            // Game over
            if (game.Winner == PlayerType.One) {
                gameManager.addTooltip(players.get(0), "Blue wins!");
                gameManager.addToGameSummary("Blue wins!");
                players.get(0).setScore(100);
                players.get(1).setScore(0);
            } else if (game.Winner == PlayerType.One) {
                gameManager.addTooltip(players.get(1), "Orange wins!");
                gameManager.addToGameSummary("Orange wins!");
                players.get(0).setScore(0);
                players.get(1).setScore(100);
            } else {
                gameManager.addToGameSummary("Draw!");
                players.get(0).setScore(50);
                players.get(1).setScore(50);
            }
            gameManager.endGame();
        } else {
            if (moveType == MoveType.SingleGrow) {
                if (playerToMove == PlayerType.One) {
                    gameManager.addToGameSummary("Blue's move");
                    playerIndex = 0;
                } else if (playerToMove == PlayerType.Two) {
                    gameManager.addToGameSummary("Orange's move");
                    playerIndex = 1;
                }
            } else {
                gameManager.addToGameSummary("Growth");
                game.MakeMove(Constants.AllGrowMove);
            }

            if (playerIndex >= 0) {
                // Get a string representation of the board for this player
                List<Integer> positionValues = new ArrayList<>();
                for (String tile : Constants.OrderedTileNames) {
                    Integer tileIndex = Constants.TileIndexes.get(tile);
                    int tileValue = game.Tiles[tileIndex];
                    // Adjust to the perspective of the current player
                    tileValue *= playerIndex == 1 ? -1 : 1;
                    positionValues.add(tileValue);
                }
                List<String> allPositionValues = positionValues.stream().map(x -> x.toString()).collect(Collectors.toList());
                String position = String.join(" ", allPositionValues);

                // Get a list of all valid moves for this player
                List<Integer> validMoves = game.GetMoves();
                List<String> validMoveTiles = validMoves.stream().map(x -> Constants.TileNames[x]).collect(Collectors.toList());
                Collections.sort(validMoveTiles);
                String moveList = String.join(" ", validMoveTiles);

                try {
                    players.get(playerIndex).sendInputLine(position);
                    players.get(playerIndex).sendInputLine(moveList);
                    players.get(playerIndex).execute();
                    String output = players.get(playerIndex).getOutputs().get(0).toUpperCase(Locale.ROOT);

                    if (output.equals("RANDOM")) {
                        output = validMoveTiles.get(rand.nextInt(validMoveTiles.size()));
                    }

                    if (!Constants.TileIndexes.containsKey(output)) {
                        players.get(playerIndex).deactivate(players.get(playerIndex).getNicknameToken() + " played an invalid move!");
                        players.get(playerIndex).setScore(0);
                        players.get(playerIndex == 0 ? 1 : 0).setScore(100);
                        gameManager.endGame();
                    } else {
                        int moveIndex = Constants.TileIndexes.get(output);
                        game.MakeMove(moveIndex, true, false);
                    }
                } catch (TimeoutException e) {
                    players.get(playerIndex).deactivate(players.get(playerIndex).getNicknameToken() + " timed out!");
                    players.get(playerIndex).setScore(0);
                    players.get(playerIndex == 0 ? 1 : 0).setScore(100);
                    gameManager.endGame();
                } catch (Exception e) {
                    players.get(playerIndex).deactivate(players.get(playerIndex).getNicknameToken() + " threw an exception!");
                    players.get(playerIndex).setScore(0);
                    players.get(playerIndex == 0 ? 1 : 0).setScore(100);
                    gameManager.endGame();
                }
            }
        }

        graphics.draw();
    }
}
