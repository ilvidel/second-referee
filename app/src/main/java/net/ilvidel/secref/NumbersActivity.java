package net.ilvidel.secref;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class NumbersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void onButtonPress(View v) {
        String label = ((Button) v).getText().toString();
        Intent i = new Intent();
        int n, result;

        try {
            n = Integer.parseInt(label);
            result = RESULT_OK;
        } catch (NumberFormatException e) {
            n = -1;
            result = RESULT_CANCELED;
        }

        i.putExtra("playerin", n);
        setResult(result, i);
        finish();
    }
}
