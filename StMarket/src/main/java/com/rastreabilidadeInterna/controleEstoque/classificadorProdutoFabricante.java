package com.rastreabilidadeInterna.controleEstoque;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Felipe Pereira on 06/02/2015.
 */
public class classificadorProdutoFabricante {

    //  Variaveis Estaticas

    private static final String NO_LINE = "NO_LINE_FOUND_IN_FILE";

    //  Tamanhos dos arrays de retorno utilizados nas funções

    private static final int ARR_PROD_SIZE = 3;
    private static final int ARR_CAIXA_SIZE = 7;
    private static final int ARR_GERAL_SIZE = 8;

    //  Padroes de codigo de caixa
    //  Utilizando Expressões regulares para reconhecimento

    private static final String PAT_SADIA       =   "3102\\d{6}11\\d{6}91\\d{4}";
    private static final String PAT_PERDIGAO    =   "3103\\d{6}92\\d{6}10\\d{7}";
    private static final String PAT_SEARA       =   "01\\d{14}15\\d{6}((3102)|(3103))\\d{6}10\\d{6}";
    private static final String PAT_NATURAFRIG  =   "(01\\d{14}3102\\d{6}3302\\d{6}30\\d{2}11\\d{6}15\\d{6}7030\\d{8}10\\d{3})|(11\\d{6}15\\d{6}7030\\d{8}10\\d{3}01\\d{14}3102\\d{6}3302\\d{6}30\\d{2})";
    private static final String PAT_FRIBOI      =   "(01\\d{14}3102\\d{6}3302\\d{6}30\\d{3}15\\d{6}11\\d{6}7030\\d{8}10[a-zA-Z0-9]{10})|(15\\d{6}11\\d{6}7030\\d{8}10[a-zA-Z0-9]{10}01\\d{14}3102\\d{6}3302\\d{6}30\\d{3})";
    private static final String PAT_MARFRIG     =   "(02\\d{14}3102\\d{6}3302\\d{6}30\\d{2}15\\d{6}11\\d{6}(7030\\d{8}|7030\\d{7}|7030\\d{6}|7030\\d{5})10\\d{6})|(15\\d{6}11\\d{6}(7030\\d{8}|7030\\d{7}|7030\\d{6}|7030\\d{5})10\\d{6}02\\d{14}3102\\d{6}3302\\d{6}30\\d{2})";

    //  Codigos de produto conhecidos que mapeiam o vetor de informações sobre o produto
    //      Codigo de indice N em COD_INDEX é o codigo correspondente ao Nº elemento em PROD_INFO

    private static final String[] COD_INDEX =
            {
                    "1789300044490",
                    "1789300044636",
                    "1789300069620",
                    "1789151535673",
                    "1789300000483",
                    "1789300006756",
                    "1789835055010",
                    "1789300010642",
                    "1789490400960",
                    "1789300002401",
                    "1789490472695",
                    "1789300044423",
                    "1789490400928",
                    "17893000526041",
                    "17898907631089",
                    "17898350550107",
            };

    private static final String[][] PROD_INFO =
            {           // Tipo                                         SIF         Fabricante
                    {   "Apresuntado fatiado Sadia"                 ,   "716"   ,   "Sadia"                             },
                    {   "Presunto cozido magro Sadia fatiado"       ,   "1"     ,   "Sadia"                             },
                    {   "Peito de peru defumado Sadia fatiado"      ,   "3681"  ,   "Sadia"                             },
                    {   "Mortadela Bolonha Ouro Perdigão fatiada"   ,   "0277"  ,   "Perdigão"                          },
                    {   "Mortadela defumada Sadia fatiada"          ,   "1"     ,   "Sadia"                             },
                    {   "Queijo mussarela fatiado Sadia "           ,   ""      ,   "Sadia"                             },
                    {   "Queijo Minas Frescal Sol Brilhante"        ,   ""      ,   "Sol Brilhante"                     },
                    {   "Salsicha hot dog Sadia"                    ,   ""      ,   "Sadia"                             },
                    {   "Salsicha hot dog Seara"                    ,   "0022"  ,   "Seara"                             },
                    {   "Linguiça toscana grossa Sadia"             ,   "1691"  ,   "Sadia"                             },
                    {   "Salsicha Resfriada Seara"                  ,   "0022"  ,   "Seara"                             },
                    {   "Linguiça calabresa defumada Sadia"         ,   ""      ,   "Sadia"                             },
                    {   "Linguiça toscana Seara"                    ,   "0426"  ,   "Seara"                             },
                    {   "Bacon Sadia"                               ,   "716"   ,   "Sadia"                             },
                    {   "Mortadela Ceratti Bologna"                 ,   "1765"  ,   "Ceratti"                           },
                    {   "Queijo Minas Frescal"                      ,   "1352"  ,   "Laticinios Villagge Ind e Com."    },
            };


    /**
     *
     * Se codigo de produto estiver presente, pesquisar ele no arquivo
     *      Se o codigo de produto estiver no arquivo, adicionar dados ao objeto de retorno
     *      Se o codigo de produto nao estiver no arquivo, pesquisar nos dados locais (acima)
     *          Se o codigo de produto estiver nos dados locais (acima) adicionar dados ao objeto de retorno
     *          Se o codigo de produto nao estiver nos dados locais, manter retorno vazio
     * Se codigo de caixa estiver presente, tentar a classificacao por codigo de caixa
     *      Se a classificacao for bem sucedida, adicionar dados ao objeto de retorno (nao sobreescrevendo dados que ja estejam la)
     *      Se a classificacao for mal sucedida, manter retorno como está
     *
     * @param codigoDeCaixa codigo de caixa, geralmente recebido do leitor de codigo de barras
     * @param codigoDeProduto codigo de produto, geralmente recebido do leitor de codigo de barras
     * @param arquivoDeCodigos arquivo que contem os codigos de produto e suas informações
     * @return objeto ResultadoDeClassificacao contendo os dados que forem possiveis de se obter
     */

    public static ResultadoDeClassificacao classificar(String codigoDeProduto, String codigoDeCaixa, File arquivoDeCodigos){
        ResultadoDeClassificacao resultadoDeClassificacao = new ResultadoDeClassificacao();

        if (codigoDeCaixa.length() > 0){
            String[] resultadoCaixa = classificaCodigoDeCaixa(codigoDeCaixa);

            resultadoDeClassificacao.setNomeDoFabricante(resultadoCaixa[0]);
            resultadoDeClassificacao.setDataDeFabricacao(resultadoCaixa[1]);
            resultadoDeClassificacao.setDataDeValidade(resultadoCaixa[2]);
            resultadoDeClassificacao.setCodigoDoLote(resultadoCaixa[3]);
            resultadoDeClassificacao.setPesoEmGramas(resultadoCaixa[4]);
            resultadoDeClassificacao.setNumeroDePecas(resultadoCaixa[5]);
            resultadoDeClassificacao.setBarcodeProduto(resultadoCaixa[6]);

        }

        if (codigoDeProduto.length() > 0){
            String linhaDoArquivo = buscarLinhaNoArquivo(arquivoDeCodigos, codigoDeProduto);
            if (!linhaDoArquivo.equals(NO_LINE)){
                resultadoDeClassificacao.setDataByLinhaDeArquivo(linhaDoArquivo);
            } else {
                int produtoNosDadosLocais = classificaCodigoDeProduto(codigoDeProduto);
                if (produtoNosDadosLocais >= 0){
                    resultadoDeClassificacao.setNomeDoProduto(PROD_INFO[produtoNosDadosLocais][0]);
                    resultadoDeClassificacao.setCodigoSif(PROD_INFO[produtoNosDadosLocais][1]);
                    resultadoDeClassificacao.setNomeDoFabricante(PROD_INFO[produtoNosDadosLocais][2]);

                }
            }
        }

        return resultadoDeClassificacao;
    }

    /**
     * Fincao que retorna uma string contendo as informacoes contidas no arquivoi
     * O arquivo produtos_ce.txt vem do ftp e contem as informacoes na seguinte ordem:
     * CODIGODOPRODUTO*FABRICANTE*CODIGOSIF*NOMEDOPRODUTO*DIASDEVALIDADE*SETOR
     *
     * @param arquivoDeCodigos File que contem as informacoes dos produtos
     * @param codigoDeProduto String a ser encontrada
     * @return String contendo a linha do arquivo inteira, contendo as infos acima
     */

    private static String buscarLinhaNoArquivo(File arquivoDeCodigos, String codigoDeProduto){
        try {
            BufferedReader leitorBufferizado = new BufferedReader(new FileReader(arquivoDeCodigos));
            String linhaAtual;
            while ((linhaAtual = leitorBufferizado.readLine()) != null){
                String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("*"));
                if (splittedLinhaAtual[0].equals(codigoDeProduto)){
                    return linhaAtual;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return NO_LINE;
    }

    /**
     * Funcao que toma como parametro um codigo de produto e retorna sua posição no vetor de codigos local (acima)
     *
     * @param codigoDeProduto codigo do produto a ser buscado
     * @return int posicao do produto no vetor de dados local, ou -1 caso nao seja encontrado
     */

    public static int classificaCodigoDeProduto(String codigoDeProduto){
        if (Arrays.asList(COD_INDEX).contains(codigoDeProduto)) {
            return Arrays.asList(COD_INDEX).indexOf(codigoDeProduto);
        } else {
            return -1;
        }
    }


    /**
     * Funcao que recebe um codigo de caixa e compara com padroes
     *
     * @param codigoDeCaixa
     * @return Array com os resultados que podem ser retirados do codigo
     * [0] = String Nome do Fabricante
     * [1] = String Data de Fabricação YYMMDD
     * [2] = String Data de Validade YYMMDD
     * [3] = String Lote
     * [4] = Peso em Gramas
     *
     * [5] = Numero de Pecas
     * [6] = Codigo De Produto
     */


    public static String[] classificaCodigoDeCaixa(String codigoDeCaixa){
        String[] resultado = new String[ARR_CAIXA_SIZE];
        Arrays.fill(resultado, "");

        String padraoCorrespondente = encontrarPadraoCorrespondente(codigoDeCaixa);

        if (padraoCorrespondente.equals("NONE_FOUND")){
            return resultado;
        }
        resultado = obterInfoDeCodigo(codigoDeCaixa, padraoCorrespondente);

        return resultado;
    }

    /**
     * Funcao que identifica qual dos padroes conhecidos corresponde ao codigo informado
     *
     * @param codigoEntrada codigo de caixa recebido para classificacao
     * @return String padrao encontrado, ou NONE_FOUND caso nenhum corresponda
     */
    private static String encontrarPadraoCorrespondente(String codigoEntrada){
        if (codigoEntrada.matches(PAT_SADIA)){
            return PAT_SADIA;
        } else if (codigoEntrada.matches(PAT_PERDIGAO)){
            return PAT_PERDIGAO;
        } else if (codigoEntrada.matches(PAT_SEARA)){
            return PAT_SEARA;
        } else if (codigoEntrada.matches(PAT_NATURAFRIG)){
            return PAT_NATURAFRIG;
        } else if (codigoEntrada.matches(PAT_FRIBOI)){
            return PAT_FRIBOI;
        } else if (codigoEntrada.matches(PAT_MARFRIG)){
            return PAT_MARFRIG;
        } else {
            return "NONE_FOUND";
        }
    }

    /**
     *
     * @param codigoDeCaixa
     * @param padraoCorrespondente
     * @return
     */
    private static String[] obterInfoDeCodigo(String codigoDeCaixa, String padraoCorrespondente){
        if (padraoCorrespondente.equals(PAT_SADIA)){
            return obterDadosSadia(codigoDeCaixa);
        }
        if (padraoCorrespondente.equals(PAT_PERDIGAO)){
            return obterDadosPerdigao(codigoDeCaixa);
        }
        if (padraoCorrespondente.equals(PAT_SEARA)){
            return obterDadosSeara(codigoDeCaixa);
        }
        if (padraoCorrespondente.equals(PAT_NATURAFRIG)){
            return obterDadosNaturafrig(codigoDeCaixa);
        }
        if (padraoCorrespondente.equals(PAT_FRIBOI)){
            return obterDadosFriboi(codigoDeCaixa);
        }
        if (padraoCorrespondente.equals(PAT_MARFRIG)){
            return obterDadosMarfrig(codigoDeCaixa);
        }
        String[] resultado = new String[ARR_CAIXA_SIZE];
        Arrays.fill(resultado, "");
        return resultado;
    }

    //  Funcoes que montam o resultado baseados em dados possiveis de serem obtidos do codigo de caixa
    //      Diferenciado por cada fabricante diferente

    private static String[] obterDadosMarfrig(String codigoDeCaixa){
        String[] resultado = new String[ARR_CAIXA_SIZE];
        Arrays.fill(resultado, "");

        resultado[0] = "Marfrig";

        //  Pega a parte do codigo que contem as datas e o lote (independente da ordem em que
        //      as duas metades do codigo foram lidas
        Pattern padrao = Pattern.compile("15\\d{6}11\\d{6}(7030\\d{8}|7030\\d{7}|7030\\d{6}|7030\\d{5})10\\d{6}");
        Matcher correspondente = padrao.matcher(codigoDeCaixa);

        if (correspondente.find()){
            resultado[1] = correspondente.group(0).substring(10, 16);
            resultado[2] = correspondente.group(0).substring(2, 8);
            resultado[3] = correspondente.group(0).substring(correspondente.group(0).length()-6);
        }

        //  Pega a parte do codigo que contem o peso. independente da ordem em que as metades
        //      foram lidas, a data esta disponivel em decigramas para esse tipo de codigo (1/10)
        padrao = Pattern.compile("02\\d{14}3102\\d{6}3302\\d{6}30\\d{2}");
        correspondente = padrao.matcher(codigoDeCaixa);

        if (correspondente.find()){
            resultado[4] = Integer.toString(Integer.parseInt(correspondente.group(0).substring(20, 26))*10);
            resultado[5] = correspondente.group(0).substring(38);
        }

        return resultado;
    }

    private static String[] obterDadosFriboi(String codigoDeCaixa){
        String[] resultado = new String[ARR_CAIXA_SIZE];
        Arrays.fill(resultado, "");

        resultado[0] = "Friboi";

        //  Pega a parte do codigo que contem as datas e o lote (independente da ordem em que
        //      as duas metades do codigo foram lidas
        Pattern padrao = Pattern.compile("15\\d{6}11\\d{6}7030\\d{8}10([a-zA-Z0-9]{10})");
        Matcher correspondente = padrao.matcher(codigoDeCaixa);
        if (correspondente.find()){
            resultado[1] = correspondente.group(0).substring(10, 16);
            resultado[2] = correspondente.group(0).substring(2, 8);
            resultado[3] = correspondente.group(0).substring(30);
        }

        //  Pega a parte do codigo que contem o peso. independente da ordem em que as metades
        //      foram lidas, o peso esta disponivel em decigramas para esse tipo de codigo (1/10)
        padrao = Pattern.compile("01\\d{14}3102\\d{6}3302\\d{6}30\\d{3}");
        correspondente = padrao.matcher(codigoDeCaixa);
        if (correspondente.find()){
            resultado[4] = Integer.toString(Integer.parseInt(correspondente.group(0).substring(20, 26))*10);
            resultado[5] = correspondente.group(0).substring(38);
            resultado[6] = correspondente.group(0).substring(2, 16);
        }

        return resultado;
    }

    private static String[] obterDadosNaturafrig(String codigoDeCaixa){
        String[] resultado = new String[ARR_CAIXA_SIZE];
        Arrays.fill(resultado, "");

        resultado[0] = "Naturafrig";

        //  Pega a parte do codigo que contem as datas e o lote (independente da ordem em que
        //      as duas metades do codigo foram lidas
        Pattern padrao = Pattern.compile("11\\d{6}15\\d{6}7030\\d{8}10\\d{3}");
        Matcher correspondente = padrao.matcher(codigoDeCaixa);
        if (correspondente.find()){
            resultado[1] = correspondente.group(0).substring(2, 8);
            resultado[2] = correspondente.group(0).substring(10, 16);
            resultado[3] = correspondente.group(0).substring(30);
        }

        //  Pega a parte do codigo que contem o peso. independente da ordem em que as metades
        //      foram lidas, a data esta disponivel em decigramas para esse tipo de codigo (1/10)
        padrao = Pattern.compile("01\\d{14}3102\\d{6}3302\\d{6}30\\d{2}");
        correspondente = padrao.matcher(codigoDeCaixa);
        if (correspondente.find()){
            resultado[4] = Integer.toString(Integer.parseInt(correspondente.group(0).substring(20, 26))*10);
            resultado[5] = correspondente.group(0).substring(38);
        }

        return resultado;
    }

    private static String[] obterDadosSeara(String codigoDeCaixa){
        String[] resultado = new String[ARR_CAIXA_SIZE];
        Arrays.fill(resultado, "");

        resultado[0] = "Seara";
        resultado[1] = "";
        resultado[2] = codigoDeCaixa.substring(18, 24);
        resultado[3] = codigoDeCaixa.substring(36);
        if (codigoDeCaixa.substring(24, 28).equals("3103")){
            resultado[4] = codigoDeCaixa.substring(28, 34);
        } else {
            resultado[4] = Integer.toString(Integer.parseInt(codigoDeCaixa.substring(28, 34)) * 10);
        }
        resultado[6] = codigoDeCaixa.substring(2, 16);

        return resultado;
    }

    private static String[] obterDadosPerdigao(String codigoDeCaixa){
        String[] resultado = new String[ARR_CAIXA_SIZE];
        Arrays.fill(resultado, "");

        resultado[0] = "Perdigão";
        resultado[1] = YDDDtoYYMMDD(codigoDeCaixa.substring(23));
        resultado[2] = "";
        resultado[3] = codigoDeCaixa.substring(20);
        resultado[4] = "";

        return resultado;
    }

    private static String[] obterDadosSadia(String codigoDeCaixa){
        String[] resultado = new String[ARR_CAIXA_SIZE];
        Arrays.fill(resultado, "");

        resultado[0] = "Sadia";
        resultado[1] = codigoDeCaixa.substring(12, 18);
        resultado[2] = "";
        resultado[3] = "1";
        resultado[4] = Integer.toString(Integer.parseInt(codigoDeCaixa.substring(4, 10))*10);

        return resultado;
    }

    private static String[] casoEspecificoQueijoMolfino(String codCaixa){
        String[] resultGeral = new String[ARR_GERAL_SIZE];
        Arrays.fill(resultGeral, "");

        if(codCaixa.contains("97794990878805")){
            resultGeral[0] = "Queijo Mozzarella Molfino";
            resultGeral[1] = "";
            resultGeral[2] = "Molfino HNOS. S.A.";
            resultGeral[3] = "";
            resultGeral[4] = codCaixa.substring(28, 34);
            resultGeral[5] = "";
            resultGeral[6] = "";
            resultGeral[7] = "";

            return resultGeral;
        }

        return resultGeral;
    }

    //  Funcoes complementares (conversoes e formatacoes)

    private static String YDDDtoYYMMDD(String dateIn){
        String anoOriginal = dateIn.substring(0, 1);
        String diasOriginal = dateIn.substring(1);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int decadaAtual = (c.get(Calendar.YEAR)) / 10;

        c.set(Calendar.YEAR, decadaAtual * 10 + Integer.parseInt(anoOriginal));
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR, 1);
        c.set(Calendar.MINUTE, 1);
        c.set(Calendar.SECOND, 1);
        c.set(Calendar.MILLISECOND, 1);
        c.add(Calendar.DATE, Integer.parseInt(diasOriginal) - 1);
        Date date = c.getTime();

        return new SimpleDateFormat("yyMMdd").format(date);
    }

}