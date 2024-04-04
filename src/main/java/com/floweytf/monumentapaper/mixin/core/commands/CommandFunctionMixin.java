package com.floweytf.monumentapaper.mixin.core.commands;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Tristian
 * @mm-patch 0032-Monumenta-added-code-control-statements-to-mcfunctio.patch
 */
@Mixin(CommandFunction.class)
public interface CommandFunctionMixin {


    @Inject(method = "fromLines", at = @At(value = "INVOKE", target = "Ljava/lang/String;trim()Ljava/lang/String;"))
    private static <T extends ExecutionCommandSource<T>> void monumenta$codeBlocks(
            ResourceLocation id,
            CommandDispatcher<T> dispatcher,
            T source,
            List<String> lines,
            CallbackInfoReturnable<CommandFunction<T>> cir,
            @Local(name = "string") LocalRef<String> string,
            @Local(name = "i") int i,
            @Local(name = "j") LocalIntRef j) {
        final var str = string.get();
        if (monumenta$beginsBlock(str)) {
            boolean first = true;
            int nesting = 1;
            for (int k = i + 1; k < lines.size(); k++) {
                final var blockLine = lines.get(k);
                final var trimmed = blockLine.trim();
                string.set(string.get() + (first ? " " : "\n" + blockLine));
                first = false;
                if (trimmed.equals("}")) {
                    nesting--;
                    if (0 == nesting) {
                        break;
                    }
                } else if (monumenta$beginsBlock(str)) {
                    nesting++;
                }
            }
            if (nesting != 0) {
                throw new IllegalArgumentException("Unterminated block starting at line " + j.get());
            }
        }
        j.set(j.get()+1);
    }
    @Unique
    private static boolean monumenta$beginsBlock(String s) {
        return s.startsWith("run ") || s.startsWith("loop ") || s.endsWith("{");
    }
}

