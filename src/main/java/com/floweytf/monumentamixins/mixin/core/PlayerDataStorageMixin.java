package com.floweytf.monumentamixins.mixin.core;

import com.floweytf.monumentamixins.mixin.api.event.PlayerDataLoadEvent;
import com.floweytf.monumentamixins.mixin.api.event.PlayerDataSaveEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Files;

// https://github.com/TeamMonumenta/monumenta-paperfork/blob/1.19/patches/server/0003-Monumenta-Add-events-for-loading-and-saving-player-d.patch
@Mixin(PlayerDataStorage.class)
public class PlayerDataStorageMixin {
    @Unique
    private static final File FAKE_FILE = new File("") {
        @Override
        public boolean exists() {
            return false;
        }
    };

    @Shadow
    @Final
    private File playerDir;

    // TODO: this is a really ugly inject
    @Inject(method = "save", at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;createTempFile(Ljava/nio/file/Path;Ljava/lang/String;Ljava/lang/String;[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;"), cancellable = true)
    private void emitSaveEvent(Player player, CallbackInfo ci, @Local(ordinal = 0) CompoundTag tag) {
        System.out.println("Emitting save player event");
        var playerData = new File(this.playerDir, player.getStringUUID() + ".dat");
        var playerDataOld = new File(this.playerDir, player.getStringUUID() + ".dat_old");

        var event = new PlayerDataSaveEvent((CraftPlayer) (player.getBukkitEntity()), playerData, tag);

        if (!event.callEvent()) {
            ci.cancel();
            return;
        }

        try {
            var file = Files.createTempFile(this.playerDir.toPath(), player.getStringUUID() + "-", ".dat");
            NbtIo.writeCompressed((CompoundTag) event.getData(), file);
            Util.safeReplaceFile(event.getPath().toPath(), file, playerDataOld.toPath());
            ci.cancel();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @ModifyVariable(method = "load", at = @At(value = "LOAD", ordinal = 0), slice = @Slice(
        from = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;warning(Ljava/lang/String;)V"),
        to = @At(value = "INVOKE:LAST", target = "Ljava/io/File;exists()Z")
    ))
    private File emitLoadEvent(File file, Player player, @Local LocalRef<CompoundTag> tag) {
        System.out.println("Emitting load player event");
        PlayerDataLoadEvent event = new PlayerDataLoadEvent((CraftPlayer)(player.getBukkitEntity()), file);
        event.callEvent();
        if (event.getData() != null) {
            tag.set((CompoundTag) event.getData());
            // what the fuck? ugly hack...
            return FAKE_FILE;
        }

        return event.getPath();
    }
}
