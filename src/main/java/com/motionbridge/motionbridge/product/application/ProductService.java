package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.db.ParameterRepository;
import com.motionbridge.motionbridge.product.db.PresentationRepository;
import com.motionbridge.motionbridge.product.db.ProductRepository;
import com.motionbridge.motionbridge.product.entity.Parameter;
import com.motionbridge.motionbridge.product.entity.Presentation;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.product.web.mapper.RestActiveProduct;
import com.motionbridge.motionbridge.product.web.mapper.RestParameter;
import com.motionbridge.motionbridge.product.web.mapper.RestPresentation;
import com.motionbridge.motionbridge.product.web.mapper.RestProduct;
import com.motionbridge.motionbridge.subscription.entity.Currency;
import com.motionbridge.motionbridge.subscription.entity.ProductName;
import com.motionbridge.motionbridge.subscription.entity.TimePeriod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.motionbridge.motionbridge.product.web.mapper.RestActiveProduct.toRestActiveProduct;
import static com.motionbridge.motionbridge.product.web.mapper.RestProduct.toRestProduct;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductService implements ManipulateProductUseCase {

    final ProductRepository repository;
    final ParameterRepository parameterRepository;
    final PresentationRepository presentationRepository;

    @Override
    public List<RestActiveProduct> getActiveProducts() {
        List<RestActiveProduct> restActiveProducts = new ArrayList<>();
        final List<Product> allActiveProducts = repository.getAllActiveProducts();
        for (Product product : allActiveProducts) {
            final Long id = product.getId();

            final List<RestPresentation> restPresentations = getRestPresentations(id);
            final List<RestParameter> restParameters = getRestParameters(id);

            restActiveProducts.add(toRestActiveProduct(product, restPresentations, restParameters));
        }
        return restActiveProducts;
    }

    @NotNull
    private List<RestPresentation> getRestPresentations(Long productId) {
        return presentationRepository
                .getAllPresentationsByProductId(productId)
                .stream()
                .map(RestPresentation::toRestPresentation)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<RestParameter> getRestParameters(Long productId) {
        return parameterRepository
                .getAllParametersByProductId(productId)
                .stream()
                .map(RestParameter::toRestParameter)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddProductResponse addProduct(CreateProductCommand command) {
        Product product = new Product(
                ProductName.valueOf(command.getType().toUpperCase()),
                command.getTitle(),
                command.getPrice(),
                Currency.valueOf(command.getCurrency().toUpperCase()),
                command.getAnimationQuantity(),
                TimePeriod.valueOf(command.getTimePeriod().toUpperCase()),
                command.getBackground()
        );
        Product saveProduct = repository.save(product);
        List<Long> presentations = new ArrayList<>();
        List<Long> parameters = new ArrayList<>();
        if (command.getParameters().size() > 0) {
            for (CreateParameter parameter : command.getParameters()) {
                final Parameter save = parameterRepository.save(new Parameter(
                        parameter.getImage(),
                        parameter.getSubtitle(),
                        parameter.getTitle(),
                        parameter.getContent(),
                        parameter.getClasses(),
                        saveProduct)
                );
                parameters.add(save.getId());
            }
        }

        if (command.getPresentations().size() > 0) {
            for (CreatePresentation presentation : command.getPresentations()) {
                final Presentation save = presentationRepository.save(
                        new Presentation(
                                presentation.getTitle(),
                                presentation.getContent(),
                                presentation.getPreview(),
                                presentation.getClasses(),
                                saveProduct
                        ));
                presentations.add(save.getId());
            }
        }

        return AddProductResponse.success(new CreateProductResponse(saveProduct.getId(), parameters, presentations));
    }

    @Override
    public List<RestProduct> getAllProducts() {
        List<RestProduct> restProducts = new ArrayList<>();
        final List<Product> products = repository.findAll();
        for (Product product : products) {
            final Long id = product.getId();

            final List<RestPresentation> restPresentations = getRestPresentations(id);
            final List<RestParameter> restParameters = getRestParameters(id);

            restProducts.add(toRestProduct(product, restPresentations, restParameters));
        }

        return restProducts;
    }

    @Transactional
    @Override
    public SwitchStatusResponse switchStatus(Long id) {
        return repository.findById(id)
                .map(product -> {
                    switchActualStatus(id);
                    return SwitchStatusResponse.SUCCESS;
                })
                .orElseGet(() -> new SwitchStatusResponse(false, Collections.singletonList("Could not change status")));
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    @Override
    public ProductOrder checkIfProductExistInOrderThenGet(Long productId) {
        ProductOrder productOrder = new ProductOrder();

        if (getProductById(productId).isPresent()) {

            Product temp = getProductById(productId).get();

            productOrder = ProductOrder
                    .builder()
                    .id(temp.getId())
                    .price(temp.getPrice())
                    .currency(temp.getCurrency().toString())
                    .animationQuantity(temp.getAnimationQuantity())
                    .type(String.valueOf(temp.getType()).toUpperCase())
                    .title(temp.getTitle())
                    .timePeriod(String.valueOf(temp.getTimePeriod()).toUpperCase())
                    .isActive(temp.getIsActive())
                    .build();
            log.info("Product with Id: " + productId + " is accessible");
        } else {
            log.warn("Product with id: " + productId + " does not exist");
        }
        return productOrder;
    }

    @Override
    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    private void switchActualStatus(Long id) {
        repository.getById(id).setIsActive(repository.getById(id).getIsActive() != null && !repository.getById(id).getIsActive());
    }
}