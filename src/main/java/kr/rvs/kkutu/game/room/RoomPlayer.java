package kr.rvs.kkutu.game.room;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.rvs.kkutu.game.IdentityProvider;
import kr.rvs.kkutu.game.holder.UserHolder;
import kr.rvs.kkutu.gui.GamePlayerTileController;
import kr.rvs.kkutu.gui.RoomPlayerTileController;

public abstract class RoomPlayer implements IdentityProvider {
    private RoomPlayerTileController lobbyTileController;
    private GamePlayerTileController gameTileController;
    private boolean ready = false;

    private static RoomPlayer get(String id) {
        return UserHolder.get(id).map(RoomPlayer.class::cast).orElseGet(() -> new RoomUnknownPlayer(id));
    }

    public static RoomPlayer get(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject json = element.getAsJsonObject();
            return RoomBotPlayer.of(json);
        } else if (element.isJsonPrimitive()) {
            String id = element.getAsString();
            return get(id);
        } else {
            return RoomUnknownPlayer.DEFAULT;
        }
    }

    public void setTileController(RoomPlayerTileController tileController) {
        this.lobbyTileController = tileController;
    }

    public abstract String getName();

    public abstract int getScore();

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        setReady0(ready);
        if (lobbyTileController != null) {
            lobbyTileController.refreshStatus();
        }
    }

    protected void setReady0(boolean ready) {
        this.ready = ready;
    }
}
