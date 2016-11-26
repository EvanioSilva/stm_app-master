package com.rastreabilidadeInterna.preparacao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.rastreabilidadeInterna.controleEstoque.ObjetoHistoricoControleEstoque;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.helpers.HistoricoXMLController;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityRelatorioPreparacao extends Activity {

    private EditText edittext;
    private Calendar myCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_relatorio_preparacao);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        executarConsulta(new Date());

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
                new DatePickerDialog(ActivityRelatorioPreparacao.this, date, myCalendar
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

    private void executarConsulta(Date date){
        HistoricoXMLController historicoXMLController = new HistoricoXMLController(
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", ""),
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", ""),
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", ""),
                date,
                HistoricoXMLController.TYPE_PR);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutHistorico);

        linearLayout.removeAllViews();

        for (ObjetoHistoricoPreparacao objetoHistoricoPreparacao : historicoXMLController.listaDeObjetosPreparacao){
            LinearLayout linearLayout1 = new LinearLayout(this);
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout1.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                linearLayout1.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }

            LinearLayout linearLayout2 = new LinearLayout(this);
            linearLayout2.setOrientation(LinearLayout.VERTICAL);
            linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5));
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout2.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                linearLayout2.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }

            TextView tvCodReceita = new TextView(this);
            tvCodReceita.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            tvCodReceita.setText(objetoHistoricoPreparacao.codigoReceita);
            tvCodReceita.setGravity(Gravity.CENTER);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvCodReceita.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                tvCodReceita.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }
            linearLayout1.addView(tvCodReceita);

            TextView tvNomeReceita = new TextView(this);
            tvNomeReceita.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            tvNomeReceita.setText(objetoHistoricoPreparacao.nomeReceita);
            tvNomeReceita.setGravity(Gravity.CENTER);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvNomeReceita.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                tvNomeReceita.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }
            linearLayout1.addView(tvNomeReceita);

            linearLayout1.addView(linearLayout2);

            for (ObjetoHistoricoPreparacao.Ingrediente ingrediente : objetoHistoricoPreparacao.ingredientesReceita){
                LinearLayout linearLayout3 = new LinearLayout(this);
                linearLayout3.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    linearLayout3.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
                } else {
                    linearLayout3.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
                }

                TextView tvCodIng = new TextView(this);
                tvCodIng.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                tvCodIng.setText(ingrediente.codigoIngrediente);
                tvCodIng.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvCodIng.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
                } else {
                    tvCodIng.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
                }
                linearLayout3.addView(tvCodIng);

                TextView tvNomeIng = new TextView(this);
                tvNomeIng.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                tvNomeIng.setText(ingrediente.nomeIngrediente);
                tvNomeIng.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvNomeIng.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
                } else {
                    tvNomeIng.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
                }
                linearLayout3.addView(tvNomeIng);

                TextView tvFabIng = new TextView(this);
                tvFabIng.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                tvFabIng.setText(ingrediente.dataFabIngrediente);
                tvFabIng.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvFabIng.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
                } else {
                    tvFabIng.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
                }
                linearLayout3.addView(tvFabIng);

                TextView tvValIng = new TextView(this);
                tvValIng.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                tvValIng.setText(ingrediente.dataValIngrediente);
                tvValIng.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvValIng.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
                } else {
                    tvValIng.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
                }
                linearLayout3.addView(tvValIng);

                TextView tvLoteIng = new TextView(this);
                tvLoteIng.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                tvLoteIng.setText(ingrediente.loteIngrediente);
                tvLoteIng.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvLoteIng.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
                } else {
                    tvLoteIng.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
                }
                linearLayout3.addView(tvLoteIng);


                linearLayout2.addView(linearLayout3);

            }

            linearLayout.addView(linearLayout1);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_relatorio_preparacao, menu);
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
    }
}
