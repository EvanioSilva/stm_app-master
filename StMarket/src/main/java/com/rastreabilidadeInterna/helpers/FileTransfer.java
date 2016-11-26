package com.rastreabilidadeInterna.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.rastreabilidadeInterna.models.CodeIndex;
import com.rastreabilidadeInterna.models.ServerMetadata;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static java.lang.System.err;

/**
 * Created by Felipe Pereira on 09/03/2015.
 */
public class FileTransfer {

    public static final String FTP_HOST = "52.204.225.11";
    public static final String FTP_USER = "safetrace";
    public static final String FTP_PASSWORD = "9VtivgcVTy0PI";
    public static final int FTP_PORT = 21;

    private String numCliente;
    private String numLoja;
    private String numTablet;

    private Context context;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

    File pathIdx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx");

    private static final String pathToIndex = Environment.getExternalStorageState() +
            File.separator + "Carrefour" + File.separator + "IndexFiles";

    private FTPClient ftpClient;
    private boolean connected;

    public FileTransfer(Context context) {
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(FTP_HOST, FTP_PORT);
            ftpClient.login(FTP_USER, FTP_PASSWORD);
            setConnected(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("Preferences", 0);
        numCliente = preferences.getString("NUMCLIENTE", "");
        numLoja = preferences.getString("NUMLOJA", "");
        numTablet = preferences.getString("NUMTABLET", "");
    }

    public String getNumCliente() {
        return numCliente;
    }

    public void setNumCliente(String numCliente) {
        this.numCliente = numCliente;
    }

    public String getNumLoja() {
        return numLoja;
    }

    public void setNumLoja(String numLoja) {
        this.numLoja = numLoja;
    }

    public String getNumTablet() {
        return numTablet;
    }

    public void setNumTablet(String numTablet) {
        this.numTablet = numTablet;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<CodeIndex> retrieveIndexes() {
        List<CodeIndex> codeIndexList = new ArrayList<CodeIndex>();

        List<FTPFile> indexFiles = getIndexFiles();

        CodeIndex.deleteAll(CodeIndex.class);

        for (FTPFile indexFile : indexFiles) {
            try {
                OutputStream outputStream = new FileOutputStream(
                        new File(pathToIndex + File.separator + indexFile.getName())
                );
                ftpClient.retrieveFile(indexFile.getName(), outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return codeIndexList;
    }

    private List<FTPFile> getIndexFiles() {

        try {
            ftpClient.changeWorkingDirectory("/STMarket/index");
            List<ServerMetadata> cdMetadata = ServerMetadata.find(ServerMetadata.class, "tag = 'CD'");
            FTPFile[] ftpFiles = ftpClient.listFiles();
            List<FTPFile> indexFiles = new ArrayList<FTPFile>();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.getName().startsWith("CD")) {
                    indexFiles.add(ftpFile);
                }
            }
            return indexFiles;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void enviarControleDeEstoque() {
        String folderIn = Environment.getExternalStorageDirectory() + File.separator
                + "Carrefour" + File.separator
                + "Estoque";
        String fileZipOut = Environment.getExternalStorageDirectory() + File.separator
                + "Carrefour" + File.separator
                + "Estoque" + File.separator
                + "ZIP_CE_"
                + context.getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMLOJA", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMTABLET", "") + "_"
                + simpleDateFormat.format(new java.util.Date()) + ".zip";
        FileZipper fileZipper = new FileZipper(getNumCliente(), getNumLoja(), getNumTablet(), fileZipOut, folderIn);
        fileZipper.ziparTudo();

        File path = new File(folderIn);

        sendEverythingInFolder(folderIn);
    }

    public void enviarFracionamento() {
        String folderIn = Environment.getExternalStorageDirectory() + File.separator
                + "Carrefour" + File.separator
                + "Fracionamento";
        String fileZipOut = Environment.getExternalStorageDirectory() + File.separator
                + "Carrefour" + File.separator
                + "Fracionamento" + File.separator
                + "ZIP_FR_"
                + context.getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMLOJA", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMTABLET", "") + "_"
                + simpleDateFormat.format(new java.util.Date()) + ".zip";
        FileZipper fileZipper = new FileZipper(getNumCliente(), getNumLoja(), getNumTablet(), folderIn, fileZipOut);
        fileZipper.ziparTudo();

        sendEverythingInFolder(folderIn);
    }

    public void enviarPreparacao() {
        String folderIn = Environment.getExternalStorageDirectory() + File.separator
                + "Carrefour" + File.separator
                + "Preparacao";
        String fileZipOut = Environment.getExternalStorageDirectory() + File.separator
                + "Carrefour" + File.separator
                + "Preparacao" + File.separator
                + "ZIP_PR_"
                + context.getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMLOJA", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMTABLET", "") + "_"
                + simpleDateFormat.format(new java.util.Date()) + ".zip";
        FileZipper fileZipper = new FileZipper(getNumCliente(), getNumLoja(), getNumTablet(), folderIn, fileZipOut);
        fileZipper.ziparTudo();

        sendEverythingInFolder(folderIn);
    }

    public void enviarCentroDeDistribuicao() {
        String folderIn = Environment.getExternalStorageDirectory() + File.separator
                + "Carrefour" + File.separator
                + "CentroDeDistribuicao";
        String fileZipOut = Environment.getExternalStorageDirectory() + File.separator
                + "Carrefour" + File.separator
                + "CentroDeDistribuicao" + File.separator
                + "ZIP_CD_"
                + context.getSharedPreferences("Preferences", 0).getString("NUMCLIENTE", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMLOJA", "")
                + context.getSharedPreferences("Preferences", 0).getString("NUMTABLET", "") + "_"
                + simpleDateFormat.format(new java.util.Date()) + ".zip";
        FileZipper fileZipper = new FileZipper(getNumCliente(), getNumLoja(), getNumTablet(), folderIn, fileZipOut);
        fileZipper.ziparTudo();

        sendEverythingInFolder(folderIn);
    }

    private void sendEverythingInFolder(String folderIn) {
        try {
            File folder = new File(folderIn);

            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory("/arquivos_novos");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            for (File file : folder.listFiles()) {
                if (file.isFile() && !file.getName().equals("produtos_ce.txt")) {
                    FileInputStream arqEnviar = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), arqEnviar);
                    arqEnviar.close();
                    file.delete();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        sendIndexFiles();
    }

    private void sendIndexFiles() {
        ftpClient.enterLocalPassiveMode();

        try {
            ftpClient.changeWorkingDirectory("/STMarket/idx");

            File fListIdx[] = pathIdx.listFiles();

            for (File idx : fListIdx) {
                for (String name : ftpClient.listNames()) {
                    if (name.equals(idx.getName())) {
                        //baixa esse arquivo
                        File arquivo2 = new File(Environment.getExternalStorageDirectory()
                                + File.separator + "Carrefour"
                                + File.separator + "Idx"
                                + File.separator + "temp.idx");

                        FileOutputStream desFileStream = new FileOutputStream(arquivo2.getAbsolutePath());
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        ftpClient.enterLocalPassiveMode();
                        ftpClient.retrieveFile(idx.getName(), desFileStream);

                        desFileStream.close();
                        break;
                    }
                }
                editaIdx2(idx.getName());

                FileInputStream arqEnviar = new FileInputStream(idx);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.storeFile(idx.getName(), arqEnviar);
                final String idxAtual = getNumCliente() + getNumLoja() + "_" + getNumTablet() + "_" + diaAtual() + ".st";
                arqEnviar.close();
                if (!idx.getName().equals(idxAtual)) {
                    idx.delete();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String diaAtual() {

        String aux = "";
        int year = ((Calendar.getInstance().get(Calendar.YEAR)) % 10);

        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int CurrentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

        if (String.valueOf(CurrentDayOfYear).length() == 1) {
            aux = "00" + CurrentDayOfYear;
        } else if (String.valueOf(CurrentDayOfYear).length() == 2) {
            aux = "0" + CurrentDayOfYear;
        } else {
            aux = String.valueOf(CurrentDayOfYear);
        }

        return String.valueOf(year) + aux;

    }

    private void editaIdx2(String nomeArquivo) {
        File temp = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + "temp.idx");
        File idx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + nomeArquivo);

        ArrayList<String> records = new ArrayList<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(temp));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();

            reader = new BufferedReader(new FileReader(idx));
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();

            temp.delete();
            idx.delete();

            idx = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Idx" + File.separator + nomeArquivo);
            FileOutputStream out = new FileOutputStream(idx);
            OutputStreamWriter OSW = new OutputStreamWriter(out, "UTF-8");
            PrintWriter Print = new PrintWriter(OSW);

            String linha;

            for (int i = 0; i < records.size(); i++) {
                linha = records.get(i);
                if (i == records.size() - 1)
                    Print.print(linha);
                else
                    Print.println(linha);
            }

            Print.close();
            OSW.close();
            out.close();
        } catch (Exception e) {
            err.format("Exception occurred trying to read '%s'.", idx.getName());
            e.printStackTrace();
        }
    }

    public void enviarLaudos() {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() +
            File.separator + "Carrefour" +
            File.separator + "Laudos");

            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory("/Laudos");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    FileInputStream arqEnviar = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), arqEnviar);
                    arqEnviar.close();
                    file.delete();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        sendIndexFiles();
    }
}