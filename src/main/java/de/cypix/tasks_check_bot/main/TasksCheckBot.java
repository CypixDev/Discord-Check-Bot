package de.cypix.tasks_check_bot.main;

import de.cypix.tasks_check_bot.configuration.ConfigManager;
import de.cypix.tasks_check_bot.console.ConsoleManager;
import de.cypix.tasks_check_bot.events.MessageListener;
import de.cypix.tasks_check_bot.events.ReadyListener;
import de.cypix.tasks_check_bot.events.UserLogger;
import de.cypix.tasks_check_bot.sql.SQLConnector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class TasksCheckBot {

    private static TasksCheckBot instance;

    private static JDA jda;
    private static JDABuilder builder;

    private static ConfigManager configManager;
    private static ConsoleManager consoleManager;
    private static SQLConnector sqlConnector;


    public static void main(String[] args) throws LoginException {
        instance = new TasksCheckBot();
        configManager = new ConfigManager();
        consoleManager = new ConsoleManager();
        consoleManager.start();
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static TasksCheckBot getInstance() {
        return instance;
    }

    public void startBot(String token) {
        try{
            builder = JDABuilder.createDefault(token);

            // Disable parts of the cache
            builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
            // Enable the bulk delete event
            builder.setBulkDeleteSplittingEnabled(false);
            // Disable compression (not recommended)
            builder.setCompression(Compression.NONE);
            // Set activity (like "playing Something")
            builder.setActivity(Activity.watching("School work"));

            jda = builder.build();

            jda.addEventListener(new ReadyListener());
            jda.addEventListener(new MessageListener());
            jda.addEventListener(new UserLogger());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void setSqlConnector(SQLConnector sqlConnector) {
        TasksCheckBot.sqlConnector = sqlConnector;
    }

    public static SQLConnector getSqlConnector() {
        return sqlConnector;
    }

    public static ConsoleManager getConsoleManager() {
        return consoleManager;
    }

    public static JDA getJda() {
        return jda;
    }

    public static JDABuilder getBuilder() {
        return builder;
    }
}
