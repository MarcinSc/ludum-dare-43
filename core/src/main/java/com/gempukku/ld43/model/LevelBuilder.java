package com.gempukku.ld43.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

        createPlayer(getFloat(playerObject, "x"), getFloat(playerObject, "y"));
        createExit(getFloat(exitObject, "x"), getFloat(exitObject, "y"));
        for (Object enemy : enemyArray) {
            JSONObject enemyObj = (JSONObject) enemy;
            createEnemy((String) enemyObj.get("type"), getFloat(enemyObj, "x"), getFloat(enemyObj, "y"));
        }
        for (Object platform : platformArray) {
            JSONObject platformObj = (JSONObject) platform;
            createPlatform(getFloat(platformObj, "x"), getFloat(platformObj, "y"),
                    getFloat(platformObj, "width"), getFloat(platformObj, "height"));
        }
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
        EntityRef levelExit = entityManager.createEntityFromPrefab("levelExit");
        Position2DComponent position = levelExit.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);
        levelExit.saveChanges();
    }

    private void createEnemy(String prefab, float x, float y) {
        EntityRef enemy = entityManager.createEntityFromPrefab(prefab);
        Position2DComponent position = enemy.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);
        enemy.saveChanges();
    }

    private void createPlayer(float x, float y) {
        EntityRef playerEntity = entityManager.createEntityFromPrefab("playerEntity");
        Position2DComponent position = playerEntity.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);
        playerEntity.saveChanges();
    }

    private void createPlatform(float x, float y, float width, float height) {
        EntityRef platformEntity = entityManager.createEntityFromPrefab("platform");

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

        platformEntity.saveChanges();
    }

    @Override
    public float getPriority() {
        return -1;
    }
}
