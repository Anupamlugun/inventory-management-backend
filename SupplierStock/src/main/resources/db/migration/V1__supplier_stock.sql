-- Create stock table 
CREATE TABLE stock (
    id BIGSERIAL PRIMARY KEY,
    product_Id BIGINT NOT NULL,
    purchase_id BIGINT NOT NULL,
    sale_id BIGINT NOT NULL,
    available BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email varchar(255) DEFAULT 'default@gmail.com' NOT NULL
);

-- Create supplier table 
CREATE TABLE supplier (
    supplier_id BIGSERIAL PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    supplier_phone VARCHAR(20) NOT NULL,
    supplier_email VARCHAR(255) NOT NULL,
    supplier_address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    email varchar(255) DEFAULT 'default@gmail.com' NOT NULL
);