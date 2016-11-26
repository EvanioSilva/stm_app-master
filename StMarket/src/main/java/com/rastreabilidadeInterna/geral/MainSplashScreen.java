package com.rastreabilidadeInterna.geral;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.models.Produto;
import com.rastreabilidadeInterna.service.UploadService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainSplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Repositorio repositorio = new Repositorio(this);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (needsUpgrade()) {
                    Log.i("precisa de update", "sim");
                    updateLista();
                } else {
                    Log.i("precisa de update", "nao");
                }

            }

        });

        thread.start();

        UploadService.startActionSend(getApplicationContext());
        Log.i("Upload Service", "Started");

        // After 5 seconds redirect to another intent
        Intent i = new Intent(getBaseContext(), ActivityTelaInicial.class);

        startActivity(i);

        //Remove activity
        finish();


    }

    private boolean needsUpgrade() {
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    getAssets().open("lista_carrefour.txt"), "UTF-8"
                            )
                    );


            class ContainerLong {
                long count;

                public long getCount() {
                    return count;
                }

                public void setCount(long count) {
                    this.count = count;
                }
            }

            final ContainerLong lines = new ContainerLong();

            while (bufferedReader.readLine() != null) lines.setCount(lines.getCount() + 1);
            bufferedReader.close();

            final ContainerLong count = new ContainerLong();

            final ContainerLong result = new ContainerLong();
            result.setCount(-1);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    count.setCount(Produto.count(Produto.class, "1 = 1", null));

                    Log.i("lines2", lines.getCount() + "");
                    Log.i("count2", count.getCount() + "");

                    if (count.getCount() != lines.getCount()){
                        result.setCount(1);
                    } else {
                        result.setCount(0);
                    }
                }
            });

            while (result.getCount() == -1){
                Thread.sleep(50);
                //Log.i("wait", "counting");
            }

            if (result.getCount() == 1){
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void updateLista() {

        Produto.deleteAll(Produto.class);

        try {
            BufferedReader bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    getAssets().open("lista_carrefour.txt"), "UTF-8"
                            )
                    );
            for (String mLine = bufferedReader.readLine();
                 mLine != null;
                 mLine = bufferedReader.readLine()) {
                String[] brokenLine = mLine.split("\\*");

                Produto produto = new Produto(
                        brokenLine[0],
                        brokenLine[1],
                        brokenLine[2],
                        brokenLine[3],
                        brokenLine[4],
                        brokenLine[5],
                        brokenLine[6],
                        brokenLine.length > 8 ? brokenLine[8] : "1"
                );

                produto.save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}