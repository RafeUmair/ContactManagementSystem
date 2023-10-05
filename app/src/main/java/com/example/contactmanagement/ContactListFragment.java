package com.example.contactmanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        Button AddButton = rootView.findViewById(R.id.AddButton);

        setupListeners(AddButton);

        ContactDAO contactDAO = MainActivity.database.contactDao();
        contactList = contactDAO.getAllContacts();

        ContactListRecyclerViewAdapter adapter = new ContactListRecyclerViewAdapter(contactList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setOnDeleteClickListener(new ContactListRecyclerViewAdapter.OnDeleteClickListener()
        {
            MainActivityData mainActivityDataViewModel = new ViewModelProvider(getActivity()).get(MainActivityData.class);
            @Override
            public void onDeleteClick(int position) {
                long phoneNoToDelete = contactList.get(position).getPhoneNo();
                contactDAO.deleteByPhoneNo(phoneNoToDelete);
                contactList.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onEditClick(int position)
            {
                Contact selectedContact = contactList.get(position);
                EditContactsFragment editFragment = new EditContactsFragment(selectedContact);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.ContactList_Container, editFragment).addToBackStack(null).commit();
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
