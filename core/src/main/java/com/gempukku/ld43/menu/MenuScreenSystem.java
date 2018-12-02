package com.gempukku.ld43.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class MenuScreenSystem extends AbstractLifeCycleSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;

    private Texture butterfly;
    private Stage stage;
    private Skin skin;
    private boolean debug = false;

    @Override
    public void initialize() {
        createSkin();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table(skin);
        table.setDebug(debug);
        table.setFillParent(true);
        stage.addActor(table);

        final TextButton newGameButton = new TextButton("New game", skin);
        newGameButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (newGameButton.isChecked())
                            gameEntityProvider.getGameEntity().send(new GoToGame());
                    }
                });
        final TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (exitButton.isChecked())
                            Gdx.app.exit();
                    }
                });

        Label titleLabel = new Label("MOM", skin, "title");
        table.add(titleLabel).colspan(2).pad(10).height(150);
        table.row();
        Label subtitleLabel = new Label("Sacrifices must? be made", skin, "subtitle");
        table.add(subtitleLabel).colspan(2).pad(10).height(100);
        table.row();


        Table middleTable = new Table();
        middleTable.setDebug(debug);

        butterfly = new Texture(Gdx.files.internal("images/butterfly.png"));

        Image image = new Image(butterfly);

        table.add(image);
        table.add(middleTable).width(600);
        table.row();

        middleTable.add(newGameButton).height(70).width(300).pad(10);
        middleTable.row();
        middleTable.add(exitButton).height(70).width(300).pad(10);

        Label dedicationLabel = new Label("For all the moms out there", skin, "dedication");
        table.add(dedicationLabel).colspan(2).pad(10).height(60);
        table.row();
    }

    private void createSkin() {

        // Title font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Kindergarden.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 5;
        BitmapFont titleFont = generator.generateFont(parameter);
        parameter.size = 70;
        parameter.borderWidth = 3;
        BitmapFont subtitleFont = generator.generateFont(parameter);
        parameter.size = 40;
        parameter.borderWidth = 2;
        BitmapFont dedicationFont = generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin();
        skin.add("default-font", dedicationFont);
        skin.add("title", titleFont);
        skin.add("subtitle", subtitleFont);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
        skin.load(Gdx.files.internal("skin/menu-skin.json"));
    }

    @ReceiveEvent
    public void renderMenu(RenderToPipeline renderToPipeline, EntityRef camera, MenuScreenComponent menuScreen) {
        stage.getViewport().update(renderToPipeline.getWidth(), renderToPipeline.getHeight(), true);

        stage.act();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        // baby blue: b6e0f0
        Gdx.gl.glClearColor(0xb6 / 255f, 0xe0 / 255f, 0xf0 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.draw();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        skin.dispose();
        stage.dispose();
    }
}
