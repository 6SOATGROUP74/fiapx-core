ALTER TABLE db_soat_pagamento.tb_pagamento ADD cod_pagamento varchar(255) NULL COMMENT 'Código do pagamento gerado pela adquirência';
ALTER TABLE db_soat_pagamento.tb_pagamento ADD copia_cola MEDIUMTEXT NULL COMMENT 'Copia cola do pagamento QR code';
ALTER TABLE db_soat_pagamento.tb_pagamento ADD qr_code_link varchar(255) NULL COMMENT 'Link de pagamento do QR code da adquirência';