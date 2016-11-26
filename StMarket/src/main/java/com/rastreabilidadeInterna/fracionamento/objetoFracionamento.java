package com.rastreabilidadeInterna.fracionamento;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rastreabilidadeInterna.helpers.LogGenerator;

public class objetoFracionamento {

	public static String[] colunas = new String[] {
            objetoFracionamentos._ID,
		    objetoFracionamentos.SELOSAFE,
            objetoFracionamentos.NOVOSELO,
		    objetoFracionamentos.DATALEITURA,
            objetoFracionamentos.FLAG,
            objetoFracionamentos.USUARIO,
            objetoFracionamentos.TIPO_PRODUTO,
            objetoFracionamentos.DATA_VALIDADE,
            objetoFracionamentos.SIF,
            objetoFracionamentos.FABRICANTE,
            objetoFracionamentos.DATA_FABRICACAO,
            objetoFracionamentos.LOTE
	};

	// Pacote do Content Provider. Precisa ser œnico.
	public static final String AUTHORITY = "nome.do.pacote.provider.";
	public long _id;

	public String seloSafe; 
	public String novoSelo;
	public String dataLeitura;
    public String dataDeValidade;
    public String tipoDoProduto;
    public String sif;
    public String fabricante;
    public String dataFabricacao;
    public String lote;

    // NAO PERSISTIDO EM BANCO
    public String areaDeUso;
    public String codigoDeBarrasProdutoCaixa;
    public String codigoDeBarrasLidoDaCaixa;
	public String codigoDeBalanca;

    public Context context;

    /*
    AJUSTE FEITO NA GAMBIARRA POR IMPOSSIBILIDADE DE ALTERAÇÃO NO BANCO
    SE A FLAG FOR == -1 É UM REGISTRO DO TIPO GRANEL, SENÃO É UM FRACIONAMENTO NORMAL
     */

	public int flag;
	public String usuarioCpf;


	public objetoFracionamento(Context context) {
        this.context = context;
	}

    public objetoFracionamento(
			long _id,
			String seloSafe,
			String novoSelo,
			String dataLeitura,
			String areaDeUso,
			int flag,
			String usuarioCpf,
			String tipoDoProduto,
			Context context) {
        this._id = _id;
        this.seloSafe = seloSafe;
        this.novoSelo = novoSelo;
        this.dataLeitura = dataLeitura;
        this.areaDeUso = areaDeUso;
        this.flag = flag;
        this.usuarioCpf = usuarioCpf;
        this.tipoDoProduto = tipoDoProduto;
        this.context = context;
    }

    public void saveFileB(String fileName) {
		LogGenerator log = new LogGenerator(context);

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");

		if (!local.exists()) {
			local.mkdir();        	
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento");
		if (!local.exists()) {
			local.mkdir();        	
		}

		try {		

			deleteFile(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento" + File.separator + fileName + ".st");
			log.append("criou arquivo: " + arquivo.toString());
			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
			PrintWriter Print = new PrintWriter(OSW);

			log.append("==== iniciando escrita no arquivo b ====");

			String linha = seloSafe + ":" + novoSelo;
			Print.print(linha);
			log.append("escreveu :: " + linha);

			Print.close();  
			OSW.close();  
			out.close();

		} catch (FileNotFoundException e) {
			//erro de arquivo
			log.append(e.getStackTrace().toString());
		} catch (IOException e) {
			//erro geral
			log.append(e.getStackTrace().toString());
		}
	}

    public void saveFileB(String fileName, ArrayList<String> linhasArquivo) {

        File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        if (!local.exists()) {
            local.mkdir();
        }

        local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento");
        if (!local.exists()) {
            local.mkdir();
        }

        try {

            deleteFile(fileName);

            File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento" + File.separator + fileName + ".st");
            FileOutputStream out = new FileOutputStream(arquivo);
            OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
            PrintWriter Print = new PrintWriter(OSW);

            for (String linha : linhasArquivo) {
                Print.println(linha);
            }

            Print.close();
            OSW.close();
            out.close();

        } catch (FileNotFoundException e) {
            //erro de arquivo
        } catch (IOException e) {
            //erro geral
        }
    }

	public void saveFileA(String fileName, String idLoja, String numCliente) {

		LogGenerator log = new LogGenerator(context);

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
		if (!local.exists()) {
			local.mkdir();        	
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento");
		if (!local.exists()) {
			local.mkdir();        	
		}

		try {

			deleteFile(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento" + File.separator + fileName + ".st");
			log.append("arquivo: " + arquivo.toString());

			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
			PrintWriter Print = new PrintWriter(OSW);

			log.append("==== iniciando escrita no arquivo ====");

			String linha = "L:";        
			Print.println(linha);

			log.append("escreveu :: " + linha);
			
			SimpleDateFormat dateNome = new SimpleDateFormat("dd/MM/yyyy");
			linha = dateNome.format(new Date( System.currentTimeMillis()));
			Print.println(linha);
			log.append("escreveu :: " + linha);

			linha = numCliente;        		 //id empresa
			Print.println(linha);
			log.append("escreveu :: " + linha);

			linha = idLoja;              	 //id local
			Print.println(linha);
			log.append("escreveu :: " + linha);

			linha = novoSelo;               //codigo origem
			Print.println(linha);
			log.append("escreveu :: " + linha);

			linha = "1";               	  //unidad. armazenamento
			Print.println(linha);
			log.append("escreveu :: " + linha);

            if (fabricante != null) {
                linha = "1-" + fabricante + "-" + dateNome.format(new Date(System.currentTimeMillis())) + "|";
                Print.print(linha);
                log.append("escreveu :: " + linha);
            }

            if (codigoDeBarrasProdutoCaixa != null && !codigoDeBarrasProdutoCaixa.isEmpty()){
                linha = "2-" + codigoDeBarrasProdutoCaixa + "-" + dateNome.format(new Date(System.currentTimeMillis())) + "|";
                Print.print(linha);
				log.append("escreveu :: " + linha);
            }

			if (sif != null) {
				linha = "3-" + sif + "-" + dateNome.format(new Date(System.currentTimeMillis())) + "|";
				Print.print(linha);
				log.append("escreveu :: " + linha);
			}

            if (dataFabricacao != null) {
                linha = "4-" + dataFabricacao+ "-" + dateNome.format(new Date(System.currentTimeMillis())) + "|";
                Print.print(linha);
                log.append("escreveu :: " + linha);
            }

			if (dataDeValidade != null) {
				linha = "5-" + dataDeValidade + "-" + dateNome.format(new Date(System.currentTimeMillis())) + "|";
				Print.print(linha);
				log.append("escreveu :: " + linha);
			}

            linha = "7-"+tipoDoProduto+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
			log.append("escreveu :: " + linha);

            if (lote != null) {
                linha = "8-" + lote + "-" + dateNome.format(new Date(System.currentTimeMillis())) + "|";
                Print.print(linha);
                log.append("escreveu :: " + linha);
            }

			linha = "9-"+usuarioCpf+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
			Print.print(linha);
			log.append("escreveu :: " + linha);

            linha = "14-"+areaDeUso+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
			log.append("escreveu :: " + linha);

            if (codigoDeBarrasLidoDaCaixa != null && !codigoDeBarrasLidoDaCaixa.isEmpty()){
                linha = "15-" + codigoDeBarrasLidoDaCaixa + "-" + dateNome.format(new Date(System.currentTimeMillis())) + "|";
                Print.print(linha);
				log.append("escreveu :: " + linha);
            }

            linha = "43-Fracionamento-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
			log.append("escreveu :: " + linha);

            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

            linha = "45-"+telephonyManager.getDeviceId()+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
			log.append("escreveu :: " + linha);

			log.append("==== terminou de escrever no arquivo ====");
            linha = "68-"+codigoDeBalanca+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
            log.append("escreveu :: " + linha);

			linha = "69-"+ new SimpleDateFormat("HH:mm:ss").format(new java.util.Date())+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
			Print.print(linha);
			log.append("escreveu :: " + linha);

			Print.close();  
			OSW.close();  
			out.close();

			Print.close();  
			OSW.close();  
			out.close();

		} catch (FileNotFoundException e) {
			//erro de arquivo
			log.append(e.getStackTrace().toString());
		} catch (IOException e) {
			//erro geral
			log.append(e.getStackTrace().toString());
		}
	}
	
	public void deleteFile(String fileName) {

		try {
			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Fracionamento" + File.separator + fileName + ".st");
			arquivo.delete();
		} catch (Exception e) {
			// erro ao apagar
		}

	}


	public static final class objetoFracionamentos implements BaseColumns {

		private objetoFracionamentos() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/objetoFracionamentos");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.objetoFracionamentos";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.objetoFracionamentos";

		// Ordenacao default para inserir no order by
		public static final String DEFAULT_SORT_ORDER = "_ID ASC";
		public static final String _ID = "_id";
		public static final String SELOSAFE = "seloSafe";
		public static final String NOVOSELO = "novoSelo";
		public static final String DATALEITURA = "dataLeitura";
		public static final String FLAG = "flag";
		public static final String USUARIO = "usuario";
        public static final String TIPO_PRODUTO = "tipo_produto";
        public static final String DATA_VALIDADE = "data_validade";
        public static final String SIF = "sif";
        public static final String FABRICANTE = "fabricante";
        public static final String DATA_FABRICACAO = "data_fabricacao";
        public static final String LOTE = "lote";

	
		public static Uri getUriId(long _id) {
			Uri uriobjetoFracionamento = ContentUris.withAppendedId(objetoFracionamentos.CONTENT_URI,
					_id);
			return uriobjetoFracionamento;
		}
	}

	@Override
	public String toString() {
		return "ObjetoFracionamento{" +
				"_id=" + _id +
				", seloSafe='" + seloSafe + '\'' +
				", novoSelo='" + novoSelo + '\'' +
				", dataLeitura='" + dataLeitura + '\'' +
				", areaDeUso='" + areaDeUso + '\'' +
				", dataDeValidade='" + dataDeValidade + '\'' +
				", tipoDoProduto='" + tipoDoProduto + '\'' +
				", codigoDeBarrasProdutoCaixa='" + codigoDeBarrasProdutoCaixa + '\'' +
				", codigoDeBarrasLidoDaCaixa='" + codigoDeBarrasLidoDaCaixa + '\'' +
				", context=" + context +
				", flag=" + flag +
				", usuarioCpf='" + usuarioCpf + '\'' +
				'}';
	}
}
