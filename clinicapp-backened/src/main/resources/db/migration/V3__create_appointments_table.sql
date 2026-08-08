CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    slot_id BIGINT NOT NULL UNIQUE REFERENCES slots(id),
    patient_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL,
    booked_at TIMESTAMP NOT NULL
);
