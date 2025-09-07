package org.teya.ledgerservice.service;

import org.teya.ledgerservice.model.Transaction;
import org.teya.ledgerservice.service.dto.LedgerAction;

public interface LegerActionService {
    Transaction performAction(LedgerAction ledgerAction);
}
