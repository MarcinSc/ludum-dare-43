package com.gempukku.secsy.gaming.rendering.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.List;

@RegisterSystem(profiles = "splash")
public class SplashRenderer extends AbstractLifeCycleSystem {
    @Inject
    private TimeManager timeManager;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private ModelBatch modelBatch;

    private BackgroundImageShaderProvider backgroundImageProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void initialize() {
        backgroundImageProvider = new BackgroundImageShaderProvider();

        modelBatch = new ModelBatch(backgroundImageProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("background", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position,
                new Material(new DepthTestAttribute(false)));
        backgroundBuilder.rect(
                -1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                1, 1, 1,
                0, 0, 1);
        model = modelBuilder.end();

        modelInstance = new ModelInstance(model);
    }

    @ReceiveEvent
    public void renderSplash(RenderToPipeline renderToPipeline, EntityRef cameraEntity, SplashSeriesComponent splashSeriesComponent) {
        List<SplashDefinition> splashDefinitions = splashSeriesComponent.getSplashDefinitions();

        long time = timeManager.getTime();
        long startTime = splashSeriesComponent.getStartTime();
        if (time >= startTime) {
            SplashDefinition splashDefinition = getSplashDefinition(splashDefinitions, time - startTime);
            if (splashDefinition != null) {
                String textureAtlasId = splashSeriesComponent.getTextureAtlasId();

                renderBackground(renderToPipeline, splashDefinition.getBackgroundColor(), textureAtlasId, splashDefinition.getTextureName(),
                        0.5f, 0.5f, 0.5f, 0.5f);
            } else {
                cameraEntity.send(new SplashSeriesEnded());
            }
        }
    }

    private SplashDefinition getSplashDefinition(List<SplashDefinition> splashDefinitions, long timeSinceStart) {
        for (SplashDefinition splashDefinition : splashDefinitions) {
            long splashDuration = splashDefinition.getDuration();
            if (splashDuration > timeSinceStart)
                return splashDefinition;
            timeSinceStart -= splashDuration;
        }
        return null;
    }

    private void renderBackground(RenderToPipeline renderToPipeline, Color backgroundColor, String textureAtlasId,
                                  String textureName, float paddingLeft, float paddingBottom, float paddingRight, float paddingTop) {
        TextureRegion texture = textureAtlasProvider.getTexture(textureAtlasId, textureName);

        float viewportWidth = renderToPipeline.getCamera().viewportWidth;
        float viewportHeight = renderToPipeline.getCamera().viewportHeight;

        int textureWidth = texture.getRegionWidth();
        int textureHeight = texture.getRegionHeight();

        float widthPaddingMultiplier = 1 + paddingLeft + paddingRight;
        float heightPaddingMultiplier = 1 + paddingTop + paddingBottom;

        float regionWidth = textureWidth * widthPaddingMultiplier;
        float regionHeight = textureHeight * heightPaddingMultiplier;

        float viewportRatio = viewportWidth / viewportHeight;
        float regionRatio = regionWidth / regionHeight;

        float leftEdge;
        float topEdge;
        float rightEdge;
        float bottomEdge;

        float width;
        float height;
        if (viewportRatio > regionRatio) {
            width = 1f;
            height = regionRatio / viewportRatio;
        } else {
            width = viewportRatio / regionRatio;
            height = 1f;
        }

        // Discounting padding for a moment
        leftEdge = 0.5f - width / 2;
        rightEdge = 0.5f + width / 2;
        topEdge = 0.5f - height / 2;
        bottomEdge = 0.5f + height / 2;

        // Apply bounds for padding
        leftEdge -= paddingLeft * width;
        rightEdge += paddingRight * width;
        bottomEdge += paddingBottom * height;
        topEdge -= paddingTop * height;

        backgroundImageProvider.setBackgroundImageStartX(texture.getU());
        backgroundImageProvider.setBackgroundImageStartY(texture.getV());
        backgroundImageProvider.setBackgroundImageWidth(texture.getU2() - texture.getU());
        backgroundImageProvider.setBackgroundImageHeight(texture.getV2() - texture.getV());

        backgroundImageProvider.setLeftEdge(leftEdge);
        backgroundImageProvider.setTopEdge(topEdge);
        backgroundImageProvider.setRightEdge(rightEdge);
        backgroundImageProvider.setBottomEdge(bottomEdge);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, texture.getTexture().getTextureObjectHandle());

        backgroundImageProvider.setBackgroundImageIndex(0);
        backgroundImageProvider.setBackgroundColor(backgroundColor);

        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        modelBatch.begin(renderToPipeline.getCamera());
        modelBatch.render(modelInstance);
        modelBatch.end();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        modelBatch.dispose();
        model.dispose();
    }
}
