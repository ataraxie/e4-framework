#!/bin/bash

# Given by --env: $E4_PROV_KEY

if [ -f /e4prov/$E4_PROV_KEY/confluencedb.tar.gz ];
then
  echo ">>> E4 postgres-init: Dump file found. Restore: /e4prov/$E4_PROV_KEY/confluencedb.tar.gz"
  START=$(date +%s)
  pg_restore -U confluence -j 8 -d confluence /e4prov/$E4_PROV_KEY/confluencedb.tar.gz
  END=$(date +%s)
  echo ">>> Time taken for restore: $(($END - $START)) seconds"
else
  echo ">>> E4 postgres-init: No dump file found starting from scratch."
fi

echo ">>> E4 postgres-init DONE"