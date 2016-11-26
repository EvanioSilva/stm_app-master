package com.rastreabilidadeInterna.centrodedistribuicao;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by Felipe Pereira on 13/04/2015.
 */
public class ModelRecepcao {
    private int _id;
    private String numeroDaRecepcao;
    private String placaDoCaminhao;
    private String dataDaRecepcao;
    private transient ArrayList<ModelProdutoRecebido> produtosRecebidos;

    public ModelRecepcao() {
    }

    public ModelRecepcao(String numeroDaRecepcao, String placaDoCaminhao, String dataDaRecepcao, ArrayList<ModelProdutoRecebido> produtosRecebidos) {
        this.numeroDaRecepcao = numeroDaRecepcao;
        this.placaDoCaminhao = placaDoCaminhao;
        this.dataDaRecepcao = dataDaRecepcao;
        this.produtosRecebidos = produtosRecebidos;
    }

    public ModelRecepcao(int _id, String numeroDaRecepcao, String placaDoCaminhao, String dataDaRecepcao, ArrayList<ModelProdutoRecebido> produtosRecebidos) {
        this._id = _id;
        this.numeroDaRecepcao = numeroDaRecepcao;
        this.placaDoCaminhao = placaDoCaminhao;
        this.dataDaRecepcao = dataDaRecepcao;
        this.produtosRecebidos = produtosRecebidos;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getNumeroDaRecepcao() {
        return numeroDaRecepcao;
    }

    public void setNumeroDaRecepcao(String numeroDaRecepcao) {
        this.numeroDaRecepcao = numeroDaRecepcao;
    }

    public String getPlacaDoCaminhao() {
        return placaDoCaminhao;
    }

    public void setPlacaDoCaminhao(String placaDoCaminhao) {
        this.placaDoCaminhao = placaDoCaminhao;
    }

    public String getDataDaRecepcao() {
        return dataDaRecepcao;
    }

    public void setDataDaRecepcao(String dataDaRecepcao) {
        this.dataDaRecepcao = dataDaRecepcao;
    }

    public ArrayList<ModelProdutoRecebido> getProdutosRecebidos() {
        return produtosRecebidos;
    }

    public void setProdutosRecebidos(ArrayList<ModelProdutoRecebido> produtosRecebidos) {
        this.produtosRecebidos = produtosRecebidos;
    }

    @Override
    public String toString() {
        return "ModelRecepcao{" +
                "_id=" + _id +
                ", numeroDaRecepcao='" + numeroDaRecepcao + '\'' +
                ", placaDoCaminhao='" + placaDoCaminhao + '\'' +
                ", dataDaRecepcao='" + dataDaRecepcao + '\'' +
                ", produtosRecebidos=" + produtosRecebidos +
                '}';
    }

    public ContentValues toValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put("numeroDaRecepcao", getNumeroDaRecepcao());
        contentValues.put("placaDoCaminhao", getPlacaDoCaminhao());
        contentValues.put("dataDaRecepcao", getDataDaRecepcao());

        return contentValues;
    }
}
