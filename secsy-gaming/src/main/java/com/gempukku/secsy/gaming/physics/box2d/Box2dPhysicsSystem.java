package com.gempukku.secsy.gaming.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.PhysicsSystem;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(profiles = "box2dPhysics", shared = {PhysicsSystem.class, Box2dPhysics.class})
public class Box2dPhysicsSystem extends AbstractLifeCycleSystem implements PhysicsSystem, Box2dPhysics {
    private static final Object groundedSensorData = new Object();
    @Inject
    private EntityManager entityManager;
    @Inject
    private TimeManager timeManager;

    private Array<Body> bodies = new Array<Body>(false, 16);
    private World world;

    World getWorld() {
        return world;
    }

    @Override
    public void initialize() {
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new SendEventsContactListener());
    }

    @Override
    public void addBody(EntityRef entity) {
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        if (position == null)
            throw new IllegalArgumentException("Passed entity has no Position2DComponent");
        Body body = createBody(entity, position);
        body.setUserData(entity);
    }

    @Override
    public void applyPulse(EntityRef entity, float x, float y) {
        world.getBodies(bodies);
        for (Body body : bodies) {
            EntityRef userData = (EntityRef) body.getUserData();
            if (entityManager.isSameEntity(userData, entity)) {
                Vector2 worldCenter = body.getWorldCenter();
                body.applyLinearImpulse(x, y, worldCenter.x, worldCenter.y, true);
            }
        }
    }

    @Override
    public void setSpeedX(EntityRef entity, float speed) {
        world.getBodies(bodies);
        for (Body body : bodies) {
            EntityRef userData = (EntityRef) body.getUserData();
            if (entityManager.isSameEntity(userData, entity)) {
                body.setLinearVelocity(speed, body.getLinearVelocity().y);
            }
        }
    }

    @Override
    public void setSpeedY(EntityRef entity, float speed) {
        world.getBodies(bodies);
        for (Body body : bodies) {
            EntityRef userData = (EntityRef) body.getUserData();
            if (entityManager.isSameEntity(userData, entity)) {
                body.setLinearVelocity(body.getLinearVelocity().x, speed);
            }
        }
    }

    @Override
    public void removeBody(EntityRef entity) {
        world.getBodies(bodies);
        for (Body body : bodies) {
            EntityRef userData = (EntityRef) body.getUserData();
            if (entityManager.isSameEntity(userData, entity))
                world.destroyBody(body);
        }
    }

    @Override
    public void processPhysics() {
        float deltaTime = timeManager.getTimeSinceLastUpdate() / 1000f;

        world.step(deltaTime, 6, 2);

        world.getBodies(bodies);
        for (Body body : bodies) {
            EntityRef userData = (EntityRef) body.getUserData();
            Position2DComponent position = userData.getComponent(Position2DComponent.class);
            position.setX(body.getPosition().x);
            position.setY(body.getPosition().y);
            userData.saveChanges();
        }
    }

    private void addFixture(Body body, PhysicsFixture boxFixture) {
        Shape shape = createShape(boxFixture);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = boxFixture.getDensity();
        fixtureDef.friction = boxFixture.getFriction();
        fixtureDef.isSensor = boxFixture.isSensor();
        fixtureDef.filter.categoryBits = boxFixture.getCategory();
        fixtureDef.filter.maskBits = boxFixture.getContactMask();

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(boxFixture.getUserData());
        shape.dispose();
    }

    private Shape createShape(PhysicsFixture physicsFixture) {
        if (physicsFixture.getType() == PhysicsFixture.Type.Box) {
            BoxFixture boxFixture = (BoxFixture) physicsFixture;
            PolygonShape shape = new PolygonShape();
            float halfWidth = boxFixture.getWidth() / 2;
            float halfHeight = boxFixture.getHeight() / 2;
            float centerX = boxFixture.getX();
            float centerY = boxFixture.getY();
            float angle = boxFixture.getAngle();
            shape.setAsBox(halfWidth, halfHeight,
                    new Vector2(centerX, centerY), angle);
            return shape;
        } else if (physicsFixture.getType() == PhysicsFixture.Type.Circle) {
            CircleFixture circleFixture = (CircleFixture) physicsFixture;
            CircleShape shape = new CircleShape();
            shape.setRadius(circleFixture.getRadius());
            shape.setPosition(new Vector2(circleFixture.getX(), circleFixture.getY()));
            return shape;
        } else {
            return null;
        }
    }

    private Body createBody(EntityRef entity, Position2DComponent position) {
        StaticBodyComponent staticBody = entity.getComponent(StaticBodyComponent.class);
        if (staticBody != null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(position.getX(), position.getY());

            Body body = world.createBody(bodyDef);
            for (PhysicsFixture boxFixture : staticBody.getFixtures()) {
                addFixture(body, boxFixture);
            }

            return body;
        }
        KinematicBodyComponent kinematicBody = entity.getComponent(KinematicBodyComponent.class);
        if (kinematicBody != null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(position.getX(), position.getY());
            bodyDef.fixedRotation = kinematicBody.isFixedRotation();

            Body body = world.createBody(bodyDef);
            for (PhysicsFixture boxFixture : staticBody.getFixtures()) {
                addFixture(body, boxFixture);
            }

            return body;
        }
        DynamicBodyComponent dynamicBody = entity.getComponent(DynamicBodyComponent.class);
        if (dynamicBody != null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(position.getX(), position.getY());
            bodyDef.fixedRotation = dynamicBody.isFixedRotation();
            bodyDef.linearDamping = dynamicBody.getLinearDumping();

            Body body = world.createBody(bodyDef);
            for (PhysicsFixture boxFixture : dynamicBody.getFixtures()) {
                addFixture(body, boxFixture);
            }

            return body;
        }
        throw new IllegalArgumentException("Passed entity has no *BodyComponent");
    }

    private class SendEventsContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            if (contact.getFixtureA().isSensor()) {
                EntityRef sensorEntity = (EntityRef) contact.getFixtureA().getBody().getUserData();
                sensorEntity.send(new Box2dSensorContactBegin(contact.getFixtureA(), contact.getFixtureB()));
            }
            if (contact.getFixtureB().isSensor()) {
                EntityRef sensorEntity = (EntityRef) contact.getFixtureB().getBody().getUserData();
                sensorEntity.send(new Box2dSensorContactBegin(contact.getFixtureB(), contact.getFixtureA()));
            }
        }

        @Override
        public void endContact(Contact contact) {
            if (contact.getFixtureA().isSensor()) {
                EntityRef sensorEntity = (EntityRef) contact.getFixtureA().getBody().getUserData();
                sensorEntity.send(new Box2dSensorContactEnd(contact.getFixtureA(), contact.getFixtureB()));
            }
            if (contact.getFixtureB().isSensor()) {
                EntityRef sensorEntity = (EntityRef) contact.getFixtureB().getBody().getUserData();
                sensorEntity.send(new Box2dSensorContactEnd(contact.getFixtureB(), contact.getFixtureA()));
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
