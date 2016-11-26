package com.rastreabilidadeInterna.helpers;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rastreabilidadeInterna.centrodedistribuicao.ModelProdutoRecebido;
import com.rastreabilidadeInterna.centrodedistribuicao.ModelProdutoRecebidoHortifruti;
import com.rastreabilidadeInterna.centrodedistribuicao.ModelRecepcao;
import com.rastreabilidadeInterna.centrodedistribuicao.ModelRecepcaoHortifruti;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by felipe on 17/08/15.
 */
public class Laudo {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static void generateCDLaudo(
            ModelRecepcao modelRecepcao,
            ModelProdutoRecebido modelProdutoRecebido,
            ArrayList<String> savedImages, ArrayList<String> rotulagem){

        Gson gson = new GsonBuilder().create();

        ArrayList<String> actualImages = new ArrayList<String>();
        ArrayList<String> actualRotulos = new ArrayList<String>();

        for (String savedImage : savedImages){
            File f = new File(savedImage);
            actualImages.add(f.getName());
        }

        for (String rotulo : rotulagem){
            File f = new File(rotulo);
            actualRotulos.add(f.getName());
        }

        JSONObject jsonObject = new JSONObject();

        try {
            Writer writer = new FileWriter(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "Carrefour"
                            + File.separator + "Laudos"
                            + File.separator + fileDate() + ".json");

            jsonObject.put("recepcao", gson.toJson(modelRecepcao));
            jsonObject.put("produto", gson.toJson(modelProdutoRecebido));
            jsonObject.put("images", gson.toJson(actualImages));
            jsonObject.put("cd", "frios");
            jsonObject.put("rotulos", gson.toJson(actualRotulos));

            writer.append(jsonObject.toString());
            writer.close();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String fileDate(){
        return dateFormat.format(new Date());
    }

    public static void generateCDHLaudo(
            ModelRecepcaoHortifruti modelRecepcaoHortifruti,
            ModelProdutoRecebidoHortifruti modelProdutoRecebidoHortifruti,
            ArrayList<String> savedImagesGraves, ArrayList<String> savedImagesLeves, ArrayList<String> savedImagesOutros) {

        Gson gson = new GsonBuilder().create();

        ArrayList<String> actualImagesGraves = new ArrayList<String>();
        ArrayList<String> actualImagesLeves = new ArrayList<String>();
        ArrayList<String> actualImagesOutros = new ArrayList<String>();

        for (String savedImage : savedImagesGraves){
            File f = new File(savedImage);
            actualImagesGraves.add(f.getName());
        }

        for (String savedImage : savedImagesLeves){
            File f = new File(savedImage);
            actualImagesLeves.add(f.getName());
        }

        for (String savedImage : savedImagesOutros){
            File f = new File(savedImage);
            actualImagesOutros.add(f.getName());
        }

        JSONObject jsonObject = new JSONObject();

        try {
            Writer writer = new FileWriter(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "Carrefour"
                            + File.separator + "Laudos"
                            + File.separator + fileDate() + ".json");

            jsonObject.put("recepcao", gson.toJson(modelRecepcaoHortifruti));
            jsonObject.put("produto", gson.toJson(modelProdutoRecebidoHortifruti));
            jsonObject.put("imagesGraves", gson.toJson(actualImagesGraves));
            jsonObject.put("imagesLeves", gson.toJson(actualImagesLeves));
            jsonObject.put("imagesOutros", gson.toJson(actualImagesOutros));
            jsonObject.put("cd", "hortifruti");

            writer.append(jsonObject.toString());
            writer.close();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
