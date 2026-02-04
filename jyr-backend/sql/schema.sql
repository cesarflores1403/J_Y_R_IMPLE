
-- TABLA: users (Usuarios del sistema)
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL UNIQUE,
    password        VARCHAR(255)    NOT NULL,
    full_name       VARCHAR(100)    NOT NULL,
    email           VARCHAR(100)    UNIQUE,
    phone           VARCHAR(20),
    role            VARCHAR(20)     NOT NULL CHECK (role IN ('ADMIN', 'CAJERO', 'BODEGUERO')),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);


-- TABLA: categories (Categorías de productos)
CREATE TABLE IF NOT EXISTS categories (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL UNIQUE,
    description     VARCHAR(255),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);


-- TABLA: products (Productos)
CREATE TABLE IF NOT EXISTS products (
    id              BIGSERIAL       PRIMARY KEY,
    code            VARCHAR(30)     NOT NULL UNIQUE,
    barcode         VARCHAR(50)     UNIQUE,
    name            VARCHAR(150)    NOT NULL,
    description     TEXT,
    category_id     BIGINT          NOT NULL REFERENCES categories(id),
    purchase_price  NUMERIC(12,2)   NOT NULL CHECK (purchase_price >= 0),
    sale_price      NUMERIC(12,2)   NOT NULL CHECK (sale_price > 0),
    stock           INTEGER         NOT NULL DEFAULT 0 CHECK (stock >= 0),
    min_stock       INTEGER         NOT NULL DEFAULT 5,
    max_stock       INTEGER,
    image_url       VARCHAR(500),
    tax_rate        NUMERIC(5,2)    DEFAULT 15.00,
    unit            VARCHAR(20)     DEFAULT 'UNIDAD',
    brand           VARCHAR(100),
    model           VARCHAR(100),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_code ON products(code);
CREATE INDEX idx_product_barcode ON products(barcode);
CREATE INDEX idx_product_category ON products(category_id);

-- TABLA: customers (Clientes)
CREATE TABLE IF NOT EXISTS customers (
    id              BIGSERIAL       PRIMARY KEY,
    identity_number VARCHAR(20)     NOT NULL UNIQUE,
    full_name       VARCHAR(150)    NOT NULL,
    email           VARCHAR(100),
    phone           VARCHAR(20),
    address         VARCHAR(255),
    rtn             VARCHAR(20),
    notes           TEXT,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);


-- TABLA: suppliers (Proveedores)
CREATE TABLE IF NOT EXISTS suppliers (
    id              BIGSERIAL       PRIMARY KEY,
    company_name    VARCHAR(150)    NOT NULL,
    rtn             VARCHAR(20)     UNIQUE,
    contact_name    VARCHAR(100),
    email           VARCHAR(100),
    phone           VARCHAR(20),
    address         VARCHAR(255),
    notes           TEXT,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

-- TABLA: documents (Facturas y Cotizaciones)
CREATE TABLE IF NOT EXISTS documents (
    id              BIGSERIAL       PRIMARY KEY,
    document_number VARCHAR(30)     NOT NULL UNIQUE,
    document_type   VARCHAR(20)     NOT NULL CHECK (document_type IN ('FACTURA', 'COTIZACION')),
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE'
                                    CHECK (status IN ('PENDIENTE', 'PAGADA', 'ANULADA', 'VENCIDA')),
    document_date   DATE            NOT NULL DEFAULT CURRENT_DATE,
    due_date        DATE,
    customer_id     BIGINT          NOT NULL REFERENCES customers(id),
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    subtotal        NUMERIC(14,2)   NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(14,2)   NOT NULL DEFAULT 0,
    discount_amount NUMERIC(14,2)   NOT NULL DEFAULT 0,
    total           NUMERIC(14,2)   NOT NULL DEFAULT 0,
    payment_method  VARCHAR(20)     CHECK (payment_method IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'CREDITO')),
    notes           TEXT,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doc_number ON documents(document_number);
CREATE INDEX idx_doc_type ON documents(document_type);
CREATE INDEX idx_doc_date ON documents(document_date);
CREATE INDEX idx_doc_customer ON documents(customer_id);


-- TABLA: document_details (Detalle de facturas/cotizaciones)
CREATE TABLE IF NOT EXISTS document_details (
    id              BIGSERIAL       PRIMARY KEY,
    document_id     BIGINT          NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    product_id      BIGINT          NOT NULL REFERENCES products(id),
    quantity        INTEGER         NOT NULL CHECK (quantity > 0),
    unit_price      NUMERIC(12,2)   NOT NULL,
    discount_percent NUMERIC(5,2)   DEFAULT 0,
    tax_rate        NUMERIC(5,2)    DEFAULT 15.00,
    subtotal        NUMERIC(14,2)   NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(14,2)   NOT NULL DEFAULT 0,
    total           NUMERIC(14,2)   NOT NULL DEFAULT 0,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_detail_document ON document_details(document_id);
CREATE INDEX idx_detail_product ON document_details(product_id);


-- TABLA: product_returns (Devoluciones)
CREATE TABLE IF NOT EXISTS product_returns (
    id              BIGSERIAL       PRIMARY KEY,
    return_number   VARCHAR(30)     NOT NULL UNIQUE,
    document_id     BIGINT          NOT NULL REFERENCES documents(id),
    product_id      BIGINT          NOT NULL REFERENCES products(id),
    customer_id     BIGINT          NOT NULL REFERENCES customers(id),
    quantity        INTEGER         NOT NULL CHECK (quantity > 0),
    refund_amount   NUMERIC(14,2),
    reason          TEXT            NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'SOLICITADA'
                                    CHECK (status IN ('SOLICITADA', 'APROBADA', 'RECHAZADA', 'COMPLETADA')),
    return_date     DATE            NOT NULL DEFAULT CURRENT_DATE,
    evidence_url    VARCHAR(500),
    resolution_notes TEXT,
    processed_by    BIGINT          REFERENCES users(id),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_return_document ON product_returns(document_id);
CREATE INDEX idx_return_status ON product_returns(status);

-- TABLA: purchase_orders (Órdenes de compra a proveedores)
CREATE TABLE IF NOT EXISTS purchase_orders (
    id              BIGSERIAL       PRIMARY KEY,
    order_number    VARCHAR(30)     NOT NULL UNIQUE,
    supplier_id     BIGINT          NOT NULL REFERENCES suppliers(id),
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    order_date      DATE            NOT NULL DEFAULT CURRENT_DATE,
    expected_date   DATE,
    received_date   DATE,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE'
                                    CHECK (status IN ('PENDIENTE', 'RECIBIDA', 'PARCIAL', 'CANCELADA')),
    subtotal        NUMERIC(14,2)   DEFAULT 0,
    tax_amount      NUMERIC(14,2)   DEFAULT 0,
    total           NUMERIC(14,2)   DEFAULT 0,
    notes           TEXT,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_po_supplier ON purchase_orders(supplier_id);
CREATE INDEX idx_po_status ON purchase_orders(status);

-- TABLA: purchase_order_details (Detalle de órdenes de compra)
CREATE TABLE IF NOT EXISTS purchase_order_details (
    id                  BIGSERIAL       PRIMARY KEY,
    purchase_order_id   BIGINT          NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    product_id          BIGINT          NOT NULL REFERENCES products(id),
    quantity            INTEGER         NOT NULL CHECK (quantity > 0),
    received_quantity   INTEGER         DEFAULT 0,
    unit_cost           NUMERIC(12,2)   NOT NULL,
    tax_rate            NUMERIC(5,2)    DEFAULT 15.00,
    subtotal            NUMERIC(14,2)   DEFAULT 0,
    tax_amount          NUMERIC(14,2)   DEFAULT 0,
    total               NUMERIC(14,2)   DEFAULT 0,
    active              BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pod_order ON purchase_order_details(purchase_order_id);

-- TABLA: inventory_movements (Movimientos de inventario)
CREATE TABLE IF NOT EXISTS inventory_movements (
    id              BIGSERIAL       PRIMARY KEY,
    product_id      BIGINT          NOT NULL REFERENCES products(id),
    movement_type   VARCHAR(20)     NOT NULL CHECK (movement_type IN ('ENTRADA', 'SALIDA', 'AJUSTE', 'DEVOLUCION')),
    quantity        INTEGER         NOT NULL,
    previous_stock  INTEGER         NOT NULL,
    new_stock       INTEGER         NOT NULL,
    reference_type  VARCHAR(50),
    reference_id    BIGINT,
    movement_date   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes           TEXT,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inv_mov_product ON inventory_movements(product_id);
CREATE INDEX idx_inv_mov_date ON inventory_movements(movement_date);
CREATE INDEX idx_inv_mov_type ON inventory_movements(movement_type);


-- DATOS INICIALES DE EJEMPLO

-- Categorías de ejemplo
INSERT INTO categories (name, description) VALUES
    ('Motor', 'Pistones, anillos, empaques y componentes internos'),
    ('Sistema de Frenos', 'Pastillas, discos, tambores y bombas de freno'),
    ('Suspensión y Dirección', 'Amortiguadores, terminales, tijeras y rótulas'),
    ('Sistema Eléctrico', 'Alternadores, motores de arranque, bujías y sensores'),
    ('Filtración', 'Filtros de aceite, aire, combustible y cabina')
ON CONFLICT (name) DO NOTHING;

-- Cliente consumidor final
INSERT INTO customers (identity_number, full_name, notes) VALUES
    ('0000-0000-00000', 'Consumidor Final', 'Cliente genérico para ventas sin identificación')
ON CONFLICT (identity_number) DO NOTHING;


-- Vista de ventas diarias
CREATE OR REPLACE VIEW v_daily_sales AS
SELECT
    d.document_date,
    COUNT(*) AS invoice_count,
    SUM(d.subtotal) AS total_subtotal,
    SUM(d.tax_amount) AS total_tax,
    SUM(d.total) AS total_sales
FROM documents d
WHERE d.document_type = 'FACTURA'
  AND d.status = 'PAGADA'
GROUP BY d.document_date
ORDER BY d.document_date DESC;

-- Vista de productos con stock bajo
CREATE OR REPLACE VIEW v_low_stock_products AS
SELECT
    p.id,
    p.code,
    p.name,
    p.stock,
    p.min_stock,
    c.name AS category_name,
    p.sale_price
FROM products p
JOIN categories c ON p.category_id = c.id
WHERE p.active = TRUE
  AND p.stock <= p.min_stock
ORDER BY p.stock ASC;

-- Vista de productos más vendidos
CREATE OR REPLACE VIEW v_top_selling_products AS
SELECT
    p.id,
    p.code,
    p.name,
    SUM(dd.quantity) AS total_sold,
    SUM(dd.total) AS total_revenue
FROM document_details dd
JOIN products p ON dd.product_id = p.id
JOIN documents d ON dd.document_id = d.id
WHERE d.document_type = 'FACTURA'
  AND d.status = 'PAGADA'
GROUP BY p.id, p.code, p.name
ORDER BY total_sold DESC;

