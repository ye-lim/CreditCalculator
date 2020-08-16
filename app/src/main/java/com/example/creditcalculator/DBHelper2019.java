package com.example.creditcalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper2019 extends SQLiteOpenHelper {  //새로 생성한 adapter 속성은 SQLiteOpenHelper이다.
    public static final String DB_NAME = "2019.db"; //기본, 전체 과목 데이터 저장 db
    public DBHelper2019(Context context) {
        super(context, DB_NAME, null, 1);    // db명과 버전만 정의 한다.
        DataBaseSetter.setDB(context, DB_NAME);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
}