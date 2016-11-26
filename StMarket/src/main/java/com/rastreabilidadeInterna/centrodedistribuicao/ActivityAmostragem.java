package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.centrodedistribuicao.objects.MemFile;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.helpers.Laudo;
import com.rastreabilidadeInterna.helpers.Random;
import com.rastreabilidadeInterna.models.Rotulo;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class ActivityAmostragem extends Activity {

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

    private MemFile memFile = null;

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 666;
    private ArrayList<String> savedImages = new ArrayList<String>();
    private Uri uriSavedImage;

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
//        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Carrefour" + File.separator +"Laudos");
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Carrefour" + File.separator +"CentroDeDistribuicao");
        imagesFolder.mkdirs();

//        File image = new File(imagesFolder, "AMT_" + Laudo.fileDate() + ".jpg");
        // getSharedPreferences("Preferences", 0).getString("numeroRecepcao", "")
//        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        SharedPreferences.Editor editor = getSharedPreferences("Preferences", 0).edit();
        //editor.putString("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
//        final String imagem = settings.getString("numeroRecepcao", "");
        final String imagem = getIntent().getExtras().getString("numeroRecepcao");
        final String ean = getIntent().getExtras().getString("codigoproduto");

        File image = new File(imagesFolder, "AMT_" + imagem + "_" + ean + "_" + Laudo.fileDate() + ".jpg");

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

    File pathIdx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx");

    private String codigoCDSTM;
    private Date dataArquivo;

    public Button btnLimpar;
    public Button btnAssociar;
    public Button btnSalvar;

    public EditText edtTotalPecas;
    public EditText edtTotalIrregular;
    public EditText edtPesoIrregular;
    public EditText edtObservacao;
    public EditText edtTotalPalets;
    public EditText edtTotalCaixas;
    public EditText edtPecasPorCaixa;

    public int total;

    public Spinner spNaoConformidade;
    public Spinner spMotivoDevolucao;
    public Spinner spConclusao;

    public TextView tvTotalAmostradas;
    public TextView tvTotalDentro;
    public TextView tvTotalFora;

    public EditText edtPh;
    public EditText edtTemperatura;

    public Button btnStatus;
    public HelperFtpIn helperFTP;

    private TextWatcher textWatcher;

    List<String> list1 = new ArrayList<String>();
    List<String> list2 = new ArrayList<String>();
    List<String> list3 = new ArrayList<String>();

    public ArrayList<String> listaEtiquetas;

    public ModelRecepcao modelRecepcao;
    public ModelProdutoRecebido modelProdutoRecebido;
    public ModelEtiquetaEstoqueCentroDeDistribuicao modelEtiquetaEstoqueCentroDeDistribuicao;

    public String codigoInterno;

    private int idRecepcao = -1;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amostragem);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String filename = extras.getString("filename","");
            if (!filename.equals("")){
                memFile = new MemFile(filename);
            }
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        helperFTP = new HelperFtpIn(this);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//  Alt Vp 11/08/16
//                if (savedImages.size() < 4) {
//                    takePicture();
//                } else {
//                    Toast.makeText(ActivityAmostragem.this, "Você já tirou 4 fotos", Toast.LENGTH_LONG);
                if (savedImages.size() < 8) {
                    takePicture();
                } else {
                    Toast.makeText(ActivityAmostragem.this, "Você já tirou 8 fotos", Toast.LENGTH_LONG);
                }
            }
        });

        MapearComponentes();
        carregarDados();
        verificarSetor();

        setStatus();

    }

    private void verificarSetor() {
        String setor = getIntent().getExtras().getString("setor");

        switch (Integer.parseInt(setor.substring(0, 2))) {
            case 24:
                //sumirComPHeTemperatura();
                sumirComPH();
                break;
            case 20:
                //sumirComParteDaAmostragem();
                sumirComPH();
                break;
            case 21:
                sumirComPH();
                //sumirComParteDaAmostragem();
                break;
            case 25:
                sumirComPH();
                //sumirComParteDaAmostragem();
                break;
            case 15:
                sumirComPH();
                //sumirComParteDaAmostragem();
                break;
            case 23:
                sumirComPH();
                //sumirComParteDaAmostragem();
                break;
            default:
                break;
        }
    }

    private void sumirComPH() {
        LinearLayout layoutDoPH = (LinearLayout) findViewById(R.id.layoutDoPH);
        layoutDoPH.setVisibility(View.GONE);

        edtPh.setText("@NF@");
    }

    private void sumirComParteDaAmostragem() {
        edtTotalIrregular.removeTextChangedListener(textWatcher);

        edtTotalPecas.removeTextChangedListener(textWatcher);

        LinearLayout layoutDaAmostragem3 = (LinearLayout) findViewById(R.id.layoutDaAmostragem3);
        layoutDaAmostragem3.setVisibility(View.GONE);
        edtTotalPecas.setText("@NF@");
        edtTotalIrregular.setText("@NF@");
        edtPesoIrregular.setText("@NF@");

        LinearLayout layoutDosPalets = (LinearLayout) findViewById(R.id.layoutDosPalets);
        layoutDosPalets.setVisibility(View.GONE);
        edtTotalPalets.setText("@NF@");
        edtTotalCaixas.setText("@NF@");
        edtPecasPorCaixa.setText("@NF@");

        LinearLayout layoutDaAmostragem1 = (LinearLayout) findViewById(R.id.layoutDaAmostragem1);
        layoutDaAmostragem1.setVisibility(View.GONE);

        LinearLayout layoutDaAmostragem2 = (LinearLayout) findViewById(R.id.layoutDaAmostragem2);
        layoutDaAmostragem2.setVisibility(View.GONE);

    }

    private void sumirComPHeTemperatura() {
        LinearLayout layoutDoPHeTemperatura = (LinearLayout) findViewById(R.id.layoutDoPHeTemperatura);
        layoutDoPHeTemperatura.setVisibility(View.GONE);
        edtPh.setText("@NF@");
        edtTemperatura.setText("@NF@");
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
        btnAssociar = (Button) findViewById(R.id.btnAssociar);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);

        edtTotalPecas = (EditText) findViewById(R.id.edtTotalPecas);
        edtTotalIrregular = (EditText) findViewById(R.id.edtTotalIrregular);
        edtPesoIrregular = (EditText) findViewById(R.id.edtTotalPesoIrregular);
        edtObservacao = (EditText) findViewById(R.id.edtObservacao);

        edtTotalPalets = (EditText) findViewById(R.id.edtTotalPalets);
        edtTotalCaixas = (EditText) findViewById(R.id.edtTotalCaixas);
        edtPecasPorCaixa = (EditText) findViewById(R.id.edtPecasPorCaixa);

        tvTotalAmostradas = (TextView) findViewById(R.id.tvTotalCaixaAmostrada);

        edtTotalPecas.setText(Integer.toString(Integer.parseInt(edtPecasPorCaixa.getText().toString()) * Integer.parseInt(edtPecasPorCaixa.getText().toString())));

        tvTotalDentro = (TextView) findViewById(R.id.tvTotalDentro);
        tvTotalFora = (TextView) findViewById(R.id.tvTotalFora);

        spNaoConformidade = (Spinner) findViewById(R.id.spNaoConformidade);
        spMotivoDevolucao = (Spinner) findViewById(R.id.spMotivoDevolucao);
        spConclusao = (Spinner) findViewById(R.id.spConclusao);

        edtPh = (EditText) findViewById(R.id.edtPh);

        edtTemperatura = (EditText) findViewById(R.id.edtTemperatura);

        btnStatus = (Button) findViewById(R.id.estoq_btnStatus);

        edtPecasPorCaixa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int totalPalets = Integer.parseInt(edtTotalPalets.getText().toString());
                int caixasPorPalet = Integer.parseInt(edtTotalCaixas.getText().toString());

                total = totalPalets * caixasPorPalet;

                if (total < 15) total = 2;
                else if (total < 50) total = 3;
                else if (total < 150) total = 5;
                else if (total < 500) total = 8;
                else if (total < 3200) total = 13;
                else if (total < 10000) total = 20;

                tvTotalAmostradas.setText(String.valueOf(total));

                edtTotalPecas.setText(Integer.toString(Integer.parseInt(edtPecasPorCaixa.getText().toString()) * total));

            }
        });

        edtTotalCaixas.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int totalPalets = Integer.parseInt(edtTotalPalets.getText().toString());
                int caixasPorPalet = Integer.parseInt(edtTotalCaixas.getText().toString());

                total = totalPalets * caixasPorPalet;

                if (total < 15) total = 2;
                else if (total < 50) total = 3;
                else if (total < 150) total = 5;
                else if (total < 500) total = 8;
                else if (total < 3200) total = 13;
                else if (total < 10000) total = 20;

                tvTotalAmostradas.setText(String.valueOf(total));

                edtTotalPecas.setText(Integer.toString(Integer.parseInt(edtPecasPorCaixa.getText().toString()) * total));

            }
        });

        edtTotalPalets.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int totalPalets = Integer.parseInt(edtTotalPalets.getText().toString());
                int caixasPorPalet = Integer.parseInt(edtTotalCaixas.getText().toString());

                total = totalPalets * caixasPorPalet;

                if (total < 15) total = 2;
                else if (total < 50) total = 3;
                else if (total < 150) total = 5;
                else if (total < 500) total = 8;
                else if (total < 3200) total = 13;
                else if (total < 10000) total = 20;

                tvTotalAmostradas.setText(String.valueOf(total));

                edtTotalPecas.setText(Integer.toString(Integer.parseInt(edtPecasPorCaixa.getText().toString()) * total));
            }
        });


        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ActivityAmostragem.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setTitle("Aguarde");
                pd.setMessage("Transferindo dados...");
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();
                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        helperFTP.enviarArquivos();
                        pd.dismiss();
                    }
                };
                mThread.start();

                //baixarArquivoDeCodigos();
            }
        });


        btnLimpar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (memFile == null) {

                    edtTotalPecas.setText("");
                    edtTotalIrregular.setText("");
                    edtObservacao.setText("");

                    edtPh.setText("");
                    edtTemperatura.setText("");

                } else {

                    limparPre();

                }

            }
        });



        btnAssociar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                new AlertDialog.Builder(ActivityAmostragem.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Finalizar Amostragem")
                        .setMessage("Tem certeza que deseja finalizar essa amostragem?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                                if ((!spConclusao.getSelectedItem().toString().equals("Aprovado")) && savedImages.size() < 3) {
//                                    Toast.makeText(ActivityAmostragem.this, "Você precisa tirar 3 fotos", Toast.LENGTH_LONG);
                                  if ((!spConclusao.getSelectedItem().toString().equals("Aprovado")) && savedImages.size() < 1) {
                                      Toast.makeText(ActivityAmostragem.this, "Você precisa tirar pelo menos 1 foto", Toast.LENGTH_LONG);
                                } else {
                                    try {
                                        validarCampos();
                                        Intent i = new Intent(getBaseContext(), ActivityAssociar.class);

                                        Repositorio repositorio = new Repositorio(ActivityAmostragem.this);
                                        if (getIntent().hasExtra("idRecepcao")) {
                                            i.putExtra("idRecepcao", getIntent().getExtras().getInt("idRecepcao"));
                                        }

                                        i.putExtra("savedImages", savedImages);
                                        i.putExtra("rotulagem", getIntent().getStringArrayListExtra("rotulagem"));

                                        i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                                        i.putExtra("cpf", getIntent().getExtras().getString("cpf"));

                                        i.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
                                        i.putExtra("placa", getIntent().getExtras().getString("placa"));
                                        i.putExtra("data", getIntent().getExtras().getString("data"));

                                        i.putExtra("codigocaixa", getIntent().getExtras().getString("codigocaixa"));
                                        i.putExtra("codigoproduto", getIntent().getExtras().getString("codigoproduto"));
                                        i.putExtra("nomeproduto", getIntent().getExtras().getString("nomeproduto"));
                                        i.putExtra("marca", getIntent().getExtras().getString("marca"));
                                        i.putExtra("fornecedor", getIntent().getExtras().getString("fornecedor"));
                                        i.putExtra("setor", getIntent().getExtras().getString("setor"));
                                        i.putExtra("sif", getIntent().getExtras().getString("sif"));
                                        i.putExtra("datafab", getIntent().getExtras().getString("datafab"));
                                        i.putExtra("dataval", getIntent().getExtras().getString("dataval"));
                                        i.putExtra("totalpalets", edtTotalPalets.getText().toString());
                                        i.putExtra("peso", getIntent().getExtras().getString("peso"));

                                        i.putExtra("codigointerno", codigoInterno);

                                        i.putExtra("ph", edtPh.getText().toString().replaceAll(",", "."));
                                        i.putExtra("temperatura", edtTemperatura.getText().toString().replaceAll(",", "."));
                                        i.putExtra("totalpecas", edtTotalPecas.getText().toString());
                                        i.putExtra("totalpecasregular", Integer.valueOf(edtTotalPecas.getText().toString()) - Integer.valueOf(edtTotalIrregular.getText().toString()));
                                        i.putExtra("totalpecasirregular", Integer.parseInt(edtTotalIrregular.getText().toString()));
                                        i.putExtra("totalpesoirregular", Float.valueOf(edtPesoIrregular.getText().toString()));
                                        i.putExtra("naoconformidade", verificarSpinner(spNaoConformidade.getSelectedItem().toString()));
                                        i.putExtra("motivodevolucao", verificarSpinner(spMotivoDevolucao.getSelectedItem().toString()));
                                        i.putExtra("observacoes", edtObservacao.getText().toString().isEmpty() ? "@NF@" : edtObservacao.getText().toString());
                                        i.putExtra("conclusao", spConclusao.getSelectedItem().toString());

                                        i.putExtra("pecasporcaixa", edtPecasPorCaixa.getText().toString());

                                        String total = edtTotalPecas.getText().toString();
                                        String irregular = edtTotalIrregular.getText().toString();
                                        String regular = String.valueOf(Integer.valueOf(total) - Integer.valueOf(irregular));

                                        i.putExtra("porcentagemregular", ((Float.valueOf(regular) * 100) / Float.valueOf(total)));
                                        i.putExtra("porcentagemirregular", ((Float.valueOf(irregular) * 100) / Float.valueOf(total)));
                                        i.putExtra("totalcaixasamostradas", Integer.parseInt(tvTotalAmostradas.getText().toString()));
                                        i.putExtra("totalpecasamostradas", Integer.valueOf(edtTotalPecas.getText().toString()));

                                        if (memFile == null) {
                                            i.putExtra("filename", "");
                                        } else {
                                            i.putExtra("filename", memFile.getFileName());
                                        }

                                        startActivity(i);
                                        finish();
                                    } catch (Exception e) {
                                        Toast.makeText(ActivityAmostragem.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }).setNegativeButton("Não", null).show();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ActivityAmostragem.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Finalizar Amostragem")
                        .setMessage("Tem certeza que deseja finalizar essa amostragem?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                                if ((!spConclusao.getSelectedItem().toString().equals("Aprovado")) && savedImages.size() < 4) {
//                                    Toast.makeText(ActivityAmostragem.this, "Você precisa tirar 3 fotos", Toast.LENGTH_LONG).show();
                                if ((!spConclusao.getSelectedItem().toString().equals("Aprovado")) && savedImages.size() < 1) {
                                    Toast.makeText(ActivityAmostragem.this, "Você precisa tirar pelo menos 1 foto", Toast.LENGTH_LONG).show();
                                } else if (!spConclusao.getSelectedItem().toString().equals("Aprovado") && spNaoConformidade.getSelectedItem().toString().equals("Selecionar")) {
                                    Toast.makeText(ActivityAmostragem.this, "Você precisa selecionar uma não conformidade", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        validarCampos();
                                        salvarDados();
                                        generateRotulo();
                                        if (!spConclusao.getSelectedItem().toString().equals("Aprovado")){
                                            Laudo.generateCDLaudo(
                                                    modelRecepcao,
                                                    modelProdutoRecebido,
                                                    savedImages,
                                                    getIntent().getStringArrayListExtra("rotulagem"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(ActivityAmostragem.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }).setNegativeButton("Não", null).show();
            }
        });

        textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                ActionIrregularChange();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        edtTotalIrregular.addTextChangedListener(textWatcher);

        edtTotalPecas.addTextChangedListener(textWatcher);


        //List<String> list1 = new ArrayList<String>();
        list1.add("Selecionar");
        list1.add("Aprovado");
        //list1.add("Devolução Parcial");
        //list1.add("Devolução Total");
        //list1.add("Recebido Fora do Padrão");
        list1.add("Recebido com Restrição");
        //list1.add("Bloqueado Total");
        //list1.add("Bloqueado Parcial");
        list1.add("Recebido parcialmente");
        list1.add("Devolvido");
        list1.add("Faltou");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConclusao.setAdapter(dataAdapter1);

        spConclusao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        //List<String> list2 = new ArrayList<String>();
        list2.add("Selecionar");
//        list2.add("Temperatura fora do padrão");
//        list2.add("Tabela nutricional em desacordo");
//        list2.add("Produto Vencido");
//        list2.add("Data de Fabricação/Validade/Lote ilegível");
//        list2.add("Alterações Organolépticas (cor esverdeada e odor)");
//        list2.add("Ausência de: Data de fabricação e/ou validade");
//        list2.add("Modo de conservação");
//        list2.add("Chancela do SIF");
//        list2.add("Informação Contém/Não contém glúten");
//        list2.add("Termógrafo");
//        list2.add("Ausência de Vácuo (total e parcial)");
//        list2.add("Presença de pó de serra (cortes com ossos)");
//        list2.add("Falha na gordura (cortes especiais)");
//        list2.add("Microbolhas");
//        list2.add("Presença de corpos estranhos");
//        list2.add("Rendimento (Peso , corte, excesso de líquido - Sangue exsudado)");
//        list2.add("Técnica (defeito na solda)");
//        list2.add("Embalagem transparente avariada");
//        list2.add("Divergência (informações entre embalagens primária e secundária)");
//        list2.add("Toalete (presença de hematomas, coágulos, carimbos)");
        list2.add("TEMPERATURA IRREGULAR");
        list2.add("ROTULAGEM IRREGULAR");
        list2.add("DATA AVANÇADA CQ");
        list2.add("DATA AVANÇADA LOGÍSTICA");
        list2.add("PRODUTO EM PERÍODO DE QUARENTENA");
        list2.add("PRESENÇA DE CORPO ESTRANHO");
        list2.add("ALTERAÇÃO FÍSICO/QUÍMICA/SENSOLRIAL");
        list2.add("PRESENÇA DE BOLOR");
        list2.add("EMBALAGEM AVARIADA, AUSENCIA DE VÁCUO");
        list2.add("DIVERGÊNCIA DE INFORMAÇÕES");
        list2.add("TRANSPORTE IRREGULAR");
        list2.add("PRESENÇA DE PRAGAS");
        list2.add("REAPRESENTAÇÃO DE MERCADORIA DEVOLVIDA");
        list2.add("DEVOLUÇÃO LOGÍSTICA");
        list2.add("RDC 26");
        list2.add("COZIMENTO SUPERFICIAL");
        list2.add("OUTROS");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNaoConformidade.setAdapter(dataAdapter2);

        //List<String> list3 = new ArrayList<String>();
        list3.add("Selecionar");
        //list3.add("ROTULAGEM IRREGULAR");
        //list3.add("DATA AVANÇADA CQ");
        //list3.add("DATA AVANÇADA LOGÍSTICA");
        //list3.add("PRODUTO EM PERÍODO DE QUARENTENA");
        //list3.add("PRESENÇA DE CORPO ESTRANHO");
        //list3.add("ALTERAÇÃO FÍSICO/QUÍMICA/SENSOLRIAL");
        //list3.add("PRESENÇA DE BOLOR");
        //list3.add("EMBALAGEM AVARIADA");
        //list3.add("AUSENCIA DE VÁCUO");
        //list3.add("DIVERGÊNCIA DE INFORMAÇÕES");
        //list3.add("TRANSPORTE IRREGULAR");
        //list3.add("PRESENÇA DE PRAGAS");
        //list3.add("REAPRESENTAÇÃO DE MERCADORIA DEVOLVIDA");
        //list3.add("OUTROS");
        list3.add("Dano mecanico grave");
        list3.add("Dano mecânico grave (atingindo a polpa)");
        list3.add("Dano mecânico grave (batido)");
        list3.add("Dano mecânico grave (ovos quebrados)");
        list3.add("Dano mecanico leve");
        list3.add("Dano por congelamento");
        list3.add("Dano por geada");
        list3.add("Dano por granizo");
        list3.add("Dano por praga - bicho furão");
        list3.add("Dano por praga - broca");
        list3.add("Dano por praga - cochonilha");
        list3.add("Dano por praga - lagarta da casca");
        list3.add("Dano por praga - mosca das frutas");
        list3.add("Dano por praga - nematóide");
        list3.add("Dano por praga - trips");
        list3.add("Data avançada p/ recebimento");
        list3.add("Deformado");
        list3.add("Degrana");
        list3.add("Desidratado (murcho)");
        list3.add("Despadronização");
        list3.add("Divergência de data (2 datas na embalagem)");
        list3.add("Divergência de data (embalagem primária X embalagem secundária)");
        list3.add("Divergência entre produto e rótulo");
        list3.add("Embalagem avariada");
        list3.add("Embalagem molhada");
        list3.add("Embalagem suja");
        list3.add("Encaroçado");
        list3.add("Escaldadura");
        list3.add("Esfolado");
        list3.add("Estriado");
        list3.add("Falta de coloração externa");
        list3.add("Fitotoxidez");
        list3.add("Formato não caracteristico");
        list3.add("Fumagina");
        list3.add("Fungo");
        list3.add("Lacre violado");
        list3.add("Leprose dos citrus");
        list3.add("Mancha de ácaro branco");
        list3.add("Mancha de ferrugem");
        list3.add("Mancha de látex");
        list3.add("Mancha grave");
        list3.add("Mancha leve");
        list3.add("Mancha negra (fungo )");
        list3.add("Maturação atrasada");
        list3.add("Maturação avançada");
        list3.add("Meia cura (pelada)");
        list3.add("Molhado (a)");
        list3.add("Murcho (a)");
        list3.add("Odor Ruim");
        list3.add("Ombro roxo/verde");
        list3.add("Outros");
        list3.add("Paletização inadequada");
        list3.add("Passado (senescência)");
        list3.add("Pedunculo/raquis desidratado");
        list3.add("Podridão");
        list3.add("Podridão - betô");
        list3.add("Podridão - alternária");
        list3.add("Podridão - antracnose");
        list3.add("Podridão - bico dágua");
        list3.add("Podridão - botrytis");
        list3.add("Podridão - chocolate");
        list3.add("Podridão - mofo/bolor");
        list3.add("Podridão - phytophtera");
        list3.add("Podridão - pinta preta");
        list3.add("Podridão apical");
        list3.add("Podridão estilar");
        list3.add("Podridão mole - erwinia");
        list3.add("Podridão peduncular");
        list3.add("Podridão seca");
        list3.add("Polpa rachada");
        list3.add("Ponta cortada");
        list3.add("Presença de pragas");
        list3.add("Queimado de sol");
        list3.add("Rachadura ");
        list3.add("Rachadura peduncular");
        list3.add("Resíduo de defensivos");
        list3.add("Resíduos de produtos químicos");
        list3.add("Rotulagem irregular");
        list3.add("Sarna");
        list3.add("Sem pedúnculo");
        list3.add("Sem rotulagem");
        list3.add("Sem vácuo");
        list3.add("Sorriso");
        list3.add("Sujo");
        list3.add("Tabela nutricional irregular");
        list3.add("Talo grosso");
        list3.add("Temperatura irregular");
        list3.add("Transporte irregular");
        list3.add("Umidade");
        list3.add("Variola");
        list3.add("Verrugose");
        list3.add("Virose (mosqueado)");
        list3.add("Coloração externa > 40 % (colorido)");
        list3.add("Pintado");
        list3.add("Quebrado");
        list3.add("Polpa exposta");
        list3.add("Queimadura por frio");
        list3.add("Raquis Desidratadas");
        list3.add("Tortuosidade");
        list3.add("Divergencia de produtos");
        list3.add("PODRIDÃO");
        list3.add("Esverdeamento");
        list3.add("Amarelado");
        list3.add("Aparência");
        list3.add("Ausência de embalagem primária individual");
        list3.add("Barriga branca");
        list3.add("Barriga de sapo");
        list3.add("Bitter Pit");
        list3.add("Bolor no Pedúnculo");
        list3.add("Bolor/ Mofo");
        list3.add("Brotamento");
        list3.add("Calibre grande");
        list3.add("Calibre pequeno");
        list3.add("Casca rachada/partida");
        list3.add("Chilling");
        list3.add("Chochamento parcial");
        list3.add("Chochamento total");
        list3.add("Colapso interno");
        list3.add("Coloração escura");
        list3.add("Congelamento");
        list3.add("Coroa fasciculada");
        list3.add("Dano grave/ Produto processado");
        list3.add("Dano mecanico cxs amassadas");
        list3.add("PESO");
        list3.add("DESCALIBRE");
        list3.add("MOFO");
        list3.add("COR");
        list3.add("TAMANHO");
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list3);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotivoDevolucao.setAdapter(dataAdapter3);

    }

    public void limparPre(){

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmação de Limpeza")
                .setMessage("Esta operação tornará esta entrada manual, deseja mesmo limpar?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edtTotalPecas.setText("");
                        edtTotalIrregular.setText("");
                        edtObservacao.setText("");

                        edtPh.setText("");
                        edtTemperatura.setText("");
                        memFile = null;
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }

    public void carregarDados(){
        if (memFile != null) {

            String answer1 = memFile.getAnswer(28);
            String answer2 = memFile.getAnswer(30);
            if ((answer1 != null) && (answer2 != null)) {
                int sum = Integer.valueOf(answer1) + Integer.valueOf(answer2);
                edtTotalPecas.setText(String.valueOf(sum));
            }

            String answer = memFile.getAnswer(35);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtTotalIrregular.setText(answer);
                }
            }

            answer = memFile.getAnswer(36);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtPesoIrregular.setText(answer);
                }
            }

            answer = memFile.getAnswer(39);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtObservacao.setText(answer);
                }
            }

            answer = memFile.getAnswer(24);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtTotalPalets.setText(answer);
                }
            }

            answer = memFile.getAnswer(25);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtTotalCaixas.setText(answer);
                }
            }

            answer1 = memFile.getAnswer(6);
            answer2 = memFile.getAnswer(25);
            if ((answer1 != null) && (answer2 != null)) {
                int div = Math.abs(Integer.valueOf(answer1) / Integer.valueOf(answer2));
                edtPecasPorCaixa.setText(String.valueOf(div));
            }

            answer = memFile.getAnswer(22);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtPh.setText(answer);
                }
            }

            answer = memFile.getAnswer(23);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    edtTemperatura.setText(answer);
                }
            }

            answer = memFile.getAnswer(37);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    spNaoConformidade.setSelection(list2.indexOf(answer));
                }
            }

            answer = memFile.getAnswer(38);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    spMotivoDevolucao.setSelection(list3.indexOf(answer));
                }
            }

            answer = memFile.getAnswer(40);
            if (!(answer == null)) {
                if (!answer.equals("@NF@")) {
                    spConclusao.setSelection(list1.indexOf(answer));
                }
            }

        }
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

    private void salvarDados() {
        lerCodigoInterno();
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


        String total = edtTotalPecas.getText().toString();

        if (total.equals("@NF@")) {
            total = "0";
        }

        String irregular = edtTotalIrregular.getText().toString();

        if (irregular.equals("@NF@")) {
            irregular = "0";
        }

        String regular = String.valueOf(Integer.valueOf(total) - Integer.valueOf(irregular));

        float pesoIrregular;

        if (edtPesoIrregular.getText().toString().isEmpty() || edtPesoIrregular.getText().toString().equals("@NF@")) {
            pesoIrregular = 0;
        } else {
            pesoIrregular = Float.valueOf(edtPesoIrregular.getText().toString());

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
                edtPh.getText().toString().replaceAll(",", "."),
                edtTemperatura.getText().toString().replaceAll(",", "."),
                extras.getString("datafab"),
                extras.getString("dataval"),
                Integer.parseInt(edtTotalPalets.getText().toString().replace("@NF@", "0")),
                Integer.parseInt(edtTotalCaixas.getText().toString().replace("@NF@", "0")),
                0,
                Integer.parseInt(tvTotalAmostradas.getText().toString().replace("@NF@", "0")),
                Integer.valueOf(edtTotalPecas.getText().toString().replace("@NF@", "0")) - Integer.valueOf(edtTotalIrregular.getText().toString().replace("@NF@", "0")),
                ((Float.valueOf(regular.replace("@NF@", "0.0")) * 100) / Float.valueOf(total.replace("@NF@", "1.0"))),
                Integer.parseInt(edtTotalIrregular.getText().toString().replace("@NF@", "0")),
                ((Float.valueOf(irregular.replace("@NF@", "0.0")) * 100) / Float.valueOf(total.replace("@NF@", "1.0"))),
                0,
                0,
                Integer.valueOf(edtTotalPecas.getText().toString().contains("@NF@") ? "0" : edtTotalPecas.getText().toString()),
                Integer.parseInt(edtTotalIrregular.getText().toString().replace("@NF@", "1")),
                pesoIrregular,
                verificarSpinner(spNaoConformidade.getSelectedItem().toString()),
                verificarSpinner(spMotivoDevolucao.getSelectedItem().toString()),
                edtObservacao.getText().toString().isEmpty() ? "@NF@" : edtObservacao.getText().toString(),
                spConclusao.getSelectedItem().toString(),
                null,
                Integer.parseInt(edtPecasPorCaixa.getText().toString().replace("@NF@", "0")),
                getIntent().getExtras().getString("peso"),
                Random.getBoolean(),
                "@NF@",
                codigoCDSTM,
                "@NF@"
        );

        Log.i("peso no model", modelProdutoRecebido.getPeso());

        modelRecepcao = repositorio.recoverRecepcao(modelRecepcao.get_id());

        modelProdutoRecebido = repositorio.recoverProdutoRecebido((int) repositorio.createProdutoRecebido(modelProdutoRecebido));

        listaEtiquetas = new ArrayList<String>();
        listaEtiquetas.add(gerarCodigoSelo(extras.getString("dataval")));

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

    private String verificarSpinner(String string) {
        if (string.equals("Selecionar")) {
            return "";
        } else {
            return string;
        }
    }

    private String gerarCodigoSelo(String dataS) {
        return getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                converterDataEmJuliano(dataS) +
                codigoInterno;
    }

    private String gerarCodigoCDSTM(Date dataArquivo) {
        if (memFile == null) {

            return "CDSTM" +
                    getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                    getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                    new SimpleDateFormat("ddMMyyyHHmmss_").format(dataArquivo) +
                    getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                    getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                    getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");

        } else {

            return memFile.getCode();

        }
    }

    private String converterDataEmJuliano(String data) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dataJuliano = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String anoAtual = simpleDateFormat.format(date);

        simpleDateFormat = new SimpleDateFormat("D");
        String diaAtual = simpleDateFormat.format(date);

        dataJuliano = anoAtual.substring(3) + ajustaZeros(diaAtual, 3);
        return dataJuliano;
    }

    private String ajustaZeros(String valorInicial, int tamanhoEsperado) {
        String valorFinal = "";
        for (int i = 0; i < tamanhoEsperado - valorInicial.length(); i++) {
            valorFinal += "0";
        }
        return valorFinal + valorInicial;
    }

    private boolean lerCodigoInternoDoArquivo() {

        String codigoDeProduto = getIntent().getExtras().getString("codigoproduto");
        Log.i("codigo nos extras", getIntent().getExtras().getString("codigoproduto"));

        File arquivoDeConversao = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "codigos_conv_frac.txt");

        try {
            BufferedReader leitorBufferizado = new BufferedReader(new FileReader(arquivoDeConversao));
            String linhaAtual;
            while ((linhaAtual = leitorBufferizado.readLine()) != null) {
                String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("*"));
                Log.i("codigo de produto", codigoDeProduto);
                Log.i("splitted linha 1", splittedLinhaAtual[1]);
                if (splittedLinhaAtual[1].equals(codigoDeProduto)) {
                    codigoInterno = splittedLinhaAtual[0];
                    Log.i("codigo interno", codigoInterno);
                    Log.i("splitte linha 0", splittedLinhaAtual[0]);
                    return true;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void lerCodigoInterno() {

//        if (lerCodigoInternoDoArquivo()) {
//            actionSalvar();
//        } else {

//            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//            dialog.setTitle("Leia o codigo de barras interno do produto");

//            Context context = this;
//            LinearLayout layout = new LinearLayout(context);
//            layout.setOrientation(LinearLayout.VERTICAL);

//            final EditText codigo = new EditText(context);
//            codigo.setHint("Codigo de barras");
//            layout.addView(codigo);

//            dialog.setView(layout);

//            dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    codigoInterno = codigo.getText().toString().replaceAll("[^A-Za-z0-9 ]", "");
//                    actionSalvar();
//                }
//            });

//            dialog.show();
//        }
       actionSalvar();

    }

    private void actionSalvar() {
        dataArquivo = new Date();
        codigoCDSTM = gerarCodigoCDSTM(dataArquivo);

        salvarDadosNoBanco();
        salvarDadosNoArquivo();
        //salvarDadosNoIdx();
        Intent it = null;

        if (memFile == null) {
            it = new Intent(ActivityAmostragem.this, ActivityRecebimento.class);
            it.putExtra("idRecepcao", idRecepcao);
        } else {
//            it = new Intent(getBaseContext(), ActivitySelecionaRecebimentoFrios.class);
            it = new Intent(getBaseContext(), ActivitySelecionaRecebimentoFriosItem.class);
//            it.putExtra("Nome", getIntent().getExtras().getString("Nome"));
//            it.putExtra("cpf", getIntent().getExtras().getString("cpf"));

            it.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
            it.putExtra("placa",  getIntent().getExtras().getString("placa"));
            it.putExtra("data",  getIntent().getExtras().getString("data"));

            it.putExtra("Nome", getIntent().getExtras().getString("Nome"));
            it.putExtra("cpf", getIntent().getExtras().getString("cpf"));

//            it.putExtra("filename", getIntent().getExtras().getString("filename").split("_")[7]);
            it.putExtra("filename",getIntent().getExtras().getString("filename").substring(0, getIntent().getExtras().getString("filename").indexOf(getIntent().getExtras().getString("filename").split("_")[7])));
        }

        startActivity(it);

        SharedPreferences.Editor editor = getSharedPreferences("Preferences", 0).edit();

        editor.putString("Nome", getIntent().getExtras().getString("Nome"));
        editor.putString("cpf", getIntent().getExtras().getString("cpf"));

        editor.putString("placa", getIntent().getExtras().getString("placa"));
        editor.putString("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
        editor.putString("data", getIntent().getExtras().getString("data"));

        editor.commit();

        finish();
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

    private void salvarDadosNoArquivo() {
        verificarPasta();


        String filenameA = fileNameA(dataArquivo);
        String filenameB = fileNameB(dataArquivo);


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

            printFileContentsB(printWriterB, codigoCDSTM);
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

        linha = "7|" + getIntent().getExtras().getString("nomeproduto");
        printWriter.print(linha);

        printWriter.close();

    }

    private String fileNameA2(int position) {
        Bundle extras = getIntent().getExtras();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_02_CD_" + position + "_ControleDeEstoque_" +
                extras.getString("placa") + "_" +
                extras.getString("numeroRecepcao") + "_" +
                simpleDateFormat.format(new Date()) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private String fileNameB(Date dataArquivo) {
        Bundle extras = getIntent().getExtras();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_B_01_CD_ControleDeEstoque_" +
                extras.getString("placa") + "_" +
                extras.getString("numeroRecepcao") + "_" +
                simpleDateFormat.format(dataArquivo) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private String fileNameA(Date dataArquivo) {
        Bundle extras = getIntent().getExtras();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_01_CD_ControleDeEstoque_" +
                extras.getString("placa") + "_" +
                extras.getString("numeroRecepcao") + "_" +
                simpleDateFormat.format(dataArquivo) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private void printFileContentsB(PrintWriter printWriter, String codigoCDSTM) throws Exception {
        for (String selo : listaEtiquetas) {
            printWriter.println(codigoCDSTM + ":" + selo);
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
        imprimirProcesso(printWriter, 15, codigoInterno);
        imprimirProcesso(printWriter, 16, modelRecepcao.getNumeroDaRecepcao());
        imprimirProcesso(printWriter, 17, modelRecepcao.getPlacaDoCaminhao());
        imprimirProcesso(printWriter, 18, modelRecepcao.getDataDaRecepcao());
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
        imprimirProcesso(printWriter, 38, (modelProdutoRecebido.getMotivoDevolucao().equals("") ? "@NF@" : modelProdutoRecebido.getMotivoDevolucao()));
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
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao" + File.separator + fileName + ".st");

        return f;
    }

    private void validarCampos() throws Exception {
        validarTotalPalets();
        validarCaixasPorPalet();
        validarPecasPorCaixa();
        validarPH();
        validarTemperatura();
        validarTotalPecas();
        validarTotalIrregular();
        validarPesoIrregular();
        validarNaoConformidade();
        validarMotivoDaDevolucao();
        validarObservacoes();
        validarConclusao();
    }

    private void validarPecasPorCaixa() throws Exception {
        validarNaoVazio(edtPecasPorCaixa);
        validarNumerico(edtPecasPorCaixa);
    }

    private void validarCaixasPorPalet() throws Exception {
        validarNaoVazio(edtTotalCaixas);
        validarNumerico(edtTotalCaixas);
    }

    private void validarTotalPalets() throws Exception {
        validarNaoVazio(edtTotalPalets);
        validarNumerico(edtTotalPalets);
    }

    private void validarConclusao() throws Exception {
        validarSelecionado(spConclusao);
    }

    private void validarObservacoes() throws Exception {

    }

    private void validarMotivoDaDevolucao() throws Exception {

    }

    private void validarNaoConformidade() throws Exception {

    }

    private void validarPesoIrregular() throws Exception {

    }

    private void validarTotalIrregular() throws Exception {
        if (spNaoConformidade.getSelectedItem().toString() != "Selecionar") {
          validarNaoVazio(edtTotalIrregular);
          validarNumerico(edtTotalIrregular);
        }
        if (edtTotalIrregular.getText().toString().equals("")) {
            edtTotalIrregular.setText("0");
        }
    }

    private void validarTotalPecas() throws Exception {
        validarNaoVazio(edtTotalPecas);
        validarNumerico(edtTotalPecas);
    }

    private void validarTemperatura() throws Exception {
    }

    private void validarPH() throws Exception {
    }

    private void validarSelecionado(Spinner spinner) throws Exception {
        if (spinner.getSelectedItem().toString().equals("Selecionar")) {
            throw new Exception("Selecione Uma Conclusão");
        }
    }

    private void validarNumerico(EditText editText) throws Exception {
        if (editText.getText().toString().equals("@NF@")) {
            return;
        }
        if (editText.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
            return;
        } else {
            throw new Exception(editText.getHint().toString() + "Precisa ser um número valido");
        }
    }

    private void validarNaoVazio(EditText editText) throws Exception {
        if (editText.getText().toString().isEmpty()) {
            throw new Exception("Preencha todos os campos (" + editText.getHint().toString() + ")");
        }
    }

    public void ActionIrregularChange() {

        String TotalRegular = "0 (0%)";
        String TotalIrregular = "0 (0%)";

        try {

            String total = edtTotalPecas.getText().toString();
            String irregular = edtTotalIrregular.getText().toString();

            if ((!total.equals("")) && (!irregular.equals(""))) {

                String regular = String.valueOf(Integer.valueOf(total) - Integer.valueOf(irregular));
                regular = regular + " (" + String.valueOf((Float.valueOf(regular) * 100) / Float.valueOf(total)) + "%)";
                irregular = irregular + " (" + String.valueOf((Float.valueOf(irregular) * 100) / Float.valueOf(total)) + "%)";

                TotalRegular = regular;
                TotalIrregular = irregular;

            }

        } finally {

            tvTotalDentro.setText(TotalRegular);
            tvTotalFora.setText(TotalIrregular);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_amostragem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                return false;
            case KeyEvent.KEYCODE_SEARCH:
                return false;
            case KeyEvent.KEYCODE_BACK:
                if (memFile != null) {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Saindo Do Controle De Estoque")
                            .setMessage("Tem certeza que deseja sair?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getBaseContext(), ActivitySelecionaRecebimentoFrios.class);
                                    //Intent i = new Intent(getBaseContext(), ActivityControleDeEstoqueCD.class);
                                    i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                                    i.putExtra("cpf", getIntent().getExtras().getString("cpf"));
                                    startActivity(i);
                                    finish();
                                }

                            })
                            .setNegativeButton("Não", null)
                            .show();
                } else {
                    Intent it = null;
                    it = new Intent(this, ActivityRecebimento.class);
                    startActivity(it);
                    finish();
                }
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
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = null;
                        if (memFile != null) {
                            i = new Intent(getBaseContext(), ActivitySelecionaRecebimentoFrios.class);
                        } else {
                            i = new Intent(getBaseContext(), ActivityControleDeEstoqueCD.class);
                        }
                        i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                        i.putExtra("cpf", getIntent().getExtras().getString("cpf"));
                        startActivity(i);
                        finish();
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }
}