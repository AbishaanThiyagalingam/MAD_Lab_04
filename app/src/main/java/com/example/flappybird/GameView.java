package com.example.flappybird;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Random;

public class GameView extends View {
    //This is custom view class

    Handler handler;
    Runnable runnable;
    final int UPDATE_MILLIS = 30;
    Bitmap background;
    Bitmap toptube,bottomtube;
    Display display;
    Point point;
    int dWidth, dHeight;
    Rect rect;
    Bitmap[] birds;
    int birdFrame = 0;
    int velocity =0, gravity=3;

    //keep track bird position
    int birdX, birdY;
    boolean gameState = false;
    int gap = 1300;  //gap between top pipe and bottom pipe
    int minTubeOffset, maxTubeOffset;
    int numberOfTubes = 4;
    int distanceBetweenTubes;
    int[] tubeX = new int[numberOfTubes];
    int[] topTubeY = new int[numberOfTubes];
   Random random;
   int tubeVelocity = 8;


    float scaleFactor = 0.4f; // You can adjust this value as needed
    Bitmap scaledTopTube,scaledBottomTube;


    public GameView(Context context) {
        super(context);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
        toptube = BitmapFactory.decodeResource(getResources(), R.drawable.toppipe);
        bottomtube = BitmapFactory.decodeResource(getResources(), R.drawable.bottompipe);

         scaledTopTube = Bitmap.createScaledBitmap(toptube, (int) (toptube.getWidth() * scaleFactor), (int) (toptube.getHeight() * scaleFactor), true);
         scaledBottomTube = Bitmap.createScaledBitmap(bottomtube, (int) (bottomtube.getWidth() * scaleFactor), (int) (bottomtube.getHeight() * scaleFactor), true);

        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y;
        rect = new Rect(0,0,dWidth,dHeight);
        birds = new Bitmap[2];
        birds[0] = BitmapFactory.decodeResource(getResources(), R.drawable.bird1);
        birds[1] = BitmapFactory.decodeResource(getResources(), R.drawable.bird2);
        birdX = dWidth / 2 - (int) (birds[0].getWidth() * scaleFactor) / 2; // Adjusted position
        birdY = dHeight / 2 - (int) (birds[0].getHeight() * scaleFactor) / 2; // Adjusted position
        distanceBetweenTubes = dHeight * 3/4;
        minTubeOffset = gap/2;
        maxTubeOffset = dHeight- minTubeOffset - gap;
        random = new Random();
        for(int i=0; i<numberOfTubes; i++){
            tubeX[i] = dWidth + i * distanceBetweenTubes;
            topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
            tubePassed[i] = false;
        }


    }

    int score = 0;
    boolean[] tubePassed = new boolean[numberOfTubes];

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Draw the background on canvas
        canvas.drawBitmap(background, null, rect, null);

        if (birdFrame == 0) {
            birdFrame = 1;
        } else {
            birdFrame = 0;
        }

        if (gameState) {
            // Update bird position
            if (birdY < dHeight - birds[0].getHeight() || velocity < 0) { // Bird does not go beyond the edges
                velocity += gravity;
                birdY += velocity;

                // Check for collision with tubes
                for (int i = 0; i < numberOfTubes; i++) {
                    if (birdX + birds[0].getWidth() >= tubeX[i] &&
                            birdX <= tubeX[i] + scaledTopTube.getWidth() &&
                            (birdY <= topTubeY[i] || birdY + birds[0].getHeight() >= topTubeY[i] + gap)) {
                        // Bird collides with tube, set game state to false (game over)
                        gameState = false;
                        return;
                    }
                }
            } else {
            // Bird has gone beyond the edges, game over
            gameState = false;
            // Display game over message along with the score
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(100);
            canvas.drawText("Game Over", dWidth / 2 - 250, dHeight / 2, paint);
            canvas.drawText("Score: " + score, dWidth / 2 - 150, dHeight / 2 + 150, paint); // Adjust the position as needed
                canvas.drawText("Tap to restart", dWidth / 2 - 200, dHeight / 2 + 300, paint);
                return;
        }

            // Update tube positions and draw them on the canvas
            for (int i = 0; i < numberOfTubes; i++) {
                tubeX[i] -= tubeVelocity;
                if (tubeX[i] < -scaledTopTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
                    tubePassed[i] = false; // Reset tubePassed only for the current tube
                }

                // Draw scaled tube images on the canvas
                canvas.drawBitmap(scaledTopTube, tubeX[i], topTubeY[i] - scaledTopTube.getHeight(), null);
                canvas.drawBitmap(scaledBottomTube, tubeX[i], topTubeY[i] + gap, null);

                // Check if bird completely passed this tube and update score
                if (tubeX[i] + scaledTopTube.getWidth() <= birdX && !tubePassed[i]) {
                    score++;
                    tubePassed[i] = true;
                    // Additional logic can be added here if needed
                }
            }
        }

        // Draw bird on the canvas
        canvas.drawBitmap(birds[birdFrame], birdX, birdY, null);
        handler.postDelayed(runnable, UPDATE_MILLIS);

        // Draw score on canvas
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        canvas.drawText("Score: " + score, 50, 100, paint);

    }


    //Get the touch event

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        float tapX = event.getX();
        float tapY = event.getY();

        if(action == MotionEvent.ACTION_DOWN){  //tap is detect on screen
            Log.d("TouchEvent", "Tap X: " + tapX + ", Tap Y: " + tapY); // Add this line for logging

            if (!gameState && tapX >= dWidth / 2 - 400 && tapX <= dWidth / 2 + 400 && tapY >= dHeight / 2 + 200 && tapY <= dHeight / 2 + 400) {
                resetGame();
                return true;
            }
            //here we want to move the bird
            velocity = -30;
            gameState = true;
        }
        return true;
    }

    private void resetGame() {
        // Reset game variables
        score = 0;
        birdY = dHeight / 2 - (int) (birds[0].getHeight() * scaleFactor) / 2;
        for (int i = 0; i < numberOfTubes; i++) {
            tubeX[i] = dWidth + i * distanceBetweenTubes;
            topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
            tubePassed[i] = false;
        }
        // Restart game loop
        handler.postDelayed(runnable, UPDATE_MILLIS);
        // Request redraw
        invalidate();
    }

}
