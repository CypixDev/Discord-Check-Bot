package de.cypix.tasks_check_bot.configuration;

public class ConfigManager {

    public ConfigManager(){

        Config.load("config.json");

        System.out.println("Token: "+Config.getInstance().token);

        // Speichern der Konfigurationsdatei
        Config.getInstance().toFile("config.json");
        System.out.println("Successfully initialized config....");

    }

    public String getToken(){
        return Config.getInstance().token;
    }

}
