ALTER TABLE
    purchase_order_items
ADD
    email VARCHAR(255) NOT NULL DEFAULT 'default@gmail.com';

ALTER TABLE
    sale_items
ADD
    email VARCHAR(255) NOT NULL DEFAULT 'default@gmail.com';