package com.example.library;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<String, Long> {
}
