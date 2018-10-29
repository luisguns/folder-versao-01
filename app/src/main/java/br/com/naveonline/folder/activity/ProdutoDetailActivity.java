package br.com.naveonline.folder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.fragment.dialog.FiltroCategoriaDialog;
import br.com.naveonline.folder.model.Contantes;
import br.com.naveonline.folder.model.Produto;

public class ProdutoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void recuperaProduto(Produto produto) {
        ImageView imageView = (ImageView) findViewById(R.id.iv_produto);
        carregaImagem(imageView, produto.foto);
    }


}
