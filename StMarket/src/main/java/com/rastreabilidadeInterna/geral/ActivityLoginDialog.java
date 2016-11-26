package com.rastreabilidadeInterna.geral;

import java.util.ArrayList;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.BD.Repositorio;
import com.rastreabilidadeInterna.centrodedistribuicao.ActivityControleDeEstoqueCD;
import com.rastreabilidadeInterna.centrodedistribuicao.ActivityControleDeEstoqueCDHortifruti;
import com.rastreabilidadeInterna.centrodedistribuicao.ActivitySelecionaRecebimentoFrios;
import com.rastreabilidadeInterna.centrodedistribuicao.CentroDeDistribuicao;
import com.rastreabilidadeInterna.controleEstoque.ActivityControleEstoque;
import com.rastreabilidadeInterna.fracionamento.ActivityFracionamento;
import com.rastreabilidadeInterna.preparacao.preparacaoPrincipal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ActivityLoginDialog extends Activity{
	private Repositorio repositorio;
	private ListView lstUsuarios;
	private ArrayList<String> listaNomes = new ArrayList<String>();
	private ArrayList<Usuario> listaUsuario = new ArrayList<Usuario>();
	private Spinner spnSpinner;
	private EditText edtSenha;
	private Button btnLogin;
	private Button btnCancel;
	private Usuario usuarioBuscado;
	private String strOrigem;

 	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_dialog);
		repositorio = new Repositorio(this);
		strOrigem = getIntent().getExtras().getString("Botao");
		defineComponente();
		preencheSpinner();
		defineAction();
	}
	
	private void defineComponente(){
		spnSpinner = (Spinner) findViewById(R.id.spnNomesUsuarios);
		edtSenha = (EditText) findViewById(R.id.edtSenha);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnCancel = (Button) findViewById(R.id.btnCancel);
	}
	
	private void defineAction(){
		spnSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		        usuarioBuscado = repositorio.buscarUsuarioNome(spnSpinner.getSelectedItem().toString());
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		btnLogin.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				if(usuarioBuscado.getSenha().equals(edtSenha.getText().toString())){
					if(strOrigem.equals("Controle")){
						Intent i=new Intent(getBaseContext(), ActivityControleEstoque.class);
						i.putExtra("Nome", usuarioBuscado.getNome());
						i.putExtra("cpf", usuarioBuscado.getCpf());
						startActivity(i);
						finish();
					}
					
					if(strOrigem.equals("Fracionamento")){
						Intent i=new Intent(getBaseContext(), ActivityFracionamento.class);
						i.putExtra("Nome", usuarioBuscado.getNome());
						i.putExtra("cpf", usuarioBuscado.getCpf());
						startActivity(i);
						finish();
					}
					
					if(strOrigem.equals("Preparacao")){
						Intent i=new Intent(getBaseContext(), preparacaoPrincipal.class);
						i.putExtra("Nome", usuarioBuscado.getNome());
						i.putExtra("cpf", usuarioBuscado.getCpf());
						startActivity(i);
						finish();
					}

					if(strOrigem.equals("CentroDeDistribuicao")){
						Intent i = new Intent(getBaseContext(), ActivitySelecionaRecebimentoFrios.class);
						//Intent i = new Intent(getBaseContext(), ActivityControleDeEstoqueCD.class);
						i.putExtra("Nome", usuarioBuscado.getNome());
						i.putExtra("cpf", usuarioBuscado.getCpf());
						startActivity(i);
						finish();
					}

					if(strOrigem.equals("CentroDeDistribuicaoHortifruti")){
						Intent i = new Intent(getBaseContext(), ActivityControleDeEstoqueCDHortifruti.class);
						i.putExtra("Nome", usuarioBuscado.getNome());
						i.putExtra("cpf", usuarioBuscado.getCpf());
						startActivity(i);
						finish();
					}
					
				}else{
					Toast.makeText(getApplicationContext(), "Senha Incorreta", Toast.LENGTH_LONG).show();
				}
			}
		});	
		
		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});
	}
	
	private void preencheSpinner(){
		listaUsuario = repositorio.buscarTodosUsuarios();
		int contaLista = 0;
		listaNomes.clear();
		while(listaUsuario.size() > contaLista){
			listaNomes.add(listaUsuario.get(contaLista).getNome());
			contaLista++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, listaNomes);
		spnSpinner.setAdapter(adapter);
	}
}
