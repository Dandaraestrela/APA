/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grasp;

// essa classe vai rodar o grasp um número de vezes
public class iterador {
    static public int iteracoes;
    
    public static void main(String[] args) {
        long tempoInicio = System.currentTimeMillis();
        iteracoes = 10;
        GRASP[] solucoes = new GRASP[iteracoes];
        int[] valores = new int[iteracoes];
        for(int i = 0; i< iteracoes; i++){
            solucoes[i] = new GRASP();
            valores[i] = solucoes[i].retorna();
        }
        int menor = Integer.MAX_VALUE;
        for(int j = 0; j< iteracoes; j++){
            if(valores[j] < menor){
                menor = valores[j];
            }
        }
        System.out.println("Menor custo calculado com " + iteracoes + " iterações = " + menor);       
        System.out.println("Tempo Total em miliSegundos: " + (System.currentTimeMillis()-tempoInicio));
    }
    
}
