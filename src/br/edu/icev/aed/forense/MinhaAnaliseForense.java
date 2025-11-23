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
public Set<String> encontrarSessoesInvalidas(String arquivo) throws IOException { // Desafio 1
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
        Queue<String> FIFO = new ArrayDeque<>();
        List<String> resultado = new ArrayList<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            leitor.readLine();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] colunas = linha.split(",", -1);
                if (colunas.length < 4) continue;
                String idDoLog = colunas[2].trim();
                if (idDoLog.equals(sessionId)) {
                    FIFO.add(colunas[3].trim());
                }
            }
        }
        resultado.addAll(FIFO);
        return resultado;
    }

    @Override
    public List<Alerta> priorizarAlertas(String arquivo, int n) throws IOException { // Desafio 3
        if (n <= 0) {return new ArrayList<>();}
        PriorityQueue<Alerta> fila = new PriorityQueue<>((a, b) -> Integer.compare(b.getSeverityLevel(), a.getSeverityLevel()));
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo)))
        {
            String linha;
            while ((linha = leitor.readLine()) != null)
            {
                if (linha.startsWith("TIMESTAMP")) continue;
                String[] col = linha.split(",", -1);
                if (col.length < 7) continue;
                long timestamp = Long.parseLong(col[0].trim());
                String userId = col[1].trim();
                String sessionId = col[2].trim();
                String actionType = col[3].trim();
                String target = col[4].trim();
                int severity = Integer.parseInt(col[5].trim());
                long bytes = 0;
                if (!col[6].trim().isEmpty()) {
                    bytes = Long.parseLong(col[6].trim());
                }
                Alerta alerta = new Alerta(timestamp, userId, sessionId, actionType, target, severity, bytes);
                fila.add(alerta);
            }
        }
        List<Alerta> resultado = new ArrayList<>();
        for (int i = 0; i < n && !fila.isEmpty(); i++) {resultado.add(fila.poll());}
        return resultado;
    }

    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) throws IOException {
        class EventoTransferencia {
            long timestamp;
            long bytes;

            public EventoTransferencia(long timestamp, long bytes) {
                this.timestamp = timestamp;
                this.bytes = bytes;
            }
        }

        List<EventoTransferencia> eventosTransferencia = new ArrayList<>();
        List<EventoTransferencia> eventos = new ArrayList<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            leitor.readLine(); // Pular o cabeçalho
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] colunas = linha.split(",", -1);
                if (colunas.length < 7) {continue;}
                if (!colunas[3].trim().equals("DATA_TRANSFER")) {continue;}
                String bytesStr = colunas[6].trim();
                if (bytesStr.isEmpty()) {continue;}
                try {
                    long timestamp = Long.parseLong(colunas[0].trim());
                    long bytes = Long.parseLong(bytesStr);
                    if (bytes > 0) {
                        eventos.add(new EventoTransferencia(timestamp, bytes));
                    }
                } catch (NumberFormatException e) {continue;}
            }
        }
        if (eventos.isEmpty()) {return new HashMap<>();}
        Map<Long, Long> picosTransferencia = new HashMap<>();
        Stack<EventoTransferencia> pilha = new Stack<>();
        for (int i = eventos.size() - 1; i >= 0; i--) {
            EventoTransferencia eventoAtual = eventos.get(i);
            while (!pilha.isEmpty() && pilha.peek().bytes <= eventoAtual.bytes) {pilha.pop();}
            if (!pilha.isEmpty()) {picosTransferencia.put(eventoAtual.timestamp, pilha.peek().timestamp);}
            pilha.push(eventoAtual);
        }
        return picosTransferencia;
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String origem, String destino) throws IOException { // Desafio 5
        //Agrupando os recursos por sessão
        Map<String, List<String>> recursosPorSessao = new HashMap<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            leitor.readLine();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] colunas = linha.split(",", -1);
                if (colunas.length < 5) continue;

                String sessionId = colunas[2].trim();
                String resource = colunas[4].trim();

                recursosPorSessao.putIfAbsent(sessionId, new ArrayList<>());
                recursosPorSessao.get(sessionId).add(resource);
            }

        }
        //Construção do grafo
        Map<String, Set<String>> grafo = new HashSet<>();
        for (List<String> caminhoSessao : recursosPorSessao.values()) {
            for (int i = 0, i < caminhoSessao.size() - 1, i++) {
                String u = caminhoSessao.get(i);
                String v = caminhoSessao.get(i + 1);

                if (!u.equals(v)) {
                    grafo.putIfAbsent(u, new HashSet<>());
                    grafo.get(u).add(v);
                }
            }
        }

        //Busca em profundidade (BFS)
        if (!grafo.containsKey(origem)) return Optional.empty();
        if (origem.equals(destino)) {
            List<String> caminhoUnico = new ArrayList<>();
            caminhoUnico.add(origem);
            return Optional.of(caminhoUnico);
        }

        Queue<String> fila = new ArrayDeque<>();
        Set<String> visitados = new HashSet<>();
        Map<String, String> predecessores = new HashMap<>();

        fila.add(origem);
        visitados.add(origem);
        boolean encontrouAlvo = false;

        while (!fila.isEmpty()) {
            String atual = fila.poll();

            if (atual.equals(destino)) {
                encontrouAlvo = true;
                break;
            }


            if (grafo.containsKey(atual)) {
                for (String vizinho :grafo.get(atual)) {
                    if (!visitados.contains(vizinho)) {
                        visitados.add(vizinho);
                        predecessores.put(vizinho, atual);
                        fila.add(vizinho);
                    }
                }
            }
        }

        //Reconstrução do caminho
        if (encontrouAlvo) {
            List<String> caminhoFinal = new ArrayList<>();
            String passo = destino;

            while (passo != null) {
                caminhoFinal.add(0, passo);
                passo = predecessores.get(passo);
            }
            return Optional.of(caminhoFinal);
        }
        return Optional.empty(); // TODO: Implementar
    }

}