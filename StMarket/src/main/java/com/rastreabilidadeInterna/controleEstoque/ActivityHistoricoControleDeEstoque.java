package com.rastreabilidadeInterna.controleEstoque;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.helpers.HistoricoXMLController;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityHistoricoControleDeEstoque extends Activity {

    private EditText edittext;
    private Calendar myCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_historico_controle_de_estoque);

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
                new DatePickerDialog(ActivityHistoricoControleDeEstoque.this, date, myCalendar
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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutHistorico);

        linearLayout.removeAllViews();

        HistoricoXMLController historicoXMLController = new HistoricoXMLController(
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", ""),
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", ""),
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", ""),
                date,
                HistoricoXMLController.TYPE_CE);

        for (ObjetoHistoricoControleEstoque objetoHistoricoControleEstoque : historicoXMLController.listaDeObjetosDeControleDeEstoque){
            LinearLayout linearLayout1 = new LinearLayout(this);
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout1.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                linearLayout1.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }

            TextView tvProduto = new TextView(this);
            tvProduto.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.5));
            tvProduto.setText(objetoHistoricoControleEstoque.getNomeProduto());
            tvProduto.setGravity(Gravity.CENTER);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvProduto.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                tvProduto.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }
            linearLayout1.addView(tvProduto);

            TextView tvDataFab = new TextView(this);
            tvDataFab.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.5));
            tvDataFab.setText(new SimpleDateFormat("dd/MM/yyyy").format(objetoHistoricoControleEstoque.getDataFabricacao()));
            tvDataFab.setGravity(Gravity.CENTER);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvDataFab.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                tvDataFab.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }
            linearLayout1.addView(tvDataFab);

            TextView tvDataVal = new TextView(this);
            tvDataVal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.5));
            tvDataVal.setText(new SimpleDateFormat("dd/MM/yyyy").format(objetoHistoricoControleEstoque.getDataValidade()));
            tvDataVal.setGravity(Gravity.CENTER);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvDataVal.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                tvDataVal.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }
            linearLayout1.addView(tvDataVal);

            TextView tvFabricante = new TextView(this);
            tvFabricante.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            tvFabricante.setText(objetoHistoricoControleEstoque.getNomeFabricante());
            tvFabricante.setGravity(Gravity.CENTER);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvFabricante.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                tvFabricante.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }
            linearLayout1.addView(tvFabricante);

            TextView tvQuantidade= new TextView(this);
            tvQuantidade.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            tvQuantidade.setText(Integer.toString(objetoHistoricoControleEstoque.getTotalPecas()));
            tvQuantidade.setGravity(Gravity.CENTER);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvQuantidade.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );
            } else {
                tvQuantidade.setBackground( getResources().getDrawable(R.drawable.border_relatorio));
            }
            linearLayout1.addView(tvQuantidade);


            linearLayout.addView(linearLayout1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_historico_controle_de_estoque, menu);
        return true;
    }

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
}
