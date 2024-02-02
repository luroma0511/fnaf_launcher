package game.api;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class DiscordRichPresenceAPI {
    DiscordRichPresence presence;
    DiscordRPC lib;
    DiscordEventHandlers handlers;
    Thread callbackThread;

    public DiscordRichPresenceAPI(){
        presence = new DiscordRichPresence();
        lib = DiscordRPC.INSTANCE;
        String appID = "1039601955888189511";
        handlers = new DiscordEventHandlers();
        handlers.ready = user -> System.out.println("Ready!");
        lib.Discord_Initialize(appID, handlers, true, null);
    }

    public void resetTime(){
        presence.startTimestamp = System.currentTimeMillis() / 1000;
    }

    public void update(String state, String gameMode, int hour, boolean cheats){
        String details = "In Menu";
        String states = "Game Mode: " + gameMode;
        String value = "Disabled";
        if (cheats){
            value = "Enabled";
        }

        if (hour == 0){
            hour = 12;
        }

        String cheat = "Cheats: " + value;
        switch (state) {
            case "load":
                details = "Loading Game";
                break;
            case "game":
                details = "Time: " + hour + " AM | " + cheat;
                break;
            case "win":
                details = "Time: " + hour + " AM | " + cheat;
                states = "Game Mode Beaten: " + gameMode;
                break;
            case "gameover":
                details = "Time: " + hour + " AM | " + cheat;
                states = "Game Over!";
                break;
        }
        presence.details = details;
        presence.state = states;
        lib.Discord_UpdatePresence(presence);
    }

    public void startThread(){
        callbackThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored){
                    System.out.println("Terminated");
                }
            }
        }, "RPC-Callback-Handler");
        callbackThread.setDaemon(true);
        callbackThread.start();
    }

    public void end(){
        callbackThread.interrupt();
        lib.Discord_Shutdown();
        lib.Discord_ClearPresence();
    }
}
