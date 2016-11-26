package com.rastreabilidadeInterna.geral;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityCadastroDeUsuario extends Activity {
	
	private EditText edtUsuarioNome;
	private EditText edtUsuarioCPF;
	private EditText edtUsuarioSenha;
	private Button btnSalvarUsuario;
	private Repositorio repositorio;
	private ListView lstUsuarios;
	private ArrayList<String> listaNomes = new ArrayList<String>();
	private ArrayList<Usuario> listaUsuario = new ArrayList<Usuario>();
 	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastrodeusuarios);
		
		defineComponents();
		repositorio = new Repositorio(this);
		defineAction();
		atualizaLista();
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Action Bar Hack
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception e) {
            // presumably, not relevant
        }

	}

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cadastrodeusuarios, menu);
        return true;
    }

	private void defineComponents(){
		edtUsuarioNome = (EditText) findViewById(R.id.edtUsuarioNome);
		edtUsuarioCPF = (EditText) findViewById(R.id.edtUsuarioCPF);
		edtUsuarioSenha = (EditText) findViewById(R.id.edtUsuarioSenha);
		btnSalvarUsuario= (Button) findViewById(R.id.btnSalvarUsuario);
		lstUsuarios = (ListView) findViewById(R.id.listusuarios);
	}
	
	private void defineAction(){
		btnSalvarUsuario.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				if(checarCampos()){
					salvarUsuario();
				}else{
					Toast.makeText(getApplicationContext(), "Todos os campos devem estar preenchidos", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
	
	private void salvarUsuario(){
		Usuario usuario = new Usuario(edtUsuarioNome.getText().toString(), edtUsuarioCPF.getText().toString(), edtUsuarioSenha.getText().toString());
		if(repositorio.inserirUsuario(usuario) != -1){
			Toast.makeText(getApplicationContext(), " Usuário Salvo com sucesso", Toast.LENGTH_LONG).show();
			atualizaLista();
		}else{
			//Toast.makeText(getApplicationContext(), "CPF já está cadastrado", Toast.LENGTH_LONG).show();
			Usuario test = (Usuario) repositorio.buscarUsuario(edtUsuarioCPF.getText().toString());
			Toast.makeText(getApplicationContext(), test.toString(), Toast.LENGTH_LONG).show();
		}
	}
	private boolean checarCampos(){
		if(edtUsuarioNome.getText().toString().equals("") || edtUsuarioCPF.getText().toString().equals("") || edtUsuarioSenha.getText().toString().equals("")){
			return false;
		}else{
			return true;
		}
	}
	
	private void atualizaLista(){
		listaUsuario = repositorio.buscarTodosUsuarios();
		int contaLista = 0;
		listaNomes.clear();
		while(listaUsuario.size() > contaLista){
			listaNomes.add(listaUsuario.get(contaLista).getNome() + " - " + listaUsuario.get(contaLista).getCpf());
			contaLista++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), 
				android.R.layout.simple_list_item_1, listaNomes) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTextColor(Color.BLACK);
				text.setTextSize(13);
				text.setGravity(Gravity.CENTER);
				return view;
			}
		};

		lstUsuarios.setAdapter(adapter);
	}
	
}
