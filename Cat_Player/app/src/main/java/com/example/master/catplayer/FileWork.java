package com.example.master.catplayer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by master on 06.07.2016.
 */
public class FileWork {
    String sd_Path;
    private ArrayList<String> songs = new ArrayList<String>();

    public FileWork(String sd_Path, ArrayList<String> songs) {
        this.sd_Path = sd_Path;
        this.songs = songs;
    }

    public void updatePlayList() {
        File file = new File(sd_Path);
        if (file.listFiles(new Mp3Filter()).length > 0) {
            addSongToList(file);
        } else {
            scanDirectory(file);
        }
    }

    private void addSongToList(File home) {
        if (home.listFiles(new Mp3Filter()).length > 0) {
            for (File file : home.listFiles(new Mp3Filter())) {
                songs.add(file.getName());
            }
        }
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else if (file.getName().endsWith(".mp3")) {
                        songs.add(file.getName());
                    }
                }
            }
        }
    }
}
