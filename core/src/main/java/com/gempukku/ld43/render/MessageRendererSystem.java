package com.gempukku.ld43.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.gempukku.ld43.model.GameScreenComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class MessageRendererSystem extends AbstractLifeCycleSystem {
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;

    private SpriteBatch spriteBatch;
    private BitmapFont messageFont;

    private long textStartTime;
    private long textDuration;
    private String text;

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();
    }

    @ReceiveEvent
    public void renderText(RenderText renderText) {
        textStartTime = timeManager.getTime();
        text = renderText.getText();
        textDuration = renderText.getDuration();
    }

    @ReceiveEvent(priority = -10)
    public void renderToPipeline(RenderToPipeline renderToPipeline, EntityRef cameraEntity, GameScreenComponent gameScreen) {
        int screenHeight = renderToPipeline.getHeight();
        int screenWidth = renderToPipeline.getWidth();

        int fontSize = screenHeight / 15;
        if (messageFont == null) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/IMMORTAL.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = fontSize;
            parameter.borderWidth = screenHeight / 200;
            parameter.borderColor = Color.BLACK;
            messageFont = generator.generateFont(parameter);
            generator.dispose();
        }

        long time = timeManager.getTime();
        if (textStartTime <= time && time < textStartTime + textDuration) {
            renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
            float color = easingResolver.resolveValue("pow5,0-1-0", 1f * (time - textStartTime) / textDuration);
            messageFont.setColor(1, 1, 1, color);
            spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, screenWidth, screenHeight);
            spriteBatch.begin();
            messageFont.draw(spriteBatch, text, 0, screenHeight / 7, screenWidth, Align.center, true);
            spriteBatch.end();
            renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
        }
    }

    @Override
    public void destroy() {
        spriteBatch.dispose();
        if (messageFont != null)
            messageFont.dispose();
    }
}
