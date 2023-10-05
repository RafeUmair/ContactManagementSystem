package com.example.contactmanagement;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDAO {
    @Insert
    void insert(Contact contact);
    @Delete
    void delete(Contact contact);
    @Update
    void update(Contact contact);
    @Query("SELECT * FROM contacts")
    List<Contact> getAllContacts();
    @Query("DELETE FROM contacts WHERE phoneNo = :phoneNo")
    void deleteByPhoneNo(long phoneNo);
}
