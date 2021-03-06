package kr.rvs.kkutu.game.holder;

import javafx.collections.ObservableList;
import kr.rvs.kkutu.EntryKkutu;
import kr.rvs.kkutu.game.User;
import kr.rvs.kkutu.gui.LobbyController;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class UserHolder {
    private static final Map<String, User> USER_MAP = new ConcurrentHashMap<>();

    private static void callback(Consumer<ObservableList<User>> consumer) {
        EntryKkutu.runOnMain(() -> consumer.accept(LobbyController.get().userView.getItems()));
    }

    public static void join(User user) {
        USER_MAP.put(user.getId(), user);
        callback(items -> items.add(user));
    }

    public static void quit(String id) {
        USER_MAP.remove(id);
        callback(items -> items.removeIf(user -> user.getId().equals(id)));
    }

    public static Optional<User> get(String id) {
        return Optional.ofNullable(USER_MAP.get(id));
    }

    public static User getOrThrow(String id) {
        return get(id).orElseThrow(IllegalStateException::new);
    }

    private UserHolder() {
    }
}
