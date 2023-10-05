package com.example.contactmanagement;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactDatabase extends RoomDatabase {
    public abstract ContactDAO contactDao();

    public static void insertSampleData(ContactDatabase database) {
        ContactDAO contactDAO = database.contactDao();

        //need to find alternative to using Long as phoneNo
        Contact contact1 = new Contact(1234567890, "john", "john@example.com", R.drawable.bear);
        Contact contact2 = new Contact(4234, "jane", "jane@example.com", R.drawable.cat);

        contactDAO.insert(contact1);
        contactDAO.insert(contact2);
    }
}
