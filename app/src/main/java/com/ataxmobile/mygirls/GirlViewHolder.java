package com.ataxmobile.mygirls;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class GirlViewHolder extends RecyclerView.ViewHolder {
    public ImageView gMark;
    public TextView name;
    public ImageView deleteContact;
    public ImageView editContact;

    public GirlViewHolder(View itemView) {
        super(itemView);
        gMark = (ImageView)itemView.findViewById(R.id.gMark );
        name = (TextView)itemView.findViewById(R.id.girl_name);
        deleteContact = (ImageView)itemView.findViewById(R.id.delete_girl );
        editContact = (ImageView)itemView.findViewById(R.id.edit_girl );
    }
}
