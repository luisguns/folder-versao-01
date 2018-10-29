package br.com.naveonline.folder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.model.Empresa;

/**
 * Created by gilmar on 11/03/18.
 */

public class EmpresaAdapter extends FirebaseRecyclerAdapter<Empresa, EmpresaAdapter.ViewHolder> {
    private Context mContext;
    private onClick onClick;

    public EmpresaAdapter(Class<Empresa> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref, Context context, onClick onClick) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
        this.onClick = onClick;
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, Empresa model, final int position) {
        final String key = getRef(position).getKey();
        viewHolder.tv_nome.setText(model.nome);
        viewHolder.tv_categoria.setText(model.categoria);

        carregaImagem(viewHolder.iv_empresa, model.foto);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick(view, position, key);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_empresa;
        TextView tv_nome;
        TextView tv_categoria;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_empresa =  itemView.findViewById(R.id.iv_empresa);
            tv_nome =  itemView.findViewById(R.id.tv_nome);
            tv_categoria = itemView.findViewById(R.id.tv_categoria);

        }
    }

    public interface onClick {
        public void onClick(View view, int idx, String key);
    }

    private void carregaImagem(final View imagem, final String url/*, final ProgressBar progressBar*/) {
        Picasso.with(mContext)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into((ImageView) imagem, new Callback() {
                    @Override
                    public void onSuccess() {
                        // progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(url)
                                //.error(R.drawable.header)
                                .into((ImageView) imagem, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        //progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });

                    }
                });
    }
}
