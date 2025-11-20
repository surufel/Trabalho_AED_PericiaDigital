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
    public List<String> reconstruirLinhaTempo(String arquivo, String sessionId) throws IOException { // Desafio 2
        List<String> resultado = new ArrayList<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            linha = leitor.readLine();
            if (linha == null) {return resultado;}
            while ((linha = leitor.readLine()) != null)
            {
                if (linha.trim().isEmpty()){continue;}
                String[] col = linha.split(",", -1);
                if (col.length < 4){continue;}
                if (!col[2].trim().equals(sessionId)){continue;}
                resultado.add(col[3].trim());
            }
        }
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

    @Override
    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) throws IOException { // Desafio 4
        Map<Long, Long> picos = new HashMap<>();
        List<EventoTransferencia> eventos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] colunas = linha.split(",");
                long timestamp = Long.parseLong(colunas[0]);
                long bytes = Long.parseLong(colunas[6]);

                if (bytes > 0) {
                    eventos.add(new EventoTransferencia(timestamp, bytes));
                }
            }
        }

        Stack<EventoTransferencia> pilha = new Stack<>();

        for (int i = eventos.size() - 1; i >= 0; i--) {
            EventoTransferencia eventoAtual = eventos.get(i);

            while (!pilha.isEmpty() && pilha.peek().bytes <= eventoAtual.bytes) {
                pilha.pop();
            }

            if (!pilha.isEmpty()) {
                picos.put(eventoAtual.timestamp, pilha.peek().timestamp);
            }

            pilha.push(eventoAtual);
        }

        return picos;
    }

    private static class EventoTransferencia {
        long timestamp;
        long bytes;

        EventoTransferencia(long timestamp, long bytes) {
            this.timestamp = timestamp;
            this.bytes = bytes;
        }
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String origem, String destino) throws IOException { // Desafio 5
        // Implementar usando BFS em grafo
        return Optional.empty(); // TODO: Implementar
    }
}