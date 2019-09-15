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
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DiscordMain {
    private static final Queue<String> SEND_QUEUE = new ConcurrentLinkedQueue<>();
    private static JDA jda;
    private static final Timer         timer   = new Timer();
    private static final StringBuilder BUILDER = new StringBuilder(2000);

    private static TextChannel channel;

    public static void submitString(String input) {
        input = TextFormat.clean(input.replaceAll("`", ""));
        if (input.length() >= 800) {
            return;
        }

        SEND_QUEUE.add(input);
    }

    public static final void start() {
        try {
            String token = DiscordUtils.getToken().trim();
            if (token.isEmpty()) {
                return;
            }
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setStatus(OnlineStatus.ONLINE)
                    .setGame(Game.of(Game.GameType.STREAMING, "starting", "https://www.twitch.tv/daporkchop_"))
                    .addEventListener(new ListenerAdapter() {
                        @Override
                        public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
                            String msg = event.getMessage().getContentRaw();
                            if (event.getAuthor().getIdLong() == 226975061880471552L && msg.startsWith("!reboot")) {
                                UtilsPE.stopNow();
                                return;
                            }

                            if (event.getChannel().getIdLong() == 412992591148220418L) {
                                event.getMessage().delete().queue();

                                if (msg.length() > 255) {
                                    return;
                                }

                                String message = TextFormat.colorize(
                                        "<&0[&9Discord&0] &r&f"
                                                + DiscordColors.getClosestTo(event.getMember().getColor()).ingame
                                                + event.getAuthor().getName().replaceAll("[^\\x20-\\xff]", "?")
                                                + "&f> "
                                                + msg.replaceAll("[^\\x20-\\xff]", "?"));
                                Server.getInstance().broadcastMessage(message);
                                //submitString(TextFormat.clean(message));
                            } else if (event.getMessage().getContentRaw().startsWith("!players")) {
                                if (event.getChannel().getIdLong() == 412888996444635139L) {
                                    return;
                                }
                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setTitle("Online players:", "https://2p2e.net");
                                builder.setColor(Color.BLACK);
                                builder.setTimestamp(LocalDateTime.now(ZoneId.of("UTC")));

                                if (Server.getInstance().getOnlinePlayers().isEmpty()) {
                                    builder.addField("0/" + Server.getInstance().getMaxPlayers(), "", false);
                                } else {
                                    StringJoiner joiner = new StringJoiner(", ");
                                    Server.getInstance().getOnlinePlayers().values().forEach(player -> joiner.add(player.getName()));
                                    builder.addField(Server.getInstance().getOnlinePlayers().size() + "/" + Server.getInstance().getMaxPlayers(), joiner.toString(), false);
                                }
                                event.getChannel().sendMessage(builder.build()).queue();
                            }
                        }
                    })
                    .buildBlocking();
            channel = jda.getTextChannelById(412992591148220418L);
            if (channel != null) {
                submitString("Server started!");

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        workOffQueue(false);
                    }
                }, 2000, 2000);
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Server server = Server.getInstance();
                    jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, "Online " + server.getOnlinePlayers().size() + "/" + server.getMaxPlayers(), "https://www.twitch.tv/daporkchop_"));
                }
            }, 10000, 30000);
        } catch (Throwable t) {
            t.printStackTrace();
            Runtime.getRuntime().exit(0);
            //just in case
            throw new IllegalStateException(t);
        }
    }

    private static void workOffQueue(boolean sync) {
        BUILDER.setLength(0);
        while (SEND_QUEUE.peek() != null && BUILDER.length() + SEND_QUEUE.peek().length() + 1 <= 2000) {
            BUILDER.append(SEND_QUEUE.poll()).append('\n');
        }
        if (!sync) {
            SEND_QUEUE.clear(); //if we're getting more than 2000 chars per second there's a good chance something is going wrong
        }
        if (BUILDER.length() > 0) {
            channel.sendMessage(BUILDER.toString()).queue();
        }
    }

    public static void shutdown() {
        if (jda == null) {
            return;
        }
        timer.purge();
        timer.cancel();
        submitString("Server shutting down!");
        while (!SEND_QUEUE.isEmpty()) {
            workOffQueue(true);
        }
        jda.shutdown();
    }

    public static void leaveAllServers() {
        List<Guild> guilds = new ArrayList<>(jda.getGuilds());
        System.out.println("Leaving " + guilds.size() + " guilds");
        for (Guild guild : guilds) {
            guild.leave().queue();
        }
        System.out.println("Done!");
    }
}
