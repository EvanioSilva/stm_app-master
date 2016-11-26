package com.rastreabilidadeInterna.preparacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
import com.rastreabilidadeInterna.geral.objetoReceita;
import com.rastreabilidadeInterna.geral.varGlobais;
import com.rastreabilidadeInterna.helpers.HistoricoXMLController;
import com.rastreabilidadeInterna.helpers.LogGenerator;

import static java.lang.System.in;


public class preparacaoPrincipal extends Activity {

    private Spinner spConfeitaria;
    private Spinner spPadaria;
    private Spinner spPratosprontos;
    private LinearLayout linearPrep;
    private Button btnEnviar;
    private TextView txCod;
    private String usuarioCpf;
    private Button btnStatus;
    private Button btnPrepDia;
    private boolean isHistorico = false;
    //private String spinnerOriginal;
    //private String spinnerSecundario;
    private Context contexto = this;
    ArrayList<String> receitas = new ArrayList<String>();
    ArrayList<String> receitas2 = new ArrayList<String>();
    ArrayList<String> receitas3 = new ArrayList<String>();
    ArrayList<String> listaFtp = new ArrayList<String>();


    private int id = 1;
    objetoReceita receitaSelecionada;

    Repositorio repositorio;
    objetoPreparacao objeto;

    private ArrayList<String> ingredientes;
    private List<EditText> editTextList = new ArrayList<EditText>();
    private ArrayList<String> listaIngredientes = new ArrayList<String>();
    private ArrayList<String> listaProcessos = new ArrayList<String>();

    File path = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao");

    private Handler handler = new Handler();
    private Boolean enviou;
    private ProgressDialog dialog;
    private String msgErro;
    private final static String SERVIDOR = "52.204.225.11";
    private final static String NOME = "safetrace";
    private final static String SENHA = "9VtivgcVTy0PI";
    private final static String CONFEITARIA = "confeitaria";
    private final static String PADARIA = "padaria";
    private final static String PRATOSPRONTOS = "pratosprontos";

    private int mYear;
    private int mMonth;
    private int mDay;

    private EditText edtAux;
    private EditText edtAux2;

    private EditText edtAuxFab;
    private EditText edtAuxVal;
    private EditText edtAuxLote;

    //private EditText edtAtual;
    //private EditText edtProx;
    private String dateHistorico;

    final Calendar c = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparacao_principal);

        objeto = new objetoPreparacao(this);
        repositorio = new Repositorio(this);
        objetoReceita receitaSelecionada = new objetoReceita();
        usuarioCpf = getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")";
        defineComponent();
        defineAction();
        defineCaminho();
        loadScreen();
        enviarArquivos();
        calendarioDataAtual();
        setStatus();

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        loadScreen();
        // enviarArquivos();
        setStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

            if (menuItem.getItemId() == R.id.action_Logout){
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

        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                OpenConfig();
                return true;
            case R.id.action_historico:
                isHistorico = true;
                showDialog(3);
                return true;
            case R.id.action_receitasdodia:
                SimpleDateFormat dateNome = new SimpleDateFormat("dd/MM/yyyy");
                String dataHoje = dateNome.format(new Date(System.currentTimeMillis()));
                Log.i("receita", dataHoje);
                ArrayList<String> receitas = repositorio.listarReceitaHoje(dataHoje);
                mostraLidosHoje(dataHoje, receitas.size(), receitas);
                return true;
            case R.id.action_atualizarreceitas:
                //DownloadFTP download = new DownloadgetApplicationContext()exto);
                Download();
                repositorio.deletaReceitas();
                //download.Download();
                return true;
            case R.id.action_relatorio:
                iniciarRelatorio();
                return true;
            case R.id.action_producao_diaria:
                Intent intent = new Intent(preparacaoPrincipal.this, ActivityProducaoDoDia.class);
                intent.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                intent.putExtra("cpf", getIntent().getExtras().getString("cpf"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void logout(){
        Intent i = new Intent(this, ActivityTelaInicial.class);
        startActivity(i);
        finish();
    }

    private void iniciarRelatorio() {
        Intent i = new Intent(this, ActivityRelatorioPreparacao.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preparacao_principal, menu);
        menu.getItem(0).setTitle("Usuário: " + getIntent().getExtras().getString("Nome"));
        return true;
    }

    private String dataAdd() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.add(Calendar.DATE, 3);
        String output = sdf.format(c.getTime());
        Log.i("DATA", output);

        return output;
    }

    private void setStatus() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates ui
                if (path.list().length > 0) {
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

    private void defineComponent() {
        spConfeitaria = (Spinner) findViewById(R.id.spinnerIngrediente);
        spPadaria = (Spinner) findViewById(R.id.spinnerPadaria);
        spPratosprontos = (Spinner) findViewById(R.id.spinnerPizzaria);
        linearPrep = (LinearLayout) findViewById(R.id.layoutPrep);
        btnEnviar = (Button) findViewById(R.id.btnEnviarPrep);
        txCod = (TextView) findViewById(R.id.txCodBal);
        btnStatus = (Button) findViewById(R.id.prep_btnStatus);
    }

    private void defineAction() {

        spConfeitaria.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (!spConfeitaria.getSelectedItem().toString().equals("")) {
                    spPadaria.setSelection(0, false);
                    spPratosprontos.setSelection(0, false);
                    String mselection = spConfeitaria.getSelectedItem().toString();
                    receitaSelecionada = repositorio.buscaReceita(mselection);
                    ingredientes = repositorio.listarIngredientes(mselection);
                    Log.i("acao", mselection);

                    linearPrep.removeAllViews();


                    editTextList.clear();
                    id = 1;
                    txCod.setText("");
                    listaIngredientes.clear();
                    listaProcessos.clear();
                    if (receitaSelecionada.intermediaria != null) {
                        btnEnviar.setText("Ler Etiqueta");
                    }
                    //cria layout genérico

                    MostrarNomeReceita(spConfeitaria.getSelectedItem().toString());
                    for (int i = 0; i < ingredientes.size(); i++) {
                        linearPrep.addView(linearLinha(i));
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spPadaria.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (!spPadaria.getSelectedItem().toString().equals("")) {
                    spConfeitaria.setSelection(0, false);
                    spPratosprontos.setSelection(0, false);
                    String mselection = spPadaria.getSelectedItem().toString();
                    receitaSelecionada = repositorio.buscaReceita(mselection);
                    ingredientes = repositorio.listarIngredientes(mselection);
                    Log.i("acao", mselection);

                    linearPrep.removeAllViews();
                    editTextList.clear();
                    id = 1;
                    txCod.setText("");
                    listaIngredientes.clear();
                    listaProcessos.clear();
                    if (receitaSelecionada.intermediaria != null) {
                        if (receitaSelecionada.intermediaria.equals("I")) {
                            btnEnviar.setText("Ler Etiqueta");
                        } else {
                            btnEnviar.setText("Ler etiqueta");
                        }
                    }


                    //Cria layout
                    MostrarNomeReceita(spPadaria.getSelectedItem().toString());

                    for (int i = 0; i < ingredientes.size(); i++) {
                        linearPrep.addView(linearLinha(i));
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spPratosprontos.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (!spPratosprontos.getSelectedItem().toString().equals("")) {
                    spPadaria.setSelection(0, false);
                    spConfeitaria.setSelection(0, false);
                    String mselection = spPratosprontos.getSelectedItem().toString();
                    receitaSelecionada = repositorio.buscaReceita(mselection);
                    ingredientes = repositorio.listarIngredientes(mselection);
                    Log.i("acao", mselection);

                    linearPrep.removeAllViews();
                    editTextList.clear();
                    id = 1;
                    txCod.setText("");
                    listaIngredientes.clear();
                    listaProcessos.clear();
                    if (receitaSelecionada.intermediaria != null) {
                        if (receitaSelecionada.intermediaria.equals("I")) {
                            btnEnviar.setText("Ler Etiqueta");
                        } else {
                            btnEnviar.setText("Ler etiqueta");
                        }
                    }
                    //cria layout genérico

                    MostrarNomeReceita(spPratosprontos.getSelectedItem().toString());
                    for (int i = 0; i < ingredientes.size(); i++) {
                        linearPrep.addView(linearLinha(i));
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        btnEnviar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                ActionEnviar();
            }

        });

        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                enviarArquivos();
                setStatus();
            }
        });

    }

    public void MostrarNomeReceita(String nomeReceita) {
        TextView txtNomeReceita = new TextView(contexto);
        txtNomeReceita.setTextSize(40);
        txtNomeReceita.setTextColor(Color.BLACK);

        txtNomeReceita.setGravity(Gravity.CENTER);
        txtNomeReceita.setText(nomeReceita);

        linearPrep.addView(txtNomeReceita);
    }

    public void ActionEnviar() {

        final ArrayList<String> ingredientesInter = checaIngredientedaReceita(receitaSelecionada.receita);


        if (camposVazios() && editTextList.size() != 0) {
            if (receitaSelecionada.intermediaria.equals("I")) {
                //Se receita intermediária então só cria o arquivo A01 e B
                dialogoIntermediario();
            } else {
                //Se receita final e possui um ou mais ingredientes intermediario então cria o arquivo A02 e B

                //final EditText input = new EditText(this);
                //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                //input.setLayoutParams(lp);
                //input.setInputType(InputType.TYPE_CLASS_NUMBER);

                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //builder.setView(input);
                //builder.setTitle("Receita intermediária");
                //builder.setMessage("Código da etiqueta:");
                //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                //            public void onClick(DialogInterface arg0, int arg1) {
                //                if (input.getText().toString().equals("")) {
                //                    Toast.makeText(getApplicationContext(), "Insira o código da etiqueta", Toast.LENGTH_LONG).show();
                //                } else {

                SharedPreferences preferences = getSharedPreferences("Preferences", 0);

                String codigo = preferences.getString("NUMCLIENTE", "") +
                        preferences.getString("NUMLOJA", "") +
                        gerarCodigo();

                SimpleDateFormat dateNome = new SimpleDateFormat("yyyyMMdd");
                String dataAtual = dateNome.format(new Date(System.currentTimeMillis()));

                dateNome = new SimpleDateFormat("HHmmss");
                String hora_arquivo = dateNome.format(new Date(System.currentTimeMillis()));

                SharedPreferences settings = getSharedPreferences("Preferences", 0);
                SharedPreferences.Editor editor = settings.edit();

                String numCliente = settings.getString("NUMCLIENTE", "");
                String numTablet = settings.getString("NUMTABLET", "");
                String numLoja = settings.getString("NUMLOJA", "");
                String idLoja = settings.getString("IDLOJA", "");


                ArrayList<String> codigosIngInter = new ArrayList<String>();

                String aux = "";
                for (int i = 0; i < listaProcessos.size(); i = i + 4) {
                    aux = listaProcessos.get(i);
                    if (repositorio.buscarObjetoIntermediario(aux) != null) {
                        codigosIngInter.add(aux);
                    } else if (aux.length() >= 5 && aux != null) {
                        if (aux.substring(0, 5).equals(settings.getString("NUMCLIENTE", "") + settings.getString("NUMLOJA", ""))) {
                            codigosIngInter.add(aux);
                        }
                    }
                    Log.i("Teste 1", aux);
                    Log.i("Teste 2", settings.getString("NUMCLIENTE", ""));
                    Log.i("Teste 3", settings.getString("NUMLOJA", ""));
                }

                Log.i("testey", "TAM = " + codigosIngInter.size());
                //TODO fazendo

                /*******************************/
                            /*String*/
                aux = "";
                int j = 0;

                ArrayList<ObjetoHistoricoPreparacao.Ingrediente> ingredientes1 = new ArrayList<ObjetoHistoricoPreparacao.Ingrediente>();
                ObjetoHistoricoPreparacao objetoHistoricoPreparacao = new ObjetoHistoricoPreparacao();

                for (int i = 0; i < listaProcessos.size(); i = i + 4) {
                    Log.i("testey", "lista Proc pos: " + i);
                    aux = aux + "<br>" + "** " + listaIngredientes.get(j) + " (" + listaProcessos.get(i) + ") ; Data fabricação: " + listaProcessos.get(i + 1)
                            + " ; Data validade: " + listaProcessos.get(i + 2) + " ; Lote: " + listaProcessos.get(i + 3);

                    ObjetoHistoricoPreparacao.Ingrediente ingrediente = objetoHistoricoPreparacao.getIngredienteInstance();
                    ingrediente.dataValIngrediente = listaProcessos.get(i + 2);
                    ingrediente.codigoIngrediente = listaProcessos.get(i);
                    ingrediente.dataFabIngrediente = listaProcessos.get(i + 1);
                    ingrediente.loteIngrediente = listaProcessos.get(i + 3);
                    ingrediente.nomeIngrediente = listaIngredientes.get(j);

                    ingredientes1.add(ingrediente);

                    j++;
                }

                /*******************************/


                objeto.areaDeUso = getSharedPreferences("Preferences", 0).getString("AREADEUSO", "");
                objeto.saveFileA("OK_A_02_Preparacao_" + dataAtual + "_" + hora_arquivo + "_" + numCliente + numLoja + numTablet, codigo, receitaSelecionada.receita, listaIngredientes, listaProcessos, usuarioCpf, idLoja, numCliente);

                //Salvar vários ingredientes Intermediarios no Arquivo B.
                if (codigosIngInter.size() != 0) {
                    objeto.saveFileB("OK_B_01_Preparacao_" + dataAtual + "_" + hora_arquivo + hora_arquivo + "_" + numCliente + numLoja + numTablet, codigosIngInter, codigo);
                }

                dateNome = new SimpleDateFormat("dd/MM/yyyy");
                String dataHoje = dateNome.format(new Date(System.currentTimeMillis()));

                objetoHistoricoPreparacao.codigoReceita = codigo;
                objetoHistoricoPreparacao.nomeReceita = receitaSelecionada.receita;
                objetoHistoricoPreparacao.ingredientesReceita = ingredientes1;

                HistoricoXMLController historicoXMLController = new HistoricoXMLController(
                        numCliente,
                        numLoja,
                        numTablet,
                        new java.util.Date(),
                        HistoricoXMLController.TYPE_PR
                );

                historicoXMLController.adicionarObjetoPreparacao(objetoHistoricoPreparacao);
                Log.i("historico", objetoHistoricoPreparacao.toString());

                repositorio.inserirReceitaHoje(receitaSelecionada.receita, codigo, dataHoje, "", "");

                listaProcessos.clear();
                setStatus();
                limparProcess();

                //	}
                //}
                //***************************************************

                //repositorio.inserirReceitaHoje(receitaSelecionada.receita, codigo, dataHoje, "", "");
                //                }
                //            }
                //        }
                //);

                //builder.setNegativeButton("Cancelar",
                //        new DialogInterface.OnClickListener() {
                //            public void onClick(DialogInterface arg0, int arg1) {
                //            }
                //        });
                //AlertDialog alerta = builder.create();
                //alerta.show();

            }
        }
    }

    private void loadScreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                receitas.clear();
                receitas.add("");
                receitas.addAll(repositorio.listarReceitas("confeitaria"));
                ArrayAdapter<String> dataAdapterConfeitaria = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, receitas);
                //dataAdapterConfeitaria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spConfeitaria.setAdapter(dataAdapterConfeitaria);

                receitas2.clear();
                receitas2.add("");
                receitas2.addAll(repositorio.listarReceitas("padaria"));
                ArrayAdapter<String> dataAdapterPadaria = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, receitas2);
                //dataAdapterPadaria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPadaria.setAdapter(dataAdapterPadaria);

                receitas3.clear();
                receitas3.add("");
                receitas3.addAll(repositorio.listarReceitas("pratosprontos"));
                ArrayAdapter<String> dataAdapterPratosprontos = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, receitas3);
                //dataAdapterPratosprontos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPratosprontos.setAdapter(dataAdapterPratosprontos);

            }
        });


    }

    public boolean checaIngrediente(String ingrediente) {

        ArrayList<String> receitas = repositorio.listarReceitasStrings();

        if (receitas.contains(ingrediente)) {
            return true;
        } else {
            return false;
        }

    }

    public ArrayList<String> checaIngredientedaReceita(String receita) {

        //receitas lista todas as receitas
        ArrayList<String> receitas = repositorio.listarReceitasStrings();


        ArrayList<String> ingredientesdareceita = repositorio.listarIngredientes(receita);


        ArrayList<String> ingredientesInterReceita = new ArrayList<String>();

        for (int i = 0; i < ingredientesdareceita.size(); i++) {
            if (receitas.contains(ingredientesdareceita.get(i))) {
                ingredientesInterReceita.add(ingredientesdareceita.get(i));
            }
        }

        return ingredientesInterReceita;
    }

    private void limparProcess() {
        for (EditText edt : editTextList) {
            edt.setText("");
            ;
        }
    }

    private String ajustaZeros(int serial, int digitos) {
        String serialString = String.valueOf(serial);
        String zeroes = "";
        for (int i = 0; i < digitos - serialString.length(); i++) {
            zeroes = zeroes + "0";
        }

        return zeroes + serialString;
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

    private Boolean camposVazios() {

        boolean flag = true;

        for (EditText edt : editTextList) {

            listaProcessos.add(edt.getText().toString());
        }

        return true;
    }

    private void calendarioDataAtual() {
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    private void updateDisplay() {
        edtAux.setText(
                new StringBuilder()

                        // Month is 0 based so add 1
                        .append(mDay).append("/")
                        .append(mMonth + 1).append("/")
                        .append(mYear).append(" "));
        calendarioDataAtual();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            String dia = "";
            String mes = "";

            //updateDisplay();
            if (isHistorico) {
                if (mDay < 10) {
                    dia += "0";
                }
                dia += mDay;

                if (mMonth < 9) {
                    mes += "0";
                }
                mes += Integer.toString(mMonth + 1);

                dateHistorico = dia + "/" + mes + "/" + year;

                ArrayList<String> receitas = repositorio.listarReceitaHoje(dateHistorico);
                mostraLidosHoje(dateHistorico, receitas.size(), receitas);
            } else {
                updateDisplay();
                view.updateDate(mYear, mMonth, mDay);
            }
            isHistorico = false;
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        switch (id) {
            case 1:
                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Data de Fabricação", dpd);
                isHistorico = false;
                return dpd;

            case 2:
                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Data de Validade", dpd);
                isHistorico = false;
                return dpd;
            case 3:
                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Data do Historico", dpd);

                return dpd;
        }
        return null;
    }


    // CRIAÇÃO DE TELA GENÉRICA ----------------------------------------------------------------

    private LinearLayout linearLinha(int pos) {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        layoutParams.setMargins(0, 0, 0, 0);

        ll.addView(txView(ingredientes.get(pos)));
        if (checaIngrediente(ingredientes.get(pos))) {

			/*
            ll.addView(editTextGenerico(ll, "Código", true, false, true, true, ""));
			ll.addView(editTextGenerico(ll, "Data Fab", false, false, false, false, "fab"));
			ll.addView(editTextGenerico(ll, "Data Val", true, true, false, false, "val"));
			ll.addView(editTextGenerico(ll, "Lote", true, false, false, false, "lote"));
			*/
            LinearLayoutIntermediario(ll);

        } else {
            LinearLayoutGenerico(ll);
        }

        return ll;
    }

    private TextView txView(String texto) {
        TextView tx = new TextView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        layoutParams.weight = 1;
        layoutParams.width = 0;
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        tx.setTextSize(20);
        tx.setLayoutParams(layoutParams);

        tx.setText("\n" + texto + "\n");
        tx.setTextColor(getResources().getColor(R.color.black));

        listaIngredientes.add(texto);

        return tx;
    }

    private void LinearLayoutIntermediario(LinearLayout lin) {

        //final TextView txNome = (TextView)lin.getChildAt(0);
        final EditText edtCodigo = new EditText(this);
        final EditText edtFab = new EditText(this);
        final EditText edtVal = new EditText(this);
        final EditText edtLote = new EditText(this);

        //Crinado o Edit de Código
        edtCodigo.setId(-id);
        id++;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        layoutParams.width = 0;
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        edtCodigo.setLayoutParams(layoutParams);

        edtCodigo.setHint("Código");
        edtCodigo.setSingleLine();
        edtCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtCodigo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                //modificado: trocou Editaux2 por editText
                if (edtCodigo.getText().toString().length() == 11) {

                    //modificado: trocou Editaux2 por editText
                    ObjetoIntermediario obj = repositorio.buscarObjetoIntermediario(edtCodigo.getText().toString());
                    if (obj != null) {
                        edtFab.setText(obj.dataFab);
                        edtVal.setText(obj.dataVal);
                        edtLote.setText("1");

                    } else {
                        edtFab.setText("");
                        edtVal.setText("");

                        if (edtCodigo.getText().toString().substring(0, 5).equals(getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") + getSharedPreferences("Preferences", 0).getString("NUMLOJA", ""))) {
                            edtLote.setText("1");
                        } else {
                            edtLote.setText("");
                        }


                    }
                } else {
                    edtFab.setText("");
                    edtVal.setText("");
                    edtLote.setText("");

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

        //Crinado o Edit de Fabricação
        edtFab.setFocusable(false);
        edtFab.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view) {
                                          edtAux = (EditText) view;
                                          showDialog(1);
                                      }
                                  }
        );
        id++;
        edtFab.setLayoutParams(layoutParams);
        edtFab.setHint("Data Fab");
        edtFab.setSingleLine();

        //Crinado o Edit de Validade
        edtVal.setId(-id);
        edtVal.setFocusable(false);
        edtVal.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view) {
                                          edtAux = (EditText) view;
                                          showDialog(2);
                                      }
                                  }
        );
        id++;
        edtVal.setLayoutParams(layoutParams);
        edtVal.setHint("Data Val");
        edtVal.setSingleLine();

        //Crinado o Edit de Lote
        edtLote.setId(-id);
        edtLote.setLayoutParams(layoutParams);
        edtLote.setHint("Lote");
        edtLote.setSingleLine();

        lin.addView(edtCodigo);
        lin.addView(edtFab);
        lin.addView(edtVal);
        lin.addView(edtLote);

        editTextList.add(edtCodigo);
        editTextList.add(edtFab);
        editTextList.add(edtVal);
        editTextList.add(edtLote);
    }

    private void LinearLayoutGenerico(LinearLayout lin) {

        //final TextView txNome = (TextView)lin.getChildAt(0);
        final EditText edtCodigo = new EditText(this);
        final EditText edtFab = new EditText(this);
        final EditText edtVal = new EditText(this);
        final EditText edtLote = new EditText(this);

        //Crinado o Edit de Código
        edtCodigo.setId(-id);
        id++;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        layoutParams.width = 0;
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        edtCodigo.setLayoutParams(layoutParams);

        edtCodigo.setHint("Código");
        edtCodigo.setSingleLine();
        edtCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtCodigo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (edtCodigo.getText().toString().length() == 11) {
                    if (edtCodigo.getText().toString().substring(0, 5).equals(getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") + getSharedPreferences("Preferences", 0).getString("NUMLOJA", ""))) {
                        edtLote.setText("1");
                    } else {
                        edtLote.setText("");
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

        //Crinado o Edit de Fabricação
        edtFab.setFocusable(false);
        edtFab.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view) {
                                          edtAux = (EditText) view;
                                          showDialog(1);
                                      }
                                  }
        );
        id++;
        edtFab.setLayoutParams(layoutParams);
        edtFab.setHint("Data Fab");
        edtFab.setSingleLine();

        //Crinado o Edit de Validade
        edtVal.setId(-id);
        edtVal.setFocusable(false);
        edtVal.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view) {
                                          edtAux = (EditText) view;
                                          showDialog(2);
                                      }
                                  }
        );
        id++;
        edtVal.setLayoutParams(layoutParams);
        edtVal.setHint("Data Val");
        edtVal.setSingleLine();

        //Crinado o Edit de Lote
        edtLote.setId(-id);
        edtLote.setLayoutParams(layoutParams);
        edtLote.setHint("Lote");
        edtLote.setSingleLine();

        lin.addView(edtCodigo);
        lin.addView(edtFab);
        lin.addView(edtVal);
        lin.addView(edtLote);

        editTextList.add(edtCodigo);
        editTextList.add(edtFab);
        editTextList.add(edtVal);
        editTextList.add(edtLote);
    }

    private EditText editTextGenerico(final LinearLayout lin, String hint, boolean fabricacao, boolean validade, boolean tipoNumerico, boolean codigoIngIntermediario, String nomeCampo) {

        final EditText ed = new EditText(this);

        if (fabricacao) {
        } else {
            ed.setFocusable(false);
            ed.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view) {
                                          edtAux = (EditText) view;
                                          showDialog(1);
                                      }
                                  }
            );
        }
        id++;
        ed.setWidth(150);
        ed.setHint(hint);
        ed.setSingleLine();

        if (validade) {
            ed.setFocusable(false);
            ed.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view) {
                                          edtAux = (EditText) view;
                                          showDialog(2);
                                      }
                                  }
            );
        }

        if (tipoNumerico) {
            ed.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if (codigoIngIntermediario) {
            //PreenchimentoAutomatico de outros campos - Ingrediente Intermediario
            //Codigo de ing intermediario

            edtAux2 = ed;


            //edtAux2.addTextChangedListener(new TextWatcher() {
            ed.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                    //modificado: trocou Editaux2 por editText
                    if (ed.getText().toString().length() == 11) {
                        //modificado: trocou Editaux2 por editText
                        ObjetoIntermediario obj = repositorio.buscarObjetoIntermediario(ed.getText().toString());
                        if (obj != null) {
                            edtAuxFab.setText(obj.dataFab);
                            edtAuxVal.setText(obj.dataVal);
                            edtAuxLote.setText("1");
                        } else {
                            edtAuxFab.setText("");
                            edtAuxVal.setText("");
                            edtAuxLote.setText("");
                        }
                    } else {
                        edtAuxFab.setText("");
                        edtAuxVal.setText("");
                        edtAuxLote.setText("");
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

            });

        }

        if (nomeCampo.equals("fab")) {
            edtAuxFab = ed;
        }

        if (nomeCampo.equals("val")) {
            edtAuxVal = ed;

        }
        if (nomeCampo.equals("lote")) {
            edtAuxLote = ed;
            //TODO
        }

        editTextList.add(ed);
        return ed;
    }

    private void dialogoIntermediario() {

        //final EditText input = new EditText(this);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //input.setLayoutParams(lp);
        //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);

        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setView(input);
        //builder.setTitle("Receita intermediária");
        //builder.setMessage("Código da etiqueta:");
        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        //    public void onClick(DialogInterface arg0, int arg1) {
        //      if (input.getText().toString().equals("")) {
        //            Toast.makeText(getApplicationContext(), "Insira o código da etiqueta", Toast.LENGTH_LONG).show();
        //        } else {
        SimpleDateFormat dateNome = new SimpleDateFormat("yyyyMMdd");
        String data_pref = dateNome.format(new Date(System.currentTimeMillis()));

        dateNome = new SimpleDateFormat("HHmmss");
        String hora_arquivo = dateNome.format(new Date(System.currentTimeMillis()));

        dateNome = new SimpleDateFormat("dd/MM/yyyy");
        String dataFab = dateNome.format(new Date(System.currentTimeMillis()));
        ObjetoIntermediario obj = new ObjetoIntermediario();

        SharedPreferences preferences = getSharedPreferences("Preferences", 0);

        String codigo = preferences.getString("NUMCLIENTE", "") +
                preferences.getString("NUMLOJA", "") +
                gerarCodigo();

        obj.codigo = codigo;
        obj.dataFab = dataFab;
        obj.dataVal = dataAdd();
        repositorio.inserirReceitaHoje(receitaSelecionada.receita, obj.codigo, obj.dataFab, obj.dataFab, obj.dataVal);

        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        String idLoja = settings.getString("IDLOJA", "");

        Log.i("pao", obj.codigo + " " + obj.dataFab + " " + obj.dataVal);
        String numCliente = settings.getString("NUMCLIENTE", "");

        ArrayList<String> codigosIngInter = new ArrayList<String>();

        String aux = "";
        for (int i = 0; i < listaProcessos.size(); i = i + 4) {
            aux = listaProcessos.get(i);
            if (repositorio.buscarObjetoIntermediario(aux) != null) {
                codigosIngInter.add(aux);
            } else if (aux.length() >= 5 && aux != null) {
                if (aux.substring(0, 5).equals(settings.getString("NUMCLIENTE", "") + settings.getString("NUMLOJA", ""))) {
                    codigosIngInter.add(aux);
                }
            }
        }

        Log.i("NRO ITEMS", Integer.toString(codigosIngInter.size()));

        objeto.areaDeUso = getSharedPreferences("Preferences", 0).getString("AREADEUSO", "");
        objeto.cliente = numCliente;
        objeto.loja = getSharedPreferences("Preferences", 0).getString("NUMLOJA", "");
        objeto.tablet = getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");

        objeto.saveFileA(
                "OK_A_01_Preparacao_"
                        + data_pref
                        + "_"
                        + hora_arquivo
                        + "_"
                        + numCliente
                        + settings.getString("NUMLOJA", "")
                        + settings.getString("NUMTABLET", ""),
                codigo,
                receitaSelecionada.receita,
                listaIngredientes,
                listaProcessos,
                usuarioCpf,
                idLoja,
                numCliente);
        if (codigosIngInter.size() > 0) {
            Log.i("LET THERE BE LIGHT", "ARQUIVO B SENDO CRIADO");
            objeto.saveFileB(
                    "OK_B_01_Preparacao_" + data_pref + "_" + hora_arquivo + hora_arquivo + "_" + numCliente + settings.getString("NUMLOJA", "") + settings.getString("NUMTABLET", ""),
                    codigosIngInter,
                    codigo);
        }
        //repositorio.inserirReceitaHoje(receitaSelecionada.receita, input.getText().toString(), dataFab, "", "");
        listaProcessos.clear();
        //					enviarArquivos();
        limparProcess();
        setStatus();
        Toast.makeText(getApplicationContext(), "Salvo com sucesso!", Toast.LENGTH_LONG).show();
        //        }
        //    }
        //});
        //builder.setNegativeButton("Cancelar",
        //        new DialogInterface.OnClickListener() {
        //            public void onClick(DialogInterface arg0, int arg1) {
        //            }
        //        });
        //AlertDialog alerta = builder.create();
        //alerta.show();
    }

    private String gerarCodigo() {
        verificarSerialPref();
        verificarDatePref();

        SharedPreferences preferences = getSharedPreferences("Preferences", 0);

        String codigo = preferences.getString("NUMTABLET", "") +
                preferences.getString("DATEPREF", "") +
                Integer.toString(preferences.getInt("SERIALPREF", 0));

        txCod.setText(codigo);

        return codigo;
    }

    private void verificarDatePref() {
        SharedPreferences preferences = getSharedPreferences("Preferences", 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");

        if (!simpleDateFormat.format(new java.util.Date()).equals(preferences.getString("DATEPREF", ""))) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("DATEPREF", simpleDateFormat.format(new java.util.Date()));
            editor.commit();
        }
    }

    private void verificarSerialPref() {
        SharedPreferences preferences = getSharedPreferences("Preferences", 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");

        if (!simpleDateFormat.format(new java.util.Date()).equals(preferences.getString("DATEPREF", ""))) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("SERIALPREF", 0);
            editor.commit();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("SERIALPREF", preferences.getInt("SERIALPREF", -1) + 1);
            editor.commit();
        }
    }


    // DIALOG PARA CONFIGURAÇÕES ------------------------------------------------------------

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

    // CÓDIGOS DE PRODUTOS GERADOS HOJE -------------------------------------------------

    private void mostraLidosHoje(String diaHoje, int tam, ArrayList<String> receitas) {

        CharSequence[] items = new String[tam];

        for (int i = 0; i < receitas.size(); i++) {
            items[i] = receitas.get(i);
            Log.i("receita", items[i] + "");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (isHistorico) {
            builder.setTitle("Receitas do dia " + diaHoje + ":");
        } else {
            builder.setTitle("Receitas de hoje " + diaHoje + ":");
        }
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(), "Selection: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    // UPLOAD ARQUIVO ----------------------------------------------------------------------------------------------------------

    private class AppZip {
        List<String> fileList;
        ProgressDialog progressDialog;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

        private final String OUTPUT_ZIP_FILE =
                Environment.getExternalStorageDirectory() +
                        File.separator + "Carrefour" +
                        File.separator + "Preparacao" +
                        File.separator + "ZIP_PR_" +
                        getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                        getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                        getSharedPreferences("Preferences", 0).getString("NUMTABLET", "") + "_" +
                        simpleDateFormat.format(new java.util.Date()) + ".zip";
        private final String SOURCE_FOLDER = Environment.getExternalStorageDirectory()
                + File.separator + "Carrefour"
                + File.separator + "Preparacao";

        AppZip(ProgressDialog progressDialog) {
            fileList = new ArrayList<String>();
            this.progressDialog = progressDialog;
        }

        public void ziparTudo() {
            Log.i("Zipando", "Tudo");
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

                        progressDialog.incrementProgressBy(1);

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
            } catch (Exception ex) {
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
                Log.i("file", generateZipEntry(node.getAbsoluteFile().toString()));
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

        if (path.list().length > 0) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Enviando Arquivos");
            progressDialog.setMessage("Por favor aguarde");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(path.listFiles().length + 2);
            progressDialog.setProgress(0);
            progressDialog.show();

            AppZip appZip = new AppZip(progressDialog);
            appZip.ziparTudo();

            progressDialog.incrementProgressBy(1);

            new Thread(new Runnable() {
                public void run() {
                    File fList[] = path.listFiles();

                    for (int i = 0; i < (fList.length); i++) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        File arquivo = fList[i];

                        enviou = envioFTP(arquivo.getName());
                        if (!enviou) break;
                        progressDialog.incrementProgressBy(1);
                    }

                    handler.post(new Runnable() {
                        public void run() {
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

    public void msg(final String mensagem) {

        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Download() {
        try {
            dialog = new ProgressDialog(this);
            dialog.setTitle("Baixando Dados");
            dialog.setMessage("Aguarde");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();

            new Thread(new Runnable() {
                public void run() {
                    File root = Environment.getExternalStorageDirectory();
                    File local1 = new File(root + File.separator + "Carrefour");
                    //DeleteRecursive(local1);
                    if (!local1.exists()) {
                        local1.mkdir();
                    }
                    local1 = new File(root + File.separator + "Carrefour" + File.separator + "Receitas");
                    if (!local1.exists()) {
                        local1.mkdir();
                    }

                    // Cria se nao existir e recupera versao do BD

                    listaArquivosFtp();

                    dialog.setMax(listaFtp.size());
                    dialog.setProgress(0);

                    for (int i = 0; i < listaFtp.size(); i++) {
                        File arquivo2 = new File(root + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + listaFtp.get(i));
                        ftpdld(arquivo2.getAbsolutePath(), listaFtp.get(i));
                        //inserir no banco arquivos baixados
                        insereBanco(new File(root + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + listaFtp.get(i)));
                        dialog.incrementProgressBy(1);
                    }
                    loadScreen();
                    handler.post(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            msg("Receitas Atualizadas");

                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            Log.e("DOWNLOAD", e.getCause().toString());
        }

    }

    public boolean ftpdld(String desFilePath, String nomeArquivo) {
        boolean status = false;
        FTPClient ftp = new FTPClient();

        try {
            ftp.connect(SERVIDOR, 21);
            status = ftp.login(NOME, SENHA);

            ftp.changeWorkingDirectory("STMarket");

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

    private void listaArquivosFtp() {

        listaFtp.clear();
        FTPClient ftp = new FTPClient();

        try {
            ftp.connect(SERVIDOR, 21);
            ftp.login(NOME, SENHA);
            ftp.enterLocalPassiveMode();

            ftp.changeWorkingDirectory("STMarket");

            String[] names = ftp.listNames();
            for (String name : names) {
                listaFtp.add(name);
                Log.i("FTP", name);
            }

            Log.i("FTP", listaFtp.size() + "");

            ftp.logout();
            ftp.disconnect();

        } catch (Exception e) {
            Log.e("Log", "download falhou: " + e.getMessage());
        }
    }

    private void insereBanco(File arquivo) {


        String receita;
        String ingrediente;

        if (arquivo.getName().contains("receitas")) {
            objetoReceita obj = new objetoReceita();
            if (arquivo.getName().contains(CONFEITARIA)) {
                obj.local = CONFEITARIA;
            }
            if (arquivo.getName().contains(PADARIA)) {
                obj.local = PADARIA;
            }
            if (arquivo.getName().contains(PRATOSPRONTOS)) {
                obj.local = PRATOSPRONTOS;
            }

            // arquivo receita

            try {

                BufferedReader reader = new BufferedReader(new FileReader(arquivo));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.substring(0, 1).equals("*")) {

                        receita = (String) line.subSequence(1, line.length());
                        repositorio.inserirReceita(receita);
                        obj.receita = receita;

                    } else {

                        if (line.equals("F") || line.equals("I")) {

                            obj.intermediaria = line;

                        } else {

                            obj.ingrediente = line;
                            repositorio.inserirNovaReceita(obj);
                            Log.i("nome", obj.receita + "   " + obj.ingrediente);

                        }

                    }
                }
                reader.close();
            } catch (Exception e) {
                System.err.format("Exception occurred trying to read '%s'.", arquivo.getName());
                e.printStackTrace();
            }
        }/*
        else{
			// arquivo ingrediente

			try{
				BufferedReader reader = new BufferedReader(new FileReader(arquivo));
				String line;
				objetoIngrediente obj = new objetoIngrediente();

				while ((line = reader.readLine()) != null){

					obj.nomeIngrediente = line.substring(0, line.indexOf("*"));
					String aux = line.substring(line.indexOf("*")+1, line.length());
					obj.codigo = aux.substring(0, aux.indexOf("*"));
					line = aux.substring(aux.indexOf("*")+1, aux.length());
					obj.peso = line.substring(0, line.indexOf("*"));
					aux = line.substring(line.indexOf("*")+1, line.length());
					obj.diasVal = aux;

					//					repositorio.inseriring
					Log.i("BANCO",obj.nomeIngrediente + " - " + obj.codigo
							+ " - " + obj.peso + " - " + obj.diasVal);

					repositorio.inserirIngrediente(obj);

				}

				reader.close();
			}

			catch (Exception e){
				System.err.format("Exception occurred trying to read '%s'.", arquivo.getName());
				e.printStackTrace();
			}
		}*/

        arquivo.delete();

    }

    void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();
    }

    private Boolean envioFTP(String nomeArquivo) {
        FTPClient ftp = new FTPClient();
        Boolean retorno = false;
        msgErro = "Falha de conexão";

        try {

            ftp.connect(SERVIDOR, 21);
            ftp.login(NOME, SENHA);

            //			ftp.changeWorkingDirectory("Teste");

            if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

                File file;

                file = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao" + File.separator + nomeArquivo);
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

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Saindo De Preparação")
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
