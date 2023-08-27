package pl.underman.playerstatz.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.Getter;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.exceptions.ConfigValidationException;
import pl.underman.playerstatz.util.annotations.ConfigScan;
import pl.underman.playerstatz.util.annotations.Configuration;
import pl.underman.playerstatz.util.annotations.NotNull;
import pl.underman.playerstatz.util.annotations.YamlConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class PluginConfigurationContext {
    private final Map<Class<?>, Object> configMap = new HashMap<>();

    @Getter
    private final Set<String> configFiles = new HashSet<>();

    private final ObjectMapper objectMapper;

    public PluginConfigurationContext(Class<?> configurationClass) {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
        this.objectMapper = new ObjectMapper(yamlFactory);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        initializeContext(configurationClass);
    }

    private void initializeContext(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Configuration.class)) {
            Logger.error("Class " + clazz.getName() + " is not a configuration class!");
            return;
        }
        ConfigScan configScan = clazz.getAnnotation(ConfigScan.class);

        if (configScan == null) {
            Logger.error("Class " + clazz.getName() + " has no ConfigScan annotation!");
            return;
        }

        String[]      packageValues = configScan.value();
        Set<Class<?>> classes       = new HashSet<>();

        for (String packageValue : packageValues) {
            classes.addAll(AnnotationHelper.findClassesWithAnnotation(
                    packageValue,
                    YamlConfig.class
            ));
        }

        loadFiles(classes);

    }

    private void loadFiles(Set<Class<?>> classes) {
        for (Class<?> loadingClass : classes) {
            String fileName = getFileName(loadingClass);
            configFiles.add(fileName);

            File configDirectory = new File(
                    PlayerStatz.getInstance().getDataFolder().getAbsolutePath() + File.separator +
                            "config");

            if (!configDirectory.exists()) {
                configDirectory.mkdirs();
            }

            File configFile = new File(configDirectory + File.separator + fileName + ".yml");

            try {
                if (configFile.createNewFile()) {
                    createFile(loadingClass, configFile);
                } else {
                    loadFile(loadingClass, configFile);
                }
            } catch (IOException e) {
                Logger.error("Could not create configuration file: " + configFile.getName());
                Logger.error(e.getMessage());
            }
        }
    }


    private <T> void loadFile(Class<T> clazz, File file) {
        T config = null;
        try {
            config = objectMapper.readValue(file, clazz);
            validateConfigValues(clazz, config, file.getAbsolutePath());
            Logger.info(file.getAbsolutePath() + " loaded successfully");
        } catch (Exception e) {
            config = createNewConfigInstance(clazz);
            Logger.error("Could not load configuration file: " + file.getName());
            Logger.error(e.getMessage());
            Logger.error("Loaded default configuration");
        }

        configMap.put(clazz, config);

    }

    private <T> void createFile(Class<T> clazz, File file) {
        T instance = null;
        try {
            instance = createNewConfigInstance(clazz);
            objectMapper.writeValue(file, instance);
            Logger.info(file.getAbsolutePath() + " created successfully");
        } catch (Exception e) {
            Logger.error("Could not generate configuration file content: " + file.getName());
            Logger.error(e.getMessage());
        }
        configMap.put(clazz, instance);
    }

    private String getFileName(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(YamlConfig.class)) {
            Logger.error("Class " + clazz.getName() + " is not a configuration class!");
            return null;
        }

        YamlConfig yamlConfig = clazz.getAnnotation(YamlConfig.class);
        String     fileName   = yamlConfig.name();
        if (fileName == null || fileName.isEmpty()) {
            fileName = clazz.getSimpleName();
        }
        return fileName;
    }

    private <T> void validateConfigValues(Class<T> clazz, T config, String fileName) {

        List<String> invalidFields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(NotNull.class)) {
                try {
                    if (field.get(config) == null) {
                        invalidFields.add(field.getName());
                    }
                } catch (IllegalAccessException e) {
                    Logger.error("Could not access field " + field.getName() + " in class " +
                            clazz.getName());
                }
            }
        }
        if (!invalidFields.isEmpty()) {
            throw new ConfigValidationException(
                    "Fields " + invalidFields + " in configuration file " + fileName +
                            " should not be empty!"
            );
        }
    }

    private <T> T createNewConfigInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Logger.error("Could not create new instance of class " + clazz.getName());
            Logger.error(e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(Class<T> clazz) {
        return (T) configMap.get(clazz);
    }
}
