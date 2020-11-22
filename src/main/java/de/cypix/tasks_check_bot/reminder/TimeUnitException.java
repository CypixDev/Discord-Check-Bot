package de.cypix.tasks_check_bot.reminder;

public class TimeUnitException extends IllegalArgumentException{

    public TimeUnitException (String s) {
        super (s);
    }

    static TimeUnitException forInputString(String s, int radix) {
        return new TimeUnitException("For input timeunit: \"" + s + "\"");
    }
}
