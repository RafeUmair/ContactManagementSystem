package com.example.contactmanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText nameEditText;
    private EditText phoneNoEditText;
    private EditText emailEditText;
    private static final int CAMERA_PIC_REQUEST = 1;
    private ImageView ProfileImage;
    private byte[] imageData;
    public AddContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddContactsFragment newInstance(String param1, String param2) {
        AddContactsFragment fragment = new AddContactsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_contacts, container, false);

        Button GoBack = rootView.findViewById(R.id.EditBackButton);
        Button AddContacts = rootView.findViewById(R.id.EditContactButton);
        Button cameraButton = rootView.findViewById(R.id.cameraButton);
        nameEditText = rootView.findViewById(R.id.ContactName);
        phoneNoEditText = rootView.findViewById(R.id.phoneNO);
        emailEditText = rootView.findViewById(R.id.EmailAddress);
        ProfileImage = rootView.findViewById(R.id.profileImage);
        setupListeners(GoBack, AddContacts);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, CAMERA_PIC_REQUEST);
            }
        });

        return rootView;
    }

    public void onResume()
    {
        super.onResume();
        nameEditText.setText("");
        phoneNoEditText.setText("");
        emailEditText.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView imageview = getView().findViewById(R.id.profileImage);
            imageview.setImageBitmap(image);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageData = stream.toByteArray();
        }
    }
    private void setupListeners(Button GoBack, Button AddContacts)
    {
        MainActivityData mainActivityDataViewModel = new ViewModelProvider(getActivity()).get(MainActivityData.class);

        GoBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                mainActivityDataViewModel.changeFragment(MainActivityData.Fragments.CONTACTLIST_FRAGMENT);
            }
        });

        AddContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String phoneNo = phoneNoEditText.getText().toString().trim();
                String email = emailEditText.getText().toString();
                long phoneNoLong;

                if (!name.isEmpty() && !phoneNo.isEmpty() && !email.isEmpty()) {
                    if (!email.contains("example.com"))
                    {
                        Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try
                        {
                            phoneNoLong = Long.parseLong(phoneNo);

                            if (isPhoneNoExists(phoneNoLong))
                            {
                                Toast.makeText(getActivity(), "Phone number already exists", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                Contact newContact = new Contact(phoneNoLong, name, email, imageData);

                                ContactDAO contactDAO = MainActivity.database.contactDao();
                                contactDAO.insert(newContact);

                                mainActivityDataViewModel.changeFragment(MainActivityData.Fragments.CONTACTLIST_FRAGMENT);
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isPhoneNoExists(long phoneNo)
    {
        MainActivityData mainActivityDataViewModel = new ViewModelProvider(getActivity()).get(MainActivityData.class);
        ContactDAO contactDAO = MainActivity.database.contactDao();
        List<Contact> contacts = contactDAO.getAllContacts();

        for (Contact contact : contacts)
        {
            if (contact.getPhoneNo() == phoneNo)
            {
                return true;
            }
        }
        return false;
    }
}