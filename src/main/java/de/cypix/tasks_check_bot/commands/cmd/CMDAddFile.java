package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CMDAddFile implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        if(args.length == 2){
            int taskId = -1;
            try{
                taskId = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                messageChannel.sendMessage("Bitte gib als TaskId eine Zahl an!").queue();
            }
            if(SQLManager.taskExists(taskId)){
                if(!message.getAttachments().isEmpty()){
                    Message.Attachment attachment = message.getAttachments().get(0);
                    try{
                        downloadAndSaveFile(attachment.getUrl(), attachment.getFileName(), SQLManager.getSchoolSubjectByTaskId(taskId), taskId);
                        messageChannel.sendMessage("Erfolgreich hochgeladen!").queue();
                    }catch(IOException e){
                        messageChannel.sendMessage("ERROR WHILE SAVING....please contact Admin...").queue();
                    }
                }else messageChannel.sendMessage("Bitte hänge auch einen Task mit an HEEHHE").queue();

            }else messageChannel.sendMessage("Dieser Task existiert nicht! (oder ist schon im Archiv!)").queue();
        }

        //TODO: send use
    }

    private void downloadAndSaveFile(String url, String fileName, SchoolSubject subject, int taskId) throws IOException {
        File file = new File("files/"+subject+"/"+taskId+"/"+fileName);
        file.mkdirs();
        InputStream in = new URL(url).openStream();
        Files.copy(in, Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
    }
}
