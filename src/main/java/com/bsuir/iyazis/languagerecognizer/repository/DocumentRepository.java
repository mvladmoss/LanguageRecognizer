package com.bsuir.iyazis.languagerecognizer.repository;

import com.bsuir.iyazis.languagerecognizer.model.mdoel.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
