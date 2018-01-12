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
import kr.rvs.kkutu.network.impl.KkutuKoreaPacketFactory;
import kr.rvs.kkutu.network.netty.WebSocket;
import kr.rvs.kkutu.network.packet.Packet;
import kr.rvs.kkutu.network.packet.in.PreRoomPacket;
import kr.rvs.kkutu.network.packet.out.RoomEnterPacket;

import java.net.URI;

public class RoomJoinHandler implements PacketHandler, EventHandler<MouseEvent> {
    @Override
    public void handle(Packet packet) {
        if (packet instanceof PreRoomPacket) {
            PreRoomPacket preRoomPacket = ((PreRoomPacket) packet);
            Room room = RoomHolder.getOrThrow(preRoomPacket.getId());
            PacketManager packetManager = new PacketManager();
            room.setPacketManager(packetManager);
            new WebSocket(
                    "Room",
                    URI.create(String.format("wss://ws.kkutu.co.kr:%s/2c727562e48cc83922ee306e9af3ed957500ed12833a0d4e8c8a0127430d219ac015a9670ceb4905e4f5abe8a422dc56&%s&%s",
                            8495 + preRoomPacket.getChannel(), preRoomPacket.getChannel(), preRoomPacket.getId())),
                    KkutuKoreaPacketFactory.get(),
                    packetManager
            ).start();
            EntryKkutu.showRoom(room, e -> packetManager.close());
        }
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY
                && event.getClickCount() % 2 == 0) {
            Room room = LobbyController.get().roomView.getSelectionModel().getSelectedItem();
            LobbyPacketManager.get().sendPacket(new RoomEnterPacket(room.getId()));
        }
    }
}