package br.edu.icev.aed.forense;

import java.io.BufferedReader; // Importação necessária para ler o arquivo
import java.io.FileReader;     // Importação necessária para ler o arquivo
import java.io.IOException;
import java.util.*; // É melhor que seja apenas java.util.*, mesmo que o objetivo seja otimizar espaço

public class MinhaAnaliseForense implements AnaliseForenseAvancada {

    public MinhaAnaliseForense(){}

/*
Implemente construtor público padrão: public MinhaClasse() {}
Use apenas bibliotecas padrão do Java + API fornecida
Trate exceções de IO adequadamente
Retorne tipos exatos especificados na interface
*/
@Override
public Set<String> encontrarSessoesInvalidas(String arquivo) throws IOException {
    Map<String, Stack<String>> pilhasUsuarios = new HashMap<>();
    Set<String> sessoesInvalidas = new HashSet<>();
    try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
        String linha = leitor.readLine();
        while ((linha = leitor.readLine()) != null)
        {
            String[] colunas = linha.split(",", -1);
            if (colunas.length < 4) continue;
            String userId = colunas[1].trim();
            String sessionId = colunas[2].trim();
            String actionType = colunas[3].trim();
            pilhasUsuarios.putIfAbsent(userId, new Stack<>());
            Stack<String> pilhaDoUsuario = pilhasUsuarios.get(userId);
            if ("LOGIN".equals(actionType)) {
                if (!pilhaDoUsuario.isEmpty()){
                    sessoesInvalidas.add(sessionId);}
                pilhaDoUsuario.push(sessionId);}
            else if ("LOGOUT".equals(actionType)) {
                if (pilhaDoUsuario.isEmpty() || !pilhaDoUsuario.peek().equals(sessionId)) {sessoesInvalidas.add(sessionId);}
                else {pilhaDoUsuario.pop();}
            }
        }
    }
    for (Stack<String> pilha : pilhasUsuarios.values()){while (!pilha.isEmpty()) {sessoesInvalidas.add(pilha.pop());}}
    return sessoesInvalidas;
}
    @Override
    public List<String> reconstruirLinhaTempo(String arquivo, String sessionId) throws IOException {
        Queue<String> fila = new ArrayDeque<>();
        List<String> resultado = new ArrayList<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null)
            {
                if (linha.startsWith("TIMESTAMP")) continue;
                String[] col = linha.split(",", -1);
                if (col.length < 4) continue;
                String sessao = col[2].trim();
                String action = col[3].trim();
                if (sessao.equals(sessionId)) {fila.add(action);}
            }
        }
        while (!fila.isEmpty()) {resultado.add(fila.poll());}
        return resultado;
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