package ensharp.ttalawa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;

import java.util.Locale;

/**
 * Created by mh on 2016-08-01.
 */
public class RestTimeTask extends AsyncTask<Void, Void, Void> implements TextToSpeech.OnInitListener {

    public NotificationCompat.Builder notificationBuilder;
    public NotificationManager notificationManager;
    public Intent intent;
    public PendingIntent contentIntent;
    public final Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.mContext.getResources(), R.drawable.splash_icon); // 아이콘 ic_menu_gallery를 띄워준다.;
    public static CountDownTimer timer;
    private String charging;
    private boolean thirtyArm;
    private boolean twentyArm;
    private boolean tenArm;
    private boolean fiveArm;
    TextToSpeech ttsArm;

    SharedPreferences pref;


    protected void onPreExecute() {

        intent = new Intent(MainActivity.mContext, MainActivity.class);
        contentIntent = PendingIntent.getActivity(MainActivity.mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Locale.getDefault().getLanguage().equals("ko")) {
            charging = "과금 0 원";
        } else{
            charging = "Charging 0 won";
        }
        thirtyArm = true;
        twentyArm = true;
        tenArm = true;
        fiveArm = true;
        ttsArm = new TextToSpeech(MainActivity.mContext, this);
        onInit(ttsArm.SUCCESS);
        //큰 아이콘
        notificationManager = (NotificationManager) MainActivity.mContext.getSystemService(MainActivity.mContext.NOTIFICATION_SERVICE);

        timer = new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                pref = new SharedPreferences(MainActivity.mContext);
                if ((millisUntilFinished < 1800000) && (thirtyArm == true)) {
                    thirtyArm = false;
                    if(pref.getValue("30", "off", "alarm").equals("on")){
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            notification(notificationManager, notificationBuilder, "30분 남았습니다", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")){
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("따릉이 자전거 반납까지 30분 남았습니다.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else{
                            notification(notificationManager, notificationBuilder, "Half an hour left", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")){
                                ttsArm.setLanguage(Locale.ENGLISH);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("30 minutes left before the end of the session.",TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }

                    }
                } else if ((millisUntilFinished < 1200000) && (twentyArm == true)) {
                    twentyArm = false;
                    if(pref.getValue("20", "off", "alarm").equals("on")){
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            notification(notificationManager, notificationBuilder, "20분 남았습니다", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("따릉이 자전거 반납까지 20분 남았습니다.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else{
                            notification(notificationManager, notificationBuilder, "20 minutes left", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.ENGLISH);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("20 minutes left before the end of the session.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                } else if ((millisUntilFinished < 600000) && (tenArm == true)) {
                    tenArm = false;
                    MainActivity.mainTxtView.setTextColor(Color.parseColor("#FF0000"));
                    if(pref.getValue("10", "off", "alarm").equals("on")){
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            notification(notificationManager, notificationBuilder, "10분 남았습니다", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("따릉이 자전거 반납까지 10분 남았습니다.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else{
                            notification(notificationManager, notificationBuilder, "10 minutes left", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.ENGLISH);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("10 minutes left before the end of the session.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                } else if ((millisUntilFinished < 300000) && (fiveArm == true)) {
                    fiveArm = false;
                    MainActivity.mainTxtView.setTextColor(Color.parseColor("#FF0000"));
                    if(pref.getValue("5", "off", "alarm").equals("on")){
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            notification(notificationManager, notificationBuilder, "5분 남았습니다", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("따릉이 자전거 반납까지 5분 남았습니다.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else{
                            notification(notificationManager, notificationBuilder, "5 minutes left", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.ENGLISH);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("5 minutes left before the end of the session.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                }
                if (Locale.getDefault().getLanguage().equals("ko")) {
                    MainActivity.mainTxtView.setText("반납까지 " + millisUntilFinished / 1000 / 60 % 60 + "분");
                } else{
                    MainActivity.mainTxtView.setText(millisUntilFinished / 1000 / 60 % 60 + "minutes left to return");
                }
                MainActivity.overChargingView.setText(charging);
                if(pref.getValue("state", "nonRent", "state").equals("nonRent")){
                    onFinish();
                    charging = "";
                    MainActivity.overChargingView.setText("");
                }
            }

            public void onFinish() {
                if (pref.getValue("state", "nonRent", "state").equals("Rent")) {
                    pref.putValue("state", "firstOver", "state");
                    if (Locale.getDefault().getLanguage().equals("ko")) {
                        charging = "과금 1000원";
                    } else{
                        charging = "Charging 1000won";
                    }

                    thirtyArm = true;
                    twentyArm = true;
                    tenArm = true;
                    fiveArm = true;
                    timeOver();
                } else {
                    initData();
                }
            }
        }.start();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (pref.getValue("state", "Rent", "state").equals("nonRent")) {
            timer.cancel(); // 타이머 중지
            initData();
        }
        return null;
    }

    protected void onPostExecute(Void result) {
    }

    public void timeOver() {
        timer = new CountDownTimer(1800000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if ((millisUntilFinished < 1800000) && (thirtyArm == true)) {
                    thirtyArm = false;
                    if(pref.getValue("30", "off", "alarm").equals("on")){
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            notification(notificationManager, notificationBuilder, "30분 남았습니다", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")){
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("따릉이 자전거 반납까지 30분 남았습니다.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else{
                            notification(notificationManager, notificationBuilder, "Half an hour left", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")){
                                ttsArm.setLanguage(Locale.ENGLISH);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("Half an hour left.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                } else if ((millisUntilFinished < 1200000) && (twentyArm == true)) {
                    twentyArm = false;
                    if(pref.getValue("20", "off", "alarm").equals("on")){
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            notification(notificationManager, notificationBuilder, "20분 남았습니다", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("따릉이 자전거 반납까지 20분 남았습니다.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else{
                            notification(notificationManager, notificationBuilder, "20 minutes left", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.ENGLISH);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("20 minutese left.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                } else if ((millisUntilFinished < 600000) && (tenArm == true)) {
                    tenArm = false;
                    MainActivity.mainTxtView.setTextColor(Color.parseColor("#FF0000"));
                    if(pref.getValue("10", "off", "alarm").equals("on")){
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            notification(notificationManager, notificationBuilder, "10분 남았습니다", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("따릉이 자전거 반납까지 10분 남았습니다.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else{
                            notification(notificationManager, notificationBuilder, "10 minutes left", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("10 minutes left.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                } else if ((millisUntilFinished < 300000) && (fiveArm == true)) {
                    fiveArm = false;
                    MainActivity.mainTxtView.setTextColor(Color.parseColor("#FF0000"));
                    if(pref.getValue("5", "off", "alarm").equals("on")){
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            notification(notificationManager, notificationBuilder, "5분 남았습니다", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("따릉이 자전거 반납까지 5분 남았습니다.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else{
                            notification(notificationManager, notificationBuilder, "5 minutes left", charging);
                            if(pref.getValue("sound", "off", "alarm").equals("on")) {
                                ttsArm.setLanguage(Locale.KOREA);
                                ttsArm.setSpeechRate(0.9f);
                                ttsArm.speak("5 minutes left.",
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                }
                if (Locale.getDefault().getLanguage().equals("ko")) {
                    MainActivity.mainTxtView.setText("반납까지 " + millisUntilFinished / 1000 / 60 % 60 + "분");
                } else{
                    MainActivity.mainTxtView.setText(millisUntilFinished / 1000 / 60 % 60 + "minutes left to return");
                }
                MainActivity.overChargingView.setText(charging);
                if (pref.getValue("state", "nonRent", "state").equals("nonRent")) {
                    timer.cancel();
                    initData();
                }
            }

            @Override
            public void onFinish() {
                thirtyArm = true;
                twentyArm = true;
                tenArm = true;
                fiveArm = true;
                switch (pref.getValue("state", "nonRent", "state")) {
                    case "firstOver": // 첫번째 과금
                        timer.cancel();
                        pref.putValue("state", "secondOver", "state");
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            charging = "과금 2000원";
                        } else{
                            charging = "Charging 2000won";
                        }
                        timeOver();
                        break;
                    case "secondOver": // 두번째 과금
                        timer.cancel();
                        pref.putValue("state", "thirdOver", "state");
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            charging = "과금 3000원";
                        } else{
                            charging = "Charging 3000won";
                        }
                        timeOver();
                        break;
                    case "thirdOver": // 세번째 과금
                        timer.cancel();
                        if (Locale.getDefault().getLanguage().equals("ko")) {
                            charging = "분실됨";
                        } else{
                            charging = "Bicycle went astray";
                        }
                        break;
                    default:
                        initData();
                        break;
                }
            }
        }.start();
    }

    public void initData() {
        MainActivity.mainTxtView.setTextColor(Color.parseColor("#000000"));
        if (Locale.getDefault().getLanguage().equals("ko")) {
            MainActivity.mainTxtView.setText("따릉이를 대여하세요");
        } else{
            MainActivity.mainTxtView.setText("Use the rental service");
        }
        pref.putValue("state", "nonRent", "state");
    }

    // 노티피케이션
    public void notification(NotificationManager pNotificationManager,
                             NotificationCompat.Builder pNotificationBuilder, String restTime, String charging) {
        pNotificationBuilder = new NotificationCompat.Builder(MainActivity.mContext);
        pNotificationBuilder
                .setLargeIcon(bitmap) // 이미지 띄워주기
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(restTime)
                .setContentText(charging) // 텍스트 띄우기
                .setTicker(restTime) // 상태 바에 뜨는 문구
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        switch (restTime.substring(0, 1)) {
            case "3":
                if (pref.getValue("vib", "off", "alarm").equals("on")) {
                    pNotificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                } else{
                    pNotificationBuilder.setVibrate(new long[]{0, 0, 0, 0, 0});
                }
                break;
            case "2":
                if (pref.getValue("vib", "off", "alarm").equals("on")) {
                    pNotificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                } else{
                    pNotificationBuilder.setVibrate(new long[]{0, 0, 0, 0, 0});
                }
                break;
            case "1":
                if (pref.getValue("vib", "off", "alarm").equals("on")) {
                    pNotificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                } else{
                    pNotificationBuilder.setVibrate(new long[]{0, 0, 0, 0, 0});
                }
                break;
            case "5":
                if (pref.getValue("vib", "off", "alarm").equals("on")) {
                    pNotificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                } else{
                    pNotificationBuilder.setVibrate(new long[]{0, 0, 0, 0, 0});
                }
                break;
            default:
                break;
        }

        pNotificationManager.notify(1, pNotificationBuilder.build());
    }

    @Override
    public void onInit(int i) {
        // for TextToSpeech
    }
}