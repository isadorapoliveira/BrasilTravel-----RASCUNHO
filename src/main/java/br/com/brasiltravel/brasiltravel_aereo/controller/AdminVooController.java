package br.com.brasiltravel.brasiltravel_aereo.controller;

import br.com.brasiltravel.brasiltravel_aereo.model.Aeroporto;
import br.com.brasiltravel.brasiltravel_aereo.model.CompanhiaAerea;
import br.com.brasiltravel.brasiltravel_aereo.model.Voo;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.ClasseVoo;
import br.com.brasiltravel.brasiltravel_aereo.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.CompanhiaAereaRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.SolicitacaoVooRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.VooRepository;
import br.com.brasiltravel.brasiltravel_aereo.service.SessaoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminVooController {

    private static final LocalDate DATA_REFERENCIA = LocalDate.of(2026, 9, 27);

    private final SessaoService sessaoService;
    private final VooRepository vooRepository;
    private final CompanhiaAereaRepository companhiaAereaRepository;
    private final AeroportoRepository aeroportoRepository;
    private final SolicitacaoVooRepository solicitacaoVooRepository;

    public AdminVooController(SessaoService sessaoService,
                              VooRepository vooRepository,
                              CompanhiaAereaRepository companhiaAereaRepository,
                              AeroportoRepository aeroportoRepository,
                              SolicitacaoVooRepository solicitacaoVooRepository) {
        this.sessaoService = sessaoService;
        this.vooRepository = vooRepository;
        this.companhiaAereaRepository = companhiaAereaRepository;
        this.aeroportoRepository = aeroportoRepository;
        this.solicitacaoVooRepository = solicitacaoVooRepository;
    }

    @GetMapping("/admin/voos")
    public String listar(HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(required = false) Long idOrigem,
                         @RequestParam(required = false) Long idDestino,
                         @RequestParam(defaultValue = "0") int pagina) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        final int tamanhoPagina = 20;
        int paginaAtual = Math.max(pagina, 0);
        Page<Voo> paginaVoos = vooRepository.buscarVoosFuturosFiltrados(
                DATA_REFERENCIA.atStartOfDay(),
                idOrigem,
                idDestino,
                PageRequest.of(paginaAtual, tamanhoPagina)
        );

        if (paginaVoos.getTotalPages() > 0 && paginaAtual >= paginaVoos.getTotalPages()) {
            paginaAtual = paginaVoos.getTotalPages() - 1;
            paginaVoos = vooRepository.buscarVoosFuturosFiltrados(
                    DATA_REFERENCIA.atStartOfDay(),
                    idOrigem,
                    idDestino,
                    PageRequest.of(paginaAtual, tamanhoPagina)
            );
        }

        long totalRegistros = paginaVoos.getTotalElements();
        long inicioExibicao = totalRegistros == 0 ? 0 : ((long) paginaAtual * tamanhoPagina) + 1;
        long fimExibicao = Math.min((long) (paginaAtual + 1) * tamanhoPagina, totalRegistros);

        model.addAttribute("voos", paginaVoos.getContent());
        model.addAttribute("aeroportos", aeroportoRepository.findAllByOrderByCodigoIataAsc());
        model.addAttribute("idOrigem", idOrigem);
        model.addAttribute("idDestino", idDestino);
        model.addAttribute("paginaAtual", paginaAtual);
        model.addAttribute("totalPaginas", paginaVoos.getTotalPages());
        model.addAttribute("totalRegistros", totalRegistros);
        model.addAttribute("tamanhoPagina", tamanhoPagina);
        model.addAttribute("inicioExibicao", inicioExibicao);
        model.addAttribute("fimExibicao", fimExibicao);
        model.addAttribute("temPaginaAnterior", paginaAtual > 0);
        model.addAttribute("temProximaPagina", paginaAtual + 1 < paginaVoos.getTotalPages());
        model.addAttribute("paginaAnterior", Math.max(paginaAtual - 1, 0));
        model.addAttribute("proximaPagina", paginaAtual + 1);
        return "admin/voos/lista";
    }

    @GetMapping("/admin/voos/novo")
    public String novo(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Voo voo = new Voo();
        voo.setCompanhia(new CompanhiaAerea());
        voo.setAeroportoOrigem(new Aeroporto());
        voo.setAeroportoDestino(new Aeroporto());
        voo.setClasse(ClasseVoo.ECONOMICA);
        voo.setCapacidadeTotal(12);
        voo.setVagasDisponiveis(12);
        voo.setPrecoBase(BigDecimal.ZERO);
        voo.setAtivo(true);

        prepararFormulario(model, voo, false);
        return "admin/voos/form";
    }

    @PostMapping("/admin/voos/salvar")
    public String salvar(@Valid @ModelAttribute("voo") Voo voo,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        normalizar(voo);
        carregarCompanhiaSelecionada(voo, bindingResult);
        carregarAeroportosSelecionados(voo, bindingResult);
        validarNumeroVoo(voo, bindingResult);
        validarDatas(voo, bindingResult);
        validarTrajeto(voo, bindingResult);
        validarCapacidade(voo, bindingResult);

        if (bindingResult.hasErrors()) {
            prepararFormulario(model, voo, voo.getIdVoo() != null);
            return "admin/voos/form";
        }

        vooRepository.save(voo);
        redirectAttributes.addFlashAttribute("sucesso", "Voo salvo com sucesso.");
        return "redirect:/admin/voos";
    }

    @GetMapping("/admin/voos/editar/{id}")
    public String editar(@PathVariable Long id,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Voo voo = vooRepository.findByIdVoo(id).orElse(null);
        if (voo == null) {
            redirectAttributes.addFlashAttribute("erro", "Voo não encontrado.");
            return "redirect:/admin/voos";
        }

        prepararFormulario(model, voo, true);
        return "admin/voos/form";
    }

    @PostMapping("/admin/voos/alternar-status/{id}")
    public String alternarStatus(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Voo voo = vooRepository.findById(id).orElse(null);
        if (voo == null) {
            redirectAttributes.addFlashAttribute("erro", "Voo não encontrado.");
            return "redirect:/admin/voos";
        }

        voo.setAtivo(!Boolean.TRUE.equals(voo.getAtivo()));
        vooRepository.save(voo);
        redirectAttributes.addFlashAttribute("sucesso", "Status do voo atualizado.");
        return "redirect:/admin/voos";
    }

    @PostMapping("/admin/voos/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Voo voo = vooRepository.findById(id).orElse(null);
        if (voo == null) {
            redirectAttributes.addFlashAttribute("erro", "Voo não encontrado.");
            return "redirect:/admin/voos";
        }

        long solicitacoesVinculadas = solicitacaoVooRepository.countByVoo_IdVoo(id);
        if (solicitacoesVinculadas > 0) {
            redirectAttributes.addFlashAttribute("erro", "Este voo já aparece em solicitações de viagem. Para preservar o histórico do banco, use Inativar em vez de excluir.");
            return "redirect:/admin/voos";
        }

        try {
            vooRepository.delete(voo);
            redirectAttributes.addFlashAttribute("sucesso", "Voo excluído com sucesso.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível excluir o voo porque existem registros vinculados.");
        }

        return "redirect:/admin/voos";
    }

    private void prepararFormulario(Model model, Voo voo, boolean modoEdicao) {
        List<CompanhiaAerea> companhias = companhiaAereaRepository.findAllByOrderByNomeAsc();
        List<Aeroporto> aeroportos = aeroportoRepository.findAllByOrderByCodigoIataAsc();

        model.addAttribute("voo", voo);
        model.addAttribute("companhias", companhias);
        model.addAttribute("aeroportos", aeroportos);
        model.addAttribute("classes", ClasseVoo.values());
        model.addAttribute("modoEdicao", modoEdicao);
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

    private void normalizar(Voo voo) {
        if (voo.getNumeroVoo() != null) {
            voo.setNumeroVoo(voo.getNumeroVoo().trim().toUpperCase());
        }
        if (voo.getClasse() == null) {
            voo.setClasse(ClasseVoo.ECONOMICA);
        }
        if (voo.getPrecoBase() == null) {
            voo.setPrecoBase(BigDecimal.ZERO);
        }
        if (voo.getCapacidadeTotal() == null) {
            voo.setCapacidadeTotal(0);
        }
        if (voo.getVagasDisponiveis() == null) {
            voo.setVagasDisponiveis(0);
        }
        if (voo.getVagasDisponiveis() > voo.getCapacidadeTotal()) {
            voo.setVagasDisponiveis(voo.getCapacidadeTotal());
        }
        voo.setAtivo(Boolean.TRUE.equals(voo.getAtivo()));
    }

    private void carregarCompanhiaSelecionada(Voo voo, BindingResult bindingResult) {
        if (voo.getCompanhia() == null || voo.getCompanhia().getIdCompanhia() == null) {
            bindingResult.rejectValue("companhia", "companhia.obrigatoria", "Selecione a companhia aérea.");
            return;
        }

        CompanhiaAerea companhia = companhiaAereaRepository.findById(voo.getCompanhia().getIdCompanhia()).orElse(null);
        if (companhia == null) {
            bindingResult.rejectValue("companhia", "companhia.invalida", "Companhia aérea não encontrada.");
            return;
        }

        voo.setCompanhia(companhia);
    }

    private void carregarAeroportosSelecionados(Voo voo, BindingResult bindingResult) {
        if (voo.getAeroportoOrigem() == null || voo.getAeroportoOrigem().getIdAeroporto() == null) {
            bindingResult.rejectValue("aeroportoOrigem", "aeroportoOrigem.obrigatorio", "Selecione o aeroporto de origem.");
        } else {
            Aeroporto origem = aeroportoRepository.findById(voo.getAeroportoOrigem().getIdAeroporto()).orElse(null);
            if (origem == null) {
                bindingResult.rejectValue("aeroportoOrigem", "aeroportoOrigem.invalido", "Aeroporto de origem não encontrado.");
            } else {
                voo.setAeroportoOrigem(origem);
            }
        }

        if (voo.getAeroportoDestino() == null || voo.getAeroportoDestino().getIdAeroporto() == null) {
            bindingResult.rejectValue("aeroportoDestino", "aeroportoDestino.obrigatorio", "Selecione o aeroporto de destino.");
        } else {
            Aeroporto destino = aeroportoRepository.findById(voo.getAeroportoDestino().getIdAeroporto()).orElse(null);
            if (destino == null) {
                bindingResult.rejectValue("aeroportoDestino", "aeroportoDestino.invalido", "Aeroporto de destino não encontrado.");
            } else {
                voo.setAeroportoDestino(destino);
            }
        }
    }

    private void validarNumeroVoo(Voo voo, BindingResult bindingResult) {
        String numero = voo.getNumeroVoo();
        if (numero == null || numero.isBlank()) {
            return;
        }

        boolean duplicado = vooRepository.existsByNumeroVooIgnoreCase(numero);
        if (duplicado) {
            Voo existente = vooRepository.findAll().stream()
                    .filter(v -> v.getNumeroVoo() != null && v.getNumeroVoo().equalsIgnoreCase(numero))
                    .findFirst()
                    .orElse(null);

            boolean mesmoRegistro = existente != null
                    && voo.getIdVoo() != null
                    && voo.getIdVoo().equals(existente.getIdVoo());

            if (!mesmoRegistro) {
                bindingResult.rejectValue("numeroVoo", "numeroVoo.duplicado", "Já existe um voo cadastrado com este número.");
            }
        }
    }

    private void validarDatas(Voo voo, BindingResult bindingResult) {
        LocalDateTime partida = voo.getDataHoraPartida();
        LocalDateTime chegada = voo.getDataHoraChegada();

        if (partida != null && chegada != null && !chegada.isAfter(partida)) {
            bindingResult.rejectValue("dataHoraChegada", "dataHoraChegada.invalida", "A data/hora de chegada deve ser posterior à partida.");
        }
    }

    private void validarTrajeto(Voo voo, BindingResult bindingResult) {
        if (voo.getAeroportoOrigem() != null
                && voo.getAeroportoOrigem().getIdAeroporto() != null
                && voo.getAeroportoDestino() != null
                && voo.getAeroportoDestino().getIdAeroporto() != null
                && voo.getAeroportoOrigem().getIdAeroporto().equals(voo.getAeroportoDestino().getIdAeroporto())) {
            bindingResult.rejectValue("aeroportoDestino", "aeroportoDestino.igualOrigem", "O aeroporto de destino deve ser diferente do aeroporto de origem.");
        }
    }

    private void validarCapacidade(Voo voo, BindingResult bindingResult) {
        if (voo.getCapacidadeTotal() != null && voo.getCapacidadeTotal() < 1) {
            bindingResult.rejectValue("capacidadeTotal", "capacidadeTotal.invalida", "A capacidade total deve ser maior que zero.");
        }
        if (voo.getCapacidadeTotal() != null
                && voo.getVagasDisponiveis() != null
                && voo.getVagasDisponiveis() > voo.getCapacidadeTotal()) {
            bindingResult.rejectValue("vagasDisponiveis", "vagasDisponiveis.invalida", "As vagas disponíveis não podem ser maiores que a capacidade total.");
        }
    }
}

