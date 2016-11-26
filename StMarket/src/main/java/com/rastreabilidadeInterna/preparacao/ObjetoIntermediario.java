package com.rastreabilidadeInterna.preparacao;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ObjetoIntermediario {

		public static String[] colunas = new String[] { 
			ObjetoIntermediarios.CODIGO, ObjetoIntermediarios.DATAFAB, ObjetoIntermediarios.DATAVAL
		};

		public static final String AUTHORITY = "com.rastreabilidadeInterna.preparacao.objetointermediario";

		public String codigo; 
		public String dataFab;
		public String dataVal;

		public ObjetoIntermediario() {
		}

		public static final class ObjetoIntermediarios implements BaseColumns {

			private ObjetoIntermediarios() {
			}

			public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/objetointermediarios");

			public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.ObjetoIntermediarios";

			public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.ObjetoIntermediarios";

			// Ordenacao default para inserir no order by
			public static final String DEFAULT_SORT_ORDER = "_ID ASC";
			public static final String CODIGO = "codigo";
			public static final String DATAFAB = "dataFab";
			public static final String DATAVAL = "dataVal";
			
			public static Uri getUriId(long _id) {
				Uri uriobjetoPaoDeLo = ContentUris.withAppendedId(ObjetoIntermediarios.CONTENT_URI,_id);
				return uriobjetoPaoDeLo;
			}
		}

		@Override
		public String toString() {
			return 	  "	Codigo: " + codigo 
					+ " Data Fabric: " + dataFab 
					+ " Data Valid: " + dataVal ;
		}

}
