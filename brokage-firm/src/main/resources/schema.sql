CREATE TABLE IF NOT EXISTS customer (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS asset (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     customer_id BIGINT NOT NULL,
                                     asset_name VARCHAR(255) NOT NULL,
    size NUMERIC(38, 2) NOT NULL,
    usable_size NUMERIC(38, 2) NOT NULL,
    UNIQUE (customer_id, asset_name),
    CONSTRAINT fk_asset_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    version BIGINT NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS stock_order (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           customer_id BIGINT NOT NULL,
                                           asset_name VARCHAR(255) NOT NULL,
    order_side VARCHAR(50) NOT NULL,
    size NUMERIC(38, 2) NOT NULL,
    price NUMERIC(38, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    create_date TIMESTAMP(6) NOT NULL,
    CONSTRAINT stock_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
    );