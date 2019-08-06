#!/bin/bash

# Given by --env: $E4_PROV_KEY, $E4_PLATFORM_NAME

if [ -f /e4prov/$E4_PROV_KEY/${E4_APP_NAME}db.tar.gz ];
then
  echo ">>> E4 postgres-init: Dump file found. Restore: /e4prov/$E4_PROV_KEY/${E4_APP_NAME}db.tar.gz"
  START=$(date +%s)
  pg_restore -U $E4_APP_NAME -j 8 -d $E4_APP_NAME /e4prov/$E4_PROV_KEY/${E4_APP_NAME}db.tar.gz
  END=$(date +%s)
  echo ">>> Time taken for restore: $(($END - $START)) seconds"
else
  echo ">>> E4 postgres-init: No dump file found starting from scratch."
fi

echo ">>> E4 postgres-init DONE"
echo "E4_DB_INIT_DONE"