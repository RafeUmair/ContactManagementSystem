package com.example.contactmanagement;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Contact> contactList;
    private ContactListRecyclerViewAdapter adapter;

    public ContactListFragment() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactListFragment newInstance(String param1, String param2) {
        ContactListFragment fragment = new ContactListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        Button AddButton = rootView.findViewById(R.id.AddButton);
        Button ImportButton = rootView.findViewById(R.id.importButton);
        setupListeners(AddButton, ImportButton);

        ContactDAO contactDAO = MainActivity.database.contactDao();
        contactList = contactDAO.getAllContacts();

        ContactListRecyclerViewAdapter adapter = new ContactListRecyclerViewAdapter(contactList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setOnDeleteClickListener(new ContactListRecyclerViewAdapter.OnDeleteClickListener() {
            MainActivityData mainActivityDataViewModel = new ViewModelProvider(getActivity()).get(MainActivityData.class);

            @Override
            public void onDeleteClick(int position) {
                long phoneNoToDelete = contactList.get(position).getPhoneNo();
                contactDAO.deleteByPhoneNo(phoneNoToDelete);
                contactList.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onEditClick(int position) {
                Contact selectedContact = contactList.get(position);
                EditContactsFragment editFragment = new EditContactsFragment(selectedContact);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.ContactList_Container, editFragment).addToBackStack(null).commit();
            }
        });
        return rootView;
    }

    private void setupListeners(Button AddButton, Button ImportButton) {
        MainActivityData mainActivityDataViewModel = new ViewModelProvider(getActivity()).get(MainActivityData.class);

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityDataViewModel.changeFragment(MainActivityData.Fragments.ADDCONTACTS_FRAGMENT);
            }
        });

        ImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

























    private void getContactList() {
        ContentResolver cr = requireActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("ContactListFragment", "Name: " + name);
                        Log.i("ContactListFragment", "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }
}


   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            if (contactData != null) {
                ContentResolver cr = requireActivity().getContentResolver();
                Cursor cursor = cr.query(contactData, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    // Get the contact's name
                    int nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    String name = (nameColumnIndex != -1) ? cursor.getString(nameColumnIndex) : "";

                    // Get the contact's email (if available)
                    String email = "";
                    int emailColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                    if (emailColumnIndex != -1) {
                        Cursor emailCursor = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))},
                                null
                        );
                        if (emailCursor != null && emailCursor.moveToFirst()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            emailCursor.close();
                        }
                    }

                    // Get the contact's phone number (if available)
                    String phone = "";
                    int phoneColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    if (phoneColumnIndex != -1) {
                        Cursor phoneCursor = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))},
                                null
                        );
                        if (phoneCursor != null && phoneCursor.moveToFirst()) {
                            phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneCursor.close();
                        }
                    }

                    // Now, you have the name, email, and phone number
                    Log.i("ContactListFragment", "Name: " + name);
                    if (!email.isEmpty()) {
                        Log.i("ContactListFragment", "Email: " + email);
                    }
                    if (!phone.isEmpty()) {
                        Log.i("ContactListFragment", "Phone Number: " + phone);
                    }

                    // Close the cursor
                    cursor.close();
                }
            }
        }
    }*/


