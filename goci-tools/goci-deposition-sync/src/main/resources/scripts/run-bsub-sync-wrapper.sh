mode={1}

if [ -z "$1" ]
then
  bsub -M 4096 -R "rusage[mem=4096]" "/hps/software/users/parkinso/spot/gwas/prod/sw/deposition_sync/sync_wrapper.sh ${mode}"
else
  bsub -M 4096 -R "rusage[mem=4096]" "/hps/software/users/parkinso/spot/gwas/prod/sw/deposition_sync/sync_wrapper.sh"
fi
exit $?