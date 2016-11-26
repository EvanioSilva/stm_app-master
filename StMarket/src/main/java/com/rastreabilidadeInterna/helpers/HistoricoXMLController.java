package com.rastreabilidadeInterna.helpers;

import android.os.Environment;
import android.util.Log;

import com.rastreabilidadeInterna.controleEstoque.ObjetoHistoricoControleEstoque;
import com.rastreabilidadeInterna.preparacao.ObjetoHistoricoPreparacao;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Felipe Pereira on 18/03/2015.
 */
public class HistoricoXMLController {

    public static final String TYPE_CE = "TYPE_CE";
    public static final String TYPE_FR = "TYPE_FR";
    public static final String TYPE_PR = "TYPE_PR";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

    private File folderPath;
    private File xmlPath;

    private String cliente;
    private String loja;
    private String tablet;

    private String type;

    private Date date;

    public ArrayList<ObjetoHistoricoControleEstoque> listaDeObjetosDeControleDeEstoque = new ArrayList<ObjetoHistoricoControleEstoque>();
    public ArrayList<ObjetoHistoricoPreparacao> listaDeObjetosPreparacao = new ArrayList<ObjetoHistoricoPreparacao>();


    // Type é usado para diferenciar CE, FRAC ou PREP
    public HistoricoXMLController(String cliente, String loja, String tablet, Date date, String type){

        this.cliente = cliente;
        this.loja = loja;
        this.tablet = tablet;
        this.type = type;

        folderPath = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "historico");
        xmlPath = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "historico" + File.separator + "historico_" + type + "_" + cliente + loja + tablet + "_" + sdf.format(date) +".xml");

        if (!folderPath.exists()){
            folderPath.mkdir();
        }
        if (!xmlPath.exists()){
            criarXmlVazio();
        } else {
            obterListaDeHistorico();
        }
    }

    public void adicionarObjetoPreparacao(ObjetoHistoricoPreparacao objetoHistoricoPreparacao){
        listaDeObjetosPreparacao.add(objetoHistoricoPreparacao);

        Log.i("objeto pre insercao", objetoHistoricoPreparacao.toString());

        xmlPath.delete();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("receitas_do_dia");
        doc.appendChild(rootElement);

        Attr attr = doc.createAttribute("data");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        attr.setValue(simpleDateFormat.format(new Date()));
        rootElement.setAttributeNode(attr);

        for (ObjetoHistoricoPreparacao objetoHistoricoPreparacao1 : listaDeObjetosPreparacao) {

            Log.i("objeto da inserção", objetoHistoricoPreparacao1.toString());


            Element receita = doc.createElement("receita");
            Attr codigoReceita = doc.createAttribute("codigo_receita");
            codigoReceita.setValue(objetoHistoricoPreparacao1.codigoReceita);
            Attr nomeReceita = doc.createAttribute("nome_receita");
            nomeReceita.setValue(objetoHistoricoPreparacao1.nomeReceita);
            receita.setAttributeNode(codigoReceita);
            receita.setAttributeNode(nomeReceita);
            rootElement.appendChild(receita);

            Element ingredientesReceita = doc.createElement("ingredientes");

            for (ObjetoHistoricoPreparacao.Ingrediente ingrediente : objetoHistoricoPreparacao1.ingredientesReceita){

                Log.i("ingrediente da inserção", ingrediente.toString());

                Element eIngrediente = doc.createElement("ingrediente");

                Attr codigoIngrediente = doc.createAttribute("codigo_ingrediente");
                codigoIngrediente.setValue(ingrediente.codigoIngrediente);

                Attr nomeIngrediente = doc.createAttribute("nome_ingrediente");
                nomeIngrediente.setValue(ingrediente.nomeIngrediente);

                Attr dataFabIngrediente = doc.createAttribute("data_fab_ingrediente");
                dataFabIngrediente.setValue(ingrediente.dataFabIngrediente);

                Attr dataValIngrediente = doc.createAttribute("data_val_ingrediente");
                dataValIngrediente.setValue(ingrediente.dataValIngrediente);

                Attr loteIngrediente = doc.createAttribute("lote_ingrediente");
                loteIngrediente.setValue(ingrediente.loteIngrediente);

                eIngrediente.setAttributeNode(codigoIngrediente);
                eIngrediente.setAttributeNode(nomeIngrediente);
                eIngrediente.setAttributeNode(dataFabIngrediente);
                eIngrediente.setAttributeNode(dataValIngrediente);
                eIngrediente.setAttributeNode(loteIngrediente);

                ingredientesReceita.appendChild(eIngrediente);

            }

            receita.appendChild(ingredientesReceita);

        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlPath);

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    public void adicionarObjetoHistorico(ObjetoHistoricoControleEstoque objetoHistoricoControleEstoque){
        listaDeObjetosDeControleDeEstoque.add(objetoHistoricoControleEstoque);

        xmlPath.delete();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("lista_do_dia");
        doc.appendChild(rootElement);

        Attr attr = doc.createAttribute("data_insercao");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        attr.setValue(simpleDateFormat.format(new Date()));
        rootElement.setAttributeNode(attr);

        for (ObjetoHistoricoControleEstoque objetoHistoricoControleEstoque1 : listaDeObjetosDeControleDeEstoque) {

            Element produto = doc.createElement("produto");
            rootElement.appendChild(produto);

            Element nome_produto = doc.createElement("nome_produto");
            nome_produto.appendChild(doc.createTextNode(objetoHistoricoControleEstoque1.getNomeProduto()));
            produto.appendChild(nome_produto);

            Element nome_fabricante = doc.createElement("nome_fabricante");
            nome_fabricante.appendChild(doc.createTextNode(objetoHistoricoControleEstoque1.getNomeFabricante()));
            produto.appendChild(nome_fabricante);

            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

            Element data_validade = doc.createElement("data_validade");
            data_validade.appendChild(doc.createTextNode(simpleDateFormat1.format(objetoHistoricoControleEstoque1.getDataValidade())));
            produto.appendChild(data_validade);

            Element data_fabricacao = doc.createElement("data_fabricacao");
            data_fabricacao.appendChild(doc.createTextNode(simpleDateFormat1.format(objetoHistoricoControleEstoque1.getDataFabricacao())));
            produto.appendChild(data_fabricacao);

            Element total_pecas = doc.createElement("total_pecas");
            total_pecas.appendChild(doc.createTextNode(Integer.toString(objetoHistoricoControleEstoque1.getTotalPecas())));
            produto.appendChild(total_pecas);

        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlPath);

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private void enviarArquivo(){
        FTPClient ftp = new FTPClient();
        Boolean retorno = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
            File fList[] = folderPath.listFiles();
            for(int i=0; i < (fList.length); i++){
                final File arquivo = fList[i];
                if (!arquivo.getName().equals("historico_ce_" + cliente + loja + tablet + "_" + sdf.format(new Date()) +".xml")) {
                    envioFTP(arquivo.getName());
                }
            }
        }
        }).start();
    }

    private void envioFTP(String fileName){
        String SERVIDOR = "52.204.225.11";
        String NOME = "safetrace";
        String SENHA = "9VtivgcVTy0PI";

        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(SERVIDOR,21);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ftp.login(NOME, SENHA);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            File file;
            file = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour"  + File.separator + "historico_ce" + File.separator + fileName);
            Log.d("NOME ARQUIVO", file.toString());
            FileInputStream arqEnviar = null;
            try {
                arqEnviar = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Log.i("FILE", arqEnviar.toString());

            ftp.enterLocalPassiveMode();
            try {
                ftp.changeWorkingDirectory("historico_controle_estoque");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ftp.storeFile(fileName, arqEnviar);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                arqEnviar.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            file.delete();
        }
    }

    private void obterListaDeHistorico(){
        if (type.equals(TYPE_CE)){
            obterListaDeHistoricoCE();
        } else if (type.equals(TYPE_PR)){
            obterListaDeHistoricoPR();
        }
    }

    private void obterListaDeHistoricoPR(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(xmlPath);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("receita");

        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    ObjetoHistoricoPreparacao objetoHistoricoPreparacao = new ObjetoHistoricoPreparacao();
                    objetoHistoricoPreparacao.nomeReceita = eElement.getAttribute("nome_receita");
                    objetoHistoricoPreparacao.codigoReceita = eElement.getAttribute("codigo_receita");

                    NodeList auxNodeList = eElement.getElementsByTagName("ingredientes");
                    Element ingredientes = (Element) auxNodeList.item(0);

                    NodeList nListIngredientes = ingredientes.getElementsByTagName("ingrediente");

                    for (int i = 0; i < nListIngredientes.getLength(); i++){
                        Node nInNode = nListIngredientes.item(i);
                        if (nInNode.getNodeType() == Node.ELEMENT_NODE){
                            Element iElement = (Element) nInNode;

                            ObjetoHistoricoPreparacao.Ingrediente ingrediente = objetoHistoricoPreparacao.getIngredienteInstance();

                            ingrediente.codigoIngrediente = iElement.getAttribute("codigo_ingrediente");
                            ingrediente.nomeIngrediente = iElement.getAttribute("nome_ingrediente");
                            ingrediente.dataFabIngrediente = iElement.getAttribute("data_fab_ingrediente");
                            ingrediente.dataValIngrediente = iElement.getAttribute("data_val_ingrediente");
                            ingrediente.loteIngrediente = iElement.getAttribute("lote_ingrediente");

                            Log.i("obteve", ingrediente.codigoIngrediente);
                            Log.i("obteve", ingrediente.nomeIngrediente);
                            Log.i("obteve", ingrediente.dataFabIngrediente);
                            Log.i("obteve", ingrediente.dataValIngrediente);
                            Log.i("obteve", ingrediente.loteIngrediente);

                            objetoHistoricoPreparacao.ingredientesReceita.add(ingrediente);
                        }
                    }

                    Log.i("ObjetoHistorico", objetoHistoricoPreparacao.toString());

                    listaDeObjetosPreparacao.add(objetoHistoricoPreparacao);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void obterListaDeHistoricoCE(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(xmlPath);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("produto");

        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    ObjetoHistoricoControleEstoque objetoHistoricoControleEstoque = new ObjetoHistoricoControleEstoque(
                            eElement.getElementsByTagName("nome_produto").item(0).getTextContent(),
                            eElement.getElementsByTagName("nome_fabricante").item(0).getTextContent(),
                            simpleDateFormat.parse(eElement.getElementsByTagName("data_fabricacao").item(0).getTextContent()),
                            simpleDateFormat.parse(eElement.getElementsByTagName("data_validade").item(0).getTextContent()),
                            Integer.parseInt(eElement.getElementsByTagName("total_pecas").item(0).getTextContent()),
                            new Date()
                    );

                    Log.i("ObjetoHistorico", objetoHistoricoControleEstoque.toString());

                    listaDeObjetosDeControleDeEstoque.add(objetoHistoricoControleEstoque);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean arquivoDeHoje(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(xmlPath);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();

        Node node = doc.getDocumentElement();
        Element element = (Element) node;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if (element.getAttribute("data_insercao").equals(simpleDateFormat.format(new Date()))){
            return true;
        }

        return false;
    }

    public void criarXmlVazio(){
        if (type.equals(TYPE_CE)){
            criarXmlVazioCE();
        } else if (type.equals(TYPE_PR)){
            criarXmlVazioPR();
        }
    }

    public void criarXmlVazioPR(){
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("receitas_do_dia");
            doc.appendChild(rootElement);

            Attr attr = doc.createAttribute("data");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            attr.setValue(simpleDateFormat.format(new Date()));
            rootElement.setAttributeNode(attr);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlPath);

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            Log.i("File", "saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public void criarXmlVazioCE(){

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("lista_do_dia");
            doc.appendChild(rootElement);

            Attr attr = doc.createAttribute("data_insercao");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            attr.setValue(simpleDateFormat.format(new Date()));
            rootElement.setAttributeNode(attr);


/*
            Element produto = doc.createElement("produto");
            rootElement.appendChild(produto);

            Element nome_produto = doc.createElement("nome_produto");
            nome_produto.appendChild(doc.createTextNode("Presunto"));
            produto.appendChild(nome_produto);

            Element nome_fabricante = doc.createElement("nome_fabricante");
            nome_fabricante.appendChild(doc.createTextNode("Sadia"));
            produto.appendChild(nome_fabricante);

            Element data_validade = doc.createElement("data_validade");
            data_validade.appendChild(doc.createTextNode("02/02/2002"));
            produto.appendChild(data_validade);

            Element data_fabricacao = doc.createElement("data_fabricacao");
            data_fabricacao.appendChild(doc.createTextNode("01/01/2001"));
            produto.appendChild(data_fabricacao);

            Element total_pecas = doc.createElement("total_pecas");
            total_pecas.appendChild(doc.createTextNode("2"));
            produto.appendChild(total_pecas);


            //
            produto = doc.createElement("produto");
            rootElement.appendChild(produto);

            nome_produto = doc.createElement("nome_produto");
            nome_produto.appendChild(doc.createTextNode("Queijo"));
            produto.appendChild(nome_produto);

            nome_fabricante = doc.createElement("nome_fabricante");
            nome_fabricante.appendChild(doc.createTextNode("Foodstuffs"));
            produto.appendChild(nome_fabricante);

            data_validade = doc.createElement("data_validade");
            data_validade.appendChild(doc.createTextNode("04/04/2004"));
            produto.appendChild(data_validade);

            data_fabricacao = doc.createElement("data_fabricacao");
            data_fabricacao.appendChild(doc.createTextNode("03/03/2003"));
            produto.appendChild(data_fabricacao);

            total_pecas = doc.createElement("total_pecas");
            total_pecas.appendChild(doc.createTextNode("4"));
            produto.appendChild(total_pecas);

*/

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlPath);

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            Log.i("File", "saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}
