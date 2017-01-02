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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivitySelecionaRecebimentoFrios extends Activity {

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

    private Button btnManual;
    private Button btnStatus;
    private LinearLayout ll;
    public HelperFtpIn helperFTP;
    private Button btnOder1;
    private Button btnOder2;
    private Button btnOder3;
    public List<String> opcoes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_recebimento_frios);

        helperFTP = new HelperFtpIn(this);
        this.setTitle("Seleção de Recebimento");

        if (!pathOrigem.exists() || !pathOrigem.isDirectory()) {
            pathOrigem.mkdir();
        }

        defineComponente();
        defineAction();
        opcoes = new ArrayList<String>();
        popularLista(false);
        preparaOrdenacao();
        setStatus();
    }

    private void preparaOrdenacao() {

        btnOder1 = (Button) findViewById(R.id.btnOrd1);
        btnOder2 = (Button) findViewById(R.id.btnOrd2);
        btnOder3 = (Button) findViewById(R.id.btnOrd3);

        //Placa
        btnOder1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Collections.sort(opcoes, new Comparator<String>() {
                    public int compare(String s1, String s2) {
                        String a =  s1.split("_")[0];
                        String b =  s2.split("_")[0];
                        return b.compareTo(a);
                    }
                });
                popularLista(true);
            }
        });

        //Fornecedor
        btnOder2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Collections.sort(opcoes, new Comparator<String>() {
                    public int compare(String s1, String s2) {
                        String a =  s1.split("_")[1];
                        String b =  s2.split("_")[1];
                        return b.compareTo(a);
                    }
                });
                popularLista(true);
            }
        });

        //Nota
        btnOder3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Collections.sort(opcoes, new Comparator<String>() {
                    public int compare(String s1, String s2) {
                        String a =  s1.split("_")[2];
                        String b =  s2.split("_")[2];
                        return b.compareTo(a);
                    }
                });
                popularLista(true);
            }
        });
    };

    private void defineComponente(){
        btnManual = (Button) findViewById(R.id.btnManual);
        ll = (LinearLayout) findViewById(R.id.list_entradas);

        btnStatus = (Button) findViewById(R.id.estoq_btnStatus);
        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ActivitySelecionaRecebimentoFrios.this);
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
                Intent i = new Intent(getBaseContext(), ActivityControleDeEstoqueCD.class);
                i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                i.putExtra("cpf", getIntent().getExtras().getString("cpf"));
                startActivity(i);
                finish();
            }
        });

    }

    private void popularLista(boolean flgAtualizarLista){

        ll.removeAllViews();

        if (!flgAtualizarLista)
        {
            File fOkList[] = pathOrigem.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("OK_");
                }
            });

            for (int i = 0; i < fOkList.length; i++) {

                String nome_arquivo_original = fOkList[i].getName();
                String nome_arquivo = nome_arquivo_original.substring(11, nome_arquivo_original.length() - 3);
                String[] nomes = nome_arquivo.split("_");

                String placa = nomes[0];
                String fornecedor = nomes[1];
                String nota = nomes[2];

                String referencia = "1_" + placa + "_" + fornecedor + "_" + nota;

                if (!opcoes.contains(referencia)) {
                    opcoes.add(referencia);
                }

            }

            File fNokList[] = pathOrigem.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("___");
                }
            });

            for (int i = 0; i < fNokList.length; i++) {

                String nome_arquivo_original = fNokList[i].getName();
                String nome_arquivo = nome_arquivo_original.substring(11, nome_arquivo_original.length() - 3);
                String[] nomes = nome_arquivo.split("_");

                String placa = nomes[0];
                String fornecedor = nomes[1];
                String nota = nomes[2];

                String referencia = "0_" + placa + "_" + fornecedor + "_" + nota;
                String referencia_espelho = "1_" + placa + "_" + fornecedor + "_" + nota;

                if (!opcoes.contains(referencia_espelho)) {
                    if (!opcoes.contains(referencia)) {
                        opcoes.add(referencia);
                    }
                }

            }

            Collections.sort(opcoes);
        }

        for (int i = 0; i<opcoes.size(); i++){

            String opcao = opcoes.get(i);
            String[] nomes = opcao.split("_");

            String ok = nomes[0];
            String placa = nomes[1];
            String fornecedor = nomes[2];
            String nota = nomes[3];

            LinearLayout entrada = new LinearLayout(this);
            entrada.setOrientation(LinearLayout.VERTICAL);
            entrada.setGravity(Gravity.CENTER_HORIZONTAL);
           if (ok.equals("0")) {
                entrada.setBackgroundColor(Color.parseColor("#eeeeee"));
            } else {
                entrada.setBackgroundColor(Color.parseColor("#aaaaaa"));
            }
            entrada.setTag(ok+"_"+placa+"_"+fornecedor+"_"+nota);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50, 10, 50, 10);
            entrada.setLayoutParams(layoutParams);


            final TextView tvCaminhao = new TextView(this);
            tvCaminhao.setText("Placa do Caminhão: " + placa);
            tvCaminhao.setTextSize(18);
            tvCaminhao.setGravity(Gravity.CENTER_HORIZONTAL);
            entrada.addView(tvCaminhao);

            TextView tvNota = new TextView(this);
            tvNota.setText("Fornecedor: " + fornecedor);
            tvNota.setTextSize(18);
            tvNota.setGravity(Gravity.CENTER_HORIZONTAL);
            entrada.addView(tvNota);

            entrada.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {

                    String[] nomes = view.getTag().toString().split("_");

                    String ok = nomes[0];
                    String placa = nomes[1];
                    String fornecedor = nomes[2];
                    String nota = nomes[3];

                    acaoSelecionado(ok, placa, nota, fornecedor);
                }
            });

            ll.addView(entrada);

        }

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

                popularLista(false);
            }
        });
    }

    private void acaoSelecionado(String ok, String placa, String nota, String fornecedor){
        String filename;
        if (ok.equals("0")){
            filename = "___A_01_CD_"+placa+"_"+fornecedor+"_"+nota+"_";
        } else {
            filename = "OK_A_01_CD_"+placa+"_"+fornecedor+"_"+nota+"_";
        }

        Intent i = new Intent(getBaseContext(), ActivityControleDeEstoqueCD.class);
        i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
        i.putExtra("cpf", getIntent().getExtras().getString("cpf"));
        i.putExtra("placa", placa);
        i.putExtra("nota", nota);
        i.putExtra("filename", filename);
        startActivity(i);
        finish();
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
