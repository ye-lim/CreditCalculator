package com.example.creditcalculator;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

public class CompleteSubActivity extends AppCompatActivity {
    private PrivSubOpenHelper phelper;
    private StudentOpenHelper shelper;
    private SQLiteOpenHelper tshelper;
    private FinishedSubjectOpenHelper fhelper;
    private SQLiteOpenHelper openHelper2014;
    private SQLiteOpenHelper openHelper2016;
    private SQLiteOpenHelper openHelper2017;
    private SQLiteOpenHelper openHelper2018;
    private SQLiteOpenHelper openHelper2019;
    private SQLiteOpenHelper tchelper;

    private SQLiteDatabase teaching_lidb;
    private SQLiteDatabase priv_subdb;
    private SQLiteDatabase total_subdb;
    private SQLiteDatabase stud_infodb;
    private SQLiteDatabase finish_subdb;
    private SQLiteDatabase db_2014_2015;
    private SQLiteDatabase db_2016;
    private SQLiteDatabase db_2017;
    private SQLiteDatabase db_2018;
    private SQLiteDatabase db_2019;

    private String major, dmajor, minor, std_no;
    private int adv_major, teaching;

    private Cursor d_cursor;
    private Cursor tc;
    private InfoMemory info;
    private File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/noname.txt");
    public TextFileReader tfr = new TextFileReader();

    //핵심교양 들었는지 체크
    private int hec1 = 0;
    private int hec2 = 0;
    private int hec3 = 0;
    private int hec4 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_sub);

        tshelper = new DBHelper(this);
        phelper = new PrivSubOpenHelper(this);
        shelper = new StudentOpenHelper(this);
        fhelper = new FinishedSubjectOpenHelper(this);
        tchelper = new teachingListHelper(this);
        openHelper2014 = new DBHelper2014(this);
        openHelper2016 = new DBHelper2016(this);
        openHelper2017 = new DBHelper2017(this);
        openHelper2018 = new DBHelper2018(this);
        openHelper2019 = new DBHelper2019(this);

        Cursor tmp = stud_infodb.rawQuery("select major, dmajor, minor, adv_major, std_no ,teaching from student;", null);
        tmp.moveToFirst();
        major = tmp.getString(tmp.getColumnIndex("major"));//주전공
        dmajor = tmp.getString(tmp.getColumnIndex("dmajor"));//복수전공
        minor = tmp.getString(tmp.getColumnIndex("minor"));//부전공
        std_no = tmp.getString(tmp.getColumnIndex("std_no"));//학번
        adv_major = tmp.getInt(tmp.getColumnIndex("adv_major"));// 심화여부
        teaching = tmp.getInt(tmp.getColumnIndex("teaching"));// 교직여부
        tmp.close();


        SQLiteDatabase database = phelper.getReadableDatabase();
        Cursor c = database.rawQuery("select * from priv_subject;", null, null); //안에 내용물이 있는지 확인
        if (c.getCount() == 0) {
            setSubjectDatas(); // priv디비 정보 넣어주기..
        }

        info = new InfoMemory();
        setSubjectFinished(); // 내가 들은 과목을 텍스트파일에서 읽어와 이수한 과목은 이수현황에 True 체크
        setCredit();

        Cursor tc = finish_subdb.rawQuery("select credit from finished_subject;", null);
        int my_credit = 0;
        while (tc.moveToNext()) {
            my_credit +=  tc.getInt(tc.getColumnIndex("credit"));
        }

        TextView credit = findViewById(R.id.my_credit);
        credit.setText(my_credit + " / 130");

        findViewById(R.id.bnt_modify).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompleteSubActivity.this, SelfInfoModifyActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
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

    public void setSubjectDatas() {
        total_subdb = tshelper.getReadableDatabase();
        priv_subdb = phelper.getWritableDatabase();
        stud_infodb = shelper.getReadableDatabase();
        teaching_lidb = tchelper.getReadableDatabase();

        // 자신이 속한 전공과 동일한 전공에 대해서만 과목 전부 가져오기 (이수현황은 아직 전부 false인 상태)
        //전공, 부전공, 복수전공, 교직여부 가져옴.
        Cursor tmp = stud_infodb.rawQuery("select major, dmajor, minor from student;", null);
        tmp.moveToNext();
        String major = tmp.getString(tmp.getColumnIndex("major"));
        String dmajor = tmp.getString(tmp.getColumnIndex("dmajor"));
        String minor = tmp.getString(tmp.getColumnIndex("minor"));
        int teaching = tmp.getColumnIndex("teaching");

        //전공, 부전공, 복수전공
        tc = total_subdb.rawQuery("select * from subjects where majorname in (?, ?, ?);", new String[]{major, dmajor, minor});
        while (tc.moveToNext()) {
            ContentValues values = new ContentValues();
            values.put("major", tc.getString(tc.getColumnIndex("majorname")));
            values.put("field", tc.getString(tc.getColumnIndex("field")));
            values.put("subname", tc.getString(tc.getColumnIndex("subname")));
            values.put("subcode", tc.getString(tc.getColumnIndex("subcode")));
            values.put("credit", tc.getLong(tc.getColumnIndex("credit")));
            values.put("required", tc.getInt(tc.getColumnIndex("required")));
            values.put("star",tc.getFloat(tc.getColumnIndex("star")));

            try {
                priv_subdb.insert("priv_subject", null, values);
            } catch (SQLiteConstraintException e) {
                Log.e("SQLiteConstraintException: ", "cannot insert UNIQUE column");
            }
        }

        //핵심교양, 공통교양
        tc = total_subdb.rawQuery("select * from subjects where field in (?, ?, ?, ?, ?);", new String[]{"핵심교양 1영역", "핵심교양 2영역", "핵심교양 3영역", "핵심교양 4영역", "공통교양"});
        while (tc.moveToNext()) {
            ContentValues values = new ContentValues();
            values.put("major", tc.getString(tc.getColumnIndex("majorname")));
            values.put("field", tc.getString(tc.getColumnIndex("field")));
            values.put("subname", tc.getString(tc.getColumnIndex("subname")));
            values.put("subcode", tc.getString(tc.getColumnIndex("subcode")));
            values.put("credit", tc.getLong(tc.getColumnIndex("credit")));
            values.put("required", tc.getInt(tc.getColumnIndex("required")));
            values.put("star",tc.getFloat(tc.getColumnIndex("star")));

            try {
                priv_subdb.insert("priv_subject", null, values);
            } catch (SQLiteConstraintException e) {
                Log.e("SQLiteConstraintException: ", "cannot insert UNIQUE column");
            }
        }

        if (teaching == 1) {
            tc = total_subdb.rawQuery("select * from subjects where field = \"교직\" and majorname in (?, ?);", new String[]{"교육학과", "학사지원팀"});
            while (tc.moveToNext()) {
                if (tc.getString(tc.getColumnIndex("subname")).equals("교육학개론")) {
                    continue;
                }
                ContentValues values = new ContentValues();
                values.put("major", tc.getString(tc.getColumnIndex("majorname")));
                values.put("field", tc.getString(tc.getColumnIndex("field")));
                values.put("subname", tc.getString(tc.getColumnIndex("subname")));
                values.put("subcode", tc.getString(tc.getColumnIndex("subcode")));
                values.put("credit", tc.getLong(tc.getColumnIndex("credit")));
                values.put("required", tc.getInt(tc.getColumnIndex("required")));
                values.put("star",tc.getFloat(tc.getColumnIndex("star")));

                priv_subdb.insert("priv_subject", null, values);
            }
        }

        tc.close();
        priv_subdb.close();
        total_subdb.close();
        stud_infodb.close();
    }

    public void setSubjectFinished() {
        priv_subdb = phelper.getReadableDatabase();

        Log.e("external path : ", path.toString());

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                updateDB(path);
            } catch (Exception e) {
                Log.e("IOException Occur:", "cannot read file");
            }
        } else {
            Toast.makeText(this, "파일 읽기 권한 요청을 확인해야만 데이터를 읽어올 수 있습니다.", Toast.LENGTH_LONG).show();
        }
        priv_subdb.close();
    }

    private void updateDB(File path) throws IOException {
        total_subdb = tshelper.getReadableDatabase();
        priv_subdb = phelper.getWritableDatabase();
        finish_subdb = fhelper.getWritableDatabase();

        info = tfr.readDataFromFile(path); //성적이랑 학점 코드 다 있음

        Cursor c = priv_subdb.rawQuery("select * from priv_subject;", null);
        if (path.exists()) {
            tc = total_subdb.rawQuery("select * from subjects ",null);
            while (tc.moveToNext()) {
                for (String code : info.getSubNo()) {
                    if (tc.getString(tc.getColumnIndex("subcode")).equals(code)) {
                        ContentValues values = new ContentValues();
                        values.put("major", tc.getString(tc.getColumnIndex("majorname")));
                        values.put("field", tc.getString(tc.getColumnIndex("field")));
                        values.put("subname", tc.getString(tc.getColumnIndex("subname")));
                        values.put("subcode", tc.getString(tc.getColumnIndex("subcode")));
                        values.put("credit", tc.getLong(tc.getColumnIndex("credit")));
                        values.put("required", tc.getInt(tc.getColumnIndex("required")));
                        values.put("star",tc.getFloat(tc.getColumnIndex("star")));
                        try {
                            finish_subdb.insert("finished_subject", null, values);
                        } catch (SQLiteConstraintException e) {
                            Log.e("SQLiteConstraintException: ", "cannot insert UNIQUE column");
                        }
                    }
                }
            }

            tc = total_subdb.rawQuery("select * from subjects where field=\"일반교양\"",null);
            while (tc.moveToNext()) {
                for (String code : info.getSubNo()) {
                    if (tc.getString(tc.getColumnIndex("subcode")).equals(code)) {
                        ContentValues values = new ContentValues();
                        values.put("major", tc.getString(tc.getColumnIndex("majorname")));
                        values.put("field", tc.getString(tc.getColumnIndex("field")));
                        values.put("subname", tc.getString(tc.getColumnIndex("subname")));
                        values.put("subcode", tc.getString(tc.getColumnIndex("subcode")));
                        values.put("credit", tc.getLong(tc.getColumnIndex("credit")));
                        values.put("required", tc.getInt(tc.getColumnIndex("required")));
                        values.put("finished",1);
                        values.put("star",tc.getFloat(tc.getColumnIndex("star")));
                        try {
                            priv_subdb.insert("priv_subject", null, values);
                        } catch (SQLiteConstraintException e) {
                            Log.e("SQLiteConstraintException: ", "cannot insert UNIQUE column");
                        }
                        Log.e("IOException Occur:", "can");
                    }
                }
            }

            while (c.moveToNext()) {
                for (String code : info.getSubNo()) {
                    if (c.getString(c.getColumnIndex("subcode")).equals(code)) {
                        String sql = "update priv_subject set finished = 1 where subcode = \"" + code + "\";";
                        priv_subdb.execSQL(sql);
                    }
                }
            }
//            Toast.makeText(this, "데이터베이스 설정 완료", Toast.LENGTH_LONG).show();
            c.close();
        } else
            Toast.makeText(this, "포탈의 \"개인성적조회\"란에서 텍스트파일을 다운로드 받은 뒤 다시 실행해 주세요.", Toast.LENGTH_LONG).show();
    }

    public void setCredit() {
        // setting credit + set progressbar
        // setting databases
        total_subdb = tshelper.getReadableDatabase();
        priv_subdb = phelper.getWritableDatabase();
        stud_infodb = shelper.getReadableDatabase();
        finish_subdb = fhelper.getReadableDatabase();
        db_2014_2015 = openHelper2014.getReadableDatabase();
        db_2016 = openHelper2016.getReadableDatabase();
        db_2017 = openHelper2017.getReadableDatabase();
        db_2018 = openHelper2018.getReadableDatabase();
        db_2019 = openHelper2019.getReadableDatabase();

        //지금까지 들은 학점 계산
        int base_major = 0;
        int deepen_major=0;
        int double_base_major = 0; //복수 핵심전공
        int double_deepen_major=0; //복수 심화전공
        int minor_sub = 0; //부전공
        int teach_major = 0; //교직
        int culture_major = 0; //공교
        int culture_deepen = 0; //핵교
        int culture_gen = 0; //일교

        // 들었던 과목들 학점 "수" 정산, number of credit
        d_cursor = finish_subdb.rawQuery("select major, field, credit from finished_subject;", null);
        while (d_cursor.moveToNext()) {
            if (d_cursor.getString(d_cursor.getColumnIndex("major")).equals(major)) {
                if (d_cursor.getString(d_cursor.getColumnIndex("field")).equals("핵심전공")) {
                    base_major += d_cursor.getInt(d_cursor.getColumnIndex("credit"));
                }
                else if (d_cursor.getString(d_cursor.getColumnIndex("field")).equals("심화전공")) {
                    deepen_major += d_cursor.getInt(d_cursor.getColumnIndex("credit"));
                }
            }

            // 복수 전공
            if (d_cursor.getString(d_cursor.getColumnIndex("major")).equals(dmajor)) {
                if (d_cursor.getString(d_cursor.getColumnIndex("field")).equals("핵심전공")) {
                    double_base_major += d_cursor.getInt(d_cursor.getColumnIndex("credit"));
                }
                else if (d_cursor.getString(d_cursor.getColumnIndex("field")).equals("심화전공")) {
                    double_deepen_major += d_cursor.getInt(d_cursor.getColumnIndex("credit"));
                }
            }

            // 부전공
            if (d_cursor.getString(d_cursor.getColumnIndex("major")).equals(minor)) {
                minor_sub += d_cursor.getInt(d_cursor.getColumnIndex("credit"));
            }
            // 교직
            if (d_cursor.getString(d_cursor.getColumnIndex("field")).equals("교직")) teach_major += d_cursor.getInt(d_cursor.getColumnIndex("credit"));

            // 공통 교양
            if (d_cursor.getString(d_cursor.getColumnIndex("field")).equals("공통교양")) culture_major += d_cursor.getInt(d_cursor.getColumnIndex("credit"));

            // 일반 교양
            if (d_cursor.getString(d_cursor.getColumnIndex("field")).equals("일반교양")) culture_gen += d_cursor.getInt(d_cursor.getColumnIndex("credit"));

            // 핵심 교양
            if (d_cursor.getString(d_cursor.getColumnIndex("field")).contains("핵심교양")) culture_deepen += d_cursor.getInt(d_cursor.getColumnIndex("credit"));
        }

        // progressBar data preprocessing
        TextView base_major_credit = findViewById(R.id.base_major_credit);
        TextView deepen_major_credit = findViewById(R.id.deepen_major_credit);
        TextView double_base_major_credit = findViewById(R.id.d_base_major_credit);
        TextView double_deepen_major_credit = findViewById(R.id.d_deepen_major_credit);
        TextView minor_credit = findViewById(R.id.minor_credit);
        TextView teach_credit = findViewById(R.id.teach_credit);
        TextView culture_credit = findViewById(R.id.culture_credit); //공통교양
        TextView culture_gen_credit = findViewById(R.id.culture_gen_credit); // 일반교양
        TextView t9 = findViewById(R.id.t9); // 핵교 아이디 t9

        switch (std_no) {
            case "2014": case "2015":
                d_cursor = db_2014_2015.rawQuery("select * from requirement where univ = \"" + major + "\";",null);
                break;
            case "2016":
                d_cursor = db_2016.rawQuery("select * from requirement where univ = \"" + major + "\";",null);
                break;
            case "2017":
                d_cursor = db_2017.rawQuery("select * from requirement where univ = \"" + major + "\";",null);
                break;
            case "2018":
                d_cursor = db_2018.rawQuery("select * from requirement where univ = \"" + major + "\";",null);
                break;
            case "2019":
                d_cursor = db_2019.rawQuery("select * from requirement where univ = \"" + major + "\";",null);
                break;
            default:
                break;
        }
        d_cursor.moveToFirst();

        // text 표기를 위한 상수 field
        int major_req = d_cursor.getInt(d_cursor.getColumnIndex("major_req"));
        int major_adv = d_cursor.getInt(d_cursor.getColumnIndex("major_adv"));
        int advmajor_req = d_cursor.getInt(d_cursor.getColumnIndex("advmajor_req"));
        int advmajor_adv = d_cursor.getInt(d_cursor.getColumnIndex("advmajor_adv"));
        int total_major_req = major_req; //핵심전공
        int total_major_adv = major_adv;//심화전공
        int double_req = d_cursor.getInt(d_cursor.getColumnIndex("double_req")); //복수 심화
        int double_adv = d_cursor.getInt(d_cursor.getColumnIndex("double_adv")); //복수 심화
        int min_credit = d_cursor.getInt(d_cursor.getColumnIndex("minor"));//부전공
        int tch_credit = d_cursor.getInt(d_cursor.getColumnIndex("teaching"));
        int cult_credit = d_cursor.getInt(d_cursor.getColumnIndex("culture_common")); // 공교
        int req_cult_credit = d_cursor.getInt(d_cursor.getColumnIndex("culture_req")); // 핵교

        if (std_no.equals("2019")) {
            int culture_ref = d_cursor.getInt(d_cursor.getColumnIndex("culture_ref"));    // 진로소양
        }

        switch (major) {
            case "간호학과": case "글로벌의과학과":
                total_major_req = 102;
                base_major += deepen_major;
                break;
            case "스포즈레저학과": case "운동재활복지학과":
                total_major_req = 81;
                base_major += deepen_major;
                if (std_no.equals("2014") || std_no.equals("2015") || std_no.equals("2016")) {
                    double_req = 45;
                    double_base_major = double_req + double_adv;
                }
                break;
            case "성악과": case "기악과": case "작곡과":
                total_major_req = 88;
                base_major += deepen_major;
                break;
            case "문화예술경영학과": case "미디어영상연기학과": case "현대실용음악학과": case "무용예술학과":
            case "메이크업디자인학과":
                total_major_req = 84;
                base_major += deepen_major;
                break;
            default:
                break;
        }

        if (adv_major == 1) {
            total_major_req += advmajor_req;
            total_major_adv += advmajor_adv;
        }

        base_major_credit.setText(base_major + "/" + total_major_req); //핵심전공
        deepen_major_credit.setText(deepen_major + "/" + total_major_adv); //심화전공
        double_base_major_credit.setText(double_base_major + "/" + double_req); //복수 핵심
        double_deepen_major_credit.setText(double_deepen_major + "/" + double_adv); // 복수 심화
        minor_credit.setText(minor_sub + "/" + min_credit); //부전공
        teach_credit.setText( teach_major+ "/" + tch_credit);
        culture_credit.setText(culture_major + "/" + cult_credit); //공통교양
        culture_gen_credit.setText(culture_gen + "/" +"0"); //일반교양
        t9.setText(culture_deepen + "/" + req_cult_credit); //핵심교양

        int total_major_credit = base_major + deepen_major;
        int total_d_major_credit = double_base_major + double_deepen_major;

        ((TextView) findViewById(R.id.table_major)).setText(total_major_credit + ""); // 주전공 총학점
        ((TextView) findViewById(R.id.table_d_major)).setText(total_d_major_credit + "");   // 복수전공 총학점
        ((TextView) findViewById(R.id.table_minor)).setText(minor_sub + "");    // 부전공 총학점
        ((TextView) findViewById(R.id.table_c1)).setText(culture_major + "");   // 공통교양 총학점
        ((TextView) findViewById(R.id.table_c2)).setText(culture_deepen + ""); // 핵심교양 총학점
        ((TextView) findViewById(R.id.table_c3)).setText(culture_gen + ""); // 일반교양 총학점

        ProgressBar p1 = findViewById(R.id.p1);
        p1.setMax(total_major_req);
        p1.setProgress(base_major);

        ProgressBar p2 = findViewById(R.id.p2);
        p2.setMax(total_major_adv);
        p2.setProgress(deepen_major);

        //복수전공 핵심
        ProgressBar p3 = findViewById(R.id.p3); //39 수정해줘야함
        p3.setMax(double_req);
        p3.setProgress(double_base_major);

        //복수전공 심화
        ProgressBar p4 = findViewById(R.id.p4);
        p4.setMax(double_adv);
        p4.setProgress(double_deepen_major);

        //부전공
        if (!minor.equals("none")) {
            ProgressBar p5 = findViewById(R.id.p5);
            p5.setMax(min_credit);
            p5.setProgress(minor_sub);
        }
        else findViewById(R.id.minor_progress).setVisibility(View.GONE);

        //교직
        if (teaching == 1) {
            ProgressBar p6 = findViewById(R.id.p6);
            p6.setMax(teach_major);
            p6.setProgress(tch_credit);
        }
        else findViewById(R.id.teaching_progress).setVisibility(View.GONE);

        if (findViewById(R.id.minor_progress).getVisibility() == View.GONE && findViewById(R.id.teaching_progress).getVisibility() == View.GONE) {
            findViewById(R.id.minor_teaching).setVisibility(View.GONE);
        }

        //공통교양
        ProgressBar p7 = findViewById(R.id.p7);
        p7.setMax(cult_credit);
        p7.setProgress(culture_major);

        //일반교양
        ProgressBar p8 = findViewById(R.id.p8);
        p8.setMax(culture_gen);
        p8.setProgress(culture_gen);
    }
}