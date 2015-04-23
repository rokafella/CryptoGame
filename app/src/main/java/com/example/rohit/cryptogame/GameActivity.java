package com.example.rohit.cryptogame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;


public class GameActivity extends Activity {

    public Question[] quiz;
    public HashMap<Integer,Boolean> selector;
    public TextView textQuestion;
    public TextView textNumber;
    public TextView textScore;
    public int score;
    int round;
    public Button[] buttons;
    int currentIndex;
    String opponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle b = getIntent().getExtras();
        opponent = b.getString("ip");
        quiz = new Question[10];
        setQuestion();
        selector = new HashMap<>();
        setMap(selector);
        textNumber = (TextView) findViewById(R.id.quizNumber);
        textQuestion = (TextView) findViewById(R.id.quizQuestion);
        textScore = (TextView) findViewById(R.id.txt_score);
        buttons = new Button[4];
        buttons[0] = (Button) findViewById(R.id.option1);
        buttons[1] = (Button) findViewById(R.id.option2);
        buttons[2] = (Button) findViewById(R.id.option3);
        buttons[3] = (Button) findViewById(R.id.option4);
        score = 0;
        round = 0;
        startGame();
    }

    public void pressOption(View v) {
        String op = getResources().getResourceName(v.getId());
        op = op.substring(op.length() - 1);
        final int option = Integer.parseInt(op)-1;
        switchButtons(false);
        if ( option == quiz[currentIndex].getCorrectAnswer()) {
            buttons[option].setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            score += 10;
            updateScore();
        }
        else {
            buttons[option].setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                buttons[option].setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                startGame();
            }
        }, 3000);
    }

    private void updateScore() {
        textScore.setText("Score: "+score);
    }

    private void switchButtons(boolean b) {
        for (int i=0; i<4; i++) {
            buttons[i].setEnabled(b);
        }
    }

    private void startGame() {
        if (round == 5) {
            Intent output = new Intent();
            output.putExtra("score", score);
            output.putExtra("ip", opponent);
            setResult(RESULT_OK, output);
            finish();
        }
        else {
            switchButtons(true);
            showQuestion(++round);
        }
    }

    private void showQuestion(int n) {
        Random r = new Random();
        currentIndex = r.nextInt(10);
        while (selector.get(currentIndex)){
            currentIndex = r.nextInt(10);
        }
        selector.put(currentIndex,true);
        textNumber.setText("Question "+n);
        textQuestion.setText(quiz[currentIndex].getQuestion());
        String[] options = quiz[currentIndex].getOptions();
        for(int i=0; i<4; i++) {
            buttons[i].setText(options[i]);
        }
    }

    private void setMap(HashMap<Integer, Boolean> selector) {
        for (int i=0; i<10; i++) {
            selector.put(i,false);
        }
    }

    private void setQuestion() {
        quiz[0] = new Question(0,"Which planet's day is longer than its year?");
        String[] s0 = {"Mercury","Venus","Mars","Pluto"};
        quiz[0].setOptions(s0,1);
        quiz[1] = new Question(1,"What can escape the gravity of a Black Hole?");
        String[] s1 = {"Information","Light","Matter","Nothing"};
        quiz[1].setOptions(s1,3);
        quiz[2] = new Question(2,"Which is the largest moon in the solar system?");
        String[] s2 = {"The Moon","Ganymede","Callisto","Titan"};
        quiz[2].setOptions(s2,1);
        quiz[3] = new Question(3,"The largest volcano in the solar system is located on which planet?");
        String[] s3 = {"Earth","Venus","Mars","Jupiter"};
        quiz[3].setOptions(s3,2);
        quiz[4] = new Question(4,"Who was the second man of the Moon?");
        String[] s4 = {"Neil Armstrong","John Glenn","Alan Shepard","Buzz Aldrin"};
        quiz[4].setOptions(s4,3);
        quiz[5] = new Question(5,"What was the first satellite put in space by man?");
        String[] s5 = {"Sputnik 1","Pioneer 1","Explorer 1","Voyager 1"};
        quiz[5].setOptions(s5,0);
        quiz[6] = new Question(6,"What is the most distant object visible to the naked eye?");
        String[] s6 = {"Large Magellanic Cloud","Small Magellanic","Center of the Milky Way","Andromeda"};
        quiz[6].setOptions(s6,3);
        quiz[7] = new Question(7,"What is the closest star to Earth?");
        String[] s7 = {"Sirius","Proxima Centauri","Sun","Vega"};
        quiz[7].setOptions(s7,2);
        quiz[8] = new Question(8,"The first terrestrial creature in space was a dog named what?");
        String[] s8 = {"Laika","Bear","Meeshka","Orion"};
        quiz[8].setOptions(s8,0);
        quiz[9] = new Question(9,"What is the only moon in the solar system with an atmosphere?");
        String[] s9 = {"The Moon","Triton","Titan","Europa"};
        quiz[9].setOptions(s9,2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
