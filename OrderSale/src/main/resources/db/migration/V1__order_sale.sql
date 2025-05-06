-- Purchase Orders: master header
CREATE TABLE purchase_order (
    id SERIAL PRIMARY KEY,
    purchase_invoice VARCHAR(255),
    purchase_date DATE NOT NULL,
    supplier_id BIGINT NOT NULL,
    grand_total BIGINT,
    email VARCHAR(255) NOT NULL DEFAULT 'default@gmail.com',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Purchase Order Line Items
CREATE TABLE purchase_order_items (
    id SERIAL PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL CONSTRAINT fk_purchase_order REFERENCES purchase_order(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    item_qty BIGINT,
    item_total_price DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Sales Transactions: master header
CREATE TABLE sale (
    id SERIAL PRIMARY KEY,
    bill VARCHAR(255) NOT NULL,
    sale_date DATE NOT NULL DEFAULT CURRENT_DATE,
    customer_name VARCHAR(255),
    sale_grand_total DOUBLE PRECISION,
    phone_number VARCHAR(20),
    email VARCHAR(255) NOT NULL DEFAULT 'default@gmail.com',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Sales Line Items
CREATE TABLE sale_items (
    id SERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL CONSTRAINT fk_sale REFERENCES sale(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    item_qty BIGINT,
    item_total_price DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);