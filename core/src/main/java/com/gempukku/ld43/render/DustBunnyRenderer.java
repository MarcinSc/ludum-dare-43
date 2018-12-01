package com.gempukku.ld43.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.gempukku.ld43.model.DustBunnyComponent;
import com.gempukku.ld43.model.GameScreenComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class DustBunnyRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private EntityIndex dustBunnyEntities;

    private ShaderProgram shaderProgram;
    private IndexBufferObject indexBufferObject;
    private VertexBufferObject vertexBufferObject;

    @Override
    public void initialize() {
        dustBunnyEntities = entityIndexManager.addIndexOnComponents(DustBunnyComponent.class);

        shaderProgram = new ShaderProgram(
                Gdx.files.internal("shaders/dustBunnyShader.vert"),
                Gdx.files.internal("shaders/dustBunnyShader.frag"));
        if (shaderProgram.isCompiled() == false)
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

    @ReceiveEvent(priority = -3)
    public void renderDustBunnies(RenderToPipeline renderToPipeline, EntityRef cameraEntity, GameScreenComponent gameScreen) {
        float seconds = timeManager.getTime() / 1000f;

        Camera camera = renderToPipeline.getCamera();

        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();

        shaderProgram.begin();

        Gdx.gl20.glEnable(GL20.GL_BLEND);

        vertexBufferObject.bind(shaderProgram);
        indexBufferObject.bind();

        shaderProgram.setUniformMatrix("u_projTrans", camera.combined);

        for (EntityRef dustBunnyEntity : dustBunnyEntities) {
            Position2DComponent position = dustBunnyEntity.getComponent(Position2DComponent.class);
            DustBunnyComponent dustBunny = dustBunnyEntity.getComponent(DustBunnyComponent.class);
            float x = position.getX() + dustBunny.getLeft();
            float y = position.getY() + dustBunny.getDown();
            float width = dustBunny.getRight() - dustBunny.getLeft();
            float height = dustBunny.getUp() - dustBunny.getDown();

            shaderProgram.setUniformf("u_bunnyColor", dustBunny.getColor());
            shaderProgram.setUniformf("u_time", seconds);
            shaderProgram.setUniformf("u_coordinates", x, y, width, height);

            Gdx.gl20.glDrawElements(Gdx.gl20.GL_TRIANGLES, indexBufferObject.getNumIndices(), GL20.GL_UNSIGNED_SHORT, 0);
        }
        vertexBufferObject.unbind(shaderProgram);
        indexBufferObject.unbind();

        Gdx.gl20.glDisable(GL20.GL_BLEND);

        shaderProgram.end();

        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    private float getNextDepth(float[] layerDepth, int i) {
        if (i == layerDepth.length - 1)
            return 0;
        return layerDepth[i + 1];
    }

    private float getCurrentDepth(float[] layerDepth, int i) {
        return layerDepth[i];
    }

    private float getPreviousDepth(float[] layerDepth, int i) {
        if (i == 0)
            return 0;
        return layerDepth[i - 1];
    }

    @Override
    public void destroy() {
        vertexBufferObject.dispose();
        indexBufferObject.dispose();
        shaderProgram.dispose();
    }
}
