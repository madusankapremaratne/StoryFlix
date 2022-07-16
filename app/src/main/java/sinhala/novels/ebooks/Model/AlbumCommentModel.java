package sinhala.novels.ebooks.Model;

public class AlbumCommentModel {

    String ID,comment,reply,albumName,coverURL;
    double albumID;
    String userName,userID,profileURL;
    String date,replyDate;

    public AlbumCommentModel(String ID, String comment, String reply, String albumName, String coverURL, double albumID, String userName, String userID, String profileURL, String date, String replyDate) {
        this.ID = ID;
        this.comment = comment;
        this.reply = reply;
        this.albumName = albumName;
        this.coverURL = coverURL;
        this.albumID = albumID;
        this.userName = userName;
        this.userID = userID;
        this.profileURL = profileURL;
        this.date = date;
        this.replyDate = replyDate;
    }

    public AlbumCommentModel(String ID,String userID,String comment, String reply, String userName, String profileURL, String date, String replyDate) {
        this.comment = comment;
        this.ID=ID;
        this.userID=userID;
        this.reply = reply;
        this.userName = userName;
        this.profileURL = profileURL;
        this.date = date;
        this.replyDate = replyDate;
    }

    public AlbumCommentModel() {
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

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public double getAlbumID() {
        return albumID;
    }

    public void setAlbumID(double albumID) {
        this.albumID = albumID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
