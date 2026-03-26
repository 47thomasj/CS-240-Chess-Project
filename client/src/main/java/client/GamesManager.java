package client;

import models.GameData;
import chess.ChessGame;
import chess.ChessGame.TeamColor;

public class GamesManager {
    private GameData[] games;
    private ChessGame currentGame;
    private TeamColor currentTeamColor;

    public GamesManager(GameData[] games) {
        this.games = games;
        this.currentGame = null;
        this.currentTeamColor = null;
    }

    public void setGames(GameData[] games) {
        this.games = games;
    }

    public void setCurrentGame(ChessGame currentGame) {
        this.currentGame = currentGame;
    }

    public ChessGame getCurrentGame() {
        return currentGame;
    }

    public GameData[] getGames() {
        return games;
    }

    public void setCurrentTeamColor(TeamColor currentTeamColor) {
        this.currentTeamColor = currentTeamColor;
    }

    public TeamColor getCurrentTeamColor() {
        return currentTeamColor;
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
