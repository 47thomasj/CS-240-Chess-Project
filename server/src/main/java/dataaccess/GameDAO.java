package dataaccess;

import model.GameData;

import java.util.*;

public class GameDAO {
    private HashMap<Integer, GameData> gameTable;
    private Set<Integer> gameIDs;
    public GameDAO() {
        gameIDs = new HashSet<Integer>();
        gameTable = new HashMap<Integer, GameData>();
    }

    public void createGame(GameData data) throws DataAccessException {
        if (gameTable.containsKey(data.gameID())) throw new DataAccessException("Error: game id already taken");
        gameTable.put(data.gameID(), data);
        gameIDs.add(data.gameID());
    }

    public GameData readGame(GameData data) throws DataAccessException {
        if (!gameTable.containsKey(data.gameID())) throw new DataAccessException("Error: invalid game id");
        return gameTable.get(data.gameID());
    }

    public void updateGame(GameData data) throws DataAccessException {
        if (!gameTable.containsKey(data.gameID())) throw new DataAccessException("Error: invalid game id");
        gameTable.replace(data.gameID(), data);
    }

    public void deleteGame(GameData data) throws DataAccessException {
        if (!gameTable.containsKey(data.gameID())) throw new DataAccessException("Error: invalid game id");
        gameTable.remove(data.gameID());
        gameIDs.remove(data.gameID());
    }

    public GameData[] listGames() {
        return gameTable.values().toArray(new GameData[0]);
    }

    public int getHighestGameID() {
        return gameIDs.stream().max(Integer::compareTo).orElse(0);
    }

    public void clear() {
        gameTable = new HashMap<Integer, GameData>();
        gameIDs = new HashSet<Integer>();
    }
}
