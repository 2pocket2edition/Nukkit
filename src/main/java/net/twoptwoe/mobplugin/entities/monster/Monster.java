package net.twoptwoe.mobplugin.entities.monster;

import cn.nukkit.entity.Entity;

public interface Monster {

    void attackEntity(Entity player);

    int getDamage();

    void setDamage(int damage);

    void setDamage(int[] damage);

    int getDamage(Integer difficulty);

    int getMinDamage();

    void setMinDamage(int damage);

    void setMinDamage(int[] damage);

    int getMinDamage(Integer difficulty);

    int getMaxDamage();

    void setMaxDamage(int damage);

    void setMaxDamage(int[] damage);

    int getMaxDamage(Integer difficulty);

    void setDamage(int damage, int difficulty);

    void setMinDamage(int damage, int difficulty);

    void setMaxDamage(int damage, int difficulty);

}
