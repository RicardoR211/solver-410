# 🧮 Solver 4=10 (Four Equals Ten)

Solucionador automático e instantâneo para o jogo mobile **4=10**.
Este projeto combina a robustez da lógica desenvolvida originalmente em **Java** com a performance web moderna do **.NET 9 (Blazor)**.

🔗 **Acesse o projeto online:** [Solver 4=10 - Fast Solution](https://solver-410-gl0uy2s9d-ricardos-projects-85a16352.vercel.app/)

---

## 🚀 Funcionalidades

* **Resolução Instantânea:** Encontra todas as combinações matemáticas possíveis para os 4 números resultarem em 10.
* **Cópia Rápida:** Botão para copiar a solução com um clique.
* **Interface Responsiva:** Funciona bem em celulares e computadores.
* **PWA (Progressive Web App):** Roda inteiramente no lado do cliente.

## 🛠️ Tecnologias Utilizadas

### Backend / Lógica
* **Java:** Utilizado para desenvolvimento do algoritmo base, testes de lógica e validação das combinações matemáticas (backtracking/força bruta).
* **C# (.NET 9):** Implementação final da lógica rodando via WebAssembly.

### Frontend
* **Blazor WebAssembly:** Framework para rodar a aplicação Single Page (SPA) direto no navegador.
* **Bootstrap:** Estilização e layout responsivo.

### Infraestrutura
* **Vercel:** Hospedagem otimizada com CI/CD contínuo.

## 📦 Como Rodar Localmente

Pré-requisitos: Você precisa ter o [.NET SDK 9.0](https://dotnet.microsoft.com/download) instalado.

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/RicardoR211/solver-410.git](https://github.com/RicardoR211/solver-410.git)
    ```

2.  **Navegue até a pasta do projeto:**
    ```bash
    cd Solver410.Client/Solver410.Client
    ```

3.  **Rode a aplicação:**
    ```bash
    dotnet watch
    ```

## ⚙️ Detalhes de Deploy

O projeto utiliza **Blazor WebAssembly** puro. A lógica matemática foi portada para C# para permitir a execução *client-side* (sem necessidade de servidor backend ativo), garantindo resposta imediata ao usuário.

---

Feito com ☕ (Java) e C# por **Ricardo**.
