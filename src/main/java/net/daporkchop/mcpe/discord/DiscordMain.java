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
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class DiscordMain {
    private final Queue<String> SEND_QUEUE = new ArrayDeque<>();
    private JDA jda;
    private final StringBuilder BUILDER = new StringBuilder(2000);
    private final AtomicInteger QUEUED_SENDS = new AtomicInteger(0);

    private TextChannel channel;

    private final boolean ENABLE = Boolean.parseBoolean(System.getProperty("2p2e.discord", "true"));

    public synchronized void submitString(String input) {
        if (!ENABLE)    {
            return;
        }

        input = TextFormat.clean(input.replaceAll("`", ""));
        if (input.length() >= 800) {
            return;
        }

        SEND_QUEUE.add(input);
    }

    public synchronized void start() {
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
                                if (msg.length() > 256) {
                                    event.getAuthor().openPrivateChannel().queue(ch -> {
                                        ch.sendMessage("Your message was too long to be relayed to Minecraft (must be max. 256 characters)").queue();
                                    });
                                    return;
                                }

                                String message = TextFormat.colorize(
                                        "<&0[&9Discord&0] &r&f"
                                                + DiscordColors.getClosestTo(event.getMember().getColor()).ingame
                                                + DiscordUtils.clean(event.getAuthor().getName())
                                                + "&f> "
                                                + DiscordUtils.clean(msg));
                                Server.getInstance().broadcastMessage(message);
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
    public synchronized void started()   {
        Server.getInstance().getScheduler().scheduleRepeatingTask(DiscordMain::updateStatus, 30 * 20, true); //30 seconds
        submitString("Server started!");
        Server.getInstance().getScheduler().scheduleRepeatingTask(() -> workOffQueue(false), 30, true); //1.5 seconds
    }

    private synchronized void updateStatus()  {
        if (ENABLE) {
            Server server = Server.getInstance();
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.of(Activity.ActivityType.WATCHING, String.format("%d/%d players", server.getOnlinePlayers().size(), server.getMaxPlayers())));
        }
    }

    private synchronized void workOffQueue(boolean sync) {
        if (!ENABLE || channel == null)    {
            SEND_QUEUE.clear();
            return;
        }
        BUILDER.setLength(0);
        while (SEND_QUEUE.peek() != null && BUILDER.length() + SEND_QUEUE.peek().length() + 1 <= 2000) {
            BUILDER.append(SEND_QUEUE.poll()).append('\n');
        }
        if (BUILDER.length() > 0) {
            MessageAction action = channel.sendMessage(BUILDER.toString());
            if (sync)   {
                action.complete();
            } else {
                action.queue();
            }
        }
    }

    public synchronized void shutdown(boolean stall) {
        if (!ENABLE || jda == null) {
            return;
        }
        submitString(stall ? "Server stalled, forcibly crashing" : "Server shutting down!");
        while (!SEND_QUEUE.isEmpty()) {
            workOffQueue(true);
        }
        jda.shutdown();
    }
}
