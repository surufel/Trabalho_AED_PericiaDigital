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
        // Construtor público padrão [cite: 31]
    }

/*
Implemente construtor público padrão: public MinhaClasse() {}
Use apenas bibliotecas padrão do Java + API fornecida
Trate exceções de IO adequadamente
Retorne tipos exatos especificados na interface
*/

    /**
     * Desafio 1 (Pilha): Encontra sessões de usuário que foram corrompidas ou
     * deixadas abertas, indicando uma possivel falha ou ataque.
     * [cite: 33]
     */
    @Override
    public Set<String> encontrarSessoesInvalidas(String arquivo) throws IOException {
        // O Mapa armazena a pilha de sessoes ativas para cada usuário
        Map<String, Stack<String>> pilhasUsuarios = new HashMap<>();

        // O Set armazena o ID de todas as sessões que identificamos como inválidas [cite: 139]
        Set<String> sessoesInvalidas = new HashSet<>();

        // Usamos try-with-resources para garantir que o arquivo seja fechado
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            // 1. Pular a linha do cabeçalho
            br.readLine();

            // 2. Ler o restante do arquivo linha por linha
            while ((linha = br.readLine()) != null) {
                String[] colunas = linha.split(",");

                // Extrair os dados relevantes do CSV [cite: 389-392]
                String userId = colunas[1];
                String sessionId = colunas[2];
                String actionType = colunas[3];

                // Garante que existe uma pilha para este usuário
                pilhasUsuarios.putIfAbsent(userId, new Stack<>());
                Stack<String> pilhaDoUsuario = pilhasUsuarios.get(userId);

                // 3. Aplicar a lógica de validação
                if ("LOGIN".equals(actionType)) {
                    // Se a pilha não está vazia, é um LOGIN aninhado. [cite: 126]
                    // A sessão atual (sessionId) é inválida.
                    if (!pilhaDoUsuario.isEmpty()) {
                        sessoesInvalidas.add(sessionId); [cite: 126]
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
                // Outros tipos de ação (FILE_ACCESS, etc.) são ignorados neste desafio
            }
        }
        // A exceção IOException é propagada, conforme a assinatura do método [cite: 38]

        // 4. Processamento pós-arquivo
        // Verifica todas as pilhas; as que não estiverem vazias têm sessões inválidas.
        for (Stack<String> pilha : pilhasUsuarios.values()) {
            while (!pilha.isEmpty()) {
                // Adiciona todas as SESSIONS_IDs restantes nas pilhas ao resultado [cite: 138]
                sessoesInvalidas.add(pilha.pop());
            }
        }

        // Retorna o conjunto de todas as sessões inválidas encontradas [cite: 139]
        return sessoesInvalidas;
    }

    @Override
    public List<String> reconstruirLinhaTempo(String arquivo, String sessionId) throws IOException {
        // Implementar usando Queue<String>
        return null; // TODO: Implementar
    }

    @Override
    public List<Alerta> priorizarAlertas(String arquivo, int n) throws IOException {
        // Implementar usando PriorityQueue<Alerta>
        return null; // TODO: Implementar
    }

    @Override
    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) throws IOException {
        // Implementar usando Stack (Next Greater Element)
        return null; // TODO: Implementar
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String origem, String destino) throws IOException {
        // Implementar usando BFS em grafo
        return Optional.empty(); // TODO: Implementar
    }
}