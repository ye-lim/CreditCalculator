package com.example.creditcalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class FinishedSubjectOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "finished_subject.db";
    public FinishedSubjectOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists finished_subject (_id integer primary key autoincrement," +
                "major text not null, field text not null, subname text not null, subcode text not null unique," +
                "credit integer not null, required integer default 0 not null check(required in (0, 1)), grade text default 0, star flaot  default 0.0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE if exists finished_subject;");
        onCreate(db);
    }
}
