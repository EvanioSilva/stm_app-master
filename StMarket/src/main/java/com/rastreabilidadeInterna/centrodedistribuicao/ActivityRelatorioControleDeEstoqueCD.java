package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityRelatorioControleDeEstoqueCD extends Activity {

    private EditText edittext;
    private Calendar myCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_relatorio_controle_de_estoque_cd);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        executarConsulta(new Date());

        setupDatePicker();


    }

    private void executarConsulta(Date date){
        Repositorio repositorio = new Repositorio(this);
        ArrayList<ModelRecepcao> modelRecepcaoArrayList = repositorio.recoverAllRecepcao(new SimpleDateFormat("dd/MM/yyyy").format(date));

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutHistorico);
        linearLayout.removeAllViews();

        if (modelRecepcaoArrayList != null){
        for (ModelRecepcao modelRecepcao : modelRecepcaoArrayList) {

            Log.i("recepcao", modelRecepcao.toString());

            LinearLayout linearLayout1 = new LinearLayout(this);
            linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
            } else {
                linearLayout1.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
            }


            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
            textView1.setText(modelRecepcao.getNumeroDaRecepcao());
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                textView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
            } else {
                textView1.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
            }

            textView1.setGravity(Gravity.CENTER);

            TextView textView2 = new TextView(this);
            textView2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
            textView2.setText(modelRecepcao.getPlacaDoCaminhao());
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                textView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
            } else {
                textView2.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
            }

            textView2.setGravity(Gravity.CENTER);

            LinearLayout linearLayout2 = new LinearLayout(this);
            linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 10.0));
            linearLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
            linearLayout2.setOrientation(LinearLayout.VERTICAL);

            for (ModelProdutoRecebido modelProdutoRecebido : modelRecepcao.getProdutosRecebidos()) {

                Log.i("recepcao", modelProdutoRecebido.toString());

                LinearLayout linearLayout3 = new LinearLayout(this);
                linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    linearLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    linearLayout3.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                TextView tvNome = new TextView(this);
                tvNome.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvNome.setText(modelProdutoRecebido.getNome());
                tvNome.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvNome.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvNome.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                TextView tvMarca = new TextView(this);
                tvMarca.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvMarca.setText(modelProdutoRecebido.getMarca());
                tvMarca.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvMarca.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvMarca.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                TextView tvForn = new TextView(this);
                tvForn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvForn.setText(modelProdutoRecebido.getFornecedor());
                tvForn.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvForn.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvForn.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                TextView tvSet = new TextView(this);
                tvSet.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvSet.setText(modelProdutoRecebido.getSetor());
                tvSet.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvSet.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvSet.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }

                TextView tvSif = new TextView(this);
                tvSif.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvSif.setText(modelProdutoRecebido.getSif());
                tvSif.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvSif.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvSif.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }


                TextView tvDFab = new TextView(this);
                tvDFab.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvDFab.setText(modelProdutoRecebido.getDataFabricacao());
                tvDFab.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvDFab.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvDFab.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }


                TextView tvDVal = new TextView(this);
                tvDVal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvDVal.setText(modelProdutoRecebido.getDataValidade());
                tvDVal.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvDVal.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvDVal.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }


                TextView tvPal = new TextView(this);
                tvPal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvPal.setText(Integer.toString(modelProdutoRecebido.getTotalPalets()));
                tvPal.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvPal.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvPal.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }


                TextView tvConc = new TextView(this);
                tvConc.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvConc.setText(modelProdutoRecebido.getConclusao());
                tvConc.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvConc.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvConc.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }


                TextView tvCxs = new TextView(this);
                tvCxs.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvCxs.setText(Integer.toString(modelProdutoRecebido.getTotalCaixas()));
                tvCxs.setGravity(Gravity.CENTER);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    tvCxs.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_relatorio));
                } else {
                    tvCxs.setBackground(getResources().getDrawable(R.drawable.border_relatorio));
                }


                linearLayout3.addView(tvNome);
                linearLayout3.addView(tvMarca);
                linearLayout3.addView(tvForn);
                linearLayout3.addView(tvSet);
                linearLayout3.addView(tvSif);
                linearLayout3.addView(tvDFab);
                linearLayout3.addView(tvDVal);
                linearLayout3.addView(tvPal);
                linearLayout3.addView(tvCxs);
                linearLayout3.addView(tvConc);


                linearLayout2.addView(linearLayout3);

            }

            linearLayout1.addView(textView1);
            linearLayout1.addView(textView2);
            linearLayout1.addView(linearLayout2);

            linearLayout.addView(linearLayout1);


        }
        }

        /*
        if (modelRecepcaoArrayList != null) {
            for (ModelRecepcao modelRecepcao : modelRecepcaoArrayList) {
                LinearLayout llRec = new LinearLayout(this);
                llRec.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                TextView tvRec = new TextView(this);
                tvRec.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvRec.setText(modelRecepcao.getNumeroDaRecepcao());
                tvRec.setGravity(Gravity.CENTER);
                tvRec.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                llRec.addView(tvRec);

                TextView tvCam = new TextView(this);
                tvCam.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                tvCam.setText(modelRecepcao.getPlacaDoCaminhao());
                tvCam.setGravity(Gravity.CENTER);
                tvCam.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                llRec.addView(tvCam);

                LinearLayout llProds = new LinearLayout(this);
                llProds.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 10.0));
                llProds.setOrientation(LinearLayout.VERTICAL);

                for (ModelProdutoRecebido modelProdutoRecebido : modelRecepcao.getProdutosRecebidos()){
                    LinearLayout llProd = new LinearLayout(this);
                    llProd.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    TextView tvNome = new TextView(this);
                    tvNome.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvNome.setText(modelProdutoRecebido.getNome());
                    tvNome.setGravity(Gravity.CENTER);
                    tvNome.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvMarca = new TextView(this);
                    tvMarca.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvMarca.setText(modelProdutoRecebido.getMarca());
                    tvMarca.setGravity(Gravity.CENTER);
                    tvMarca.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvForn = new TextView(this);
                    tvForn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvForn.setText(modelProdutoRecebido.getFornecedor());
                    tvForn.setGravity(Gravity.CENTER);
                    tvForn.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvSet = new TextView(this);
                    tvSet.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvSet.setText(modelProdutoRecebido.getSetor());
                    tvSet.setGravity(Gravity.CENTER);
                    tvSet.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvSif = new TextView(this);
                    tvSif.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvSif.setText(modelProdutoRecebido.getSif());
                    tvSif.setGravity(Gravity.CENTER);
                    tvSif.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvDFab = new TextView(this);
                    tvDFab.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvDFab.setText(modelProdutoRecebido.getDataFabricacao());
                    tvDFab.setGravity(Gravity.CENTER);
                    tvDFab.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvDVal = new TextView(this);
                    tvDVal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvDVal.setText(modelProdutoRecebido.getDataValidade());
                    tvDVal.setGravity(Gravity.CENTER);
                    tvDVal.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvPal = new TextView(this);
                    tvPal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvPal.setText(Integer.toString(modelProdutoRecebido.getTotalPalets()));
                    tvPal.setGravity(Gravity.CENTER);
                    tvPal.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvConc = new TextView(this);
                    tvConc.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvConc.setText(modelProdutoRecebido.getConclusao());
                    tvConc.setGravity(Gravity.CENTER);
                    tvConc.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    TextView tvCxs = new TextView(this);
                    tvCxs.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float) 1.0));
                    tvCxs.setText(Integer.toString(modelProdutoRecebido.getTotalCaixas()));
                    tvCxs.setGravity(Gravity.CENTER);
                    tvCxs.setBackgroundDrawable( getResources().getDrawable(R.drawable.border_relatorio) );

                    llProd.addView(tvNome);
                    llProd.addView(tvMarca);
                    llProd.addView(tvForn);
                    llProd.addView(tvSet);
                    llProd.addView(tvSif);
                    llProd.addView(tvDFab);
                    llProd.addView(tvDVal);
                    llProd.addView(tvPal);
                    llProd.addView(tvCxs);
                    llProd.addView(tvConc);

                    llRec.addView(llProd);

                }

                llRec.addView(llProds);

                linearLayout.addView(llRec);
            }
        }*/
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
                new DatePickerDialog(ActivityRelatorioControleDeEstoqueCD.this, date, myCalendar
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
        getMenuInflater().inflate(R.menu.menu_activity_relatorio_controle_de_estoque_cd, menu);
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
