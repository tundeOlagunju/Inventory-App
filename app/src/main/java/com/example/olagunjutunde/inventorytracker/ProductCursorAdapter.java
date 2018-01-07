package com.example.olagunjutunde.inventorytracker;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olagunjutunde.inventorytracker.data.ProductContract;

import java.util.zip.Inflater;

import static com.example.olagunjutunde.inventorytracker.EditorActivity.calculateInSampleSize;
import static com.example.olagunjutunde.inventorytracker.R.id.price;

/**
 * Created by OLAGUNJU TUNDE on 12/25/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {



    public ProductCursorAdapter(Context context,Cursor c){
        super(context,c,0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      return LayoutInflater.from(context).inflate(R.layout.grid_item,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {


       final int id = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));

        int nameIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
      int priceIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int imageIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);


        String name = cursor.getString(nameIndex);
        final int quantity = cursor.getInt(quantityIndex);
        float price = cursor.getFloat(priceIndex);
       byte[] image = cursor.getBlob(imageIndex);



        TextView nameView = (TextView)view.findViewById(R.id.name);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);ImageView gridImageView = (ImageView) view.findViewById(R.id.grid_image);

        nameView.setText(name);
        priceView.setText(String.valueOf(price));
        quantityView.setText(String.valueOf(quantity));


        //Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        //gridImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,
              //  200, false));



        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds=true;
        BitmapFactory.decodeByteArray(image, 0, image.length,opt);
        opt.inSampleSize = calculateInSampleSize(opt,200,200);
        opt.inJustDecodeBounds=false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length,opt);
        gridImageView.setImageBitmap(bitmap);


      ImageButton saleButton = (ImageButton) view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quantity == 0){
                    Toast.makeText(context,"Product is out of stock.Order more!!",Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues contentValues = new ContentValues();

                ContentResolver contentResolver = context.getContentResolver();

                if(quantity > 0){

                    int currentQuantity = quantity;
                    currentQuantity = currentQuantity -1;

                    Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,id);

                    contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,currentQuantity);

                     contentResolver.update(currentProductUri,contentValues,null,null);
                    //notify all listeners(catalog and editor activity to update their UI)
                  //  contentResolver.notifyChange(currentProductUri,null);


                }
            }
        });





    }
    private int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;	//Default subsampling size
        // See if image raw height and width is bigger than that of required view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            //bigger
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


}
