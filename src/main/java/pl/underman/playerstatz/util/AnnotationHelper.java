package pl.underman.playerstatz.util;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public abstract class AnnotationHelper {

    private AnnotationHelper() {
    }

    @SafeVarargs
    public static Set<Class<?>> findClassesWithAnnotation(
            String packageName,
            Class<? extends Annotation>... annotations
    ) {
        Reflections   reflections = new Reflections(packageName);
        Set<Class<?>> result      = new HashSet<>();
        for (Class<? extends Annotation> annotation : annotations) {
            result.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return result;
    }
}
