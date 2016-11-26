package com.rastreabilidadeInterna.centrodedistribuicao.objects;

import android.os.Environment;

import com.rastreabilidadeInterna.centrodedistribuicao.ModelProdutoHortifruti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by root on 17/07/16.
 */
public class MemFile {

    private String fileName;

    private String code;

    public static File pathOrigem = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour" + File.separator + "Origem");

    private Map<Integer, String> answers = new HashMap<Integer, String>();

    public MemFile(String fileName){
        this.fileName = fileName;
        startRead();
    }

    private void startRead(){
        try {
            BufferedReader leitorBufferizado = new BufferedReader(new FileReader(pathOrigem+File.separator+this.fileName));
            String linhaAtual;

            int lineCounter = 0;

            while ((linhaAtual = leitorBufferizado.readLine()) != null){

                lineCounter++;

                if (lineCounter == 5){
                    this.code = linhaAtual;
                }

                if (lineCounter>6) {

                    String[] splittedLinhaAtual = linhaAtual.split(Pattern.quote("|"));

                    int index = Integer.valueOf(splittedLinhaAtual[0]);
                    String answer = splittedLinhaAtual[1];

                    answers.put(index,answer);
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName(){
        return this.fileName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAnswer(int index){
        if (answers.containsKey(index)){
            return answers.get(index);
        } else {
            return null;
        }
    }

    public void setAnswer(int index, String answer){
        if (answers.containsKey(index)){
            update(index, answer);
        } else {
            insert(index, answer);
        }
    }

    private void update(int index, String answer){

    }

    private void insert(int index, String answer){
        String newLine = System.getProperty("line.separator");
        PrintWriter printWriter = null;

        try {
            printWriter = new PrintWriter(new FileOutputStream(pathOrigem+File.separator+fileName, true));
            printWriter.write(newLine + String.valueOf(index)+"|"+answer);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
        }
    }


}
