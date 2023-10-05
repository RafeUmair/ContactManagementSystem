package com.example.contactmanagement;


import android.content.Context;
import androidx.room.Room;

public class ContactDBInstance {
    private static ContactDatabase database;
    public static ContactDatabase getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), ContactDatabase.class, "Contact_database").allowMainThreadQueries().build();
        }
        return database;
    }
}