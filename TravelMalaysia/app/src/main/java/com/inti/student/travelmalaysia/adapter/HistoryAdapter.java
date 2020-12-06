package com.inti.student.travelmalaysia.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inti.student.travelmalaysia.R;
import com.inti.student.travelmalaysia.model.HistoryModel;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<HistoryModel> {

    public HistoryAdapter(Activity context, ArrayList<HistoryModel> notification) {
        super(context, 0, notification);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_booking, parent, false);
        }

        HistoryModel current = getItem(position);

        TextView idBook = listItemView.findViewById(R.id.id_booking);
        idBook.setText("ID : " + current.getIdBook());

        TextView date = listItemView.findViewById(R.id.date);
        date.setText(current.getDate());

        TextView history = listItemView.findViewById(R.id.history);
        history.setText(current.getHistory());

        TextView tvTotal = listItemView.findViewById(R.id.tv_total);
        tvTotal.setText("Total :");

        TextView total = listItemView.findViewById(R.id.total);
        total.setText("RM " + current.getTotal());

        ImageView imageIcon = listItemView.findViewById(R.id.image);

        if (current.hasImage()) {
            imageIcon.setImageResource(current.getImageResourceId());
            imageIcon.setVisibility(View.VISIBLE);
        } else {
            imageIcon.setVisibility(View.GONE);
        }

        return listItemView;
    }
}