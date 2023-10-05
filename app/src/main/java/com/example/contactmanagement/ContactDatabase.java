package com.example.contactmanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import java.io.ByteArrayOutputStream;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactDatabase extends RoomDatabase {
    public abstract ContactDAO contactDao();

    public static void insertSampleData(final ContactDatabase database, Context context) {
        final ContactDAO contactDAO = database.contactDao();
        database.getOpenHelper().getWritableDatabase().beginTransaction();
        try {
            Contact contact1 = new Contact(1234567890, "john", "john@example.com", imageToByteArray(context, R.drawable.bear));
            Contact contact2 = new Contact(4234, "jane", "jane@example.com", imageToByteArray(context, R.drawable.cat));

            contactDAO.insert(contact1);
            contactDAO.insert(contact2);

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private static byte[] imageToByteArray(Context context, int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}