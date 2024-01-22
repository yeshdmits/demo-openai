CREATE TABLE decision
(
    id UUID UNIQUE,
    document_id_fk UUID,
    description CLOB,
    decision_result CLOB,
    foreign key (document_id_fk) references document(id)
);
