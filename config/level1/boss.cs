using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;

/**
 * Win by building an unbroken chain of volcanoes from one side of the board to the other.
 **/

internal class Player
{
    private static void Main(string[] args)
    {
        List<string> allTiles = new List<string>();

        int numberOfTiles = int.Parse(Console.ReadLine()); // Number of tiles on the board
        for (int i = 0; i < numberOfTiles; i++)
        {
            string[] inputs = Console.ReadLine().Split(' ');
            string tileName = inputs[0]; // Name of the tile at this index (e.g., N01 or S25)
            int neighbor1 = int.Parse(inputs[1]); // Index of a neighboring tile
            int neighbor2 = int.Parse(inputs[2]); // Index of a neighboring tile
            int neighbor3 = int.Parse(inputs[3]); // Index of a neighboring tile

            allTiles.Add(tileName);
        }

        // game loop
        while (true)
        {
            string position = Console.ReadLine(); // Space-separated list of the volcano levels for every tile on the board (in index order); value will be positive for your volcanoes, negative for your opponent's volcanoes, or 0 if empty
            string moves = Console.ReadLine(); // Space-separate list of all valid moves in this position

            var allMoves = moves.Split(' ').ToList();
            var tiles = position.Split(' ').Select(x => int.Parse(x)).ToList();

            // Pick moves closest to the equator first on empty tiles
            var bestMoves = new List<string>();
            for (int i = 22; i <= 40; i += 2)
            {
                var move = "N" + i.ToString().PadLeft(2, '0');
                if (allMoves.Contains(move))
                {
                    var index = allTiles.IndexOf(move);
                    if (tiles[index] == 0)
                    {
                        bestMoves.Add(move);
                    }
                }
            }
            for (int i = 22; i <= 40; i += 2)
            {
                var move = "S" + i.ToString().PadLeft(2, '0');
                if (allMoves.Contains(move))
                {
                    var index = allTiles.IndexOf(move);
                    if (tiles[index] == 0)
                    {
                        bestMoves.Add(move);
                    }
                }
            }

            // Otherwise pick moves away from the edges
            allMoves = allMoves.OrderByDescending(x => int.Parse(Regex.Replace(x, "[^0-9]", ""))).Where(x => tiles[allTiles.IndexOf(x)] == 0).ToList();

            if (bestMoves.Count > 0)
            {
                var rand = new Random();
                Console.WriteLine(bestMoves[rand.Next(bestMoves.Count)]);
            }
            else if (allMoves.Count > 0)
            {
                Console.WriteLine(allMoves.FirstOrDefault());
            }
            else
            {
                Console.WriteLine("RANDOM");
            }
        }
    }
}