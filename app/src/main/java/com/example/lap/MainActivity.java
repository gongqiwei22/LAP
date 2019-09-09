package com.example.lap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    //显示组件
    private ImageButton previous;
    private ImageButton play;
    private ImageButton stop;
    private ImageButton next;
    private ListView list;

    private ArrayList<Music> musicArrayList;           //歌曲列表对象
    private int number = 0;                            //当前歌曲的序号，下标从0开始
    private MediaPlayer player = new MediaPlayer();    //媒体播放类

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        registerListeners();
        initMusicList();
        initListView();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            load(number);
        }

        checkMusicfile();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //主页导航按钮
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    //获取显示组件
    private void findViews() {
        previous = (ImageButton)findViewById(R.id.imageButton1);
        play = (ImageButton)findViewById(R.id.imageButton2);
        stop = (ImageButton)findViewById(R.id.imageButton3);
        next = (ImageButton)findViewById(R.id.imageButton4);
        list = (ListView)findViewById(R.id.listView1);
    }

    //为显示组件注册监听器
    private void registerListeners() {
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNumberToPrevious();
                play(number);
                previous.setBackgroundResource(R.mipmap.previous);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null && player.isPlaying()) {
                    pause();
                    play.setBackgroundResource(R.mipmap.play);
                } else {
                    play.setBackgroundResource(R.mipmap.stop);
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                play.setBackgroundResource(R.mipmap.stop);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNumberToNext();
                play(number);
                play.setBackgroundResource(R.mipmap.stop);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                number = position;
                play(number);
                play.setBackgroundResource(R.mipmap.stop);
            }
        });
    }
    //初始化音乐列表对象
    private void initMusicList() {
        musicArrayList = MusicList.getMusicList();
        //避免重复添加音乐
        if (musicArrayList.isEmpty()){
            Cursor mMusicCursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,
                    null, MediaStore.Audio.AudioColumns.TITLE);
            //标题
            int indexTitle = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
            //艺术家
            int indexArtist = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
            //总时长
            int indexTotalTime = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
            //路径
            int indexPath = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
            //通过mMusicCursor游标遍历数据库，并将Music类对象加载到ArrayList中
            for (mMusicCursor.moveToFirst();!mMusicCursor.isAfterLast();mMusicCursor.moveToNext()){
                String strTitle = mMusicCursor.getString(indexTitle);
                String strArtist = mMusicCursor.getString(indexArtist);
                String strTotalTime = mMusicCursor.getString(indexTotalTime);
                String strPath = mMusicCursor.getString(indexPath);
                if (strArtist.equals("<unknown>"))
                    strArtist = "无艺术家";
                Music music = new Music(strArtist,strPath,strTitle,strTotalTime);
                musicArrayList.add(music);
            }

        }
    }

    //设置适配器并初始化listview
    private void initListView() {
        List<Map<String,String>> list_map = new ArrayList<Map<String, String>>();
        HashMap<String,String> map;
        SimpleAdapter simpleAdapter;
        for (Music music:musicArrayList){
            map = new HashMap<String, String>();
            map.put("musicName",music.getMusicName());
            map.put("musicArtist",music.getMusicArtist());
            list_map.add(map);
        }
        String[] from = new String[]{"musicName","musicArtist"};
        int[] to = {R.id.listview_tv_title_item,R.id.listview_tv_artist_item};
        simpleAdapter = new SimpleAdapter(this,list_map,R.layout.listview,from,to);
        list.setAdapter(simpleAdapter);
    }

    //如果列表没有歌曲，则播放按钮不可用，并提醒用户
    private void checkMusicfile() {
        if (musicArrayList.isEmpty()){
            previous.setEnabled(false);
            play.setEnabled(false);
            stop.setEnabled(false);
            next.setEnabled(false);
            Toast.makeText(getApplicationContext(),"当前没有歌曲文件",Toast.LENGTH_SHORT).show();
        }else {
            previous.setEnabled(true);
            play.setEnabled(true);
            stop.setEnabled(true);
            next.setEnabled(true);
        }
    }

    //读取音乐文件
    private void load(int number){
        try {
            player.reset();
            player.setDataSource(MusicList.getMusicList().get(number).getMusicPath());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放音乐
    private void play(int number){
        if (player != null && player.isPlaying()){
            player.stop();
        }
        load(number);
        player.start();
    }
    //暂停音乐
    private void pause(){
        if (player.isPlaying()){
            player.pause();
        }
    }
    //停止播放
    private void stop(){
        player.stop();
    }
    //恢复播放（暂停之后)
    private void resume(){
        player.start();
    }
    //重新播放（播放完成之后)
    private void replay(){
        player.start();
    }

    //选择下一曲
    private void moveNumberToNext(){
        //判断是否达到了列表底端
        if ((number) == MusicList.getMusicList().size()-1){
            Toast.makeText(MainActivity.this,"已到底端",Toast.LENGTH_SHORT).show();
        }else {
            ++number;
            play(number);
        }
    }
    //选择上一曲
    private void moveNumberToPrevious(){
        //判断是否达到了列表顶端
        if ((number) == 0){
            Toast.makeText(MainActivity.this,"已到顶端",Toast.LENGTH_SHORT).show();
        }else {
            --number;
            play(number);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initMusicList();
                }else {
                    Toast.makeText(this,"你拒绝了这一权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
