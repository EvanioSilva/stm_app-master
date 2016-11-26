package com.rastreabilidadeInterna.controleEstoque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.helpers.HistoricoXMLController;
import com.rastreabilidadeInterna.helpers.LogGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static java.lang.System.console;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;

public class ActivityAssociarEtiquetas extends Activity {

    int quantidadeCaixas;
    int quantidadePecas;
    String nomeProduto;
    String usuarioCpf;
    String usuarioNome;
    String codigoProduto;
    String codigoCaixa;
    String nomeFabricante;
    String sif;
    String dataFab;
    String dataVal;
    String pesoLiquido;
    String lote;

    LogGenerator log;

    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_associar_etiquetas);

        log = new LogGenerator(this);

        TextView textViewNomeDoProduto = (TextView) findViewById(R.id.textViewNomeProduto);
        textViewNomeDoProduto.setText(getIntent().getExtras().getString("nomeProduto"));

        list = new ArrayList<String>();

        Bundle bundle = getIntent().getExtras();

        quantidadeCaixas = bundle.getInt("quantidadeCaixas");
        log.append("quantidade de caixas: " + quantidadeCaixas);

        quantidadePecas = bundle.getInt("quantidadePecas");
        log.append("quantidade de pecas: " + quantidadePecas);

        nomeProduto = bundle.getString("nomeProduto");
        log.append("nome do produto: " + nomeProduto);

        usuarioCpf = bundle.getString("usuarioCpf");
        log.append("usuario cpf: " + usuarioCpf);

        usuarioNome = bundle.getString("usuarioNome");
        log.append("usuario nome: " + usuarioNome);

        codigoProduto = bundle.getString("codigoProduto");
        log.append("codigo do produto: " + codigoProduto);

        codigoCaixa = bundle.getString("codigoCaixa");
        log.append("codigo de caixa: " + codigoCaixa);

        nomeFabricante = bundle.getString("nomeFabricante");
        log.append("nome do fabricante: " + nomeFabricante);

        sif = bundle.getString("sif");
        log.append("sif: " + sif);

        dataFab = bundle.getString("dataFab");
        log.append("data fab: " + dataFab);

        dataVal = bundle.getString("dataVal");
        log.append("data val: " + dataVal);

        pesoLiquido = bundle.getString("pesoLiquido");
        log.append("peso liquido: " + pesoLiquido);

        lote = bundle.getString("lote");
        log.append("lote: " + lote);

        Button buttonSalvar = (Button) findViewById(R.id.buttonFinalizarLeitura);
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSalvar();
            }
        });

        Button buttonOk = (Button) findViewById(R.id.buttonOK);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editTextEtiqueta);
                addCod(editText.getText().toString());
            }
        });

        RadioGroup rdgrpUnico = (RadioGroup) findViewById(R.id.rdgrpUnico);
        rdgrpUnico.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateNumeroDeSelos();
            }
        });

        EditText editText = (EditText) findViewById(R.id.editTextEtiqueta);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                log.append("salvando selo: " + s.toString());
                if (s.toString().length() >= 11){
                    addCod(s.toString().replaceAll("[^A-Za-z0-9]", ""));
                }
            }
        });
    }

    private void addCod(String codigo){
        int qtdTotal = 0;
        EditText editText = (EditText) findViewById(R.id.editTextEtiqueta);

        RadioGroup rdgrpUnico = (RadioGroup) findViewById(R.id.rdgrpUnico);
        if (rdgrpUnico.getCheckedRadioButtonId() == R.id.rdbtCaixas) {
            qtdTotal = quantidadeCaixas;
        } else {
            qtdTotal = quantidadeCaixas * quantidadePecas;
        }

        if (list.size() == qtdTotal){
            Toast.makeText(getApplicationContext(), "Você já inseriu todos os codigos necessários!", Toast.LENGTH_LONG).show();
            return;
        }

        if(codigo.equals("")){
            Toast.makeText(getApplicationContext(), "Insira o código Safe Trace", Toast.LENGTH_LONG).show();
        }else if(verificaSelo(codigo)){
            Toast.makeText(getApplicationContext(), "Código Safe Trace já inserido", Toast.LENGTH_LONG).show();
            editText.setText("");
        }
        else{
            list.add(codigo);
            updateListView();

            updateNumeroDeSelos();
        }
        editText.setText("");
    }

    private void actionSalvar() {
        Repositorio repositorio = new Repositorio(this);

        log.append("========= associar etiquetas =========");
        log.append("iniciando salvamento");

        objetoControleEstoque objetoCe = new objetoControleEstoque(this);

        log.append("objeto ce: " + objetoCe.toString());

        objetoControleEstoqueCodSafe objetoSa = new objetoControleEstoqueCodSafe(this);

        log.append("objeto sa: " + objetoSa.toString());

        objetoCe.usuarioNomeCpf = usuarioNome + "(" + usuarioCpf + ")";
        log.append("usuario nome cpf: " + objetoCe.usuarioNomeCpf);

        objetoCe._id = 0;
        log.append("id: " + objetoCe._id);

        objetoCe.codigo = codigoProduto;
        log.append("codigo: " + objetoCe.codigo);

        objetoCe.caixa = codigoCaixa;
        log.append("caixa: " + objetoCe.caixa);

        objetoCe.fabricante = nomeFabricante;
        log.append("fabricante: " + objetoCe.fabricante);

        objetoCe.sif = sif;
        log.append("sif: " + sif);

        objetoCe.dataFab = dataFab;
        log.append("data fab: " + objetoCe.dataFab);

        objetoCe.dataVal = dataVal;
        log.append("data val: " + objetoCe.dataVal);

        objetoCe.tipoProduto = nomeProduto;
        log.append("tipo do produto: " + objetoCe.tipoProduto);

        RadioGroup rdgrpUnico = (RadioGroup) findViewById(R.id.rdgrpUnico);

        if (rdgrpUnico.getCheckedRadioButtonId() == R.id.rdbtCaixas) {
            objetoCe.qtd = Integer.toString(quantidadeCaixas);
            log.append("quantidade: " + objetoCe.qtd);
        } else {
            objetoCe.qtd = Integer.toString(quantidadeCaixas * quantidadePecas);
            log.append("quantidade: " + objetoCe.qtd);
        }

        HistoricoXMLController historicoXMLController = new HistoricoXMLController(
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", ""),
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", ""),
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", ""),
                new java.util.Date(),
                HistoricoXMLController.TYPE_CE
        );

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            historicoXMLController.adicionarObjetoHistorico(
                    new ObjetoHistoricoControleEstoque(
                            nomeProduto,
                            nomeFabricante,
                            simpleDateFormat.parse(dataFab),
                            simpleDateFormat.parse(dataVal),
                            Integer.parseInt(objetoCe.qtd),
                            new java.util.Date()
                    )
            );
        } catch (ParseException e) {
            e.printStackTrace();
            log.append(e.getStackTrace().toString());
        }


        if (lote.length() > 0) {
            objetoCe.lote = lote;
            log.append("lote: " + objetoCe.lote);
        } else {
            objetoCe.lote = dataFab;
            log.append("lote: " + objetoCe.lote);
        }
        objetoCe._id = repositorio.salvarControleEstoque_Origem(objetoCe);

        log.append("objeto ce apos atribuições: " + objetoCe.toString());

        long id_teste = objetoCe._id;

        objetoSa.tipo = nomeProduto;

        if (pesoLiquido.equals("")) {
            objetoCe.pesoLiquido = "0";
            objetoCe.pesoMedioLote = "0";
        } else {
            objetoCe.pesoLiquido = pesoLiquido;
            objetoCe.pesoMedioLote = Integer.toString(Integer.parseInt(pesoLiquido) * quantidadeCaixas);
        }

        log.append("objeto ce apos atribuições: " + objetoCe.toString());

        SimpleDateFormat dateNome = new SimpleDateFormat("yyyyMMdd");
        String data_arquivo = dateNome.format(new Date(currentTimeMillis()));

        dateNome = new SimpleDateFormat("HHmmss");
        String hora_arquivo = dateNome.format(new Date(currentTimeMillis()));

        SharedPreferences settings = getSharedPreferences("Preferences", 0);
        String prefix = settings.getString("NUMCLIENTE", "") + settings.getString("NUMLOJA", "");
        String prefix2 = settings.getString("NUMCLIENTE", "") + settings.getString("NUMLOJA", "") + "_" + settings.getString("NUMTABLET", null) + "_";
        String idLoja = settings.getString("IDLOJA", "");
        SimpleDateFormat formatAux = new SimpleDateFormat("ddMMyy");
        String data_aux = formatAux.format(new Date(currentTimeMillis()));

        String numCliente = settings.getString("NUMCLIENTE", "");

        String fileName = "OK_A_01_ControleEstoque_Origem_" + "_" + data_arquivo + "_" + hora_arquivo + "_" + numCliente + settings.getString("NUMLOJA", "") + settings.getString("NUMTABLET", "");
        log.append("filename: " + fileName);

        String auxCod = "STM" + prefix + data_aux + hora_arquivo + "_" + numCliente + settings.getString("NUMLOJA", "") + settings.getString("NUMTABLET", "");

        objetoCe.areaDeUso = getSharedPreferences("Preferences", 0).getString("AREADEUSO", "");

        objetoCe.saveFile(fileName, auxCod, idLoja, numCliente);

        for (int i = 0; i < list.size(); i++) {
            objetoSa._id = 0;
            objetoSa.codigoSafe = list.get(i);

            objetoSa._id = repositorio.salvarControleEstoque_CodSafe(objetoSa);
            id_teste = objetoSa._id;

            objetoSa = repositorio.buscarControleEstoque_CodSafe(id_teste);
            Log.i("controle", objetoSa.toString());

            fileName = "OK_A_02_ControleEstoque_CodSaf" + "_" + i + "_" + data_arquivo + "_" + hora_arquivo + "_" + numCliente + settings.getString("NUMLOJA", "") + settings.getString("NUMTABLET", "");
            log.append("filename: " + fileName);
            objetoSa.saveFile(fileName, idLoja, numCliente);

        }

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");

        int i = 0;

        for (String listItem : list) {


            objetoSa._id = 0;
            objetoSa.codigoSafe = listItem;

            objetoSa._id = repositorio.salvarControleEstoque_CodSafe(objetoSa);

            fileName = "OK_A_02_ControleEstoque_CodSaf" +
                    "_" + i +
                    "_" + data_arquivo +
                    "_" + hora_arquivo +
                    "_" + numCliente +
                    settings.getString("NUMLOJA", "") +
                    settings.getString("NUMTABLET", "");
            log.append("filename: " + fileName);

            objetoSa.saveFile(fileName, idLoja, numCliente);

            i++;
        }


        fileName = "OK_B_01_ControleEstoque_CodSaf_" +
                data_arquivo + "_" +
                hora_arquivo + "_" +
                numCliente +
                settings.getString("NUMLOJA", "") +
                settings.getString("NUMTABLET", "");
        log.append("filename: " + fileName);

        objetoSa.saveFileB(fileName, auxCod, list);

        for (int contaLista = 0; contaLista < list.size(); contaLista++) {
            repositorio.inserirIdxBaixado(list.get(contaLista).substring(5, 11),
                    objetoSa.tipo,
                    objetoCe.fabricante,
                    objetoCe.sif,
                    objetoCe.lote,
                    objetoCe.dataFab,
                    objetoCe.dataVal);
        }

        fileName = prefix2 + diaAtual();
        log.append("filename: " + fileName);

        int pos = idxExiste(fileName + ".idx");

        if (pos == -1) {
            objetoSa.saveFileIdx(fileName,
                    list,
                    objetoSa.tipo,
                    objetoCe.fabricante,
                    objetoCe.sif,
                    objetoCe.lote,
                    objetoCe.dataFab,
                    objetoCe.dataVal);
            log.append("salvando idx");
        } else {
            editaIdx(pos, objetoSa.tipo,
                    objetoCe.fabricante,
                    objetoCe.sif,
                    objetoCe.lote,
                    objetoCe.dataFab,
                    objetoCe.dataVal);
            log.append("editando idx");
            Log.i("controle", "IDX já existe");

        }

        Toast.makeText(getApplicationContext(), "Salvo com sucesso", Toast.LENGTH_LONG).show();

        list.clear();
        updateListView();
        updateNumeroDeSelos();
        finish();
    }

    private void updateListView(){

        ListView lvList = (ListView) findViewById(R.id.listViewEtiquetas);

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

    private void updateNumeroDeSelos() {
        RadioGroup rdgrpUnico = (RadioGroup) findViewById(R.id.rdgrpUnico);
        int checkedButtonID = rdgrpUnico.getCheckedRadioButtonId();

        TextView textView = (TextView) findViewById(R.id.textViewSelosLidos);

        if (checkedButtonID == R.id.rdbtCaixas){
            textView.setText(Integer.toString(list.size()) + "/" + quantidadeCaixas);
            if (quantidadeCaixas == list.size()){
                textView.setTextColor(Color.GREEN);
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

            } else {
                textView.setTextColor(Color.RED);
            }
        } else {
            textView.setText(Integer.toString(list.size()) + "/" + (quantidadeCaixas * quantidadePecas));
            if (quantidadeCaixas * quantidadePecas == list.size()){
                textView.setTextColor(Color.GREEN);
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

            } else {
                textView.setTextColor(Color.RED);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_associar_etiquetas, menu);
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

    private int idxExiste(String nomeArquivo){
        File pathIdx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx");

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

    private void editaIdx(int pos, String tipo, String fabricante, String sif, String lote, String dFab, String dVal){
        File pathIdx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx");

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

    private boolean verificaSelo(String selo){
        for(int i=0; i<list.size(); i++){
            if(list.get(i).equals(selo))
                return true;
        }

        return false;
    }
}

