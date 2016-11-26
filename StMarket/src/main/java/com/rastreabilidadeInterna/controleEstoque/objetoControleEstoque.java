package com.rastreabilidadeInterna.controleEstoque;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.telephony.TelephonyManager;

import com.rastreabilidadeInterna.centrodedistribuicao.ActivityAmostragem;
import com.rastreabilidadeInterna.helpers.LogGenerator;

public class objetoControleEstoque {

	public static String[] colunas = new String[] { objetoControleEstoques._ID, 
		objetoControleEstoques.CODIGO, objetoControleEstoques.FABRICANTE, objetoControleEstoques.SIF,
		objetoControleEstoques.DATAFAB, objetoControleEstoques.DATAVAL, objetoControleEstoques.QTD,
		objetoControleEstoques.CAIXA, objetoControleEstoques.LOTE, objetoControleEstoques.USUARIO
	};

	// Pacote do Content Provider. Precisa ser œnico.
	public static final String AUTHORITY = "nome.do.pacote.provider.";
	public long _id;

	public String codigo; 
	public String fabricante; 
	public String sif;
	public String dataFab;
	public String dataVal;
	public String qtd;
	public String caixa;
	public String lote;
	public String usuarioNomeCpf;


    //      Obtido do tablet
    public String areaDeUso;        //Fora do banco
    //      PESO EM GRAMAS
    public String pesoLiquido;      //Fora do banco
    //      PESO DA CAIXA * NRO DE CAIXAS
    public String pesoMedioLote;    //Fora do banco
    //      codigo de barras unidade da caixa
    public String codigoInterno;
    //      tipo do produto
    public String tipoProduto;

    private Context context;

    public objetoControleEstoque(Context context) {
        this.context = context;
	}

	public void saveFile(String fileName, String codGen, String idLoja, String numCliente) {
        LogGenerator log = new LogGenerator(context);

		File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        log.append("criou local: " + local.toString());
		if (!local.exists()) {
            log.append("local nao existe, making dir");
			local.mkdir();        	
		}

		local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque");
        log.append("criou local: " + local.toString());
		if (!local.exists()) {
            log.append("local nao existe, making dir");
			local.mkdir();        	
		}

		try {

            log.append("deletando fileName: " + fileName);
			deleteFile(fileName);

			File arquivo = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + fileName + ".st");
            log.append("criado o arquivo: " + arquivo.toString());

            log.append("criando FileOutputStream");
			FileOutputStream out = new FileOutputStream(arquivo);
            log.append("criando OutputStreamWriter");
			OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
            log.append("criando PrintWriter");
			PrintWriter Print = new PrintWriter(OSW);

            log.append("==== escrevendo arquivo ====");

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

			linha = codGen;               //codigo origem
			Print.println(linha);
            log.append("escreveu :: " + linha);

			linha = "1";               	  //unidad. armazenamento
			Print.println(linha);
            log.append("escreveu :: " + linha);

			linha = "2-"+codigo+"/"+caixa+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);
            log.append("escreveu :: " + linha);

			linha = "1-"+fabricante+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);
            log.append("escreveu :: " + linha);

			linha = "3-"+sif+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);
            log.append("escreveu :: " + linha);

			linha = "4-"+dataFab+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);
            log.append("escreveu :: " + linha);

			linha = "5-"+dataVal+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
			Print.print(linha);
            log.append("escreveu :: " + linha);

            linha = "6-"+qtd+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
            log.append("escreveu :: " + linha);

            linha = "7-"+tipoProduto+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
            log.append("escreveu :: " + linha);

			linha = "8-"+lote+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
			Print.print(linha);
            log.append("escreveu :: " + linha);

			linha = "9-"+usuarioNomeCpf+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";   
			Print.print(linha);
            log.append("escreveu :: " + linha);

            linha = "12-"+pesoLiquido+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
            log.append("escreveu :: " + linha);

            linha = "13-"+pesoMedioLote+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
            log.append("escreveu :: " + linha);

            linha = "14-"+areaDeUso+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
            log.append("escreveu :: " + linha);

            if (codigoInterno != null && !codigoInterno.isEmpty()) {
                linha = "15-" + codigoInterno + "-" + dateNome.format(new Date(System.currentTimeMillis())) + "|";
                Print.print(linha);
                log.append("escreveu :: " + linha);
            }

            linha = "42-Controle De Estoque-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
            log.append("escreveu :: " + linha);

            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

            linha = "45-"+telephonyManager.getDeviceId()+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
            Print.print(linha);
            log.append("escreveu :: " + linha);

            log.append("==== fim da escrita ====");

			linha = "69-"+ new SimpleDateFormat("HH:mm:ss").format(new java.util.Date())+"-"+dateNome.format(new Date( System.currentTimeMillis()))+"|";
			Print.print(linha);
			log.append("escreveu :: " + linha);

			Print.close();  
			OSW.close();  
			out.close();

		} catch (FileNotFoundException e) {
            log.append(e.getStackTrace().toString());
			//erro de arquivo
		} catch (IOException e) {
			//erro geral
            log.append(e.getStackTrace().toString());
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

	public static final class objetoControleEstoques implements BaseColumns {

		private objetoControleEstoques() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/objetoControleEstoques");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.objetoControleEstoques";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.objetoControleEstoques";

		// Ordenacao default para inserir no order by
		public static final String DEFAULT_SORT_ORDER = "_ID ASC";
		public static final String _ID = "_id";
		public static final String CODIGO = "codigo";
		public static final String FABRICANTE = "fabricante";
		public static final String SIF = "sif";
		public static final String DATAFAB = "dataFab";
		public static final String DATAVAL = "dataVal";
		public static final String QTD = "qtd";
		public static final String CAIXA = "caixa";
		public static final String LOTE = "lote";
		public static final String USUARIO = "usuario";
		

		public static Uri getUriId(long _id) {
			Uri uriobjetoControleEstoque = ContentUris.withAppendedId(objetoControleEstoques.CONTENT_URI,
					_id);
			return uriobjetoControleEstoque;
		}
	}

    @Override
    public String toString() {
        return "ObjetoControleEstoque{" +
                "tipoProduto='" + tipoProduto + '\'' +
                ", codigoInterno='" + codigoInterno + '\'' +
                ", pesoMedioLote='" + pesoMedioLote + '\'' +
                ", pesoLiquido='" + pesoLiquido + '\'' +
                ", areaDeUso='" + areaDeUso + '\'' +
                ", usuarioNomeCpf='" + usuarioNomeCpf + '\'' +
                ", lote='" + lote + '\'' +
                ", caixa='" + caixa + '\'' +
                ", qtd='" + qtd + '\'' +
                ", dataVal='" + dataVal + '\'' +
                ", dataFab='" + dataFab + '\'' +
                ", sif='" + sif + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", codigo='" + codigo + '\'' +
                ", _id=" + _id +
                '}';
    }
}
