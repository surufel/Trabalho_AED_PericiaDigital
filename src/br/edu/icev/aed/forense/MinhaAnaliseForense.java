package br.edu.icev.aed.forense;

import java.io.BufferedReader; // Importação necessária para ler o arquivo
import java.io.FileReader;     // Importação necessária para ler o arquivo
import java.io.IOException;
import java.util.HashMap;      // Importação para o Mapa
import java.util.HashSet;      // Importação para o Conjunto (Set)
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;       // Importação para a Pilha

public class MinhaAnaliseForense implements AnaliseForenseAvancada {

    public MinhaAnaliseForense(){
            //construtor solicitado
    }

/*
Implemente construtor público padrão: public MinhaClasse() {}
Use apenas bibliotecas padrão do Java + API fornecida
Trate exceções de IO adequadamente
Retorne tipos exatos especificados na interface
*/

    /*
     * Desafio 1 (Pilha): Encontra sessões de usuário que foram corrompidas ou
     * deixadas abertas, indicando uma possivel falha ou ataque.
     * [cite: 33]
     */

    @Override
    public Set<String> encontrarSessoesInvalidas(String arquivo) throws IOException {
        Map<String>, Stack<String>> pilhaUsuarios = new HashMap<>();

        Set<String> sessoesInvalidas = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))){

            br.readLine();

            while ((linha = br.readLine()) != null) {
                String[] colunas = linha.split(",");

                String userId = colunas[1];
                String sessionId = colunas[2];
                String actionType = colunas[3];

                pilhasUsuarios.putIfAbsent(userId, new Stack<>());
                Stack<String> pilhaDoUsuario = pilhasUsuarios.get(userId);

                // 3. Aplicar a lógica de validação
                if ("LOGIN".equals(actionType)) {
                    // Se a pilha não está vazia, é um LOGIN aninhado. [cite: 126]
                    // A sessão atual (sessionId) é inválida.
                    if (!pilhaDoUsuario.isEmpty()) {
                        sessoesInvalidas.add(sessionId); [cite:
                        126]
                    }
                    // Empilha a sessão atual de qualquer forma [cite: 127]
                    pilhaDoUsuario.push(sessionId);
                } else if ("LOGOUT".equals(actionType)) {
                    // Se a pilha está vazia ou o topo não bate, é um LOGOUT inválido. [cite: 128]
                    if (pilhaDoUsuario.isEmpty() || !pilhaDoUsuario.peek().equals(sessionId)) {
                        sessoesInvalidas.add(sessionId);
                    } else {
                        // Se bateu, é um LOGOUT válido, desempilha. [cite: 129]
                        pilhaDoUsuario.pop();
                    }
                }
            }
        }
        for (Stack<String> pilha : pilhasUsuarios.values()) {
            while (!pilha.isEmpty()) {
                // Adiciona todas as SESSIONS_IDs restantes nas pilhas ao resultado [cite: 138]
                sessoesInvalidas.add(pilha.pop());
            }
        }

        return sessoesInvalidas;
            }

    @Override
    public List<String> reconstruirLinhaTempo(String arquivo, String sessionId) throws IOException {
        List<String> resultado = new ArrayList<>();
        Queue<String> fila = new ArrayDeque<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null){
                if(linha.startsWith("TIMESTAMP")){continue;}
                String[] linha_dividida = linha.split(",", -1);
                if(linha_dividida.length < 4){continue;} // 0 = TIMESTAMP, 1 = USER_ID, 2 = SESSION_ID, 3 = ACTION_TYPE
                String sessao = linha_dividida[2].trim();
                String action = linha_dividida[3].trim();
                if(sessao.equals(sessionId)){fila.add(action);}
            }
        }
        while (!fila.isEmpty()) {resultado.add(fila.poll());}
        return resultado;
    }
    }

    @Override
    public List<Alerta> priorizarAlertas(String arquivo, int n) throws IOException {
        // Implementar usando PriorityQueue<Alerta>
    }

    @Override
    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) throws IOException {
        // Implementar usando Stack (Next Greater Element)
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String origem, String destino) throws IOException {
        // Implementar usando BFS em grafo
    }
}