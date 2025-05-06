-- -- V1__category_products.sql
-- CREATE TABLE category (
--     category_id BIGSERIAL PRIMARY KEY,
--     category_name VARCHAR(255) UNIQUE NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     email varchar(255) DEFAULT 'default@gmail.com' NOT NULL,
-- );
-- CREATE TABLE products (
--     product_Id BIGSERIAL PRIMARY KEY,
--     product_name VARCHAR(255) NOT NULL,
--     product_price INT NOT NULL CHECK (product_price >= 0),
--     category_id BIGINT NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     status BOOLEAN NOT NULL DEFAULT TRUE,
--     email varchar(255) DEFAULT 'default@gmail.com' NOT NULL,
--     CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE
-- );
CREATE TABLE category (
    category_id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email VARCHAR(255) DEFAULT 'default@gmail.com' NOT NULL
);

CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_price INT NOT NULL CHECK (product_price >= 0),
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    email VARCHAR(255) DEFAULT 'default@gmail.com' NOT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE
);