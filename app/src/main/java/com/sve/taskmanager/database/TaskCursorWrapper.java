package com.sve.taskmanager.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sve.taskmanager.Task;
import com.sve.taskmanager.database.TaskDbSchema.TaskTable;

import java.util.Date;
import java.util.UUID;

public class TaskCursorWrapper extends CursorWrapper {
    public TaskCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Task getTask() {
        String uuidString = getString(getColumnIndex(TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TaskTable.Cols.TITLE));
        long date = getLong(getColumnIndex(TaskTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(TaskTable.Cols.SOLVED));
        String user = getString(getColumnIndex(TaskTable.Cols.USER));

        Task task = new Task(UUID.fromString(uuidString));
        task.setTitle(title);
        task.setDate(new Date(date));
        task.setSolved(isSolved != 0);
        task.setUser(user);

        return task;
    }
}
