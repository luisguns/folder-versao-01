package br.com.naveonline.folder.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.naveonline.folder.R;

/**
 * Created by gilmar on 12/03/18.
 */

public class EditEmpresaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_empresa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
