package br.com.naveonline.folder.fragment.dialog;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.naveonline.folder.R;

/**
 * Created by gilmar on 13/03/18.
 */

public class FiltroCategoriaDialog extends DialogFragment {
    private Callback callback;
    private List<String> listCategorias;

    public static void show(FragmentManager fm, Callback callback) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("filtrar_categoria");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        FiltroCategoriaDialog frag = new FiltroCategoriaDialog();
        frag.callback = callback;
        frag.show(ft, "filtrar_categoria");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.dialog_filtro_categoria));
        View view = inflater.inflate(R.layout.dialog_filtro_categoria, container, false);

        String[] categorias = getResources().getStringArray(R.array.spinner_array_categoria);
        // Remove o 1° item da lista(Selecione uma categoria)
        listCategorias = new ArrayList<>(Arrays.asList(categorias));
        listCategorias.remove(0);

        ListView listView = view.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listCategorias);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(onClick());

        return view;
    }

    private AdapterView.OnItemClickListener onClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(callback != null) {
                    callback.onFiltroCategoria(listCategorias.get(position));
                }
                dismiss();
            }
        };
    }

    public static interface Callback {
        public void onFiltroCategoria(String categoria);
    }
}
