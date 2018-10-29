package br.com.naveonline.folder.fragment;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Carregando...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    protected void carregaImagem(final View imagem, final String url) {
        Picasso.with(getActivity())
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into((ImageView) imagem, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(getActivity())
                                .load(url)
                                //.error(R.drawable.header)
                                .into((ImageView) imagem, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        // mProgressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                    }
                                });

                    }
                });
    }
}
