package com.rastreabilidadeInterna.centrodedistribuicao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ActivityRelatorioCDHorifruti extends Activity {

    private EditText edittext;
    private Calendar myCalendar;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_relatorio_cdhorifruti);

        setupDatePicker();
        setupSpinner();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        executarConsulta(new Date(), "Todos", false);
    }

    private void setupSpinner() {
        spinner = (Spinner) findViewById(R.id.spinnerFornecedor);

        Iterator<ModelProdutoRecebidoHortifruti> modelProdutoRecebidoHortifrutiIterator =
                ModelProdutoHortifruti.findAll(ModelProdutoRecebidoHortifruti.class);

        ArrayList<String> fornecedores = new ArrayList<String>();
        fornecedores.add("Todos");

        while (modelProdutoRecebidoHortifrutiIterator.hasNext()) {
            ModelProdutoRecebidoHortifruti model = modelProdutoRecebidoHortifrutiIterator.next();
            fornecedores.add(model.getFornecedor());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fornecedores);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (edittext.getText().toString().isEmpty()) {
                    executarConsulta(myCalendar.getTime(), spinner.getSelectedItem().toString(), true);
                } else {
                    executarConsulta(myCalendar.getTime(), spinner.getSelectedItem().toString(), false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (edittext.getText().toString().isEmpty()) {
                    executarConsulta(myCalendar.getTime(), "Todos", true);
                } else {
                    executarConsulta(myCalendar.getTime(), "Todos", false);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_relatorio_cdhorifruti, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
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

    private void setupDatePicker() {

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
                new DatePickerDialog(ActivityRelatorioCDHorifruti.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));

        Date date = myCalendar.getTime();
        executarConsulta(date, spinner.getSelectedItem().toString(), false);
    }

    private void executarConsulta(Date date, String fornecedor, boolean ignoreDate) {
        LinearLayout fatherLayout = (LinearLayout) findViewById(R.id.LinearLayoutHistorico);
        fatherLayout.removeAllViews();

        List<ModelRecepcaoHortifruti> modelRecepcaoHortifrutiList = null;

        if (!ignoreDate) {
            modelRecepcaoHortifrutiList = ModelRecepcaoHortifruti.find(
                    ModelRecepcaoHortifruti.class,
                    "data_recepcao = ?",
                    new SimpleDateFormat("dd/MM/yyyy").format(date)
            );
        } else {
            modelRecepcaoHortifrutiList = ModelRecepcaoHortifruti.listAll(
                    ModelRecepcaoHortifruti.class
            );
        }

        double totalRecebido = 0;
        double totalDevolvido = 0;

        for (ModelRecepcaoHortifruti modelRecepcaoHortifruti : modelRecepcaoHortifrutiList) {
            List<ModelProdutoRecebidoHortifruti> modelProdutoRecebidoHortifrutiList = null;
            if (fornecedor.equals("Todos")) {
                modelProdutoRecebidoHortifrutiList =
                        ModelProdutoRecebidoHortifruti.find(
                                ModelProdutoRecebidoHortifruti.class,
                                "model_recepcao_hortifruti = ?",
                                Long.toString(modelRecepcaoHortifruti.getId())
                        );
            } else {
                modelProdutoRecebidoHortifrutiList =
                        ModelProdutoRecebidoHortifruti.find(
                                ModelProdutoRecebidoHortifruti.class,
                                "model_recepcao_hortifruti = ? and fornecedor = '" + fornecedor + "'",
                                Long.toString(modelRecepcaoHortifruti.getId())
                        );
            }


            for (ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti : modelProdutoRecebidoHortifrutiList) {
                totalRecebido += modelProdutoRecebidoHortifruti.getVolumeEntregueKg();
                totalDevolvido += modelProdutoRecebidoHortifruti.getVolumeDevolvidoKg();

                LinearLayout linearLayout = new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams layoutParamsChild = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

                if (modelProdutoRecebidoHortifruti.getParecerFinalDoCQ() != null) {

                    if (modelProdutoRecebidoHortifruti.getParecerFinalDoCQ().contains("Devolução Total")) {
                        linearLayout.setBackgroundColor(Color.rgb(244, 67, 54));
                    } else if (modelProdutoRecebidoHortifruti.getParecerFinalDoCQ().contains("Recebido c/ Restrição")) {
                        linearLayout.setBackgroundColor(Color.rgb(33, 150, 243));
                    } else if (modelProdutoRecebidoHortifruti.getParecerFinalDoCQ().contains("Bloqueado")) {
                        linearLayout.setBackgroundColor(Color.rgb(255, 152, 0));
                    }
                    if (modelRecepcaoHortifruti.getNumeroBonoRomaneio().equals("#GAMBI#")) {
                        // Nao tem nenhum, obter os 2
                        Button button = new Button(this);
                        button.setText("Informar Bono");
                        button.setLayoutParams(layoutParamsChild);
                        button.setTag(Long.toString(modelRecepcaoHortifruti.getId()));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adicionarBono(Long.parseLong(view.getTag().toString()));
                            }
                        });
                        linearLayout.addView(button);

                        Button button2 = new Button(this);
                        button2.setText("Informar Placa");
                        button2.setLayoutParams(layoutParamsChild);
                        button2.setTag(Long.toString(modelRecepcaoHortifruti.getId()));
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adicionarPlaca(Long.parseLong(view.getTag().toString()));
                            }
                        });
                        linearLayout.addView(button2);
                    } else {
                        try {
                            if (modelRecepcaoHortifruti.getNumeroBonoRomaneio().split("#GAMBI#")[0].equals("")) {
                                Button button = new Button(this);
                                button.setText("Informar Bono");
                                button.setLayoutParams(layoutParamsChild);
                                button.setTag(Long.toString(modelRecepcaoHortifruti.getId()));
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        adicionarBono(Long.parseLong(view.getTag().toString()));
                                    }
                                });
                                linearLayout.addView(button);
                            } else {
                                TextView tvBono = new TextView(this);
                                tvBono.setText(modelRecepcaoHortifruti.getNumeroBonoRomaneio().split("#GAMBI#")[0]);
                                tvBono.setLayoutParams(layoutParamsChild);
                                tvBono.setGravity(Gravity.CENTER);

                                linearLayout.addView(tvBono);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        try {
                            if (modelRecepcaoHortifruti.getNumeroBonoRomaneio().split("#GAMBI#").length == 1) {
                                Button button = new Button(this);
                                button.setText("Informar Placa");
                                button.setLayoutParams(layoutParamsChild);
                                button.setTag(Long.toString(modelRecepcaoHortifruti.getId()));
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        adicionarPlaca(Long.parseLong(view.getTag().toString()));
                                    }
                                });
                                linearLayout.addView(button);
                            } else {
                                TextView tvBono = new TextView(this);
                                tvBono.setText(modelRecepcaoHortifruti.getNumeroBonoRomaneio().split("#GAMBI#")[1]);
                                tvBono.setLayoutParams(layoutParamsChild);
                                tvBono.setGravity(Gravity.CENTER);

                                linearLayout.addView(tvBono);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }


                    TextView tvNome = new TextView(this);
                    tvNome.setText(modelProdutoRecebidoHortifruti.getNomeDoProduto());
                    tvNome.setLayoutParams(layoutParamsChild);
                    tvNome.setGravity(Gravity.CENTER);
                    linearLayout.addView(tvNome);

                    TextView tvFabr = new TextView(this);
                    tvFabr.setText(modelProdutoRecebidoHortifruti.getFornecedor());
                    tvFabr.setLayoutParams(layoutParamsChild);
                    tvFabr.setGravity(Gravity.CENTER);
                    linearLayout.addView(tvFabr);

                    if (modelProdutoRecebidoHortifruti.getTotalEntregueEmCaixas() == -1) {
                        Button button2 = new Button(this);
                        button2.setText("Informar");
                        button2.setLayoutParams(layoutParamsChild);
                        button2.setTag(Long.toString(modelProdutoRecebidoHortifruti.getId()));
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adicionarPesoTotal(Long.parseLong(view.getTag().toString()));
                            }
                        });
                        linearLayout.addView(button2);
                    } else {
                        TextView tvQtKg = new TextView(this);
                        tvQtKg.setText(Double.toString(modelProdutoRecebidoHortifruti.getTotalEntregueEmCaixas()));
                        tvQtKg.setLayoutParams(layoutParamsChild);
                        tvQtKg.setGravity(Gravity.CENTER);
                        linearLayout.addView(tvQtKg);
                    }

                    TextView tvTotal = new TextView(this);
                    tvTotal.setText(Double.toString(modelProdutoRecebidoHortifruti.getPorcentagemProdutosRecebidos()));
                    tvTotal.setLayoutParams(layoutParamsChild);
                    tvTotal.setGravity(Gravity.CENTER);
                    linearLayout.addView(tvTotal);

                    TextView tvTotalR = new TextView(this);
                    tvTotalR.setText(Double.toString(100 - modelProdutoRecebidoHortifruti.getPorcentagemProdutosRecebidos()));
                    tvTotalR.setLayoutParams(layoutParamsChild);
                    tvTotalR.setGravity(Gravity.CENTER);
                    linearLayout.addView(tvTotalR);

                    if (modelProdutoRecebidoHortifruti.getParecerFinalDoCQ().isEmpty()) {
                        Button button3 = new Button(this);
                        button3.setText("Informar");
                        button3.setLayoutParams(layoutParamsChild);
                        button3.setTag(Long.toString(modelProdutoRecebidoHortifruti.getId()));
                        button3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adicionarParecerFinal(Long.parseLong(view.getTag().toString()));
                            }
                        });
                        linearLayout.addView(button3);
                    } else {
                        TextView tvRes = new TextView(this);
                        tvRes.setText(modelProdutoRecebidoHortifruti.getParecerFinalDoCQ());
                        tvRes.setLayoutParams(layoutParamsChild);
                        tvRes.setGravity(Gravity.CENTER);
                        linearLayout.addView(tvRes);
                    }

                    fatherLayout.addView(linearLayout);
                }

            }


            double porcentagemTotalEntregue = (totalRecebido - totalDevolvido) / totalRecebido * 100;
            double porcentagemTotalDevolvido = totalDevolvido / totalRecebido * 100;

            TextView textViewRecebido = (TextView) findViewById(R.id.porcentagemTotalRecebida);
            textViewRecebido.setText(new DecimalFormat("###.##").format(porcentagemTotalEntregue) + "%");

            TextView textViewDevolvido = (TextView) findViewById(R.id.porcentagemTotalDevolvida);
            textViewDevolvido.setText(new DecimalFormat("###.##").format(porcentagemTotalDevolvido) + "%");

        }
    }

    private void adicionarBono(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bono/Romaneio");
        builder.setMessage("Informe o código Bono/Romaneio");

        final long Id = id;

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.dialog_relatorio_cd_hortifruti, null);
        final TextView label = (TextView) linearLayout.findViewById(R.id.textViewLabel);
        final EditText edit = (EditText) linearLayout.findViewById(R.id.editTextInfo);
        edit.setHint("Numero Bono/Romaneio");

        label.setText("Numero Bono/Romaneio");

        builder.setView(linearLayout);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edit.getText().toString().isEmpty()) {
                    Toast.makeText(
                            ActivityRelatorioCDHorifruti.this,
                            "Insira um numero válido",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    ModelRecepcaoHortifruti modelRecepcaoHortifruti =
                            ModelRecepcaoHortifruti.findById(ModelRecepcaoHortifruti.class, Id);

                    String bonoAtual = modelRecepcaoHortifruti.getNumeroBonoRomaneio();

                    String placa = "";

                    try {
                        placa = bonoAtual.split("#GAMBI#")[1];
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    modelRecepcaoHortifruti.setNumeroBonoRomaneio(edit.getText().toString() + "#GAMBI#" + placa);
                    modelRecepcaoHortifruti.save();
                    verificarArquivamentoBono(Id);
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void adicionarPlaca(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Placa do Caminhão");
        builder.setMessage("Informe a Placa do Caminhão");

        final long Id = id;

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.dialog_relatorio_cd_hortifruti, null);
        final TextView label = (TextView) linearLayout.findViewById(R.id.textViewLabel);
        final EditText edit = (EditText) linearLayout.findViewById(R.id.editTextInfo);
        edit.setHint("Placa do Caminhão");

        label.setText("Placa do Caminhão");

        builder.setView(linearLayout);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edit.getText().toString().isEmpty()) {
                    Toast.makeText(
                            ActivityRelatorioCDHorifruti.this,
                            "Insira um numero válido",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    ModelRecepcaoHortifruti modelRecepcaoHortifruti =
                            ModelRecepcaoHortifruti.findById(ModelRecepcaoHortifruti.class, Id);

                    String bonoAtual = modelRecepcaoHortifruti.getNumeroBonoRomaneio();

                    String bono = "";

                    try {
                        bono = bonoAtual.split("#GAMBI#")[0];
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    modelRecepcaoHortifruti.setNumeroBonoRomaneio(bono + "#GAMBI#" + edit.getText().toString());
                    modelRecepcaoHortifruti.save();
                    verificarArquivamentoBono(Id);
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void adicionarPesoTotal(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total Recebido");
        builder.setMessage("Informe o peso total recebido");

        final long Id = id;

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.dialog_relatorio_cd_hortifruti, null);
        final TextView label = (TextView) linearLayout.findViewById(R.id.textViewLabel);
        final EditText edit = (EditText) linearLayout.findViewById(R.id.editTextInfo);

        edit.setHint("Peso total recebido em Kg");
        edit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        label.setText("Peso total recebido em Kg");

        builder.setView(linearLayout);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edit.getText().toString().isEmpty()) {
                    Toast.makeText(
                            ActivityRelatorioCDHorifruti.this,
                            "Insira um numero válido",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti =
                            ModelProdutoRecebidoHortifruti.findById(ModelProdutoRecebidoHortifruti.class,
                                    Id);

                    modelProdutoRecebidoHortifruti.setVolumeEntregueKg(
                            Double.parseDouble(edit.getText().toString()));

                    modelProdutoRecebidoHortifruti.setPorcentagemProdutosRecebidos(
                            (1 - (modelProdutoRecebidoHortifruti.getVolumeDevolvidoKg() /
                                    modelProdutoRecebidoHortifruti.getVolumeEntregueKg())) * 100
                    );

                    modelProdutoRecebidoHortifruti.save();
                    verificarArquivamento(Id, -1);
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void adicionarParecerFinal(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total Recebido");
        builder.setMessage("Informe o Parecer Final");

        final long Id = id;

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.dialog_relatorio_cd_hortifruti, null);
        final TextView label = (TextView) linearLayout.findViewById(R.id.textViewLabel);
        final EditText edit = (EditText) linearLayout.findViewById(R.id.editTextInfo);

        edit.setHint("EM DESENVOLVIMENTO");
        edit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        label.setText("EM DESENVOLVIMENTO");

        builder.setView(linearLayout);

        /*
        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edit.getText().toString().isEmpty()) {
                    Toast.makeText(
                            ActivityRelatorioCDHorifruti.this,
                            "Insira um numero válido",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti =
                            ModelProdutoRecebidoHortifruti.findById(ModelProdutoRecebidoHortifruti.class,
                                    Id);

                    modelProdutoRecebidoHortifruti.setVolumeEntregueKg(
                            Double.parseDouble(edit.getText().toString()));

                    modelProdutoRecebidoHortifruti.setPorcentagemProdutosRecebidos(
                            (1 - (modelProdutoRecebidoHortifruti.getVolumeDevolvidoKg() /
                                    modelProdutoRecebidoHortifruti.getVolumeEntregueKg())) * 100
                    );

                    modelProdutoRecebidoHortifruti.save();
                    verificarArquivamento(Id, -1);
                }
            }
        });
        */
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void adicionarTotalCaixas(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total De Caixas");
        builder.setMessage("Informe o numero de caixas recebidas");

        final long Id = id;

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.dialog_relatorio_cd_hortifruti, null);
        final TextView label = (TextView) linearLayout.findViewById(R.id.textViewLabel);
        final EditText edit = (EditText) linearLayout.findViewById(R.id.editTextInfo);

        edit.setHint("Quantidade de Caixas Recebidas");
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);

        label.setText("Quantidade de Caixas Recebidas");

        builder.setView(linearLayout);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edit.getText().toString().isEmpty()) {
                    Toast.makeText(
                            ActivityRelatorioCDHorifruti.this,
                            "Insira um numero válido",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti =
                            ModelProdutoRecebidoHortifruti.findById(ModelProdutoRecebidoHortifruti.class,
                                    Id);
                    modelProdutoRecebidoHortifruti.setTotalEntregueEmCaixas(
                            Integer.parseInt(edit.getText().toString())
                    );

                    List<ModelProdutoHortifruti> modelProdutoHortifrutis =
                            ModelProdutoHortifruti.find(
                                    ModelProdutoHortifruti.class,
                                    "nome_produto = ?",
                                    modelProdutoRecebidoHortifruti.getNomeDoProduto()
                            );

                    for (ModelProdutoHortifruti modelProdutoHortifruti : modelProdutoHortifrutis) {
                        modelProdutoRecebidoHortifruti.setVolumeEntregueKg(
                                modelProdutoHortifruti.pesoProduto *
                                        modelProdutoRecebidoHortifruti.getTotalEntregueEmCaixas() /
                                        1000
                        );

                    }

                    modelProdutoRecebidoHortifruti.setPorcentagemProdutosRecebidos(
                            (1 - (modelProdutoRecebidoHortifruti.getVolumeDevolvidoKg() /
                                    modelProdutoRecebidoHortifruti.getVolumeEntregueKg())) * 100
                    );

                    modelProdutoRecebidoHortifruti.save();
                    verificarArquivamento(Id, -1);
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void verificarArquivamento(long id, int contador) {
        ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti =
                ModelProdutoRecebidoHortifruti.findById(ModelProdutoRecebidoHortifruti.class, id);

        if (modelProdutoRecebidoHortifruti.getTotalEntregueEmCaixas() != -1
                && modelProdutoRecebidoHortifruti.getVolumeEntregueKg() != -1
                && !modelProdutoRecebidoHortifruti.getModelRecepcaoHortifruti().
                getNumeroBonoRomaneio().isEmpty()) {
            reescreverArquivo(modelProdutoRecebidoHortifruti, contador);
            Log.i("produto completo", modelProdutoRecebidoHortifruti.toString());
        } else {
            Log.i("produto incompleto", modelProdutoRecebidoHortifruti.toString());
        }

        Date date = myCalendar.getTime();
        executarConsulta(date, spinner.getSelectedItem().toString(), false);

    }

    private void reescreverArquivo(ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti, int contador) {
        verificarPasta();

        String filenameA;

        if (contador > -1) {
            filenameA = fileNameA(new Date()) + "_" + contador;
        } else {
            filenameA = fileNameA(new Date());
        }

        try {

            deleteFile(filenameA);

            File arquivoA = gerarFile(filenameA);

            FileOutputStream fileOutputStreamA = new FileOutputStream(arquivoA);
            OutputStreamWriter outputStreamWriterA = new OutputStreamWriter(fileOutputStreamA, "UTF-8");
            PrintWriter printWriterA = new PrintWriter(outputStreamWriterA);

            printFileContentsA(printWriterA, modelProdutoRecebidoHortifruti);
            outputStreamWriterA.close();
            fileOutputStreamA.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printFileContentsA(PrintWriter printWriter, ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti) throws Exception {
        String linha = "L:";
        printWriter.println(linha);

        SimpleDateFormat dateNome = new SimpleDateFormat("dd/MM/yyyy");
        linha = dateNome.format(new java.sql.Date(System.currentTimeMillis()));
        printWriter.println(linha);

        linha = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "");                 //id empresa
        printWriter.println(linha);

        linha = getSharedPreferences("Preferences", 0).getString("NUMLOJA", "");                 //id local
        printWriter.println(linha);

        linha = modelProdutoRecebidoHortifruti.getCodigoCDSTM();               //codigo origem
        printWriter.println(linha);

        linha = "1";                  //unidad. armazenamento
        printWriter.println(linha);

        printProcessos(printWriter, modelProdutoRecebidoHortifruti);

        printWriter.close();
    }

    private void printProcessos(PrintWriter printWriter, ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti) throws Exception {
        imprimirProcesso(printWriter, 7, modelProdutoRecebidoHortifruti.getNomeDoProduto());
        imprimirProcesso(printWriter, 9, getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")");
        imprimirProcesso(printWriter, 16, modelProdutoRecebidoHortifruti.getModelRecepcaoHortifruti().getNumeroBonoRomaneio());
        imprimirProcesso(printWriter, 18, modelProdutoRecebidoHortifruti.getModelRecepcaoHortifruti().getDataRecepcao());
        imprimirProcesso(printWriter, 19, modelProdutoRecebidoHortifruti.getCodigoDoProduto());
        imprimirProcesso(printWriter, 20, modelProdutoRecebidoHortifruti.getFornecedor());
        imprimirProcesso(printWriter, 25, modelProdutoRecebidoHortifruti.getTotalEntregueEmCaixas() + "");
        imprimirProcesso(printWriter, 27, modelProdutoRecebidoHortifruti.getCaixasAvaliadas() + "");
        imprimirProcesso(printWriter, 47, modelProdutoRecebidoHortifruti.getPodridao() + "");
        imprimirProcesso(printWriter, 48, modelProdutoRecebidoHortifruti.getDefGraves() + "");
        imprimirProcesso(printWriter, 49, modelProdutoRecebidoHortifruti.getDefLeves() + "");
        imprimirProcesso(printWriter, 50, modelProdutoRecebidoHortifruti.getDescalibre() + "");
        imprimirProcesso(printWriter, 51, modelProdutoRecebidoHortifruti.getPesoDaAmostra() + "");
        imprimirProcesso(printWriter, 52, modelProdutoRecebidoHortifruti.getBrix() + "");
        imprimirProcesso(printWriter, 53, modelProdutoRecebidoHortifruti.getEstagio());
        imprimirProcesso(printWriter, 54, modelProdutoRecebidoHortifruti.getLbs());
        imprimirProcesso(printWriter, 55, modelProdutoRecebidoHortifruti.getDemaisDefeitos());
        imprimirProcesso(printWriter, 56, modelProdutoRecebidoHortifruti.getVolumeEntregueKg() + "");
        imprimirProcesso(printWriter, 57, modelProdutoRecebidoHortifruti.getVolumeDevolvidoCaixas() + "");
        imprimirProcesso(printWriter, 58, modelProdutoRecebidoHortifruti.getVolumeDevolvidoKg() + "");
        imprimirProcesso(printWriter, 59, modelProdutoRecebidoHortifruti.getDescricaoDefeitoPrincipal());
        imprimirProcesso(printWriter, 60, modelProdutoRecebidoHortifruti.getParecerFinalDoCQ());
        imprimirProcesso(printWriter, 61, modelProdutoRecebidoHortifruti.getPorcentagemProdutosRecebidos() + "");
        imprimirProcesso(printWriter, 63, modelProdutoRecebidoHortifruti.getPorcentagemPodridao() + "");
        imprimirProcesso(printWriter, 64, modelProdutoRecebidoHortifruti.getPorcentagemDefGraves() + "");
        imprimirProcesso(printWriter, 65, modelProdutoRecebidoHortifruti.getPorcentagemDefLeves() + "");
        imprimirProcesso(printWriter, 66, modelProdutoRecebidoHortifruti.getPorcentagemDescalibre() + "");

        imprimirProcesso(printWriter, 69, new SimpleDateFormat("HH:mm:ss").format(new Date()));

        imprimirProcesso(printWriter, 41, "Centro De Distribuição Hortifruti");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imprimirProcesso(printWriter, 45, telephonyManager.getDeviceId());

    }

    private void imprimirProcesso(PrintWriter printWriter, int codigo, String value) {
        String linha = codigo + "-" + value + "-" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "|";
        printWriter.print(linha);
    }

    private File gerarFile(String fileName) {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao" + File.separator + fileName + ".st");

        return f;
    }

    private String fileNameA(Date dataArquivo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_01_CD_ControleDeEstoqueHortifruti_" +
                simpleDateFormat.format(dataArquivo) +
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

    private void verificarArquivamentoBono(long id) {
        List<ModelProdutoRecebidoHortifruti> modelProdutoRecebidoHortifrutiList =
                ModelProdutoRecebidoHortifruti.find(
                        ModelProdutoRecebidoHortifruti.class,
                        "model_recepcao_hortifruti = ?",
                        id + ""
                );

        int i = 0;
        for (ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti :
                modelProdutoRecebidoHortifrutiList) {

            verificarArquivamento(modelProdutoRecebidoHortifruti.getId(), i);
            i++;
        }

        Date date = myCalendar.getTime();
        executarConsulta(date, spinner.getSelectedItem().toString(), false);

    }

}
