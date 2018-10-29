package br.com.naveonline.folder.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ListProduto implements Parcelable{
    public static final String  KEY = "produtos";
    public List<Produto> produtos;

    public ListProduto(List<Produto> produtos) {
        this.produtos = produtos;
    }

    protected ListProduto(Parcel in) {
    }

    public static final Creator<ListProduto> CREATOR = new Creator<ListProduto>() {
        @Override
        public ListProduto createFromParcel(Parcel in) {
            return new ListProduto(in);
        }

        @Override
        public ListProduto[] newArray(int size) {
            return new ListProduto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
