package com.example.olagunjutunde.inventorytracker;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.olagunjutunde.inventorytracker.data.ProductContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.data;
import static android.R.attr.onClick;
import static android.R.id.edit;
import static android.R.id.input;
import static com.example.olagunjutunde.inventorytracker.R.id.price;
import static com.example.olagunjutunde.inventorytracker.R.id.quantity;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final  String LOG_TAG = EditorActivity.class.getSimpleName();

    private ImageView productImageView;

    private EditText mProductNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mBrandEditText;
    private EditText mColourEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierMailEditText;
    private EditText mSupplierPhoneEditText;
    private EditText mRestockQuantityText;

    private TextInputLayout inputLayoutProductName;
    private TextInputLayout inputLayoutEmail;

    private FloatingActionButton selectImage;
    private Button mIncreaseButton;
    private Button mDecreaseButton;

    private boolean mProductHasChanged ;


    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;

    private static final int EXISTING_PRODUCT_LOADER = 12 ;

    private Uri mCurrentProductUri;

private  View.OnTouchListener mTouchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mProductHasChanged = true;
        return false;
    }
} ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);



        mCurrentProductUri = getIntent().getData();

        if (mCurrentProductUri == null){

            setTitle("Add a Product");

            LinearLayout restockView = (LinearLayout)findViewById(R.id.restock_view);
            restockView.setVisibility(View.GONE);


            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }
        else{

            setTitle("Edit Product");
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(EXISTING_PRODUCT_LOADER,null,this);
        }


        productImageView = (ImageView) findViewById(R.id.product_image);
         selectImage =(FloatingActionButton)findViewById(R.id.fab_camera);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()){

                    case R.id.fab_camera:
                        new MaterialDialog.Builder(EditorActivity.this)
                                .title("Set Your Image")
                                .items(R.array.uploadImages)
                                .itemsIds(R.array.itemsIds)
                                .itemsCallback(new MaterialDialog.ListCallback(){
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                        switch (position){

                                            case 0:
                                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                                photoPickerIntent.setType("image/*");
                                                startActivityForResult(photoPickerIntent,SELECT_PHOTO);
                                                break;
                                            /**
                                            case 1:
                                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                startActivityForResult(intent,CAPTURE_PHOTO);
                                                break;
                                             **/
                                            case 1:
                                                productImageView.setImageResource(R.drawable.ic_gallery);
                                                break;
                                        }
                                    }
                                })
                                .show();
                        break;



                }
            }
        });


        /**
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            productImageView.setEnabled(false);

            } else {

            productImageView.setEnabled(true);
        }

**/

        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText =(EditText)findViewById(R.id.edit_product_price);
        mBrandEditText =(EditText)findViewById(R.id.edit_product_brand);
        mColourEditText =(EditText)findViewById(R.id.edit_product_colour);
        mSupplierNameEditText=(EditText) findViewById(R.id.edit_supplier_name);
        mSupplierMailEditText =(EditText)findViewById(R.id.edit_supplier_email);
        mSupplierPhoneEditText=(EditText)findViewById(R.id.edit_supplier_phone);
        mQuantityEditText =(EditText)findViewById(R.id.edit_product_quantity);
        mRestockQuantityText=(EditText)findViewById(R.id.edit_restock_quantity);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mBrandEditText.setOnTouchListener(mTouchListener);
        mColourEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierMailEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);




        mIncreaseButton = (Button)findViewById(R.id.increase);
        mDecreaseButton = (Button) findViewById(R.id.decrease);

        inputLayoutProductName =(TextInputLayout)findViewById(R.id.input_layout_product_name);
        inputLayoutEmail =(TextInputLayout)findViewById(R.id.input_layout_email);


      //  mProductNameEditText.addTextChangedListener(new MyTextWatcher(mProductNameEditText));
        //mSupplierMailEditText.addTextChangedListener(new MyTextWatcher(mSupplierMailEditText));
       //mPriceEditText.addTextChangedListener(new MyTextWatcher(mProductNameEditText));

        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantityByOne();
            }
        });


        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantityByOne();
            }
        });

    }


private void increaseQuantityByOne(){
    String quantityString = mQuantityEditText.getText().toString().trim();
    int quantityInt = Integer.parseInt(quantityString);

    quantityInt = quantityInt + 1;

    mQuantityEditText.setText(String.valueOf(quantityInt));

}


private void decreaseQuantityByOne(){
    String quantityString = mQuantityEditText.getText().toString().trim();
    int quantityInt = Integer.parseInt(quantityString);

    if (quantityInt == 0){
       Toast.makeText(getApplicationContext(),"Quantity must be a positive value",Toast.LENGTH_SHORT).show();
        return;
    }
    quantityInt = quantityInt - 1;
    mQuantityEditText.setText(String.valueOf(quantityInt));

}




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0 ){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                productImageView.setEnabled(true);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap = scaleImage(this, selectedImageUri);
                    productImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
       /**
        else  if(requestCode == CAPTURE_PHOTO) {
            if (resultCode == RESULT_OK) {

            }
        }   **/

        }

        private  void onCaptureImageResult(Intent data) throws IOException{

            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
         //   BitmapFactory.decodeByteArray(dat,0,dat.length,options);

             // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 200, 200);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;


           // Bitmap bmp1=BitmapFactory.decodeByteArray(dat,0,dat.length,options);

            //productImageView.setImageBitmap(bmp1);

        }
        private void saveProduct(){

            Intent intent = new Intent(EditorActivity.this,DetailsActivity.class);

            hideKeyboard(this);
            productImageView.setDrawingCacheEnabled(true);
            productImageView.buildDrawingCache();
            Bitmap bitmap = productImageView.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();



            String nameString = mProductNameEditText.getText().toString().trim();
            String priceString = mPriceEditText.getText().toString().trim();
            String colourString = mColourEditText.getText().toString().trim();
            String brandString = mBrandEditText.getText().toString().trim();
            String quantityString = mQuantityEditText.getText().toString().trim();
            String supplierNameString = mSupplierNameEditText.getText().toString().trim();
            String supplierMailString = mSupplierMailEditText.getText().toString().trim();
            String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
            String restockQuantityString = mRestockQuantityText.getText().toString().trim();



            if(TextUtils.isEmpty(quantityString)) {

                Toast.makeText(getBaseContext(),"Quantity field cannot be empty",Toast.LENGTH_SHORT).show();
                return;
            }

            int quantityInt = Integer.parseInt(quantityString);

            if(mCurrentProductUri == null&&TextUtils.isEmpty(nameString)&&TextUtils.isEmpty(priceString)&&TextUtils.isEmpty(colourString)
                    &&TextUtils.isEmpty(brandString)&&quantityInt==0&&TextUtils.isEmpty(supplierNameString)&&
                    TextUtils.isEmpty(supplierMailString)&&TextUtils.isEmpty(supplierMailString)&&TextUtils.isEmpty(supplierPhoneString)
                     &&TextUtils.isEmpty(restockQuantityString)){

                Toast.makeText(getApplicationContext(),"Nothing to save.Product discarded",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }


            if(TextUtils.isEmpty(nameString)){
                inputLayoutProductName.setError("Product requires a name");
                requestFocus(mProductNameEditText);
                return;
            }else{
                inputLayoutProductName.setErrorEnabled(false);
            }

            if(TextUtils.isEmpty(priceString)) {
                priceString = "0";
            }

          double priceFloat = Float.parseFloat(priceString);

            if(priceFloat == 0){
                mPriceEditText.setError("Enter the price of the product");
                return;
            }

            if(!TextUtils.isEmpty(supplierMailString)&&!isValidEmail(supplierMailString)){
                inputLayoutEmail.setError("Enter a valid email");
                requestFocus(mSupplierMailEditText);
                return;


            }else{
                inputLayoutEmail.setErrorEnabled(false);
            }


            if(mCurrentProductUri== null&&quantityInt==0){
                mQuantityEditText.setError("Quantity of " + nameString + " must be entered");
                Toast.makeText(getBaseContext(),"Quantity of a new product cannot be zero",Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE,data);
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,nameString);
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,priceFloat);
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,quantityInt);
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_BRAND,brandString);
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_COLOUR,colourString);
            contentValues.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,supplierNameString);
            contentValues.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL,supplierMailString);
            contentValues.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE,supplierPhoneString);
            contentValues.put(ProductContract.ProductEntry.COLUMN_RESTOCK_QUANTITY,restockQuantityString);

            if(mCurrentProductUri == null) {
                Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, contentValues);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, "Failed to save product",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this,"Product saved successfully",
                            Toast.LENGTH_SHORT).show();
                    intent.setData(newUri);
                    startActivity(intent);
                }

            }else{

                int rowsAffected = getContentResolver().update(mCurrentProductUri, contentValues, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, "Failed to update product.",
                            Toast.LENGTH_SHORT).show();
                    intent.setData(mCurrentProductUri);
                    startActivity(intent);
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this,"Product updated successfully.",
                            Toast.LENGTH_SHORT).show();
                    intent.setData(mCurrentProductUri);
                    startActivity(intent);
                }

            }



        }


        private void deleteProduct(){
            if (mCurrentProductUri != null) {

                int deletedRows = getContentResolver().delete(mCurrentProductUri, null, null);

                if (deletedRows == 0){
                    Toast.makeText(getBaseContext(),"Product deletion failed ",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Product deleted",Toast.LENGTH_SHORT).show();
                }
            }


            finish();

        }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentProductUri == null){
           MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    /**
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_product_name:
                    validateName();
                    break;
                case R.id.edit_product_price:
                    validateEmail();
                    break;
                case R.id.edit_supplier_email:
                    validatePassword();
                    break;
            }
        }

 **/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      final  Intent detailsIntent = new Intent(EditorActivity.this,DetailsActivity.class);
        detailsIntent.setData(mCurrentProductUri);
        switch(item.getItemId()){
            case R.id.action_save:
                saveProduct();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if(mCurrentProductUri==null && !mProductHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                if (mCurrentProductUri!= null && !mProductHasChanged ) {
                    startActivity(detailsIntent);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mCurrentProductUri != null) {
                            startActivity(detailsIntent);
                        }else{
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    }
                };

                showUnsavedChangesDialog(discardButtonListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        final Intent detailsIntent = new Intent(EditorActivity.this,DetailsActivity.class);
        detailsIntent.setData(mCurrentProductUri);

        if(mCurrentProductUri==null && !mProductHasChanged){
            NavUtils.navigateUpFromSameTask(EditorActivity.this);
            return ;
        }

        if (mCurrentProductUri!= null && !mProductHasChanged ) {
            startActivity(detailsIntent);
            return ;
        }


        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discard.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(mCurrentProductUri != null) {
                            startActivity(detailsIntent);
                        }else{
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }




    private static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    //Given the bitmap size and View size calculate a subsampling size (powers of 2)
    static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
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


    public static Bitmap scaleImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 500 || rotatedHeight > 500) {
            float widthRatio = ((float) rotatedWidth) / ((float) 500);
            float heightRatio = ((float) rotatedHeight) / ((float) 500);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
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



            mProductNameEditText.setText(productName);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantityAvailable));
            mColourEditText.setText(colour);
            mBrandEditText.setText(brand);
            mSupplierMailEditText.setText(email);
            mSupplierPhoneEditText.setText(phone);
            mSupplierNameEditText.setText(supplierName);


            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds=true;
             BitmapFactory.decodeByteArray(image, 0, image.length,opt);
            opt.inSampleSize = calculateInSampleSize(opt,1024,900);
            opt.inJustDecodeBounds=false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length,opt);

            productImageView.setImageBitmap(bitmap);
            mRestockQuantityText.setText(String.valueOf(restockQuantity));

        }



    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


        mProductNameEditText.setText("");
        mPriceEditText.setText(String.valueOf("0"));
        mQuantityEditText.setText(String.valueOf("0"));
        mColourEditText.setText("");
        mBrandEditText.setText("");
        mSupplierMailEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mSupplierNameEditText.setText("");
        productImageView.setImageResource(R.drawable.ic_add_product);
        mRestockQuantityText.setText("");

    }

    private void showDeleteConfirmationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    deleteProduct();
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




private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonListener){

    AlertDialog.Builder builder= new AlertDialog.Builder(this);

    builder.setTitle("Discard your changes and quit editing?");
    builder.setPositiveButton("Discard",discardButtonListener);
    builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (dialog!= null) {
                dialog.dismiss();
            }
        }
    });


    AlertDialog dialog = builder.create();
    dialog.show();
}




}


