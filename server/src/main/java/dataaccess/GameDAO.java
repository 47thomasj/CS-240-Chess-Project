package dataaccess;

import model.GameData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameDAO {
    private Map<Integer, GameData> gameTable;
    public GameDAO() {
        gameTable = new Map<Integer, GameData>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public GameData get(Object key) {
                return null;
            }

            @Nullable
            @Override
            public GameData put(Integer key, GameData value) {
                return null;
            }

            @Override
            public GameData remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NotNull Map<? extends Integer, ? extends GameData> m) {

            }

            @Override
            public void clear() {

            }

            @NotNull
            @Override
            public Set<Integer> keySet() {
                return Set.of();
            }

            @NotNull
            @Override
            public Collection<GameData> values() {
                return List.of();
            }

            @NotNull
            @Override
            public Set<Entry<Integer, GameData>> entrySet() {
                return Set.of();
            }
        };
    }

    void createGame(GameData data) throws DataAccessException {
        if (gameTable.containsKey(data.gameID())) throw new DataAccessException("Error: game id already taken");
        gameTable.put(data.gameID(), data);
    }

    GameData readGame(GameData data) throws DataAccessException {
        if (!gameTable.containsKey(data.gameID())) throw new DataAccessException("Error: invalid game id");
        return gameTable.get(data.gameID());
    }

    void updateGame(GameData data) throws DataAccessException {
        if (!gameTable.containsKey(data.gameID())) throw new DataAccessException("Error: invalid game id");
        gameTable.replace(data.gameID(), data);
    }

    void deleteGame(GameData data) throws DataAccessException {
        if (!gameTable.containsKey(data.gameID())) throw new DataAccessException("Error: invalid game id");
        gameTable.remove(data.gameID());
    }

    GameData[] listGames() {
        return gameTable.values().toArray(new GameData[0]);
    }

    void clear() {
        gameTable = new Map<Integer, GameData>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public GameData get(Object key) {
                return null;
            }

            @Nullable
            @Override
            public GameData put(Integer key, GameData value) {
                return null;
            }

            @Override
            public GameData remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NotNull Map<? extends Integer, ? extends GameData> m) {

            }

            @Override
            public void clear() {

            }

            @NotNull
            @Override
            public Set<Integer> keySet() {
                return Set.of();
            }

            @NotNull
            @Override
            public Collection<GameData> values() {
                return List.of();
            }

            @NotNull
            @Override
            public Set<Entry<Integer, GameData>> entrySet() {
                return Set.of();
            }
        };
    }
}
