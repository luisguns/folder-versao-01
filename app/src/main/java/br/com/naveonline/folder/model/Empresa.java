package br.com.naveonline.folder.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Empresa {

    public String uid;
    public String nome;
    public String sobre;
    public String foto;
    public String telefone;
    public Boolean whatsapp;
    public Boolean entrega;
    public Boolean pagamento;
    public String rua;
    public String numero;
    public String bairro;
    public String cep;
    public String cidade;
    public String estado;
    public Integer latitude;
    public Integer longitude;
    public String pais;//país
    public String categoria;
    public List<Funcionamento> funcionamentos;
    public String data_registro;
    public String user_uid;

    public Empresa() {

    }

    public Empresa(String uid, String nome, String sobre, String foto, String telefone, Boolean whatsapp, Boolean entrega, Boolean pagamento, String rua, String numero, String bairro, String cep, String cidade, String estado, Integer latitude, Integer longitude, String pais, String categoria, List<Funcionamento> funcionamentos, String data_registro, String user_uid) {
        this.uid = uid;
        this.nome = nome;
        this.sobre = sobre;
        this.foto = foto;
        this.telefone = telefone;
        this.whatsapp = whatsapp;
        this.entrega = entrega;
        this.pagamento = pagamento;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pais = pais;
        this.categoria = categoria;
        this.funcionamentos = funcionamentos;
        this.data_registro = data_registro;
        this.user_uid = user_uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("sobre", sobre);
        result.put("foto", foto);
        result.put("telefone", telefone);
        result.put("whatsapp", whatsapp);
        result.put("entrega", entrega);
        result.put("pagamento", pagamento);
        result.put("rua", rua);
        result.put("numero", numero);
        result.put("bairro", bairro);
        result.put("cep", cep);
        result.put("cidade", cidade);
        result.put("estado", estado);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("pais", pais);
        result.put("categoria", categoria);
        result.put("data_registro", data_registro);
        result.put("user_uid", user_uid);
        result.put("funcionamentos", convertFuncionamento());
        return result;
    }

    private HashMap<Object, Object> convertFuncionamento() {
        HashMap<Object, Object> funcionamentoList = new HashMap<>();
        if (funcionamentos.size() > 0) {
            for (int i = 0; i < funcionamentos.size(); i++) {
                Map<String, Object> funcMap = new ObjectMapper().convertValue(funcionamentos.get(i), Map.class);
                funcionamentoList.put(String.valueOf(i), funcMap);
            }
        }
        return funcionamentoList;
    }
}
