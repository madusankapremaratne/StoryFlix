package sinhala.novels.ebooks.Model;

public class EpiModel {

    private double epiID,albumID;
    private String content;
    private double createdDate;
    private String title;

    public EpiModel(double epiID, double albumID, String content, double createdDate, String title) {
        this.epiID = epiID;
        this.albumID = albumID;
        this.content = content;
        this.createdDate = createdDate;
        this.title = title;
    }

    public EpiModel() {
    }

    public double getEpiID() {
        return epiID;
    }

    public void setEpiID(double epiID) {
        this.epiID = epiID;
    }

    public double getAlbumID() {
        return albumID;
    }

    public void setAlbumID(double albumID) {
        this.albumID = albumID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(double createdDate) {
        this.createdDate = createdDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
