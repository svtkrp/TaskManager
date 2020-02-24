package com.sve.taskmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sve.taskmanager.database.TaskManagerBaseHelper;
import com.sve.taskmanager.database.UserCursorWrapper;
import com.sve.taskmanager.database.TaskManagerDbSchema.UserTable;

import java.util.ArrayList;
import java.util.List;

public class UserLab {

    public static final String ADMIN_LOGIN = "admin";
    public static final String ADMIN_NAME = "Admin";
    public static final User ADMIN = new User(ADMIN_LOGIN, ADMIN_NAME);

    private static UserLab sUserLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static UserLab get(Context context) {
        if (sUserLab == null) {
            sUserLab = new UserLab(context);
        }
        return sUserLab;
    }

    private UserLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TaskManagerBaseHelper(mContext).getWritableDatabase();
        checkAndAddAdmin();
    }

    private void checkAndAddAdmin() {
        UserCursorWrapper cursor = queryUsers(
                UserTable.Cols.LOGIN + " = ?",
                new String[] {ADMIN_LOGIN}
        );
        try {
            if (cursor.getCount() == 0) {
                addUser(ADMIN);
            //} else if (cursor.getCount() > 1) {
                //fixme: exception / deleting
            }
        } finally {
            cursor.close();
        }
    }

    public int getUserCount() {
        UserCursorWrapper cursor = queryUsers(null, null);
        return cursor.getCount();
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        UserCursorWrapper cursor = queryUsers(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                users.add(cursor.getUser());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return users;
    }

    public List<User> getUsersWithoutAdmin() {
        List<User> users = new ArrayList<>();
        User user;

        UserCursorWrapper cursor = queryUsers(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                user = cursor.getUser();
                if (!user.getLogin().equals(ADMIN_LOGIN)) {
                    users.add(user);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return users;
    }

    public User getUser(String login) {
        if (login == null) return null;
        UserCursorWrapper cursor = queryUsers(
                UserTable.Cols.LOGIN + " = ?",
                new String[] {login}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getUser();
        } finally {
            cursor.close();
        }
    }

    public void addUser(User user) {
        if (user == null) return;
        ContentValues values = getContentValues(user);
        mDatabase.insert(UserTable.NAME, null, values);
    }

    public void deleteUser(User user, TaskLab taskLab) {
        if (user == null) return;

        taskLab.replaceCustomerWithAdmin(user);
        taskLab.replaceExecutorWithNull(user);

        String login = user.getLogin();
        mDatabase.delete(UserTable.NAME,
                UserTable.Cols.LOGIN + " = ?",
                new String[] {login}
        );
    }

    public void updateUser(User user) {
        if (user == null) return;
        String login = user.getLogin();
        ContentValues values = getContentValues(user);
        mDatabase.update(UserTable.NAME, values,
                UserTable.Cols.LOGIN + " = ?",
                new String[] {login}
        );
    }

    private UserCursorWrapper queryUsers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                UserTable.NAME,
                null, // Все столбцы
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new UserCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.Cols.LOGIN, user.getLogin());
        values.put(UserTable.Cols.NAME, user.getName());
        return values;
    }
}
