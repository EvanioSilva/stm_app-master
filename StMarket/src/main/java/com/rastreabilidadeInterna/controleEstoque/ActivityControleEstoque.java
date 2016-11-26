package com.rastreabilidadeInterna.controleEstoque;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
import org.apache.commons.net.ftp.FTPReply;

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
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.geral.varGlobais;
import com.rastreabilidadeInterna.helpers.HistoricoXMLController;
import com.rastreabilidadeInterna.helpers.LogGenerator;

import static java.lang.System.*;

public class ActivityControleEstoque extends Activity{

	private Repositorio repositorio;
    private LogGenerator log;

    //  Declaracao de todos elementos visuais utilizados nessa tela

    //  Buttons
    private ImageButton imgCamera;          //  Button que chama o processo de leitura de barcode por camera para o barcode do produto
	private ImageButton imgCamera_Caixa;    //  Button que chama o processo de leitura de barcode por camera para o barcode da caixa

    private Button btnLimpar;               //  Button que chama a funcao que limpa os campos (ou retorna para valores default)
    private Button btnStatus;               //  Button que mostra se as mudancas feitas localmente ja foram sincronizadas com o server, tambem utilizado para executar dita sincronizacao

    //  EditTexts
    private EditText edtOrigem;             //  EditText que recolhe o codigo de barras do produto (menor codigo)
	private EditText edtOrigem_Caixa;       //  EditText que recolhe o codigo de barras da caixa (maior codigo)
	private EditText edtFabricante;         //  EditText que recolhe o nome do fabricante (preenchido automaticamente quando possivel)
	private EditText edtSif;                //  EditText que recolhe o codigo SIF do produto (preenchido automaticamente quando possivel)
	private EditText edtDataFab;            //  EditText que contem a data de fabricacao do produto (preenchido automaticamente quando possivel)
	private EditText edtDataVal;            //  EditText que contem a data de validade do produto (preenchido automaticamente quando possivel)
	private EditText edtQtd;                //  EditText que contem o numero de pecas por caixa (preenchido automaticamente quando possivel)
    private EditText edtQtdCaixas;          //  EditText que contem o numero total de caixas de um mesmo produto
	private EditText edtTipo;               //  EditText que contem o nome do produto (preenchido automaticamente quando possivel)
	private EditText edtLote;               //  EditText que contem o codigo do lote do produto (preenchido automaticamente quando possivel)
    private EditText edtPeso;               //  EditText que contem o peso da caixa (não obrigatório)

    //  TextViews
    private TextView tvQtd;                 //  TextView usado como label para edtQtd (costumava ser atualizado durante a execucao em versoes anteriores)

    ResultadoDeClassificacao resultadosClassificacao = new ResultadoDeClassificacao();

    private String usuarioCpf;
	private boolean isUnico;
	private RadioButton rdbtCaixas;
	private RadioButton rdbtPecas;
	private RadioGroup rdgrpUnico;
	private EditText edtCodSafe;
	//private ImageButton imgCamera2;
	private Button btnAdd;

    private Button buttonAssociarEtiquetas;

    private String codigoInterno;

	private ListView lvList;

    private TextView tvPecasComSelo;
    private TextView tvNroDePecas;

	public static final String PREFS_NAME = "Preferences";

	private Button btnSalvar;

	private Handler  handler = new Handler();
    private TextWatcher textWatcher;

	int flag = 1;

	final ArrayList<String> list = new ArrayList<String>();

    private Boolean enviou;
	private ProgressDialog dialog;
	private String msgErro;
	private final static String SERVIDOR = "52.204.225.11";
	private final static String NOME = "safetrace";
	private final static String SENHA = "9VtivgcVTy0PI";

	File path = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque");
	File pathIdx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx");

    File pathCodes = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + "produtos_ce.txt");
    File pathCodesFr = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "codigos_conv_frac.txt");

	//	static final int DATE_DIALOG_ID = 0;
	private int mYear;
	private int mMonth;
	private int mDay;

	final Calendar c = Calendar.getInstance();

	private int dataFlag = 0;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controleestoque_principal);

        log = new LogGenerator(this);
        log.append("==========================================");
        log.append("Iniciando Controle De Estoque");
        log.append("==========================================");

		repositorio = new Repositorio(this);
		defineCaminho();
		defineComponent();
		defineAction();

		loadScreen();
		enviarArquivos();

        baixarArquivoDeCodigos();

        calendarioDataAtual();
		setStatus();

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
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
            case R.id.action_historico_controle_estoque:
                startHistorico();
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
    private void startHistorico(){
        Intent i = new Intent(getBaseContext(), ActivityHistoricoControleDeEstoque.class);
        i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
        i.putExtra("cpf", getIntent().getExtras().getString("cpf"));
        startActivity(i);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("Nome", getIntent().getExtras().getString("Nome"));
        savedInstanceState.putString("cpf", getIntent().getExtras().getString("cpf"));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        getIntent().putExtra("Nome", savedInstanceState.getString("Nome"));
        getIntent().putExtra("cpf", savedInstanceState.getString("cpf"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_controleestoque_principal, menu);

        if(getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("Nome") != null) {
                menu.getItem(0).setTitle("Usuário: " + getIntent().getExtras().getString("Nome"));
            }
        }

        return true;
    }

    @Override
	public void onResume(){
		super.onResume();
		super.onResume();
        setStatus();
        limpar();
		// put your code here...
		//loadScreen();
	}

	private void setStatus(){

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				//stuff that updates ui
				if (path.list().length > 1) {
					btnStatus.setBackgroundResource(R.drawable.background_red);
				}
				else{
					btnStatus.setBackgroundResource(R.drawable.background_green);
				}
			}
		});
	}

	private void defineCaminho(){
		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
		if (!local.exists()) {
			local.mkdir();
		}

		if (!path.exists()) {
			path.mkdir();
		}

		if (!pathIdx.exists()) {
			pathIdx.mkdir();
		}
	}

	private void defineComponent(){
		imgCamera = (ImageButton) findViewById(R.id.ibCamera);
		edtOrigem = (EditText) findViewById(R.id.edtOrigem);
		imgCamera_Caixa = (ImageButton) findViewById(R.id.ibCamera_caixa);
		edtOrigem_Caixa = (EditText) findViewById(R.id.edtOrigem_caixa);
		edtFabricante = (EditText) findViewById(R.id.edtFabr);
		edtSif = (EditText) findViewById(R.id.edtSif);
		edtLote = (EditText) findViewById(R.id.edtLote);
		edtDataFab = (EditText) findViewById(R.id.edtDataFab);
		edtDataFab.setFocusable(false);
		edtDataVal = (EditText) findViewById(R.id.edtDataVal);
		edtDataVal.setFocusable(false);
		edtQtd = (EditText) findViewById(R.id.edtQtd);
        edtQtdCaixas = (EditText) findViewById(R.id.edtQtdCaixas);
		edtTipo = (EditText) findViewById(R.id.spinnerTipo);
		btnLimpar = (Button) findViewById(R.id.btnLimpar);
		rdbtPecas = (RadioButton) findViewById(R.id.rdbtPecas);
		rdbtCaixas = (RadioButton) findViewById(R.id.rdbtCaixas);
		rdgrpUnico = (RadioGroup) findViewById(R.id.rdgrpUnico);
//		edtCodSafe = (EditText) findViewById(R.id.edtSeloSafe);
        edtPeso = (EditText) findViewById(R.id.edtPeso);

		//imgCamera2 = (ImageButton) findViewById(R.id.ibCamera2);
		btnAdd = (Button) findViewById(R.id.btnAdd);
        buttonAssociarEtiquetas = (Button) findViewById(R.id.btnLerEtiquetas);

		lvList = (ListView) findViewById(R.id.listview);

		btnSalvar = (Button) findViewById(R.id.btnSalvar);
		btnStatus = (Button) findViewById(R.id.estoq_btnStatus);
		tvQtd = (TextView) findViewById(R.id.tvQtd);

        tvPecasComSelo = (TextView) findViewById(R.id.tvPecasComSelo);
        tvNroDePecas = (TextView) findViewById(R.id.tvNroPecas);

        textWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };

	}

	private void defineAction(){

        buttonAssociarEtiquetas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CamposCompletos()) {
                    int qtdCaixas = 0;
                    int qtdPecas = 0;

                    String nomeProduto = edtTipo.getText().toString();


                    if (!edtQtdCaixas.getText().toString().equals("")) {
                        qtdCaixas = Integer.parseInt(edtQtdCaixas.getText().toString());
                    }

                    if (!edtQtd.getText().toString().equals("")) {
                        qtdPecas = Integer.parseInt(edtQtd.getText().toString());
                    }

                    Intent intent = new Intent(getBaseContext(), ActivityAssociarEtiquetas.class);

                    intent.putExtra("quantidadeCaixas", qtdCaixas);
                    intent.putExtra("quantidadePecas", qtdPecas);
                    intent.putExtra("nomeProduto", nomeProduto);
                    intent.putExtra("usuarioCpf", getIntent().getExtras().getString("cpf"));
                    intent.putExtra("usuarioNome", getIntent().getExtras().getString("Nome"));
                    intent.putExtra("codigoProduto", edtOrigem.getText().toString());
                    intent.putExtra("codigoCaixa", edtOrigem_Caixa.getText().toString());
                    intent.putExtra("nomeFabricante", edtFabricante.getText().toString());
                    intent.putExtra("sif", edtSif.getText().toString());
                    intent.putExtra("dataFab", edtDataFab.getText().toString());
                    intent.putExtra("dataVal", edtDataVal.getText().toString());
                    intent.putExtra("pesoLiquido", edtPeso.getText().toString());
                    intent.putExtra("lote", edtLote.getText().toString());

                    Log.i("Nome Produto", nomeProduto);

                    startActivity(intent);
                }
            }
        });

		btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                enviarArquivos();
                //baixarArquivoDeCodigos();
            }
        });

/*		rdgrpUnico.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (checkedId == R.id.rdbtCaixas) {
                    isUnico = true;
                    editor.putString("Unico", "sim");
                    //Toast.makeText(getApplicationContext(), "sim", Toast.LENGTH_LONG).show();
                } else {
                    isUnico = false;
                    editor.putString("Unico", "nao");
                    //Toast.makeText(getApplicationContext(), "nao", Toast.LENGTH_LONG).show();
                }
                editor.commit();
                updateNumeroDeSelos();
            }
        });
*/
        edtOrigem_Caixa.addTextChangedListener(textWatcher);

        edtOrigem_Caixa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //you can call or do what you want with your EditText here
                if (!hasFocus) {
                    eliminarEspacosVazios();

                    checaInversaoDeCodigos();

                    resultadosClassificacao = classificadorProdutoFabricante.classificar(edtOrigem.getText().toString(), edtOrigem_Caixa.getText().toString(), pathCodes);
                    if (edtOrigem.getText().toString().isEmpty()) {
                        resultadosClassificacao = classificadorProdutoFabricante.classificar(resultadosClassificacao.getBarcodeProduto(), edtOrigem_Caixa.getText().toString(), pathCodes);
                    }
                    updateEditsWithResults(resultadosClassificacao);
                }
            }
        });

        edtOrigem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //you can call or do what you want with your EditText here
                if (!hasFocus) {
                    eliminarEspacosVazios();

                    checaInversaoDeCodigos();
                    resultadosClassificacao = classificadorProdutoFabricante.classificar(edtOrigem.getText().toString(), edtOrigem_Caixa.getText().toString(), pathCodes);
                    updateEditsWithResults(resultadosClassificacao);
                    Log.i("Peso", resultadosClassificacao.getPesoEmGramas());
                }
            }
        });

        edtOrigem.addTextChangedListener(textWatcher);

/*		edtCodSafe.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(edtCodSafe.getText().toString().length() == 11){
						addCod(edtCodSafe.getText().toString());
						edtCodSafe.setText("");
						edtCodSafe.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtCodSafe.getWindowToken(), 0);
				}
			}
		});


        edtCodSafe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtCodSafe, 0);

            }
        });

*/


        edtQtd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!edtQtd.getText().toString().equals("")) {
                        if (Integer.parseInt(edtQtd.getText().toString()) < 1) {
                            //edtQtd.setText("1");
                        }
                    } else {
                        //edtQtd.setText("1");
                    }

                }
            }
        });


        edtQtdCaixas.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!edtQtdCaixas.getText().toString().equals("")) {
                        if (Integer.parseInt(edtQtdCaixas.getText().toString()) < 1) {
                            //edtQtdCaixas.setText("1");
                        }
                    } else {
                        //edtQtdCaixas.setText("1");
                    }
                }
            }
        });

        /*

		imgCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = 1;
				// set the last parameter to true to on front light if available

				Intent intent = new Intent("com.google.zxing.client.android.SCAN"); 
				intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A");
				startActivityForResult(intent, 0);
			}
		});


		imgCamera_Caixa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                // set the last parameter to true to open front light if available

                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A");
                startActivityForResult(intent, 0);

            }
        });

        */

		btnLimpar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				limpar();

			}
		});

		/*imgCamera2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = 2;
				// set the last parameter to true to open front light if available
				//				IntentIntegrator.initiateScan(controleEstoquePrincipal.this, R.layout.capture,
				//						R.id.viewfinder_view, R.id.preview_view, true);

				Intent intent = new Intent("com.google.zxing.client.android.SCAN"); 
				intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A");
				startActivityForResult(intent, 0);
				
			}
		});*/

        /*
		btnAdd.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				if(edtCodSafe.getText().toString().length() == 11){
					addCod(edtCodSafe.getText().toString());
					edtCodSafe.setText("");					
				}
				else{
					Toast.makeText(getApplicationContext(), "Tamanho da etiqueta deve ser 11", Toast.LENGTH_LONG).show();
				}
			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
                int itemPosition = list.size() - position - 1;
                confirmaExcluir(list.get(itemPosition), itemPosition);
            }
        });

        */

		btnSalvar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
                log.append("Clicou no botão de salvar");
                lerCodigoInterno();
            }
		});

		edtDataFab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				//				imm.hideSoftInputFromWindow(edtDataFab.getWindowToken(), 0);

				dataFlag = 0;
				showDialog(1);
			}
		});

		edtDataVal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dataFlag = 1;
				showDialog(2);
			}
		});

        edtDataFab.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (datasNaoVazias()){
                    mostrarVidaUtil();
                }
            }
        });

        edtDataVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (datasNaoVazias()){
                    mostrarVidaUtil();
                }
            }
        });

	}

    private boolean lerCodigoInternoDoArquivo(){
            String codigoDeProduto = edtOrigem.getText().toString();
            log.append("caçando codigo interno no arquivo");
            log.append("codigo: " + codigoDeProduto);

            try {
                BufferedReader leitorBufferizado = new BufferedReader(new FileReader(pathCodesFr));
                String linhaAtual;
                while ((linhaAtual = leitorBufferizado.readLine()) != null) {
                    String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("*"));
                    if (splittedLinhaAtual[1].equals(codigoDeProduto)) {
                        codigoInterno = splittedLinhaAtual[0];
                        log.append("encontrou o codigo no arquivo: " + codigoInterno);
                        return true;
                    }
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            log.append("não encontrou o codigo no arquivo, return false");
            return false;
    }

    private void lerCodigoInterno(){
        log.append("Iniciou Leitura do Codigo Interno");
        if (lerCodigoInternoDoArquivo()){
            log.append("Encontrou codigo interno no arquivo, iniciar salvamento");
            actionSalvar();
            return;
        }

        log.append("iniciando leitura do dialogo");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Leia o codigo de barras interno do produto");

        Context context = this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText codigo = new EditText(context);
        codigo.setHint("Codigo de barras");
        layout.addView(codigo);

        dialog.setView(layout);

        dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                codigoInterno = codigo.getText().toString().replaceAll("[^A-Za-z0-9 ]", "");
                log.append("leu codigo do diálogo: " + codigoInterno);
                log.append("iniciar salvamento");
                actionSalvar();
            }
        });

        dialog.show();
    }

    private boolean datasNaoVazias(){
        return ((!edtDataVal.getText().toString().isEmpty())) && (!edtDataVal.getText().toString().isEmpty());
    }

    private void mostrarVidaUtil(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        TextView diasAteVencimento = (TextView) findViewById(R.id.diasAteVencimento);
        try {
            long d1 = simpleDateFormat.parse(edtDataFab.getText().toString()).getTime();
            long d2 = simpleDateFormat.parse(edtDataVal.getText().toString()).getTime();
            java.util.Date data = new java.util.Date();
            long datual = data.getTime();

            long vida = (d2-d1)/(1000*60*60*24);
            long atual = (datual-d1)/(1000*60*60*24);

            long rest = vida - atual;

            String test = "";

            if (atual > vida * 2 / 3){

                diasAteVencimento.setText(Long.toString(rest));
                diasAteVencimento.setTextSize(30);
                diasAteVencimento.setBackgroundColor(Color.parseColor("#AB0000"));
                diasAteVencimento.setTextColor(Color.parseColor("#FFE0E0"));

            } else if(atual < vida * 1 / 3) {
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

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void eliminarEspacosVazios() {
        edtOrigem.removeTextChangedListener(textWatcher);
        edtOrigem_Caixa.removeTextChangedListener(textWatcher);

        String caixa = edtOrigem_Caixa.getText().toString();
        caixa = caixa.replaceAll("[^A-Za-z0-9]", "");

        String prod = edtOrigem.getText().toString();
        prod = prod.replaceAll("[^A-Za-z0-9]", "");

        edtOrigem_Caixa.setText(caixa);
        edtOrigem.setText(prod);

        edtOrigem_Caixa.addTextChangedListener(textWatcher);
        edtOrigem.addTextChangedListener(textWatcher);

    }

    private void checaInversaoDeCodigos(){
        String codCaixa = edtOrigem_Caixa.getText().toString();
        String codProduto = edtOrigem.getText().toString();

        if (codCaixa.length() < codProduto.length()){
            edtOrigem_Caixa.setText(codProduto);
            edtOrigem.setText(codCaixa);
        }
    }

    private void updateEditsWithResults(ResultadoDeClassificacao resultadosClassificacao){

        if (!resultadosClassificacao.getNomeDoFabricante().isEmpty()) {
            edtFabricante.setText(resultadosClassificacao.getNomeDoFabricante());

            edtFabricante.setBackgroundColor(Color.parseColor("#dddddd"));
            edtFabricante.setTextColor(Color.parseColor("#666666"));
        }

        if (!resultadosClassificacao.getCodigoDoLote().isEmpty()) {
            edtLote.setText(resultadosClassificacao.getCodigoDoLote());

            edtLote.setBackgroundColor(Color.parseColor("#dddddd"));
            edtLote.setTextColor(Color.parseColor("#666666"));
        }

        if (!resultadosClassificacao.getBarcodeProduto().isEmpty()) {
            edtOrigem.setText(resultadosClassificacao.getBarcodeProduto());

            edtOrigem.setBackgroundColor(Color.parseColor("#dddddd"));
            edtOrigem.setTextColor(Color.parseColor("#666666"));
        }


        edtQtd.setText((resultadosClassificacao.getNumeroDePecas().equals("")) ? "1" : resultadosClassificacao.getNumeroDePecas());

        if (!resultadosClassificacao.getNomeDoProduto().equals("")) {
            edtTipo.setText(resultadosClassificacao.getNomeDoProduto());

            edtTipo.setBackgroundColor(Color.parseColor("#dddddd"));
            edtTipo.setTextColor(Color.parseColor("#666666"));
        }

        if (!resultadosClassificacao.getCodigoSif().equals("")) {
            edtSif.setText(resultadosClassificacao.getCodigoSif());

            edtSif.setBackgroundColor(Color.parseColor("#dddddd"));
            edtSif.setTextColor(Color.parseColor("#666666"));
        }

        if (!resultadosClassificacao.getPesoEmGramas().equals("")) {
            edtPeso.setText(resultadosClassificacao.getPesoEmGramas());

            edtPeso.setBackgroundColor(Color.parseColor("#dddddd"));
            edtPeso.setTextColor(Color.parseColor("#666666"));
        }

        int diasDeValidade = 0;

        if (!edtDataVal.getText().toString().isEmpty()){
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                java.util.Date date = simpleDateFormat.parse(edtDataVal.getText().toString());
                resultadosClassificacao.setDataDeValidade(sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!edtDataFab.getText().toString().isEmpty()){
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                java.util.Date date = simpleDateFormat.parse(edtDataFab.getText().toString());
                resultadosClassificacao.setDataDeFabricacao(sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!resultadosClassificacao.getDiasDeValidade().isEmpty()){

            diasDeValidade = Integer.parseInt(resultadosClassificacao.getDiasDeValidade());
            Log.i("Dias de Validade", Integer.toString(diasDeValidade));

            if (resultadosClassificacao.getDataDeValidade().isEmpty() && !resultadosClassificacao.getDataDeFabricacao().isEmpty()) {
                resultadosClassificacao.setDataDeValidade(diasDeValidade);
            }

            if (!resultadosClassificacao.getDataDeValidade().isEmpty() && resultadosClassificacao.getDataDeFabricacao().isEmpty()) {
                resultadosClassificacao.setDataDeFabricacao(diasDeValidade);
            }

        } else {

        }

        Log.i("Teste Fab", resultadosClassificacao.getDataDeFabricacao());
        Log.i("Teste Val", resultadosClassificacao.getDataDeValidade());

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

   	private void calendarioDataAtual(){
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

    private String converterResultDataEmExibData(String dataOriginal){
        SimpleDateFormat simpleDateFormatResult = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat simpleDateFormatExib = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date data = new java.util.Date();

        if (dataOriginal.length() != 6){
            return "";
        }

        try {
            data = simpleDateFormatResult.parse(dataOriginal);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return simpleDateFormatExib.format(data);

    }

	private void updateDisplay() {

		if (dataFlag == 0){
			this.edtDataFab.setText(
					new StringBuilder()
                            // Month is 0 based so add 1
					.append(mDay).append("/")
					.append(mMonth + 1).append("/")
					.append(mYear).append(" "));
        }
		else{
			this.edtDataVal.setText(
					new StringBuilder()
                            // Month is 0 based so add 1
					.append(mDay).append("/")
					.append(mMonth + 1).append("/")
					.append(mYear).append(" "));

		}
		//		calendarioDataAtual();

    }

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
            updateEditsWithResults(resultadosClassificacao);
            view.updateDate(mYear, mMonth, mDay);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		switch (id) {
		case 1:
			dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Data de Fabricação", dpd);
			return dpd;

		case 2:
			dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Data de Validade", dpd);
			return dpd;
		}
		return null;
	}

	private void loadScreen(){

		edtDataFab.setHint("dd/mm/aaaa");
		edtDataVal.setHint("dd/mm/aaaa");
		edtTipo.setText("");
		SharedPreferences settings = getSharedPreferences("Preferences", 0);
		String strUnico = settings.getString("Unico", "nao");

		/*
		ArrayList<String> lista = new ArrayList<String>();

		lista = repositorio.listarTipos();

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTipo.setAdapter(dataAdapter);*/

	}

	private void limpar(){
		edtOrigem_Caixa.setText("");
		edtOrigem.setText("");
        edtOrigem_Caixa.setText("");
        edtOrigem.setText("");
        edtFabricante.setText("");
		edtSif.setText("");
		edtLote.setText("");
		edtDataFab.setText("");
		edtDataVal.setText("");
		edtQtd.setText("");
        edtQtdCaixas.setText("");
		edtTipo.setText("");

        edtPeso.setText("");

		edtOrigem.setEnabled(true);
		edtFabricante.setEnabled(true);
		edtSif.setEnabled(true);
		edtLote.setEnabled(true);
		edtDataFab.setEnabled(true);
		edtDataVal.setEnabled(true);

        TextView diasAteVencimento = (TextView) findViewById(R.id.diasAteVencimento);

        diasAteVencimento.setText("Aguardando datas...");
        diasAteVencimento.setTextSize(15);
        diasAteVencimento.setBackgroundColor(Color.parseColor("#DDDDDD"));
        diasAteVencimento.setTextColor(Color.parseColor("#000000"));

        resetarEstilos();

        resultadosClassificacao = new ResultadoDeClassificacao();
    }

    private void resetarEstilos(){
        EditText et = new EditText(this);
        Drawable originalDrawable = et.getBackground();

        if (edtTipo.getText().toString().isEmpty()) {
            edtTipo.setBackgroundDrawable(originalDrawable);
            edtTipo.setTextColor(Color.BLACK);
        }

        if (edtFabricante.getText().toString().isEmpty()) {
            edtFabricante.setBackgroundDrawable(originalDrawable);
            edtFabricante.setTextColor(Color.BLACK);
        }

        if (edtDataFab.getText().toString().isEmpty()) {
            edtDataFab.setBackgroundDrawable(originalDrawable);
            edtDataFab.setTextColor(Color.BLACK);
        }

        if (edtDataVal.getText().toString().isEmpty()) {
            edtDataVal.setBackgroundDrawable(originalDrawable);
            edtDataVal.setTextColor(Color.BLACK);
        }

        if (edtSif.getText().toString().isEmpty()) {
            edtSif.setBackgroundDrawable(originalDrawable);
            edtSif.setTextColor(Color.BLACK);
        }

        if (edtLote.getText().toString().isEmpty()) {
            edtLote.setBackgroundDrawable(originalDrawable);
            edtLote.setTextColor(Color.BLACK);
        }
    }

    /*
	private void updateListView(){

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), 
				android.R.layout.simple_list_item_1, list) {
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
	*/


	private void actionSalvar(){
        log.append("iniciando actionSalvar");
        if(CamposCompletos()){
            log.append("campos completos, iniciando salvamento");

            objetoControleEstoque objetoCe = new objetoControleEstoque(this);
            log.append("instanciando objeto CE");
            log.append(objetoCe.toString());

            objetoControleEstoqueCodSafe objetoSa = new objetoControleEstoqueCodSafe(this);
            log.append("instanciando objeto SA");
            log.append(objetoSa.toString());

            log.append("colocando usuario no objeto");
            log.append("cpf e user nos extras: " + getIntent().getExtras().getString("Nome") + "-" + getIntent().getExtras().getString("cpf"));

            objetoCe.usuarioNomeCpf = getIntent().getExtras().getString("Nome") +
                            "("+ getIntent().getExtras().getString("cpf")+
                            ")";

            log.append("usuario e cpf dentro do objeto: " + objetoCe.usuarioNomeCpf);
            log.append("colocando id no objeto");

            objetoCe._id = 0;

            log.append("id dentro do objeto" + objetoCe._id);
            log.append("colocando codigo dentro do objeto");
            log.append("codigo do edit: " + edtOrigem.getText().toString());
            objetoCe.codigo = edtOrigem.getText().toString();
            log.append("codigo dentro do objeto: " + objetoCe.codigo);

            log.append("colocando caixa dentro do objeto");
            log.append("caixa do edit: " + edtOrigem_Caixa.getText().toString());
			objetoCe.caixa = edtOrigem_Caixa.getText().toString();
            log.append("caixa dentro do objeto: " + objetoCe.caixa);

            log.append("colocando fabricante dentro do objeto");
            log.append("fabricante do edit: " + edtFabricante.getText().toString());
			objetoCe.fabricante = edtFabricante.getText().toString();
            log.append("fabricante dentro do objeto: " + objetoCe.fabricante);

            log.append("colocando sif dentro do objeto");
            log.append("sif do edit: " + edtSif.getText().toString());
			objetoCe.sif = edtSif.getText().toString();
            log.append("sif dentro do objeto: " + edtSif.getText().toString());

            log.append("colocando data fab dentro do objeto");
            log.append("data fab do edit: " + edtDataFab.getText().toString());
			objetoCe.dataFab = edtDataFab.getText().toString();
            log.append("data fab dentro do objeto: " + objetoCe.dataFab);

            log.append("colocando data val dentro do objeto");
            log.append("data val do edit: " + edtDataVal.getText().toString());
			objetoCe.dataVal = edtDataVal.getText().toString();
            log.append("data val dentro do objeto: " + objetoCe.dataVal);

            log.append("colocando quantidade dentro do objeto");
            log.append("qtd do edit: " + edtQtd.getText().toString());
            log.append("qtdCaixas do edit: " + edtQtdCaixas.getText().toString());
            log.append("multiplicacao dos edits: " + Integer.toString(Integer.parseInt(edtQtd.getText().toString()) * Integer.parseInt(edtQtdCaixas.getText().toString())));
            objetoCe.qtd = Integer.toString(Integer.parseInt(edtQtd.getText().toString()) * Integer.parseInt(edtQtdCaixas.getText().toString()));
            log.append("qtd dentro do objeto: " + objetoCe.qtd);

            log.append("colocando codigo interno dentro do objeto");
            log.append("codigo interno lido ou encontrado: " + codigoInterno);
            objetoCe.codigoInterno = codigoInterno;
            log.append("codigo interno dentro do objeto: " + objetoCe.codigoInterno);

            log.append("colocando tipo de produto dentro do objeto");
            log.append("tipo do produto do edit" + edtTipo.getText().toString());
            objetoCe.tipoProduto = edtTipo.getText().toString();
            log.append("tipo do produto dentro do objeto: " + objetoCe.tipoProduto);

            log.append("escrevendo o lote no objeto");
            if (edtLote.getText().toString().length() > 0) {
                log.append("lote nao vazio, escrevendo no objeto");
                log.append("lote do edit: " + edtLote.getText().toString());
                objetoCe.lote = edtLote.getText().toString();
                log.append("lote dentro do objeto: " + objetoCe.lote);
            } else {
                log.append("lote vazio, usando a data fab");
                log.append("lote data fab do edit: " + edtDataFab.getText().toString());
                objetoCe.lote = edtDataFab.getText().toString();
                log.append("lote dentro do objeto: " + objetoCe.lote);
            }

            log.append("escrevendo peso no objeto");
            if (edtPeso.getText().toString().equals("")){
                log.append("peso vazio, escrevendo zeros");
                objetoCe.pesoLiquido = "0";
                log.append("peso liquido no objeto: " + objetoCe.pesoLiquido);
                objetoCe.pesoMedioLote = "0";
                log.append("peso medio lote: " + objetoCe.pesoMedioLote);
            } else {
                log.append("peso nao vazio, escrevendo no objeto");
                log.append("peso liquido do edit: " + edtPeso.getText().toString());
                objetoCe.pesoLiquido = edtPeso.getText().toString();
                log.append("peso liquido no objeto: " + objetoCe.pesoLiquido);

                log.append("peso medio do lote calculado: " + Integer.toString(Integer.parseInt(edtPeso.getText().toString()) *
                        Integer.parseInt(edtQtdCaixas.getText().toString())));
                objetoCe.pesoMedioLote =
                        Integer.toString(Integer.parseInt(edtPeso.getText().toString()) *
                                Integer.parseInt(edtQtdCaixas.getText().toString()));
                log.append("peso medio do lote no objeto: " + objetoCe.pesoMedioLote);
            }

            log.append("escrevendo area de uso no objeto");
            log.append("area de uso nas preferences: " + getSharedPreferences("Preferences", 0).getString("AREADEUSO", ""));
            objetoCe.areaDeUso = getSharedPreferences("Preferences", 0).getString("AREADEUSO", "");
            log.append("area de uso dentro do objeto: " + objetoCe.areaDeUso);

            log.append("objeto ce depois das atribuições: " + objetoCe.toString());

            log.append("iniciando controler do historico XML");
            HistoricoXMLController historicoXMLController = new HistoricoXMLController(
                    getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", ""),
                    getSharedPreferences("Preferences", 0).getString("NUMLOJA", ""),
                    getSharedPreferences("Preferences", 0).getString("NUMTABLET", ""),
                    new java.util.Date(),
                    HistoricoXMLController.TYPE_CE
            );

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            log.append("adicionando objeto ao historico");
            try {
                historicoXMLController.adicionarObjetoHistorico(
                        new ObjetoHistoricoControleEstoque(
                                edtTipo.getText().toString(),
                                edtFabricante.getText().toString(),
                                simpleDateFormat.parse(edtDataFab.getText().toString()),
                                simpleDateFormat.parse(edtDataVal.getText().toString()),
                                Integer.parseInt(objetoCe.qtd),
                                new java.util.Date()
                                )
                );
            } catch (ParseException e) {
                log.append("erro de historico");
                log.append(e.getStackTrace().toString());
                e.printStackTrace();
            }

            long idInserido = repositorio.salvarControleEstoque_Origem(objetoCe);
            log.append("id Inserido no banco: " + idInserido);
			objetoCe._id = idInserido;
            log.append("id dentro do objeto: " + objetoCe._id);
            log.append("objeto ce apos inserção no banco: " + objetoCe.toString());

            log.append("escrevendo tipo no objeto sa: " + edtTipo.getText().toString());
			objetoSa.tipo = edtTipo.getText().toString();
            log.append("tipo no objeto sa: " + objetoSa.toString());
            log.append("objeto sa: " + objetoSa.toString());

            SimpleDateFormat dateNome = new SimpleDateFormat("yyyyMMdd");
			String data_arquivo = dateNome.format(new Date( currentTimeMillis()));
            log.append("data do arquivo: " + data_arquivo);

			dateNome = new SimpleDateFormat("HHmmss");
			String hora_arquivo = dateNome.format(new Date( currentTimeMillis()));
            log.append("hora arquivo: " +   hora_arquivo);

			SharedPreferences settings = getSharedPreferences("Preferences", 0);
			String prefix = settings.getString("NUMCLIENTE", "") +
                    settings.getString("NUMLOJA", "");
            log.append("prefix: " + prefix);

			String prefix2 = settings.getString("NUMCLIENTE", "") +
                    settings.getString("NUMLOJA", "")+
                    "_"+settings.getString("NUMTABLET", null)+
                    "_";
            log.append("prefix 2: " + prefix2);

            String idLoja = settings.getString("NUMLOJA", "");
            log.append("id loja: " + idLoja);

			SimpleDateFormat formatAux = new SimpleDateFormat("ddMMyy");
			String data_aux = formatAux.format(new Date( currentTimeMillis()));
            log.append("data_aux: " + data_aux);

			String numCliente = settings.getString("NUMCLIENTE", "");
            log.append("num cliente: " + numCliente);

			String fileName = "OK_A_01_ControleEstoque_Origem_" +
                    "_"  + data_arquivo +
                    "_" + hora_arquivo +
                    "_" + numCliente+settings.getString("NUMLOJA", "") +
                    settings.getString("NUMTABLET", "");
            log.append("fileName: " + fileName);

			String auxCod = "STM" +
                    prefix +
                    data_aux +
                    hora_arquivo +
                    "_" + numCliente+settings.getString("NUMLOJA", "") +
                    settings.getString("NUMTABLET", "");
            log.append("codigo stm: " + auxCod);

            objetoCe.tipoProduto = edtTipo.getText().toString();
            log.append("objeto ce: " + objetoCe.toString());

            log.append("======== iniciar salvamento do arquivo ========");
            objetoCe.saveFile(fileName, auxCod, idLoja, numCliente);
            log.append("======== fim do salvamento do arquivo =========");

            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");

            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

            java.util.Date dataEdit = new java.util.Date();
            try {
                dataEdit = simpleDateFormat2.parse(edtDataVal.getText().toString());
                log.append("dataEdit: " + dataEdit);
            } catch (ParseException e) {
                e.printStackTrace();
                log.append(e.getStackTrace().toString());
            }

            String dataValString = converterDataEmJuliano(dataEdit);
            log.append("dataValString: " + dataValString);

            log.append("objeto sa: " + objetoSa.toString());
            objetoSa._id = 0;
            log.append("objeto sa id: " + objetoSa._id);
            objetoSa.codigoSafe =
                    settings.getString("NUMCLIENTE", "") +
                    settings.getString("NUMLOJA", "") +
                    dataValString +
                    edtOrigem.getText().toString()
            ;
            log.append("objeto sa codigo safe: " + objetoSa.codigoSafe);

            objetoSa._id = repositorio.salvarControleEstoque_CodSafe(objetoSa);
            log.append("objeto sa id: " + objetoSa._id);

            fileName =
                    "OK_A_02_ControleEstoque_CodSaf" +
                            "_" + 0 +
                            "_" + data_arquivo +
                            "_" + hora_arquivo +
                            "_" + numCliente +
                            settings.getString("NUMLOJA", "") +
                            settings.getString("NUMTABLET", "");
            log.append("filename: " + fileName);

            log.append("======== iniciando salvamento do a02 ========");
            objetoSa.saveFile(fileName, idLoja, numCliente);
            log.append("======== fim do salvamento do a02 ===========");

            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(objetoSa.codigoSafe);
            log.append("adicionou no array list: " + objetoSa.codigoSafe);

            log.append("salvar arquivo B");
            fileName = "OK_B_01_ControleEstoque_CodSaf_" +
                    data_arquivo +
                    "_" + hora_arquivo +
                    "_" + numCliente+settings.getString("NUMLOJA", "") +
                    settings.getString("NUMTABLET", "");
            log.append("filename: " + fileName);

            log.append("======== iniciando salvamento do B ========");
			objetoSa.saveFileB(fileName, auxCod, arrayList);
            log.append("======== final do salvamento do b =========");

            ArrayList<String> codigosIDX = new ArrayList<String>();
            try {
                codigosIDX.add(numCliente + idLoja + converterDataEmJuliano(new SimpleDateFormat("dd/MM/yyyy").parse(objetoCe.dataVal)) + codigoInterno);
                log.append("adicionou aos codigos IDX (array): " + codigosIDX.get(codigosIDX.size()-1));
            } catch (ParseException e) {
                e.printStackTrace();
                log.append(e.getStackTrace().toString());
            }

            log.append("inserindo idx no banco");
            repositorio.inserirIdxBaixado(codigosIDX.get(0), objetoSa.tipo, objetoCe.fabricante, objetoCe.sif, objetoCe.lote, objetoCe.dataFab, objetoCe.dataVal);

			fileName = prefix2 + diaAtual();
            log.append("filename: " + fileName);

			int pos = idxExiste(fileName + ".idx");
            log.append("pos: " + pos);

			if(pos == -1) {
                log.append("======= salvando idx =======");
                objetoSa.saveFileIdx(fileName,
                        codigosIDX,
                        objetoSa.tipo,
                        objetoCe.fabricante,
                        objetoCe.sif,
                        objetoCe.lote,
                        objetoCe.dataFab,
                        objetoCe.dataVal);
                log.append("======= fim salvar idx =====");
            } else{
                log.append("======= editando idx =======");
				editaIdx(pos,
                        objetoSa.tipo,
                        objetoCe.fabricante,
                        objetoCe.sif,
                        objetoCe.lote,
                        objetoCe.dataFab,
                        objetoCe.dataVal);
                log.append("======= fim da edicao ======");
			}

			Toast.makeText(getApplicationContext(), "Salvo com sucesso", Toast.LENGTH_LONG).show();

            log.append("Limpando Array List");
			arrayList.clear();
            log.append("Setting Status");
			setStatus();
            log.append("Limpando Campos");
            limpar();

        }
	}

	private void editaIdx(int pos, String tipo, String fabricante, String sif, String lote, String dFab, String dVal){

		File fListIdx[] = pathIdx.listFiles();
		File arquivo = fListIdx[pos];
		//		arquivo.getName().equals(nomeArquivo)

		ArrayList<String> records = new ArrayList<String>();

		try{
			BufferedReader reader = new BufferedReader(new FileReader(arquivo));
			String line;
			while ((line = reader.readLine()) != null){
				records.add(line);
			}
			reader.close();

			for(int i=0; i<list.size(); i++){
				records.add(
                    list.get(i) + "-" +
                    tipo + "-" +
                    fabricante + "-" +
                    sif + "-" +
                    lote + "-" +
                    dFab + "-" +
                    dVal
                );
			}

			objetoControleEstoqueCodSafe objAux = new objetoControleEstoqueCodSafe(this);
			objAux.editaFileIdx(arquivo.getName(), records);
		}
		catch (Exception e){
			err.format("Exception occurred trying to read '%s'.", arquivo.getName());
			e.printStackTrace();
		}
	}

	private void interpretaCaixa(String primeiroCodigo, String segundoCodigo ){
		if(segundoCodigo == null){
			Log.i("PrimeiroCodigo", primeiroCodigo.substring(0, 4));
			if(primeiroCodigo.substring(0, 4).equals("3102")){
				edtFabricante.setText("Sadia");
			}
		}
	}

	private void codigoSadia(String codigo, int qtd) {
		edtFabricante.setText("Sadia");
		String data_fabricacao = codigo.substring(12, 18);
		String data_validade = "";

		int ano = Integer.valueOf(data_fabricacao.substring(0,2));
		int mes = Integer.valueOf(data_fabricacao.substring(2,4));
		int dia = Integer.valueOf(data_fabricacao.substring(4,6));

		Date a = (Date) new Date(100+ano,mes-1,dia);

		String formato = "dd/MM/yyyy";
		SimpleDateFormat dataFormatada = new SimpleDateFormat(formato);
		data_fabricacao = dataFormatada.format(a);
		edtDataFab.setText(data_fabricacao);

		a.setDate(a.getDate() + qtd);

		dataFormatada = new SimpleDateFormat(formato);
		data_validade = dataFormatada.format(a);
		edtDataVal.setText(data_validade);
	}

	private void codigoPerdigao(String codigo) {
		edtFabricante.setText("PerdigÃ£o");
		String data_fabricacao = codigo.substring(codigo.length()-4, codigo.length());
		String data_validade = "";

		int ano = Integer.valueOf(data_fabricacao.substring(0,1));
		int dias = Integer.valueOf(data_fabricacao.substring(1,data_fabricacao.length()));

		Date a = (Date) new Date(110+ano,0,0);
		a.setDate(a.getDate() + dias);

		String formato = "dd/MM/yyyy";
		SimpleDateFormat dataFormatada = new SimpleDateFormat(formato);
		data_fabricacao = dataFormatada.format(a);
		edtDataFab.setText(data_fabricacao);

		a.setDate(a.getDate() + 90);

		dataFormatada = new SimpleDateFormat(formato);
		data_validade = dataFormatada.format(a);
		edtDataVal.setText(data_validade);
	}

	private void codigoSeara(String codigo, int qtd) {

		if(edtOrigem_Caixa.getText().toString().length() == 42){
			edtFabricante.setText("Seara");
			String data_fabricacao = codigo.substring(18, 24);
			String data_validade = "";

			int ano = Integer.valueOf(data_fabricacao.substring(0,2));
			int mes = Integer.valueOf(data_fabricacao.substring(2,4));
			int dia = Integer.valueOf(data_fabricacao.substring(4,6));

			Date a = (Date) new Date(100+ano,mes-1,dia);

			String formato = "dd/MM/yyyy";
			SimpleDateFormat dataFormatada = new SimpleDateFormat(formato);
			data_fabricacao = dataFormatada.format(a);
			edtDataVal.setText(data_fabricacao);

			a.setDate(a.getDate() - qtd);

			dataFormatada = new SimpleDateFormat(formato);
			data_validade = dataFormatada.format(a);
			edtDataFab.setText(data_validade);
		}
	}

	private int idxExiste(String nomeArquivo){
		File fListIdx[] = pathIdx.listFiles();
		//Log.i("Verifica se Existe", "Existente: " + fListIdx[0].getName()+ " Novo" + nomeArquivo);
		for(int i=0; i < (fListIdx.length); i++){
			File arquivo = fListIdx[i];

			if(arquivo.getName().equals(nomeArquivo)){
				return i;
			}
		}

		return -1;
	}

	private String diaAtual(){

		String aux="";
		int year = ((Calendar.getInstance().get(Calendar.YEAR)) % 10);

		Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
		int CurrentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

		if(String.valueOf(CurrentDayOfYear).length() == 1){
			aux = "00"+CurrentDayOfYear;
		}
		else if(String.valueOf(CurrentDayOfYear).length() == 2){
			aux = "0"+CurrentDayOfYear;
		}
		else{
			aux = String.valueOf(CurrentDayOfYear);
		}

		return String.valueOf(year) + aux;

	}

	private void addCod(String codigo){
        if (list.size() == Integer.parseInt(tvNroDePecas.getText().toString())){
            Toast.makeText(getApplicationContext(), "Você já inseriu todos os codigos necessários!", Toast.LENGTH_LONG).show();
            return;
        }

		if(codigo.equals("")){
			Toast.makeText(getApplicationContext(), "Insira o código Safe Trace", Toast.LENGTH_LONG).show();
		}else if(verificaSelo(codigo)){
			Toast.makeText(getApplicationContext(), "Código Safe Trace já inserido", Toast.LENGTH_LONG).show();
			edtCodSafe.setText("");
		}
		else{
			list.add(codigo);
	//		updateListView();

            if(edtQtd.getText().toString().equals("")){
                tvPecasComSelo.setText(Integer.toString(list.size()));
                //tvNroDePecas.setText(Integer.toString(0));
            } else{
                tvPecasComSelo.setText(Integer.toString(list.size()));
                //tvNroDePecas.setText(edtQtd.getText().toString());
            }

            if (tvPecasComSelo.getText().toString().equals(tvNroDePecas.getText().toString())){
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                TextView tv = (TextView) findViewById(R.id.tvBarraPecas);
                if (tvNroDePecas.getText().toString().equals(tvPecasComSelo.getText().toString())){
                    tvNroDePecas.setTextColor(Color.GREEN);
                    tvPecasComSelo.setTextColor(Color.GREEN);
                    tv.setTextColor(Color.GREEN);
                } else {
                    tvNroDePecas.setTextColor(Color.RED);
                    tvPecasComSelo.setTextColor(Color.RED);
                    tv.setTextColor(Color.RED);
                }
            }
		}
	}

	private boolean verificaSelo(String selo){
		for(int i=0; i<list.size(); i++){
			if(list.get(i).equals(selo))
				return true;
		}

		return false;
	}

	private boolean CamposCompletos() {

        log.append("testando se campos estão completos");

		if(edtOrigem.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira o código de produto", Toast.LENGTH_LONG).show();
            log.append("codigo do produto vazio");
            return false;

		}
        log.append("codigo do produto OK");

		//		if(edtOrigem_Caixa.getText().toString().equals("")){
		//			Toast.makeText(getApplicationContext(), "Insira o cï¿½digo de caixa", Toast.LENGTH_LONG).show();
		//			return false;
		//		}

		if(edtFabricante.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira o Fabricante", Toast.LENGTH_LONG).show();
            log.append("fabricante vazio");
			return false;
		}
        log.append("fabricante OK");

		if(edtSif.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira o SIF", Toast.LENGTH_LONG).show();
            log.append("sif vazio");
			return false;
		}
        log.append("sif OK");

		if(edtDataFab.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira a Data de Fabricação", Toast.LENGTH_LONG).show();
            log.append("data fab vazio");
			return false;
		}
        log.append("data fab OK");

		if(edtDataVal.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira a Data de Validade", Toast.LENGTH_LONG).show();
            log.append("data val vazio");
			return false;
		}
        log.append("data val OK");

		if(edtQtd.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira a Quantidade", Toast.LENGTH_LONG).show();
            log.append("quantidade vazio");
			return false;
		}
        log.append("data OK");

		if(edtTipo.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira o Nome do Produto", Toast.LENGTH_LONG).show();
            log.append("nome do produto vazio");
			return false;
		}
        log.append("nome do produto OK");

        if(edtQtd.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Insira a Quantidade", Toast.LENGTH_LONG).show();
            log.append("Quantidade Vazia");
            return false;
        }
        log.append("quantidade OK");

        if(edtQtdCaixas.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Insira a Quantidade", Toast.LENGTH_LONG).show();
            log.append("Quantidade Vazia");
            return false;
        }
        log.append("quantidade OK");

        log.append("todos campos OK");
        return true;
	}

    private void baixarArquivoDeCodigos(){
        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        Log.i("Processo de Download", "Iniciado");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPClient ftpClient = new FTPClient();

                try {
                    ftpClient.connect(SERVIDOR, 21);
                    ftpClient.login(NOME, SENHA);

                    if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){

                        ftpClient.changeWorkingDirectory("/STMarket");
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        ftpClient.enterLocalPassiveMode();

                        String arquivoRemoto = "produtos_ce.txt";
                        String arquivoRemotoFr = "codigos_conv_frac.txt";

                        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pathCodes));
                        OutputStream outputStreamFr = new BufferedOutputStream(new FileOutputStream(pathCodesFr));

                        boolean sucesso = ftpClient.retrieveFile(arquivoRemoto, outputStream);

                        outputStream.close();

                        if (sucesso) {
                            Log.i("Arquivo Status: ", "Baixado");
                        } else {
                            Log.i("Arquivo Status: ", "Failure");
                        }

                        sucesso = ftpClient.retrieveFile(arquivoRemotoFr, outputStreamFr);

                        outputStreamFr.close();

                        if (sucesso) {
                            Log.i("Arquivo Status: ", "Baixado");
                        } else {
                            Log.i("Arquivo Status: ", "Failure");
                        }

                        ftpClient.disconnect();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class AppZip
    {
        List<String> fileList;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

        private final String OUTPUT_ZIP_FILE = Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + "ZIP_CE_" + getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") + getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") + getSharedPreferences("Preferences", 0).getString("NUMTABLET", "")+"_"+ simpleDateFormat.format(new java.util.Date()) + ".zip";
        private final String SOURCE_FOLDER = Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque";

        AppZip(){
            fileList = new ArrayList<String>();
        }

        public void ziparTudo()
        {
            this.generateFileList(new File(SOURCE_FOLDER));
            this.zipIt(OUTPUT_ZIP_FILE);
        }

        /**
         * Zip it
         * @param zipFile output ZIP file location
         */
        public void zipIt(String zipFile){

            byte[] buffer = new byte[1024];

            try{

                if (this.fileList.size() > 1) {

                        FileOutputStream fos = new FileOutputStream(zipFile);
                        ZipOutputStream zos = new ZipOutputStream(fos);

                        Log.i("Output to Zip", zipFile);


                        for (String file : this.fileList) {

                            if (file.equals("produtos_ce.txt")) {

                            } else{

                                if (file.substring(file.length()-3).equals(".st")) {

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
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }

        /**
         * Traverse a directory and get all files,
         * and add the file into fileList
         * @param node file or directory
         */
        public void generateFileList(File node){

            //add file only0
            if(node.isFile()){
                fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
            }

            if(node.isDirectory()){
                String[] subNote = node.list();
                for(String filename : subNote){
                    generateFileList(new File(node, filename));
                }
            }

        }

        /**
         * Format the file path for zip
         * @param file file path
         * @return Formatted file path
         */
        private String generateZipEntry(String file){
            return file.substring(SOURCE_FOLDER.length()+1, file.length());
        }
    }

	private void enviarArquivos() {

        LogGenerator logGenerator = new LogGenerator(this);
        logGenerator.enviarLogs();

        baixarArquivoDeCodigos();

        AppZip appZip = new AppZip();
        appZip.ziparTudo();

        if ((path.list().length > 0) || (pathIdx.list().length > 0)) {

			SharedPreferences settings = getSharedPreferences("Preferences", 0);
			final String aux = settings.getString("NUMCLIENTE", "") + settings.getString("NUMLOJA", "")+"_"+ settings.getString("NUMTABLET", "")+"_"+ diaAtual() + ".st";

			dialog = new ProgressDialog(this);
            dialog.setTitle("Conectando");
            dialog.setMessage("Enviando dados, por favor aguarde...");
            dialog.setMax(4);
            dialog.setProgress(1);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();

			new Thread(new Runnable() {
				public void run() {

                    dialog.setProgress(2);

					File fList[] = path.listFiles();
					for(int i=0; i < (fList.length); i++){
						File arquivo = fList[i];
                        Log.i("FILE", "RAS = " + arquivo.getName());
                        if (!arquivo.getName().contains("produtos_ce.txt")){
                            enviou = envioFTP(arquivo.getName(), 0, aux);
                            if (!enviou) break;
                        } else {
                            enviou = true;
                        }
					}

                    dialog.setProgress(3);

                        File fListIdx[] = pathIdx.listFiles();
                        for(int i=0; i < (fListIdx.length); i++){
                            File arquivo = fListIdx[i];
                            Log.i("FILE", "IDX = "+arquivo.getName());
                            enviou = envioFTP(arquivo.getName(), 1, aux);
                            if (!enviou) break;
                        }

					handler.post(new Runnable() {
						public void run() {
							dialog.dismiss();
							if (!enviou) {
								Log.i("envia", msgErro);
								Toast.makeText(getApplicationContext(), msgErro, Toast.LENGTH_LONG).show();
							}

						}
					});

                    dialog.setProgress(4);
					setStatus();
					dialog.dismiss();

				}
			}).start();
		}
		setStatus();
	}

	private Boolean envioFTP( String nomeArquivo, int flag, String idxAtual) {
		FTPClient ftp = new FTPClient();
		Boolean retorno = false;
		msgErro = "Falha de conexão";

		try {

			ftp.connect(SERVIDOR,21);
			ftp.login(NOME, SENHA);

			if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

				//				ftp.changeWorkingDirectory("Teste");
				File file;

				if(flag == 0){
                        file = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour"  + File.separator + "Estoque"+ File.separator + nomeArquivo);
                        Log.d("NOME ARQUIVO", file.toString());
                        FileInputStream arqEnviar = new FileInputStream(file);

                        Log.i("FILE", arqEnviar.toString());

                        ftp.enterLocalPassiveMode();
                        ftp.changeWorkingDirectory("arquivos_novos");
                        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                        ftp.storeFile(nomeArquivo, arqEnviar);

                        arqEnviar.close();
                        file.delete();
                        retorno = true;

				}
				else{
					ftp.enterLocalPassiveMode();

					ftp.changeWorkingDirectory("STMarket");
					ftp.changeWorkingDirectory("idx");

					for (String name : ftp.listNames()) {
						if(name.equals(nomeArquivo)){
							//baixa esse arquivo
							File arquivo2 = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + "temp.idx");

							FileOutputStream desFileStream = new FileOutputStream(arquivo2.getAbsolutePath());
							ftp.setFileType(FTP.BINARY_FILE_TYPE);
							ftp.enterLocalPassiveMode();
							ftp.retrieveFile(nomeArquivo, desFileStream);

							Log.i("FILE", desFileStream.toString());
							desFileStream.close();
							break;
						}
					}
					editaIdx2(nomeArquivo);

					file = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + nomeArquivo);
					Log.d("NOME ARQUIVO", file.toString());
					FileInputStream arqEnviar = new FileInputStream(file);

					ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

					ftp.storeFile(nomeArquivo, arqEnviar);
					arqEnviar.close();

					if(!nomeArquivo.equals(idxAtual)){
						file.delete();
					}

					retorno = true;

				}
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

	private void editaIdx2(String nomeArquivo){
		File temp = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + "temp.idx");
		File idx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + nomeArquivo);

		ArrayList<String> records = new ArrayList<String>();

		try{
			BufferedReader reader = new BufferedReader(new FileReader(temp));
			String line;
			while ((line = reader.readLine()) != null){
				records.add(line);
			}
			reader.close();

			reader = new BufferedReader(new FileReader(idx));
			while ((line = reader.readLine()) != null){
				records.add(line);
			}
			reader.close();

			temp.delete();
			idx.delete();

			idx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + nomeArquivo);
			FileOutputStream out = new FileOutputStream(idx);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
			PrintWriter Print = new PrintWriter(OSW);

			String linha;

			for(int i=0; i<records.size(); i++){
				linha = records.get(i);
				if(i==records.size()-1)
					Print.print(linha);
				else
					Print.println(linha);
			}

			Print.close();
			OSW.close();
			out.close();
		}
		catch (Exception e){
			err.format("Exception occurred trying to read '%s'.", idx.getName());
			e.printStackTrace();
		}
	}

	private void extraiData(String dataTotal){
		//		Toast.makeText(this, dataTotal, 0).show();
		String ano = "20"+dataTotal.substring(0, 2);
		String mes = dataTotal.substring(2, 4);
		String dia = dataTotal.substring(4, 6);

		edtDataFab.setText(dia + "/" + mes + "/" + ano);

		int aux = Integer.parseInt(ano) + 1;

		edtDataVal.setText(dia + "/" + mes + "/" + aux);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if(requestCode == 0) {
			if(resultCode == RESULT_OK) {
				String result = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.i("xZing", "contents: "+result+" format: "+format);
				// Handle successful scan

				 Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
				 // Vibrate for 500 milliseconds
				 v.vibrate(500);


				if(flag == 1){
					if(result.length() > 14){
						edtOrigem_Caixa.setText(edtOrigem_Caixa.getText().toString() + result);
					}
					else {
						edtOrigem.setText(result);
					}


				}
				else if (flag == 2){
					if(result.length() == 11){
						addCod(result);
					}
					else{
						Toast.makeText(getApplicationContext(), "Tamanho da etiqueta deve ser 11", Toast.LENGTH_LONG).show();
					}
				}
			}
			else if(resultCode == RESULT_CANCELED) {
				// Handle cancel
				Log.i("xZing", "Cancelled");
			}
		}
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

	private void confirmaExcluir(String codigo, final int pos) {

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Excluir");
		builder.setMessage("Dejesa mesmo excluir " + codigo + " ?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				list.remove(pos);
	//			updateListView();
				if(edtQtd.getText().toString().equals("")){
				} else{
					tvPecasComSelo.setText(Integer.toString(list.size()));

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

    /**
     *  Metodo que converte a data em seu formato juliano
     * @return {@link java.lang.String} contendo a data no formado YDDD
     */
    private String converterDataEmJuliano(java.util.Date data){
        String dataJuliano = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String anoAtual = simpleDateFormat.format(data);

        simpleDateFormat = new SimpleDateFormat("D");
        String diaAtual = simpleDateFormat.format(data);

        dataJuliano = anoAtual.substring(3) + ajustaZeros(diaAtual, 3);
        return dataJuliano;
    }

    /**
     *  Metodo que ajusta valores completando com zeros a esqueda
     * @param valorInicial {@link java.lang.String} a ser ajustada
     * @param tamanhoEsperado numero de casas que o valor deve possuir no total
     * @return {@link java.lang.String} ajustada com zeros a esquerda
     */
    private String ajustaZeros(String valorInicial, int tamanhoEsperado){
        String valorFinal = "";
        for (int i = 0; i < tamanhoEsperado - valorInicial.length(); i++){
            valorFinal += "0";
        }
        return valorFinal + valorInicial;
    }


}
