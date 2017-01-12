package com.example.zhangjiawen.myapplication6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjiawen on 2016/10/11.
 */
public class Panel extends View {

    private int mPanelWidth;//整个棋盘的宽度
    private float mLinelHeight;//一个格子的高度
    private int MAX_LINE=10;//每行（列）的格子数量
    private int MAX_COUNTIN_LINE = 5;
    private Paint mPaint = new Paint();
    private Bitmap mWhitePiece;//白棋子
    private Bitmap mBlackPiece;//黑棋子
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;//定义棋子占方格的比例
    private boolean mIsWhite = true;//白棋子先行
    private ArrayList<Point> mWhiteArray = new ArrayList<>();//定义数组，用来存放白棋子的坐标
    private ArrayList<Point> mBlackArray = new ArrayList<>();//定义数组，用来存放黑棋子的坐标
    private boolean mIsGameOver;//判断游戏是否结束
    private boolean mIsWhiteWinner;//判断白棋是否胜利


    public Panel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44855aee);//设置棋盘颜色及透明度
        init();
    }



    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //读取棋子图片
        mWhitePiece = BitmapFactory.decodeResource(getResources() , R.drawable.white);
        mBlackPiece = BitmapFactory.decodeResource(getResources() , R.drawable.black);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize , heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED){  //UNSPECIFIED表示未指定尺寸
            width = heightSize;
        }else if (heightSize == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        setMeasuredDimension(width , width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLinelHeight = mPanelWidth * 1.0f / MAX_LINE;
        //自定义棋子的宽度
        int pieceWidth = (int) (mLinelHeight * ratioPieceOfLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece , pieceWidth , pieceWidth , false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece , pieceWidth , pieceWidth , false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver){
            return false;
        }

        int action = event.getAction();//获得棋子当前的动作
        if (action == MotionEvent.ACTION_UP){//如果棋子落下,获取棋子的坐标，并添加到数组中
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x , y);//获取坐标
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)){//如果该坐标已经有棋子，则不能在该点再下
                return false;
            }
            if (mIsWhite){//判断是否为白棋
                mWhiteArray.add(p);
            }else{
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;//改变该值，以控制下一步所添加的棋子颜色
        }

        return true;
    }

    //获取坐标
    private Point getValidPoint(int x, int y) {
        return new Point((int) (x /mLinelHeight) ,(int) (y / mLinelHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);//绘制棋盘
        drawPieces(canvas);//绘制棋子
        checkGameOver();//判断胜利方
    }

    //判断胜利方
    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        if (whiteWin || blackWin){
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;
            if (mIsWhiteWinner){
                Toast.makeText(getContext(), "白棋胜利", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "黑棋胜利", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //判断是否有五个连成一线
    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points){
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x , y , points);
            if (win){
                return true;
            }
            win = checkVetical(x , y , points);
            if (win){
                return true;
            }
            win = checkLeftDiagonal(x , y , points);
            if (win){
                return true;
            }
            win = checkRightDiagonal(x , y , points);
            if (win){
                return true;
            }
        }
        return false;
    }

    //判断横向是否连成五个
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //判断左边
        for (int i = 1; i < MAX_COUNTIN_LINE; i++){
            if (points.contains(new Point(x - i, y))){
                count++;
            }else{
                break;
            }
        }
        if (count == MAX_COUNTIN_LINE){
            return true;
        }
        //判断右边
        for (int i = 1; i < MAX_COUNTIN_LINE; i++){
            if (points.contains(new Point(x + i, y))){
                count++;
            }else{
                break;
            }
        }
        if (count == MAX_COUNTIN_LINE){
            return true;
        }
        return false;
    }

    //判断纵向是否连成五个
    private boolean checkVetical(int x, int y, List<Point> points) {
        int count = 1;
        //判断上边
        for (int i = 1; i < MAX_COUNTIN_LINE; i++){
            if (points.contains(new Point(x , y + i))){
                count++;
            }else{
                break;
            }
        }
        if (count == MAX_COUNTIN_LINE){
            return true;
        }
        //判断下边
        for (int i = 1; i < MAX_COUNTIN_LINE; i++){
            if (points.contains(new Point(x , y - i))){
                count++;
            }else{
                break;
            }
        }
        if (count == MAX_COUNTIN_LINE){
            return true;
        }
        return false;
    }

    //判断左斜是否连成五个
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //判断左下
        for (int i = 1; i < MAX_COUNTIN_LINE; i++){
            if (points.contains(new Point(x - i , y - i))){
                count++;
            }else{
                break;
            }
        }
        if (count == MAX_COUNTIN_LINE){
            return true;
        }
        //判断右上
        for (int i = 1; i < MAX_COUNTIN_LINE; i++){
            if (points.contains(new Point(x + i, y + i))){
                count++;
            }else{
                break;
            }
        }
        if (count == MAX_COUNTIN_LINE){
            return true;
        }
        return false;
    }

    //判断右斜是否连成五个
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //判断左上
        for (int i = 1; i < MAX_COUNTIN_LINE; i++){
            if (points.contains(new Point(x - i , y + i))){
                count++;
            }else{
                break;
            }
        }
        if (count == MAX_COUNTIN_LINE){
            return true;
        }
        //判断右下
        for (int i = 1; i < MAX_COUNTIN_LINE; i++){
            if (points.contains(new Point(x + i, y - i))){
                count++;
            }else{
                break;
            }
        }
        if (count == MAX_COUNTIN_LINE){
            return true;
        }
        return false;
    }

    //绘制棋子
    private void drawPieces(Canvas canvas) {
        //绘制白棋子
        for(int i = 0, n = mWhiteArray.size() ; i < n ; i++){
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece ,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLinelHeight ,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLinelHeight , null);
        }
        //绘制黑棋子
        for(int i = 0, n = mBlackArray.size() ; i < n ; i++){
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece ,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLinelHeight ,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLinelHeight , null);
        }
    }

    //绘制棋盘
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLinelHeight;
        //横线的绘制
        for (int i = 0; i < MAX_LINE; i++){
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX , y , endX , y , mPaint);
        }
        //纵线的绘制
        for (int i = 0; i < MAX_LINE; i++){
            int startY = (int) (lineHeight / 2);
            int endY = (int) (w - lineHeight / 2);
            int x = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(x , startY , x , endY , mPaint);
        }
    }

    public void start(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //棋子的存储与恢复
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
