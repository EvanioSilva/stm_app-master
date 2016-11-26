package com.rastreabilidadeInterna.preparacao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rastreabilidadeInterna.helpers.HistoricoXMLController;

public class objetoPreparacao {

    public String areaDeUso;
    public String cliente;
    public String loja;
    public String tablet;

    public Context context;

    public objetoPreparacao(Context context){
        this.context = context;
    }

	public void saveFileA(String fileName, String codigo, String nomeProduto, ArrayList<String> listaIngr, ArrayList<String> listaProc, String usuario, String idLoja, String numCliente) {


		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
		if (!local.exists()) {
			local.mkdir();        	
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao");
		if (!local.exists()) {
			local.mkdir();        	
		}

		try {		

			deleteFile(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao" + File.separator + fileName + ".st");
			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8"); 
			PrintWriter Print = new PrintWriter(OSW);

			String linha = "L:";        
			Print.println(linha);

			SimpleDateFormat dateNome = new SimpleDateFormat("dd/MM/yyyy");
			linha = dateNome.format(new Date( System.currentTimeMillis()));
			Print.println(linha);

			linha = numCliente;       		 //id empresa
			Print.println(linha);

			linha = idLoja;              	 //id local
			Print.println(linha);

			linha = codigo;               //codigo origem
			Print.println(linha);

			linha = "1";           	  //unidad. armazenamento
			Print.println(linha);

			linha = "10-"+nomeProduto+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);

			linha = "9-"+usuario+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);

            linha = "14-"+areaDeUso+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);

            Log.i("Teste Area", linha);

            linha = "44-Preparação-"+dateNome.format(new java.sql.Date( System.currentTimeMillis()))+"|";
            Print.print(linha);

            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

            linha = "45-"+telephonyManager.getDeviceId()+"-"+dateNome.format(new java.sql.Date( System.currentTimeMillis()))+"|";
            Print.print(linha);

			linha = "69-"+ new SimpleDateFormat("HH:mm:ss").format(new java.util.Date())+"-"+dateNome.format(new java.sql.Date( System.currentTimeMillis()))+"|";
			Print.print(linha);

			//processos
			String aux = "";
			int j=0;

			Log.i("tam", "lista Ingred: " + listaIngr.size());
			Log.i("tam", "lista Proc: " + listaProc.size());

            ArrayList<ObjetoHistoricoPreparacao.Ingrediente> ingredientes1 = new ArrayList<ObjetoHistoricoPreparacao.Ingrediente>();
            ObjetoHistoricoPreparacao objetoHistoricoPreparacao = new ObjetoHistoricoPreparacao();

            for(int i=0; i<listaProc.size(); i=i+4){
				Log.i("tam", "lista Proc pos: " + i);
				aux = aux + "<br>" + "** " + listaIngr.get(j) + " (" + listaProc.get(i) + ") ; Data fabricação: "	+ listaProc.get(i+1) 
						+ " ; Data validade: " + listaProc.get(i+2) + " ; Lote: " + listaProc.get(i+3);

                ObjetoHistoricoPreparacao.Ingrediente ingrediente = objetoHistoricoPreparacao.getIngredienteInstance();
                ingrediente.dataValIngrediente = listaProc.get(i+2);
                ingrediente.codigoIngrediente = listaProc.get(i);
                ingrediente.dataFabIngrediente = listaProc.get(i+1);
                ingrediente.loteIngrediente = listaProc.get(i+3);
                ingrediente.nomeIngrediente = listaIngr.get(j);

                Log.i("Ingrediente", ingrediente.toString());

                ingredientes1.add(ingrediente);

				j++;
			}

            objetoHistoricoPreparacao.codigoReceita = codigo;
            objetoHistoricoPreparacao.nomeReceita = nomeProduto;
            objetoHistoricoPreparacao.ingredientesReceita = ingredientes1;

            HistoricoXMLController historicoXMLController = new HistoricoXMLController(
                    numCliente,
                    loja,
                    tablet,
                    new java.util.Date(),
                    HistoricoXMLController.TYPE_PR
            );

            Log.i("Objeto historico", objetoHistoricoPreparacao.toString());

            historicoXMLController.adicionarObjetoPreparacao(objetoHistoricoPreparacao);

			Log.i("preparacao", aux);

			linha = "11-"+aux+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);

			Print.close();  
			OSW.close();  
			out.close();

			Print.close();  
			OSW.close();  
			out.close();

		} catch (FileNotFoundException e) {
			//erro de arquivo
		} catch (IOException e) {
			//erro geral
		}
	}

	public void saveFileB(String fileName, ArrayList<String> origem, String destino) {

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
		if (!local.exists()) {
			local.mkdir();        	
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao");
		if (!local.exists()) {
			local.mkdir();        	
		}

		try {		

			deleteFile(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao" + File.separator + fileName + ".st");
			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8"); 
			PrintWriter Print = new PrintWriter(OSW);
			String linha;

			//Vários Ingredientes - Qual é a origem?
			//Origem = ingrediente intermediario 
			//TODO 
			//Qual é o destino é a receita - codigo é o codigo digitado 


			for(int i=0; i<origem.size(); i++){
				linha = origem.get(i) + ":" + destino;
				Print.println(linha);
			}
			Print.close();  
			OSW.close();  
			out.close();

		} catch (FileNotFoundException e) {
			//erro de arquivo
            e.printStackTrace();
		} catch (IOException e) {
			//erro geral
            e.printStackTrace();
		}
	}

	public void deleteFile(String fileName) {

		try {
			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Preparacao" + File.separator + fileName + ".st");
			arquivo.delete();
		} catch (Exception e) {
			// erro ao apagar
		}

	}

}
