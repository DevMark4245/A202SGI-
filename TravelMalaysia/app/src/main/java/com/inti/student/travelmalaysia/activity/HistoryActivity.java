package com.inti.student.travelmalaysia.activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.inti.student.travelmalaysia.R;
import com.inti.student.travelmalaysia.model.HistoryModel;
import com.inti.student.travelmalaysia.adapter.HistoryAdapter;
import com.inti.student.travelmalaysia.database.DatabaseHelper;
import com.inti.student.travelmalaysia.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {

    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    SessionManager session;
    String id_book = "", location, destination, date, adult, child, history, total;
    String email;
    TextView tvNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        tvNotFound = findViewById(R.id.noHistory);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        email = user.get(SessionManager.KEY_EMAIL);

        refreshList();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbHistory);
        toolbar.setTitle("Booking History");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshList() {
        final ArrayList<HistoryModel> historyList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM TB_BOOK, TB_PRICE WHERE TB_BOOK.id_book = TB_PRICE.id_book AND username='" + email + "'", null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            id_book = cursor.getString(0);
            location = cursor.getString(1);
            destination = cursor.getString(2);
            date = cursor.getString(3);
            adult = cursor.getString(4);
            child = cursor.getString(5);
            total = cursor.getString(10);
            history = "Successfully booked transport from " + location + " to " + destination + " on " + date + ". " +
                    "Total number of persons is " + adult + " adults and " + child + " children.";
            historyList.add(new HistoryModel(id_book, date, history, total, R.drawable.profile));
        }

        ListView listBook = findViewById(R.id.list_booking);
        HistoryAdapter arrayAdapter = new HistoryAdapter(this, historyList);
        listBook.setAdapter(arrayAdapter);

        //delete data
        listBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String selection = historyList.get(i).getIdBook();
                final CharSequence[] dialog = {"Delete Data"};
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setTitle("Option");
                builder.setItems(dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        try {
                            db.execSQL("DELETE FROM TB_BOOK where id_book = " + selection + "");
                            id_book = "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        refreshList();
                    }
                });
                builder.create().show();
            }
        });

        if (id_book.equals("")) {
            tvNotFound.setVisibility(View.VISIBLE);
            listBook.setVisibility(View.GONE);
        } else {
            tvNotFound.setVisibility(View.GONE);
            listBook.setVisibility(View.VISIBLE);
        }

    }
}
