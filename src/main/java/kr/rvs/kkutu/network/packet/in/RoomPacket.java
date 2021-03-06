package kr.rvs.kkutu.network.packet.in;

import com.google.gson.JsonObject;
import kr.rvs.kkutu.game.room.Room;
import kr.rvs.kkutu.network.packet.ReadablePacket;

public class RoomPacket implements ReadablePacket {
    private Room room;

    @Override
    public String type() {
        return "room";
    }

    @Override
    public void read(JsonObject json) {
        this.room = Room.of(json.get("room").getAsJsonObject());
    }

    public Room getRoom() {
        return room;
    }
}
