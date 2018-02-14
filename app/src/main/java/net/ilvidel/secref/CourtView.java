package net.ilvidel.secref;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class CourtView extends View {

    private int courtHeight;
    private int courtWidth;
    private int left;
    private int top;
    private Point center;
    private Point[] localCoords = new Point[6];
    private Point[] visitCoords = new Point[6];

    private int[] rightPlayers = new int[6];
    private int[] leftPlayers = new int[6];
    private boolean isServingLeft;
    private Point selected = null;

    public CourtView(Context context) {
        super(context);
        init();
    }

    public CourtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        center = new Point(0, 0);
        int bgColor = 0XFF63B7F8;
        setBackgroundColor(bgColor);

        isServingLeft = true;
        leftPlayers = new int[6];
        rightPlayers = new int[6];

        for (int i=0; i<6; i++){
            localCoords[i] = new Point();
            visitCoords[i] = new Point();
        }
    }

    public int[] getLeftPlayers() {
        return leftPlayers;
    }

    public void setLeftPlayers(int[] leftPlayers) {
        this.leftPlayers = leftPlayers;
        invalidate();
    }

    public int[] getRightPlayers() {
        return rightPlayers;
    }

    public void setRightPlayers(int[] rightPlayers) {
        this.rightPlayers = rightPlayers;
        invalidate();
    }

    public void setIsServingLeft(boolean isServingLeft) {
        this.isServingLeft = isServingLeft;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calcDimensions();

        drawFloor(canvas);
        drawLines(canvas);
        drawPlayers(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec / 2);
    }

    private void calcDimensions() {
        courtWidth = (int) Math.min(this.getWidth() * 0.9, this.getHeight() * 1.8);
        courtHeight = courtWidth / 2;

        center.x =  getWidth() / 2;
        center.y = getHeight() / 2;

        left = (int) (center.x - courtWidth / 2.0);
        top = (int) (center.y - courtHeight / 2.0);
    }

    private void drawFloor(Canvas canvas) {
        int floorColor = 0xFFFFB95B;
        Paint paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(floorColor);
        canvas.drawRect(left, top, left + courtWidth, top + courtHeight, paint);
    }

    private void drawLines(Canvas canvas) {
        int lineColor = Color.WHITE;
        Paint paint = new Paint();
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.STROKE);

        // perimeter
        paint.setStrokeWidth(3);
        canvas.drawRect(left, top, left + courtWidth, top + courtHeight, paint);

        // local attack line
        canvas.drawLine(center.x - courtWidth / 6, top, center.x - courtWidth / 6, top + courtHeight, paint);

        // visitor attack line
        canvas.drawLine(center.x + courtWidth / 6, top, center.x + courtWidth / 6, top + courtHeight, paint);

        // center line
        paint.setStrokeWidth(8);
        canvas.drawLine(center.x, top, center.x, top + courtHeight, paint);
    }

    private void drawPlayers(Canvas canvas) {
        calcPoints();
        float textSize = courtWidth / 12f;
        float cornerRadius = 16f;
        Paint paint = new Paint();

        paint.setTextSize(textSize);
        paint.setFakeBoldText(true);

//        // draw a circle on the selected player
//        if(selected != null) {
//            paint.setColor(Color.YELLOW);
//            canvas.drawRoundRect(
//                    selected.x - textSize,
//                    selected.y - textSize*0.7f,
//                    selected.x + textSize,
//                    selected.y + textSize*0.7f,
//                    cornerRadius, cornerRadius,
//                    paint);
//        }

        // start drawing players
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 6; i++) {
            String text;
            Rect bounds = new Rect();

            if (i == 0 && isServingLeft) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(20f);
                canvas.drawRoundRect(
                        localCoords[i].x - textSize,
                        localCoords[i].y - textSize*0.7f,
                        localCoords[i].x + textSize,
                        localCoords[i].y +textSize*0.7f,
                        cornerRadius, cornerRadius,
                        paint
                );
                paint.setStyle(Paint.Style.FILL);
            }

            paint.setColor(Color.RED);
            canvas.drawRoundRect(
                    localCoords[i].x - textSize,
                    localCoords[i].y - textSize*0.7f,
                    localCoords[i].x + textSize,
                    localCoords[i].y +textSize*0.7f,
                    cornerRadius, cornerRadius,
                    paint
            );

            if (leftPlayers[i] > 0) {
                paint.setColor(Color.BLACK);
                paint.setTextAlign(Paint.Align.CENTER);
                text = String.valueOf(leftPlayers[i]);
                paint.getTextBounds(text, 0, text.length(), bounds);
                canvas.drawText(
                        text,
                        localCoords[i].x,
                        localCoords[i].y + bounds.height()/2,
                        paint
                );
            }

            if (i == 0 && !isServingLeft) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(20f);
                canvas.drawRoundRect(
                        visitCoords[i].x - textSize,
                        visitCoords[i].y - textSize*0.7f,
                        visitCoords[i].x +textSize,
                        visitCoords[i].y +textSize*0.7f,
                        cornerRadius, cornerRadius,
                        paint
                );
                paint.setStyle(Paint.Style.FILL);
            }

            paint.setColor(Color.BLUE);
            canvas.drawRoundRect(
                    visitCoords[i].x - textSize,
                    visitCoords[i].y - textSize*0.7f,
                    visitCoords[i].x +textSize,
                    visitCoords[i].y +textSize*0.7f,
                    cornerRadius, cornerRadius,
                    paint
            );

            if (rightPlayers[i] > 0) {
                paint.setColor(Color.BLACK);
                paint.setTextAlign(Paint.Align.CENTER);
                text = String.valueOf(rightPlayers[i]);
                paint.getTextBounds(text, 0, text.length(), bounds);
                canvas.drawText(
                        text,
                        visitCoords[i].x,
                        visitCoords[i].y + bounds.height()/2,
                        paint
                );
            }
        }
    }

    private void calcPoints() {
        // VI
        localCoords[5].x = center.x - courtWidth *9/24 ;
        localCoords[5].y = center.y;// + myFont.getSize() / 2;
        visitCoords[5].x = center.x + courtWidth *9/24;
        visitCoords[5].y = center.y;// + myFont.getSize() / 2;

        // III
        localCoords[2].x = center.x - courtWidth / 8;
        localCoords[2].y = localCoords[5].y;
        visitCoords[2].x = center.x + courtWidth / 8;
        visitCoords[2].y = visitCoords[5].y;

        // I
        localCoords[0].x = localCoords[5].x;
        localCoords[0].y = center.y + courtHeight / 3;// + myFont.getSize() / 2;
        visitCoords[0].x = visitCoords[5].x;
        visitCoords[0].y = center.y - courtHeight / 3;// + myFont.getSize() / 2;

        // V
        localCoords[4].x = localCoords[5].x;
        localCoords[4].y = center.y - courtHeight / 3;// + myFont.getSize() / 2;
        visitCoords[4].x = visitCoords[5].x;
        visitCoords[4].y = center.y + courtHeight / 3;// + myFont.getSize() / 2;

        // II
        localCoords[1].x = localCoords[2].x;
        localCoords[1].y = localCoords[0].y;
        visitCoords[1].x = visitCoords[2].x;
        visitCoords[1].y = visitCoords[0].y;

        // IV
        localCoords[3].x = localCoords[2].x;
        localCoords[3].y = localCoords[4].y;
        visitCoords[3].x = visitCoords[2].x;
        visitCoords[3].y = visitCoords[4].y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_UP)
            return super.onTouchEvent(e);

        Point tap = new Point((int) e.getX(), (int) e.getY());
        int radius = 50;
        selected = null;

        for (Point p : localCoords) {
            if (Math.abs(p.x - tap.x) < radius && Math.abs(p.y - tap.y) < radius) {
                selected = p;
            }
        }

        for (Point p : visitCoords) {
            if (Math.abs(p.x - tap.x) < radius && Math.abs(p.y - tap.y) < radius) {
                selected = p;
            }
        }

        invalidate();
        return super.onTouchEvent(e);
    }

    public int getPlayerOut() {
        if(selected == null) return -1;

        for(int i=0; i<localCoords.length; i++) {
            if(localCoords[i].equals(selected)) return leftPlayers[i];
            if(visitCoords[i].equals(selected)) return rightPlayers[i];
        }

        return -1;
    }

    public void setPlayerIn(int in) {
        if(selected == null) return;

        for(int i=0; i<localCoords.length; i++) {
            if(localCoords[i].equals(selected)) {
                leftPlayers[i] = in;
                break;
            }

            if(visitCoords[i].equals(selected)) {
                rightPlayers[i] = in;
                break;
            }
        }

        selected = null;
        invalidate();
    }

}
