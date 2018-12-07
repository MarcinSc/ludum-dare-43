package com.gempukku.secsy.gaming.rendering.postprocess.tint.color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderPipeline;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(
        profiles = "colorTint")
public class ColorTintPostProcessor extends AbstractLifeCycleSystem {
    @Inject
    private EasingResolver easingResolver;
    @Inject
    private TimeManager timeManager;

    private ShaderProgram shaderProgram;
    private VertexBufferObject vertexBufferObject;
    private IndexBufferObject indexBufferObject;

    @Override
    public void initialize() {
        shaderProgram = new ShaderProgram(
                Gdx.files.internal("shader/viewToScreenCoords.vert"),
                Gdx.files.internal("shader/colorTint.frag"));
        if (!shaderProgram.isCompiled())
            throw new IllegalArgumentException("Error compiling shader: " + shaderProgram.getLog());

        float[] verticeData = new float[]{
                0, 0, 0,
                0, 1, 0,
                1, 0, 0,
                1, 1, 0};
        short[] indices = {0, 1, 2, 2, 1, 3};

        vertexBufferObject = new VertexBufferObject(true, 4, VertexAttribute.Position());
        indexBufferObject = new IndexBufferObject(true, indices.length);
        vertexBufferObject.setVertices(verticeData, 0, verticeData.length);
        indexBufferObject.setIndices(indices, 0, indices.length);
    }

    @ReceiveEvent(priorityName = "gaming.renderer.tint.color")
    public void processTint(RenderToPipeline renderToPipeline, EntityRef renderingEntity, ColorTintComponent tint) {
        long time = timeManager.getTime();
        long effectStart = tint.getEffectStart();
        long effectDuration = tint.getEffectDuration();

        if (effectStart <= time && time < effectStart + effectDuration) {
            float alpha = 1f * (time - effectStart) / effectDuration;
            float factor = easingResolver.resolveValue(tint.getAlpha(), alpha);

            if (factor > 0) {
                RenderPipeline renderPipeline = renderToPipeline.getRenderPipeline();

                FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();

                int width = currentBuffer.getWidth();
                int height = currentBuffer.getHeight();
                Color color = tint.getColor();

                FrameBuffer newBuffer = renderPipeline.getNewFrameBuffer(currentBuffer);

                newBuffer.begin();

                shaderProgram.begin();

                vertexBufferObject.bind(shaderProgram);
                indexBufferObject.bind();

                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 0);
                Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, currentBuffer.getColorBufferTexture().getTextureObjectHandle());

                shaderProgram.setUniformf("u_sourceTexture", 0);
                shaderProgram.setUniformf("u_factor", factor);
                shaderProgram.setUniformf("u_color", color);

                Gdx.gl20.glDrawElements(Gdx.gl20.GL_TRIANGLES, indexBufferObject.getNumIndices(), GL20.GL_UNSIGNED_SHORT, 0);
                vertexBufferObject.unbind(shaderProgram);
                indexBufferObject.unbind();

                shaderProgram.end();

                newBuffer.end();

                renderPipeline.returnFrameBuffer(currentBuffer);
                renderPipeline.setCurrentBuffer(newBuffer);
            }
        }
    }

    @Override
    public void postDestroy() {
        vertexBufferObject.dispose();
        indexBufferObject.dispose();
        shaderProgram.dispose();
    }
}
