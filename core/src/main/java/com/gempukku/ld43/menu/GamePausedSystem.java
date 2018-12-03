package com.gempukku.ld43.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gempukku.ld43.model.GameScreenComponent;
import com.gempukku.ld43.model.GlobalEntityComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.PausedComponent;

@RegisterSystem
public class GamePausedSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityManager entityManager;
    @Inject
    private GameEntityProvider gameEntityProvider;

    private boolean paused;
    private boolean isEscPressed = false;

    private Skin skin;
    private Stage pausedStage;
    private boolean debug;

    @Override
    public void initialize() {
        createSkin();

        pausedStage = new Stage();

        Table table = new Table(skin);
        table.setDebug(debug);
        table.setFillParent(true);
        pausedStage.addActor(table);


        Label titleLabel = new Label("Game paused", skin);
        table.add(titleLabel);
        table.row();

        final TextButton textButton = new TextButton("Go to menu", skin);
        textButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        goToMenu();
                    }
                });
        table.add(textButton).height(70).width(300).pad(10);
        table.row();
    }

    @ReceiveEvent
    public void goToGame(GoToGame goToGame) {
        Gdx.input.setInputProcessor(pausedStage);
    }

    private void goToMenu() {
        paused = false;
        for (EntityRef entity : entityManager.getAllEntities()) {
            if (!entity.hasComponent(GlobalEntityComponent.class)) {
                entityManager.destroyEntity(entity);
            }
        }
        gameEntityProvider.getGameEntity().send(new GoToMenu());
    }

    private void createSkin() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Kindergarden.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2;
        BitmapFont dedicationFont = generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin();
        skin.add("default-font", dedicationFont);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
        skin.load(Gdx.files.internal("skin/pause-skin.json"));
    }

    @ReceiveEvent
    public void checkForGameMenu(GameLoopUpdate gameLoopUpdate, EntityRef gameEntity, GameScreenComponent gameScreen) {
        if (!isEscPressed && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            isEscPressed = true;
            if (!paused) {
                paused = true;
                gameEntity.createComponent(PausedComponent.class);
            } else {
                paused = false;
                gameEntity.removeComponents(PausedComponent.class);
            }
            gameEntity.saveChanges();
        } else if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            isEscPressed = false;
        }
    }

    @ReceiveEvent(priority = -10)
    public void renderPausedMenu(RenderToPipeline renderToPipeline, EntityRef cameraEntity, GameScreenComponent gameScreen) {
        if (paused) {
            pausedStage.getViewport().update(renderToPipeline.getWidth(), renderToPipeline.getHeight(), true);

            pausedStage.act();
            renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
            pausedStage.draw();
            renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
        }
    }

    @Override
    public void destroy() {
        pausedStage.dispose();
    }
}
