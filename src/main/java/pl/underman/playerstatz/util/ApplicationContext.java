package pl.underman.playerstatz.util;

import org.bukkit.event.Listener;
import org.reflections.Reflections;
import pl.underman.playerstatz.util.annotations.EventListener;
import pl.underman.playerstatz.util.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class ApplicationContext {
    Map<Class<?>, Object>   componentsRegistryMap = new HashMap<>();
    Map<Class<?>, Listener> listenersRegistryMap  = new HashMap<>();

    public ApplicationContext(Class<?> configurationClass) {
        initializeContext(configurationClass);
    }

    private void initializeContext(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Configuration.class)) {
            Logger.debug("ApplicationContext.initializeContext: " + clazz.getName() + " is not a configuration class!");
            return;
        }

        ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
        String[]      packageValues = componentScan.value();
        Set<Class<?>> classes       = new HashSet<>();
        Arrays.stream(packageValues).forEach(packageValue -> classes.addAll(findClasses(packageValue)));
        instantiateComponents(classes);
        autowireFields();
    }

    private void instantiateComponents(Set<Class<?>> classes) {
        for (Class<?> loadingClass : classes) {
            try {
                if (loadingClass.isAnnotationPresent(Component.class)) {
                    Constructor<?> constructor = loadingClass.getDeclaredConstructor();
                    Object         newInstance = constructor.newInstance();
                    componentsRegistryMap.put(loadingClass, newInstance);
                }
                if (loadingClass.isAnnotationPresent(EventListener.class)) {
                    if (Listener.class.isAssignableFrom(loadingClass)) {
                        Constructor<?> constructor = loadingClass.getDeclaredConstructor();
                        Listener       newInstance = (Listener) constructor.newInstance();
                        listenersRegistryMap.put(loadingClass, newInstance);
                        componentsRegistryMap.put(loadingClass, newInstance);
                    } else {
                        Logger.debug("ApplicationContext.instantiateComponents: " + loadingClass.getName() +
                                " is not bukkit event listener!");
                    }
                }
            } catch (Exception e) {
                Logger.debug("ApplicationContext.instantiateComponents: " + loadingClass.getName() +
                        "could not be instantiated!");
            }
        }
    }

    private void autowireFields() {
        for (Map.Entry<Class<?>, Object> entry : componentsRegistryMap.entrySet()) {
            Class<?>    clazz  = entry.getKey();
            Object      object = entry.getValue();
            List<Field> fields = new ArrayList<>();

            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    try {
                        field.set(object, componentsRegistryMap.get(field.getType()));
                    } catch (IllegalAccessException e) {
                        Logger.debug("ApplicationContext.autowireFields: " + field.getName() + " could not be autowired!");
                    }
                }
            }
        }
    }

    private Set<Class<?>> findClasses(String packageName) {
        Reflections   reflections = new Reflections(packageName);
        Set<Class<?>> result      = new HashSet<>();

        result.addAll(reflections.getTypesAnnotatedWith(Component.class));
        result.addAll(reflections.getTypesAnnotatedWith(EventListener.class));

        return result;
    }


    @SuppressWarnings("unchecked")
    public <T> T getComponentInstance(Class<T> clazz) {
        if (componentsRegistryMap.containsKey(clazz)) {
            return (T) componentsRegistryMap.get(clazz);
        } else {
            Logger.debug("ApplicationContext.getInstance: " + clazz.getName() + " not found in componentsRegistryMap");
        }
        return null;
    }

    public Listener[] getListenerInstances() {
        Listener[] result = new Listener[listenersRegistryMap.size()];
        Arrays.setAll(result, i -> listenersRegistryMap.values().toArray()[i]);
        return result;
    }


}
