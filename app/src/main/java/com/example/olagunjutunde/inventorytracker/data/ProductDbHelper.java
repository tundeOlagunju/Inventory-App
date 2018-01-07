package com.example.olagunjutunde.inventorytracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by OLAGUNJU TUNDE on 12/23/2017.
 */

/** This class helps to create the database in sQLite
 * Helps to create open and manage database connection
 * **/
public class ProductDbHelper extends SQLiteOpenHelper {

    /** Name of the database file**/
    public static final String DATA_BASE_NAME = "inventory.db" ;

    /**
     * Database version.
     */
    public static final int DATA_BASE_VERSION = 1;


    public ProductDbHelper(Context context){

        super(context,DATA_BASE_NAME,null,DATA_BASE_VERSION);

    }



    /**
     * This is called when the database is created for the first time.
     */

    /**
     *
     * CREATE TABLE products (_id INTEGER PRIMARY KEY AUTOINCREMENT,
     *                          product_name TEXT NOT NULL,
     *                          brand TEXT,
     *                          colour TEXT,
     *                          price REAL NOT NULL,
     *                          supplier_name TEXT NOT NULL,
     *                          phone TEXT NOT NULL,
     *                          product_image BLOB,
     *                          email TEXT NOT NULL
     *                          );
     */


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


  String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + "("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_BRAND + " TEXT,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT 0,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_COLOUR + " TEXT,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT ,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE+ " BLOB ,"
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE + " TEXT ,"
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL + " TEXT, "
               + ProductContract.ProductEntry.COLUMN_RESTOCK_QUANTITY + " INTEGER DEFAULT 0 );";

sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
