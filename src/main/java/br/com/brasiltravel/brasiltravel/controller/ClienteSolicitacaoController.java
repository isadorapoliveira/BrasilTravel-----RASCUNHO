package br.com.brasiltravel.brasiltravel.controller;

import br.com.brasiltravel.brasiltravel.dto.NovaSolicitacaoForm;
import br.com.brasiltravel.brasiltravel.model.SolicitacaoViagem;
import br.com.brasiltravel.brasiltravel.model.Usuario;
import br.com.brasiltravel.brasiltravel.repository.SolicitacaoViagemRepository;
import br.com.brasiltravel.brasiltravel.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel.repository.DestinoRepository;
import br.com.brasiltravel.brasiltravel.repository.VooRepository;
import br.com.brasiltravel.brasiltravel.service.SessaoService;
import br.com.brasiltravel.brasiltravel.service.SolicitacaoViagemService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
public class ClienteSolicitacaoController {

    private final SessaoService sessaoService;
    private final VooRepository vooRepository;
    private final AeroportoRepository aeroportoRepository;
    private final DestinoRepository destinoRepository;
    private final SolicitacaoViagemRepository solicitacaoViagemRepository;
    private final SolicitacaoViagemService solicitacaoViagemService;

    public ClienteSolicitacaoController(SessaoService sessaoService,
                                        VooRepository vooRepository,
                                        AeroportoRepository aeroportoRepository,
                                        DestinoRepository destinoRepository,
                                        SolicitacaoViagemRepository solicitacaoViagemRepository,
                                        SolicitacaoViagemService solicitacaoViagemService) {
        this.sessaoService = sessaoService;
        this.vooRepository = vooRepository;
        this.aeroportoRepository = aeroportoRepository;
        this.destinoRepository = destinoRepository;
        this.solicitacaoViagemRepository = solicitacaoViagemRepository;
        this.solicitacaoViagemService = solicitacaoViagemService;
    }

    @GetMapping("/cliente/solicitacoes")
    public String minhasSolicitacoes(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = validarCliente(session, redirectAttributes);
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("solicitacoes", solicitacaoViagemRepository.findByUsuarioOrderByDataCriacaoDesc(usuario));
        return "cliente/solicitacoes/lista";
    }

    @GetMapping("/cliente/solicitacoes/nova")
    public String nova(@RequestParam(required = false) Long idDestino,
                       HttpSession session,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Usuario usuario = validarCliente(session, redirectAttributes);
        if (usuario == null) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("form")) {
            NovaSolicitacaoForm form = new NovaSolicitacaoForm();
            if (idDestino != null) {
                form.setIdDestinoPrincipal(idDestino);
            }
            model.addAttribute("form", form);
        }
        prepararFormulario(model);
        return "cliente/solicitacoes/form";
    }

    @PostMapping("/cliente/solicitacoes/salvar")
    public String salvar(@Valid @ModelAttribute("form") NovaSolicitacaoForm form,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Usuario usuario = validarCliente(session, redirectAttributes);
        if (usuario == null) {
            return "redirect:/login";
        }

        if (form.getDataIda() != null && form.getDataVolta() != null && form.getDataVolta().isBefore(form.getDataIda())) {
            bindingResult.rejectValue("dataVolta", "dataVolta.invalida", "A data de volta não pode ser anterior à data de ida.");
        }
        if (form.getIdVooIda() != null && form.getIdVooIda().equals(form.getIdVooVolta())) {
            bindingResult.rejectValue("idVooVolta", "idVooVolta.invalido", "O voo de volta deve ser diferente do voo de ida.");
        }

        if (bindingResult.hasErrors()) {
            prepararFormulario(model);
            return "cliente/solicitacoes/form";
        }

        try {
            SolicitacaoViagem solicitacao = solicitacaoViagemService.criarSolicitacao(usuario, form);
            redirectAttributes.addFlashAttribute("sucesso", "Solicitação criada com sucesso.");
            return "redirect:/cliente/solicitacoes/" + solicitacao.getIdSolicitacao();
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            prepararFormulario(model);
            return "cliente/solicitacoes/form";
        }
    }

    @GetMapping("/cliente/solicitacoes/{id}")
    public String detalhe(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = validarCliente(session, redirectAttributes);
        if (usuario == null) {
            return "redirect:/login";
        }

        SolicitacaoViagem solicitacao = solicitacaoViagemRepository.findByIdSolicitacao(id).orElse(null);
        if (solicitacao == null || !solicitacao.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("erro", "Solicitação não encontrada para o usuário logado.");
            return "redirect:/cliente/solicitacoes";
        }

        model.addAttribute("solicitacao", solicitacao);
        return "cliente/solicitacoes/detalhe";
    }

    @PostMapping("/cliente/solicitacoes/cancelar/{id}")
    public String cancelar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = validarCliente(session, redirectAttributes);
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            solicitacaoViagemService.cancelarPeloCliente(id, usuario);
            redirectAttributes.addFlashAttribute("sucesso", "Solicitação cancelada com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/cliente/solicitacoes";
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("destinos", destinoRepository.findByAtivoTrueOrderByCidadeAsc());
        model.addAttribute("aeroportos", aeroportoRepository.findByAtivoTrueOrderByCodigoIataAsc());
        model.addAttribute("voos", vooRepository.findByAtivoTrueAndVagasDisponiveisGreaterThanOrderByDataHoraPartidaAsc(0));
        model.addAttribute("dataMinima", LocalDate.of(2026, 9, 27));
        model.addAttribute("telefoneSac", "(47) 3433-0000");
    }

    private Usuario validarCliente(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = sessaoService.usuarioLogado(session).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Faça login para acessar suas solicitações.");
        }
        return usuario;
    }
}
