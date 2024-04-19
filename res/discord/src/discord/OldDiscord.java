package discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OldDiscord {
    private static boolean changeStatus;
    private static boolean printStatus;
    private static String details;
    private static String state;
    private static String imageKey;
    private static RichPresenceUpdater richPresenceUpdater;

    public static void init(){
        richPresenceUpdater = new RichPresenceUpdater();
        changeStatus = true;
        details = "Candy's 3 Deluxe v1.5";
        state = "State: Pre-Alpha";
        imageKey = "customnight";
        JDABuilder.createDefault("MTEyNjM2MTYxOTA5NDU4OTQ5MQ.G-L2Mw.R4grasjNT64YtWZoTnghQK5GMTGyTB6ScnHOzU")
                .addEventListeners(richPresenceUpdater)
                .build();
    }

    public static void setDetails(String details){
        OldDiscord.details = details;
        changeStatus = true;
    }

    public static void setState(String state){
        OldDiscord.state = state;
        changeStatus = true;
    }

    public static void setImageKey(String imageKey){
        OldDiscord.imageKey = imageKey;
        changeStatus = true;
    }

    public static void print(){
        if (richPresenceUpdater.getJDA() == null || !printStatus) return;
        Activity activity = richPresenceUpdater.getJDA().getPresence().getActivity();
        if (activity == null) return;
        System.out.println(activity.getName());
        System.out.println(activity.getState());
        System.out.println(imageKey);
        printStatus = false;
    }

    static class RichPresenceUpdater extends ListenerAdapter {
        private JDA jda;

        @Override
        public void onReady(@NotNull ReadyEvent event) {
            jda = event.getJDA();
            if (!changeStatus) return;
            Activity activity = Activity.streaming(details, imageKey)
                    .withState(state);
            jda.getPresence().setPresence(OnlineStatus.ONLINE, activity);
            System.out.println(jda.getUsers().get(0).getName());
            System.out.println();
            changeStatus = false;
            printStatus = true;
        }

        public JDA getJDA(){
            return jda;
        }
    }
}

