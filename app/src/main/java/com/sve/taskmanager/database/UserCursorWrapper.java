package com.sve.taskmanager.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sve.taskmanager.model.User;
import com.sve.taskmanager.database.TaskManagerDbSchema.UserTable;

public class UserCursorWrapper extends CursorWrapper {
    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String login = getString(getColumnIndex(UserTable.Cols.LOGIN));
        String name = getString(getColumnIndex(UserTable.Cols.NAME));

        return new User(login, name);
    }
}
