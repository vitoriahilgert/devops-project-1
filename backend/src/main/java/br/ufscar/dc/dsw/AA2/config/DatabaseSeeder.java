package br.ufscar.dc.dsw.AA2.config;

import br.ufscar.dc.dsw.AA2.models.Project;
import br.ufscar.dc.dsw.AA2.models.Strategy;
import br.ufscar.dc.dsw.AA2.models.User;
import br.ufscar.dc.dsw.AA2.models.enums.UserRoleEnum;
import br.ufscar.dc.dsw.AA2.repositories.ProjectRepository;
import br.ufscar.dc.dsw.AA2.repositories.StrategyRepository;
import br.ufscar.dc.dsw.AA2.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder, ProjectRepository projectRepository, StrategyRepository strategyRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setEmail("vihilgerttomasel@gmail.com");
                admin.setPassword(passwordEncoder.encode("password"));
                admin.setName("Vitória Admin");
                admin.setRole(UserRoleEnum.ADMIN);
                userRepository.save(admin);
                System.out.println("Usuário Admin 'vihilgerttomasel@gmail.com' cadastrado.");

                User tester1 = new User();
                tester1.setEmail("nandaq2003@gmail.com");
                tester1.setPassword(passwordEncoder.encode("password"));
                tester1.setName("Maria Fernanda");
                tester1.setRole(UserRoleEnum.TESTER);
                userRepository.save(tester1);
                System.out.println("Usuário Tester 'nandaq2003@gmail.com' cadastrado.");

                User tester2 = new User();
                tester2.setEmail("rodrigo@estudante.ufscar.br");
                tester2.setPassword(passwordEncoder.encode("password"));
                tester2.setName("Rodrigo");
                tester2.setRole(UserRoleEnum.TESTER);
                userRepository.save(tester2);
                System.out.println("Usuário Tester 'rodrigo@estudante.ufscar.br' cadastrado.");

                User tester3 = new User();
                tester3.setEmail("sakai@gmail.com");
                tester3.setPassword(passwordEncoder.encode("password"));
                tester3.setName("Pedro Sakai");
                tester3.setRole(UserRoleEnum.TESTER);
                userRepository.save(tester3);
                System.out.println("Usuário Tester 'sakai@gmail.com' cadastrado.");

                System.out.println("Seed de usuários executada com sucesso.");
            } else {
                System.out.println("Banco de dados já contém usuários. Seed de usuários ignorada.");
            }
            if (projectRepository.count() == 0) {
                User testerMaria = userRepository.findByEmail("nandaq2003@gmail.com").orElse(null);
                User testerRodrigo = userRepository.findByEmail("rodrigo@estudante.ufscar.br").orElse(null);

                Project project1 = new Project();
                project1.setName("ETv1");
                project1.setDescription("Exploratory testing em jogos 2D com foco em bugs graves");

                if (testerMaria != null) {
                    project1.addAllowedMember(testerMaria);
                }
                if (testerRodrigo != null) {
                    project1.addAllowedMember(testerRodrigo);
                }

                projectRepository.save(project1);
                System.out.println("Projeto 'ETv1' cadastrado com testadores associados.");
            } else {
                System.out.println("Banco de dados já contém projetos. Seed de projetos ignorada.");
            }
            if (strategyRepository.count() == 0) {
                Strategy strategy1 = new Strategy();
                strategy1.setName("Testes Funcionais");
                strategy1.setDescription("Verificam se as principais funcionalidades do jogo, como mecânicas, menus e sistemas de progressão, operam corretamente.");
                strategy1.setExamples(
                        "1. Verificação de Lógica de Missão: Executar uma missão do início ao fim, garantindo que todos os passos, diálogos e recompensas funcionem. \n" +
                                "2. Teste de Mecânicas Essenciais: Focar no ciclo principal de jogo, como pular, atacar e usar habilidades em diversas situações. \n" +
                                "3. Análise da Economia: Comprar/vender itens, verificar preços, testar o sistema de criação (crafting) e a integridade do inventário."
                );
                strategy1.setTips(
                        "Dica 1: Tenha um checklist das funcionalidades críticas para não esquecer de nada. \n" +
                                "Dica 2: Tente executar as ações em ordens diferentes da esperada para encontrar bugs de estado."
                );
                strategyRepository.save(strategy1);


                Strategy strategy2 = new Strategy();
                strategy2.setName("Testes de Desempenho");
                strategy2.setDescription("Avaliam a performance do jogo em diferentes dispositivos e configurações, garantindo uma experiência fluida.");
                strategy2.setExamples(
                        "1. Teste de Estresse: Ir para a área mais exigente do jogo (cidade grande, batalha com muitos inimigos) para monitorar a taxa de quadros (FPS) e procurar por travamentos. \n" +
                                "2. Teste de Longa Duração (Soak Test): Deixar o jogo rodando por 1-2 horas para detectar vazamentos de memória (memory leaks) que causam degradação gradual da performance."
                );
                strategy2.setTips(
                        "Dica 1: Utilize ferramentas de monitoramento de performance (como o MSI Afterburner ou o medidor de FPS nativo) para obter dados precisos. \n" +
                                "Dica 2: Anote a localização e as ações exatas que causam quedas de FPS para facilitar a reprodução do bug."
                );
                strategyRepository.save(strategy2);


                Strategy strategy3 = new Strategy();
                strategy3.setName("Testes de Compatibilidade");
                strategy3.setDescription("Verificam se o jogo funciona adequadamente em diversas plataformas, sistemas operacionais e configurações de hardware.");
                strategy3.setExamples(
                        "1. Validação de Configurações Gráficas: Alternar entre todas as predefinições (Baixo, Médio, Alto) e procurar por artefatos visuais ou texturas ausentes. \n" +
                                "2. Teste de Múltiplas Resoluções: Executar o jogo em diferentes resoluções (Full HD, 4K) e proporções (16:9, 21:9) para verificar se a interface (UI) se adapta corretamente."
                );
                strategy3.setTips(
                        "Dica 1: Se não tiver vários monitores, muitos sistemas operacionais permitem simular outras resoluções nas configurações de vídeo. \n" +
                                "Dica 2: Reinicie o jogo após aplicar mudanças drásticas de configuração gráfica para garantir que elas persistam."
                );
                strategyRepository.save(strategy3);


                Strategy strategy4 = new Strategy();
                strategy4.setName("Testes de Usabilidade");
                strategy4.setDescription("Focam na experiência do jogador, buscando identificar obstáculos na interface do usuário e na curva de aprendizado.");
                strategy4.setExamples(
                        "1. Avaliação da Experiência Inicial (FTUE): Jogar os primeiros 30 minutos como um jogador novo, avaliando a clareza do tutorial e o engajamento inicial. \n" +
                                "2. Auditoria de Menus: Tentar realizar tarefas comuns (equipar item, salvar jogo) e medir a facilidade e o número de cliques, identificando frustrações."
                );
                strategy4.setTips(
                        "Dica 1: Anote seus pensamentos e sentimentos em tempo real. 'Fiquei confuso aqui', 'Isso foi frustrante', 'Não entendi para que serve este menu'. \n" +
                                "Dica 2: Evite usar conhecimento prévio do jogo. Tente pensar genuinamente como alguém que nunca viu o jogo antes."
                );
                strategyRepository.save(strategy4);


                Strategy strategy5 = new Strategy();
                strategy5.setName("Testes de Acessibilidade");
                strategy5.setDescription("Garantem que o jogo seja jogável por pessoas com diferentes tipos de limitações ou deficiências.");
                strategy5.setExamples(
                        "1. Verificação de Legendas e Daltonismo: Ativar os modos para daltônicos e testar todas as opções de customização de legendas (tamanho, cor, fundo). \n" +
                                "2. Teste de Remapeamento de Controles: Remapear as ações principais para teclas/botões diferentes e jogar para garantir que não há conflitos."
                );
                strategy5.setTips(
                        "Dica 1: Teste os extremos das configurações. Aumente o tamanho da legenda ao máximo e ao mínimo. \n" +
                                "Dica 2: Ao remapear controles, tente criar combinações ilógicas para ver se o jogo lida bem com isso (ex: mapear duas ações importantes para o mesmo botão)."
                );
                strategyRepository.save(strategy5);


                Strategy strategy6 = new Strategy();
                strategy6.setName("Testes Manuais");
                strategy6.setDescription("Realizados por testadores que jogam o jogo para avaliar diversos aspectos, incluindo a jogabilidade e a experiência geral.");
                strategy6.setExamples(
                        "1. Sessão de Teste Exploratório (Ad-Hoc): Jogar livremente, sem roteiro, tentando quebrar a sequência de eventos e alcançar lugares inacessíveis. \n" +
                                "2. Teste de Persona: Adotar um estilo de jogo específico (ex: 'pacifista', 'apressado') para descobrir bugs em caminhos menos comuns do jogo."
                );
                strategy6.setTips(
                        "Dica 1: Pense 'O que não deveria ser possível fazer aqui?'. E tente fazer. \n" +
                                "Dica 2: Combine habilidades e itens de formas estranhas. Interrompa animações com outras ações (pular, esquivar)."
                );
                strategyRepository.save(strategy6);


                Strategy strategy7 = new Strategy();
                strategy7.setName("Testes Automatizados");
                strategy7.setDescription("Utilizam scripts para executar testes repetitivos e garantir que as funcionalidades existentes não sejam afetadas por novas alterações.");
                strategy7.setExamples(
                        "1. Execução de Suíte de Regressão: Rodar scripts que verificam automaticamente as funcionalidades críticas após uma nova alteração no código. \n" +
                                "2. Validação de Build (BVT): Executar um script rápido que verifica se a nova compilação do jogo ao menos inicia e carrega o menu principal."
                );
                strategy7.setTips(
                        "Dica 1: Esta estratégia é geralmente executada por um engenheiro de QA em automação. O papel do testador manual aqui é analisar os relatórios de falha gerados. \n" +
                                "Dica 2: Se um teste automatizado falhar, o próximo passo é tentar reproduzir o erro manualmente para entender a causa raiz."
                );
                strategyRepository.save(strategy7);
            } else {
                System.out.println("Banco de dados já contém estratégias. Seed de estratégias ignorada.");
            }
        };
    }
}
