package kr.rvs.kkutu.network.handler;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import kr.rvs.kkutu.EntryKkutu;
import kr.rvs.kkutu.game.holder.RoomHolder;
import kr.rvs.kkutu.game.room.Room;
import kr.rvs.kkutu.gui.LobbyController;
import kr.rvs.kkutu.network.LobbyPacketManager;
import kr.rvs.kkutu.network.PacketHandler;
import kr.rvs.kkutu.network.PacketManager;
import kr.rvs.kkutu.network.packet.Packet;
import kr.rvs.kkutu.network.packet.in.PreRoomPacket;
import kr.rvs.kkutu.network.packet.out.RoomEnterPacket;

public class RoomJoinHandler implements PacketHandler, EventHandler<MouseEvent> {
    @Override
    public void handle(PacketManager manager, Packet packet) {
        if (packet instanceof PreRoomPacket) {
            PreRoomPacket preRoomPacket = ((PreRoomPacket) packet);
            Room room = RoomHolder.getOrThrow(preRoomPacket.getId());
            EntryKkutu.initRoom(preRoomPacket.getChannel(), room);
        }
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY
                && event.getClickCount() % 2 == 0) {
            Room room = LobbyController.get().roomView.getSelectionModel().getSelectedItem().getRoom();
            if (room.isPassword()) {
                EntryKkutu.waitForUserInput(dialog -> {
                    dialog.setHeaderText(room + " 입장");
                    dialog.setContentText("비밀번호:");
                }, input -> LobbyPacketManager.get().sendPacket(new RoomEnterPacket(room.getId(), input)));
            } else {
                LobbyPacketManager.get().sendPacket(new RoomEnterPacket(room.getId()));
            }
        }
    }
}
