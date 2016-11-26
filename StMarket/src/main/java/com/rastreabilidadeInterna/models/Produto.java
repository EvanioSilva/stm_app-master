package com.rastreabilidadeInterna.models;

import com.orm.SugarRecord;

/**
 * Created by felipe on 09/07/15.
 */
public class Produto extends SugarRecord<Produto> {
    private String descricaoEstrategiaMercado;
    private String codigoRMS;
    private String codigoEAN;
    private String descricaoProduto;
    private String razaoSocialFornecedor;
    private String tipoProduto;
    private String diasValidade;
    private String peso;

    public Produto() {
    }

    public  Produto(
            String descricaoEstrategiaMercado,
            String codigoRMS,
            String codigoEAN,
            String descricaoProduto,
            String razaoSocialFornecedor,
            String tipoProduto,
            String diasValidade,
            String peso) {
        this.descricaoEstrategiaMercado = descricaoEstrategiaMercado;
        this.codigoRMS = codigoRMS;
        this.codigoEAN = codigoEAN;
        this.descricaoProduto = descricaoProduto;
        this.razaoSocialFornecedor = razaoSocialFornecedor;
        this.tipoProduto = tipoProduto;
        this.diasValidade = diasValidade;
        this.peso = peso;
    }

    public String getDescricaoEstrategiaMercado() {
        return descricaoEstrategiaMercado;
    }

    public void setDescricaoEstrategiaMercado(String descricaoEstrategiaMercado) {
        this.descricaoEstrategiaMercado = descricaoEstrategiaMercado;
    }

    public String getCodigoRMS() {
        return codigoRMS;
    }

    public void setCodigoRMS(String codigoRMS) {
        this.codigoRMS = codigoRMS;
    }

    public String getCodigoEAN() {
        return codigoEAN;
    }

    public void setCodigoEAN(String codigoEAN) {
        this.codigoEAN = codigoEAN;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getRazaoSocialFornecedor() {
        return razaoSocialFornecedor;
    }

    public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
        this.razaoSocialFornecedor = razaoSocialFornecedor;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public String getDiasValidade() {
        return diasValidade;
    }

    public void setDiasValidade(String diasValidade) {
        this.diasValidade = diasValidade;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "descricaoEstrategiaMercado='" + descricaoEstrategiaMercado + '\'' +
                ", codigoRMS='" + codigoRMS + '\'' +
                ", codigoEAN='" + codigoEAN + '\'' +
                ", descricaoProduto='" + descricaoProduto + '\'' +
                ", razaoSocialFornecedor='" + razaoSocialFornecedor + '\'' +
                ", tipoProduto='" + tipoProduto + '\'' +
                ", diasValidade='" + diasValidade + '\'' +
                ", peso='" + peso + '\'' +
                '}';
    }
}
