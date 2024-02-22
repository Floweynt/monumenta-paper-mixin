package com.floweytf.monumentapaper.accessor;

import net.minecraft.world.level.BaseSpawner;

public interface EntityAccessor {
    BaseSpawner getSpawner();

    void setSpawner(BaseSpawner spawner);

    boolean getDelveReprime();

    void setDelveReprime(boolean delveReprime);
}
