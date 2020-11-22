package de.cypix.tasks_check_bot.reminder;

public enum TimeUnit {

    SECOND(0, "s", "sec", "secs", "second", "seconds"),
    MINUTE(1, "m", "min", "minute", "minutes"),
    HOUR(2, "h", "hour", "hours"),
    DAY(3, "d", "day", "days");

    private final int id;
    private final String[] aliases;

    TimeUnit(int id, String... aliases) {
        this.id = id;
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public int getId() {
        return id;
    }

    public static TimeUnit getById(int id){
        for (TimeUnit value : TimeUnit.values()) {
            if(value.getId() == id) return value;
        }
        throw new TimeUnitException("Time Unit for id "+id+" not found!");
    }

    public static TimeUnit getByAlias(String alias){
        for (TimeUnit value : TimeUnit.values()) {
            for (String valueAlias : value.getAliases()) {
                if(valueAlias.equalsIgnoreCase(alias)) return value;
            }
        }
        throw new TimeUnitException("Time Unit for alias "+alias+" not found!");
    }
}
