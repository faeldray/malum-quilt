package ca.rttv.malum.mixin;

import ca.rttv.malum.util.handler.ScreenParticleHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ca.rttv.malum.util.particle.screen.base.ScreenParticle.RenderOrder.BEFORE_TOOLTIPS;

@Mixin(HandledScreen.class)
final class HandledScreenMixin {
    @Inject(at = @At("RETURN"), method = "render")
    private void beforeTooltipParticle(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ScreenParticleHandler.renderParticles(BEFORE_TOOLTIPS);
    }
}
