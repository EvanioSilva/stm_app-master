package com.rastreabilidadeInterna.geral;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class objetoReceita {

	public static String[] colunas = new String[] { objetoReceitas.RECEITA, objetoReceitas.INGREDIENTE, objetoReceitas.LOCAL, objetoReceitas.INTERMEDIARIA
	};

	// Pacote do Content Provider. Precisa ser œnico.
	public static final String AUTHORITY = "nome.do.pacote.provider.";
	
	public String receita;
	public String ingrediente;
	public String local;
	public String intermediaria;

	public objetoReceita() {
	}

	public static final class objetoReceitas implements BaseColumns {

		private objetoReceitas() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/objetoReceitas");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.objetoReceitas";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.objetoReceitas";

		// Ordenacao default para inserir no order by
		public static final String DEFAULT_SORT_ORDER = "_ID ASC";
		public static final String RECEITA = "receita";
		public static final String INGREDIENTE = "ingrediente";
		public static final String  LOCAL = "local";
		public static final String INTERMEDIARIA = "intermediaria";


		public static Uri getUriId(long _id) {
			Uri uriobjetoReceita = ContentUris.withAppendedId(objetoReceitas.CONTENT_URI,
					_id);
			return uriobjetoReceita;
		}
	}

	@Override
	public String toString() {
		return 	" Receita: " + receita + 
				" Ingrediente: " + ingrediente +
				" Local: " + local +
				" Intermediaria: " + intermediaria ;	
	}

}
