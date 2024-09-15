package util.deluxe;

import util.JSONHandler;

public class CandysJSONHandler extends JSONHandler {
    @Override
    public CandysUser readUser(String json) {
        json = readJSON(json);
        return getGson().fromJson(json, CandysUser.class);
    }

    @Override
    public DeluxeGuestStoreTable readScoreTable(String json) {
        json = readJSON(json);
        return getGson().fromJson(json, DeluxeGuestStoreTable.class);
    }

    private String readJSON(String json){
        json = json.replaceAll("\\\\", "");
        json = json.substring(0, json.length() - 1);
        System.out.println(json);
        return json;
    }
}