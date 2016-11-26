package com.rastreabilidadeInterna.geral;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.geral.Fragment.TelaInicialCentroDeDistribuicao;
import com.rastreabilidadeInterna.geral.Fragment.TelaInicialLoja;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class ActivityTelaInicial extends Activity {

	private TextView versao;
	private TextView config;
    private Button buttonAlterarArea;
	private int flag=1;
    private TextView areaDoTablet;

	public static final String PREFS_NAME = "Preferences";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tela_inicial);

        checarPrefs();

        setFragment();

        defineComponente();
		defineAction();
		configIni();
		showConfigs();

        File local = new File(Environment.getExternalStorageDirectory() + File.separator + "Carrefour");
        if (!local.exists()) {
            local.mkdir();
        }


        // Action Bar Hack
		/*
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
        */

	}

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                OpenConfig();
                return true;
            case R.id.action_usuarios:
                Intent i=new Intent(getBaseContext(), ActivityCadastroDeUsuario.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_inicial, menu);
        return true;
    }
	
	@Override
	public void onResume(){
		super.onResume();
		defineAction();
        showConfigs();
        setFragment();
	}

    private void setFragment(){
        if (getSharedPreferences("Preferences", 0).getString("AREADEUSO", "").equals("Centro de distribuicao")){
            Fragment fragment = new TelaInicialCentroDeDistribuicao();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.telaInicialFragmentHolder, fragment);
            fragmentTransaction.commit();

        } else {
            Fragment fragment = new TelaInicialLoja();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.telaInicialFragmentHolder, fragment);
            fragmentTransaction.commit();

        }
    }


    private void checarPrefs(){
        /*SharedPreferences.Editor e = getSharedPreferences("Preferences", 0).edit();
        e.remove("AREADEUSO");
        e.commit();
        */

        if (getSharedPreferences("Preferences", 0).getString("AREADEUSO", "").equals("")){
            Intent intent = new Intent(getBaseContext(), ActivityConfiguracaoDeArea.class);
            startActivity(intent);
        }
    }
	
	private void showConfigs(){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		config.setText("C"+settings.getString("NUMCLIENTE", "")+"L"+settings.getString("NUMLOJA", "")+"T"+settings.getString("NUMTABLET", ""));

        String areaValue = getSharedPreferences("Preferences", 0).getString("AREADEUSO", "");
        String[] areaValues = getResources().getStringArray(R.array.areas_de_uso_values);
        int valuePosition = 0;
        for (int i = 0; i < areaValues.length; i++){
            if (areaValues[i].equals(areaValue)){
                valuePosition = i;
            }
        }
        String[] areaNames = getResources().getStringArray(R.array.areas_de_uso);
        areaDoTablet.setText(areaNames[valuePosition]);

    }
	
	private void configIni(){
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		if(!settings.contains("SERIAL")){
			editor.putInt("SERIAL", 1);
		}

		SimpleDateFormat dateNome = new SimpleDateFormat("yyyyMMdd");
		String data_pref = dateNome.format(new Date( System.currentTimeMillis()));
		
		if(!settings.contains("DATA")){
			editor.putString("DATA", data_pref);
		}
		if(!settings.contains("NUMTABLET")){
			editor.putString("NUMTABLET", "00");
		}
		if(!settings.contains("NUMCLIENTE")){
			editor.putString("NUMCLIENTE", "00");
		}
		if(!settings.contains("NUMLOJA")){
			editor.putString("NUMLOJA", "000");
		}
		editor.commit();
	}
	
	private void defineComponente(){
		config = (TextView) findViewById(R.id.abas_confg);
        buttonAlterarArea = (Button) findViewById(R.id.buttonAlterarArea);
        areaDoTablet = (TextView) findViewById(R.id.areaDoTablet);
	}
	
	private void defineAction(){

        buttonAlterarArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ActivityConfiguracaoDeArea.class);
                startActivity(intent);
            }
        });

        /*

		b1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				Intent i=new Intent(getBaseContext(), ActivityLoginDialog.class);
				i.putExtra("Botao", "Controle");
				startActivity(i);
				//telaLogin();
			}
		});

		b2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				Intent i=new Intent(getBaseContext(), ActivityLoginDialog.class);
				i.putExtra("Botao", "Fracionamento");
				startActivity(i);
			}
		});

		b3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				Intent i=new Intent(getBaseContext(), ActivityLoginDialog.class);
				i.putExtra("Botao", "Preparacao");
				startActivity(i);
			}
		});

        */

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
				if(flag == 1){
					if (input.getText().toString().equals("safeadm")) {

						Intent it = null;
						it = new Intent(getApplicationContext(), varGlobais.class);
						startActivity(it);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

					} else {
						Toast.makeText(getApplicationContext(), "Senha Da Safe Incorreta", Toast.LENGTH_LONG).show();
					}
				}
				else{
					if (input.getText().toString().equals("lojaadm")) {

						Intent it = null;
						it = new Intent(getApplicationContext(), receitas.class);
						startActivity(it);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

					} else {
						Toast.makeText(getApplicationContext(), "Senha Da Loja Incorreta", Toast.LENGTH_LONG).show();
					}
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
	
}
