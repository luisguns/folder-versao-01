package br.com.naveonline.folder.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.model.Contantes;
import br.com.naveonline.folder.model.Empresa;
import br.com.naveonline.folder.model.Produto;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

public class NewProdutoFragment extends BaseFragment {
    private static final String TAG = "NewProdutoFragment";
    private static final int GALERRY_REQUEST = 123;
    private DatabaseReference mProdutoDatabase;
    private DatabaseReference mEmpresaDatabase;
    private StorageReference mStorage;
    private Uri mImagemUri;
    private ImageView mImageProduto;
    private Switch mPromocaoSwitch;
    private EditText mDescricaoEd;
    private Spinner mEmpresaSpinner;
    private List<Empresa> mEmpresaList;
    private Empresa mEmpresa;

    public NewProdutoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_produto, container, false);

        mProdutoDatabase = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_PRODUTO);
        mEmpresaDatabase = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_EMPRESA);
        mProdutoDatabase.keepSynced(true);
        mEmpresaDatabase.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();

        mImageProduto = (ImageView) view.findViewById(R.id.iv_produto);
        mImageProduto.setOnClickListener(onClickGaleria());
        mPromocaoSwitch = (Switch) view.findViewById(R.id.switch_promocao);
        mDescricaoEd = (EditText) view.findViewById(R.id.ed_descricao);
        mEmpresaSpinner = (Spinner) view.findViewById(R.id.spinner_empresas);
        mEmpresaSpinner.setOnItemSelectedListener(onClickEmpresaSpinner());

        view.findViewById(R.id.btn_submit).setOnClickListener(onClickSubmit());

        mEmpresaList = new ArrayList<>();
        mEmpresaDatabase.addListenerForSingleValueEvent(onLoadEmpresa());

        return view;
    }

    private ValueEventListener onLoadEmpresa() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Empresa e = postSnapshot.getValue(Empresa.class);
                    e.uid = postSnapshot.getKey();
                    mEmpresaList.add(e);
                }
                populaSpinnerEmpresas();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        };
    }

    private AdapterView.OnItemSelectedListener onClickEmpresaSpinner() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEmpresa = mEmpresaList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private View.OnClickListener onClickGaleria() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 124);
                } else {
                    galeria();
                }
            }
        };
    }

    @RequiresPermission(allOf = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void galeria() {
        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("image/*");
        startActivityForResult(intentGallery, GALERRY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERRY_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                File fileImagem = FileUtil.from(getActivity(), data.getData());
                fileImagem = Compressor.getDefault(getActivity()).compressToFile(fileImagem);
                mImagemUri = Uri.fromFile(fileImagem);
                carregaImagem(mImageProduto, mImagemUri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener onClickSubmit() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProduto();
            }
        };
    }

    private void submitProduto() {
        final Boolean promocao = mPromocaoSwitch.isChecked();
        final String descricao = mDescricaoEd.getText().toString();
        final Empresa empresa = mEmpresa;

        showProgressDialog();

        StorageReference filePath = mStorage.child(Contantes.IMAGEM_PRODUTO).child(mImagemUri.getLastPathSegment());
        filePath.putFile(mImagemUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                save(descricao, downloadUrl.toString(), promocao, empresa);
            }
        });

    }

    private void save(String descricao, String imagem, Boolean promocao, Empresa empresa) {
        //String data = String.valueOf(new Date());

        String key = mProdutoDatabase.push().getKey();
        Produto produto = new Produto(descricao, imagem, promocao, formtarData(), empresa.categoria, empresa);
        Map<String, Object> values = produto.toMap();
        HashMap<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, values);
        mProdutoDatabase.updateChildren(childUpdates);
        hideProgressDialog();
        getActivity().finish();
    }

    private void populaSpinnerEmpresas() {
        String[] nomes = new String[mEmpresaList.size()];

        for (int i = 0; i < mEmpresaList.size(); i++) {
            nomes[i] = mEmpresaList.get(i).nome;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, nomes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mEmpresaSpinner.setAdapter(adapter);
    }

    private String formtarData() {
        Date data = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        String parse = fmt.format(data);
        return parse;
    }
}
