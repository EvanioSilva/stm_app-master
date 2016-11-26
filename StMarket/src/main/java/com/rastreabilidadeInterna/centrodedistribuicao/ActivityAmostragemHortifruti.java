package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.helpers.Laudo;
import com.rastreabilidadeInterna.models.Produto;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class ActivityAmostragemHortifruti extends Activity {

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

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_LEVES = 666;
    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_GRAVES = 777;
    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_OUTROS = 888;

    private ArrayList<String> savedImagesGraves = new ArrayList<String>();
    private ArrayList<String> savedImagesLeves = new ArrayList<String>();
    private ArrayList<String> savedImagesOutros = new ArrayList<String>();

    private Uri uriSavedImageGrave;
    private Uri uriSavedImageLeve;
    private Uri uriSavedImageOutro;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("savedImagesGraves", savedImagesGraves);
        outState.putStringArrayList("savedImagesLeves", savedImagesLeves);
        outState.putStringArrayList("savedImagesOutros", savedImagesOutros);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedImagesGraves = savedInstanceState.getStringArrayList("savedImagesGraves");
        savedImagesLeves = savedInstanceState.getStringArrayList("savedImagesLeves");
        savedImagesOutros = savedInstanceState.getStringArrayList("savedImagesOutros");

        View viewGraves = findViewById(R.id.pictureTakerDefGraves);
        LinearLayout linearLayoutGraves = (LinearLayout) viewGraves.findViewById(R.id.linearlayout);
        linearLayoutGraves.removeAllViews();
        for (String savedImage : savedImagesGraves) {
            ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
            Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
            linearLayoutGraves.addView(imageView);
        }

        View viewLeves = findViewById(R.id.pictureTakerDefLeves);
        LinearLayout linearLayoutLeves = (LinearLayout) viewLeves.findViewById(R.id.linearlayout);
        linearLayoutLeves.removeAllViews();
        for (String savedImage : savedImagesLeves) {
            ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
            Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
            linearLayoutLeves.addView(imageView);
        }

        View viewOutros = findViewById(R.id.pictureTakerOutrosDefs);
        LinearLayout linearLayoutOutros = (LinearLayout) viewOutros.findViewById(R.id.linearlayout);
        linearLayoutOutros.removeAllViews();
        for (String savedImage : savedImagesOutros) {
            ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
            Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
            linearLayoutOutros.addView(imageView);
        }
    }

    private void takePicture(int requestCode) {
        //camera stuff
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Carrefour" + File.separator + "Laudos");
        imagesFolder.mkdirs();

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_GRAVES) {
            File image = new File(imagesFolder, Laudo.fileDate() + "_GRV" + ".jpg");
            uriSavedImageGrave = Uri.fromFile(image);

            savedImagesGraves.add(uriSavedImageGrave.toString());

            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImageGrave);
            startActivityForResult(imageIntent, requestCode);
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_LEVES) {
            File image = new File(imagesFolder, Laudo.fileDate() + "_LEV" + ".jpg");
            uriSavedImageLeve = Uri.fromFile(image);

            savedImagesLeves.add(uriSavedImageLeve.toString());

            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImageLeve);
            startActivityForResult(imageIntent, requestCode);
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_OUTROS) {
            File image = new File(imagesFolder, Laudo.fileDate() + "_OTR" + ".jpg");
            uriSavedImageOutro = Uri.fromFile(image);

            savedImagesOutros.add(uriSavedImageOutro.toString());

            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImageOutro);
            startActivityForResult(imageIntent, requestCode);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_GRAVES:
                View view1 = (View) findViewById(R.id.pictureTakerDefGraves);
                LinearLayout linearLayout1 = (LinearLayout) view1.findViewById(R.id.linearlayout);
                linearLayout1.removeAllViews();
                for (String savedImage : savedImagesGraves) {
                    ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
                    Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
                    linearLayout1.addView(imageView);
                }
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_LEVES:
                View view2 = (View) findViewById(R.id.pictureTakerDefLeves);
                LinearLayout linearLayout2 = (LinearLayout) view2.findViewById(R.id.linearlayout);
                linearLayout2.removeAllViews();
                for (String savedImage : savedImagesLeves) {
                    ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
                    Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
                    linearLayout2.addView(imageView);
                }
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_OUTROS:
                View view3 = (View) findViewById(R.id.pictureTakerOutrosDefs);
                LinearLayout linearLayout3 = (LinearLayout) view3.findViewById(R.id.linearlayout);
                linearLayout3.removeAllViews();
                for (String savedImage : savedImagesOutros) {
                    ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
                    Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
                    linearLayout3.addView(imageView);
                }
                break;
        }
    }

    public Button btnLimpar;
    public Button btnAssociar;
    public Button btnSalvar;

    public EditText edtCaixasAvaliadas;
    public EditText edtPodridao;
    public EditText edtDefGraves;
    public EditText edtDefLeves;
    public EditText edtDescalibre;
    public EditText edtPesoAmostragem;
    public EditText edtBrix;
    public EditText edtEstagio;
    public EditText edtLbs;
    public EditText edtOutrosDefeitos;
    public EditText edtEntregue;
    public EditText edtDevolvidoCx;
    public EditText edtDevolvidoPeso;

    public TextView tvProdutoRecebido;
    public TextView tvPodridao;
    public TextView tvDefGraves;
    public TextView tvDefLeves;
    public TextView tvDescalibre;

    public AutoCompleteTextView spDefeitoPrincipal;
    public AutoCompleteTextView spDefeitoPrincipalLeve;
    public Spinner spParecerFinalCQ;

    public Button btnStatus;
    public HelperFtpIn helperFTP;

    public ArrayList<String> listaEtiquetas;

    public String codigoInterno;
    public double pesoPorCaixa = -1;

    private long idProdutoRecebido;
    private long idRecepcao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amostragem_hortifruti);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        helperFTP = new HelperFtpIn(this);

        MapearComponentes();
        idProdutoRecebido = getIntent().getLongExtra("idProdutoRecebidoBanco", -1);

        calcularPesos();

        setStatus();

        View viewGraves = findViewById(R.id.pictureTakerDefGraves);
        Button buttonGraves = (Button) viewGraves.findViewById(R.id.button);
        buttonGraves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savedImagesGraves.size() < 3) {
                    takePicture(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_GRAVES);
                } else {
                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você já tirou 3 fotos", Toast.LENGTH_LONG);
                }
            }
        });

        View viewLeves = findViewById(R.id.pictureTakerDefLeves);
        Button buttonLeves = (Button) viewLeves.findViewById(R.id.button);
        buttonLeves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savedImagesLeves.size() < 3) {
                    takePicture(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_LEVES);
                } else {
                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você já tirou 3 fotos", Toast.LENGTH_LONG);
                }
            }
        });

        View viewOutros = findViewById(R.id.pictureTakerOutrosDefs);
        Button buttonOutros = (Button) viewOutros.findViewById(R.id.button);
        buttonOutros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savedImagesOutros.size() < 3) {
                    takePicture(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_OUTROS);
                } else {
                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você já tirou 3 fotos", Toast.LENGTH_LONG);
                }
            }
        });

    }

    private void calcularPesos() {
        ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti =
                ModelProdutoRecebidoHortifruti.findById(
                        ModelProdutoRecebidoHortifruti.class,
                        idProdutoRecebido);

        List<Produto> produtos = Produto.find(Produto.class,
                "DESCRICAO_PRODUTO = '" + modelProdutoRecebidoHortifruti.getNomeDoProduto() + "'"
        );

        //      Log.i("produto model", produtos.get(0).toString());

        //      Log.i("nome", modelProdutoRecebidoHortifruti.getNomeDoProduto());

        try {
            pesoPorCaixa = Double.parseDouble(produtos.get(0).getPeso());
            Log.i("peso parseado", pesoPorCaixa + "");
        } catch (Exception e) {
            setStatus();
            pesoPorCaixa = 1;
        }

        if (modelProdutoRecebidoHortifruti.getTotalEntregueEmCaixas() != -1) {
            edtEntregue.setText(new DecimalFormat("###.##")
                    .format(pesoPorCaixa * modelProdutoRecebidoHortifruti.getTotalEntregueEmCaixas()));
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

    public void MapearComponentes() {
        btnLimpar = (Button) findViewById(R.id.btnLimpar);
        btnAssociar = (Button) findViewById(R.id.btnAssociar);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);

        edtCaixasAvaliadas = (EditText) findViewById(R.id.edtNumeroCaixasAvaliadas);
        edtPodridao = (EditText) findViewById(R.id.edtPodridao);
        edtDefGraves = (EditText) findViewById(R.id.edtDefeitosGraves);
        edtDefLeves = (EditText) findViewById(R.id.edtDefeitosLeves);
        edtDescalibre = (EditText) findViewById(R.id.edtDescalibre);
        edtPesoAmostragem = (EditText) findViewById(R.id.edtPesoAmostra);
        edtBrix = (EditText) findViewById(R.id.edtBrix);
        edtEstagio = (EditText) findViewById(R.id.edtEstagio);
        edtLbs = (EditText) findViewById(R.id.edtLbs);
        edtOutrosDefeitos = (EditText) findViewById(R.id.spDescricaoDefeitoOutros);
        edtEntregue = (EditText) findViewById(R.id.edtVolumeEntregue);
        edtDevolvidoCx = (EditText) findViewById(R.id.edtVolumeDevolvidoCx);
        edtDevolvidoPeso = (EditText) findViewById(R.id.edtVolumeDevolvidoKg);

        tvProdutoRecebido = (TextView) findViewById(R.id.tvPorcentagemRecebida);
        tvPodridao = (TextView) findViewById(R.id.tvProdridao);
        tvDefGraves = (TextView) findViewById(R.id.tvDefeitosGraves);
        tvDefLeves = (TextView) findViewById(R.id.tvDefeitosLeves);
        tvDescalibre = (TextView) findViewById(R.id.tvDescalibre);

        spDefeitoPrincipal = (AutoCompleteTextView) findViewById(R.id.spDescricaoDefeitoPrincipalGraves);
        spDefeitoPrincipalLeve = (AutoCompleteTextView) findViewById(R.id.spDescricaoDefeitoPrincipalLeves);
        spParecerFinalCQ = (Spinner) findViewById(R.id.spParecerFinalCQ);

        btnStatus = (Button) findViewById(R.id.estoq_btnStatus);

        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ActivityAmostragemHortifruti.this);
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

        btnAssociar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                new AlertDialog.Builder(ActivityAmostragemHortifruti.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Finalizar Amostragem")
                        .setMessage("Tem certeza que deseja finalizar essa amostragem?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if ((!spDefeitoPrincipal.getText().toString().equals("")) && savedImagesGraves.size() < 3) {
                                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você Precisa Tirar 3 Fotos nos Defeitos Graves", Toast.LENGTH_LONG).show();
                                } else if ((!spDefeitoPrincipalLeve.getText().toString().equals("")) && savedImagesLeves.size() < 3) {
                                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você Precisa Tirar 3 Fotos nos Defeitos Leves", Toast.LENGTH_LONG).show();
                                } else if (edtOutrosDefeitos.getText().toString().length() > 0 && savedImagesOutros.size() < 3) {
                                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você Precisa Tirar 3 Fotos nos Outros Defeitos", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        validarCampos();
                                        salvarDadosNoBanco(gerarCodigoCDSTM(new Date()));

                                        Intent i = new Intent(getBaseContext(), ActivityAssociarHortifruti.class);

                                        i.putExtra("idRecepcaoBanco", idProdutoRecebido);

                                        if (getIntent().hasExtra("idRecepcao")) {
                                            i.putExtra("idRecepcao", getIntent().getExtras().getInt("idRecepcao"));
                                        }

                                        i.putExtra("savedImagesGraves", savedImagesGraves);
                                        i.putExtra("savedImagesLeves", savedImagesLeves);
                                        i.putExtra("savedImagesOutros", savedImagesOutros);

                                        //geral
                                        i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                                        i.putExtra("cpf", getIntent().getExtras().getString("cpf"));

                                        //ControleEstoqueCDHortifruti
                                        i.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
                                        i.putExtra("data", getIntent().getExtras().getString("data"));

                                        //Recepção
                                        i.putExtra("codigoproduto", getIntent().getExtras().getString("codigoproduto"));
                                        i.putExtra("nomeproduto", getIntent().getExtras().getString("nomeproduto"));
                                        i.putExtra("fornecedor", getIntent().getExtras().getString("fornecedor"));
                                        i.putExtra("totalcx", getIntent().getExtras().getString("setor"));

                                        //Amostragem
                                        i.putExtra("caixasAvaliadas", edtCaixasAvaliadas.getText().toString().replaceAll(",", "."));
                                        i.putExtra("podridao", edtPodridao.getText().toString().replaceAll(",", "."));
                                        i.putExtra("defGraves", edtDefGraves.getText().toString());
                                        i.putExtra("defLeves", edtDefLeves.getText().toString());
                                        i.putExtra("descalibre", edtDescalibre.getText().toString());
                                        i.putExtra("pesoAmostragem", edtPesoAmostragem.getText().toString());
                                        i.putExtra("brix", edtBrix.getText().toString());
                                        i.putExtra("estagio", edtEstagio.getText().toString());
                                        i.putExtra("lbs", edtLbs.getText().toString());
                                        i.putExtra("outrosDefeitos", edtOutrosDefeitos.getText().toString());
                                        i.putExtra("entregue", edtEntregue.getText().toString());
                                        i.putExtra("devolvidoCx", edtDevolvidoCx.getText().toString());
                                        i.putExtra("devolvidoPeso", edtDevolvidoPeso.getText().toString());
                                        i.putExtra("defeitoPrincipal", spDefeitoPrincipal.getText().toString());
                                        i.putExtra("parecer", spParecerFinalCQ.getSelectedItem().toString());
                                        i.putExtra("produtoRecebido", tvProdutoRecebido.getText().toString());

                                        i.putExtra("porcentagempodridao", tvPodridao.getText().toString());
                                        i.putExtra("porcentagemdefgraves", tvDefGraves.getText().toString());
                                        i.putExtra("porcentagemdefleves", tvDefLeves.getText().toString());
                                        i.putExtra("porcentagemdescalibre", tvDescalibre.getText().toString());

                                        startActivity(i);
                                        finish();
                                    } catch (Exception e) {
                                        Toast.makeText(ActivityAmostragemHortifruti.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                            }
                        }).setNegativeButton("Não", null).show();

            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ActivityAmostragemHortifruti.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Finalizar Amostragem")
                        .setMessage("Tem certeza que deseja finalizar essa amostragem?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if ((!spDefeitoPrincipal.getText().toString().equals("")) && savedImagesGraves.size() < 3) {
                                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você Precisa Tirar 3 Fotos nos Defeitos Graves", Toast.LENGTH_LONG).show();
                                } else if ((!spDefeitoPrincipalLeve.getText().toString().equals("")) && savedImagesLeves.size() < 3) {
                                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você Precisa Tirar 3 Fotos nos Defeitos Leves", Toast.LENGTH_LONG).show();
                                } else if ((!edtOutrosDefeitos.getText().toString().isEmpty()) && savedImagesOutros.size() < 3) {
                                    Toast.makeText(ActivityAmostragemHortifruti.this, "Você Precisa Tirar 3 Fotos nos Outros Defeitos", Toast.LENGTH_LONG).show();
                                } else {

                                    try {
                                        validarCampos();
                                        salvarDados();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(ActivityAmostragemHortifruti.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }).setNegativeButton("Não", null).show();

            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtCaixasAvaliadas.setText("");
                edtPodridao.setText("");
                edtDefGraves.setText("");
                edtDefLeves.setText("");
                edtDescalibre.setText("");
                edtPesoAmostragem.setText("");
                edtBrix.setText("");
                edtEstagio.setText("");
                edtLbs.setText("");
                edtOutrosDefeitos.setText("");
                edtEntregue.setText("");
                edtDevolvidoCx.setText("");
                edtDevolvidoPeso.setText("");

                spDefeitoPrincipal.setSelection(0);
                spDefeitoPrincipalLeve.setSelection(0);
                spParecerFinalCQ.setSelection(0);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calcularPorcentagens();
            }
        };

        edtPesoAmostragem.addTextChangedListener(textWatcher);
        edtPodridao.addTextChangedListener(textWatcher);
        edtDefGraves.addTextChangedListener(textWatcher);
        edtDefLeves.addTextChangedListener(textWatcher);
        edtDescalibre.addTextChangedListener(textWatcher);

        TextWatcher textWatcherPesos = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    edtPesoAmostragem.setText(
                            new DecimalFormat("###").
                                    format(Double.parseDouble(edtCaixasAvaliadas.getText().toString()) * (pesoPorCaixa == -1 ? 1 : pesoPorCaixa * 1000))
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    edtDevolvidoPeso.setText(
                            pesoPorCaixa == -1
                                    ? edtDevolvidoCx.getText().toString()
                                    : new DecimalFormat("###.##").format(Double.parseDouble(edtDevolvidoCx.getText().toString()) * pesoPorCaixa)
                            );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        edtCaixasAvaliadas.addTextChangedListener(textWatcherPesos);
        edtDevolvidoCx.addTextChangedListener(textWatcherPesos);

        TextWatcher textWatcherPorcentagens = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (edtEntregue.getText().toString().isEmpty() || edtEntregue.getText().toString().equals("0")) {

                    } else {

                        double pesoEntregue = Double.parseDouble(edtEntregue.getText().toString());
                        double pesoDevolvido = Double.parseDouble(edtDevolvidoPeso.getText().toString());

                        double pesoRecebido = (pesoEntregue - pesoDevolvido) / pesoEntregue * 100;

                        if (edtEntregue.getText().toString().isEmpty()) {
                            tvProdutoRecebido.setText("NF");
                        } else {
                            tvProdutoRecebido.setText(new DecimalFormat("###.##").format(pesoRecebido) + "%");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        edtEntregue.addTextChangedListener(textWatcherPorcentagens);
        edtDevolvidoPeso.addTextChangedListener(textWatcherPorcentagens);

        List<String> list1 = new ArrayList<String>();
        list1.add("Selecionar");
        list1.add("Amarelado");
        list1.add("Aparência");
        list1.add("Ausência de embalagem primária individual");
        list1.add("Barriga branca");
        list1.add("Barriga de sapo");
        list1.add("Bitter Pit");
        list1.add("Bolor no Pedúnculo");
        list1.add("Bolor/ Mofo");
        list1.add("Brotamento");
        list1.add("Calibre grande");
        list1.add("Calibre pequeno");
        list1.add("Casca rachada/partida");
        list1.add("Chilling");
        list1.add("Chochamento parcial");
        list1.add("Chochamento total");
        list1.add("Colapso interno");
        list1.add("Coloração escura");
        list1.add("Congelamento");
        list1.add("Coroa fasciculada");
        list1.add("Dano grave/ Produto processado");
        list1.add("Dano mecanico cxs amassadas");
        list1.add("Dano mecanico grave");
        list1.add("Dano mecânico grave (atingindo a polpa)");
        list1.add("Dano mecânico grave (batido)");
        list1.add("Dano mecânico grave (ovos quebrados)");
        list1.add("Dano mecanico leve");
        list1.add("Dano por congelamento");
        list1.add("Dano por geada");
        list1.add("Dano por granizo");
        list1.add("Dano por praga - bicho furão");
        list1.add("Dano por praga - broca");
        list1.add("Dano por praga - cochonilha");
        list1.add("Dano por praga - lagarta da casca");
        list1.add("Dano por praga - mosca das frutas");
        list1.add("Dano por praga - nematóide");
        list1.add("Dano por praga - trips");
        list1.add("Data avançada p/ recebimento");
        list1.add("Deformado");
        list1.add("Degrana");
        list1.add("Desidratado (murcho)");
        list1.add("Despadronização");
        list1.add("Divergência de data (2 datas na embalagem)");
        list1.add("Divergência de data (embalagem primária X embalagem secundária)");
        list1.add("Divergência entre produto e rótulo");
        list1.add("Embalagem avariada");
        list1.add("Embalagem molhada");
        list1.add("Embalagem suja");
        list1.add("Encaroçado");
        list1.add("Escaldadura");
        list1.add("Esfolado");
        list1.add("Estriado");
        list1.add("Esverdeamento");
        list1.add("Falta de coloração externa");
        list1.add("Fitotoxidez");
        list1.add("Formato não caracteristico");
        list1.add("Fumagina");
        list1.add("Fungo");
        list1.add("Lacre violado");
        list1.add("Leprose dos citrus");
        list1.add("Mancha de ácaro branco");
        list1.add("Mancha de ferrugem");
        list1.add("Mancha de látex");
        list1.add("Mancha grave");
        list1.add("Mancha leve");
        list1.add("Mancha negra (fungo )");
        list1.add("Maturação atrasada");
        list1.add("Maturação avançada");
        list1.add("Meia cura (pelada)");
        list1.add("Molhado (a)");
        list1.add("Murcho (a)");
        list1.add("Odor Ruim");
        list1.add("Ombro roxo/verde");
        list1.add("Outros");
        list1.add("Paletização inadequada");
        list1.add("Passado (senescência)");
        list1.add("Pedunculo/raquis desidratado");
        list1.add("Podridão");
        list1.add("Podridão - betô");
        list1.add("Podridão - alternária");
        list1.add("Podridão - antracnose");
        list1.add("Podridão - bico d'água");
        list1.add("Podridão - botrytis");
        list1.add("Podridão - chocolate");
        list1.add("Podridão - mofo/bolor");
        list1.add("Podridão - phytophtera");
        list1.add("Podridão - pinta preta");
        list1.add("Podridão apical");
        list1.add("Podridão estilar");
        list1.add("Podridão mole - erwinia");
        list1.add("Podridão peduncular");
        list1.add("Podridão seca");
        list1.add("Polpa rachada");
        list1.add("Ponta cortada");
        list1.add("Presença de pragas");
        list1.add("Produto em período de quarentena");
        list1.add("Queimado de sol");
        list1.add("Rachadura ");
        list1.add("Rachadura peduncular");
        list1.add("Resíduo de defensivos");
        list1.add("Resíduos de produtos químicos");
        list1.add("Rotulagem irregular");
        list1.add("Sarna");
        list1.add("Sem pedúnculo");
        list1.add("Sem rotulagem");
        list1.add("Sem vácuo");
        list1.add("Sorriso");
        list1.add("Sujo");
        list1.add("Tabela nutricional irregular");
        list1.add("Talo grosso");
        list1.add("Temperatura irregular");
        list1.add("Transporte irregular");
        list1.add("Umidade");
        list1.add("Variola");
        list1.add("Verrugose");
        list1.add("Virose (mosqueado)");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
        spDefeitoPrincipal.setAdapter(dataAdapter1);
        spDefeitoPrincipal.setThreshold(1);
        spDefeitoPrincipalLeve.setAdapter(dataAdapter1);
        spDefeitoPrincipalLeve.setThreshold(1);

        List<String> list3 = new ArrayList<String>();
        list3.add("Selecionar");
        list3.add("Bloqueado");
        list3.add("Bloqueio Parcial");
        list3.add("Recebido c/ Restrição");
        list3.add("Recebido");
        list3.add("Recebido Parcial");
        list3.add("Devolução Total");
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list3);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spParecerFinalCQ.setAdapter(dataAdapter3);

    }

    private void calcularPorcentagens() {
        calcularPodridao();
        calcularDefGraves();
        calcularDefLeves();
        calcularDescalibre();
    }

    private void calcularDescalibre() {
        try {
            double descalibre = Double.parseDouble(edtDescalibre.getText().toString());
            double total = Double.parseDouble(edtPesoAmostragem.getText().toString());

            double porcentagem = descalibre / total * 100;

            tvDescalibre.setText(new DecimalFormat("###.##").format(porcentagem) + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcularDefLeves() {
        try {
            double defLeves = Double.parseDouble(edtDefLeves.getText().toString());
            double total = Double.parseDouble(edtPesoAmostragem.getText().toString());

            double porcentagem = defLeves / total * 100;

            tvDefLeves.setText(new DecimalFormat("###.##").format(porcentagem) + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcularPodridao() {
        try {
            double podridao = Double.parseDouble(edtPodridao.getText().toString());
            double total = Double.parseDouble(edtPesoAmostragem.getText().toString());

            double porcentagem = podridao / total * 100;

            tvPodridao.setText(new DecimalFormat("###.##").format(porcentagem) + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcularDefGraves() {
        try {
            double defGraves = Double.parseDouble(edtDefGraves.getText().toString());
            double total = Double.parseDouble(edtPesoAmostragem.getText().toString());

            double porcentagem = defGraves / total * 100;

            tvDefGraves.setText(new DecimalFormat("###.##").format(porcentagem) + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salvarDados() {
        actionSalvar();
    }

    private boolean lerCodigoInternoDoArquivo() {

        String codigoDeProduto = getIntent().getExtras().getString("codigoproduto");

        File arquivoDeConversao = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "codigos_conv_frac.txt");

        try {
            BufferedReader leitorBufferizado = new BufferedReader(new FileReader(arquivoDeConversao));
            String linhaAtual;
            while ((linhaAtual = leitorBufferizado.readLine()) != null) {
                String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("*"));
                if (splittedLinhaAtual[1].equals(codigoDeProduto)) {
                    codigoInterno = splittedLinhaAtual[0];
                    return true;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void actionSalvar() {
        Date dataArquivo = new Date();
        String codigoCDSTM = gerarCodigoCDSTM(dataArquivo);

        salvarDadosNoBanco(codigoCDSTM);
        salvarDadosNoArquivo(codigoCDSTM, dataArquivo);
        Intent it = null;
        it = new Intent(ActivityAmostragemHortifruti.this, ActivityRecebimentoHortifruti.class);

        it.putExtra("idProdutoRecebidoBanco", idProdutoRecebido);
        it.putExtra("idProdutoRecebidoBanco", idProdutoRecebido);

        SharedPreferences.Editor editor = getSharedPreferences("Preferences", 0).edit();

        editor.putString("Nome", getIntent().getExtras().getString("Nome"));
        editor.putString("cpf", getIntent().getExtras().getString("cpf"));

        editor.putString("placa", getIntent().getExtras().getString("placa"));
        editor.putString("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
        editor.putString("data", getIntent().getExtras().getString("data"));

        editor.commit();

        startActivity(it);
        finish();
    }

    private void salvarDadosNoBanco(String codigoCDSTM) {
        ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti =
                ModelProdutoRecebidoHortifruti.findById(
                        ModelProdutoRecebidoHortifruti.class, idProdutoRecebido);

        modelProdutoRecebidoHortifruti.setCaixasAvaliadas(Integer.parseInt(edtCaixasAvaliadas.getText().toString()));
        modelProdutoRecebidoHortifruti.setPodridao(!edtPodridao.getText().toString().isEmpty() ? Double.parseDouble(edtPodridao.getText().toString()) : 0);
        modelProdutoRecebidoHortifruti.setDefGraves(!edtDefGraves.getText().toString().isEmpty() ? Double.parseDouble(edtDefGraves.getText().toString()) : 0);
        modelProdutoRecebidoHortifruti.setDefLeves(!edtDefLeves.getText().toString().isEmpty() ? Double.parseDouble(edtDefLeves.getText().toString()) : 0);
        modelProdutoRecebidoHortifruti.setDescalibre(!edtDescalibre.getText().toString().isEmpty() ? Double.parseDouble(edtDescalibre.getText().toString()) : 0);
        modelProdutoRecebidoHortifruti.setPesoDaAmostra(Integer.parseInt(edtPesoAmostragem.getText().toString()));
        modelProdutoRecebidoHortifruti.setBrix(Double.parseDouble(edtBrix.getText().toString().isEmpty() ? "-1" : edtBrix.getText().toString()));
        modelProdutoRecebidoHortifruti.setEstagio(edtEstagio.getText().toString());
        modelProdutoRecebidoHortifruti.setLbs(edtLbs.getText().toString());
        modelProdutoRecebidoHortifruti.setDemaisDefeitos(edtOutrosDefeitos.getText().toString());
        modelProdutoRecebidoHortifruti.setTotalEntregueEmCaixas(
                edtEntregue.getText().toString().isEmpty() || edtEntregue.getText().toString().equals("0") ?
                        -1 :
                        Integer.parseInt(edtEntregue.getText().toString()));
        modelProdutoRecebidoHortifruti.setVolumeDevolvidoCaixas(Integer.parseInt(edtDevolvidoCx.getText().toString()));
        modelProdutoRecebidoHortifruti.setVolumeDevolvidoKg(Double.parseDouble(edtDevolvidoPeso.getText().toString()));
        modelProdutoRecebidoHortifruti.setDescricaoDefeitoPrincipal(spDefeitoPrincipal.getText().toString());
        modelProdutoRecebidoHortifruti.setParecerFinalDoCQ(spParecerFinalCQ.getSelectedItem().toString());
        modelProdutoRecebidoHortifruti.setPorcentagemProdutosRecebidos(Double.parseDouble(
                tvProdutoRecebido.getText().toString().equals("NF")
                        ? "-1"
                        : tvProdutoRecebido.getText().toString().replace("%", "").replace(",", ".")));
        modelProdutoRecebidoHortifruti.setPorcentagemPodridao(Double.parseDouble(tvPodridao.getText().toString().replace("%", "").replace(",", ".")));
        modelProdutoRecebidoHortifruti.setPorcentagemDefGraves(Double.parseDouble(tvDefGraves.getText().toString().replace("%", "").replace(",", ".")));
        modelProdutoRecebidoHortifruti.setPorcentagemDefLeves(Double.parseDouble(tvDefLeves.getText().toString().replace("%", "").replace(",", ".")));
        modelProdutoRecebidoHortifruti.setPorcentagemDescalibre(Double.parseDouble(tvDescalibre.getText().toString().replace("%", "").replace(",", ".")));
        modelProdutoRecebidoHortifruti.setDescricaoDefeitoPrincipalLeve(spDefeitoPrincipalLeve.getText().toString());
        modelProdutoRecebidoHortifruti.setCodigoCDSTM(codigoCDSTM);
        modelProdutoRecebidoHortifruti.setVolumeEntregueKg(
                !edtEntregue.getText().toString().isEmpty() ?
                        Double.parseDouble(edtEntregue.getText().toString()) :
                        -1
        );

        modelProdutoRecebidoHortifruti.save();


        if (!spParecerFinalCQ.getSelectedItem().toString().equals("Aprovado")) {
            Laudo.generateCDHLaudo(modelProdutoRecebidoHortifruti.getModelRecepcaoHortifruti(), modelProdutoRecebidoHortifruti, savedImagesGraves, savedImagesLeves, savedImagesOutros);
        }
    }

    private void salvarDadosNoArquivo(String codigoCDSTM, Date dataArquivo) {
        verificarPasta();


        String filenameA = fileNameA(dataArquivo);
        //String filenameB = fileNameB(dataArquivo);

        try {
            deleteFile(filenameA);
            //deleteFile(filenameB);

            File arquivoA = gerarFile(filenameA);
            //File arquivoB = gerarFile(filenameB);

            FileOutputStream fileOutputStreamA = new FileOutputStream(arquivoA);
            OutputStreamWriter outputStreamWriterA = new OutputStreamWriter(fileOutputStreamA, "UTF-8");
            PrintWriter printWriterA = new PrintWriter(outputStreamWriterA);

            //FileOutputStream fileOutputStreamB = new FileOutputStream(arquivoB);
            //OutputStreamWriter outputStreamWriterB = new OutputStreamWriter(fileOutputStreamB, "UTF-8");
            //PrintWriter printWriterB = new PrintWriter(outputStreamWriterB);

            printFileContentsA(printWriterA, codigoCDSTM);
            outputStreamWriterA.close();
            fileOutputStreamA.close();

            //printFileContentsB(printWriterB, codigoCDSTM);
            //outputStreamWriterB.close();
            //fileOutputStreamB.close();

            //createArquivosA2();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String gerarCodigoCDSTM(Date dataArquivo) {
        return "CDHSTM" +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                new SimpleDateFormat("ddMMyyyHHmmss_").format(dataArquivo) +
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
        imprimirProcesso(printWriter, 7, getIntent().getExtras().getString("nomeproduto"));
        imprimirProcesso(printWriter, 9, getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")");
        imprimirProcesso(printWriter, 16, getIntent().getExtras().getString("numeroRecepcao"));
        imprimirProcesso(printWriter, 18, getIntent().getExtras().getString("data"));
        imprimirProcesso(printWriter, 19, getIntent().getExtras().getString("codigoproduto"));
        imprimirProcesso(printWriter, 20, getIntent().getExtras().getString("fornecedor"));
        imprimirProcesso(printWriter, 25, getIntent().getExtras().getString("totalcx"));
        imprimirProcesso(printWriter, 27, edtCaixasAvaliadas.getText().toString().replaceAll(",", "."));
        imprimirProcesso(printWriter, 47, edtPodridao.getText().toString().replaceAll(",", "."));
        imprimirProcesso(printWriter, 48, edtDefGraves.getText().toString());
        imprimirProcesso(printWriter, 49, edtDefLeves.getText().toString());
        imprimirProcesso(printWriter, 50, edtDescalibre.getText().toString());
        imprimirProcesso(printWriter, 51, edtPesoAmostragem.getText().toString());
        imprimirProcesso(printWriter, 52, edtBrix.getText().toString());
        imprimirProcesso(printWriter, 53, edtEstagio.getText().toString());
        imprimirProcesso(printWriter, 54, edtLbs.getText().toString());
        imprimirProcesso(printWriter, 55, edtOutrosDefeitos.getText().toString());
        imprimirProcesso(printWriter, 56, edtEntregue.getText().toString());
        imprimirProcesso(printWriter, 57, edtDevolvidoCx.getText().toString());
        imprimirProcesso(printWriter, 58, edtDevolvidoPeso.getText().toString());
        imprimirProcesso(printWriter, 59, spDefeitoPrincipal.getText().toString());
        imprimirProcesso(printWriter, 60, spParecerFinalCQ.getSelectedItem().toString());
        imprimirProcesso(printWriter, 61, tvProdutoRecebido.getText().toString());

        imprimirProcesso(printWriter, 63, tvPodridao.getText().toString());
        imprimirProcesso(printWriter, 64, tvDefGraves.getText().toString());
        imprimirProcesso(printWriter, 65, tvDefLeves.getText().toString());
        imprimirProcesso(printWriter, 66, tvDescalibre.getText().toString());

        imprimirProcesso(printWriter, 69, new SimpleDateFormat("HH:mm:ss").format(new Date()));

        imprimirProcesso(printWriter, 41, "Centro De Distribuição Hortifruti");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imprimirProcesso(printWriter, 45, telephonyManager.getDeviceId());

    }

    private void imprimirProcesso(PrintWriter printWriter, int codigo, String value) {
        String linha = codigo + "-" + value + "-" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "|";
        printWriter.print(linha);
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
                e.printStackTrace();
            }
        }
    }

    private String fileNameA2(int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_02_CD_" + position + "_ControleDeEstoqueHortifruti_" +
                simpleDateFormat.format(new Date()) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private String fileNameB(Date dataArquivo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_B_01_CD_ControleDeEstoqueHortifruti_" +
                simpleDateFormat.format(dataArquivo) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private String fileNameA(Date dataArquivo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_01_CD_ControleDeEstoqueHortifruti_" +
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

    private File gerarFile(String fileName) {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao" + File.separator + fileName + ".st");

        return f;
    }

    private void validarCampos() throws Exception {
        validarCaixasAvaliadas();
        //validarPodridao();
        //validarDefGraves();
        //validarDefLeves();
        //validarDescalibre();
        validarPesoAmostragem();
        //validarBrix();
        //validarEstagio();
        //validarLbs();
        //validarOutrosDefeitos();
        validarEntregue();
        validarDevolvidoCx();
        validarDevolvidoPeso();
        //validarDefeitoPrincipal();
        validarParecerFinalCQ();
    }

    private void validarCaixasAvaliadas() throws Exception {
        validarNaoVazio(edtCaixasAvaliadas);
        validarNumerico(edtCaixasAvaliadas);
    }

    private void validarPodridao() throws Exception {
        validarNaoVazio(edtPodridao);
        validarNumerico(edtPodridao);
    }

    private void validarDefGraves() throws Exception {
        validarNaoVazio(edtDefGraves);
        validarNumerico(edtDefGraves);
    }

    private void validarDefLeves() throws Exception {
        validarNaoVazio(edtDefLeves);
        validarNumerico(edtDefLeves);
    }

    private void validarDescalibre() throws Exception {
        validarNaoVazio(edtDescalibre);
        validarNumerico(edtDescalibre);
    }

    private void validarPesoAmostragem() throws Exception {
        validarNaoVazio(edtPesoAmostragem);
        validarNumerico(edtPesoAmostragem);
    }

    private void validarBrix() throws Exception {
        validarNaoVazio(edtBrix);
        validarNumerico(edtBrix);
    }

    private void validarEstagio() throws Exception {
        validarNaoVazio(edtEstagio);
        validarNumerico(edtEstagio);
    }

    private void validarLbs() throws Exception {
        validarNaoVazio(edtLbs);
        validarNumerico(edtLbs);
    }

    private void validarOutrosDefeitos() throws Exception {

    }

    private void validarEntregue() throws Exception {
        //validarNaoVazio(edtEntregue);
        //validarNumerico(edtEntregue);
    }

    private void validarDevolvidoCx() throws Exception {
        validarNaoVazio(edtDevolvidoCx);
        validarNumerico(edtDevolvidoCx);
    }

    private void validarDevolvidoPeso() throws Exception {
        validarNaoVazio(edtDevolvidoPeso);
        validarNumerico(edtDevolvidoPeso);
    }

    private void validarDefeitoPrincipal() throws Exception {

    }

    private void validarParecerFinalCQ() throws Exception {
        validarSelecionado(spParecerFinalCQ);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_amostragem_hortifruti, menu);
        return true;
    }

    public boolean onOptionsItemSelected(int featureId, MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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