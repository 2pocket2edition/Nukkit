package net.daporkchop.mcpe.discord;

import cn.nukkit.utils.TextFormat;

import java.awt.*;

public enum DiscordColors {
    BLACK(0x000000, TextFormat.BLACK),
    DARK_BLUE(0x0000AA, TextFormat.DARK_BLUE),
    DARK_GREEN(0x00AA00, TextFormat.DARK_GREEN),
    DARK_AQUA(0x00AAAA, TextFormat.DARK_AQUA),
    DARK_RED(0xAA0000, TextFormat.DARK_RED),
    DARK_PURPLE(0xAA00AA, TextFormat.DARK_PURPLE),
    GOLD(0xFFAA00, TextFormat.GOLD),
    GRAY(0xAAAAAA, TextFormat.GRAY),
    DARK_GRAY(0x555555, TextFormat.DARK_GRAY),
    BLUE(0x5555FF, TextFormat.BLUE),
    GREEN(0x55FF55, TextFormat.GREEN),
    AQUA(0x55FFFF, TextFormat.AQUA),
    RED(0xFF5555, TextFormat.RED),
    LIGHT_PURPLE(0xFF55FF, TextFormat.LIGHT_PURPLE),
    YELLOW(0xFFFF55, TextFormat.YELLOW),
    WHITE(0xFFFFFF, TextFormat.WHITE);

    public final Color color;
    public final TextFormat ingame;

    DiscordColors(int color, TextFormat ingame)  {
        this.color = new Color(color);
        this.ingame = ingame;
    }

    public static final DiscordColors getClosestTo(Color color) {
        double distance = Double.MAX_VALUE;
        DiscordColors toReturn = null;

        for (DiscordColors test : values())   {
            double newDistance = getDistance(color, test.color);
            if (newDistance < distance) {
                toReturn = test;
                distance = newDistance;
            }
        }
        return toReturn;
    }

    public static final double getDistance(Color a, Color b)  {
        return Math.pow(a.getRed() - b.getRed(), 2)
                + Math.pow(a.getGreen() - b.getGreen(), 2)
                + Math.pow(a.getBlue() - b.getBlue(), 2);
    }
}
