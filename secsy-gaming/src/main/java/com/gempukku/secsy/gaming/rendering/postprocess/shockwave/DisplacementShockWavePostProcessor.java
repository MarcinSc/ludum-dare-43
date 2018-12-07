package com.gempukku.secsy.gaming.rendering.postprocess.shockwave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
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
        profiles = "displacementShockWave")
public class DisplacementShockWavePostProcessor extends AbstractLifeCycleSystem {
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
                Gdx.files.internal("shader/displacementShockWave.frag"));
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

    private Vector3 positionInScreenCoords = new Vector3();
    private Vector3 tmp2 = new Vector3();

    @ReceiveEvent(priorityName = "gaming.renderer.shockWave.displacement")
    public void processTint(RenderToPipeline renderToPipeline, EntityRef renderingEntity, DisplacementShockWaveComponent displacementShockWave) {
        long time = timeManager.getTime();
        long effectStart = displacementShockWave.getEffectStart();
        long effectDuration = displacementShockWave.getEffectDuration();

        if (effectStart <= time && time < effectStart + effectDuration) {
            float alpha = 1f * (time - effectStart) / effectDuration;

            float effectAlpha = easingResolver.resolveValue(displacementShockWave.getAlpha(), alpha);
            if (effectAlpha > 0) {
                float distance = easingResolver.resolveValue(displacementShockWave.getDistance(), alpha);
                float size = easingResolver.resolveValue(displacementShockWave.getSize(), alpha);
                float noiseImpact = easingResolver.resolveValue(displacementShockWave.getNoiseImpact(), alpha);
                float noiseVariance = easingResolver.resolveValue(displacementShockWave.getNoiseVariance(), alpha);

                RenderPipeline renderPipeline = renderToPipeline.getRenderPipeline();

                FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();

                int width = currentBuffer.getWidth();
                int height = currentBuffer.getHeight();

                FrameBuffer newBuffer = renderPipeline.getNewFrameBuffer(currentBuffer);

                newBuffer.begin();

                shaderProgram.begin();

                vertexBufferObject.bind(shaderProgram);
                indexBufferObject.bind();

                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 0);
                Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, currentBuffer.getColorBufferTexture().getTextureObjectHandle());

                Matrix4 combinedTransform = renderToPipeline.getCamera().combined;
                positionInScreenCoords.set(displacementShockWave.getPosition());
                positionInScreenCoords.mul(combinedTransform);

                tmp2.set(displacementShockWave.getPosition());
                tmp2.add(distance, 0, 0);
                tmp2.mul(combinedTransform);

                float distanceInScreenCoords = tmp2.dst(positionInScreenCoords);

                tmp2.set(displacementShockWave.getPosition());
                tmp2.add(distance, 0, 0);
                tmp2.add(size, 0, 0);
                tmp2.mul(combinedTransform);

                float sizeInScreenCoords = tmp2.dst(positionInScreenCoords) - distanceInScreenCoords;

                float heightToWidth = 1f * height / width;

                shaderProgram.setUniformf("u_sourceTexture", 0);
                shaderProgram.setUniformf("u_position", positionInScreenCoords.x * 0.5f, positionInScreenCoords.y * 0.5f * heightToWidth);
                shaderProgram.setUniformf("u_distance", distanceInScreenCoords * 0.5f);
                shaderProgram.setUniformf("u_size", sizeInScreenCoords * 0.5f);
                shaderProgram.setUniformf("u_alpha", effectAlpha);
                shaderProgram.setUniformf("u_heightToWidth", heightToWidth);
                shaderProgram.setUniformf("u_noiseImpact", noiseImpact);
                shaderProgram.setUniformf("u_noiseVariance", noiseVariance);

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
