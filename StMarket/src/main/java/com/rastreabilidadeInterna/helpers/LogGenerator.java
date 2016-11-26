package com.rastreabilidadeInterna.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Felipe Pereira on 05/05/2015.
 */
public class LogGenerator {

    File logFile;
    File folder;
    PrintWriter out;
    Context context;

    public LogGenerator(Context context){
        this.context = context;

        folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "logs");


        if (!folder.exists()){
            folder.mkdir();
        }

        String date = new SimpleDateFormat("dd_MM_yyyy").format(new Date());
        logFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" +
                File.separator + "logs" +
                File.separator + "LOG_"
                + context.getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMLOJA", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMTABLET", "")+ "_" + date + ".nsl");
    }

    private String timestamp(){
        return new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss::").format(new Date());
    }

    public void append(String message){
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
            out.println(timestamp() + message);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarLogs(){
        if (folder.list().length > 0) {
            new Thread(new Runnable() {
                public void run() {
                    File fList[] = folder.listFiles();

                    for(int i=0; i < (fList.length); i++){
                        File arquivo = fList[i];
                        if(arquivo.isFile()){
                            boolean enviou;
                            enviou = envioFTP(arquivo.getName());
                            if (!enviou) break;
                        }
                    }

                }
            }).start();
        }
    }

    private Boolean envioFTP( String nomeArquivo ) {
        FTPClient ftp = new FTPClient();
        Boolean retorno = false;

        FtpConnectionHelper ftpConnectionHelper = new FtpConnectionHelper();

        try {

            ftp.connect(ftpConnectionHelper.getServidorFtp(), ftpConnectionHelper.getPortaFtp());
            ftp.login(ftpConnectionHelper.getUsuarioFtp(), ftpConnectionHelper.getSenhaFtp());

//			ftp.changeWorkingDirectory("Teste");

            if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "logs" + File.separator + nomeArquivo);
                FileInputStream arqEnviar = new FileInputStream(file);

                ftp.enterLocalPassiveMode();
                ftp.changeWorkingDirectory("log_stm");
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);


                ftp.storeFile(nomeArquivo, arqEnviar);

                arqEnviar.close();
                file.delete();
                retorno = true;
            }


            ftp.logout();
            ftp.disconnect();

        } catch (Exception e){
            e.printStackTrace();
        }
        return retorno;
    }

}
