package com.floweytf.monumentapaper.mixin.core.behaviour.spawner;

import com.floweytf.monumentapaper.accessor.EntityAccessor;
import com.floweytf.monumentapaper.accessor.SpawnerAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Flowey
 * @mm-patch 0025-Monumenta-Mobs-that-despawn-return-to-their-spawners.patch
 *
 * Mobs that despawn return to their spawners
 */
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Unique
    private boolean monumenta$isDespawnCandidate() {
        var spawner = ((EntityAccessor) this).getSpawner();

        return spawner != null &&
            getHealth() >= 1 &&
            this.getY() > 0 &&
            ((SpawnerAccessor) spawner).getBlockPos() != null;
    }

    @Unique
    public void monumenta$despawn() {
        if (monumenta$isDespawnCandidate()) {
            // Get the closest player to spawner
            var spawner = ((EntityAccessor) this).getSpawner();
            var delveReprime = ((EntityAccessor) this).getDelveReprime();

            var pos = ((SpawnerAccessor) spawner).getBlockPos();
            var player = level().getNearestPlayer(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                -1, false
            );

            if (player != null) {
                // Figure out how far player is from spawner.
                double dX = player.getX() - pos.getX();
                double dY = player.getY() - pos.getY();
                double dZ = player.getZ() - pos.getZ();
                double nearestPlayerDistanceSquared = dX * dX + dY * dY + dZ * dZ;
                if (nearestPlayerDistanceSquared >= 576) { // 24 * 24
                    // No players are next to the spawner the mob came from - reprime it
                    if (delveReprime) {
                        // Player died nearby in a delve -> reprime to 5s
                        spawner.spawnDelay = 100;
                    } else {
                        spawner.spawnDelay = 0;
                    }
                }
            }
        }

        discard();
    }

    @Redirect(
        method = "checkDespawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;discard()V"
        )
    )
    private void replaceDiscardDespawn(Mob instance) {
        monumenta$despawn();
    }
}
