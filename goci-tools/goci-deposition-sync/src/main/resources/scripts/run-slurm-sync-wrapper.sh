#!/bin/bash
#SBATCH -t 04:00:00

#SBATCH --mem=4G
mode=${1}
echo "mode is ${mode}"
if [ -z "$1" ]
then
  srun -t 04:00:00 --mem=4G  /hps/software/users/parkinso/spot/gwas/prod/sw/deposition_sync/sync_wrapper.sh
else
  srun -t 04:00:00 --mem=4G  /hps/software/users/parkinso/spot/gwas/prod/sw/deposition_sync/sync_wrapper.sh ${mode}
fi
exit $?