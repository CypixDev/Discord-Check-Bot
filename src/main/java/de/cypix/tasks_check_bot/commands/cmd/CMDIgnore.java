package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDIgnore implements PrivateCommand {

    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        if(args.length == 2){
            try{
                int subjectId = Integer.parseInt(args[1]);
                if(SchoolSubject.exists(subjectId)){
                    SchoolSubject schoolSubject = SchoolSubject.getById(subjectId);
                    if(!SQLManager.isIgnoringSubject(user, schoolSubject)){
                        SQLManager.insertIgnoringSubject(user, schoolSubject);
                        messageChannel.sendMessage("Bei deinen Todo's steht nun nichts mehr von "+schoolSubject.getSubjectName()+" "+schoolSubject.getEmoji()).queue();
                    }else{
                        SQLManager.deleteFromIgnoreSubject(user, schoolSubject);
                        messageChannel.sendMessage("Bei deinen Todo's kommen nun auch wieder Task's von "+schoolSubject.getSubjectName()+" "+schoolSubject.getEmoji()).queue();
                    }
                }else messageChannel.sendMessage("Dieser Task existiert nicht!").queue();
            }catch(NumberFormatException e){
                messageChannel.sendMessage("Bitte verwende eine Zahl!!").queue();
            }
        }
    }
}
