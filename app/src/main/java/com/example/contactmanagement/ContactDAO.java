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


    //THESE STILL NEED TO BE DONE, delete and update

    @Delete
    void delete(Contact contact);

    @Update
    void update(Contact contact);


    @Query("SELECT * FROM contacts")
    List<Contact> getAllContacts();

    @Query("DELETE FROM contacts WHERE phoneNo = :phoneNo")
    void deleteByPhoneNo(long phoneNo); // Add this method to delete a contact by phone number

}
