package com.gempukku.secsy.gaming.rendering.pipeline;

import com.badlogic.gdx.Gdx;
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
import com.gempukku.secsy.gaming.rendering.RenderingSystem;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(profiles = "pipelineRenderer", shared = RenderingSystem.class)
public class RenderPipelineRenderer extends AbstractLifeCycleSystem implements RenderingSystem {
    private RenderPipelineImpl renderPipeline = new RenderPipelineImpl();

    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private TimeManager timeManager;

    private ShaderProgram shaderProgram;
    private VertexBufferObject vertexBufferObject;
    private IndexBufferObject indexBufferObject;

    @Override
    public void initialize() {
        shaderProgram = new ShaderProgram(
                Gdx.files.internal("shader/viewToScreenCoords.vert"),
                Gdx.files.internal("shader/copy.frag"));
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
    public void destroy() {
        vertexBufferObject.dispose();
        indexBufferObject.dispose();
        shaderProgram.dispose();
        renderPipeline.cleanup();
    }

    private void renderToScreen() {
        shaderProgram.begin();

        cleanBuffer();

        vertexBufferObject.bind(shaderProgram);
        indexBufferObject.bind();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, renderPipeline.getCurrentBuffer().getColorBufferTexture().getTextureObjectHandle());

        shaderProgram.setUniformf("u_sourceTexture", 0);
        shaderProgram.setUniformf("u_textureStart", 0, 0);
        shaderProgram.setUniformf("u_textureSize", 1, 1);

        Gdx.gl20.glDrawElements(Gdx.gl20.GL_TRIANGLES, indexBufferObject.getNumIndices(), GL20.GL_UNSIGNED_SHORT, 0);
        vertexBufferObject.unbind(shaderProgram);
        indexBufferObject.unbind();

        shaderProgram.end();
    }

    private void cleanBuffer() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }
}
