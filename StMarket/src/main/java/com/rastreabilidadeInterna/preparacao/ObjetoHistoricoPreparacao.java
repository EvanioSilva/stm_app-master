package com.rastreabilidadeInterna.preparacao;

import java.util.ArrayList;

/**
 * Created by Felipe Pereira on 01/04/2015.
 */
public class ObjetoHistoricoPreparacao {

    public ObjetoHistoricoPreparacao() {
        ingredientesReceita = new ArrayList<Ingrediente>();
    }

    public Ingrediente getIngredienteInstance() {
        return new Ingrediente();
    }

    public class Ingrediente{
        public String codigoIngrediente;
        public String nomeIngrediente;
        public String dataFabIngrediente;
        public String dataValIngrediente;
        public String loteIngrediente;

        public Ingrediente() {
        }

        @Override
        public String toString() {
            return "Ingrediente{" +
                    "codigoIngrediente='" + codigoIngrediente + '\'' +
                    ", nomeIngrediente='" + nomeIngrediente + '\'' +
                    ", dataFabIngrediente='" + dataFabIngrediente + '\'' +
                    ", dataValIngrediente='" + dataValIngrediente + '\'' +
                    ", loteIngrediente='" + loteIngrediente + '\'' +
                    '}';
        }

        public void setCodigoIngrediente(String codigoIngrediente) {
            this.codigoIngrediente = codigoIngrediente;
        }

        public void setNomeIngrediente(String nomeIngrediente) {
            this.nomeIngrediente = nomeIngrediente;
        }

        public void setDataFabIngrediente(String dataFabIngrediente) {
            this.dataFabIngrediente = dataFabIngrediente;
        }

        public void setDataValIngrediente(String dataValIngrediente) {
            this.dataValIngrediente = dataValIngrediente;
        }

        public void setLoteIngrediente(String loteIngrediente) {
            this.loteIngrediente = loteIngrediente;
        }
    }

    public String codigoReceita;
    public String nomeReceita;
    public ArrayList<Ingrediente> ingredientesReceita;

    @Override
    public String toString() {
        String s = "ObjetoHistoricoPreparacao{" +
                "codigoReceita='" + codigoReceita + '\'' +
                ", nomeReceita='" + nomeReceita + '\'' +
                ", ingredientesReceita=";

        for (Ingrediente i : ingredientesReceita){
            s += "["+i.codigoIngrediente +"-"+
                    i.nomeIngrediente +"-"+
                    i.dataFabIngrediente +"-"+
                    i.dataValIngrediente +"-"+ i.loteIngrediente+
                    "]";
        }

        s += '}';
        return s;
    }
}
