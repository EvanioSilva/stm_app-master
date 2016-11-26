package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityTestes extends Activity {

    private EditText edittext;
    private Calendar myCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testes);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        executarConsulta(new Date());

        setupDatePicker();



    }

    private void executarConsulta(Date date) {
        Repositorio repositorio = new Repositorio(this);
        ArrayList<ModelRecepcao> modelRecepcaoArrayList = repositorio.recoverAllRecepcao(new SimpleDateFormat("dd/MM/yyyy").format(date));

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutHistorico);
        linearLayout.removeAllViews();

        if (modelRecepcaoArrayList != null) {
            for (ModelRecepcao modelRecepcao : modelRecepcaoArrayList) {

                Log.i("recepcao", modelRecepcao.toString());

                LinearLayout linearLayout1 = new LinearLayout(this);
                linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    linearLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    linearLayout1.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }


                TextView textView1 = new TextView(this);
                textView1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                textView1.setText(modelRecepcao.getNumeroDaRecepcao());
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textView1.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                textView1.setGravity(Gravity.CENTER);

                TextView textView2 = new TextView(this);
                textView2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                textView2.setText(modelRecepcao.getPlacaDoCaminhao());
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textView2.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                textView2.setGravity(Gravity.CENTER);

                LinearLayout linearLayout2 = new LinearLayout(this);
                linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 8.0));
                linearLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                linearLayout2.setOrientation(LinearLayout.VERTICAL);

                for (final ModelProdutoRecebido modelProdutoRecebido : modelRecepcao.getProdutosRecebidos()) {

                    Log.i("recepcao", modelProdutoRecebido.toString());

                    LinearLayout linearLayout3 = new LinearLayout(this);
                    linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        linearLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                    } else {
                        linearLayout3.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                    }

                    TextView tvNome = new TextView(this);
                    tvNome.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvNome.setText(modelProdutoRecebido.getNome());
                    tvNome.setGravity(Gravity.CENTER);
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        tvNome.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                    } else {
                        tvNome.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                    }

                    TextView tvMarca = new TextView(this);
                    tvMarca.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvMarca.setText(modelProdutoRecebido.getMarca());
                    tvMarca.setGravity(Gravity.CENTER);
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        tvMarca.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                    } else {
                        tvMarca.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                    }

                    TextView tvForn = new TextView(this);
                    tvForn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvForn.setText(modelProdutoRecebido.getFornecedor());
                    tvForn.setGravity(Gravity.CENTER);
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        tvForn.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                    } else {
                        tvForn.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                    }

                    TextView tvSif = new TextView(this);
                    tvSif.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvSif.setText(modelProdutoRecebido.getSif());
                    tvSif.setGravity(Gravity.CENTER);
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        tvSif.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                    } else {
                        tvSif.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                    }


                    TextView tvDFab = new TextView(this);
                    tvDFab.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvDFab.setText(modelProdutoRecebido.getDataFabricacao());
                    tvDFab.setGravity(Gravity.CENTER);
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        tvDFab.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                    } else {
                        tvDFab.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                    }


                    TextView tvDVal = new TextView(this);
                    tvDVal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvDVal.setText(modelProdutoRecebido.getDataValidade());
                    tvDVal.setGravity(Gravity.CENTER);
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        tvDVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                    } else {
                        tvDVal.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                    }

                    Button buttonTeste = new Button(this);
                    buttonTeste.setText("Testes");
                    buttonTeste.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                    linearLayout3.addView(tvNome);
                    linearLayout3.addView(tvMarca);
                    linearLayout3.addView(tvForn);
                    linearLayout3.addView(tvSif);
                    linearLayout3.addView(tvDFab);
                    linearLayout3.addView(tvDVal);
                    linearLayout3.addView(buttonTeste);

                    buttonTeste.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ActivityTestes.this, ActivityTeste.class);

                            intent.putExtra("model", modelProdutoRecebido.get_id());

                            startActivity(intent);
                        }
                    });

                    linearLayout2.addView(linearLayout3);

                }

                linearLayout1.addView(textView1);
                linearLayout1.addView(textView2);
                linearLayout1.addView(linearLayout2);

                linearLayout.addView(linearLayout1);

            }
        }

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

    private String fileNameA(Date dataArquivo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_04_CD_ControleDeEstoque_" +
                simpleDateFormat.format(dataArquivo) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private File gerarFile(String fileName) {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao" + File.separator + fileName + ".st");

        return f;
    }

    private void salvarDadosNoArquivo(ModelProdutoRecebido modelProdutoRecebido) {
        verificarPasta();

        Date dataArquivo = new Date();

        String filenameA = fileNameA(dataArquivo);

        String codigoCDSTM = modelProdutoRecebido.getCodigoCDSTM();

        try {
            deleteFile(filenameA);

            File arquivoA = gerarFile(filenameA);

            FileOutputStream fileOutputStreamA = new FileOutputStream(arquivoA);
            OutputStreamWriter outputStreamWriterA = new OutputStreamWriter(fileOutputStreamA, "UTF-8");
            PrintWriter printWriterA = new PrintWriter(outputStreamWriterA);

            printFileContentsA(printWriterA, codigoCDSTM, modelProdutoRecebido);
            outputStreamWriterA.close();
            fileOutputStreamA.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void printFileContentsA(PrintWriter printWriter, String codigoCDSTM, ModelProdutoRecebido modelProdutoRecebido) throws Exception {
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

        printProcessos(printWriter, modelProdutoRecebido);

        printWriter.close();
    }

    private void printProcessos(PrintWriter printWriter, ModelProdutoRecebido modelProdutoRecebido) throws Exception {
        Repositorio repositorio = new Repositorio(this);

        imprimirProcesso(printWriter, 1, modelProdutoRecebido.getMarca());
        imprimirProcesso(printWriter, 2, modelProdutoRecebido.getCodigoBarrasProduto() + "/" + modelProdutoRecebido.getCodigoBarrasCaixa());
        imprimirProcesso(printWriter, 3, modelProdutoRecebido.getSif());
        imprimirProcesso(printWriter, 4, modelProdutoRecebido.getDataFabricacao());
        imprimirProcesso(printWriter, 5, modelProdutoRecebido.getDataValidade());
        imprimirProcesso(printWriter, 7, modelProdutoRecebido.getNome());
        imprimirProcesso(printWriter, 6, Integer.toString(modelProdutoRecebido.getPecasPorCaixa() * modelProdutoRecebido.getTotalCaixas()));
        imprimirProcesso(printWriter, 9, getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")");
        imprimirProcesso(printWriter, 12, modelProdutoRecebido.getPeso());
        imprimirProcesso(printWriter, 16, repositorio.recoverRecepcao(modelProdutoRecebido.getIdRecepcao()).getNumeroDaRecepcao());
        imprimirProcesso(printWriter, 17, repositorio.recoverRecepcao(modelProdutoRecebido.getIdRecepcao()).getPlacaDoCaminhao());
        imprimirProcesso(printWriter, 18, repositorio.recoverRecepcao(modelProdutoRecebido.getIdRecepcao()).getDataDaRecepcao());
        imprimirProcesso(printWriter, 19, modelProdutoRecebido.getCodigoBarrasCaixa());
        imprimirProcesso(printWriter, 20, modelProdutoRecebido.getFornecedor());
        imprimirProcesso(printWriter, 21, modelProdutoRecebido.getSetor());
        imprimirProcesso(printWriter, 22, (modelProdutoRecebido.getPh().equals("") ? "@NF@" : modelProdutoRecebido.getPh()));
        imprimirProcesso(printWriter, 23, (modelProdutoRecebido.getTemperatura().replace("-", "-").equals("")) ? "@NF@" : modelProdutoRecebido.getTemperatura().replace("-", "-"));
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

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imprimirProcesso(printWriter, 45, telephonyManager.getDeviceId());

        imprimirProcesso(printWriter, 46, modelProdutoRecebido.getDescongelamento());

        imprimirProcesso(printWriter, 69, new SimpleDateFormat("HH:mm:ss").format(new Date()));

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

    private void updateProdutoRecebido(
            ModelProdutoRecebido modelProdutoRecebido,
            String temperatura,
            String ph,
            String descongelamento){

        modelProdutoRecebido.setTemperatura(temperatura);
        modelProdutoRecebido.setPh(ph);
        modelProdutoRecebido.setDescongelamento(descongelamento);

        Repositorio repositorio = new Repositorio(this);
        repositorio.updateProdutoRecebido(modelProdutoRecebido);

        salvarDadosNoArquivo(modelProdutoRecebido);

    }

    private void updateProdutoRecebido(
            ModelProdutoRecebido modelProdutoRecebido,
            String descongelamento){

        modelProdutoRecebido.setDescongelamento(descongelamento);

        Repositorio repositorio = new Repositorio(this);
        repositorio.updateProdutoRecebido(modelProdutoRecebido);

        salvarDadosNoArquivo(modelProdutoRecebido);
    }

    private void updateProdutoRecebidoF(
            ModelProdutoRecebido modelProdutoRecebido,
            String fatiamento){

        modelProdutoRecebido.setFatiamento(fatiamento);

        Repositorio repositorio = new Repositorio(this);
        repositorio.updateProdutoRecebido(modelProdutoRecebido);

        salvarDadosNoArquivo(modelProdutoRecebido);
    }

    private void setupDatePicker() {

        edittext = (EditText) findViewById(R.id.editTextDateHistorico);
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ActivityTestes.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));

        Date date = myCalendar.getTime();
        executarConsulta(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_relatorio_controle_de_estoque_cd, menu);
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
