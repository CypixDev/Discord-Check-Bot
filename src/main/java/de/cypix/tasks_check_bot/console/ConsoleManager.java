package de.cypix.tasks_check_bot.console;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.manager.TasksManager;
import de.cypix.tasks_check_bot.sql.SQLConnector;

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
                System.out.println("Bot is "+(TasksCheckBot.getJda().isUnavailable(400) ? "Offline" : "Online"));
                return;
            }
        }
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("start")){
                if(args[1].equalsIgnoreCase("bot")){
                    TasksCheckBot.getInstance().startBot(TasksCheckBot.getConfigManager().getToken());
                    System.out.println("Started Bot...");
                    return;
                }
                if(args[1].equalsIgnoreCase("sql")){
                    TasksCheckBot.setSqlConnector(new SQLConnector("localhost", "discrod_tasks",
                            "root", "", 3306));
                    System.out.println("Stated SQL....");
                    return;
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
                    System.out.println("Stated SQL....");
                    return;
                }
            }
        }
        sendHelp();
    }

    private void sendHelp() {
        System.out.println("Her is gelp....");
        System.out.println("You can use status,start,stop and restart");
    }

}
