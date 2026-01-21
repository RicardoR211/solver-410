import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    /* Testes de matemática básica */

    @Test
    void testOperacoesBasicas(){
        // Testando se 2 + 2 = 4
        assertEquals(4.0, Main.operar(2, 2, Main.Op.ADD), 0.0001);
        assertEquals(25.0, Main.operar(5, 5, Main.Op.MUL), 0.0001);
    }

    @Test
    void testDivisaoPorZero(){
        // Deve retornar NaN e não travar o programa
        double resultado = Main.operar(5, 0, Main.Op.DIV);
        assertTrue(Double.isNaN(resultado), "Divisão por zero deve retornar NaN");
    }

    @Test
    void testPrecisaoDecimal() {
        // teste do caso (1 + 1/9) * 9
        // -> (1 + 0.111...) * 9 = 1.111... * 9 = 9.999... = 10

        // Faz 1 / 9
        double divisao = Main.operar(1, 9, Main.Op.DIV);

        // Faz 1 + (1/9)
        double soma = Main.operar(1, divisao, Main.Op.ADD);

        //Faz (1.111...) * 9
        double resultadoFinal = Main.operar(soma, 9, Main.Op.MUL);

        //Validando se é 10
        assertTrue(Main.is10(resultadoFinal), "A conta (1 + 1/9) * 9 deveria ser considerado 10");

        //Testes manuais
        assertTrue(Main.is10(10.0000001), "Deve aceitar pequenas variações (+)");
        assertTrue(Main.is10(9.9999999), "Deve aceitar pequenas variações (-)");
        assertFalse(Main.is10(10.0001), "Não deve aceitar variações grandes");
    }

    @Test
    void testRegraParentesesDisjuntos_Multiplicacao() {
        // Simula o caso: (A + B) * (C + D)
        // Isso cairia no Caminho 3 (formatarDireita)
        // Ops: +, *, +

        //Forçando a chamada de formatarDireita com esses operadores
        String resultado = Main.formatarDireita(1, 1, 1,1,
                Main.Op.ADD, Main.Op.MUL, Main.Op.ADD);

        // A lógica é que (A+B) dentro de * -> Precisa de parenteses ()
        // E na direita, (C+D) dentro de * -> Precisa também de parenteses ()
        // Resultado seria (1+1) * (1+1) -> 2 Parênteses -> NULL

        assertNull(resultado, "Deve retornar NULL para (A+B)*(C+D)");
    }

    @Test
    void testRegraParentesesDisjuntos_Divisao() {
        // O caso: (1+7) / (4/5)
        // Ops: +, /, /

        String resultado = Main.formatarDireita(1, 7,4,5,
                Main.Op.ADD, Main.Op.DIV, Main.Op.DIV);

        // A lógica é que (1 + 7) dentro de / -> Precisa de parenteses ()
        // Direita (4/5) está à direita de uma divisão / -> Precisa () (Chamei de "Traicoeiro" no main
        // Resultado seria (1+7) / (4/5) -> 2 parenteses -> NULL

        assertNull(resultado, "Deve retornar nul para (A+B)/(C/D)");
    }

    @Test
    void testCasoValido1199() {
        // Caso: (1 + 1/9) * 9 -> Válido (1 parêntese)
        // Caminho Meio: A op1 (B op2 C) op3 D

        String resultado = Main.formatarMeio(1,1,9,9,
                Main.Op.ADD, Main.Op.DIV, Main.Op.MUL);

        assertNotNull(resultado, "O resultado não deveria ser nulo" +
                "pois essa conta é válida");
        System.out.println("Formatação 1 1 9 9: " + resultado);
    }
}
