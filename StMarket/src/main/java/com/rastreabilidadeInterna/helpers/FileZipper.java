package com.rastreabilidadeInterna.helpers;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.System.in;

/**
 * Created by felipe on 23/07/15.
 */
public class FileZipper {
    private List<String> fileList;
    private String numCliente;
    private String numLoja;
    private String numTablet;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

    private String outputZipFile;
    private String inputFolder;

    public FileZipper(String numCliente, String numLoja, String numTablet, String outputZipFile, String inputFolder) {
        this.numCliente = numCliente;
        this.numLoja = numLoja;
        this.numTablet = numTablet;
        this.outputZipFile = outputZipFile;
        this.inputFolder = inputFolder;
        fileList = new ArrayList<String>();
    }

    public void ziparTudo() {
        this.generateFileList(new File(inputFolder));
        this.zipIt(outputZipFile);
    }

    /**
     * Zip it
     *
     * @param zipFile output ZIP file location
     */
    public void zipIt(String zipFile) {

        byte[] buffer = new byte[1024];

        try {

            if (this.fileList.size() > 1) {

                FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos);

                Log.i("Output to Zip", zipFile);


                for (String file : this.fileList) {

                    if (file.equals("produtos_ce.txt")) {

                    } else {

                        if (file.substring(file.length() - 3).equals(".st")) {

                            Log.i("File Added", file);
                            ZipEntry ze = new ZipEntry(file);
                            zos.putNextEntry(ze);

                            FileInputStream in =
                                    new FileInputStream(inputFolder + File.separator + file);

                            int len;
                            while ((len = in.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }

                            new File(inputFolder + File.separator + file).delete();

                        }
                    }

                }

                in.close();

                zos.closeEntry();
                //remember close it
                zos.close();

                System.out.println("Done");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     *
     * @param node file or directory
     */
    public void generateFileList(File node) {

        //add file only0
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }

    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file) {
        return file.substring(inputFolder.length() + 1, file.length());
    }
}
