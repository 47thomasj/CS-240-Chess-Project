package dataaccess;

import model.AuthData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthDAO {
    private Map<String, AuthData> authTable;
    public AuthDAO() {
        authTable = new Map<String, AuthData>() {
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
            public AuthData get(Object key) {
                return null;
            }

            @Nullable
            @Override
            public AuthData put(String key, AuthData value) {
                return null;
            }

            @Override
            public AuthData remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NotNull Map<? extends String, ? extends AuthData> m) {

            }

            @Override
            public void clear() {

            }

            @NotNull
            @Override
            public Set<String> keySet() {
                return Set.of();
            }

            @NotNull
            @Override
            public Collection<AuthData> values() {
                return List.of();
            }

            @NotNull
            @Override
            public Set<Entry<String, AuthData>> entrySet() {
                return Set.of();
            }
        };
    }

    void createAuth(AuthData data) throws DataAccessException {
        authTable.put(data.authToken(), data);
    }

    AuthData readAuth(AuthData data) throws DataAccessException {
        if (!authTable.containsKey(data.authToken())) throw new DataAccessException("Error: unauthorized");
        return authTable.get(data.authToken());
    }

    void updateAuth(AuthData data) throws DataAccessException {
        if (!authTable.containsKey(data.authToken())) throw new DataAccessException("Error: unauthorized");
        authTable.replace(data.authToken(), data);
    }

    void deleteAuth(AuthData data) throws DataAccessException {
        if (!authTable.containsKey(data.authToken())) throw new DataAccessException("Error: unauthorized");
        authTable.remove(data.authToken());
    }

    void clear() {
        authTable = new Map<String, AuthData>() {
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
            public AuthData get(Object key) {
                return null;
            }

            @Nullable
            @Override
            public AuthData put(String key, AuthData value) {
                return null;
            }

            @Override
            public AuthData remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NotNull Map<? extends String, ? extends AuthData> m) {

            }

            @Override
            public void clear() {

            }

            @NotNull
            @Override
            public Set<String> keySet() {
                return Set.of();
            }

            @NotNull
            @Override
            public Collection<AuthData> values() {
                return List.of();
            }

            @NotNull
            @Override
            public Set<Entry<String, AuthData>> entrySet() {
                return Set.of();
            }
        };
    }
}
