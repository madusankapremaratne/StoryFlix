package sinhala.novels.ebooks.Model;

import java.util.ArrayList;

public class MainModel {

    String name;
    int id;
    ArrayList<AlbumModel> arrayList;
    int timeRefreshed=0;

    public MainModel(String name, int id, ArrayList<AlbumModel> arrayList, int timeRefreshed) {
        this.name = name;
        this.id = id;
        this.arrayList = arrayList;
        this.timeRefreshed = timeRefreshed;
    }

    public int getTimeRefreshed() {
        return timeRefreshed;
    }

    public void setTimeRefreshed(int timeRefreshed) {
        this.timeRefreshed = timeRefreshed;
    }

    public MainModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public MainModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<AlbumModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<AlbumModel> arrayList) {
        this.arrayList = arrayList;
    }
}
