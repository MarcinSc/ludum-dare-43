package com.gempukku.secsy.gaming.rendering.postprocess.rain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.noise.ImprovedNoise;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.Random;

@RegisterSystem(profiles = "rain")
public class RainPostProcessor extends AbstractLifeCycleSystem {
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;

    private ShapeRenderer shapeRenderer;
    private Random rnd = new Random();

    @Override
    public void initialize() {
        shapeRenderer = new ShapeRenderer();
    }

    @ReceiveEvent(priorityName = "gaming.renderer.rain")
    public void render(RenderToPipeline renderToPipeline, EntityRef cameraEntity, RainComponent rain) {
        long time = timeManager.getTime();
        long effectStart = rain.getEffectStart();
        long effectDuration = rain.getEffectDuration();

        if (effectStart <= time && time < effectStart + effectDuration) {
            float alpha = 1f * (time - effectStart) / effectDuration;

            float rainAlpha = easingResolver.resolveValue(rain.getAlpha(), alpha);
            if (rainAlpha > 0) {
                float rainAngle = easingResolver.resolveValue(rain.getRainAngle(), alpha);
                float rainAngleVariance = easingResolver.resolveValue(rain.getRainAngleVariance(), alpha);
                float rainAngleVarianceSpeed = easingResolver.resolveValue(rain.getRainAngleVarianceSpeed(), alpha);

                long timeTick = Math.round(timeManager.getTime() * 0.1f);

                float angle = rainAngle + rainAngleVariance * ImprovedNoise.noise(time * 0.1f * rainAngleVarianceSpeed, 0, 0);
                Color rainColor = rain.getRainColor();
                shapeRenderer.setColor(rainColor.r, rainColor.g, rainColor.b, rainColor.a * rainAlpha);

                int[] dropCounts = new int[]{300, 100, 50, 10};
                int[] lineLengths = new int[]{20, 40, 60, 80};
                int[] lineWidths = new int[]{1, 3, 6, 9};

                float sin = MathUtils.sin(angle);

                FrameBuffer currentBuffer = renderToPipeline.getRenderPipeline().getCurrentBuffer();
                int width = currentBuffer.getWidth();
                int height = currentBuffer.getHeight();
                currentBuffer.begin();
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                for (int layer = 0; layer < dropCounts.length; layer++) {
                    for (long i = timeTick - dropCounts[layer]; i < timeTick; i++) {
                        float lineLength = lineLengths[layer];
                        int lineWidth = lineWidths[layer];
                        rnd.setSeed(i * 100000 + layer * 500000);
                        float startX = rnd.nextFloat();
                        float positionY = 1 - 1f * (timeTick - i) / dropCounts[layer];
                        float positionYStart = positionY + lineLength / height / 2;
                        float positionYEnd = positionY - lineLength / height / 2;
                        float positionXStart = startX * (1 + Math.abs(sin)) - sin * positionYStart;
                        float positionXEnd = startX * (1 + Math.abs(sin)) - sin * positionYEnd;
                        shapeRenderer.rectLine(positionXStart * width, positionYStart * height,
                                positionXEnd * width, positionYEnd * height, lineWidth);
                    }
                }
                shapeRenderer.end();
                Gdx.gl.glDisable(GL20.GL_BLEND);
                currentBuffer.end();
            }
        }
    }

    @Override
    public void destroy() {
        shapeRenderer.dispose();
    }
}
