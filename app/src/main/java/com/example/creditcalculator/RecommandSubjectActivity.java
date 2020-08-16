package com.example.creditcalculator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecommandSubjectActivity extends AppCompatActivity {
    private PrivSubOpenHelper phelper;
    private StudentOpenHelper shelper;
    private FinishedSubjectOpenHelper fhelper;
    private SQLiteDatabase priv_subdb;
    private SQLiteDatabase stud_infodb;
    private SQLiteDatabase finished_subdb;
    private ListView lv;
    private CheckBox major, dmajor, minor, again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand_subject);

        phelper = new PrivSubOpenHelper(this);
        shelper = new StudentOpenHelper(this);
        fhelper = new FinishedSubjectOpenHelper(this);

        priv_subdb = phelper.getReadableDatabase();
        stud_infodb = shelper.getReadableDatabase();
        finished_subdb = fhelper.getReadableDatabase();

        List<String> list = new ArrayList<>();
        lv = findViewById(R.id.priv_subject_list);
        Cursor c = priv_subdb.rawQuery("select * from priv_subject;", null);
        while (c.moveToNext()) {
            if (c.getInt(c.getColumnIndex("finished")) == 0) {
                String s = c.getString(c.getColumnIndex("subname"));
                list.add(s);
            }
        }

        SimpleCursorAdapter sa = new SimpleCursorAdapter(this, R.layout.text_2, c,
                new String[]{"subname","field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate},1);
        lv.setAdapter(sa);
        getTotalHeightofListView(lv);

        Cursor minc = stud_infodb.rawQuery("select major, dmajor, minor from student;", null);
        minc.moveToNext();
        String minorname = minc.getString(minc.getColumnIndex("minor"));

        major = findViewById(R.id.b_major);
        dmajor = findViewById(R.id.b_dmajor);
        minor = findViewById(R.id.b_minor);
        again = findViewById(R.id.b_again);

        if (minorname.equals("none")) minor.setVisibility(View.GONE);

        findViewById(R.id.b_major).setOnClickListener(view -> {
            resetSelectAll(view.getId());
            Cursor tmp = stud_infodb.rawQuery("select major, dmajor, minor from student;", null);
            tmp.moveToNext();
            String major = tmp.getString(tmp.getColumnIndex("major"));
            tmp.close();
            Cursor c1 = priv_subdb.rawQuery("select * from priv_subject order by case when major = \""+major+"\" THEN 0 else 99 end;", null);
            SimpleCursorAdapter sa1 = new SimpleCursorAdapter(RecommandSubjectActivity.this, R.layout.text_2, c1,
                    new String[]{"subname","field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate},1);
            lv.setAdapter(sa1);
            getTotalHeightofListView(lv);
        });

        findViewById(R.id.b_dmajor).setOnClickListener(view -> {
            resetSelectAll(view.getId());
            Cursor tmp = stud_infodb.rawQuery("select major, dmajor, minor from student;", null);
            tmp.moveToNext();
            String dmajor = tmp.getString(tmp.getColumnIndex("dmajor"));
            tmp.close();
            Cursor c12 = priv_subdb.rawQuery("select * from priv_subject order by case when major = \""+dmajor+"\" THEN 0 else 99 end;", null);
            SimpleCursorAdapter sa12 = new SimpleCursorAdapter(RecommandSubjectActivity.this, R.layout.text_2, c12,
                    new String[]{"subname","field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate},1);
            lv.setAdapter(sa12);
            getTotalHeightofListView(lv);
        });

        findViewById(R.id.b_minor).setOnClickListener(view -> {
            resetSelectAll(view.getId());
            Cursor tmp = stud_infodb.rawQuery("select major, dmajor, minor from student;", null);
            tmp.moveToNext();
            String minor = tmp.getString(tmp.getColumnIndex("minor"));
            tmp.close();
            if (minor.equals("none")) return;
            Cursor c13 = priv_subdb.rawQuery("select * from priv_subject order by case when major = \""+minor+"\" THEN 0 else 99 end;", null);
            ListView lv = findViewById(R.id.priv_subject_list);
            SimpleCursorAdapter sa13 = new SimpleCursorAdapter(RecommandSubjectActivity.this, R.layout.text_2, c13,
                    new String[]{"subname","field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate},1);
            lv.setAdapter(sa13);
            getTotalHeightofListView(lv);
        });


        findViewById(R.id.b_again).setOnClickListener(view -> {
            resetSelectAll(view.getId());
            Cursor c14 = finished_subdb.rawQuery("select * from finished_subject WHERE grade in (\"C+\",\"C-\",\"C0\",\"D+\",\"D0\",\"D-\",\"F\");", null);
            ListView lv = findViewById(R.id.priv_subject_list);
            SimpleCursorAdapter sa14 = new SimpleCursorAdapter(RecommandSubjectActivity.this, R.layout.text_2, c14,
                    new String[]{"subname","field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate},1);
            lv.setAdapter(sa14);
            getTotalHeightofListView(lv);
        });

        Cursor tc = finished_subdb.rawQuery("select credit from finished_subject;", null);
        int my_credit = 0;
        while (tc.moveToNext()) {
            my_credit +=  tc.getInt(tc.getColumnIndex("credit"));
        }

        TextView credit = findViewById(R.id.my_credit);
        credit.setText(my_credit + " / 130");

        findViewById(R.id.bnt_modify).setOnClickListener(view -> {
            Intent intent = new Intent(RecommandSubjectActivity.this, SelfInfoModifyActivity.class);
            startActivity(intent);
        });
    }
    public void onClick(View v) {
        if (v.getId() == R.id.insert) {
            Intent intent = new Intent(this, AdditionalInfoActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.bnt1) {
            Intent intent = new Intent(this, CompleteSubActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.bnt2) {
            Intent intent = new Intent(this, CompletedSubListActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.bnt3) {
            Intent intent = new Intent(this, RecommandSubjectActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.bnt4) {
            Intent intent = new Intent(this, SelfInfoActivity.class);
            startActivity(intent);
        }

    }

    public static void getTotalHeightofListView(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private void resetSelectAll(int resid) {
        major.setChecked(false); dmajor.setChecked(false); minor.setChecked(false); again.setChecked(false);
        switch (resid) {
            case R.id.b_major:
                major.setChecked(true);
                break;
            case R.id.b_dmajor:
                dmajor.setChecked(true);
                break;
            case R.id.b_minor:
                minor.setChecked(true);
                break;
            case R.id.b_again:
                again.setChecked(true);
                break;
            default:
                break;
        }
    }
}