package com.rastreabilidadeInterna.geral;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;

public class ActivityIngredientes extends Activity{

	private Button btnAdm;

	private Button btnNovoIng;
	private ListView lvList;

	private List<EditText> editTextList = new ArrayList<EditText>();
	
	Repositorio repositorio;
	
	private boolean bdAlteracao = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ingredientes);

		repositorio = new Repositorio(this);

		defineComponente();
		defineAction();
		loadScreen();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		// put your code here...
		loadScreen();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		// gerar arquivos
		loadScreen();
	}


	private void defineComponente(){
		btnAdm = (Button) findViewById(R.id.ing_btnAdm);
		btnNovoIng = (Button) findViewById(R.id.ing_novoIng);
		lvList = (ListView) findViewById(R.id.ing_listview);
	}

	private void defineAction(){

		btnAdm.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				OpenConfig();
			}
		});

		btnNovoIng.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				editaIngrediente(1, null);
			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		        String text = (((TextView) view).getText()).toString();		        
//		        Toast.makeText(getApplicationContext(), text, 0).show();
	        
				String nome = text.substring(0, text.indexOf(" - "));
				String aux = text.substring(text.indexOf(" - ")+3, text.length());
				String codigo = aux.substring(0, aux.indexOf(" - "));
				
//		        Toast.makeText(getApplicationContext(), nome + "|" + aux + "|" + codigo, 0).show();

				objetoIngrediente obj = repositorio.buscarIngrediente(nome, codigo);
				
				// abrir diálogo de editar ingrediente
				
				editaIngrediente(2, obj);
			}
		}); 

	}

	private void loadScreen(){
		
		ArrayList<String> ingredientes = new ArrayList<String>();
		List<objetoIngrediente> objIngredientes = repositorio.listarIngredientes();

		for(int i=0; i<objIngredientes.size();i++){
			String strIngrd = objIngredientes.get(i).nomeIngrediente + " - " + objIngredientes.get(i).codigo  + " - " + 
					objIngredientes.get(i).peso + " - " + objIngredientes.get(i).diasVal;
			ingredientes.add(strIngrd);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), 
				android.R.layout.simple_list_item_1, ingredientes) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTextColor(Color.BLACK);
				text.setTextSize(25);
				text.setGravity(Gravity.CENTER);
				return view;
			}
		};

		lvList.setAdapter(adapter);
	}



	private void OpenConfig() {

		final EditText input = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(input);
		builder.setTitle("Configurações");
		builder.setMessage("Digite a senha para configurar");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				if (input.getText().toString().equals("safeadm")) {

					Intent it = null;
					it = new Intent(getApplicationContext(), varGlobais.class);
					startActivity(it);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

				} else {
					Toast.makeText(getApplicationContext(), "Senha incorreta", Toast.LENGTH_LONG).show();
				}
			}
		});
		builder.setNegativeButton("Cancelar",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		AlertDialog alerta = builder.create();
		alerta.show();
	}
	
	private void editaIngrediente(int flag, final objetoIngrediente obj) {
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		ll.setGravity(Gravity.CENTER_VERTICAL);
		layoutParams.setMargins(0, 0, 0, 0);

		ll.addView(linearLinha("Nome ingrediente: ", "nome ingrediente", 0, 0));
		ll.addView(linearLinha("Codigo: ", "codigo", 1, 1));
		ll.addView(linearLinha("Peso: ", "peso", 2, 2));
		ll.addView(linearLinha("Dias de validade: ", "dias de validade", 1, 3));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(ll);
		
		if(flag == 1){
			builder.setTitle("Novo Ingrediente");
			builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					if(editTextList.get(0).getText().toString().equals("")){
						editTextList.clear();
						Toast.makeText(getApplicationContext(), "Insira o nome da receita", 0).show();
					}
					else{
						objetoIngrediente obj = new objetoIngrediente();
						obj.nomeIngrediente = editTextList.get(0).getText().toString();
						obj.codigo = editTextList.get(1).getText().toString();
						obj.peso = editTextList.get(2).getText().toString();
						obj.diasVal = editTextList.get(3).getText().toString();
						
						Log.i("INGRED", obj.nomeIngrediente + " " + obj.codigo + " "+ obj.peso + " "+ obj.diasVal);

						repositorio.inserirIngrediente(obj);
						bdAlteracao = true;
						loadScreen();
						editTextList.clear();
					}
				}
			});
			builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
		}
		
		else{
			builder.setTitle("Editar Ingrediente");
			
			editTextList.get(0).setText(obj.nomeIngrediente);
			Log.i("TAM", obj.nomeIngrediente.length() + "");

			editTextList.get(1).setText(obj.codigo);
			editTextList.get(2).setText(obj.peso);
			editTextList.get(3).setText(obj.diasVal);
			
			builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					if(editTextList.get(0).getText().toString().equals("")){
						editTextList.clear();
						Toast.makeText(getApplicationContext(), "Insira o nome da receita", 0).show();
					}
					else{
						obj.nomeIngrediente = editTextList.get(0).getText().toString();
						obj.codigo = editTextList.get(1).getText().toString();
						obj.peso = editTextList.get(2).getText().toString();
						obj.diasVal = editTextList.get(3).getText().toString();
						
						Log.i("INGRED", obj.nomeIngrediente + " " + obj.codigo + " "+ obj.peso + " "+ obj.diasVal);

						repositorio.atualizarIngrediente(obj);
						bdAlteracao = true;
						loadScreen();
						editTextList.clear();
						Toast.makeText(getApplicationContext(), "Editado com sucesso", 0).show();
					}
				}
			});
			builder.setNegativeButton("Deletar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					if(editTextList.get(0).getText().toString().equals("")){
						editTextList.clear();
						Toast.makeText(getApplicationContext(), "Insira o nome da receita", 0).show();
					}
					else{
						repositorio.deletaIngrediente(editTextList.get(0).getText().toString());
						repositorio.deleteIngredienteReceita(editTextList.get(0).getText().toString(), "");
						bdAlteracao = true;
						loadScreen();
						editTextList.clear();
						Toast.makeText(getApplicationContext(), "Ingrediente deletado", 0).show();
					}
				}
			});
		}
		
		final AlertDialog alerta = builder.create();
		
		alerta.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
					editTextList.clear();
//                	finish();
                    alerta.dismiss();
                }
                return true;
            }
        });

		
		alerta.show();
	}
	
	private LinearLayout linearLinha(String texto, String hint, int flag, int id){
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams layoutParams = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		ll.setGravity(Gravity.CENTER_VERTICAL);
		layoutParams.setMargins(0, 0, 0, 0);

		ll.addView(txView(texto));
		ll.addView(editTextGenerico(hint, id, flag));

		Log.i("layout", "Linha inteira criada criado");

		return ll;
	}

	private TextView txView(String texto){
		TextView tx = new TextView(this);
		tx.setWidth(300);
		tx.setText("\n"+texto+"\n");
//		tx.setGravity(Gravity.LEFT);
		tx.setTextSize(25);
		tx.setTextColor(getResources().getColor(R.color.white));
		Log.i("layout", "Textview criado");

//		listaIngredientes.add(texto);

		return tx;
	}

	private EditText editTextGenerico(String hint, int id, int flag) {
		EditText editText = new EditText(this);

		editText.setId(id);
		editText.setWidth(300);
		editText.setHint(hint);
		editText.setSingleLine();

		if(flag == 1){
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		else if(flag == 2){
			editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		else{
			editText.setInputType(InputType.TYPE_CLASS_TEXT);			
		}

		Log.i("layout", "EditText criado");
		editTextList.add(editText);

		return editText;
	}
	


	
}
