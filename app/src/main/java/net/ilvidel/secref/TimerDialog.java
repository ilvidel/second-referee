package net.ilvidel.secref;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class TimerDialog extends DialogFragment {

    private static final String TIME = "sec";

    private TextView timeText;
    private TimerTask mTimerTask;
    private int seconds = 30;

    /**
     * Create a new instance of TimerDialog, providing "seconds" as an argument.
     */
    static TimerDialog newInstance(int seconds) {
        TimerDialog f = new TimerDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("seconds", seconds);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.timeout_large);
        builder.setNegativeButton(R.string.cancel, null);

        View view = View.inflate(getActivity(), R.layout.timer_dialog, null);
        timeText = view.findViewById(R.id.timeText);

        seconds = getArguments().getInt("seconds");

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt(TIME);
            timeText.setText(String.format("%d:%02d", seconds / 60, seconds % 60));
        }
        startTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TIME, seconds);
    }

    private void startTimer() {
        Timer t = new Timer();

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                seconds--;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (seconds >= 0) {
                            timeText.setText(String.format("%d:%02d", seconds / 60, seconds % 60));
                        }
                        if(seconds < 7) {
                            timeText.setBackgroundResource(R.color.colorAccent);
                        }
                    }
                });
                if (seconds == 0) {
                    Vibrator v = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {0, 150, 75, 150, 75};
                    v.vibrate(pattern, -1);
                } else if(seconds < 0) {
                    this.cancel();
                    dismiss();
                }
            }
        };

        t.scheduleAtFixedRate(mTimerTask, 1000, 1000);
    }

    private void stopTimer() {
        mTimerTask.cancel();
    }
}
