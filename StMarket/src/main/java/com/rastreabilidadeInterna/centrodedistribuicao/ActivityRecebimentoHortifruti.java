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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.geral.ActivityTelaInicial;
import com.rastreabilidadeInterna.models.Produto;

import java.util.ArrayList;
import java.util.List;

public class ActivityRecebimentoHortifruti extends Activity {

    private class HelperFtpIn extends HelperFTP {

        public HelperFtpIn(Context context) {
            super(context);
        }

        public int enviarArquivos() {
            super.enviarArquivos();
            setStatus();
            return 1;
        }
    }

    List<String> list2 = new ArrayList<String>();

    private long idRecepcao;
    private long idProdutoRecebido;

    private String userName;
    private String userCpf;

    public Button btnLimpar;
    public Button btnAmostragem;

    public AutoCompleteTextView edtNomeProduto;
    public AutoCompleteTextView edtNomeFornecedor;

    public EditText edtCodigoProduto;
    public EditText edtTotalCaixas;

    public Button btnStatus;
    public HelperFtpIn helperFTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recebimento_hortifruti);

        helperFTP = new HelperFtpIn(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        obterDadosExtras();
        MapearComponentes();

        setStatus();
    }

    private void obterDadosExtras() {
        idRecepcao = getIntent().getLongExtra("idRecepcaoBanco", -1);
        idProdutoRecebido = getIntent().getLongExtra("idProdutoRecebidoBanco", -1);
        userName = getIntent().getStringExtra("Nome");
        userCpf = getIntent().getStringExtra("cpf");

        if (idRecepcao == -1 && idProdutoRecebido != -1) {
            ModelProdutoRecebidoHortifruti model = ModelProdutoRecebidoHortifruti.findById(
                    ModelProdutoRecebidoHortifruti.class, idProdutoRecebido);

            idRecepcao = model.getModelRecepcaoHortifruti().getId();
        }
    }

    private void setStatus() {

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
        btnLimpar = (Button) findViewById(R.id.btnLimpar);
        btnAmostragem = (Button) findViewById(R.id.btnAmostrar);

        edtNomeProduto = (AutoCompleteTextView) findViewById(R.id.edtNomeProduto);
        edtNomeFornecedor = (AutoCompleteTextView) findViewById(R.id.edtNomeFornecedor);

        edtCodigoProduto = (EditText) findViewById(R.id.edtCodigoProduto);
        edtTotalCaixas = (EditText) findViewById(R.id.edtTotalEntregue);

        btnStatus = (Button) findViewById(R.id.estoq_btnStatus);

        btnStatus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ActivityRecebimentoHortifruti.this);
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

        edtCodigoProduto.setOnFocusChangeListener(
                new View.OnFocusChangeListener()

                {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        //you can call or do what you want with your EditText here
                        if (!hasFocus) {
                            eliminarEspacosVazios();

                            //resultadosClassificacao = ClassificadorProdutoFabricante.classificar(edtCodigoProduto.getText().toString(), edtCodigoCaixa.getText().toString(), pathCodes);
                            //updateEditsWithResults(resultadosClassificacao);
                            //TODO Repositório

                            try {

                                List<Produto> produtos = Produto.find(Produto.class, "codigo_ean = '" + edtCodigoProduto.getText().toString() + "'");

                                if (produtos.size() > 0) {
                                    Produto produto = produtos.get(0);

                                    edtNomeProduto.setText(produto.getDescricaoProduto());
                                    edtNomeFornecedor.setText(produto.getRazaoSocialFornecedor());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

        );

        Button btnFinalizarCarga = (Button) findViewById(R.id.btnFinalizarCarga);
        btnFinalizarCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ActivityRecebimentoHortifruti.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Finalizar Carga")
                        .setMessage("Tem certeza que deseja Finalizar essa Carga?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ActivityRecebimentoHortifruti.this, ActivityControleDeEstoqueCDHortifruti.class);

                                if (getIntent().getExtras() != null) {
                                    if (getIntent().getExtras().getString("Nome") == null) {
                                        intent.putExtra("Nome", getSharedPreferences("Preferences", 0).getString("Nome", ""));
                                    } else {
                                        intent.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                                    }

                                    if (getIntent().getExtras().getString("cpf") == null) {
                                        intent.putExtra("cpf", getSharedPreferences("Preferences", 0).getString("cpf", ""));
                                    } else {
                                        intent.putExtra("cpf", getIntent().getExtras().getString("cpf"));
                                    }
                                }

                                startActivity(intent);
                                finish();
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        btnLimpar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                limpar();
            }
        });

        btnAmostragem.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if ((!list2.contains(edtNomeProduto.getText().toString()) && edtCodigoProduto.getText().toString().isEmpty())){
                    Toast.makeText(ActivityRecebimentoHortifruti.this, "Para adicionar um produto fora da lista é necessário inserir um código", Toast.LENGTH_LONG).show();
                } else {

                    Intent i = new Intent(getBaseContext(), ActivityAmostragemHortifruti.class);

                    ModelRecepcaoHortifruti modelRecepcaoHortifruti = ModelRecepcaoHortifruti.findById(
                            ModelRecepcaoHortifruti.class, idRecepcao
                    );

                    ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti =
                            new ModelProdutoRecebidoHortifruti();

                    modelProdutoRecebidoHortifruti.setCodigoDoProduto(
                            edtCodigoProduto.getText().toString());
                    modelProdutoRecebidoHortifruti.setNomeDoProduto(
                            edtNomeProduto.getText().toString());
                    modelProdutoRecebidoHortifruti.setFornecedor(
                            edtNomeFornecedor.getText().toString());
                    modelProdutoRecebidoHortifruti.setTotalEntregueEmCaixas(
                            edtTotalCaixas.getText().toString().isEmpty()
                                    ? -1
                                    : Integer.parseInt(edtTotalCaixas.getText().toString()
                            )
                    );


                    modelProdutoRecebidoHortifruti.setModelRecepcaoHortifruti(modelRecepcaoHortifruti);

                    modelProdutoRecebidoHortifruti.save();

                    i.putExtra("idProdutoRecebidoBanco", modelProdutoRecebidoHortifruti.getId());

                    try {
                        validarCampos();

                        if (getIntent().getExtras() != null) {
                            if (getIntent().getExtras().getString("Nome") == null) {
                                i.putExtra("Nome", getSharedPreferences("Preferences", 0).getString("Nome", ""));
                            } else {
                                i.putExtra("Nome", getIntent().getExtras().getString("Nome"));
                            }

                            if (getIntent().getExtras().getString("cpf") == null) {
                                i.putExtra("cpf", getSharedPreferences("Preferences", 0).getString("cpf", ""));
                            } else {
                                i.putExtra("cpf", getIntent().getExtras().getString("cpf"));
                            }

                            if (getIntent().getExtras().getString("numeroRecepcao") == null) {
                                i.putExtra("numeroRecepcao", getSharedPreferences("Preferences", 0).getString("numeroRecepcao", ""));
                            } else {
                                i.putExtra("numeroRecepcao", getIntent().getExtras().getString("numeroRecepcao"));
                            }

                            if (getIntent().getExtras().getString("data") == null) {
                                i.putExtra("data", getSharedPreferences("Preferences", 0).getString("data", ""));
                            } else {
                                i.putExtra("data", getIntent().getExtras().getString("data"));
                            }
                        } else {
                            i.putExtra("numeroRecepcao", getSharedPreferences("Preferences", 0).getString("numeroRecepcao", ""));
                            i.putExtra("data", getSharedPreferences("Preferences", 0).getString("data", ""));
                        }

                        if (getIntent().hasExtra("idRecepcao")) {
                            i.putExtra("idRecepcao", getIntent().getExtras().getInt("idRecepcao"));
                        }

                        i.putExtra("codigoproduto", edtCodigoProduto.getText().toString());
                        i.putExtra("nomeproduto", edtNomeProduto.getText().toString());
                        i.putExtra("fornecedor", edtNomeFornecedor.getText().toString());
                        i.putExtra("totalcx", edtTotalCaixas.getText().toString());

                        limpar();
                        startActivity(i);
                        finish();

                    } catch (Exception e) {
                        Toast.makeText(ActivityRecebimentoHortifruti.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
            }

        });

        List<String> list1 = new ArrayList<String>();
        list1.add("Selecionar");
        list1.add("AGRO COMERCIAL");
        list1.add("AGRO TERENAS");
        list1.add("ALFA CITRUS");
        list1.add("AMIGOS DO BEM INSTITUICAO");
        list1.add("ANDRÉ SOARES");
        list1.add("BELLAMESA");
        list1.add("BENASSI");
        list1.add("BERRYGOOD");
        list1.add("BETO COMERCIAL");
        list1.add("BRATRADE");
        list1.add("BRAVIS COMERCIAL");
        list1.add("CAIÇARA");
        list1.add("CANAÃ");
        list1.add("CASCAJÚ");
        list1.add("CEREALISTA NETO");
        list1.add("CHINATOWN");
        list1.add("COOPER COM");
        list1.add("COOPERALHO");
        list1.add("CORRÊA");
        list1.add("CRIS IMPORT");
        list1.add("CRISFRUT");
        list1.add("DA TERRINHA");
        list1.add("DOM DIEGO");
        list1.add("ECOSABOR");
        list1.add("EDMILSON TROMBETTA");
        list1.add("EDSON AUGUSTO");
        list1.add("ELIEZER");
        list1.add("EMPÓRIO DO ALHO");
        list1.add("ETAFRUIT");
        list1.add("FAZENDA BARRA BONITA");
        list1.add("FAZENDA DO ITALIANO");
        list1.add("FC IMPORT");
        list1.add("FISCHER");
        list1.add("FRUTACC");
        list1.add("FRUTAMINA");
        list1.add("FRUTART");
        list1.add("FRUTIZICO");
        list1.add("FRUTMEL");
        list1.add("GEOVANI MILCHESKY");
        list1.add("GILBERTO BRAGA");
        list1.add("GONÇALO PEREIRA");
        list1.add("GRAMKOM BIO");
        list1.add("GVT");
        list1.add("HATTORI COMERCIAL");
        list1.add("HÉLIO DE ATHAYDE");
        list1.add("HORTA E ARTE");
        list1.add("HORTA VITAE");
        list1.add("IANA");
        list1.add("IPANEMA");
        list1.add("ITAUEIRA");
        list1.add("IVAIR PEDRO");
        list1.add("JC GONÇALVES");
        list1.add("JKS");
        list1.add("JUAREZ/UVALE");
        list1.add("JUSTO MARQUES");
        list1.add("KATAYAMA");
        list1.add("KORIN");
        list1.add("LA FERRETI");
        list1.add("LA RIOJA");
        list1.add("LABRUNIER");
        list1.add("MAGÁRIO");
        list1.add("MALLMANN");
        list1.add("MARCELO ALABARSE");
        list1.add("MARTA ELAINE");
        list1.add("MELINA");
        list1.add("MENA KAHO");
        list1.add("MENEGHETTI IND");
        list1.add("MOACIR ALVES");
        list1.add("MORENA FRUTA");
        list1.add("NATIVE");
        list1.add("NIVALDO BEATO");
        list1.add("NOVO SABOR");
        list1.add("OKKER");
        list1.add("ONION");
        list1.add("PEABIRU");
        list1.add("PORTO ALIMENTOS");
        list1.add("PREVITALI");
        list1.add("PRIBEL");
        list1.add("PRÓ ATIVA");
        list1.add("QUEIROZ GALVÃO");
        list1.add("QUERO MAIS");
        list1.add("RAP");
        list1.add("RASIP");
        list1.add("REAL FRUTAS");
        list1.add("RJU");
        list1.add("ROQUE");
        list1.add("RUBIFRUT");
        list1.add("SANTA MARTA");
        list1.add("SANTA RITA");
        list1.add("SATOSHI");
        list1.add("SCHIO");
        list1.add("SEBASTIÃO RODRIGUES");
        list1.add("SILVANA SHIMOKAWA");
        list1.add("SILVESTRIN");
        list1.add("SOCIEDADE / BACATÃO");
        list1.add("SOJA MANIA");
        list1.add("SOLAR");
        list1.add("SP UVALE");
        list1.add("TAKAOKA");
        list1.add("TEMPERALHO");
        list1.add("TOFUTURA");
        list1.add("TOMITA ITIMURA");
        list1.add("TRADING CARREFOUR");
        list1.add("TREBESHI");
        list1.add("UGBP");
        list1.add("VALDEMAR");
        list1.add("VALDIR SCUCATO");
        list1.add("VALE VERDE");
        list1.add("VAPZA");
        list1.add("VITFRUT/ ÁUREO TAVARES");
        list1.add("VWK");
        list1.add("WAGNER TROMBETA");
        list1.add("ZUCCA");
        ArrayAdapter<String> dataAdapter1 =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list1);
        edtNomeFornecedor.setThreshold(1);
        edtNomeFornecedor.setAdapter(dataAdapter1);


        list2.add("Selecionar");
        list2.add("ABACATE  KG");
        list2.add("ABACATE AVOCADO KG");
        list2.add("ABACATE ORG VIVER 600G");
        list2.add("ABACAXI HAVAI UN");
        list2.add("ABACAXI PEROLA UN");
        list2.add("ABOBORA JAPONESA VIVER ORG 600GR");
        list2.add("ABOBORA KABOTIA/JAPONESA KG");
        list2.add("ABOBORA MORANGA KG");
        list2.add("ABOBORA PAULISTA KG");
        list2.add("ABOBORA SECA KG");
        list2.add("ABOBRINHA BRASILEIRA CRFO 600G");
        list2.add("ABOBRINHA BRASILEIRA KG");
        list2.add("ABOBRINHA BRASILEIRA ORG VIVER 600G");
        list2.add("ABOBRINHA ITALIANA CRFO 400G");
        list2.add("ABOBRINHA ITALIANA KG");
        list2.add("ABOBRINHA ITALIANA ORG VIVER 600G");
        list2.add("ABOBRINHA PAULISTA KG");
        list2.add("ACAFRAO CRFO 50G");
        list2.add("ALCACHOFRA 12 UNI");
        list2.add("ALECRIM CRFO 20 G");
        list2.add("ALFACE AMERICA ORG EMB 250G");
        list2.add("ALFACE AMERICA ORG VIVER  250G");
        list2.add("ALHO APERITIVO EM CONSERVA 390G");
        list2.add("ALHO E CEBOLA CRFO 200 G");
        list2.add("ALHO FRITO CRFO 100 G");
        list2.add("ALHO GRANEL KG");
        list2.add("ALHO IN NATURA CRFO  GO 200G");
        list2.add("ALHO IN NATURA CRFO  GO 500G");
        list2.add("ALHO IN NATURA CRFO 200G");
        list2.add("ALHO IN NATURA CRFO 500G");
        list2.add("ALHO PICADO COM SAL G.O 200G");
        list2.add("ALHO PICADO SEM SAL G.O 200G");
        list2.add("ALHO TRITURADO CRFO 1KG");
        list2.add("ALHO TRITURADO CRFO 200G");
        list2.add("ALHO TRITURADO CRFO 300G");
        list2.add("ALMONDEGA SOJA EMP");
        list2.add("AMEIXA BDJ 500G");
        list2.add("AMEIXA C CAROCO 400G");
        list2.add("AMEIXA C CAROCO CRFO 200G");
        list2.add("AMEIXA C CAROCO CRFO 400G");
        list2.add("AMEIXA IMP EMB CRFO 600G");
        list2.add("AMEIXA IMPORT KG - AL");
        list2.add("AMEIXA NAC EMB CRFO 600G");
        list2.add("AMEIXA NACIONAL KG BR");
        list2.add("AMEIXA ORG VIVER 500G");
        list2.add("AMEIXA S CAROÇO CARREFOUR 150 G");
        list2.add("AMEIXA S CAROCO CRFO 180G");
        list2.add("AMEIXA S/ CAROCO CRFO 200G");
        list2.add("AMEIXA SECA C CAROCO KG");
        list2.add("AMEIXA SECA S/CAROCO KG");
        list2.add("AMENDOA CONFEITADA CRFO 150G");
        list2.add("AMENDOA CRUA S CASCA CRFO 150G");
        list2.add("AMENDOA DEFUMADA CRFO 130G");
        list2.add("AMENDOA TORRADA SALGADA 150G");
        list2.add("AMENDOIM APIMENT SALSA CEB 140G");
        list2.add("AMENDOIM CROC APIMENTADO 110G");
        list2.add("AMENDOIM CROC NATURAL  110 GR");
        list2.add("AMENDOIM CROC SALSA CEBO 110G");
        list2.add("AMENDOIM CROCANTE APIMENTADO CRFO 150G");
        list2.add("AMENDOIM CROCANTE JAPONES 200G");
        list2.add("AMENDOIM S/PELE  150G");
        list2.add("AMENDOIM TORRADO C PELE CRFO 150G");
        list2.add("AMENDOIM TORRADO SEM PELE 150G");
        list2.add("AMORA  IMPORTADA BDJ 125G");
        list2.add("AMORA BJ 100GR");
        list2.add("AMORA IMPORTADA 170G");
        list2.add("ANIS ESTRELADO CRFO 10G");
        list2.add("ARROZ INTEGRAL VAPZA 280G");
        list2.add("ASPARGOS BCO IMPORTADO 450G");
        list2.add("ASPARGOS VERDE IMPORTADO 420G");
        list2.add("ATEMOIA ORG VIVER 500G");
        list2.add("ATEMOYA KG");
        list2.add("AVELA S CASCA CRFO 120G");
        list2.add("BACALHAU COM BATATAS VAPZA 400G");
        list2.add("BANANA BABY KG");
        list2.add("BANANA DA TERRA KG");
        list2.add("BANANA MACA KG");
        list2.add("BANANA NANICA G.O KG");
        list2.add("BANANA NANICA ORG  VIVER 600G");
        list2.add("BANANA PRATA G.O KG");
        list2.add("BANANA PRATA ORG VIVER 600G");
        list2.add("BATATA ASTERIX PCT 2 KG");
        list2.add("BATATA BOLINHA CRFO 2 KG");
        list2.add("BATATA BOLINHA PCT 2KG");
        list2.add("BATATA COZIDA VAPZA 500G");
        list2.add("BATATA COZIDA VAPZA EM CUBOS 500G");
        list2.add("BATATA DOCE BRANCA KG");
        list2.add("BATATA DOCE CRFO 400G");
        list2.add("BATATA DOCE ORG VIVER 600G");
        list2.add("BATATA DOCE ROXA KG");
        list2.add("BATATA INGLESA ORG 600G");
        list2.add("BATATA LAVADA PCT 2 KG");
        list2.add("BATATA ORG  VIVER 600G");
        list2.add("BATATA ROXA CRFO 400G");
        list2.add("BERINJELA CRFO 400G");
        list2.add("BERINJELA KG");
        list2.add("BERINJELA ORG VIVER 600G");
        list2.add("BETERRABA CRFO 400G");
        list2.add("BETERRABA CUBADA VAPZA 500G");
        list2.add("BETERRABA KG");
        list2.add("BETERRABA ORG 600G");
        list2.add("BETERRABA ORG VIVER 600G");
        list2.add("BICARBONATO DE SODIO CRFO 50G");
        list2.add("BROCOLIS NINJA 300G");
        list2.add("BROCOLIS NINJA ORG 300G");
        list2.add("BROCOLIS NINJA ORG VIVER 300G");
        list2.add("CACAU KG");
        list2.add("CAJA MANGA KG");
        list2.add("CAMOMILA CRFO 10G");
        list2.add("CANELA EM CASCA CRFO 10G");
        list2.add("CANELA EM PO CRFO 30G");
        list2.add("CANJICA AMARELA VAPZA 280 GR");
        list2.add("CANJICA BRANCA VAPZA 280 GR");
        list2.add("CANJICA WAPZA 500G");
        list2.add("CAQUI CHOCOLATE KG");
        list2.add("CAQUI FUYU KG");
        list2.add("CAQUI FUYU ORG VIVER 600G");
        list2.add("CAQUI RAMA FORTE BAND 500G");
        list2.add("CAQUI RAMA FORTE CRFO 500G");
        list2.add("CAQUI RAMA FORTE KG");
        list2.add("CAQUI RAMA FORTE ORG VIVER 600G");
        list2.add("CARA KG");
        list2.add("CARAMBOLA KG");
        list2.add("CARNE EM TIRAS VAPZA 400G");
        list2.add("CARNE SECA CURADA COZIDA DESFIADA VAPZA");
        list2.add("CASTANHA  TORRADA SEM SAL CRFO 500G");
        list2.add("CASTANHA CAJU TORR SALG CFRO GO 190G");
        list2.add("CASTANHA CAJU TRITURADA CRFO 110G");
        list2.add("CASTANHA CARAMELIZ C GERGELIM CRFO 100G");
        list2.add("CASTANHA CARAMELIZ C GERGELIM CRFO 200G");
        list2.add("CASTANHA CARAMELIZ C GERGELIM CRFO 200G");
        list2.add("CASTANHA CARAMELIZADA CRFO 100G");
        list2.add("CASTANHA CARAMELIZADA CRFO 200G");
        list2.add("CASTANHA DE CAJU 100 G CX");
        list2.add("CASTANHA DO BRASIL SEM CASCA CRFO 120G");
        list2.add("CASTANHA DO PARA E CASTANHA DE CAJU CRFO 150G");
        list2.add("CASTANHA NATURAL CRFO 100G");
        list2.add("CASTANHA NATURAL CRFO 200G");
        list2.add("CASTANHA NATURAL CRFO 500G");
        list2.add("CASTANHA PARA C/CASCA KG");
        list2.add("CASTANHA PARA G.O S/ CASCA 200G");
        list2.add("CASTANHA PARA S/CASCA KG");
        list2.add("CASTANHA PORTUGUESA KG");
        list2.add("CASTANHA TORRADA SALGADA CRFO 100G");
        list2.add("CASTANHA TORRADA SALGADA CRFO 200G");
        list2.add("CASTANHA TORRADA SALGADA CRFO 500G");
        list2.add("CEBOLA  KG");
        list2.add("CEBOLA BRANCA CRFO 500G");
        list2.add("CEBOLA NACIONAL PCT 1 KG");
        list2.add("CEBOLA ORG 500G");
        list2.add("CEBOLA ORG VIVER 500G");
        list2.add("CEBOLA PIRULITO CRFO 1KG");
        list2.add("CEBOLA PIRULITO PCT 1KG");
        list2.add("CEBOLA ROXA CRFO 500G");
        list2.add("CEBOLA ROXA KG");
        list2.add("CENOURA BABY PCTE 250G");
        list2.add("CENOURA CRFO 500G");
        list2.add("CENOURA KG");
        list2.add("CENOURA ORG 600G");
        list2.add("CENOURA ORG VIVER 600G");
        list2.add("CEREJA KG");
        list2.add("CHA VERDE CRFO 20G");
        list2.add("CHIMICHURRI CRFO 20G");
        list2.add("CHUCHU CRFO 500G");
        list2.add("CHUCHU KG");
        list2.add("CHUCHU ORG VIVER 600G");
        list2.add("CIDRA KG");
        list2.add("COCO SECO KG");
        list2.add("COCO VERDE UN");
        list2.add("COENTRO MOIDO CRFO 30G");
        list2.add("COGUMELO AGARICUS BLAZEI SECO 200G");
        list2.add("COGUMELO DUO SALUTARE ORG FUNZIONALE 200G");
        list2.add("COGUMELO MIX SHIMEJI PARIS FZ SAO JOSE 200G");
        list2.add("COGUMELO PARIS FZ SAO JOSE NAT 200 G");
        list2.add("COGUMELO PARIS NATURAL CRFO GO 150G");
        list2.add("COGUMELO PARIS NATURAL MAX CRFO GO 150G");
        list2.add("COGUMELO PARIS NATURAL MAXIMO GO 200G");
        list2.add("COGUMELO PLEUROTUS BCO CRFO GO 150G");
        list2.add("COGUMELO PLEUROTUS BRANCO SECO CRFO GO");
        list2.add("COGUMELO PLEUROTUS SALMON CRFO");
        list2.add("COGUMELO PORTO BELO CRFO GO 200G");
        list2.add("COGUMELO SHIMEJI BCO CRFO GO 150G");
        list2.add("COGUMELO SHIMEJI BCO FZ SAO JOSE 200G");
        list2.add("COGUMELO SHIMEJI ORG FUNZIONALE ZUCCA 200G");
        list2.add("COGUMELO SHIMEJI PRETO CRFO GO 150G");
        list2.add("COGUMELO SHIMEJI PRETO FAZ SAO");
        list2.add("COGUMELO SHIMEJI PRETO ZUCCA GO   200G");
        list2.add("COGUMELO SHITAKE CRFO 200G");
        list2.add("COGUMELO SHITAKE CRFO GO 150G");
        list2.add("COGUMELO SHITAKE FATIADO CRFO GO 150G");
        list2.add("COGUMELO SHITAKE ORG FUNZIONALE ZUCCA 200G");
        list2.add("COGUMELO TUTTI FUNGHI DUO CRFO GO 150G");
        list2.add("COGUMELO TUTTI FUNGHI DUO G O 200G ZUCCA");
        list2.add("COGUMELO TUTTI FUNGHI TRIO CRFO GO 150G");
        list2.add("COLORAU CRFO 100G");
        list2.add("COMINHO COM PIMENTA CRFO 100G");
        list2.add("COMINHO EM PO CRFO 100G");
        list2.add("COUVE BROCOLIS ORG 300G");
        list2.add("COUVE FLOR 300G");
        list2.add("COUVE FLOR ORG 350G");
        list2.add("COUVE FLOR ORG VIVER 350G");
        list2.add("CURRY CRFO 10G");
        list2.add("DAMASCO DOCE TURCO 250G");
        list2.add("DAMASCO E AMEIXA CRFO 200G");
        list2.add("DAMASCO E TAMARA CRFO 200G");
        list2.add("DAMASCO NATURAL EXTRA MACIO ELMAS 400G");
        list2.add("DAMASCO SECO ELMAS 200G");
        list2.add("DAMASCO SECO ELMAS 400G");
        list2.add("DAMASCO SECO EXTRA MACIA ELMAS 400G");
        list2.add("DAMASCO TURCO SECO CRFO 200G");
        list2.add("DOYPACK DAMASCO SECO 200G");
        list2.add("ERVA CIDREIRA CRFO 10G");
        list2.add("ERVA DOCE CRFO 10G");
        list2.add("ERVAS FINAS CRFO 10G");
        list2.add("ERVILHA TORTA CRFO 250G");
        list2.add("ERVILHA TORTA VIVER 200 G");
        list2.add("FEIJAO BRANCO VAPZA 500G");
        list2.add("FEIJAO CARIOCA VAPZA 280 GR");
        list2.add("FEIJAO CARIOCA VAPZA 500G");
        list2.add("FEIJÃO FRADINHO VAPZA 280 GR");
        list2.add("FEIJAO PRETO VAPZA 280 GR");
        list2.add("FEIJAO PRETO VAPZA 500G");
        list2.add("FEIJOADA VAPZA 500 G");
        list2.add("FIGO DA INDIA KG");
        list2.add("FIGO ROXO C/8 UN");
        list2.add("FIGO SECO SOFT ELMAS 400G");
        list2.add("FIGO SECO TURCO SELO OURO 250G - EX");
        list2.add("FIGO TURCO 250G");
        list2.add("FIGO TURCO SECO 200 GR SELECTION");
        list2.add("FIGO VERDE KG");
        list2.add("FOLHAS DE BOLDO CRFO 5 G");
        list2.add("FOLHAS DE LOURO CRFO 5 G");
        list2.add("FRAMBOESA IMP BERRYGOOD 125G");
        list2.add("FRANGO COZ. DESFIADO L.V 400G");
        list2.add("FRUTAS CRISTALIZADAS CUBINHOS CRFO 150G");
        list2.add("GENGIBRE CRFO 300G");
        list2.add("GENGIBRE KG");
        list2.add("GERGELIM KG");
        list2.add("GERMINADO DE LENTILHA 150G");
        list2.add("GERMINADO-CROCMIX 100G");
        list2.add("GERMINADO-FEIJAO AZUKI 100GR");
        list2.add("GERMINADO-GR.O DE BICO  150G");
        list2.add("GOIABA BRANCA KG");
        list2.add("GOIABA BRANCA ORG VIVER 500G");
        list2.add("GOIABA VERMELHA KG");
        list2.add("GOIABA VERMELHA ORG VIVER 500G");
        list2.add("GOMA PRONTA PARA TAPIOCA 1KG");
        list2.add("GOMA PRONTA PARA TAPIOCA 500G");
        list2.add("GRANADILLA KG");
        list2.add("GRAO BICO VAPZA 500G");
        list2.add("GRAPE FRUIT IMP KG");
        list2.add("GRAPEFRUIT NACIONAL  KG");
        list2.add("GRAVIOLA KG");
        list2.add("HAMB SOJA EMP ERVAS FINAS");
        list2.add("HAMB SOJA EMP OREGANO");
        list2.add("HAMB SOJA EMP SALSA ALHO");
        list2.add("HIBISCUS CRFO 20G");
        list2.add("INHAME KG");
        list2.add("INHAME ORG VIVER 600G");
        list2.add("JABUTICABA EMB CRFO 500G");
        list2.add("JABUTICABA KG");
        list2.add("JACA KG");
        list2.add("JAMBO ROSA KG");
        list2.add("JATOBÁ");
        list2.add("JENIPAPO KG");
        list2.add("JILO CRFO 300G");
        list2.add("JILO KG");
        list2.add("JILO ORG VIVER 400 GR");
        list2.add("KINO KG");
        list2.add("KIWI IMPORT KG - AL");
        list2.add("KIWI NACIONAL KG");
        list2.add("KIWI TURMA DA MONICA FICHER 600G");
        list2.add("LARANJA BAHIA G.O 2KG");
        list2.add("LARANJA BAHIA IMPORTADA KG");
        list2.add("LARANJA BAHIA NAC KG");
        list2.add("LARANJA BAIA CRFO GO 2KG");
        list2.add("LARANJA KINKAN KG");
        list2.add("LARANJA LIMA  KG");
        list2.add("LARANJA LIMA G.O PCT 2KG");
        list2.add("LARANJA LIMA ORG VIVER 1KG");
        list2.add("LARANJA PERA G.O PCT 3 KG");
        list2.add("LARANJA PERA KG");
        list2.add("LARANJA PERA ORG VIVER 1KG");
        list2.add("LARANJA PREMIUM KG");
        list2.add("LAV FR VERD PURY VITTA 350 ML");
        list2.add("LENTILHA VAPZA & VUPT LV 280G");
        list2.add("LICHIA 450GR");
        list2.add("LIMA PERSIA KG");
        list2.add("LIMAO ORG  VIVER 500G");
        list2.add("LIMAO ORG 500G");
        list2.add("LIMAO SICILIANO KG");
        list2.add("LIMAO TAHITI G.O KG");
        list2.add("LIMAO TAHITI G.O PCT 1KG");
        list2.add("MACA CRFO GO 1KG");
        list2.add("MACA FUJI CAT 2KG");
        list2.add("MACA FUJI KG");
        list2.add("MACA GALA KG");
        list2.add("MACA GALINHA PINTADINHA PCT 1KG");
        list2.add("MACA GRAN SMITH KG");
        list2.add("MACA ORG VIVER 400G");
        list2.add("MACA RED IMPORT KG - AL");
        list2.add("MACA SENINHA PCT 1KG");
        list2.add("MACA TURMA DA MONICA PCT 1KG");
        list2.add("MACADAMIA CRFO 120G");
        list2.add("MACADAMIA E AVELA CRFO 140G");
        list2.add("MACAXEIRA KG");
        list2.add("MAMAO FORMOSA GO KG");
        list2.add("MAMAO FORMOSA KG");
        list2.add("MAMAO PAPAYA G.O KG");
        list2.add("MANDIOCA COZIDA VAPZA 500G");
        list2.add("MANDIOCA DESCASCADA ORG VIVER 600G");
        list2.add("MANDIOCA RAIZ KG");
        list2.add("MANDIOCA SERTANEJA 700G");
        list2.add("MANDIOQUINHA CRFO 250G");
        list2.add("MANDIOQUINHA KG");
        list2.add("MANDIOQUINHA ORG VIVER 400G");
        list2.add("MANDIOQUINHA VAPZA 250G");
        list2.add("MANDIOQUINHA VAPZA 500G");
        list2.add("MANGA BOURBON KG");
        list2.add("MANGA HADEN KG");
        list2.add("MANGA ORG VIVER 600G");
        list2.add("MANGA PALMER  KG");
        list2.add("MANGA ROSA KG");
        list2.add("MANGA TOMMY KG");
        list2.add("MANGOSTIN KG");
        list2.add("MANJERICAO CRFO 10G");
        list2.add("MARACUJA AZEDO KG");
        list2.add("MARACUJA DOCE KG");
        list2.add("MAXIXE CRFO 300G");
        list2.add("MELANCIA BABY KG");
        list2.add("MELANCIA GO KG");
        list2.add("MELANCIA UN");
        list2.add("MELAO AMARELO KG");
        list2.add("MELAO ANDINO KG");
        list2.add("MELAO CANTALUPE KG");
        list2.add("MELAO CHARENTEAL");
        list2.add("MELAO GALIA KG");
        list2.add("MELAO NET MELOW KG");
        list2.add("MELAO ORANGE KG");
        list2.add("MELAO PELE SAPO KG");
        list2.add("MELÃO PELE SAPO REDE GO KG");
        list2.add("MELAO REDINHA G.O KG");
        list2.add("MELISSA CRFO 10G");
        list2.add("MEXERICA CRAVO KG");
        list2.add("MEXERICA DECOPOM  KG");
        list2.add("MEXERICA IMPORT URUGUAI KG - AL");
        list2.add("MEXERICA MURGOTE KG");
        list2.add("MEXERICA PONKAN KG");
        list2.add("MEXERICA RIO KG");
        list2.add("MEXERICA VERONA KG");
        list2.add("MILHO VERDE COZIDO L. VAPZA");
        list2.add("MILHO VERDE CRFO 600G");
        list2.add("MILHO VERDE ORG VIVER 500G");
        list2.add("MINI ABOBORA PUMPKINO KG");
        list2.add("MIRTILO IMPORTADO BDJ 125G");
        list2.add("MIX AMENDOIM 550G");
        list2.add("MIX AMENDOIM CRFO 550G");
        list2.add("MIX CUB DE FRUTAS AVELÃ AMENDOA");
        list2.add("MIX DE FRUTAS SECAS CRFO 130G");
        list2.add("MIX FRUTAS SECAS QUADRADO ZUCCA");
        list2.add("MIX FRUTAS SECAS REDONDO ZUCCA");
        list2.add("MIX FRUTAS SECAS REDONDO ZUCCA");
        list2.add("MIX FRUTAS SECAS RETANGULAR ZUCCA");
        list2.add("MIX PASTA DE FRUTAS SECAS DAMASCO");
        list2.add("MIX PASTA DE FRUTAS SECAS FIGO");
        list2.add("MONOPORÇÃO DAMASCO SECO 35G");
        list2.add("NECTAR DE LARANJA ORG GRAMKOW");
        list2.add("NECTAR DE LARANJA ORG GRAMKOW BIO 1L");
        list2.add("NECTAR GOIABA ORG GRAMKOW 1L");
        list2.add("NECTAR GOIABA ORG GRAMKOW 300ml");
        list2.add("NECTAR LARANJA ORG GRAMKOW BIO 300ML");
        list2.add("NECTAR MANGA ORG GRAMKOW 1L");
        list2.add("NECTAR MANGA ORG GRAMKOW 300ml");
        list2.add("NECTAR MARACUJA ORG NATIVE 1l");
        list2.add("NECTAR MARACUJA ORGANICO GRAMKOW 1l");
        list2.add("NECTAR MARACUJA ORGANICO GRAMKOW 300ml");
        list2.add("NECTAR MARACUJA ORGANICO NATIVE 200 ML");
        list2.add("NECTAR MORAN ORG GRAMKOW 1L");
        list2.add("NECTAR MORANGO ORG GRAMKOW 300ml");
        list2.add("NECTAR UVA ORG GRAMKOW 1L");
        list2.add("NECTAR UVA ORG GRAMKOW 300ml");
        list2.add("NECTAR UVA ORGANICO NATIVE 1L");
        list2.add("NECTAR UVA ORGANICO NATIVE 200ML");
        list2.add("NECTARINA IMPORT KG - AL");
        list2.add("NECTARINA NACIONAL KG BR");
        list2.add("NECTARINA ORG VIVER 500G");
        list2.add("NESPERA KG");
        list2.add("NOZ MOSCADA MOIDA CRFO 10G");
        list2.add("NOZES C CASCA 400G");
        list2.add("NOZES C CASCA LA RIOJA 200G");
        list2.add("NOZES C/CASCA KG");
        list2.add("NOZES PECAN KG");
        list2.add("NOZES S CASCA 200G");
        list2.add("NOZES S CASCA CRFO 100G");
        list2.add("NOZES S/CASCA NATACHE 100G");
        list2.add("NOZES SEM CASCA KG");
        list2.add("NOZES SEM CASCA LA RIOJA 150G");
        list2.add("OREGANO CRFO 10G");
        list2.add("OVO ANA MARIA BRAGA C 10");
        list2.add("OVO ANA MARIA BRAGA C 6");
        list2.add("OVO BCO EXT PVC YANA C 20");
        list2.add("OVO BCO GD ESTOJ YANA C 12");
        list2.add("OVO BCO GDE C/12");
        list2.add("OVO CAIPIRA CRFO GO C 10");
        list2.add("OVO CAIPIRA CRFO GO C 6");
        list2.add("OVO CAIPIRA LABEL C10");
        list2.add("OVO CODORNA CRFO GO C 15");
        list2.add("OVO CODORNA CRFO GO C 30");
        list2.add("OVO DE CODORNA  CRFO C15");
        list2.add("OVO DE CODORNA  CRFO C30");
        list2.add("OVO DE CODORNA C30");
        list2.add("OVO EXTRA BCO PVC 20 CRFO");
        list2.add("OVO EXTRA VERM PVC C20 CRFO");
        list2.add("OVO GDE BCO C/6 CRFO");
        list2.add("OVO GDE BCO C12 CRFO");
        list2.add("OVO GDE BCO C20 CRFO");
        list2.add("OVO GDE BCO VIT E VIVER  C6");
        list2.add("OVO GDE BCO VIVER VIT E C 10");
        list2.add("OVO GDE VERM C12 CRFO");
        list2.add("OVO GDE VERM C20 CRFO");
        list2.add("OVO GDE VERM ITO C10");
        list2.add("OVO GDE VERM VIT E VIVER  C6");
        list2.add("OVO GDE VERM VIVER VIT E C 10");
        list2.add("OVO JUMBO KATAYAMA C/ 10");
        list2.add("OVO MEDIO BCO CRFO C12");
        list2.add("OVO VERMELHO KORIN C/10");
        list2.add("OVO VM EXT YANA C12");
        list2.add("OVOS CAIPIRA LABEL ROUGE C 6");
        list2.add("OVOS CAIPIRA ORGANICO C 6");
        list2.add("OVOS COLONIAL COCORICO C 10");
        list2.add("OVOS COLONIAL COCORICO C 6");
        list2.add("OVOS OMEGA 3 LABEL ROUGE C 10");
        list2.add("OVOS OMEGA 3 LABEL ROUGE C 6");
        list2.add("OVOS TIPO GDE BCO OMEGA 3 C/10");
        list2.add("OVOS TIPO GDE BCO OMEGA 3 C/6");
        list2.add("PALMITO IN NATURA PARA LASANHA 210GR");
        list2.add("PALMITO IN NATURA PICADO 250GR");
        list2.add("PALMITO IN NATURA PUPUNHA CARPACHIO 210GR");
        list2.add("PALMITO IN NATURA PUPUNHA CORACAO 600GR");
        list2.add("PALMITO IN NATURA PUPUNHA MISTO 500G3");
        list2.add("PALMITO IN NATURA PUPUNHA MISTO 500G3");
        list2.add("PALMITO IN NATURA PUPUNHA TOLETE 400GR");
        list2.add("PALMITO IN NATURA SPAGUET 210GR");
        list2.add("PASTA ALHO C SAL G.O 200G");
        list2.add("PASTA ALHO S SAL G.O 200G");
        list2.add("PASTA DE ALHO CRFO 200 G");
        list2.add("PEPINO CAIPIRA CRFO 500G");
        list2.add("PEPINO CAIPIRA KG");
        list2.add("PEPINO CAIPIRA ORG VIVER 400G");
        list2.add("PEPINO COMUM CRFO 500G");
        list2.add("PEPINO COMUM ORG VIVER 400G");
        list2.add("PEPINO JAPONES CAMPO KG");
        list2.add("PEPINO JAPONES CRFO 500G");
        list2.add("PEPINO JAPONES GO CRFO 400G");
        list2.add("PEPINO JAPONES ORG VIVER 400G");
        list2.add("PEPINO SALADA KG");
        list2.add("PERA ASIATICA IMPORT KG - AL");
        list2.add("PERA BEURRE ALEXANDER LUCAS KG");
        list2.add("PERA CRFO CUMBUCA 500 G");
        list2.add("PERA DANJOU IMPORT KG - AL");
        list2.add("PERA DOYENEE DU COMICE KG");
        list2.add("PÊRA ERCOLINI BDJ 1KG");
        list2.add("PERA PACKHAMS TRIPH IMPORT KG - AL");
        list2.add("PERA PORTUGUESA EMB CRFO 900G");
        list2.add("PERA PORTUGUESA KG EX");
        list2.add("PERA RED IMP KG- AL");
        list2.add("PERA TURMA DA MONICA BDJ 600G");
        list2.add("PERA WILLIANS IMP EMB CRFO 900G");
        list2.add("PERA WILLIANS IMP GIFFARD KG CALIBRE 110");
        list2.add("PERA WILLIANS IMP KG");
        list2.add("PESSEGO EMB VIVER 400G");
        list2.add("PESSEGO IMP EMB CRFO 700G");
        list2.add("PESSEGO IMPORT KG - AL");
        list2.add("PESSEGO NAC EMB CRFO 500G");
        list2.add("PESSEGO NACIONAL KG");
        list2.add("PESSEGO ORG VIVER 400G");
        list2.add("PHYSALIS KG");
        list2.add("PIMENTA AMARELA CRFO 150G");
        list2.add("PIMENTA ARDIDA CRFO 150G");
        list2.add("PIMENTA CALABRESA MOIDA CRFO 20G");
        list2.add("PIMENTA CAMBUCI CRFO 300G");
        list2.add("PIMENTA CAMBUCI ORG VIVER 250G");
        list2.add("PIMENTA DO REINO EM GR.O CRFO 20G");
        list2.add("PIMENTA DO REINO EM PO CRFO 50G");
        list2.add("PIMENTA MALAGUETA CRFO 150G");
        list2.add("PIMENTAO AMARELO CRFO 300G");
        list2.add("PIMENTAO AMARELO GO CRFO 350G");
        list2.add("PIMENTAO AMARELO KG");
        list2.add("PIMENTAO AMARELO ORG VIVER 400G");
        list2.add("PIMENTAO COLORIDO CRFO 300G");
        list2.add("PIMENTAO COLORIDO ORG VIVER 400 GR");
        list2.add("PIMENTAO CREME GO CRFO 300G");
        list2.add("PIMENTAO ROXO GO CRFO 300G");
        list2.add("PIMENTAO TRICOLOR GO CRFO 350G");
        list2.add("PIMENTAO VERDE CRFO 300G");
        list2.add("PIMENTAO VERDE CRFO GO 350G");
        list2.add("PIMENTAO VERDE KG");
        list2.add("PIMENTAO VERDE ORG VIVER 400G");
        list2.add("PIMENTAO VERM CRFO 300G");
        list2.add("PIMENTAO VERM CRFO GO 350G");
        list2.add("PIMENTAO VERM ORG VIVER 400G");
        list2.add("PIMENTAO VERMELHO KG");
        list2.add("PINHA KG");
        list2.add("PINHAO CRFO 300G");
        list2.add("PINHAO KG");
        list2.add("PISTACHE SALGADO CRFO 110G");
        list2.add("PISTACHE SALGADO SELECTION 110G");
        list2.add("POTE REDONDO AMORAS BRANCAS SECA");
        list2.add("QUIABO CRFO 300G");
        list2.add("QUIABO ORG VIVER 300GR");
        list2.add("RABANETE CRFO 400G");
        list2.add("RABANETE ORG VIVER 300G");
        list2.add("REPOLHO LISO ORG 400G");
        list2.add("REPOLHO ROXO ORG VIVER 300G");
        list2.add("REPOLHO VERDE ORG  400G");
        list2.add("REPOLHO VERDE ORG VIVER 400G");
        list2.add("ROMA KG");
        list2.add("SAPOTI KG");
        list2.add("SELETA LEGUMES VAPZA 500G");
        list2.add("SEMENTE ABOBORA SALGADA CRFO 120G");
        list2.add("SERIGUELA KG");
        list2.add("SOJABURGUER ALHO E ERVAS TOFUTURA 190G");
        list2.add("SOJABURGUER PICCANTO COM PIMENTA CAL TOF");
        list2.add("SUCO DE UVA INT SELECTION GO 1LT");
        list2.add("SUCO DE UVA INT SELECTION GO 500ML");
        list2.add("SUCO DE UVA MENA KAO ORGANICO 1 LT");
        list2.add("SUCO DE UVA MENA KAO ORGANICO 300 ml");
        list2.add("SUCO GOIABA ORGANICO GRAMKOW 1L");
        list2.add("SUCO GOIABA ORGANICO GRAMKOW 3ML");
        list2.add("SUCO LARANJA ORG NATIVE 1L");
        list2.add("SUCO LARANJA ORG NATIVE 200ml");
        list2.add("SUCO MARACUJA ORGANICO GRAMKOW");
        list2.add("SUCO MORANGO ORG GRAMKOW 1L");
        list2.add("SUCO MORANGO ORG GRAMKOW 300ML");
        list2.add("SUCO TROPICAL AÇAI C/GUARANA ORG NATIVE 1l");
        list2.add("SUCO TROPICAL AÇAI C/GUARANA ORG NATIVE 200ML");
        list2.add("SUCO TROPICAL GOIABA ORG NATIVE 1L");
        list2.add("SUCO TROPICAL GOIABA ORG NATIVE 200ML");
        list2.add("SUCO TROPICAL MANGA ORG NATIVE 1l");
        list2.add("SUCO TROPICAL MANGA ORG NATIVE 200ML");
        list2.add("SUCO UVA ORG GRAMKOW 1L");
        list2.add("SUCO UVA ORGANICO GRAMKOW 300ML");
        list2.add("SUCO UVA ORGANICO MENAKAO 300ML");
        list2.add("TAMARA C CAROCO CRFO 150G");
        list2.add("TAMARILLO KG");
        list2.add("TAMARINDO 300G");
        list2.add("TAMARINDO KG");
        list2.add("TANGERINA G.O PCTE 1KG");
        list2.add("TANGERINA MURCOT ORG VIVER 1KG");
        list2.add("TANGERINA MURGOT CRFO GO 2KG");
        list2.add("TANGERINA PONKAN ORG VIVER 1KG");
        list2.add("TANGERINA PREMIUM G.O KG");
        list2.add("TANGERINA PREMIUM KG");
        list2.add("TANGERINA SEM SEMENTE CRFO GO 500G");
        list2.add("TEMPERO BAIANO CRFO 30G");
        list2.add("TEMPERO BRUSCHETTA CRFO 30G");
        list2.add("TEMPERO VINAGRETE CRFO 20G");
        list2.add("TOFU ORG ERVAS FINAS TOFUTURA 300G");
        list2.add("TOFU ORG PICANTE TOFUTURA 300G");
        list2.add("TOFU ORG TOFUTURA 300G");
        list2.add("TOFU PREMIUM DEF TOFUTURA 300G");
        list2.add("TOFU PREMIUM DEF TOFUTURA 70G");
        list2.add("TOFUBURGUER LEGUMES E CEREAIS TOFUTURA 1");
        list2.add("TOFUBURGUER TOMATE E MILHO TOFUTURA 180G");
        list2.add("TOMATE KG");
        list2.add("TOMATE SWEET GRAPE GO 180G");
        list2.add("TOMATE CAMPO CRFO GO 200G");
        list2.add("TOMATE CAMPO CRFO GO 400G");
        list2.add("TOMATE CAQUI CRFO 400G");
        list2.add("TOMATE CAQUI KG");
        list2.add("TOMATE CAQUI ORG  500G");
        list2.add("TOMATE CAQUI ORG VIVER 500G");
        list2.add("TOMATE CARMEM CRFO 500G");
        list2.add("TOMATE CEREJA CRFO 300G");
        list2.add("TOMATE CEREJA CRFO GO 200G");
        list2.add("TOMATE CEREJA ORG 250G");
        list2.add("TOMATE CEREJA ORG VIVER 250G");
        list2.add("TOMATE GRAPE CRFO GO 150G");
        list2.add("TOMATE GRAPE ORG VIVER 180G");
        list2.add("TOMATE ITALIANO CRFO 1KG");
        list2.add("TOMATE ITALIANO CRFO 500G");
        list2.add("TOMATE ITALIANO CRFO GO 400G");
        list2.add("TOMATE ITALIANO GOURMET  1KG");
        list2.add("TOMATE ITALIANO KG");
        list2.add("TOMATE ITALIANO ORG 500G");
        list2.add("TOMATE ITALIANO ORG VIVER 500G");
        list2.add("TOMATE LONGA VIDA CEREJA ITIMURA 200G");
        list2.add("TOMATE MASCOTE ITIMURA 180G");
        list2.add("TOMATE MEDITERR CRFO GO 400G");
        list2.add("TOMATE MEDITERRANEO GO CRFO 200G");
        list2.add("TOMATE MINI ITALIANO ORG  300G");
        list2.add("TOMATE PARA MOLHO ORG 500G");
        list2.add("TOMATE PREMIUM KG");
        list2.add("TOMATE SALADA CRFO 500G");
        list2.add("TOMATE SALADA ORG 500G");
        list2.add("TOMATE SALADA ORG VIVER 500G");
        list2.add("TOMATE SECO CRFO 430G");
        list2.add("TOMATE SUMMER SUN CRFO 250G");
        list2.add("TOMATE SWEET GRAPE GO 180G");
        list2.add("TOMATE SWEET GRAPE ORG 180G");
        list2.add("TOMATE TROP CRFO GO 200G");
        list2.add("UMBU KG");
        list2.add("UVA BENITAKA BJD 500G");
        list2.add("UVA BENITAKA EMB CRFO 500G");
        list2.add("UVA BRASIL EMB CRFO 500G");
        list2.add("UVA CHRISTIMAS ROSE GO BDJ 500G");
        list2.add("UVA CRINSON  BRATRADE BDJ 500G");
        list2.add("UVA CRINSON  CRFO GO 500G");
        list2.add("UVA FESTIVAL CRFO 500G");
        list2.add("UVA FLAME CRFO GO BD 500G");
        list2.add("UVA IMPERIAL S SEMENTE 500G");
        list2.add("UVA IMPERIAL VALE PREMIUM 500 G");
        list2.add("UVA ITALIA BRATRADE BDJ 500G");
        list2.add("UVA ITALIA EMB CRFO 500G");
        list2.add("UVA JACK SALUTE GO 500 GR");
        list2.add("UVA JUBILEE CRFO GO BDJ 500G");
        list2.add("UVA NIAGARA 500G");
        list2.add("UVA NIAGARA BAND 1,2 KG");
        list2.add("UVA NIAGARA EMB CRFO 500G");
        list2.add("UVA NIAGARA ORG VIVER 500G");
        list2.add("UVA PASSA CLARA 200G");
        list2.add("UVA PASSA CLARA CRFO 150G");
        list2.add("UVA PASSA ESCURA 200G");
        list2.add("UVA PASSA ESCURA 200GR CRFO (TRADING)");
        list2.add("UVA PASSA ESCURA CRFO 200G");
        list2.add("UVA PASSA ESCURA S/ SEMENTE KG");
        list2.add("UVA PASSA ORG VIVER 200G");
        list2.add("UVA RED GLOBE  CRFO GO 500G");
        list2.add("UVA RED GLOBE  VALE PREMIUM 500G");
        list2.add("UVA REGAL CRFO GO 500G");
        list2.add("UVA RIBIER CRFO GO BDJ 500G");
        list2.add("UVA RUBI DBJ 500G");
        list2.add("UVA S SEMENTE BLACK STAR CRFO GO 500G");
        list2.add("UVA SUGAR CRYSPY GO 500 GR");
        list2.add("UVA SUGRAONE CRFO GO 500G");
        list2.add("UVA SUMMER ROYAL CRFO GO 500G");
        list2.add("UVA SWEET CELEBRATION CRFO GO 500G");
        list2.add("UVA SWEET GLOBE CRFO GO 500G");
        list2.add("UVA SWEET MAYABELLE 500G");
        list2.add("UVA SWEET SAPHIRE CFRO GO BDJ 500G");
        list2.add("UVA SWEET SUNSHINE CRFO GO BDJ 500G");
        list2.add("UVA SWEET SURPRISE GO 500GR");
        list2.add("UVA THOMPSON BLACK BRATRADE BDJ 500G");
        list2.add("UVA THOMPSON CRFO GO 500G");
        list2.add("UVA THOMPSON EMB. CRFO 500G");
        list2.add("UVA TIMCO 500G");
        list2.add("VAGEM EXTRA FINA ORG VIVER 300G");
        list2.add("VAGEM MACARRAO KG");
        list2.add("VAGEM MACARRAO CRFO 300G");
        list2.add("VAGEM ORG VIVER 300G");
        list2.add("YACON CRFO 300G");
        ArrayAdapter<String> dataAdapter2 =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list2);
        edtNomeProduto.setThreshold(1);
        edtNomeProduto.setAdapter(dataAdapter2);

        edtNomeProduto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
//                    if (!list2.contains(edtNomeProduto.getText().toString())) {
//                        edtNomeProduto.setText("");
//                    }

                    try {

                        List<Produto> produtos = Produto.find(Produto.class, "descricao_produto = '" + edtNomeProduto.getText().toString() + "'");

                        if (produtos.size() > 0) {
                            Produto produto = produtos.get(0);

                            edtCodigoProduto.setText(produto.getCodigoEAN());
                            edtNomeFornecedor.setText(produto.getRazaoSocialFornecedor());
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void limpar() {
        edtNomeFornecedor.setText("");
        edtNomeProduto.setText("");
        edtTotalCaixas.setText("");
        edtCodigoProduto.setText("");
    }

    private void validarCampos() throws Exception {
        validarCodigoDeProduto();
        validarFornecedor();
        validarProduto();
        validarTotalRecebido();
    }

    private void validarProduto() throws Exception {
        validarNaoVazia(edtNomeProduto);
    }

    private void validarFornecedor() throws Exception {
        validarNaoVazia(edtNomeFornecedor);
    }

    private void validarCodigoDeProduto() throws Exception {
        validarNaoVazia(edtCodigoProduto);
    }

    private void validarTotalRecebido() throws Exception {
        //validarNumerico(edtTotalCaixas);
    }

    private void validarNumerico(EditText editText) throws Exception {
        if (editText.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
            return;
        } else {
            throw new Exception(editText.getHint().toString() + "Precisa ser um número valido");
        }
    }

    private void validarNaoVazia(EditText editText) throws Exception {
        if (editText.getText().toString().isEmpty()) {
            throw new Exception("Preencha todos os campos (" + editText.getHint().toString() + ")");
        }
    }

    private void validarListaNaoVazia(Spinner spinner) throws Exception {
        if (spinner.getSelectedItemPosition() == 0) {
            throw new Exception("Preencha todos os campos");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_amostragem, menu);
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

    private void eliminarEspacosVazios() {
        String prod = edtCodigoProduto.getText().toString();
        prod = prod.replaceAll("[^A-Za-z0-9]", "");
        edtCodigoProduto.setText(prod);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Saindo Do Controle De Estoque")
                .setMessage("Tem certeza que deseja sair?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }
}
