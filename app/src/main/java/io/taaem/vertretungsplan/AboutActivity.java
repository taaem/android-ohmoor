package io.taaem.vertretungsplan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import io.taaem.vertretungsplan.R;

public class AboutActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView = (TextView) findViewById(R.id.about_text);
        String aboutText = "Copyright by taaem (https://github.com/taaem) \n" +
                "License NYD\n" +
                "Source NYP";
        String nHY = "Noch nichts zu sehen";
        textView.setText(nHY);

        Button btn = (Button) findViewById(R.id.fdb_button);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("*/*");
                intent.setData(Uri.parse("mailto:taaem@mailbox.org"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Vertretungsplan Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(intent, "Send Email"));
            }

        });
    }
}
    
