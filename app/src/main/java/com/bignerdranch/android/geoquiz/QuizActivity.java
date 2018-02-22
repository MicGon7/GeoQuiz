package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;


    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviouButton;
    private Button mCheatButton;
    private HashMap<Integer, Boolean> questionMap = new HashMap<Integer, Boolean>();

    private TextView mQuestionTextView;


    // Declare and Initialize an array of question objects
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),

    };

    private int mCurrentIndex;
    private int mCorrectAnswers;
    private int scorePercentage;
    private boolean mIsCheater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        // Map current question to answer result.
        questionMap.put(0, false);
        questionMap.put(1, false);
        questionMap.put(2, false);
        questionMap.put(3, false);
        questionMap.put(4, false);
        questionMap.put(5, false);


        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        //TODO Ask about redundant casting

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
                setQuestionAnswered(mCurrentIndex);
                disableAnswerButtons();
                if (hasAllQuestionsBeenAnswered()) {
                    displayResults();
                }
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
                setQuestionAnswered(mCurrentIndex);
                disableAnswerButtons();
                if (hasAllQuestionsBeenAnswered()) {
                    displayResults();
                }
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                enabledAnswerButtons();
                updateQuestion();

                Log.d(TAG, String.valueOf(questionMap));
                if (hasQuestionBeenAnswered(mCurrentIndex)) {
                    disableAnswerButtons();
                }

                if (hasAllQuestionsBeenAnswered()) {
                    displayResults();
                }
            }

        });

        mPreviouButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex > 0) {
                    mCurrentIndex--;
                }
                enabledAnswerButtons();
                updateQuestion();
                if (hasQuestionBeenAnswered(mCurrentIndex)) {
                    disableAnswerButtons();
                }
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);

                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });


        updateQuestion();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        saveInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mIsCheater) {
            messageResId = R.string.judgement_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mCorrectAnswers++;
                Log.d(TAG, String.valueOf("Correct Answers: " + mCorrectAnswers));

            } else {
                messageResId = R.string.incorrect_toast;
            }

        }


        //TODO reposition toast to top of screen.
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void setQuestionAnswered(int currentIndex) {
        questionMap.put(currentIndex, true);
    }

    private boolean hasQuestionBeenAnswered(int currentIndex) {
        return questionMap.get(currentIndex);
    }

    private void disableAnswerButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private void enabledAnswerButtons() {
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
    }

    private boolean hasAllQuestionsBeenAnswered() {
        if (questionMap.containsValue(false)) {
            return false;
        }
        return true;
    }

    private void displayResults() {

        scorePercentage = (int) ((mCorrectAnswers * 100.0f) / mQuestionBank.length);

        Log.d(TAG, String.valueOf("Score Percentage: " + scorePercentage));
        Toast.makeText(this,
                String.valueOf(scorePercentage) + "% Correct!", Toast.LENGTH_LONG).show();

    }

}
