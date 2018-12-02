package com.gempukku.ld43.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.gempukku.ld43.menu.GoToGame;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.ObstacleComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorTriggerComponent;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;

@RegisterSystem
public class LevelBuilder extends AbstractLifeCycleSystem {
    public static final int PILE_COUNT_PER_METER = 10;
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void initialize(GoToGame goToGame) {
        loadLevel("levels/level1.json");
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
            createEntityAtPosition((String) objectObj.get("prefab"), getFloat(objectObj, "x"), getFloat(objectObj, "y"));
        }
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
            dustLayer.setUp(height * 2);
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
