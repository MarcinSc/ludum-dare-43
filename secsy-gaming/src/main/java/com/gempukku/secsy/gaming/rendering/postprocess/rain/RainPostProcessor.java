package com.gempukku.secsy.gaming.rendering.postprocess.rain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.noise.ImprovedNoise;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.Random;

@RegisterSystem(profiles = "rain")
public class RainPostProcessor extends AbstractLifeCycleSystem {
    @Inject
    private TimeManager timeManager;

    private ShapeRenderer shapeRenderer;
    private Random rnd = new Random();

    @Override
    public void initialize() {
        shapeRenderer = new ShapeRenderer();
    }

    @ReceiveEvent(priorityName = "gaming.renderer.rain")
    public void render(RenderToPipeline renderToPipeline, EntityRef cameraEntity, RainComponent rain) {
        long time = Math.round(timeManager.getTime() * 0.1f);
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        float angle = rain.getRainAngle() + rain.getRainAngleVariance() * (float) ImprovedNoise.noise(time * rain.getRainAngleVarianceSpeed(), 0, 0);
        shapeRenderer.setColor(rain.getRainColor());

        int[] dropCounts = new int[]{300, 100, 50, 10};
        int[] lineLengths = new int[]{20, 40, 60, 80};
        int[] lineWidths = new int[]{1, 3, 6, 9};

        float sin = (float) Math.sin(angle);

        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int layer = 0; layer < dropCounts.length; layer++) {
            for (long i = time - dropCounts[layer]; i < time; i++) {
                float lineLength = lineLengths[layer];
                int lineWidth = lineWidths[layer];
                rnd.setSeed(i * 100000 + layer * 500000);
                float startX = rnd.nextFloat();
                float positionY = 1 - 1f * (time - i) / dropCounts[layer];
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
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        shapeRenderer.dispose();
    }
}
