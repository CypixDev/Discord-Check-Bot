package de.cypix.tasks_check_bot.sql;

import de.cypix.tasks_check_bot.main.TasksCheckBot;

import java.sql.*;

public class SQLConnector {
    private static SQLConnector instance;

    private String host, database, user, password;
    private int port;
    private Connection connection;
    private boolean firstConnected;


    public SQLConnector(String host, String database, String user, String password, int port) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
        firstConnected = false;


        SQLConnector.instance = this;
        connect();
        createTable();
    }

    public SQLConnector(boolean fromConfig){
        if(fromConfig){
            this.host = TasksCheckBot.getConfigManager().getSQLHost();
            this.database = TasksCheckBot.getConfigManager().getSQLDatabase();
            this.user = TasksCheckBot.getConfigManager().getSQLUser();
            this.password = TasksCheckBot.getConfigManager().getSQLPassword();
            this.port = TasksCheckBot.getConfigManager().getSQLPort();
            firstConnected = false;

            SQLConnector.instance = this;
            connect();
            createTable();
        }
    }

    private void createTable() {
        if(isConnected()){
            executeUpdate("CREATE TABLE IF NOT EXISTS task(task_id INT PRIMARY KEY AUTO_INCREMENT, subject_id TINYINT, task_description VARCHAR(255), " +
                    "task_link VARCHAR(255), task_deadline DATE);");
            executeUpdate("CREATE TABLE IF NOT EXISTS user(user_id INT PRIMARY KEY AUTO_INCREMENT, discord_id LONG, discord_name VARCHAR(255));");
            executeUpdate("CREATE TABLE IF NOT EXISTS private_channel(user_id INT, private_channel_id LONG);");
            executeUpdate("CREATE TABLE IF NOT EXISTS finish_user(user_id INT, task_id INT);");
            executeUpdate("CREATE TABLE IF NOT EXISTS user_ignore(user_id INT, subject_id TINYINT);");
        }
    }


    public ResultSet getResultSet(String query) {
        if (isConnected()) {
            try {
                //PreparedStatement preparedStatement = connection.prepareStatement(query);
                //ResultSet rs = preparedStatement.getResultSet();
                return connection.createStatement().executeQuery(query);
                //preparedStatement.close();
                //return rs;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            reconnect();
            try {
                //PreparedStatement preparedStatement = connection.prepareStatement(query);
                //ResultSet rs = preparedStatement.getResultSet();
                return connection.createStatement().executeQuery(query);
                //preparedStatement.close();
                //return rs;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isConnected() {
        try {
            if (connection == null || !connection.isValid(10) || connection.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void executeUpdate(String qry) {
        if (isConnected()) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qry);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            reconnect();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qry);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
                            + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin",
                    "" + user, password);
            System.out.println("Successfully connected to Database!");
            firstConnected = true;
        } catch (SQLException e) {
            e.printStackTrace();
            firstConnected = false;
        }
    }

    public void closeConnection() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
            } finally {
                connection = null;
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static SQLConnector getInstance() {
        return instance;
    }

    private void reconnect(){
        if(firstConnected){
            if(!isConnected()){
                connect();
            }
        }
    }

    public boolean checkLogin(String user, String password) {
        ResultSet rs = getResultSet("SELECT * FROM users WHERE username='"+user+"' AND password='"+password+"';");
        try {
            if(rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }
}