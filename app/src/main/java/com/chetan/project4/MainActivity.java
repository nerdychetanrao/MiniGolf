package com.chetan.project4;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public int WinHoleID, WinIDGrp;
    public int isel = 0, P1CurrentID = 0, P2CurrentID = 0;
    Button mButton;
    public TextView mTextView;
    RadioGroup radioGrp;
    RadioButton rb;
    public Thread p1 = null;
    public Thread p2 = null;
    public Handler h1;
    public Handler h2;
    ArrayList<Integer> grpID1 = new ArrayList<Integer>(Arrays.asList(1001,1002,1003,1004,1005,1006,1007,1008,1009,1010));
    ArrayList<Integer> grpID2 = new ArrayList<Integer>(Arrays.asList(1011,1012,1013,1014,1015,1016,1017,1018,1019,1020));
    ArrayList<Integer> grpID3 = new ArrayList<Integer>(Arrays.asList(1021,1022,1023,1024,1025,1026,1027,1028,1029,1030));
    ArrayList<Integer> grpID4 = new ArrayList<Integer>(Arrays.asList(1031,1032,1033,1034,1035,1036,1037,1038,1039,1040));
    ArrayList<Integer> grpID5 = new ArrayList<Integer>(Arrays.asList(1041,1042,1043,1044,1045,1046,1047,1048,1049,1050));
    ArrayList<ArrayList<Integer>> holeIdList = new ArrayList<ArrayList<Integer>>(Arrays.asList(grpID1,grpID2, grpID3, grpID4, grpID5));
    public boolean gameStarted = false;
    int[] selectarray = new int[]{CLOSE_GROUP, SAME_GROUP, TARGET_SHOT};
    public static final int NEW_GAME = 0;
    public static final int P1_UPDATE = 1;
    public static final int P2_UPDATE = 2;
    public static final int NEXT_MOVE = 3;
    public static final int GAME_OVER = 4;
    public static final int RANDOM_SHOT = 6;
    public static final int CLOSE_GROUP = 7;
    public static final int SAME_GROUP = 8;
    public static final int TARGET_SHOT = 9;
    public static final int JACKPOT = 10;
    public static final int NEAR_MISS = 11;
    public static final int NEAR_GROUP = 12;
    public static final int BIG_MISS = 13;
    public static final int CATASTROPHE = 14;

    Handler mHandler = new Handler(){
        private Message m;
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // sends a message to thread 1 to go ahead and put down a piece
                case NEW_GAME:
                    h1.sendMessage(msg);
                    break;

                // means an update was received from thread 1. updates the board and
                // notifies thread 2 to make a move
                // also checks if game is finished.
                case P1_UPDATE:
                    Log.i("MAIN","P1 update");
                    mTextView.setText("Player1 - "+getStatusString(msg.arg2));
                    rb = (RadioButton)findViewById(msg.arg1);
                    ScrollView scroll = (ScrollView)findViewById(R.id.scrollView1);
                    scroll.scrollTo(0, rb.getTop());
                    //rb.setEnabled(true);
                    rb.setChecked(true);
                    rb.setTextColor(getResources().getColor(R.color.P1color));
                    if(msg.arg2 == CATASTROPHE){
                        //Button mainbtn = (Button)findViewById(R.id.start_button);
                        mTextView.setText("P2 wins");
                        //scroll.scrollTo(0, rb.getTop());
                        Toast.makeText(MainActivity.this, "GAME OVER P2 wins!",
                                Toast.LENGTH_SHORT).show();
                        m = h1.obtainMessage(GAME_OVER);
                        m.what = GAME_OVER;
                        h2.sendMessage(m);
                        resetThreads();
                        break;
                    }
                    if(msg.arg1 == WinHoleID){
                        Toast.makeText(MainActivity.this, "GAME OVER P1 wins!",
                                Toast.LENGTH_SHORT).show();
                        //scroll.scrollTo(0, rb.getTop());
                        rb.setTextColor(getResources().getColor(R.color.P1color));
                        mTextView.setText("P1 wins Jackpot");
                        m = h1.obtainMessage(GAME_OVER);
                        m.what = GAME_OVER;
                        h2.sendMessage(m);
                        resetThreads();
                        break;
                    }
                    m = h1.obtainMessage(NEXT_MOVE);
                    m.what = NEXT_MOVE;
                    h2.sendMessage(m);
                    break;

                // means an update was received from thread 1. updates the board and
                // notifies thread 2 to make a move
                // also checks if the game is finished.
                case P2_UPDATE:
                    Log.i("MAIN","P2 update");
                    mTextView.setText("Player2 - "+getStatusString(msg.arg2));
                    rb = (RadioButton)findViewById(msg.arg1);
                    //rb.setEnabled(true);
                    rb.setChecked(true);
                    rb.setTextColor(getResources().getColor(R.color.P2color));
                    ScrollView scroll1 = (ScrollView)findViewById(R.id.scrollView1);
                    scroll1.scrollTo(0, rb.getTop());
                    if(msg.arg2 == CATASTROPHE){
                        scroll1.scrollTo(0, rb.getTop());
                        mTextView.setText("P1 wins");
                        Toast.makeText(MainActivity.this, "GAME OVER P1 wins!",
                                Toast.LENGTH_SHORT).show();
                        m = h2.obtainMessage(GAME_OVER);
                        m.what = GAME_OVER;
                        h1.sendMessage(m);
                        resetThreads();
                        break;
                    }
                    if(msg.arg1 == WinHoleID){
                        Toast.makeText(MainActivity.this, "GAME OVER P2 wins!",
                                Toast.LENGTH_SHORT).show();
                        //Button mainbtn = (Button)findViewById(R.id.start_button);
                        scroll1.scrollTo(0, rb.getTop());
                        rb.setTextColor(getResources().getColor(R.color.P2color));
                        mTextView.setText("P2 wins Jackpot");
                        m = h2.obtainMessage(GAME_OVER);
                        m.what = GAME_OVER;
                        h1.sendMessage(m);
                        resetThreads();
                        break;
                    }
                    m = h2.obtainMessage(NEXT_MOVE);
                    m.what = NEXT_MOVE;
                    h1.sendMessage(m);
                    break;

                default:
                    break;
            }
        }
    };

    public class Player1 implements Runnable  {
        private boolean firstMoveMade = false;
        private int prevID = 0;
        ArrayList<Integer> usedID = new ArrayList<Integer>(50);
        public int selStratergyP1(){
            int[] arr1 = new int[]{CLOSE_GROUP, TARGET_SHOT};
            Log.i("MAIN","sel stratergy before "+isel);
            int pstatus = statusMsg(prevID);
            int ret = 0;
            switch (pstatus){
                case BIG_MISS:
                    ret = CLOSE_GROUP;
                    break;
                case NEAR_MISS:
                    ret = SAME_GROUP;
                    break;
                case NEAR_GROUP:
                    ret = TARGET_SHOT;
                    break;
            }

            Log.i("MAIN","sel stratergy after "+isel);
            return ret;
        }

        public void run() {

            // Starts the looper and the handler for thread 1
            Looper.prepare();
            h1 = new Handler() {
                public void handleMessage(Message msg) {
                    // sleep for 1 second so it's detectable by a human
                    try {
                        Log.i("MAIN","Before sleep layout");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.i("HARSH ", "INTERRUPTED !");
                        return;
                    }

                    // less than 3 moves... keep putting down pieces at random position
                    if (msg.what == NEXT_MOVE ) {
                        Log.i("MAIN","next move P1");
                        Message m = new Message();
                        //m.arg1 = new Random().nextInt(50);
                        //m.arg1 = shots(RANDOM_SHOT, 0);
                        boolean idflag = false;
                        while (!idflag){
                            P1CurrentID = shots(selStratergyP1(), prevID);
                            if (!usedID.contains(P1CurrentID))
                                idflag = true;
                        }
                        //P1CurrentID = shots(selStratergyP1(), prevID);
                        usedID.add(P1CurrentID);
                        m.arg2 = statusMsg(P1CurrentID);
                        m.arg1 = P1CurrentID;
                        m.what = P1_UPDATE;
                        prevID = m.arg1;
                        mHandler.sendMessage(m);

                    }
                    else if (msg.what == GAME_OVER) {
                        resetThreads();
                    }
                }
            };


            // player 1 is always the first to start
            if (!firstMoveMade) {
                firstMoveMade = true;
                Message m = new Message();
                m.what = P1_UPDATE;
                //mTextView.setText("Set in first move");
                //Message m = h1.obtainMessage(NEXT_MOVE);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                P1CurrentID = shots(RANDOM_SHOT, 0);
                usedID.add(P1CurrentID);
                m.arg1 = P1CurrentID;
                m.arg2 = statusMsg(P1CurrentID);
                //m.what = NEXT_MOVE;
                prevID = m.arg1;
                mHandler.sendMessage(m);
                // posts a runnable to the handler of the main ui thread
                /*mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });*/
            }
            Looper.loop();
        }
    }

    public class Player2 implements Runnable {
        boolean firstmove = false;
        int prevID = 0;
        ArrayList<Integer> usedID = new ArrayList<Integer>(50);
        public int selStratergyP2(){
            int[] arr1 = new int[]{CLOSE_GROUP, SAME_GROUP, TARGET_SHOT};
            return arr1[new Random().nextInt(arr1.length)];
        }

        public void run() {

            // Starts the looper and the handler for thread 1
            Looper.prepare();
            h2 = new Handler() {
                public void handleMessage(Message msg) {
                    // sleep for 1 second so it's detectable by a human
                    try {
                        Log.i("MAIN","Before sleep p2");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.i("HARSH ", "INTERRUPTED !");
                        return;
                    }

                    // less than 3 moves. keep putting pieces on the board randomly
                    if (msg.what == NEXT_MOVE) {

                        Log.i("MAIN","next move P2 layout");
                        Message m = new Message();
                        boolean idflag = false;
                        while (!idflag){
                            if (!firstmove){
                                firstmove = true;
                                P2CurrentID = shots(RANDOM_SHOT, prevID);
                                break;
                            }
                            P2CurrentID = shots(selStratergyP2(), prevID);
                            if (!usedID.contains(P2CurrentID))
                                idflag = true;
                        }

                        usedID.add(P2CurrentID);
                        m.arg2 = statusMsg(P2CurrentID);
                        m.arg1 = P2CurrentID;
                        m.what = P2_UPDATE;
                        prevID = m.arg1;
                        mHandler.sendMessage(m);

                    }
                    else if (msg.what == GAME_OVER) {
                        resetThreads();
                    }
                }
            };

            Message m = new Message();
            m.what = 999;
            mHandler.sendMessage(m);

            Looper.loop();
        }
    }

    public int shots(int shotType, int prevID){
        Log.i("MAIN","IN SHOTS");
        int calcValue = 1001;
        switch (shotType){
            case RANDOM_SHOT:
                calcValue = holeIdList.get(new Random().nextInt(5)).get(new Random().nextInt(10));
                break;
            case CLOSE_GROUP:
                for(int i=0;i<5;i++){
                    if(holeIdList.get(i).contains(prevID))
                        if(i == 4)
                            calcValue = holeIdList.get(i-new Random().nextInt(2)).get(new Random().nextInt(10));
                        else if(i == 0)
                            calcValue = holeIdList.get(i+new Random().nextInt(2)).get(new Random().nextInt(10));
                        else
                            calcValue = holeIdList.get(i + ( new Random().nextBoolean() ? 1 : -1 ) * new Random().nextInt(2)).get(new Random().nextInt(10));
                }
                break;
            case SAME_GROUP :
                for(int i=0;i<5;i++){
                    if(holeIdList.get(i).contains(prevID))
                        calcValue = holeIdList.get(i).get(new Random().nextInt(10));
                }
                break;
            case TARGET_SHOT:
                for(int i=0;i<5;i++){
                    if(holeIdList.get(i).contains(prevID))
                        if(i == 4)
                            calcValue = prevID - new Random().nextInt(5);
                        else
                            calcValue = prevID + new Random().nextInt(5);
                }
                break;
            default:
                break;
        }
        Log.i("MAIN","calcualted value "+calcValue);
        return calcValue;
    }
    
    public int statusMsg(int currentID){
        int status = 0;
        if(currentID == WinHoleID)
            status = JACKPOT;
        else if(P1CurrentID == P2CurrentID)
            status = CATASTROPHE;
        else {
            for (int i = 0; i < 5; i++) {
                if (holeIdList.get(i).contains(currentID)) {
                    if (i == WinIDGrp)
                        status = NEAR_MISS;
                    else if (Math.abs(i - WinIDGrp) == 1)
                        status = NEAR_GROUP;
                    else if (Math.abs(i-WinIDGrp)>1)
                        status = BIG_MISS;
                }
            }
        }
        return status;
    }
    
    public String getStatusString(int id){
        String str = "";
        switch (id){
            case JACKPOT:
                str = "Jackpot" ;
                break;
            case NEAR_MISS:
                str = "Near Miss";
                break;
            case NEAR_GROUP :
                str = "Near Group";
                break;
            case BIG_MISS:
                str = "Big Miss";
                break;
            case CATASTROPHE:
                str = "Catastrophe";
                break;
            default:
                break;
        }
        return str;
    }
    
    public void resetThreads() {
        h1.removeCallbacksAndMessages(null);
        h2.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);

        if (p1.isAlive() && p2.isAlive()) {
            p1.interrupt();
            p2.interrupt();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.start_button);
        mTextView = (TextView)findViewById(R.id.textview1);
        radioGrp = (RadioGroup) findViewById(R.id.radio_group1);

        Log.i("MAIN","IN ON CREATE");
    }

    public void add_holes(RadioGroup rgp, int id){
        rgp.setOrientation(LinearLayout.VERTICAL);
        for(int i=0;i<5;i++)
            for(int j=0;j<10;j++){
                RadioButton rbn = new RadioButton(this);
                rbn.setId(holeIdList.get(i).get(j));
                String id_str = "HOLE " + String.valueOf(holeIdList.get(i).get(j)-1000);
                rbn.setText(id_str);
                rbn.setEnabled(false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                params.weight = 1.0f;
                if(j==9){
                    rbn.setBackground(getResources().getDrawable(R.drawable.custom_divider) );
                }
                rbn.setLayoutParams(params);
                rgp.addView(rbn);

            }

    }

    public void start_click(View v){
        mButton.setText("RESTART");
        Log.i("MAIN","start layout");
        if (!gameStarted) {
            gameStarted = true;
            add_holes(radioGrp,1000);
            WinHoleID = holeIdList.get(new Random().nextInt(5)).get(new Random().nextInt(10));
            for(int i=0;i<5;i++)
                if(holeIdList.get(i).contains(WinHoleID))
                    WinIDGrp = i;
            RadioButton rb1 = (RadioButton) findViewById(WinHoleID);
            //rb1.setEnabled(true);
            rb1.setChecked(true);
            rb1.setText("Winning Hole");
            rb1.setTextColor(getResources().getColor(R.color.golfcourse));
            rb1.setHighlightColor(getResources().getColor(R.color.golfcourse));
            p1 = new Thread(new Player1());
            p2 = new Thread(new Player2());
            p1.start();
            p2.start();
        }

        // if button is pressed again, reset everything, start over
        else {
            Log.i("MAIN","reset layout");
            resetThreads();
            radioGrp.clearCheck();
            radioGrp.removeAllViews();

            Toast.makeText(MainActivity.this, "NEW GAME",
                    Toast.LENGTH_SHORT).show();
            gameStarted = false;
            mButton.setText("Start Game");
            mTextView.setText("Start Game");
        }
    }
}
