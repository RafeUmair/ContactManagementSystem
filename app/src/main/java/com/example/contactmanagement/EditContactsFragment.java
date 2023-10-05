package com.example.contactmanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Contact contact;
    EditText contactNameEditText;
    EditText phoneNoEditText;
    EditText emailEditText;
    ImageView imageEdit;

    public EditContactsFragment() {
        // Required empty public constructor
    }

    public EditContactsFragment(Contact contact)
    {
        this.contact = contact;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditContactsFragment newInstance(String param1, String param2) {
        EditContactsFragment fragment = new EditContactsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_edit_contacts, container, false);

        contactNameEditText = rootView.findViewById(R.id.ContactName);
        phoneNoEditText = rootView.findViewById(R.id.phoneNO);
        emailEditText = rootView.findViewById(R.id.EmailAddress);
        imageEdit = rootView.findViewById(R.id.imageView);

        contactNameEditText.setText(contact.getName());
        phoneNoEditText.setText(String.valueOf(contact.getPhoneNo()));
        emailEditText.setText(contact.getEmail());
        imageEdit.setImageResource(contact.getPhotoResourceId());

        Button editContactButton = rootView.findViewById(R.id.EditContactButton);
        Button GobackEditButton = rootView.findViewById(R.id.EditBackButton);

        setupListeners(GobackEditButton, editContactButton);
        return rootView;
    }

    public void updatePhoneNumber(Contact oldContact, long newPhoneNumber)
    {
        Contact newContact = new Contact(newPhoneNumber, oldContact.getName(), oldContact.getEmail(), oldContact.getPhotoResourceId());
        ContactDAO contactDAO = MainActivity.database.contactDao();
        contactDAO.insert(newContact);

        newContact.setName(oldContact.getName());
        newContact.setEmail(oldContact.getEmail());
        newContact.setPhotoResourceId(oldContact.getPhotoResourceId());

        contactDAO.update(newContact);

        contactDAO.delete(oldContact);
    }
    private void setupListeners(Button GobackEditButton, Button editContactButton)
    {
        MainActivityData mainActivityDataViewModel = new ViewModelProvider(getActivity()).get(MainActivityData.class);

        GobackEditButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                mainActivityDataViewModel.changeFragment(MainActivityData.Fragments.CONTACTLIST_FRAGMENT);
            }
        });

        editContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedName = contactNameEditText.getText().toString();
                String updatedEmail = emailEditText.getText().toString();
                String phoneNo = phoneNoEditText.getText().toString();

                if (updatedName.isEmpty() || phoneNo.isEmpty() || updatedEmail.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }

                else if (!updatedEmail.contains("example.com"))
                {
                    Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        long phoneNoLong = Long.parseLong(phoneNo);
                        if (phoneNoLong != contact.getPhoneNo()) {
                            updatePhoneNumber(contact, phoneNoLong);
                        }

                        contact.setName(updatedName);
                        contact.setEmail(updatedEmail);

                        ContactDAO contactDAO = MainActivity.database.contactDao();
                        contactDAO.update(contact);

                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    catch (NumberFormatException e)
                    {
                        Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}