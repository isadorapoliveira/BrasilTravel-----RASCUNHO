package br.com.brasiltravel.brasiltravel_aereo.controller;

import br.com.brasiltravel.brasiltravel_aereo.dto.RelatorioCompanhiaDTO;
import br.com.brasiltravel.brasiltravel_aereo.dto.RelatorioDestinoDTO;
import br.com.brasiltravel.brasiltravel_aereo.dto.RelatorioOcupacaoDTO;
import br.com.brasiltravel.brasiltravel_aereo.model.SolicitacaoVoo;
import br.com.brasiltravel.brasiltravel_aereo.model.Voo;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.StatusSolicitacao;
import br.com.brasiltravel.brasiltravel_aereo.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.CompanhiaAereaRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.SolicitacaoVooRepository;
import br.com.brasiltravel.brasiltravel_aereo.service.SessaoService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminRelatorioController {

    private final SessaoService sessaoService;
    private final SolicitacaoVooRepository solicitacaoVooRepository;
    private final CompanhiaAereaRepository companhiaAereaRepository;
    private final AeroportoRepository aeroportoRepository;

    private static final DateTimeFormatter PERIODO_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final DateTimeFormatter DATA_HORA_CSV = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final LocalDate INICIO_PADRAO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIM_PADRAO = LocalDate.of(2026, 9, 27);

    public AdminRelatorioController(SessaoService sessaoService,
                                    SolicitacaoVooRepository solicitacaoVooRepository,
                                    CompanhiaAereaRepository companhiaAereaRepository,
                                    AeroportoRepository aeroportoRepository) {
        this.sessaoService = sessaoService;
        this.solicitacaoVooRepository = solicitacaoVooRepository;
        this.companhiaAereaRepository = companhiaAereaRepository;
        this.aeroportoRepository = aeroportoRepository;
    }

    @GetMapping("/admin/relatorios")
    public String relatorios(HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(required = false) String tipo,
                             @RequestParam(required = false) String gerar,
                             @RequestParam(required = false) Boolean agruparMes,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim,
                             @RequestParam(required = false) Long idCompanhia,
                             @RequestParam(required = false) List<Long> idOrigens,
                             @RequestParam(required = false) List<Long> idDestinosAeroporto,
                             @RequestParam(required = false) StatusSolicitacao status) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        inicio = inicio == null ? INICIO_PADRAO : inicio;
        fim = fim == null ? FIM_PADRAO : fim;
        status = status == null ? StatusSolicitacao.FINALIZADA : status;
        idOrigens = normalizarLista(idOrigens);
        idDestinosAeroporto = normalizarLista(idDestinosAeroporto);

        boolean separarPorMes = Boolean.TRUE.equals(agruparMes);
        boolean gerado = tipo != null && !tipo.isBlank() && gerar != null;
        adicionarFiltros(model, tipo, inicio, fim, idCompanhia, idOrigens, idDestinosAeroporto, status, separarPorMes, gerado);

        if (gerado) {
            ResultadoRelatorio resultado = consultar(tipo, inicio, fim, idCompanhia, idOrigens, idDestinosAeroporto, status, separarPorMes);
            model.addAttribute("porCompanhia", resultado.porCompanhia());
            model.addAttribute("porOcupacao", resultado.porOcupacao());
            model.addAttribute("porDestino", resultado.porDestino());
        }

        return "admin/relatorios/index";
    }

    @GetMapping("/admin/relatorios/csv")
    public ResponseEntity<byte[]> csv(HttpSession session,
                                      RedirectAttributes redirectAttributes,
                                      @RequestParam String tipo,
                                      @RequestParam(required = false) Boolean agruparMes,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim,
                                      @RequestParam(required = false) Long idCompanhia,
                                      @RequestParam(required = false) List<Long> idOrigens,
                                      @RequestParam(required = false) List<Long> idDestinosAeroporto,
                                      @RequestParam(required = false) StatusSolicitacao status) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return ResponseEntity.status(302).build();
        }

        status = status == null ? StatusSolicitacao.FINALIZADA : status;
        ResultadoRelatorio resultado = consultar(tipo, inicio, fim, idCompanhia, normalizarLista(idOrigens), normalizarLista(idDestinosAeroporto), status, Boolean.TRUE.equals(agruparMes));
        String csv = gerarCsv(tipo, resultado, Boolean.TRUE.equals(agruparMes));
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename(nomeArquivoCsv(tipo)).build());

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private void adicionarFiltros(Model model,
                                  String tipo,
                                  LocalDate inicio,
                                  LocalDate fim,
                                  Long idCompanhia,
                                  List<Long> idOrigens,
                                  List<Long> idDestinosAeroporto,
                                  StatusSolicitacao status,
                                  boolean agruparMes,
                                  boolean gerado) {
        model.addAttribute("tipo", tipo);
        model.addAttribute("gerado", gerado);
        model.addAttribute("agruparMes", agruparMes);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("idCompanhia", idCompanhia);
        model.addAttribute("idOrigens", idOrigens);
        model.addAttribute("idDestinosAeroporto", idDestinosAeroporto);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("statusOpcoes", StatusSolicitacao.values());
        model.addAttribute("companhias", companhiaAereaRepository.findAllByOrderByNomeAsc());
        model.addAttribute("aeroportos", aeroportoRepository.findAllByOrderByCodigoIataAsc());
    }

    private ResultadoRelatorio consultar(String tipo,
                                         LocalDate inicio,
                                         LocalDate fim,
                                         Long idCompanhia,
                                         List<Long> idOrigens,
                                         List<Long> idDestinosAeroporto,
                                         StatusSolicitacao status,
                                         boolean agruparMes) {
        LocalDateTime inicioDia = inicio.atStartOfDay();
        LocalDateTime fimDia = fim.plusDays(1).atStartOfDay().minusNanos(1);

        List<SolicitacaoVoo> voos = solicitacaoVooRepository.buscarParaRelatorioPeriodoStatus(inicioDia, fimDia, status).stream()
                .filter(sv -> idCompanhia == null || sv.getVoo().getCompanhia().getIdCompanhia().equals(idCompanhia))
                .filter(sv -> contem(idOrigens, sv.getVoo().getAeroportoOrigem().getIdAeroporto()))
                .filter(sv -> contem(idDestinosAeroporto, sv.getVoo().getAeroportoDestino().getIdAeroporto()))
                .collect(Collectors.toList());

        List<RelatorioCompanhiaDTO> porCompanhia = List.of();
        List<RelatorioOcupacaoDTO> porOcupacao = List.of();
        List<RelatorioDestinoDTO> porDestino = List.of();

        if ("companhia".equals(tipo)) {
            porCompanhia = consolidarPorCompanhia(voos, agruparMes);
        } else if ("ocupacao".equals(tipo)) {
            porOcupacao = consolidarPorOcupacao(voos, agruparMes);
        } else if ("destino".equals(tipo)) {
            porDestino = consolidarPorDestino(voos, agruparMes);
        }

        return new ResultadoRelatorio(porCompanhia, porOcupacao, porDestino);
    }

    private List<RelatorioCompanhiaDTO> consolidarPorCompanhia(List<SolicitacaoVoo> voos, boolean agruparMes) {
        Map<String, Acumulador> mapa = new LinkedHashMap<>();
        for (SolicitacaoVoo sv : voos) {
            String periodo = periodo(sv.getSolicitacao().getDataCriacao(), agruparMes);
            String companhia = sv.getVoo().getCompanhia().getNome();
            String chave = periodo + "|" + companhia;
            mapa.computeIfAbsent(chave, k -> new Acumulador(periodo, companhia))
                    .adicionar(sv.getSolicitacao().getIdSolicitacao(), sv.getQtdPassageiros(), sv.getPrecoTotal(), sv.getPrecoUnitario());
        }
        return mapa.values().stream()
                .map(a -> new RelatorioCompanhiaDTO(a.periodo, a.rotulo1, a.quantidadeSolicitacoes(), a.passageiros, a.receita, a.precoMedio()))
                .collect(Collectors.toList());
    }

    private List<RelatorioOcupacaoDTO> consolidarPorOcupacao(List<SolicitacaoVoo> voos, boolean agruparMes) {
        Map<String, AcumuladorOcupacao> mapa = new LinkedHashMap<>();
        for (SolicitacaoVoo sv : voos) {
            Voo voo = sv.getVoo();
            String periodo = periodo(sv.getSolicitacao().getDataCriacao(), agruparMes);
            String chave = periodo + "|" + voo.getIdVoo();
            mapa.computeIfAbsent(chave, k -> new AcumuladorOcupacao(periodo, voo))
                    .adicionar(sv.getQtdPassageiros(), sv.getPrecoTotal());
        }
        return mapa.values().stream()
                .sorted(Comparator.comparing((AcumuladorOcupacao a) -> a.periodo).thenComparing(a -> a.dataHoraPartida))
                .map(AcumuladorOcupacao::toDto)
                .collect(Collectors.toList());
    }

    private List<RelatorioDestinoDTO> consolidarPorDestino(List<SolicitacaoVoo> voos, boolean agruparMes) {
        Map<String, Acumulador> mapa = new LinkedHashMap<>();
        for (SolicitacaoVoo sv : voos) {
            if (!Integer.valueOf(1).equals(sv.getOrdemVoo())) {
                continue;
            }
            String periodo = periodo(sv.getSolicitacao().getDataCriacao(), agruparMes);
            String destino = sv.getVoo().getAeroportoDestino().getDestino().getNomeCompleto();
            String chave = periodo + "|" + destino;
            mapa.computeIfAbsent(chave, k -> new Acumulador(periodo, destino))
                    .adicionar(sv.getSolicitacao().getIdSolicitacao(), sv.getQtdPassageiros(), sv.getPrecoTotal(), sv.getPrecoUnitario());
        }
        return mapa.values().stream()
                .map(a -> new RelatorioDestinoDTO(a.periodo, a.rotulo1, a.quantidadeSolicitacoes(), a.passageiros, a.receita, a.ticketMedio()))
                .collect(Collectors.toList());
    }

    private String periodo(LocalDateTime data, boolean agruparMes) {
        return agruparMes ? data.format(PERIODO_FORMATTER) : "Geral";
    }

    private String gerarCsv(String tipo, ResultadoRelatorio resultado, boolean agruparMes) {
        StringBuilder sb = new StringBuilder();
        if ("companhia".equals(tipo)) {
            sb.append(colunaPeriodo(agruparMes)).append("Companhia;Solicitacoes;Passageiros;Receita total;Preco medio\n");
            for (RelatorioCompanhiaDTO r : resultado.porCompanhia()) {
                appendPeriodo(sb, agruparMes, r.getPeriodo());
                sb.append(csv(r.getCompanhia())).append(';')
                        .append(r.getQuantidadeSolicitacoes()).append(';')
                        .append(r.getQuantidadePassageiros()).append(';')
                        .append(moeda(r.getReceitaTotal())).append(';')
                        .append(moeda(r.getPrecoMedio())).append('\n');
            }
        } else if ("ocupacao".equals(tipo)) {
            sb.append(colunaPeriodo(agruparMes)).append("Voo;Companhia;Origem;Destino;Data do voo;Classe;Capacidade total;Passageiros reservados;Vagas disponiveis;Taxa ocupacao;Receita total\n");
            for (RelatorioOcupacaoDTO r : resultado.porOcupacao()) {
                appendPeriodo(sb, agruparMes, r.getPeriodo());
                sb.append(csv(r.getNumeroVoo())).append(';')
                        .append(csv(r.getCompanhia())).append(';')
                        .append(csv(r.getOrigem())).append(';')
                        .append(csv(r.getDestino())).append(';')
                        .append(csv(r.getDataHoraPartida() == null ? "" : r.getDataHoraPartida().format(DATA_HORA_CSV))).append(';')
                        .append(csv(r.getClasse())).append(';')
                        .append(r.getCapacidadeTotal()).append(';')
                        .append(r.getPassageirosReservados()).append(';')
                        .append(r.getVagasDisponiveis()).append(';')
                        .append(moeda(r.getTaxaOcupacao())).append("%;")
                        .append(moeda(r.getReceitaTotal())).append('\n');
            }
        } else if ("destino".equals(tipo)) {
            sb.append(colunaPeriodo(agruparMes)).append("Destino;Solicitacoes;Passageiros;Receita total;Ticket medio\n");
            for (RelatorioDestinoDTO r : resultado.porDestino()) {
                appendPeriodo(sb, agruparMes, r.getPeriodo());
                sb.append(csv(r.getDestino())).append(';')
                        .append(r.getQuantidadeSolicitacoes()).append(';')
                        .append(r.getQuantidadePassageiros()).append(';')
                        .append(moeda(r.getReceitaTotal())).append(';')
                        .append(moeda(r.getTicketMedio())).append('\n');
            }
        }
        return sb.toString();
    }

    private String nomeArquivoCsv(String tipo) {
        if ("companhia".equals(tipo)) {
            return "relatorio-viagens-por-companhia.csv";
        }
        if ("ocupacao".equals(tipo)) {
            return "relatorio-ocupacao-disponibilidade-voos.csv";
        }
        if ("destino".equals(tipo)) {
            return "relatorio-demanda-por-destino.csv";
        }
        return "relatorio-brasiltravel.csv";
    }

    private List<Long> normalizarLista(List<Long> valores) {
        if (valores == null) {
            return new ArrayList<>();
        }
        return valores.stream().filter(v -> v != null && v > 0).collect(Collectors.toList());
    }

    private boolean contem(List<Long> ids, Long id) {
        return ids == null || ids.isEmpty() || ids.contains(id);
    }

    private String colunaPeriodo(boolean agruparMes) {
        return agruparMes ? "Periodo;" : "";
    }

    private void appendPeriodo(StringBuilder sb, boolean agruparMes, String periodo) {
        if (agruparMes) {
            sb.append(csv(periodo)).append(';');
        }
    }

    private String csv(String valor) {
        if (valor == null) {
            return "";
        }
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }

    private String moeda(BigDecimal valor) {
        return valor == null ? "0,00" : valor.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }

    private String validarAdmin(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sessaoService.estaLogado(session)) {
            redirectAttributes.addFlashAttribute("erro", "Faça login como administrador.");
            return "redirect:/login";
        }
        if (!sessaoService.ehAdmin(session)) {
            redirectAttributes.addFlashAttribute("erro", "Acesso permitido apenas para administradores.");
            return "redirect:/cliente/perfil";
        }
        return null;
    }

    private record ResultadoRelatorio(
            List<RelatorioCompanhiaDTO> porCompanhia,
            List<RelatorioOcupacaoDTO> porOcupacao,
            List<RelatorioDestinoDTO> porDestino
    ) {}

    private static class Acumulador {
        private final String periodo;
        private final String rotulo1;
        private final java.util.Set<Long> solicitacoes = new java.util.LinkedHashSet<>();
        private long passageiros = 0L;
        private BigDecimal receita = BigDecimal.ZERO;
        private BigDecimal somaPrecosUnitarios = BigDecimal.ZERO;
        private long quantidadePrecos = 0L;

        private Acumulador(String periodo, String rotulo1) {
            this.periodo = periodo;
            this.rotulo1 = rotulo1;
        }

        private void adicionar(Long idSolicitacao, Integer qtdPassageiros, BigDecimal precoTotal, BigDecimal precoUnitario) {
            if (idSolicitacao != null) {
                solicitacoes.add(idSolicitacao);
            }
            passageiros += qtdPassageiros == null ? 0 : qtdPassageiros;
            receita = receita.add(precoTotal == null ? BigDecimal.ZERO : precoTotal);
            if (precoUnitario != null) {
                somaPrecosUnitarios = somaPrecosUnitarios.add(precoUnitario);
                quantidadePrecos++;
            }
        }

        private Long quantidadeSolicitacoes() {
            return (long) solicitacoes.size();
        }

        private BigDecimal precoMedio() {
            if (quantidadePrecos == 0) {
                return BigDecimal.ZERO;
            }
            return somaPrecosUnitarios.divide(BigDecimal.valueOf(quantidadePrecos), 2, RoundingMode.HALF_UP);
        }

        private BigDecimal ticketMedio() {
            if (solicitacoes.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return receita.divide(BigDecimal.valueOf(solicitacoes.size()), 2, RoundingMode.HALF_UP);
        }
    }

    private static class AcumuladorOcupacao {
        private final String periodo;
        private final String numeroVoo;
        private final String companhia;
        private final String origem;
        private final String destino;
        private final LocalDateTime dataHoraPartida;
        private final String classe;
        private final int capacidadeTotal;
        private long passageirosReservados = 0L;
        private BigDecimal receita = BigDecimal.ZERO;

        private AcumuladorOcupacao(String periodo, Voo voo) {
            this.periodo = periodo;
            this.numeroVoo = voo.getNumeroVoo();
            this.companhia = voo.getCompanhia().getNome();
            this.origem = voo.getAeroportoOrigem().getCodigoIata() + " - " + voo.getAeroportoOrigem().getDestino().getNomeCompleto();
            this.destino = voo.getAeroportoDestino().getCodigoIata() + " - " + voo.getAeroportoDestino().getDestino().getNomeCompleto();
            this.dataHoraPartida = voo.getDataHoraPartida();
            this.classe = voo.getClasse() == null ? "" : voo.getClasse().name().replace('_', ' ');
            int capacidade = voo.getCapacidadeTotal() == null ? 0 : voo.getCapacidadeTotal();
            if (capacidade <= 0) {
                capacidade = (voo.getVagasDisponiveis() == null ? 0 : voo.getVagasDisponiveis());
            }
            this.capacidadeTotal = capacidade;
        }

        private void adicionar(Integer qtdPassageiros, BigDecimal precoTotal) {
            passageirosReservados += qtdPassageiros == null ? 0 : qtdPassageiros;
            receita = receita.add(precoTotal == null ? BigDecimal.ZERO : precoTotal);
        }

        private RelatorioOcupacaoDTO toDto() {
            int vagasDisponiveis = Math.max(capacidadeTotal - Math.toIntExact(Math.min(passageirosReservados, Integer.MAX_VALUE)), 0);
            BigDecimal taxa = BigDecimal.ZERO;
            if (capacidadeTotal > 0) {
                taxa = BigDecimal.valueOf(passageirosReservados)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(capacidadeTotal), 2, RoundingMode.HALF_UP);
            }
            return new RelatorioOcupacaoDTO(periodo, numeroVoo, companhia, origem, destino, dataHoraPartida, classe,
                    capacidadeTotal, passageirosReservados, vagasDisponiveis, taxa, receita);
        }
    }
}
