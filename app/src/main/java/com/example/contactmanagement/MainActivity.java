package com.example.contactmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private ContactListFragment ContactList = new ContactListFragment();
    private AddContactsFragment AddContacts = new AddContactsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityData mainActivityDataViewModel = new ViewModelProvider(this).get(MainActivityData.class);
        setupFragmentSwapper(mainActivityDataViewModel);

        loadInitialContactList();
    }

    private void loadInitialContactList()
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.ContactList_Container);

        View ContactListContainer = findViewById(R.id.ContactList_Container);
        if (frag == null) {
            fm.beginTransaction().add(R.id.ContactList_Container, ContactList).commit();
        } else {
            fm.beginTransaction().replace(R.id.ContactList_Container, ContactList).commit();
        }
    }

    private void loadAddContactsScreen()
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.ContactList_Container);

        View ContactListContainer = findViewById(R.id.ContactList_Container);
        if (frag == null)
        {
            fm.beginTransaction().add(R.id.ContactList_Container, AddContacts).commit();
        }

        else
        {
            fm.beginTransaction().replace(R.id.ContactList_Container, AddContacts).commit();
        }
    }
    private void setupFragmentSwapper(MainActivityData mainActivityDataViewModel) {
        mainActivityDataViewModel.clickedValue.observe(this, new Observer<MainActivityData.Fragments>() {
            @Override
            public void onChanged(MainActivityData.Fragments clickedValue) {
                switch (mainActivityDataViewModel.getCurrentFragment()) {
                    case CONTACTLIST_FRAGMENT:
                        loadInitialContactList();
                        break;
                    case ADDCONTACTS_FRAGMENT:
                        loadAddContactsScreen();
                        break;
                }
            }
        });
    }
}
