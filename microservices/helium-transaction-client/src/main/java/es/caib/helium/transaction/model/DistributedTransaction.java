package es.caib.helium.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class DistributedTransaction implements Serializable {

    private String id;
    private DistributedTransactionStatus status;
    private String errorMessage;
    private final List<DistributedTransactionParticipant> participants = new ArrayList<>();

}