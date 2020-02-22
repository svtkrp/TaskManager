package com.sve.taskmanager.database;

public class TaskManagerDbSchema {

    public static final class TaskTable {
        public static final String NAME = "tasks";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String CUSTOMER = "customer";
            public static final String EXECUTOR = "executor";
        }
    }

    public static final class UserTable {
        public static final String NAME = "users";

        public static final class Cols {
            public static final String LOGIN = "login";
            public static final String NAME = "name";
        }
    }
}
