package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.centrodedistribuicao.objects.MemFile;
import com.rastreabilidadeInterna.controleEstoque.classificadorProdutoFabricante;
import com.rastreabilidadeInterna.controleEstoque.ResultadoDeClassificacao;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.helpers.Laudo;
import com.rastreabilidadeInterna.models.Produto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityRecebimento extends Activity {

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 666;
    private ArrayList<String> savedImages = new ArrayList<String>();
    private Uri uriSavedImage;

    private boolean interpretaCodigoCaixa = true;

    private MemFile memFile = null;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("savedImages", savedImages);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedImages = savedInstanceState.getStringArrayList("savedImages");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearLayout.removeAllViews();
        for (String savedImage : savedImages) {
            ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
            Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
            linearLayout.addView(imageView);
        }
    }

    private void takePicture() {
        //camera stuff
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Carrefour" + File.separator + "Laudos");
        imagesFolder.mkdirs();

//        File image = new File(imagesFolder, "RTL_" + Laudo.fileDate() + ".jpg");
//        File image = new File(imagesFolder, "RTL_" + getString("numeroRecepcao") + ".jpg");
        File image = new File(imagesFolder, "RTL_" + getSharedPreferences("Preferences", 0).getString("numeroRecepcao", "") + ".jpg");
        uriSavedImage = Uri.fromFile(image);

        savedImages.add(uriSavedImage.toString());

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
                linearLayout.removeAllViews();
                for (String savedImage : savedImages) {
                    ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
                    Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
                    linearLayout.addView(imageView);
                }
        }
    }

    private class HelperFtpIn extends HelperFTP {

        public HelperFtpIn(Context context) {
            super(context);
        }

        public int enviarArquivos() {
            super.enviarArquivos();
            setStatus();
            return 1;
        }
    }

    File pathCodes = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + "produtos_ce.txt");

    public Button btnLimpar;
    public Button btnAmostragem;

    Calendar myCalendar;
    EditText edittext;

    public EditText edtCodigoCaixa;
    public EditText edtCodigoProduto;
    public AutoCompleteTextView edtNomeProduto;
    public EditText edtMarca;
    public EditText edtFornecedor;
    public EditText edtSIF;
    public EditText edtDataFab;
    public EditText edtDataVal;
    public EditText edtPeso;

    public Spinner spSetor;

    public Button btnStatus;
    public HelperFtpIn helperFTP;

    public List<String> list1 = new ArrayList<String>();


    private ResultadoDeClassificacao resultadosClassificacao = new ResultadoDeClassificacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recebimento);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String filename = extras.getString("filename","");
            if (!filename.equals("")){
                memFile = new MemFile(filename);
            }
        }

        helperFTP = new HelperFtpIn(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        MapearComponentes();
        carregarDados();
        setupDatePicker();

//        Button button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                takePicture();
//            }
//        });

        setStatus();
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

    public void MapearComponentes() {
        btnLimpar = (Button) findViewById(R.id.btnLimpar);
        btnAmostragem = (Button) findViewById(R.id.btnAmostrar);

        edtCodigoCaixa = (EditText) findViewById(R.id.edtOrigem_caixa);
        edtCodigoProduto = (EditText) findViewById(R.id.edtOrigem);

        edtNomeProduto = (AutoCompleteTextView) findViewById(R.id.edtTipo);

        final ArrayList<String> strings = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                List<Produto> produtos = Produto.listAll(Produto.class);

                for (Produto produto : produtos) {
                    strings.add(produto.getDescricaoProduto());
                }
                final ArrayAdapter<String> dataAdapter2 =
                        new ArrayAdapter<String>(ActivityRecebimento.this, android.R.layout.simple_dropdown_item_1line, strings);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        edtNomeProduto.setThreshold(1);
                        edtNomeProduto.setAdapter(dataAdapter2);

                    }
                });

            }
        });

        thread.start();

        edtNomeProduto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
//                if (!strings.contains(edtNomeProduto.getText().toString())) {
//                    edtNomeProduto.setText("");
//                }
            }
        });

        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtFornecedor = (EditText) findViewById(R.id.edtFornecedor);
        edtSIF = (EditText) findViewById(R.id.edtSif);
        edtDataFab = (EditText) findViewById(R.id.edtDataFab);
        edtDataVal = (EditText) findViewById(R.id.edtDataVal);
        edtPeso = (EditText) findViewById(R.id.edtPesoCaixa);

        spSetor = (Spinner) findViewById(R.id.spSetor);

        //List<String> list1 = new ArrayList<String>();
        list1.add("24-Carnes");
        list1.add("20-Salsicharia");
        list1.add("21-Pescados");
        //list1.add("25-Pratos Prontos");
        list1.add("15-PAS");
        list1.add("23-Padaria");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSetor.setAdapter(dataAdapter1);

        btnStatus = (Button) findViewById(R.id.estoq_btnStatus);

        Button btnFinalizarCarga = (Button) findViewById(R.id.btnFinalizarCarga);
        btnFinalizarCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ActivityRecebimento.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Finalizar Carga")
                        .setMessage("Tem certeza que deseja Finalizar essa Carga?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ActivityRecebimento.this, ActivityControleDeEstoqueCD.class);
                                intent.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                                intent.putExtra("cpf", getIntent().getExtras().getString("cpf"));
                                startActivity(intent);
                                finish();
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();

            }
        });

        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ActivityRecebimento.this);
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


        btnLimpar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                limpar(false);
            }
        });

        btnAmostragem.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(getBaseContext(), ActivityAmostragem.class);

                try {
                    validarCampos();

                    if (getIntent().getExtras() != null) {
                        if (getIntent().getExtras().getString("Nome") == null) {
                            i.putExtra("Nome", getSharedPreferences("Preferences", 0).getString("Nome", ""));
                        } else {
                            i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                        }

                        if (getIntent().getExtras().getString("cpf") == null) {
                            i.putExtra("cpf", getSharedPreferences("Preferences", 0).getString("cpf", ""));
                        } else {
                            i.putExtra("cpf", getIntent().getExtras().getString("cpf"));
                        }

                        if (getIntent().getExtras().getString("numeroRecepcao") == null) {
                            i.putExtra("numeroRecepcao", getSharedPreferences("Preferences", 0).getString("numeroRecepcao", ""));
                        } else {
                            i.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
                        }

                        if (getIntent().getExtras().getString("placa") == null) {
                            i.putExtra("placa", getSharedPreferences("Preferences", 0).getString("placa", ""));
                        } else {
                            i.putExtra("placa", getIntent().getExtras().getString("placa"));
                        }

                        if (getIntent().getExtras().getString("data") == null) {
                            i.putExtra("data", getSharedPreferences("Preferences", 0).getString("data", ""));
                        } else {
                            i.putExtra("data", getIntent().getExtras().getString("data"));
                        }
                    } else {
                        i.putExtra("numeroRecepcao", getSharedPreferences("Preferences", 0).getString("numeroRecepcao", ""));
                        i.putExtra("placa", getSharedPreferences("Preferences", 0).getString("placa", ""));
                        i.putExtra("data", getSharedPreferences("Preferences", 0).getString("data", ""));


                    }

                    if (getIntent().hasExtra("idRecepcao")) {
                        i.putExtra("idRecepcao", getIntent().getExtras().getInt("idRecepcao"));
                    }

                    i.putExtra("codigocaixa", edtCodigoCaixa.getText().toString());
                    i.putExtra("codigoproduto", edtCodigoProduto.getText().toString());
                    i.putExtra("nomeproduto", edtNomeProduto.getText().toString());
                    i.putExtra("marca", edtMarca.getText().toString());
                    i.putExtra("fornecedor", edtFornecedor.getText().toString());
                    i.putExtra("setor", spSetor.getSelectedItem().toString());
                    i.putExtra("sif", edtSIF.getText().toString());
                    i.putExtra("datafab", edtDataFab.getText().toString());
                    i.putExtra("dataval", edtDataVal.getText().toString());
                    i.putExtra("peso", edtPeso.getText().toString());

                    if (memFile != null) {
                        i.putExtra("filename", memFile.getFileName());
                    } else {
                        i.putExtra("filename", "");
                    }

                    Log.i("peso no edit", edtPeso.getText().toString());
                    Log.i("peso no extra", i.getExtras().getString("peso"));

                    i.putStringArrayListExtra("rotulagem", savedImages);

                    startActivity(i);
                    limpar(true);
                    finish();

                } catch (Exception e) {
                    Toast.makeText(ActivityRecebimento.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }

        });

        edtCodigoCaixa.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        //you can call or do what you want with your EditText here

                        if (interpretaCodigoCaixa) {
                            if (!hasFocus) {
                                eliminarEspacosVazios();

                                checaInversaoDeCodigos();
                                resultadosClassificacao = classificadorProdutoFabricante.classificar(edtCodigoProduto.getText().toString(), edtCodigoCaixa.getText().toString(), pathCodes);
                                if (edtCodigoProduto.getText().toString().isEmpty()) {
                                    resultadosClassificacao = classificadorProdutoFabricante.classificar(resultadosClassificacao.getBarcodeProduto(), edtCodigoCaixa.getText().toString(), pathCodes);
                                }
                                updateEditsWithResults(resultadosClassificacao);
                            }
                        }
                    }
                }
        );

        edtCodigoProduto.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (interpretaCodigoCaixa) {

                            //you can call or do what you want with your EditText here
                            if (!hasFocus) {
                                eliminarEspacosVazios();

                                checaInversaoDeCodigos();

                                resultadosClassificacao = classificadorProdutoFabricante.classificar(edtCodigoProduto.getText().toString(), edtCodigoCaixa.getText().toString(), pathCodes);
                                updateEditsWithResults(resultadosClassificacao);
                                updateEditsWithProductData();

                                try {

                                    List<Produto> produtos = Produto.find(Produto.class, "codigo_ean = '" + edtCodigoProduto.getText().toString() + "'");

                                    if (produtos.size() > 0) {
                                        Produto produto = produtos.get(0);

                                        resultadosClassificacao.setDiasDeValidade(produto.getDiasValidade());

                                        updateEditsWithResults(resultadosClassificacao);

                                        edtNomeProduto.setText(produto.getDescricaoProduto());
                                        edtFornecedor.setText(produto.getRazaoSocialFornecedor());
                                        //edtPeso.setText(produto.getPeso());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }

        );

        edtDataFab.addTextChangedListener(new

                                                  TextWatcher() {
                                                      @Override
                                                      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                      }

                                                      @Override
                                                      public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                      }

                                                      @Override
                                                      public void afterTextChanged(Editable s) {
                                                          if (datasNaoVazias()) {
                                                              mostrarVidaUtil();
                                                          }
                                                      }
                                                  }

        );

        edtDataVal.addTextChangedListener(new

                                                  TextWatcher() {
                                                      @Override
                                                      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                      }

                                                      @Override
                                                      public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                      }

                                                      @Override
                                                      public void afterTextChanged(Editable s) {
                                                          if (datasNaoVazias()) {
                                                              mostrarVidaUtil();
                                                          }
                                                      }
                                                  }

        );

    }

    public void carregarDados(){
        if (memFile != null) {

            String answer = memFile.getAnswer(2);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {

                    String[] part = answer.split("/");

                    if (part.length >= 2) {
                        this. interpretaCodigoCaixa = false;
                        edtCodigoCaixa.setText(part[0]);
                        edtCodigoProduto.setText(part[1]);
                    }

                }
            }

            answer = memFile.getAnswer(7);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtNomeProduto.setText(answer);
                    edtNomeProduto.setFocusableInTouchMode(false);
                }
            }

            answer = memFile.getAnswer(1);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtMarca.setText(answer);
                }
            }

            answer = memFile.getAnswer(20);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtFornecedor.setText(answer);
                    edtFornecedor.setFocusableInTouchMode(false);
                }
            }

            answer = memFile.getAnswer(3);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtSIF.setText(answer);
                    if (!answer.equals("0")) {
                        edtSIF.setFocusableInTouchMode(false);
                    }
                }
            }

            answer = memFile.getAnswer(4);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtDataFab.setText(answer);
                }
            }

            answer = memFile.getAnswer(5);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtDataVal.setText(answer);
                }
            }

            answer = memFile.getAnswer(21);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    spSetor.setSelection(list1.indexOf(answer));
                }
            }

            answer = memFile.getAnswer(12);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtPeso.setText(answer);
                }
            }

        }
    }

    private void updateEditsWithProductData() {
        Log.i("Produto", "Editado");
        List<Produto> produtos = Produto.find(Produto.class, "codigo_ean = '" + edtCodigoProduto.getText().toString() + "'");
        for (Produto produto : produtos) {
            Log.i("Produto", produto.toString());
        }
    }

    private void limpar(boolean force) {



        if ((memFile == null) || (force)) {

            edtCodigoCaixa.setText("");
            edtCodigoProduto.setText("");
            edtNomeProduto.setText("");
            edtMarca.setText("");
            edtFornecedor.setText("");
            edtSIF.setText("");
            edtDataFab.setText("");
            edtDataVal.setText("");
            edtPeso.setText("");

            edtFornecedor.setFocusableInTouchMode(true);
            edtNomeProduto.setFocusableInTouchMode(true);
            edtSIF.setFocusableInTouchMode(true);

            TextView diasAteVencimento = (TextView) findViewById(R.id.diasAteVencimento);
            diasAteVencimento.setText("Aguardando datas...");
            diasAteVencimento.setBackgroundColor(Color.GRAY);
            diasAteVencimento.setTextColor(Color.BLACK);

        } else {

            limparPre();

        }
    }

    public void limparPre(){

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmação de Limpeza")
                .setMessage("Esta operação tornará esta entrada manual, deseja mesmo limpar?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edtCodigoCaixa.setText("");
                        edtCodigoProduto.setText("");
                        edtNomeProduto.setText("");
                        edtMarca.setText("");
                        edtFornecedor.setText("");
                        edtSIF.setText("");
                        edtDataFab.setText("");
                        edtDataVal.setText("");
                        edtPeso.setText("");
                        memFile = null;

                        edtFornecedor.setFocusableInTouchMode(true);
                        edtNomeProduto.setFocusableInTouchMode(true);
                        edtSIF.setFocusableInTouchMode(true);

                        TextView diasAteVencimento = (TextView) findViewById(R.id.diasAteVencimento);
                        diasAteVencimento.setText("Aguardando datas...");
                        diasAteVencimento.setBackgroundColor(Color.GRAY);
                        diasAteVencimento.setTextColor(Color.BLACK);
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void setupDatePicker() {

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

        edtDataFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                edittext = edtDataFab;
                new DatePickerDialog(ActivityRecebimento.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edtDataVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                edittext = edtDataVal;
                new DatePickerDialog(ActivityRecebimento.this, date, myCalendar
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
        updateEditsWithResults(resultadosClassificacao);
    }

    private void validarCampos() throws Exception {
        validarRotulagem();
        validarCodigoDeCaixa();
        validarCodigoDeProduto();
        validarNome();
        validarMarca();
        validarFornecedor();
        validarSetor();
        validarSif();
        validarDataFab();
        validarDataVal();
        validarTotalCaixas();
        validarTotalPalets();
        validarTotalPedido();
        validarPeso();
    }

    private void validarRotulagem() throws Exception {
        // APOSTA

        // Fernando diz que isso será removido 2 dias após entrega
        // Felipe diz que o vasco vai mandar antes de mostrar pro CD
        // Rafael diz que em 7 dias

        // Isso foi removido, quem diria?
        // Vasco mandou remover apos mostrar para o CD, mas levou uns 20 dias!
        // Ninguem acertou o tempo, mas todos acertaram que seria removido! Congrats!

//        if (savedImages.size() == 0) {
//            throw new Exception("Você precisa tirar uma foto do Rótulo");
//        }
    }

    private void validarPeso() throws Exception {
        validarNaoVazia(edtPeso);
    }

    private void validarTotalPedido() throws Exception {
        //validarNaoVazia(edtTotalPedido);
        //validarNumerico(edtTotalPedido);
    }

    private void validarTotalPalets() throws Exception {

    }

    private void validarTotalCaixas() throws Exception {

    }

    private void validarDataVal() throws Exception {
        validarNaoVazia(edtDataVal);
        validarData(edtDataVal);
    }

    private void validarDataFab() throws Exception {
        validarNaoVazia(edtDataFab);
        validarData(edtDataFab);
    }

    private void validarSif() throws Exception {
        validarNaoVazia(edtSIF);
    }

    private void validarSetor() throws Exception {

    }

    private void validarFornecedor() throws Exception {
        validarNaoVazia(edtFornecedor);
    }

    private void validarMarca() throws Exception {
        validarNaoVazia(edtMarca);
    }

    private void validarNome() throws Exception {
        validarNaoVazia(edtNomeProduto);
    }

    private void validarCodigoDeProduto() throws Exception {
        validarNaoVazia(edtCodigoProduto);
    }

    private void validarCodigoDeCaixa() throws Exception {
        validarNaoVazia(edtCodigoCaixa);
    }

    private void validarNumerico(EditText editText) throws Exception {
        if (editText.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
            return;
        } else {
            throw new Exception(editText.getHint().toString() + "Precisa ser um número valido");
        }
    }

    private void validarData(EditText editText) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            simpleDateFormat.parse(editText.getText().toString());
        } catch (Exception e) {
            throw new Exception("Preencha ambas datas corretamente");
        }
    }

    private void validarNaoVazia(EditText editText) throws Exception {
        if (editText.getText().toString().isEmpty()) {
            throw new Exception("Preencha todos os campos (" + editText.getHint().toString() + ")");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_amostragem, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_Logout) {
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

    public void logout() {
        Intent i = new Intent(this, ActivityTelaInicial.class);
        startActivity(i);
        finish();
    }

    private void eliminarEspacosVazios() {
        String caixa = edtCodigoCaixa.getText().toString();
        caixa = caixa.replaceAll("[^A-Za-z0-9]", "");

        String prod = edtCodigoProduto.getText().toString();
        prod = prod.replaceAll("[^A-Za-z0-9]", "");

        edtCodigoCaixa.setText(caixa);
        edtCodigoProduto.setText(prod);
    }

    private void checaInversaoDeCodigos() {
        String codCaixa = edtCodigoCaixa.getText().toString();
        String codProduto = edtCodigoProduto.getText().toString();

        if (codCaixa.length() < codProduto.length()) {
            edtCodigoCaixa.setText(codProduto);
            edtCodigoProduto.setText(codCaixa);
        }
    }

    private void updateEditsWithResults(ResultadoDeClassificacao resultadosClassificacao) {

        if (!resultadosClassificacao.getNomeDoFabricante().isEmpty()) {
            //edtMarca.setText(resultadosClassificacao.getNomeDoFabricante());
            edtFornecedor.setText(resultadosClassificacao.getNomeDoFabricante());

            //edtMarca.setBackgroundColor(Color.parseColor("#dddddd"));
            //edtMarca.setTextColor(Color.parseColor("#666666"));
        }

        /*if (!resultadosClassificacao.getSetor().isEmpty()) {
            edtSetor.setText(resultadosClassificacao.getSetor());
            edtSetor.setBackgroundColor(Color.parseColor("#dddddd"));
            edtSetor.setTextColor(Color.parseColor("#666666"));
        }
        */

        /*
        if (!resultadosClassificacao.getCodigoDoLote().isEmpty()) {
            edtLote.setText(resultadosClassificacao.getCodigoDoLote());

            edtLote.setBackgroundColor(Color.parseColor("#dddddd"));
            edtLote.setTextColor(Color.parseColor("#666666"));
        }
        */

        //edtQtd.setText((resultadosClassificacao.getNumeroDePecas().equals("")) ? "1" : resultadosClassificacao.getNumeroDePecas());

        if (!resultadosClassificacao.getNomeDoProduto().equals("")) {
            edtNomeProduto.setText(resultadosClassificacao.getNomeDoProduto());

            edtNomeProduto.setBackgroundColor(Color.parseColor("#dddddd"));
            edtNomeProduto.setTextColor(Color.parseColor("#666666"));
        }

        if (!resultadosClassificacao.getBarcodeProduto().isEmpty()) {
            edtCodigoProduto.setText(resultadosClassificacao.getBarcodeProduto());

            edtCodigoProduto.setBackgroundColor(Color.parseColor("#dddddd"));
            edtCodigoProduto.setTextColor(Color.parseColor("#666666"));
        }

        if (!resultadosClassificacao.getCodigoSif().equals("")) {
            edtSIF.setText(resultadosClassificacao.getCodigoSif());

            edtSIF.setBackgroundColor(Color.parseColor("#dddddd"));
            edtSIF.setTextColor(Color.parseColor("#666666"));
        }


        /*
        if (!resultadosClassificacao.getPesoEmGramas().equals("")) {
            edtPeso.setText(resultadosClassificacao.getPesoEmGramas());

            edtPeso.setBackgroundColor(Color.parseColor("#dddddd"));
            edtPeso.setTextColor(Color.parseColor("#666666"));
        }
        */


        int diasDeValidade = 0;

        if (!edtDataVal.getText().toString().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                java.util.Date date = simpleDateFormat.parse(edtDataVal.getText().toString());
                resultadosClassificacao.setDataDeValidade(sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!edtDataFab.getText().toString().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                java.util.Date date = simpleDateFormat.parse(edtDataFab.getText().toString());
                resultadosClassificacao.setDataDeFabricacao(sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!resultadosClassificacao.getDiasDeValidade().isEmpty()) {

            try {
                diasDeValidade = Integer.parseInt(resultadosClassificacao.getDiasDeValidade());
                Log.i("Dias de Validade", Integer.toString(diasDeValidade));

                if (resultadosClassificacao.getDataDeValidade().isEmpty() && !resultadosClassificacao.getDataDeFabricacao().isEmpty()) {
                    resultadosClassificacao.setDataDeValidade(diasDeValidade);
                }

                if (!resultadosClassificacao.getDataDeValidade().isEmpty() && resultadosClassificacao.getDataDeFabricacao().isEmpty()) {
                    resultadosClassificacao.setDataDeFabricacao(diasDeValidade);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

        }

        if (!resultadosClassificacao.getDataDeValidade().isEmpty()) {
            edtDataVal.setText(converterResultDataEmExibData(resultadosClassificacao.getDataDeValidade()));

            edtDataVal.setBackgroundColor(Color.parseColor("#dddddd"));
            edtDataVal.setTextColor(Color.parseColor("#666666"));
        }

        if (!resultadosClassificacao.getDataDeFabricacao().isEmpty()) {
            edtDataFab.setText(converterResultDataEmExibData(resultadosClassificacao.getDataDeFabricacao()));

            edtDataFab.setBackgroundColor(Color.parseColor("#dddddd"));
            edtDataFab.setTextColor(Color.parseColor("#666666"));
        }

    }

    private String converterResultDataEmExibData(String dataOriginal) {
        SimpleDateFormat simpleDateFormatResult = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat simpleDateFormatExib = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date data = new java.util.Date();

        if (dataOriginal.length() != 6) {
            return "";
        }

        try {
            data = simpleDateFormatResult.parse(dataOriginal);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return simpleDateFormatExib.format(data);

    }

    private boolean datasNaoVazias() {
        return ((!edtDataVal.getText().toString().isEmpty())) && (!edtDataVal.getText().toString().isEmpty());
    }

    private void mostrarVidaUtil() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        TextView diasAteVencimento = (TextView) findViewById(R.id.diasAteVencimento);
        edtPeso.setText(edtDataFab.getText().toString());
        try {
            long d1 = simpleDateFormat.parse(edtDataFab.getText().toString()).getTime();
            long d2 = simpleDateFormat.parse(edtDataVal.getText().toString()).getTime();
            java.util.Date data = new java.util.Date();
            long datual = data.getTime();

            long vida = (d2 - d1) / (1000 * 60 * 60 * 24);
            long atual = (datual - d1) / (1000 * 60 * 60 * 24);

            long rest = vida - atual;

            String test = "";

            if (atual > vida * 2 / 3) {

                diasAteVencimento.setText(Long.toString(rest));
                diasAteVencimento.setTextSize(30);
                diasAteVencimento.setBackgroundColor(Color.parseColor("#AB0000"));
                diasAteVencimento.setTextColor(Color.parseColor("#FFE0E0"));

            } else if (atual < vida * 1 / 3) {
                diasAteVencimento.setText(Long.toString(rest));
                diasAteVencimento.setTextSize(30);
                diasAteVencimento.setBackgroundColor(Color.parseColor("#058C00"));
                diasAteVencimento.setTextColor(Color.parseColor("#CEFFCC"));
            } else {
                diasAteVencimento.setText(Long.toString(rest));
                diasAteVencimento.setTextSize(30);
                diasAteVencimento.setBackgroundColor(Color.parseColor("#E0DD00"));
                diasAteVencimento.setTextColor(Color.parseColor("#FFFED1"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
