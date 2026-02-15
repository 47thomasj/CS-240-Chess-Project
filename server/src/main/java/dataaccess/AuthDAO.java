package dataaccess;

import model.AuthData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.UUID;

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

    public void createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTable.put(authToken, authData);
    }

    public AuthData readAuth(AuthData data) throws DataAccessException {
        if (!authTable.containsKey(data.authToken())) throw new DataAccessException("Error: unauthorized");
        return authTable.get(data.authToken());
    }

    public void updateAuth(AuthData data) throws DataAccessException {
        if (!authTable.containsKey(data.authToken())) throw new DataAccessException("Error: unauthorized");
        authTable.replace(data.authToken(), data);
    }

    public void deleteAuth(AuthData data) throws DataAccessException {
        if (!authTable.containsKey(data.authToken())) throw new DataAccessException("Error: unauthorized");
        authTable.remove(data.authToken());
    }

    public void clear() {
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
