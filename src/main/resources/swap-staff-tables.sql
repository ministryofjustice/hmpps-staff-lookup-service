BEGIN;
    ALTER TABLE "staff" RENAME TO "staff_old";
    ALTER TABLE "staff_temp" RENAME TO "staff";
    ALTER TABLE "staff_old" RENAME TO "staff_temp";
    TRUNCATE TABLE "staff_temp";
COMMIT;