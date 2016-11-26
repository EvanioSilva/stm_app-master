package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActivityControleDeEstoqueCD extends Activity {

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

    public EditText edtNumRecepcao;
    public EditText edtPlaca;
    public EditText edtData;

    public HelperFtpIn helperFTP;

    public Button btnLimpa;
    public Button btnRecebe;
    public Button btnStatus;

    private String filenamemask = "";

    @Override
    protected void onResume() {
        super.onResume();

        setStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_controle_de_estoque_cd);

        helperFTP = new HelperFtpIn(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        MapearComponentes();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            edtPlaca.setText(extras.getString("placa"));
            edtNumRecepcao.setText(extras.getString("nota"));
            filenamemask = extras.getString("filename","");
        }

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
        edtNumRecepcao = (EditText) findViewById(R.id.edtNumRecepcao);
        edtPlaca = (EditText) findViewById(R.id.edtPlaca);
        edtData = (EditText) findViewById(R.id.edtDataRecebimento);

        btnLimpa = (Button) findViewById(R.id.btnLimpar);
        btnRecebe = (Button) findViewById(R.id.btnReceber);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String data = df.format(c.getTime());
        edtData.setText(data);

        btnStatus = (Button) findViewById(R.id.estoq_btnStatus);

        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ActivityControleDeEstoqueCD.this);
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

        btnLimpa.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (filenamemask.equals("")) {

                    edtNumRecepcao.setText("");
                    edtPlaca.setText("");
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String data = df.format(c.getTime());
                    edtData.setText(data);

                } else {

                    limparPre();

                }

            }
        });

        btnRecebe.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                try {
                    validarCampos();

                    Intent i;

                    if (filenamemask.equals("")) {
                        i = new Intent(getBaseContext(), ActivityRecebimento.class);
                    } else {
                        i = new Intent(getBaseContext(), ActivitySelecionaRecebimentoFriosItem.class);
                    }
                    i.putExtra("numeroRecepcao", edtNumRecepcao.getText().toString());
                    i.putExtra("placa", edtPlaca.getText().toString());
                    i.putExtra("data", edtData.getText().toString());

                    i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                    i.putExtra("cpf", getIntent().getExtras().getString("cpf"));

                    i.putExtra("filename", filenamemask);

                    startActivity(i);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(ActivityControleDeEstoqueCD.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void validarCampos() throws Exception {
        validarNumeroRecepcao();
        validarPlacaCaminhao();
    }

    private void validarPlacaCaminhao() throws Exception {
        validarNaoVazio(edtPlaca);
        validarFormatoPlaca(edtPlaca);
    }

    private void validarFormatoPlaca(EditText editText) throws Exception {
//        if (edtPlaca.getText().toString().matches("[a-zA-Z]{3}\\d{4}")) {
        if (edtPlaca.getText().toString().matches("[A-Z]{3}\\d{4}")) {
            return;
        } else {
            throw new Exception("Preencha a placa do caminhão corretamente (XXX0000)");
        }
    }

    private void validarNumeroRecepcao() throws Exception {
        validarNaoVazio(edtNumRecepcao);
    }

    private void validarNaoVazio(EditText editText) throws Exception {
        if (editText.getText().toString().isEmpty()) {
            throw new Exception("Preencha todos os dados (" + editText.getHint().toString() + ")");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_controle_de_estoque_cd, menu);
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
        if (id == R.id.action_relatorio) {
            Intent i = new Intent(this, ActivityRelatorioControleDeEstoqueCD.class);
            startActivity(i);
        }
        if (id == R.id.action_testes) {
            Intent i = new Intent(this, ActivityTestes.class);
            i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
            i.putExtra("cpf", getIntent().getExtras().getString("cpf"));
            startActivity(i);
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


        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        Intent i = new Intent(this, ActivityTelaInicial.class);
        startActivity(i);
        finish();
    }

    public void limparPre(){

        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Confirmação de Limpeza")
            .setMessage("Esta operação tornará esta entrada manual, deseja mesmo limpar?")
            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    edtNumRecepcao.setText("");
                    edtPlaca.setText("");
                    filenamemask = "";
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String data = df.format(c.getTime());
                    edtData.setText(data);
                }

            })
            .setNegativeButton("Não", null)
            .show();
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
