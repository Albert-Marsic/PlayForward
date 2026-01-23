package PlayForward.demo.user;

import PlayForward.demo.campaign.Kampanja;
import PlayForward.demo.campaign.KampanjaRepository;
import PlayForward.demo.campaign.PopisIgracakaRepository;
import PlayForward.demo.listing.Igracka;
import PlayForward.demo.listing.IgrackaRepository;
import PlayForward.demo.listing.StatusIgracke;
import PlayForward.demo.request.Zahtjev;
import PlayForward.demo.request.ZahtjevRepository;
import PlayForward.demo.review.RecenzijaRepository;
import PlayForward.demo.user.dto.AdminDonationView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUserServiceTest {

    private KorisnikRepository korisnikRepo;
    private AdminRepository adminRepo;
    private DonatorRepository donatorRepo;
    private PrimateljRepository primateljRepo;
    private IgrackaRepository igrackaRepo;
    private KampanjaRepository kampanjaRepo;
    private PopisIgracakaRepository popisIgracakaRepo;
    private ZahtjevRepository zahtjevRepo;
    private RecenzijaRepository recenzijaRepo;
    private AdminService adminService;

    private AdminUserService service;

    @BeforeEach
    void setup() {
        korisnikRepo = mock(KorisnikRepository.class);
        adminRepo = mock(AdminRepository.class);
        donatorRepo = mock(DonatorRepository.class);
        primateljRepo = mock(PrimateljRepository.class);
        igrackaRepo = mock(IgrackaRepository.class);
        kampanjaRepo = mock(KampanjaRepository.class);
        popisIgracakaRepo = mock(PopisIgracakaRepository.class);
        zahtjevRepo = mock(ZahtjevRepository.class);
        recenzijaRepo = mock(RecenzijaRepository.class);
        adminService = mock(AdminService.class);

        service = new AdminUserService(
                korisnikRepo, adminRepo, donatorRepo, primateljRepo,
                igrackaRepo, kampanjaRepo, popisIgracakaRepo,
                zahtjevRepo, recenzijaRepo, adminService
        );
    }

    @Test
    void testListUsers_empty() {
        when(korisnikRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(Collections.emptyList());
        when(donatorRepo.findAll()).thenReturn(Collections.emptyList());
        when(primateljRepo.findAll()).thenReturn(Collections.emptyList());

        List<?> users = service.listUsers(10, 0);
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void testListDonations_empty() {
        when(igrackaRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(Collections.emptyList());
        List<?> donations = service.listDonations(10, 0);
        assertNotNull(donations);
        assertEquals(0, donations.size());
    }

    @Test
    void testListCampaigns_empty() {
        when(kampanjaRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(Collections.emptyList());
        List<?> campaigns = service.listCampaigns(10, 0);
        assertNotNull(campaigns);
        assertEquals(0, campaigns.size());
    }

    @Test
    void testGetStats_empty() {
        when(korisnikRepo.count()).thenReturn(0L);
        when(zahtjevRepo.count()).thenReturn(0L);
        when(igrackaRepo.findAll()).thenReturn(Collections.emptyList());
        when(kampanjaRepo.findAll()).thenReturn(Collections.emptyList());

        var stats = service.getStats();
        assertEquals(0L, stats.get("ukupnoKorisnika"));
        assertEquals(0L, stats.get("aktivnihDonacija"));
        assertEquals(0L, stats.get("aktivnihKampanja"));
        assertEquals(0L, stats.get("ukupnoZahtjeva"));
    }

    @Test
    void testDeleteUserById_invalidId() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.deleteUserById(0L));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testDeleteDonationById_notFound() {
        when(igrackaRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.deleteDonationById(1L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testDeleteCampaignById_notFound() {
        when(kampanjaRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.deleteCampaignById(1L));
        assertEquals(404, ex.getStatusCode().value());
    }
    @Test
    void testListUsers_limitExceedsMax() {
        Korisnik k = new Korisnik();
        k.setId(1L);
        k.setEmail("user@example.com");
        when(korisnikRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(k));
        when(donatorRepo.findAll()).thenReturn(Collections.emptyList());
        when(primateljRepo.findAll()).thenReturn(Collections.emptyList());

        List<?> users = service.listUsers(1000, 0);
        assertEquals(1, users.size());
    }

    @Test
    void testListDonations_nullStatusAndNullDonator() {
        Igracka igracka = new Igracka();
        igracka.setId(1L);
        igracka.setNaziv("Test Toy");
        igracka.setStatus(null);
        igracka.setDonator(null);

        when(igrackaRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(igracka));

        var donations = service.listDonations(10, 0);
        assertEquals(1, donations.size());
        assertNull(((AdminDonationView) donations.get(0)).status);
        assertNull(((AdminDonationView) donations.get(0)).donator.email);
    }

    @Test
    void testGetStats_withActiveDonationsAndCampaigns() {
        Igracka igracka = new Igracka();
        igracka.setStatus(StatusIgracke.DOSTUPNO);
        Kampanja kampanja = new Kampanja();
        kampanja.setRokTrajanja(java.time.LocalDate.now().plusDays(1));

        when(korisnikRepo.count()).thenReturn(1L);
        when(zahtjevRepo.count()).thenReturn(2L);
        when(igrackaRepo.findAll()).thenReturn(List.of(igracka));
        when(kampanjaRepo.findAll()).thenReturn(List.of(kampanja));

        var stats = service.getStats();
        assertEquals(1L, stats.get("ukupnoKorisnika"));
        assertEquals(1L, stats.get("aktivnihDonacija"));
        assertEquals(1L, stats.get("aktivnihKampanja"));
        assertEquals(2L, stats.get("ukupnoZahtjeva"));
    }

    @Test
    void testDeleteDonationById_withExistingRequests() {
        Igracka igracka = new Igracka();
        igracka.setId(1L);

        Zahtjev zahtjev = new Zahtjev();
        zahtjev.setId(10L);

        when(igrackaRepo.findById(1L)).thenReturn(Optional.of(igracka));
        when(zahtjevRepo.findByIgracka_Id(1L)).thenReturn(List.of(zahtjev));

        service.deleteDonationById(1L);

        verify(recenzijaRepo).deleteByZahtjev_IdIn(List.of(10L));
        verify(zahtjevRepo).deleteAll(List.of(zahtjev));
        verify(igrackaRepo).delete(igracka);
    }

}
