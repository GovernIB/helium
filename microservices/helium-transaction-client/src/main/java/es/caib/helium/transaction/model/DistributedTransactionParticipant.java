package es.caib.helium.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributedTransactionParticipant implements Serializable {

    private String serviceId;
    private DistributedTransactionStatus status;

}
