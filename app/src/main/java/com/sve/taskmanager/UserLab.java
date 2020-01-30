package com.sve.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sve.taskmanager.database.UserBaseHelper;
import com.sve.taskmanager.database.UserCursorWrapper;
import com.sve.taskmanager.database.UserDbSchema.UserTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserLab {

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
        mDatabase = new UserBaseHelper(mContext).getWritableDatabase();
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

    public User getUser(UUID id) {
        if (id == null) return null;
        UserCursorWrapper cursor = queryUsers(
                UserTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
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

    public void deleteUser(User user) {
        if (user == null) return;
        String uuidString = user.getId().toString();
        mDatabase.delete(UserTable.NAME,
                UserTable.Cols.UUID + " = ?",
                new String[] {uuidString}
        );
    }

    public void updateUser(User user) {
        if (user == null) return;
        String uuidString = user.getId().toString();
        ContentValues values = getContentValues(user);
        mDatabase.update(UserTable.NAME, values,
                UserTable.Cols.UUID + " = ?",
                new String[] {uuidString}
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
        values.put(UserTable.Cols.UUID, user.getId().toString());
        values.put(UserTable.Cols.NAME, user.getName());
        return values;
    }
}
