package br.com.naveonline.folder.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.model.Contantes;
import br.com.naveonline.folder.model.Empresa;
import br.com.naveonline.folder.model.Funcionamento;

/**
 * Created by gilmar on 12/03/18.
 */

public class EditEmpresaFragment extends NewEmpresaFragment {
    private DatabaseReference mDatabase;
    private Empresa empresa;
    private String key;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getActivity().getIntent().getStringExtra("key");
        if (key == null) {
            throw new IllegalArgumentException("Chave(key) incorreta!");
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_EMPRESA).child(key);
    }

    @Override
    public void onStart() {
        super.onStart();
        btnDeletar.setVisibility(View.VISIBLE);
        btnDeletar.setOnClickListener(onClickDeletar());

        //mImageEmpresa.setVisibility(View.GONE);
        //mPagamentoSwitch.setVisibility(View.VISIBLE);

        // Carrega empresa
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresa = dataSnapshot.getValue(Empresa.class);

                if (empresa != null) {
                    mNomeEd.setText(empresa.nome);
                    mTelefoneEd.setText(empresa.telefone);
                    mRuaEd.setText(empresa.rua);
                    mNumeroEd.setText(empresa.numero);
                    mBairroEd.setText(empresa.bairro);
                    mCepEd.setText(empresa.cep);
                    mWhatsappSwitch.setChecked(empresa.whatsapp);
                    mEntregaSwitch.setChecked(empresa.entrega);
                    mPagamentoSwitch.setChecked(empresa.pagamento);
                    //carregaImagem(mImageEmpresa, empresa.foto);

                    for (Funcionamento e : empresa.funcionamentos) {

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase.addValueEventListener(listener);
    }

    public View.OnClickListener onClickDeletar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.removeValue();
                getActivity().finish();
            }
        };
    }

    @Override
    public void submitEmpresa() {
        final String nome = mNomeEd.getText().toString();
        final String telefone = mTelefoneEd.getText().toString();
        final Boolean whatsapp = mWhatsappSwitch.isChecked();
        final String rua = mRuaEd.getText().toString();
        final String numero = mNumeroEd.getText().toString();
        final String bairro = mBairroEd.getText().toString();
        final String cep = mCepEd.getText().toString();
        final String sobre = null;
        final Boolean entrega = mEntregaSwitch.isChecked();
        final Boolean pagamento = mPagamentoSwitch.isChecked();
        final String cidade = "Picos";
        final String estado = "PI";
        final Integer latitude = 0;
        final Integer longitude = 0;
        final String pais = "Brasil"; // País
        final String categoria = mCategoria;
        final String data_registro = String.valueOf(new Date());
        final String user_uid = "user_id";


           /* Empresa e = new Empresa(null, nome, sobre, empresa.foto, telefone, whatsapp, entrega, pagamento,
                    rua, numero, bairro, cep, cidade, estado, latitude, longitude, pais,
                    categoria, mFuncionamentos, data_registro, user_uid);
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_EMPRESA);
            Map<String, Object> empresaValues = e.toMap();
            HashMap<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, empresaValues);
            database.updateChildren(childUpdates);
            getActivity().finish();*/

            // Troca logo
            showProgressDialog();

            StorageReference filePath = mStorage.child(Contantes.IMAGEM_EMPRESA).child(mImageUri.getLastPathSegment());

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Empresa e = new Empresa(null, nome, sobre, downloadUrl.toString(), telefone, whatsapp, entrega, pagamento,
                            rua, numero, bairro, cep, cidade, estado, latitude, longitude, pais,
                            categoria, mFuncionamentos, data_registro, user_uid);


                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_EMPRESA);
                    Map<String, Object> empresaValues = e.toMap();
                    HashMap<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(key, empresaValues);
                    database.updateChildren(childUpdates);
                    hideProgressDialog();
                    getActivity().finish();
                }
            });
            // fim troca logo

    }
}
