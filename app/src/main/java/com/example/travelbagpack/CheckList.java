package com.example.travelbagpack;

import com.example.travelbagpack.Models.AboutUs;
import com.example.travelbagpack.R;
import com.example.travelbagpack.Data.AppData;
import com.example.travelbagpack.Database.RoomDB;
import com.example.travelbagpack.Models.Items;
import com.example.travelbagpack.Constants.MyConstants;
import com.example.travelbagpack.Adapter.CheckListAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckList extends AppCompatActivity {

    RecyclerView recyclerView;
    CheckListAdapter checkListAdapter;
    RoomDB database;
    List<Items> itemsList = new ArrayList<>();
    String header, show;

    EditText txtAdd;
    Button btnAdd;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        header = intent.getStringExtra(MyConstants.HEADER_SMALL);
        show = intent.getStringExtra(MyConstants.SHOW_SMALL);

        getSupportActionBar().setTitle(header);

        txtAdd = findViewById(R.id.txtAdd);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayout = findViewById(R.id.linearLayout);

        database = RoomDB.getInstance(this);

        if (MyConstants.FALSE_STRING.equals(show)) {
            linearLayout.setVisibility(View.GONE);
            itemsList = database.mainDao().getAllSelected(true);
        } else {
            itemsList = database.mainDao().getAll(header);
        }

        updateRecycler(itemsList);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = txtAdd.getText().toString();
                if (itemName != null && !itemName.isEmpty()) {
                    addNewItem(itemName);
                    Toast.makeText(CheckList.this, "Item Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CheckList.this, "Empty can't be added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_one, menu);

            if (MyConstants.MY_SELECTIONS.equals(header)) {
                menu.findItem(R.id.btnSearch).setVisible(false);
                menu.findItem(0).setVisible(false);
                menu.findItem(2).setVisible(false);
                menu.findItem(3).setVisible(false);
            } else if (MyConstants.MY_LIST_CAMEL_CASE.equals(header)) {
                menu.findItem(R.id.btnMySelections).setVisible(false);
            }

            MenuItem searchItem = menu.findItem(R.id.btnSearch);
            SearchView searchView = (SearchView) searchItem.getActionView();

            setSearchViewListener(searchView);

            return true;
        } catch (Exception e) {
            Log.e("OptionsMenu", "Error in onCreateOptionsMenu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void setSearchViewListener(SearchView searchView) {
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Set<Items> uniqueItemsSet = new HashSet<>();
                    List<Items> priorityList = new ArrayList<>();
                    List<Items> secondaryList = new ArrayList<>();
                    String lowerCaseQuery = newText.toLowerCase();

                    for (Items item : itemsList) {
                        String itemNameLowerCase = item.getItemname().toLowerCase();
                        if (itemNameLowerCase.startsWith(lowerCaseQuery)) {
                            if (uniqueItemsSet.add(item)) {
                                priorityList.add(item);
                            }
                        } else if (itemNameLowerCase.contains(lowerCaseQuery)) {
                            if (uniqueItemsSet.add(item)) {
                                secondaryList.add(item);
                            }
                        }
                    }

                    List<Items> combinedList = new ArrayList<>(priorityList);
                    combinedList.addAll(secondaryList);

                    updateRecycler(combinedList);
                    return true;
                }
            });
        } else {
            Log.e("SearchView", "SearchView is null");
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, CheckList.class);
        AppData appData = new AppData(database, this);

        int id = item.getItemId();
        if (id == R.id.btnMySelections) {
            intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_SELECTIONS);
            intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.FALSE_STRING);
            startActivityForResult(intent, 101);
            return true;
        }

        if (id == R.id.btnCustomList) {
            intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_LIST_CAMEL_CASE);
            intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.TRUE_STRING);
            startActivity(intent);
            return true;
        }

        if (id == R.id.btnDeleteDefault) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete default data")
                    .setMessage("Are you sure?\n\nAs this will delete the data provided by app while installing")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            appData.persistDataByCategory(header, true);
                            itemsList = database.mainDao().getAll(header);
                            updateRecycler(itemsList);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setIcon(R.drawable.aleart_ic).show();
            return true;
        }

        if (id == R.id.btnReset) {
            new AlertDialog.Builder(this)
                    .setTitle("Reset to default")
                    .setMessage("Are you sure?\n\nAs this will load the default data provided by app and will delete the custom data you have added in " + header)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            appData.persistDataByCategory(header, false);
                            itemsList = database.mainDao().getAll(header);
                            updateRecycler(itemsList);

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(R.drawable.aleart_ic)
                    .show();
            return true;
        }
        if (id == R.id.btnAboutUs) {
            intent = new Intent(this, AboutUs.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.btnExit) {
            this.finishAffinity();
            Toast.makeText(this, "Happy Journey\nExit Completed", Toast.LENGTH_SHORT).show();
            return true;
        }

        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            itemsList = database.mainDao().getAll(header);
            updateRecycler(itemsList);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addNewItem(String itemName) {
        Items item = new Items();
        item.setChecked(false);
        item.setCategory(header);
        item.setItemname(itemName);
        item.setAddedby(MyConstants.USER_SMALL);
        database.mainDao().saveItem(item);
        itemsList = database.mainDao().getAll(header);
        updateRecycler(itemsList);
        recyclerView.scrollToPosition(checkListAdapter.getItemCount() - 1);
        txtAdd.setText("");
    }

    private void updateRecycler(List<Items> itemsList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        checkListAdapter = new CheckListAdapter(CheckList.this, itemsList, database, show);
        recyclerView.setAdapter(checkListAdapter);
    }
}
