package com.gempukku.secsy.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.math.MathUtils;
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
        profiles = "gaussianBlur")
public class GaussianBlurPostProcessor extends AbstractLifeCycleSystem {
    public static final int MAX_BLUR_RADIUS = 16;
    private static final float[][] kernelCache = new float[1 + MAX_BLUR_RADIUS][];

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
                Gdx.files.internal("shader/gaussianBlur.frag"));
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

    @ReceiveEvent(priorityName = "gaming.renderer.gaussianBlur")
    public void render(RenderToPipeline renderToPipeline, EntityRef renderingEntity, GaussianBlurComponent blur) {
        long time = timeManager.getTime();
        long effectStart = blur.getEffectStart();
        long effectDuration = blur.getEffectDuration();

        if (effectStart <= time && time < effectStart + effectDuration) {
            float alpha = 1f * (time - effectStart) / effectDuration;
            int blurRadius = MathUtils.round(easingResolver.resolveValue(blur.getBlurRadius(), alpha));
            if (blurRadius > 0) {
                float[] kernel = getKernel(blurRadius);
                RenderPipeline renderPipeline = renderToPipeline.getRenderPipeline();

                FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();

                shaderProgram.begin();
                shaderProgram.setUniformf("u_sourceTexture", 0);
                shaderProgram.setUniformi("u_blurRadius", blurRadius);
                shaderProgram.setUniformf("u_pixelSize", 1f / currentBuffer.getWidth(), 1f / currentBuffer.getHeight());
                shaderProgram.setUniform1fv("u_kernel", kernel, 0, kernel.length);

                vertexBufferObject.bind(shaderProgram);
                indexBufferObject.bind();

                shaderProgram.setUniformf("u_vertical", 1);
                executeBlur(renderPipeline);
                shaderProgram.setUniformf("u_vertical", 0);
                executeBlur(renderPipeline);

                vertexBufferObject.unbind(shaderProgram);
                indexBufferObject.unbind();

                shaderProgram.end();
            }
        }
    }

    @Override
    public void destroy() {
        vertexBufferObject.dispose();
        indexBufferObject.dispose();
        shaderProgram.dispose();
    }

    private void executeBlur(RenderPipeline renderPipeline) {
        FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();
        int textureHandle = currentBuffer.getColorBufferTexture().getTextureObjectHandle();

        FrameBuffer frameBuffer = renderPipeline.getNewFrameBuffer(currentBuffer);
        frameBuffer.begin();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureHandle);

        Gdx.gl20.glDrawElements(Gdx.gl20.GL_TRIANGLES, indexBufferObject.getNumIndices(), GL20.GL_UNSIGNED_SHORT, 0);

        frameBuffer.end();

        renderPipeline.returnFrameBuffer(currentBuffer);
        renderPipeline.setCurrentBuffer(frameBuffer);
    }

    private static float[] getKernel(int blurRadius) {
        if (kernelCache[blurRadius] == null) {
            float[] kernel = GaussianBlurKernel.create1DBlurKernel(blurRadius);
            kernelCache[blurRadius] = new float[1 + MAX_BLUR_RADIUS];
            System.arraycopy(kernel, 0, kernelCache[blurRadius], 0, kernel.length);
        }
        return kernelCache[blurRadius];
    }
}
