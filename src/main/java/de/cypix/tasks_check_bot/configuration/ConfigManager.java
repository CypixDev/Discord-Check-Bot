package de.cypix.tasks_check_bot.configuration;

public class ConfigManager {

    public ConfigManager(){

        Config.load("config.json");

        System.out.println("Token: "+Config.getInstance().token);
        System.out.println("ChannelName: "+Config.getInstance().channelName);
        System.out.println("Password: "+Config.getInstance().sqlPassword);

        // Speichern der Konfigurationsdatei
        Config.getInstance().toFile("config.json");
        System.out.println("Successfully initialized config....");

    }

    public String getToken(){
        return Config.getInstance().token;
    }

    public String getPassword() {return Config.getInstance().sqlPassword;}

    public String getChannelName(){return Config.getInstance().channelName;}
}
