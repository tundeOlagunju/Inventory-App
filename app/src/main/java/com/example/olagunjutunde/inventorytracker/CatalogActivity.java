package com.example.olagunjutunde.inventorytracker;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olagunjutunde.inventorytracker.data.ProductContract;
import com.example.olagunjutunde.inventorytracker.data.ProductDbHelper;


import static android.R.attr.data;
import static android.R.attr.visible;
import static android.os.Build.VERSION_CODES.M;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

private static  final String LOG_TAG = CatalogActivity.class.getSimpleName();



    private ProductCursorAdapter mAdapter;
    View emptyView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ActivityCompat.requestPermissions(CatalogActivity.this,
                new String[]{  Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE},123);

        // Setup FAB to open EditorActivity
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                //make element transition animation for the fab
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CatalogActivity.this,fab,"fab_button");
                startActivity(intent,options.toBundle());
            }
        });

        // Find the GridView which will be populated with the product data
        GridView gridView =(GridView) findViewById(R.id.grid);
       // find the empty view
        emptyView =  findViewById(R.id.empty_view);
        // set empty view on the grid view if the grid is empty
        gridView.setEmptyView(emptyView);
        //set up an adapter to create a grid item for each row and column of pet data in the Cursor
        mAdapter = new ProductCursorAdapter(this,null);
        //set the adapter on the gridView
        gridView.setAdapter(mAdapter);

        // Setup the item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,id);

                Pair<View,String> pair1 = Pair.create(findViewById(R.id.grid_image),"image_transition");
                 Pair<View,String> pair2 = Pair.create(findViewById(R.id.name),"name_transition");


                //make element transition animation for image and name
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(CatalogActivity.this,
                      pair1 ,pair2);

                Intent intent = new Intent(CatalogActivity.this,DetailsActivity.class);
                //set URI on the intent to pass along
                intent.setData(currentProductUri);

                //start Details Activity
                startActivity(intent,activityOptionsCompat.toBundle());

            }
        });

        LoaderManager loaderManager = getSupportLoaderManager();
        //initialize the loader
        loaderManager.initLoader(0,null,this);

    }

    /**
     * Helper method to delete all the products in the grid view
     */

    private  void deleteAllProducts(){

        int rowDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI,null,null);

        if (rowDeleted > 0){
            Toast.makeText(getApplicationContext(),"All products deleted",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"No items to delete",Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Helper method to show the Delete Confirmation Dialog
     */
    private void showConfirmationDialog(){

        //create an Alert Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure you want to delete all the products?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllProducts();
                        emptyView.setVisibility(View.VISIBLE);
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

        // create and show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView =(SearchView)  MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search items");

        //Note:
        //MenuItemCompat.OnActionExpandListener interface has a static implementation and
        //is not an instance method so it is called on its class



        MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                CatalogActivity.this.setItemsVisibility(menu, searchItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                CatalogActivity.this.setItemsVisibility(menu, searchItem, true);
                return true;
            }
        });






        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            Cursor cursor= getProductsListByKeyword(query);

                if (cursor==null){
                    ImageView emptyImage = (ImageView) findViewById(R.id.empty_image);
                    emptyImage.setVisibility(View.GONE);
                    TextView emptyText = (TextView) findViewById(R.id.empty_state_text);
                    emptyText.setText("No items found ");

                }else
                    {

                    }
                mAdapter.swapCursor(cursor);

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

               Cursor  cursor= getProductsListByKeyword(newText);
                if (cursor!=null){
                    mAdapter.swapCursor(cursor);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case(R.id.delete_all_products):
                showConfirmationDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE};

        return new CursorLoader(this, ProductContract.ProductEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null) {

            mAdapter.swapCursor(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }




    public Cursor  getProductsListByKeyword(String search) {

       ProductDbHelper dbHelper = new ProductDbHelper(this);
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  rowid as " +
                ProductContract.ProductEntry._ID + "," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + "," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_BRAND + "," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY+ "," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_COLOUR+ "," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_BRAND + "," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + "," +
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + "," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE + "," +
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE + "," +
                ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL + "," +
                ProductContract.ProductEntry.COLUMN_RESTOCK_QUANTITY +
                " FROM " + ProductContract.ProductEntry.TABLE_NAME +
                " WHERE " + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + "  LIKE  '%" +search + "%' "
                ;


        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;



    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setItemsVisibility(final Menu menu, final MenuItem exception,
                                    final boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception)
                item.setVisible(visible);
        }
    }


}
