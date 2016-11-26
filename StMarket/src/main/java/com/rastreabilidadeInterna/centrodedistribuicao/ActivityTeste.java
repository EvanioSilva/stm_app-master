package com.rastreabilidadeInterna.centrodedistribuicao;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.helpers.Laudo;
import com.rastreabilidadeInterna.models.Rotulo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityTeste extends AppCompatActivity {

    private ModelProdutoRecebido modelProdutoRecebido;

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 666;
    private ArrayList<String> savedImages = new ArrayList<String>();
    private Uri uriSavedImage;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("savedImages", savedImages);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        savedImages = savedInstanceState.getStringArrayList("savedImages");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearLayout.removeAllViews();
        for (String savedImage : savedImages) {
            ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
            Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
            linearLayout.addView(imageView);
        }
    }

    private void takePicture() {
        //camera stuff
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Carrefour" + File.separator +"Laudos");
        imagesFolder.mkdirs();

        File image = new File(imagesFolder, "AMT_" + Laudo.fileDate() + ".jpg");
        uriSavedImage = Uri.fromFile(image);

        savedImages.add(uriSavedImage.toString());

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
                linearLayout.removeAllViews();
                for (String savedImage : savedImages) {
                    ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.simple_image_view, null);
                    Picasso.with(this).load(savedImage).resize(100, 100).centerCrop().into(imageView);
                    linearLayout.addView(imageView);
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);

        defineModelProduto();
        defineSpinners();
        defineButton();
    }

    private void defineButton(){
        Button button = (Button) findViewById(R.id.btn_concluir);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Repositorio repositorio = new Repositorio(ActivityTeste.this);

                Spinner spDesc = (Spinner) findViewById(R.id.sp_descongelamento);
                modelProdutoRecebido.setDescongelamento(spDesc.getSelectedItem().toString());

                Spinner spConclusao = (Spinner) findViewById(R.id.sp_conclusao);
                modelProdutoRecebido.setConclusao(spConclusao.getSelectedItem().toString());

                ModelRecepcao modelRecepcao = repositorio.recoverRecepcao(modelProdutoRecebido.getIdRecepcao());

                List<Rotulo> rotulos = Rotulo.listAll(Rotulo.class);

                ArrayList<String> rotulagem = new ArrayList<String>();

                for (Rotulo rotulo : rotulos) {
                    rotulagem.add(rotulo.getImageUri());
                }

                if (!modelProdutoRecebido.getConclusao().equals("Aprovado")){
                    Laudo.generateCDLaudo(modelRecepcao, modelProdutoRecebido, savedImages, rotulagem);
                }



                EditText ph = (EditText) findViewById(R.id.edt_ph);
                modelProdutoRecebido.setPh(ph.getText().toString());

                EditText temp = (EditText) findViewById(R.id.edt_temp);
                modelProdutoRecebido.setTemperatura(temp.getText().toString());

                if (!modelProdutoRecebido.getConclusao().equals("Aprovado") && savedImages.size() < 4){
                    Toast.makeText(ActivityTeste.this, "Você precisa tirar 4 fotos", Toast.LENGTH_LONG).show();
                } else {

                    repositorio.updateProdutoRecebido(modelProdutoRecebido);

                    salvarDadosNoArquivo(modelProdutoRecebido);

                    finish();
                }
            }
        });

        Button btnPhoto = (Button) findViewById(R.id.button);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private void defineSpinners(){
        Spinner spConclusao = (Spinner) findViewById(R.id.sp_conclusao);

        List<String> list1 = new ArrayList<String>();
        list1.add(modelProdutoRecebido.getConclusao());
        list1.add("Aprovado");
        list1.add("Devolução Parcial");
        list1.add("Devolução Total");
        list1.add("Recebido Fora do Padrão");
        list1.add("Recebido com Restrição");
        list1.add("Bloqueado Total");
        list1.add("Bloqueado Parcial");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConclusao.setAdapter(dataAdapter1);

        Spinner spDesc = (Spinner) findViewById(R.id.sp_descongelamento);

        List<String> list2 = new ArrayList<String>();
        list2.add(modelProdutoRecebido.getDescongelamento().equals("@NF@") ? "Não Realizado" : modelProdutoRecebido.getDescongelamento());
        list2.add("Conforme");
        list2.add("Não Conforme");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDesc.setAdapter(dataAdapter2);

        Spinner spFat = (Spinner) findViewById(R.id.sp_fatiamento);

        List<String> list3 = new ArrayList<String>();
        list3.add(modelProdutoRecebido.getFatiamento().equals("@NF@") ? "Não Realizado" : modelProdutoRecebido.getFatiamento());
        list3.add("Não Realizado");
        list3.add("Conforme");
        list3.add("Não Conforme");
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list3);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFat.setAdapter(dataAdapter3);
    }

    private void defineModelProduto(){
        Repositorio repositorio = new Repositorio(this);
        modelProdutoRecebido = repositorio.recoverProdutoRecebido(getIntent().getIntExtra("model", 0));

        TextView tvNomeProduto = (TextView) findViewById(R.id.product_name);
        tvNomeProduto.setText(modelProdutoRecebido.getNome());

        if (modelProdutoRecebido.getSetor().contains("24-")){
            hideFatiamento();
        }

        if (modelProdutoRecebido.getSetor().contains("21-")) {
            hidePh();
            hideTemperatura();
            hideFatiamento();
        }

        if (modelProdutoRecebido.getSetor().contains("20-")) {
            hidePh();
            hideTemperatura();
            hideDescongelamento();
        }

        EditText ph = (EditText) findViewById(R.id.edt_ph);
        ph.setText(modelProdutoRecebido.getPh().equals("@NF@") ? "" : modelProdutoRecebido.getPh());

        EditText temp = (EditText) findViewById(R.id.edt_temp);
        temp.setText(modelProdutoRecebido.getTemperatura().equals("@NF@") ? "" : modelProdutoRecebido.getTemperatura());
    }

    private void hideFatiamento(){
        Spinner fat = (Spinner) findViewById(R.id.sp_fatiamento);
        fat.setEnabled(false);
    }

    private void hideDescongelamento(){
        Spinner desc = (Spinner) findViewById(R.id.sp_descongelamento);
        desc.setEnabled(false);
    }

    private void hidePh(){
        EditText ph = (EditText) findViewById(R.id.edt_ph);
        ph.setEnabled(false);
        ph.setText("Não é necessário");
    }

    private void hideTemperatura(){
        EditText temp = (EditText) findViewById(R.id.edt_temp);
        temp.setEnabled(false);
        temp.setText("Não é necessário");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_teste, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private void salvarDadosNoArquivo(ModelProdutoRecebido modelProdutoRecebido) {
        verificarPasta();

        Date dataArquivo = new Date();

        String filenameA = fileNameA(dataArquivo);

        String codigoCDSTM = modelProdutoRecebido.getCodigoCDSTM();

        try {
            deleteFile(filenameA);

            File arquivoA = gerarFile(filenameA);

            FileOutputStream fileOutputStreamA = new FileOutputStream(arquivoA);
            OutputStreamWriter outputStreamWriterA = new OutputStreamWriter(fileOutputStreamA, "UTF-8");
            PrintWriter printWriterA = new PrintWriter(outputStreamWriterA);

            printFileContentsA(printWriterA, codigoCDSTM, modelProdutoRecebido);
            outputStreamWriterA.close();
            fileOutputStreamA.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private String fileNameA(Date dataArquivo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return "OK_A_04_CD_ControleDeEstoque_" +
                simpleDateFormat.format(dataArquivo) +
                getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") +
                getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") +
                getSharedPreferences("Preferences", 0).getString("NUMTABLET", "");
    }

    private File gerarFile(String fileName) {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao" + File.separator + fileName + ".st");

        return f;
    }

    private void printFileContentsA(PrintWriter printWriter, String codigoCDSTM, ModelProdutoRecebido modelProdutoRecebido) throws Exception {
        String linha = "L:";
        printWriter.println(linha);

        SimpleDateFormat dateNome = new SimpleDateFormat("dd/MM/yyyy");
        linha = dateNome.format(new java.sql.Date(System.currentTimeMillis()));
        printWriter.println(linha);

        linha = getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "");                 //id empresa
        printWriter.println(linha);

        linha = getSharedPreferences("Preferences", 0).getString("NUMLOJA", "");                 //id local
        printWriter.println(linha);

        linha = codigoCDSTM;               //codigo origem
        printWriter.println(linha);

        linha = "1";                  //unidad. armazenamento
        printWriter.println(linha);

        printProcessos(printWriter, modelProdutoRecebido);

        printWriter.close();
    }

    private void printProcessos(PrintWriter printWriter, ModelProdutoRecebido modelProdutoRecebido) throws Exception {
        Repositorio repositorio = new Repositorio(this);

        imprimirProcesso(printWriter, 1, modelProdutoRecebido.getMarca());
        imprimirProcesso(printWriter, 2, modelProdutoRecebido.getCodigoBarrasProduto() + "/" + modelProdutoRecebido.getCodigoBarrasCaixa());
        imprimirProcesso(printWriter, 3, modelProdutoRecebido.getSif());
        imprimirProcesso(printWriter, 4, modelProdutoRecebido.getDataFabricacao());
        imprimirProcesso(printWriter, 5, modelProdutoRecebido.getDataValidade());
        imprimirProcesso(printWriter, 7, modelProdutoRecebido.getNome());
        imprimirProcesso(printWriter, 6, Integer.toString(modelProdutoRecebido.getPecasPorCaixa() * modelProdutoRecebido.getTotalCaixas()));
        imprimirProcesso(printWriter, 9, getIntent().getExtras().getString("Nome") + "(" + getIntent().getExtras().getString("cpf") + ")");
        imprimirProcesso(printWriter, 12, modelProdutoRecebido.getPeso());
        imprimirProcesso(printWriter, 16, repositorio.recoverRecepcao(modelProdutoRecebido.getIdRecepcao()).getNumeroDaRecepcao());
        imprimirProcesso(printWriter, 17, repositorio.recoverRecepcao(modelProdutoRecebido.getIdRecepcao()).getPlacaDoCaminhao());
        imprimirProcesso(printWriter, 18, repositorio.recoverRecepcao(modelProdutoRecebido.getIdRecepcao()).getDataDaRecepcao());
        imprimirProcesso(printWriter, 19, modelProdutoRecebido.getCodigoBarrasCaixa());
        imprimirProcesso(printWriter, 20, modelProdutoRecebido.getFornecedor());
        imprimirProcesso(printWriter, 21, modelProdutoRecebido.getSetor());
        imprimirProcesso(printWriter, 22, (modelProdutoRecebido.getPh().equals("") ? "@NF@" : modelProdutoRecebido.getPh()));
        imprimirProcesso(printWriter, 23, (modelProdutoRecebido.getTemperatura().replace("-", "-").equals("")) ? "@NF@" : modelProdutoRecebido.getTemperatura().replace("-", "-"));
        imprimirProcesso(printWriter, 24, Integer.toString(modelProdutoRecebido.getTotalPalets()));
        imprimirProcesso(printWriter, 25, Integer.toString(modelProdutoRecebido.getTotalCaixas()));
        imprimirProcesso(printWriter, 26, "0");
        imprimirProcesso(printWriter, 27, Integer.toString(modelProdutoRecebido.getTotalCaixasAmostradas()));
        imprimirProcesso(printWriter, 28, Integer.toString(modelProdutoRecebido.getTotalPecasRegular()));
        imprimirProcesso(printWriter, 29, Float.toString(modelProdutoRecebido.getTotalPorcentagemRegular()));
        imprimirProcesso(printWriter, 30, Integer.toString(modelProdutoRecebido.getTotalPecasIrregular()));
        imprimirProcesso(printWriter, 31, Float.toString(modelProdutoRecebido.getTotalPorcentagemIrregular()));
        imprimirProcesso(printWriter, 32, "0");
        imprimirProcesso(printWriter, 33, "0");
        imprimirProcesso(printWriter, 34, Integer.toString(modelProdutoRecebido.getTotalPecasAmostradas()));
        imprimirProcesso(printWriter, 35, Integer.toString(modelProdutoRecebido.getTotalPecasIrregulares()));
        imprimirProcesso(printWriter, 36, Float.toString(modelProdutoRecebido.getTotalPesoIrregular()));
        imprimirProcesso(printWriter, 37, modelProdutoRecebido.getNaoConformidade());
        imprimirProcesso(printWriter, 38, modelProdutoRecebido.getMotivoDevolucao());
        imprimirProcesso(printWriter, 39, modelProdutoRecebido.getObservacoes());
        imprimirProcesso(printWriter, 40, modelProdutoRecebido.getConclusao());
        imprimirProcesso(printWriter, 41, "Centro De Distribuição");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imprimirProcesso(printWriter, 45, telephonyManager.getDeviceId());

        imprimirProcesso(printWriter, 46, modelProdutoRecebido.getDescongelamento());

        imprimirProcesso(printWriter, 69, new SimpleDateFormat("HH:mm:ss").format(new Date()));

    }

    private void imprimirProcesso(PrintWriter printWriter, int codigo, String value) {
        try {
            //String linha = codigo + "-" + value + "-" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "|";
            //printWriter.print(linha);
            String linha = codigo + "|" + value;
            printWriter.println(linha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
