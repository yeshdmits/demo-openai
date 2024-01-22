CREATE TABLE document
(
    id UUID UNIQUE,
    name VARCHAR(255) NOT NULL,
    content CLOB NOT NULL,
    request CLOB,
    response CLOB,
    prompt_text CLOB,
    document_type VARCHAR(55)
);
