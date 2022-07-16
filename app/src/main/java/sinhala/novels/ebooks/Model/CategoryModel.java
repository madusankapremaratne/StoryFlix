package sinhala.novels.ebooks.Model;

import java.util.ArrayList;

public class CategoryModel  {

    String name;
    double categoryID;
    ArrayList<AlbumModel> arrayList;

    public CategoryModel(String name, double categoryID) {
        this.name = name;
        this.categoryID = categoryID;
    }

    public CategoryModel(String name, double categoryID, ArrayList<AlbumModel> arrayList) {
        this.name = name;
        this.categoryID = categoryID;
        this.arrayList = arrayList;
    }

    public CategoryModel() {
    }

    public ArrayList<AlbumModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<AlbumModel> arrayList) {
        this.arrayList = arrayList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(double categoryID) {
        this.categoryID = categoryID;
    }
}
