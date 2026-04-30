package br.ufscar.dc.dsw.AA2.dtos.testSession;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UpdateSessionRequestDTO {
    @Min(value = 1, message = "A duração precisa ser de no mínimo 1 minuto.")
    @NotNull(message = "A duração da sessão não pode ser nula.")
    private int duration;

    @NotNull(message = "O ID da estratégia não pode ser nulo.")
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "O formato do ID da estratégia precisa ser um UUID."
    )
    private String strategyId;

    @NotNull(message = "A descrição da sessão não pode ser nula.")
    private String description;

    public UpdateSessionRequestDTO() {}

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
