package com.example.dummya;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DummyAService extends Service {
    private IBinder mIBinder = new MyBinder();

    public int var = 777; //서비스바인딩의 예시로 출력할 값

    class MyBinder extends Binder{
        DummyAService getService(){
            return DummyAService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("LOG", "onBind()");
        return mIBinder;
    }

    @Override
    public void onCreate() {
        Log.e("LOG", "onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LOG", "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("LOG", "onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("LOG", "onUnbind()");
        return super.onUnbind(intent);
    }

}