package com.sve.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sve.taskmanager.database.TaskBaseHelper;
import com.sve.taskmanager.database.TaskCursorWrapper;
import com.sve.taskmanager.database.TaskDbSchema.TaskTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskLab {
    private static TaskLab sTaskLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static TaskLab get(Context context) {
        if (sTaskLab == null) {
            sTaskLab = new TaskLab(context);
        }
        return sTaskLab;
    }

    private TaskLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TaskBaseHelper(mContext).getWritableDatabase();
    }

    public int getTaskCount() {
        TaskCursorWrapper cursor = queryTasks(null, null);
        return cursor.getCount();
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        TaskCursorWrapper cursor = queryTasks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tasks.add(cursor.getTask());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return tasks;
    }

    public Task getTask(UUID id) {
        if (id == null) return null;
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getTask();
        } finally {
            cursor.close();
        }
    }

    public void addTask(Task task) {
        if (task == null) return;
        ContentValues values = getContentValues(task);
        mDatabase.insert(TaskTable.NAME, null, values);
    }

    public void deleteTask(Task task) {
        if (task == null) return;
        String uuidString = task.getId().toString();
        mDatabase.delete(TaskTable.NAME,
                TaskTable.Cols.UUID + " = ?",
                new String[] {uuidString}
        );
    }

    public void updateTask(Task task) {
        if (task == null) return;
        String uuidString = task.getId().toString();
        ContentValues values = getContentValues(task);
        mDatabase.update(TaskTable.NAME, values,
                TaskTable.Cols.UUID + " = ?",
                new String[] {uuidString}
        );
    }

    public void replaceUserWithNull(User user) {
        if (user == null) return;
        String userId = user.getId().toString();
        ContentValues values;
        Task task;

        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.USER + " = ?",
                new String[] {userId}
        );

        try {
            if (cursor.getCount() == 0) {
                return;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                task = cursor.getTask();
                values = getContentValuesUserIsNull(task);
                mDatabase.update(TaskTable.NAME, values,
                        TaskTable.Cols.UUID + " = ?",
                        new String[] {task.getId().toString()}
                );
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    public int getTaskCountOfUser(User user) {
        if (user == null) return getTaskCount();
        String userId = user.getId().toString();

        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.USER + " = ?",
                new String[] {userId}
        );

        return cursor.getCount();
    }

    public List<Task> getTasksOfUser(User user) {
        if (user == null) return getTasks();
        String userId = user.getId().toString();
        List<Task> tasks = new ArrayList<>();

        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.USER + " = ?",
                new String[] {userId}
        );

        try {
            if (cursor.getCount() == 0) {
                return tasks;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tasks.add(cursor.getTask());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return tasks;
    }

    private TaskCursorWrapper queryTasks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TaskTable.NAME,
                null, // Все столбцы
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new TaskCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.UUID, task.getId().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.SOLVED, task.isSolved() ? 1 : 0);
        values.put(TaskTable.Cols.USER, task.getUser());
        return values;
    }

    private static ContentValues getContentValuesUserIsNull(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.UUID, task.getId().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.SOLVED, task.isSolved() ? 1 : 0);
        values.putNull(TaskTable.Cols.USER);
        return values;
    }
}