package com.example.manage_contacts.repository;

import com.example.manage_contacts.entity.Contact;
import com.example.manage_contacts.entity.UserContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserContactRepository extends JpaRepository<UserContact, UUID> {

    @Query(value = "SELECT C FROM UserContact AS UC " +
            "JOIN Contact AS C ON UC.idContact = C.id " +
            "WHERE UC.idUser = :idUser " +
            "AND C.name like concat('%',:name,'%') " +
            "AND C.phoneNumber like concat('%',:phone,'%') " +
            "AND C.email like concat('%',:email,'%') " +
            "AND C.status = 'ACTIVE' ")
    Page<Contact> findAllByIdUser(UUID idUser, String name, String phone, String email, Pageable pageable);

}
