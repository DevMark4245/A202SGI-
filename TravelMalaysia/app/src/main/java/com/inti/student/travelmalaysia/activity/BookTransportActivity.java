package com.inti.student.travelmalaysia.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.inti.student.travelmalaysia.R;
import com.inti.student.travelmalaysia.database.DatabaseHelper;
import com.inti.student.travelmalaysia.session.SessionManager;

import java.util.Calendar;
import java.util.HashMap;

public class BookTransportActivity extends AppCompatActivity {

    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Spinner spinLocation, spinDestination, spinAdult, spinChild;
    SessionManager session;
    String email;
    int id_book;
    public String sLocat, sDest, sDate, sAdult, sChild;
    int numAdult, numChild;
    int adultPrice, childPrice;
    int totalAdultPrice, totalChildPrice, totalPrice;
    private EditText etDate;
    private DatePickerDialog dpDate;
    Calendar newCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_transport);

        dbHelper = new DatabaseHelper(BookTransportActivity.this);
        db = dbHelper.getReadableDatabase();

        final String[] locationArr = {"Penang", "Selangor", "Sabah", "Sarawak", "Malacca"};
        final String[] destinationArr = {"Penang", "Selangor", "Sabah", "Sarawak", "Malacca"};
        final String[] adultArr = {"0", "1", "2", "3", "4", "5"};
        final String[] childArr = {"0", "1", "2", "3", "4", "5"};

        spinLocation = findViewById(R.id.location);
        spinDestination = findViewById(R.id.destination);
        spinAdult = findViewById(R.id.adult);
        spinChild = findViewById(R.id.children);

        ArrayAdapter<CharSequence> adapterAsal = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, locationArr);
        adapterAsal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLocation.setAdapter(adapterAsal);

        ArrayAdapter<CharSequence> adapterTujuan = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, destinationArr);
        adapterTujuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDestination.setAdapter(adapterTujuan);

        ArrayAdapter<CharSequence> adapterDewasa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, adultArr);
        adapterDewasa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAdult.setAdapter(adapterDewasa);

        ArrayAdapter<CharSequence> adapterAnak = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, childArr);
        adapterAnak.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinChild.setAdapter(adapterAnak);

        spinLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sLocat = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sDest = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinAdult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sAdult = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sChild = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnBook = findViewById(R.id.book);

        etDate = findViewById(R.id.departure_date);
        etDate.setInputType(InputType.TYPE_NULL);
        etDate.requestFocus();
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);
        setDateTimeField();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceCalc();
                if (sLocat != null && sDest != null && sDate != null && sAdult != null) {
                    if ((sLocat.equalsIgnoreCase("penang") && sDest.equalsIgnoreCase("penang"))
                            || (sLocat.equalsIgnoreCase("selangor") && sDest.equalsIgnoreCase("selangor"))
                            || (sLocat.equalsIgnoreCase("sabah") && sDest.equalsIgnoreCase("sabah"))
                            || (sLocat.equalsIgnoreCase("sarawak") && sDest.equalsIgnoreCase("sarawak"))
                            || (sLocat.equalsIgnoreCase("malacca") && sDest.equalsIgnoreCase("malacca"))) {
                        Toast.makeText(BookTransportActivity.this, "Starting location and destination cannot be the same!", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(BookTransportActivity.this)
                                .setTitle("Book transport now?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            db.execSQL("INSERT INTO TB_BOOK (location, destination, date, adult, child) VALUES ('" +
                                                    sLocat + "','" +
                                                    sDest + "','" +
                                                    sDate + "','" +
                                                    sAdult + "','" +
                                                    sChild + "');");
                                            cursor = db.rawQuery("SELECT id_book FROM TB_BOOK ORDER BY id_book DESC", null);
                                            cursor.moveToLast();
                                            if (cursor.getCount() > 0) {
                                                cursor.moveToPosition(0);
                                                id_book = cursor.getInt(0);
                                            }
                                            db.execSQL("INSERT INTO TB_PRICE (username, id_book, price_adult, price_child, price_total) VALUES ('" +
                                                    email + "','" +
                                                    id_book + "','" +
                                                    totalAdultPrice + "','" +
                                                    totalChildPrice + "','" +
                                                    totalPrice + "');");
                                            Toast.makeText(BookTransportActivity.this, "Booking success", Toast.LENGTH_LONG).show();
                                            finish();
                                        } catch (Exception e) {
                                            Toast.makeText(BookTransportActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create();
                        dialog.show();
                    }
                } else {
                    Toast.makeText(BookTransportActivity.this, "Please fill all the information!", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupToolbar();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbKrl);
        toolbar.setTitle("Booking form");
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

    public void priceCalc() {
        if (sLocat.equalsIgnoreCase("penang") && sDest.equalsIgnoreCase("selangor")) {
            adultPrice = 100;
            childPrice = 70;
        } else if (sLocat.equalsIgnoreCase("penang") && sDest.equalsIgnoreCase("sabah")) {
            adultPrice = 200;
            childPrice = 150;
        } else if (sLocat.equalsIgnoreCase("penang") && sDest.equalsIgnoreCase("sarawak")) {
            adultPrice = 150;
            childPrice = 120;
        } else if (sLocat.equalsIgnoreCase("penang") && sDest.equalsIgnoreCase("malacca")) {
            adultPrice = 180;
            childPrice = 140;
        } else if (sLocat.equalsIgnoreCase("selangor") && sDest.equalsIgnoreCase("penang")) {
            adultPrice = 100;
            childPrice = 70;
        } else if (sLocat.equalsIgnoreCase("selangor") && sDest.equalsIgnoreCase("sabah")) {
            adultPrice = 120;
            childPrice = 100;
        } else if (sLocat.equalsIgnoreCase("selangor") && sDest.equalsIgnoreCase("sarawak")) {
            adultPrice = 120;
            childPrice = 90;
        } else if (sLocat.equalsIgnoreCase("selangor") && sDest.equalsIgnoreCase("malacca")) {
            adultPrice = 190;
            childPrice = 160;
        } else if (sLocat.equalsIgnoreCase("sabah") && sDest.equalsIgnoreCase("penang")) {
            adultPrice = 200;
            childPrice = 150;
        } else if (sLocat.equalsIgnoreCase("sabah") && sDest.equalsIgnoreCase("selangor")) {
            adultPrice = 120;
            childPrice = 100;
        } else if (sLocat.equalsIgnoreCase("sabah") && sDest.equalsIgnoreCase("sarawak")) {
            adultPrice = 170;
            childPrice = 130;
        } else if (sLocat.equalsIgnoreCase("sabah") && sDest.equalsIgnoreCase("malacca")) {
            adultPrice = 180;
            childPrice = 150;
        } else if (sLocat.equalsIgnoreCase("sarawak") && sDest.equalsIgnoreCase("penang")) {
            adultPrice = 150;
            childPrice = 120;
        } else if (sLocat.equalsIgnoreCase("sarawak") && sDest.equalsIgnoreCase("selangor")) {
            adultPrice = 120;
            childPrice = 90;
        } else if (sLocat.equalsIgnoreCase("sarawak") && sDest.equalsIgnoreCase("malacca")) {
            adultPrice = 80;
            childPrice = 40;
        } else if (sLocat.equalsIgnoreCase("sarawak") && sDest.equalsIgnoreCase("sabah")) {
            adultPrice = 170;
            childPrice = 130;
        } else if (sLocat.equalsIgnoreCase("malacca") && sDest.equalsIgnoreCase("penang")) {
            adultPrice = 180;
            childPrice = 140;
        } else if (sLocat.equalsIgnoreCase("malacca") && sDest.equalsIgnoreCase("selangor")) {
            adultPrice = 190;
            childPrice = 160;
        } else if (sLocat.equalsIgnoreCase("malacca") && sDest.equalsIgnoreCase("sarawak")) {
            adultPrice = 80;
            childPrice = 40;
        } else if (sLocat.equalsIgnoreCase("malacca") && sDest.equalsIgnoreCase("sabah")) {
            adultPrice = 180;
            childPrice = 150;
        }

        numAdult = Integer.parseInt(sAdult);
        numChild = Integer.parseInt(sChild);

        totalAdultPrice = numAdult * adultPrice;
        totalChildPrice = numChild * childPrice;
        totalPrice = totalAdultPrice + totalChildPrice;
    }

    private void setDateTimeField() {
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpDate.show();
            }
        });

        dpDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String[] month = {"January", "February", "March", "April", "May",
                        "June", "July", "August", "September", "October", "November", "December"};
                sDate = dayOfMonth + " " + month[monthOfYear] + " " + year;
                etDate.setText(sDate);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}