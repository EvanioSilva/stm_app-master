package com.rastreabilidadeInterna.geral;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.rastreabilidadeInterna.BD.Repositorio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class DownloadFTP {
	private Context contexto;
	Repositorio repositorio;

	private ProgressDialog dialog;
	private final static String SERVIDOR = "52.204.225.11";
	private final static String NOME = "safetrace";
	private final static String SENHA = "9VtivgcVTy0PI";
	public static final String PREFS_NAME = "Preferences";
	private final static String CONFEITARIA = "confeitaria";
	private final static String PADARIA =  "padaria";
	private final static String PRATOSPRONTOS = "pratosprontos";

	private Handler  handler = new Handler();

	ArrayList<String> listaFtp = new ArrayList<String>();
	
	public DownloadFTP(Context context){
		this.contexto = context;
		repositorio = new Repositorio(this.contexto);
	}
	
	
	public void msg(final String mensagem){

		handler.post(new Runnable() {						
			public void run() {	
				Toast.makeText(contexto, mensagem, Toast.LENGTH_LONG).show();
			}
		});			
	}
	public void Download() { 
		try {  
			dialog = ProgressDialog.show(contexto, "Conectando", "Baixando dados, por favor aguarde...", false, true);

			Thread background = new Thread() {
				public void run() {
					try {
						File root = Environment.getExternalStorageDirectory();
						File local1 = new File(root + File.separator + "Carrefour");
						DeleteRecursive(local1);
						if (!local1.exists()) {
							local1.mkdir();         
						}
						local1 = new File(root + File.separator + "Carrefour" + File.separator + "Receitas");
						if (!local1.exists()) {
							local1.mkdir();
						}

						// Cria se nao existir e recupera versao do BD

						listaArquivosFtp();
						for(int i = 0; i< listaFtp.size(); i++ ){
							File arquivo2 = new File(root + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + listaFtp.get(i));         
							ftpdld(arquivo2.getAbsolutePath(), listaFtp.get(i));
							msg("Receitas Atualizadas");
							//inserir no banco arquivos baixados
							insereBanco(new File(root + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + listaFtp.get(i)));
								
						}
						dialog.dismiss();
						//baixa arquivos e popula banco
											
						
					} catch (Exception e) {
						dialog.dismiss();
					}

				}
			};
			background.start();
			background.join();
		} catch (Exception e) {  
			Log.e("DOWNLOAD", e.getCause().toString());  
		}
		
	}

	public boolean ftpdld(String desFilePath, String nomeArquivo) {  
		boolean status = false;  
		FTPClient ftp = new FTPClient();  

		try {
			ftp.connect(SERVIDOR,21);
			status = ftp.login(NOME, SENHA);

			ftp.changeWorkingDirectory("STMarket");

			if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				// verifica se o arquivo ja existe localmente
				FileOutputStream desFileStream = new FileOutputStream(desFilePath); 
				ftp.setFileType(FTP.BINARY_FILE_TYPE);  
				ftp.enterLocalPassiveMode();  
				status = ftp.retrieveFile(nomeArquivo, desFileStream);  
				desFileStream.close(); 
			}

			ftp.logout();                        
			ftp.disconnect();
			return status;  
		} catch (Exception e) {  
			Log.e("Log", "download falhou: "+e.getMessage());
		} 

		return status;  
	}

	private void listaArquivosFtp(){

		listaFtp.clear();
		FTPClient ftp = new FTPClient();  

		try {
			ftp.connect(SERVIDOR,21);
			ftp.login(NOME, SENHA);
			ftp.enterLocalPassiveMode();  

			ftp.changeWorkingDirectory("STMarket");  

			String[] names = ftp.listNames();
			for (String name : names) {
				listaFtp.add(name);
				Log.i("FTP", name);
			}

			Log.i("FTP", listaFtp.size()+"");

			ftp.logout();                        
			ftp.disconnect();

		} catch (Exception e) {  
			Log.e("Log", "download falhou: "+e.getMessage());
		}
	}
	
	private void insereBanco(File arquivo){


		String receita;
		String ingrediente;
		
		if(arquivo.getName().contains("receitas")){
			objetoReceita obj = new objetoReceita();
			if(arquivo.getName().contains(CONFEITARIA)){
				obj.local = CONFEITARIA;
			}
			if(arquivo.getName().contains(PADARIA)){
				obj.local = PADARIA;
			}
			if(arquivo.getName().contains(PRATOSPRONTOS)){
				obj.local = PRATOSPRONTOS;
			}
			
			// arquivo receita

			try{
				
				BufferedReader reader = new BufferedReader(new FileReader(arquivo));
				String line;

				while ((line = reader.readLine()) != null){
					if(line.substring(0, 1).equals("*")){
						
						receita = (String) line.subSequence(1, line.length());
						repositorio.inserirReceita(receita);
						obj.receita = receita;
						
					}else{
						
						if(line.equals("F") || line.equals("I") ){
							
							obj.intermediaria = line;
							
						}else{
							
							obj.ingrediente = line;
							repositorio.inserirNovaReceita(obj);
							Log.i("nome",obj.receita + "   " + obj.ingrediente);
					
					}
					
					}
				}
				reader.close();
			}
			catch (Exception e){
				System.err.format("Exception occurred trying to read '%s'.", arquivo.getName());
				e.printStackTrace();
			}
		}/*
		else{
			// arquivo ingrediente

			try{
				BufferedReader reader = new BufferedReader(new FileReader(arquivo));
				String line;
				objetoIngrediente obj = new objetoIngrediente();

				while ((line = reader.readLine()) != null){

					obj.nomeIngrediente = line.substring(0, line.indexOf("*"));
					String aux = line.substring(line.indexOf("*")+1, line.length());
					obj.codigo = aux.substring(0, aux.indexOf("*"));
					line = aux.substring(aux.indexOf("*")+1, aux.length());
					obj.peso = line.substring(0, line.indexOf("*"));
					aux = line.substring(line.indexOf("*")+1, line.length());
					obj.diasVal = aux;

					//					repositorio.inseriring
					Log.i("BANCO",obj.nomeIngrediente + " - " + obj.codigo
							+ " - " + obj.peso + " - " + obj.diasVal);

					repositorio.inserirIngrediente(obj);

				}

				reader.close();
			}

			catch (Exception e){
				System.err.format("Exception occurred trying to read '%s'.", arquivo.getName());
				e.printStackTrace();
			}
		}*/

		arquivo.delete();

	}
	
	void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}
}
