package com.offsidegame.offside;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.QuestionEvent;

public class AnswerQuestionActivity extends AppCompatActivity implements IQuestionHolder {

    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private Question question;

    private TextView questionTextView;

    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            isBoundToSignalRService = true;
            //EventBus.getDefault().post(new SignalRServiceBoundEvent(context));
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBoundToSignalRService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        //get the question
        String newQuestionKey = QuestionEvent.QuestionStates.NEW_QUESTION;
        question = (Question) getIntent().getSerializableExtra(newQuestionKey);
//        //pass to fragment
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(newQuestionKey, question);
//        AnswersFragment myFrag = new AnswersFragment();
//        myFrag.setArguments(bundle);

        questionTextView = (TextView) findViewById(R.id.question_text);
        questionTextView.setText(question.getQuestionText());


        Intent bindServiceIntent = new Intent();
        bindServiceIntent.setClass(context, SignalRService.class);
        bindService(bindServiceIntent, signalRServiceConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    public void onStart() {
        super.onStart();
     //   EventBus.getDefault().register(context);
    }

    @Override
    public void onStop() {
       // EventBus.getDefault().unregister(context);
        // Unbind from the service
        if (isBoundToSignalRService) {
            unbindService(signalRServiceConnection);
            isBoundToSignalRService = false;
        }

        super.onStop();
    }


    @Override
    public Question getQuestion() {
        return question;
    }
}
