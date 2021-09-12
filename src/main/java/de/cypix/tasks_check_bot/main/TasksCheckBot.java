package de.cypix.tasks_check_bot.main;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cypix.tasks_check_bot.commands.CommandManager;
import de.cypix.tasks_check_bot.commands.cmd.*;
import de.cypix.tasks_check_bot.configuration.ConfigManager;
import de.cypix.tasks_check_bot.console.ConsoleManager;
import de.cypix.tasks_check_bot.events.CommandListener;
import de.cypix.tasks_check_bot.events.ReactionListener;
import de.cypix.tasks_check_bot.events.ReadyListener;
import de.cypix.tasks_check_bot.events.UserLogger;
import de.cypix.tasks_check_bot.manager.TasksManager;
import de.cypix.tasks_check_bot.reminder.ReminderManager;
import de.cypix.tasks_check_bot.scheduler.CheckScheduler;
import de.cypix.tasks_check_bot.spring.Application;
import de.cypix.tasks_check_bot.sql.SQLConnector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.SpringApplication;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TasksCheckBot {

    private static TasksCheckBot instance;

    private static SpringApplication springApplication;

    public static Logger logger;

    private static JDA jda;
    private static JDABuilder builder;

    private static ConfigManager configManager;
    private static ConsoleManager consoleManager;
    private static SQLConnector sqlConnector;
    private static CommandManager commandManager;
    private static ReminderManager reminderManager;
    private static CheckScheduler checkScheduler;



    public static void main(String[] args) {
        setupLogger();
        instance = new TasksCheckBot();
        configManager = new ConfigManager();
        consoleManager = new ConsoleManager();
        consoleManager.start();
        commandManager = new CommandManager();

        registerCommands();

        //starting Spring server...
        springApplication = new SpringApplication(Application.class);
        springApplication.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        springApplication.run(args);
                //SpringApplication.run(Application.class, args);

        if(configManager.isStatingAutomatically()){
            instance.startSQL();
            instance.startBot(true);
        }
        reminderManager = new ReminderManager();
        reminderManager.start();

        checkScheduler = new CheckScheduler(new Runnable() {
            @Override
            public void run() {
                TasksManager.updateAllTasks();
            }
        }, 60);
    }

    private static String calcDate(long milliseconds) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyy_MMM_dd_HH_mm");
        Date resultDate = new Date(milliseconds);
        return date_format.format(resultDate);
    }

    private static void setupLogger() {
        logger = Logger.getLogger("Noyce");
        FileHandler fh;
        File file = new File("latest.log");
        new File("log").mkdirs();
        if(file.exists()){
        try {
            Path source = Paths.get("latest.log");
            Path target = Paths.get("log/"+calcDate(System.currentTimeMillis())+".log");
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else System.out.println("Log file not exists!");
        try {


            // This block configure the logger with handler and formatter
            fh = new FileHandler("latest.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void registerCommands() {
        commandManager.registerCommand("help", new CMDHelp());
        commandManager.registerCommand("ping", new CMDPing());
        commandManager.registerCommand("deltask", new CMDDelTask());
        commandManager.registerCommand("addtask", new CMDAddTask());
        commandManager.registerCommand("addptask", new CMDAddPrivateTask());
        commandManager.registerCommand("delalltasks", new CMDDelAllTasks());
        commandManager.registerCommand("updatetask", new CMDUpdateTask());
        commandManager.registerCommand("archive", new CMDArchive());
        commandManager.registerCommand("list", new CMDList());
        commandManager.registerCommand("addfile", new CMDAddFile());
        commandManager.registerCommand("update", new CMDUpdate());
        commandManager.registerCommand("todo", new CMDTodo());
        commandManager.registerCommand("ignore", new CMDIgnore());
        commandManager.registerCommand("reminder", new CMDReminder());
        commandManager.registerCommand("finish", new CMDFinish());
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static TasksCheckBot getInstance() {
        return instance;
    }

    public void startSQL(){
        sqlConnector = new SQLConnector(true);
        System.out.println("Started SQL....");
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

    public static Logger getLogger() {
        return logger;
    }

    public static ReminderManager getReminderManager() {
        return reminderManager;
    }
}
