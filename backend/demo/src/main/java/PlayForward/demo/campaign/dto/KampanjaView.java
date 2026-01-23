package PlayForward.demo.campaign.dto;

import java.time.LocalDate;
import java.util.List;

public class KampanjaView {
    public Long id;
    public String naziv;
    public String opis;
    public String napredak;
    public LocalDate rokTrajanja;
    public String status;
    public Integer postotak;
    public KampanjaPrimateljView primatelj;
    public List<PopisIgracakaView> popisi;
}
