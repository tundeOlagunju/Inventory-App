package com.example.olagunjutunde.inventorytracker.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by OLAGUNJU TUNDE on 12/23/2017.
 */

public class ProductContract {

public static final String CONTENT_AUTHORITY ="com.example.olagunjutunde.inventorytracker";

   public static final Uri BASE_CONTENT_URI= Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS ="products";


private ProductContract(){}

    public static  abstract class ProductEntry implements  BaseColumns{



        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCTS);



        public static final String TABLE_NAME="products";




        public static final String _ID = BaseColumns._ID;


        public static final String COLUMN_PRODUCT_NAME= "product_name";
        public static final String COLUMN_PRODUCT_BRAND ="brand";
        public static final String COLUMN_PRODUCT_QUANTITY ="quantity";
        public static final String COLUMN_PRODUCT_COLOUR ="colour";
        public static final String COLUMN_PRODUCT_PRICE ="price";
        public static final String COLUMN_SUPPLIER_NAME ="supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE ="phone";
        public static final String COLUMN_SUPPLIER_EMAIL ="email";
        public static final String COLUMN_PRODUCT_IMAGE ="product_image";
        public static final String COLUMN_RESTOCK_QUANTITY="restock_quantity";



    }
}
