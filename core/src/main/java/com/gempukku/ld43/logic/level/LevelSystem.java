package com.gempukku.ld43.logic.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.gempukku.ld43.model.*;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.ObstacleComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;
import com.gempukku.secsy.gaming.physics.basic2d.SensorTriggerComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.rendering.postprocess.tint.color.ColorTintComponent;
import com.gempukku.secsy.gaming.time.TimeEntityProvider;
import com.gempukku.secsy.gaming.time.TimeManager;
import com.gempukku.secsy.gaming.time.delay.DelayManager;
import com.gempukku.secsy.gaming.time.delay.DelayedActionTriggeredEvent;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;

@RegisterSystem
public class LevelSystem extends AbstractLifeCycleSystem {
    public static final int PILE_COUNT_PER_METER = 10;
    @Inject
    private EntityManager entityManager;
    @Inject
    private GameEntityProvider gameEntityProvider;
    @Inject
    private TimeEntityProvider timeEntityProvider;
    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private DelayManager delayManager;
    @Inject
    private TimeManager timeManager;

    private long startTime;
    private long completionTime = -10000;

    @ReceiveEvent
    public void adjustCameraTint(GameLoopUpdate gameLoop, EntityRef game, GameScreenComponent gameScreen) {
        EntityRef camera = cameraEntityProvider.getCameraEntity();
        ColorTintComponent colorTint = camera.getComponent(ColorTintComponent.class);

        long time = timeManager.getTime();
        if (completionTime <= time && time < completionTime + 2000) {
            if (time < completionTime + 1000)
                colorTint.setFactor((time - completionTime) / 1000f);
            else
                colorTint.setFactor((completionTime + 2000 - time) / 1000f);
            camera.saveChanges();
        } else if (time < startTime + 1000) {
            colorTint.setFactor((startTime + 1000 - time) / 1000f);
            camera.saveChanges();
        } else {
            colorTint.setFactor(0);
            camera.saveChanges();
        }
    }

    @ReceiveEvent
    public void gameStarted(GameStarted gameStarted, EntityRef gameEntity, LevelComponent level) {
        startTime = timeManager.getTime();
        loadLevel("levels/level" + level.getLevelIndex() + ".json");
    }

    @ReceiveEvent
    public void loadLevelAfterDelay(DelayedActionTriggeredEvent trigger, EntityRef gameEntity, LevelComponent level) {
        destroyAllNonGlobalEntities();
        loadLevel("levels/level" + level.getLevelIndex() + ".json");
    }

    @ReceiveEvent
    public void levelComplete(SensorContactBegin sensorContactBegin, EntityRef entity, PlayerComponent player) {
        if (sensorContactBegin.getSensorType().equals("completionSensor")
                && sensorContactBegin.getSensorTrigger().hasComponent(CompletionComponent.class)) {
            entityManager.destroyEntity(entity);

            EntityRef gameEntity = gameEntityProvider.getGameEntity();
            LevelComponent level = gameEntity.getComponent(LevelComponent.class);
            level.setLevelIndex(level.getLevelIndex() + 1);
            gameEntity.saveChanges();

            completionTime = timeManager.getTime();

            delayManager.addDelayedAction(gameEntity, "loadLevel", 1000);
        }
    }

    @ReceiveEvent
    public void playerDied(PlayerDied playerDied, EntityRef entity) {
        entityManager.destroyEntity(entity);

        completionTime = timeManager.getTime();

        EntityRef gameEntity = gameEntityProvider.getGameEntity();
        delayManager.addDelayedAction(gameEntity, "loadLevel", 1000);
    }

    private void destroyAllNonGlobalEntities() {
        for (EntityRef entity : entityManager.getAllEntities()) {
            if (!entity.hasComponent(GlobalEntityComponent.class)) {
                entityManager.destroyEntity(entity);
            }
        }
    }

    private void loadLevel(String levelFile) {
        JSONObject level = loadJSON(levelFile);
        JSONObject playerObject = (JSONObject) level.get("player");
        JSONObject exitObject = (JSONObject) level.get("exit");
        JSONArray enemyArray = (JSONArray) level.get("enemies");
        JSONArray platformArray = (JSONArray) level.get("platforms");
        JSONArray objectArray = (JSONArray) level.get("objects");

        createPlayer(getFloat(playerObject, "x"), getFloat(playerObject, "y"));
        createExit(getFloat(exitObject, "x"), getFloat(exitObject, "y"));
        for (Object enemy : enemyArray) {
            JSONObject enemyObj = (JSONObject) enemy;
            createEnemy((String) enemyObj.get("type"), getFloat(enemyObj, "x"), getFloat(enemyObj, "y"));
        }
        for (Object platform : platformArray) {
            JSONObject platformObj = (JSONObject) platform;
            createPlatform((String) platformObj.get("type"), getFloat(platformObj, "x"), getFloat(platformObj, "y"),
                    getFloat(platformObj, "width"), getFloat(platformObj, "height"));
        }
        for (Object object : objectArray) {
            JSONObject objectObj = (JSONObject) object;
            if (objectObj.containsKey("x"))
                createEntityAtPosition((String) objectObj.get("prefab"), getFloat(objectObj, "x"), getFloat(objectObj, "y"));
            else
                createEntity((String) objectObj.get("prefab"));
        }
    }

    private void createEntity(String prefab) {
        entityManager.createEntityFromPrefab(prefab);
    }

    private void createEntityAtPosition(String prefab, float x, float y) {
        EntityRef entity = entityManager.createEntityFromPrefab(prefab);
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);
        entity.saveChanges();
    }

    private JSONObject loadJSON(String levelFile) {
        FileHandle file = Gdx.files.internal(levelFile);
        JSONParser jsonParser = new JSONParser();
        Reader reader = file.reader("UTF-8");
        try {
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to load level", exp);
        } catch (ParseException exp) {
            throw new RuntimeException("Unable to load level", exp);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private float getFloat(JSONObject object, String key) {
        return ((Number) object.get(key)).floatValue();
    }

    private void createExit(float x, float y) {
        createEntityAtPosition("levelExit", x, y);
    }

    private void createEnemy(String prefab, float x, float y) {
        createEntityAtPosition(prefab, x, y);
    }

    private void createPlayer(float x, float y) {
        createEntityAtPosition("playerEntity", x, y);
    }

    private void createPlatform(String type, float x, float y, float width, float height) {
        EntityRef platformEntity = entityManager.createEntityFromPrefab(type);

        Position2DComponent position = platformEntity.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);

        ObstacleComponent obstacle = platformEntity.getComponent(ObstacleComponent.class);
        obstacle.setRight(width);
        obstacle.setUp(height);

        SensorTriggerComponent sensorTrigger = platformEntity.getComponent(SensorTriggerComponent.class);
        sensorTrigger.setRight(width);
        sensorTrigger.setUp(height);

        PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
        platform.setRight(width);
        platform.setUp(height);

        DustLayerComponent dustLayer = platformEntity.getComponent(DustLayerComponent.class);
        if (dustLayer != null) {
            dustLayer.setRight(width);
            dustLayer.setUp(height * 3);
            dustLayer.setDown(height);
            float[] layerDepth = new float[MathUtils.floor(width * PILE_COUNT_PER_METER)];
            dustLayer.setLayerDepth(layerDepth);
        }

        platformEntity.saveChanges();
    }

    @Override
    public float getPriority() {
        return -1;
    }
}
