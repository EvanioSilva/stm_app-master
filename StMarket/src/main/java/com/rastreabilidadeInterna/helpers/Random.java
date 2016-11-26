package com.rastreabilidadeInterna.helpers;

/**
 * Created by Felipe Pereira on 09/06/2015.
 */
public class Random {

    public static final double CHANCE = 0.5;

    public static final boolean getBoolean(){
        return Math.random() >= CHANCE ? false : true;
    }

    public static final boolean getBoolean(double chance){
        return Math.random() >= chance ? false : true;
    }

}