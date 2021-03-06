package kr.rvs.kkutu.gui;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import kr.rvs.kkutu.EntryKkutu;
import kr.rvs.kkutu.game.Profile;
import kr.rvs.kkutu.game.User;
import kr.rvs.kkutu.game.room.RoomData;
import kr.rvs.kkutu.network.LobbyPacketManager;
import kr.rvs.kkutu.network.handler.ErrorHandler;
import kr.rvs.kkutu.network.handler.LobbyChatHandler;
import kr.rvs.kkutu.network.handler.RoomJoinHandler;
import kr.rvs.kkutu.network.handler.UpdateHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    private static final LobbyController instance = new LobbyController();

    public TitledPane titledUsersPane;
    public ListView<User> userView;
    public TextField userSearchField;

    public TableView<RoomData> roomView;
    public TextArea chatArea;
    public TextField chatField;

    public ImageView profileImage;
    public Label userNameLabel;
    public Label userLevelLabel;
    public Label hasMoneyLabel;
    public Label totalWinLabel;

    public static LobbyController get() {
        return instance;
    }

    public void setServerName(String name) {
        titledUsersPane.setText(name);
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

    public void myProfileInit(User user) {
        userNameLabel.setText(user.getProfile().getNick());
        userLevelLabel.setText("1 레벨");
        hasMoneyLabel.setText(user.getMoney() + " 핑");
        totalWinLabel.setText("통산 0 승");
    }

    private void setupInstantText(TextField field) {
        field.focusedProperty().addListener(new InstantTextCleaner(field));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LobbyChatHandler chatHandler = new LobbyChatHandler();
        UpdateHandler updateHandler = new UpdateHandler();
        RoomJoinHandler roomHandler = new RoomJoinHandler();
        LobbyPacketManager.get().addHandler(
                chatHandler,
                updateHandler,
                roomHandler,
                ErrorHandler.get()
        );
        chatField.setOnKeyPressed(chatHandler);
        roomView.setRowFactory(table -> new RoomTableRow(roomHandler));

        setupInstantText(chatField);
        setupInstantText(userSearchField);
    }
}
