package br.com.naveonline.folder.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Produto {

    public String descricao;
    public String foto;
    public Boolean promocao;
    public String data_registro;
    public Empresa empresa;
    public String categoria;// categoria da empresa para fazer o filtro

    public Produto() {
    }

    public Produto(String descricao, String foto, Boolean promocao, String data_registro, String categoria, Empresa empresa) {
        this.descricao = descricao;
        this.foto = foto;
        this.promocao = promocao;
        this.data_registro = data_registro;
        this.categoria = categoria;
        this.empresa = empresa;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("descricao", descricao);
        result.put("foto", foto);
        result.put("data_registro", data_registro);
        result.put("categoria", categoria);
        result.put("empresa", converteEmpresa());
        return result;
    }

    //teste com Map... Qualquer coisa mudar para HashMap<Object, Object>
    private Map converteEmpresa() {
        Map map = new ObjectMapper().convertValue(empresa, Map.class);
        return map;
    }

}
