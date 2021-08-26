package de.cypix.tasks_check_bot.sql;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.manager.SchoolTask;
import de.cypix.tasks_check_bot.reminder.ReminderTask;
import de.cypix.tasks_check_bot.reminder.TimeUnit;
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
            TasksCheckBot.getSqlConnector().executeUpdatee("INSERT INTO user(discord_id, discord_name) VALUES ("+user.getId()+", '"+user.getName()+"')");
        }
    }

    public static void insertPrivateChannelId(long userId, long privateChannelId){
        if(getPrivateChannelId(userId) == -1){
            TasksCheckBot.getSqlConnector().executeUpdatee("INSERT INTO private_channel() VALUES ((SELECT user_id FROM user WHERE discord_id="+userId+"), "+privateChannelId+")");
        }
    }

    public static boolean isConnected() {
        return TasksCheckBot.getSqlConnector() != null && TasksCheckBot.getSqlConnector().isConnected();
    }

    public static void insertNewTask(SchoolSubject schoolSubject, String till, String description){
        TasksCheckBot.getSqlConnector().executeUpdatee("INSERT INTO task(subject_id, task_description, task_deadline) VALUES ("+schoolSubject.getId()+", '"+description+"', '"+till+"');");
    }
    public static void insertNewPrivateTask(SchoolSubject schoolSubject, String till, String description, int userId){
        try {
            TasksCheckBot.getSqlConnector().executeUpdate("INSERT INTO task(subject_id, task_description, task_deadline, userid) VALUES ("+schoolSubject.getId()+", '"+description+"', '"+till+"', '"+userId+"');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                        rs.getTimestamp("task_deadline").toLocalDateTime(),
                        rs.getInt("userid")));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    public static List<Integer> getAllTasksForList(){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT task_id FROM task" +
                " WHERE task_deadline > DATE_SUB(NOW(),INTERVAL 2 DAY) AND userid=-1");
        List<Integer> list = new ArrayList<>();
        try{
            while(rs.next()){
                list.add(rs.getInt("task_id"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    public static List<SchoolTask> getAllTasksAsTask(){
        ResultSet rs = TasksCheckBot.
                getSqlConnector().
                getResultSet("SELECT * FROM task" +
                " WHERE task_deadline > DATE_SUB(NOW(),INTERVAL 2 DAY)");
        List<SchoolTask> list = new ArrayList<>();
        try{
            while(rs.next()){
                list.add(new SchoolTask(rs.getInt("task_id"),
                        SchoolSubject.getById(rs.getInt("subject_id")),
                        rs.getString("task_description"),
                        rs.getString("task_link"),
                        rs.getTimestamp("task_deadline").toLocalDateTime(),
                        rs.getInt("userid")));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public static SchoolTask getTaskInfo(int taskId){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * FROM task WHERE task_id="+taskId+
                " AND task_deadline > DATE_SUB(NOW(),INTERVAL 2 DAY)");
        try{
            if(rs.next()){
                return new SchoolTask(rs.getInt("task_id"),
                        SchoolSubject.getById(rs.getInt("subject_id")),
                        rs.getString("task_description"),
                        rs.getString("task_link"),
                        rs.getTimestamp("task_deadline").toLocalDateTime(),
                        rs.getInt("userid"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isIgnoringSubject(long userId, SchoolSubject schoolSubject){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * FROM user_ignore WHERE user_id="+userId+";");
        try{
            while(rs.next()){
                if(rs.getInt("subject_id") == schoolSubject.getId()) return true;
            }
        }catch(SQLException ignored){

        }
        return false;
    }
    public static boolean isIgnoringSubject(User user, SchoolSubject schoolSubject){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT subject_id FROM user_ignore " +
                "INNER JOIN user ON user.user_id=user_ignore.user_id WHERE discord_id="+user.getIdLong()+";");
        try{
            while(rs.next()){
                if(rs.getInt("subject_id") == schoolSubject.getId()) return true;
            }
        }catch(SQLException ignored){ }
        return false;
    }
    public static boolean isIgnoringSubjectByDiscordId(long discordId, SchoolSubject schoolSubject){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT subject_id FROM user "+
                "INNER JOIN user_ignore ON user.user_id=user_ignore.user_id WHERE discord_id="+discordId+";");
        try{
            while(rs.next()){
                if(rs.getInt("subject_id") == schoolSubject.getId()) return true;
            }
        }catch(SQLException ignored){ }
        return false;
    }

    public static boolean insertIgnoringSubject(User user, SchoolSubject schoolSubject){
        if(!isIgnoringSubject(user, schoolSubject)){
            TasksCheckBot.getSqlConnector().executeUpdatee("INSERT INTO user_ignore() VALUES " +
                    "((SELECT user_id FROM user WHERE discord_id="+user.getIdLong()+"), "+schoolSubject.getId()+")");
            return true;
        }
        return false;
    }

    public static boolean deleteFromIgnoreSubject(User user, SchoolSubject schoolSubject){
        if(isIgnoringSubject(user, schoolSubject)){
            TasksCheckBot.getSqlConnector().executeUpdatee("DELETE FROM user_ignore WHERE user_id=" +
                    "(SELECT user_id from user WHERE discord_id="+user.getIdLong()+")" +
                    " AND subject_id="+schoolSubject.getId());
            return true;
        }
        return false;
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
            TasksCheckBot.getSqlConnector().executeUpdatee("UPDATE task WHERE task_id="+taskId+" SET task_description='"+description+"';");
        }
    }
    public static void updateDeadLine(int taskId, String deadLine){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdatee("UPDATE task WHERE task_id="+taskId+" SET task_deadline='"+deadLine+"';");
        }
    }
    public static void updateLink(int taskId, String link){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdatee("UPDATE task WHERE task_id="+taskId+" SET task_link='"+link+"';");
        }
    }
    public static void updateSubject(int taskId, SchoolSubject schoolSubject){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdatee("UPDATE task WHERE task_id="+taskId+" SET subject_id="+schoolSubject.getId()+";");
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
            TasksCheckBot.getSqlConnector().executeUpdatee("INSERT INTO finish_user() VALUES ("+userId+","+taskId+")");
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
            TasksCheckBot.getSqlConnector().executeUpdatee("UPDATE task WHERE task_id="+taskId+" SET subject_id="+subjectId+";");
        }
    }

    public static void delTask(int taskId){
        TasksCheckBot.getSqlConnector().executeUpdatee("DELETE FROM task WHERE task_id="+taskId+";");
    }

    public static void delTasksFromSubject(SchoolSubject schoolSubject){
        TasksCheckBot.getSqlConnector().executeUpdatee("DELETE FROM task WHERE subject_id="+schoolSubject.getId()+";");
    }

    public static void archiveTask(int taskId){

    }

    public static void markAsNotFinish(int userId, int taskId) {
        try {
            TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM finish_user WHERE user_id="+userId+" AND task_id="+taskId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<ReminderTask> getAllReminderTasks() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user ON user.user_id=user_reminder.user_id");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }
    public static List<ReminderTask> getAllReminderTasksPerSecond() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user.user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user on user.user_id=user_reminder.user_id" +
                " WHERE time_unit=0");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }
    public static List<ReminderTask> getAllReminderTasksPerMinute() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user.user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user on user.user_id=user_reminder.user_id" +
                " WHERE time_unit=1");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }
    public static List<ReminderTask> getAllReminderTasksPerHour() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user.user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user on user.user_id=user_reminder.user_id" +
                " WHERE time_unit=2");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }
    public static List<ReminderTask> getAllReminderTasksPerDay() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user.user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user on user.user_id=user_reminder.user_id" +
                " WHERE time_unit=3");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }

    public static List<ReminderTask> getAllReminderTasks(long discordId) throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user_id, time_before, time_unit from user_reminder " +
                        "WHERE user_id = "+
                        "(SELECT user_id FROM user WHERE discord_id="+discordId+")");


        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }

    public static boolean insertReminderTask(int userId, int timeBefore, TimeUnit timeUnit) throws SQLException{
        if(!reminderTaskExists(userId, timeBefore, timeUnit)){
            TasksCheckBot.getSqlConnector().executeUpdate("INSERT INTO user_reminder() VALUES ("+userId+", "+timeBefore+", "+timeUnit.getId()+")");
            return true;
        }return false;
    }
    public static boolean insertReminderTask(long discordId, int timeBefore, TimeUnit timeUnit) throws SQLException{
        if(!reminderTaskExists(discordId, timeBefore, timeUnit)){
            TasksCheckBot.getSqlConnector().executeUpdate("INSERT INTO user_reminder() VALUES ("+"(SELECT user_id FROM user WHERE discord_id="+discordId+"),"+
                    " "+timeBefore+", "+timeUnit.getId()+")");
            return true;
        }return false;
    }
    public static boolean reminderTaskExists(int userId, int timeBefore, TimeUnit timeUnit) throws SQLException{
        return TasksCheckBot.getSqlConnector().getResultSet("SELECT user_id from user_reminder WHERE user_id="+userId+
                " AND time_before="+timeBefore+
                " AND time_unit="+timeUnit.getId()).next();
    }
    public static boolean reminderTaskExists(long discordId, int timeBefore, TimeUnit timeUnit) throws SQLException{
        return TasksCheckBot.getSqlConnector().getResultSet("SELECT user_id from user_reminder" +
                " WHERE user_id="+"(SELECT user_id FROM user WHERE discord_id="+discordId+")"+
                " AND time_before="+timeBefore+
                " AND time_unit="+timeUnit.getId()).next();
    }

    public static void removeReminderTask(int userId, int timeBefore, TimeUnit timeUnit) throws SQLException{
        TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM user_reminder WHERE user_id="+userId+
                " AND time_before="+timeBefore+
                " AND time_unit="+timeUnit.getId());
    }
    public static void removeReminderTask(long discordId, int timeBefore, TimeUnit timeUnit) throws SQLException{
        TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM user_reminder WHERE user_id="+"(SELECT user_id FROM user WHERE discord_id="+discordId+")"+
                " AND time_before="+timeBefore+
                " AND time_unit="+timeUnit.getId());
    }

    public static void removeAllReminderTasks(int userId) throws SQLException{
        TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM user_reminder WHERE user_id="+userId);
    }
    public static void removeAllReminderTasks(long discordId) throws SQLException{
        TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM user_reminder WHERE user_id="+"(SELECT user_id FROM user WHERE discord_id="+discordId+")");
    }
}
