package br.ufac.sgcmapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.ufac.sgcmapi.model.Atendimento;
import br.ufac.sgcmapi.model.EStatus;
import br.ufac.sgcmapi.model.Profissional;
import br.ufac.sgcmapi.repository.AtendimentoRepository;

@ExtendWith(MockitoExtension.class)
public class AtendimentoServiceTest {
    @Mock
    private AtendimentoRepository repo;

    @InjectMocks
    private AtendimentoService servico;

    Atendimento a1;
    Atendimento a2;
    Profissional p1;
    List<Atendimento> atendimentos;

    @BeforeEach
    public void setUp() {
        a1 = new Atendimento();
        a2 = new Atendimento();
        p1 = new Profissional();
        a1.setId(1L);
        a2.setId(2L);
        a1.setHora(LocalTime.of(14, 00));
        a2.setHora(LocalTime.of(15, 00));
        p1.setNome("Joao");
        a1.setProfissional(p1);
        atendimentos = new ArrayList<>();
        atendimentos.add(a1);
        atendimentos.add(a2);
    }

    @Test
    public void testAtendimentoDelete() {
        Mockito.doNothing().when(repo).deleteById(1L);
        repo.deleteById(1L);
        Mockito.verify(repo, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testAtendimentoGetAll() {
        Mockito.when(repo.findAll()).thenReturn(atendimentos);
        List<Atendimento> result = servico.get();
        assertEquals(2, result.size());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testAtendimentoGetById() {
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(a1));
        Atendimento result = servico.get(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testAtendimentoGetTermoBusca() {
        Mockito.when(repo.busca("Joao")).thenReturn(atendimentos);
        List<Atendimento> busca = servico.get("Joao");
        Atendimento result = busca.get(0);
        assertEquals("Joao", result.getProfissional().getNome());
    }

    @Test
    void testGetHorarios() {
        Mockito.when(repo.findByProfissionalAndDataAndStatusNot(
                Mockito.any(Profissional.class),
                Mockito.eq(LocalDate.now()),
                Mockito.eq(EStatus.CANCELADO))).thenReturn(atendimentos);

        List<String> result = servico.getHorarios(1L, LocalDate.now());
        assertEquals(2, result.size());
        assertTrue(result.contains("15:00:00"));
    }

    @Test
    void testAtendimentoSave() {
        Mockito.when(repo.save(any(Atendimento.class))).thenReturn(a1);
        Atendimento result = servico.save(a1);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testUpdateStatus() {
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(a1));
        Atendimento result = servico.updateStatus(1L);
        assertNotNull(result);
        assertEquals(EStatus.CONFIRMADO, result.getStatus());
    }
}
