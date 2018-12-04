package com.gempukku.ld43.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gempukku.secsy.gaming.SecsyGameApplication;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Set<String> activeProfiles = new HashSet<String>();
        // Basic setup - scanning all the things from ClassPath
        activeProfiles.add("reflectionsEntityComponentFieldHandler");
        activeProfiles.add("reflectionsNameComponentManager");
        activeProfiles.add("reflectionsComponentFieldConverter");
        activeProfiles.add("reflectionsPrefabSource");
        // Basic entity related staff
        activeProfiles.add("simpleEntityManager");
        activeProfiles.add("prefabManager");
        activeProfiles.add("nameConventionComponents");
        activeProfiles.add("annotationEventDispatcher");
        activeProfiles.add("simpleEntityIndexManager");
        // Rest of the generic stuff
        activeProfiles.add("gameLoop");
        activeProfiles.add("eventInputProcessor");
        activeProfiles.add("audioManager");
        activeProfiles.add("pipelineRenderer");
        activeProfiles.add("easing");
        activeProfiles.add("time");
        activeProfiles.add("splash");
        activeProfiles.add("textureAtlas");
        activeProfiles.add("2dCamera");
        activeProfiles.add("platformer2dMovement");
        activeProfiles.add("basic2dPhysics");
        activeProfiles.add("grainPostProcessor");
        activeProfiles.add("backgroundMusic");
        activeProfiles.add("ai");
        activeProfiles.add("delayActions");
        activeProfiles.add("colorTint");
        activeProfiles.add("particle2d");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.fullscreen = true;
        config.resizable = false;

        DisplayMode largestDisplayMode = getLargestDisplayMode();
        config.width = largestDisplayMode.getWidth();
        config.height = largestDisplayMode.getHeight();

        config.foregroundFPS = 0;
        config.vSyncEnabled = true;
        config.title = "Ludum Dare 43";
        new LwjglApplication(new SecsyGameApplication(0, 0, activeProfiles), config);
    }

    private static DisplayMode getLargestDisplayMode() {
        int width = 0;
        DisplayMode result = null;
        GraphicsEnvironment ge = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (DisplayMode displayMode : gs[0].getDisplayModes()) {
            if (displayMode.getWidth() > width) {
                width = displayMode.getWidth();
                result = displayMode;
            }
        }
        return result;
    }
}
