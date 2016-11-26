package com.rastreabilidadeInterna.controleEstoque;

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
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;

import com.rastreabilidadeInterna.helpers.LogGenerator;

public class objetoControleEstoqueCodSafe {

	public static String[] colunas = new String[] { objetoControleEstoqueCodSafes._ID, 
		objetoControleEstoqueCodSafes.CODIGOSAFE, objetoControleEstoqueCodSafes.TIPO
	};

	// Pacote do Content Provider. Precisa ser œnico.
	public static final String AUTHORITY = "nome.do.pacote.provider.";
	public long _id;

	public String codigoSafe; 
	public String tipo;

	private Context context;

	public objetoControleEstoqueCodSafe(Context context) {
		this.context = context;
	}




	public void saveFile(String fileName, String idLoja, String numCliente) {

        LogGenerator log = new LogGenerator(context);
//		SharedPreferences settings = ctx.getSharedPreferences("Preferences", 0);

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        log.append("criou arquivo: " + local.toString());
		if (!local.exists()) {
			local.mkdir();
            log.append("local nao existe, making dir");
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque");
        log.append("criou arquivo: " + local.toString());
		if (!local.exists()) {
			local.mkdir();
            log.append("local nao existe, making dir");
		}

		try {

            log.append("deletando arquivo: " + fileName);
			deleteFile(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + fileName + ".st");
            log.append("arquivo: " + arquivo.toString());

			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
			PrintWriter Print = new PrintWriter(OSW);

            log.append("==== comecou escrever ====");

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

			linha = codigoSafe;               //codigo origem
			Print.println(linha);
            log.append("escreveu :: " + linha);

			linha = "1";               	  //unidad. armazenamento
			Print.println(linha);
            log.append("escreveu :: " + linha);

			linha = "7-"+tipo+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);
            log.append("escreveu :: " + linha);

            log.append("==== terminou de escrever ====");

			Print.close();  
			OSW.close();  
			out.close();

		} catch (FileNotFoundException e) {
			//erro de arquivo
		} catch (IOException e) {
			//erro geral
		}
	}

	public void saveFileB(String fileName, 	String origem, ArrayList<String> list) {

        LogGenerator log = new LogGenerator(context);

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        log.append("criou arquivo: " + local.toString());
		if (!local.exists()) {
			local.mkdir();
            log.append("arquivo nao existe, making dir");
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque");
        log.append("criou arquivo: " + local.toString());
		if (!local.exists()) {
			local.mkdir();
            log.append("arquivo nao existe, making dir");
		}

		try {

            log.append("deletando arquivo: " + fileName);
			deleteFile(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + fileName + ".st");
            log.append("criou arquivo: " + arquivo.toString());

			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "ISO-8859-1"); 
			PrintWriter Print = new PrintWriter(OSW);

			String linha;

			for(int i=0; i<list.size(); i++){
				linha = origem + ":" + list.get(i);
				if(i==list.size()-1) {
                    Print.print(linha);
                    log.append("escreveu :: " + linha);
                } else {
                    Print.println(linha);
                    log.append("escreveu :: " + linha);
                }

			}

            log.append("terminou de escrever");

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
	

	public void saveFileIdx(String fileName, ArrayList<String> list, String tipo, String fabricante, String sif, String lote, String dataFab, String dataVal) {

        LogGenerator log = new LogGenerator(context);

        log.append("salvando arquivo idx");

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        log.append("criou arquivo: " + local.toString());
		if (!local.exists()) {
			local.mkdir();
            log.append("local nao existe, making dir");
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx");
        log.append("criou arquivo: " + local.toString());
		if (!local.exists()) {
			local.mkdir();
            log.append("local nao existe, making dir");
		}

		try {

            log.append("deletando arquivo: " + fileName);
			deleteFile(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + fileName + ".idx");
            log.append("criou arquivo: " + arquivo.toString());

			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
			PrintWriter Print = new PrintWriter(OSW);

			String linha;

			for(int i=0; i<list.size(); i++){
				linha = list.get(i) + "-" + tipo + "-" + fabricante + "-" + sif + "-" + lote + "-" + dataFab + "-" + dataVal;
				if(i==list.size()-1) {
                    Print.print(linha);
                    log.append("escreveu :: " + linha);
                } else {
                    Print.println(linha);
                    log.append("escreveu :: " + linha);
                }

			}


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
	
	public void editaFileIdx(String fileName, ArrayList<String> list) {

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
		if (!local.exists()) {
			local.mkdir();        	
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx");
		if (!local.exists()) {
			local.mkdir();        	
		}

		try {		

			deleteFile2(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + fileName);
			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
			PrintWriter Print = new PrintWriter(OSW);

			String linha;

			for(int i=0; i<list.size(); i++){
				linha = list.get(i);
				if(i==list.size()-1)
					Print.print(linha);
				else
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

	public void deleteFile(String fileName) {

		try {
			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + fileName + ".st");
			arquivo.delete();
		} catch (Exception e) {
			// erro ao apagar
		}

	}
	
	public void deleteFile2(String fileName) {

		try {
			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + fileName);
			arquivo.delete();
		} catch (Exception e) {
			// erro ao apagar
		}

	}


	
	public static final class objetoControleEstoqueCodSafes implements BaseColumns {

		private objetoControleEstoqueCodSafes() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/objetoControleEstoqueCodSafes");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.objetoControleEstoqueCodSafes";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.objetoControleEstoqueCodSafes";

		// Ordenacao default para inserir no order by
		public static final String DEFAULT_SORT_ORDER = "_ID ASC";
		public static final String _ID = "_id";
		public static final String CODIGOSAFE = "codigoSafe";
		public static final String TIPO = "tipo";		


		public static Uri getUriId(long _id) {
			Uri uriobjetoControleEstoqueCodSafe = ContentUris.withAppendedId(objetoControleEstoqueCodSafes.CONTENT_URI,
					_id);
			return uriobjetoControleEstoqueCodSafe;
		}
	}

    @Override
    public String toString() {
        return "ObjetoControleEstoqueCodSafe{" +
                "_id=" + _id +
                ", codigoSafe='" + codigoSafe + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
