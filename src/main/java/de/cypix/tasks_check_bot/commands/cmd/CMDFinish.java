package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDFinish implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        if(args.length == 2){
            try{
                int taskId = Integer.parseInt(args[1]);
                if(SQLManager.taskExists(taskId)){
                    if(SQLManager.markAsFinish(SQLManager.getUserId(user.getIdLong()), taskId)){
                        messageChannel.sendMessage("Erfolgreich als Fertig markiert!").queue();
                    }else messageChannel.sendMessage("Bereits als Fertig markiert!").queue();
                }else messageChannel.sendMessage("Dieser Task existiert nicht!").queue();
            }catch(NumberFormatException e){
                messageChannel.sendMessage("Bitte gib eine ZAHL ein die GRÖßER als 0 ist!").queue();
            }
        }
    }
}
