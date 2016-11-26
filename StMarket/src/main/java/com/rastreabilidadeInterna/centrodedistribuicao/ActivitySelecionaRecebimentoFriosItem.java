package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.centrodedistribuicao.objects.MemFile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivitySelecionaRecebimentoFriosItem extends Activity {

    private class HelperFtpIn extends HelperFTP {

        public HelperFtpIn(Context context) {
            super(context);
        }

        public int enviarArquivos() {
            super.enviarArquivos();
            setStatus();
            setList();
            return 1;
        }
    }

    public static File pathOrigem = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Origem");

    private Button btnManual;;
    private Button btnStatus;
    private LinearLayout ll;
    public HelperFtpIn helperFTP;

    private String filenamemask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_recebimento_frios_item);

        helperFTP = new HelperFtpIn(this);
        this.setTitle("Seleção de Produto Recebido");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            filenamemask = extras.getString("filename","");
        }

        defineComponente();
        defineAction();
        carregaLista();

        setStatus();
    }

    private void defineComponente(){
        btnManual = (Button) findViewById(R.id.btnManual);
        ll = (LinearLayout) findViewById(R.id.list_entradas);

        btnStatus = (Button) findViewById(R.id.estoq_btnStatus);
        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ActivitySelecionaRecebimentoFriosItem.this);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setTitle("Aguarde");
                pd.setMessage("Transferindo dados...");
                pd.setCancelable(false);
                pd.setMax(3);
                pd.setProgress(1);
                pd.show();
                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        pd.setProgress(2);
                        helperFTP.enviarArquivos();
                        pd.setProgress(3);
                        pd.dismiss();
                    }
                };
                mThread.start();

                //baixarArquivoDeCodigos();
            }
        });
    }

    private void defineAction(){

        btnManual.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), ActivityRecebimento.class);

                i.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
                i.putExtra("placa", getIntent().getExtras().getString("placa"));
                i.putExtra("data", getIntent().getExtras().getString("data"));

                i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                i.putExtra("cpf", getIntent().getExtras().getString("cpf"));

                i.putExtra("filename", filenamemask);
                i.putExtra("produto", "");

                startActivity(i);
                finish();
            }
        });

    }

    private void carregaLista(){

        final String subOK = "OK"+filenamemask.substring(2);
        final String subNOK = "__"+filenamemask.substring(2);

        ll.removeAllViews();
        List<String> opcoes = new ArrayList<String>();

        File fNokList[] = pathOrigem.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(subNOK);
            }
        });

        for (int i = 0; i<fNokList.length; i++) {
            opcoes.add(fNokList[i].getName());
        }

        File fOkList[] = pathOrigem.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(subOK);
            }
        });

        for (int i = 0; i<fOkList.length; i++) {
            opcoes.add(fOkList[i].getName());
        }

        for (int i = 0; i<opcoes.size(); i++){

            String opcao = opcoes.get(i);

            String ok = "1";
            if (opcao.substring(0,2).equals("__")) {
                ok = "0";
            }

            LinearLayout entrada = new LinearLayout(this);
            entrada.setOrientation(LinearLayout.VERTICAL);
            entrada.setGravity(Gravity.CENTER_HORIZONTAL);
            if (ok.equals("0")) {
                entrada.setBackgroundColor(Color.parseColor("#eeeeee"));
            } else {
                entrada.setBackgroundColor(Color.parseColor("#aaaaaa"));
            }
            entrada.setTag(opcao);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50, 10, 50, 10);
            entrada.setLayoutParams(layoutParams);

            MemFile memFile = new MemFile(opcao);

            final TextView tvCaminhao = new TextView(this);
//            tvCaminhao.setText(memFile.getAnswer(7));
            tvCaminhao.setText("#" + memFile.getAnswer(2).split("/")[0] + ": " + memFile.getAnswer(7));
            tvCaminhao.setTextSize(18);
            tvCaminhao.setGravity(Gravity.CENTER_HORIZONTAL);
            entrada.addView(tvCaminhao);

            TextView tvNota = new TextView(this);
            tvNota.setText("Lote: " + memFile.getAnswer(12));
            tvNota.setTextSize(18);
            tvNota.setGravity(Gravity.CENTER_HORIZONTAL);
            entrada.addView(tvNota);

            entrada.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {

                    String fileName = view.getTag().toString();
                    acaoSelecionado(fileName);
                }
            });

            ll.addView(entrada);

        }

    }

    private void acaoSelecionado(String filename){

        if (filename.substring(0,2).equals("__")){
            String newName = "OK"+filename.substring(2);
            renameFile(filename,newName);
            filename = newName;
        }

        Intent i = new Intent(getBaseContext(), ActivityRecebimento.class);

        i.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
        i.putExtra("placa", getIntent().getExtras().getString("placa"));
        i.putExtra("data", getIntent().getExtras().getString("data"));

        i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
        i.putExtra("cpf", getIntent().getExtras().getString("cpf"));

        i.putExtra("filename", filename);

        startActivity(i);
        finish();
    }

    private void renameFile(String nameFrom, String nameTo){
        File from = new File(pathOrigem,nameFrom);
        File to = new File(pathOrigem,nameTo);
        if(from.exists())
            from.renameTo(to);
    }

    private void setStatus() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates ui
                if (helperFTP.path.list().length > 0) {
                    btnStatus.setBackgroundResource(R.drawable.background_red);
                } else {
                    btnStatus.setBackgroundResource(R.drawable.background_green);
                }
            }
        });
    }

    private void setList() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                carregaLista();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Saindo Do Controle De Estoque")
                .setMessage("Tem certeza que deseja sair?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }

}
