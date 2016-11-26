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
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityControleDeEstoqueCDHortifruti extends Activity {

    private class HelperFtpIn extends HelperFTP{

        public HelperFtpIn(Context context) {
            super(context);
        }

        public int enviarArquivos(){
            super.enviarArquivos();
            setStatus();
            return 1;
        }
    }

    public EditText edtNumRecepcao;
    public EditText edtPlacaCaminhao;
    public EditText edtData;

    public HelperFtpIn helperFTP;

    public Button btnLimpa;
    public Button btnRecebe;
    public Button btnStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle_de_estoque_cdhortifruti);

        helperFTP = new HelperFtpIn(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        MapearComponentes();

        setStatus();


    }

    private void setStatus(){

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
        edtNumRecepcao = (EditText) findViewById(R.id.edtNumRomaneio);
        edtData = (EditText) findViewById(R.id.edtDataRecebimento);
        edtPlacaCaminhao = (EditText) findViewById(R.id.edtPlacaCaminhao);

        btnLimpa = (Button) findViewById(R.id.btnLimpar);
        btnRecebe = (Button) findViewById(R.id.btnReceber);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String data = df.format(c.getTime());
        edtData.setText(data);

        btnStatus = (Button) findViewById(R.id.estoq_btnStatus);

        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ActivityControleDeEstoqueCDHortifruti.this);
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

        btnLimpa.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                edtNumRecepcao.setText("");
                edtPlacaCaminhao.setText("");
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String data = df.format(c.getTime());
                edtData.setText(data);
            }
        });

        btnRecebe.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                try {
                    validarCampos();

                    Intent i = new Intent(getBaseContext(), ActivityRecebimentoHortifruti.class);
                    i.putExtra("idRecepcaoBanco", salvarNoBanco());

                    i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                    i.putExtra("cpf", getIntent().getExtras().getString("cpf"));

                    i.putExtra("numeroRecepcao", edtNumRecepcao.getText().toString() + "#GAMBI#" + edtPlacaCaminhao.getText().toString());

                    startActivity(i);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(ActivityControleDeEstoqueCDHortifruti.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private long salvarNoBanco(){
        ModelRecepcaoHortifruti modelRecepcaoHortifruti = new ModelRecepcaoHortifruti(
                edtNumRecepcao.getText().toString() + "#GAMBI#" + edtPlacaCaminhao.getText().toString(),
                edtData.getText().toString(),
                new ArrayList<ModelProdutoRecebidoHortifruti>()
        );

        modelRecepcaoHortifruti.save();

        return modelRecepcaoHortifruti.getId();
    }

    private void validarCampos() throws Exception{
        //validarNumeroRecepcao();
    }

    private void validarNumeroRecepcao() throws Exception{
        validarNaoVazio(edtNumRecepcao);
    }

    private void validarNaoVazio(EditText editText) throws Exception{
        if (editText.getText().toString().isEmpty()){
            throw new Exception("Preencha todos os dados (" + editText.getHint().toString() + ")");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_controle_de_estoque_cdhortifruti, menu);
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
            Intent i = new Intent(this, ActivityRelatorioCDHorifruti.class);
            i.putExtra("Nome", getIntent().getStringExtra("Nome"));
            i.putExtra("cpf", getIntent().getStringExtra("cpf"));
            startActivity(i);
        }
        if (id == R.id.action_testes) {
            //Intent i = new Intent(this, )
        }

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


        return super.onOptionsItemSelected(item);
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
