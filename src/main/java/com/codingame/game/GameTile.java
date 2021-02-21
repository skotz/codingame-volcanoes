package com.codingame.game;

import com.codingame.gameengine.module.entities.Polygon;
import com.codingame.gameengine.module.entities.Text;

public class GameTile
{
    public PointF Location;
    public boolean Upright;
    public Rectangle BoundingBox;
    public Polygon Path;
    public Text TileValue;
}
