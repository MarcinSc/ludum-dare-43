package com.gempukku.secsy.gaming.rendering.postprocess.gamma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderPipeline;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

import java.util.Random;

@RegisterSystem(
        profiles = "gamma")
public class GammaPostProcessor extends AbstractLifeCycleSystem {
    private ShaderProgram shaderProgram;
    private VertexBufferObject vertexBufferObject;
    private IndexBufferObject indexBufferObject;

    private Random rnd = new Random();

    @Override
    public void initialize() {
        shaderProgram = new ShaderProgram(
                Gdx.files.internal("shader/viewToScreenCoords.vert"),
                Gdx.files.internal("shader/gamma.frag"));
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

    @ReceiveEvent(priorityName = "gaming.renderer.gamma")
    public void render(RenderToPipeline renderToPipeline, EntityRef renderingEntity, GammaComponent gamma) {
        float factor = gamma.getFactor();

        if (factor > 0) {
            RenderPipeline renderPipeline = renderToPipeline.getRenderPipeline();

            FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();

            int width = currentBuffer.getWidth();
            int height = currentBuffer.getHeight();

            FrameBuffer newBuffer = renderPipeline.getNewFrameBuffer(width, height);

            newBuffer.begin();

            shaderProgram.begin();

            Gdx.gl20.glEnable(GL20.GL_BLEND);

            vertexBufferObject.bind(shaderProgram);
            indexBufferObject.bind();

            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 0);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, currentBuffer.getColorBufferTexture().getTextureObjectHandle());

            shaderProgram.setUniformf("u_sourceTexture", 0);
            shaderProgram.setUniformf("u_factor", factor);
            shaderProgram.setUniformf("u_random", rnd.nextFloat(), rnd.nextFloat());

            Gdx.gl20.glDrawElements(Gdx.gl20.GL_TRIANGLES, indexBufferObject.getNumIndices(), GL20.GL_UNSIGNED_SHORT, 0);
            vertexBufferObject.unbind(shaderProgram);
            indexBufferObject.unbind();

            shaderProgram.end();

            newBuffer.end();

            renderPipeline.returnFrameBuffer(currentBuffer);
            renderPipeline.setCurrentBuffer(newBuffer);
        }
    }

    @Override
    public void postDestroy() {
        vertexBufferObject.dispose();
        indexBufferObject.dispose();
        shaderProgram.dispose();
    }
}
