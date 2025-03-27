package com.footballstore.product.service;

import com.footballstore.exception.DomainException;
import com.footballstore.product.model.Brand;
import com.footballstore.product.model.Category;
import com.footballstore.product.model.Product;
import com.footballstore.product.repository.ProductRepository;
import com.footballstore.web.dto.ProductRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .imageUrl(productRequest.getImageUrl())
                .brand(productRequest.getBrand())
                .isInStock(productRequest.isInStock())
                .deleted(false)
                .build();

        productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        List<Category> categoryOrder = List.of(Category.BOOTS, Category.BALLS, Category.JERSEYS);

        return productRepository.findAllByDeletedFalse().stream()
                .sorted(Comparator.comparing(product -> categoryOrder.indexOf(product.getCategory())))
                .toList();
    }

    public List<Product> getFeaturedProducts() {
        Pageable limit = PageRequest.of(0, 3); // Fetch only 3 products
        return productRepository.findRandomInStockProducts(limit);
    }



    public void updateProduct(UUID productId, ProductRequest productRequest) {
        Product product = getProductById(productId);

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setBrand(productRequest.getBrand());
        product.setInStock(productRequest.isInStock());

        productRepository.save(product);

    }

    public void deleteProduct(UUID productId) {
        Product product = getProductById(productId);
        product.setDeleted(true);
        productRepository.save(product);
    }

    public Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> new DomainException("Product not found."));
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(Category.valueOf(category));
    }

    public List<Product> getSearchedProducts(String name) {
        return productRepository.findByNameIgnoreCaseContaining(name);
    }

    @Transactional
    public void createAppProducts() {
        if(productRepository.count() == 0) {
            List<Product> products = List.of(
                    Product.builder().name("Adidas Predator Elite").description("Amazing football boots from Adidas").price(new BigDecimal("359.99")).category(Category.BOOTS).imageUrl("https://brand.assets.adidas.com/image/upload/f_auto,q_auto,fl_lossy/Predator_28f40307d9.jpg").brand(Brand.ADIDAS).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike Phantom Luna II Pro").description("New vision from Nike").price(new BigDecimal("339.99")).category(Category.BOOTS).imageUrl("https://www.sportvision.bg/files/images/slike_proizvoda/media/FJ2/FJ2572-400/images/FJ2572-400.jpg").brand(Brand.NIKE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike Zoom Mercurial Vapor 16").description("Incredible football boots").price(new BigDecimal("299.99")).category(Category.BOOTS).imageUrl("https://gfx.r-gol.com/media/res/products/597/194597/465x605/fq8693-800_1.webp").brand(Brand.NIKE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Puma Future 7 Ultimate").description("Football boots with top quality").price(new BigDecimal("199.99")).category(Category.BOOTS).imageUrl("https://www.futbolemotion.com/imagesarticulos/220563/grandes/bota-puma-future-7-match-fgag-mujer-white-black-poison-pink-0.webp").brand(Brand.PUMA).isInStock(true).deleted(false).build(),
                    Product.builder().name("Mizuno Morelia IV Pro").description("Elegant, comfortable football boots").price(new BigDecimal("249.99")).category(Category.BOOTS).imageUrl("https://www.futbolemotion.com/imagesarticulos/231019/750/bota-mizuno-morelia-neo-iv-japan-fg-black-white-red-0.jpg").brand(Brand.MIZUNO).isInStock(true).deleted(false).build(),
                    Product.builder().name("New Balance Tekela V4+").description("Feel the balance with these boots").price(new BigDecimal("279.99")).category(Category.BOOTS).imageUrl("https://gfx.r-gol.com/media/res/products/823/187823/st2fn45_1.webp").brand(Brand.NEW_BALANCE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Adidas UCL League Istanbul").description("Highest quality ball from Adidas").price(new BigDecimal("149.99")).category(Category.BALLS).imageUrl("https://gfx.r-gol.com/media/res/products/11/159011/hu1580-5_1.jpg").brand(Brand.ADIDAS).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike Premier League Flight").description("Highest quality ball from Nike").price(new BigDecimal("169.99")).category(Category.BALLS).imageUrl("https://gfx.r-gol.com/media/res/products/179/163179/dn3602-101-5_1.jpg").brand(Brand.NIKE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Puma Neymar Jr Diamond").description("Highest quality ball Puma").price(new BigDecimal("179.99")).category(Category.BALLS).imageUrl("https://gfx.r-gol.com/media/res/products/344/157344/083949-02-4_1.jpg").brand(Brand.PUMA).isInStock(true).deleted(false).build(),
                    Product.builder().name("Adidas Messi Club").description("Highest quality ball Messi collection").price(new BigDecimal("229.99")).category(Category.BALLS).imageUrl("https://gfx.r-gol.com/media/res/products/758/137758/465x605/pilka-adidas-messi-club-rozmiar-5_1.png").brand(Brand.ADIDAS).isInStock(true).deleted(false).build(),
                    Product.builder().name("Adidas UCL League 24/25").description("Champions League ball").price(new BigDecimal("339.99")).category(Category.BALLS).imageUrl("https://gfx.r-gol.com/media/res/products/526/198526/465x605/jh1294-futs_1.webp").brand(Brand.ADIDAS).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike Premier League+").description("Premier League Academy ball").price(new BigDecimal("309.99")).category(Category.BALLS).imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6EMDkTUFpSqfTL89BZbitycUBaAiBzATyUg&s").brand(Brand.NIKE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike FC Barcelona Jersey").description("Barcelona jersey primary").price(new BigDecimal("99.99")).category(Category.JERSEYS).imageUrl("https://gfx.r-gol.com/media/res/products/389/121389/465x605/koszulka-nike-fc-barcelona-breathe-stadium-domowa-junior-894458-456_1.png").brand(Brand.NIKE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike FC Barcelona Green").description("Barca jersey made for winners").price(new BigDecimal("89.99")).category(Category.JERSEYS).imageUrl("https://www.11teamsports.com/cdn-cgi/image/format=webp,width=1400/media/c4/d2/08/1727071872/nike-fc-barcelona-trikot-ucl-2024-2025-gruen-f702-fq2022-fan-shop-front.png").brand(Brand.NIKE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Adidas RealMadrid Jersey").description("Top-quality jersey RealMadrid").price(new BigDecimal("79.99")).category(Category.JERSEYS).imageUrl("https://www.tudnfanshop.com/cdn/shop/files/PwfJDC2S_WHT_52f6b0c5-15d5-4520-85e1-88a417d586e8.png?v=1726762716&width=1500").brand(Brand.ADIDAS).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike Liverpool Jersey").description("Top-quality jersey Liverpool").price(new BigDecimal("69.99")).category(Category.JERSEYS).imageUrl("https://soccerwearhouse.com/cdn/shop/files/1_fb3f3d93-fa46-4634-bd3a-f2ffa7cc1121.png?v=1724863514").brand(Brand.NIKE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike AtleticoMadrid Jersey").description("Amazing jersey AtleticoMadrid").price(new BigDecimal("59.99")).category(Category.JERSEYS).imageUrl("https://cdn.shoplightspeed.com/shops/611228/files/59377341/nike-atletico-madrid-23-24-home-jersey-red-white.jpg").brand(Brand.NIKE).isInStock(true).deleted(false).build(),
                    Product.builder().name("Nike Chelsea Jersey").description("Amazing jersey Chelsea collection").price(new BigDecimal("89.99")).category(Category.JERSEYS).imageUrl("https://cdn.media.amplience.net/i/frasersdev/36744318_o.jpg?v=240827143238").brand(Brand.NIKE).isInStock(true).deleted(false).build()
            );
            productRepository.saveAll(products);

            log.info("Successfully created app products.");
        }
    }
}
