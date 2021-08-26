package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.reminder.ReminderTask;
import de.cypix.tasks_check_bot.reminder.TimeUnit;
import de.cypix.tasks_check_bot.reminder.TimeUnitException;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;


public class CMDReminder implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        //list
        if(args.length == 2){
            if(args[1].equalsIgnoreCase("list")){
                StringBuilder stringBuilder = new StringBuilder("Hier alle deine Tasks mit Erinnerungen \n");
                try {
                    for (ReminderTask reminderTask : SQLManager.getAllReminderTasks(user.getIdLong())) {
                        stringBuilder.append(reminderTask.getTimeBefore());
                        stringBuilder.append(reminderTask.getTimeUnit().getAliases()[1]);
                        stringBuilder.append("\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                messageChannel.sendMessage(stringBuilder.toString()).queue();
                return;
            }
        }

        if(args.length == 4){
            if(args[1].equalsIgnoreCase("add")){
                int timeBefore = -1;
                try{
                    timeBefore = Integer.parseInt(args[2]);
                    if(timeBefore <= 0){
                        throw new NumberFormatException("Number is less than or equal to 0");
                    }
                }catch (NumberFormatException e){
                    messageChannel.sendMessage("ERROR: "+e.getMessage()).queue();
                    return;
                }
                TimeUnit timeUnit;
                try{
                    timeUnit = TimeUnit.getByAlias(args[3]);
                }catch(TimeUnitException e){
                    messageChannel.sendMessage("ERROR: "+e.getMessage()).queue();
                    return;
                }

                try {
                    if(SQLManager.insertReminderTask(user.getIdLong(), timeBefore, timeUnit)){
                        messageChannel.sendMessage("Erfolgreich hinzugefügt!").queue();
                    }else messageChannel.sendMessage("Dieser Reminder ist bereits hinzugefügt!").queue();

                } catch (SQLException e) {
                    e.printStackTrace();
                    messageChannel.sendMessage("Something went wrong, I'm sorry!").queue();
                }
                return;
            }
            if(args[1].equalsIgnoreCase("remove")){
                int timeBefore = -1;
                try{
                    timeBefore = Integer.parseInt(args[2]);
                    if(timeBefore <= 0){
                        throw new NumberFormatException("Number is less than or equal to 0");
                    }
                }catch (NumberFormatException e){
                    messageChannel.sendMessage("ERROR: "+e.getMessage()).queue();
                    return;
                }
                TimeUnit timeUnit;
                try{
                    timeUnit = TimeUnit.getByAlias(args[3]);
                }catch(TimeUnitException e){
                    messageChannel.sendMessage("ERROR: "+e.getMessage()).queue();
                    return;
                }

                try {
                    SQLManager.removeReminderTask(user.getIdLong(), timeBefore, timeUnit);
                    messageChannel.sendMessage("Erfolgreich entfernt!").queue();
                } catch (SQLException e) {
                    e.printStackTrace();
                    messageChannel.sendMessage("Something went wrong, I'm sorry!").queue();
                }
                return;
            }
        }
        if(args.length == 3){
            if(args[1].equalsIgnoreCase("remove")){
                if(args[2].equalsIgnoreCase("all")){
                    try {
                        SQLManager.removeAllReminderTasks(user.getIdLong());
                        messageChannel.sendMessage("Es wurden alle Erinnerungen für Tasks entfernt!").queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        messageChannel.sendMessage("Something went wrong, I'm sorry!").queue();
                    }
                    return;
                }
            }
        }
    }
}
