package com.rastreabilidadeInterna.models;

import com.orm.SugarRecord;

/**
 * Created by felipe on 08/07/15.
 */
public class CodeIndex extends SugarRecord<CodeIndex>{
    private String codigoDeRastreabilidade;
    private String nomeDoProduto;
    private String nomeDoFabricante;
    private String codigoSif;
    private String codigoLote;
    private String dataFabricacao;
    private String dataValidade;
    private String origem;

    public CodeIndex() {
    }

    public CodeIndex(String codigoDeRastreabilidade, String nomeDoProduto, String nomeDoFabricante, String codigoSif, String codigoLote, String dataFabricacao, String dataValidade, String origem) {
        this.codigoDeRastreabilidade = codigoDeRastreabilidade;
        this.nomeDoProduto = nomeDoProduto;
        this.nomeDoFabricante = nomeDoFabricante;
        this.codigoSif = codigoSif;
        this.codigoLote = codigoLote;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.origem = origem;
    }

    public String getCodigoDeRastreabilidade() {
        return codigoDeRastreabilidade;
    }

    public void setCodigoDeRastreabilidade(String codigoDeRastreabilidade) {
        this.codigoDeRastreabilidade = codigoDeRastreabilidade;
    }

    public String getNomeDoProduto() {
        return nomeDoProduto;
    }

    public void setNomeDoProduto(String nomeDoProduto) {
        this.nomeDoProduto = nomeDoProduto;
    }

    public String getNomeDoFabricante() {
        return nomeDoFabricante;
    }

    public void setNomeDoFabricante(String nomeDoFabricante) {
        this.nomeDoFabricante = nomeDoFabricante;
    }

    public String getCodigoSif() {
        return codigoSif;
    }

    public void setCodigoSif(String codigoSif) {
        this.codigoSif = codigoSif;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public String getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(String dataFabricacao) {
        this.dataFabricacao = dataFabricacao;
    }

    public String getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(String dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    @Override
    public String toString() {
        return "CodIndex{" +
                "codigoDeRastreabilidade='" + codigoDeRastreabilidade + '\'' +
                ", nomeDoProduto='" + nomeDoProduto + '\'' +
                ", nomeDoFabricante='" + nomeDoFabricante + '\'' +
                ", codigoSif='" + codigoSif + '\'' +
                ", codigoLote='" + codigoLote + '\'' +
                ", dataFabricacao='" + dataFabricacao + '\'' +
                ", dataValidade='" + dataValidade + '\'' +
                ", origem='" + origem + '\'' +
                '}';
    }
}
