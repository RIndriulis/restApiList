package raimundasindriulis.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    CardView titleCard;
    CardView locoCard;
    CardView ageCard;
    float screenWidth;

    static String ROOT_URL = ".ieskok.lt/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
        screenWidth = (float) metrics.widthPixels;
        TextView title = (TextView) findViewById(R.id.title);
        final TextView age = (TextView) findViewById(R.id.age);
        final TextView loco = (TextView) findViewById(R.id.loco);
        ImageView image = (ImageView) findViewById(R.id.image);
        titleCard = (CardView) findViewById(R.id.titleCard);
        locoCard = (CardView) findViewById(R.id.locoCard);
        ageCard = (CardView) findViewById(R.id.ageCard);
        locoCard.setTranslationX(screenWidth*-1);
        ageCard.setTranslationX(screenWidth);
        title.setText(getIntent().getStringExtra("vardas"));
        age.setText(getIntent().getStringExtra("age"));
        loco.setText(getIntent().getStringExtra("loco"));
        Picasso.with(getBaseContext()).load("http://f" + getIntent().getIntExtra("f", 0) + ROOT_URL + getIntent().getIntExtra("id", 0) + ".jpg").into(image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.arcmotion);
            getWindow().setSharedElementEnterTransition(transition);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    titleCard.animate().scaleX(1).scaleY(1);
                    locoCard.animate().translationX(0).start();
                    ageCard.animate().translationX(0).start();
                }
                @Override
                public void onTransitionResume(Transition transition) {
                }
                @Override
                public void onTransitionCancel(Transition transition) {
                }
                @Override
                public void onTransitionStart(Transition transition) {
                }
                @Override
                public void onTransitionPause(Transition transition) {

                }
            });
        }

    }

    public void onBackPressed() {
        locoCard.animate().translationX(screenWidth*-1).start();
        ageCard.animate().translationX(screenWidth).start();
        titleCard.animate().scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                supportFinishAfterTransition();
            }
        });
    }

}
