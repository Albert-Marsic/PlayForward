package PlayForward.demo.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RecenzijaCreateRequest {
    @NotNull
    @Min(1)
    @Max(5)
    public Integer ocjena;

    @NotBlank
    public String komentar;

    @NotNull
    public Long idDonator;

    @NotNull
    public Long idPrimatelj;
}
