package com.rastreabilidadeInterna.BD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class SQLiteHelper extends SQLiteOpenHelper {

	private static final String CATEGORIA = "categoria";

	private String[] scriptSQLCreate;
	private String scriptSQLDelete;

	SQLiteHelper(Context context, String nomeBanco, int versaoBanco,
			String[] scriptSQLCreate, String scriptSQLDelete) {
		super(context, nomeBanco, null, versaoBanco);
		this.scriptSQLCreate = scriptSQLCreate;
		this.scriptSQLDelete = scriptSQLDelete;
	}

	// Criar novo banco...
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(CATEGORIA, "Criando banco com sql");
		int qtdeScripts = scriptSQLCreate.length;

		// Executa cada sql passado como parâmetro
		for (int i = 0; i < qtdeScripts; i++) {
			String sql = scriptSQLCreate[i];
			Log.i(CATEGORIA, sql);
			// Cria o banco de dados executando o script de criação
			db.execSQL(sql);
		}

        updateV3(db);
        updateV4(db);
        updateV5(db);
        updateV6(db);
        updateV7(db);
        updateV8(db);
	}

	// Mudou a versão...
	@Override
	public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int novaVersao) {
        updateV3(db);
        updateV4(db);
        updateV5(db);
        updateV6(db);
        updateV7(db);
        updateV8(db);
	}

    private void updateV8(SQLiteDatabase db){
        String query = "create table rotulo(\n" +
                "    _id int primary key,\n" +
                "    image_uri text,\n" +
                "    model_produto_recebido int\n" +
                ")";

        try{
            db.execSQL(query);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateV7(SQLiteDatabase db){
        String query1 = "ALTER TABLE ProdutoRecebido ADD COLUMN rotulado INTEGER";
        String query2 = "ALTER TABLE ProdutoRecebido ADD COLUMN descongelamento TEXT";
        String query3 = "ALTER TABLE ProdutoRecebido ADD COLUMN codigoCDSTM TEXT";
        String query4 = "ALTER TABLE ProdutoRecebido ADD COLUMN fatiamento TEXT";


        try {
            db.execSQL(query1);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query2);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query3);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query4);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateV6(SQLiteDatabase db){
        String query1 = "ALTER TABLE fracionamento ADD COLUMN sif TEXT";
        String query2 = "ALTER TABLE fracionamento ADD COLUMN fabricante TEXT";
        String query3 = "ALTER TABLE fracionamento ADD COLUMN data_fabricacao TEXT";
        String query4 = "ALTER TABLE fracionamento ADD COLUMN lote TEXT";

        try {
            db.execSQL(query1);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query2);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query3);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query4);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateV5(SQLiteDatabase db){

        String query1 = "CREATE TABLE [ProducaoDiaria] (" +
                "_id INTEGER PRIMARY KEY," +
                "[receita] TEXT," +
                "[quantidade] TEXT," +
                "[data] TEXT," +
                "[hora] TEXT," +
                "[loja] TEXT" +
                ")";

        try {
            db.execSQL(query1);
        } catch (Exception e){
            e.printStackTrace();
        }

        String query2 = "alter table fracionamento add column data_validade text";

        try {
            db.execSQL(query2);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateV4(SQLiteDatabase db){
        String query1 = "" +
                "CREATE TABLE [Recepcao] (_id INTEGER PRIMARY KEY,\n" +
                "  [numeroDaRecepcao] TEXT, \n" +
                "  [placaDoCaminhao] TEXT, \n" +
                "  [dataDaRecepcao] TEXT);\n" +
                "\n";

        String query2 = "CREATE TABLE [ProdutoRecebido] (_id INTEGER PRIMARY KEY,\n" +
                "  idRecepcao INTEGER," +
                "  [codigoBarrasCaixa] TEXT, \n" +
                "  [codigoBarrasProduto] TEXT, \n" +
                "  [nome] TEXT, \n" +
                "  [marca] TEXT, \n" +
                "  [fornecedor] TEXT, \n" +
                "  [setor] TEXT, \n" +
                "  [sif] TEXT, \n" +
                "  [ph] TEXT, \n" +
                "  [temperatura] TEXT, \n" +
                "  [dataFabricacao] TEXT, \n" +
                "  [dataValidade] TEXT, \n" +
                "  [totalPalets] INTEGER, \n" +
                "  [totalCaixas] INTEGER, \n" +
                "  [totalPedido] INTEGER, \n" +
                "  [totalCaixasAmostradas] INTEGER, \n" +
                "  [totalPecasRegular] INTEGER, \n" +
                "  [totalPorcentagemRegular] REAL, \n" +
                "  [totalPecasIrregular] INTEGER, \n" +
                "  [totalPorcentagemIrregular] REAL, \n" +
                "  [totalRecebido] INTEGER, \n" +
                "  [totalDevolvido] INTEGER, \n" +
                "  [totalPecasAmostradas] INTEGER, \n" +
                "  [totalPecasIrregulares] INTEGER, \n" +
                "  [totalPesoIrregular] REAL, \n" +
                "  [naoConformidade] TEXT, \n" +
                "  [motivoDevolucao] TEXT, \n" +
                "  [observacoes] TEXT, \n" +
                "  [conclusao] TEXT);";

        String query8 = "alter table ProdutoRecebido add column peso text";

        //String query3 = "DROP TABLE IF EXISTS Recepcao";
        //String query4 = "DROP TABLE IF EXISTS ProdutoRecebido";
        //String query6 = "DROP TABLE IF EXISTS EtiquetaEstoqueCentroDeDistribuicao";

        String query5 = "CREATE TABLE EtiquetaEstoqueCentroDeDistribuicao (" +
                "_id INTEGER PRIMARY KEY, " +
                "codigoEtiqueta TEXT," +
                "codigoProdutoRecebido INTEGER" +
                ")";

        String query7 = "ALTER TABLE ProdutoRecebido ADD pecasPorCaixa integer";

        try {
            //db.execSQL(query6);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            //db.execSQL(query3);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            //db.execSQL(query4);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query1);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query2);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query5);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query7);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query8);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateV3(SQLiteDatabase db){
        String query1 = "ALTER TABLE idxBaixado ADD fabricante text";
        String query2 = "ALTER TABLE idxBaixado ADD sif text";
        String query3 = "ALTER TABLE idxBaixado ADD lote text";
        String query4 = "ALTER TABLE idxBaixado ADD data_fabricacao text";
        String query5 = "ALTER TABLE idxBaixado ADD data_validade text";
        String query6 = "ALTER TABLE fracionamento ADD tipo_produto text";

        try {
            db.execSQL(query1);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query2);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query3);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query4);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query5);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            db.execSQL(query6);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
