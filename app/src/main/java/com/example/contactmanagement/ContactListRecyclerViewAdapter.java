package com.example.contactmanagement;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.ContactViewHolder> {
    private List<Contact> contactList;
    private OnDeleteClickListener onDeleteClickListener; // Add this field

    public ContactListRecyclerViewAdapter(List<Contact> contactList) {
        this.contactList = contactList;
        Log.d("ContactListAdapter", "Contact list size: " + contactList.size()); // Log the list size
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_cell, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(String.valueOf(contact.getPhoneNo()));
        holder.emailTextView.setText(contact.getEmail());
        holder.contactImage.setImageResource(contact.getPhotoResourceId());
        Log.d("ContactListAdapter", "Binding item at position: " + position); // Log the binding process

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView phoneTextView;
        private TextView emailTextView;
        private ImageView contactImage;
        private Button deleteButton;
        private Button editButton;

        public ContactViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contactName);
            phoneTextView = itemView.findViewById(R.id.contactPhone);
            emailTextView = itemView.findViewById(R.id.contactEmail);
            contactImage = itemView.findViewById(R.id.contactPhoto);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    // Interface for delete button click listener
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    // Setter method for onDeleteClickListener
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }
}
