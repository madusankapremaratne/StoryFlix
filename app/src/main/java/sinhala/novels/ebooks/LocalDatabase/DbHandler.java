package sinhala.novels.ebooks.LocalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DbHandler extends SQLiteOpenHelper {

    public static final int VERSION=1;
    public static final String DB_NAME="StoryFlixDatabase";

    //Table Names
    public static final String FAVORITE_TABLE="Favorites";
    public static final String VIEW_COUNT_TABLE="ViewCount";
    public static final String COUNTED_TABLE="Counted";
    public static final String BOOKMARK_TABLE="Bookmarks";


    public static final String ALBUM_ID="AlbumID";
    public static final String EPISODE_ID="EpisodeID";
    public static final String ALBUM_NAME="AlbumName";
    public static final String IMAGE_URL="ImageURL";
    public static final String AUTHOR_NAME="AuthorName";
    public static final String ID="ID";

    public DbHandler(@Nullable Context context) {
        super(context, DB_NAME,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Categories
        db.execSQL("CREATE TABLE "+FAVORITE_TABLE+" ("+ID+" DECIMAL PRIMARY KEY,"+ALBUM_ID+" DECIMAL, "+ALBUM_NAME+" TEXT,"+IMAGE_URL+" TEXT, "+AUTHOR_NAME+" TEXT);");
        db.execSQL("CREATE TABLE "+VIEW_COUNT_TABLE+" ("+EPISODE_ID+" DECIMAL,"+ALBUM_ID+" DECIMAL);");
        db.execSQL("CREATE TABLE "+BOOKMARK_TABLE+" ("+EPISODE_ID+" DECIMAL PRIMARY KEY ,"+ALBUM_ID+" DECIMAL);");
        db.execSQL("CREATE TABLE "+COUNTED_TABLE+" ("+ALBUM_ID+" DECIMAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void setFavorite(LocalFavModel model){

        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(ID,model.getID());
        contentValues.put(ALBUM_ID,model.getAlbumID());
        contentValues.put(ALBUM_NAME,model.getAlbumName());
        contentValues.put(IMAGE_URL,model.getImageURL());
        contentValues.put(AUTHOR_NAME,model.getAuthorName());

        sqLiteDatabase.insert(FAVORITE_TABLE,null,contentValues);
        sqLiteDatabase.close();

    }

    public void setBookmark(double epiID,double albumID){

        SQLiteDatabase database=getWritableDatabase();
        database.delete(BOOKMARK_TABLE,ALBUM_ID+" =?",new String[]{String.valueOf(albumID)});
        database.close();


        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(EPISODE_ID,epiID);
        contentValues.put(ALBUM_ID,albumID);

        sqLiteDatabase.insert(BOOKMARK_TABLE,null,contentValues);
        sqLiteDatabase.close();

    }

    public void deleteBookmark(double albumID){

        SQLiteDatabase database=getWritableDatabase();
        database.delete(BOOKMARK_TABLE,ALBUM_ID+" =?",new String[]{String.valueOf(albumID)});
        database.close();

    }

    public void setNewCounted(double albumID){

        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ALBUM_ID,albumID);
        sqLiteDatabase.insert(COUNTED_TABLE,null,contentValues);
        sqLiteDatabase.close();

        SQLiteDatabase deleteD=getWritableDatabase();
        deleteD.delete(VIEW_COUNT_TABLE,ALBUM_ID+" =?",new String[]{String.valueOf(albumID)});
        deleteD.close();

    }

    public double isFavorite(double albumID){

        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String Query="SELECT * FROM "+FAVORITE_TABLE+" WHERE "+ALBUM_ID+" = "+albumID;
        Cursor cursor=sqLiteDatabase.rawQuery(Query,null);
        double id=0;
        while (cursor.moveToNext()){
            id=cursor.getDouble(0);
        }
        cursor.close();
        sqLiteDatabase.close();
        return id;

    }

    public boolean isBookmarked(double epiID){

        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String Query="SELECT * FROM "+BOOKMARK_TABLE+" WHERE "+EPISODE_ID+" = "+epiID;
        Cursor cursor=sqLiteDatabase.rawQuery(Query,null);
        boolean b=false;
        if (cursor.getCount()>0){
            b=true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return b;

    }

    public ArrayList<LocalFavModel> getFavoriteItems(){

        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        ArrayList<LocalFavModel> categoryDataHolder=new ArrayList<>();
        String Query="SELECT * FROM "+FAVORITE_TABLE+" ORDER BY "+ID+" DESC";
        Cursor cursor=sqLiteDatabase.rawQuery(Query,null);
        while (cursor.moveToNext()){
            LocalFavModel model=new LocalFavModel(cursor.getDouble(0),cursor.getDouble(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
            categoryDataHolder.add(model);
        }
        cursor.close();
        sqLiteDatabase.close();
        return categoryDataHolder;

    }

    public void deleteFavorite(double albumID){

        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        sqLiteDatabase.delete(FAVORITE_TABLE,ALBUM_ID+" =?",new String[]{String.valueOf(albumID)});
        sqLiteDatabase.close();

    }

    public boolean isCounted(double albumID) {
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String Query="SELECT * FROM "+COUNTED_TABLE+" WHERE "+ALBUM_ID+" = "+albumID;
        Cursor cursor=sqLiteDatabase.rawQuery(Query,null);
        boolean b=false;
        if (cursor.getCount()>0){
            b=true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return b;
    }

    public void setNewViewCount(double epiID,double albumID){

        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String query="SELECT * FROM "+VIEW_COUNT_TABLE+" WHERE "+EPISODE_ID+" = "+epiID;
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);
        if (cursor.getCount()==0){

            SQLiteDatabase writableDb=getWritableDatabase();
            ContentValues contentValues=new ContentValues();
            contentValues.put(ALBUM_ID,epiID);
            contentValues.put(ALBUM_ID,albumID);
            writableDb.insert(VIEW_COUNT_TABLE,null,contentValues);
            writableDb.close();


        }

        cursor.close();
        sqLiteDatabase.close();

    }

    public double getReadCount(double albumID) {
        double i=0;
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String query="SELECT * FROM "+VIEW_COUNT_TABLE+" WHERE "+ALBUM_ID+" = "+albumID;
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);
        i=cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();
        return i;
    }

}
