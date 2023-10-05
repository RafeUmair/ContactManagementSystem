package com.example.contactmanagement;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private long phoneNo;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "photoResourceId")
    private int photoResourceId;

    public Contact(long phoneNo, String name, String email, int photoResourceId) {
        this.phoneNo = phoneNo;
        this.name = name;
        this.email = email;
        this.photoResourceId = photoResourceId;
    }

    public long getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(long phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhotoResourceId() {
        return photoResourceId;
    }

    public void setPhotoResourceId(int photoResourceId) {
        this.photoResourceId = photoResourceId;
    }
}
