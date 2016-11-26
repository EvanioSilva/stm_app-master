package com.rastreabilidadeInterna.centrodedistribuicao;

import com.orm.SugarRecord;

/**
 * Created by felipe on 01/07/15.
 */
public class ModelProdutoHortifruti extends SugarRecord<ModelProdutoHortifruti> {
    String nomeProduto;
    double pesoProduto;

    public ModelProdutoHortifruti() {
    }

    public ModelProdutoHortifruti(String nomeProduto, double pesoProduto) {
        this.nomeProduto = nomeProduto;
        this.pesoProduto = pesoProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public double getPesoProduto() {
        return pesoProduto;
    }

    public void setPesoProduto(double pesoProduto) {
        this.pesoProduto = pesoProduto;
    }
}
