package br.com.naveonline.folder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.activity.EditEmpresaActivity;
import br.com.naveonline.folder.activity.EmpresasActivity;
import br.com.naveonline.folder.activity.NewEmpresaActivity;
import br.com.naveonline.folder.adapter.EmpresaAdapter;
import br.com.naveonline.folder.model.Contantes;
import br.com.naveonline.folder.model.Empresa;

/**
 * A placeholder fragment containing a simple view.
 */
public class EmpresasFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseEmpresa;

    public EmpresasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empresas, container, false);

        mDatabaseEmpresa = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_EMPRESA);
        mDatabaseEmpresa.keepSynced(true);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        // ordenação decrecente
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(llm);

        Query query = mDatabaseEmpresa;

        EmpresaAdapter adapter = new EmpresaAdapter(Empresa.class, R.layout.adapter_empresas, EmpresaAdapter.ViewHolder.class, query, getContext(), onClickItem());
        mRecyclerView.setAdapter(adapter);
    }

    private EmpresaAdapter.onClick onClickItem() {
        return new EmpresaAdapter.onClick() {
            @Override
            public void onClick(View view, int idx, String key) {
                Intent intent = new Intent(getActivity(), EditEmpresaActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        };
    }

}
