#!/bin/bash
#SBATCH -t 04:00:00

#SBATCH --mem=4G
mode=${1}
echo "mode is ${mode}"
if [ -z "$1" ]
then
  /hps/software/users/parkinso/spot/gwas/prod/sw/deposition_sync/sync_wrapper.sh
else
  /hps/software/users/parkinso/spot/gwas/prod/sw/deposition_sync/sync_wrapper.sh ${mode}
fi
exit $?