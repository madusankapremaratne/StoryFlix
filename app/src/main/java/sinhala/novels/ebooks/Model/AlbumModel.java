package sinhala.novels.ebooks.Model;


public class AlbumModel {

    private String albumName;
    private double albumID,categoryID;
    private String coverURL;
    private double viewCount;
    private String tagline,previewText;
    private double epiCount,createdDate;
    private String authorName;
    public AlbumModel(String albumName, double albumID, double categoryID, String coverURL, double viewCount, String tagline, String previewText, double epiCount, double createdDate, String authorName) {
        this.albumName = albumName;
        this.albumID = albumID;
        this.categoryID = categoryID;
        this.coverURL = coverURL;
        this.viewCount = viewCount;
        this.tagline = tagline;
        this.previewText = previewText;
        this.epiCount = epiCount;
        this.createdDate = createdDate;
        this.authorName = authorName;
    }

    public AlbumModel(String albumName, double albumID, double categoryID, String coverURL, double viewCount, String tagline, String previewText, double epiCount, String authorName) {
        this.albumName = albumName;
        this.albumID = albumID;
        this.categoryID = categoryID;
        this.coverURL = coverURL;
        this.viewCount = viewCount;
        this.tagline = tagline;
        this.previewText = previewText;
        this.epiCount = epiCount;
        this.authorName = authorName;
    }

    public AlbumModel() {
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public double getAlbumID() {
        return albumID;
    }

    public void setAlbumID(double albumID) {
        this.albumID = albumID;
    }

    public double getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(double categoryID) {
        this.categoryID = categoryID;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public double getViewCount() {
        return viewCount;
    }

    public void setViewCount(double viewCount) {
        this.viewCount = viewCount;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText = previewText;
    }

    public double getEpiCount() {
        return epiCount;
    }

    public void setEpiCount(double epiCount) {
        this.epiCount = epiCount;
    }

    public double getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(double createdDate) {
        this.createdDate = createdDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
