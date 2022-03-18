package com.google.android.settings.gestures.assist.bubble;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.format.DateFormat;
import com.google.android.settings.gestures.assist.AssistGestureHelper;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class AssistGestureGameDrawable extends Drawable {
    private AssistGestureHelper mAssistGestureHelper;
    private Rect mBounds;
    private boolean mBubbleTouchedBottom;
    private Context mContext;
    private TimeAnimator mDriftAnimation;
    private int mGameState;
    private GameStateListener mGameStateListener;
    private int mKilledBubbles;
    private long mLastGestureTime;
    private float mLastProgress;
    private Bubble mLastShrunkBubble;
    private int mLastStage;
    private float mLastTime;
    private float mNextBubbleTime;
    private Paint mPaint;
    private boolean mServiceConnected;
    private int mTopKilledBubbles;
    private long mTopKilledBubblesDate;
    private Vibrator mVibrator;
    private boolean mBubbleShouldShrink = true;
    private AssistGestureHelper.GestureListener mGestureListener = new AssistGestureHelper.GestureListener() { // from class: com.google.android.settings.gestures.assist.bubble.AssistGestureGameDrawable.1
        @Override // com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
        public void onGestureProgress(float f, int i) {
            AssistGestureGameDrawable.this.onGestureProgress(f, i);
        }

        @Override // com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
        public void onGestureDetected() {
            AssistGestureGameDrawable.this.onGestureDetected();
        }
    };
    private VibrationEffect mErrorVibrationEffect = VibrationEffect.get(1);
    private List<Bubble> mBubbles = new ArrayList();
    private List<Bubble> mDeadBubbles = new ArrayList();
    private List<SpiralingAndroid> mSpiralingAndroids = new ArrayList();

    /* loaded from: classes2.dex */
    public interface GameStateListener {
        void gameStateChanged(int i);

        void updateScoreText(String str);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public AssistGestureGameDrawable(Context context, GameStateListener gameStateListener) {
        this.mContext = context;
        this.mAssistGestureHelper = new AssistGestureHelper(context);
        this.mGameStateListener = gameStateListener;
        this.mVibrator = (Vibrator) context.getSystemService(Vibrator.class);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.mTopKilledBubbles = Settings.Secure.getIntForUser(context.getContentResolver(), "assist_gesture_egg_top_score", 0, -2);
        this.mTopKilledBubblesDate = Settings.Secure.getLongForUser(context.getContentResolver(), "assist_gesture_egg_top_score_time", 0L, -2);
        updateScoreText();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyGameStateChanged() {
        GameStateListener gameStateListener = this.mGameStateListener;
        if (gameStateListener != null) {
            gameStateListener.gameStateChanged(this.mGameState);
        }
    }

    private void updateScoreText() {
        String charSequence = DateFormat.format("MM/dd/yyyy HH:mm:ss", this.mTopKilledBubblesDate).toString();
        GameStateListener gameStateListener = this.mGameStateListener;
        gameStateListener.updateScoreText("" + this.mKilledBubbles + "/" + this.mTopKilledBubbles + " " + charSequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGestureProgress(float f, int i) {
        Bubble bubble;
        if (this.mGameState == 3) {
            if (i == 0 && this.mLastStage == 2) {
                this.mVibrator.vibrate(this.mErrorVibrationEffect);
            }
            if (i == 0) {
                this.mBubbleShouldShrink = true;
            }
            synchronized (this) {
                int i2 = 0;
                while (true) {
                    if (i2 >= this.mBubbles.size()) {
                        break;
                    } else if (this.mBubbles.get(i2).getState() != 0) {
                        i2++;
                    } else if (i == 0 || this.mBubbles.get(0).equals(this.mLastShrunkBubble)) {
                        this.mBubbleShouldShrink = true;
                        this.mLastShrunkBubble = this.mBubbles.get(0);
                    } else {
                        this.mBubbleShouldShrink = false;
                    }
                }
                Bubble bubble2 = this.mLastShrunkBubble;
                if (bubble2 != null && this.mBubbleShouldShrink && bubble2.getState() == 0) {
                    this.mLastShrunkBubble.setSize(Math.max((int) (bubble.getOriginalSize() - (this.mLastShrunkBubble.getOriginalSize() * f)), 16));
                }
            }
            this.mLastProgress = f;
            this.mLastStage = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGestureDetected() {
        if (this.mGameState == 3) {
            this.mLastProgress = 0.0f;
            this.mLastStage = 0;
            this.mBubbleShouldShrink = true;
            long currentTimeMillis = System.currentTimeMillis();
            this.mLastGestureTime = currentTimeMillis;
            if (this.mLastShrunkBubble != null) {
                synchronized (this) {
                    this.mLastShrunkBubble.setState(1);
                }
                int i = this.mKilledBubbles + 1;
                this.mKilledBubbles = i;
                if (i > this.mTopKilledBubbles) {
                    this.mTopKilledBubbles = i;
                    this.mTopKilledBubblesDate = currentTimeMillis;
                    Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "assist_gesture_egg_top_score", this.mTopKilledBubbles, -2);
                    Settings.Secure.putLongForUser(this.mContext.getContentResolver(), "assist_gesture_egg_top_score_time", this.mTopKilledBubblesDate, -2);
                }
                this.mNextBubbleTime = 0.0f;
                updateScoreText();
            }
        }
    }

    public void disconnectService() {
        this.mAssistGestureHelper.setListener(null);
        this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
        this.mServiceConnected = false;
    }

    private void connectService() {
        this.mAssistGestureHelper.bindToElmyraServiceProxy();
        this.mAssistGestureHelper.setListener(this.mGestureListener);
        this.mServiceConnected = true;
    }

    public void pauseGame() {
        if (this.mGameState != 1) {
            this.mGameState = 1;
            notifyGameStateChanged();
            disconnectService();
            this.mNextBubbleTime -= this.mLastTime;
            TimeAnimator timeAnimator = this.mDriftAnimation;
            if (timeAnimator != null) {
                timeAnimator.pause();
            }
        }
    }

    private void resetGameState() {
        resetSpiralingAndroids(this.mBounds);
        this.mDeadBubbles.clear();
        this.mKilledBubbles = 0;
        updateScoreText();
        this.mBubbleTouchedBottom = false;
    }

    public void startGame(boolean z) {
        if (this.mBounds == null) {
            this.mGameState = 2;
            notifyGameStateChanged();
        } else if (this.mGameState != 3) {
            if (z) {
                resetGameState();
            }
            connectService();
            if (this.mBubbleTouchedBottom) {
                this.mGameState = 4;
            } else {
                this.mGameState = 3;
                notifyGameStateChanged();
            }
            if (this.mDriftAnimation == null) {
                TimeAnimator timeAnimator = new TimeAnimator();
                this.mDriftAnimation = timeAnimator;
                timeAnimator.setTimeListener(new TimeAnimator.TimeListener() { // from class: com.google.android.settings.gestures.assist.bubble.AssistGestureGameDrawable.2
                    @Override // android.animation.TimeAnimator.TimeListener
                    public void onTimeUpdate(TimeAnimator timeAnimator2, long j, long j2) {
                        AssistGestureGameDrawable.this.mLastTime = ((float) j) * 0.001f;
                        if (AssistGestureGameDrawable.this.mGameState == 3) {
                            synchronized (this) {
                                if (AssistGestureGameDrawable.this.mLastTime > AssistGestureGameDrawable.this.mNextBubbleTime) {
                                    AssistGestureGameDrawable.this.mBubbles.add(new Bubble(AssistGestureGameDrawable.this.mBounds));
                                    AssistGestureGameDrawable assistGestureGameDrawable = AssistGestureGameDrawable.this;
                                    assistGestureGameDrawable.mNextBubbleTime = assistGestureGameDrawable.mLastTime + 1.0f;
                                }
                                for (int size = AssistGestureGameDrawable.this.mBubbles.size() - 1; size >= 0; size--) {
                                    Bubble bubble = (Bubble) AssistGestureGameDrawable.this.mBubbles.get(size);
                                    bubble.update(j, j2);
                                    if (bubble.isBubbleDead()) {
                                        AssistGestureGameDrawable.this.mBubbles.remove(size);
                                    } else if (bubble.isBubbleTouchingTop() && bubble.getState() == 0) {
                                        AssistGestureGameDrawable.this.mDeadBubbles.add(bubble);
                                        AssistGestureGameDrawable.this.mBubbles.remove(size);
                                    } else if (AssistGestureGameDrawable.this.hasCollisionWithDeadBubbles(bubble)) {
                                        if (bubble.getPoint().y + bubble.getSize() > AssistGestureGameDrawable.this.mBounds.bottom) {
                                            AssistGestureGameDrawable.this.mGameState = 4;
                                            AssistGestureGameDrawable.this.mBubbleTouchedBottom = true;
                                        }
                                        if (bubble.getState() == 0) {
                                            AssistGestureGameDrawable.this.mDeadBubbles.add(bubble);
                                            AssistGestureGameDrawable.this.mBubbles.remove(size);
                                        }
                                    }
                                }
                            }
                        }
                        if (AssistGestureGameDrawable.this.mGameState == 4) {
                            synchronized (this) {
                                boolean z2 = false;
                                for (int i = 0; i < AssistGestureGameDrawable.this.mSpiralingAndroids.size(); i++) {
                                    SpiralingAndroid spiralingAndroid = (SpiralingAndroid) AssistGestureGameDrawable.this.mSpiralingAndroids.get(i);
                                    if (spiralingAndroid.getAndroid().getBounds().bottom < AssistGestureGameDrawable.this.mBounds.bottom) {
                                        spiralingAndroid.update(j, j2);
                                        z2 = true;
                                    }
                                }
                                if (AssistGestureGameDrawable.this.mServiceConnected) {
                                    AssistGestureGameDrawable.this.disconnectService();
                                }
                                if (!z2) {
                                    AssistGestureGameDrawable.this.notifyGameStateChanged();
                                    AssistGestureGameDrawable.this.mDriftAnimation.pause();
                                }
                            }
                        }
                        AssistGestureGameDrawable.this.invalidateSelf();
                    }
                });
            }
            this.mDriftAnimation.start();
        }
    }

    private double distance(Bubble bubble, Bubble bubble2) {
        PointF point = bubble.getPoint();
        PointF point2 = bubble2.getPoint();
        return Math.sqrt(Math.pow(point2.x - point.x, 2.0d) + Math.pow(point2.y - point.y, 2.0d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasCollisionWithDeadBubbles(Bubble bubble) {
        Bubble bubble2;
        for (int i = 0; i < this.mDeadBubbles.size(); i++) {
            if (distance(bubble, this.mDeadBubbles.get(i)) < bubble.getSize() + bubble2.getSize()) {
                return true;
            }
        }
        return false;
    }

    private void resetSpiralingAndroids(Rect rect) {
        synchronized (this) {
            this.mSpiralingAndroids.clear();
            for (int i = 0; i < 40; i++) {
                this.mSpiralingAndroids.add(new SpiralingAndroid(this.mContext, rect));
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect rect) {
        this.mBounds = rect;
        if (this.mGameState == 2) {
            startGame(true);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int i;
        float f;
        float f2;
        long currentTimeMillis = System.currentTimeMillis();
        canvas.save();
        synchronized (this) {
            for (int i2 = 0; i2 < this.mBubbles.size(); i2++) {
                Bubble bubble = this.mBubbles.get(i2);
                this.mPaint.setColor(bubble.getColor());
                canvas.drawCircle(bubble.getPoint().x, bubble.getPoint().y, bubble.getSize(), this.mPaint);
            }
            for (int i3 = 0; i3 < this.mDeadBubbles.size(); i3++) {
                Bubble bubble2 = this.mDeadBubbles.get(i3);
                this.mPaint.setColor(bubble2.getColor());
                canvas.drawCircle(bubble2.getPoint().x, bubble2.getPoint().y, bubble2.getSize(), this.mPaint);
            }
        }
        this.mPaint.setColor(-1);
        this.mPaint.setAlpha(180);
        float height = this.mBounds.height() - 80;
        float height2 = this.mBounds.height();
        if (currentTimeMillis - this.mLastGestureTime < 450) {
            float centerX = (float) ((this.mBounds.centerX() * (currentTimeMillis - this.mLastGestureTime)) / 450);
            f2 = this.mBounds.centerX() - centerX;
            f = this.mBounds.centerX() + centerX;
        } else {
            float centerX2 = this.mBounds.centerX() * this.mLastProgress;
            f = this.mBounds.width() - centerX2;
            f2 = centerX2;
        }
        canvas.drawRect(f2, height, f, height2, this.mPaint);
        if (this.mGameState != 3) {
            synchronized (this) {
                for (i = 0; i < this.mSpiralingAndroids.size(); i++) {
                    canvas.save();
                    SpiralingAndroid spiralingAndroid = this.mSpiralingAndroids.get(i);
                    Drawable android2 = spiralingAndroid.getAndroid();
                    canvas.rotate(spiralingAndroid.getCurrentRotation(), android2.getBounds().centerX(), android2.getBounds().centerY());
                    spiralingAndroid.getAndroid().draw(canvas);
                    canvas.restore();
                }
            }
        }
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }
}
