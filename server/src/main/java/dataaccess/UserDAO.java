package dataaccess;

import model.UserData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDAO {

    private Map<String, UserData> userTable;
    private UserDAO() {
        userTable = new Map<String, UserData>() {
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
            public UserData get(Object key) {
                return null;
            }

            @Nullable
            @Override
            public UserData put(String key, UserData value) {
                return null;
            }

            @Override
            public UserData remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NotNull Map<? extends String, ? extends UserData> m) {

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
            public Collection<UserData> values() {
                return List.of();
            }

            @NotNull
            @Override
            public Set<Entry<String, UserData>> entrySet() {
                return Set.of();
            }
        };
    }

    public void createUser(UserData data) throws DataAccessException {
        if (userTable.containsKey(data.username())) throw new DataAccessException("Error: username already taken");
        userTable.put(data.username(), data);
    }

    public UserData readUser(String username) throws DataAccessException {
        if (!userTable.containsKey(username)) throw new DataAccessException("Error: invalid username");
        return userTable.get(username);
    }
    public void updateUser(UserData data) throws DataAccessException {
        if (!userTable.containsKey(data.username())) throw new DataAccessException("Error: invalid username");
        userTable.replace(data.username(), data);
    }
    public void deleteUser(UserData data) throws DataAccessException {
        if (!userTable.containsKey(data.username())) throw new DataAccessException("Error: invalid username");
        userTable.remove(data.username());
    }

    public void clear() {
        userTable = new Map<String, UserData>() {
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
            public UserData get(Object key) {
                return null;
            }

            @Nullable
            @Override
            public UserData put(String key, UserData value) {
                return null;
            }

            @Override
            public UserData remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NotNull Map<? extends String, ? extends UserData> m) {

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
            public Collection<UserData> values() {
                return List.of();
            }

            @NotNull
            @Override
            public Set<Entry<String, UserData>> entrySet() {
                return Set.of();
            }
        };
    }
}
