package com.rastreabilidadeInterna.fracionamento;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.controleEstoque.classificadorProdutoFabricante;
import com.rastreabilidadeInterna.controleEstoque.ResultadoDeClassificacao;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.geral.varGlobais;
import com.rastreabilidadeInterna.helpers.FtpConnectionHelper;
import com.rastreabilidadeInterna.helpers.LogGenerator;
import com.rastreabilidadeInterna.models.Produto;

import static java.lang.System.in;

public class ActivityFracionamento extends Activity {

    /**
     * O {@link com.rastreabilidadeInterna.BD.Repositorio} que fornece dados persistidos em banco
     */
    private Repositorio repositorio;

    /**
     * O {@link android.widget.EditText} que contém o valor do selo a ser inserido
     */
    private EditText edtSelo;

    /**
     * O {@link android.widget.Button} utilizado para limpar as informações da tela
     */
    private Button btnLimpar;
    /**
     * O {@link android.widget.Button} utilizado para salvar as informações
     */
    private Button btnEnviar;
    /**
     * O {@link android.widget.Button} utilizado mostrar o status de sincroina servidor/tablet, também utilizado para iniciar a sincronização
     */
    private Button btnStatus;
    /**
     * O {@link android.widget.Button} utilizado para iniciar a atividade de inserção de um novo fracionamento do tipo granel
     */
    private Button btnGranel;

    /**
     * O {@link android.widget.ListView} utilizado para mostrar os fracionamentos lidos no dia
     */
    private ListView lvList;

    private TextView txDia;

    private boolean downloadFTPNecessario = true;
    private Boolean enviou;

    private Handler handler = new Handler();

    final ArrayList<String> list = new ArrayList<String>();

    private ProgressDialog progressDialog;
    private ProgressDialog dialog;

    private String msgErro;

    File path = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento");
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    int diaTela;
    int diasDeValidade;

    LogGenerator log;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new LogGenerator(this);
        log.append("========================================");
        log.append("Iniciando Fracionamento");
        log.append("========================================");
        setContentView(R.layout.activity_fracionamento_principal);
        settings = getSharedPreferences("Preferences", 0);
        editor = settings.edit();
        repositorio = new Repositorio(this);
        defineComponent();
        defineAction();
        defineCaminho();
//		enviarArquivos();

        if (downloadFTPNecessario) {
            DownloadFTP();
        }
        populaList();
        diaAtualTela();

        diaTela = (Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

        setStatus();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



    }

    @Override
    public void onResume() {
        super.onResume();
        setStatus();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("downloadFTPNecessario", false);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        downloadFTPNecessario = savedInstanceState.getBoolean("downloadFTPNecessario");
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
        } else if (item.getItemId() == R.id.action_historico){
            Intent intent = new Intent(this, ActivityHistoricoFracionamento.class);
            startActivity(intent);
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void logout(){
        Intent i = new Intent(this, ActivityTelaInicial.class);
        startActivity(i);
        finish();
    }

    private void historicoFracionamento() {
        Intent intent = new Intent(this, ActivityHistoricoFracionamento.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fracionamento_principal, menu);
        menu.getItem(0).setTitle("Usuário: " + getIntent().getExtras().getString("Nome"));
        return true;
    }

    private void setStatus() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates ui
                if (path.list().length > 1) {
                    btnStatus.setBackgroundResource(R.drawable.background_red);
                } else {
                    btnStatus.setBackgroundResource(R.drawable.background_green);
                }

            }
        });
    }


    private void defineCaminho() {
        File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        if (!local.exists()) {
            local.mkdir();
        }

        if (!path.exists()) {
            path.mkdir();
        }
    }

    private void diaAtualTela() {
        SimpleDateFormat dataMod = new SimpleDateFormat("dd/MM/yyyy");
        txDia.setText("Selos lidos no dia: " + dataMod.format(new Date(System.currentTimeMillis())));
    }

    private void populaList() {
        list.clear();

        List<objetoFracionamento> listObj = new ArrayList<objetoFracionamento>();

        SimpleDateFormat dataMod = new SimpleDateFormat("dd/MM/yyyy");
        listObj = repositorio.listarFracionamentos(dataMod.format(new Date(System.currentTimeMillis())));

        objetoFracionamento obj = new objetoFracionamento(this);


        String finalSelo;
        String tipo;
        String vaiPraLista = "";

        if (listObj != null) {

            int quantidade = 1;

            for (int i = 0; i < listObj.size(); i++) {

                if (i > 0) {

                    if (listObj.get(i).tipoDoProduto.equals(listObj.get(i - 1).tipoDoProduto)) {
                        quantidade++;

                        if (i + 1 == listObj.size()) {
                            list.add("QTD:  " + quantidade + " | " + vaiPraLista);
                        } else if (!listObj.get(i + 1).tipoDoProduto.equals(listObj.get(i).tipoDoProduto)) {
                            list.add("QTD:  " + quantidade + " | " + vaiPraLista);
                        }

                    } else {
                        quantidade = 1;
                        obj = listObj.get(i);

                        Log.i("Atual", obj.tipoDoProduto);
            /*
            FLAG != 1 SIGNIFICA QUE É UM FRACIONAMENTO COMUM, E NÃO UM GRANEL
            IMPOSSIVEL MECHER NA ESTRUTURA DO BANCO, PORTANTO FOI NECESSARIO ESSA ADAPTAÇÃO
            */
                        if (obj.flag != -1) {

                            if (obj.novoSelo.substring(obj.novoSelo.length() - 6, obj.novoSelo.length() - 5).equals("9")) {
                                String selo = obj.novoSelo.substring(obj.novoSelo.length() - 6);

                                try {
                                    String dataLeituraS = obj.dataLeitura;
                                    java.util.Date dataLeituraD = new SimpleDateFormat("dd/MM/yyyy").parse(dataLeituraS);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(dataLeituraD);
                                    calendar.add(Calendar.DATE, 3);
                                    String dataValidadeS = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

                                    vaiPraLista = selo + " - " + obj.tipoDoProduto + " - " + dataValidadeS;
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } else {
                                vaiPraLista = obj.seloSafe.substring(5) + " - " + repositorio.listarIdxBaixadoComValidade(obj.seloSafe.substring(5));
                            }
                        }

                        if (i + 1 == listObj.size()) {
                            list.add("QTD:  " + quantidade + " | " + vaiPraLista);
                        } else if (!listObj.get(i + 1).tipoDoProduto.equals(listObj.get(i).tipoDoProduto)) {
                            list.add("QTD:  " + quantidade + " | " + vaiPraLista);
                        }

                    }
                } else {

                    quantidade = 1;
                    obj = listObj.get(i);
                    /*
                    FLAG != 1 SIGNIFICA QUE É UM FRACIONAMENTO COMUM, E NÃO UM GRANEL
                    IMPOSSIVEL MECHER NA ESTRUTURA DO BANCO, PORTANTO FOI NECESSARIO ESSA ADAPTAÇÃO
                    */
                    if (obj.flag != -1) {

                        if (obj.novoSelo.substring(obj.novoSelo.length() - 6, obj.novoSelo.length() - 5).equals("9")) {
                            String selo = obj.novoSelo.substring(obj.novoSelo.length() - 6);
                            vaiPraLista = selo + " - " + obj.tipoDoProduto + " - " + obj.dataDeValidade;
                        } else {
                            vaiPraLista = obj.seloSafe.substring(5) + " - " + repositorio.listarIdxBaixadoComValidade(obj.seloSafe.substring(5));
                        }
                    }

                    if (i + 1 == listObj.size()) {
                        list.add("QTD:  " + quantidade + " | " + vaiPraLista);
                    } else if (!listObj.get(i + 1).tipoDoProduto.equals(listObj.get(i).tipoDoProduto)) {
                        list.add("QTD:  " + quantidade + " | " + vaiPraLista);
                    }

                }
            }
        }
        updateListView();
    }

    private ArrayList<String> obterListaHistorico(String data) {
        List<objetoFracionamento> listObj = new ArrayList<objetoFracionamento>();
        ArrayList<String> listaString = new ArrayList<String>();

        listObj = repositorio.listarFracionamentos(data);

        objetoFracionamento obj = new objetoFracionamento(this);

        String finalSelo;
        String tipo;

        for (int i = 0; i < listObj.size(); i++) {
            obj = listObj.get(i);

            Log.i("Objeto", obj.toString());

            /*

            FLAG != 1 SIGNIFICA QUE É UM FRACIONAMENTO COMUM, E NÃO UM GRANEL
            IMPOSSIVEL MECHER NA ESTRUTURA DO BANCO, PORTANTO FOI NECESSARIO ESSA ADAPTAÇÃO

             */

            //pegar apenas substring e add o tipo do produto/selo
            if (obj.flag != -1) {
                Log.i("novo selo", obj.novoSelo);
                Log.i("selo safe", obj.seloSafe);
                if (obj.novoSelo.substring(obj.novoSelo.length() - 6, obj.novoSelo.length() - 5).equals("9")) {

                    String selo = obj.novoSelo.substring(obj.novoSelo.length() - 6);
                    String codigo = obj.seloSafe.substring(13);

                    File pathCodesFr = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "codigos_conv_frac.txt");

                    boolean found = false;

                    try {
                        BufferedReader leitorBufferizado = new BufferedReader(new FileReader(pathCodesFr));
                        String linhaAtual;
                        while ((linhaAtual = leitorBufferizado.readLine()) != null) {
                            String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("*"));
                            if (splittedLinhaAtual[0].equals(codigo)) {
                                listaString.add(selo + " - " + splittedLinhaAtual[3]);
                                Log.i("Lista Add", selo + " - " + splittedLinhaAtual[3]);
                                found = true;
                            }
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }

                    if (!found) {
                        listaString.add(selo + " - " + obj.tipoDoProduto);
                    }
                } else {
                    listaString.add(obj.seloSafe.substring(5) + " - " + repositorio.listarIdxBaixado(obj.seloSafe.substring(5)));
                    Log.i("Lista Add", obj.seloSafe.substring(5) + " - " + repositorio.listarIdxBaixado(obj.seloSafe.substring(5)));
                }
            }

        }
        return listaString;
    }

    private void defineComponent() {
        log.append("Definindo componentes");
        edtSelo = (EditText) findViewById(R.id.edtSeloFrac);
        lvList = (ListView) findViewById(R.id.listviewFrac);
        btnLimpar = (Button) findViewById(R.id.btnLimparFrac);
        btnEnviar = (Button) findViewById(R.id.btnEnviarFrac);
        txDia = (TextView) findViewById(R.id.txDiaAtual);
        btnStatus = (Button) findViewById(R.id.frac_btnStatus);
        btnGranel = (Button) findViewById(R.id.btnGranel);
    }

    private void mostrarFracionamentos() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        ArrayList<String> lista = new ArrayList<String>();

                        String dataHistorico = "";

                        String dia = "";
                        String mes = "";

                        if (monthOfYear < 9) {
                            mes = "0";
                        }
                        mes = mes + Integer.toString(monthOfYear + 1);

                        if (dayOfMonth < 10) {
                            dia = "0";
                        }
                        dia += dayOfMonth;

                        dataHistorico = dia + "/" + mes + "/" + year;

                        Log.i("Data", dataHistorico);

                        lista = obterListaHistorico(dataHistorico);

                        Log.i("Lista", Integer.toString(lista.size()));

                        showHistoricoDialog(lista, dataHistorico);
                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

    private void showHistoricoDialog(ArrayList<String> lista, String dataHistorico) {
        CharSequence[] items = new String[lista.size()];

        for (int i = 0; i < lista.size(); i++) {
            items[i] = lista.get(i);
            Log.i("receita", items[i] + "");
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Fracionamentos do dia " + dataHistorico);
        dialogBuilder.setCancelable(true);

        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(), "Selection: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.create().show();
    }

    private void defineAction() {

        log.append("Definindo listener do granel");
        btnGranel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ActivityGranel.class);

                intent.putExtra("Nome", getIntent().getExtras().getString("Nome"))
                ;
                intent.putExtra("cpf", getIntent().getExtras().getString("cpf"));

                startActivity(intent);
            }
        });

        log.append("definindo listener do status");
        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                enviarArquivos();
                DownloadFTP();
                setStatus();
            }
        });

        log.append("definindo listener do limpar");
        btnLimpar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSelo.setText("");
            }
        });

        log.append("definindo listener do enviar");
        btnEnviar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                salvar();
            }
        });

        log.append("definindo listener do selo");
        edtSelo.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (edtSelo.getText().toString().length() == 11 && edtSelo.getText().toString().substring(0, 2).equals(getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", ""))) {
                    salvar();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        edtSelo.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean temFoco) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

    }

    private void updateListView() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(super.getCount() - position - 1, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lvList.setAdapter(adapter);
    }

    private void verificarDiaTela() {
        log.append("verificando dia na tela");
        int diaAux = (Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

        if (diaAux != diaTela) {
            diaTela = diaAux;
            list.clear();
            updateListView();
            populaList();
            diaAtualTela();
        }

    }

    private boolean validarSelo(String selo) {
        try {
            validarSeloNaoVazio(selo);
            validarTamanhoSelo(selo, 11);
            return true;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            return false;
        }
    }

    private void validarTamanhoSelo(String selo, int tamanho) throws Exception {
        if (selo.length() != tamanho) {
            throw new Exception("O código de selo precisa ter " + tamanho + " characteres");
        }
    }

    private void validarSeloNaoVazio(String seloIn) throws Exception {
        if (seloIn.isEmpty()) {
            throw new Exception("O código do selo não foi digitado");
        }
    }

    private objetoFracionamento criarObjetoFracionamento() {
        objetoFracionamento objetoFracionamento = new objetoFracionamento(
                0,
                edtSelo.getText().toString(),
                novaEtiqueta(),
                new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()),
                getSharedPreferences("Preferences", 0).getString("AREADEUSO", ""),
                1,
                getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")",
                "",
                this
        );
        return objetoFracionamento;
    }

    private void salvar() {
        String selo = edtSelo.getText().toString();
        log.append("iniciando salvamento");
        if (selo.length() == 11 &&
                selo.substring(0, 2).equals(getSharedPreferences("Preferences", 0)
                        .getString("NUMCLIENTE", ""))) {
            log.append("selo normal padrão safe");
            Log.i("selo", "normal");
            salvarSeloNormal();
        } else {
            if (selo.isEmpty()) {
                Toast.makeText(this, "Leia uma etiqueta ou um código de barras de produto", Toast.LENGTH_LONG).show();
            } else {
                log.append("selo formato bizarro sem padrão");
                salvarSeloBizarro();
            }
        }

    }

    private void salvarSeloBizarro() {
        final String selo = edtSelo.getText().toString();

        long count = Produto.count(Produto.class, "codigo_ean = '" + edtSelo.getText().toString() + "'", null);

        Produto produtoEncontrado = null;

        if (count > 0) {
            List<Produto> produtos = Produto.find(Produto.class, "codigo_ean = '" + edtSelo.getText().toString() + "'");
            for (Produto produto : produtos) {
                Log.i("produto", produto.toString());
                produtoEncontrado = produto;
            }
        }

        TextView tvAlerta = new TextView(this);
        tvAlerta.setText("Produto não cadastrado previamente, por favor preencha todas as informações corretamente!");
        tvAlerta.setTextColor(Color.RED);
        tvAlerta.setBackgroundColor(Color.parseColor("#FFA8A8"));
        tvAlerta.setPadding(5, 5, 5, 5);
        tvAlerta.setTextSize((float) 20.0);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Confirme o nome do produto, a data de validade e a quantidade");

        Context context = this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tvTipo = new TextView(this);
        tvTipo.setText("Nome do Produto: ");
        layout.addView(tvTipo);

        final EditText tipo = new EditText(context);
        tipo.setHint("Nome do produto");
        layout.addView(tipo);

        TextView tvQtd = new TextView(this);
        tvQtd.setText("Quantidade: ");
        layout.addView(tvQtd);

        final EditText quantidade = new EditText(context);
        quantidade.setHint("Quantidade");
        quantidade.setText("1");
        quantidade.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(quantidade);

        TextView tvDFab = new TextView(this);
        tvDFab.setText("Data de Fabricação: ");
        layout.addView(tvDFab);

        final EditText dataF = new EditText(context);
        dataF.setHint("Data de Farbicação");
        dataF.setFocusable(false);
        dataF.setFocusableInTouchMode(false);
        layout.addView(dataF);

        TextView tvVal = new TextView(this);
        tvVal.setText("Data de Validade: ");
        layout.addView(tvVal);

        final EditText data = new EditText(context);
        data.setHint("Data de validade");
        data.setFocusable(false);
        data.setFocusableInTouchMode(false);
        layout.addView(data);

        TextView tvLote = new TextView(this);
        tvLote.setText("Lote: ");
        layout.addView(tvLote);

        final EditText lote = new EditText(context);
        lote.setHint("Lote");
        layout.addView(lote);

        TextView tvSif = new TextView(this);
        tvSif.setText("SIF: ");
        layout.addView(tvSif);

        final EditText sif = new EditText(context);
        sif.setHint("SIF");
        sif.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(sif);

        TextView tvFab = new TextView(this);
        tvFab.setText("Fabricante: ");
        layout.addView(tvFab);

        final EditText fab = new EditText(context);
        fab.setHint("Fabricante");
        layout.addView(fab);

        final Calendar myCalendar = Calendar.getInstance();

        class FlagDias {
            private boolean value;

            public FlagDias(boolean value) {
                this.value = value;
            }

            public boolean getValue() {
                return value;
            }

            public void setValue(boolean value) {
                this.value = value;
            }
        }

        final FlagDias flagDias = new FlagDias(false);

        class myDateSetListener implements DatePickerDialog.OnDateSetListener {

            String editAtual = "";

            public void setEditAtual(String editAtual) {
                this.editAtual = editAtual;
            }


            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                if (editAtual.equals("V")) {
                    data.setText(simpleDateFormat.format(myCalendar.getTime()));

                    if (!flagDias.getValue()) {
                        dataF.setText(calcularDataFab(myCalendar));
                    }
                } else if (editAtual.equals("F")) {
                    dataF.setText(simpleDateFormat.format(myCalendar.getTime()));

                    if (!flagDias.getValue()) {
                        data.setText(calcularDataVal(myCalendar));
                    }
                }

                try {
                    String codIdx = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "")
                            + getSharedPreferences("Preferences", 0).getString("NUMLOJA", "")
                            + converterDataEmJuliano(new SimpleDateFormat("dd/MM/yyyy").parse(data.getText().toString()))
                            + edtSelo.getText().toString();
                    String[] idx = repositorio.listarIdxBaixadoHistorico(codIdx);
                    Log.i("codigo idx", codIdx);
                    if (idx[2].isEmpty()) {
                        Log.i("pegou do", "classificador");
                    } else {
                        sif.setText(idx[2]);
                        Log.i("pegou do", "produtos idx");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        }

        final myDateSetListener date = new myDateSetListener();

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                date.setEditAtual("V");

                DatePickerDialog dpd = new DatePickerDialog(ActivityFracionamento.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                dpd.setTitle("Data de Validade");
                dpd.show();

            }
        });

        dataF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                date.setEditAtual("F");

                DatePickerDialog dpd = new DatePickerDialog(ActivityFracionamento.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                dpd.setTitle("Data de Fabricação");
                dpd.show();
            }
        });

        File pathCodesFr = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "codigos_conv_frac.txt");
        String codigoParaSalvar = selo;
        boolean found = false;
        try {
            BufferedReader leitorBufferizado = new BufferedReader(new FileReader(pathCodesFr));
            String linhaAtual;
            Log.i("Selo", selo);
            while ((linhaAtual = leitorBufferizado.readLine()) != null && !found) {
                String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("*"));
                Log.i("Linha Atual 0", splittedLinhaAtual[0]);
                Log.i("Linha Atual 2", splittedLinhaAtual[2]);
                if (splittedLinhaAtual[0].equals(selo)) {
                    tipo.setText(splittedLinhaAtual[2]);
                    codigoParaSalvar = splittedLinhaAtual[1];
                    found = true;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        File produtosCE = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + "produtos_ce.txt");
        ResultadoDeClassificacao resultadoDeClassificacao = classificadorProdutoFabricante.classificar(codigoParaSalvar, "", produtosCE);

        if (!found && resultadoDeClassificacao.getNomeDoProduto().isEmpty()) {
            layout.addView(tvAlerta);
        }

        dialog.setView(layout);


        if (tipo.getText().toString().isEmpty()) {
            tipo.setText(resultadoDeClassificacao.getNomeDoProduto());
        }

        try {
            String codIdx = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "")
                    + getSharedPreferences("Preferences", 0).getString("NUMLOJA", "")
                    + converterDataEmJuliano(new SimpleDateFormat("dd/MM/yyyy").parse(resultadoDeClassificacao.getDataDeValidade()))
                    + edtSelo.getText().toString();
            String[] idx = repositorio.listarIdxBaixadoHistorico(codIdx);
            if (idx[2].isEmpty()) {
                sif.setText(resultadoDeClassificacao.getCodigoSif());
                Log.i("pegou do", "classificador");
            } else {
                sif.setText(idx[2]);
                Log.i("pegou do", "produtos CE");
            }
        } catch (Exception e){
            e.printStackTrace();
            sif.setText(resultadoDeClassificacao.getCodigoSif());
        }

        try {
            data.setText(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyMMdd").parse(resultadoDeClassificacao.getDataDeValidade())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fab.setText(resultadoDeClassificacao.getNomeDoFabricante());

        resultadoDeClassificacao.setDataDeFabricacao(resultadoDeClassificacao.getDiasDeValidade());

        if (!resultadoDeClassificacao.getDiasDeValidade().isEmpty()) {
            diasDeValidade = Integer.valueOf(resultadoDeClassificacao.getDiasDeValidade());
            flagDias.setValue(false);
        } else {
            diasDeValidade = 0;
            flagDias.setValue(true);
        }

        String dataFabAux = "";
        try {
            dataFabAux = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyMMdd").parse(resultadoDeClassificacao.getDataDeFabricacao()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String dataFab = dataFabAux;
        dataF.setText(dataFab);

        lote.setText(resultadoDeClassificacao.getCodigoDoLote());

        final String finalCodigoParaSalvar = codigoParaSalvar;
        final String finalSelo = selo;
        dialog.setPositiveButton("Confirmar", null);

        if (count > 0) {
            tipo.setText(produtoEncontrado.getDescricaoProduto());
            fab.setText(produtoEncontrado.getRazaoSocialFornecedor());
        }

        final AlertDialog dialog1 = dialog.create();
        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String nomeProduto = tipo.getText().toString();
                        String dataVal = data.getText().toString();
                        String quant = quantidade.getText().toString();
                        String codSif = sif.getText().toString();
                        String nomeFab = fab.getText().toString();
                        String dataFab = dataF.getText().toString();
                        String lot = lote.getText().toString();

                        if (nomeProduto.equals("")) {
                            Toast.makeText(ActivityFracionamento.this, "Insira o nome do produto", Toast.LENGTH_LONG).show();
                        } else if (dataVal.equals("")) {
                            Toast.makeText(ActivityFracionamento.this, "Insira a data de validade", Toast.LENGTH_LONG).show();
                        } else if (codSif.equals("")) {
                            Toast.makeText(ActivityFracionamento.this, "Insira o codigo SIF", Toast.LENGTH_LONG).show();
                        } else if (nomeFab.equals("")) {
                            Toast.makeText(ActivityFracionamento.this, "Insira o nome do fabricante", Toast.LENGTH_LONG).show();
                        } else if (quant.equals("") || Integer.valueOf(quant) == 0) {
                            Toast.makeText(ActivityFracionamento.this, "Insira a quantidade", Toast.LENGTH_LONG).show();
                        } else if (dataFab.equals("") || Integer.valueOf(quant) == 0) {
                            Toast.makeText(ActivityFracionamento.this, "Insira a data de validade", Toast.LENGTH_LONG).show();
                            // LOTE OBRIGATORIO
                            // COMENTAR PROXIMAS DUAS LINHAS PARA REMOVER OBRIGATORIEDADE
                            //} else if (lot.equals("") || Integer.valueOf(quant) == 0) {
                            //    Toast.makeText(ActivityFracionamento.this, "Insira o lote", Toast.LENGTH_LONG).show();
                        } else if (menorOuIgualHoje(dataVal)) {
                            Toast.makeText(ActivityFracionamento.this, "Confira a Data de Validade", Toast.LENGTH_LONG).show();
                        } else if (dataValMenorQueDataFab(dataVal, dataFab)) {
                            Toast.makeText(ActivityFracionamento.this, "Confira as datas de Validade e Fabricação", Toast.LENGTH_LONG).show();
                        } else {
                            ExecutarSalvamentoBizarro(nomeProduto, dataVal, quant, codSif, nomeFab, dataFab, lot, finalCodigoParaSalvar, finalSelo);
                            dialog1.dismiss();
                        }
                    }
                });
            }
        });

        dialog1.show();
    }

    private String converterDataEmJuliano(java.util.Date data){
        String dataJuliano = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String anoAtual = simpleDateFormat.format(data);

        simpleDateFormat = new SimpleDateFormat("D");
        String diaAtual = simpleDateFormat.format(data);

        dataJuliano = anoAtual.substring(3) + ajustaZeros(diaAtual, 3);
        return dataJuliano;
    }

    private boolean dataValMenorQueDataFab(String dataVal, String dataFab){
        try {
            java.util.Date dateV = new SimpleDateFormat("dd/MM/yyyy").parse(dataVal);
            java.util.Date dateF = new SimpleDateFormat("dd/MM/yyyy").parse(dataFab);

            if (dateV.before(dateF)){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean menorOuIgualHoje(String dataVal){

        try {
            java.util.Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dataVal);
            if (date.before(new java.util.Date()) || date.equals(new java.util.Date())){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String calcularDataFab(Calendar calendar) {
        ResultadoDeClassificacao resultadoDeClassificacao = new ResultadoDeClassificacao();
        resultadoDeClassificacao.setDataDeValidade(new SimpleDateFormat("yyMMdd").format(calendar.getTime()));
        resultadoDeClassificacao.setDataDeFabricacao(diasDeValidade);

        try {
            return (
                    new SimpleDateFormat("dd/MM/yyyy")
                            .format(
                                    new SimpleDateFormat("yyMMdd").parse(
                                            resultadoDeClassificacao.getDataDeFabricacao()
                                    )
                            )
            );
        } catch (Exception e) {
            return new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        }
    }

    private String calcularDataVal(Calendar calendar) {
        ResultadoDeClassificacao resultadoDeClassificacao = new ResultadoDeClassificacao();
        resultadoDeClassificacao.setDataDeFabricacao(new SimpleDateFormat("yyMMdd").format(calendar.getTime()));
        resultadoDeClassificacao.setDataDeValidade(diasDeValidade);

        try {
            return (
                    new SimpleDateFormat("dd/MM/yyyy")
                            .format(
                                    new SimpleDateFormat("yyMMdd").parse(
                                            resultadoDeClassificacao.getDataDeValidade()
                                    )
                            )
            );
        } catch (Exception e) {
            return new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        }
    }

    private void ExecutarSalvamentoBizarro(String nome, String data, String quant, String codSif, String nomeFab, String dFab, String lot, String codigoLido, String selo) {
        String nomeProduto = nome;
        String dataValidade = data;
        int qtdProduto = Integer.valueOf(quant);
        String sif = codSif;
        String nomeFabricante = nomeFab;
        String dataFabricacao = dFab;
        String lote = lot;

        String codigoDoArquivoB = null;
        try {
            codigoDoArquivoB = gerarPrefixoSemData() +
                    converterEmJuliano(new SimpleDateFormat("dd/MM/yyyy").parse(dataValidade)) +
                    codigoLido;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String numCliente = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "");
        String idLoja = getSharedPreferences("Preferences", 0).getString("NUMLOJA", "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName;
        ArrayList<String> linhasDoArquivo = new ArrayList<String>();

        for (int i = 0; i < qtdProduto; i++) {

            verificarSerialDiario();
            String codigoDeRastreabilidade = gerarCodigoDeRastreabilidade();

            String codigoDeRastreabilidadeArquivo = gerarPrefixo() + codigoDeRastreabilidade;
            linhasDoArquivo.add(codigoDoArquivoB + ":" + codigoDeRastreabilidadeArquivo);

            objetoFracionamento objetoFracionamento = new objetoFracionamento(
                    0,
                    codigoDoArquivoB, // selo do B selo safe :: juliano + codigo interno
                    codigoDeRastreabilidadeArquivo, // selo do A novo selo :: 9TTSSS
                    new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()),
                    getSharedPreferences("Preferences", 0).getString("AREADEUSO", ""),
                    1,
                    getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")",
                    nomeProduto,
                    this
            );
            objetoFracionamento.dataDeValidade = dataValidade;
            objetoFracionamento.sif = sif;
            objetoFracionamento.fabricante = nomeFabricante;
            objetoFracionamento.dataFabricacao = dataFabricacao;
            objetoFracionamento.lote = lote;

            objetoFracionamento.codigoDeBalanca = edtSelo.getText().toString();

            repositorio.salvarFracionamento(objetoFracionamento);

            File pathCodesFr = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "codigos_conv_frac.txt");

            objetoFracionamento.codigoDeBarrasProdutoCaixa = codigoLido;
            objetoFracionamento.codigoDeBarrasLidoDaCaixa = selo;

            try {
                BufferedReader leitorBufferizado = new BufferedReader(new FileReader(pathCodesFr));
                String linhaAtual;
                while ((linhaAtual = leitorBufferizado.readLine()) != null) {
                    String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("*"));
                    if (splittedLinhaAtual[0].equals(codigoLido)) {
                        objetoFracionamento.codigoDeBarrasProdutoCaixa = splittedLinhaAtual[1];
                    }
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            fileName = "OK_A_03_Fracionamento_" + simpleDateFormat.format(new java.util.Date()) + "_" + numCliente + settings.getString("NUMLOJA", "") + settings.getString("NUMTABLET", "") + "_" + getSharedPreferences("Preferences", 0).getInt("SERIALDIARIO", -1);
            objetoFracionamento.saveFileA(fileName, idLoja, numCliente);

            SharedPreferences.Editor editor1 = getSharedPreferences("Preferences", 0).edit();
            editor1.putInt("SERIALDIARIO", getSharedPreferences("Preferences", 0).getInt("SERIALDIARIO", -1) + 1);
            editor1.commit();

            if (i == qtdProduto - 1) {
                fileName = "OK_B_02_Fracionamento_" + simpleDateFormat.format(new java.util.Date()) + "_" + numCliente + settings.getString("NUMLOJA", "") + settings.getString("NUMTABLET", "");
                objetoFracionamento.saveFileB(fileName, linhasDoArquivo);
            }

        }

        populaList();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000, 0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edtSelo.setText("");
                            edtSelo.setFocusable(true);
                            edtSelo.setFocusableInTouchMode(true);
                            edtSelo.requestFocus();
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                            setStatus();
                            //enviarArquivos();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        thread.start();

    }

    private String gerarPrefixo() {
        String CC = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "");
        String LLL = getSharedPreferences("Preferences", 0).getString("NUMLOJA", "");
        String DDDD = converterEmJuliano(new java.util.Date());

        return CC + LLL + DDDD;
    }

    private String gerarPrefixoSemData() {
        String CC = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "");
        String LLL = getSharedPreferences("Preferences", 0).getString("NUMLOJA", "");

        return CC + LLL;
    }

    /**
     * Metodo que ajusta valores completando com zeros a esqueda
     *
     * @param valorInicial    {@link java.lang.String} a ser ajustada
     * @param tamanhoEsperado numero de casas que o valor deve possuir no total
     * @return {@link java.lang.String} ajustada com zeros a esquerda
     */
    private String ajustaZeros(String valorInicial, int tamanhoEsperado) {
        String valorFinal = "";
        for (int i = 0; i < tamanhoEsperado - valorInicial.length(); i++) {
            valorFinal += "0";
        }
        return valorFinal + valorInicial;
    }

    /**
     * Metodo que converte a data atual em seu formato juliano
     *
     * @return {@link java.lang.String} contendo a data no formado YDDD
     */
    private String converterEmJuliano(java.util.Date date) {
        String dataJuliano = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String anoAtual = simpleDateFormat.format(date);

        simpleDateFormat = new SimpleDateFormat("D");
        String diaAtual = simpleDateFormat.format(date);

        dataJuliano = anoAtual.substring(3) + ajustaZeros(diaAtual, 3);
        return dataJuliano;
    }


    private String gerarCodigoDeRastreabilidade() {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", 0);
        String TT = sharedPreferences.getString("NUMTABLET", "");
        String SSS = ajustaZeros(Integer.toString(sharedPreferences.getInt("SERIALDIARIO", -1)), 3);

        return "9" + TT + SSS;
    }

    private void verificarSerialDiario() {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", 0);
        String dataPrefs = sharedPreferences.getString("DATASERIALDIARIO", "NOTFOUND");
        if (dataPrefs.equals("NOTFOUND") || notToday(dataPrefs)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putString("DATASERIALDIARIO", simpleDateFormat.format(new java.util.Date()));
            editor1.putInt("SERIALDIARIO", 0);
            editor1.commit();
        }
    }

    private boolean notToday(String dataAComparar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (simpleDateFormat.format(new java.util.Date()).equals(dataAComparar)) {
            return false;
        }
        return true;
    }

    private void salvarSeloNormal() {
        verificarDiaTela();
        log.append("criando objeto fracionamento");
        objetoFracionamento objetoFracionamento;
        //

        log.append("selo nao pode receber foco");
        edtSelo.setFocusable(false);

        if (validarSelo(edtSelo.getText().toString())) {
            objetoFracionamento = criarObjetoFracionamento();
            log.append("objeto fracionamento criado: " + objetoFracionamento.toString());
            SimpleDateFormat dataMod = new SimpleDateFormat("dd/MM/yyyy");

            String[] idx = repositorio.listarIdxBaixadoHistorico(edtSelo.getText().toString());

            log.append("tipo do produto: " + repositorio.listarIdxBaixado(edtSelo.getText().toString()));
            objetoFracionamento.tipoDoProduto = idx[0];
            log.append("tipo colocado no objeto: " + objetoFracionamento.tipoDoProduto);

            objetoFracionamento.fabricante = idx[1];
            objetoFracionamento.sif = idx[2];
            objetoFracionamento.lote = idx[3];
            objetoFracionamento.dataFabricacao = idx[4];
            objetoFracionamento.dataDeValidade = idx[5];

            log.append("salvando no banco local");
            repositorio.salvarFracionamento(objetoFracionamento);

            SimpleDateFormat dateNome = new SimpleDateFormat("yyyyMMdd");
            String data_arquivo = dateNome.format(new Date(System.currentTimeMillis()));
            log.append("data arquivo: " + data_arquivo);

            String aux2 = settings.getString("NUMTABLET", "");
            String idLoja = settings.getString("IDLOJA", "");

            dateNome = new SimpleDateFormat("HHmmss");
            String hora_arquivo = dateNome.format(new Date(System.currentTimeMillis()));
            String numCliente = settings.getString("NUMCLIENTE", "");
            //+ "_" + numTablet+numLoja+numCliente
            String fileName = "OK_A_03_Fracionamento_" + data_arquivo + "_" + hora_arquivo + "_" + numCliente + settings.getString("NUMLOJA", "") + settings.getString("NUMTABLET", "");
            log.append("filename: " + fileName);
            log.append("salvando arquivo A");
            objetoFracionamento.saveFileA(fileName, idLoja, numCliente);
            log.append("objeto: " + objetoFracionamento.toString());

            fileName = "OK_B_02_Fracionamento_" + data_arquivo + "_" + hora_arquivo + "_" + numCliente + settings.getString("NUMLOJA", "") + settings.getString("NUMTABLET", "");
            log.append("filename: " + fileName);
            objetoFracionamento.saveFileB(fileName);

            String aux = edtSelo.getText().toString().substring(5, 11);
            log.append("aux: " + aux);
            String tipo = repositorio.listarIdxBaixado(aux);
            log.append("tipo: " + tipo);

            log.append("adicionou a lista: " + aux + " - " + tipo);

            //list.add(aux + " - " + tipo);
            //updateListView();

            populaList();


            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        log.append("iniciou thread de timeout");
                        Thread.sleep(1000, 0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                edtSelo.setText("");
                                edtSelo.setFocusable(true);
                                edtSelo.setFocusableInTouchMode(true);
                                edtSelo.requestFocus();
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                log.append("setando status");
                                setStatus();
                                //enviarArquivos();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.append(e.getStackTrace().toString());
                    }

                }
            };

            thread.start();
        }
    }

    private String diaAtual() {

        String aux = "";
        int year = ((Calendar.getInstance().get(Calendar.YEAR)) % 10);

        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int CurrentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

        if (String.valueOf(CurrentDayOfYear).length() == 1) {
            aux = "00" + CurrentDayOfYear;
        } else if (String.valueOf(CurrentDayOfYear).length() == 2) {
            aux = "0" + CurrentDayOfYear;
        } else {
            aux = String.valueOf(CurrentDayOfYear);
        }

        return String.valueOf(year) + aux;

    }

    private String novaEtiqueta() {
        String aux = edtSelo.getText().toString();

        return aux.substring(0, 5) + diaAtual() + aux.substring(5, aux.length());
    }

    //UPLOAD -----------------------------------------------------------------------------------------------------

    private class AppZip {
        List<String> fileList;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

        private final String OUTPUT_ZIP_FILE = Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento" + File.separator + "ZIP_FR_" + getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") + getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") + getSharedPreferences("Preferences", 0).getString("NUMTABLET", "") + "_" + simpleDateFormat.format(new java.util.Date()) + ".zip";
        private final String SOURCE_FOLDER = Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento";

        AppZip() {
            fileList = new ArrayList<String>();
        }

        public void ziparTudo() {
            this.generateFileList(new File(SOURCE_FOLDER));
            this.zipIt(OUTPUT_ZIP_FILE);
        }

        /**
         * Zip it
         *
         * @param zipFile output ZIP file location
         */
        public void zipIt(String zipFile) {

            byte[] buffer = new byte[1024];

            try {

                if (this.fileList.size() > 1) {

                    FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos);

                    Log.i("Output to Zip", zipFile);


                    for (String file : this.fileList) {

                        if (file.equals("produtos_ce.txt")) {

                        } else {

                            if (file.substring(file.length() - 3).equals(".st")) {

                                Log.i("File Added", file);
                                ZipEntry ze = new ZipEntry(file);
                                zos.putNextEntry(ze);

                                FileInputStream in =
                                        new FileInputStream(SOURCE_FOLDER + File.separator + file);

                                int len;
                                while ((len = in.read(buffer)) > 0) {
                                    zos.write(buffer, 0, len);
                                }

                                new File(SOURCE_FOLDER + File.separator + file).delete();

                            }
                        }

                    }

                    in.close();

                    zos.closeEntry();
                    //remember close it
                    zos.close();

                    System.out.println("Done");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        /**
         * Traverse a directory and get all files,
         * and add the file into fileList
         *
         * @param node file or directory
         */
        public void generateFileList(File node) {

            //add file only0
            if (node.isFile()) {
                fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
            }

            if (node.isDirectory()) {
                String[] subNote = node.list();
                for (String filename : subNote) {
                    generateFileList(new File(node, filename));
                }
            }

        }

        /**
         * Format the file path for zip
         *
         * @param file file path
         * @return Formatted file path
         */
        private String generateZipEntry(String file) {
            return file.substring(SOURCE_FOLDER.length() + 1, file.length());
        }
    }


    private void enviarArquivos() {

        LogGenerator logGenerator = new LogGenerator(this);
        logGenerator.enviarLogs();

        AppZip appZip = new AppZip();
        appZip.ziparTudo();

        if (path.list().length > 1) {
            progressDialog = new ProgressDialog(ActivityFracionamento.this);
            progressDialog.setTitle("Conectando");
            progressDialog.setMessage("Enviando dados, por favor aguarde...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setMax(3);
            progressDialog.setProgress(1);

            new Thread(new Runnable() {
                public void run() {
                    File fList[] = path.listFiles();

                    for (int i = 0; i < (fList.length); i++) {
                        File arquivo = fList[i];
                        if (arquivo.isFile()) {
                            enviou = envioFTP(arquivo.getName());
                            if (!enviou) break;
                        }
                    }

                    progressDialog.setProgress(2);

                    handler.post(new Runnable() {
                        public void run() {
                            progressDialog.setProgress(3);
                            progressDialog.dismiss();
                            if (!enviou) {
                                Toast.makeText(getApplicationContext(), msgErro, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    setStatus();
                }
            }).start();
        }
        setStatus();
    }

    private Boolean envioFTP(String nomeArquivo) {
        FTPClient ftp = new FTPClient();
        Boolean retorno = false;
        msgErro = "";

        FtpConnectionHelper ftpConnectionHelper = new FtpConnectionHelper();

        try {

            ftp.connect(ftpConnectionHelper.getServidorFtp(), ftpConnectionHelper.getPortaFtp());
            ftp.login(ftpConnectionHelper.getUsuarioFtp(), ftpConnectionHelper.getSenhaFtp());

//			ftp.changeWorkingDirectory("Teste"); 

            if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento" + File.separator + nomeArquivo);
                Log.d("NOME ARQUIVO", file.toString());
                FileInputStream arqEnviar = new FileInputStream(file);

                ftp.enterLocalPassiveMode();
                ftp.changeWorkingDirectory("arquivos_novos");
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);


                ftp.storeFile(nomeArquivo, arqEnviar);

                arqEnviar.close();
                file.delete();
                retorno = true;
            }


            ftp.logout();
            ftp.disconnect();

        } catch (SocketException e) {
            msgErro = "1 - " + e.getMessage();
            retorno = false;
        } catch (IOException e) {
            msgErro = "2 - " + e.getMessage();
            retorno = false;
        } catch (Exception e) {
            msgErro = "3 - " + e.getMessage();
            retorno = false;
        }
        return retorno;
    }

    // DOWNLOAD   ----------------------------------------------------------------------------------------------------

    public void DownloadFTP() {
        try {
            dialog = new ProgressDialog(this);
            dialog.setTitle("Conectando");
            dialog.setMessage("Baixando dados, por favor aguarde...");
            dialog.setMax(4);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(1);
            dialog.show();
            new Thread(new Runnable() {
                public void run() {
                    //comando
                    File root = Environment.getExternalStorageDirectory();
                    File local1 = new File(root + File.separator + "Carrefour");
                    if (!local1.exists()) {
                        local1.mkdir();
                    }
                    local1 = new File(root + File.separator + "Carrefour" + File.separator + "Fracionamento");
                    if (!local1.exists()) {
                        local1.mkdir();
                    }
                    local1 = new File(root + File.separator + "Carrefour" + File.separator + "Fracionamento" + File.separator + "Download");
                    if (!local1.exists()) {
                        local1.mkdir();
                    }

                    SharedPreferences settings = getSharedPreferences("Preferences", 0);
                    String prefix = settings.getString("NUMCLIENTE", "") + settings.getString("NUMLOJA", "");

                    ArrayList<String> listaLocal = new ArrayList<String>();
                    ArrayList<String> listaFtp = new ArrayList<String>();
                    listaFtp = listaArquivosFtp();
                    //Log.i("Lista FTP",listaFtp.get(0).toString());
                    File fList[] = local1.listFiles();

                    for (int i = 0; i < (fList.length); i++) {
                        File arquivo = fList[i];
                        if (arquivo.isFile()) {
                            listaLocal.add(arquivo.getName());
                        }
                    }
                    dialog.setProgress(2);

                    for (int i = 0; i < listaFtp.size(); i++) {
                        if (listaFtp.get(i).toString().substring(0, 5).equals(prefix)) {
                            //if (listaLocal.contains(listaFtp.get(i).toString())) {
                            File arquivo2 = new File(root + File.separator + "Carrefour" + File.separator + "Fracionamento" + File.separator + "Download" + File.separator + listaFtp.get(i).toString());
                            Log.i("FTP", listaFtp.get(i).toString());
                            ftpdld(arquivo2.getAbsolutePath(), listaFtp.get(i).toString());
                            //inserir no banco arquivos baixados
                            insereBancoIdx(arquivo2);
                            //}
                        }
                    }
                    dialog.setProgress(3);

                    handler.post(new Runnable() {
                        public void run() {
                            dialog.setProgress(4);
                            dialog.dismiss();

                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            Log.e("DOWNLOAD", e.getCause().toString());
        }
    }

    private void insereBancoIdx(File arquivo) {

        String selo = "";
        String tipo = "";
        String fabricante = "";
        String sif = "";
        String lote = "";
        String dataFab = "";
        String dataVal = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(arquivo));
            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("-");
                if (parts[0] != null) {
                    selo = parts[0];
                } else {
                    selo = "";
                }

                if (parts[1] != null) {
                    tipo = parts[1];
                } else {
                    tipo = "";
                }

                if (parts.length >= 3) {

                    if (parts[2] != null) {
                        fabricante = parts[2];
                    } else {
                        fabricante = "";
                    }

                    if (parts[3] != null) {
                        sif = parts[3];
                    } else {
                        sif = "";
                    }

                    if (parts[4] != null) {
                        lote = parts[4];
                    } else {
                        lote = "";
                    }

                    if (parts[5] != null) {
                        dataFab = parts[5];
                    } else {
                        dataFab = "";
                    }

                    if (parts[6] != null) {
                        dataVal = parts[6];
                    } else {
                        dataVal = "";
                    }
                }

                repositorio.inserirIdxBaixado(selo, tipo, fabricante, sif, lote, dataFab, dataVal);
            }
            reader.close();

        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", arquivo.getName());
            e.printStackTrace();
        }

    }

    private ArrayList<String> listaArquivosFtp() {

//		boolean status = false;  
        FTPClient ftp = new FTPClient();
        ArrayList<String> listaFtp = new ArrayList<String>();

        FtpConnectionHelper ftpConnectionHelper = new FtpConnectionHelper();

        try {
            ftp.connect(ftpConnectionHelper.getServidorFtp(), ftpConnectionHelper.getPortaFtp());
            ftp.login(ftpConnectionHelper.getUsuarioFtp(), ftpConnectionHelper.getSenhaFtp());
            ftp.enterLocalPassiveMode();

            ftp.changeWorkingDirectory("STMarket");
            ftp.changeWorkingDirectory("idx");

            FTPFile[] files = ftp.listFiles();
            for (FTPFile ftpFile : files) {
                String name = ftpFile.getName();
                //Log.i("DATA", name.substring(name.length()-8, name.length()-4));
                if (name.substring(0, 5).equals(settings.getString("NUMCLIENTE", "") + settings.getString("NUMLOJA", ""))) {
                    if ((Integer.parseInt(diaAtual()) - Integer.parseInt(name.substring(name.length() - 8, name.length() - 4))) < 5) {
                        listaFtp.add(name);
                        Log.i("FTP", name);
                    }

                }
            }

            Log.i("FTP", listaFtp.size() + "");

            ftp.logout();
            ftp.disconnect();

            return listaFtp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        listaFtp.clear();
        return listaFtp;

    }

    public boolean ftpdld(String desFilePath, String nomeArquivo) {
        boolean status = false;
        FTPClient ftp = new FTPClient();
        FtpConnectionHelper ftpConnectionHelper = new FtpConnectionHelper();

        try {
            ftp.connect(ftpConnectionHelper.getServidorFtp(), ftpConnectionHelper.getPortaFtp());
            status = ftp.login(ftpConnectionHelper.getUsuarioFtp(), ftpConnectionHelper.getSenhaFtp());

            ftp.changeWorkingDirectory("STMarket");
            ftp.changeWorkingDirectory("idx");

            if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                // verifica se o arquivo ja existe localmente
                FileOutputStream desFileStream = new FileOutputStream(desFilePath);
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalPassiveMode();
                status = ftp.retrieveFile(nomeArquivo, desFileStream);
                desFileStream.close();
            }

            ftp.logout();
            ftp.disconnect();
            return status;
        } catch (Exception e) {
            Log.e("Log", "download falhou: " + e.getMessage());
        }

        return status;
    }

    //-----------------------------------------------------------------------


//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//		case IntentIntegrator.REQUEST_CODE:
//			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
//					resultCode, data);
//			if (scanResult == null) {
//				return;
//			}
//			final String result = scanResult.getContents();
//			if (result != null) {
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						edtSelo.setText(result);
//
//					}
//				});
//			}
//			break;
//		default:
//		}
//	}

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Saindo Do Fracionamento")
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

    private void OpenConfig() {

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(input);
        builder.setTitle("Configurações");
        builder.setMessage("Digite a senha para configurar");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (input.getText().toString().equals("safeadm")) {

                    Intent it = null;
                    it = new Intent(getApplicationContext(), varGlobais.class);
                    startActivity(it);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {
                    Toast.makeText(getApplicationContext(), "Senha incorreta", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

}
