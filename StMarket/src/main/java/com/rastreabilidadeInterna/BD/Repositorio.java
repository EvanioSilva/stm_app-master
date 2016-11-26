package com.rastreabilidadeInterna.BD;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.rastreabilidadeInterna.centrodedistribuicao.ModelEtiquetaEstoqueCentroDeDistribuicao;
import com.rastreabilidadeInterna.centrodedistribuicao.ModelProdutoRecebido;
import com.rastreabilidadeInterna.centrodedistribuicao.ModelRecepcao;
import com.rastreabilidadeInterna.controleEstoque.objetoControleEstoque;
import com.rastreabilidadeInterna.controleEstoque.objetoControleEstoque.objetoControleEstoques;
import com.rastreabilidadeInterna.controleEstoque.objetoControleEstoqueCodSafe;
import com.rastreabilidadeInterna.controleEstoque.objetoControleEstoqueCodSafe.objetoControleEstoqueCodSafes;
import com.rastreabilidadeInterna.fracionamento.ModelIndexBaixado;
import com.rastreabilidadeInterna.fracionamento.objetoFracionamento;
import com.rastreabilidadeInterna.fracionamento.objetoFracionamento.objetoFracionamentos;
import com.rastreabilidadeInterna.geral.Usuario;
import com.rastreabilidadeInterna.geral.objetoIngrediente;
import com.rastreabilidadeInterna.geral.objetoReceita;
import com.rastreabilidadeInterna.geral.objetoIngrediente.objetoIngredientes;
import com.rastreabilidadeInterna.geral.objetoReceita.objetoReceitas;
import com.rastreabilidadeInterna.preparacao.ObjetoIntermediario;
import com.rastreabilidadeInterna.preparacao.ObjetoIntermediario.ObjetoIntermediarios;

public class Repositorio {

    //		private SQLiteDatabase sqLiteDatabase;

    // Nome da Categoria
    private static final String CATEGORIA = "categoria";
    // Nome do banco
    private static final String NOME_BANCO = "carrefoursqlite";
    // Nome das tabelas
    public static final String NOME_TABELA_1 = "controleEstoque_Origem";
    public static final String NOME_TABELA_2 = "controleEstoque_CodSafe";
    public static final String NOME_TABELA_3 = "fracionamento";
    public static final String NOME_TABELA_4 = "tipoProduto";
    public static final String NOME_TABELA_5 = "idxBaixado";
    public static final String NOME_TABELA_6 = "novaReceita";
    public static final String NOME_TABELA_7 = "receitaDia";
    public static final String NOME_TABELA_8 = "paoDeLo";
    public static final String NOME_TABELA_9 = "Receita";
    public static final String NOME_TABELA_10 = "Ingrediente";
    public static final String NOME_TABELA_11 = "Usuario";

    // Controle de versao

    // Versão   1: Criação
    //          2: Modificação no IDX beta
    //          >= 3: Modificação no IDX
    //          5: Modificação no Fracionamento
    //          >= 7: CD
    //          >= 22: Produção Diária
    //          >= 27: dados extras no fracionamento
    //			>= 31: CD multi area
    private static final int VERSAO_BANCO = 35 ;
    // Script para fazer drop na tabela
    private static final String SCRIPT_DATABASE_DELETE = "DROP TABLE IF EXISTS controleEstoque_Origem; "
            + "DROP TABLE IF EXISTS controleEstoque_CodSafe; " + "DROP TABLE IF EXISTS fracionamento; "
            + "DROP TABLE IF EXISTS tipoProduto; " + "DROP TABLE IF EXISTS idxBaixado; " + "DROP TABLE IF EXISTS novaReceita; "
            + "DROP TABLE IF EXISTS receitaDia; " + "DROP TABLE IF EXISTS paoDeLo; " + "DROP TABLE IF EXISTS Receita; "
            + "DROP TABLE IF EXISTS Ingrediente; " + "DROP TABLE IF EXISTS Usuario; ";

    private static final String[] SCRIPT_DATABASE_CREATE = new String[]{
            "create table controleEstoque_Origem ( _id integer primary key autoincrement, codigo text, fabricante text, sif text, dataFab text, dataVal text, qtd text, caixa text, lote text, usuario text);",
            "create table controleEstoque_CodSafe (_id integer primary key autoincrement, codigoSafe text, tipo text);",
            "create table fracionamento (_id integer primary key autoincrement, seloSafe text, novoSelo text, dataLeitura text, flag integer, usuario text);",
            "create table tipoProduto (tipo text);",
            "create table idxBaixado (selo text, tipo text, data date);",
            "create table novaReceita (receita text, ingrediente text, local text, intermediaria text);",
            "create table receitaDia (receita text, codigo text, dataHoje text,dataFab text, dataVal text);",
            "create table paoDeLo (codigo text, dataFab text, dataVal text);",
            "create table Receita (nomeReceita text);",
            "create table Usuario (cpf text primary key, nome text, senha text);",
            "create table Ingrediente (nomeIngrediente text, codigo text, peso text, diasVal text);"
    };

    // Classe utilitária para abrir, criar, e atualizar o banco de dados
    private SQLiteHelper dbHelper;
    // Ponteiro para o Banco de Dados
    protected SQLiteDatabase db;

    protected Context context;

    // Construtor do Repositorio
    public Repositorio(Context ctx) {

        context = ctx;

        // Criar utilizando um script SQL
        dbHelper = new SQLiteHelper(ctx, Repositorio.NOME_BANCO,
                Repositorio.VERSAO_BANCO, Repositorio.SCRIPT_DATABASE_CREATE,
                Repositorio.SCRIPT_DATABASE_DELETE);

        // abre o banco no modo escrita para poder alterar também
        db = dbHelper.getWritableDatabase();
    }

    protected Repositorio() {
    }

    // /-----------------------------------------------------------------------------//
    // SALVAR //
    // -----------------------------------------------------------------------------//
    public long salvarControleEstoque_Origem(objetoControleEstoque objeto) {
        long _id = objeto._id;
        if (_id != 0) {
            atualizarControleEstoque_Origem(objeto);
        } else {
            _id = inserirControleEstoque_Origem(objeto);
        }
        return _id;
    }

    public long salvarControleEstoque_CodSafe(objetoControleEstoqueCodSafe objeto) {
        long _id = objeto._id;
        if (_id != 0) {
            atualizarControleEstoque_CodSafe(objeto);
        } else {
            _id = inserirControleEstoque_CodSafe(objeto);
        }
        return _id;
    }

    public long salvarFracionamento(objetoFracionamento objeto) {
        long _id = objeto._id;
        if (_id != 0) {
            atualizarFracionamento(objeto);
        } else {
            _id = inserirFracionamento(objeto);
        }
        return _id;
    }


    // -----------------------------------------------------------------------------//
    // INSERIR 1 //
    // -----------------------------------------------------------------------------//
    public long inserirControleEstoque_Origem(objetoControleEstoque objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoControleEstoques.CODIGO, objeto.codigo);
        values.put(objetoControleEstoques.FABRICANTE, objeto.fabricante);
        values.put(objetoControleEstoques.SIF, objeto.sif);
        values.put(objetoControleEstoques.DATAFAB, objeto.dataFab);
        values.put(objetoControleEstoques.DATAVAL, objeto.dataVal);
        values.put(objetoControleEstoques.QTD, objeto.qtd);
        values.put(objetoControleEstoques.CAIXA, objeto.caixa);
        values.put(objetoControleEstoques.LOTE, objeto.lote);
        values.put(objetoControleEstoques.USUARIO, objeto.usuarioNomeCpf);

        long _id = inserirControleEstoque_Origem(values);
        return _id;
    }

    public long inserirUsuario(Usuario objeto) {
        ContentValues values = new ContentValues();
        values.put("cpf", objeto.getCpf());
        values.put("nome", objeto.getNome());
        values.put("senha", objeto.getSenha());
        long _id = inserirUsuario(values);
        return _id;
    }

    public long inserirControleEstoque_CodSafe(objetoControleEstoqueCodSafe objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoControleEstoqueCodSafes.CODIGOSAFE, objeto.codigoSafe);
        values.put(objetoControleEstoqueCodSafes.TIPO, objeto.tipo);

        long _id = inserirControleEstoque_CodSafe(values);
        return _id;
    }

    public long inserirFracionamento(objetoFracionamento objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoFracionamentos.SELOSAFE, objeto.seloSafe);
        values.put(objetoFracionamentos.NOVOSELO, objeto.novoSelo);
        values.put(objetoFracionamentos.DATALEITURA, objeto.dataLeitura);
        values.put(objetoFracionamentos.FLAG, objeto.flag);
        values.put(objetoFracionamentos.TIPO_PRODUTO, objeto.tipoDoProduto);
        values.put(objetoFracionamentos.DATA_VALIDADE, objeto.dataDeValidade);
        values.put(objetoFracionamentos.SIF, objeto.sif);
        values.put(objetoFracionamentos.FABRICANTE, objeto.fabricante);
        values.put(objetoFracionamentos.LOTE, objeto.lote);
        values.put(objetoFracionamentos.DATA_FABRICACAO, objeto.dataFabricacao);

        long _id = inserirFracionamento(values);
        return _id;
    }

    public long inserirTipo(String tipo) {
        ContentValues values = new ContentValues();
        values.put("TIPO", tipo);

        long _id = inserirTipo(values);
        return _id;
    }


    public long inserirIdxBaixado(String selo, String tipo, String fabricante, String sif, String lote, String dataFab, String dataVal) {
        ContentValues values = new ContentValues();
        values.put("SELO", selo);
        values.put("TIPO", tipo);
        values.put("DATA", new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis())));
        values.put("FABRICANTE", fabricante);
        values.put("SIF", sif);
        values.put("LOTE", lote);
        values.put("DATA_FABRICACAO", dataFab);
        values.put("DATA_VALIDADE", dataVal);

        long _id = inserirIdxBaixado(values);
        return _id;
    }

    public long inserirNovaReceita(objetoReceita objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoReceitas.RECEITA, objeto.receita);
        values.put(objetoReceitas.INGREDIENTE, objeto.ingrediente);
        values.put(objetoReceitas.LOCAL, objeto.local);
        values.put(objetoReceitas.INTERMEDIARIA, objeto.intermediaria);

        long _id = inserirNovaReceita(values);
        return _id;
    }

    public long inserirReceitaHoje(String receita, String codigo, String dataHoje, String dataFab, String dataVal) {
        ContentValues values = new ContentValues();
        values.put("RECEITA", receita);
        values.put("CODIGO", codigo);
        values.put("DATAHOJE", dataHoje);
        values.put("DATAFAB", dataFab);
        values.put("DATAVAL", dataVal);

        long _id = inserirReceitaHoje(values);
        return _id;
    }

    public long inserirPaoDeLo(ObjetoIntermediario objeto) {
        ContentValues values = new ContentValues();
        values.put(ObjetoIntermediarios.CODIGO, objeto.codigo);
        values.put(ObjetoIntermediarios.DATAFAB, objeto.dataFab);
        values.put(ObjetoIntermediarios.DATAVAL, objeto.dataVal);

        long _id = inserirPaoDeLo(values);
        return _id;
    }

    public void createTableRotulo(){
        // Criar utilizando um script SQL
        SQLiteHelper dbHelper1 = new SQLiteHelper(context, "stmarket.db",
                13, Repositorio.SCRIPT_DATABASE_CREATE,
                Repositorio.SCRIPT_DATABASE_DELETE);

        // abre o banco no modo escrita para poder alterar também
        SQLiteDatabase db1 = dbHelper1.getWritableDatabase();

        String query = "create table rotulo(\n" +
                "    _id int primary key,\n" +
                "    image_uri text,\n" +
                "    model_produto_recebido int\n" +
                ")";

        try{
            db1.execSQL(query);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public long inserirReceita(String nomeReceita) {
        ContentValues values = new ContentValues();
        values.put("NOMERECEITA", nomeReceita);

        long _id = inserirReceita(values);
        return _id;
    }

    public long inserirIngrediente(objetoIngrediente objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoIngredientes.NOMEINGREDIENTE, objeto.nomeIngrediente);
        values.put(objetoIngredientes.CODIGO, objeto.codigo);
        values.put(objetoIngredientes.PESO, objeto.peso);
        values.put(objetoIngredientes.DIASVAL, objeto.diasVal);

        long _id = inserirIngrediente(values);
        return _id;
    }


    // -----------------------------------------------------------------------------//
    // INSERIR 2 //
    // -----------------------------------------------------------------------------//
    public long inserirControleEstoque_Origem(ContentValues valores) {
        long _id = db.insert(NOME_TABELA_1, "", valores);
        return _id;
    }

    public long inserirControleEstoque_CodSafe(ContentValues valores) {
        long _id = db.insert(NOME_TABELA_2, "", valores);
        return _id;
    }

    public long inserirFracionamento(ContentValues valores) {
        long _id = db.insert(NOME_TABELA_3, "", valores);
        return _id;
    }

    public long inserirTipo(ContentValues values) {
        long _id = db.insert(NOME_TABELA_4, "", values);
        return _id;
    }

    public long inserirIdxBaixado(ContentValues values) {
        long _id = db.insert(NOME_TABELA_5, "", values);
        return _id;
    }

    public long inserirNovaReceita(ContentValues values) {
        long _id = db.insert(NOME_TABELA_6, "", values);
        return _id;
    }

    public long inserirReceitaHoje(ContentValues values) {
        long _id = db.insert(NOME_TABELA_7, "", values);
        return _id;
    }

    public long inserirPaoDeLo(ContentValues values) {
        long _id = db.insert(NOME_TABELA_8, "", values);
        return _id;
    }

    public long inserirReceita(ContentValues values) {
        long _id = db.insert(NOME_TABELA_9, "", values);
        return _id;
    }

    public long inserirIngrediente(ContentValues values) {
        long _id = db.insert(NOME_TABELA_10, "", values);
        return _id;
    }

    public long inserirUsuario(ContentValues values) {
        long _id = db.insert(NOME_TABELA_11, "", values);
        return _id;
    }


    // -----------------------------------------------------------------------------//
    // ATUALIZA 1 //
    // -----------------------------------------------------------------------------//
    public long atualizarControleEstoque_Origem(objetoControleEstoque objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoControleEstoques._ID, objeto._id);
        values.put(objetoControleEstoques.CODIGO, objeto.codigo);
        values.put(objetoControleEstoques.FABRICANTE, objeto.fabricante);
        values.put(objetoControleEstoques.SIF, objeto.sif);
        values.put(objetoControleEstoques.DATAFAB, objeto.dataFab);
        values.put(objetoControleEstoques.DATAVAL, objeto.dataVal);
        values.put(objetoControleEstoques.QTD, objeto.qtd);
        values.put(objetoControleEstoques.CAIXA, objeto.caixa);
        values.put(objetoControleEstoques.LOTE, objeto.lote);
        values.put(objetoControleEstoques.USUARIO, objeto.usuarioNomeCpf);

        String _id = String.valueOf(objeto._id);

        String where = objetoControleEstoques._ID + "=?";
        String[] whereArgs = new String[]{_id};

        int count = atualizarControleEstoque_Origem(values, where, whereArgs);

        return count;
    }

    public long atualizarControleEstoque_CodSafe(objetoControleEstoqueCodSafe objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoControleEstoqueCodSafes._ID, objeto._id);
        values.put(objetoControleEstoqueCodSafes.CODIGOSAFE, objeto.codigoSafe);
        values.put(objetoControleEstoqueCodSafes.TIPO, objeto.tipo);

        String _id = String.valueOf(objeto._id);

        String where = objetoControleEstoqueCodSafes._ID + "=?";
        String[] whereArgs = new String[]{_id};

        int count = atualizarControleEstoque_CodSafe(values, where, whereArgs);

        return count;
    }

    public long atualizarFracionamento(objetoFracionamento objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoFracionamentos._ID, objeto._id);
        values.put(objetoFracionamentos.SELOSAFE, objeto.seloSafe);
        values.put(objetoFracionamentos.NOVOSELO, objeto.novoSelo);
        values.put(objetoFracionamentos.DATALEITURA, objeto.dataLeitura);
        values.put(objetoFracionamentos.FLAG, objeto.flag);
        values.put(objetoFracionamentos.TIPO_PRODUTO, objeto.tipoDoProduto);
        values.put(objetoFracionamentos.DATA_FABRICACAO, objeto.dataFabricacao);
        values.put(objetoFracionamentos.LOTE, objeto.lote);
        values.put(objetoFracionamentos.FABRICANTE, objeto.fabricante);
        values.put(objetoFracionamentos.DATA_VALIDADE, objeto.dataDeValidade);
        values.put(objetoFracionamentos.SIF, objeto.sif);

        String _id = String.valueOf(objeto._id);

        String where = objetoFracionamentos._ID + "=?";
        String[] whereArgs = new String[]{_id};

        int count = atualizarFracionamento(values, where, whereArgs);

        return count;
    }

    public long atualizarIngrediente(objetoIngrediente objeto) {
        ContentValues values = new ContentValues();
        values.put(objetoIngredientes.NOMEINGREDIENTE, objeto.nomeIngrediente);
        values.put(objetoIngredientes.CODIGO, objeto.codigo);
        values.put(objetoIngredientes.PESO, objeto.peso);
        values.put(objetoIngredientes.DIASVAL, objeto.diasVal);

        String _id = String.valueOf(objeto.nomeIngrediente);

        String where = objetoIngredientes.NOMEINGREDIENTE + "=?";
        String[] whereArgs = new String[]{_id};

        int count = atualizarIngrediente(values, where, whereArgs);

        return count;
    }

    // -----------------------------------------------------------------------------//
    // ATUALIZA 2 //
    // -----------------------------------------------------------------------------//
    public int atualizarControleEstoque_Origem(ContentValues valores, String where,
                                               String[] whereArgs) {
        int count = db.update(NOME_TABELA_1, valores, where, whereArgs);
        Log.i(CATEGORIA, "Atualizou [" + count + "] registros");
        return count;
    }

    public int atualizarControleEstoque_CodSafe(ContentValues valores, String where,
                                                String[] whereArgs) {
        int count = db.update(NOME_TABELA_2, valores, where, whereArgs);
        Log.i(CATEGORIA, "Atualizou [" + count + "] registros");
        return count;
    }

    public int atualizarFracionamento(ContentValues valores, String where,
                                      String[] whereArgs) {
        int count = db.update(NOME_TABELA_3, valores, where, whereArgs);
        Log.i(CATEGORIA, "Atualizou [" + count + "] registros");
        return count;
    }

    public int atualizarIngrediente(ContentValues valores, String where,
                                    String[] whereArgs) {
        int count = db.update(NOME_TABELA_10, valores, where, whereArgs);
        Log.i(CATEGORIA, "Atualizou [" + count + "] registros");
        return count;
    }


    // -----------------------------------------------------------------------------//
    // BUSCA //
    // -----------------------------------------------------------------------------//
    public objetoControleEstoque buscarControleEstoque_Origem(long _id) {
        // select * from objeto where _id=?
        Cursor c = db.query(true, NOME_TABELA_1, objetoControleEstoque.colunas, objetoControleEstoques._ID
                + "=" + _id, null, null, null, null, null);

        if (c.getCount() > 0) {
            // Posiciona no primeiro elemento do cursor
            c.moveToFirst();
            objetoControleEstoque objeto = new objetoControleEstoque(context);
            // Ler os dados
            objeto._id = c.getLong(0);
            objeto.codigo = c.getString(1);
            objeto.fabricante = c.getString(2);
            objeto.sif = c.getString(3);
            objeto.dataFab = c.getString(4);
            objeto.dataVal = c.getString(5);
            objeto.qtd = c.getString(6);
            objeto.caixa = c.getString(7);
            objeto.lote = c.getString(8);
            objeto.usuarioNomeCpf = c.getString(9);
            c.close();
            return objeto;
        }
        c.close();
        return null;
    }

    public ArrayList<objetoControleEstoque> buscarControleEstoque_Origem_Historico(int quantidade) {
        // select * from objeto where _id=?
        Cursor c = db.query(NOME_TABELA_1, null, null, null, null, null, (objetoControleEstoques._ID + " DESC"), Integer.toString(quantidade));
        ArrayList<objetoControleEstoque> arrayObjetos = new ArrayList<objetoControleEstoque>();

        if (c.getCount() > 0) {

            // Posiciona no primeiro elemento do cursor
            c.moveToFirst();
            objetoControleEstoque objeto = new objetoControleEstoque(context);

            while (!c.isAfterLast()) {
                // Ler os dados
                objeto._id = c.getLong(0);
                objeto.codigo = c.getString(1);
                objeto.fabricante = c.getString(2);
                objeto.sif = c.getString(3);
                objeto.dataFab = c.getString(4);
                objeto.dataVal = c.getString(5);
                objeto.qtd = c.getString(6);
                objeto.caixa = c.getString(7);
                objeto.lote = c.getString(8);
                objeto.usuarioNomeCpf = c.getString(9);

                arrayObjetos.add(objeto);

                c.moveToNext();
            }

            c.close();
            return arrayObjetos;

        }

        c.close();
        return null;
    }


    public Usuario buscarUsuario(String texto){
			/*Cursor c = db.query(true, NOME_TABELA_11, Usuario.colunas, "cpf"
					+ " LIKE " + texto, null, null, null, null, null);*/
			Cursor c  = db.rawQuery("SELECT * FROM Usuario where cpf = '" + texto + "'", null);
			c.moveToFirst();
			Usuario busca = new Usuario(c.getString(1), c.getString(0), c.getString(2));
			c.close();
			return busca;
		}
		
		public Usuario buscarUsuarioNome(String texto){
			/*Cursor c = db.query(true, NOME_TABELA_11, Usuario.colunas, "cpf"
					+ " LIKE " + texto, null, null, null, null, null);*/
			Cursor c  = db.rawQuery("SELECT * FROM Usuario where nome = '"+texto+"'", null);
			c.moveToFirst();
			Usuario busca = new Usuario(c.getString(1), c.getString(0), c.getString(2));
			c.close();
			return busca;
		}
		
		public ArrayList<Usuario> buscarTodosUsuarios(){
			ArrayList<Usuario> lista = new ArrayList<Usuario>();
			Usuario user;
			Cursor c  = db.rawQuery("SELECT * FROM Usuario", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				user = new Usuario(c.getString(1), c.getString(0), c.getString(2));
				lista.add(user);
				c.moveToNext();
			}
			return lista;
			
		}
		
		public objetoControleEstoqueCodSafe buscarControleEstoque_CodSafe(long _id) {
			// select * from objeto where _id=?
			Cursor c = db.query(true, NOME_TABELA_2, objetoControleEstoqueCodSafe.colunas, objetoControleEstoqueCodSafes._ID
                    + "=" + _id, null, null, null, null, null);

			if (c.getCount() > 0) {
				// Posicinoa no primeiro elemento do cursor
				c.moveToFirst();
				objetoControleEstoqueCodSafe objeto = new objetoControleEstoqueCodSafe(context);
				// Ler os dados
				objeto._id = c.getLong(0);
				objeto.codigoSafe = c.getString(1);
				objeto.tipo  = c.getString(2);
				
				c.close();
				return objeto;
			}
			c.close();
			return null;
		}
		
		public objetoFracionamento buscarFracionamento(String selo) {
			// select * from objeto where _id=?
			Cursor c = db.query(true, NOME_TABELA_3, objetoFracionamento.colunas, objetoFracionamentos.SELOSAFE
					+ "= '" + selo+ "'", null, null, null, null, null);
			
			if (c.getCount() > 0) {
				// Posicinoa no primeiro elemento do cursor
				c.moveToFirst();
				objetoFracionamento objeto = new objetoFracionamento(context);
				// Ler os dados
				objeto._id = c.getLong(0);
				objeto.seloSafe = c.getString(1);
				objeto.novoSelo  = c.getString(2);
				objeto.dataLeitura  = c.getString(3);
				objeto.flag  = c.getInt(4);
                objeto.usuarioCpf = c.getString(5);
                objeto.tipoDoProduto = c.getString(6);
                objeto.dataDeValidade = c.getString(7);
                objeto.sif = c.getString(8);
                objeto.fabricante = c.getString(9);
                objeto.dataFabricacao = c.getString(10);
                objeto.lote = c.getString(11);
				
				c.close();
				return objeto;
			}
			c.close();
			return null;
		}
		
		public ObjetoIntermediario buscarObjetoIntermediario(String codigo) {
			// select * from objeto where _id=?
			Cursor c = db.rawQuery("SELECT codigo, datafab, dataval FROM receitaDia where codigo = '" + codigo + "'", null);
			
			if (c.getCount() > 0) {
				// Posicinoa no primeiro elemento do cursor
				c.moveToFirst();
				ObjetoIntermediario objeto = new ObjetoIntermediario();
				// Ler os dados
				objeto.codigo = c.getString(0);
				objeto.dataFab  = c.getString(1);
				objeto.dataVal  = c.getString(2);
				
				c.close();
				return objeto;
			}
			c.close();
			return null;
		}
		
		public void deletaReceitas(){
			db.delete(NOME_TABELA_6, null, null);
			db.delete(NOME_TABELA_9, null, null);
			//db.rawQuery("delete from Ingrediente", null);
			//db.rawQuery("delete from tipoProduto", null);
		}
		
		public objetoIngrediente buscarIngrediente(String nome, String codigo) {

			Cursor c = db.query(true, NOME_TABELA_10, objetoIngrediente.colunas, objetoIngredientes.NOMEINGREDIENTE
					+ "= '" + nome+ "' AND " + objetoIngredientes.CODIGO + "= '" + codigo+ "'", null, null, null, null, null);
			
			if (c.getCount() > 0) {
				// Posicinoa no primeiro elemento do cursor
				c.moveToFirst();
				objetoIngrediente objeto = new objetoIngrediente();
				// Ler os dados
				objeto.nomeIngrediente = c.getString(0);
				objeto.codigo  = c.getString(1);
				objeto.peso  = c.getString(2);
				objeto.diasVal  = c.getString(3);
				
				c.close();
				return objeto;
			}
			c.close();
			return null;
		}


		// -----------------------------------------------------------------------------//
		// LISTAS //
		// -----------------------------------------------------------------------------//
		public List<objetoControleEstoque> listarControleEstoque_Origems() {
			List<objetoControleEstoque> objetos = new ArrayList<objetoControleEstoque>();
			try {

				Cursor c = db.query(NOME_TABELA_1, objetoControleEstoque.colunas, null, null,null, null, null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
						objetoControleEstoque objeto = new objetoControleEstoque(context);
						objetos.add(objeto);

						// recupera os atributos de objeto
						objeto._id = c.getLong(0);
						objeto.codigo = c.getString(1);
						objeto.fabricante = c.getString(2);
						objeto.sif = c.getString(3);
						objeto.dataFab = c.getString(4);
						objeto.dataVal = c.getString(5);
						objeto.qtd = c.getString(6);
						objeto.qtd = c.getString(7);
						objeto.lote = c.getString(8);
						objeto.usuarioNomeCpf = c.getString(9);

					} while (c.moveToNext());

				}
				//c.close();

			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objeto ObjetoControleEstoque: " + e.toString());

				return null;
			}
			return objetos;
		}
		
		public List<objetoControleEstoqueCodSafe> listarControleEstoque_CodSafes() {
			List<objetoControleEstoqueCodSafe> objetos = new ArrayList<objetoControleEstoqueCodSafe>();
			try {
				// Idem a: SELECT _id,atributo1,atributo2,atributo3 from OBJETO
				// where nome = ?
				Cursor c = db.query(NOME_TABELA_2, objetoControleEstoqueCodSafe.colunas, null, null,
						null, null, null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
						objetoControleEstoqueCodSafe objeto = new objetoControleEstoqueCodSafe(context);
						objetos.add(objeto);

						// recupera os atributos de objeto
						objeto._id = c.getLong(0);
						objeto.codigoSafe = c.getString(1);
						objeto.tipo  = c.getString(2);


					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objeto ObjetoControleEstoqueCodSafe: " + e.toString());

				return null;
			}

			return objetos;
		}
		
		public List<objetoFracionamento> listarFracionamentos(String diaAtual) {
			List<objetoFracionamento> objetos = new ArrayList<objetoFracionamento>();
			try {
				// Idem a: SELECT _id,atributo1,atributo2,atributo3 from OBJETO
				// where nome = ?
//				Cursor c = db.query(NOME_TABELA_3, ObjetoFracionamento.colunas, null, null,
//						null, null, null);

            Cursor c = db.query(true, NOME_TABELA_3, objetoFracionamento.colunas, objetoFracionamentos.DATALEITURA
                    + "= '" + diaAtual + "'", null, null, null, null, null);


            if (c.moveToFirst()) {

                // Loop até o final
                do {
                    objetoFracionamento objeto = new objetoFracionamento(context);
                    objetos.add(objeto);

                    // recupera os atributos de objeto
                    objeto._id = c.getLong(0);
                    objeto.seloSafe = c.getString(1);
                    objeto.novoSelo = c.getString(2);
                    objeto.dataLeitura = c.getString(3);
                    objeto.flag = c.getInt(4);
                    objeto.usuarioCpf = c.getString(5);
                    objeto.tipoDoProduto = c.getString(6);

                    if (c.isNull(7)) {
                        objeto.dataDeValidade = "";
                        //    Log.i("is", "null");
                    } else {
                        objeto.dataDeValidade = c.getString(7);
                        //    Log.i("is", "not null");
                    }

                    if (c.isNull(8)) {
                        objeto.sif = "";
                        //	Log.i("is", "null");
                    } else {
                        objeto.sif = c.getString(8);
                        //	Log.i("is", "not null");
                    }

                    if (c.isNull(9)) {
                        objeto.fabricante = "";
                        //	Log.i("is", "null");
                    } else {
                        objeto.fabricante = c.getString(9);
                        //	Log.i("is", "not null");
                    }

                    if (c.isNull(10)) {
                        objeto.dataFabricacao = "";
                        //    Log.i("is", "null");
                    } else {
                        objeto.dataFabricacao = c.getString(10);
                        //    Log.i("is", "not null");
                    }

                    if (c.isNull(11)) {
                        objeto.lote = "";
                        //    Log.i("is", "null");
                    } else {
                        objeto.lote = c.getString(11);
                        //    Log.i("is", "not null");
                    }

                    Log.i("objeto in repo", objeto.toString());


					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objeto ObjetoFracionamento: " + e.toString());
            return null;
        }

        return objetos;
    }


    public ArrayList<String> listarTipos() {
        ArrayList<String> lista = new ArrayList<String>();
        try {

            Cursor c = db.query(true, NOME_TABELA_4, null, null, null, null, null, null, null);


            if (c.moveToFirst()) {
                String tipo = "";
                // Loop até o final
                do {
                    tipo = c.getString(0);
                    lista.add(tipo);

                } while (c.moveToNext());
            }
            c.close();
        } catch (SQLException e) {
            Log.e(CATEGORIA, "Erro ao buscar o tipo: " + e.toString());

            return null;
        }

        return lista;
    }

    public String listarIdxBaixado(String selo) {
        String retorno = "";
        try {

            Cursor c = db.query(true, NOME_TABELA_5, null, "selo" + "= '" + selo + "'", null, null, null, null, null);


            if (c.moveToLast()) {

                do {
                    retorno = c.getString(1) + c.getString(3);
//						objetos.add(tipo);

                } while (c.moveToNext());
            }
            c.close();
        } catch (SQLException e) {
            Log.e(CATEGORIA, "Erro ao buscar o tipo: " + e.toString());

            return null;
        }

        return retorno;
    }

    public String listarIdxBaixadoComValidade(String selo) {
        String retorno = "";
        try {

            Cursor c = db.query(true, NOME_TABELA_5, null, "selo" + "= '" + selo + "'", null, null, null, null, null);


            if (c.moveToLast()) {

                do {

                    try {
                        String dataLeituraS = c.getString(3);
                        java.util.Date dataLeituraD = new SimpleDateFormat("dd/MM/yyyy").parse(dataLeituraS);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dataLeituraD);
                        calendar.add(Calendar.DATE, 3);
                        retorno = c.getString(1) + " - " + new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
                    } catch (Exception e){
                        try {
                            retorno = c.getString(1);
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
//						objetos.add(tipo);

                } while (c.moveToNext());
            }
            c.close();
        } catch (SQLException e) {
            Log.e(CATEGORIA, "Erro ao buscar o tipo: " + e.toString());

            return null;
        }

        return retorno;
    }

    public String[] listarIdxBaixadoHistorico(String selo) {
        String[] retorno = new String[]{"", "", "", "", "", ""};
        try {

            Cursor c = db.query(true, NOME_TABELA_5, null, "selo = '" + selo + "'", null, null, null, null, null);


            if (c.moveToFirst()) {

                do {
                    retorno[0] = c.getString(1); // nome
                    retorno[1] = c.getString(3); // fab
                    retorno[2] = c.getString(4); // sif
                    retorno[3] = c.getString(5); // lote
                    retorno[4] = c.getString(6); // datafab
                    retorno[5] = c.getString(7); // dataval
//						objetos.add(tipo);

                    Log.i("retorno", retorno[2]);

                } while (c.moveToNext());
            }
            c.close();
        } catch (SQLException e) {
            Log.e(CATEGORIA, "Erro ao buscar o tipo: " + e.toString());

            return null;
        }

        return retorno;
    }

		public ArrayList<String> listarIngredientes(String receita) {
			ArrayList<String> ingredientes = new ArrayList<String>();
			try {
				//TODO
				Cursor c = db.query(true, NOME_TABELA_6,objetoReceita.colunas, objetoReceitas.RECEITA
						+ "= '" + receita + "'", null, null, null, null, null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
							ingredientes.add(c.getString(1));							
				
					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objeto ingredientes da receita: " + e.toString());
				return null;
			}

			return ingredientes;
		}

		public ArrayList<String> listarReceitas(String local) {
			ArrayList<String> receitas = new ArrayList<String>();
			try {

				Cursor c = db.query(true, NOME_TABELA_6,objetoReceita.colunas, objetoReceitas.LOCAL
				+"= '" + local+"'", null, null, null, objetoReceitas.RECEITA, null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
						if(!receitas.contains(c.getString(0)))
							receitas.add(c.getString(0));

					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objeto receitas: " + e.toString());
				return null;
			}

			return receitas;
		}


		public ArrayList<String> listarReceitasStrings() {
			ArrayList<String> receitas = new ArrayList<String>();
			try {

				Cursor c = db.rawQuery("select * from novaReceita", null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
						if(!receitas.contains(c.getString(0)))
							receitas.add(c.getString(0)); 
				
					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objeto receitas: " + e.toString());
				return null;
			}

			return receitas;
		}
		public ArrayList<objetoReceita> listarTodasReceitas(String local){
			ArrayList<objetoReceita> lista = new ArrayList<objetoReceita>();
			try {
				
				Cursor c = db.rawQuery("select * from novaReceita where local = '" +local+"' order by receita", null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
						objetoReceita objeto = new objetoReceita();

						// recupera os atributos de objeto
						objeto.receita= c.getString(0);
						objeto.ingrediente = c.getString(1);
						objeto.local  = c.getString(2);
						objeto.intermediaria  = c.getString(3);
						lista.add(objeto);
					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objeto ObjetoFracionamento: " + e.toString());

				return null;
			}
			
			return lista;
		}
		
		public objetoReceita buscaReceita(String nomeReceita){
			objetoReceita receita = new objetoReceita();
			try {
				
				Cursor c = db.rawQuery("select * from novaReceita where receita = '" +nomeReceita+"'", null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
						// recupera os atributos de objeto
						receita.receita= c.getString(0);
						receita.ingrediente = c.getString(1);
						receita.local  = c.getString(2);
						receita.intermediaria  = c.getString(3);
					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objeto ObjetoFracionamento: " + e.toString());

				return null;
			}
			
			return receita;
		}

		public ArrayList<String> listarReceitaHoje(String dataHoje) {
			ArrayList<String> receitas = new ArrayList<String>();
			String aux = "";
			try {
				
				Cursor c = db.query(true, NOME_TABELA_7, null, "DATAHOJE"
						+ "= '" + dataHoje + "'", null, null, null, null, null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
						aux = c.getString(1) + "  -  " + c.getString(0)  ;
						receitas.add(aux);
				
					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o receitas hoje: " + e.toString());
				return null;
			}

			return receitas;
		}
		
		public ArrayList<String> listarReceitasNovo() {
			ArrayList<String> receitas = new ArrayList<String>();
			try {
				
				Cursor c = db.query(true, NOME_TABELA_9, null, null, null, null, null, null, null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
							receitas.add(c.getString(0)); 
				
					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar receitas: " + e.toString());
				return null;
			}

			return receitas;
		}
		
		public List<objetoIngrediente> listarIngredientes() {
			List<objetoIngrediente> objetos = new ArrayList<objetoIngrediente>();
			try {

				Cursor c = db.query(true, NOME_TABELA_10, null, null, null, null, null, null, null);

				if (c.moveToFirst()) {

					// Loop até o final
					do {
						objetoIngrediente objeto = new objetoIngrediente();
						// recupera os atributos de objeto
						objeto.nomeIngrediente = c.getString(0);
						objeto.codigo = c.getString(1);
						objeto.peso  = c.getString(2);
						objeto.diasVal  = c.getString(3);
						objetos.add(objeto);

					} while (c.moveToNext());
				}
				c.close();
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao buscar o objetos ingrediente: " + e.toString());

				return null;
			}

			return objetos;
		}

		
		// -----------------------------------------------------------------------------//
		// DELETE //
		// -----------------------------------------------------------------------------//
		
		public Boolean deleteIngredienteReceita(String ingrediente, String receita) {
			try {
				
				if(!receita.equals("")){
					db.delete(NOME_TABELA_6, objetoReceitas.RECEITA + "=? AND " 
							+ objetoReceitas.INGREDIENTE + "=?", new String[] {receita, ingrediente});					
				}
				else{
					db.delete(NOME_TABELA_6, objetoReceitas.INGREDIENTE + "=?", new String[] {ingrediente});										
				}
				
						
			} catch (SQLException e) {
				Log.e("BANCO", "Erro ao deletar ingrediente da receita: " + e.toString());
				return false;
			}
			
			Log.i("BANCO", "Apagou");
			return true;
		}
		
		public Boolean deletaReceita(String receita) {
			try {
				Log.i("BANCO", receita);
				
				db.delete(NOME_TABELA_6, objetoReceitas.RECEITA + "=? ", new String[]{receita});
				db.delete(NOME_TABELA_9, "NOMERECEITA =? ", new String[] {receita});
						
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao deletar receita: " + e.toString());
				return false;
			}

			return true;
		}
		
		public Boolean deletaIngrediente(String ingrediente) {
			try {
				Log.i("BANCO", ingrediente);
				
				db.delete(NOME_TABELA_10, "NOMEINGREDIENTE =?", new String[] {ingrediente});
						
			} catch (SQLException e) {
				Log.e(CATEGORIA, "Erro ao deletar o ingrediente: " + e.toString());
				return false;
			}

			return true;
		}


		

		// -----------------------------------------------------------------------------//
		// FINALIZANDO //
		// -----------------------------------------------------------------------------//
		public void apagarControleEstoque_Origem() {
			db.execSQL("DROP TABLE IF EXISTS controleEstoque_Origem;");
			db.execSQL("create table controleEstoque_Origem ( _id integer primary key autoincrement, codigo text, fabricante text, sif text, dataFab text, dataVal text, qtd text, caixa text, lote text, usuario text);");
		}
		
		public void apagarControleEstoque_CodSafe() {
			db.execSQL("DROP TABLE IF EXISTS controleEstoque_CodSafe;");
			db.execSQL("create table controleEstoque_CodSafe (_id integer primary key autoincrement, codigoSafe text, tipo text);");
		}
		
		public void apagarFracionamento() {
			db.execSQL("DROP TABLE IF EXISTS fracionamento;");
			db.execSQL("create table fracionamento (_id integer primary key autoincrement, seloSafe text, novoSelo text, dataLeitura text, flag integer);");
		}

		
		
		public Cursor query(SQLiteQueryBuilder queryBuilder, String[] projection,
				String selection, String[] selectionArgs, String groupBy,
				String having, String orderBy) {
			Cursor c = queryBuilder.query(this.db, projection, selection,
					selectionArgs, groupBy, having, orderBy);

			return c;
		}
		
		
		public void fechar() {
			// fecha o banco de dados
			if (db != null) {
				db.close();
			}
			if (dbHelper != null) {
				dbHelper.close();
			}
		}

    public ModelIndexBaixado encontrarModelIndexBaixado(String selo) {
        ModelIndexBaixado modelIndexBaixado = new ModelIndexBaixado();

        try {

            Cursor c = db.query(true, NOME_TABELA_5, null, "selo" + "= '" + selo + "'", null, null, null, null, null);

            if (c.moveToLast()) {

                do {
                    modelIndexBaixado.selo = c.getString(0);
                    modelIndexBaixado.tipo = c.getString(1);
                    modelIndexBaixado.fabricante = c.getString(3);
                    modelIndexBaixado.sif = c.getString(4);
                    modelIndexBaixado.lote = c.getString(5);
                    modelIndexBaixado.dataFabricacao = c.getString(6);
                    modelIndexBaixado.dataValidade = c.getString(7);

                } while (c.moveToNext());
            }
            c.close();
        } catch (SQLException e) {
            Log.e(CATEGORIA, "Erro ao buscar o tipo: " + e.toString());

            return null;
        }

        return modelIndexBaixado;
    }

    /**************************
     *
     * Metodos referentes ao Centro de Distribuição
     *
     *************************/

    /**
     * **********************
     * <p/>
     * EtiquetasControleDeEstoqueCentroDeDistribuição
     * <p/>
     * ***********************
     */

    public long createEtiquetaEstoqueCentroDeDistribuicao(ModelEtiquetaEstoqueCentroDeDistribuicao etiqueta) {
        return db.insert("EtiquetaEstoqueCentroDeDistribuicao", "null", etiqueta.toValues());

    }

    public ModelEtiquetaEstoqueCentroDeDistribuicao recoverEtiquetaEstoqueCentroDeDistribuicao(int _id) {
        Cursor cursor = db.query("EtiquetaEstoqueCentroDeDistribuicao", null, "_id = " + _id, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ModelEtiquetaEstoqueCentroDeDistribuicao etiqueta = new ModelEtiquetaEstoqueCentroDeDistribuicao(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2)
            );
            return etiqueta;
        }
        return null;
    }

    public ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> recoverListOfEtiquetas(int idProdutoRecebido) {
        Cursor cursor = db.query("EtiquetaEstoqueCentroDeDistribuicao", null, "codigoProdutoRecebido = " + idProdutoRecebido, null, null, null, null, null);
        ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao> etiquetaEstoqueCentroDeDistribuicaos = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            etiquetaEstoqueCentroDeDistribuicaos = new ArrayList<ModelEtiquetaEstoqueCentroDeDistribuicao>();
            ModelEtiquetaEstoqueCentroDeDistribuicao etiqueta = new ModelEtiquetaEstoqueCentroDeDistribuicao(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2)
            );
            etiquetaEstoqueCentroDeDistribuicaos.add(etiqueta);
            cursor.moveToNext();
        }
        return etiquetaEstoqueCentroDeDistribuicaos;
    }

    /**
     * **********************
     * <p/>
     * ProdutosRecebidos
     * <p/>
     * ***********************
     */


    public long createProdutoRecebido(ModelProdutoRecebido produtoRecebido) {
        return db.insert("ProdutoRecebido", "null", produtoRecebido.toValues());

    }

    public long updateProdutoRecebido(ModelProdutoRecebido produtoRecebido){
        return db.update("ProdutoRecebido", produtoRecebido.toValues(), "_id = " + produtoRecebido.get_id(), null);
    }

    public ModelProdutoRecebido recoverProdutoRecebido(int _id) {
        Cursor cursor = db.query("ProdutoRecebido", null, "_id = " + _id, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ModelProdutoRecebido modelProdutoRecebido = new ModelProdutoRecebido(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getInt(13),
                    cursor.getInt(14),
                    cursor.getInt(15),
                    cursor.getInt(16),
                    cursor.getInt(17),
                    cursor.getFloat(18),
                    cursor.getInt(19),
                    cursor.getFloat(20),
                    cursor.getInt(21),
                    cursor.getInt(22),
                    cursor.getInt(23),
                    cursor.getInt(24),
                    cursor.getFloat(25),
                    cursor.getString(26),
                    cursor.getString(27),
                    cursor.getString(28),
                    cursor.getString(29),
                    recoverListOfEtiquetas(cursor.getInt(0)),
                    cursor.getInt(30),
                    cursor.getString(31),
                    cursor.getInt(32) == 0 ? false : true,
                    cursor.getString(33),
                    cursor.getString(34),
                    cursor.getString(35)
            );

            return modelProdutoRecebido;
        }
        return null;

    }

    /**
     * **********************
     * <p/>
     * Recepcao
     * <p/>
     * ***********************
     */

    public long createRecepcao(ModelRecepcao modelRecepcao) {
        long result = db.insert("Recepcao", null, modelRecepcao.toValues());

        return result;
    }

    public ModelRecepcao recoverRecepcao(int _id) {
        Cursor cursor = db.query("Recepcao", null, "_id = " + _id, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ModelRecepcao modelRecepcao = new ModelRecepcao(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    getListOfProdutosRecebidos(_id)
            );

            return modelRecepcao;
        }
        return null;

    }

    public ArrayList<ModelRecepcao> recoverAllRecepcao(String date) {
        Cursor cursor = db.query("Recepcao", null, "dataDaRecepcao = '" + date + "'", null, null, null, null, null);
        ArrayList<ModelRecepcao> modelRecepcaoArrayList = new ArrayList<ModelRecepcao>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ModelRecepcao modelRecepcao = new ModelRecepcao(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        getListOfProdutosRecebidos(cursor.getInt(0))
                );
                modelRecepcaoArrayList.add(modelRecepcao);
            }
            return modelRecepcaoArrayList;
        }
        return null;
    }

    public ArrayList<ModelProdutoRecebido> getListOfProdutosRecebidos(int recepcaoId) {
        ArrayList<ModelProdutoRecebido> produtosRecebidos = new ArrayList<ModelProdutoRecebido>();

        Cursor cursor = db.query("ProdutoRecebido", null, "idRecepcao = " + recepcaoId, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ModelProdutoRecebido modelProdutoRecebido = new ModelProdutoRecebido(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getInt(13),
                        cursor.getInt(14),
                        cursor.getInt(15),
                        cursor.getInt(16),
                        cursor.getInt(17),
                        cursor.getFloat(18),
                        cursor.getInt(19),
                        cursor.getFloat(20),
                        cursor.getInt(21),
                        cursor.getInt(22),
                        cursor.getInt(23),
                        cursor.getInt(24),
                        cursor.getFloat(25),
                        cursor.getString(26),
                        cursor.getString(27),
                        cursor.getString(28),
                        cursor.getString(29),
                        recoverListOfEtiquetas(cursor.getInt(0)),
                        cursor.getInt(30),
                        cursor.getString(31),
                        cursor.getInt(32) == 0 ? false : true,
                        cursor.getString(33),
                        cursor.getString(34),
                        cursor.getString(35)
                );

                produtosRecebidos.add(modelProdutoRecebido);

            }

        }

        return produtosRecebidos;
    }

    /**
     * **************************
     * <p/>
     * Produção Diária
     * <p/>
     * ***************************
     */

    public long createProducaoDiaria(String receita, String quantidade, String data, String hora, String loja) {
        ContentValues values = new ContentValues();
        values.put("receita", receita);
        values.put("quantidade", quantidade);
        values.put("data", data);
        values.put("hora", hora);
        values.put("loja", loja);
        return db.insert("ProducaoDiaria", null, values);
    }

    public void deletarTodosUsuarios() {
        db.delete("Usuario", null, null);
    }
}