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
import android.util.Log;
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
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.centrodedistribuicao.objects.MemFile;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.helpers.Laudo;
import com.rastreabilidadeInterna.helpers.Random;
import com.rastreabilidadeInterna.models.Rotulo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityAssociar extends Activity {

    private String codigoCDSTM;

    private EditText editTextEtiqueta;
    private Button buttonOK;
    private ListView listViewEtiquetas;
    private Button buttonFinalizar;

    public Button btnStatus;
    public HelperFTP helperFTP;

    private MemFile memFile = null;

    private int idRecepcao;

    int totalEtiquetas;

    private ModelProdutoRecebido modelProdutoRecebido;
    private ModelRecepcao modelRecepcao;

    private ArrayList<String> listaEtiquetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String filename = extras.getString("filename","");
            if (!filename.equals("")){
                memFile = new MemFile(filename);
            }
        }

        listaEtiquetas = new ArrayList<String>();
        totalEtiquetas = Integer.parseInt(getIntent().getExtras().getString("totalcaixas"));

        helperFTP = new HelperFTP(this);

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

    private void actionSalvar() {
        codigoCDSTM = gerarCodigoCDSTM();

        salvarDadosNoBanco();
        salvarDadosNoArquivo();

        Laudo.generateCDLaudo(modelRecepcao, modelProdutoRecebido, getIntent().getStringArrayListExtra("savedImages"), getIntent().getStringArrayListExtra("rotulagem"));

        SharedPreferences.Editor editor = getSharedPreferences("Preferences", 0).edit();

        editor.putString("Nome", getIntent().getExtras().getString("Nome"));
        editor.putString("cpf", getIntent().getExtras().getString("cpf"));

        editor.putString("placa", getIntent().getExtras().getString("placa"));
        editor.putString("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
        editor.putString("data", getIntent().getExtras().getString("data"));

        editor.commit();

        Intent it = null;
        it = new Intent(this, ActivityRecebimento.class);

        it.putExtra("idRecepcao", idRecepcao);
        it.putExtra("placa", getIntent().getExtras().getString("placa"));
        it.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
        it.putExtra("data", getIntent().getExtras().getString("data"));

        startActivity(it);
        finish();
    }

    private void generateRotulo() {
        ArrayList<String> images = getIntent().getStringArrayListExtra("rotulagem");

        for (String image : images) {
            Rotulo rotulo = new Rotulo();
            rotulo.setModelProdutoRecebido(modelProdutoRecebido);
            rotulo.setImageUri(image);

            rotulo.save();
        }
    }

    private void salvarDadosNoArquivo() {
        verificarPasta();

        String filenameA = fileNameA();
        String filenameB = fileNameB();

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
        for (String selo : listaEtiquetas) {
            printWriter.println(gerarCodigoCDSTM() + ":" + selo);
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
        imprimirProcesso(printWriter, 1, modelProdutoRecebido.getMarca());
        imprimirProcesso(printWriter, 2, modelProdutoRecebido.getCodigoBarrasProduto() + "/" + modelProdutoRecebido.getCodigoBarrasCaixa());
        imprimirProcesso(printWriter, 3, modelProdutoRecebido.getSif());
        imprimirProcesso(printWriter, 4, modelProdutoRecebido.getDataFabricacao());
        imprimirProcesso(printWriter, 5, modelProdutoRecebido.getDataValidade());
        imprimirProcesso(printWriter, 7, modelProdutoRecebido.getNome());
        imprimirProcesso(printWriter, 6, Integer.toString(modelProdutoRecebido.getPecasPorCaixa() * modelProdutoRecebido.getTotalCaixas()));
        imprimirProcesso(printWriter, 9, getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")");
        imprimirProcesso(printWriter, 12, modelProdutoRecebido.getPeso());
        imprimirProcesso(printWriter, 15, getIntent().getExtras().getString("codigointerno"));
        imprimirProcesso(printWriter, 16, modelRecepcao.getNumeroDaRecepcao());
        imprimirProcesso(printWriter, 17, modelRecepcao.getPlacaDoCaminhao());
        imprimirProcesso(printWriter, 18, modelRecepcao.getDataDaRecepcao());
        imprimirProcesso(printWriter, 19, modelProdutoRecebido.getCodigoBarrasCaixa());
        imprimirProcesso(printWriter, 20, modelProdutoRecebido.getFornecedor());
        imprimirProcesso(printWriter, 21, modelProdutoRecebido.getSetor());
        imprimirProcesso(printWriter, 22, modelProdutoRecebido.getPh());
        imprimirProcesso(printWriter, 23, modelProdutoRecebido.getTemperatura().replace("-", "-"));
        imprimirProcesso(printWriter, 24, Integer.toString(modelProdutoRecebido.getTotalPalets()));
        imprimirProcesso(printWriter, 25, Integer.toString(modelProdutoRecebido.getTotalCaixas()));
        imprimirProcesso(printWriter, 26, "0");
        imprimirProcesso(printWriter, 27, Integer.toString(modelProdutoRecebido.getTotalCaixasAmostradas()));
        imprimirProcesso(printWriter, 28, Integer.toString(modelProdutoRecebido.getTotalPecasRegular()));
        imprimirProcesso(printWriter, 29, Float.toString(modelProdutoRecebido.getTotalPorcentagemRegular()));
        imprimirProcesso(printWriter, 30, Integer.toString(modelProdutoRecebido.getTotalPecasIrregular()));
        imprimirProcesso(printWriter, 31, Float.toString(modelProdutoRecebido.getTotalPorcentagemIrregular()));
        imprimirProcesso(printWriter, 32, "0");
        imprimirProcesso(printWriter, 33, "0");
        imprimirProcesso(printWriter, 34, Integer.toString(modelProdutoRecebido.getTotalPecasAmostradas()));
        imprimirProcesso(printWriter, 35, Integer.toString(modelProdutoRecebido.getTotalPecasIrregulares()));
        imprimirProcesso(printWriter, 36, Float.toString(modelProdutoRecebido.getTotalPesoIrregular()));
        imprimirProcesso(printWriter, 37, modelProdutoRecebido.getNaoConformidade());
        imprimirProcesso(printWriter, 38, modelProdutoRecebido.getMotivoDevolucao());
        imprimirProcesso(printWriter, 39, modelProdutoRecebido.getObservacoes());
        imprimirProcesso(printWriter, 40, modelProdutoRecebido.getConclusao());
        imprimirProcesso(printWriter, 41, "Centro De Distribuição");

        imprimirProcesso(printWriter, 69, new SimpleDateFormat("HH:mm:ss").format(new Date()));

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imprimirProcesso(printWriter, 45, telephonyManager.getDeviceId());

    }

    private void imprimirProcesso(PrintWriter printWriter, int codigo, String value) {
        try {
            //String linha = codigo + "-" + value + "-" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "|";
            //printWriter.print(linha);
            String linha = codigo + "|" + value;
            printWriter.println(linha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File gerarFile(String fileName) {
        return new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao" + File.separator + fileName + ".st");
    }

    private String gerarCodigoCDSTM() {
        if (memFile == null) {

            return "CDSTM" +
                    getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                    getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                    new SimpleDateFormat("ddMMyyyHHmmss_").format(new Date()) +
                    getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                    getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                    getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");

        } else {

            return memFile.getCode();

        }
    }

    private String fileNameB() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_B_01_CD_ControleDeEstoque_" +
                simpleDateFormat.format(new Date()) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private String fileNameA() {
        if (memFile == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
            return "OK_A_01_CD_ControleDeEstoque_" +
                    simpleDateFormat.format(new Date()) +
                    getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                    getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                    getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
        } else {
            return  memFile.getFileName();
        }
    }

    private String fileNameA2(int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_02_CD_" + position + "_ControleDeEstoque_" +
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

        textView.setText(listaEtiquetas.size() + "/" + totalEtiquetas);

        if ((listaEtiquetas.size() / totalEtiquetas) == 1) {
            textView.setTextColor(Color.GREEN);
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        } else if ((listaEtiquetas.size() / totalEtiquetas) < 1) {
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

    private void salvarDadosNoBanco() {
        Bundle extras = getIntent().getExtras();

        modelRecepcao = new ModelRecepcao(
                extras.getString("numeroRecepcao"),
                extras.getString("placa"),
                extras.getString("data"),
                null
        );

        Repositorio repositorio = new Repositorio(this);

        if (getIntent().hasExtra("idRecepcao")) {
            modelRecepcao = repositorio.recoverRecepcao(getIntent().getExtras().getInt("idRecepcao"));
            idRecepcao = modelRecepcao.get_id();
        } else {
            modelRecepcao = repositorio.recoverRecepcao((int) repositorio.createRecepcao(modelRecepcao));
            idRecepcao = modelRecepcao.get_id();
        }

        modelProdutoRecebido = new ModelProdutoRecebido(
                0,
                modelRecepcao.get_id(),
                extras.getString("codigocaixa"),
                extras.getString("codigoproduto"),
                extras.getString("nomeproduto"),
                extras.getString("marca"),
                extras.getString("fornecedor"),
                extras.getString("setor"),
                extras.getString("sif"),
                extras.getString("ph"),
                extras.getString("temperatura"),
                extras.getString("datafab"),
                extras.getString("dataval"),
                extras.getInt("totalpalets"),
                Integer.parseInt(extras.getString("totalcaixas")),
                extras.getInt("totalpedido"),
                extras.getInt("totalcaixasamostradas"),
                extras.getInt("totalpecasregular"),
                extras.getFloat("porcentagemregular"),
                extras.getInt("totalpecasirregular"),
                extras.getFloat("porcentagemirregular"),
                extras.getInt("totalrecebido"),
                extras.getInt("totaldevolvido"),
                extras.getInt("totalpecasamostradas"),
                extras.getInt("totalpecasirregulares"),
                extras.getFloat("totalpesoirregular"),
                extras.getString("naoconformidade"),
                extras.getString("motivodevolucao"),
                extras.getString("observacoes"),
                extras.getString("conclusao"),
                null,
                extras.getInt("pecasporcaixa"),
                extras.getString("peso"),
                Random.getBoolean(),
                "@NF@",
                codigoCDSTM,
                "@NF@"
        );

        Log.i("peso no model", modelProdutoRecebido.getPeso());

        modelRecepcao = repositorio.recoverRecepcao(modelRecepcao.get_id());
        modelProdutoRecebido = repositorio.recoverProdutoRecebido((int) repositorio.createProdutoRecebido(modelProdutoRecebido));

        for (String etiqueta : listaEtiquetas) {
            ModelEtiquetaEstoqueCentroDeDistribuicao modelEtiqueta = new ModelEtiquetaEstoqueCentroDeDistribuicao(
                    0,
                    etiqueta,
                    modelProdutoRecebido.get_id()
            );
            modelEtiqueta = repositorio.recoverEtiquetaEstoqueCentroDeDistribuicao((int) repositorio.createEtiquetaEstoqueCentroDeDistribuicao(modelEtiqueta));

        }

        modelRecepcao = repositorio.recoverRecepcao(modelRecepcao.get_id());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_associar, menu);
        return true;
    }

    @Override
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
                it = new Intent(this, ActivityRecebimento.class);
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
