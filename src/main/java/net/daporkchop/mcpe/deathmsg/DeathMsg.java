package net.daporkchop.mcpe.deathmsg;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import net.daporkchop.mcpe.UtilsPE;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DeathMsg {
    private static final Map<EntityDamageEvent.DamageCause, BiFunction<String, EntityDamageEvent, String>> deathMessages = new EnumMap<>(EntityDamageEvent.DamageCause.class);

    static {
        deathMessages.put(EntityDamageEvent.DamageCause.CONTACT,
                (name, event) -> {
                    switch (UtilsPE.random(2)) {
                        case 0:
                            return "&b" + name + " &4was impaled on a cactus";
                        case 1:
                            return "&b" + name + " &4was pricked to death by a cactus";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                (name, event) -> {
                    if (event instanceof EntityDamageByEntityEvent) {
                        String attacker = ((EntityDamageByEntityEvent) event).getDamager().getName();
                        if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
                            Item item = ((Player) ((EntityDamageByEntityEvent) event).getDamager()).getInventory().getItemInHand();
                            if (!item.getCustomName().isEmpty()) {
                                String i = item.getCustomName();
                                switch (UtilsPE.random(3)) {
                                    case 0:
                                        return "&b" + name + " &4was killed by &b" + attacker + " &4using &b" + i;
                                    case 1:
                                        return "&b" + attacker + " &4killed &b" + name + " &4using &b" + i;
                                    case 2:
                                        return "&b" + name + " &4was slashed into gibs by &b" + attacker + "&4" + (attacker.endsWith("s") ? "'" : "'s") + " &b" + i;
                                }
                            }
                        }
                        switch (UtilsPE.random(3)) {
                            case 0:
                                return "&b" + name + " &4was killed by &b" + attacker;
                            case 1:
                                return "&b" + name + " &4was slashed into gibs by &b" + attacker;
                            case 2:
                                return "&b" + name + " &4was sent to their doom by &b" + attacker;
                        }
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.PROJECTILE,
                (name, event) -> {
                    if (event instanceof EntityDamageByEntityEvent) {
                        Entity shooter = ((EntityDamageByEntityEvent) event).getDamager();
                        if (shooter instanceof EntityLiving) {
                            String attacker = shooter.getName();
                            switch (UtilsPE.random(2)) {
                                case 0:
                                    return "&b" + name + " &4was shot by &b" + attacker;
                                case 1:
                                    return "&b" + name + " &4was struck by &b" + attacker + (attacker.endsWith("s") ? "&4'" : "&4's") + " arrow";
                            }
                        } else {
                            return "&b" + name + " &4was killed by a passing arrow";
                        }
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.SUFFOCATION,
                (name, event) -> {
                    switch (UtilsPE.random(2)) {
                        case 0:
                            return "&b" + name + " &4got their head stuck in a block";
                        case 1:
                            return "&b" + name + " &4was killed in a block";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.FALL,
                (name, event) -> {
                    switch (UtilsPE.random(4)) {
                        case 0:
                            return "&b" + name + " &4fell and crushed their lungs with their legbones";
                        case 1:
                            return "&b" + name + " &4tripped too hard and died";
                        case 2:
                            return "&b" + name + " &4had a lethal encounter with gravity";
                        case 3:
                            return "&b" + name + " &4fell too fast";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.FIRE,
                (name, event) -> {
                    switch (UtilsPE.random(2)) {
                        case 0:
                            return "&b" + name + " &4was burnt to a crisp";
                        case 1:
                            return "&b" + name + " &4burned too long";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.FIRE_TICK,
                deathMessages.get(EntityDamageEvent.DamageCause.FIRE));
        deathMessages.put(EntityDamageEvent.DamageCause.LAVA,
                (name, event) -> {
                    switch (UtilsPE.random(2)) {
                        case 0:
                            return "&b" + name + " &4took a swim in lava";
                        case 1:
                            return "&b" + name + " &4drowned in lava";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                (name, event) -> {
                    switch (UtilsPE.random(3)) {
                        case 0:
                            return "&b" + name + " &4was blasted to pieces";
                        case 1:
                            return "&b" + name + " &4exploded too hard";
                        case 2:
                            return "&b" + name + " &4blew up and away";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
                (name, event) -> {
                    if (event instanceof EntityDamageByEntityEvent) {
                        String killer = ((EntityDamageByEntityEvent) event).getDamager().getName();
                        switch (UtilsPE.random(3)) {
                            case 0:
                                return "&b" + name + " &4was blown up by &b" + killer;
                            case 1:
                                return "&b" + name + " &4was blasted to pieces by &b" + killer;
                            case 2:
                                return "&b" + killer + " &4blew up &b" + name;
                        }
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.VOID,
                (name, event) -> {
                    switch (UtilsPE.random(2)) {
                        case 0:
                            return "&b" + name + " &4fell through the world";
                        case 1:
                            return "&b" + name + " &4was taken by the void";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.SUICIDE,
                (name, event) -> {
                    switch (UtilsPE.random(4)) {
                        case 0:
                            return "&b" + name + " &4took the pill";
                        case 1:
                            return "&b" + name + " &4gave up and faded out of existence";
                        case 2:
                            return "&b" + name + " &4couldn't take it any more";
                        case 3:
                            return "&b" + name + " &4committed suicide";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.MAGIC,
                (name, event) -> {
                    switch (UtilsPE.random(2)) {
                        case 0:
                            return "&b" + name + " &4was killed by magic";
                        case 1:
                            return "&b" + name + " &4was magicked to death";
                    }
                    return "";
                });
        deathMessages.put(EntityDamageEvent.DamageCause.LIGHTNING,
                (name, event) -> {
                    switch (UtilsPE.random(2)) {
                        case 0:
                            return "&b" + name + " &4was fatally struck by lighting";
                        case 1:
                            return "&b" + name + " &4was electrocuted";
                    }
                    return "";
                });
    }

    public static final String getDeathMessage(Player player) {
        try {
            return TextFormat.colorize(deathMessages.get(player.getLastDamageCause().getCause())
                    .apply(player.getName(), player.getLastDamageCause()))
                    .replace("\n", "");
        } catch (NullPointerException e) {
            return player.getName() + " died";
        }
    }
}
