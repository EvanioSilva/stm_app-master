create table rotulo(
    _id int primary key,
    image_uri text,
    model_produto_recebido int
);
create table produto(
    _id int primary key,
    descricao_estrategia_mercado text,
    codigo_rms text,
    codigo_ean text,
    descricao_produto text,
    razao_social_fornecedor text,
    tipo_produto text,
    dias_validade text
);