package kr.rvs.kkutu.network.packet.in;

import com.google.gson.JsonObject;
import kr.rvs.kkutu.network.packet.ReadablePacket;
import kr.rvs.kkutu.util.Validate;

public class YellPacket implements ReadablePacket {
    private String value;

    @Override
    public String type() {
        return "yell";
    }

    @Override
    public void read(JsonObject json) {
        Validate.isTrue(json.has("value"), json.toString());
        this.value = json.get("value").getAsString();
    }

    public String getValue() {
        return value;
    }
}
