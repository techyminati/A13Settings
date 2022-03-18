package com.google.android.settings.gestures.assist.bubble;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.window.R;
import com.google.android.settings.gestures.assist.AssistGestureHelper;
import com.google.android.settings.gestures.assist.bubble.AssistGestureBubbleActivity;
import com.google.android.settings.gestures.assist.bubble.AssistGestureGameDrawable;
/* loaded from: classes2.dex */
public class AssistGestureBubbleActivity extends Activity {
    private AssistGestureHelper mAssistGestureHelper;
    private TextView mCurrentScoreTextView;
    private AssistGestureGameDrawable mEasterEggDrawable;
    private AssistGesturePlayButtonDrawable mEasterEggPlayDrawable;
    private int mGameState;
    private ImageView mGameView;
    private Handler mHandler;
    private boolean mIsNavigationHidden;
    private ImageView mPlayView;
    private boolean mShouldStartNewGame = true;
    private AssistGestureGameDrawable.GameStateListener mEasterEggListener = new AssistGestureGameDrawable.GameStateListener() { // from class: com.google.android.settings.gestures.assist.bubble.AssistGestureBubbleActivity.1
        @Override // com.google.android.settings.gestures.assist.bubble.AssistGestureGameDrawable.GameStateListener
        public void updateScoreText(String str) {
            AssistGestureBubbleActivity.this.mCurrentScoreTextView.setText(str);
        }

        @Override // com.google.android.settings.gestures.assist.bubble.AssistGestureGameDrawable.GameStateListener
        public void gameStateChanged(int i) {
            AssistGestureBubbleActivity.this.mGameState = i;
            if (i == 4) {
                AssistGestureBubbleActivity.this.pauseGame();
                AssistGestureBubbleActivity.this.mShouldStartNewGame = true;
            }
        }
    };
    private AssistGestureHelper.GestureListener mGestureListener = new AnonymousClass2();

    /* renamed from: com.google.android.settings.gestures.assist.bubble.AssistGestureBubbleActivity$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    class AnonymousClass2 implements AssistGestureHelper.GestureListener {
        @Override // com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
        public void onGestureProgress(float f, int i) {
        }

        AnonymousClass2() {
        }

        @Override // com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
        public void onGestureDetected() {
            AssistGestureBubbleActivity.this.mAssistGestureHelper.setListener(null);
            AssistGestureBubbleActivity.this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
            AssistGestureBubbleActivity.this.mHandler.post(new Runnable() { // from class: com.google.android.settings.gestures.assist.bubble.AssistGestureBubbleActivity$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AssistGestureBubbleActivity.AnonymousClass2.this.lambda$onGestureDetected$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onGestureDetected$0() {
            AssistGestureBubbleActivity assistGestureBubbleActivity = AssistGestureBubbleActivity.this;
            assistGestureBubbleActivity.startGame(assistGestureBubbleActivity.mShouldStartNewGame);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGameState() {
        if (this.mPlayView.getVisibility() != 4 || !this.mIsNavigationHidden) {
            pauseGame();
        } else {
            startGame(this.mShouldStartNewGame);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pauseGame() {
        if (this.mPlayView.getVisibility() == 4) {
            this.mPlayView.setVisibility(0);
        }
        this.mEasterEggDrawable.pauseGame();
        this.mAssistGestureHelper.bindToElmyraServiceProxy();
        this.mAssistGestureHelper.setListener(this.mGestureListener);
    }

    public void startGame(boolean z) {
        enterFullScreen();
        if (this.mPlayView.getVisibility() == 0) {
            this.mPlayView.setVisibility(4);
        }
        this.mEasterEggDrawable.startGame(z);
        this.mShouldStartNewGame = false;
    }

    private void registerDecorViewListener() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() { // from class: com.google.android.settings.gestures.assist.bubble.AssistGestureBubbleActivity.3
            @Override // android.view.View.OnSystemUiVisibilityChangeListener
            public void onSystemUiVisibilityChange(int i) {
                if ((i & 4) == 0) {
                    AssistGestureBubbleActivity.this.mIsNavigationHidden = false;
                } else {
                    AssistGestureBubbleActivity.this.mIsNavigationHidden = true;
                }
                AssistGestureBubbleActivity.this.updateGameState();
            }
        });
    }

    private void unregisterDecorViewListener() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.assist_gesture_bubble_activity);
        getWindow().setBackgroundDrawableResource(R.drawable.assist_gesture_bubble_activity_bg);
        this.mHandler = new Handler(getMainLooper());
        this.mAssistGestureHelper = new AssistGestureHelper(getApplicationContext());
        this.mCurrentScoreTextView = (TextView) findViewById(R.id.current_score);
        this.mGameView = (ImageView) findViewById(R.id.game_view);
        AssistGestureGameDrawable assistGestureGameDrawable = new AssistGestureGameDrawable(getApplicationContext(), this.mEasterEggListener);
        this.mEasterEggDrawable = assistGestureGameDrawable;
        this.mGameView.setImageDrawable(assistGestureGameDrawable);
        this.mPlayView = (ImageView) findViewById(R.id.play_view);
        AssistGesturePlayButtonDrawable assistGesturePlayButtonDrawable = new AssistGesturePlayButtonDrawable();
        this.mEasterEggPlayDrawable = assistGesturePlayButtonDrawable;
        assistGesturePlayButtonDrawable.setAlpha(200);
        this.mPlayView.setImageDrawable(this.mEasterEggPlayDrawable);
        this.mPlayView.setOnTouchListener(new View.OnTouchListener() { // from class: com.google.android.settings.gestures.assist.bubble.AssistGestureBubbleActivity.4
            boolean mTouching;

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked != 0) {
                    if (actionMasked != 1) {
                        if (actionMasked == 3) {
                            this.mTouching = false;
                        }
                    } else if (this.mTouching) {
                        AssistGestureBubbleActivity.this.mPlayView.setVisibility(4);
                        AssistGestureBubbleActivity.this.enterFullScreen();
                        AssistGestureBubbleActivity assistGestureBubbleActivity = AssistGestureBubbleActivity.this;
                        assistGestureBubbleActivity.startGame(assistGestureBubbleActivity.mShouldStartNewGame);
                        this.mTouching = false;
                    }
                } else if (AssistGestureBubbleActivity.this.mEasterEggPlayDrawable.hitTest(motionEvent.getX(), motionEvent.getY())) {
                    this.mTouching = true;
                } else {
                    this.mTouching = false;
                }
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(3846);
    }

    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        this.mEasterEggDrawable.pauseGame();
        unregisterDecorViewListener();
        this.mAssistGestureHelper.setListener(null);
        this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        registerDecorViewListener();
        enterFullScreen();
    }
}
