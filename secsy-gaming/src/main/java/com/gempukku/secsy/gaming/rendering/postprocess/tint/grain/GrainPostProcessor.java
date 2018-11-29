package com.gempukku.secsy.gaming.rendering.postprocess.tint.grain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderPipeline;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.rendering.postprocess.tint.texture.TextureTintShaderProvider;

import java.util.Random;

@RegisterSystem(
        profiles = "grainPostProcessor"
)
public class GrainPostProcessor extends AbstractLifeCycleSystem {
    private ModelBatch modelBatch;

    private TextureTintShaderProvider tintShaderProvider;
    private ModelInstance modelInstance;
    private Model model;
    private Texture grainTexture;
    private TextureRegion grainTextureRegion;

    @Override
    public void preInitialize() {
        grainTexture = new Texture(Gdx.files.internal("image/grain.png"));
        grainTextureRegion = new TextureRegion(grainTexture);

        tintShaderProvider = new TextureTintShaderProvider();

        modelBatch = new ModelBatch(tintShaderProvider);
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

    @ReceiveEvent(priorityName = "gaming.renderer.tint.grain")
    public void render(RenderToPipeline event, EntityRef renderingEntity, GrainComponent tint) {
        float factor = tint.getFactor();

        if (factor > 0) {
            RenderPipeline renderPipeline = event.getRenderPipeline();

            Random rnd = new Random();
            tintShaderProvider.setTintShift(new Vector2(rnd.nextFloat(), rnd.nextFloat()));

            tintShaderProvider.setFactor(factor);

            FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();

            FrameBuffer frameBuffer = renderPipeline.getNewFrameBuffer(currentBuffer.getWidth(), currentBuffer.getHeight());
            frameBuffer.begin();

            setupTintTexture(event.getCamera(), tint);

            setupSourceTexture(renderPipeline);

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            modelBatch.begin(event.getCamera());
            modelBatch.render(modelInstance);
            modelBatch.end();

            frameBuffer.end();
            renderPipeline.returnFrameBuffer(currentBuffer);
            renderPipeline.setCurrentBuffer(frameBuffer);
        }
    }

    private void setupSourceTexture(RenderPipeline renderPipeline) {
        tintShaderProvider.setSourceTextureIndex(0);

        int textureHandle = renderPipeline.getCurrentBuffer().getColorBufferTexture().getTextureObjectHandle();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureHandle);
    }

    private void setupTintTexture(Camera camera, GrainComponent tint) {
        tintShaderProvider.setTintTextureIndex(1);

        int tintTextureHandle = grainTexture.getTextureObjectHandle();
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, tintTextureHandle);

        tintShaderProvider.setTintTextureOrigin(new Vector2(grainTextureRegion.getU(), grainTextureRegion.getV()));
        tintShaderProvider.setTintTextureSize(new Vector2(grainTextureRegion.getU2() - grainTextureRegion.getU(), grainTextureRegion.getV2() - grainTextureRegion.getV()));

        tintShaderProvider.setRepeatFactor(
                new Vector2(
                        Gdx.graphics.getWidth() / grainTextureRegion.getRegionWidth() / tint.getGrainSize(),
                        Gdx.graphics.getHeight() / grainTextureRegion.getRegionHeight() / tint.getGrainSize()));
    }

    @Override
    public void postDestroy() {
        grainTexture.dispose();
        modelBatch.dispose();
        model.dispose();
    }
}
