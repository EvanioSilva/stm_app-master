package com.rastreabilidadeInterna.geral;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class Usuario {
	public static final String AUTHORITY = "nome.do.pacote.provider.";
	public long _id;
	private String nome;
	private String cpf;
	private String senha; 
	public static String[] colunas = new String[] { Usuarios.CPF, 
		Usuarios.NOME, Usuarios.SENHA
	};
	public Usuario(String n, String c, String s){
		this.nome = n;
		this.cpf = c;
		this.senha = s;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public static final class Usuarios implements BaseColumns {

		private Usuarios() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/Usuarios");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.Usuarios";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.Usuarios";

		// Ordenacao default para inserir no order by
		public static final String DEFAULT_SORT_ORDER = "_ID ASC";
		public static final String CPF = "cpf";
		public static final String NOME = "nome";
		public static final String SENHA = "senha";		


		public static Uri getUriId(long _id) {
			Uri Usuario = ContentUris.withAppendedId(Usuarios.CONTENT_URI,
					_id);
			return Usuario;
		}
	}
	@Override
	public String toString() {
		return "Usuario [nome=" + nome + ", cpf=" + cpf + ", senha=" + senha
				+ "]";
	}
	
	
	
	
}
