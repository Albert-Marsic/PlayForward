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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
}
