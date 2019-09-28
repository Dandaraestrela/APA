/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grasp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author danda
 */
public class GRASP {
    static private No[] nos;
    static private Veiculo[] veiculos;
    static private int[][] distancias;
    static private int numVeiculos;
    static private int numClientes;
    static private int capacidadeV;
    static private int custoPrimeiraFase;
    static private int custoSegundaFase;

    public GRASP() {
        long tempoInicio = System.currentTimeMillis();

        String linha = "";
        String[] entradas = new String[2];
        String[] distanciasString;
        
        // lendo os dados e adicionando aos nossos campos
        try {
            BufferedReader br = new BufferedReader(new FileReader("P-n19-k2.txt"));
            linha = br.readLine();
            while (!linha.contentEquals("DEMAND_SECTION:")) {
                entradas = linha.split(" ");
                switch (entradas[0]) {
                    case "DIMENSION:":
                        numClientes = Integer.parseInt(entradas[1]);
                        nos = new No[numClientes];
                        distanciasString = new String[numClientes];
                        distancias = new int[numClientes][numClientes];
                        break;

                    case "VEHICLES:":
                        numVeiculos = Integer.parseInt(entradas[1]);
                        veiculos = new Veiculo[numVeiculos];
                        break;

                    case "CAPACITY:":
                        capacidadeV = Integer.parseInt(entradas[1]);

                        for (int i = 0; i < numVeiculos; i++) {
                            veiculos[i] = new Veiculo(capacidadeV);
                        }
                        break;

                    default:
                        break;
                }
                linha = br.readLine();
            }

            int indice, demandaAux;
            for (int i = 0; i < numClientes; i++) {
                linha = br.readLine();
                entradas = linha.split("   ");
                indice = Integer.parseInt(entradas[0].trim());
                demandaAux = Integer.parseInt(entradas[1].trim());

                nos[i] = new No(indice, demandaAux);
            }
            // chega no espaço em branco
            linha = br.readLine();
            // chega no DEMAND
            linha = br.readLine();

            // pega dados da matriz
            for (int i = 0; i < numClientes; i++) {
                linha = br.readLine();
                linha = linha.trim();
                distanciasString = linha.split("   ");
                for (int j = 0; j < numClientes; j++) {
                    distancias[i][j] = Integer.parseInt(distanciasString[j].trim());
                }
            }

            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        custoPrimeiraFase = gulosoAleatorio();
        //System.out.println("Custo da primeira fase do GRASP = " + custoPrimeiraFase);
        custoSegundaFase = VND(veiculos);
        //System.out.println("Custo da segunda fase do GRASP = " + custoSegundaFase);
        
    }
    
    static public int gulosoAleatorio() {
        float alpha = 0.4F;
        int custoVisitado, finalCusto = 0;
        int veiculoAtual = 0;
        int auxSobrou = numClientes;
        // os nós pendentes tirando o armazem
        int numNosPendentes = numClientes;
        int N;
        
        while (existeNaoVisitado()) {
            // lista de candidatos
            No[] listaC = new No[nos.length];
            
            for(int x = 0; x < nos.length; x++){
                listaC[x] = nos[x].clone();
            }
            
            N = round(alpha*(numNosPendentes));
            
            // criação da lista de candidatos restritos
            if(N > 0){
                // aqui é a situação correta, onde ainda há muitos elementos e o N nao é arredondado p zero
            }else{
                N = numNosPendentes;
            }

            No[] listaCR = new No[N];
            int[] indexCR = new int[N];
            
            int indiceEscolhido = 0;
            No escolhido = null;

            // "infinito"
            int custoMin = Integer.MAX_VALUE;
            
            // inicializa o veículo com o primeiro nó que é o armazem, se ele for novo
            if (veiculos[veiculoAtual].getCaminho().isEmpty()) {
                veiculos[veiculoAtual].getCaminho().add(nos[0]);
                nos[0].setVisitado(true);
            }
            
            for(int j = 0; j < N; j++){
                custoMin = Integer.MAX_VALUE;
            // ESCOLHA DO MENOR DO CAMINHO
                for (int i = 1; i < numClientes; i++) {
                    
                    // escolhe o com menor custo
                    if (!listaC[i].isVisitado()) {
                        // verifica se o que tem no caminhão dá p abastecer esse nó
                        if (veiculos[veiculoAtual].getCargaAtual() >= listaC[i].getDemanda()) {
                            custoVisitado = distancias[veiculos[veiculoAtual].getLocAtual()][i];
                            if (custoVisitado < custoMin) {
                                custoMin = custoVisitado;
                                indiceEscolhido = i;
                                escolhido = listaC[i];     
                            }
                        }
                    }
                } 
            indexCR[j] = indiceEscolhido;
            listaCR[j] = escolhido; 
            listaC[indiceEscolhido].setVisitado(true);

            }
            // AQUI VAMOS ESCOLHER UM ENTRE A LISTACR ALEATORIAMENTE
            if(listaCR.length == 1){
                escolhido = listaCR[0];
                indiceEscolhido = indexCR[0];
                custoMin = distancias[veiculos[veiculoAtual].getLocAtual()][0];
            }else{
            Random random = new Random();
            int numero = random.nextInt(N);
            escolhido = listaCR[numero];
            indiceEscolhido = indexCR[numero];
            custoMin = distancias[veiculos[veiculoAtual].getLocAtual()][indiceEscolhido];
            }
            
            // aqui já foi escolhido
            if (escolhido != null) {
                //adiciona escolhido à rota
                veiculos[veiculoAtual].getCaminho().add(escolhido);
                //diminui da capacidade do caminhão
                veiculos[veiculoAtual].setCargaAtual(veiculos[veiculoAtual].getCargaAtual() - escolhido.getDemanda());
                //muda ultima localização do caminhão
                veiculos[veiculoAtual].setLocAtual(escolhido.getID());

                nos[indiceEscolhido].setVisitado(true);
                numNosPendentes--;
                finalCusto += custoMin;
                auxSobrou--;
            } else {
                // vamos mudar de veiculo pois ninguem pode entrar no atual
                if (veiculoAtual + 1 < numVeiculos) {
                    if (veiculos[veiculoAtual].getLocAtual() != 0) {
                        //caminhão volta p casa
                        finalCusto += distancias[veiculos[veiculoAtual].getLocAtual()][0];
                        veiculos[veiculoAtual].getCaminho().add(nos[0]);
                    }
                    veiculoAtual += 1;
                } else {
                    // return para que mesmo que não encontre solução, esta instancia seja desconsiderada colocando seu custo muito alto
                    return Integer.MAX_VALUE;
                }
            }
        }
        // aqui precisamos atualizar pois, além de nao haver mais carros
        // não há mais clientes
        finalCusto += distancias[veiculos[veiculoAtual].getLocAtual()][0];
        veiculos[veiculoAtual].getCaminho().add(nos[0]);
        return finalCusto;
    }

    static public boolean existeNaoVisitado() {
        for (int i = 0; i < numClientes; i++) {
            if (!nos[i].isVisitado()) {
                return true;
            }
        }
        return false;
    }
    
    static public int calculaCusto(ArrayList<No> rota) {
        int custoCalculado = 0;
        int tamanho = rota.size();

        for (int i = 0; i < tamanho - 1; i++) {
            custoCalculado += distancias[rota.get(i).getID()][rota.get(i + 1).getID()];
        }
        return custoCalculado;
    }
    
    static public int VND(Veiculo[] carros){
        int numCarros = carros.length, tamanhoRota;
        int custoEncontrado = 0;
        Veiculo[] funcional = carros.clone();

        for(int i = 0; i < numCarros; i++){
            tamanhoRota = funcional[i].getCaminho().size();
            
            // p cada nó da rota desconsiderando o primeiro e último (depósito)
            for(int j = 1; j < tamanhoRota - 1; j++){
            // aqui pega o "primeiro"
            No auxNo1 = new No(funcional[i].getCaminho().get(j).getID(), funcional[i].getCaminho().get(j).getDemanda());
            // aqui pega o "segundo"
            No auxNo2 = new No(funcional[i].getCaminho().get(j+1).getID(), funcional[i].getCaminho().get(j+1).getDemanda());
            
            int custoAntes = calculaCusto(carros[i].getCaminho());
            funcional[i].getCaminho().set(j, auxNo2);
            funcional[i].getCaminho().set(j+1, auxNo1);
            int custoDepois = calculaCusto(funcional[i].getCaminho());
            
            // se custo depois de inverter for melhor deixa, se não troca
            // VND
            if(custoDepois > custoAntes){
                funcional[i].getCaminho().set(j, auxNo1);
                funcional[i].getCaminho().set(j+1, auxNo2);
              }
            }
            custoEncontrado += calculaCusto(funcional[i].getCaminho());
        }
        return custoEncontrado;
    }
    
    public int retorna(){
        return custoSegundaFase;
    }
}
