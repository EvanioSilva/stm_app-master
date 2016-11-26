package com.rastreabilidadeInterna.controleEstoque;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by Felipe Pereira on 13/02/2015.
 */
public class ResultadoDeClassificacao {
    private String barcodeProduto;
    private String barcodeCaixa;
    private String nomeDoFabricante;
    private String nomeDoProduto;
    private String diasDeValidade;
    private String dataDeValidade;
    private String dataDeFabricacao;
    private String codigoSif;
    private String pesoEmGramas;
    private String numeroDePecas;
    private String codigoDoLote;
    private String setor;

    public ResultadoDeClassificacao() {
        this.barcodeProduto = "";
        this.barcodeCaixa = "";
        this.nomeDoFabricante = "";
        this.nomeDoProduto = "";
        this.diasDeValidade = "";
        this.dataDeValidade = "";
        this.dataDeFabricacao = "";
        this.codigoSif = "";
        this.pesoEmGramas = "";
        this.numeroDePecas = "";
        this.codigoDoLote = "";
        this.setor = "";
    }

    public void setDataByLinhaDeArquivo(String linhaDeArquivo) {
        // Separa a linha por *
        String[] splittedLinhaDeArquivo = linhaDeArquivo.split(Pattern.quote("*"));

        // Coloca cada parte onde se deve
        setBarcodeProduto(splittedLinhaDeArquivo[0]);
        setNomeDoFabricante(splittedLinhaDeArquivo[1]);
        setCodigoSif(splittedLinhaDeArquivo[2]);
        setNomeDoProduto(splittedLinhaDeArquivo[3]);
        setDiasDeValidade(splittedLinhaDeArquivo[4]);

        if (splittedLinhaDeArquivo.length > 5) {
            setSetor(splittedLinhaDeArquivo[5]);
        }
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getCodigoDoLote() {
        return codigoDoLote;
    }

    public void setCodigoDoLote(String codigoDoLote) {
        this.codigoDoLote = codigoDoLote;
    }

    public String getBarcodeProduto() {
        return barcodeProduto;
    }

    public void setBarcodeProduto(String barcodeProduto) {
        this.barcodeProduto = barcodeProduto;
    }

    public String getBarcodeCaixa() {
        return barcodeCaixa;
    }

    public void setBarcodeCaixa(String barcodeCaixa) {
        this.barcodeCaixa = barcodeCaixa;
    }

    public String getNomeDoFabricante() {
        return nomeDoFabricante;
    }

    public void setNomeDoFabricante(String nomeDoFabricante) {
        this.nomeDoFabricante = nomeDoFabricante;
    }

    public String getNomeDoProduto() {
        return nomeDoProduto;
    }

    public void setNomeDoProduto(String nomeDoProduto) {
        this.nomeDoProduto = nomeDoProduto;
    }

    public String getDiasDeValidade() {
        return diasDeValidade;
    }

    public void setDiasDeValidade(String diasDeValidade) {
        this.diasDeValidade = diasDeValidade;
    }

    public String getDataDeValidade() {
        return dataDeValidade;
    }

    public void setDataDeValidade(String dataDeValidade) {
        this.dataDeValidade = dataDeValidade;
    }

    public String getDataDeFabricacao() {
        return dataDeFabricacao;
    }

    public void setDataDeFabricacao(String dataDeFabricacao) {
        this.dataDeFabricacao = dataDeFabricacao;
    }

    public String getCodigoSif() {
        return codigoSif;
    }

    public void setCodigoSif(String codigoSif) {
        this.codigoSif = codigoSif;
    }

    public String getPesoEmGramas() {
        return pesoEmGramas;
    }

    public void setPesoEmGramas(String pesoEmGramas) {
        this.pesoEmGramas = pesoEmGramas;
    }

    public String getNumeroDePecas() {
        return numeroDePecas;
    }

    public void setNumeroDePecas(String numeroDePecas) {
        this.numeroDePecas = numeroDePecas;
    }

    public void setDataDeValidade(int diasDeValidade){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(this.getDataDeFabricacao()));
            c.add(Calendar.DATE, diasDeValidade);
            this.setDataDeValidade(sdf.format(c.getTime()));
            Log.i("DATAVAL Set To", dataDeValidade);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setDataDeFabricacao(int diasDeValidade){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(this.getDataDeValidade()));
            c.add(Calendar.DATE, -diasDeValidade);
            this.setDataDeFabricacao(sdf.format(c.getTime()));
            Log.i("DATAFAB Set To", dataDeFabricacao);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
