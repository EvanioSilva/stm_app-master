package com.rastreabilidadeInterna.fracionamento;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.geral.varGlobais;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Felipe Pereira on 24/02/2015.
 */

public class ActivityGranel extends Activity{

    /**************************************************
    *   Atributos da Classe
     **************************************************/

    /**
     *  O {@link android.widget.Button} Utilizado para registrar um codigo de peca lido
     */
    private Button btnOKPeca;
    /**
     *  O {@link android.widget.Button} Utilizado para registrar um codigo de balanca lido
     */
    private Button btnOKBalanca;
    /**
     *  O {@link android.widget.Button} Utilizado para salvar e finalizar a atividade
     */
    private Button btnFinalizar;
    /**
     *  O {@link android.widget.EditText} Utilizado para recolher o codigo de peca
     */
    private EditText editTextCodigoPeca;
    /**
     *  O {@link android.widget.EditText} Utilizado para recolher o codigo de balanca
     */
    private EditText editTextCodigoBalanca;
    /**
     *  O {@link android.widget.ListView} Utilizado para mostrar os codigos de peca lidos
     */
    private ListView listViewPeca;
    /**
     *  O {@link android.widget.ListView} Utilizado para mostrar os codigos de balanca lidos
     */
    private ListView listViewBalanca;
    /**
     *  O {@link android.widget.CheckBox} Utilizado para informar se está utilizando sobras do dia anterior
     */
    private CheckBox checkBoxSobras;
    /**
     *  O {@link java.util.ArrayList} Contendo os codigos de etiqueta de peca que forem inseridos
     */
    private ArrayList<String> codigosDePeca = new ArrayList<String>();
    /**
     *  O {@link java.util.ArrayList} Contendo os codigos de balanca que forem inseridos
     */
    private ArrayList<String> codigosDeBalanca = new ArrayList<String>();
    /**
     *  As {@link android.content.SharedPreferences} contendo as informacoes de tablet, cliente e loja
     */
    private SharedPreferences sharedPreferences;

    /**************************************************
     *   Metodos da classe
     **************************************************/

    /**************************************************
     *   Metodos Override
     **************************************************/

    /**
     *  Metodo chamado quando a Activity se inicia, chama os metodos que buscam os elementos de layout e atribui acoes a eles
     *  Metodo padrao do Android
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fracionamento_granel);

        // Relacionar as variaveis com os elementos de layout
        atribuirElementosDeLayout();
        // Atribuir uma acao para cada elemento
        atribuirAcoesAosElementos();
        // Inicializar shared preferences
        recuperarSharedPreferences();


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putStringArrayList("codigosDePeca", codigosDePeca);
        savedInstanceState.putStringArrayList("codigosDeBalanca", codigosDeBalanca);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        codigosDePeca = savedInstanceState.getStringArrayList("codigosDePeca");
        codigosDeBalanca = savedInstanceState.getStringArrayList("codigosDeBalanca");

        atualizarListaDeCodigosDeBalanca();
        atualizarListaDeCodigosDePeca();
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
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fracionamento_granel, menu);
        menu.getItem(0).setTitle("Usuário: " + getIntent().getExtras().getString("Nome"));
        return true;
    }

    /**************************************************
     *   Metodos do Menu
     **************************************************/

    private void abrirConfiguracoes() {
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(input);
        builder.setTitle("Configurações");
        builder.setMessage("Digite a senha para configurar");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (input.getText().toString().equals("safeadm")) {

                    Intent it = null;
                    it = new Intent(getApplicationContext(), varGlobais.class);
                    startActivity(it);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {
                    Toast.makeText(getApplicationContext(), "Senha incorreta", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    /**************************************************
     *   Metodos de inicializacao
     **************************************************/

    /**
     *  Funcao que atribui os elementos de layout aos atributos da classe
     */
    private void atribuirElementosDeLayout(){
        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);
        btnOKBalanca = (Button) findViewById(R.id.btnOKBalanca);
        btnOKPeca = (Button) findViewById(R.id.btnOKPeca);

        editTextCodigoBalanca = (EditText) findViewById(R.id.editTextCodigoBalanca);
        editTextCodigoPeca = (EditText) findViewById(R.id.editTextCodigoPeca);

        listViewBalanca = (ListView) findViewById(R.id.listViewBalanca);
        listViewPeca = (ListView) findViewById(R.id.listViewPeca);

        checkBoxSobras = (CheckBox) findViewById(R.id.checkBoxSobras);
    }

    /**
     *  Funcao que atribui listeners aos elementos do layout
     */
    private void atribuirAcoesAosElementos(){
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarFracionamentoGranel();
            }
        });
        btnOKBalanca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarCodigoDeBalanca();
            }
        });
        btnOKPeca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarCodigoPeca();
            }
        });
        editTextCodigoPeca.addTextChangedListener(textWatcherCodigoDePeca);
        editTextCodigoBalanca.addTextChangedListener(textWatcherCodigoDeBalanca);
    }

    TextWatcher textWatcherCodigoDePeca = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editTextCodigoPeca.getText().toString().length() == 11) salvarCodigoPeca();
        }
    };

    TextWatcher textWatcherCodigoDeBalanca = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editTextCodigoBalanca.getText().toString().length() == 4) salvarCodigoDeBalanca();
        }
    };

    /**
     * Metodo que recupera as informacoes armazenadas nas shared preferences
     */
    private void recuperarSharedPreferences(){
        sharedPreferences = getSharedPreferences("Preferences", 0);
    }

    /**************************************************
     *   Metodos de processo
     **************************************************/

    /**
     * Metodo que recolhe o valor do codigo de balanca e armazena no vetor (se passar as validacoes)
     */
    private void salvarCodigoDeBalanca(){
        String codigoDeBalanca = editTextCodigoBalanca.getText().toString();

        if (validarCodigoDeBalanca(codigoDeBalanca)){
            codigosDeBalanca.add(codigoDeBalanca);
            atualizarListaDeCodigosDeBalanca();
            limparEditText(editTextCodigoBalanca);
        }
    }

    /**
     *  Metodo que recolhe o valor do codigo da etiqueta da peca e armazena no arraylist (se passar as validacoes)
     */
    private void salvarCodigoPeca(){
        String codigoDePeca = editTextCodigoPeca.getText().toString();

        if (validarCodigoDePeca(codigoDePeca)){
            codigosDePeca.add(codigoDePeca);
            atualizarListaDeCodigosDePeca();
            limparEditText(editTextCodigoPeca);
        }
    }

    /**
     *  Metodo que inicia o processo de validacao e salvamento do novo fracionamento a granel
     */
    private void salvarFracionamentoGranel(){
        if (validarFracionamentoGranel()){
            salvarFracionamentoGranelNoBanco();
            salvarFracionamentoGranelNoArquivo();
            Toast.makeText(this, "Granel Salvo Com Sucesso!", Toast.LENGTH_SHORT);
            finish();
        }
    }

    /**
     *  Metodo que recupera as informacoes fornecidas pelo usuario e cria os arquivos A e B
     */
    private void salvarFracionamentoGranelNoArquivo(){
        objetoFracionamento objetoFracGranel = new objetoFracionamento(this);
        ArrayList<String> linhasArquivo = new ArrayList<String>();
        String data_arquivo = "";
        String hora_arquivo = "";

        int contadorArquivo = 0;

        for (String codBalanca : codigosDeBalanca){

            // Arquivo A é criado utilizando informacoes armazenadas dentro do ObjetoFracionamento
            // Aqui sao preenchidas as informacoes e o arquivo A é criado a seguir
            // Se cria um arquivo A diferente por cada codigo de peca

            objetoFracGranel._id = 0;
            objetoFracGranel.flag = -1;
            objetoFracGranel.usuarioCpf = getIntent().getExtras().getString("Nome") + "("+ getIntent().getExtras().getString("cpf")+")";
            objetoFracGranel.novoSelo = sharedPreferences.getString("NUMCLIENTE", "") + sharedPreferences.getString("NUMLOJA", "") + converterHojeEmJuliano() + "99" + codBalanca;
            objetoFracGranel.areaDeUso = sharedPreferences.getString("AREADEUSO", "");

            SimpleDateFormat dateNome = new SimpleDateFormat("yyyyMMdd");
            data_arquivo = dateNome.format(new java.sql.Date( System.currentTimeMillis()));

            dateNome = new SimpleDateFormat("HHmmss");
            hora_arquivo = dateNome.format(new java.sql.Date( System.currentTimeMillis()));

            String fileName = "OK_A_03_Fracionamento_" +
                    data_arquivo + "_" +
                    hora_arquivo + "_" +
                    sharedPreferences.getString("NUMCLIENTE", "") +
                    sharedPreferences.getString("NUMLOJA", "") +
                    sharedPreferences.getString("NUMTABLET", "") + "_" +
                    ajustaZeros(Integer.toString(contadorArquivo), 3);

            contadorArquivo++;

            objetoFracGranel.saveFileA(fileName, sharedPreferences.getString("NUMLOJA", ""), sharedPreferences.getString("NUMCLIENTE", ""));


            // Aqui é onde as informacoes que serao inseridas no arquivo B sao coletadas
            // Um unico arquivo B é criado relacionando todos codigos de peca com todos codigos de balanca
            for (String codPeca : codigosDePeca){
                linhasArquivo.add(codPeca + ":" + sharedPreferences.getString("NUMCLIENTE", "") + sharedPreferences.getString("NUMLOJA", "") + converterHojeEmJuliano() + "99" + codBalanca);
            }
        }

        // Criacao e preenchimento do arquivo B
        String fileName = "OK_B_02_Fracionamento_" +
                data_arquivo + "_" +
                hora_arquivo+ "_" +
                sharedPreferences.getString("NUMCLIENTE", "") +
                sharedPreferences.getString("NUMLOJA", "") +
                sharedPreferences.getString("NUMTABLET", "");
        objetoFracGranel.saveFileB(fileName, linhasArquivo);
    }

    /**
     *  Metodo que percorre e insere no banco cada codigo de balanca para cada codigo de peca
     */
    private void salvarFracionamentoGranelNoBanco(){
        for (String codPeca : codigosDePeca){
            for (String codBalanca : codigosDeBalanca){
                inserirNoBanco(codBalanca, codPeca);
            }
        }
    }

    /**
     *  Metodo que executa o processo de persistir as informacoes no banco de dados
     *  A mesma tabela é usada para Fracionamentos e Granel
     *  A variavel flag é setada para -1 para identificar um Fracionamento como sendo do tipo Granel
     *  Isso é importante para diferencia-los durante recuperacao de informacao
     *
     * @param codBalanca {@link java.lang.String} Contendo o codigo da balanca a ser persistido
     * @param codPeca {@link java.lang.String} Contendo o codigo de peca a ser persistido
     */
    private void inserirNoBanco(String codBalanca, String codPeca){

        // Cria o objeto a ser persistido
        objetoFracionamento objetoFracGranel = new objetoFracionamento(this);
        objetoFracGranel.seloSafe = codPeca;

        String dataJuliano = converterHojeEmJuliano();
        objetoFracGranel.novoSelo =
                sharedPreferences.getString("NUMCLIENTE", "") +
                sharedPreferences.getString("NUMLOJA", "") +
                dataJuliano +
                codBalanca;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        objetoFracGranel.dataLeitura = simpleDateFormat.format(new Date());

        objetoFracGranel.flag = -1;

        objetoFracGranel.usuarioCpf = getIntent().getExtras().getString("Nome") + "("+ getIntent().getExtras().getString("cpf")+")";

        objetoFracGranel._id = 0;

        // Persiste o objeto usando o repositorio
        Repositorio repositorio = new Repositorio(this);

        repositorio.salvarFracionamento(objetoFracGranel);

        objetoFracionamento objetoTeste = new objetoFracionamento(this);
        objetoTeste = repositorio.buscarFracionamento(codPeca);
    }

    /**************************************************
     *   Metodos de validacao principais
     **************************************************/

    /**
     *  Metodo que valida se todos os dados necessarios foram inseridos (no minimo 1 peca e 1 codigo de balanca)
     * @return true se tudo estiver ok, caso contrario exibe um {@link android.widget.Toast} contendo o erro e retorna false
     */
    private boolean validarFracionamentoGranel(){
        try {
            validarArrayNaoVazio(codigosDePeca);
            validarArrayDeBalanca();
            return true;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     *  Metodo principal de validacao de codigo de etiqueta de peca, chama todos os metodos que fazem validacoes especificas relevantes
     *  Se alguma validacao falhar, mostra um {@link android.widget.Toast} para o usuario com as informacoes sobre o erro
     * @param codigoDePeca codigo de peca a ser validado
     * @return true se a validacao for ok, false caso contrario
     */
    private boolean validarCodigoDePeca(String codigoDePeca){
        try{
            validarTamanho(codigoDePeca, 11);
            return true;
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     *  Metodo principal de validacao de codigo de balanca, chama todos os metodos que fazem validacoes especificas
     *  Se alguma validacao falhar, mostra um {@link android.widget.Toast} com as informacoes sobre o erro encontrado
     * @return true se a validacao for ok, false caso contrario
     */
    private boolean validarCodigoDeBalanca(String codigoDeBalanca){
        try {
            validarTamanho(codigoDeBalanca, 4);
            validarUnicidade(codigoDeBalanca, codigosDeBalanca);
            return true;
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**************************************************
     *   Metodos de validacao utilizados pelos principais
     **************************************************/

    /**
     *  Metodo que verifica se algum código foi digitado, caso não insere o valor "0000" nele (padrão)
     */
    private void validarArrayDeBalanca(){
        if (codigosDeBalanca.size() == 0){
            codigosDeBalanca.add("0000");
        }
    }

    /**
     *  Metodo que verifica se o {@link java.util.ArrayList} não está vazio
     * @param arrayList O {@link java.util.ArrayList} a ser validado
     * @return true se o tamanho do array for maior ou igual a 1
     * @throws Exception A {@link java.lang.Exception} que informa que algum dos arrays está vazio
     */
    private boolean validarArrayNaoVazio(ArrayList<?> arrayList) throws Exception{
        if (arrayList.size() > 0){
            return true;
        } else {
            throw new Exception("Você precisa inserir no mínimo um codigo de Peça.");
        }
    }

    /**
     *  Metodo que compara se o codigo ja se encontra na lista
     * @param codigo codigo a ser validado
     * @param codigos lista de codigos lidos anteriormente
     * @return true se o codigo for unico
     * @throws java.lang.Exception se o codigo ja tiver sido utilizado
     */
    private boolean validarUnicidade(String codigo, ArrayList<String> codigos) throws Exception{
        if (codigos.contains(codigo)) {
            throw new Exception("O código ja foi utilizado anteriormente");
        } else {
            return true;
        }
    }

    /**
     *  Metodo que compara se o codigo possui o tamanho minimo exigido
     * @param codigo A {@link java.lang.String} contendo o Codigo a ser validado
     * @param tamanho O tamanho exigido para o codigo
     * @return true se o tamanho supera o requerimento
     * @throws java.lang.Exception se o tamanho for invalido
     */
    private boolean validarTamanho(String codigo, int tamanho) throws Exception{
         if (codigo.length() == tamanho) {
             return true;
         } else {
             throw new Exception("O codigo inserido precisa ter " + tamanho + " caracteres.");
         }
    }

    /**
     *  Metodo que compara se o inicio do codigo corresponde ao inicio esperado (99 nesse caso)
     * @param codigo A {@link java.lang.String} contendo o Codigo a ser validado
     * @param inicio A {@link java.lang.String} contendo o inicio esperado para o codigo
     * @return true se o codigo comeca com o valor indicado, falso caso contrario
     */
    private boolean validarInicio(String codigo, String inicio) throws Exception{
        if (codigo.substring(0, inicio.length()).equals(inicio)) {
            return true;
        } else {
            throw new Exception("O codigo precisa começar com " + inicio + ".");
        }
    }

    /**************************************************
     *   Metodos relacionados a UI
     **************************************************/

    /**
     *  Metodo que limpa o {@link android.widget.EditText} apos salvar seu valor na lista
     * @param editText {@link android.widget.EditText} a ser limpo
     */
    private void limparEditText(EditText editText){
        editText.setText("");
    }

    /**
     *  Metodo que atualiza o list view dos codigos de balanca apos a insercao de um novo codigo
     */
    private void atualizarListaDeCodigosDeBalanca(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, codigosDeBalanca);
        listViewBalanca.setAdapter(arrayAdapter);
    }

    /**
     *  Metodo que atualiza o list view dos codigos de etiqueta de peca apos a insercao de um novo codigo
     */
    private void atualizarListaDeCodigosDePeca(){
        ArrayList<String> itemsDaListView = new ArrayList<String>();
        Repositorio repositorio = new Repositorio(getApplicationContext());

        for (String codigoPeca : codigosDePeca){
            itemsDaListView.add(codigoPeca + " : " + repositorio.listarIdxBaixado(codigoPeca.substring(5)));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemsDaListView);
        listViewPeca.setAdapter(arrayAdapter);
    }

    /**
     *  Metodo que verifica se o usuário realmente deseja encerrar a atividade ao pressionar Back
     */

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Saindo Do Fracionamento Granel")
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


    /**************************************************
     *   Metodos de Conversão
     **************************************************/

    /**
     *  Metodo que converte a data atual em seu formato juliano
     * @return {@link java.lang.String} contendo a data no formado YDDD
     */
    private String converterHojeEmJuliano(){
        String dataJuliano = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String anoAtual = simpleDateFormat.format(new Date());

        simpleDateFormat = new SimpleDateFormat("D");
        String diaAtual = simpleDateFormat.format(new Date());

        dataJuliano = anoAtual.substring(3) + ajustaZeros(diaAtual, 3);
        return dataJuliano;
    }

    /**
     *  Metodo que ajusta valores completando com zeros a esqueda
     * @param valorInicial {@link java.lang.String} a ser ajustada
     * @param tamanhoEsperado numero de casas que o valor deve possuir no total
     * @return {@link java.lang.String} ajustada com zeros a esquerda
     */
    private String ajustaZeros(String valorInicial, int tamanhoEsperado){
        String valorFinal = "";
        for (int i = 0; i < tamanhoEsperado - valorInicial.length(); i++){
            valorFinal += "0";
        }
        return valorFinal + valorInicial;
    }

}