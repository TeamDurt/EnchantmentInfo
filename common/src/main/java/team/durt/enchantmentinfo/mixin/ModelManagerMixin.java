package team.durt.enchantmentinfo.mixin;

import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.durt.enchantmentinfo.gui.ColorManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
    @Inject(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;startTick()V"
            )
    )
    private void onReload(PreparableReloadListener.PreparationBarrier preparationBarrier,
                          ResourceManager resourceManager,
                          ProfilerFiller profilerFiller1,
                          ProfilerFiller profilerFiller2,
                          Executor executor1,
                          Executor executor2,
                          CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ColorManager.getInstance().load(resourceManager, profilerFiller1);
    }
}
