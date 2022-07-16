package sinhala.novels.ebooks.Model;

public class EpiCommentModel {

    String id,comment,reply,albumName,coverURL;
    double albumID,epiID;
    String userName,userID,profileURL,date,replyDate;

    public EpiCommentModel(String id,String userID,String comment, String reply, String userName, String profileURL, String date, String replyDate) {
        this.comment = comment;
        this.id=id;
        this.userID=userID;
        this.reply = reply;
        this.userName = userName;
        this.profileURL = profileURL;
        this.date = date;
        this.replyDate = replyDate;
    }

    public EpiCommentModel(String id, String comment, String reply, String albumName, String coverURL, double albumID, double epiID, String userName, String userID, String profileURL, String date, String replyDate) {
        this.id = id;
        this.comment = comment;
        this.reply = reply;
        this.albumName = albumName;
        this.coverURL = coverURL;
        this.albumID = albumID;
        this.epiID = epiID;
        this.userName = userName;
        this.userID = userID;
        this.profileURL = profileURL;
        this.date = date;
        this.replyDate = replyDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public double getAlbumID() {
        return albumID;
    }

    public void setAlbumID(double albumID) {
        this.albumID = albumID;
    }

    public double getEpiID() {
        return epiID;
    }

    public void setEpiID(double epiID) {
        this.epiID = epiID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }
}
