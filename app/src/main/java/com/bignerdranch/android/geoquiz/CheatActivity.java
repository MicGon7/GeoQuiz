package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.bignerdranch.geoquiz.answer_shown";
    private static final String KEY_CHEATED = "cheater";
    private static final String EXTRA_CHEAT_COUNT = "cheat limit";


    private TextView mAnswerTextView;
    private TextView mBuildVersionTextView;
    private Button mShowAnswerButton;
    private boolean mAnswerIsTrue;
    private boolean mIsCheater;
    private int mCheatLimit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mBuildVersionTextView = (TextView) findViewById(R.id.build_version);

        mBuildVersionTextView.setText("API Level " + Build.VERSION.SDK_INT);

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswerText();
                mIsCheater = true;
                mCheatLimit -= 1;
                setAnswerShownResult(true, mCheatLimit);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.
                            createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }

            }
        });

        if (savedInstanceState != null) {
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATED, false);

            if(mIsCheater) {
                handleAnswerText();
            }

        }


    }

    private void setAnswerShownResult(boolean isAnswerShown, int cheatLimit) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_CHEAT_COUNT, cheatLimit);
        setResult(RESULT_OK, data);
    }

    // Get data from QuizActvity -> Android Manager
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int getCheatCount(Intent result) {
        return result.getIntExtra(EXTRA_CHEAT_COUNT, 0 );
    }


    private void handleAnswerText() {
        if (mAnswerIsTrue) {
            mAnswerTextView.setText(R.string.true_button);
        } else {
            mAnswerTextView.setText(R.string.false_button);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putBoolean(KEY_CHEATED, mIsCheater);
    }

}
