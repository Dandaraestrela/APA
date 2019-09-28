/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grasp;

public class No {
    private int ID;
    private int demanda;
    private boolean visitado;
    
    public No(int ID, int demanda){
    this.ID = ID;
    this.demanda = demanda;
    visitado = false;
    }

    public int getID() {
        return ID;
    }

    public int getDemanda() {
        return demanda;
    }

    public void setDemanda(int demanda) {
        this.demanda = demanda;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }
    
    public No clone(){
        No clonado = new No(this.getID(), this.getDemanda());
        clonado.setVisitado(this.isVisitado());
        return clonado;
    }
    
}

