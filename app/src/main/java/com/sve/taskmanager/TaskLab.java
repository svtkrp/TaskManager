package com.sve.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sve.taskmanager.database.TaskManagerBaseHelper;
import com.sve.taskmanager.database.TaskCursorWrapper;
import com.sve.taskmanager.database.TaskManagerDbSchema.TaskTable;

import java.util.ArrayList;
import java.util.List;

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
        mDatabase = new TaskManagerBaseHelper(mContext).getWritableDatabase();
    }

    public Task createAndAddEmptyTask() {
        Task task = new Task();
        task.setCustomer(CurrentUserPreferences.getStoredUserLogin(mContext));
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

    public Task getTask(Long id) {
        if (id == null) return null;
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.ID + " = ?",
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
        String idString = task.getId().toString();
        mDatabase.delete(TaskTable.NAME,
                TaskTable.Cols.ID + " = ?",
                new String[] {idString}
        );
    }

    public void updateTask(Task task) {
        if (task == null) return;
        String idString = task.getId().toString();
        ContentValues values = getContentValues(task);
        mDatabase.update(TaskTable.NAME, values,
                TaskTable.Cols.ID + " = ?",
                new String[] {idString}
        );
    }

    public void replaceCustomerWithAdmin(User customer) {
        if (customer == null) return;
        String customerLogin = customer.getLogin();
        ContentValues values;
        Task task;

        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.CUSTOMER + " = ?",
                new String[] {customerLogin}
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
                        TaskTable.Cols.ID + " = ?",
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
        String executorLogin = executor.getLogin();
        ContentValues values;
        Task task;

        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.EXECUTOR + " = ?",
                new String[] {executorLogin}
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
                        TaskTable.Cols.ID + " = ?",
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
        String customerLogin = customer.getLogin();
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.CUSTOMER + " = ?",
                new String[] {customerLogin}
        );
        return cursor.getCount();
    }

    public List<Task> getTasksOfCustomer(User customer) {
        //fixme if (customer == null) return
        String customerLogin = customer.getLogin();
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.CUSTOMER + " = ?",
                new String[] {customerLogin}
        );
        return getTaskList(cursor);
    }

    public int getTaskCountOfExecutor(User executor) {
        //fixme if (executor == null) return
        String executorLogin = executor.getLogin();
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.EXECUTOR + " = ?",
                new String[] {executorLogin}
        );
        return cursor.getCount();
    }

    public List<Task> getTasksOfExecutor(User executor) {
        //fixme if (executor == null) return
        String executorLogin = executor.getLogin();
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.EXECUTOR + " = ?",
                new String[] {executorLogin}
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
        values.put(TaskTable.Cols.ID, task.getId());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.SOLVED, task.isSolved() ? 1 : 0);
        values.put(TaskTable.Cols.CUSTOMER, task.getCustomer());
        values.put(TaskTable.Cols.EXECUTOR, task.getExecutor());
        return values;
    }

    private static ContentValues getContentValuesCustomerIsAdmin(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.ID, task.getId());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.SOLVED, task.isSolved() ? 1 : 0);
        values.put(TaskTable.Cols.CUSTOMER, UserLab.ADMIN_LOGIN);
        values.put(TaskTable.Cols.EXECUTOR, task.getExecutor());
        return values;
    }

    private static ContentValues getContentValuesExecutorIsNull(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.ID, task.getId());
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