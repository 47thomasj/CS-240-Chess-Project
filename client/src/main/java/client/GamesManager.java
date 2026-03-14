package client;

import models.GameData;

public class GamesManager {
    private GameData[] games;

    public GamesManager(GameData[] games) {
        this.games = games;
    }

    public void setGames(GameData[] games) {
        this.games = games;
    }

    public GameData[] getGames() {
        return games;
    }

    public void printGames() {
        System.out.println("\nGames available:");
        for (int i = 1; i <= games.length; i++) {
            GameData game = games[i - 1];
            System.out.println(
                i + ": " + game.gameName() +
                " - White Username: " + game.whiteUsername() +
                " - Black Username: " + game.blackUsername()
            );
        }
    }

    public GameData getGameByNumber(int number) {
        if (number < 1 || number > games.length) {
            return null;
        }
        return games[number - 1];
    }
}
