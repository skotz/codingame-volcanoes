package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Polygon;
import com.codingame.gameengine.module.entities.RoundedRectangle;
import com.codingame.gameengine.module.entities.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Graphics {
    private GraphicEntityModule graphics;
    private Board game;

    private int gameWidth = 1920;
    private int gameHeight = 1080;

    public float TileSize = 156;
    public int TileSpacing = 8;
    public int BoardSpacing = 23;
    public int TopBoardSpacing = 145;
    public float TileWidth = TileSize;
    public float TileHeight = (float)(TileWidth * Math.sqrt(3) / 2);
    public float TileHorizontalSpacing = (float)(TileSpacing * Math.sqrt(3) / 2);
    public float TileHorizontalInverseSpacing = (float)(TileSpacing / 2);

    public int MainFontSize = 14;
    public int SubTextFontSize = 8;

    public int PlayerOneVolcanoTileColor = GetColor(18, 11, 134);
    public int PlayerOneMagmaChamberTileColor = GetColor(39, 29, 211);
    public int PlayerOneDormantTileColor = GetColor(7, 3, 76);
    public int PlayerTwoVolcanoTileColor = GetColor(192, 114, 0);
    public int PlayerTwoMagmaChamberTileColor= GetColor(255, 151, 0);
    public int PlayerTwoDormantTileColor = GetColor(114, 68, 0);
    public int EmptyTileColor = GetColor(128, 128, 128);
    public int BackgroundColor = GetColor(240, 240, 240);
    public int ReviewBackgroundColor = GetColor(200, 200, 200);
    public int HoverTileBorderColor = GetColor(230, 0, 113);
    public int HoverAdjacentTileBorderColor = GetColor(200, 200, 200);
    public int HoverAntipodeTileBorderColor = GetColor(0, 219, 48);
    public int RecentEruptionTileBorderColor = GetColor(0, 0, 0);
    public int TurnClockPlayerToMoveBorderColor = GetColor(255, 255, 0);

    public List<GameTile> _tiles;
    public List<RoundedRectangle> _turnClock;

    public Graphics(GraphicEntityModule graphicEntityModule, Board gameState) {
        graphics = graphicEntityModule;
        game = gameState;

        InitializeTiles();
        InitializeText();
        InitializeTurnClock();
    }

    private void InitializeTurnClock()
    {
        int center = gameWidth / 2;
        int spacing = 5;
        int width = 50;
        int start = center - (width * 6 + spacing * 5) / 2;

        _turnClock = new ArrayList<>();

        _turnClock.add(graphics.createRoundedRectangle().setX(start).setY(-100).setHeight(200).setAlpha(0.5).setWidth(width).setFillColor(PlayerOneVolcanoTileColor).setZIndex(5));
        start += width + spacing;
        _turnClock.add(graphics.createRoundedRectangle().setX(start).setY(-150).setHeight(200).setAlpha(0.5).setWidth(width).setFillColor(PlayerTwoVolcanoTileColor).setZIndex(5));
        start += width + spacing;
        _turnClock.add(graphics.createRoundedRectangle().setX(start).setY(-150).setHeight(200).setAlpha(0.5).setWidth(width).setFillColor(EmptyTileColor).setZIndex(5));
        start += width + spacing;
        _turnClock.add(graphics.createRoundedRectangle().setX(start).setY(-150).setHeight(200).setAlpha(0.5).setWidth(width).setFillColor(PlayerTwoVolcanoTileColor).setZIndex(5));
        start += width + spacing;
        _turnClock.add(graphics.createRoundedRectangle().setX(start).setY(-150).setHeight(200).setAlpha(0.5).setWidth(width).setFillColor(PlayerOneVolcanoTileColor).setZIndex(5));
        start += width + spacing;
        _turnClock.add(graphics.createRoundedRectangle().setX(start).setY(-150).setHeight(200).setAlpha(0.5).setWidth(width).setFillColor(EmptyTileColor).setZIndex(5));
    }

    private void InitializeText()
    {
        for (int i = 0; i < 80; i++) {
            int triangleAdjust = (_tiles.get(i).Upright ? 1 : -1) * (int)TileHeight / 3;
            Rectangle box = _tiles.get(i).BoundingBox;
            graphics.createText(Constants.TileNames[i])
                    .setFontFamily("Arial")
                    .setFontSize(32)
                    .setFillColor(0xffffff)
                    .setAnchor(0.5)
                    .setX(box.X + box.Width / 2)
                    .setY(box.Y + box.Height / 2 + triangleAdjust)
                    .setZIndex(200)
                    .setAlpha(0.5);
            _tiles.get(i).TileValue = graphics.createText("")
                    .setFontFamily("Arial")
                    .setFontSize(50)
                    .setFillColor(0xffffff)
                    .setAnchor(0.5)
                    .setX(box.X + box.Width / 2)
                    .setY(box.Y + box.Height / 2)
                    .setZIndex(200)
                    .setAlpha(0);
        }
    }

    private void InitializeTiles()
    {
        _tiles = new ArrayList<>();

        for (int outer = 0; outer < 20; outer++)
        {
            int outerRow = outer / 5;
            int outerCol = outer % 5;

            for (int inner = 0; inner < 4; inner++)
            {
                // Details of outer triangle
                float x = outerCol * (TileWidth * 2 + TileHorizontalSpacing * 4) + TileSpacing + BoardSpacing;
                float y = outerRow * (TileHeight * 2 + TileSpacing * 2 + TileHorizontalInverseSpacing) + TileSpacing + BoardSpacing + TopBoardSpacing;
                boolean upright = outerRow % 2 == 0;
                if (outerRow >= 2)
                {
                    x += TileWidth + TileHorizontalSpacing * 2;
                    y -= TileHeight * 2 + TileSpacing * 2;
                }

                // Adjust to settings of inner triangle
                if (upright)
                {
                    switch (inner)
                    {
                        case 0: // e.g., N07
                            x += TileWidth / 2 + TileHorizontalSpacing;
                            y += TileHeight + TileSpacing;
                            upright = !upright;
                            break;

                        case 1: // e.g., N01
                            x += TileWidth / 2 + TileHorizontalSpacing;
                            break;

                        case 2: // e.g., N08
                            x += TileWidth + TileHorizontalSpacing * 2;
                            y += TileHeight + TileSpacing + TileHorizontalInverseSpacing;
                            break;

                        case 3: // e.g., N06
                            y += TileHeight + TileSpacing + TileHorizontalInverseSpacing;
                            break;
                    }
                }
                else
                {
                    switch (inner)
                    {
                        case 0: // e.g., N22
                            x += TileWidth / 2 + TileHorizontalSpacing;
                            y += TileHorizontalInverseSpacing;
                            upright = !upright;
                            break;

                        case 1: // e.g., S32
                            x += TileWidth / 2 + TileHorizontalSpacing;
                            y += TileHeight + TileSpacing + TileHorizontalInverseSpacing;
                            break;

                        case 2: // e.g., N21
                            break;

                        case 3: // e.g., N23
                            x += TileWidth + TileHorizontalSpacing * 2;
                            break;
                    }
                }

                PointF[] points;
                if (upright)
                {
                    points = new PointF[] {
                            new PointF(x + TileWidth / 2, y),
                            new PointF(x, y + TileHeight),
                            new PointF(x + TileWidth, y + TileHeight)
                    };
                }
                else
                {
                    points = new PointF[] {
                            new PointF(x + TileWidth / 2, y + TileHeight),
                            new PointF(x, y),
                            new PointF(x + TileWidth, y)
                    };
                }

                Polygon path = graphics.createPolygon().setFillColor(EmptyTileColor).setX(0).setY(0).setZIndex(100);
                for (PointF point : points) {
                    path.addPoint((int)point.X, (int)point.Y);
                }

                GameTile tile = new GameTile();
                tile.Location = new PointF(x, y);
                tile.Upright = upright;
                tile.BoundingBox = new Rectangle((int)x, (int)y, (int)TileWidth, (int)TileHeight);
                tile.Path = path;

                _tiles.add(tile);
            }
        }
    }

    public void drawAvatars(List<Player> players) {
        int size = 100;
        int space = 10;
        int border = size + space * 2;

        graphics.createRectangle()
                .setX(space).setY(space)
                .setWidth(border).setHeight(border)
                .setFillColor(PlayerOneVolcanoTileColor);
        graphics.createSprite()
                .setX(space * 2).setY(space * 2)
                .setImage(players.get(0).getAvatarToken())
                .setBaseWidth(size).setBaseHeight(size);
        graphics.createText(players.get(0).getNicknameToken())
                .setX(border + space * 2).setY(space + border / 2)
                .setAnchorX(0).setAnchorY(0.5)
                .setFontFamily("Arial")
                .setFillColor(PlayerOneDormantTileColor)
                .setFontSize(80);

        graphics.createRectangle()
                .setX(gameWidth - border - space).setY(space)
                .setWidth(border).setHeight(border)
                .setFillColor(PlayerTwoVolcanoTileColor);
        graphics.createSprite()
                .setX(gameWidth - border).setY(space * 2)
                .setImage(players.get(1).getAvatarToken())
                .setBaseWidth(size).setBaseHeight(size);
        graphics.createText(players.get(1).getNicknameToken())
                .setX(gameWidth - border - space * 2).setY(space + border / 2)
                .setAnchorX(1).setAnchorY(0.5)
                .setFontFamily("Arial")
                .setFillColor(PlayerTwoDormantTileColor)
                .setFontSize(80);
    }

    public void drawBackground() {
        graphics.createRectangle().setFillColor(BackgroundColor).setWidth(gameWidth).setHeight(gameHeight).setX(0).setY(0);
    }

    public void draw() {
        for (int i = 0; i < 80; i++) {
            DrawTile(i, -1);
            if (game.Tiles[i] == 0) {
                _tiles.get(i).TileValue.setText(" ").setAlpha(0);
                graphics.commitEntityState(0, _tiles.get(i).TileValue);
            } else {
                _tiles.get(i).TileValue.setText(Integer.toString(Math.abs(game.Tiles[i]))).setAlpha(1);
                graphics.commitEntityState(0, _tiles.get(i).TileValue);
            }
        }
        // Turn clock
        for (int i = 0; i < 6; i++) {
            _turnClock.get(i).setY((game.Turn - 1) % 6 == i ? -100 : -150);
            _turnClock.get(i).setAlpha((game.Turn - 1) % 6 == i ? 1 : 0.5);
        }
    }

    private void DrawTile(int index, int lastPlayTile) {
        int tileColor = EmptyTileColor;

        if (game.Tiles[index] > 0) {
            if (Math.abs(game.Tiles[index]) > Settings.MaxMagmaChamberLevel) {
                if (Settings.AllowDormantVolcanoes && game.Dormant[index]) {
                    tileColor = PlayerOneDormantTileColor;
                } else {
                    tileColor = PlayerOneVolcanoTileColor;
                }
            } else {
                tileColor = PlayerOneMagmaChamberTileColor;
            }
        } else if (game.Tiles[index] < 0) {
            if (Math.abs(game.Tiles[index]) > Settings.MaxMagmaChamberLevel) {
                if (Settings.AllowDormantVolcanoes && game.Dormant[index]) {
                    tileColor = PlayerTwoDormantTileColor;
                } else {
                    tileColor = PlayerTwoVolcanoTileColor;
                }
            } else {
                tileColor = PlayerTwoMagmaChamberTileColor;
            }
        }

        // Winning paths
        if (game.WinningPathPlayerOne.size() > 0 || game.WinningPathPlayerTwo.size() > 0) {
            if (!game.WinningPathPlayerOne.contains(index) && !game.WinningPathPlayerTwo.contains(index)) {
                _tiles.get(index).Path.setAlpha(0.15);
            }
        }

        _tiles.get(index).Path.setFillColor(tileColor);
    }

    private int GetColor(int r, int g, int b) {
        return r * 256 * 256 + g * 256 + b;
    }
}
