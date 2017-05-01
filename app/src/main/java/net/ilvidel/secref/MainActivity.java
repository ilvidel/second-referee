package net.ilvidel.secref;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Team left, right;
    private CourtView court;
    private String label_time, label_subs;

    private Button buttonScoreL;
    private Button buttonScoreR;
    private Button buttonTOL;
    private Button buttonTOR;
    private Button buttonSubstL;
    private Button buttonSubstR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        left = new Team();
        left.serving(false);

        right = new Team();
        right.serving(true);
        court = (CourtView) findViewById(R.id.court_view);
        court.setIsServingLeft(left.isServing());

        // buttons
        buttonScoreL = (Button) findViewById(R.id.scoreLeft);
        buttonScoreR = (Button) findViewById(R.id.scoreRight);
        buttonTOL = (Button) findViewById(R.id.timeoutLeft);
        buttonTOR = (Button) findViewById(R.id.timeoutRight);
        buttonSubstL = (Button) findViewById(R.id.substLeft);
        buttonSubstR = (Button) findViewById(R.id.substRight);

        // screen-size-dependent labels
        int size = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (size){
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                label_time = getResources().getString(R.string.timeout_large);
                label_subs = getResources().getString(R.string.substitution_long);
                break;
            default:
                label_time = getResources().getString(R.string.timeout);
                label_subs = getResources().getString(R.string.substitution);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Bundle b = new Bundle();
        b.putIntArray("lefties", court.getLeftPlayers());
        b.putIntArray("righties", court.getRightPlayers());
        b.putSerializable("left", left);
        b.putSerializable("right", right);
        savedInstanceState.putBundle("players", b);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle b = savedInstanceState.getBundle("players");

        left = (Team) b.getSerializable("left");
        right = (Team) b.getSerializable("right");
        court.setLeftPlayers(b.getIntArray("lefties"));
        court.setRightPlayers(b.getIntArray("righties"));

        buttonScoreL.setText(String.format("%02d", left.getScore()));
        buttonScoreR.setText(String.format("%02d", right.getScore()));
        buttonTOL.setText(String.format("%s (%d)", label_time, left.getTimeoutCount()));
        buttonTOR.setText(String.format("%s (%d)", label_time, right.getTimeoutCount()));
        buttonSubstL.setText(String.format("%s (%d)", label_subs, left.getSubsCount()));
        buttonSubstR.setText(String.format("%s (%d)", label_subs, right.getSubsCount()));
    }

    public void onScoreLeft(View v) {
        left.score();
        if(right.isServing()) {
            left.rotate();
            court.setLeftPlayers(left.roster());
            left.serving(true);
            right.serving(false);
        }

        court.setIsServingLeft(left.isServing());
        court.invalidate();
        buttonScoreL.setText(String.format("%02d", left.getScore()));
    }

    public void onScoreRight(View v) {
        right.score();
        if(left.isServing()) {
            right.rotate();
            court.setRightPlayers(right.roster());
            right.serving(true);
            left.serving(false);
        }

        court.setIsServingLeft(left.isServing());
        court.invalidate();
        buttonScoreR.setText(String.format("%02d", right.getScore()));
    }

    public void onTimeoutLeft(View v) {
        left.timeout();
        buttonTOL.setText(String.format("%s (%d)", label_time, left.getTimeoutCount()));

        if(left.getTimeoutCount() == 0) buttonTOL.setEnabled(false);
        showTimer(30);
    }

    public void onTimeoutRight(View v) {
        right.timeout();
        buttonTOR.setText(String.format("%s (%d)", label_time, right.getTimeoutCount()));

        if(right.getTimeoutCount() == 0) buttonTOR.setEnabled(false);
        showTimer(30);
    }

    public void onSubstitutionLeft(View v) {
        left.setSubsCount(left.getSubsCount() - 1);
        buttonSubstL.setText(String.format("%s (%d)", label_subs, left.getSubsCount()));

        if(left.getSubsCount() == 0) buttonSubstL.setEnabled(false);
    }

    public void onSubstitutionRight(View v) {
        right.setSubsCount(right.getSubsCount() - 1);
        buttonSubstR.setText(String.format("%s (%d)", label_subs, right.getSubsCount()));

        if(right.getSubsCount() == 0) buttonSubstR.setEnabled(false);
    }

    public void onSwapSides(View v) {
        Team aux = left.clone();
        left = right.clone();
        right = aux;

        EditText leftName = (EditText) findViewById(R.id.leftFieldName);
        EditText rightName = (EditText) findViewById(R.id.rightFieldName);
        Editable buf = leftName.getText();
        leftName.setText(rightName.getText());
        rightName.setText(buf);

        court.setLeftPlayers(left.roster());
        court.setRightPlayers(right.roster());
        court.invalidate();
    }

    public void onRotateLeft(View v) {
        left.rotate();
        court.setLeftPlayers(left.roster());
    }

    public void onRotateRight(View v) {
        right.rotate();
        court.setRightPlayers(right.roster());
    }

    public void onDeletePointLeft(View v) {
        if(left.getScore() < 1) return;
        left.setScore(left.getScore() - 1);
        buttonScoreL.setText(String.format("%02d", left.getScore()));
    }

    public void onDeletePointRight(View v) {
        if(right.getScore() < 1) return;

        right.setScore(right.getScore()-1);
        buttonScoreR.setText(String.format("%02d", right.getScore()));
    }

    public void onChangeService(View v){
        if(left.isServing()) {
            left.serving(false);
            right.serving(true);
        } else {
            left.serving(true);
            right.serving(false);
        }

        court.setIsServingLeft(left.isServing());
        court.invalidate();
    }

    public void onCourtTap(View v) {
        int out = court.getPlayerOut();
        if(out == -1) {
            Toast t = Toast.makeText(this, R.string.tapped_nowhere, Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        //show dialog to choose number
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = LayoutInflater.from(this);
        View chooser = li.inflate(R.layout.fragment_number_chooser_dialog, null);

        builder.setView(chooser);
        final EditText userInput = (EditText) chooser.findViewById(R.id.number_edit);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int n = Integer.parseInt(userInput.getText().toString());
                        setPlayer(n);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void onResetButton(View v) {
        left.startSet();
        right.startSet();

        buttonSubstL.setText(String.format("%s (%d)", label_subs, left.getSubsCount()));
        buttonSubstL.setEnabled(true);
        buttonSubstR.setText(String.format("%s (%d)", label_subs, right.getSubsCount()));
        buttonSubstR.setEnabled(true);

        buttonTOL.setText(String.format("%s (%d)", label_time, left.getTimeoutCount()));
        buttonTOL.setEnabled(true);
        buttonTOR.setText(String.format("%s (%d)", label_time, right.getTimeoutCount()));
        buttonTOR.setEnabled(true);

        buttonScoreL.setText("00");
        buttonScoreR.setText("00");

        court.setLeftPlayers(left.roster());
        court.setRightPlayers(right.roster());
        court.invalidate();
    }

    void setPlayer(int number) {
        if(number < 0) return;
        court.setPlayerIn(number);

        left.roster(court.getLeftPlayers());
        right.roster(court.getRightPlayers());
    }

    /**
     * Display a dialog with a countdown
     * @param seconds The number of seconds for countdown
     */
    void showTimer(int seconds) {
        TimerDialog timerDialog = TimerDialog.newInstance(seconds);
        timerDialog.show(getFragmentManager(), "NoticeDialogFragment");
    }
}
