package br.com.naveonline.folder.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.activity.MainActivity;
import br.com.naveonline.folder.activity.ProdutoDetailActivity;
import br.com.naveonline.folder.adapter.ProdutoAdapter;
import br.com.naveonline.folder.model.Contantes;
import br.com.naveonline.folder.model.Empresa;
import br.com.naveonline.folder.model.Produto;

public class MainFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseProduto;
    private ProgressDialog mProgressDialog;

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mDatabaseProduto = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_PRODUTO);
        mDatabaseProduto.keepSynced(true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Carregando");
        mProgressDialog.setMessage("Por favor aguarde...");
        mProgressDialog.show();

        mDatabaseProduto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }


    public void initView() {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        // ordenação decrecente
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(llm);

        // oredenação descrecente - limitToLast
        Query query = mDatabaseProduto.orderByValue().limitToLast(200);
        // oredenação crecente - limitToFirst
        //limitToFirst(100);

        final ProdutoAdapter adapter = new ProdutoAdapter(Produto.class, R.layout.adapter_produto,
                ProdutoAdapter.ViewHolder.class, query, getContext(), onClickItem()) {

        };

        mRecyclerView.setAdapter(adapter);

    }

    public void lista(String categoria) {
        // exemplo de filtro(query)
        // query.orderByChild("descricao").equalTo("Bora de sushi");
        Query query = FirebaseDatabase.getInstance().getReference().child(Contantes.TABELA_PRODUTO);

        ProdutoAdapter adapter = new ProdutoAdapter(Produto.class, R.layout.adapter_produto,
                ProdutoAdapter.ViewHolder.class, query.orderByChild("categoria").equalTo(categoria), getContext(), onClickItem());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelable(ListProduto.KEY, );
        //outState.putInt("position",  mRecyclerView.getAdapterPosition());
    }

    private ProdutoAdapter.onClick onClickItem() {
        return new ProdutoAdapter.onClick() {
            @Override
            public void onClick(View view, int idx, String key) {
                Intent intent = new Intent(getActivity(), ProdutoDetailActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        };
    }
}
