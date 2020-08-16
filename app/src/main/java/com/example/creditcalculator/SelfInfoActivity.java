package com.example.creditcalculator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelfInfoActivity extends AppCompatActivity { //전화연결하는곳!! + 내정보 확인
    private String major_name;
    private String double_major_name;
    private String minor_name;
    private FinishedSubjectOpenHelper fhelper;
    private StudentOpenHelper helper;
    private SQLiteDatabase db;
    private SQLiteDatabase fdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);

        helper = new StudentOpenHelper(this);
        db = helper.getReadableDatabase();
        fhelper = new FinishedSubjectOpenHelper(this);
        fdb = fhelper.getReadableDatabase();

        Cursor c = db.rawQuery("select * from student;", null);
        c.moveToFirst();
        TextView mtv = findViewById(R.id.info_major_name); //주전공 이름
        major_name = c.getString(c.getColumnIndex("major"));
        mtv.setText(major_name);

        TextView dmtv = findViewById(R.id.info_dmajor_name);
        double_major_name = c.getString(c.getColumnIndex("dmajor"));
        if (!double_major_name.equals("none")) dmtv.setText(double_major_name);
        else dmtv.setText("없음");

        TextView mintv = findViewById(R.id.info_minor_name);
        minor_name = c.getString(c.getColumnIndex("minor"));
        if (!minor_name.equals("none")) mintv.setText(minor_name);
        else mintv.setText("없음");
        //없으면 아예안보이게 만들자!

        findViewById(R.id.m_dial).setOnClickListener(dialListener);
        findViewById(R.id.dm_dial).setOnClickListener(dialListener);
        findViewById(R.id.min_dial).setOnClickListener(dialListener);

        findViewById(R.id.m_dial2).setOnClickListener(dial2Listener);
        findViewById(R.id.dm_dial2).setOnClickListener(dial2Listener);
        findViewById(R.id.min_dial2).setOnClickListener(dial2Listener);

        Cursor tc = fdb.rawQuery("select credit from finished_subject;", null);
        int my_credit = 0;
        while (tc.moveToNext()) {
            my_credit +=  tc.getInt(tc.getColumnIndex("credit"));
        }

        TextView credit = findViewById(R.id.my_credit);
        credit.setText(my_credit + " / 130");

        findViewById(R.id.bnt_modify).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfInfoActivity.this, SelfInfoModifyActivity.class);
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
    View.OnClickListener dialListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setDials();
            String number = "0";
            switch (view.getId()) {
                case R.id.m_dial:
                    number = dials.get(major_name);
                    break;
                case R.id.dm_dial:
                    number = dials.get(double_major_name);
                    break;
                case R.id.min_dial:
                    number = dials.get(minor_name);
            }

            Uri uri= Uri.parse("tel:"+number); //전화와 관련된 Data는 'Tel:'으로 시작. 이후는 전화번호
            Intent i= new Intent(Intent.ACTION_DIAL,uri); //시스템 액티비티인 Dial Activity의 action값
            startActivity(i);//액티비티 실행
        }
    };

    View.OnClickListener dial2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setSite();
            String site1 = "0";
            switch (view.getId()) {
                case R.id.m_dial2:
                    Log.e("qweqwe", major_name);
                    site1 = site.get(major_name);
                    break;
                case R.id.dm_dial2:
                    site1 = site.get(double_major_name);
                    break;
                case R.id.min_dial2:
                    site1 = site.get(minor_name);
            }
            Log.e("qweqweqwe",site1);
            Uri uri= Uri.parse(site1); //전화와 관련된 Data는 'Tel:'으로 시작. 이후는 전화번호
            Intent i = new Intent(Intent.ACTION_VIEW, uri); //시스템 액티비티인 Dial Activity의 action값
            startActivity(i);//액티비티 실행
        }
    };

    private Map<String, String> dials = new HashMap<>();
    private Map<String, String> site = new HashMap<>();

    public  void setSite(){
        site.put("성신여자대학교", "https://www.sungshin.ac.kr/sites/main_kor/main.jsp");
        site.put("교직과", "https://www.sungshin.ac.kr/teaching/13385/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGdGVhY2hpbmclMkYzODA5JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("교양교육대학", "https://www.sungshin.ac.kr/generaledu/12626/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZ2VuZXJhbGVkdSUyRjM2NDAlMkZhcnRjbExpc3QuZG8lM0Y%3D");

        site.put("인문과학대학공지", "https://www.sungshin.ac.kr/humanity/11973/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaHVtYW5pdHklMkYzNDQ3JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("국어국문학과", "https://www.sungshin.ac.kr/korean/11161/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGa29yZWFuJTJGMzIxNSUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("영어영문학과", "https://www.sungshin.ac.kr/english/11191/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZW5nbGlzaCUyRjMyMjMlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("독어독문학과", "https://www.sungshin.ac.kr/german/11222/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZ2VybWFuJTJGMzIzMiUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("불어불문학과", "https://www.sungshin.ac.kr/france/11252/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZnJhbmNlJTJGMzI0MyUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("일어일문학과", "https://www.sungshin.ac.kr/japanese/11294/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGamFwYW5lc2UlMkYzMjU3JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("중어중문학과", "https://www.sungshin.ac.kr/chinese/11324/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY2hpbmVzZSUyRjMyNzAlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("사학과", "https://www.sungshin.ac.kr/history/11353/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaGlzdG9yeSUyRjMyNzglMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("사회과학대학공지", "https://www.sungshin.ac.kr/social/11377/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGc29jaWFsJTJGMzI4NyUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("정치외교학과", "https://www.sungshin.ac.kr/politics/11395/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGcG9saXRpY3MlMkYzMjkwJTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("심리학과", "https://www.sungshin.ac.kr/psy/11421/subview.do");
        site.put("지리학과", "https://www.sungshin.ac.kr/geographic/11450/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZ2VvZ3JhcGhpYyUyRjMzMDUlMkZhcnRjbExpc3QuZG8lM0Y%3D");

        site.put("경제학과", "https://www.sungshin.ac.kr/economic/11479/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZWNvbm9taWMlMkYzMzEzJTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("경영학과", "https://www.sungshin.ac.kr/business/11507/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGYnVzaW5lc3MlMkYzMzI0JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("미디어커뮤니케이션학과", "https://www.sungshin.ac.kr/mediacomm/11530/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbWVkaWFjb21tJTJGMzMzMCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("융합보안학과", "https://www.sungshin.ac.kr/cse/11786/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY3NlJTJGMzQwNCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("법학대학공지", "https://www.sungshin.ac.kr/lawdean/11555/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbGF3ZGVhbiUyRjMzMzglMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("법학과", "https://www.sungshin.ac.kr/law/11575/subview.do");

        site.put("자연과학대학공지", "https://www.sungshin.ac.kr/natscience/11625/subview.do");
        site.put("수학과", "https://www.sungshin.ac.kr/math/11644/subview.do");
        site.put("통계학과", "https://www.sungshin.ac.kr/statistics/11671/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGc3RhdGlzdGljcyUyRjMzNzUlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("생명과학·화학부", "https://www.sungshin.ac.kr/bio/11726/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGYmlvJTJGMzM5MCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("생명과학", "https://www.sungshin.ac.kr/bio/11726/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGYmlvJTJGMzM5MCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");

        site.put("지식서비스공과대학공지", "https://www.sungshin.ac.kr/eng/11759/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZW5nJTJGMzM5MyUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("IT학부", "https://www.sungshin.ac.kr/it/11698/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaXQlMkYzMzgzJTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("컴퓨터소프트웨어", "https://www.sungshin.ac.kr/ce/11806/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY2UlMkYzNDA5JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("정보미디어", "https://www.sungshin.ac.kr/infosys/11823/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaW5mb3N5cyUyRjM0MTMlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("청정융합과학과", "029207922");

        site.put("간호대학", "https://www.sungshin.ac.kr/nursing/12641/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbnVyc2luZyUyRjM2NDQlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("간호학과", "https://www.sungshin.ac.kr/nurse/12661/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbnVyc2UlMkYzNjU4JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("교육학과", "https://www.sungshin.ac.kr/education/12198/subview.do");
        site.put("사회교육과", "https://www.sungshin.ac.kr/edusociety/12226/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZWR1c29jaWV0eSUyRjM1MjglMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("윤리교육과", "https://www.sungshin.ac.kr/eduethics/12254/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZWR1ZXRoaWNzJTJGMzUzNyUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("한문교육과", "https://www.sungshin.ac.kr/educhinese/12282/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZWR1Y2hpbmVzZSUyRjM1NDUlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("유아교육과", "https://www.sungshin.ac.kr/edukids/12310/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZWR1a2lkcyUyRjM1NTMlMkZhcnRjbExpc3QuZG8lM0Y%3D");

        site.put("미술대학", "https://www.sungshin.ac.kr/midae/14140/subview.do");
        site.put("동양화과", "https://www.sungshin.ac.kr/orient/12352/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGb3JpZW50JTJGMzU2NCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("서양화과", "https://www.sungshin.ac.kr/western/12380/subview.do");
        site.put("조소과", "https://www.sungshin.ac.kr/carving/12408/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY2FydmluZyUyRjM1OTIlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("공예과", "https://www.sungshin.ac.kr/crafts/12438/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY3JhZnRzJTJGMzU5NSUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("산업디자인과", "https://www.sungshin.ac.kr/indusdesign/12475/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaW5kdXNkZXNpZ24lMkYzNjE2JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("음악대학", "https://www.sungshin.ac.kr/music/12510/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbXVzaWMlMkYzNjE3JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("성악과", "https://www.sungshin.ac.kr/music/12510/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbXVzaWMlMkYzNjE3JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("기악과", "https://www.sungshin.ac.kr/music/12510/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbXVzaWMlMkYzNjE3JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("작곡과", "https://www.sungshin.ac.kr/music/12510/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbXVzaWMlMkYzNjE3JTJGYXJ0Y2xMaXN0LmRvJTNG");

        site.put("융합문화예술대학", "https://www.sungshin.ac.kr/cvgarts/12687/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY3ZnYXJ0cyUyRjM2NTklMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("문화예술경영학과", "https://www.sungshin.ac.kr/cultureart/12707/subview.do");
        site.put("미디어영상연기학과", "https://www.sungshin.ac.kr/vmacting/12736/subview.do");
        site.put("현대실용음악학과", "https://www.sungshin.ac.kr/ctpmusic/12764/subview.do");
        site.put("무용예술학과", "https://www.sungshin.ac.kr/danceart/12784/subview.do");
        site.put("메이크업디자인학과", "https://www.sungshin.ac.kr/insbeauty/14011/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaW5zYmVhdXR5JTJGMzk3OCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");

        site.put("의류학과", "https://www.sungshin.ac.kr/clean/11841/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY2xlYW4lMkYzNDE4JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("식품영양학과", "https://www.sungshin.ac.kr/nutrition/12038/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbnV0cml0aW9uJTJGMzQ3MSUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("생활문화소비자학과", "https://www.sungshin.ac.kr/family/12137/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZmFtaWx5JTJGMzUwNiUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");

        site.put("생활과학대공지", " https://www.sungshin.ac.kr/haw/11903/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaGF3JTJGMzQzNCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("스포츠레저학과", "https://www.sungshin.ac.kr/sport/11923/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGc3BvcnQlMkYzNDQxJTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("운동재활복지학과", "https://www.sungshin.ac.kr/exercise/11948/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZXhlcmNpc2UlMkYzNDQ2JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("정보시스템", "https://www.sungshin.ac.kr/infosys/11823/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaW5mb3N5cyUyRjM0MTMlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("독일어문·문화학과", "https://www.sungshin.ac.kr/german/11222/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZ2VybWFuJTJGMzIzMiUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("프랑스어문·문화학과", "https://www.sungshin.ac.kr/france/11252/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZnJhbmNlJTJGMzI0MyUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("일본어문·문화학과", "https://www.sungshin.ac.kr/japanese/11294/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGamFwYW5lc2UlMkYzMjU3JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("중국어문·문화학과", "https://www.sungshin.ac.kr/chinese/11324/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY2hpbmVzZSUyRjMyNzAlMkZhcnRjbExpc3QuZG8lM0Y%3D");

        site.put("지식산업법학과", "https://www.sungshin.ac.kr/kilaw/11605/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGa2lsYXclMkYzMzUyJTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("화학과", "https://www.sungshin.ac.kr/chm/11745/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY2htJTJGMzM5MSUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("컴퓨터공학과", "https://www.sungshin.ac.kr/ce/11806/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY2UlMkYzNDA5JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("정보시스템공학과", "https://www.sungshin.ac.kr/infosys/11823/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaW5mb3N5cyUyRjM0MTMlMkZhcnRjbExpc3QuZG8lM0Y%3D");
        site.put("융합보안공학과", "https://www.sungshin.ac.kr/cse/11786/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY3NlJTJGMzQwNCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("서비스·디자인공학과", "https://www.sungshin.ac.kr/serdesign/11769/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGc2VyZGVzaWduJTJGMzM5NSUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("바이오식품공학과", "https://www.sungshin.ac.kr/bif/11855/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGYmlmJTJGMzQyMiUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("바이오생명공학과", "https://www.sungshin.ac.kr/bte/11878/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGYnRlJTJGMzQyMyUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("청정융합에너지공학과", "https://www.sungshin.ac.kr/clean/11841/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY2xlYW4lMkYzNDE4JTJGYXJ0Y2xMaXN0LmRvJTNG");
        site.put("의류산업학과", "https://www.sungshin.ac.kr/cloth/12107/subview.do");
        site.put("사회복지학과", "https://www.sungshin.ac.kr/welfare/12067/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGd2VsZmFyZSUyRjM0NzIlMkZhcnRjbExpc3QuZG8lM0Y%3D");

        site.put("뷰티산업학과", "https://www.sungshin.ac.kr/insbeauty/14011/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGaW5zYmVhdXR5JTJGMzk3OCUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("소비자생활문화산업학과", "https://www.sungshin.ac.kr/family/12137/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZmFtaWx5JTJGMzUwNiUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("글로벌비즈니스학과", "https://www.sungshin.ac.kr/globiz/12085/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZ2xvYml6JTJGMzQ4MiUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("글로벌의과학과", "https://www.sungshin.ac.kr/gms/12003/subview.do");
        site.put("뷰티 생활산업국제대학공지", "https://www.sungshin.ac.kr/lifeindustry/14084/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGbGlmZWluZHVzdHJ5JTJGNDAwMyUyRmFydGNsTGlzdC5kbyUzRg%3D%3D");
        site.put("사범대학", "https://www.sungshin.ac.kr/teacher/12167/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGdGVhY2hlciUyRjM1MDclMkZhcnRjbExpc3QuZG8lM0Y%3D");
    }
    public void setDials() {
        dials.put("국어국문학과", "029207069");
        dials.put("영여영문학과", "029207077");
        dials.put("독어독문학과", "029207084");
        dials.put("불어불문학과", "029207090");
        dials.put("일어일문학과", "029207096");
        dials.put("중어중문학과", "029207101");
        dials.put("사학과", "029207106");
        dials.put("정치외교학과", "029207127");
        dials.put("심리학과", "029207132");
        dials.put("지리학과", "029207137");

        dials.put("경제학과", "029207143");
        dials.put("경영학과", "029207148");
        dials.put("미디어커뮤니케이션학과", "029207805");
        dials.put("융합보안학과", "029207938");
        dials.put("법학과", "029207122");
        dials.put("수학과", "029207159");
        dials.put("통계학과", "029207180");
        dials.put("생명과학·화학부", "029207169");
        dials.put("생명과학", "029207169");

        dials.put("IT학부", "029207151");
        dials.put("컴퓨터소프트웨어", "029207151");
        dials.put("정보미디어", "029207151");
        dials.put("청정융합과학과", "029207922");

        dials.put("간호학과", " 029207720");
        dials.put("글로벌의과학과", "029207705");
        dials.put("교육학과", "029207720");
        dials.put("사회교육과", "029207221");
        dials.put("윤리교육과", "029207226");
        dials.put("한문교육과", "029207230");
        dials.put("유아교육과", "029207234");

        dials.put("미술대학", "029207241");
        dials.put("동양화과", "029207242");
        dials.put("서양화과", "029207248");
        dials.put("조소과", "029207259");
        dials.put("공예과", "029207259");
        dials.put("산업디자인과", "029207269");
        dials.put("성악과", "029207277");
        dials.put("기악과", "029207277");
        dials.put("작곡과", "029207277");

        dials.put("융합문화예술대학", "029207780");
        dials.put("문화예술경영학과", "029207820");
        dials.put("미디어영상연기학과", "029207825");
        dials.put("현대실용음악학과", "029207825");
        dials.put("무용예술학과", "029207835");
        dials.put("메이크업디자인학과", "029207845");
        dials.put("생활과학대학", "029207194");
        dials.put("의류학과", "029207195");
        dials.put("식품영양학과", "029207200");
        dials.put("생활문화소비자학과", "029207194");

        dials.put("사회복지학과", " 029207134");
        dials.put("스포츠레저학과", "029207835");
        dials.put("운동재활복지학과", "029207835");
        dials.put("인문과학대학", "029207068");
        dials.put("정보시스템", "029207151");
        dials.put("독일어문·문화학과", "029207084");
        dials.put("프랑스어문·문화학과", "029207090");
        dials.put("일본어문·문화학과", "029207096");
        dials.put("중국어문·문화학과", "029207101");
        dials.put("법과대학", "029207102");

        dials.put("지식산업법학과", "029207880");
        dials.put("화학과", "029207164");
        dials.put("컴퓨터공학과", "029207151");
        dials.put("정보시스템공학과", "029207151");
        dials.put("융합보안공학과", "029207938");
        dials.put("서비스·디자인공학과", "029207166");
        dials.put("바이오식품공학과", "029202697");
        dials.put("바이오생명공학과", "029207169");
        dials.put("청정융합에너지공학과", "029207101");
        dials.put("의류산업학과", "029207195");

        dials.put("뷰티산업학과", "029207845");
        dials.put("소비자생활문화산업학과", "029207194");
        dials.put("글로벌비즈니스학과", " 029207195");
        dials.put("뷰티 생활산업국제대학", "029202720");
        dials.put("사범대학", "029207533");
    }
}
