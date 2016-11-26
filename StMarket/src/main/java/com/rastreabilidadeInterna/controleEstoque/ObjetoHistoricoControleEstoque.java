package com.rastreabilidadeInterna.controleEstoque;

import android.os.Environment;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Felipe Pereira on 18/03/2015.
 */
public class ObjetoHistoricoControleEstoque {
    private String nomeProduto;
    private String nomeFabricante;
    private Date dataFabricacao;
    private Date dataValidade;
    private int totalPecas;
    private Date dataDeInsercao;

    public ObjetoHistoricoControleEstoque(String nomeProduto, String nomeFabricante, Date dataFabricacao, Date dataValidade, int totalPecas, Date dataDeInsercao) {
        this.nomeProduto = nomeProduto;
        this.nomeFabricante = nomeFabricante;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.totalPecas = totalPecas;
        this.dataDeInsercao = dataDeInsercao;
    }

    @Override
    public String toString() {
        return "ObjetoHistorico{" +
                "nomeProduto='" + nomeProduto + '\'' +
                ", nomeFabricante='" + nomeFabricante + '\'' +
                ", dataFabricacao=" + dataFabricacao +
                ", dataValidade=" + dataValidade +
                ", totalPecas=" + totalPecas +
                ", dataDeInsercao=" + dataDeInsercao +
                '}';
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getNomeFabricante() {
        return nomeFabricante;
    }

    public void setNomeFabricante(String nomeFabricante) {
        this.nomeFabricante = nomeFabricante;
    }

    public Date getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(Date dataFabricacao) {
        this.dataFabricacao = dataFabricacao;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public int getTotalPecas() {
        return totalPecas;
    }

    public void setTotalPecas(int totalPecas) {
        this.totalPecas = totalPecas;
    }

    public Date getDataDeInsercao() {
        return dataDeInsercao;
    }

    public void setDataDeInsercao(Date dataDeInsercao) {
        this.dataDeInsercao = dataDeInsercao;
    }
}
