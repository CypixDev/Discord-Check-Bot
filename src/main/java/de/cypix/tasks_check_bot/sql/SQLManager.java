package de.cypix.tasks_check_bot.sql;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.manager.SchoolTask;
import de.cypix.tasks_check_bot.manager.TasksManager;
import net.dv8tion.jda.api.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    public static long getPrivateChannelId(long discordId){
        if(isConnected()){
            ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * from private_channel WHERE (SELECT user_id from user where discord_id="+discordId+");");

            try {
                if(rs.next()){
                    return rs.getLong("private_channel_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    public static SchoolSubject getSchoolSubjectByTaskId(int taskId){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * FROM task WHERE task_id="+taskId);
        try {
            if(rs.next()){
                return SchoolSubject.getById(rs.getInt("subject_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static int getLocalIdByDiscordId(long userId){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * from user WHERE discord_id="+userId+";");
        try {
            if(rs.next()) return rs.getInt("user_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void insertUser(User user){
        if(getLocalIdByDiscordId(Long.parseLong(user.getId())) == -1){
            TasksCheckBot.getSqlConnector().executeUpdate("INSERT INTO user(discord_id, discord_name) VALUES ("+user.getId()+", '"+user.getName()+"')");
        }
    }

    public static void insertPrivateChannelId(long userId, long privateChannelId){
        if(getPrivateChannelId(userId) == -1){
            TasksCheckBot.getSqlConnector().executeUpdate("INSERT INTO private_channel() VALUES ((SELECT user_id FROM user WHERE discord_id="+userId+"), "+privateChannelId+")");
        }
    }

    public static boolean isConnected() {
        return TasksCheckBot.getSqlConnector() != null && TasksCheckBot.getSqlConnector().isConnected();
    }

    public static void insertNewTask(SchoolSubject schoolSubject, String till, String description){
        TasksCheckBot.getSqlConnector().executeUpdate("INSERT INTO task(subject_id, task_description, task_deadline) VALUES ("+schoolSubject.getId()+", '"+description+"', '"+till+"');");
    }
    public static List<SchoolTask> getAllTasks(SchoolSubject schoolSubject){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * FROM task WHERE subject_id="+schoolSubject.getId()+"" +
                " AND task_deadline > DATE_SUB(NOW(),INTERVAL 2 DAY)");
        List<SchoolTask> list = new ArrayList<>();
        try{
            while(rs.next()){
                list.add(new SchoolTask(rs.getInt("task_id"),
                        schoolSubject,
                        rs.getString("task_description"),
                        rs.getString("task_link"),
                        rs.getTimestamp("task_deadline").toLocalDateTime()));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public static boolean taskExists(int taskId){
        try {
            return TasksCheckBot.getSqlConnector().getResultSet("SELECT * from task WHERE task_id="+taskId).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void updateDescription(int taskId, String description){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdate("UPDATE task WHERE task_id="+taskId+" SET task_description='"+description+"';");
        }
    }
    public static void updateDeadLine(int taskId, String deadLine){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdate("UPDATE task WHERE task_id="+taskId+" SET task_deadline='"+deadLine+"';");
        }
    }
    public static void updateLink(int taskId, String link){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdate("UPDATE task WHERE task_id="+taskId+" SET task_link='"+link+"';");
        }
    }
    public static void updateSubject(int taskId, SchoolSubject schoolSubject){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdate("UPDATE task WHERE task_id="+taskId+" SET subject_id="+schoolSubject.getId()+";");
        }
    }
    public static int getUserId(long discordId){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user_id FROM user WHERE discord_id="+discordId+";");
        try {
            if(rs.next()){
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static boolean markAsFinish(int userId, int taskId){
        if(!isTaskFinished(userId, taskId)){
            TasksCheckBot.getSqlConnector().executeUpdate("INSERT INTO finish_user() VALUES ("+userId+","+taskId+")");
            return true;
        }else return false;
    }
    public static List<String> getFinishedTasksDiscordNames(int taskId){
        List<String> names = new ArrayList<>();
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet(
                "SELECT discord_name FROM finish_user" +
                " INNER JOIN user ON user.user_id = finish_user.user_id" +
                        " WHERE task_Id="+taskId+";");
        try{
            while(rs.next()){
                names.add(rs.getString("discord_name"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return names;
    }
    public static boolean isTaskFinished(int userId, int taskId){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * FROM finish_user WHERE user_id="+userId+" AND task_id="+taskId+";");
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void updateSubject(int taskId, int subjectId){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdate("UPDATE task WHERE task_id="+taskId+" SET subject_id="+subjectId+";");
        }
    }

    public static void delTask(int taskId){
        TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM task WHERE task_id="+taskId+";");
    }

    public static void delTasksFromSubject(SchoolSubject schoolSubject){
        TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM task WHERE subject_id="+schoolSubject.getId()+";");
    }

    public static void archiveTask(int taskId){

    }

    public static void markAsNotFinish(int userId, int taskId) {
        TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM finish_user WHERE user_id="+userId+" AND task_id="+taskId);
    }
}
