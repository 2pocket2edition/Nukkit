package net.daporkchop.mcpe.discord;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordMain {
    private static final List<String> list = new ArrayList<>();
    private static JDA jda;
    private static Timer timer = new Timer();

    private static Message message;

    public static void submitString(String input) {
        input = TextFormat.clean(input);
        if (input.length() >= 800) {
            return;
        }

        synchronized (list) {
            if (totalStringLengths() + input.length() <= 800) {
                list.add(input);
            } else {
                int currLength = 0, currCount = 0, targetLength = input.length();
                for (String s : list) {
                    currLength += s.length();
                    currCount++;
                    if (currLength >= targetLength) {
                        break;
                    }
                }
                for (int i = 0; i < currCount; i++) {
                    list.remove(0);
                }
                list.add(input);
            }
        }
    }

    private static int totalStringLengths() {
        int toReturn = 0;
        for (String s : list) {
            toReturn += s.length();
        }
        return toReturn;
    }

    public static final void start() {
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(DiscordUtils.getToken())
                    .setStatus(OnlineStatus.ONLINE)
                    .setGame(Game.of(Game.GameType.STREAMING, "starting", "https://www.twitch.tv/daporkchop_"))
                    .addEventListener(new ListenerAdapter() {
                        @Override
                        public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
                            if (event.getChannel().getIdLong() == 412992591148220418L) {
                                event.getMessage().delete().queue();

                                String msg = event.getMessage().getContentRaw();
                                if (msg.length() > 255) {
                                    return;
                                }

                                String message = TextFormat.colorize(
                                        "<&0[&9Discord&0] "
                                                + DiscordColors.getClosestTo(event.getMember().getColor()).ingame
                                                + event.getAuthor().getName().replaceAll("[^\\x20-\\x7e]", "?")
                                                + "&f> "
                                                + msg
                                                    .replaceAll("[^\\x20-\\x7e]", "?"));
                                Server.getInstance().broadcastMessage(message);
                                submitString(TextFormat.clean(message));
                            }
                        }
                    })
                    .buildBlocking();
            TextChannel channel = jda.getTextChannelById(412992591148220418L);
            message = channel.getMessageById(413020177513447434L).complete();
            loadFromInitialMessage();
            submitString("Server started!");

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateMessage();
                }
            }, 2000, 2000);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Server server = Server.getInstance();
                    jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, "Online " + server.getOnlinePlayers().size() + "/" + server.getMaxPlayers(), "https://www.twitch.tv/daporkchop_"));
                }
            }, 10000, 60000);
        } catch (Throwable t) {
            t.printStackTrace();
            Runtime.getRuntime().exit(0);
            //just in case
            throw new IllegalStateException(t);
        }
    }

    public static final void updateMessage() {
        synchronized (list) {
            String ok = "```\n";
            for (String s : list) {
                ok += s + "\n";
            }
            ok += "```";
            message.editMessage(ok).queue();
        }
    }

    public static final void updateMessageNow() {
        synchronized (list) {
            String ok = "```\n";
            for (String s : list) {
                ok += s + "\n";
            }
            ok += "```";
            message.editMessage(ok).complete();
        }
    }

    public static final void loadFromInitialMessage() {
        String msg = message.getContentRaw();
        if (!msg.startsWith("```\n")) {
            return;
        }
        String[] split = msg.split("\n");
        for (int i = 1; i < split.length - 1; i++) {
            list.add(split[i]);
        }
    }

    public static final void shutdown() {
        timer.purge();
        timer.cancel();
        submitString("Server shutting down!");
        updateMessageNow();
        jda.shutdown();
    }

    //debug code below
    public static void main(String... args) {
        start();

        sendDatMessage();

        shutdown();
    }

    public static void leaveAllServers() {
        List<Guild> guilds = new ArrayList<>(jda.getGuilds());
        System.out.println("Leaving " + guilds.size() + " guilds");
        for (Guild guild : guilds) {
            guild.leave().queue();
        }
        System.out.println("Done!");
    }

    public static void sendDatMessage() {
        TextChannel channel = jda.getTextChannelById(412992591148220418L);
        channel.sendMessage("```\nhello\nthere\n```").queue();
    }
}
