package com.ataxmobile.mygirls;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GirlAdapter extends RecyclerView.Adapter<GirlViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Girls> listContacts;
    private ArrayList<Girls> mArrayList;

    private SqliteDatabase mDatabase;

    // constructor
    public GirlAdapter(Context context, ArrayList<Girls> listContacts) {
        this.context = context;
        this.listContacts = listContacts;
        this.mArrayList = listContacts;
        mDatabase = new SqliteDatabase(context);
    }

    @Override
    public GirlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.girls_list_layout, parent, false);
        return new GirlViewHolder(view);
    }


    @Override
    public void onBindViewHolder(GirlViewHolder holder, int position) {
        final Girls contacts = listContacts.get(position);
        GregorianCalendar locC = (GregorianCalendar) Calendar.getInstance();

        if(contacts.getSub(locC) == 1) holder.gMark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_v_drop));
        else if((contacts.getSub(locC) == 2) || (contacts.getSub(locC) == 3)) holder.gMark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_v_fetus));
        else if(contacts.getPMS(locC) == 1) holder.gMark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_v_light_cloud));
        else holder.gMark.setImageDrawable(null);
//        holder.gMark.setImageTintList(R.color.design_default_color_error);

        holder.name.setText(contacts.getName());

        holder.editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ListActivity la = (ListActivity) view.getContext();
                ListActivity la = (ListActivity) GirlAdapter.this.context;
//                ListActivity la = (ListActivity) view.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
                la.editPostion(contacts.getId());
//                editTaskDialog(contacts);
            }
        });

        holder.deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete row from database
                mDatabase.deleteContact(contacts.getId());

                //refresh the activity page.
                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listContacts = mArrayList;
                } else {
                    ArrayList<Girls> filteredList = new ArrayList<>();
                    for (Girls contacts : mArrayList) {
                        if (contacts.getName().toLowerCase().contains(charString)) {
                            filteredList.add(contacts);
                        }
                    }
                    listContacts = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listContacts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listContacts = (ArrayList<Girls>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    private void editTaskDialog(final Girls contacts){
/**
        if(contacts != null){
            String eName = contacts.getName();
            Intent intent = new Intent(ListActivity.this, AddActivity.class);
            intent.putExtra("activity","list");
            intent.putExtra("name",eName);
            View view = LayoutInflater.inflate(R.layout.girls_list_layout, null);
            view.startActivity(intent);
        }
**/
        
        //        LayoutInflater inflater = LayoutInflater.from(context);
/***
        View subView = inflater.inflate(R.layout.add_contact_layout, null);

        final EditText nameField = (EditText)subView.findViewById(R.id.enter_name);
        final EditText contactField = (EditText)subView.findViewById(R.id.enter_phno);

        if(contacts != null){
            nameField.setText(contacts.getName());
            contactField.setText(String.valueOf(contacts.getPhno()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit contact");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("EDIT CONTACT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final String ph_no = contactField.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(context, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                }
                else{
                    mDatabase.updateContacts(new Contacts(contacts.getId(), name, ph_no));
                    //refresh the activity
                    ((Activity)context).finish();
                    context.startActivity(((Activity)context).getIntent());
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
 ****/
    }
}