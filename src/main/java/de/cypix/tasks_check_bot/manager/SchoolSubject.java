package de.cypix.tasks_check_bot.manager;

public enum SchoolSubject {

    FR("Französisch", 0, ":flag_fr:"), MA_KREU("Mathe-Kreu", 1),
    MA_ROSA("Mathe-Rosa", 2), EN("Englisch", 3, ":flag_gb:"),
    DE("Deutsch", 4, ":pencil:"), SP("Sport", 5, ":medal:"), TI("Technische Informatik", 6),
    IK("Informatik", 7, ":computer:"), EKT("Elektrotechnik", 8, ":electric_plug:"), PH("Physik", 9, ":test_tube:"),
    GG("Gesselschaft mit Geschichte", 10, ":book:"), WBL("Wirtschaft und Betriebs Lehre", 11, ":bar_chart:"),
    RL_Kath("Religion-Katholisch", 12, ":pray:"), RL_EV("Religion-Evangelisch", 13, ":pray:"), GENERAL("Allgemein", 14);

    private String subjectName;
    private int id;
    private String emoji;

    SchoolSubject(String subjectName, int id) {
        this.subjectName = subjectName;
        this.id = id;
    }

    SchoolSubject(String subjectName, int id, String emoji) {
        this.subjectName = subjectName;
        this.id = id;
        this.emoji = emoji;
    }

    public static SchoolSubject getById(int id){
        for (SchoolSubject value : SchoolSubject.values()) {
            if(value.getId() == id) return value;
        }
        return SchoolSubject.GENERAL;
    }

    public static int getHighestSubjectId(){
        int current = 0;
        for (SchoolSubject value : SchoolSubject.values()) {
            if(value.getId() > current) current = value.getId();
        }
        return current;
    }

    public static boolean exists(int schoolSubjectId){
        for (SchoolSubject value : SchoolSubject.values()) {
            if(value.getId() == schoolSubjectId) return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
