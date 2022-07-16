package sinhala.novels.ebooks.LocalDatabase;

public class LocalFavModel {

    double ID,albumID;
    String albumName,imageURL,authorName;

    public LocalFavModel(double ID, double albumID, String albumName, String imageURL, String authorName) {
        this.ID = ID;
        this.albumID = albumID;
        this.albumName = albumName;
        this.imageURL = imageURL;
        this.authorName = authorName;
    }

    public LocalFavModel() {
    }

    public double getID() {
        return ID;
    }

    public void setID(double ID) {
        this.ID = ID;
    }

    public double getAlbumID() {
        return albumID;
    }

    public void setAlbumID(double albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
