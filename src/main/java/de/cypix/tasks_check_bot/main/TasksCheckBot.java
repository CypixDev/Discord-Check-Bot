package de.cypix.tasks_check_bot.main;

import de.cypix.tasks_check_bot.commands.cmd.*;
import de.cypix.tasks_check_bot.commands.CommandManager;
import de.cypix.tasks_check_bot.configuration.ConfigManager;
import de.cypix.tasks_check_bot.console.ConsoleManager;
import de.cypix.tasks_check_bot.events.CommandListener;
import de.cypix.tasks_check_bot.events.ReactionListener;
import de.cypix.tasks_check_bot.events.ReadyListener;
import de.cypix.tasks_check_bot.events.UserLogger;
import de.cypix.tasks_check_bot.manager.TasksManager;
import de.cypix.tasks_check_bot.scheduler.CheckScheduler;
import de.cypix.tasks_check_bot.sql.SQLConnector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;

public class TasksCheckBot {

    private static TasksCheckBot instance;

    private static JDA jda;
    private static JDABuilder builder;

    private static ConfigManager configManager;
    private static ConsoleManager consoleManager;
    private static SQLConnector sqlConnector;
    private static CommandManager commandManager;
    public final static Logger logger = Logger.getLogger(TasksCheckBot.class);


    public static void main(String[] args) throws LoginException {
        setupLogger();
        instance = new TasksCheckBot();
        configManager = new ConfigManager();
        consoleManager = new ConsoleManager();
        consoleManager.start();
        commandManager = new CommandManager();

        registerCommands();

        if(configManager.isStatingAutomatically()){
            instance.startSQL();
            instance.startBot(true);
        }
        CheckScheduler scheduler = new CheckScheduler(new Runnable() {
            @Override
            public void run() {
                TasksManager.updateAllTasks();
            }
        }, 60);
    }

    private static void setupLogger() {

        logger.info("dings");
        logger.error("dings");
    }

    private static void registerCommands() {
        commandManager.registerCommand("help", new CMDHelp());
        commandManager.registerCommand("ping", new CMDPing());
        commandManager.registerCommand("deltask", new CMDDelTask());
        commandManager.registerCommand("addtask", new CMDAddTask());
        commandManager.registerCommand("delalltasks", new CMDDelAllTasks());
        commandManager.registerCommand("updatetask", new CMDUpdateTask());
        commandManager.registerCommand("archive", new CMDArchive());
        commandManager.registerCommand("list", new CMDList());
        commandManager.registerCommand("addfile", new CMDAddFile());
        commandManager.registerCommand("update", new CMDUpdate());
        commandManager.registerCommand("todo", new CMDTodo());
        logger.info("Registered all tasks!");
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static TasksCheckBot getInstance() {
        return instance;
    }

    public void startSQL(){
        sqlConnector = new SQLConnector(true);
        System.out.println("Stated SQL....");
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

            configureMemoryUsage();

            jda = builder.build();

            jda.addEventListener(new ReadyListener());
            jda.addEventListener(new CommandListener());
            jda.addEventListener(new ReactionListener());
            jda.addEventListener(new UserLogger());

        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Started Bot...");

    }
    public void startBot(boolean fromConfig) {
        try{
            builder = JDABuilder.createDefault(configManager.getToken());

            // Disable parts of the cache
            builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
            // Enable the bulk delete event
            builder.setBulkDeleteSplittingEnabled(false);
            // Disable compression (not recommended)
            builder.setCompression(Compression.NONE);
            // Set activity (like "playing Something")
            builder.setActivity(Activity.watching("School work"));

            configureMemoryUsage();

            jda = builder.build();

            jda.addEventListener(new ReadyListener());
            jda.addEventListener(new CommandListener());
            jda.addEventListener(new ReactionListener());
            jda.addEventListener(new UserLogger());

        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Started Bot...");
    }
    public void configureMemoryUsage() {
        // Disable cache for member activities (streaming/games/spotify)
        builder.disableCache(CacheFlag.ACTIVITY); //TODO: maybe later...

        // Only cache members who are either in a voice channel or owner of the guild
        builder.setMemberCachePolicy(MemberCachePolicy.ALL.and(MemberCachePolicy.ONLINE));

        // Disable member chunking on startup
        //builder.setChunkingFilter(ChunkingFilter.NONE);

        // Disable presence updates and typing events
        //builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);

        // Consider guilds with more than 50 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled.
        //builder.setLargeThreshold(50);
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

    public static CommandManager getCommandManager() {
        return commandManager;
    }
}
