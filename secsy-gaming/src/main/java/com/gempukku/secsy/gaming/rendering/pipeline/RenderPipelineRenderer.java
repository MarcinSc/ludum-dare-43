package com.gempukku.secsy.gaming.rendering.pipeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.math.Rectangle;
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
    @Inject(optional = true)
    private RenderStrategy renderStrategy = new DefaultRenderStrategy();

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

        vertexBufferObject = new VertexBufferObject(false, 4, VertexAttribute.Position());
        indexBufferObject = new IndexBufferObject(true, indices.length);
        vertexBufferObject.setVertices(verticeData, 0, verticeData.length);
        indexBufferObject.setIndices(indices, 0, indices.length);
    }

    @Override
    public void render(int width, int height) {
        EntityRef cameraEntity = cameraEntityProvider.getCameraEntity();
        if (cameraEntity != null) {
            int renderBufferWidth = renderStrategy.getRenderBufferWidth(width, height);
            int renderBufferHeight = renderStrategy.getRenderBufferHeight(width, height);
            Pixmap.Format renderBufferFormat = renderStrategy.getRenderBufferFormat(width, height);
            FrameBuffer drawFrameBuffer = renderPipeline.getNewFrameBuffer(renderBufferWidth, renderBufferHeight, renderBufferFormat);
            try {
                renderPipeline.setCurrentBuffer(drawFrameBuffer);

                renderPipeline.getCurrentBuffer().begin();
                cleanBuffer(renderStrategy.getScreenFillColor());
                renderPipeline.getCurrentBuffer().end();

                float deltaTime = timeManager.getTimeSinceLastUpdate() / 1000f;

                GetCamera getCamera = new GetCamera(deltaTime, width, height);
                cameraEntity.send(getCamera);

                cameraEntity.send(new RenderToPipeline(renderPipeline, getCamera.getCamera(), deltaTime, renderBufferWidth, renderBufferHeight));

                renderToScreen(width, height, renderBufferWidth, renderBufferHeight);

                renderPipeline.returnFrameBuffer(renderPipeline.getCurrentBuffer());
            } finally {
                renderPipeline.ageOutBuffers();
            }
        } else {
            cleanBuffer(renderStrategy.getScreenFillColor());
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

    private Rectangle tempRectangle = new Rectangle();
    private float[] vertices = new float[12];

    private void renderToScreen(int screenWidth, int screenHeight, int renderWidth, int renderHeight) {
        shaderProgram.begin();

        cleanBuffer(renderStrategy.getScreenFillColor());

        Rectangle renderRectangle = renderStrategy.getScreenRenderRectangle(screenWidth, screenHeight, renderWidth, renderHeight, tempRectangle);
        float x = renderRectangle.x / screenWidth;
        float y = renderRectangle.y / screenHeight;
        float width = renderRectangle.width / screenWidth;
        float height = renderRectangle.height / screenHeight;
        vertices[0] = x;
        vertices[1] = y;
        vertices[3] = x;
        vertices[4] = y + height;
        vertices[6] = x + width;
        vertices[7] = y;
        vertices[9] = x + width;
        vertices[10] = y + height;
        vertexBufferObject.setVertices(vertices, 0, 12);

        vertexBufferObject.bind(shaderProgram);
        indexBufferObject.bind();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, renderPipeline.getCurrentBuffer().getColorBufferTexture().getTextureObjectHandle());

        shaderProgram.setUniformf("u_sourceTexture", 0);
        shaderProgram.setUniformf("u_textureStart", x, y);
        shaderProgram.setUniformf("u_textureSize", width, height);

        Gdx.gl20.glDrawElements(Gdx.gl20.GL_TRIANGLES, indexBufferObject.getNumIndices(), GL20.GL_UNSIGNED_SHORT, 0);
        vertexBufferObject.unbind(shaderProgram);
        indexBufferObject.unbind();

        shaderProgram.end();
    }

    private void cleanBuffer(Color color) {
        Gdx.gl.glClearColor(color.r, color.g, color.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }
}
