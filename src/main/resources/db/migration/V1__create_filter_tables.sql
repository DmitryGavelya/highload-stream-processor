CREATE TABLE string_filters (
    field_name TEXT PRIMARY KEY
    expected_value TEXT NOT NULL
)

CREATE TABLE int_filters (
    field_name TEXT PRIMARY KEY
    expected_value INTEGER NOT NULL
)

CREATE TABLE bool_filters (
    field_name TEXT PRIMARY KEY
    expected_value BOOLEAN NOT NULL
)
