package com.gempukku.ld43.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.gempukku.secsy.gaming.audio.AudioManager;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class MenuScreenSystem extends AbstractLifeCycleSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;
    @Inject
    private AudioManager audioManager;

    private Texture butterfly;
    private Sound sampleSound;

    private Stage menuStage;
    private Stage settingsStage;

    private Skin skin;
    private boolean debug = false;

    private Stage currentStage;

    @Override
    public void initialize() {
        createSkin();

        createMenuStage();
        createSettingsStage();

        setCurrentStage(menuStage);
    }

    private void createSettingsStage() {
        sampleSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));

        settingsStage = new Stage();

        Table table = new Table(skin);
        table.setDebug(debug);
        table.setFillParent(true);

        final Label masterLabel = new Label("Master volume", skin, "subtitle");
        table.add(masterLabel);
        table.row();

        final Slider masterSlider = new Slider(0f, 1f, 0.01f, false, skin);
        masterSlider.setValue(0.05f);
        masterSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        audioManager.setMasterVolume(masterSlider.getValue());
                    }
                });
        table.add(masterSlider).width(300);
        table.row();

        Label musicLabel = new Label("Music volume", skin, "subtitle");
        table.add(musicLabel);
        table.row();

        final Slider musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        musicSlider.setValue(1f);
        musicSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        audioManager.setMusicVolume(musicSlider.getValue());
                    }
                });
        table.add(musicSlider).width(300);
        table.row();

        Label fxLabel = new Label("FX volume", skin, "subtitle");
        table.add(fxLabel);
        table.row();

        final Slider fxSlider = new Slider(0f, 1f, 0.01f, false, skin);
        fxSlider.setValue(1f);
        fxSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        audioManager.setFXVolume(fxSlider.getValue());
                        audioManager.playSound(sampleSound);
                    }
                });
        table.add(fxSlider).width(300);
        table.row();

        final TextButton backButton = new TextButton("Back to menu", skin);
        backButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (backButton.isChecked())
                            setCurrentStage(menuStage);
                    }
                });
        table.add(backButton).height(70).width(400).pad(10);
        table.row();

        settingsStage.addActor(table);
    }

    private void createMenuStage() {
        menuStage = new Stage();

        Table table = new Table(skin);
        table.setDebug(debug);
        table.setFillParent(true);
        menuStage.addActor(table);

        final TextButton newGameButton = new TextButton("New game", skin);
        newGameButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (newGameButton.isChecked())
                            gameEntityProvider.getGameEntity().send(new GoToGame());
                    }
                });
        final TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (settingsButton.isChecked())
                            setCurrentStage(settingsStage);
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
        middleTable.add(settingsButton).height(70).width(300).pad(10);
        middleTable.row();
        middleTable.add(exitButton).height(70).width(300).pad(10);
        middleTable.row();

        Label dedicationLabel = new Label("For all the moms out there", skin, "dedication");
        table.add(dedicationLabel).colspan(2).pad(10).height(60);
        table.row();

        Label aboutLabel = new Label("Created with LibGDX for Ludum Dare 43", skin, "dedication");
        table.add(aboutLabel).colspan(2).pad(10).height(60);
        table.row();
    }

    private void setCurrentStage(Stage stage) {
        currentStage = stage;
        Gdx.input.setInputProcessor(currentStage);
    }

    private void createSkin() {
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
        currentStage.getViewport().update(renderToPipeline.getWidth(), renderToPipeline.getHeight(), true);

        currentStage.act();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        // baby blue: b6e0f0
        Gdx.gl.glClearColor(0xb6 / 255f, 0xe0 / 255f, 0xf0 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        currentStage.draw();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        sampleSound.dispose();
        skin.dispose();
        settingsStage.dispose();
        menuStage.dispose();
    }
}
