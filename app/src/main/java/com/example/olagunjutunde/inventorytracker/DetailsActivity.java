package com.example.olagunjutunde.inventorytracker;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olagunjutunde.inventorytracker.data.ProductContract;

import org.w3c.dom.Text;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.olagunjutunde.inventorytracker.EditorActivity.calculateInSampleSize;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    private Uri mCurrentProductUri;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    private ImageView detailsImage;
    private TextView  detailsPrice;
    private TextView  detailsBrand;
    private TextView  detailsColour;
    private  TextView detailsQuantity;
    private  TextView detailsName;
    private  TextView detailsPhone;
    private  TextView detailsEmail;
    private ImageButton    detailsMailButton;
    private ImageButton detailsPhoneButton;
    private ImageButton detailsMsgButton;
    private TextView restockQuantityView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);



        toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mCurrentProductUri = getIntent().getData();
        if(mCurrentProductUri != null) {

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(1234, null, this);
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        detailsImage = (ImageView) findViewById(R.id.detail_image);
        detailsBrand =(TextView) findViewById(R.id.details_brand);
        detailsColour =(TextView) findViewById(R.id.details_colour);
        detailsPrice = (TextView) findViewById(R.id.details_price);
        detailsQuantity =(TextView) findViewById(R.id.details_quantity);
        detailsName =(TextView) findViewById(R.id.details_supplier_name);
        detailsPhone =(TextView) findViewById(R.id.details_phone);
        detailsEmail =(TextView) findViewById(R.id.details_email);
        detailsMailButton =(ImageButton) findViewById(R.id.button_mail_details);
        detailsMsgButton=(ImageButton) findViewById(R.id.button_msg_details);
        detailsPhoneButton = (ImageButton) findViewById(R.id.button_phone_details);
        restockQuantityView = (TextView) findViewById(R.id.details_restock);


        detailsMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        detailsPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        detailsMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextMessage();
            }
        });




    }



    private void sendMail(){

       String  productNameString = collapsingToolbarLayout.getTitle().toString();
        String supplierMailString = detailsEmail.getText().toString().trim();
        String supplierNameString =   detailsName.getText().toString().trim();
        String restockQuantityString    = restockQuantityView.getText().toString().trim();

        int restockQuantityInt = Integer.parseInt(restockQuantityString);

        if(supplierMailString.equals("None")) {
            Toast.makeText(getBaseContext(), "Make sure the supplier's email is entered", Toast.LENGTH_LONG).show();
            return;

        }


        if(supplierNameString.equals("None")) {
            Toast.makeText(getBaseContext(), "Make sure the supplier's name is entered", Toast.LENGTH_LONG).show();
            return;

        }




        if (restockQuantityInt == 0) {

            Toast.makeText(getBaseContext(), "Restock quantity cannot be zero.Please Make sure a valid restock " +
                    "quantity is entered ", Toast.LENGTH_LONG).show();
            return;
        }
        else if(restockQuantityInt < 0){
            Toast.makeText(getBaseContext(), "Restock quantity cannot be negative .Please Make sure a valid restock" +
                    "quantity is entered  ", Toast.LENGTH_LONG).show();
            return;
        }


        // Create restock e-mail subject
        String restockSubject = "Ordering" + " " + productNameString;

        // Create restock message to send as E-mail message
        String restockMessage = "Hello" + " " + supplierNameString +  "," +"\n" +
                "I would like to order" + " " +
                restockQuantityString + " " +
                "pieces of " + " " +
                productNameString;


        // Create an e-mail intent to send an e-mail to the supplier
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + supplierMailString));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, restockSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, restockMessage);

        // Start the e-mail intent
        startActivity(Intent.createChooser(emailIntent, "Send Email"));

    }






    private  void makePhoneCall() {

        String supplierPhoneString = detailsPhone.getText().toString().trim();

        if (supplierPhoneString.equals("None")) {

            Toast.makeText(getBaseContext(),"Make sure the supplier's phone number is entered",Toast.LENGTH_LONG).show();
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + supplierPhoneString));




        if (ActivityCompat.checkSelfPermission(DetailsActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);

    }



    private void sendTextMessage(){



        String  productNameString = collapsingToolbarLayout.getTitle().toString();
        String supplierPhoneString = detailsPhone.getText().toString().trim();
        String supplierNameString =   detailsName.getText().toString().trim();
        String restockQuantityString    = restockQuantityView.getText().toString().trim();


        int restockQuantityInt = Integer.parseInt(restockQuantityString);




        if (supplierPhoneString.equals("None")) {

            Toast.makeText(getBaseContext(),"Make sure the supplier's phone number is entered",Toast.LENGTH_LONG).show();
            return;
        }


        if(supplierNameString.equals("None")) {
            Toast.makeText(getBaseContext(), "Make sure the supplier's name is entered", Toast.LENGTH_LONG).show();
            return;

        }




        if (restockQuantityInt == 0) {

            Toast.makeText(getBaseContext(), "Restock quantity cannot be zero.Please Make sure a valid restock " +
                    " quantity is entered ", Toast.LENGTH_LONG).show();
            return;
        }
        else if(restockQuantityInt < 0){
            Toast.makeText(getBaseContext(), "Restock quantity cannot be negative .Please Make sure a valid restock" +
                    " quantity is entered  ", Toast.LENGTH_LONG).show();
            return;
        }


        // Create restock message to send as E-mail message
        String restockMessage = "Hello" + " " + supplierNameString +  "," +"\n" +
                "I would like to order" + " " +
                restockQuantityString + " " +
                "pieces of " + " " +
                productNameString;


        // Create an e-mail intent to send an e-mail to the supplier
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", supplierPhoneString);
        smsIntent.putExtra("sms_body", restockMessage);

        try {
            startActivity(smsIntent);
            finish();
            Log.i(LOG_TAG ,"Finished sending SMS");
        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getApplicationContext(),"SMS failed , please try again later",Toast.LENGTH_SHORT).show();
        }
    }







    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String [] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_BRAND,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_COLOUR,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL,
                ProductContract.ProductEntry.COLUMN_RESTOCK_QUANTITY

        };


        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {



        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


        if (cursor.moveToFirst()){

            int nameIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int brandIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_BRAND);
            int quantityIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

            int colourIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_COLOUR);
            int priceIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int supplierNameIndex= cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);

            int imageIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);


            int phoneIndex= cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE);
            int emailIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL);
            int restockQuantityIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_RESTOCK_QUANTITY);





            String productName = cursor.getString(nameIndex);
            String brand = cursor.getString(brandIndex);
            int quantityAvailable = cursor.getInt(quantityIndex);
            String colour = cursor.getString(colourIndex);
            Float price = cursor.getFloat(priceIndex);
            String supplierName = cursor.getString(supplierNameIndex);
            byte [] image = cursor.getBlob(imageIndex);
            String phone = cursor.getString(phoneIndex);
            String email = cursor.getString(emailIndex);
            int restockQuantity = cursor.getInt(restockQuantityIndex);





            collapsingToolbarLayout.setTitle(productName);
            collapsingToolbarLayout.setTransitionName("name_transition");
            detailsPrice.setText(String.valueOf(price));
            detailsQuantity.setText(String.valueOf(quantityAvailable));
            if (!TextUtils.isEmpty(colour)){
            detailsColour.setText(colour);
            }else{
                detailsColour.setText("None");
            }

            if(!TextUtils.isEmpty(brand)) {
                detailsBrand.setText(brand);
            }else {
                detailsBrand.setText("None");
            }

            if(!TextUtils.isEmpty(supplierName)) {
                detailsName.setText(supplierName);
            }else {
                detailsName.setText("None");
            }

            if(!TextUtils.isEmpty(phone)) {
                detailsPhone.setText(phone);
            }else {
                detailsPhone.setText("None");
            }


            if(!TextUtils.isEmpty(email)) {
                detailsEmail.setText(email);
            }else {
                detailsEmail.setText("None");
            }


            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds=true;
            BitmapFactory.decodeByteArray(image, 0, image.length,opt);
            opt.inSampleSize = calculateInSampleSize(opt,1024,900);
            opt.inJustDecodeBounds=false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length,opt);
            detailsImage.setImageBitmap(bitmap);

            restockQuantityView.setText(String.valueOf(restockQuantity));

        }



    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


        collapsingToolbarLayout.setTitle("");
        collapsingToolbarLayout.setTransitionName("");
        detailsPrice.setText(String.valueOf("0"));
        detailsQuantity.setText(String.valueOf("0"));
        detailsColour.setText("");
        detailsBrand.setText("");
        detailsName.setText("");
        detailsImage.setImageResource(R.drawable.ic_add_product);
        detailsPhone.setText("");
        detailsEmail.setText("");
        restockQuantityView.setText("");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_product:
                Intent intent = new Intent(DetailsActivity.this,EditorActivity.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
                return true;
            case R.id.delete_product:
                showDeleteConfirmationDialog();
                return  true;

            case android.R.id.home:
                Intent homeIntent = new Intent(DetailsActivity.this,CatalogActivity.class);
                startActivity(homeIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDeleteConfirmationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCurrentProduct();
                        Intent catalogIntent = new Intent(DetailsActivity.this,CatalogActivity.class);
                        startActivity(catalogIntent);
                    }
                }
        );
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void deleteCurrentProduct(){

        if (mCurrentProductUri != null) {

            int deletedRows = getContentResolver().delete(mCurrentProductUri, null, null);

            if (deletedRows == 0){
                Toast.makeText(getBaseContext(),"Product deletion failed ",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Product deleted",Toast.LENGTH_SHORT).show();
            }
        }
    }






    @Override
    public void onBackPressed() {
          Intent catalogIntent = new Intent(DetailsActivity.this,CatalogActivity.class);
        startActivity(catalogIntent);

    }
}
