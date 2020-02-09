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

    public Task createAndAddEmptyTask() {
        Task task = new Task();
        task.setCustomer(UserLab.get(mContext)
                .getUser(CurrentUserPreferences.getStoredUsername(mContext)).getId().toString());
        addTask(task);
        return task;
    }

    public int getTaskCount() {
        TaskCursorWrapper cursor = queryTasks(null, null);
        return cursor.getCount();
    }

    public List<Task> getTasks() {
        TaskCursorWrapper cursor = queryTasks(null, null);
        return getTaskList(cursor);
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

    public void replaceCustomerWithAdmin(User customer) {
        if (customer == null) return;
        String customerId = customer.getId().toString();
        ContentValues values;
        Task task;

        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.CUSTOMER + " = ?",
                new String[] {customerId}
        );

        try {
            if (cursor.getCount() == 0) {
                return;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                task = cursor.getTask();
                values = getContentValuesCustomerIsAdmin(task);
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

    public void replaceExecutorWithNull(User executor) {
        if (executor == null) return;
        String executorId = executor.getId().toString();
        ContentValues values;
        Task task;

        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.EXECUTOR + " = ?",
                new String[] {executorId}
        );

        try {
            if (cursor.getCount() == 0) {
                return;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                task = cursor.getTask();
                values = getContentValuesExecutorIsNull(task);
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

    public int getTaskCountOfCustomer(User customer) {
        //fixme if (customer == null) return
        String customerId = customer.getId().toString();
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.CUSTOMER + " = ?",
                new String[] {customerId}
        );
        return cursor.getCount();
    }

    public List<Task> getTasksOfCustomer(User customer) {
        //fixme if (customer == null) return
        String customerId = customer.getId().toString();
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.CUSTOMER + " = ?",
                new String[] {customerId}
        );
        return getTaskList(cursor);
    }

    public int getTaskCountOfExecutor(User executor) {
        //fixme if (executor == null) return
        String executorId = executor.getId().toString();
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.EXECUTOR + " = ?",
                new String[] {executorId}
        );
        return cursor.getCount();
    }

    public List<Task> getTasksOfExecutor(User executor) {
        //fixme if (executor == null) return
        String executorId = executor.getId().toString();
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.EXECUTOR + " = ?",
                new String[] {executorId}
        );
        return getTaskList(cursor);
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
        values.put(TaskTable.Cols.CUSTOMER, task.getCustomer());
        values.put(TaskTable.Cols.EXECUTOR, task.getExecutor());
        return values;
    }

    private static ContentValues getContentValuesCustomerIsAdmin(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.UUID, task.getId().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.SOLVED, task.isSolved() ? 1 : 0);
        values.put(TaskTable.Cols.CUSTOMER, UserLab.ADMIN_ID.toString());
        values.put(TaskTable.Cols.EXECUTOR, task.getExecutor());
        return values;
    }

    private static ContentValues getContentValuesExecutorIsNull(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.UUID, task.getId().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.SOLVED, task.isSolved() ? 1 : 0);
        values.put(TaskTable.Cols.CUSTOMER, task.getCustomer());
        values.putNull(TaskTable.Cols.EXECUTOR);
        return values;
    }

    private static List<Task> getTaskList(TaskCursorWrapper cursor) {
        List<Task> tasks = new ArrayList<>();

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
}