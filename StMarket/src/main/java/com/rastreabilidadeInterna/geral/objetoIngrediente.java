package com.rastreabilidadeInterna.geral;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class objetoIngrediente {

	public static String[] colunas = new String[] { objetoIngredientes.NOMEINGREDIENTE, objetoIngredientes.CODIGO,
		objetoIngredientes.PESO, objetoIngredientes.DIASVAL
	};

	// Pacote do Content Provider. Precisa ser œnico.
	public static final String AUTHORITY = "nome.do.pacote.provider.";
	
	public String nomeIngrediente;
	public String codigo;
	public String peso;
	public String diasVal;

	public objetoIngrediente() {
	}

	public static final class objetoIngredientes implements BaseColumns {

		private objetoIngredientes() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/objetoIngredientes");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.objetoIngredientes";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.objetoIngredientes";

		// Ordenacao default para inserir no order by
		public static final String NOMEINGREDIENTE = "nomeIngrediente";
		public static final String CODIGO = "codigo";
		public static final String PESO = "peso";
		public static final String DIASVAL = "diasVal";


		public static Uri getUriId(long _id) {
			Uri uriobjetoIngrediente = ContentUris.withAppendedId(objetoIngredientes.CONTENT_URI,
					_id);
			return uriobjetoIngrediente;
		}
	}

	@Override
	public String toString() {
		return 	" nome Ingrediente: " + nomeIngrediente + 
				" codigo: " + codigo +
				" Peso: " + peso +
				" dias validade: " + diasVal;	
	}

}
