package de.cypix.tasks_check_bot.console;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.manager.TasksManager;
import de.cypix.tasks_check_bot.sql.SQLConnector;
import net.dv8tion.jda.api.entities.PrivateChannel;

import java.util.Scanner;

public class ConsoleManager extends Thread{

    private Scanner scanner;

    @Override
    public void run() {
        startConsole();
    }

    public ConsoleManager(){ }

    private void startConsole(){
        scanner = new Scanner(System.in);

        System.out.println("Console is now ready to get commands....");
        while(scanner.hasNext()){
            handleConsoleInput(scanner.nextLine());
        }
    }

    private void handleConsoleInput(String input){
        String[] args = input.split(" ");
        if(args.length == 0){
            return;
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("update")){
                TasksManager.updateTaskOverview();
                System.out.println("updated!");
            }
            if(args[0].equalsIgnoreCase("status")){
                System.out.println("SQL is "+(TasksCheckBot.getSqlConnector() != null && TasksCheckBot.getSqlConnector().isConnected() ? "connected" : "disconnected"));
                System.out.println("Bot is "+(TasksCheckBot.getJda().isUnavailable(400) ? "Offline(400ms)" : "Online"));
                return;
            }
            if(args[0].equalsIgnoreCase("stop")){
                System.out.println("Shutting everything down....");
                TasksCheckBot.getSqlConnector().closeConnection();
                TasksCheckBot.getJda().shutdownNow();
                System.exit(1);
                return;
            }
        }
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("start")){
                if(args[1].equalsIgnoreCase("bot")){
                    TasksCheckBot.getInstance().startBot(true);
                    return;
                }
                if(args[1].equalsIgnoreCase("sql")){
                    TasksCheckBot.getInstance().startSQL();
                    return;
                }
                if(args[1].equalsIgnoreCase("all")){
                    TasksCheckBot.getInstance().startSQL();
                    TasksCheckBot.getInstance().startBot(true);
                    return;
                }
            }
            if(args[0].equalsIgnoreCase("stop")){
                if(args[1].equalsIgnoreCase("sql")){
                    TasksCheckBot.getSqlConnector().closeConnection();
                    System.out.println("Closed connection to sql");
                    return;
                }
                if(args[1].equalsIgnoreCase("bot")){
                    TasksCheckBot.getJda().shutdownNow();
                    System.out.println("Bot is now offline!");
                    return;
                }
                if(args[1].equalsIgnoreCase("schedulers")){
                    TasksCheckBot.getReminderManager().getReminderScheduler().cancel(); //TODO: Don't know if its working and you can maybe not start it again
                }
            }
        }
        if(args.length == 3){
            if(args[0].equalsIgnoreCase("start")){
                if(args[1].equalsIgnoreCase("bot")){
                    String token = args[2];
                    TasksCheckBot.getInstance().startBot(token);
                    System.out.println("Start Bot....");
                    return;
                }
                if(args[1].equalsIgnoreCase("sql")){
                    TasksCheckBot.setSqlConnector(new SQLConnector("localhost", "Discord_tasks",
                            "Discord_tasks", args[2], 3306));
                    System.out.println("Started SQL....");
                    return;
                }
                if(args[1].equalsIgnoreCase("scheduler")){
                    TasksCheckBot.getReminderManager().run();//TODO: don't know if its working or not...
                }
            }
        }
        if(args.length >= 3){
            if(args[0].equalsIgnoreCase("write")){
                String tag = args[1];
                StringBuilder message = new StringBuilder();
                for (int i = 2; i < args.length; i++)
                    message.append(args[i]).append(" ");/*
                TasksCheckBot.getJda().openPrivateChannelById(tag).queue(e -> {
                    e.sendMessage(message.toString()).queue();
                });*/
                for (PrivateChannel privateChannel : TasksCheckBot.getJda().getPrivateChannels()) {
                    if(privateChannel.getUser().getAsTag().equals(tag)) {
                        privateChannel.sendMessage(message.toString()).queue();
                    }
                }
                return;
            }
        }
        sendHelp();
    }

    private void sendHelp() {
        System.out.println("No Help for you!");
    }

}
