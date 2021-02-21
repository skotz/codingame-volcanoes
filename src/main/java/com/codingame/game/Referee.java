package com.codingame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    private int gameWidth = 1920;
    private int gameHeight = 1080;

    private Board game;
    private List<String> initialInput;

    @Override
    public void init() {
        // Initialize your game here.

        game = new Board();

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

        drawBackground();
    }

    private void drawBackground() {
        graphicEntityModule.createCircle().setX(gameWidth / 2).setY(gameHeight / 2);
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
                String output = players.get(playerIndex).getOutputs().get(0);

                if (!Constants.TileIndexes.containsKey(output)) {
                    players.get(playerIndex).deactivate(players.get(playerIndex).getNicknameToken() + " played an invalid move!");
                } else {
                    int moveIndex = Constants.TileIndexes.get(output);
                    game.MakeMove(moveIndex, true, false);
                }
            } catch (TimeoutException e) {
                players.get(playerIndex).deactivate(players.get(playerIndex).getNicknameToken() + " timed out!");
            }
        }
    }
}
