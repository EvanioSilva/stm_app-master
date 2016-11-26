package com.rastreabilidadeInterna.centrodedistribuicao;

import android.content.ContentValues;

import com.orm.SugarRecord;

/**
 * Created by Felipe Pereira on 13/04/2015.
 */
public class ModelEtiquetaHortifruti extends SugarRecord<ModelEtiquetaHortifruti>{
    private String codigoSafe;
    private String codigoProduto;

    private ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti;

    public ModelEtiquetaHortifruti() {
    }

    public ModelEtiquetaHortifruti(String codigoSafe, String codigoProduto, ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti) {
        this.codigoSafe = codigoSafe;
        this.codigoProduto = codigoProduto;
        this.modelProdutoRecebidoHortifruti = modelProdutoRecebidoHortifruti;
    }

    public String getCodigoSafe() {
        return codigoSafe;
    }

    public void setCodigoSafe(String codigoSafe) {
        this.codigoSafe = codigoSafe;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public ModelProdutoRecebidoHortifruti getModelProdutoRecebidoHortifruti() {
        return modelProdutoRecebidoHortifruti;
    }

    public void setModelProdutoRecebidoHortifruti(ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti) {
        this.modelProdutoRecebidoHortifruti = modelProdutoRecebidoHortifruti;
    }

    @Override
    public String toString() {
        return "ModelEtiquetaHortifruti{" +
                "modelProdutoRecebidoHortifruti=" + modelProdutoRecebidoHortifruti +
                ", codigoProduto=" + codigoProduto +
                ", codigoSafe='" + codigoSafe + '\'' +
                '}';
    }

}
