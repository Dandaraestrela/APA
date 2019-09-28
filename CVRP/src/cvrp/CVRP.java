package cvrp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CVRP {

    static private No[] nos;
    static private Veiculo[] veiculos;
    static private int[][] distancias;
    static private int numVeiculos;
    static private int numClientes;
    static private int capacidadeV;
    static private int custoFinal;

    public static void main(String[] args) {
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
        custoFinal = guloso();
        System.out.println("Custo final do algoritmo guloso = " + custoFinal);

        int custoAux2 = VND(veiculos);
        int custoAux1 = Movimentos(veiculos);

        System.out.println("Custo após movimentos = " + custoAux1);

        System.out.println("Custo após movimentos VND = " + custoAux2);
        System.out.println("Tempo Total em miliSegundos: " + (System.currentTimeMillis() - tempoInicio));
    }

    static public int guloso() {
        int custoVisitado, finalCusto = 0;
        int veiculoAtual = 0;
        int auxSobrou = numClientes;

        while (existeNaoVisitado()) {

            int indiceEscolhido = 0;
            No escolhido = null;

            // "infinito"
            int custoMin = Integer.MAX_VALUE;

            if (veiculos[veiculoAtual].getCaminho().isEmpty()) {
                veiculos[veiculoAtual].getCaminho().add(nos[0]);
                nos[0].setVisitado(true);
            }

            for (int i = 0; i < numClientes; i++) {
                // escolhe o com menor custo
                if (!nos[i].isVisitado()) {
                    if (veiculos[veiculoAtual].getCargaAtual() >= nos[i].getDemanda()) {
                        custoVisitado = distancias[veiculos[veiculoAtual].getLocAtual()][i];
                        if (custoVisitado < custoMin) {
                            custoMin = custoVisitado;
                            indiceEscolhido = i;
                            escolhido = nos[i];
                        }
                    }
                }
            }

            if (escolhido != null) {
                //adiciona escolhido à rota
                veiculos[veiculoAtual].getCaminho().add(escolhido);
                //diminui da capacidade do caminhão
                veiculos[veiculoAtual].setCargaAtual(veiculos[veiculoAtual].getCargaAtual() - escolhido.getDemanda());
                //muda ultima localização do caminhão
                veiculos[veiculoAtual].setLocAtual(escolhido.getID());

                nos[indiceEscolhido].setVisitado(true);
                finalCusto += custoMin;
                auxSobrou--;
            } else {
                // vamos mudar de veiculo pois ninguem pode entrar no atual
                if (veiculoAtual + 1 < numVeiculos) {
                    // APAGARRRR System.out.println("mudou de carro");
                    if (veiculos[veiculoAtual].getLocAtual() != 0) {
                        //caminhão volta p casa
                        finalCusto += distancias[veiculos[veiculoAtual].getLocAtual()][0];
                        veiculos[veiculoAtual].getCaminho().add(nos[0]);
                    }
                    veiculoAtual += 1;
                } else {
                    System.out.println("Não há solução.");
                    System.exit(0);
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

    static public int Movimentos(Veiculo[] carros) {
        int custoMeio = 0, custoIF = 0, custoSP = 0;
        int tamanhoRota = 0;
        int auxTroca1Meio = 0, auxTroca2Meio = 0, auxTroca1IF = 0, auxTroca2IF = 0, auxTroca1SP = 0, auxTroca2SP = 0;

        Veiculo[] trocaMeio = carros.clone();
        Veiculo[] trocaInicialFinal = carros.clone();
        Veiculo[] trocaSegundoPenult = carros.clone();

        //troca as rotas de todos os carros
        for (int i = 0; i < carros.length; i++) {
            tamanhoRota = carros[i].getCaminho().size();
            // só consegue trocar se tiver mais do que dois ou dois
            if (tamanhoRota >= 2) {
                // vê se o tamanho da rota é par ou impar, para pegar os centrais
                if (tamanhoRota % 2 == 0) {
                    auxTroca1Meio = tamanhoRota / 2;
                    auxTroca2Meio = auxTroca1Meio + 1;
                } else {
                    auxTroca1Meio = (tamanhoRota + 1) / 2;
                    auxTroca2Meio = auxTroca1Meio - 1;
                }
                auxTroca1IF = 1;
                auxTroca2IF = tamanhoRota - 2;
                auxTroca1SP = 2;
                auxTroca2SP = tamanhoRota - 3;

                // troca Meio
                No noTroca1Meio = new No(trocaMeio[i].getCaminho().get(auxTroca1Meio).getID(), trocaMeio[i].getCaminho().get(auxTroca1Meio).getDemanda());
                No noTroca2Meio = new No(trocaMeio[i].getCaminho().get(auxTroca2Meio).getID(), trocaMeio[i].getCaminho().get(auxTroca2Meio).getDemanda());
                trocaMeio[i].getCaminho().set(auxTroca1Meio, noTroca2Meio);
                trocaMeio[i].getCaminho().set(auxTroca2Meio, noTroca1Meio);
                custoMeio += calculaCusto(trocaMeio[i].getCaminho());

                // troca Inicial -> Final
                No noTroca1IF = new No(trocaInicialFinal[i].getCaminho().get(auxTroca1IF).getID(), trocaInicialFinal[i].getCaminho().get(auxTroca1IF).getDemanda());
                No noTroca2IF = new No(trocaInicialFinal[i].getCaminho().get(auxTroca2IF).getID(), trocaInicialFinal[i].getCaminho().get(auxTroca2IF).getDemanda());
                trocaInicialFinal[i].getCaminho().set(auxTroca1IF, noTroca2IF);
                trocaInicialFinal[i].getCaminho().set(auxTroca2IF, noTroca1IF);
                custoIF += calculaCusto(trocaInicialFinal[i].getCaminho());

                // troca Segundo -> Penúltimo
                No noTroca1SP = new No(trocaSegundoPenult[i].getCaminho().get(auxTroca1SP).getID(), trocaSegundoPenult[i].getCaminho().get(auxTroca1SP).getDemanda());
                No noTroca2SP = new No(trocaSegundoPenult[i].getCaminho().get(auxTroca2SP).getID(), trocaSegundoPenult[i].getCaminho().get(auxTroca2SP).getDemanda());
                trocaSegundoPenult[i].getCaminho().set(auxTroca1SP, noTroca2SP);
                trocaSegundoPenult[i].getCaminho().set(auxTroca2SP, noTroca1SP);
                custoSP += calculaCusto(trocaSegundoPenult[i].getCaminho());

            }
        }
        if (custoMeio < custoFinal) {
            System.out.println("meio");
            return custoMeio;
        } else if (custoIF < custoFinal) {
            System.out.println("IF");
            return custoIF;
        } else if (custoSP < custoFinal) {
            System.out.println("SP");
        }
        return custoFinal;
    }

    static public int VND(Veiculo[] carros) {
        int numCarros = carros.length, tamanhoRota;
        int custoEncontrado = 0;
        Veiculo[] funcional = carros.clone();

        for (int i = 0; i < numCarros; i++) {
            tamanhoRota = funcional[i].getCaminho().size();

            // p cada nó da rota desconsiderando o primeiro e último (depósito)
            for (int j = 1; j < tamanhoRota - 1; j++) {
                // aqui pega o "primeiro"
                No auxNo1 = new No(funcional[i].getCaminho().get(j).getID(), funcional[i].getCaminho().get(j).getDemanda());
                // aqui pega o "segundo"
                No auxNo2 = new No(funcional[i].getCaminho().get(j + 1).getID(), funcional[i].getCaminho().get(j + 1).getDemanda());

                int custoAntes = calculaCusto(carros[i].getCaminho());
                funcional[i].getCaminho().set(j, auxNo2);
                funcional[i].getCaminho().set(j + 1, auxNo1);
                int custoDepois = calculaCusto(funcional[i].getCaminho());

                // se custo depois de inverter for melhor deixa, se não troca
                // VND
                if (custoDepois > custoAntes) {
                    funcional[i].getCaminho().set(j, auxNo1);
                    funcional[i].getCaminho().set(j + 1, auxNo2);
                }
            }
            custoEncontrado += calculaCusto(funcional[i].getCaminho());
        }
        return custoEncontrado;
    }
}
