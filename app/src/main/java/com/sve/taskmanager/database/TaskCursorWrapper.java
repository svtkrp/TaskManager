package com.sve.taskmanager.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sve.taskmanager.model.Task;
import com.sve.taskmanager.database.TaskManagerDbSchema.TaskTable;

import java.util.Date;

public class TaskCursorWrapper extends CursorWrapper {
    public TaskCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Task getTask() {
        long id = getLong(getColumnIndex(TaskTable.Cols.ID));
        String title = getString(getColumnIndex(TaskTable.Cols.TITLE));
        long date = getLong(getColumnIndex(TaskTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(TaskTable.Cols.SOLVED));
        String customer = getString(getColumnIndex(TaskTable.Cols.CUSTOMER));
        String executor = getString(getColumnIndex(TaskTable.Cols.EXECUTOR));

        Task task = new Task(id);
        task.setTitle(title);
        task.setDate(new Date(date));
        task.setSolved(isSolved != 0);
        task.setCustomer(customer);
        task.setExecutor(executor);

        return task;
    }
}
