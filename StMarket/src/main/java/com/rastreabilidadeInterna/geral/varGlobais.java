package com.rastreabilidadeInterna.geral;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;

public class varGlobais extends Activity{

	private EditText edtNumTablet;
	private EditText edtNumCliente;
	private EditText edtNumLoja;
	//private EditText edtIDLoja;
	private Button btnSalvar;
	private Button btnDelete;
	private ListView lvList;
	private ImageButton ibAdd;
	private EditText edtTipo;
	private ImageButton ibAddReceita;
	private TextView txAdd;
	
	public static final String PREFS_NAME = "Preferences";
	
	ArrayList<String> list = new ArrayList<String>();
	
	Repositorio repositorio;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.varglobais);
		
		repositorio = new Repositorio(this);

		defineComponents();
		LoadScreen();
		defineAction();

	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	private void defineComponents(){
		edtNumTablet = (EditText) findViewById(R.id.vglob_numTablet);
		edtNumCliente = (EditText) findViewById(R.id.vglob_numCliente);
		edtNumLoja = (EditText) findViewById(R.id.vglob_numLoja);
		//edtIDLoja = (EditText) findViewById(R.id.vglob_IDLoja);
		btnSalvar = (Button) findViewById(R.id.vglob_btnSalvar);
        btnDelete = (Button) findViewById(R.id.vglob_btnDelete);
		lvList = (ListView) findViewById(R.id.listviewTipo);
		ibAdd = (ImageButton) findViewById(R.id.ibAdd);
		edtTipo = (EditText) findViewById(R.id.vglob_tipo);
		ibAddReceita = (ImageButton) findViewById(R.id.ibAddReceita);
		txAdd = (TextView) findViewById(R.id.tv_add);

	}

	private void defineAction(){
		
		if(txAdd.getText().toString().equals("Add produto:")){
			ibAddReceita.setEnabled(false);
		}

		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				deleteAllUsers();
			}
		});
		
		btnSalvar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				salvar();
			}
		});
		
		ibAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddTipo();
			}
		});

		ibAddReceita.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent it = null;
				it = new Intent(getApplicationContext(), ActivityAdicionarReceita.class);
				startActivity(it);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

			}
		});


	}

    private void deleteAllUsers(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Todos usuários serão deletados. Continuar?");
        builder.setTitle("Confirmar Exclusão");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Repositorio repositorio = new Repositorio(varGlobais.this);
                repositorio.deletarTodosUsuarios();
                Toast.makeText(varGlobais.this, "Todos os usuários foram deletados", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

	private void salvar(){

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		if(edtNumTablet.getText().toString().length()!=2){
			Toast.makeText(getApplicationContext(), "O número do TABLET precisa ter 2 dígitos", Toast.LENGTH_LONG).show();
		}
		if(edtNumCliente.getText().toString().length()!=2){
			Toast.makeText(getApplicationContext(), "O número do CLIENTE precisa ter 2 dígitos", Toast.LENGTH_LONG).show();
		}
		if(edtNumLoja.getText().toString().length()!=3){
			Toast.makeText(getApplicationContext(), "O número da LOJA precisa ter 3 dígitos", Toast.LENGTH_LONG).show();
		}
		else{
			editor.putString("NUMTABLET", edtNumTablet.getText().toString());
			editor.putString("NUMCLIENTE", edtNumCliente.getText().toString());
			editor.putString("NUMLOJA", edtNumLoja.getText().toString());
			editor.putString("IDLOJA", edtNumLoja.getText().toString());
		
			editor.commit();
			
			Toast.makeText(getApplicationContext(), "Variáveis globais salvas com sucesso.", Toast.LENGTH_LONG).show();			
			edtNumLoja.setFocusable(false);
		}

	}
	
	private void updateListView(){

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), 
				android.R.layout.simple_list_item_1, list) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTextColor(Color.BLACK);
				return view;
			}
		};

		lvList.setAdapter(adapter);
	}
	
	private void AddTipo(){
		if(edtTipo.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira o produto", Toast.LENGTH_LONG).show();
		}
		else if(list.contains(edtTipo.getText().toString())){
			Toast.makeText(getApplicationContext(), "Produto já existe", Toast.LENGTH_LONG).show();			
		}
		else{
			repositorio.inserirTipo(edtTipo.getText().toString());
			list.add(edtTipo.getText().toString());
			updateListView();
			edtTipo.setText("");
		}
	}
	
	public void LoadScreen(){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		edtNumTablet.setText(settings.getString("NUMTABLET", ""));
		edtNumCliente.setText(settings.getString("NUMCLIENTE", ""));
		edtNumLoja.setText(settings.getString("NUMLOJA", ""));
		//edtIDLoja.setText(settings.getString("IDLOJA", ""));
		
		list = repositorio.listarTipos();
		updateListView();
	}

}
