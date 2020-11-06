package de.cypix.tasks_check_bot.main;

import de.cypix.tasks_check_bot.configuration.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class TasksCheckBot {

    private static JDA jda;
    private static JDABuilder builder;

    private static ConfigManager configManager;

    public static void main(String[] args) throws LoginException {
        configManager = new ConfigManager();

        jda = JDABuilder.createDefault(configManager.getToken()).build();
        builder = JDABuilder.createDefault(args[0]);

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Disable compression (not recommended)
        builder.setCompression(Compression.NONE);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.watching("TV"));

        builder.build();
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
