package util.gamejolt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import util.JSONHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamejoltManager {
    private static final HttpRequestBuilder builder = new HttpRequestBuilder();
    private static Map<String, String> httpResponses;
    private static boolean httpWait;
    private static MessageDigest crypt;

    private final ExecutorService executorService;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static final Timer pingTimer = new Timer();

    public final DataStore dataStore;
    public final Session session;
    public final Score score;
    public final Trophy trophy;

    private final String gameID;
    private final String key;
    public final String username;
    private final String token;
    public final String id;

    private static final String baseURL = "https://api.gamejolt.com/api/game/";

    public GamejoltManager(String gameID, String key, String username, String token, String id){
        executorService = Executors.newSingleThreadExecutor();
        try {
            crypt = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        httpResponses = new HashMap<>();
        dataStore = new DataStore();
        session = new Session();
        score = new Score();
        trophy = new Trophy();

        this.gameID = gameID;
        this.key = key;
        this.username = username;
        this.token = token;
        this.id = id;
    }

    public void initialize(){
        execute(() -> {
            if (session.check(this)) session.open(this);
            session.ping(this);
            session.pinged = true;
        });
    }

    public static class DataStore {
        private String fetch = "";
        public boolean loaded;

        private String baseURL(GamejoltManager gamejoltManager, String key){
            return "?game_id=" + gamejoltManager.gameID + "&key=" + key;
        }

        public void set(GamejoltManager gamejoltManager, String key, String value) {
            String url = baseURL + "v1_2/data-store/set/"
                    + baseURL(gamejoltManager, key)
                    + "&data=" + URLEncoder.encode(value, StandardCharsets.UTF_8)
                    + "&format=json";
            while (!httpResponses.containsKey("data-set")) gamejoltManager.sendRequest(url, "data-set");
            httpResponses.remove("data-set");
        }

        public void fetch(GamejoltManager gamejoltManager, String key) {
            httpResponses.remove("data-fetch");
            String url = baseURL + "v1/data-store/"
                    + baseURL(gamejoltManager, key);
            while (!httpResponses.containsKey("data-fetch")) gamejoltManager.sendRequest(url, "data-fetch");
            if (httpResponses.get("data-fetch").contains("message:")) return;
            String httpResponse = httpResponses.get("data-fetch");
            fetch = httpResponse.substring(httpResponse.indexOf("data:") + 6, httpResponse.length() - 2);
        }

        public void remove(GamejoltManager gamejoltManager, String key) {
            String url = baseURL + "v1/data-store/remove/"
                    + baseURL(gamejoltManager, key);
            while (!httpResponses.containsKey("data-remove")) gamejoltManager.sendRequest(url, "data-remove");
            httpResponses.remove("data-remove");
        }

        public String getFetch() {
            return fetch;
        }
    }

    public static class Session {
        private boolean pinged;

        private String baseURL(GamejoltManager gamejoltManager){
            return "?game_id=" + gamejoltManager.gameID
                    + "&username=" + gamejoltManager.username
                    + "&user_token=" + gamejoltManager.token;
        }

        private void open(GamejoltManager gamejoltManager) {
            String url = baseURL + "v1/sessions/open/"
                    + baseURL(gamejoltManager);
            gamejoltManager.sendRequest(url, "session-open");
        }

        private void close(GamejoltManager gamejoltManager) {
            String url = baseURL + "v1/sessions/close/"
                    + baseURL(gamejoltManager);
            gamejoltManager.sendRequest(url, "session-close");
        }

        private void ping(GamejoltManager gamejoltManager) {
            pingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    String url = baseURL + "v1/sessions/ping/"
                            + baseURL(gamejoltManager);
                    System.out.println("ping time");
                    gamejoltManager.sendRequest(url, "session-ping");
                }
            }, 0, 30_000);
        }

        private boolean check(GamejoltManager gamejoltManager) {
            String url = baseURL + "v1_2/sessions/check/"
                    + baseURL(gamejoltManager);
            gamejoltManager.sendRequest(url, "session-check");
            String httpResponse = httpResponses.get("session-check");
            return httpResponse == null || !httpResponse.substring(httpResponse.indexOf("success") + 10, httpResponse.lastIndexOf("\"")).equals("true");
        }

        public boolean isPinged() {
            return pinged;
        }
    }

    public static class Score {
        private final String sortURL = "&sort=";

        private String baseURL(GamejoltManager gamejoltManager){
            return "?game_id=" + gamejoltManager.gameID;
        }

        public void add(GamejoltManager gamejoltManager, String tableID, String sort, String score){
            if (sort.equals("0")) return;
            String url = baseURL + "v1/scores/add/"
                    + baseURL(gamejoltManager)
                    + sortURL + sort
                    + "&score=" + score
                    + "&username=" + gamejoltManager.username
                    + "&user_token=" + gamejoltManager.token
                    + "&table_id=" + tableID;
            while (!httpResponses.containsKey("score-add")) gamejoltManager.sendRequest(url, "score-add");
            httpResponses.remove("score-add");
            System.out.println("Added score: " + score);
        }

        public util.gamejolt.Score fetch(GamejoltManager gamejoltManager, JSONHandler jsonHandler, String tableID){
            httpResponses.remove("score-fetch");
            String url = baseURL + "v1_2/scores/"
                    + baseURL(gamejoltManager)
                    + "&username=" + gamejoltManager.username
                    + "&user_token=" + gamejoltManager.token
                    + "&table_id=" + tableID;
            while (!httpResponses.containsKey("score-fetch")) gamejoltManager.sendRequest(url, "score-fetch");
            String score = httpResponses.get("score-fetch");
            if (score == null) return null;
            score = score.substring(score.indexOf("score") + 9, score.length() - 3);
            if (score.isEmpty()) return null;
            return jsonHandler.getGson().fromJson(score, util.gamejolt.Score.class);
        }
    }

    public static class Trophy {
        private boolean addProcessing;
        private final Stack<String> trophyIDs = new Stack<>();
        private final Stack<String> newTrophies = new Stack<>();
        private final Stack<util.gamejolt.Trophy> unlockedTrophies = new Stack<>();
        private final Stack<util.gamejolt.Trophy> allTrophies = new Stack<>();

        private String baseURL(GamejoltManager gamejoltManager){
            return "?game_id=" + gamejoltManager.gameID
                    + "&username=" + gamejoltManager.username
                    + "&user_token=" + gamejoltManager.token;
        }

        public void add(GamejoltManager gamejoltManager) {
            String url = baseURL + "v1/trophies/add-achieved/"
                    + baseURL(gamejoltManager)
                    + "&trophy_id=" + trophyIDs.getFirst();
            addProcessing = true;
            while (!httpResponses.containsKey("trophy-add")) gamejoltManager.sendRequest(url, "trophy-add");
            if (!httpResponses.get("trophy-add").contains("message:")) newTrophies.addLast(trophyIDs.getFirst());
            trophyIDs.removeFirst();
            httpResponses.remove("trophy-add");
            addProcessing = false;
        }

        public void remove(GamejoltManager gamejoltManager, String trophyID) {
            String url = baseURL + "v1_2/trophies/remove-achieved/"
                    + baseURL(gamejoltManager)
                    + "&trophy_id=" + trophyID;
            gamejoltManager.sendRequest(url, "trophy-remove");
        }

        public boolean fetch(GamejoltManager gamejoltManager, JSONHandler jsonHandler, String stackID) {
            httpResponses.remove("trophy-fetch");
            String url = baseURL + "v1_2/trophies/"
                    + baseURL(gamejoltManager);
            if (stackID != null) url += "&trophy_id=" + stackID;
            url += "&format=json";
            while (!httpResponses.containsKey("trophy-fetch")) gamejoltManager.sendRequest(url, "trophy-fetch");
            String response = httpResponses.get("trophy-fetch");
            if (response == null){
                Gdx.app.error("GameJolt API", "Failed to fetch trophies");
                return false;
            }
            String jsonResponse = response.substring(response.indexOf(":{") + 1, response.lastIndexOf("}"));
            Gdx.app.log("JSON Response", jsonResponse);
            ResponseTrophy responseTrophy = jsonHandler.getGson().fromJson(jsonResponse, ResponseTrophy.class);
            if (responseTrophy.success) {
                if (stackID == null) {
                    for (util.gamejolt.Trophy trophy : responseTrophy.trophies) {
                        getAllTrophies().addLast(trophy);
                    }
                } else unlockedTrophies.addLast(responseTrophy.trophies[0]);
                return true;
            }
            Gdx.app.error("GameJolt API", "Failed to fetch trophies");
            return false;
        }

        public boolean isAddProcessing() {
            return addProcessing;
        }

        public void addID(String trophyID){
            trophyIDs.addLast(trophyID);
        }

        public boolean isIDsEmpty(){
            return trophyIDs.empty();
        }

        public Stack<String> getNewTrophies() {
            return newTrophies;
        }

        public Stack<util.gamejolt.Trophy> getAllTrophies() {
            return allTrophies;
        }

        public Stack<util.gamejolt.Trophy> getUnlockedTrophies() {
            return unlockedTrophies;
        }

        public util.gamejolt.Trophy retrieveNext(){
            util.gamejolt.Trophy trophy = unlockedTrophies.getFirst();
            unlockedTrophies.removeFirst();
            return trophy;
        }
    }

    private static String encrypt(String message){
        crypt.reset();
        try {
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] digest = crypt.digest(bytes);
            StringBuilder sb = new StringBuilder();

            for (byte b : digest) {
                sb.append(Integer.toHexString(b & 255 | 256), 1, 3);
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void httpLock(){
        while (httpWait){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }
    }

    private void sendRequest(String url, String key){
        httpLock();
        httpWait = true;
        String signature = encrypt(url + this.key);
        url += "&signature=" + signature;
        Net.HttpRequest request = builder.newRequest()
                .method("GET")
                .url(url)
                .build();
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                httpResponses.put(key, httpResponse.getResultAsString());
                System.out.println(httpResponses.get(key));
                httpWait = false;
            }

            @Override
            public void failed(Throwable t) {
                System.err.println("The request failed...");
                httpWait = false;
            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP Request", "Request cancelled");
            }
        });
        httpLock();
    }

    public void execute(Runnable runnable){
        executorService.execute(() -> {
            running.set(true);
            runnable.run();
            running.set(false);
        });
    }

    public boolean threadRunning(){
        return running.get();
    }

    public void dispose() {
        execute(() -> {
            if (session.check(this)) session.close(this);
            pingTimer.cancel();
        });
        executorService.shutdown();
    }
}