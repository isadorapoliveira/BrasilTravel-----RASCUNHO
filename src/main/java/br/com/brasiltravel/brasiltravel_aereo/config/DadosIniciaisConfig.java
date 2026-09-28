package br.com.brasiltravel.brasiltravel_aereo.config;

import br.com.brasiltravel.brasiltravel_aereo.model.Aeroporto;
import br.com.brasiltravel.brasiltravel_aereo.model.CompanhiaAerea;
import br.com.brasiltravel.brasiltravel_aereo.model.Destino;
import br.com.brasiltravel.brasiltravel_aereo.model.SolicitacaoViagem;
import br.com.brasiltravel.brasiltravel_aereo.model.SolicitacaoVoo;
import br.com.brasiltravel.brasiltravel_aereo.model.Usuario;
import br.com.brasiltravel.brasiltravel_aereo.model.Voo;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.ClasseVoo;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.StatusSolicitacao;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.TipoUsuario;
import br.com.brasiltravel.brasiltravel_aereo.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.CompanhiaAereaRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.DestinoRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.SolicitacaoViagemRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.SolicitacaoVooRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.UsuarioRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.VooRepository;
import br.com.brasiltravel.brasiltravel_aereo.service.SenhaService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciaisConfig implements CommandLineRunner {

    private static final LocalDate DATA_REFERENCIA = LocalDate.of(2026, 9, 27);
    private static final LocalDate INICIO_HISTORICO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIM_HISTORICO = DATA_REFERENCIA;

    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;
    private final AeroportoRepository aeroportoRepository;
    private final CompanhiaAereaRepository companhiaAereaRepository;
    private final VooRepository vooRepository;
    private final SolicitacaoViagemRepository solicitacaoViagemRepository;
    private final SolicitacaoVooRepository solicitacaoVooRepository;
    private final SenhaService senhaService;

    private int sequencialVoo = 1;

    @Value("${app.dados-exemplo.recriar:true}")
    private boolean recriarDadosExemplo;

    public DadosIniciaisConfig(UsuarioRepository usuarioRepository,
                               DestinoRepository destinoRepository,
                               AeroportoRepository aeroportoRepository,
                               CompanhiaAereaRepository companhiaAereaRepository,
                               VooRepository vooRepository,
                               SolicitacaoViagemRepository solicitacaoViagemRepository,
                               SolicitacaoVooRepository solicitacaoVooRepository,
                               SenhaService senhaService) {
        this.usuarioRepository = usuarioRepository;
        this.destinoRepository = destinoRepository;
        this.aeroportoRepository = aeroportoRepository;
        this.companhiaAereaRepository = companhiaAereaRepository;
        this.vooRepository = vooRepository;
        this.solicitacaoViagemRepository = solicitacaoViagemRepository;
        this.solicitacaoVooRepository = solicitacaoVooRepository;
        this.senhaService = senhaService;
    }

    @Override
    public void run(String... args) {
        Usuario admin = buscarOuCriarUsuario("Administrador BrasilTravel", "admin@brasiltravel.com", "Admin123", "000.000.000-00", "(47) 3433-0000", TipoUsuario.ADMIN);

        Usuario[] clientes = new Usuario[] {
                buscarOuCriarUsuario("Cliente Demonstração", "cliente@brasiltravel.com", "Cliente123", "111.111.111-11", "(47) 99999-1111", TipoUsuario.CLIENTE),
                buscarOuCriarUsuario("Mariana Oliveira", "mariana@brasiltravel.com", "Cliente123", "222.222.222-22", "(11) 98888-2222", TipoUsuario.CLIENTE),
                buscarOuCriarUsuario("Rafael Souza", "rafael@brasiltravel.com", "Cliente123", "333.333.333-33", "(21) 97777-3333", TipoUsuario.CLIENTE),
                buscarOuCriarUsuario("Camila Ferreira", "camila@brasiltravel.com", "Cliente123", "444.444.444-44", "(41) 96666-4444", TipoUsuario.CLIENTE),
                buscarOuCriarUsuario("Bruno Almeida", "bruno@brasiltravel.com", "Cliente123", "555.555.555-55", "(31) 95555-5555", TipoUsuario.CLIENTE),
                buscarOuCriarUsuario("Fernanda Costa", "fernanda@brasiltravel.com", "Cliente123", "666.666.666-66", "(81) 94444-6666", TipoUsuario.CLIENTE),
                buscarOuCriarUsuario("Lucas Martins", "lucas@brasiltravel.com", "Cliente123", "777.777.777-77", "(85) 93333-7777", TipoUsuario.CLIENTE),
                buscarOuCriarUsuario("Patrícia Lima", "patricia@brasiltravel.com", "Cliente123", "888.888.888-88", "(62) 92222-8888", TipoUsuario.CLIENTE)
        };

        if (!recriarDadosExemplo && destinoRepository.count() > 0) {
            return;
        }

        limparDadosComerciais();
        sequencialVoo = 1;

        Destino joinville = destino("Joinville", "SC", "Origem regional para conexões nacionais pelo Sul e Sudeste.");
        Destino saoPaulo = destino("São Paulo", "SP", "Principal centro de conexões aéreas do país.");
        Destino rio = destino("Rio de Janeiro", "RJ", "Destino nacional de lazer, eventos e negócios.");
        Destino salvador = destino("Salvador", "BA", "Destino do Nordeste com forte demanda turística.");
        Destino foz = destino("Foz do Iguaçu", "PR", "Destino de natureza, lazer e fronteira internacional.");
        Destino brasilia = destino("Brasília", "DF", "Destino frequente para negócios e compromissos institucionais.");
        Destino recife = destino("Recife", "PE", "Destino corporativo e turístico do Nordeste.");
        Destino fortaleza = destino("Fortaleza", "CE", "Destino de lazer com alta procura em temporada.");
        Destino curitiba = destino("Curitiba", "PR", "Destino regional para negócios e conexões no Sul.");
        Destino beloHorizonte = destino("Belo Horizonte", "MG", "Destino empresarial e cultural do Sudeste.");
        Destino portoAlegre = destino("Porto Alegre", "RS", "Destino estratégico no Sul do Brasil.");
        Destino manaus = destino("Manaus", "AM", "Destino da região Norte com procura turística e empresarial.");
        Destino goiania = destino("Goiânia", "GO", "Destino do Centro-Oeste com fluxo comercial constante.");
        Destino natal = destino("Natal", "RN", "Destino de lazer do Nordeste com procura sazonal.");

        Aeroporto[] aeroportos = new Aeroporto[] {
                aeroporto(joinville, "JOI", "Aeroporto de Joinville", "Joinville - SC"),
                aeroporto(saoPaulo, "GRU", "Aeroporto Internacional de Guarulhos", "Guarulhos - SP"),
                aeroporto(saoPaulo, "CGH", "Aeroporto de Congonhas", "São Paulo - SP"),
                aeroporto(rio, "GIG", "Aeroporto Internacional do Galeão", "Rio de Janeiro - RJ"),
                aeroporto(rio, "SDU", "Aeroporto Santos Dumont", "Rio de Janeiro - RJ"),
                aeroporto(salvador, "SSA", "Aeroporto Internacional de Salvador", "Salvador - BA"),
                aeroporto(foz, "IGU", "Aeroporto Internacional de Foz do Iguaçu", "Foz do Iguaçu - PR"),
                aeroporto(brasilia, "BSB", "Aeroporto Internacional de Brasília", "Brasília - DF"),
                aeroporto(recife, "REC", "Aeroporto Internacional do Recife", "Recife - PE"),
                aeroporto(fortaleza, "FOR", "Aeroporto Internacional de Fortaleza", "Fortaleza - CE"),
                aeroporto(curitiba, "CWB", "Aeroporto Internacional Afonso Pena", "Curitiba - PR"),
                aeroporto(beloHorizonte, "CNF", "Aeroporto Internacional de Confins", "Confins - MG"),
                aeroporto(portoAlegre, "POA", "Aeroporto Internacional Salgado Filho", "Porto Alegre - RS"),
                aeroporto(manaus, "MAO", "Aeroporto Internacional Eduardo Gomes", "Manaus - AM"),
                aeroporto(goiania, "GYN", "Aeroporto de Goiânia", "Goiânia - GO"),
                aeroporto(natal, "NAT", "Aeroporto Internacional de Natal", "São Gonçalo do Amarante - RN")
        };

        CompanhiaAerea[] companhias = new CompanhiaAerea[] {
                companhia("Azul Linhas Aéreas", "AD", "https://www.voeazul.com.br", "0800 887 1118"),
                companhia("GOL Linhas Aéreas", "G3", "https://www.voegol.com.br", "0300 115 2121"),
                companhia("LATAM Airlines Brasil", "LA", "https://www.latamairlines.com", "0300 570 5700"),
                companhia("BrasilTravel Connect", "BT", "https://www.brasiltravel.com.br", "(47) 3433-0000")
        };

        List<Voo> voosHistoricos = gerarMalhaAerea(aeroportos, companhias, INICIO_HISTORICO, FIM_HISTORICO, 3, false);
        List<Voo> voosFuturos = gerarMalhaAerea(aeroportos, companhias, DATA_REFERENCIA, DATA_REFERENCIA.plusDays(30), 1, true);
        gerarSolicitacoesDemonstracao(clientes, voosHistoricos);

        System.out.println("Dados comerciais BrasilTravel Aéreo carregados. Admin: " + admin.getEmail()
                + " | Voos históricos: " + voosHistoricos.size()
                + " | Voos futuros: " + voosFuturos.size());
    }

    private List<Voo> gerarMalhaAerea(Aeroporto[] aeroportos, CompanhiaAerea[] companhias, LocalDate inicio, LocalDate fim, int intervaloDias, boolean ativo) {
        List<Voo> voos = new ArrayList<>();
        for (LocalDate data = inicio; !data.isAfter(fim); data = data.plusDays(intervaloDias)) {
            for (int i = 0; i < aeroportos.length; i++) {
                for (int j = 0; j < aeroportos.length; j++) {
                    if (i == j) {
                        continue;
                    }
                    CompanhiaAerea companhia = companhias[Math.floorMod(i * 31 + j * 7 + data.getDayOfYear(), companhias.length)];
                    int hora = 6 + Math.floorMod(i * 2 + j + data.getDayOfMonth(), 13);
                    int minuto = Math.floorMod(i + j + data.getDayOfMonth(), 2) * 30;
                    int duracaoHoras = 1 + Math.abs(i - j) % 4;
                    int duracaoMinutos = Math.floorMod(i + j, 3) * 15;
                    BigDecimal preco = BigDecimal.valueOf(260 + (Math.abs(i - j) * 52L) + (data.getMonthValue() * 14L) + (ativo ? 45L : 0L));
                    ClasseVoo classe = (sequencialVoo % 8 == 0) ? ClasseVoo.EXECUTIVA : ClasseVoo.ECONOMICA;
                    // Capacidade operacional exibida pelo site: representa a cota comercial
                    // disponível para a agência, não a capacidade física total da aeronave.
                    // Mantida propositalmente baixa para tornar o relatório de ocupação útil na demonstração.
                    int capacidadeTotal = (classe == ClasseVoo.EXECUTIVA)
                            ? 6 + Math.floorMod(i + j + data.getDayOfYear(), 3)
                            : 8 + Math.floorMod(i * 3 + j * 5 + data.getDayOfYear(), 7);
                    int vagas = capacidadeTotal;
                    String numero = companhia.getCodigoIata() + String.format("%05d", sequencialVoo);

                    voos.add(voo(companhia, aeroportos[i], aeroportos[j], numero,
                            data.atTime(hora, minuto),
                            data.atTime(hora, minuto).plusHours(duracaoHoras).plusMinutes(duracaoMinutos),
                            preco.toString(), capacidadeTotal, vagas, classe, ativo));
                    sequencialVoo++;
                }
            }
        }
        return voos;
    }

    private void gerarSolicitacoesDemonstracao(Usuario[] clientes, List<Voo> voosHistoricos) {
        int criadas = 0;
        for (LocalDate data = INICIO_HISTORICO; !data.isAfter(FIM_HISTORICO.minusDays(6)); data = data.plusDays(3)) {
            final LocalDate dataAtual = data;
            List<Voo> voosDoDia = voosHistoricos.stream()
                    .filter(v -> v.getDataHoraPartida().toLocalDate().equals(dataAtual))
                    .toList();
            if (voosDoDia.isEmpty()) {
                continue;
            }

            for (int n = 0; n < 5; n++) {
                Voo ida = voosDoDia.get(Math.floorMod(data.getDayOfYear() * 13 + n * 29 + criadas * 7, voosDoDia.size()));
                Voo volta = encontrarVolta(voosHistoricos, ida);
                if (volta == null) {
                    continue;
                }

                Usuario cliente = clientes[criadas % clientes.length];
                int passageiros = 2 + Math.floorMod(criadas, 4);
                StatusSolicitacao st = statusDemonstracao(criadas);
                LocalDateTime criacao = data.minusDays(1).atTime(8 + Math.floorMod(n, 9), Math.floorMod(n * 10, 60));
                if (criacao.toLocalDate().isBefore(INICIO_HISTORICO)) {
                    criacao = INICIO_HISTORICO.atTime(9 + n, 0);
                }
                if (criacao.toLocalDate().isAfter(FIM_HISTORICO)) {
                    continue;
                }

                String observacoes = "Solicitação comercial de demonstração " + (criadas + 1) + ".";
                solicitacao(cliente, ida.getAeroportoDestino().getDestino(), ida.getDataHoraPartida().toLocalDate(), volta.getDataHoraPartida().toLocalDate(), st, observacoes, ida, volta, passageiros, criacao);
                criadas++;
            }
        }
    }

    private StatusSolicitacao statusDemonstracao(int indice) {
        int faixa = Math.floorMod(indice, 10);
        if (faixa <= 4) {
            return StatusSolicitacao.FINALIZADA;
        }
        if (faixa == 5) {
            return StatusSolicitacao.CONFIRMADA;
        }
        if (faixa == 6) {
            return StatusSolicitacao.CANCELADA;
        }
        if (faixa == 7) {
            return StatusSolicitacao.EM_ATENDIMENTO;
        }
        if (faixa == 8) {
            return StatusSolicitacao.PENDENTE_CLIENTE;
        }
        return StatusSolicitacao.AGUARDANDO_ATENDIMENTO;
    }

    private Voo encontrarVolta(List<Voo> voos, Voo ida) {
        return voos.stream()
                .filter(v -> v.getAeroportoOrigem().getIdAeroporto().equals(ida.getAeroportoDestino().getIdAeroporto()))
                .filter(v -> v.getAeroportoDestino().getIdAeroporto().equals(ida.getAeroportoOrigem().getIdAeroporto()))
                .filter(v -> v.getDataHoraPartida().isAfter(ida.getDataHoraPartida().plusDays(2)))
                .findFirst()
                .orElse(null);
    }

    private Usuario buscarOuCriarUsuario(String nome, String email, String senha, String cpf, String telefone, TipoUsuario tipo) {
        return usuarioRepository.findByEmailIgnoreCase(email).orElseGet(() -> usuarioRepository.save(new Usuario(
                nome,
                email,
                senhaService.gerarHash(senha),
                cpf,
                telefone,
                tipo
        )));
    }

    private void limparDadosComerciais() {
        solicitacaoVooRepository.deleteAllInBatch();
        solicitacaoViagemRepository.deleteAllInBatch();
        vooRepository.deleteAllInBatch();
        aeroportoRepository.deleteAllInBatch();
        companhiaAereaRepository.deleteAllInBatch();
        destinoRepository.deleteAllInBatch();
    }

    private Destino destino(String cidade, String estado, String descricao) {
        return destinoRepository.save(new Destino(cidade, estado, "Brasil", descricao, true));
    }

    private Aeroporto aeroporto(Destino destino, String codigo, String nome, String endereco) {
        return aeroportoRepository.save(new Aeroporto(destino, codigo, nome, endereco, true));
    }

    private CompanhiaAerea companhia(String nome, String codigo, String site, String telefone) {
        return companhiaAereaRepository.save(new CompanhiaAerea(nome, codigo, site, telefone, true));
    }

    private Voo voo(CompanhiaAerea companhia, Aeroporto origem, Aeroporto destino, String numero, LocalDateTime partida, LocalDateTime chegada, String preco, int capacidadeTotal, int vagas, ClasseVoo classe, boolean ativo) {
        return vooRepository.save(new Voo(
                companhia,
                origem,
                destino,
                numero,
                partida,
                chegada,
                new BigDecimal(preco),
                capacidadeTotal,
                vagas,
                classe,
                ativo
        ));
    }

    private void solicitacao(Usuario usuario, Destino destino, LocalDate dataIda, LocalDate dataVolta, StatusSolicitacao status, String observacoes, Voo ida, Voo volta, int passageiros, LocalDateTime dataCriacao) {
        BigDecimal totalIda = ida.getPrecoBase().multiply(BigDecimal.valueOf(passageiros));
        BigDecimal total = totalIda;
        SolicitacaoViagem solicitacao = new SolicitacaoViagem(usuario, destino, dataIda, dataVolta, status, BigDecimal.ZERO, observacoes);
        solicitacao.setDataCriacao(dataCriacao);
        solicitacao.adicionarVoo(new SolicitacaoVoo(solicitacao, ida, passageiros, 1, ida.getPrecoBase(), totalIda));
        if (volta != null) {
            BigDecimal totalVolta = volta.getPrecoBase().multiply(BigDecimal.valueOf(passageiros));
            total = total.add(totalVolta);
            solicitacao.adicionarVoo(new SolicitacaoVoo(solicitacao, volta, passageiros, 2, volta.getPrecoBase(), totalVolta));
        }
        solicitacao.setValorTotal(total);
        solicitacaoViagemRepository.save(solicitacao);
    }
}
