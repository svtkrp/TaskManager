package com.sve.taskmanager.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sve.taskmanager.User;
import com.sve.taskmanager.database.UserDbSchema.UserTable;

import java.util.UUID;

public class UserCursorWrapper extends CursorWrapper {
    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String uuidString = getString(getColumnIndex(UserTable.Cols.UUID));
        String name = getString(getColumnIndex(UserTable.Cols.NAME));

        User user = new User(UUID.fromString(uuidString));
        user.setName(name);

        return user;
    }
}
