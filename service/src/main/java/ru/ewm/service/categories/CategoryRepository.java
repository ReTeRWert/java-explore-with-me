package ru.ewm.service.categories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  //  List<Category> findAllOrderByIdAsc(Pageable pageable);
}
