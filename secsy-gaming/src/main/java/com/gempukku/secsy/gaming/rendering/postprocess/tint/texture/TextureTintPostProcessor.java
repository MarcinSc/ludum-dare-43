package com.gempukku.secsy.gaming.rendering.postprocess.tint.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderPipeline;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.Random;

@RegisterSystem(
        profiles = "textureTint")
public class TextureTintPostProcessor extends AbstractLifeCycleSystem {
    @Inject
    private TextureAtlasProvider textureAtlasProvider;
    @Inject
    private EasingResolver easingResolver;
    @Inject
    private TimeManager timeManager;

    private ShaderProgram shaderProgram;
    private VertexBufferObject vertexBufferObject;
    private IndexBufferObject indexBufferObject;

    private Random rnd = new Random();

    @Override
    public void initialize() {
        shaderProgram = new ShaderProgram(
                Gdx.files.internal("shader/viewToScreenCoords.vert"),
                Gdx.files.internal("shader/textureTint.frag"));
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

    @ReceiveEvent(priorityName = "gaming.renderer.tint.texture")
    public void render(RenderToPipeline renderToPipeline, EntityRef renderingEntity, TextureTintComponent tint) {
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

                FrameBuffer newBuffer = renderPipeline.getNewFrameBuffer(currentBuffer);

                newBuffer.begin();

                shaderProgram.begin();

                vertexBufferObject.bind(shaderProgram);
                indexBufferObject.bind();

                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
                Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, currentBuffer.getColorBufferTexture().getTextureObjectHandle());

                TextureRegion texture = textureAtlasProvider.getTexture(tint.getTextureAtlasId(), tint.getTextureName());

                int tintTextureHandle = texture.getTexture().getTextureObjectHandle();
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
                Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, tintTextureHandle);

                shaderProgram.setUniformf("u_sourceTexture", 0);
                shaderProgram.setUniformf("u_tintTexture", 1);
                shaderProgram.setUniformf("u_factor", factor);
                shaderProgram.setUniformf("u_tintTextureOrigin", texture.getU(), texture.getV());
                shaderProgram.setUniformf("u_tintTextureSize", texture.getU2() - texture.getU(), texture.getV2() - texture.getV());
                shaderProgram.setUniformf("u_tintShift", 0, 0);
                shaderProgram.setUniformf("u_repeatFactor", 1, 1);

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
