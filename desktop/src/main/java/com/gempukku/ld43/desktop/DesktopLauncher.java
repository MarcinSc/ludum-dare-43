package com.gempukku.ld43.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gempukku.secsy.gaming.SecsyGameApplication;

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

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 600;
        config.resizable = false;

        config.foregroundFPS = 0;
        config.vSyncEnabled = true;
        config.title = "Ludum Dare 43";
        new LwjglApplication(new SecsyGameApplication(800, 600, activeProfiles), config);
    }
}
