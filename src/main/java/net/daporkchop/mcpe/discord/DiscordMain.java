package net.daporkchop.mcpe.discord;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import net.daporkchop.mcpe.UtilsPE;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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

@UtilityClass
public class DiscordMain {
    private final Queue<String> SEND_QUEUE = new ConcurrentLinkedQueue<>();
    private JDA jda;
    private final StringBuilder BUILDER = new StringBuilder(2000);

    private TextChannel channel;

    private final boolean ENABLE = Boolean.parseBoolean(System.getProperty("2p2e.discord", "true"));

    public void submitString(String input) {
        if (!ENABLE)    {
            return;
        }

        input = TextFormat.clean(input.replaceAll("`", ""));
        if (input.length() >= 800) {
            return;
        }

        SEND_QUEUE.add(input);
    }

    public void start() {
        if (!ENABLE)    {
            return;
        }

        try {
            String token = DiscordUtils.getToken().trim();
            Preconditions.checkState(!token.isEmpty(), "Discord token is not set!");
            jda = JDABuilder.createLight(token)
                    .setStatus(OnlineStatus.IDLE)
                    .setActivity(Activity.of(Activity.ActivityType.DEFAULT, "starting..."))
                    .addEventListeners(new ListenerAdapter() {
                        @Override
                        public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
                            if (event.getAuthor().getIdLong() == 259335296947191808L)   {
                                return;
                            }

                            String msg = event.getMessage().getContentRaw();
                            if (event.getAuthor().getIdLong() == 226975061880471552L && msg.startsWith("!reboot")) {
                                UtilsPE.stopNow();
                                return;
                            }

                            if (event.getChannel().getIdLong() == 412992591148220418L) {
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
                    .build().awaitReady();
            channel = jda.getTextChannelById(412992591148220418L);
        } catch (Throwable t) {
            t.printStackTrace();
            Runtime.getRuntime().exit(0);
            //just in case
            throw new IllegalStateException(t);
        }
    }

    @SuppressWarnings("deprecation")
    public void started()   {
        Server.getInstance().getScheduler().scheduleRepeatingTask(DiscordMain::updateStatus, 30 * 20, true); //30 seconds
        submitString("Server started!");
        Server.getInstance().getScheduler().scheduleRepeatingTask(() -> workOffQueue(false), 30, true); //1.5 seconds
    }

    private static void updateStatus()  {
        if (ENABLE) {
            Server server = Server.getInstance();
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.of(Activity.ActivityType.WATCHING, String.format("%d/%d players", server.getOnlinePlayers().size(), server.getMaxPlayers())));
        }
    }

    private static void workOffQueue(boolean sync) {
        if (!ENABLE || channel == null)    {
            SEND_QUEUE.clear();
            return;
        }
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
        if (!ENABLE || jda == null) {
            return;
        }
        submitString("Server shutting down!");
        while (!SEND_QUEUE.isEmpty()) {
            workOffQueue(true);
        }
        jda.shutdown();
    }
}
