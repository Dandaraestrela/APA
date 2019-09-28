/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grasp;

import java.util.ArrayList;

public class Veiculo {
    private int capacidade;
    private int locAtual;
    private int cargaAtual;
    private ArrayList<No> caminho;
    
    
    public Veiculo(int capacidade){
    this.capacidade = capacidade;
    cargaAtual = this.capacidade;
    locAtual = 0;
    caminho = new ArrayList<>();
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public int getCargaAtual() {
        return cargaAtual;
    }

    public void setCargaAtual(int cargaAtual) {
        this.cargaAtual = cargaAtual;
    }
    
    public int getLocAtual() {
        return locAtual;
    }

    public void setLocAtual(int locAtual) {
        this.locAtual = locAtual;
    }

    public ArrayList<No> getCaminho() {
        return caminho;
    }
    
}
