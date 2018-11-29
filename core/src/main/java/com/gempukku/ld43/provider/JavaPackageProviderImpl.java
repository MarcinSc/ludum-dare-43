package com.gempukku.ld43.provider;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.JavaPackageProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RegisterSystem(shared = JavaPackageProvider.class)
public class JavaPackageProviderImpl implements JavaPackageProvider {
    private Set<String> javaPackages = new HashSet<String>();

    public JavaPackageProviderImpl() {
        javaPackages.add("com.gempukku");
    }

    @Override
    public Collection<String> getJavaPackages() {
        return javaPackages;
    }
}
