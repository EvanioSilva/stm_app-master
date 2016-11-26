package com.rastreabilidadeInterna.centrodedistribuicao;

import android.content.ContentValues;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;

/**
 * Created by Felipe Pereira on 23/06/2015.
 */
public class ModelProdutoRecebidoHortifruti extends SugarRecord<ModelProdutoRecebidoHortifruti>{

    private String codigoDoProduto;
    private String nomeDoProduto;
    private String fornecedor;
    private int totalEntregueEmCaixas;
    private int caixasAvaliadas;
    private int pesoDaAmostra;
    private double podridao;
    private double defGraves;
    private double defLeves;
    private double descalibre;
    private double porcentagemPodridao;
    private double porcentagemDefGraves;
    private double porcentagemDefLeves;
    private double porcentagemDescalibre;
    private double brix;
    private String estagio;
    private String lbs;
    private String descricaoDefeitoPrincipal;
    private String demaisDefeitos;
    private String parecerFinalDoCQ;
    private double volumeEntregueKg;
    private double volumeDevolvidoCaixas;
    private double volumeDevolvidoKg;
    private double porcentagemProdutosRecebidos;
    private String codigoCDSTM;

    private String descricaoDefeitoPrincipalLeve;

    private ModelRecepcaoHortifruti modelRecepcaoHortifruti;

    @Ignore
    private ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> etiquetas;

    public ModelProdutoRecebidoHortifruti() {
    }

    public ModelProdutoRecebidoHortifruti(String codigoDoProduto, String nomeDoProduto, String fornecedor, int totalEntregueEmCaixas, int caixasAvaliadas, int pesoDaAmostra, double podridao, double defGraves, double defLeves, double descalibre, double porcentagemPodridao, double porcentagemDefGraves, double porcentagemDefLeves, double porcentagemDescalibre, double brix, String estagio, String lbs, String descricaoDefeitoPrincipal, String demaisDefeitos, String parecerFinalDoCQ, double volumeEntregueKg, double volumeDevolvidoCaixas, double volumeDevolvidoKg, double porcentagemProdutosRecebidos, String descricaoDefeitoPrincipalLeve, ModelRecepcaoHortifruti modelRecepcaoHortifruti, ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> etiquetas, String codigoCDSTM) {
        this.codigoDoProduto = codigoDoProduto;
        this.nomeDoProduto = nomeDoProduto;
        this.fornecedor = fornecedor;
        this.totalEntregueEmCaixas = totalEntregueEmCaixas;
        this.caixasAvaliadas = caixasAvaliadas;
        this.pesoDaAmostra = pesoDaAmostra;
        this.podridao = podridao;
        this.defGraves = defGraves;
        this.defLeves = defLeves;
        this.descalibre = descalibre;
        this.porcentagemPodridao = porcentagemPodridao;
        this.porcentagemDefGraves = porcentagemDefGraves;
        this.porcentagemDefLeves = porcentagemDefLeves;
        this.porcentagemDescalibre = porcentagemDescalibre;
        this.brix = brix;
        this.estagio = estagio;
        this.lbs = lbs;
        this.descricaoDefeitoPrincipal = descricaoDefeitoPrincipal;
        this.demaisDefeitos = demaisDefeitos;
        this.parecerFinalDoCQ = parecerFinalDoCQ;
        this.volumeEntregueKg = volumeEntregueKg;
        this.volumeDevolvidoCaixas = volumeDevolvidoCaixas;
        this.volumeDevolvidoKg = volumeDevolvidoKg;
        this.porcentagemProdutosRecebidos = porcentagemProdutosRecebidos;
        this.descricaoDefeitoPrincipalLeve = descricaoDefeitoPrincipalLeve;
        this.modelRecepcaoHortifruti = modelRecepcaoHortifruti;
        this.etiquetas = etiquetas;
        this.codigoCDSTM = codigoCDSTM;
    }

    public String getDescricaoDefeitoPrincipalLeve() {
        return descricaoDefeitoPrincipalLeve;
    }

    public void setDescricaoDefeitoPrincipalLeve(String descricaoDefeitoPrincipalLeve) {
        this.descricaoDefeitoPrincipalLeve = descricaoDefeitoPrincipalLeve;
    }

    public String getCodigoCDSTM() {
        return codigoCDSTM;
    }

    public void setCodigoCDSTM(String codigoCDSTM) {
        this.codigoCDSTM = codigoCDSTM;
    }

    public double getPodridao() {
        return podridao;
    }

    public void setPodridao(double podridao) {
        this.podridao = podridao;
    }

    public double getDefGraves() {
        return defGraves;
    }

    public void setDefGraves(double defGraves) {
        this.defGraves = defGraves;
    }

    public double getDefLeves() {
        return defLeves;
    }

    public void setDefLeves(double defLeves) {
        this.defLeves = defLeves;
    }

    public double getDescalibre() {
        return descalibre;
    }

    public void setDescalibre(double descalibre) {
        this.descalibre = descalibre;
    }

    public ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public ModelRecepcaoHortifruti getModelRecepcaoHortifruti() {
        return modelRecepcaoHortifruti;
    }

    public void setModelRecepcaoHortifruti(ModelRecepcaoHortifruti modelRecepcaoHortifruti) {
        this.modelRecepcaoHortifruti = modelRecepcaoHortifruti;
    }

    public String getCodigoDoProduto() {
        return codigoDoProduto;
    }

    public void setCodigoDoProduto(String codigoDoProduto) {
        this.codigoDoProduto = codigoDoProduto;
    }

    public String getNomeDoProduto() {
        return nomeDoProduto;
    }

    public void setNomeDoProduto(String nomeDoProduto) {
        this.nomeDoProduto = nomeDoProduto;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getTotalEntregueEmCaixas() {
        return totalEntregueEmCaixas;
    }

    public void setTotalEntregueEmCaixas(int totalEntregueEmCaixas) {
        this.totalEntregueEmCaixas = totalEntregueEmCaixas;
    }

    public int getCaixasAvaliadas() {
        return caixasAvaliadas;
    }

    public void setCaixasAvaliadas(int caixasAvaliadas) {
        this.caixasAvaliadas = caixasAvaliadas;
    }

    public int getPesoDaAmostra() {
        return pesoDaAmostra;
    }

    public void setPesoDaAmostra(int pesoDaAmostra) {
        this.pesoDaAmostra = pesoDaAmostra;
    }

    public double getPorcentagemPodridao() {
        return porcentagemPodridao;
    }

    public void setPorcentagemPodridao(double porcentagemPodridao) {
        this.porcentagemPodridao = porcentagemPodridao;
    }

    public double getPorcentagemDefGraves() {
        return porcentagemDefGraves;
    }

    public void setPorcentagemDefGraves(double porcentagemDefGraves) {
        this.porcentagemDefGraves = porcentagemDefGraves;
    }

    public double getPorcentagemDefLeves() {
        return porcentagemDefLeves;
    }

    public void setPorcentagemDefLeves(double porcentagemDefLeves) {
        this.porcentagemDefLeves = porcentagemDefLeves;
    }

    public double getPorcentagemDescalibre() {
        return porcentagemDescalibre;
    }

    public void setPorcentagemDescalibre(double porcentagemDescalibre) {
        this.porcentagemDescalibre = porcentagemDescalibre;
    }

    public double getBrix() {
        return brix;
    }

    public void setBrix(double brix) {
        this.brix = brix;
    }

    public String getEstagio() {
        return estagio;
    }

    public void setEstagio(String estagio) {
        this.estagio = estagio;
    }

    public String getLbs() {
        return lbs;
    }

    public void setLbs(String lbs) {
        this.lbs = lbs;
    }

    public String getDescricaoDefeitoPrincipal() {
        return descricaoDefeitoPrincipal;
    }

    public void setDescricaoDefeitoPrincipal(String descricaoDefeitoPrincipal) {
        this.descricaoDefeitoPrincipal = descricaoDefeitoPrincipal;
    }

    public String getDemaisDefeitos() {
        return demaisDefeitos;
    }

    public void setDemaisDefeitos(String demaisDefeitos) {
        this.demaisDefeitos = demaisDefeitos;
    }

    public String getParecerFinalDoCQ() {
        return parecerFinalDoCQ;
    }

    public void setParecerFinalDoCQ(String parecerFinalDoCQ) {
        this.parecerFinalDoCQ = parecerFinalDoCQ;
    }

    public double getVolumeEntregueKg() {
        return volumeEntregueKg;
    }

    public void setVolumeEntregueKg(double volumeEntregueKg) {
        this.volumeEntregueKg = volumeEntregueKg;
    }

    public double getVolumeDevolvidoCaixas() {
        return volumeDevolvidoCaixas;
    }

    public void setVolumeDevolvidoCaixas(double volumeDevolvidoCaixas) {
        this.volumeDevolvidoCaixas = volumeDevolvidoCaixas;
    }

    public double getVolumeDevolvidoKg() {
        return volumeDevolvidoKg;
    }

    public void setVolumeDevolvidoKg(double volumeDevolvidoKg) {
        this.volumeDevolvidoKg = volumeDevolvidoKg;
    }

    public double getPorcentagemProdutosRecebidos() {
        return porcentagemProdutosRecebidos;
    }

    public void setPorcentagemProdutosRecebidos(double porcentagemProdutosRecebidos) {
        this.porcentagemProdutosRecebidos = porcentagemProdutosRecebidos;
    }

    @Override
    public String toString() {
        return "ModelProdutoRecebidoHortifruti{" +
                "etiquetas=" + etiquetas +
                ", codigoDoProduto='" + codigoDoProduto + '\'' +
                ", nomeDoProduto='" + nomeDoProduto + '\'' +
                ", fornecedor='" + fornecedor + '\'' +
                ", totalEntregueEmCaixas=" + totalEntregueEmCaixas +
                ", caixasAvaliadas=" + caixasAvaliadas +
                ", pesoDaAmostra=" + pesoDaAmostra +
                ", podridao=" + podridao +
                ", defGraves=" + defGraves +
                ", defLeves=" + defLeves +
                ", descalibre=" + descalibre +
                ", porcentagemPodridao=" + porcentagemPodridao +
                ", porcentagemDefGraves=" + porcentagemDefGraves +
                ", porcentagemDefLeves=" + porcentagemDefLeves +
                ", porcentagemDescalibre=" + porcentagemDescalibre +
                ", brix=" + brix +
                ", estagio='" + estagio + '\'' +
                ", lbs='" + lbs + '\'' +
                ", descricaoDefeitoPrincipal='" + descricaoDefeitoPrincipal + '\'' +
                ", demaisDefeitos='" + demaisDefeitos + '\'' +
                ", parecerFinalDoCQ='" + parecerFinalDoCQ + '\'' +
                ", volumeEntregueKg=" + volumeEntregueKg +
                ", volumeDevolvidoCaixas=" + volumeDevolvidoCaixas +
                ", volumeDevolvidoKg=" + volumeDevolvidoKg +
                ", porcentagemProdutosRecebidos=" + porcentagemProdutosRecebidos +
                ", codigoCDSTM='" + codigoCDSTM + '\'' +
                ", descricaoDefeitoPrincipalLeve='" + descricaoDefeitoPrincipalLeve + '\'' +
                ", modelRecepcaoHortifruti=" + modelRecepcaoHortifruti +
                '}';
    }
}