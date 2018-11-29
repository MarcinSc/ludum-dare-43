package com.gempukku.secsy.gaming.rendering.pipeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.rendering.RenderingSystem;
import com.gempukku.secsy.gaming.rendering.copy.CopyShaderProvider;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(profiles = "pipelineRenderer", shared = RenderingSystem.class)
public class RenderPipelineRenderer extends AbstractLifeCycleSystem implements RenderingSystem {
    private RenderPipelineImpl renderPipeline = new RenderPipelineImpl();

    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private TimeManager timeManager;

    private CopyShaderProvider copyShaderProvider = new CopyShaderProvider();
    private ModelBatch copyModelBatch;
    private Model copyModel;
    private ModelInstance copyModelInstance;

    @Override
    public void preInitialize() {
        copyModelBatch = new ModelBatch(copyShaderProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("screen", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position,
                new Material(new DepthTestAttribute(0, false)));
        backgroundBuilder.rect(
                0, 1, 1,
                0, 0, 1,
                1, 0, 1,
                1, 1, 1,
                0, 0, 1);
        copyModel = modelBuilder.end();

        copyModelInstance = new ModelInstance(copyModel);
    }

    @Override
    public void render(int width, int height) {
        EntityRef cameraEntity = cameraEntityProvider.getCameraEntity();
        if (cameraEntity != null) {
            FrameBuffer drawFrameBuffer = renderPipeline.getNewFrameBuffer(width, height);
            try {
                renderPipeline.setCurrentBuffer(drawFrameBuffer);

                renderPipeline.getCurrentBuffer().begin();
                cleanBuffer();
                renderPipeline.getCurrentBuffer().end();

                float deltaTime = timeManager.getTimeSinceLastUpdate() / 1000f;

                GetCamera getCamera = new GetCamera(deltaTime, width, height);
                cameraEntity.send(getCamera);

                cameraEntity.send(new RenderToPipeline(renderPipeline, getCamera.getCamera(), deltaTime, width, height));

                renderToScreen();

                renderPipeline.returnFrameBuffer(renderPipeline.getCurrentBuffer());
            } finally {
                renderPipeline.ageOutBuffers();
            }
        } else {
            cleanBuffer();
        }
        renderPipeline.ageOutBuffers();
    }

    @Override
    public void postDestroy() {
        copyModel.dispose();
        copyModelBatch.dispose();
        renderPipeline.cleanup();
    }

    private void renderToScreen() {
        copyShaderProvider.setSourceTextureIndex(0);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, renderPipeline.getCurrentBuffer().getColorBufferTexture().getTextureObjectHandle());

        cleanBuffer();

        copyModelBatch.begin(null);
        copyModelBatch.render(copyModelInstance);
        copyModelBatch.end();
    }


    private void cleanBuffer() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }
}
