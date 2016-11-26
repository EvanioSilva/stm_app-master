package com.rastreabilidadeInterna.centrodedistribuicao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.rastreabilidadeInterna.helpers.FileTransfer;
import com.rastreabilidadeInterna.helpers.LogGenerator;
import com.rastreabilidadeInterna.service.UploadService;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.System.err;
import static java.lang.System.in;

/**
 * Created by Felipe Pereira on 15/04/2015.
 */
public class HelperFTP {

    private Boolean enviou;

    public Context context;

    private Handler handler = new Handler();

    private final static String SERVIDOR = "52.204.225.11";
    private final static String NOME = "safetrace";
    private final static String SENHA = "rZ6TmMzmuqa2";

    public static File path = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao");
    public static File pathIdx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx");

    public static File pathCodes = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Estoque" + File.separator + "produtos_ce.txt");
    public static File pathCodesFr = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "codigos_conv_frac.txt");

    public static File pathPesosHF = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "peso_por_caixa.txt");

    public static File pathOrigem = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Origem");

//    public static File pathLaudos = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Laudos");



    public HelperFTP(Context context){

        this.context = context;

        if (!path.exists() || !path.isDirectory()) {
            path.mkdir();
            Log.i("Nao Existe", "True");
        }

    }

    public int enviarArquivos() {

        LogGenerator logGenerator = new LogGenerator(context);
        logGenerator.enviarLogs();

        Log.i("FILE", "INICIO");
        baixarArquivosOrigem();
        Log.i("FILE", "FIM");
        baixarArquivoDeCodigos();
        baixarArquivoDePesos();

        AppZip appZip = new AppZip();
        appZip.ziparTudo();

        if ((path.list().length > 0) || (pathIdx.list().length > 0)) {

            SharedPreferences settings = context.getSharedPreferences("Preferences", 0);
            final String aux = settings.getString("NUMCLIENTE", "") + settings.getString("NUMLOJA", "")+"_"+ settings.getString("NUMTABLET", "")+"_"+ diaAtual() + ".st";

            Thread thread = new Thread(new Runnable() {
                public void run() {

                    File fList[] = path.listFiles();
                    for(int i=0; i < (fList.length); i++){
                        File arquivo = fList[i];
                        Log.i("FILE", "RAS = " + arquivo.getName());
                        if (!arquivo.getName().contains("produtos_ce.txt")){
                            enviou = envioFTP(arquivo.getName(), 0, aux);
                            if (!enviou) break;
                        } else {
                            enviou = true;
                        }
                    }

                    File fListIdx[] = pathIdx.listFiles();
                    for(int i=0; i < (fListIdx.length); i++){
                        File arquivo = fListIdx[i];
                        Log.i("FILE", "IDX = "+arquivo.getName());
                        enviou = envioFTP(arquivo.getName(), 1, aux);
                        if (!enviou) break;
                    }

                    handler.post(new Runnable() {
                        public void run() {
                            if (!enviou) {

                            }

                        }
                    });

                }
            });
            thread.start();
            while (thread.isAlive()){
                // wait lol
            }
            enviarArquivos();
            return 1;
        }
//        FileTransfer fileTransfer = new FileTransfer(context);
//        fileTransfer.enviarLaudos();

        return 1;
    }

    private String diaAtual(){

        String aux="";
        int year = ((Calendar.getInstance().get(Calendar.YEAR)) % 10);

        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int CurrentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

        if(String.valueOf(CurrentDayOfYear).length() == 1){
            aux = "00"+CurrentDayOfYear;
        }
        else if(String.valueOf(CurrentDayOfYear).length() == 2){
            aux = "0"+CurrentDayOfYear;
        }
        else{
            aux = String.valueOf(CurrentDayOfYear);
        }

        return String.valueOf(year) + aux;

    }

    private Boolean envioFTP( String nomeArquivo, int flag, String idxAtual) {
        FTPClient ftp = new FTPClient();
        Boolean retorno = false;

        try {

            ftp.connect(SERVIDOR,21);
            ftp.login(NOME, SENHA);

            if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

                //				ftp.changeWorkingDirectory("Teste");
                File file;

                if(flag == 0){
                    file = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour"  + File.separator + "CentroDeDistribuicao" + File.separator + nomeArquivo);
                    Log.d("NOME ARQUIVO", file.toString());
                    FileInputStream arqEnviar = new FileInputStream(file);

                    Log.i("FILE", arqEnviar.toString());

                    ftp.enterLocalPassiveMode();
                    //ftp.changeWorkingDirectory("arquivos_novos");
                    ftp.changeWorkingDirectory("arquivos_novos");
                    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                    ftp.storeFile(nomeArquivo, arqEnviar);

                    arqEnviar.close();
                    file.delete();
                    retorno = true;

                }
                else{
                    ftp.enterLocalPassiveMode();

                    ftp.changeWorkingDirectory("STMarket");
                    ftp.changeWorkingDirectory("idx");

                    for (String name : ftp.listNames()) {
                        if(name.equals(nomeArquivo)){
                            //baixa esse arquivo
                            File arquivo2 = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + "temp.idx");

                            FileOutputStream desFileStream = new FileOutputStream(arquivo2.getAbsolutePath());
                            ftp.setFileType(FTP.BINARY_FILE_TYPE);
                            ftp.enterLocalPassiveMode();
                            ftp.retrieveFile(nomeArquivo, desFileStream);

                            Log.i("FILE", desFileStream.toString());
                            desFileStream.close();
                            break;
                        }
                    }
                    editaIdx2(nomeArquivo);

                    file = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + nomeArquivo);
                    Log.d("NOME ARQUIVO", file.toString());
                    FileInputStream arqEnviar = new FileInputStream(file);

                    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

                    ftp.storeFile(nomeArquivo, arqEnviar);
                    arqEnviar.close();

                    if(!nomeArquivo.equals(idxAtual)){
                        file.delete();
                    }

                    retorno = true;

                }
            }

            ftp.logout();
            ftp.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retorno;
    }

    private void editaIdx2(String nomeArquivo){
        File temp = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + "temp.idx");
        File idx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + nomeArquivo);

        ArrayList<String> records = new ArrayList<String>();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(temp));
            String line;
            while ((line = reader.readLine()) != null){
                records.add(line);
            }
            reader.close();

            reader = new BufferedReader(new FileReader(idx));
            while ((line = reader.readLine()) != null){
                records.add(line);
            }
            reader.close();

            temp.delete();
            idx.delete();

            idx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + nomeArquivo);
            FileOutputStream out = new FileOutputStream(idx);
            OutputStreamWriter OSW = new OutputStreamWriter(out, "ISO-8859-1");
            PrintWriter Print = new PrintWriter(OSW);

            String linha;

            for(int i=0; i<records.size(); i++){
                linha = records.get(i);
                if(i==records.size()-1)
                    Print.print(linha);
                else
                    Print.println(linha);
            }

            Print.close();
            OSW.close();
            out.close();
        }
        catch (Exception e){
            err.format("Exception occurred trying to read '%s'.", idx.getName());
            e.printStackTrace();
        }
    }

    private class AppZip{
        List<String> fileList;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

        private final String OUTPUT_ZIP_FILE = Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao" + File.separator + "ZIP_CDCE_" + context.getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "") + context.getSharedPreferences("Preferences", 0).getString("NUMLOJA", "") + context.getSharedPreferences("Preferences", 0).getString("NUMTABLET", "")+"_"+ simpleDateFormat.format(new java.util.Date()) + ".zip";
        private final String SOURCE_FOLDER = Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "CentroDeDistribuicao";

        AppZip(){
            fileList = new ArrayList<String>();
        }

        public void ziparTudo()
        {
            this.generateFileList(new File(SOURCE_FOLDER));
            this.zipIt(OUTPUT_ZIP_FILE);
        }

        /**
         * Zip it
         * @param zipFile output ZIP file location
         */
        public void zipIt(String zipFile){

            byte[] buffer = new byte[1024];

            try{

                if (this.fileList.size() > 1) {

                    FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos);

                    Log.i("Output to Zip", zipFile);


                    for (String file : this.fileList) {

                        if (file.equals("produtos_ce.txt")) {

                        } else{

//                            if (file.substring(file.length()-3).equals(".st")) {

                                Log.i("File Added", file);
                                ZipEntry ze = new ZipEntry(file);
                                zos.putNextEntry(ze);

                                FileInputStream in =
                                        new FileInputStream(SOURCE_FOLDER + File.separator + file);

                                int len;
                                while ((len = in.read(buffer)) > 0) {
                                    zos.write(buffer, 0, len);
                                }

                                new File(SOURCE_FOLDER + File.separator + file).delete();

//                            }
                        }

                    }

                    in.close();

                    zos.closeEntry();
                    //remember close it
                    zos.close();

                    System.out.println("Done");
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }

        /**
         * Traverse a directory and get all files,
         * and add the file into fileList
         * @param node file or directory
         */
        public void generateFileList(File node){

            //add file only0
            if(node.isFile()){
                fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
            }

            if(node.isDirectory()){
                String[] subNote = node.list();
                for(String filename : subNote){
                    generateFileList(new File(node, filename));
                }
            }

        }

        /**
         * Format the file path for zip
         * @param file file path
         * @return Formatted file path
         */
        private String generateZipEntry(String file){
            return file.substring(SOURCE_FOLDER.length()+1, file.length());
        }
    }

    private void baixarArquivoDeCodigos(){
        SharedPreferences settings = context.getSharedPreferences("Preferences", 0);
        Log.i("Processo de Download", "Iniciado");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPClient ftpClient = new FTPClient();

                try {
                    ftpClient.connect(SERVIDOR, 21);
                    ftpClient.login(NOME, SENHA);

                    if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){

                        ftpClient.changeWorkingDirectory("/STMarket");
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        ftpClient.enterLocalPassiveMode();

                        String arquivoRemoto = "produtos_ce.txt";
                        String arquivoRemotoFr = "codigos_conv_frac.txt";

                        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pathCodes));
                        OutputStream outputStreamFr = new BufferedOutputStream(new FileOutputStream(pathCodesFr));

                        boolean sucesso = ftpClient.retrieveFile(arquivoRemoto, outputStream);

                        outputStream.close();

                        if (sucesso) {
                            Log.i("Arquivo Status: ", "Baixado");
                        } else {
                            Log.i("Arquivo Status: ", "Failure");
                        }

                        sucesso = ftpClient.retrieveFile(arquivoRemotoFr, outputStreamFr);

                        outputStreamFr.close();

                        if (sucesso) {
                            Log.i("Arquivo Status: ", "Baixado");
                        } else {
                            Log.i("Arquivo Status: ", "Failure");
                        }

                        ftpClient.disconnect();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void baixarArquivoDePesos(){
        Log.i("Processo de Download", "Iniciado");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPClient ftpClient = new FTPClient();

                try {
                    ftpClient.connect(SERVIDOR, 21);
                    ftpClient.login(NOME, SENHA);

                    if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){

                        ftpClient.changeWorkingDirectory("/STMarket");
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        ftpClient.enterLocalPassiveMode();

                        String arquivoRemoto = "peso_por_caixa.txt";

                        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pathPesosHF));

                        boolean sucesso = ftpClient.retrieveFile(arquivoRemoto, outputStream);

                        outputStream.close();
                        ftpClient.disconnect();

                        if (sucesso){
                            fillDataBase();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void fillDataBase(){
        ModelProdutoHortifruti.deleteAll(ModelProdutoHortifruti.class);

        try {
            BufferedReader leitorBufferizado = new BufferedReader(new FileReader(pathPesosHF));
            String linhaAtual;
            while ((linhaAtual = leitorBufferizado.readLine()) != null){
                String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("*"));

                ModelProdutoHortifruti modelProdutoHortifruti = new ModelProdutoHortifruti();

                modelProdutoHortifruti.setNomeProduto(splittedLinhaAtual[0]);
                modelProdutoHortifruti.setPesoProduto(
                        Double.parseDouble(splittedLinhaAtual[1]
                                .replace(",", ".")      // No arquivo vem com , e nao .
                                .replace(" ", ""))      // Limpando espaços
                                * 1000);                // Kg para g

                modelProdutoHortifruti.save();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public void baixarArquivosOrigem(){
        Log.i("FILE", "Iniciado");
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
                FTPClient ftpClient = new FTPClient();
                Log.i("FILE", "1");

                try {
                    ftpClient.connect(SERVIDOR, 21);
                    ftpClient.login(NOME, SENHA);
                    Log.i("FILE", "2");

                    if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){

                        Log.i("FILE", "APAGAR PASTA");
                        deleteDir(pathOrigem);

                        if (!pathOrigem.exists() || !pathOrigem.isDirectory()) {
                            pathOrigem.mkdir();
                            Log.i("Criando pasta Origem", "True");
                        }


                        Log.i("FILE", "ACESSAR PASTA ORIGEM NO FTP");

                        SharedPreferences settings = context.getSharedPreferences("Preferences", 0);
                        final String pasta = ("/origem/") + settings.getString("NUMLOJA", "");
//                        ftpClient.changeWorkingDirectory("/origem/") + settings.getString("NUMLOJA", "");
//                        String pasta = ("/origem/") + getString("NUMLOJA", "");
                        ftpClient.changeWorkingDirectory(pasta);
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        ftpClient.enterLocalPassiveMode();

                        FTPFile[] files = ftpClient.listFiles();

                        Log.i("FILE", "BAIXAR "+String.valueOf(files.length)+" ARQUIVOS");
                        for (int i=0; i<files.length; i++){

                            String arquivoRemoto = (files[i]).getName();
                            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pathOrigem+File.separator+"___"+arquivoRemoto));
                            boolean sucesso = ftpClient.retrieveFile(arquivoRemoto, outputStream);
                            outputStream.close();

                            Log.i("FILE", "BAIXOU "+arquivoRemoto);

                        }

                        ftpClient.disconnect();
                    }
                } catch (Exception e){
                    Log.i("FILE", "ERRO");
                    e.printStackTrace();
                }
        //    }
        //}).start();
    }

}
