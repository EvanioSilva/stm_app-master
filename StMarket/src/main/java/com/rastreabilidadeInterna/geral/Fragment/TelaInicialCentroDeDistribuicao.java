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

import com.example.rastreabilidadeinterna.R;
import com.rastreabilidadeInterna.geral.ActivityLoginDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TelaInicialCentroDeDistribuicao#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TelaInicialCentroDeDistribuicao extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Button buttonEstoque;
    public Button buttonHortifruti;
    public Button buttonFracionamento;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TelaInicialCentroDeDistribuicao.
     */
    // TODO: Rename and change types and number of parameters
    public static TelaInicialCentroDeDistribuicao newInstance(String param1, String param2) {
        TelaInicialCentroDeDistribuicao fragment = new TelaInicialCentroDeDistribuicao();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TelaInicialCentroDeDistribuicao() {
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
        View view = inflater.inflate(R.layout.fragment_tela_inicial_centro_de_distribuicao, container, false);

        buttonEstoque = (Button) view.findViewById(R.id.btnCtlEstoqueCD);
        buttonHortifruti = (Button) view.findViewById(R.id.btnCtlEstoqueCDHortifruti);
        buttonFracionamento = (Button) view.findViewById(R.id.btnFracion);

        buttonEstoque.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i=new Intent(getActivity().getBaseContext(), ActivityLoginDialog.class);
                i.putExtra("Botao", "CentroDeDistribuicao");
                startActivity(i);
                //telaLogin();
            }
        });

        buttonHortifruti.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i=new Intent(getActivity().getBaseContext(), ActivityLoginDialog.class);
                i.putExtra("Botao", "CentroDeDistribuicaoHortifruti");
                startActivity(i);
                //telaLogin();
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
