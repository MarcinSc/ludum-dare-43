package com.gempukku.secsy.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderPipeline;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem(
        profiles = "gaussianBlur")
public class GaussianBlurPostProcessor extends AbstractLifeCycleSystem {
    private ModelBatch modelBatch;

    private GaussianBlurShaderProvider blurShaderProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void preInitialize() {
        blurShaderProvider = new GaussianBlurShaderProvider();

        modelBatch = new ModelBatch(blurShaderProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("screen", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, new Material());
        backgroundBuilder.rect(
                0, 1, 1,
                0, 0, 1,
                1, 0, 1,
                1, 1, 1,
                0, 0, 1);
        model = modelBuilder.end();

        modelInstance = new ModelInstance(model);
    }

    @ReceiveEvent(priorityName = "gaming.renderer.gaussianBlur")
    public void render(RenderToPipeline event, EntityRef renderingEntity, GaussianBlurComponent blur) {
        int blurRadius = blur.getBlurRadius();

        if (blurRadius > 0) {
            blurShaderProvider.setSourceTextureIndex(0);
            blurShaderProvider.setBlurRadius(blurRadius);

            RenderPipeline renderPipeline = event.getRenderPipeline();

            blurShaderProvider.setVertical(true);
            executeBlur(event, renderPipeline);
            blurShaderProvider.setVertical(false);
            executeBlur(event, renderPipeline);
        }
    }

    private void executeBlur(RenderToPipeline event, RenderPipeline renderPipeline) {
        FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();
        int textureHandle = currentBuffer.getColorBufferTexture().getTextureObjectHandle();

        FrameBuffer frameBuffer = renderPipeline.getNewFrameBuffer(currentBuffer.getWidth(), currentBuffer.getHeight());
        frameBuffer.begin();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureHandle);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(event.getCamera());
        modelBatch.render(modelInstance);
        modelBatch.end();

        frameBuffer.end();
        renderPipeline.returnFrameBuffer(currentBuffer);
        renderPipeline.setCurrentBuffer(frameBuffer);
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }
}
