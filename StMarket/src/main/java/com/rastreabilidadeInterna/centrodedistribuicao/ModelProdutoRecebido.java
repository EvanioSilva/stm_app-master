package com.rastreabilidadeInterna.centrodedistribuicao;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by Felipe Pereira on 13/04/2015.
 */
public class ModelProdutoRecebido {
    private int _id;
    private int idRecepcao;
    private String codigoBarrasCaixa;
    private String codigoBarrasProduto;
    private String nome;
    private String marca;
    private String fornecedor;
    private String setor;
    private String sif;
    private String ph;
    private String temperatura;
    private String dataFabricacao;
    private String dataValidade;
    private int totalPalets;
    private int totalCaixas;
    private int totalPedido;
    private int totalCaixasAmostradas;
    private int totalPecasRegular;
    private float totalPorcentagemRegular;
    private int totalPecasIrregular;
    private float totalPorcentagemIrregular;
    private int totalRecebido;
    private int totalDevolvido;
    private int totalPecasAmostradas;
    private int totalPecasIrregulares;
    private float totalPesoIrregular;
    private String naoConformidade;
    private String motivoDevolucao;
    private String observacoes;
    private String conclusao;
    private int pecasPorCaixa;
    private String peso;
    private boolean rotulado;
    private String descongelamento;
    private String codigoCDSTM;
    private String fatiamento;

    private ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> etiquetas;

    public ModelProdutoRecebido() {
    }

    public ModelProdutoRecebido(int _id, int idRecepcao, String codigoBarrasCaixa, String codigoBarrasProduto, String nome, String marca, String fornecedor, String setor, String sif, String ph, String temperatura, String dataFabricacao, String dataValidade, int totalPalets, int totalCaixas, int totalPedido, int totalCaixasAmostradas, int totalPecasRegular, float totalPorcentagemRegular, int totalPecasIrregular, float totalPorcentagemIrregular, int totalRecebido, int totalDevolvido, int totalPecasAmostradas, int totalPecasIrregulares, float totalPesoIrregular, String naoConformidade, String motivoDevolucao, String observacoes, String conclusao, ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> etiquetas, int pecasPorCaixa, String peso, boolean rotulado, String descongelamento, String codigoCDSTM, String fatiamento) {
        this._id = _id;
        this.idRecepcao = idRecepcao;
        this.codigoBarrasCaixa = codigoBarrasCaixa;
        this.codigoBarrasProduto = codigoBarrasProduto;
        this.nome = nome;
        this.marca = marca;
        this.fornecedor = fornecedor;
        this.setor = setor;
        this.sif = sif;
        this.ph = ph;
        this.temperatura = temperatura;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.totalPalets = totalPalets;
        this.totalCaixas = totalCaixas;
        this.totalPedido = totalPedido;
        this.totalCaixasAmostradas = totalCaixasAmostradas;
        this.totalPecasRegular = totalPecasRegular;
        this.totalPorcentagemRegular = totalPorcentagemRegular;
        this.totalPecasIrregular = totalPecasIrregular;
        this.totalPorcentagemIrregular = totalPorcentagemIrregular;
        this.totalRecebido = totalRecebido;
        this.totalDevolvido = totalDevolvido;
        this.totalPecasAmostradas = totalPecasAmostradas;
        this.totalPecasIrregulares = totalPecasIrregulares;
        this.totalPesoIrregular = totalPesoIrregular;
        this.naoConformidade = naoConformidade;
        this.motivoDevolucao = motivoDevolucao;
        this.observacoes = observacoes;
        this.conclusao = conclusao;
        this.etiquetas = etiquetas;
        this.pecasPorCaixa = pecasPorCaixa;
        this.peso = peso;
        this.rotulado = rotulado;
        this.descongelamento = descongelamento;
        this.codigoCDSTM = codigoCDSTM;
        this.fatiamento = fatiamento;
    }

    public String getFatiamento() {
        return fatiamento;
    }

    public void setFatiamento(String fatiamento) {
        this.fatiamento = fatiamento;
    }

    public String getCodigoCDSTM() {
        return codigoCDSTM;
    }

    public void setCodigoCDSTM(String codigoCDSTM) {
        this.codigoCDSTM = codigoCDSTM;
    }

    public String getDescongelamento() {
        return descongelamento;
    }

    public void setDescongelamento(String descongelamento) {
        this.descongelamento = descongelamento;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getIdRecepcao() {
        return idRecepcao;
    }

    public void setIdRecepcao(int idRecepcao) {
        this.idRecepcao = idRecepcao;
    }

    public String getCodigoBarrasCaixa() {
        return codigoBarrasCaixa;
    }

    public void setCodigoBarrasCaixa(String codigoBarrasCaixa) {
        this.codigoBarrasCaixa = codigoBarrasCaixa;
    }

    public String getCodigoBarrasProduto() {
        return codigoBarrasProduto;
    }

    public void setCodigoBarrasProduto(String codigoBarrasProduto) {
        this.codigoBarrasProduto = codigoBarrasProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getSif() {
        return sif;
    }

    public void setSif(String sif) {
        this.sif = sif;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
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

    public int getTotalPalets() {
        return totalPalets;
    }

    public void setTotalPalets(int totalPalets) {
        this.totalPalets = totalPalets;
    }

    public int getTotalCaixas() {
        return totalCaixas;
    }

    public void setTotalCaixas(int totalCaixas) {
        this.totalCaixas = totalCaixas;
    }

    public int getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(int totalPedido) {
        this.totalPedido = totalPedido;
    }

    public int getTotalCaixasAmostradas() {
        return totalCaixasAmostradas;
    }

    public void setTotalCaixasAmostradas(int totalCaixasAmostradas) {
        this.totalCaixasAmostradas = totalCaixasAmostradas;
    }

    public int getTotalPecasRegular() {
        return totalPecasRegular;
    }

    public void setTotalPecasRegular(int totalPecasRegular) {
        this.totalPecasRegular = totalPecasRegular;
    }

    public float getTotalPorcentagemRegular() {
        return totalPorcentagemRegular;
    }

    public void setTotalPorcentagemRegular(float totalPorcentagemRegular) {
        this.totalPorcentagemRegular = totalPorcentagemRegular;
    }

    public int getTotalPecasIrregular() {
        return totalPecasIrregular;
    }

    public void setTotalPecasIrregular(int totalPecasIrregular) {
        this.totalPecasIrregular = totalPecasIrregular;
    }

    public float getTotalPorcentagemIrregular() {
        return totalPorcentagemIrregular;
    }

    public void setTotalPorcentagemIrregular(float totalPorcentagemIrregular) {
        this.totalPorcentagemIrregular = totalPorcentagemIrregular;
    }

    public int getTotalRecebido() {
        return totalRecebido;
    }

    public void setTotalRecebido(int totalRecebido) {
        this.totalRecebido = totalRecebido;
    }

    public int getTotalDevolvido() {
        return totalDevolvido;
    }

    public void setTotalDevolvido(int totalDevolvido) {
        this.totalDevolvido = totalDevolvido;
    }

    public int getTotalPecasAmostradas() {
        return totalPecasAmostradas;
    }

    public void setTotalPecasAmostradas(int totalPecasAmostradas) {
        this.totalPecasAmostradas = totalPecasAmostradas;
    }

    public int getTotalPecasIrregulares() {
        return totalPecasIrregulares;
    }

    public void setTotalPecasIrregulares(int totalPecasIrregulares) {
        this.totalPecasIrregulares = totalPecasIrregulares;
    }

    public float getTotalPesoIrregular() {
        return totalPesoIrregular;
    }

    public void setTotalPesoIrregular(float totalPesoIrregular) {
        this.totalPesoIrregular = totalPesoIrregular;
    }

    public String getNaoConformidade() {
        return naoConformidade;
    }

    public void setNaoConformidade(String naoConformidade) {
        this.naoConformidade = naoConformidade;
    }

    public String getMotivoDevolucao() {
        return motivoDevolucao;
    }

    public void setMotivoDevolucao(String motivoDevolucao) {
        this.motivoDevolucao = motivoDevolucao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getConclusao() {
        return conclusao;
    }

    public void setConclusao(String conclusao) {
        this.conclusao = conclusao;
    }

    @Override
    public String toString() {
        return "ModelProdutoRecebido{" +
                "_id=" + _id +
                ", idRecepcao=" + idRecepcao +
                ", codigoBarrasCaixa='" + codigoBarrasCaixa + '\'' +
                ", codigoBarrasProduto='" + codigoBarrasProduto + '\'' +
                ", nome='" + nome + '\'' +
                ", marca='" + marca + '\'' +
                ", fornecedor='" + fornecedor + '\'' +
                ", setor='" + setor + '\'' +
                ", sif='" + sif + '\'' +
                ", ph='" + ph + '\'' +
                ", temperatura='" + temperatura + '\'' +
                ", dataFabricacao='" + dataFabricacao + '\'' +
                ", dataValidade='" + dataValidade + '\'' +
                ", totalPalets=" + totalPalets +
                ", totalCaixas=" + totalCaixas +
                ", totalPedido=" + totalPedido +
                ", totalCaixasAmostradas=" + totalCaixasAmostradas +
                ", totalPecasRegular=" + totalPecasRegular +
                ", totalPorcentagemRegular=" + totalPorcentagemRegular +
                ", totalPecasIrregular=" + totalPecasIrregular +
                ", totalPorcentagemIrregular=" + totalPorcentagemIrregular +
                ", totalRecebido=" + totalRecebido +
                ", totalDevolvido=" + totalDevolvido +
                ", totalPecasAmostradas=" + totalPecasAmostradas +
                ", totalPecasIrregulares=" + totalPecasIrregulares +
                ", totalPesoIrregular=" + totalPesoIrregular +
                ", naoConformidade='" + naoConformidade + '\'' +
                ", motivoDevolucao='" + motivoDevolucao + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", conclusao='" + conclusao + '\'' +
                ", etiquetas='" + etiquetas + '\'' +
                ", pecasPorCaixa='" + pecasPorCaixa + '\'' +
                ", rotulado='" + rotulado + '\'' +
                ", descongelamento='" + descongelamento + '\'' +
                ", codigoCDSTM='" + codigoCDSTM + '\'' +
                ", fatiamento='" + fatiamento + '\'' +
                '}';
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put("codigoBarrasCaixa", getCodigoBarrasCaixa());
        values.put("idRecepcao", getIdRecepcao());
        values.put("codigoBarrasProduto", getCodigoBarrasProduto());
        values.put("nome", getNome());
        values.put("marca", getMarca());
        values.put("fornecedor", getFornecedor());
        values.put("setor", getSetor());
        values.put("sif", getSif());
        values.put("ph", getPh());
        values.put("temperatura", getTemperatura());
        values.put("dataFabricacao", getDataFabricacao());
        values.put("dataValidade", getDataValidade());
        values.put("totalPalets", getTotalPalets());
        values.put("totalCaixas", getTotalCaixas());
        values.put("totalPedido", getTotalPedido());
        values.put("totalCaixasAmostradas", getTotalCaixasAmostradas());
        values.put("totalPecasRegular", getTotalPecasRegular());
        values.put("totalPorcentagemRegular", getTotalPorcentagemRegular());
        values.put("totalPecasIrregular", getTotalPecasIrregular());
        values.put("totalPorcentagemIrregular", getTotalPorcentagemIrregular());
        values.put("totalRecebido", getTotalRecebido());
        values.put("totalDevolvido", getTotalDevolvido());
        values.put("totalPecasAmostradas", getTotalPecasAmostradas());
        values.put("totalPecasIrregulares", getTotalPecasIrregulares());
        values.put("totalPesoIrregular", getTotalPesoIrregular());
        values.put("naoConformidade", getNaoConformidade());
        values.put("motivoDevolucao", getMotivoDevolucao());
        values.put("observacoes", getObservacoes());
        values.put("conclusao", getConclusao());
        values.put("pecasPorCaixa", getPecasPorCaixa());
        values.put("peso", getPeso());
        values.put("rotulado", isRotulado());
        values.put("descongelamento", getDescongelamento());
        values.put("codigoCDSTM", getCodigoCDSTM());
        values.put("fatiamento", getFatiamento());

        return values;

    }

    public int getPecasPorCaixa() {
        return pecasPorCaixa;
    }

    public void setPecasPorCaixa(int pecasPorCaixa) {
        this.pecasPorCaixa = pecasPorCaixa;
    }

    public ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public boolean isRotulado() {
        return rotulado;
    }

    public void setRotulado(boolean rotulado) {
        this.rotulado = rotulado;
    }
}
