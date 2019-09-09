package com.example.lap;

import java.util.ArrayList;

/**
 * Created by 123 on 2019/9/4.
 */

public class MusicList {
    private static ArrayList<Music> musiccarray = new ArrayList<Music>();
    private MusicList(){}

    public static ArrayList<Music> getMusicList(){
        return musiccarray;
    }
}
