package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.util.UUID;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Participant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParticipantRepositoryTest extends AbstractCoreRepositoryTest {

    @Test
    void participant_ok() {
        assertTrue(participantRepository.findAll().isEmpty());
        Participant participant = createParticipant();
        final String participantKey = participant.getKey();


        Participant result = participantRepository.findByKey(participantKey).orElseThrow();
        assertEquals(participant.getName(), result.getName());
        assertEquals(participant.getKey(), result.getKey());
        assertEquals(participant.getGender(), result.getGender());
        assertEquals(participant.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(participant.getContestType(), result.getContestType());
    }

    @Test
    void keyConstraint_exceptionThrown() {
        Participant existingParticipant = createParticipant();
        Participant participant = new Participant();
        participant.setKey(existingParticipant.getKey());
        participant.setName("Rico Lewis");
        participant.setGender(CoreEntityGender.MALE);

        assertThrows(DataIntegrityViolationException.class, () -> participantRepository.save(participant));
        participant.setKey(UUID.randomUUID().toString());
        Assertions.assertDoesNotThrow(() -> participantRepository.save(participant));
    }

    @Test
    void notNullNameConstraint__exceptionThrown() {
        Participant participant = createParticipant();
        participant.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> participantRepository.saveAndFlush(participant));
    }

    @Test
    void notNullContestTypeConstraint__exceptionThrown() {
        Participant participant = createParticipant();
        participant.setContestType(null);
        assertThrows(DataIntegrityViolationException.class, () -> participantRepository.saveAndFlush(participant));
    }

    @Test
    void notNullGenderConstraint__exceptionThrown() {
        Participant participant = createParticipant();
        participant.setGender(null);
        assertThrows(DataIntegrityViolationException.class, () -> participantRepository.saveAndFlush(participant));
    }

    @Test
    void notNullDateOfBirthConstraint__exceptionThrown() {
        Participant participant = createParticipant();
        participant.setDateOfBirth(null);
        assertThrows(DataIntegrityViolationException.class, () -> participantRepository.saveAndFlush(participant));
    }


}
