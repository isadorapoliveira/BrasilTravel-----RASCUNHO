package br.com.brasiltravel.brasiltravel.service;

import br.com.brasiltravel.brasiltravel.dto.NovaSolicitacaoForm;
import br.com.brasiltravel.brasiltravel.model.Aeroporto;
import br.com.brasiltravel.brasiltravel.model.Destino;
import br.com.brasiltravel.brasiltravel.model.SolicitacaoViagem;
import br.com.brasiltravel.brasiltravel.model.SolicitacaoVoo;
import br.com.brasiltravel.brasiltravel.model.Usuario;
import br.com.brasiltravel.brasiltravel.model.Voo;
import br.com.brasiltravel.brasiltravel.model.enums.StatusSolicitacao;
import br.com.brasiltravel.brasiltravel.repository.SolicitacaoViagemRepository;
import br.com.brasiltravel.brasiltravel.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel.repository.DestinoRepository;
import br.com.brasiltravel.brasiltravel.repository.VooRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class SolicitacaoViagemService {

    private static final LocalDate DATA_REFERENCIA = LocalDate.of(2026, 9, 27);

    private final VooRepository vooRepository;
    private final AeroportoRepository aeroportoRepository;
    private final DestinoRepository destinoRepository;
    private final SolicitacaoViagemRepository solicitacaoViagemRepository;

    public SolicitacaoViagemService(VooRepository vooRepository,
                                    AeroportoRepository aeroportoRepository,
                                    DestinoRepository destinoRepository,
                                    SolicitacaoViagemRepository solicitacaoViagemRepository) {
        this.vooRepository = vooRepository;
        this.aeroportoRepository = aeroportoRepository;
        this.destinoRepository = destinoRepository;
        this.solicitacaoViagemRepository = solicitacaoViagemRepository;
    }

    @Transactional
    public SolicitacaoViagem criarSolicitacao(Usuario usuario, NovaSolicitacaoForm form) {
        validarDatas(form.getDataIda(), form.getDataVolta());

        int qtdPassageiros = form.getQtdPassageiros() == null ? 1 : form.getQtdPassageiros();
        if (qtdPassageiros < 1) {
            throw new IllegalArgumentException("A quantidade de passageiros deve ser maior que zero.");
        }

        if (form.getIdDestinoPrincipal() == null) {
            throw new IllegalArgumentException("Selecione o destino da viagem.");
        }

        Destino destinoPrincipal = destinoRepository.findById(form.getIdDestinoPrincipal())
                .orElseThrow(() -> new IllegalArgumentException("Destino selecionado não encontrado."));

        if (form.getIdAeroportoPartida() == null) {
            throw new IllegalArgumentException("Selecione o aeroporto de partida.");
        }

        Aeroporto aeroportoPartida = aeroportoRepository.findByIdAeroporto(form.getIdAeroportoPartida())
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto de partida não encontrado."));

        if (aeroportoPartida.getDestino().getIdDestino().equals(destinoPrincipal.getIdDestino())) {
            throw new IllegalArgumentException("O destino da viagem deve ser diferente da cidade do aeroporto de partida.");
        }

        Voo vooIda = vooRepository.findByIdVoo(form.getIdVooIda())
                .orElseThrow(() -> new IllegalArgumentException("Voo de ida não encontrado."));

        validarVooDisponivel(vooIda, qtdPassageiros, "ida");
        validarVooIdaParaDestino(vooIda, destinoPrincipal);
        validarAeroportoPartida(vooIda, aeroportoPartida);
        validarDataDoVoo(vooIda, form.getDataIda(), "ida");

        Voo vooVolta = null;
        if (form.getIdVooVolta() != null) {
            if (form.getDataVolta() == null) {
                throw new IllegalArgumentException("Selecione a data de volta para reservar o voo de volta.");
            }
            if (form.getIdVooVolta().equals(form.getIdVooIda())) {
                throw new IllegalArgumentException("O voo de volta deve ser diferente do voo de ida.");
            }
            vooVolta = vooRepository.findByIdVoo(form.getIdVooVolta())
                    .orElseThrow(() -> new IllegalArgumentException("Voo de volta não encontrado."));
            validarVooDisponivel(vooVolta, qtdPassageiros, "volta");
            validarVooVoltaDoDestino(vooVolta, destinoPrincipal);
            validarAeroportoRetorno(vooVolta, aeroportoPartida);
            validarDataDoVoo(vooVolta, form.getDataVolta(), "volta");
            if (vooVolta.getDataHoraPartida().toLocalDate().isBefore(vooIda.getDataHoraPartida().toLocalDate())) {
                throw new IllegalArgumentException("O voo de volta deve ocorrer depois do voo de ida.");
            }
        }

        BigDecimal totalIda = vooIda.getPrecoBase().multiply(BigDecimal.valueOf(qtdPassageiros));
        BigDecimal total = totalIda;

        SolicitacaoViagem solicitacao = new SolicitacaoViagem();
        solicitacao.setUsuario(usuario);
        solicitacao.setDestinoPrincipal(destinoPrincipal);
        solicitacao.setDataIda(form.getDataIda());
        solicitacao.setDataVolta(form.getDataVolta());
        solicitacao.setStatusSolicitacao(StatusSolicitacao.AGUARDANDO_ATENDIMENTO);
        solicitacao.setObservacoes(form.getObservacoes());

        solicitacao.adicionarVoo(new SolicitacaoVoo(solicitacao, vooIda, qtdPassageiros, 1, vooIda.getPrecoBase(), totalIda));
        vooIda.setVagasDisponiveis(vooIda.getVagasDisponiveis() - qtdPassageiros);

        if (vooVolta != null) {
            BigDecimal totalVolta = vooVolta.getPrecoBase().multiply(BigDecimal.valueOf(qtdPassageiros));
            total = total.add(totalVolta);
            solicitacao.adicionarVoo(new SolicitacaoVoo(solicitacao, vooVolta, qtdPassageiros, 2, vooVolta.getPrecoBase(), totalVolta));
            vooVolta.setVagasDisponiveis(vooVolta.getVagasDisponiveis() - qtdPassageiros);
        }

        solicitacao.setValorTotal(total);
        return solicitacaoViagemRepository.save(solicitacao);
    }

    @Transactional
    public void cancelarPeloCliente(Long idSolicitacao, Usuario usuario) {
        SolicitacaoViagem solicitacao = solicitacaoViagemRepository.findByIdSolicitacao(idSolicitacao)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        if (!solicitacao.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("Esta solicitação não pertence ao usuário logado.");
        }

        if (solicitacao.getStatusSolicitacao() == StatusSolicitacao.CANCELADA) {
            throw new IllegalArgumentException("Esta solicitação já está cancelada.");
        }
        if (solicitacao.getStatusSolicitacao() == StatusSolicitacao.CONFIRMADA
                || solicitacao.getStatusSolicitacao() == StatusSolicitacao.FINALIZADA) {
            throw new IllegalArgumentException("Solicitações confirmadas ou finalizadas não podem ser canceladas pelo cliente. Entre em contato com o SAC pelo telefone (47) 3433-0000.");
        }

        devolverVagasSeNecessario(solicitacao);
        solicitacao.setStatusSolicitacao(StatusSolicitacao.CANCELADA);
        solicitacaoViagemRepository.save(solicitacao);
    }

    @Transactional
    public void alterarStatusAdmin(Long idSolicitacao, StatusSolicitacao novoStatus) {
        SolicitacaoViagem solicitacao = solicitacaoViagemRepository.findByIdSolicitacao(idSolicitacao)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        if (novoStatus == null) {
            throw new IllegalArgumentException("Status inválido.");
        }

        StatusSolicitacao statusAnterior = solicitacao.getStatusSolicitacao();
        if (novoStatus == StatusSolicitacao.CANCELADA && statusAnterior != StatusSolicitacao.CANCELADA) {
            devolverVagasSeNecessario(solicitacao);
        }

        solicitacao.setStatusSolicitacao(novoStatus);
        solicitacaoViagemRepository.save(solicitacao);
    }


    private void validarAeroportoPartida(Voo voo, Aeroporto aeroportoPartida) {
        Long origemDoVoo = voo.getAeroportoOrigem().getIdAeroporto();
        if (!aeroportoPartida.getIdAeroporto().equals(origemDoVoo)) {
            throw new IllegalArgumentException("O voo de ida deve partir do aeroporto selecionado.");
        }
    }

    private void validarAeroportoRetorno(Voo voo, Aeroporto aeroportoPartida) {
        Long destinoDoVoo = voo.getAeroportoDestino().getIdAeroporto();
        if (!aeroportoPartida.getIdAeroporto().equals(destinoDoVoo)) {
            throw new IllegalArgumentException("O voo de volta deve retornar para o aeroporto de partida selecionado.");
        }
    }

    private void validarVooIdaParaDestino(Voo voo, Destino destinoPrincipal) {
        Long destinoDoVoo = voo.getAeroportoDestino().getDestino().getIdDestino();
        if (!destinoPrincipal.getIdDestino().equals(destinoDoVoo)) {
            throw new IllegalArgumentException("O voo de ida deve ter como destino a cidade selecionada.");
        }
    }

    private void validarVooVoltaDoDestino(Voo voo, Destino destinoPrincipal) {
        Long origemDoVoo = voo.getAeroportoOrigem().getDestino().getIdDestino();
        if (!destinoPrincipal.getIdDestino().equals(origemDoVoo)) {
            throw new IllegalArgumentException("O voo de volta deve partir da cidade selecionada como destino da viagem.");
        }
    }

    private void validarDatas(LocalDate dataIda, LocalDate dataVolta) {
        if (dataIda == null) {
            throw new IllegalArgumentException("Informe a data de ida.");
        }
        if (dataIda.isBefore(DATA_REFERENCIA)) {
            throw new IllegalArgumentException("A data de ida não pode ser anterior à data atual de referência do sistema.");
        }
        if (dataVolta != null && dataVolta.isBefore(dataIda)) {
            throw new IllegalArgumentException("A data de volta não pode ser anterior à data de ida.");
        }
    }

    private void validarVooDisponivel(Voo voo, int qtdPassageiros, String trecho) {
        if (!Boolean.TRUE.equals(voo.getAtivo())) {
            throw new IllegalArgumentException("O voo de " + trecho + " está inativo.");
        }
        if (voo.getVagasDisponiveis() == null || voo.getVagasDisponiveis() < qtdPassageiros) {
            throw new IllegalArgumentException("O voo de " + trecho + " não possui vagas suficientes.");
        }
    }

    private void validarDataDoVoo(Voo voo, LocalDate dataSelecionada, String trecho) {
        if (dataSelecionada == null) {
            throw new IllegalArgumentException("Selecione a data de " + trecho + ".");
        }
        if (!voo.getDataHoraPartida().toLocalDate().equals(dataSelecionada)) {
            throw new IllegalArgumentException("O voo de " + trecho + " deve corresponder exatamente à data selecionada.");
        }
    }

    private void devolverVagasSeNecessario(SolicitacaoViagem solicitacao) {
        if (solicitacao.getStatusSolicitacao() == StatusSolicitacao.CANCELADA) {
            return;
        }
        for (SolicitacaoVoo sv : solicitacao.getVoosSolicitados()) {
            Voo voo = sv.getVoo();
            voo.setVagasDisponiveis(voo.getVagasDisponiveis() + sv.getQtdPassageiros());
            vooRepository.save(voo);
        }
    }
}
