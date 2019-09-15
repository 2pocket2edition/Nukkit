package net.twoptwoe.mobplugin;

import cn.nukkit.level.Level;

public enum EnumDimension {
    OVERWORLD,
    NETHER,
    THE_END;

    public static final EnumDimension getFromWorld(Level level) {
        String name = level.getName();
        switch (name) {
            case "nether":
                return NETHER;
            case "end":
                return THE_END;
            case "world":
            default:
                return OVERWORLD;
        }
    }
}
