package com.offsidegame.offside.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import com.facebook.Profile;
import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.fragments.QuestionsFragment;
import com.offsidegame.offside.activities.fragments.ScoresFragment;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.Score;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KFIR on 11/21/2016.
 */

public class QuestionAdapter extends ArrayAdapter<Question> {

    private Context context;
    public QuestionAdapter(Context context, ArrayList<Question> questions) {
        super(context, 0, questions);
        this.context = context;
    }




    private class ViewHolder {
        public TextView questionQuestionText;
        public ImageView questionRightWrongAnswerIndicator;
        public ImageView questionFbPicture;
        //public TextView answerNumber;
        public TextView questionAnswerText;
        public TextView questionPercentUsersAnswered;
        public TextView questionScore;
       // public LinearLayout questionAnswerListItem;
//        public TextView questionAnsweredByText;
//        public TextView questionPercentSignText;
//        public TextView questionYouCanEarnText;
//        public TextView questionDividerText;
//        public LinearLayout questionBackgroundRoot;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Question question = getItem(position);
        Answer answer = new Answer();
        for (Answer ans: question.getAnswers())
        {
            if (ans.isTheAnswerOfTheUser())
                answer = ans;
        }

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.questionQuestionText = (TextView) convertView.findViewById(R.id.question_question_text);
            viewHolder.questionRightWrongAnswerIndicator = (ImageView) convertView.findViewById(R.id.question_right_wrong_answer_indicator);
            viewHolder.questionFbPicture = (ImageView) convertView.findViewById(R.id.question_fb_picture);
            //viewHolder.answerNumber = (TextView) convertView.findViewById(R.id.answer_number);
            viewHolder.questionAnswerText = (TextView) convertView.findViewById(R.id.question_answer_text);
            viewHolder.questionPercentUsersAnswered = (TextView) convertView.findViewById(R.id.question_percent_users_answered);
            viewHolder.questionScore = (TextView) convertView.findViewById(R.id.question_score);
            //viewHolder.questionAnswerListItem = (LinearLayout) convertView.findViewById(R.id.question_answer_list_item);
//            viewHolder.questionAnsweredByText = (TextView) convertView.findViewById(R.id.question_answered_by_text);
//            viewHolder.questionPercentSignText = (TextView) convertView.findViewById(R.id.question_percent_sign_text);
//            viewHolder.questionYouCanEarnText = (TextView) convertView.findViewById(R.id.question_you_can_earn_text);
//            viewHolder.questionDividerText = (TextView) convertView.findViewById(R.id.question_divider_text);
//            viewHolder.questionBackgroundRoot = (LinearLayout) convertView.findViewById(R.id.question_background_root);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ImageView fbProfilePicture  = viewHolder.questionFbPicture;
        viewHolder.questionFbPicture.setAlpha(0.0f);
        loadFbImage(fbProfilePicture);

        viewHolder.questionQuestionText.setText(question.getQuestionText());

         if (!question.isActive()) {
            if (answer.isCorrect())
                viewHolder.questionRightWrongAnswerIndicator.setImageResource(R.drawable.ic_done_black_24dp);
            else
                viewHolder.questionRightWrongAnswerIndicator.setImageResource(R.drawable.ic_clear_red_24dp);
        }

        viewHolder.questionAnswerText.setText(answer.getAnswerText());


        viewHolder.questionPercentUsersAnswered.setText(Long.toString(Math.round(answer.getPercentUsersAnswered())));
        viewHolder.questionScore.setText(Long.toString(Math.round(answer.getScore())));






        return convertView;

    }


    private void loadFbImage(final ImageView fbProfilePicture) {
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
        String userPictureUrl = settings.getString(context.getString(R.string.user_profile_picture_url_key), "");
        Uri imageUri = Uri.parse(userPictureUrl);

        //fbProfilePicture.setImageURI(imageUri);

        Picasso.with(context).load(imageUri).into(fbProfilePicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) fbProfilePicture.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                fbProfilePicture.setImageDrawable(roundedImage);
                fbProfilePicture.animate().alpha(1.1f).setDuration(1000).start();
            }

            @Override
            public void onError() {

            }
        });
    }

}
