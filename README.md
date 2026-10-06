# Checkpoint 5 — Bug Hunt PetFiap

## Identificação

**Grupo:** Os Debugadores (Altere para o nome do seu grupo)

| Integrante       | RM       | Turma |
|------------------|----------|-------|
| Miguel Vanucci   | RM563491 | 2CCPG |
| Joao Vitor       | RM566541 | 2CCPG |
| Samuel da Silva  | RM564435 | 2CCPG |
| Henry dos Santos | RM565309 | 2CCPG |

| Campo | |
|---|---|
| **Total de bugs corrigidos** | 12 / 12 |
| **Total de ajustes de Clean Code** | 6 / 6 |
| **Total de testes novos escritos** | 6 / 6 |
| **Suíte final (Run As → JUnit Test)** | 26 testes, 0 falhas |

---

## Parte 1 — Bugs encontrados

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | O teste falhava esperando o nome do pet (ex: `Rex`), mas recebia `null`. | `AtendimentoBuilder.java`, método `comPet()` com sombreamento de variável (`petNome = petNome;`). | Adicionada a palavra-chave `this` (`this.petNome = petNome;`) para salvar na classe. | Encapsulamento / Palavra-chave `this`. |
| bug02 | O teste de validação de valores nulos no Builder não lançava exceção. | `AtendimentoBuilder.java`, método `construir()`. Não validava se os dados eram nulos. | Adicionado `if (petNome == null || petPorte == null)` lançando `IllegalArgumentException`. | Validação de estado / Exceções. |
| bug03 | A Factory devolvia um objeto `Banho` quando o teste pedia `TOSA`. | `AtendimentoFactory.java`, no bloco `switch`. O `case "TOSA"` instanciava um `Banho`. | Alterado de `new Banho(...)` para `new Tosa(...)`. | Padrão Factory / Instanciação. |
| bug04 | Os dados do pet vinham nulos na `ConsultaVeterinaria`. | `ConsultaVeterinaria.java`, construtor. Chamava `super();` vazio, perdendo os argumentos. | Alterado para `super(protocolo, petNome, petPorte, tutorNome, dataHora);`. | Herança / Construtores (`super`). |
| bug05 | Teste acusava que o Singleton estava a criar instâncias diferentes. | `GeradorProtocolo.java`, no `getInstancia()`. Retornava a instância mas não a guardava na variável. | Adicionado `instancia = new GeradorProtocolo();` antes do return. | Padrão Singleton / Atributos estáticos. |
| bug06 | O sistema permitia agendar horários duplicados para o mesmo pet. | `AgendaService.java`, método `agendar()`. Comparava Strings e Datas usando o operador `==`. | Substituído o operador `==` pelo método `.equals()` para comparar os valores. | Comparação de Objetos. |
| bug07 | O controller recebia `null` em vez da exceção de erro. | `AgendaService.java`, método `buscarPorId()`. O `try-catch` "engolia" o erro gerado pelo `orElseThrow`. | Removido o bloco `try-catch`, deixando a exceção subir para o controller. | Tratamento de Exceções. |
| bug08 | O banco não conseguiria criar novos registos (Code Review). | `Atendimento.java`. O atributo `id` não tinha anotação para gerar valor automático. | Adicionado `@GeneratedValue(strategy = GenerationType.IDENTITY)` no `@Id`. | JPA / Mapeamento. |
| bug09 | Teste novo de preços do Banho falhava (inversão de valores). | `Banho.java`, método `calcularPreco()`. A lógica dos preços por porte estava invertida. | Ajustado para retornar 60 se "PEQUENO", 80 se "MEDIO" e 100 se grande. | Regras de Negócio / Polimorfismo. |
| bug10 | Teste novo revelou que era possível agendar no passado. | `AtendimentoBuilder.java`, método `construir()`. Faltava validação de data. | Adicionado `if (dataHora.isBefore(LocalDateTime.now()))` lançando exceção. | Regras de Negócio. |
| bug11 | Teste novo revelou que a Tosa durava 30 min em vez de 60. | `Tosa.java`. O método `getDuracaoMinutos` tinha um parâmetro `(String porte)` inútil, criando sobrecarga. | Removido o parâmetro e adicionada a anotação `@Override`. | Sobrescrita vs Sobrecarga. |
| bug12 | Teste novo permitia cancelar um atendimento "CONCLUIDO". | `Atendimento.java`, método `cancelar()`. Mudava o status sem conferir o status anterior. | Adicionado `if (!"AGENDADO".equals(status))` para barrar o cancelamento. | Transição de Estados. |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | `AtendimentoFactory.java`, método `criar()`. | Nomenclatura ruim. Letras curtas como `p`, `t`, `n` não explicam o que são. | Renomeei para nomes claros como `protocolo`, `tipo`, `petNome`. |
| clean02 | `AtendimentoController.java`, no fim do arquivo. | Código morto. Havia um método comentado para "uso futuro". | Apaguei o método inteiro para manter o código limpo. |
| clean03 | `GeradorProtocolo.java`, no construtor. | Uso indevido de console (`System.out.println`) prejudica o servidor. | Apaguei o print. Em produção usaríamos logs adequados. |
| clean04 | `AgendaService.java`, método `agendar()`. | Uso indevido de console para recibos. | Apaguei o `System.out.println`. |
| clean05 | `AtendimentoController.java`, nos retornos. | *Magic Numbers*. Números literais como 201 e 409 soltos no código. | Troquei por constantes do Spring como `HttpStatus.CREATED`. |
| clean06 | `AgendaService.java`, no `for` de `agendar()`. | Nomes de variáveis sem sentido (`doPet` e `a`). | Renomeei para `atendimentosDoPet` e `atendimentoCadastrado`. |

## Parte 3 — Testes novos (regras que estavam sem cobertura)

| # | Teste escrito (classe.método) | Regra coberta | Resultado ao escrever (vermelho/verde) |
|---|---|---|---|
| teste01 | `BanhoTest.deveCalcularPrecosCorretosPorPorte` | Valida se os valores 60, 80 e 100 são aplicados aos portes certos. | Vermelho (revelou bug 09, preços invertidos). |
| teste02 | `AtendimentoBuilderTest.deveRecusarAgendamentoNoPassado` | Garante que recusa agendamento com datas passadas. | Vermelho (revelou bug 10, faltava a validação). |
| teste03 | `TosaTest.deveDurar60Minutos` | Garante que a duração da Tosa é 60 minutos e não 30. | Vermelho (revelou bug 11, erro de sobrecarga). |
| teste04 | `AgendaServiceTest.deveRecusarCancelamentoDeAtendimentoConcluido` | Recusa cancelar se já estiver concluído. | Vermelho (revelou bug 12, faltava validar status). |
| teste05 | `AgendaServiceTest.deveCancelarAtendimentoAgendado` | Confirma o cancelamento de um atendimento agendado. | Verde (a regra básica já funcionava). |
| teste06 | `ConsultaVeterinariaTest.deveCustar150ReaisIndependenteDoPorte` | Valida que a consulta sempre custa 150. | Verde (o retorno já era 150 estático). |

---

## Parte 4 — Perguntas de reflexão

### 1. A suíte como contrato (Aula 15)
Eu usei as mensagens de erro (como `expected: <Rex> but was: <null>`) para saber exatamente qual variável estava com problema. Isso me levou direto ao erro do `this` no `AtendimentoBuilder`. A vantagem da suíte é que ela roda em segundos e aponta a linha exata do erro, enquanto testar "na mão" com curl exige subir o servidor, o banco de dados e tentar adivinhar onde o erro aconteceu pelo código de erro na web.

### 2. Mock e injeção de dependência (Aulas 13 a 15)
Em produção, o Spring usa o `@Autowired` para injetar o repositório real conectado ao banco de dados. Nos testes, o `@Mock` cria um repositório "falso" na memória, e o `@InjectMocks` o coloca dentro do `AgendaService`. Por isso o teste roda rápido e sem banco: nós isolamos o serviço da internet e mandamos o falso repositório responder o que queremos só para validar a lógica de negócio.

### 3. `==` vs `.equals()` (Aula 7)
O operador `==` compara apenas se os objetos estão no mesmo endereço de memória. Já o `.equals()` compara o valor (o conteúdo) deles. O `==` pode funcionar por sorte com textos simples (como a palavra "Rex" por causa do pool do Java), mas falha com objetos diferentes como o `LocalDateTime`. Ao trocar para `.equals()` no conflito de agenda, o código passou a comparar as datas e textos corretamente, impedindo as duplicidades.

### 4. Sobrescrita vs sobrecarga (Aula 7)
Sobrescrita (override) é substituir exatamente o mesmo método da classe pai. Sobrecarga (overload) é criar um método novo com o mesmo nome, mas recebendo parâmetros diferentes. No bug da `Tosa`, o estagiário adicionou um parâmetro `(String porte)`, criando um método novo que nunca era chamado em vez de substituir o tempo de 30 minutos. Se ele tivesse usado `@Override`, o Java teria alertado o erro na hora.

### 5. Singleton manual vs bean do Spring (Aula 14)
O Singleton garante que só exista um único objeto rodando no sistema todo. O nosso `GeradorProtocolo` feito à mão tinha um erro porque criava um objeto novo sem salvar na variável, gerando números repetidos de protocolo. Já o `AgendaService` não tem esse risco porque o Spring transforma todas as classes anotadas com `@Service` em Singletons automaticamente de forma segura quando a aplicação sobe.

### 6. Cobertura de testes: onde parar? (Aula 15)
Sim, vale a pena manter os testes verdes para evitar regressão (impedir que alguém quebre essa regra no futuro sem querer). Num projeto real com prazo apertado, tentar 100% de cobertura é perda de tempo porque você acaba testando coisas inúteis. Eu priorizaria testar o "caminho feliz" das regras de negócio principais (como agendar) e os erros mais críticos que possam travar ou dar prejuízo à aplicação.

---
