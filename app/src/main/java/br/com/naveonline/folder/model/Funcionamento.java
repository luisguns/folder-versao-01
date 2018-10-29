package br.com.naveonline.folder.model;

import java.util.ArrayList;
import java.util.List;

public class Funcionamento {

    public String dia;
    public String horaAbrir;
    public String horaFecha;
    public Boolean aberto;

    public static List<Funcionamento> setFuncionamento() {
        List<Funcionamento> funcionamentos = new ArrayList<>();
        String[] dia = {"Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"};
        for (int i = 0; i < 7 ; i++) {
            Funcionamento f = new Funcionamento();
            f.dia = dia[i];
            f.horaFecha = f.horaAbrir = "00:00";
            f.aberto = false;
            funcionamentos.add(f);
        }
        return funcionamentos;
    }

}
