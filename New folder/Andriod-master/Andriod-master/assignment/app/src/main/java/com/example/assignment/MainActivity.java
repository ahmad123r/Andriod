package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.example.assignment.OptionAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String ANSWERS_KEY = "answers";

    private TextView textLevel;
    private TextView textRightAnswered;
    private TextView textQuestion;
    private TextView textResult;
    private RecyclerView recyclerViewOptions;

    private int level = 0;
    private int correctAnswers = 0;
    private int rightAnswer = 0;
    private String realOperation = "";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    private List<Question> questionList;
    private List<Boolean> answerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLevel = findViewById(R.id.textQuestionNumber);
        textRightAnswered = findViewById(R.id.textRightAnswered);
        textQuestion = findViewById(R.id.textQuestion);
        textResult = findViewById(R.id.textResult);
        recyclerViewOptions = findViewById(R.id.recyclerViewOptions);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        gson = new Gson();

        textLevel.setText("Q: " + level + " / 10");
        textRightAnswered.setText("RA: " + correctAnswers + " / 10");

        retrieveAnswerList();

        if (level < 10) {
            getARandomQuestion();
        }
    }

    private void retrieveAnswerList() {
        String jsonAnswers = sharedPreferences.getString(ANSWERS_KEY, null);
        if (jsonAnswers != null) {
            Type type = new TypeToken<List<Boolean>>() {}.getType();
            answerList = gson.fromJson(jsonAnswers, type);
        } else {
            answerList = new ArrayList<>();
        }
    }

    private void saveAnswerList() {
        String jsonAnswers = gson.toJson(answerList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ANSWERS_KEY, jsonAnswers);
        editor.apply();
    }

    public void onButtonClick(View view) {
        recreate();
    }

    private void getARandomQuestion() {
        // Get random number for question
        int firstNumber = new Random().nextInt(10);
        int secondNumber = new Random().nextInt(10);

        // Get random operation (+, -, *)
        int operation = new Random().nextInt(3) + 1;

        // Get three options
        List<String> options = generateOptions(firstNumber, secondNumber, operation);

        if (operation == 1) {
            realOperation = "+";
            rightAnswer = firstNumber + secondNumber;
            textQuestion.setText(firstNumber + " " + realOperation + " " + secondNumber + " = ?");
        } else if (operation == 2) {
            realOperation = "*";
            rightAnswer = firstNumber * secondNumber;
            textQuestion.setText(firstNumber + " " + realOperation + " " + secondNumber + " = ?");
        } else if (operation == 3) {
            realOperation = "-";
            if (firstNumber < secondNumber) {
                rightAnswer = secondNumber - firstNumber;
                textQuestion.setText(secondNumber + " " + realOperation + " " + firstNumber + " = ?");
            } else {
                rightAnswer = firstNumber - secondNumber;
                textQuestion.setText(firstNumber + " " + realOperation + " " + secondNumber + " = ?");
            }
        }

        // Shuffle the options
        Collections.shuffle(options);

        OptionAdapter adapter = new OptionAdapter(options);
        recyclerViewOptions.setAdapter(adapter);
        recyclerViewOptions.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new OptionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (Integer.parseInt(options.get(position)) == rightAnswer) {
                    options.set(position, options.get(position) + " (Correct)");
                    correctAnswers++;
                    answerList.add(true);
                } else {
                    options.set(position, options.get(position) + " (Wrong)");
                    answerList.add(false);
                }

                adapter.notifyDataSetChanged();

                level++;
                textLevel.setText("Q: " + level + " / 10");
                textRightAnswered.setText("RA: " + correctAnswers + " / 10");

                saveAnswerList();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (level < 10) {
                            options.remove(options.size() - 1);
                            getARandomQuestion();
                        } else {
                            textResult.setText("You answered " + correctAnswers + " / 10");
                            textResult.setVisibility(View.VISIBLE);
                        }
                    }
                }, 1000); // 1 sec
            }
        });
    }

    private List<String> generateOptions(int firstNumber, int secondNumber, int operation) {
        List<String> options = new ArrayList<>();

        if (operation == 1) {
            // Addition operation
            options.add(String.valueOf(firstNumber + secondNumber));

            // Generate two random incorrect options
            options.add(String.valueOf(generateRandomOption(firstNumber + secondNumber)));
            options.add(String.valueOf(generateRandomOption(firstNumber + secondNumber)));
        } else if (operation == 2) {
            // Multiplication operation
            options.add(String.valueOf(firstNumber * secondNumber));

            // Generate two random incorrect options
            options.add(String.valueOf(generateRandomOption(firstNumber * secondNumber)));
            options.add(String.valueOf(generateRandomOption(firstNumber * secondNumber)));
        } else if (operation == 3) {
            // Subtraction operation
            if (firstNumber < secondNumber) {
                options.add(String.valueOf(secondNumber - firstNumber));

                // Generate two random incorrect options
                options.add(String.valueOf(generateRandomOption(secondNumber - firstNumber)));
                options.add(String.valueOf(generateRandomOption(secondNumber - firstNumber)));
            } else {
                options.add(String.valueOf(firstNumber - secondNumber));

                // Generate two random incorrect options
                options.add(String.valueOf(generateRandomOption(firstNumber - secondNumber)));
                options.add(String.valueOf(generateRandomOption(firstNumber - secondNumber)));
            }
        }

        return options;
    }

    private int generateRandomOption(int correctOption) {
        int randomOption;
        do {
            randomOption = new Random().nextInt(100);
        } while (randomOption == correctOption);
        return randomOption;
    }
}
