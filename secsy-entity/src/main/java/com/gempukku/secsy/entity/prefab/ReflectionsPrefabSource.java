package com.gempukku.secsy.entity.prefab;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.google.common.collect.Multimap;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.Vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@RegisterSystem(profiles = "reflectionsPrefabSource", shared = PrefabSource.class)
public class ReflectionsPrefabSource extends AbstractLifeCycleSystem implements PrefabSource {
    private Reflections reflections;
    private String fileExtension = "prefab";
    private String folderName = "prefabs";

    @Override
    public void preInitialize() {
        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(new PrefabsScanner())
                .setUrls(ClasspathHelper.forPackage(folderName));

        reflections = new Reflections(scanConfiguration, new PrefabsScanner());
    }

    @Override
    public void processAllPrefabs(PrefabSink prefabSink) throws IOException {
        Multimap<String, String> resources = reflections.getStore().get("PrefabsScanner");

        for (String prefabName : resources.keySet()) {
            Collection<String> paths = resources.get(prefabName);
            if (paths.size() > 1)
                throw new IllegalStateException("More than one prefab with the same name found: " + prefabName);

            try {
                InputStream prefabInputStream = ReflectionsPrefabSource.class.getResourceAsStream("/" + paths.iterator().next());
                try {
                    prefabSink.processPrefab(prefabName, prefabInputStream);
                } finally {
                    prefabInputStream.close();
                }
            } catch (IOException exp) {
                throw new RuntimeException("Unable to read prefab data", exp);
            }
        }
    }

    private class PrefabsScanner extends ResourcesScanner {
        private int extensionLength = fileExtension.length();

        public boolean acceptsInput(String file) {
            return file.endsWith("." + fileExtension);
        }

        public Object scan(Vfs.File file, Object classObject) {
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.length() - extensionLength - 1);
            this.getStore().put(fileName, file.getRelativePath());
            return classObject;
        }
    }
}
