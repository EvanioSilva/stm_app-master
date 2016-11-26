package com.rastreabilidadeInterna.models;

import com.orm.SugarRecord;
import com.rastreabilidadeInterna.centrodedistribuicao.ModelProdutoRecebido;

/**
 * Created by felipe on 20/08/15.
 */
public class Rotulo extends SugarRecord<Rotulo>{
    String imageUri;
    ModelProdutoRecebido modelProdutoRecebido;

    public Rotulo() {
    }

    public Rotulo(String imageUri, ModelProdutoRecebido modelProdutoRecebido) {
        this.imageUri = imageUri;
        this.modelProdutoRecebido = modelProdutoRecebido;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ModelProdutoRecebido getModelProdutoRecebido() {
        return modelProdutoRecebido;
    }

    public void setModelProdutoRecebido(ModelProdutoRecebido modelProdutoRecebido) {
        this.modelProdutoRecebido = modelProdutoRecebido;
    }
}
