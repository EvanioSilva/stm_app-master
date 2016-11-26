package com.rastreabilidadeInterna.fracionamento;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityHistoricoFracionamento extends Activity {

    private EditText edittext;
    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_historico_fracionamento);

        setupDatePicker();

    }

    private void setupDatePicker(){

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
                new DatePickerDialog(ActivityHistoricoFracionamento.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel(){

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));

        Date date = myCalendar.getTime();
        executarConsulta(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_historico_fracionamento, menu);
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

    private void executarConsulta(Date date){
        Repositorio repositorio = new Repositorio(this);
        List<objetoFracionamento> fracionamentos = repositorio.listarFracionamentos(new SimpleDateFormat("dd/MM/yyyy").format(date));
        ArrayList<String> strings = new ArrayList<String>();
        int sdk = android.os.Build.VERSION.SDK_INT;

        objetoFracionamento objetoFracionamentoAnterior = new objetoFracionamento(0, "", "", "", "", 0, "", "", this);

        TextView quantidadeAtualTv = new TextView(this);


        int quantidadeAtual = 1;

        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.LinearLayoutHistorico);
        linearLayout1.removeAllViews();

        for (objetoFracionamento objetoFracionamento : fracionamentos){

            if (objetoFracionamentoAnterior.tipoDoProduto.equals(objetoFracionamento.tipoDoProduto)){

                quantidadeAtual++;
                quantidadeAtualTv.setText(Integer.toString(quantidadeAtual));

            } else {

                quantidadeAtualTv = new TextView(this);
                quantidadeAtualTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    quantidadeAtualTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    quantidadeAtualTv.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }
                quantidadeAtualTv.setPadding(5, 5, 5, 5);

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                TextView textViewNome = new TextView(this);
                textViewNome.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textViewNome.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textViewNome.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }
                textViewNome.setPadding(5, 5, 5, 5);

                TextView textViewFab = new TextView(this);
                textViewFab.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textViewFab.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textViewFab.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }
                textViewFab.setPadding(5, 5, 5, 5);

                TextView textViewSif = new TextView(this);
                textViewSif.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textViewSif.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textViewSif.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                textViewSif.setPadding(5, 5, 5, 5);

                TextView textViewLote = new TextView(this);
                textViewLote.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textViewLote.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textViewLote.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }
                textViewLote.setPadding(5, 5, 5, 5);

                TextView textViewDataFab = new TextView(this);
                textViewDataFab.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textViewDataFab.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textViewDataFab.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                textViewDataFab.setPadding(5, 5, 5, 5);

                TextView textViewDataVal = new TextView(this);
                textViewDataVal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textViewDataVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textViewDataVal.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                textViewDataVal.setPadding(5, 5, 5, 5);

                TextView textViewSelo = new TextView(this);
                textViewSelo.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    textViewSelo.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    textViewSelo.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                textViewSelo.setPadding(5, 5, 5, 5);

                String[] results;
                if (
                        objetoFracionamento.novoSelo.length() == 15
                                &&
                                (!objetoFracionamento.novoSelo.
                                        substring(objetoFracionamento.novoSelo.length() - 6, objetoFracionamento.novoSelo.length() - 5)
                                        .equals("9"))) {
                    results = repositorio.listarIdxBaixadoHistorico(objetoFracionamento.novoSelo.substring(9));
                    textViewSelo.setText(objetoFracionamento.novoSelo.substring(9));
                } else {
                    results = repositorio.listarIdxBaixadoHistorico(objetoFracionamento.seloSafe);
                    textViewSelo.setText(objetoFracionamento.novoSelo.substring(objetoFracionamento.novoSelo.length() - 6));
                }

                Log.i("OBJETO", objetoFracionamento.toString());
                Log.i("RESULT", results.toString());

                if (results[0] != null){
                    if (results[0].isEmpty()){
                        textViewNome.setText(objetoFracionamento.tipoDoProduto);
                    }else {
                        textViewNome.setText(results[0]);
                    }
                } else {
                    textViewNome.setText(objetoFracionamento.tipoDoProduto);
                }

                if (results[1] != null) {
                    if (results[1].isEmpty()) {
                        textViewFab.setText(objetoFracionamento.fabricante);
                    } else {
                        textViewFab.setText(results[1]);
                    }
                }else {
                    textViewFab.setText(objetoFracionamento.fabricante);
                }

                if (results[2] != null) {
                    if (results[2].isEmpty()) {
                        textViewSif.setText(objetoFracionamento.sif);
                    } else {
                        textViewSif.setText(results[2]);
                    }
                }else {
                    textViewSif.setText(objetoFracionamento.sif);
                }

                if (results[3] != null) {
                    if (results[3].isEmpty()) {
                        textViewLote.setText(objetoFracionamento.lote);
                    } else {
                        textViewLote.setText(results[3]);
                    }
                } else {
                    textViewLote.setText(objetoFracionamento.lote);
                }

                if (results[4] != null) {
                    if (results[4].isEmpty()) {
                        textViewDataFab.setText(objetoFracionamento.dataFabricacao);
                    } else {
                        textViewDataFab.setText(results[4]);
                    }
                } else {
                    textViewDataFab.setText(objetoFracionamento.dataFabricacao);
                }

                if (results[5] != null) {
                    if (results[5].isEmpty()) {
                        textViewDataVal.setText(objetoFracionamento.dataDeValidade);
                    } else {
                        textViewDataVal.setText(results[5]);
                    }
                } else {
                    textViewDataVal.setText(objetoFracionamento.dataDeValidade);
                }

                linearLayout.addView(textViewSelo);
                linearLayout.addView(textViewNome);
                linearLayout.addView(textViewFab);
                linearLayout.addView(textViewSif);
                linearLayout.addView(textViewLote);
                linearLayout.addView(textViewDataFab);
                linearLayout.addView(textViewDataVal);
                linearLayout.addView(quantidadeAtualTv);

                linearLayout1.addView(linearLayout);

                quantidadeAtual = 1;
                quantidadeAtualTv.setText(Integer.toString(quantidadeAtual));
            }

            objetoFracionamentoAnterior = objetoFracionamento;

        }
    }
}