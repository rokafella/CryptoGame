package com.example.rohit.cryptogame;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class FinalScreen extends Fragment {

    public TextView selfScore;
    public TextView oppScore;
    public TextView waitText;
    public TextView result;
    public ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_final_screen, container, false);
        selfScore = (TextView) view.findViewById(R.id.txt_selfscore);
        oppScore = (TextView) view.findViewById(R.id.txt_oppscore);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        waitText = (TextView) view.findViewById(R.id.txt_wait);
        result = (TextView) view.findViewById(R.id.txt_result);
        progressBar.setVisibility(View.VISIBLE);
        waitText.setVisibility(View.VISIBLE);
        selfScore.setText("You: "+((MainActivity)getActivity()).getUserScore());
        return view;
    }

    public void updateScore(String s) {
        progressBar.setVisibility(View.GONE);
        waitText.setVisibility(View.GONE);
        oppScore.setText("Opponent: "+s);

        if (Integer.parseInt(s) == ((MainActivity)getActivity()).getUserScore()) {
            result.setText("TIE");
        }
        else if (Integer.parseInt(s) < ((MainActivity)getActivity()).getUserScore()) {
            result.setText("YOU WIN");
        }
        else {
            result.setText("YOU LOOSE");
        }
    }
}
