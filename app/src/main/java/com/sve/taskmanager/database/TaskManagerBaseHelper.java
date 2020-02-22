package com.sve.taskmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sve.taskmanager.database.TaskManagerDbSchema.TaskTable;
import com.sve.taskmanager.database.TaskManagerDbSchema.UserTable;

public class TaskManagerBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "TaskManager.db";

    public TaskManagerBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TaskTable.NAME + "( " +
                TaskTable.Cols.ID + " integer primary key, " + // "integer" in sqlite = long
                TaskTable.Cols.TITLE + ", " +
                TaskTable.Cols.DATE + ", " +
                TaskTable.Cols.SOLVED + ", " +
                TaskTable.Cols.CUSTOMER + ", " +
                TaskTable.Cols.EXECUTOR +
                ")"
        );

        db.execSQL("create table " + UserTable.NAME + "( " +
                UserTable.Cols.LOGIN + " text primary key, " +
                UserTable.Cols.NAME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
