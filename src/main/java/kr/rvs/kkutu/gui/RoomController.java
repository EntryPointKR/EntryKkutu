package kr.rvs.kkutu.gui;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import kr.rvs.kkutu.EntryKkutu;
import kr.rvs.kkutu.game.GameProcessorFactory;
import kr.rvs.kkutu.game.Profile;
import kr.rvs.kkutu.game.room.Room;
import kr.rvs.kkutu.game.room.RoomPlayer;
import kr.rvs.kkutu.network.PacketManager;
import kr.rvs.kkutu.network.handler.BotHandler;
import kr.rvs.kkutu.network.handler.ErrorHandler;
import kr.rvs.kkutu.network.handler.RoomChatHandler;
import kr.rvs.kkutu.network.handler.RoomHandler;
import kr.rvs.kkutu.util.Static;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class RoomController implements Initializable {
    private final Room room;
    private final GameProcessor processor;
    public Button readyButton;
    public TilePane userTilePane;
    public TextArea chatArea;
    public TextField chatField;
    public StackPane stackPane;

    public RoomController(Room room) {
        this.room = room;
        this.processor = room.getMode().getProcessorFactory().create(this);
    }

    public void chat(String message) {
        EntryKkutu.runOnMain(() -> {
            chatArea.appendText(message);
            chatArea.appendText("\n");
        });
    }

    public void chat(Profile profile, String message) {
        chat(String.format("%s: %s", profile.getNick(), message));
    }

    public void update() {
        processor.update(room);
        setPlayers(room.getPlayers());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PacketManager manager = room.getPacketManager();
        RoomChatHandler chatHandler = new RoomChatHandler(this);
        GameProcessorFactory factory = room.getMode().getProcessorFactory();

        // GameProcessor
        FXMLLoader loader = new FXMLLoader(factory.fxmlUrl());
        loader.setController(processor);
        try {
            Node node = loader.load();
            node.setVisible(false);
            stackPane.getChildren().add(node);
        } catch (Exception e) {
            Static.log(Level.WARNING, "GameProcessor can't load.");
        }

        manager.addHandler(
                chatHandler,
                new RoomHandler(room),
                ErrorHandler.get(),
                processor,
                new BotHandler()
        );

        chatField.setOnKeyPressed(chatHandler);
        chatField.focusedProperty().addListener(new InstantTextCleaner(chatField));

        readyButton.setOnMouseClicked(e -> room.ready());
        room.roomInit(this);
    }

    public void join(RoomPlayer player) {
        EntryKkutu.runOnMain(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/part/RoomUserTile.fxml"));
                RoomPlayerTileController tile = new RoomPlayerTileController(player, room);
                loader.setController(tile);
                player.setTileController(tile);
                Node node = loader.load();
                node.getProperties().put("id", player.getId());
                userTilePane.getChildren().add(node);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public void quit(String id) {
        EntryKkutu.runOnMain(() -> userTilePane.getChildren().removeIf(node ->
                id.equals(node.getProperties().get("id"))));
    }

    public void setPlayers(Iterable<RoomPlayer> players) {
        EntryKkutu.runOnMain(() -> {
            userTilePane.getChildren().clear();
            players.forEach(this::join);
        });
    }

    public Room getRoom() {
        return room;
    }
}
