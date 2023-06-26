#!/bin/sh

# Author : Abayomi Mosaku
# Copyright (c) ebi.ac.uk

# Ensure all branches ready for release are merged into dev / master


DEV_BRANCH='gwas-ui-296-Release-Prod'

MASTER_BRANCH='2.x-stable'

echo "What is the new version number ?"
read VERSION_NUMBER
echo "Initializing Software Release for version $VERSION_NUMBER"

# switch to dev branch
git checkout $DEV_BRANCH

# pull changes from remote dev branch
git pull origin $DEV_BRANCH

# switch to master branch
git checkout $MASTER_BRANCH

# pull changes from remote master branch
git pull origin $MASTER_BRANCH

# integrate contents of dev branch with master branch
git merge $DEV_BRANCH

# compile to ensure evrything is still as expected
mvn clean compile

# Create a new temporary branch for release
git checkout -b release-branch

# run the version number upgrade script
./update-version.sh -v $VERSION_NUMBER

# Ensure everything still builds
mvn clean compile
git commit -a -m  "$VERSION_NUMBER:  Release done"

# CREATE A NEW TAG AND PUSH TO REMOTE.
git tag -a $VERSION_NUMBER -m "$VERSION_NUMBER tag"

# push the tagged version to master
git push origin --tags $MASTER_BRANCH

# Check out dev
git checkout $DEV_BRANCH

# merge the temporary release branch back into it.
git merge release-branch
git push origin $DEV_BRANCH

# Check out master
git checkout $MASTER_BRANCH

# Merge dev back to master
git merge $DEV_BRANCH
git push origin $MASTER_BRANCH

# then delete the unnecessary temporary branch
git branch -d release-branch
