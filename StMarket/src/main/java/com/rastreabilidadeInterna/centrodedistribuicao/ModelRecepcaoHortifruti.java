package com.rastreabilidadeInterna.centrodedistribuicao;

import android.content.ContentValues;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;

/**
 * Created by Felipe Pereira on 23/06/2015.
 */
public class ModelRecepcaoHortifruti extends SugarRecord<ModelRecepcaoHortifruti>{
    private String numeroBonoRomaneio;
    private String dataRecepcao;

    @Ignore
    private ArrayList<ModelProdutoRecebidoHortifruti> modelProdutoRecebidoHortifrutiArrayList;

    public ModelRecepcaoHortifruti() {
    }

    public ModelRecepcaoHortifruti(String numeroBonoRomaneio, String dataRecepcao, ArrayList<ModelProdutoRecebidoHortifruti> modelProdutoRecebidoHortifrutiArrayList) {
        this.numeroBonoRomaneio = numeroBonoRomaneio;
        this.dataRecepcao = dataRecepcao;
        this.modelProdutoRecebidoHortifrutiArrayList = modelProdutoRecebidoHortifrutiArrayList;
    }

    public String getNumeroBonoRomaneio() {
        return numeroBonoRomaneio;
    }

    public void setNumeroBonoRomaneio(String numeroBonoRomaneio) {
        this.numeroBonoRomaneio = numeroBonoRomaneio;
    }

    public String getDataRecepcao() {
        return dataRecepcao;
    }

    public void setDataRecepcao(String dataRecepcao) {
        this.dataRecepcao = dataRecepcao;
    }

    public ArrayList<ModelProdutoRecebidoHortifruti> getModelProdutoRecebidoHortifrutiArrayList() {
        return modelProdutoRecebidoHortifrutiArrayList;
    }

    public void setModelProdutoRecebidoHortifrutiArrayList(ArrayList<ModelProdutoRecebidoHortifruti> modelProdutoRecebidoHortifrutiArrayList) {
        this.modelProdutoRecebidoHortifrutiArrayList = modelProdutoRecebidoHortifrutiArrayList;
    }

    @Override
    public String toString() {
        return "ModelRecepcaoHortifruti{" +
                ", numeroBonoRomaneio='" + numeroBonoRomaneio + '\'' +
                ", dataRecepcao='" + dataRecepcao + '\'' +
                '}';
    }

}
