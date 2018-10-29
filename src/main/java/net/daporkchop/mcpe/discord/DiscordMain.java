package net.daporkchop.mcpe.discord;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import net.daporkchop.mcpe.UtilsPE;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordMain {
    private static final List<String> list = new ArrayList<>();
    private static JDA jda;
    private static final Timer timer = new Timer();
    private static boolean dirty;

    private static Message message;

    public static void submitString(String input) {
        if (message == null || jda == null) {
            return;
        }

        input = TextFormat.clean(input.replaceAll("`", ""));
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
            dirty = true;
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
            String token = DiscordUtils.getToken().trim();
            if (token.isEmpty())     {
                return;
            }
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
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

                                if (event.getMember().isOwner() && msg.startsWith("!reboot"))   {
                                    UtilsPE.stopNow();
                                    return;
                                }

                                String message = TextFormat.colorize(
                                        "<&0[&9Discord&0] &r&f"
                                                + DiscordColors.getClosestTo(event.getMember().getColor()).ingame
                                                + event.getAuthor().getName().replaceAll("[^\\x20-\\x7e]", "?")
                                                + "&f> "
                                                + msg.replaceAll("[^\\x20-\\x7e]", "?"));
                                Server.getInstance().broadcastMessage(message);
                                submitString(TextFormat.clean(message));
                            } else if (event.getMessage().getContentRaw().startsWith("!players")) {
                                if (event.getChannel().getIdLong() == 412888996444635139L)  {
                                    return;
                                }
                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setTitle("Online players:", "http://2p2e.net");
                                builder.setColor(Color.BLACK);
                                builder.setTimestamp(LocalDateTime.now(ZoneId.of("UTC")));

                                if (Server.getInstance().getOnlinePlayers().size() == 0)    {
                                    builder.addField("0/" + Server.getInstance().getMaxPlayers(), "", false);
                                } else {
                                    StringBuilder builder1 = new StringBuilder();
                                    Server.getInstance().getOnlinePlayers().values().forEach(player -> builder1.append(player.getName() + ", "));
                                    builder.addField(Server.getInstance().getOnlinePlayers().size() + "/" + Server.getInstance().getMaxPlayers(), builder1.toString().substring(0, builder1.length() - 2), false);
                                }
                                event.getChannel().sendMessage(builder.build()).queue();
                            }
                        }
                    })
                    .buildBlocking();
            TextChannel channel = jda.getTextChannelById(412992591148220418L);
            if (channel != null) {
                message = channel.getMessageById(488246220863569940L).complete();
                try {
                    MessageHistory history;
                    do {
                        history = channel.getHistoryAfter(488246220863569940L, 99).complete();
                        if (!history.getRetrievedHistory().isEmpty()) {
                            channel.deleteMessages(history.getRetrievedHistory()).complete();
                        }
                    } while (history.size() == 99);
                } catch (IllegalStateException e) {
                    //shrug
                }
                loadFromInitialMessage();
                submitString("Server started!");

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        updateMessage();
                    }
                }, 2000, 2000);
            }
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
        if (!dirty || message == null || jda == null) {
            return;
        }
        synchronized (list) {
            String ok = "```\n";
            for (String s : list) {
                ok += s + "\n";
            }
            ok += "```";
            message.editMessage(ok).queue();
            dirty = false;
        }
    }

    public static final void updateMessageNow() {
        if (!dirty || message == null || jda == null) {
            return;
        }
        synchronized (list) {
            String ok = "```\n";
            for (String s : list) {
                ok += s + "\n";
            }
            ok += "```";
            message.editMessage(ok).complete();
            dirty = false;
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
        if (jda == null)    {
            return;
        }
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
