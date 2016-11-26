package com.rastreabilidadeInterna.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.rastreabilidadeInterna.helpers.FileTransfer;

public class UploadService extends IntentService {
    private static final String ACTION_SEND = "com.rastreabilidadeInterna.service.action.sendFiles";

    public static void startActionSend(Context context) {
        Log.i("Upload Service", "Starting");
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_SEND);
        context.startService(intent);
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND.equals(action)) {
                handleActionSend();
            }
        }
    }

    private void handleActionSend() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //enviarArquivos();
                    Log.i("Background Service", "Running");
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    private void enviarArquivos(){
        FileTransfer fileTransfer = new FileTransfer(this);
        fileTransfer.enviarCentroDeDistribuicao();
        fileTransfer.enviarControleDeEstoque();
        fileTransfer.enviarFracionamento();
        fileTransfer.enviarPreparacao();
        fileTransfer.enviarLaudos();
    }
}