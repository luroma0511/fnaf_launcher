package util.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class Discord {
    private final DiscordRichPresence presence = new DiscordRichPresence();
    private DiscordRPC lib;
    public boolean updateStatus;
    private boolean active;

    private String details = "";
    private String state = "";
    private String largeImageKey = "";

    public void start(String appID){
        lib = DiscordRPC.INSTANCE;
        var handlers = new DiscordEventHandlers();
        handlers.ready = _ -> System.out.println("Ready!");
        lib.Discord_Initialize(appID, handlers, true, null);
        active = true;
        updateStatus = true;
    }

    public void update(){
        if (!updateStatus || !active) return;
        presence.details = details;
        presence.state = state;
        presence.largeImageKey = largeImageKey;
        lib.Discord_UpdatePresence(presence);
        updateStatus = false;
    }

    public void setDetails(String details){
        this.details = details;
        updateStatus = true;
    }

    public void setState(String state){
        this.state = state;
        updateStatus = true;
    }

    public void setImageKey(String imageKey){
        this.largeImageKey = imageKey;
        updateStatus = true;
    }

    public void end(){
        lib.Discord_Shutdown();
        lib.Discord_ClearPresence();
        active = false;
        updateStatus = false;
    }
}
