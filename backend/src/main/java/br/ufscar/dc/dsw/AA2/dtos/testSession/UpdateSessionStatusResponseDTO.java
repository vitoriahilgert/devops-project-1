package br.ufscar.dc.dsw.AA2.dtos.testSession;

import br.ufscar.dc.dsw.AA2.models.TestSession;
import br.ufscar.dc.dsw.AA2.models.enums.TestSessionStatusEnum;

import java.util.UUID;

public class UpdateSessionStatusResponseDTO {
    private TestSessionStatusEnum oldStatus;
    private TestSessionStatusEnum updatedStatus;

    public UpdateSessionStatusResponseDTO(TestSession session, TestSessionStatusEnum oldStatus) {
        updatedStatus = session.getStatus();
        this.oldStatus = oldStatus;
    }

    public TestSessionStatusEnum getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(TestSessionStatusEnum oldStatus) {
        this.oldStatus = oldStatus;
    }

    public TestSessionStatusEnum getUpdatedStatus() {
        return updatedStatus;
    }

    public void setUpdatedStatus(TestSessionStatusEnum updatedStatus) {
        this.updatedStatus = updatedStatus;
    }
}
