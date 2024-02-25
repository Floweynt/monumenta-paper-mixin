package com.floweytf.monumentapaper.accessor;

import net.minecraft.core.BlockPos;

public interface SpawnerAccessor {
    BlockPos getBlockPos();

    void setBlockPos(BlockPos pos);
}
