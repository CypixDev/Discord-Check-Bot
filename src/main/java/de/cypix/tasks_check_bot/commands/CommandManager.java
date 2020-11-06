package de.cypix.tasks_check_bot.commands;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    public ConcurrentHashMap<String, PrivateCommand> commands;

    public CommandManager() {
        this.commands = new ConcurrentHashMap<>();
    }
    public void registerCommand(String cmdName, PrivateCommand privateCommandClass){
        this.commands.put(cmdName, privateCommandClass);
    }
    //returns false if command not exists
    public boolean perform(String command, Member member, MessageChannel messageChannel, Message message, String[] args){
        if(commands.containsKey(command.toLowerCase())){
            this.commands.get(command.toLowerCase()).performCommand(member, messageChannel, message, args);
            return true;
        }
        return false;
    }
}
