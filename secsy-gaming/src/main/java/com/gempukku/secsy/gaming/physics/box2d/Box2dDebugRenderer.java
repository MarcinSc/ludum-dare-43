package com.gempukku.secsy.gaming.physics.box2d;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderPipeline;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem(profiles = {"box2dPhysics", "debugPhysics"})
public class Box2dDebugRenderer {
    private Box2DDebugRenderer renderer = new Box2DDebugRenderer();

    @Inject
    private Box2dPhysics box2dPhysics;

    @ReceiveEvent
    public void renderDebug(RenderToPipeline renderToPipeline, EntityRef cameraEntity) {
        RenderPipeline renderPipeline = renderToPipeline.getRenderPipeline();

        renderPipeline.getCurrentBuffer().begin();
        renderer.render(((Box2dPhysicsSystem) box2dPhysics).getWorld(), renderToPipeline.getCamera().combined);
        renderPipeline.getCurrentBuffer().end();
    }
}
