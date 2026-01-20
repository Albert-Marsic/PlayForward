package PlayForward.demo;

import PlayForward.demo.user.AdminService;
import PlayForward.demo.user.Donator;
import PlayForward.demo.user.DonatorRepository;
import PlayForward.demo.user.Korisnik;
import PlayForward.demo.user.KorisnikRepository;
import PlayForward.demo.user.Primatelj;
import PlayForward.demo.user.PrimateljRepository;
import PlayForward.demo.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthController.class)
class OAuthControllerRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KorisnikRepository korisnikRepository;

    @MockBean
    private DonatorRepository donatorRepository;

    @MockBean
    private PrimateljRepository primateljRepository;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private AdminService adminService;

    @Test
    void getRoleReturnsDonator() throws Exception {
        String email = "donator@example.com";
        Korisnik korisnik = korisnikWithId(10L, email);

        when(korisnikRepository.findByEmail(anyString())).thenReturn(Optional.of(korisnik));
        when(donatorRepository.existsById(10L)).thenReturn(true);
        when(primateljRepository.existsById(10L)).thenReturn(false);

        mockMvc.perform(get("/api/auth/role")
                .with(oauth2Login().attributes(attrs -> attrs.put("email", email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("DONATOR"))
                .andExpect(jsonPath("$.registered").value(true));
    }

    @Test
    void postRoleAssignsRecipient() throws Exception {
        String email = "recipient@example.com";
        Korisnik korisnik = korisnikWithId(22L, email);

        when(korisnikRepository.findByEmail(anyString())).thenReturn(Optional.of(korisnik));
        when(donatorRepository.existsById(22L)).thenReturn(false);
        when(primateljRepository.existsById(22L)).thenReturn(false, true);
        when(primateljRepository.saveAndFlush(any(Primatelj.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/auth/role")
                .with(oauth2Login().attributes(attrs -> {
                    attrs.put("email", email);
                    attrs.put("name", "Recipient User");
                }))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"RECIPIENT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("RECIPIENT"));

        var primateljCaptor = org.mockito.ArgumentCaptor.forClass(Primatelj.class);
        verify(primateljRepository).saveAndFlush(primateljCaptor.capture());
        assertThat(primateljCaptor.getValue().getKorisnik()).isEqualTo(korisnik);
        verify(donatorRepository, never()).save(any(Donator.class));
    }

    private Korisnik korisnikWithId(Long id, String email) {
        Korisnik korisnik = new Korisnik();
        korisnik.setEmail(email);
        korisnik.setImeKorisnik("Test User");
        ReflectionTestUtils.setField(korisnik, "id", id);
        return korisnik;
    }
}
