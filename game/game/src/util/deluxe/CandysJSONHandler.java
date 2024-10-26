package util.deluxe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CandysJSONHandler {
    public final Gson candysGson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(CandysUser.class, new CandysUser()).create();

    public final Gson guestTable = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(DeluxeGuestStoreTable.class, new DeluxeGuestStoreTable()).create();

    public String writeCandysUser(CandysUser candysUser){
        return candysGson.toJson(candysUser);
    }

    public String writeGuestTable(DeluxeGuestStoreTable guestStoreTable){
        return guestTable.toJson(guestStoreTable);
    }

    public CandysUser readUser(String json) {
        json = readJSON(json);
        return candysGson.fromJson(json, CandysUser.class);
    }

    public DeluxeGuestStoreTable readScoreTable(String json) {
        json = readJSON(json);
        return guestTable.fromJson(json, DeluxeGuestStoreTable.class);
    }

    private String readJSON(String json){
        json = json.replaceAll("\\\\", "");
        json = json.substring(0, json.length() - 1);
        return json;
    }
}