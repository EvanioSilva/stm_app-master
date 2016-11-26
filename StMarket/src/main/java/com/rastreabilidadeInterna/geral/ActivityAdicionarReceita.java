package com.rastreabilidadeInterna.geral;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;

public class ActivityAdicionarReceita extends Activity{
	
	private EditText edtReceita;
	private EditText edtIngrediente;
	private ListView lvList;
	private Button btnSalvar;
	private ImageButton imgAdd;
	
	final ArrayList<String> list = new ArrayList<String>();
	
	objetoReceita objeto;
	Repositorio repositorio;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receita);
		
		objeto = new objetoReceita();
		repositorio = new Repositorio(this);
		
		defineComponent();
		defineAction();
		
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}
	
	private void defineComponent(){
		edtReceita = (EditText) findViewById(R.id.rec_nome);
		edtIngrediente = (EditText) findViewById(R.id.rec_ingrediente);
		lvList = (ListView) findViewById(R.id.listviewIngrediente);
		btnSalvar = (Button) findViewById(R.id.rec_btnSalvar);
		imgAdd = (ImageButton) findViewById(R.id.ibAddIngrediente);
	}
	
	private void defineAction(){
		imgAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddIngrediente();
			}
		});
		
		btnSalvar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				salvar();
			}
		});
		
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// ListView Clicked item index
				int itemPosition = position;
				list.remove(itemPosition);
				updateListView();        

			}
		}); 


	}
	
	private void salvar(){
		if(edtReceita.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira o nome da receita", Toast.LENGTH_LONG).show();
		}
		else if(list.size()==0){
			Toast.makeText(getApplicationContext(), "Insira um ingrediente", Toast.LENGTH_LONG).show();
		}
		else{
			objeto.receita = edtReceita.getText().toString();
			for(int i=0; i<list.size(); i++){
				objeto.ingrediente = list.get(i);
				//salvar no banco o objeto
				repositorio.inserirNovaReceita(objeto);
			}
			edtReceita.setText("");
			list.clear();
			updateListView();
			Toast.makeText(getApplicationContext(), "Receita salva com sucesso", Toast.LENGTH_LONG).show();
		}
	}
	
	private void AddIngrediente(){
		if(edtIngrediente.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Insira o ingrediente", Toast.LENGTH_LONG).show();
		}
		else if(list.contains(edtIngrediente.getText().toString())){
			Toast.makeText(getApplicationContext(), "Ingrediente já inserido", Toast.LENGTH_LONG).show();			
		}
		else{
//			repositorio.inserirTipo(edtTipo.getText().toString());
			list.add(edtIngrediente.getText().toString());
			updateListView();
			edtIngrediente.setText("");
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


}
