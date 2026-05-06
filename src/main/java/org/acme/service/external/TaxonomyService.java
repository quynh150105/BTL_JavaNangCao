package org.acme.service.external;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.acme.entity.Category;
import org.acme.entity.Country;
import org.acme.repository.CategoryRepository;
import org.acme.repository.CountryRepository;

import java.util.List;

@ApplicationScoped
public class TaxonomyService {

    @Inject
    private  CategoryRepository categoryRepository;
    @Inject
    private  CountryRepository countryRepository;

    public List<Category> resolveCategories(List<String> names){
        return names.stream().map(name -> {
            return categoryRepository.find("name", name)
                    .firstResultOptional()
                    .orElseGet(() -> {
                        Category c = Category.builder()
                                .name(name)
                                .slug(name.toLowerCase().replace(" ", "-"))
                                .build();
                        categoryRepository.persist(c);
                        return c;
                    });
        }).toList();
    }

    public List<Country> resolveCountries(List<String> names){
        return names.stream().map(name ->{
            return countryRepository.find("name", name)
                    .firstResultOptional()
                    .orElseGet(() -> {
                        Country c = Country.builder()
                                .name(name)
                                .slug(name.toLowerCase())
                                .build();
                        countryRepository.persist(c);
                        return c;
                    });
        }).toList();
    }

}
