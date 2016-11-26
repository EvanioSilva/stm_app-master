package com.rastreabilidadeInterna.preparacao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.geral.objetoReceita;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityProducaoDoDia extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_producao_do_dia);

        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Confeitaria");
        spinnerArray.add("Padaria");
        spinnerArray.add("Pratos Prontos");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.sp_prep_area);
        sItems.setAdapter(adapter);

        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadReceitas(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button button = (Button) findViewById(R.id.btnSalvar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDados();
            }
        });

    }

    private void salvarDados(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.llReceitas);
        int childCount = layout.getChildCount();
        ArrayList<String> receitas = new ArrayList<String>();
        ArrayList<String> quantidades = new ArrayList<String>();
        for (int i = 0; i < childCount; i++){
            LinearLayout ll = (LinearLayout) layout.getChildAt(i);
            TextView tv = (TextView) ll.getChildAt(0);
            EditText et = (EditText) ll.getChildAt(1);

            if (!et.getText().toString().isEmpty()) {
                receitas.add(tv.getText().toString());
                quantidades.add(et.getText().toString());
            }
        }

        salvarNoArquivo(receitas, quantidades);
        salvarNoBanco(receitas, quantidades);
        finish();
    }

    private String gerarTabelaHtml(ArrayList<String> receitas, ArrayList<String> quantidades){
        String html = "<table>" +
                "<tr>" +
                    "<td>Receita</td>" +
                    "<td>Quantidade</td>" +
                "</tr>";

        for (int i = 0; i < receitas.size(); i++){
            html += "<tr>" +
                    "<td> "+ receitas.get(i) + "</td>" +
                    "<td> "+ quantidades.get(i) + "</td>" +
                    "</tr>";
        }

        html += "</table>";

        return html;
    }

    private String data(){
        return new SimpleDateFormat("ddMMyyyy").format(new Date());
    }

    private String hora(){
        return new SimpleDateFormat("HHmmss").format(new Date());
    }

    private String loja(){
        return getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private void salvarNoArquivo(ArrayList<String> receitas, ArrayList<String> quantidades){
        String fileName = "OK_V_PROD_PREP_" + data() + "_" + hora() + "_" + loja();

        File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        if (!local.exists()) {
            local.mkdir();
        }

        local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao");
        if (!local.exists()) {
            local.mkdir();
        }

        try {

            deleteFile(fileName);

            File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao" + File.separator + fileName + ".st");
            FileOutputStream out = new FileOutputStream(arquivo);
            OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
            PrintWriter Print = new PrintWriter(OSW);

            String linha = "1";        //id cliente
            Print.println(linha);

            SimpleDateFormat dateNome = new SimpleDateFormat("dd/MM/yyyy");
            linha = dateNome.format(new Date( System.currentTimeMillis()));
            Print.println(linha);

            linha = "0.0";               //latitude
            Print.println(linha);

            linha = "0.0";               //longitude
            Print.println(linha);

            linha = "1000";               //id usuário
            Print.println(linha);


            linha = "46-"+gerarTabelaHtml(receitas, quantidades)+"---"+dateNome.format(new Date())+"|";
            Print.print(linha);

            linha = "9-"+getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")" +"---"+dateNome.format(new Date())+"|";
            Print.print(linha);

            linha = "14-"+getSharedPreferences("Preferences", 0).getString("AREADEUSO", "")+"---"+dateNome.format(new Date())+"|";
            Print.print(linha);

            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            linha = "14-"+telephonyManager.getDeviceId()+"---"+dateNome.format(new Date())+"|";
            Print.print(linha);

            Print.close();
            OSW.close();
            out.close();

        } catch (FileNotFoundException e) {
            //erro de arquivo
        } catch (IOException e) {
            //erro geral
        }
    }

    private void salvarNoBanco(ArrayList<String> receitas, ArrayList<String> quantidades){
        Repositorio repositorio = new Repositorio(this);
        String data = data();
        String hora = hora();
        String loja = loja();
        for (int i = 0; i < receitas.size(); i++){
            repositorio.createProducaoDiaria(
                    receitas.get(i),
                    quantidades.get(i),
                    data,
                    hora,
                    loja
                    );
        }
    }

    private void loadReceitas(int position){
        Repositorio repositorio = new Repositorio(this);
        ArrayList<objetoReceita> receitas = new ArrayList<objetoReceita>();
        switch (position){
            case 0:
                receitas = repositorio.listarTodasReceitas("confeitaria");
                break;
            case 1:
                receitas = repositorio.listarTodasReceitas("padaria");
                break;
            case 2:
                receitas = repositorio.listarTodasReceitas("pratosprontos");
                break;
        }
        generateForm(receitas);
    }

    private void generateForm(ArrayList<objetoReceita> receitas){
        LinearLayout layout = (LinearLayout) findViewById(R.id.llReceitas);
        layout.removeAllViews();
        ArrayList<String> receitasStrings = new ArrayList<String>();
        for (objetoReceita receita : receitas){
            if (!receita.intermediaria.equals("I")) {
                if (!receitasStrings.contains(receita.receita)){
                    receitasStrings.add(receita.receita);
                }
            }
        }

        for (String receitaString : receitasStrings) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1.0));

            TextView textView = new TextView(this);
            textView.setText(receitaString);
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1.0));
            textView.setTextSize((float) 25);

            EditText editText = new EditText(this);
            editText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1.0));
            editText.setTextSize((float) 25);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            linearLayout.addView(textView);
            linearLayout.addView(editText);

            layout.addView(linearLayout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_producao_do_dia, menu);
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
