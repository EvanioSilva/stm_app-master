package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.helpers.Laudo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityAssociarHortifruti extends Activity {

    private long idProdutoRecebido;

    private EditText editTextEtiqueta;
    private Button buttonOK;
    private ListView listViewEtiquetas;
    private Button buttonFinalizar;

    public Button btnStatus;
    public HelperFTP helperFTP;

    private int idRecepcao;

    //int totalEtiquetas;

    private ArrayList<String> listaEtiquetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associar_hortiffruti);

        listaEtiquetas = new ArrayList<String>();
        //totalEtiquetas = 1000;

        helperFTP = new HelperFTP(this);

        idProdutoRecebido = getIntent().getLongExtra("idProdutoRecebidoBanco", -1);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        updateCounter();
        mapearElementos();

    }

    private void mapearElementos() {
        editTextEtiqueta = (EditText) findViewById(R.id.editTextEtiqueta);
        buttonOK = (Button) findViewById(R.id.buttonOK);
        listViewEtiquetas = (ListView) findViewById(R.id.listViewEtiquetas);
        buttonFinalizar = (Button) findViewById(R.id.buttonFinalizarLeitura);

        buttonFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSalvar();
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editTextEtiqueta);
                addCod(editText.getText().toString());
            }
        });

        editTextEtiqueta = (EditText) findViewById(R.id.editTextEtiqueta);
        editTextEtiqueta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 11) {
                    addCod(s.toString().replaceAll("[^A-Za-z0-9]", ""));
                }
            }
        });
    }

    private void salvarDadosNoBanco(){
        ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti =
                ModelProdutoRecebidoHortifruti.findById(
                        ModelProdutoRecebidoHortifruti.class, idProdutoRecebido);

        for (String etiqueta : listaEtiquetas) {
            ModelEtiquetaHortifruti modelEtiquetaHortifruti = new ModelEtiquetaHortifruti(
                    gerarCodigoCDSTM(),
                    etiqueta,
                    modelProdutoRecebidoHortifruti
            );

            modelEtiquetaHortifruti.save();
        }

        if (!modelProdutoRecebidoHortifruti.getParecerFinalDoCQ().equals("Aprovado")){
            Laudo.generateCDHLaudo(
                    modelProdutoRecebidoHortifruti.getModelRecepcaoHortifruti(),
                    modelProdutoRecebidoHortifruti,
                    getIntent().getStringArrayListExtra("savedImagesGraves"),
                    getIntent().getStringArrayListExtra("savedImagesLeves"),
                    getIntent().getStringArrayListExtra("savedImagesOutros"));
        }
    }

    private void actionSalvar() {
        salvarDadosNoBanco();
        salvarDadosNoArquivo();

        SharedPreferences.Editor editor = getSharedPreferences("Preferences", 0).edit();

        editor.putString("Nome", getIntent().getExtras().getString("Nome"));
        editor.putString("cpf", getIntent().getExtras().getString("cpf"));

        editor.putString("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
        editor.putString("data", getIntent().getExtras().getString("data"));

        editor.commit();

        Intent it = null;
        it = new Intent(this, ActivityRecebimentoHortifruti.class);

        it.putExtra("idProdutoRecebidoBanco", idProdutoRecebido);
        it.putExtra("idRecepcao", idRecepcao);
        it.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
        it.putExtra("data", getIntent().getExtras().getString("data"));

        startActivity(it);
        finish();
    }

    private void salvarDadosNoArquivo() {
        verificarPasta();

        String filenameA = fileNameA();
        String filenameB = fileNameB();

        String codigoCDSTM = gerarCodigoCDSTM();

        try {
            deleteFile(filenameA);
            deleteFile(filenameB);

            File arquivoA = gerarFile(filenameA);
            File arquivoB = gerarFile(filenameB);

            FileOutputStream fileOutputStreamA = new FileOutputStream(arquivoA);
            OutputStreamWriter outputStreamWriterA = new OutputStreamWriter(fileOutputStreamA, "UTF-8");
            PrintWriter printWriterA = new PrintWriter(outputStreamWriterA);

            FileOutputStream fileOutputStreamB = new FileOutputStream(arquivoB);
            OutputStreamWriter outputStreamWriterB = new OutputStreamWriter(fileOutputStreamB, "UTF-8");
            PrintWriter printWriterB = new PrintWriter(outputStreamWriterB);

            printFileContentsA(printWriterA, codigoCDSTM);
            outputStreamWriterA.close();
            fileOutputStreamA.close();

            printFileContentsB(printWriterB);
            outputStreamWriterB.close();
            fileOutputStreamB.close();

            createArquivosA2();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createArquivosA2() {
        for (String selo : listaEtiquetas) {
            try {
                String filenameA2 = fileNameA2(listaEtiquetas.indexOf(selo));
                deleteFile(filenameA2);
                File arquivoA2 = gerarFile(filenameA2);
                FileOutputStream fileOutputStreamA2 = new FileOutputStream(arquivoA2);
                OutputStreamWriter outputStreamWriterA2 = new OutputStreamWriter(fileOutputStreamA2, "UTF-8");
                PrintWriter printWriterA2 = new PrintWriter(outputStreamWriterA2);

                printFileContentsA2(printWriterA2, selo);
                outputStreamWriterA2.close();
                fileOutputStreamA2.close();
            } catch (Exception e) {

            }
        }
    }

    private void printFileContentsA2(PrintWriter printWriter, String selo) throws Exception {
        String linha = "L:";
        printWriter.println(linha);

        SimpleDateFormat dateNome = new SimpleDateFormat("dd/MM/yyyy");
        linha = dateNome.format(new java.sql.Date(System.currentTimeMillis()));
        printWriter.println(linha);

        linha = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "");                 //id empresa
        printWriter.println(linha);

        linha = getSharedPreferences("Preferences", 0).getString("NUMLOJA", "");                 //id local
        printWriter.println(linha);

        linha = selo;               //codigo origem
        printWriter.println(linha);

        linha = "1";                  //unidad. armazenamento
        printWriter.println(linha);

        linha = "7-" + getIntent().getExtras().getString("nomeproduto") + "-" + dateNome.format(new java.sql.Date(System.currentTimeMillis())) + "|";
        printWriter.print(linha);

        printWriter.close();

    }

    private void printFileContentsB(PrintWriter printWriter) throws Exception {
        List<ModelEtiquetaHortifruti> modelEtiquetaHortifrutiList = ModelEtiquetaHortifruti.find(ModelEtiquetaHortifruti.class, "modelProdutoRecebidoHortifruti = ?", Long.toString(idProdutoRecebido));
        for (ModelEtiquetaHortifruti etiquetaHortifruti : modelEtiquetaHortifrutiList) {
            printWriter.println(etiquetaHortifruti.getCodigoSafe() + ":" + etiquetaHortifruti.getCodigoProduto());
        }
        printWriter.close();
    }

    private void printFileContentsA(PrintWriter printWriter, String codigoCDSTM) throws Exception {
        String linha = "L:";
        printWriter.println(linha);

        SimpleDateFormat dateNome = new SimpleDateFormat("dd/MM/yyyy");
        linha = dateNome.format(new java.sql.Date(System.currentTimeMillis()));
        printWriter.println(linha);

        linha = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "");                 //id empresa
        printWriter.println(linha);

        linha = getSharedPreferences("Preferences", 0).getString("NUMLOJA", "");                 //id local
        printWriter.println(linha);

        linha = codigoCDSTM;               //codigo origem
        printWriter.println(linha);

        linha = "1";                  //unidad. armazenamento
        printWriter.println(linha);

        printProcessos(printWriter);

        printWriter.close();
    }

    private void printProcessos(PrintWriter printWriter) throws Exception {
        //TODO quantidade imprimirProcesso(printWriter, 6, Integer.toString(modelProdutoRecebido.getPecasPorCaixa() * modelProdutoRecebido.getTotalCaixas()));
        imprimirProcesso(printWriter, 7, getIntent().getExtras().getString("nomeproduto"));
        imprimirProcesso(printWriter, 9, getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")");
        imprimirProcesso(printWriter, 16, getIntent().getExtras().getString("numeroRecepcao"));
        imprimirProcesso(printWriter, 18, getIntent().getExtras().getString("data"));
        imprimirProcesso(printWriter, 19, getIntent().getExtras().getString("codigoproduto"));
        imprimirProcesso(printWriter, 20, getIntent().getExtras().getString("fornecedor"));
        imprimirProcesso(printWriter, 25, getIntent().getExtras().getString("totalcx"));
        imprimirProcesso(printWriter, 27, getIntent().getExtras().getString("caixasAvaliadas"));
        imprimirProcesso(printWriter, 47, getIntent().getExtras().getString("podridao"));
        imprimirProcesso(printWriter, 48, getIntent().getExtras().getString("defGraves"));
        imprimirProcesso(printWriter, 49, getIntent().getExtras().getString("defLeves"));
        imprimirProcesso(printWriter, 50, getIntent().getExtras().getString("descalibre"));
        imprimirProcesso(printWriter, 51, getIntent().getExtras().getString("pesoAmostragem"));
        imprimirProcesso(printWriter, 52, getIntent().getExtras().getString("brix"));
        imprimirProcesso(printWriter, 53, getIntent().getExtras().getString("estagio"));
        imprimirProcesso(printWriter, 54, getIntent().getExtras().getString("lbs"));
        imprimirProcesso(printWriter, 55, getIntent().getExtras().getString("outrosDefeitos"));
        imprimirProcesso(printWriter, 56, getIntent().getExtras().getString("entregue"));
        imprimirProcesso(printWriter, 57, getIntent().getExtras().getString("devolvidoCx"));
        imprimirProcesso(printWriter, 58, getIntent().getExtras().getString("devolvidoPeso"));
        imprimirProcesso(printWriter, 59, getIntent().getExtras().getString("defeitoPrincipal"));
        imprimirProcesso(printWriter, 60, getIntent().getExtras().getString("parecer"));
        imprimirProcesso(printWriter, 61, getIntent().getExtras().getString("produtoRecebido"));

        imprimirProcesso(printWriter, 63, getIntent().getExtras().getString("porcentagempodridao"));
        imprimirProcesso(printWriter, 64, getIntent().getExtras().getString("porcentagemdefgraves"));
        imprimirProcesso(printWriter, 65, getIntent().getExtras().getString("porcentagemdefleves"));
        imprimirProcesso(printWriter, 66, getIntent().getExtras().getString("porcentagemdescalibre"));

        imprimirProcesso(printWriter, 69, new SimpleDateFormat("HH:mm:ss").format(new Date()));

        imprimirProcesso(printWriter, 41, "Centro De Distribuição Hortifruti");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imprimirProcesso(printWriter, 45, telephonyManager.getDeviceId());

    }

    private void imprimirProcesso(PrintWriter printWriter, int codigo, String value) {
        String linha = codigo + "-" + value + "-" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "|";
        printWriter.print(linha);
    }

    private File gerarFile(String fileName) {
        return new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao" + File.separator + fileName + ".st");
    }

    private String gerarCodigoCDSTM() {
        return "CDHSTM" +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                new SimpleDateFormat("ddMMyyyHHmmss_").format(new Date()) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private String fileNameB() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_B_01_CD_ControleDeEstoqueHortifruti_" +
                simpleDateFormat.format(new Date()) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private String fileNameA() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_01_CD_ControleDeEstoqueHortifruti_" +
                simpleDateFormat.format(new Date()) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private String fileNameA2(int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_02_CD_" + position + "_ControleDeEstoqueHortifruti_" +
                simpleDateFormat.format(new Date()) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private void verificarPasta() {
        File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        if (!local.exists()) {
            local.mkdir();
        }

        local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao");
        if (!local.exists()) {
            local.mkdir();
        }
    }






    private void addCod(String codigo) {
        int qtdTotal = 0;
        EditText editText = (EditText) findViewById(R.id.editTextEtiqueta);

        if (codigo.equals("")) {
            Toast.makeText(getApplicationContext(), "Insira o código Safe Trace", Toast.LENGTH_LONG).show();
        } else if (verificaSelo(codigo)) {
            Toast.makeText(getApplicationContext(), "Código Safe Trace já inserido", Toast.LENGTH_LONG).show();
            editText.setText("");
        } else {
            listaEtiquetas.add(codigo);
            updateListView();
            updateCounter();
        }
        editText.setText("");
    }

    private void updateCounter() {
        TextView textView = (TextView) findViewById(R.id.textViewSelosLidos);

        textView.setText(listaEtiquetas.size() /*+ "/" + totalEtiquetas*/);

        if (/*(listaEtiquetas.size() / totalEtiquetas) == 1*/ true) {
            textView.setTextColor(Color.GREEN);
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        } else if ((listaEtiquetas.size() /* / totalEtiquetas*/) < 1) {
            textView.setTextColor(Color.RED);

        }

    }

    private void updateListView() {

        ListView lvList = (ListView) findViewById(R.id.listViewEtiquetas);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, listaEtiquetas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(super.getCount() - position - 1, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setTextSize(20);
                text.setGravity(Gravity.LEFT);
                return view;
            }
        };

        lvList.setAdapter(adapter);
    }

    private boolean verificaSelo(String selo) {
        for (int i = 0; i < listaEtiquetas.size(); i++) {
            if (listaEtiquetas.get(i).equals(selo))
                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_associar_hortifruti, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_Logout){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    logout();
                }
            });
            builder.setTitle("Confirmar Logout");
            builder.setMessage("Deseja mesmo sair?");
            builder.create().show();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void logout(){
        Intent i = new Intent(this, ActivityTelaInicial.class);
        startActivity(i);
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                return false;
            case KeyEvent.KEYCODE_SEARCH:
                return false;
            case KeyEvent.KEYCODE_BACK:
                Intent it = null;
                it = new Intent(this, ActivityRecebimentoHortifruti.class);
                startActivity(it);
                finish();
                return false;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return false;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Saindo Do Controle De Estoque")
                .setMessage("Tem certeza que deseja sair?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }
}
