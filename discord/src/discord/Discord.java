package discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Discord {
    static DiscordRichPresence discordRichPresence;

    public static void init() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
        }).build();
        DiscordRPC.discordInitialize("1126361619094589491", handlers, true);
        discordRichPresence = new DiscordRichPresence();
        discordRichPresence.details = "Details";
        discordRichPresence.state = "state";
        discordRichPresence.largeImageKey = "customnight";
        DiscordRPC.discordUpdatePresence(discordRichPresence);
        DiscordRPC.discordRunCallbacks();
    }

    public static void print(){
    }
}
