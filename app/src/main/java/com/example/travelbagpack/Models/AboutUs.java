package com.example.travelbagpack.Models;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelbagpack.R;

public class AboutUs extends AppCompatActivity {

    ImageView imglinkdln, imggit;
    TextView txtmail, txtnbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About us");

        imglinkdln = findViewById(R.id.imglinkdln);
        imggit = findViewById(R.id.imggit);
        txtmail = findViewById(R.id.txtmail);
        txtnbr = findViewById(R.id.txtnbr);

        imglinkdln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.linkedin.com/in/dhairya-ahya/"));
                startActivity(intent);
            }
        });

        txtmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "dhairyaahya678@gmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "From TravelbagPack");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    System.out.println(e);
                }
            }
        });

        imggit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/dhairyaahya678/"));
                startActivity(intent);
            }
        });

        txtnbr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+917767800678")); // Replace with the actual phone number
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
