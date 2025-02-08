# language: pt

Funcionalidade: API - Pagamento

  Cenário: Realizar um pagamento
    Quando receber uma solicitação de pagamento
    Então a solitação de pagamento de QR do PagBank deve ser realizada com sucesso