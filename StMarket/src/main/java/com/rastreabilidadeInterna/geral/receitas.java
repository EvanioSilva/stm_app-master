package com.rastreabilidadeInterna.geral;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;

public class receitas extends Activity{

	private Button btnAdm;

	private Spinner spReceita;

	private Button btnNovaRec;
	private Button btnApagaRec;
	private Button btnAddIng;
	private Button btnEditaIng;
	private TextView tvInfo;

	private ArrayList<String> ingredientes = new ArrayList<String>();
	List<objetoIngrediente> objIngredientes;
	private ListView lvList;

	Repositorio repositorio;

	private ProgressDialog dialog;
	private final static String SERVIDOR = "54.232.88.231";
	private final static String NOME = "safecafe";
	private final static String SENHA = "GbdE$5uF";
	public static final String PREFS_NAME = "Preferences";

	private Handler  handler = new Handler();

	ArrayList<String> listaFtp = new ArrayList<String>();
	private String msgErro;
	private Boolean enviou;

	private boolean bdAlteracao = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receitas);

		repositorio = new Repositorio(this);

		defineComponente();
		defineAction();
		defineCaminho();
		loadScreen();
		DownloadFTP();

		//baixar arquivos e popular banco ao entrar
	}

	@Override
	public void onResume(){
		super.onResume();
		// put your code here...
		loadScreen();
	}

	@Override
	public void onBackPressed() {
		// your code.

		try{
			dialog = ProgressDialog.show(this, "Conectando", "Enviando dados, por favor aguarde...", false, true);

			File caminho = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + "Envio");

			if (!caminho.exists()) {
				caminho.mkdir();
			}

			if(caminho.listFiles().length > 0){
				if(bdAlteracao){
					apagaArquivos(caminho);
					geraArquivo();
				}
				else{
					//	 				qual procedimento? se = 2 ou 1 ou mais
					//					enviarArquivos(caminho);
				}
			}
			else if(caminho.listFiles().length == 0){
				if(bdAlteracao){
					geraArquivo();
				}
			}

			dialog.dismiss();
			Log.i("TESTE", "pronto");
			finish();

		} catch (Exception e) {
			dialog.dismiss();
			finish();
		}
	}


	//	@Override
	//	public void onStop(){
	//		super.onStop();
	//
	//
	//		// gerar e enviar arquivos
	//
	//		File caminho = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + "Envio");
	//		
	//		if (!caminho.exists()) {
	//			caminho.mkdir();
	//		}
	//
	//		if(caminho.listFiles().length > 0){
	//			if(bdAlteracao){
	//				apagaArquivos(caminho);
	//				geraArquivo();
	//			}
	//			else{
	//// 				qual procedimento? se = 2 ou 1 ou mais
	////				enviarArquivos(caminho);
	//			}
	//		}
	//		else if(caminho.listFiles().length == 0){
	//			if(bdAlteracao){
	//				geraArquivo();
	//			}
	//		}
	//		
	//		dialog.dismiss();
	//
	//	}

	private void apagaArquivos(File caminho){
		File fList[] = caminho.listFiles();

		for(int i=0; i < (fList.length); i++){
			final File arquivo = fList[i];
			arquivo.delete();
		} 
	}

	private void defineCaminho(){
		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
		if (!local.exists()) {
			local.mkdir();
		}		
	}

	private void defineComponente(){
		btnAdm = (Button) findViewById(R.id.rec_btnAdm);
		spReceita = (Spinner) findViewById(R.id.rec_spinner);
		btnNovaRec = (Button) findViewById(R.id.rec_novaRec);
		btnApagaRec = (Button) findViewById(R.id.rec_delRec);
		btnAddIng = (Button) findViewById(R.id.rec_addIng);
		btnEditaIng = (Button) findViewById(R.id.rec_edtIng);
		lvList = (ListView) findViewById(R.id.rec_listview);
		tvInfo = (TextView) findViewById(R.id.rec_txInfo);
	}

	private void defineAction(){

		btnAdm.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				OpenConfig();
			}
		});

		spReceita.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				
				//				String mselection = spReceita.getSelectedItem().toString();
				//
				//				ingredientes = repositorio.listarIngredientes(mselection);
				//				Log.i("acao", mselection);

				updateListView();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//  
			}
		});


		btnNovaRec.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				novaReceita();
			}
		});

		btnApagaRec.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				if(spReceita.isClickable()){
					confirmaExcluir(spReceita.getSelectedItem().toString(), 0, 1);						
				}
			}
		});

		btnAddIng.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				objIngredientes = repositorio.listarIngredientes();
				ArrayList<String> tdsIngred = new ArrayList<String>();

				for(int i=0; i<objIngredientes.size();i++){
					String strIngrd = objIngredientes.get(i).nomeIngrediente + " - " + objIngredientes.get(i).codigo  + " - " + 
							objIngredientes.get(i).peso + " - " + objIngredientes.get(i).diasVal;
					tdsIngred.add(strIngrd);
				}

				listaIngredientes(tdsIngred.size(), tdsIngred);
			}
		});

		btnEditaIng.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				Intent it = new Intent(getApplicationContext(), ActivityIngredientes.class);
				startActivity(it);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// ListView Clicked item index
				int itemPosition = position;
				confirmaExcluir(ingredientes.get(itemPosition), itemPosition, 2);
				Log.i("TAM", ingredientes.get(itemPosition).length() + "");
			}
		}); 

	}

	private void loadScreen(){
		final ArrayList<String> receitas;
		receitas = repositorio.listarReceitasNovo();

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, receitas);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spReceita.setAdapter(dataAdapter);

		if(receitas.size() > 0){
			spReceita.setClickable(true);
		}
		else{
			spReceita.setClickable(false);
		}

	}

	private void updateListView(){

		if(spReceita.isClickable()){
			ingredientes = repositorio.listarIngredientes(spReceita.getSelectedItem().toString());			
			tvInfo.setText("Ingredientes de " + spReceita.getSelectedItem().toString());
		} 
		else{
			ingredientes.clear();
			tvInfo.setText("Ingredientes de ");
		}


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), 
				android.R.layout.simple_list_item_1, ingredientes) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTextColor(Color.BLACK);
				text.setTextSize(25);
				text.setGravity(Gravity.CENTER);
				return view;
			}
		};

		lvList.setAdapter(adapter);

	}

	private void OpenConfig() {

		final EditText input = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(input);
		builder.setTitle("Configurações");
		builder.setMessage("Digite a senha para configurar");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				if (input.getText().toString().equals("safeadm")) {

					Intent it = null;
					it = new Intent(getApplicationContext(), varGlobais.class);
					startActivity(it);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

				} else {
					Toast.makeText(getApplicationContext(), "Senha incorreta", Toast.LENGTH_LONG).show();
				}
			}
		});
		builder.setNegativeButton("Cancelar",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		AlertDialog alerta = builder.create();
		alerta.show();
	}

	private void confirmaExcluir(final String codigo, final int pos, final int flag) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Excluir");
		builder.setMessage("Dejesa mesmo excluir " + codigo + " ?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {

				//apagar do banco
				if(flag == 1){
					repositorio.deletaReceita(codigo);
					bdAlteracao = true;
					loadScreen();
					updateListView();
				}
				else{
					repositorio.deleteIngredienteReceita(ingredientes.get(pos), spReceita.getSelectedItem().toString());
					bdAlteracao = true;
					updateListView();					
				}
			}
		});
		builder.setNegativeButton("Cancelar",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		AlertDialog alerta = builder.create();
		alerta.show();
	}

	private void novaReceita() {

		final EditText input = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setInputType(InputType.TYPE_CLASS_TEXT);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(input);
		builder.setTitle("Nova Receita");
		builder.setMessage("Digite o nome da nova receita:");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				if (!input.getText().toString().equals("")) {

					//					objetoReceita objeto = new objetoReceita();
					//					objeto.receita = input.getText().toString();
					//					objeto.ingrediente = "";
					//					repositorio.inserirNovaReceita(objeto);

					repositorio.inserirReceita(input.getText().toString());
					bdAlteracao = true;
					loadScreen();

				} else {
					Toast.makeText(getApplicationContext(), "Digite um nome de receita", Toast.LENGTH_LONG).show();
				}
			}
		});
		builder.setNegativeButton("Cancelar",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		AlertDialog alerta = builder.create();
		alerta.show();
	}

	private void listaIngredientes(int tam, final ArrayList<String> vetIngredientes){

		CharSequence[] items = new String[tam];

		for(int i=0; i<vetIngredientes.size(); i++){
			items[i] = vetIngredientes.get(i);
			Log.i("receita", items[i]+"");
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Selecione um ingrediente para adicionar à receita :");
		builder.setCancelable(true);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				objetoReceita obj = new objetoReceita();
				obj.receita = spReceita.getSelectedItem().toString();
				obj.ingrediente = vetIngredientes.get(item).substring(0, vetIngredientes.get(item).indexOf(" - "));
				repositorio.inserirNovaReceita(obj);
				bdAlteracao = true;

				updateListView();
				Toast.makeText(getApplicationContext(), "Ingrediente adicionado à receita", Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// CRIAR ARQUIVO PARA ENVIO --------------------------------------------------------------------------------------

	public void geraArquivo() {

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
		if (!local.exists()) {
			local.mkdir();        	
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Receitas");
		if (!local.exists()) {
			local.mkdir();        	
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + "Envio");
		if (!local.exists()) {
			local.mkdir();        	
		}

		try {		

			SimpleDateFormat dateNome = new SimpleDateFormat("yyyyMMdd");
			String data_arquivo = dateNome.format(new Date( System.currentTimeMillis()));		

			dateNome = new SimpleDateFormat("HHmmss");
			String hora_arquivo = dateNome.format(new Date( System.currentTimeMillis()));

			// ARQUIVO INGREDIENTE ------------------------

			String nomeArquivoI = "I" + data_arquivo+hora_arquivo;

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + "Envio"
					+ File.separator + nomeArquivoI + ".txt");
			FileOutputStream out = new FileOutputStream(arquivo);
			OutputStreamWriter OSW = new OutputStreamWriter(out, "ISO-8859-1"); 
			PrintWriter Print = new PrintWriter(OSW);

			String linha;

			List<objetoIngrediente> objIngredientes = repositorio.listarIngredientes();

			for(int i=0; i<objIngredientes.size(); i++){
				linha = objIngredientes.get(i).nomeIngrediente + "*" + objIngredientes.get(i).codigo + "*" 
						+ objIngredientes.get(i).peso + "*" + objIngredientes.get(i).diasVal;

				Log.i("ARQUIVO", linha);

				if(i==objIngredientes.size()-1)
					Print.print(linha);
				else
					Print.println(linha);
			}

			Print.close();  
			OSW.close();  
			out.close();

			// ARQUIVO RECEITA ------------------------
			String nomeArquivoR = "R" + data_arquivo+hora_arquivo;

			arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + "Envio"
					+ File.separator + nomeArquivoR + ".txt");
			out = new FileOutputStream(arquivo);
			OSW = new OutputStreamWriter(out, "ISO-8859-1"); 
			Print = new PrintWriter(OSW);

			ArrayList<String> receitas = repositorio.listarReceitas("Confeitaria");

			for(int i=0; i<receitas.size(); i++){
				linha = "*"+receitas.get(i);
				Print.println(linha);
				Log.i("ARQUIVO", linha);

				ArrayList<String> ingredientes = repositorio.listarIngredientes(receitas.get(i));

				for(int j=0; j<ingredientes.size(); j++){
					linha = ingredientes.get(j);

					Log.i("ARQUIVO", linha);

					if( (i == receitas.size()-1) && (j == ingredientes.size()-1))
						Print.print(linha);
					else
						Print.println(linha);
				}
			}

			Print.close();  
			OSW.close();  
			out.close();

			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();

			editor.putString("VLOCAL", data_arquivo+hora_arquivo);
			editor.commit();

			enviarArquivos(local);

		} catch (FileNotFoundException e) {
			//erro de arquivo
		} catch (IOException e) {
			//erro geral
		}
	}


	// INSERIR NO BANCO ----------------------------------------------------------------------------------------------

	private void insereBanco(File arquivo, String versao){


		String receita;
		String ingrediente;


		if(arquivo.getName().contains("R")){
			// arquivo receita

			try{
				BufferedReader reader = new BufferedReader(new FileReader(arquivo));
				String line;
				objetoReceita obj = new objetoReceita();

				while ((line = reader.readLine()) != null){
					if(line.substring(0, 1).equals("*")){
						receita = (String) line.subSequence(1, line.length());
						repositorio.inserirReceita(receita);
						obj.receita = receita;
					}
					else{
						obj.ingrediente = line;
						repositorio.inserirNovaReceita(obj);
						Log.i("nome",obj.receita + "   " + obj.ingrediente);
					}
				}
				reader.close();
			}
			catch (Exception e){
				System.err.format("Exception occurred trying to read '%s'.", arquivo.getName());
				e.printStackTrace();
			}
		}
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
		}

		arquivo.delete();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString("VLOCAL", versao);
		editor.commit();

	}

	//UPLOAD -----------------------------------------------------------------------------------------------------

	private void enviarArquivos(final File path) {


		new Thread(new Runnable() {
			public void run() {

				File fList[] = path.listFiles();

				// deleta os existentes no ftp

				if(deletaArquivosFtp()){
					for(int i=0; i < (fList.length); i++){
						final File arquivo = fList[i];
						enviou = envioFTP(arquivo);
						handler.post(new Runnable() {
							public void run() {
								if (!enviou) {
									Log.i("envia", msgErro);
									//											Toast.makeText(getApplicationContext(), msgErro, Toast.LENGTH_LONG).show();
								}
								else{
									// deleta arquivo 
									arquivo.delete();
								}
							}
						});
					}
				}

				//					setStatus();
			}
		}).start();		

		//		setStatus();
	}

	private Boolean envioFTP(File arquivo) {
		FTPClient ftp = new FTPClient();  
		Boolean retorno = false;
		msgErro = "";

		try {

			ftp.connect(SERVIDOR,21);
			ftp.login(NOME, SENHA);

			ftp.changeWorkingDirectory("STMarket"); 

			if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

				Log.d("NOME ARQUIVO", arquivo.getName());
				FileInputStream arqEnviar = new FileInputStream(arquivo);

				ftp.enterLocalPassiveMode();

				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

				ftp.storeFile(arquivo.getName(), arqEnviar);

				arqEnviar.close();   
				arquivo.delete();
				retorno = true;
			}         


			ftp.logout();                        
			ftp.disconnect();

		} catch (SocketException e) {
			msgErro = "1 - " + e.getMessage();
			retorno = false;
		} catch (IOException e) {
			msgErro = "2 - " + e.getMessage();
			retorno = false;
		} catch (Exception e) {
			msgErro = "3 - " + e.getMessage();
			retorno = false;
		}
		return retorno;
	}

	private boolean deletaArquivosFtp(){

		FTPClient ftp = new FTPClient();  

		try {
			ftp.connect(SERVIDOR,21);
			ftp.login(NOME, SENHA);
			ftp.enterLocalPassiveMode();  
			ftp.changeWorkingDirectory("STMarket");  

			String[] names = ftp.listNames();

			for (String name : names) {
				ftp.deleteFile(name);
				//				listaFtp.add(name);
				Log.i("FTP", name);
			}

			ftp.logout();                        
			ftp.disconnect();

			return true;

		} catch (Exception e) {  
			Log.e("Log", "Erro ao excluir arquivos no ftp: "+e.getMessage());
		}

		return false;
	}





	// DOWNLOAD   ----------------------------------------------------------------------------------------------------

	public void msg(final String mensagem){

		handler.post(new Runnable() {						
			public void run() {	
				Toast.makeText(receitas.this, mensagem, Toast.LENGTH_LONG).show();
			}
		});			
	}

	public void DownloadFTP() { 
		try {  
			dialog = ProgressDialog.show(this, "Conectando", "Baixando dados, por favor aguarde...", false, true);


			Thread background = new Thread() {
				public void run() {

					try {
						File root = Environment.getExternalStorageDirectory();
						File local1 = new File(root + File.separator + "Carrefour");
						if (!local1.exists()) {
							local1.mkdir();         
						}
						local1 = new File(root + File.separator + "Carrefour" + File.separator + "Receitas");
						if (!local1.exists()) {
							local1.mkdir();
						}

						// Cria se nao existir e recupera versao do BD

						SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();

						if(!settings.contains("VLOCAL")){
							editor.putString("VLOCAL", "0");
						}
						editor.commit();

						String versaoLocal = settings.getString("VLOCAL", "");

						listaArquivosFtp();

						if(listaFtp.size() == 2){
							String versaoFtpI = listaFtp.get(0).substring(1, listaFtp.get(0).length()-4);
							String versaoFtpR = listaFtp.get(1).substring(1, listaFtp.get(1).length()-4);

							Log.i("FTP", versaoLocal + " - " + versaoFtpI + " - " + versaoFtpR);
							//							msg(versaoLocal + " - " + versaoFtpI + " - " + versaoFtpR);

							if(versaoFtpI.equals(versaoFtpR)){
								if(Long.valueOf(versaoFtpR).longValue() > Long.valueOf(versaoLocal).longValue()){
									//baixa arquivos e popula banco
									File arquivo2 = new File(root + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + listaFtp.get(0));         
									ftpdld(arquivo2.getAbsolutePath(), listaFtp.get(0));

									arquivo2 = new File(root + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + listaFtp.get(1));         
									ftpdld(arquivo2.getAbsolutePath(), listaFtp.get(1));

									msg("BAIXOU");

									//inserir no banco arquivos baixados

									insereBanco(new File(root + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + listaFtp.get(0)), versaoFtpI);
									insereBanco(new File(root + File.separator + "Carrefour" + File.separator + "Receitas" + File.separator + listaFtp.get(1)), versaoFtpR);

								}
								else{
									msg("Arquivos estão atualizados");
								}
							}
							else{
								msg("Arquivos ftp possuem versão diferentes entre si");
							}

						}
						else{
							// pasta sem arquivos Receita e Ingrediente, ou faltando algum, ou a mais.
							if(listaFtp.size() > 2){
								msg("Quantidade de arquivos incorreto no servidor(+)");
							}
							else if(listaFtp.size() == 1){
								msg("Quantidade de arquivos incorreto no servidor(-)");
							}
							else{
								// nenhum arquivo. Criar arquivos
								msg("Nenhum arquivo no servidor");
							}
						}

						dialog.dismiss();						

					} catch (Exception e) {
						dialog.dismiss();
					}

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							//stuff that updates ui
							loadScreen();
							updateListView();
						}
					});

				}
			};
			background.start();

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




}
