package com.example.contactmanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.contactmanagement.ContactListRecyclerViewAdapter;

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

        setupListeners(AddButton);

        // Initialize contactList with data from the database
        ContactDAO contactDAO = MainActivity.database.contactDao();
        contactList = contactDAO.getAllContacts();

        ContactListRecyclerViewAdapter adapter = new ContactListRecyclerViewAdapter(contactList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // Set up the OnDeleteClickListener for the adapter
        adapter.setOnDeleteClickListener(new ContactListRecyclerViewAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                long phoneNoToDelete = contactList.get(position).getPhoneNo();
                contactDAO.deleteByPhoneNo(phoneNoToDelete);
                contactList.remove(position);
                adapter.notifyItemRemoved(position);

                // You can remove the item from the list and update the RecyclerView
                // You may also want to update the database to reflect the changes
                // Example: contactDAO.deleteContact(contactList.get(position));
                // Then, update the list and notify the adapter
                // contactList.remove(position);
                // adapter.notifyItemRemoved(position);
            }
        });


        return rootView;
    }




    private void setupListeners(Button AddButton) {
        MainActivityData mainActivityDataViewModel = new ViewModelProvider(getActivity()).get(MainActivityData.class);

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityDataViewModel.changeFragment(MainActivityData.Fragments.ADDCONTACTS_FRAGMENT);
            }
        });
    }


}
