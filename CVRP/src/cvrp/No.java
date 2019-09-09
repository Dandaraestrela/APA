
package cvrp;


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

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }
    
}
