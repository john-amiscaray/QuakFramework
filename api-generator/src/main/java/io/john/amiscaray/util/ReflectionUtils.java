package io.john.amiscaray.util;

import com.google.common.reflect.ClassPath;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * A Utility class to load classes from the project using this maven plugin. Created because for some reason the reflections
 * library was not working in this case...
 */
public class ReflectionUtils {

    public static List<? extends Class<?>> loadClassesFromPackage(File projectClassOutputDirectory, String packageName) throws IOException {
        var urls = new URL[]{projectClassOutputDirectory.toURI().toURL()};
        var classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());

        return ClassPath.from(classLoader)
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName()
                        .startsWith(packageName))
                .map(ClassPath.ClassInfo::load)
                .toList();
    }

}
