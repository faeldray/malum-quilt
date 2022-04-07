package ca.rttv.malum.util.handler;

import ca.rttv.malum.config.ClientConfig;
import ca.rttv.malum.util.ExtendedShader;
import ca.rttv.malum.util.RenderLayers;
import ca.rttv.malum.util.ShaderUniformHandler;
import ca.rttv.malum.util.helper.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.util.HashMap;

public class RenderHandler {
    public static final HashMap<RenderLayer, BufferBuilder> BUFFERS = new HashMap<>();
    public static final HashMap<RenderLayer, ShaderUniformHandler> HANDLERS = new HashMap<>();
    public static VertexConsumerProvider.Immediate DELAYED_RENDER;
    public static Matrix4f PARTICLE_MATRIX = null;

    public static void init() {
        DELAYED_RENDER = VertexConsumerProvider.immediate(BUFFERS, new BufferBuilder(256));
    }

    public static void renderLast(MatrixStack stack) {
        stack.push();
        if (ClientConfig.DELAYED_PARTICLE_RENDERING) {
            RenderSystem.getModelViewStack().push();
            RenderSystem.getModelViewStack().loadIdentity();
            if (PARTICLE_MATRIX != null) RenderSystem.getModelViewStack().mulPoseMatrix(PARTICLE_MATRIX);
            RenderSystem.applyModelViewMatrix();
            DELAYED_RENDER.draw(RenderLayers.ADDITIVE_PARTICLE);
            DELAYED_RENDER.draw(RenderLayers.ADDITIVE_BLOCK_PARTICLE);
            RenderSystem.getModelViewStack().pop();
            RenderSystem.applyModelViewMatrix();
        }
        for (RenderLayer layer : BUFFERS.keySet()) {
            Shader shader = RenderHelper.getShader(layer);
            if (HANDLERS.containsKey(layer)) {
                ShaderUniformHandler handler = HANDLERS.get(layer);
                handler.updateShaderData(shader);
            }
            DELAYED_RENDER.draw(layer);

            if (shader instanceof ExtendedShader extendedShaderInstance) {
                extendedShaderInstance.setUniformDefaults();
            }
        }
        DELAYED_RENDER.draw();
        stack.pop();
    }
}
