import java.util.*;

public class Main {

    /* --- CONFIGURAÇÃO --- */
    // Enum simples para as operações
    enum Op {
        ADD('+'), SUB('-'), MUL('*'), DIV('/');
        final char symbol;

        Op(char symbol) {
            this.symbol = symbol;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== 4=10 SOLVER ===");

        /* --- Leitura dos dados ---*/
        System.out.println("Digite os 4 números:");
        int[] input = new int[4];
        try {
            for (int i = 0; i < 4; i++) {
                input[i] = scanner.nextInt();

                // Como a regra do jogo diz  tem q ser entre 0-9
                if (input[i] < 0 || input[i] > 9) {
                    System.out.println("Erro: Apenas dígitos de 0 a 9 são permitidos! (Você digitou " + input[i] + ")");
                    return; // Encerra o programa
                }
            }
        } catch (Exception e) {
            System.out.println("Erro: Digite apenas números inteiros.");
            return;
        }

        // Leitura dos Operadores Proibidos (Ex: "+-" para bloquear some e subtração)
        System.out.println("Digite os operadores PROIBIDOS juntos (ex" +
                " +-) ou enter para continuar");
        scanner.nextLine();
        String proibidosInput = scanner.nextLine();

        long start = System.nanoTime();

        /* --- Filtro de operadores --- */
        List<Op> opsPermitidos = new ArrayList<>();
        for(Op op : Op.values()) {
            // Só adiciona se o símbolo não estiver na lista de proibidos
            if(proibidosInput.indexOf(op.symbol) == -1) {
                opsPermitidos.add(op);
            }
        }

        //Converte para array para ser rápido no loop
        Op[] ops = opsPermitidos.toArray(new Op[0]);

        if (ops.length == 0) {
            System.out.println("Erro: Você proibiu todos os operadores!");
            return;
        }

        /* --- Geração e calculo --- */

        //Gera perumtações únicas para evitar recalcular números repetidos (ex: 5 5 5 5)
        Set<List<Integer>> permutacoesUnicas = new HashSet<>();
        permutar(input, 4, permutacoesUnicas);

        Set<String> solucoes = new HashSet<>();

        // Loop triplo aninhado para testar combinações de operadores
        for (List<Integer> nums : permutacoesUnicas) {
            double a = nums.get(0), b = nums.get(1), c = nums.get(2), d = nums.get(3);

            for (Op op1 : ops) {
                for (Op op2 : ops) {
                    for (Op op3 : ops) {

                        // CAMINHO 1: Prioridade Esquerda -> ((a . b) . c) . d
                        double res1 = operar(operar(operar(a, b, op1), c, op2), d, op3);
                        if (is10(res1)) {
                            solucoes.add(formatarEsquerda(a, b, c, d, op1, op2, op3));
                        }

                        // CAMINHO 2: Prioridade Meio -> a . (b . c) . d
                        double meio = operar(b, c, op2);
                        double res2 = operar(operar(a, meio, op1), d, op3);
                        if (is10(res2)) {
                            solucoes.add(formatarMeio(a, b, c, d, op1, op2, op3));
                        }

                        // CAMINHO 3: Prioridade Direita -> a . b . (c . d)
                        double fim = operar(c, d, op3);
                        double res3 = operar(operar(a, b, op1), fim, op2);
                        if (is10(res3)) {
                            solucoes.add(formatarDireita(a, b, c, d, op1, op2, op3));
                        }
                    }
                }
            }
        }

        long end = System.nanoTime();

        // --- 4. EXIBIÇÃO ---
        System.out.println("\n---------------------------");
        System.out.println("Encontradas: " + solucoes.size());
        System.out.println("Tempo: " + (end - start) / 1000 + " µs"); // Microssegundos
        System.out.println("---------------------------");

        // Ordena alfabeticamente para ficar organizado
        List<String> listaFinal = new ArrayList<>(solucoes);
        Collections.sort(listaFinal);

        for (String s : listaFinal) {
            System.out.println(s);
        }
    }

    /* --- LÓGICA MATEMÁTICA --- */

    //Faz as contas e protege contra divisão por zero
    private static double operar(double a, double b, Op op) {
        switch (op) {
            case ADD: return a + b;
            case SUB: return a - b;
            case MUL: return a * b;
            case DIV:
                //Se for 0 ele retorna um NaN ao invés de travar
                if (Math.abs(b) < 0.000001) return  Double.NaN;
                return a / b;
            default: return 0;
        }
    }

    //Verifica se é 10.
    private static boolean is10(double val) {
        //Se ele for beeeem próximo de 10. Trata o erro de ponto flutuante.
        return Math.abs(val - 10.0) < 0.000001;
    }

    /* --- FORMATAÇÃO INTELIGENTE (JSON / VISUAL) --- */

    // Verifica se "opDentro" precisa de parênteses quando está dentro de "opFora"
    // Ex: (4+5)*2 -> SIM (+ é mais fraco que *)
    // Ex: (4*5)+2 -> não (* é forte, não precisa proteger)
    private static boolean precisaParenteses(Op opDentro, Op opFora, boolean isDireita) {
        //Força Multiplicação/Divisão ganhar de Soma/Subtração
        boolean dentroFraco = (opDentro == Op.ADD || opDentro == Op.SUB);
        boolean foraForte = (opFora == Op.MUL || opFora == Op.DIV);
        if (dentroFraco && foraForte) return true;

        //Lado direito "traiçoeiro" da subtração e divisão
        if(isDireita) {
            //Se fora é Menos, e dentro é Menos ou Maios -> protege
            // Ex: 10 - (2 + 3) ou 10 - (5 - 2)
            if (opFora == Op.SUB && (opDentro == Op.ADD || opDentro == Op.SUB)) {
                return true;
            }
            //Se fora é Dividir, e dentro é Dividir ou Multiplicar -> protege.
            // Ex: 20 / (10 / 2) -> 20 / 5 = 4. Sem parenteses daria 1.
            if (opFora == Op.DIV && (opDentro == Op.MUL || opDentro == Op.DIV)) {
                return true;
            }
        }

        return false;
    }

    // Formata: ((A op1) op2 C) op3 D
    private static String formatarEsquerda(double a, double b,
                                           double c, double  d,
                                           Op o1, Op o2, Op o3) {
        // (A op1 B) está á Esquerda de op2
        String p1 = String.format("%d %c %d", (int)a, o1.symbol, (int)b);
        if (precisaParenteses(o1,o2,false)) p1 = "(" + p1 + ")";

        // (Resultado[p1] op2 C) está à esquerda de op3
        String p2 = String.format("%s %c %d", p1, o2.symbol, (int)c);
        if(precisaParenteses(o2, o3, false)) p2 = "(" + p2 + ")";

        return String.format("%s %c %d", p2, o3.symbol, (int)d);
    }

    // Formata: A op1 (B op2 C) op3 D
    private static String formatarMeio(double a, double b,
                                       double c, double d,
                                       Op o1, Op o2, Op o3) {
        String meio = String.format("%d %c %d", (int)b, o2.symbol, (int)c);

        // O grupo do meio (B op2 C) interage com DOIS operadores:
        // Ele está à direita de o1
        boolean pEsquerda = precisaParenteses(o2, o1, true);
        // Ele está à esquerda de o3
        boolean pDireita = precisaParenteses(o2, o3, false);

        if(pEsquerda || pDireita) meio = "(" + meio + ")";

        //Contruindo a parte da esquerda: A op1 (meio)
        String parteEsquerda = String.format("%d %c %s", (int)a,
                o1.symbol, meio);

        if(precisaParenteses(o1, o3, false)) {
            parteEsquerda = "(" + parteEsquerda + ")";
        }

        return String.format("%s %c %d", parteEsquerda,
                o3.symbol, (int)d);
    }

    // Formata: A op1 B op2 (C op3 D)
    private static String formatarDireita(double a, double b,
                                          double c, double d,
                                          Op o1, Op o2, Op o3) {
        // (C op3 D) está à Direita de op2
        String fim = String.format("%d %c %d", (int)c, o3.symbol, (int)d);
        if (precisaParenteses(o3, o2, true)) fim = "(" + fim + ")";

        // (A op1 B) está à Esquerda de op2
        String inicio = String.format("%d %c %d", (int)a, o1.symbol,
                (int)b);
        if (precisaParenteses(o1, o2, false)) inicio = "(" + inicio + ")";

        return String.format("%s %c %s", inicio, o2.symbol, fim);
    }

    /* --- PERMUTAÇÃO --- */
    private static void permutar(int[] arr, int n, Set<List<Integer>> unicos) {
        if (n == 1) {
            List<Integer> l = new ArrayList<>();
            for(int i : arr) l.add(i);
            unicos.add(l);
        } else {
            for (int i = 0; i < n - 1; i++) {
                permutar(arr, n - 1, unicos);
                int j = (n % 2 == 0) ? i : 0;
                int temp = arr[j];
                arr[j] = arr[n-1];
                arr[n-1] = temp;
            }
            permutar(arr, n - 1, unicos);
        }
    }
}
