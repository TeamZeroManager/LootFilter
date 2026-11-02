package com.teamzeromanager.lootfilter.config;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/** Stores client-side filter selections between game launches. */
public final class LootFilterSelection {
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("lootfilter.properties");
    private static final Set<String> ENABLED = new LinkedHashSet<>();
    private static boolean loaded;

    private LootFilterSelection() {}

    private static synchronized void load() {
        if (loaded) return;
        loaded = true;
        if (!Files.exists(FILE)) return;

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(FILE)) {
            properties.load(reader);
            String value = properties.getProperty("enabled", "");
            Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(ENABLED::add);
        } catch (IOException exception) {
            System.err.println("[LootFilter] Could not read filter selections: " + exception.getMessage());
        }
    }

    public static synchronized boolean isEnabled(String registryId) {
        load();
        return ENABLED.contains(registryId);
    }

    public static synchronized void toggle(String registryId) {
        load();
        if (!ENABLED.add(registryId)) ENABLED.remove(registryId);
        save();
    }

    private static void save() {
        Properties properties = new Properties();
        properties.setProperty("enabled", ENABLED.stream().sorted().collect(Collectors.joining(",")));
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                properties.store(writer, "LootFilter enabled registry IDs");
            }
        } catch (IOException exception) {
            System.err.println("[LootFilter] Could not save filter selections: " + exception.getMessage());
        }
    }
}
