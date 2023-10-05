package com.example.contactmanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditContactsFragment extends Fragment {

    private Contact contact;
    private EditText contactNameEditText;
    private EditText phoneNoEditText;
    private EditText emailEditText;
    private ImageView profileImage;
    private byte[] imageData = new byte[0]; // Initialize as an empty byte array

    private static final int CAMERA_PIC_REQUEST = 1;

    public EditContactsFragment() {
        // Required empty public constructor
    }

    public EditContactsFragment(Contact contact) {
        this.contact = contact;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_contacts, container, false);

        contactNameEditText = rootView.findViewById(R.id.ContactName);
        phoneNoEditText = rootView.findViewById(R.id.phoneNO);
        emailEditText = rootView.findViewById(R.id.EmailAddress);
        profileImage = rootView.findViewById(R.id.imageView);

        contactNameEditText.setText(contact.getName());
        phoneNoEditText.setText(String.valueOf(contact.getPhoneNo()));
        emailEditText.setText(contact.getEmail());

        byte[] photoData = contact.getPhotoData();

        if (photoData != null && photoData.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
            profileImage.setImageBitmap(bitmap);
        } else {
            profileImage.setImageResource(R.drawable.cat);
        }

        Button editPictureButton = rootView.findViewById(R.id.editPictureBtn);
        Button editContactButton = rootView.findViewById(R.id.EditContactButton);
        Button goBackEditButton = rootView.findViewById(R.id.EditBackButton);

        setupListeners(goBackEditButton, editContactButton, editPictureButton);
        return rootView;
    }

    public void updateContact(Contact oldContact, long newPhoneNumber, String updatedName, String updatedEmail)
    {
        Contact newContact = new Contact(newPhoneNumber, updatedName, updatedEmail, imageData);
        ContactDAO contactDAO = MainActivity.database.contactDao();
        contactDAO.insert(newContact);

        newContact.setName(updatedName);
        newContact.setEmail(updatedEmail);
        newContact.setPhotoData(imageData);

        contactDAO.update(newContact);
        contactDAO.delete(oldContact);
    }

    private void setupListeners(Button goBackEditButton, Button editContactButton, Button EditPicture)
    {
        MainActivityData mainActivityDataViewModel = new ViewModelProvider(getActivity()).get(MainActivityData.class);

        goBackEditButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                mainActivityDataViewModel.changeFragment(MainActivityData.Fragments.CONTACTLIST_FRAGMENT);
            }
        });

        editContactButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String updatedName = contactNameEditText.getText().toString();
                String updatedEmail = emailEditText.getText().toString();
                String phoneNo = phoneNoEditText.getText().toString();

                if (updatedName.isEmpty() || phoneNo.isEmpty() || updatedEmail.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else if (!updatedEmail.contains("example.com")) {
                    Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        long phoneNoLong = Long.parseLong(phoneNo);
                        if (phoneNoLong != contact.getPhoneNo())
                        {
                            updateContact(contact, phoneNoLong, updatedName, updatedEmail);
                        }

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
        EditPicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, CAMERA_PIC_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView imageView = getView().findViewById(R.id.imageView); // Use the correct ImageView ID
            imageView.setImageBitmap(image);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageData = stream.toByteArray();
        }
    }
}