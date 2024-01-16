CREATE TABLE document
(
    id UUID DEFAULT RANDOM_UUID(),
    name VARCHAR(255),
    content CLOB,
    request CLOB,
    response CLOB,
    prompt_text CLOB,
    document_type VARCHAR(55)
);
