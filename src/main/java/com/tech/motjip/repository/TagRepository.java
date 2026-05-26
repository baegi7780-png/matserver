package com.tech.motjip.repository;

import com.tech.motjip.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByTagName(String tagName);
}