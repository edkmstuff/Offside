package com.offsidegame.offside;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflates the menu; this addsitems to the action bar if it is exist
        getMenuInflater().inflate(R.menu.admin_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = true;
        int id= item.getItemId();

        switch (id){
            case R.id.action_createGame:
                onClickMenuShowCreateGame(item);
                break;
            case R.id.action_askQuestion:
                onClickMenuShowAskQuestion(item);
                break;
            case R.id.action_answerQuestion:
                onClickMenuShowAnswerQuestion(item);
                break;
            default:
                handled = super.onOptionsItemSelected(item);
        }

        return handled;

    }

    void onClickMenuShowCreateGame (MenuItem item){
        Intent intent = new Intent(this,CreateGameActivity.class);
        startActivity(intent);

    }
    void onClickMenuShowAskQuestion (MenuItem item){
        //Intent intent = new Intent(this,AskQuestionActivity.class);
        Intent intent = new Intent(this,QuestionActivity.class);
        startActivity(intent);;
    }

    void onClickMenuShowAnswerQuestion (MenuItem item){
        Intent intent = new Intent(this,AnswerQuestionActivity.class);
        startActivity(intent);;
    }

}
