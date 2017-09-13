package com.example.user.dminesweeper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dminesweeper.GameActivity;
import com.example.user.dminesweeper.R;


public class MainActivity extends AppCompatActivity {

    public static int EASY = 5;
    public static int MEDIUM = 10;
    public static int HARD = 10;
    public static int SMALL = 5;
    public static int LARGE = 10;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, GameActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showHighScores();
    }


    private void showHighScores() {
        TextView highScoresTxt = (TextView) findViewById(R.id.scores);

        if (GameActivity.highScores!= null) {
            highScoresTxt.setText(GameActivity.highScores.toString());
        }else{
            highScoresTxt.setText("No Scores to show");
        }
    }

    public void buttonEasyClicked(View view) {
        intent.putExtra("NUM_OF_MINES", EASY );
        intent.putExtra("NUM_OF_ROWS", LARGE );
        Log.d("TAG","Easy button clicked");
        startActivity(intent);
    }

    public void buttonMediumClicked(View view) {
        intent.putExtra("NUM_OF_MINES", MEDIUM );
        intent.putExtra("NUM_OF_ROWS", LARGE );
        startActivity(intent);
    }

    public void buttonHardClicked(View view) {
        intent.putExtra("NUM_OF_MINES", HARD );
        intent.putExtra("NUM_OF_ROWS", SMALL );
        startActivity(intent);
    }
}
