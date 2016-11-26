package com.rastreabilidadeInterna.geral;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;

public class ActivityConfiguracaoDeArea extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_configuracao_de_area);

        definirElementos();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_configuracao_de_area, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void definirElementos(){
        definirSpinner();
        definirButton();
    }

    private void definirButton(){
        Button button = (Button) findViewById(R.id.buttonSalvarAreaDeUso);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarAreaDeUso();
            }
        });
    }

    private void salvarAreaDeUso(){
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerAreaDeUso);

        String[] arrayValues = getResources().getStringArray(R.array.areas_de_uso_values);

        editor.putString("AREADEUSO", arrayValues[spinner.getSelectedItemPosition()]);

        if(editor.commit()){
            Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_LONG).show();
        } else {
            salvarAreaDeUso();
        }

        finish();
    }

    private void definirSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.spinnerAreaDeUso);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.areas_de_uso, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
