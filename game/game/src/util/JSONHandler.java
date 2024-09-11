package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class JSONHandler {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public <T> String write(T data){
        return gson.toJson(data);
    }

    public abstract User readUser(String json);

    public abstract GuestScoreTable readScoreTable(String json);

    public Gson getGson() {
        return gson;
    }
}