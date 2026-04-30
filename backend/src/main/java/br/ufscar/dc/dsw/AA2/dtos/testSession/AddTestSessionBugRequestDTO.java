package br.ufscar.dc.dsw.AA2.dtos.testSession;

import jakarta.validation.constraints.NotNull;

public class AddTestSessionBugRequestDTO {
    @NotNull(message = "Um bug precisa ser informado.")
    private String bug;

    public AddTestSessionBugRequestDTO() {
    }

    public void setBug(String bug) {
        this.bug = bug;
    }

    public String getBug() {
        return bug;
    }
}
