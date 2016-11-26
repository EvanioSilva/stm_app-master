package com.rastreabilidadeInterna.geral.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.geral.ActivityLoginDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TelaInicialLoja#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TelaInicialLoja extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Button buttonEstoque;
    public Button buttonFracionamento;
    public Button buttonPreparacao;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TelaInicialLoja.
     */
    // TODO: Rename and change types and number of parameters
    public static TelaInicialLoja newInstance(String param1, String param2) {
        TelaInicialLoja fragment = new TelaInicialLoja();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TelaInicialLoja() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tela_inicial_loja, container, false);

        buttonEstoque = (Button) view.findViewById(R.id.btnCtlEstoque);
        buttonFracionamento = (Button) view.findViewById(R.id.btnFracion);
        buttonPreparacao = (Button) view.findViewById(R.id.btnPrepar);

        buttonEstoque.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i=new Intent(getActivity().getBaseContext(), ActivityLoginDialog.class);
                i.putExtra("Botao", "Controle");
                startActivity(i);
                //telaLogin();
            }
        });

        buttonFracionamento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i=new Intent(getActivity().getBaseContext(), ActivityLoginDialog.class);
                i.putExtra("Botao", "Fracionamento");
                startActivity(i);
            }
        });

        buttonPreparacao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i=new Intent(getActivity().getBaseContext(), ActivityLoginDialog.class);
                i.putExtra("Botao", "Preparacao");
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
