package com.nova.yeobaek.domain.item.repository.color;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.item.domain.Color;

import java.util.List;
import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {

    List<Color> findByNameIn(List<String> names);

    Optional<Color> findByName(String name);
}
