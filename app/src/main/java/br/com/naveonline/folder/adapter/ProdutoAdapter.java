package br.com.naveonline.folder.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.model.Produto;
import br.com.naveonline.folder.utils.IOUtils;
import br.com.naveonline.folder.utils.SDCardUtils;
import br.com.naveonline.folder.view.RoundedImageView;

public class ProdutoAdapter extends FirebaseRecyclerAdapter<Produto, ProdutoAdapter.ViewHolder> {
    private Context mContext;
    private onClick onClick;

    public ProdutoAdapter(Class<Produto> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref, Context context, onClick onClick) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
        this.onClick = onClick;
    }



    @Override
    protected void populateViewHolder(ViewHolder viewHolder, final Produto model, final int position) {
        final String key = getRef(position).getKey();
        viewHolder.tv_descricao_produto.setText(model.descricao);
        viewHolder.tv_nome_empresa.setText(model.empresa.nome);

        carregaImagem(viewHolder.iv_produto, model.foto);
        carregaImagem(viewHolder.iv_empresa, model.empresa.foto);
        viewHolder.tv_data_registro.setText(model.data_registro);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onClick(v, position, key);
            }
        });

        viewHolder.iv_telefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + model.empresa.telefone);
                Intent intentFone = new Intent(Intent.ACTION_DIAL, uri);
                mContext.startActivity(intentFone);
            }
        });

        viewHolder.iv_compartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CompartilharTask(model.foto, model.empresa.nome, model.descricao).execute();
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_produto;
        RoundedImageView iv_empresa;
        ImageView iv_compartilhar;
        ImageView iv_telefone;
        TextView tv_descricao_produto;
        TextView tv_nome_empresa;
        TextView tv_data_registro;


        public ViewHolder(View itemView) {
            super(itemView);
            iv_produto = (ImageView) itemView.findViewById(R.id.iv_produto);
            iv_empresa = (RoundedImageView) itemView.findViewById(R.id.iv_empresa);
            iv_compartilhar = (ImageView) itemView.findViewById(R.id.iv_compartilhar);
            iv_telefone = (ImageView) itemView.findViewById(R.id.iv_telefone);
            tv_descricao_produto = (TextView) itemView.findViewById(R.id.tv_descricao_produto);
            tv_nome_empresa = (TextView) itemView.findViewById(R.id.tv_nome_empresa);
            tv_data_registro = (TextView) itemView.findViewById(R.id.tv_data_registro);

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

    private class CompartilharTask extends AsyncTask<Void, Void, String> {
        private String url;
        private String nomeEmpresa;
        private String descricao;
        private String piber = "Baixe o Folder: https://play.google.com/store/apps/details?id=br.com.naveonline.folder";
        private Uri imageUri;
        private ProgressDialog mProgressDialog;

        public CompartilharTask(String url, String nomeEmpresa, String descricao) {
            this.url = url;
            this.nomeEmpresa = nomeEmpresa;
            this.descricao = descricao;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            // Faz o download da foto
            String fileName = url.substring(url.lastIndexOf("/"));
            // Cria o arquivo no SD card
            File file = SDCardUtils.getPrivateFile(mContext, fileName + System.currentTimeMillis() + ".jpg");
            IOUtils.downloadToFile(url, file);
            // Salva a url para compartilhar a foto
            imageUri = Uri.fromFile(file);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Cria a intent com a foto
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            //shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, nomeEmpresa + " " + descricao + "\n\n" + piber);
//            shareIntent.setType("image/*");
            shareIntent.setType("*/*");
            mContext.startActivity(Intent.createChooser(shareIntent, "Enviar Foto.."));
            hideProgressDialog();
        }

        private void showProgressDialog() {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Carregando...");
            }

            mProgressDialog.show();
        }

        private void hideProgressDialog() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }
}
