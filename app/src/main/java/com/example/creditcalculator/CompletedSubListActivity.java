package com.example.creditcalculator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CompletedSubListActivity extends AppCompatActivity {
    private PrivSubOpenHelper phelper;
    private StudentOpenHelper shelper;
    private FinishedSubjectOpenHelper fhelper;
    private SQLiteDatabase priv_subdb;
    private SQLiteDatabase stud_infodb;
    private SQLiteDatabase finished_subdb;
    private ListView lv;
    private SimpleCursorAdapter sa;
    private CheckBox rst_box, base, deep, again;
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_sub_list);
        phelper = new PrivSubOpenHelper(this);
        shelper = new StudentOpenHelper(this);
        fhelper = new FinishedSubjectOpenHelper(this);

        priv_subdb = phelper.getReadableDatabase();
        stud_infodb = shelper.getReadableDatabase();
        finished_subdb = fhelper.getReadableDatabase();

        rst_box = findViewById(R.id.rst_check);
        base = findViewById(R.id.c_base);
        deep = findViewById(R.id.c_deep);
        again = findViewById(R.id.c_again);

        c = finished_subdb.rawQuery("select * from finished_subject;", null);
        lv = findViewById(R.id.finished_subject_list);
        sa = new SimpleCursorAdapter(CompletedSubListActivity.this, R.layout.text_3, c,
                new String[]{"subname", "field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate}, 1);
        lv.setAdapter(sa);
        getTotalHeightofListView(lv);

        rst_box.setOnClickListener(view -> {
            resetSelectAll(view.getId());
            c = finished_subdb.rawQuery("select * from finished_subject;", null);
            sa = new SimpleCursorAdapter(CompletedSubListActivity.this, R.layout.text_3, c,
                    new String[]{"subname", "field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate}, 1);
            lv.setAdapter(sa);
            sa.notifyDataSetChanged();
            getTotalHeightofListView(lv);
        });

        base.setOnClickListener(view -> {
            resetSelectAll(view.getId());
            c = finished_subdb.rawQuery("select * from finished_subject order by case when field = \"핵심전공\" then 0 when field = \"심화전공\" then 1 else 99 end;", null);
            sa = new SimpleCursorAdapter(CompletedSubListActivity.this, R.layout.text_3, c,
                    new String[]{"subname", "field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate}, 1);
            lv.setAdapter(sa);
            sa.notifyDataSetChanged();
            getTotalHeightofListView(lv);
        });

        deep.setOnClickListener(view -> {
            resetSelectAll(view.getId());
            c = finished_subdb.rawQuery("select * from finished_subject order by case when field = \"심화전공\" then 0 when field = \"핵심전공\" then 1 else 99 end;", null);
            sa = new SimpleCursorAdapter(CompletedSubListActivity.this, R.layout.text_3, c,
                    new String[]{"subname", "field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate}, 1);
            lv.setAdapter(sa);
            sa.notifyDataSetChanged();
            getTotalHeightofListView(lv);
        });

        again.setOnClickListener(view -> {
            resetSelectAll(view.getId());
            c = finished_subdb.rawQuery("select * from finished_subject WHERE grade in (\"C+\",\"C-\",\"C0\",\"D+\",\"D0\",\"D-\",\"F\");", null);
            sa = new SimpleCursorAdapter(CompletedSubListActivity.this, R.layout.text_3, c,
                    new String[]{"subname", "field", "star"}, new int[]{R.id.subname, R.id.field, R.id.starrate}, 1);
            lv.setAdapter(sa);
            sa.notifyDataSetChanged();
            getTotalHeightofListView(lv);
        });

        Cursor tc = finished_subdb.rawQuery("select credit from finished_subject;", null);
        int my_credit = 0;
        while (tc.moveToNext()) {
            my_credit += tc.getInt(tc.getColumnIndex("credit"));
        }

        TextView credit = findViewById(R.id.my_credit);
        credit.setText(my_credit + " / 130");

        findViewById(R.id.bnt_modify).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompletedSubListActivity.this, SelfInfoModifyActivity.class);
                startActivity(intent);
            }
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
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private void resetSelectAll(int resid) {
        rst_box.setChecked(false); base.setChecked(false); deep.setChecked(false); again.setChecked(false);
        switch (resid) {
            case R.id.rst_check:
                rst_box.setChecked(true);
                break;
            case R.id.c_base:
                base.setChecked(true);
                break;
            case R.id.c_deep:
                deep.setChecked(true);
                break;
            case R.id.c_again:
                again.setChecked(true);
                break;
            default:
                break;
        }
    }
}
