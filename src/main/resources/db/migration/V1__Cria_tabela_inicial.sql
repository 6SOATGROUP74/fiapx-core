-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema db_soat
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema db_soat
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `db_soat_pagamento` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `db_soat_pagamento` ;

-- -----------------------------------------------------
-- Table `db_soat`.`tb_pagamento`
-- -----------------------------------------------------
DROP TABLE IF EXISTS tb_pagamento;
CREATE TABLE tb_pagamento (
                              id_pagamento bigint NOT NULL AUTO_INCREMENT,
                              numero_pedido bigint NOT NULL,
                              valor_total DECIMAL(10,2) NOT NULL,
                              tipo_do_pagamento VARCHAR(255) NULL DEFAULT NULL,
                              status enum('PENDENTE','APROVADO','REPROVADO','CANCELADO') NOT NULL DEFAULT 'PENDENTE',
                              data_pagamento datetime NOT NULL,
                              PRIMARY KEY (id_pagamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
