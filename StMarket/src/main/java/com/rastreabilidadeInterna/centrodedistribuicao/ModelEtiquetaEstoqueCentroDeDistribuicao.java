package com.rastreabilidadeInterna.centrodedistribuicao;

import android.content.ContentValues;

import com.orm.SugarRecord;

/**
 * Created by Felipe Pereira on 13/04/2015.
 */
public class ModelEtiquetaEstoqueCentroDeDistribuicao{
    private int _id;
    private String codigoSafe;
    private int codigoProduto;

    public ModelEtiquetaEstoqueCentroDeDistribuicao() {
    }

    public ModelEtiquetaEstoqueCentroDeDistribuicao(int _id, String codigoSafe, int codigoProduto) {
        this._id = _id;
        this.codigoSafe = codigoSafe;
        this.codigoProduto = codigoProduto;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCodigoSafe() {
        return codigoSafe;
    }

    public void setCodigoSafe(String codigoSafe) {
        this.codigoSafe = codigoSafe;
    }

    public int getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(int codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    @Override
    public String toString() {
        return "ModelEtiquetaEstoqueCentroDeDistribuicao{" +
                "_id=" + _id +
                ", codigoSafe='" + codigoSafe + '\'' +
                ", codigoProduto=" + codigoProduto +
                '}';
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues();
        values.put("codigoEtiqueta", getCodigoSafe());
        values.put("codigoProdutoRecebido", getCodigoProduto());
        return values;
    }
}
