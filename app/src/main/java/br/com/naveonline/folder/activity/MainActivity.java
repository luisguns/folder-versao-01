package br.com.naveonline.folder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.naveonline.folder.R;
import br.com.naveonline.folder.fragment.MainFragment;
import br.com.naveonline.folder.fragment.dialog.FiltroCategoriaDialog;

public class MainActivity extends BaseActivity {
    private MainFragment frag;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_launcher));
        setSupportActionBar(toolbar);
        findViewById(R.id.fab).setOnClickListener(onClickFab());

        // Intancia do fragmento
        frag = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    private View.OnClickListener onClickFab() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, NewProdutoActivity.class));
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_empresa:
                startActivity(new Intent(MainActivity.this, EmpresasActivity.class));
                return true;
            case R.id.action_filtro:
                FiltroCategoriaDialog.show(getSupportFragmentManager(), new FiltroCategoriaDialog.Callback() {
                    @Override
                    public void onFiltroCategoria(String categoria) {
                        Toast.makeText(MainActivity.this, "Lista de " + categoria + " disponível.", Toast.LENGTH_SHORT).show();
                        frag.lista(categoria);
                        flag = true;
                    }
                });
                return true;
            case R.id.action_atualizar:
                frag.initView();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Botão(voltar) do aparelho
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Quando clicar para voltar, depois que estiver clicado no filtro, recarrega a lista novamente e não sai da aplicação
        // Se não olve clique no filtro, sai da aplicação quando clicar no botão voltar
        if (flag) {
            frag.initView();
            flag = false;
        } else {
            finish();
        }
        return false;
    }
}
