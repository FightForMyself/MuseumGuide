package com.example.museumguide;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout mDrawerLayout;
    private TextView navText;
    private ImageView my_scan;

    private Relic[] relics={new Relic("石犀",R.drawable.shixi),new Relic("青铜器",
    R.drawable.qingtong),new Relic("石器",R.drawable.shiqi),new Relic("兵马俑",
    R.drawable.bmy)};

    private List<Relic> relicList=new ArrayList<>();
    private RelicAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置没有标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navText=(TextView)findViewById(R.id.nav_text);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView =(NavigationView) findViewById(R.id.nav_view);
        my_scan=(ImageView)findViewById(R.id.my_scan);//扫码
        my_scan.setOnClickListener(this);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
            //设置导航按钮图标
        }
        navView.setCheckedItem(R.id.nav_museum);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
               int id=item.getItemId();
               String string=null;
               switch (id){
                   case R.id.nav_museum:
                       string="场馆简介";
                       break;
                   case R.id.nav_guide:
                       string="参观指南";
                       break;
                   case R.id.nav_map:
                       startActivity(new Intent(MainActivity.this,LbsActivity.class));
                       string="地理信息";
                       break;
                   case R.id.nav_setting:
                       string="设置";
                       break;
               }
//               if(!TextUtils.isEmpty(string))
//                   navText.setText("你点击了"+string);
                mDrawerLayout.closeDrawers();
                return  true;
            }
        });
        initRelics();
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new RelicAdapter(relicList);
        recyclerView.setAdapter(adapter);
    }
//循环输出四个文物的图片
    private void initRelics() {
        relicList.clear();
        for(int i=0;i<50;i++)
        {
            Random random=new Random();
            int index=random.nextInt(relics.length);
            relicList.add(relics[index]);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item)//对HomeAsUp按钮的点击事件进行处理
    {
        switch (item.getItemId()){
            case android.R.id.home://HomeAsUp按钮的id永远是android.R.id.home
                mDrawerLayout.openDrawer(GravityCompat.START);//显示滑动菜单
                break;
            case R.id.search_bar:
                Toast.makeText(this,"你点击了搜索",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return  true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.my_scan://扫码点击事件
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
                }else {
                    startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class),0);
                }
                Intent openCameraIntent = new Intent(this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
        }
    }
//扫码结果处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            //resultTextView.setText(scanResult);
        }
    }
}
