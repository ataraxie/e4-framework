#!/bin/bash

# Given by --env: $E4_PROV_KEY, $E4_PLATFORM_NAME

DUMPFILE_PATH="/e4prov/$E4_PROV_KEY/${E4_APP_NAME}db.sql"

if [ -f $DUMPFILE_PATH ];
then
  echo ">>> E4 mysql-init: Dump file found. Restore: $DUMPFILE_PATH"
  START=$(date +%s)
  mysql -u root jira < $DUMPFILE_PATH
  END=$(date +%s)
  echo ">>> Time taken for restore: $(($END - $START)) seconds"
else
  echo ">>> E4 mysql-init: No dump file found starting from scratch."
fi

echo ">>> E4 mysql-init DONE"
echo "E4_DB_INIT_DONE"