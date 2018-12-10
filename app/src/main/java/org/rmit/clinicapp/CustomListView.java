package org.rmit.clinicapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListView extends ArrayAdapter<Clinic> {

    private ArrayList<Clinic> clinics;
    private Activity context;

    public CustomListView(@NonNull Activity context, ArrayList<Clinic>clinics){
        super(context, R.layout.custom_cell,clinics);
        this.context = context;
        this.clinics = clinics;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if(r == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.custom_cell,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) r.getTag();
        }
        try{
            viewHolder.name.setText(clinics.get(position).getName());
            viewHolder.lead.setText((clinics.get(position).getLead_physician()));
            viewHolder.specialization.setText(clinics.get(position).getSpecialization());
        }catch (Exception e){

        }
        return r;
    }

    class ViewHolder{
        TextView name;
        TextView lead;
        TextView specialization;
        ViewHolder(View v){
            name = v.findViewById(R.id.name);
            lead = v.findViewById(R.id.leadphysic);
            specialization = v.findViewById(R.id.specialization);
        }
    }
}

