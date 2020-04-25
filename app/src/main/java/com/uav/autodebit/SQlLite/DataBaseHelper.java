package com.uav.autodebit.SQlLite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.IOException;

public class DataBaseHelper extends SQLiteOpenHelper {
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.uav.autodebit/databases/";

    private static String DB_NAME = "Autopedb";



    public static SQLiteDatabase myDataBase;

    private final Context myContext;

    private static int version=1;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, version);
        this.myContext = context;
    }


    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        SQLiteDatabase db_Read = null;

        if(dbExist){
            //do nothing - database already exist
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            db_Read = this.getReadableDatabase();
            db_Read.close();

        }
    }


    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
     public static boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch(SQLiteException e){
            //database does't exist yet.
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }


    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

        //create a notification table
        myDataBase.execSQL("create table if not exists notification (ID INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT,Message TEXT,ImageUrl TEXT,TimeStamp TEXT,SmallImage TEXT,ActivityName TEXT,MoveActivity TEXT)");
        myDataBase.execSQL("create table if not exists status (StatusId INTEGER PRIMARY KEY AUTOINCREMENT, StatusName TEXT)");
        myDataBase.execSQL("create table if not exists oxigentransaction (TypeId INTEGER PRIMARY KEY AUTOINCREMENT, CustomerId INTEGER  NOT NULL,ServiceTypeId INTEGER  NOT NULL,Amount TEXT  NOT NULL , OperatorName TEXT NOT NULL)");
        myDataBase.execSQL("create table if not exists oxigenbillerfilter (TypeFilterId INTEGER PRIMARY KEY AUTOINCREMENT, FilterKey TEXT NOT NULL , FilterValue TEXT NOT NULL ,OxigenTxnId INTEGER,FOREIGN KEY (OxigenTxnId) REFERENCES oxigentransaction(TypeId))");

    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();

        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
