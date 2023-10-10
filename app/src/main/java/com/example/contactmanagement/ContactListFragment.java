package com.example.contactmanagement;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;
import android.Manifest;

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
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    private ImageView profileImage;

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

        adapter = new ContactListRecyclerViewAdapter(contactList);
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
            public void onClick(View view)
            {
                importContactsFromDevice();
            }
        });
    }

    private void importContactsFromDevice()
    {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }

        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        };

        Cursor cursor = requireActivity().getContentResolver().query(contactsUri, projection, null, null, null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    String phoneNumber = getPhoneNumber(cursor);
                    String emailAddress = getEmailAddress(cursor);
                    long phoneNoLong = Long.parseLong(phoneNumber);

                    if (!isContactDuplicate(phoneNoLong)) {
                        Contact newContact = new Contact(phoneNoLong, contactName, emailAddress, imageToByteArray(requireContext(), R.drawable.cat));

                        ContactDAO contactDAO = MainActivity.database.contactDao();
                        contactDAO.insert(newContact);
                        Toast.makeText(requireContext(), "Contacts imported successfully!", Toast.LENGTH_SHORT).show();
                        contactList.add(newContact);
                        adapter.notifyDataSetChanged();
                    }
                    
                    else
                    {
                        Toast.makeText(requireContext(), "Duplicate phone number not allowed: " + phoneNumber, Toast.LENGTH_SHORT).show();
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }

    private boolean isContactDuplicate(long phoneNo) {
        for (Contact contact : contactList) {
            if (contact.getPhoneNo() == phoneNo) {
                return true;
            }
        }
        return false;
    }

    private String getPhoneNumber(Cursor cursor)
    {
        String phoneNumber = "";
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        Cursor phoneCursor = requireActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );

        if(phoneCursor != null)
        {
            try
            {
                if (phoneCursor.moveToFirst())
                {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            }
            finally
            {
                phoneCursor.close();
            }
        }

        return phoneNumber;
    }

    private String getEmailAddress(Cursor cursor)
    {
        String emailAddress = "";
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        Cursor emailCursor = requireActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );

        if (emailCursor != null)
        {
            try
            {
                if (emailCursor.moveToFirst())
                {
                    emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                }
            }
            finally
            {
                emailCursor.close();
            }
        }

        return emailAddress;
    }
    private static byte[] imageToByteArray(Context context, int resourceId)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}