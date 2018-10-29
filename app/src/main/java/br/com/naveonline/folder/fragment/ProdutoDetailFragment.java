package br.com.naveonline.folder.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.activity.ProdutoDetailActivity;
import br.com.naveonline.folder.model.Contantes;
import br.com.naveonline.folder.model.Funcionamento;
import br.com.naveonline.folder.model.Produto;
import br.com.naveonline.folder.view.RoundedImageView;

public class ProdutoDetailFragment extends BaseFragment {
    private DatabaseReference mProdutoDatabase;
    private TextView mDescView;
    private TextView mNomeEmpView;
    private TextView mRuaNumView;
    private TextView mBairEsCidView;
    private TextView mCepView;
    private TextView mFoneView;
    private TextView mEntregaView;
    private RoundedImageView mEmpresaImageView;
    private LinearLayout mDiaSemanaLl;
    private LinearLayout mHoraLl;
    private String telefone;
    private String telefoneLigar;
    private ImageView ivTelefone;
    private String endereco = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_produto_detail, container, false);


        String key = (String) getActivity().getIntent().getExtras().get("key");
        Log.i("Script", key);

        if (key == null) {
            throw new IllegalArgumentException("Chave(key) incorreta!");
        }
        mProdutoDatabase = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_PRODUTO).child(key);

        mDescView = (TextView) view.findViewById(R.id.tv_descricao_produto);
        mNomeEmpView = (TextView) view.findViewById(R.id.tv_nome_empresa);
        mRuaNumView = (TextView) view.findViewById(R.id.tv_rua_numero_empresa);
        mBairEsCidView = (TextView) view.findViewById(R.id.tv_bairro_estado_cidade_empresa);
        mCepView = (TextView) view.findViewById(R.id.tv_cep);
        mFoneView = (TextView) view.findViewById(R.id.tv_telefone_empresa);
        mEntregaView = (TextView) view.findViewById(R.id.tv_entrega);
        mEmpresaImageView = (RoundedImageView) view.findViewById(R.id.iv_empresa);
        ivTelefone = (ImageView) view.findViewById(R.id.iv_telefone);
        mDiaSemanaLl = (LinearLayout) view.findViewById(R.id.lln_dia_semana_empresa);
        mHoraLl = (LinearLayout) view.findViewById(R.id.lln_hora_empresa);
        view.findViewById(R.id.btn_ligar).setOnClickListener(onClickLigar());
        view.findViewById(R.id.btn_mapa).setOnClickListener(onClickAbrirMapa());
        view.findViewById(R.id.btn_deletar).setOnClickListener(onClickDelete());


        return view;
    }

    private View.OnClickListener onClickDelete() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProdutoDatabase.removeValue();
                getActivity().finish();
            }
        };
    }


    private View.OnClickListener onClickAbrirMapa() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //exemplo de endereço
                //"Bebelu - R. cel. Antonio Rodrigues, 341 - Fatima, Picos - PI, 64600-000"
                if (endereco != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://maps.google.co.in/maps?q=" + endereco));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        };
    }

    private View.OnClickListener onClickLigar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + telefoneLigar);
                Intent intentFone = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intentFone);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener protutoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Produto produto = dataSnapshot.getValue(Produto.class);
                String whatsapp = "";

                if (produto != null) {
                    // Endereço para o google maps
                    endereco = produto.empresa.nome + " - R." + produto.empresa.rua + "," + produto.empresa.numero + "-" + produto.empresa.bairro + ", " + produto.empresa.cidade + "-" + produto.empresa.estado + "," + produto.empresa.cep;

                    mDescView.setText(produto.descricao);
                    mNomeEmpView.setText(produto.empresa.nome);
                    mRuaNumView.setText(produto.empresa.rua + ", " + produto.empresa.numero);
                    mBairEsCidView.setText(produto.empresa.bairro + ", " + produto.empresa.estado + " - " + produto.empresa.cidade);
                    mCepView.setText(produto.empresa.cep);
                    telefoneLigar = produto.empresa.telefone;

                    if (produto.empresa.whatsapp) {
                        whatsapp = " (WhatsApp)";
                    }

                    telefone = produto.empresa.telefone + whatsapp;

                    mFoneView.setText(telefone);
                    carregaImagem(mEmpresaImageView, produto.empresa.foto);

                    Boolean entrega = produto.empresa.entrega;
                    if (entrega) {
                        mEntregaView.setText("Fazemos entrega");
                        mEntregaView.setTextColor(getActivity().getResources().getColor(android.R.color.holo_green_light));
                    } else {
                        mEntregaView.setText("Não fazemos entrega");
                        mEntregaView.setTextColor(getActivity().getResources().getColor(android.R.color.holo_red_dark));
                    }

                    List<Funcionamento> funcionamentos = produto.empresa.funcionamentos;
                    populaHorarrioFuncionamento(funcionamentos);

                    ProdutoDetailActivity produtoDetailActivity = (ProdutoDetailActivity) getActivity();
                    produtoDetailActivity.recuperaProduto(produto);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mProdutoDatabase.addValueEventListener(protutoListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDiaSemanaLl.removeAllViews();
        mHoraLl.removeAllViews();
    }

    private void populaHorarrioFuncionamento(List<Funcionamento> funcionamentos) {
        for (Funcionamento f : funcionamentos) {
            TextView tv_dia = new TextView(getContext());
            TextView tv_hora = new TextView(getContext());

            Boolean abertoFleg = f.aberto;
            if (!abertoFleg) {
                tv_hora.setText("Fechado");
                tv_hora.setTextColor(getActivity().getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tv_hora.setText(f.horaAbrir + " às " + f.horaFecha);
            }
            tv_dia.setText(f.dia);
            mDiaSemanaLl.addView(tv_dia);
            mHoraLl.addView(tv_hora);
        }
    }

}
