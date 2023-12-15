package com.example.interactionservice.repository;

import com.example.interactionservice.entity.Interaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, UUID> {

    @Query(value = "SELECT i.* FROM interactions as i " +
            "JOIN contacts as c ON i.id_contact = c.id " +
            "WHERE i.id_user = ?1 " +
            "AND i.id_contact= ?2 " +
            "AND c.status= 'ACTIVE' ", nativeQuery = true)
    Page<Interaction> getHistory(UUID idUser, UUID idContact, Pageable pageable);


    @Query(value = "SELECT contacts.id AS contact_id, COUNT(*) AS interaction_count " +
            "FROM interactions " +
            "JOIN contacts ON interactions.id_contact = contacts.id " +
            "WHERE interactions.id_user = :userId " +
            "GROUP BY contacts.id " +
            "ORDER BY interaction_count DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Object[] findMostInteractedContact(@Param("userId") UUID userId);

    @Query(value = "SELECT COUNT(i.id) FROM interactions i WHERE i.id_user = :userId", nativeQuery = true)
    long countTotalInteractionsByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT COUNT(i.id) FROM interactions i WHERE i.id_user = :userId AND i.interaction_type = :interactionType", nativeQuery = true)
    long countInteractionsByType(@Param("userId") UUID userId, @Param("interactionType") String interactionType);

    @Query(value = "SELECT * FROM interactions i WHERE i.id_user = :userId AND i.id = :interactionId", nativeQuery = true)
    Optional<Interaction> checkInteraction(@Param("userId") UUID userId, @Param("interactionId") UUID interactionId);
}
