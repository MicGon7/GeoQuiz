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
    private static final String KEY_CHEATED = "cheated";
    private static final String KEY_CHEAT_COUNT = "cheat count";
    private static final String KEY_QUESTION_MAP = "question map";
    private static final String KEY_CHEAT_MAP = "cheat map";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPreviouButton;
    private TextView mQuestionTextView;
    private TextView mCheatCountTextView;

    private HashMap<Integer, Boolean> questionMap = new HashMap<Integer, Boolean>();
    private HashMap<Integer, Boolean> cheatMap = new HashMap<Integer, Boolean>();
    private int mCurrentIndex;
    private int mCorrectAnswers;
    private boolean mQuestionAnswered;
    private int mScorePercentage;
    private boolean mIsCheater;
    private int mCheatCounter = 3;



    // Declare and Initialize an array of question objects
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        questionMap = populateBooleanMap(questionMap);
        cheatMap = populateBooleanMap(cheatMap);


        // Initialize widgets.
        mQuestionTextView = findViewById(R.id.question_text_view);
        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mNextButton =  findViewById(R.id.next_button);
        mPreviouButton = findViewById(R.id.previous_button);
        mCheatButton = findViewById(R.id.cheat_button);
        mCheatCountTextView = findViewById(R.id.cheat_count_text_view);

        mCheatCountTextView.setText(String.valueOf("Cheats Remaining: " + mCheatCounter));

        // Setup listeners.
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAnswerSelection(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAnswerSelection(false);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                handleQuestionTransition();
            }
        });

        mPreviouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentIndex > 0) {
                    mCurrentIndex--;
                }
                handleQuestionTransition();
            }
        });


        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
                setQuestionCheated();

            }
        });

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCheatCounter = savedInstanceState.getInt(KEY_CHEAT_COUNT, 0);
            questionMap = ((HashMap<Integer, Boolean>)savedInstanceState.getSerializable(KEY_QUESTION_MAP));
            cheatMap = ((HashMap<Integer, Boolean>)savedInstanceState.getSerializable(KEY_CHEAT_MAP));

        }

        handleQuestionTransition();

    }

    private void handleAnswerSelection(Boolean answer) {
        checkAnswer(answer);
        setQuestionAnswered();
        disableAnswerButtons();
        mCheatButton.setEnabled(false);

        if (hasAllQuestionsBeenAnswered()) {
            displayResults();
        }
    }

    private void handleQuestionTransition() {

        enabledAnswerButtons();
        Log.d(TAG, "Current Index: " + mCurrentIndex);
        Log.d(TAG, "Question map: " + questionMap);
        Log.d(TAG, "Cheat map: " + cheatMap);
        mQuestionAnswered = questionMap.get(mCurrentIndex);
        mIsCheater = cheatMap.get(mCurrentIndex);

        if(mCheatCounter == 0 || mIsCheater){
            mCheatButton.setEnabled(false);
        } else {
            mCheatButton.setEnabled(true);
        }

        if (mQuestionAnswered) {
            disableAnswerButtons();
            mCheatButton.setEnabled(false);
        }

        if (hasAllQuestionsBeenAnswered()) {
            displayResults();
        }
        updateQuestion();
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
                mIsCheater = false;
            }

        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void setQuestionAnswered() {
        questionMap.put(mCurrentIndex, true);
    }

    private void setQuestionCheated() {
        cheatMap.put(mCurrentIndex, true);
    }

    private boolean hasAllQuestionsBeenAnswered() {
        if (questionMap.containsValue(false)) {
            return false;
        }
        return true;
    }

    private void disableAnswerButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private void enabledAnswerButtons() {
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
    }

    private void displayResults() {

        mScorePercentage = (int) ((mCorrectAnswers * 100.0f) / mQuestionBank.length);

        Log.d(TAG, String.valueOf("Score Percentage: " + mScorePercentage));
        Toast.makeText(this,
                String.valueOf(mScorePercentage) + "% Correct!", Toast.LENGTH_LONG).show();

    }

    private HashMap populateBooleanMap(HashMap<Integer, Boolean> m) {
        for(int i = 0; i < mQuestionBank.length; i++){
            m.put(i, false);
        }
        return m;
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        saveInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        saveInstanceState.putBoolean(KEY_CHEATED, mIsCheater);
        saveInstanceState.putInt(KEY_CHEAT_COUNT, mCheatCounter);
        saveInstanceState.putSerializable(KEY_QUESTION_MAP, questionMap);
        saveInstanceState.putSerializable(KEY_CHEAT_MAP, cheatMap);
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

            mCheatCounter--;

            if(mCheatCounter == 0){
                mCheatButton.setEnabled(false);
            }
            mCheatCountTextView.setText(String.valueOf("Cheats Remaining: " + mCheatCounter));
        }
    }

}
