package com.example.lap;

/**
 * Created by 123 on 2019/9/4.
 */

public class Music {
    private String musicName;    //歌曲名
    private String musicArtist;  //艺术家
    private String musicPath;    //路径
    private String musicDuration;//时长

    public Music(String musicArtist,String musicDuration,String musicName,String musicPath){
        this.musicArtist = musicArtist;
        this.musicDuration = musicDuration;
        this.musicName = musicName;
        this.musicPath = musicPath;
    }
    public String getMusicName(){
        return this.musicName;
    }
    public String getMusicArtist(){
        return this.musicArtist;
    }
    public String getMusicPath(){
        return this.musicPath;
    }
    public String getMusicDuration(){
        return this.musicDuration;
    }
}
