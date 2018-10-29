package br.com.naveonline.folder.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.model.Contantes;
import br.com.naveonline.folder.model.Empresa;
import br.com.naveonline.folder.model.Funcionamento;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

import static br.com.naveonline.folder.model.Funcionamento.setFuncionamento;

public class NewEmpresaFragment extends BaseFragment {
    private static final int GALERRY_REQUEST = 123;
    private DatabaseReference mDatabase;
    protected StorageReference mStorage;
    protected Uri mImageUri;
    protected ImageView mImageEmpresa;
    protected EditText mNomeEd;
    protected EditText mTelefoneEd;
    protected EditText mRuaEd;
    protected EditText mNumeroEd;
    protected EditText mBairroEd;
    protected EditText mCepEd;
    protected Switch mWhatsappSwitch;
    protected Spinner mCategoriaSpinner;
    protected List<Funcionamento> mFuncionamentos = null;
    protected String mCategoria;
    protected Switch mEntregaSwitch;
    protected Switch mPagamentoSwitch;
    protected Button btnDeletar;

    public NewEmpresaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_empresa, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_EMPRESA);
        mDatabase.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();

        mImageEmpresa = view.findViewById(R.id.iv_produto);

        if ((ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 124);
        } else {
            mImageEmpresa.setOnClickListener(onClickGaleria());
        }
        mNomeEd = view.findViewById(R.id.ed_nome);
        mTelefoneEd = view.findViewById(R.id.ed_telefone);
        mRuaEd = view.findViewById(R.id.ed_rua);
        mNumeroEd = view.findViewById(R.id.ed_numero);
        mBairroEd = view.findViewById(R.id.ed_bairro);
        mCepEd = view.findViewById(R.id.ed_cep);
        mWhatsappSwitch = view.findViewById(R.id.switch_whatsapp);
        mEntregaSwitch = view.findViewById(R.id.switch_entrega);
        mPagamentoSwitch = view.findViewById(R.id.switch_pagamento);
        mCategoriaSpinner = view.findViewById(R.id.spinner_categoria);
        mCategoriaSpinner.setOnItemSelectedListener(onCategoriaItemSelected());

        funcionamento(view);

        view.findViewById(R.id.btn_submit).setOnClickListener(onClickSubmit());
        btnDeletar = (Button) view.findViewById(R.id.btn_deletar);

        return view;
    }




    //@RequiresPermission(allOf = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    protected View.OnClickListener onClickGaleria() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, GALERRY_REQUEST);
            }
        };
    }

    private AdapterView.OnItemSelectedListener onCategoriaItemSelected() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoria = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private View.OnClickListener onClickSubmit() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEmpresa();
            }
        };
    }

    @UiThread
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


        showProgressDialog();

        StorageReference filePath = mStorage.child(Contantes.IMAGEM_EMPRESA).child(mImageUri.getLastPathSegment());

        filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Empresa e = new Empresa(null, nome, sobre, downloadUrl.toString(), telefone, whatsapp, entrega, pagamento,
                        rua, numero, bairro, cep, cidade, estado, latitude, longitude, pais,
                        categoria, mFuncionamentos, data_registro, user_uid);

                String key = mDatabase.push().getKey();
                Map<String, Object> empresaValues = e.toMap();
                HashMap<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(key, empresaValues);
                mDatabase.updateChildren(childUpdates);
                hideProgressDialog();
                getActivity().finish();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERRY_REQUEST && resultCode == Activity.RESULT_OK) {

            try {
                mImageUri = data.getData();
                File fileImage = FileUtil.from(getActivity(), data.getData());
                fileImage = Compressor.getDefault(getActivity()).compressToFile(fileImage);
                mImageUri = Uri.fromFile(fileImage);
                carregaImagem(mImageEmpresa, mImageUri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void funcionamento(View view) {
        mFuncionamentos = setFuncionamento();
        domingo(view);
        segunda(view);
        terca(view);
        quarta(view);
        quinta(view);
        sexta(view);
        sabado(view);
    }

    private void domingo(View view) {
        final Switch switch_aberto_domingo = (Switch) view.findViewById(R.id.switch_aberto_domingo);
        switch_aberto_domingo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_aberto_domingo.isChecked()) {
                    switch_aberto_domingo.setText("Aberto");
                    mFuncionamentos.get(0).aberto = true;
                } else {
                    switch_aberto_domingo.setText("Fechado");
                    mFuncionamentos.get(0).aberto = false;
                }
            }
        });
        Spinner spinner_abre_domingo = (Spinner) view.findViewById(R.id.spinner_abre_domingo);
        spinner_abre_domingo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(0).horaAbrir = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner spinner_fecha_domingo = (Spinner) view.findViewById(R.id.spinner_fecha_domingo);
        spinner_fecha_domingo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(0).horaFecha = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void segunda(View view) {
        final Switch switch_aberto_segunda = (Switch) view.findViewById(R.id.switch_aberto_segunda);
        switch_aberto_segunda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_aberto_segunda.isChecked()) {
                    switch_aberto_segunda.setText("Aberto");
                    mFuncionamentos.get(1).aberto = true;
                } else {
                    switch_aberto_segunda.setText("Fechado");
                    mFuncionamentos.get(1).aberto = false;
                }
            }
        });
        Spinner spinner_abre_segunda = (Spinner) view.findViewById(R.id.spinner_abre_segunda);
        spinner_abre_segunda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(1).horaAbrir = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner spinner_fecha_segunda = (Spinner) view.findViewById(R.id.spinner_fecha_segunda);
        spinner_fecha_segunda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(1).horaFecha = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void terca(View view) {
        final Switch switch_aberto_terca = (Switch) view.findViewById(R.id.switch_aberto_terca);
        switch_aberto_terca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_aberto_terca.isChecked()) {
                    switch_aberto_terca.setText("Aberto");
                    mFuncionamentos.get(2).aberto = true;
                } else {
                    switch_aberto_terca.setText("Fechado");
                    mFuncionamentos.get(2).aberto = false;
                }
            }
        });
        Spinner spinner_abre_terca = (Spinner) view.findViewById(R.id.spinner_abre_terca);
        spinner_abre_terca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(2).horaAbrir = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner spinner_fecha_terca = (Spinner) view.findViewById(R.id.spinner_fecha_terca);
        spinner_fecha_terca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(2).horaFecha = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void quarta(View view) {
        final Switch switch_aberto_quarta = view.findViewById(R.id.switch_aberto_quarta);
        switch_aberto_quarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_aberto_quarta.isChecked()) {
                    switch_aberto_quarta.setText("Aberto");
                    mFuncionamentos.get(3).aberto = true;
                } else {
                    switch_aberto_quarta.setText("Fechado");
                    mFuncionamentos.get(3).aberto = false;
                }
            }
        });
        Spinner spinner_abre_quarta = view.findViewById(R.id.spinner_abre_quarta);
        spinner_abre_quarta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(3).horaAbrir = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner spinner_fecha_quarta = view.findViewById(R.id.spinner_fecha_quarta);
        spinner_fecha_quarta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(3).horaFecha = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void quinta(View view) {
        final Switch switch_aberto_quinta = view.findViewById(R.id.switch_aberto_quinta);
        switch_aberto_quinta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_aberto_quinta.isChecked()) {
                    switch_aberto_quinta.setText("Aberto");
                    mFuncionamentos.get(4).aberto = true;
                } else {
                    switch_aberto_quinta.setText("Fechado");
                    mFuncionamentos.get(4).aberto = false;
                }
            }
        });
        Spinner spinner_abre_quinta = view.findViewById(R.id.spinner_abre_quinta);
        spinner_abre_quinta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(4).horaAbrir = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner spinner_fecha_quinta = view.findViewById(R.id.spinner_fecha_quinta);
        spinner_fecha_quinta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(4).horaFecha = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sexta(View view) {
        final Switch switch_aberto_sexta = view.findViewById(R.id.switch_aberto_sexta);
        switch_aberto_sexta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_aberto_sexta.isChecked()) {
                    switch_aberto_sexta.setText("Aberto");
                    mFuncionamentos.get(5).aberto = true;
                } else {
                    switch_aberto_sexta.setText("Fechado");
                    mFuncionamentos.get(5).aberto = false;
                }
            }
        });
        Spinner spinner_abre_sexta = view.findViewById(R.id.spinner_abre_sexta);
        spinner_abre_sexta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(5).horaAbrir = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner spinner_fecha_sexta = (Spinner) view.findViewById(R.id.spinner_fecha_sexta);
        spinner_fecha_sexta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(5).horaFecha = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sabado(View view) {
        final Switch switch_aberto_sabado = (Switch) view.findViewById(R.id.switch_aberto_sabado);
        switch_aberto_sabado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_aberto_sabado.isChecked()) {
                    switch_aberto_sabado.setText("Aberto");
                    mFuncionamentos.get(6).aberto = true;
                } else {
                    switch_aberto_sabado.setText("Fechado");
                    mFuncionamentos.get(6).aberto = false;
                }
            }
        });
        Spinner spinner_abre_sabado = (Spinner) view.findViewById(R.id.spinner_abre_sabado);
        spinner_abre_sabado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(6).horaAbrir = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner spinner_fecha_sabado = (Spinner) view.findViewById(R.id.spinner_fecha_sabado);
        spinner_fecha_sabado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFuncionamentos.get(6).horaFecha = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}
