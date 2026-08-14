CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL UNIQUE REFERENCES appointments(id),
    medicines TEXT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL
);